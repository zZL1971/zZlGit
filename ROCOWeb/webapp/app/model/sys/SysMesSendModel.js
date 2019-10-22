Ext.define("SMSWeb.model.sys.SysMesSendModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'sendUser'},
		{name:'receiveUser'},
		{name:'csUser'},
		{name:'sendFlag'},
		{name:'msgType'},
		{name:'msgTitle'},
		{name:'msgBody'},
		{name:'receiveUserEmail'},
		{name:'sendUserEmail'},
		{name:'sendUserEmailPsw'},
		{name:'hasReaded'},
		{name:'sendTime',type:'date',dateFormat:'Y-m-d H:i:s'}
	]
});