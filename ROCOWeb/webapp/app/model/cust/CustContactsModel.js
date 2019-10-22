Ext.define("SMSWeb.model.cust.CustContactsModel",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id'},
		{name:'createUser'},
		{name:'updateUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
	   	{ name: 'parnr', type: 'string'},
        { name: 'namev', type: 'string'},
        { name: 'abtnr', type: 'string' },
        { name: 'telNumber', type: 'string' },
        { name: 'vtext', type: 'string' }
	]
});