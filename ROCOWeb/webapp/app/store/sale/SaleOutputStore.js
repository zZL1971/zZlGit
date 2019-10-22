Ext.define("SMSWeb.store.sale.SaleOutputStore",{
	extend:'Ext.data.Store',
	alias:'widget.SaleOutputStore',
	model:'SMSWeb.model.sale.SaleOutputModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/getSaleOutputLogList',
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
				var formValues = Ext.ComponentQuery.query("form[id=queryForm]")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});