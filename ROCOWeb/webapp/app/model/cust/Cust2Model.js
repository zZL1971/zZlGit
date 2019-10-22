Ext.define("SMSWeb.model.cust.Cust2Model",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id'},
		{name:'createUser'},
		{name:'updateUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
	   	{ name: 'startDate', type: 'date',dateFormat: 'Y-m-d'},
        { name: 'endDate', type: 'date',dateFormat: 'Y-m-d'},
        {name:'fanDianName'},
        { name: 'zheKou', type: 'float' },
//      { name: 'active', type: 'bool' }
        { name: 'total', type: 'float' },
        { name: 'yuJi', type: 'float' },
        { name: 'shiJi', type: 'float' },
        { name: 'shengYu', type: 'float' },
        { name: 'status', type: 'float' }
	]
});