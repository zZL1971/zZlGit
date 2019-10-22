Ext.override(Ext.tree.ViewDropZone, {
    getPosition: function (e, node) {
        var view = this.view,
        record = view.getRecord(node),
        y = e.getPageY(),
        noAppend = record.isLeaf(),
        noBelow = false,
        region = Ext.fly(node).getRegion(),
        fragment;

        if (record.isRoot()) {
            return 'append';
        }

        if (this.appendOnly) {
            return noAppend ? false : 'append';
        }
        if (!this.allowParentInsert) {
            //leehom modify begin
            noBelow = this.allowLeafInserts || (record.hasChildNodes() && record.isExpanded());
            //leehom modify end
        }

        if(this.allowLeafInserts){
        	fragment = (region.bottom - region.top) / 3;
			if (y >= region.top && y < (region.top + fragment)) {
				return 'before';
			}
			else if (y >= (region.bottom - fragment) && y <= region.bottom) {
				return 'after';
			}
			else {
				return 'append';
			}
        }
        
        /*fragment = (region.bottom - region.top) / (noAppend ? 2 : 3);
        if (y >= region.top && y < (region.top + fragment)) {
            return 'before';
        }
        else if (!noBelow && (noAppend || (y >= (region.bottom - fragment) && y <= region.bottom))) {
            return 'after';
        }
        else {
            return 'append';
        }*/
    },
    handleNodeDrop: function (data, targetNode, position) {
        var me = this,
        view = me.view,
        parentNode = targetNode.parentNode,
        store = view.getStore(),
        recordDomNodes = [],
        records, i, len,
        insertionMethod, argList,
        needTargetExpand,
        transferData,
        processDrop;
        if (data.copy) {
            records = data.records;
            data.records = [];
            for (i = 0, len = records.length; i < len; i++) {
                data.records.push(Ext.apply({}, records[i].data));
            }
        }
        me.cancelExpand();
        if (position == 'before') {
            insertionMethod = parentNode.insertBefore;
            argList = [null, targetNode];
            targetNode = parentNode;
        }
        else if (position == 'after') {
            if (targetNode.nextSibling) {
                insertionMethod = parentNode.insertBefore;
                argList = [null, targetNode.nextSibling];
            }
            else {
                insertionMethod = parentNode.appendChild;
                argList = [null];
            }
            targetNode = parentNode;
        }
        else {
            //leehom add begin
            if (this.allowLeafInserts) {
                if (targetNode.get('leaf')) {
                    targetNode.set('leaf', false);
                    targetNode.set('expanded', true);
                }
            }
            //leehom add end
            if (!targetNode.isExpanded()) {
                needTargetExpand = true;
            }
            insertionMethod = targetNode.appendChild;
            argList = [null];
        }

        transferData = function () {
            var node;
            for (i = 0, len = data.records.length; i < len; i++) {
                argList[0] = data.records[i];
                node = insertionMethod.apply(targetNode, argList);

                if (Ext.enableFx && me.dropHighlight) {
                    recordDomNodes.push(view.getNode(node));
                }
            }
            if (Ext.enableFx && me.dropHighlight) {
                Ext.Array.forEach(recordDomNodes, function (n) {
                    if (n) {
                        Ext.fly(n.firstChild ? n.firstChild : n).highlight(me.dropHighlightColor);
                    }
                });
            }
        };
        if (needTargetExpand) {
            targetNode.expand(false, transferData);
        }
        else {
            transferData();
        }
    }
});


Ext.override(Ext.tree.plugin.TreeViewDragDrop, {
    allowLeafInserts: true,

    onViewRender: function (view) {
        var me = this;
        if (me.enableDrag) {
            me.dragZone = Ext.create('Ext.tree.ViewDragZone', {
                view: view,
                allowLeafInserts: me.allowLeafInserts,
                ddGroup: me.dragGroup || me.ddGroup,
                dragText: me.dragText,
                repairHighlightColor: me.nodeHighlightColor,
                repairHighlight: me.nodeHighlightOnRepair
            });
        }

        if (me.enableDrop) {
            me.dropZone = Ext.create('Ext.tree.ViewDropZone', {
                view: view,
                ddGroup: me.dropGroup || me.ddGroup,
                allowContainerDrops: me.allowContainerDrops,
                appendOnly: me.appendOnly,
                allowLeafInserts: me.allowLeafInserts,
                allowParentInserts: me.allowParentInserts,
                expandDelay: me.expandDelay,
                dropHighlightColor: me.nodeHighlightColor,
                dropHighlight: me.nodeHighlightOnDrop
            });
        }
    }
});