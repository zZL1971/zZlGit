Ext.define('Ext.ux.window.ListViewWindow', {
	extend : 'Ext.window.Window',
	title : '数据弹窗-BPM1.0',
	layout:'border',
	modal : true,
	width:900,
	height:500,
	maximizable:true,
	moduleData:null,
	defaultQueryCondition:{},
	initComponent : function() {
		var me = this;
		//表格
		var grid = Ext.create('Ext.ux.grid.UXGrid',{
			region : 'center',
			moduleData:me.moduleData,
			pOrderCode:me.defaultQueryCondition.pOrderCode,
			pageToolbar:false,
			gridCurd:true,
			gridLoad:true,
			defaultQueryCondition:me.defaultQueryCondition
		});
		
		me.items=[grid];
		me.callParent(arguments);
	}
});