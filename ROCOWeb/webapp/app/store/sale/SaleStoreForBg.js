Ext.define("SMSWeb.store.sale.SaleStoreForBg",{
	extend:'Ext.data.Store',
	alias:'widget.SaleStoreForBg',
	model:'SMSWeb.model.sale.SaleModelForBg',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/listForWin',
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
				formValues = Ext.ComponentQuery.query("NewSaleWindowForBg form")[0].getValues();
/*				var _queryBgType = '2';
				formValues.queryBgType= _queryBgType;*/
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});