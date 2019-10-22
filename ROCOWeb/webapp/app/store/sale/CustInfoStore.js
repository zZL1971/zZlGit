Ext.define("SMSWeb.store.sale.CustInfoStore",{
	extend:'Ext.data.Store',
	alias:'widget.CustInfoStore',
	model:'SMSWeb.model.sale.CustInfoModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/cust/gainCustInfo',
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
				formValues = Ext.ComponentQuery.query("NewCustWindow form")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});