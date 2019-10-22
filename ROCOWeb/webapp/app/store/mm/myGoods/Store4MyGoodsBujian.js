Ext.define("SMSWeb.store.mm.myGoods.Store4MyGoodsBujian",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.myGoods.MyGoodsModel',
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/myGoods/queryMyBjList',
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
				var formValues = Ext.ComponentQuery.query("MyGoodsBujianMainFormView")[0].getValues();
				var pid = Ext.ComponentQuery.query("MyGoodsBujianWindow")[0].formId;
				formValues["pid"]=pid;
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});