var queryType=1;
Ext.QuickTips.init();
Ext.Loader.setConfig({
			enabled : true
		});
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');// ����IE·��ʶ��bug by ab
Ext.application({
	name : 'SMSWeb',
	appFolder : "app",
	launch : function() {
		Ext.create('Ext.container.Viewport', {
					layout : 'fit',
					padding : "0 0 0 0",
					items : {
						xtype : 'SaleOutputDataPanel' 
					}
				});
	},
	controllers : ['sale.SaleController']
});
