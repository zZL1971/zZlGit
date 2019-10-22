Ext.define("SMSWeb.store.delivery.DeliveryGridStore",{
	extend:'Ext.data.Store',
	alias:'widget.DeliveryGridStore',
	model:'SMSWeb.model.delivery.DeliveryModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'delivery/lineList',
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
				var formValues = Ext.ComponentQuery.query("DeliveryFormView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});