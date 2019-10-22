Ext.define("SMSWeb.store.main.Store4Menu",{
	extend:'Ext.data.TreeStore',
	alias:'widget.Store4Menu',
	model:'SMSWeb.model.main.MenuModel',
	defaultRoodId:'root',
	proxy:{
		type:'ajax',
		url:'core/main/menu',
		reader:  'json',
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad:true
});