var queryType = "1";
Ext.QuickTips.init();
Ext.Loader.setConfig({
			enabled : true
		});
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');// 修正IE路径识别bug by ab
//Ext.util.CSS.swapStyleSheet("theme", "extjs/4.2.1/resources/css/ext-all-gray.css");
Ext.application({
			name : 'SMSWeb',
			appFolder : "app",
			launch : function() {
				Ext.create('Ext.container.Viewport', {
							layout : 'fit',
							padding : "0 0 0 0",
							items : {
								xtype : 'NewSaleItemReportPanel'
							}
						});
			},
			controllers : ['sale.SaleController']
		});
