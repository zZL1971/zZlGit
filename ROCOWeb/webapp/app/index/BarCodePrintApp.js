Ext.QuickTips.init();
Ext.Loader.setConfig({
			enabled : true
		});
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');
Ext.application({
			name : 'SMSWeb',
			appFolder : "app",
			launch : function() {
				Ext.create('Ext.container.Viewport', {
							layout : 'fit',
							padding : "0 0 0 0",
							items : {
								xtype : 'BarCodePrint'
							}
						});
			},
			controllers : ['print.PrintController']
		});
