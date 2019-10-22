Ext.Loader.setConfig({enabled:true});//必须加这句，否则会报错
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');//修正IE路径识别bug by ab
Ext.application({
		//定义命名控件
		name:'SMSWeb',
		//定义文件夹
		appFolder:'app',
		requires:['SMSWeb.view.report.ReportView'],
		launch:function(){
	        Ext.create('Ext.container.Viewport', {
	        	layout:'fit',
	        	padding : "0 0 0 0",
	            items: {
	            	xtype: 'reportView'
	            }
	        });
		},
		controllers:[
			'report.ReportController'
		]
	}
);