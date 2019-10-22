Ext.onReady(function(){
	Ext.QuickTips.init();
	Ext.Loader.setConfig({
		enabled:true
	});
	Ext.form.Field.prototype.msgTarget='title';
	Ext.application({
		name : 'SRM',
		appFolder : "app",
		launch:function(){
	        Ext.create('Ext.container.Viewport', {
	        	layout:'fit',
	            items: {
	            	width: 750,
	            	height: 530,
	            	xtype: 'trie.MainLayout'
	            }
	        });
		},
		controllers:[
			'trie.TrieTreeController'
		]
	});
})
