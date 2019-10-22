Ext.define("SMSWeb.store.tm.NewOrderDetailStore",{
	extend:'Ext.data.Store',
	alias:'widget.NewOrderDetailStore',
	model:'SMSWeb.model.tm.NewOrderDetailModel',
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'report/orderDetail',
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
        	console.log(store);
        		var startTime=$("#datefield-1014-inputEl").val();
        		var endTime=$("#datefield-1015-inputEl").val();
        		var NewOrderDetail = Ext.ComponentQuery.query("NewOrderDetail")[0];
        		var kunnr = NewOrderDetail.ownerCt.kunnr;
        		var value={startTime:startTime,kunnr:kunnr,endTime:endTime};
				Ext.apply(store.proxy.extraParams, value);
        	}
    },
	autoLoad:false
});