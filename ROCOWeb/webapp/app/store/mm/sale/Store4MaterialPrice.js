Ext.define("SMSWeb.store.mm.sale.Store4MaterialPrice",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.pc.ModelMaterialPrice',
	groupField : 'type',
	proxy:{
		type:'ajax',
		url:'main/mm/queryMaterialPrice',
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