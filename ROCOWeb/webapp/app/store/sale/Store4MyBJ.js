Ext.define("SMSWeb.store.sale.Store4MyBJ",{
	extend:'Ext.data.Store',
	model:'SMSWeb.model.sale.MyBJModel',
//	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/myGoods/queryMyBjList',
		reader:{
			type:'json',
//			root:'content',
//			totalProperty :'totalElements'
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