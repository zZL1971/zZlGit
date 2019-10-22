Ext.define("SMSWeb.store.mm.myGoods.Store4MyGoods",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.mm.myGoods.MyGoodsModel',
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/myGoods/queryMyGoodsList',
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
				var formValues = Ext.ComponentQuery.query("MyGoodsMainFormView")[0].getValues();
				var _kunnr = Ext.ComponentQuery.query("MyGoodsWindow")[0].kunnr;
				var _bgOrderType = Ext.ComponentQuery.query("MyGoodsWindow")[0].bgOrderType
				formValues.kunnr = _kunnr;
				formValues.bgOrderType = _bgOrderType;
				Ext.apply(store.proxy.extraParams, formValues);
        	}
    },
	autoLoad:false
});