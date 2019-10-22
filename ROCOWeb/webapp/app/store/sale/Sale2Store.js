Ext.define("SMSWeb.store.sale.Sale2Store",{
	extend:'Ext.data.Store',
	alias:'widget.Sale2Store',
	model:'SMSWeb.model.sale.Sale2Model',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/sale/list',
		reader:{
			type:'json',
			root:'content',
			idProperty:'zdefind',
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
				var formValues = Ext.ComponentQuery.query("Sale2FormView")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});