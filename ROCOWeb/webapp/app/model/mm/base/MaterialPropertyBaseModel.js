Ext.define("SMSWeb.model.mm.base.MaterialPropertyBaseModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		{name:'propertyDesc'},
		{name:'propertyCode'},
		{name:'orderby'},
		{name:'infoDesc'}
	]
});