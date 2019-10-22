Ext.define("SRM.model.trie.PricePolicyModel",{
	extend:'Ext.data.Model',
	fields:[
		{name:'id',type:'string'},
		{name:'isable',type:'string'},
		{name:'discotyle',type:'string'},
		{name:'kunnr',type:'string'},
		{name:'name1',type:'string'},
		
		//{name:'starttime',type:'date',dateFormat:'Y-m-d H:i:s'},
		//{name:'endtime',type:'date',dateFormat:'Y-m-d H:i:s'},
		
		{name:'starttime',type:'string'},
		{name:'endtime',type:'string'},
		
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{name:'updateUser',type:'string'},
		{name:'createUser',type:'string'},
		{name:'saleorder',type:'string'}
	],
	proxy:{
		type:'ajax',
		//url:'core/trie/list',
		api: {
            create: '/core/dd/savepricepolicy',
            //read: 'core/trie/list'
        },
        headers:{
			"Content-Type":"application/json; charset=utf-8"        
        },
        listeners:{  
	        exception:function( proxy, response, operation, eOpts ) {   
	           var res= Ext.JSON.decode(response.responseText);
	           if(!res.success){
	           		Ext.Msg.show({title: '【'+res.errorCode+'】testcgg', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
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