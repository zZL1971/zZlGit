Ext.define("SMSWeb.view.sale.SaleCheckSheetMainView", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.SaleCheckSheetMainView',
	layout : 'border',
	autoScroll : true,
	border : false,
	items : [ {
		tbar : [ {
			xtype : 'button',
			text : '查询',
			id : 'csquerySale',
			iconCls : 'table_search'
		} ],
		region : 'north',
		xtype : 'SaleCheckShFormView'
	}
	,
	{
	 region:'center',
	xtype:'SaleCheckSheetGridView'
	}
	]
});