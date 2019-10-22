Ext.define("SMSWeb.model.bg.BgModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{ name: 'pid', type: 'string' },
		{name:'createUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		
//		{ name: 'serialNumber', type: 'string' },
//		{ name: 'type', type: 'string' },
//		{ name: 'colour', type: 'string' },
//		{ name: 'unitPrice', type: 'float' },
//		{ name: 'zheKou', type: 'float' },
//		{ name: 'zheKouJia', type: 'float' },
//		{ name: 'amount', type: 'float' },
//		{ name: 'touYingArea', type: 'float' },
//		{ name: 'totalPrice', type: 'float' },
		
		{ name: 'bgCode', type: 'string' },
		{ name: 'orderCode', type: 'string' },
		{ name: 'clients', type: 'string' },
		{ name: 'bgType', type: 'string' },
		{ name: 'contacts', type: 'string' },
		{ name: 'tel', type: 'string' },
		{ name: 'orderStatus', type: 'string' },
		{name:'updateUser',type:'string'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{ name: 'reason', type: 'string'},
		{name:'orderDate',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'orderType',type:'string'}
		
	]
});