Ext.define("SMSWeb.view.main.TreeView", {
			extend : 'Ext.tree.Panel',
			alias : 'widget.TreeView',
			store : 'main.Store4Menu',
			enableKeyNav : true,
			columnLines : true,
			collapsible : true,
			rootVisible : false,
			tools : [{
				type : 'refresh',
				tooltip : 'Refresh form Data',
				handler : function(event, toolEl,panel) {
						panel.ownerCt.getStore().reload();
				}
			},{
				type:'expand',
				tooltip: '展开',
				hidden:true,
				handler: function(event, toolEl, panel){
					//panel.ownerCt.getStore().reload();
					panel.ownerCt.expandAll();
					panel.query("tool[type=expand]")[0].hide(true);
					panel.query("tool[type=collapse]")[0].show(true);
				}
			},{
				type:'collapse',
				tooltip: '收拢',
				handler: function(event, toolEl, panel){
					panel.ownerCt.collapseAll();     
					panel.query("tool[type=expand]")[0].show(true);
					panel.query("tool[type=collapse]")[0].hide(true);
				}
			}
			]
		});