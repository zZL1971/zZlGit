Ext.QuickTips.init();
Ext.Loader.setConfig({
			enabled : true
		});
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');// 修正IE路径识别bug by ab
Ext.application({
			name : 'SMSWeb',
			appFolder : "app",
			launch : function() {
				Ext.create('Ext.container.Viewport', {
							layout : 'fit',
							padding : "0 0 0 0",
							items : {
								xtype : 'ComponentMainView'
							}
						});
			},
			controllers : ['component.ComponentController']
		});
