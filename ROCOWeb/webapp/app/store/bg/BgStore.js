Ext.define("SMSWeb.store.bg.BgStore",{
	extend:'Ext.data.Store',
	alias:'widget.BgStore',
	model:'SMSWeb.model.bg.BgModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/bg/list',
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
        		var formValues = Ext.ComponentQuery.query("BgFormView")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});