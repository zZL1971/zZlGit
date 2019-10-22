Ext.define("SMSWeb.store.sale.SaleItemStoreForBg",{
	extend:'Ext.data.Store',
	alias:'widget.SaleItemStoreForBg',
	model:'SMSWeb.model.sale.SaleItemModelForBg',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/listSaleItemForWin',
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