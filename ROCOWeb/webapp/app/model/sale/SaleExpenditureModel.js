Ext.define("SMSWeb.model.sale.SaleExpenditureModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{name:'miaoshu',type:'string'},
		{name:'wsize',type:'string'},
		{name:'materialHeadId',type:'string'},
		{name:'matnr',type:'string'},
		{ name: 'number', type: 'string' },
		{ name: 'unit', type: 'string' },
		{ name:'chanxian',type: 'string'},
		{ name:'price',type: 'string'}
	]
});