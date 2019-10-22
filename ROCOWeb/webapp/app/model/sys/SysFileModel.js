Ext.define("SMSWeb.model.sys.SysFileModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime'},
		{name:'updateTime'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		{name:'uploadFileNameOld'},
		{name:'remark'}
	]
});