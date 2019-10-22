Ext.define("SMSWeb.store.sys.SysMesSendStore",{
	extend:'Ext.data.Store',
	alias:'widget.SysMesSendStore',
	model:'SMSWeb.model.sys.SysMesSendModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'core/sysMesSend/list',
		reader:{
			type:'json',
			root:'content',
			totalProperty :'totalElements'
		},
		writer:{
			type:'json'
		}
	},
	listeners: {
        beforeload:function(store,operation,epts){
        		var formValues;
				formValues = Ext.ComponentQuery.query("SysMesSendMainView form")[0].getValues();
//				Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
//				});
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});