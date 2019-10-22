Ext.define("SMSWeb.store.cust.Cust2Store",{
	extend:'Ext.data.Store',
	alias:'widget.Cust2Store',
	model:'SMSWeb.model.cust.Cust2Model',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/cust/findItemsByPid',
		reader:{
			type:'json',
			root:'content',
			totalProperty :'totalElements'
		},
		writer:{
			type:'json'
		},
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
//	listeners: {
//        beforeload:function(store,operation,epts){
//        		if(Ext.getCmp('custMainView')){
//        			var searchs = Ext.getCmp('custMainView').getComponent('custForm').getSearchs();
//        			operation.params = searchs;
//        		}
//        }
//  },
	autoLoad:false
});