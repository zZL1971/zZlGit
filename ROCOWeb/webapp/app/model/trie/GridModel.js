Ext.define("SRM.model.trie.GridModel",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id',type:'string'},
		{name:'stat',type:'string'},
		{name:'keyVal',type:'string'},
		{name:'type',type:'string'},
		{name:'typeDesc',type:'string'},
		{name:'typeKey',type:'string'},
		{name:'orderBy',type:'int'},
		{name:'descZhCn',type:'string'},
		{name:'descZhTw',type:'string'},
		{name:'descEnUs',type:'string'},
		
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateUser',type:'string'},
		{name:'createUser',type:'string'},
		{name:'rowStatus',type:'string'},
		{name:'trieTree.id',type:'string',defalutValue:''}
	],
	proxy:{
		type:'ajax',
		url:'core/trie/list',
		api: {
            create: '/core/dd/save',
            read: 'core/trie/list'//,
            //update: 'php/task/update.php',
            //destroy: 'php/task/delete.php'
        },
        headers:{
			"Content-Type":"application/json; charset=utf-8"        
        },
        listeners:{  
	        exception:function( proxy, response, operation, eOpts ) {   
	           var res= Ext.JSON.decode(response.responseText);
	           if(!res.success){
	           		Ext.Msg.show({title: '【'+res.errorCode+'】数据字典查询-错误提示', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
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