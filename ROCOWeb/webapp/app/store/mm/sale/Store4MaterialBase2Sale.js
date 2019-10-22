Ext.define("SMSWeb.store.mm.sale.Store4MaterialBase2Sale",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.base.MaterialBaseModel',
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/mm/queryMaterialGrid',
		extraParams :{'is4Sale':'true'},
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
				var formValues = Ext.ComponentQuery.query("MaterialMainFormBase2SaleView")[0].getValues();
				var _kunnr = Ext.ComponentQuery.query("MaterialBase2SaleWindow")[0].kunnr;
				if("FB"==formValues.type){
					formValues.isStandard="0";
				}else{
					formValues.isStandard="1";
				}
				var orderTypeValue=Ext.getCmp("orderType").getValue();
				formValues.orderType=orderTypeValue;
				formValues.kunnr=_kunnr;
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});