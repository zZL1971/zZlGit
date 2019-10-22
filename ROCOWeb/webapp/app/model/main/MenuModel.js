Ext.define("SMSWeb.model.main.MenuModel",{
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime'},
		{name:'updateTime'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		
		{name:'text',mapping:'descZhCn',sortable:true},
		{name:'url',type:'string',sortable:true},
		{name:'icon',type:'string',sortable:true},
		{name:'pid',type:'string',sortable:true},
		{name:'oderby',type:'int',sortable:true},
		{name:'leaf',type:'boolean',sortable:true},
		{name:'expanded',type:'boolean',sortable:true,defaultValue:true}
		
	    
	]
});