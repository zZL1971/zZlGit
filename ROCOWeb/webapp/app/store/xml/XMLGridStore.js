Ext.define("SMSWeb.store.xml.XMLGridStore",{
	extend:'Ext.data.Store',
	alias:'widget.XMLStore',
	model:'SMSWeb.model.xml.XMLModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'xmlcontrol/list',
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
				var formValues = Ext.ComponentQuery.query("XMLFormView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});