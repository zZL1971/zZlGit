Ext.define("SMSWeb.store.cust.CustXindaiStore",{
	extend:'Ext.data.Store',
	alias:'widget.CustXindaiStore',
	model:'SMSWeb.model.cust.CustXindaiModel',
	remoteSort:false,
	pageSize:25,
	proxy:{
		type:'ajax',
		url:'main/cust/findCustXindaiData',
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
        	var endDate = Ext.ComponentQuery.query("datefield[id=endDate1]")[0];
        	var startDate = Ext.ComponentQuery.query("datefield[id=startDate1]")[0];
        	var kunnr = Ext.ComponentQuery.query("textfield[id=kunnr]")[0];
        	var values={endDate:endDate.value,startDate:startDate.value,kunnr:kunnr.value};
			Ext.apply(store.proxy.extraParams,values);
        	}
    },
	autoLoad:false
});