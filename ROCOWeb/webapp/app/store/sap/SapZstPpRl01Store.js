Ext.define("SMSWeb.store.sap.SapZstPpRl01Store",{
	extend:'Ext.data.Store',
	alias:'widget.SapZstPpRl01Store',
	model:'SMSWeb.model.sap.SapZstPpRl01Model',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sapZstPpRl01/list',
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