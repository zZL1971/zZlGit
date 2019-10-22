Ext.define("MeWeb.model.system.menu.SysMenuModel", {
	extend:'Ext.data.Model',
	fields:[
		{name:'id',type:'string'},
		{name:'orderBy',type:'string'},
		{name:'descZhCn',type:'string'},
		{name:'descZhTw',type:'string'},
		{name:'descEnUs',type:'string'},
		{name:'expanded',type:'boolean',defaultValue:true},
		{name:'parent.id',type:'string'},
		{name:'leaf',type:'boolean',defaultvalue:true},
		//{name:'checked',type:'boolean'},
		
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateUser',type:'string'},
		{name:'createUser',type:'string'},
		{name:'rowStatus',type:'string'},
		{name:'remark',type:'string'}
	]
});