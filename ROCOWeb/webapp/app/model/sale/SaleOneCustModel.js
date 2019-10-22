Ext.define("SMSWeb.model.sale.SaleOneCustModel",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id'},
		{name:'createUser'},
		{name:'updateUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		//{name:'rowStatus'},
		{ name: 'kunnr', type: 'string' },
		{ name: 'anred', type: 'string' },
		{ name: 'saleOneCustName1', type: 'string' },
		{ name: 'street', type: 'string' },
		{ name: 'pstlz', type: 'string' },
        { name: 'mcod3', type: 'string' },
        { name: 'land1', type: 'string', value:'CN'},
        { name: 'regio', type: 'string' },
        { name: 'telf1', type: 'string' },
        { name: 'ort02', type: 'string' },
        { name: 'socAddress', type: 'string' },
        { name: 'saleOneCustType', type: 'string' }
	]
});