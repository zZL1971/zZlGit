Ext.define("MeWeb.model.system.user.SysUserModel",{
	extend:'Ext.data.Model',
	fields:[
		//业务字段
		{name:'loginNo',type:'string'},
		{name:'userName',type:'string'},
		{name:'userType',type:'string'},
		{name:'userGroup',type:'string'},
		{name:'sex',type:'string'},
		{name:'userNo',type:'string'},
		
		//系统默认字段
		{name:'id',type:'string'},
		{name:'createTime',type:'date'},
		{name:'updateTime',type:'date'},
		{name:'updateUser',type:'string'},
		{name:'createUser',type:'string'},
		{name:'rowStatus',type:'string'}
	],
	proxy:{
		type:'ajax',
		//url:'core/user/list',//hibernate查询
		url:'core/ext/grid/page/SYS_USER_QUERY',//jdbc查询
		
        headers:{
			"Content-Type":"application/json; charset=utf-8"        
        },
        listeners:{  
	        exception:function( proxy, response, operation, eOpts ) {   
	           var res= Ext.JSON.decode(response.responseText);
	           if(!res.success){
	           		Ext.Msg.show({title: '【'+res.errorCode+'】-错误提示', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
	           }
	        }  
	    },
		reader:{
			type:'json',
			root:'content',
			totalProperty :'totalElements'
		},
		writer:{
			type:'json'
		}
	}
});