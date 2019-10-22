Ext.define("SMSWeb.store.sale.SaleStore",{
	extend:'Ext.data.Store',
	alias:'widget.SaleStore',
	model:'SMSWeb.model.sale.SaleModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/findItemsByPid',
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
//        	}
//        },
	autoLoad:false
});