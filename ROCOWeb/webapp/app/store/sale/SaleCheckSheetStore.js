Ext.define("SMSWeb.store.sale.SaleCheckSheetStore",{
	extend:'Ext.data.Store',
	alias:'widget.SaleCheckSheetStore',
	model:'SMSWeb.model.sale.SaleCheckSheetModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		//url:'main/sale/listchecksheet?Kunnr='+'CS00003&StartDate=20160101&EndDate=20161216',
		url:'main/sale/listchecksheet',
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
		console.log("comein");
				var formValues = Ext.ComponentQuery.query("SaleCheckShFormView")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});