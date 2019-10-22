Ext.define("SMSWeb.store.sale.SaleComplaintStore",{
	extend:'Ext.data.Store',
	alias:'widget.SaleComplaintStore',
	model:'SMSWeb.model.sale.SaleComplaintModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/myGoods/getComplainid',
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
/*	listeners: {
        beforeload:function(store,operation,epts){
				var formValues = Ext.ComponentQuery.query("XMLFormView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
				Ext.apply(store.proxy.extraParams);
        	}
        },*/
	autoLoad:false
});