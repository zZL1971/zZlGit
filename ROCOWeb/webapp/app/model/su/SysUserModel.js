Ext.define("SMSWeb.model.su.SysUserModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime'},
		{name:'updateTime'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		{name:'loginNo'},
		{name:'userNo'},
		{name:'userName'},
		{name:'sex'},
		{name:'password'},
		{name:'deptName'},
		{name:'telphone'},
		{name:'email'}
	]
});