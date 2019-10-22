Ext.define("SMSWeb.store.mm.base.Store4SysDataDicdBase",{
	extend:'Ext.data.Store',
	fields: ['ID','DESC_ZH_CN','KEY_VAL'],
	proxy:{
		type:'ajax',
		url:'main/mm/queryBySysDataDicd',
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