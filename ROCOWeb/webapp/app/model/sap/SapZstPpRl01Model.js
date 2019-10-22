Ext.define("SMSWeb.model.sap.SapZstPpRl01Model", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		
		{name:'werks'},
		{name:'week'},
		{name:'libai'},
		{name:'text'},
		{name:'work'},
		{name:'werksDate',type:'date',dateFormat:'Y-m-d'}
	]
});