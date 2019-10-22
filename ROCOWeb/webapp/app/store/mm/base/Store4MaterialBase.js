Ext.define("SMSWeb.store.mm.base.Store4MaterialBase",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.base.MaterialBaseModel',
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/mm/queryMaterialGrid',
		extraParams :{'isStandard':'1'},
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
				var formValues = Ext.ComponentQuery.query("MaterialMainFormBaseView")[0].getValues();
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});