Ext.define("SMSWeb.store.xml.XMLInfoStore",{
	extend:'Ext.data.Store',
	alias:'widget.XMLInfoStore',
	model:'SMSWeb.model.xml.XMLInfoModel',
	remoteSort:false,
	pageSize:25,
	/*proxy:{
		type:'ajax',
		url:'main/sale/list',
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
	},*/
	listeners: {
        beforeload:function(store,operation,epts){
				var formValues = Ext.ComponentQuery.query("Sale2FormView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
        },
	autoLoad:false
});