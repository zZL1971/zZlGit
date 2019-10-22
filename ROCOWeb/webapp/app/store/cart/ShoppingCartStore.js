Ext.define("SMSWeb.store.cart.ShoppingCartStore",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.cart.ShoppingCartModel',
	pageSize:25,
	remoteSort:false,
	proxy:{
		type:'ajax',
		url:'main/cart/goods',
		extraParams :{'isStandard':'1'},
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
	listeners: {
        beforeload:function(store,operation,epts){
			var formValues = Ext.ComponentQuery.query("ShoppingCart2SaleView")[0].getValues();
			Ext.apply(store.proxy.extraParams, formValues);
    	}
},
	autoLoad:false
});