Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Loader.setConfig({
		enabled:true
	});
	Ext.form.Field.prototype.msgTarget='title';
	Ext.application({
		name : 'SMSWeb',
		appFolder : "app",
		launch:function(){
	        Ext.create('Ext.container.Viewport', {
	        	layout:'fit',
	        	padding : "0 0 0 0",
	            items: {
					xtype:'tm1stview'
	            }
	        });
		},
		controllers:[
			'tm.TM1stController'
		]
	});
})
