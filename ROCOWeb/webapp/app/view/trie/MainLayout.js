Ext.define('SRM.view.trie.MainLayout', {
	extend: 'Ext.panel.Panel',
	layout: 'border',
	alias:'widget.trie.MainLayout',
	border:false,
	frame:false,
    defaults: {
        split: true,
        collapsible: true
    },
    items:[{
    	region: 'west',
    	xtype: 'trie.Tree',
        margin: '5 0 5 5',
    	width: 200,
    	tools:[{
			type:'plus',
			tooltip: '添加主模块'
		},{
			type:'refresh',
			tooltip: '刷新',
			handler: function(event, toolEl, panel){
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
		}]
    },{
    	region: 'center',
        margin: '5 5 5 0',
        collapsible: false,
        xtype:'trie.Grid'
    }]
});