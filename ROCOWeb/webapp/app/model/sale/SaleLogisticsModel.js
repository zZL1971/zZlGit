Ext.define("SMSWeb.model.sale.SaleLogisticsModel",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id'},
		{name:'ppcDate',type:'date',dateFormat:'Y-m-d'},
		{name:'psDate',type:'date',dateFormat:'Y-m-d'},
		{name:'pcDate',type:'date',dateFormat:'Y-m-d'},
		{name:'pbDate',type:'date',dateFormat:'Y-m-d'},
		{name:'poDate',type:'date',dateFormat:'Y-m-d'},
		{ name: 'kunnr', type: 'string' },
		{ name: 'saleFor', type: 'string' },
		{ name: 'deliveryDay', type: 'string' },
		{ name: 'kunnrS', type: 'string' },
		{ name: 'pid', type: 'string' }
	]
});