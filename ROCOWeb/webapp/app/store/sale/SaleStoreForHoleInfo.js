Ext.define("SMSWeb.store.sale.SaleStoreForHoleInfo",{
	extend:'Ext.data.Store',
	alias:'widget.SaleStoreForHoleInfo',
	model:'SMSWeb.model.sale.SaleModelForHoleInfo',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/listForHoleInfo',
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
        		var formValues;
				formValues = Ext.ComponentQuery.query("NewSaleWindowForHoleInfo form")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});