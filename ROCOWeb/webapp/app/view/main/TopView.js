Ext.define('SMSWeb.view.main.TopView', {
	extend : 'Ext.toolbar.Toolbar',
	alias : 'widget.maintop',
	height : 54,
	defaults : {
		xtype : 'buttontransparent'
	},
	requires: [
		'Ext.ux.window.Notification'
	],
	cls : 'ux-toolbar-header',
	items : [{
				xtype : 'image',
				src : ""
			}, {
				xtype : 'label',
				style : 'font-weight: bold;font-size:30px;color:#fff;',
				text : 'ROCO'

			}, {
				xtype : 'label',
				height : 12,
				style : 'font-weight: bold;font-size:14px;vertical-align: text-bottom;color:#fff;',
				text : 'Sales Manager 2019'

			}, '->',
			{
				text:'我要吐槽',
				id:'grumble',
				glyph:0xf0a4
			}, 
			{
				text : '帮助',
				glyph : 0xf009,
				menu : [{
							xtype:'tbtext',
							style:'margin-bottom:10px;margin-top:5px;',
							text : '<a href="LK-劳卡家具ERP前端接单系统用户操作手册（经销商）_v3.pdf" target="_blank" >前端接单系统用户操作手册（经销商）</a>'
						}, {
							xtype:'tbtext',
							style:'margin-bottom:10px',
							text : '<a href="ERP-前端接单系统经常遇的问题及处理方法.pdf" target="_blank" >ERP-前端接单系统经常遇的问题及处理方法</a>'
						},{
							xtype:'tbtext',
							style:'margin-bottom:10px',
							text : '<a href="橱柜订单录单操作流程标准 .docx" target="_blank" >橱柜订单录单操作流程标准</a>',
							hidden : !(CURR_USER_KUNNR_CUP.indexOf('X')>-1)
						}]
			},{
				xtype: 'tbtext', text: '欢迎<font color=blue>'+CURR_USER_LOGIN_USER_NAME+'</font>登录　'
			}, {
				text : '我的消息',
				id : 'myMsg',
				glyph : 0xf0e0,
				menu : [{
					text : '发送系统消息',
					hidden:CURR_USER_LOGIN_NO=="admin"?false:true,
					itemId:'sendMessage'
				}]
			}, {
				text : '个人管理',
				glyph : 0xf009,
				menu : [{
							text : '密码设置',
							itemId:'setpwd'
						}, {
							text : '短信通知号码设置',
				            itemId:'setTelephoneNum',
				            hidden:!(USER_ROLE_ID.indexOf('5C2wEJEdUB6hknTTad5pgb')>-1)
						}]
			}, {
				text : '注销',
				glyph : 0xf011,
				handler : function(me, epts) {
					me.up('toolbar').fireEvent('loginout');
				}
			},{
				text : '重新登录',
				glyph : 0xf011,
				id:'relogin',
				hidden:true,
				listeners:{
					click:function(me,e,eopts){
						var keys_;
						
						Ext.Ajax.request({
							url:'/se/r/',
							async:false,
							success:function(response,opts){
								var cls = Ext.decode(response.responseText);
								keys_ = cls;
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
						Ext.create('SMSWeb.view.login.Login',{relogin:true,loginUser:CURR_USER_LOGIN_NO,seKey:keys_}).show();
					}
				}
			}, {
				text : '设置',
				glyph : 0xf013,
				handler:function(me,epts){
					
				}
			}],
	listeners :{
		render:function(ths,eOpts){
			var websocket;var oldpushlettip=true;var message_ = Ext.create('Ext.util.MixedCollection');var message_sum_ = 0;
		    if ('WebSocket' in window) {
		        websocket = new WebSocket("wss://"+basePathWs+"webscoket/webSocketServer");
		    } else if ('MozWebSocket' in window) {
		        websocket = new MozWebSocket("wss://"+basePathWs+"webscoket/webSocketServer");
		    } else {
		        //websocket = new SockJS("http://"+basePathWs+"sockjs/webSocketServer");
		        
		        //alert("2");
		        
		    	//pushlet
		    	PL.userId = CURR_USER_LOGIN_NO;
		    	//PL._init();
		    	PL.join();
		    	PL.listen();
		    	PL.subscribe('/pms/bgService');
		    	PL.joinListen('/pms/bgService');//事件标识 在数据源中引用
		    	//setTimeout('onData()',5000);   //5秒后执行yourFunction(),只执行一次 
		    	function onData(event){
		    	    if(event.get(CURR_USER_LOGIN_NO) > 0){
		    	    	var context = eval(event.get('content'));
		    	    	//alert(context);
		    	    	if(oldpushlettip){
			    	    	var notificationConfig = {
					   			title: '推送消息',
					   			iconCls: 'ux-notification-icon-information',
					   			html: 'SPRO平台的消息推送服务已放弃对IE9及以下版本的维护更新,<br/><font color=#FF8C00>推荐使用Chrome、支持Chrome内核(搜狗等)、火狐浏览器</font>。',
					   			//autoCloseDelay: 1500,
					   			slideBackDuration: 500,
					   			slideInDuration:100,
					   			autoClose:false,
					   			slideInAnimation: 'easeIn',
					   			slideBackAnimation: 'easeIn'
					   		};
					   											
					   		Ext.create('Ext.ux.window.Notification', notificationConfig).show();
					   		oldpushlettip= false;
		    	    	}
		    	    	
		    	    	Ext.getCmp("myMsg").setText("我的消息（"+event.get(CURR_USER_LOGIN_NO)+"）");
		    	    	console.log("message:"+context.length);
		    	    	if(context.length<4){
		    	    		for(var i=0;i<4;i++){
			    	    		if(!message_.containsKey(context[i].ID)){
			    	    			message_.add(context[i].ID,context[i]);
				    	    		var notificationConfig = {
					    				title: '推送消息',
					    				iconCls: 'ux-notification-icon-information',
					    				html: context[i].MSG_BODY,
					    				slideBackDuration: 500,
					    				width:300,
					    				slideInDuration:100,
					    				slideInAnimation: 'easeIn',
					    				slideBackAnimation: 'easeIn'
					    			};
				    				Ext.create('widget.uxNotification', notificationConfig).show();
			    	    		}
			    	    	}
		    	    	}else{
		    	    		if(context.length>message_.getCount()){
		    	    			var notificationConfig = {
				    				title: '推送消息',
				    				iconCls: 'ux-notification-icon-information',
				    				html: "你有"+context.length+"条新的消息，请注意查收！",
				    				slideBackDuration: 500,
				    				slideInDuration:100,
				    				width:300,
				    				slideInAnimation: 'easeIn',
				    				slideBackAnimation: 'easeIn'
				    			};
			    				Ext.create('widget.uxNotification', notificationConfig).show();
		    	    		}
		    	    		
		    	    		for(var i=0;i<context.length;i++){
			    	    		if(!message_.containsKey(context[i].ID)){
			    	    			message_.add(context[i].ID,context[i]);
			    	    		}
			    	    	}
		    	    	}
		    	    }else{
		    	    	Ext.getCmp("myMsg").setText("我的消息");
		    	    }
		    	}
		    }
		    
		    if(websocket){
		    	websocket.onopen = function (evnt) {
		        };
		        websocket.onmessage = function (evnt) {
		        	var html_ = "";
		        	var res_ = evnt.data;
		        	if(res_.indexOf('[#]')!=-1){
		        		var sum_ = res_.substr(3)/1;
		        		if(!isNaN(sum_)){
		        			message_sum_ += sum_;
		        			html_="你有"+message_sum_+"条新的消息，请注意查收！";
		        		}
		        	}else{
		        		message_sum_ += 1;
		        		html_ = res_;
		        	}
					Ext.ComponentQuery.query('buttontransparent[id=myMsg]')[0].setText("我的消息（"+message_sum_+"）");
		        	var notificationConfig = {
		       			title: '推送消息',
		       			iconCls: 'ux-notification-icon-information',
		       			html: html_,
		       			//autoCloseDelay: 1500,
		       			slideBackDuration: 500,
		       			slideInDuration:100,
		       			autoClose:false,
		       			width:300,
		       			slideInAnimation: 'easeIn',
		       			slideBackAnimation: 'easeIn'
		       		};
		       											
		       		Ext.create('Ext.ux.window.Notification', notificationConfig).show();
		        };
		        websocket.onerror = function (evnt) {
		        };
		        websocket.onclose = function (evnt) {
		        	var notificationConfig = {
		       			title: '推送消息',
		       			iconCls: 'ux-notification-icon-information',
		       			html: "服务器已断开连接",
		       			slideBackDuration: 500,
		       			slideInDuration:100,
		       			width:300,
		       			slideInAnimation: 'easeIn',
		       			slideBackAnimation: 'easeIn'
		       		};
		       		Ext.create('Ext.ux.window.Notification', notificationConfig).show();
		        }
		    }
		}
	}
});

