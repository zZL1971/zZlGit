Ext.Loader.setConfig({enabled:true});//必须加这句，否则会报错
Ext.application({
		//定义命名控件
		name:'SMSWeb',
		//定义文件夹
		appFolder:'app',
		requires:['SMSWeb.view.login.Login'],
		//页面完全加载后将运行此方法
		launch:function() {
	        Ext.create('SMSWeb.view.login.Login',{loginUser:CURR_USER_LOGIN_NO}).show();
	    },
	    controllers : ['login.LoginController']
	}
);