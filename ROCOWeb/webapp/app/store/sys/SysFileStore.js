Ext.define("SMSWeb.store.sys.SysFileStore", {
	extend : 'Ext.data.Store',
	model : 'SMSWeb.model.sys.SysFileModel',
	proxy : {
		type : 'ajax',
		url : 'main/sysFile/querySysFile',
		reader : {
			type : 'json'
		},
		writer : {
			type : 'json'
		},
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad : false
});