Ext.define("SMSWeb.model.mm.base.MaterialFileBaseModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime'},
		{name:'updateTime'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		{name:'status'},
		{name:'statusdesc'},
		{name:'uploadFileNameOld'},
		{name:'uploadFilePath'},
		{name:'uploadFileName'},
		{name:'remark'}
	]
});