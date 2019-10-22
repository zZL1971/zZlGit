Ext.define("SMSWeb.store.mm.base.Store4MaterialBaseFile",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.base.MaterialFileBaseModel',
	proxy:{
		type:'ajax',
		url:'main/mm/queryMaterialFile',
		reader:{
			type:'json'
		},
		writer:{
			type:'json'
		},
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad:false
});