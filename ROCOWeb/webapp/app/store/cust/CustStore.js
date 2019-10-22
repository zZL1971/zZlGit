Ext.define("SMSWeb.store.cust.CustStore",{
	extend:'Ext.data.Store',
	alias:'widget.CustStore',
	model:'SMSWeb.model.cust.CustModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/cust/list',
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
				if("undefined"!=typeof(Ext.ComponentQuery.query("CustFormView")[0])){
					formValues = Ext.ComponentQuery.query("CustFormView")[0].getValues();
				}else{
					formValues = Ext.ComponentQuery.query("NewCustWindowForSale form")[0].getValues();
				}
			    
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});