Ext.define("MeWeb.view.system.user.SysUserMainView", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.SysUserMainView',
	layout : 'border',
	border : false,
	items:[{
		layout:'border',
		//title:'用户管理',
		region : 'center',
		//margin:'5',
		tbar : [{
			xtype : 'button',
			text : '查询',
			id : 'query',
			tooltip : '查询',
			iconCls : 'table_search'
		},{
			xtype : 'button',
			text : '新增',
			id : 'add',
			tooltip : '新增',
			iconCls : 'table_add'
		}],
		items : [{
				xtype : 'user.SearchForm',
				region : 'north'
			},{
				xtype : 'user.Grid',
				region : 'center'
			}]
	}]
});