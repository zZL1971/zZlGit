Ext.define("SMSWeb.model.sys.SysBzModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'zid'},
		{name:'remark'},
		{name:'text'}
	]
});