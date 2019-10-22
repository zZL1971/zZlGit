Ext.define("SMSWeb.store.sale.TcStore",{
	extend:'Ext.data.Store',
	alias:'widget.TcStore',
	model:'SMSWeb.model.sale.TcModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/listForTc',
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
				formValues = Ext.ComponentQuery.query("NewTcPanel form")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:true
});