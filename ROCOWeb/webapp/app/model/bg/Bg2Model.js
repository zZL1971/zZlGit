Ext.define("SMSWeb.model.bg.Bg2Model",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string'},
		//{ name: 'pid', type: 'string'},保存时不能传外键pid，会请求不到后台
		{name:'createUser'},
		{name:'updateUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		//{name:'rowStatus'},
		
		{name:'posex'},
		{ name: 'matnr', type: 'string' },
		{ name: 'mtart', type: 'string' },
		{ name: 'maktx', type: 'string' },
		
		
		{ name: 'serialNumber', type: 'string' },
		
		{ name: 'name', type: 'string' },
		{ name: 'spec', type: 'string' },
		{ name: 'area', type: 'string' },
		{ name: 'unit', type: 'string' },
		{ name: 'colour', type: 'string' },
		{ name: 'colourDesc', type: 'string' },
		{ name: 'itemDesc', type: 'string' },
		{ name: 'remark', type: 'string' },
		{ name: 'isSale', type: 'string' },
		
		
		{ name: 'type', type: 'string' },
		{ name: 'itemDesc', type: 'string' },
		{ name: 'unitPrice', type: 'float' },
		{ name: 'zheKou', type: 'float' },
        { name: 'zheKouJia', type: 'float' },
//        { name: 'active', type: 'bool' }
        { name: 'amount', type: 'float' },
        { name: 'touYingArea', type: 'float' },
        { name: 'totalPrice', type: 'float' },
        { name: 'status', type: 'string' },
        { name: 'materialHeadId', type: 'string' },
        { name: 'isStandard', type: 'string' },
        { name: 'myGoodsId', type: 'string' },
        
        { name: 'saleItemId', type: 'string' }
        
	]
});