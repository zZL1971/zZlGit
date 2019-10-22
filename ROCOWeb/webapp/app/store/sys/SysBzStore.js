Ext.define("SMSWeb.store.sys.SysBzStore",{
	extend:'Ext.data.Store',
	alias:'widget.SysBzStore',
	model:'SMSWeb.model.sys.SysBzModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sysBz/findSysBzsByZid',
		reader:{
			type:'json',
			root:'content',
			totalProperty :'totalElements'
		},
		writer:{
			type:'json'
		}
	},
//	listeners: {
//        beforeload:function(store,operation,epts){
//        		if(Ext.getCmp('custMainView')){
//        			var searchs = Ext.getCmp('custMainView').getComponent('custForm').getSearchs();
//        			operation.params = searchs;
//        		}
//        	}
//        },
	autoLoad:false
});