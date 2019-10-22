Ext.define("SMSWeb.store.component.ComponentGridStore",{
	extend:'Ext.data.Store',
	alias:'widget.ComponentGridStore',
	model:'SMSWeb.model.component.ComponentModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'delivery/componentList',
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
				var formValues = Ext.ComponentQuery.query("ComponentFormView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});