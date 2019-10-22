Ext.define("SMSWeb.store.mm.pc.Store4ConditionValue",{
	extend:'Ext.data.Store',
	fields: ['ID','DESC_ZH_CN'],
	proxy:{
		type:'ajax',
		url:'main/mm/queryBySysDataDicd',
		extraParams :{'keyVal':'MATERIAL_PRICE_CONDITION'},
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
	autoLoad:true
});