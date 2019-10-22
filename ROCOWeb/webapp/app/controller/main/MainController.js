Ext.define("SMSWeb.controller.main.MainController", {
	extend : 'Ext.app.Controller',
	refs:[{
    	ref : 'MainView',
		selector : 'MainView' 
    },{
    	ref : 'TreeView',
		selector : 'TreeView' 
    },{
    	ref:'WorkSpace',
    	selector:'WorkSpace'
    }],
    requires : ["Ext.ux.ButtonTransparent","Ext.ux.form.DictCombobox"],
	init : function() {
		this.control({
			'TreeView':{
				itemdblclick:function(tree,record,item,index,e,options){
					var me = this,mainView = me.getMainView();
     				var tab = Ext.getCmp('tab_' + record.get('id'));
					if(!tab){
						if(record.get('url')){
							tab = Ext.create("Ext.ux.IFrame", {
	                		  xtype: 'iframepanel',
			                  id: "tab_" + record.get('id'),
			                  title:record.get('text'),
			                  iconCls: '',
			                  closable: true,
			                  layout: 'fit',
			                  loadMask: 'Loading Business Components...',
			                  border: false//,
	         					   });
							mainView.query('tabpanel')[0].add(tab);
							mainView.query('tabpanel')[0].setActiveTab("tab_" + record.get('id'));  
							tab.load(record.get('url'));
						}
					}else{
						tab.show(); 
					}
				}
			},
			'maintop button[id=myMsg]':{
				click:function(){
					var me = this,mainView = me.getMainView();
     				var tab = Ext.getCmp('tab_message');
					if(!tab){
						 
						tab = Ext.create("Ext.ux.IFrame", {
                		  xtype: 'iframepanel',
		                  id: "tab_message",
		                  title:"我的消息",
		                  iconCls: '',
		                  closable: true,
		                  layout: 'fit',
		                  loadMask: 'Loading Business Components...',
		                  border: false//,
         					   });
						mainView.query('tabpanel')[0].add(tab);
						mainView.query('tabpanel')[0].setActiveTab("tab_message");  
						tab.load("core/sysMesSend/query");
						 
					}else{
						tab.show(); 
					}
				}
			},'maintop button[id=grumble]':{
				click:function(){
					var _win;
					var _form=Ext.create('Ext.form.Panel',{
						fieldDefaults : {
							labelAlign : 'left',
							labelWidth : 40,
							labelStyle : 'font-weight:bold',
							msgTarget :'side'
						},
						bodyPadding : 10,
						defaults : {
							margins : '0 0 10 0'
						},
						items:[{
							xtype:'dictcombobox',
							fieldLabel:'类型',
							dict:'GRUMBLE_TYPE',
							name:'grumbleType',
							hidden:true
						},{
							xtype : 'textareafield',
							fieldLabel : '内容',
							name:'grumbleContent',
							allowBlank : false,
							width : 370
						}],
						buttons:[{
							xtype:'button',
						 	text : '提交',
						 	formBind: true,
						 	listeners:{
	            				click:function(){
	            					var form = this.up('form').getForm();
	            					form.submit({
	            						url:'/main/user/saveGrumble',
	            						waitMsg : 'Please wait',
										waitTitle : '处理中...',
										success : function(form, action) {
											Ext.Msg.show({
												title:'提示',
												msg:action.result.msg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.INFO
											});
											_win.close();
										},
										failure : function(formx, action) {
											Ext.Msg.show({
												title:'提示'+action.result.errorCode,
												msg:action.result.errorMsg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.ERROR
											});
										}
	            					});
	            				}
            				}
						}]
					});
					_win = Ext.create('Ext.window.Window',{
						title : '我要吐槽',
			            modal : true,
			            plain : true,
			            border:false,
						items:[_form]
					});
					
					_win.show();
				}
			},
			'maintop menuitem[itemId=sendMessage]':{
				click:function(){
					var win_;
					var form_ = Ext.create('Ext.form.Panel',{
						fieldDefaults : {
							labelAlign : 'left',
							labelWidth : 40,
							labelStyle : 'font-weight:bold',
							msgTarget :'side'
						},
						bodyPadding : 10,
						defaults : {
							margins : '0 0 10 0'
						},
						items : [{
							xtype : 'textfield',
							name:'type',
							fieldLabel : '标题',
							allowBlank : false,
							value:'系统消息',
							readOnly:true,
							fieldCls:'ux-form-readonly',
							width : 370
						}, {
							xtype : 'textareafield',
							fieldLabel : '内容',
							name:'content',
							allowBlank : false,
							width : 370
						}],
						buttons:[{
							xtype:'combobox',
							fieldLabel : '快速选择',
							labelWidth : 60,
							queryMode: 'local',
							forceSelection:true,
							typeAhead: true,
							store:Ext.create('Ext.data.Store', {  
						        fields:["id","text"],  
						        data: [{id:'',text:''},{id:"服务器5分钟后关闭进行更新，请大家及时保存现在操作的单据，谢谢大家的配合。预计耗时10分钟。",text:'服务器更新提示'},{id:"服务器5分钟后关闭重启，请大家及时保存现在操作的单据，谢谢大家的配合。预计耗时10分钟。",text:'服务器重启提示'}]  
						    }),
						    listeners:{
						    	select:function( combo, records, eOpts){
						    		this.up('form').getForm().findField('content').setValue(records[0].get("id"));
						    	}
						    }
						},'->',{
						 	xtype:'button',
						 	text : '确认发送',
						 	formBind: true,
						 	listeners:{
	            				click:function(){
	            					var form = this.up('form').getForm();
	            					form.submit({
	            						url:'/core/msg/send',
	            						waitMsg : 'Please wait',
										waitTitle : '处理中...',
										success : function(form, action) {
											Ext.Msg.show({
												title:'提示',
												msg:action.result.msg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.INFO
											});
											win_.close();
										},
										failure : function(formx, action) {
											Ext.Msg.show({
												title:'提示'+action.result.errorCode,
												msg:action.result.errorMsg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.ERROR
											});
										}
	            					});
	            				}
            				}
						}]
					});
					
					win_ = Ext.create('Ext.window.Window',{
						title : '系统消息',
			            modal : true,
			            plain : true,
			            border:false,
						items:[form_]
					});
					
					win_.show();
				}
			},
			'maintop menuitem[itemId=setpwd]':{
				click:function(){
					var win_;
					var form_ = Ext.create('Ext.form.Panel',{
						fieldDefaults : {
							labelAlign : 'left',
							labelWidth : 85,
							labelStyle : 'font-weight:bold',
							msgTarget :'side'
						},
						bodyPadding : 10,
						defaults : {
							margins : '0 0 10 0'
						},
						items : [{
							xtype : 'textfield',
							name:'oldpwd',
							fieldLabel : '原密码',
							blankText : '原密码不能为空',
							allowBlank : false,
							width : 270,
							inputType : 'password'
						}, {
							xtype : 'textfield',
							itemId:'passoword',
							name:'pwd',
							fieldLabel : '新密码',
							allowBlank : false,
							blankText : '新密码不能为空',
							width : 270,
							//regex: /^([a-zA-Z0-9]{6,})$/i,
							//regexText: '密码必须同时包含字母和数字,且最少有6位',
							inputType : 'password'
						}, {
							xtype : 'textfield',
							id:'pwd2',
							fieldLabel : '重复新密码',
							allowBlank : false,
							blankText : '再次输入新密码不能为空',
							width : 270,
							vtype:'password',
							initialPassField:'passoword',
							inputType : 'password'
						}],
						buttons:[{
						 	xtype:'button',
						 	text : '确认修改',
						 	listeners:{
	            				click:function(){
	            					
	            					var publicKeyExponent,publicKeyModulus;
	            					
	            					Ext.Ajax.request({
										url:'/relogin',
										method:'POST',
										async:false,
										dataType: "json",
										success:function(response,opts){
											var jsonResult = Ext.decode(response.responseText);
											if(jsonResult.success){
												publicKeyExponent = jsonResult.publicKeyExponent;
												publicKeyModulus = jsonResult.publicKeyModulus;
											}else{
												Ext.Msg.show({
													title:"错误提示["+jsonResult.errorCode+"]:",
													icon:Ext.Msg.ERROR,
													msg:jsonResult.errorMsg,
													buttons:Ext.Msg.OK
												});
											}
										},
										failure:function(response,opts){
											Ext.Msg.alert("can't",'error');
										}
									});
	            					
	            					var form = this.up('form').getForm();
	            					var oldpwd = form.findField('oldpwd');
	            					oldpwd.setValue(k(oldpwd.getValue(),publicKeyExponent,publicKeyModulus));
	            					var pwd = form.findField('pwd');
	            					pwd.setValue(k(pwd.getValue(),publicKeyExponent,publicKeyModulus));
	            					var pwd2 = form.findField('pwd2');
	            					pwd2.setValue(k(pwd2.getValue(),publicKeyExponent,publicKeyModulus));
	            					form.submit({
	            						url:'/mpwd',
	            						waitMsg : 'Please wait',
										waitTitle : '处理中...',
										success : function(form, action) {
											Ext.Msg.show({
												title:'提示',
												msg:action.result.msg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.INFO
											});
											win_.close();
										},
										failure : function(formx, action) {
											Ext.Msg.show({
												title:'提示'+action.result.errorCode,
												msg:action.result.errorMsg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.ERROR
											});
										}
	            					});
	            				}
            				}
						}]
					});
					
					win_ = Ext.create('Ext.window.Window',{
						title : '密码设置',
			            modal : true,
			            plain : true,
			            border:false,
						items:[form_]
					});
					
					win_.show();
				}
			},
			'maintop menuitem[itemId=setTelephoneNum]':{
				click:function(){
					var win_;
					
					var Telform_ = Ext.create('Ext.form.Panel',{
						fieldDefaults : {
							labelAlign : 'left',
							labelWidth : 85,
							labelStyle : 'font-weight:bold',
							msgTarget :'side'
						},
						bodyPadding : 10,
						defaults : {
							margins : '0 0 10 0'
						},
						items : [{
							xtype : 'textfield',
							name:'oldtelnum',
							fieldLabel : '原手机号码',
							allowBlank : true,
							width : 270,
							//disabled:true,
							readOnly:true,
							emptyText: TEL_NUM,
							style:'color:red;'
						}, {
							xtype : 'textfield',
							itemId:'telnum',
							name:'telnum',
							fieldLabel : '新手机号码',
							allowBlank : false,
							blankText : '新手机号码不能为空',
							width : 270,
							regex: /^1[3,4,5,7,8]\d{9}$/,
							regexText: '号码不匹配'
							
						}, {
							xtype : 'textfield',
							itemId:'surenum',
							name:'surenum',
							fieldLabel : '重复新手机号码',
							allowBlank : false,
							blankText : '再次输入新号码不能为空',
							width : 270,
							regex: /^1[3,4,5,7,8]\d{9}$/,
							regexText: '号码不匹配'
						}],
						buttons:[{
						 	xtype:'button',
						 	text : '确认修改',
						 	listeners:{
	            				click:function(){
	            				
	            					var form = this.up('form').getForm();
	            					var oldtelnum = form.findField('oldtelnum').getValue();
	            					var telnum = form.findField('telnum').getValue();
	            					TEL_NUM = telnum;//在前台回写手机号
	            					var surenum = form.findField('surenum').getValue();
	            					var kunnr =  CURR_USER_KUNNR;
									if(!telnum){
										Ext.MessageBox.alert("提示","请先填写变更手机号码");
										return false;
									}
	            					if(telnum!=surenum){
	            						Ext.MessageBox.alert("提示","手机号码与确认手机号码不同,请核对!");
	            						return false;
	            					}
	            					if(oldtelnum==telnum.trim()){
	            						Ext.MessageBox.alert("提示","新号码与原号码相同不需要修改!");
	            						return false;
	            					}
	            					form.submit({
	            						url:'/mtel',
	            						params : {
											'telnum' : telnum,
											'surenum' : surenum,
											'kunnr' : kunnr
										},
	            						waitMsg : 'Please wait',
										waitTitle : '处理中...',
										success : function(form, action) {
											Ext.Msg.show({
												title:'提示',
												msg:action.result.msg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.INFO
											});
											win_.close();
										},
										failure : function(formx, action) {
											Ext.Msg.show({
												title:'提示'+action.result.errorCode,
												msg:action.result.errorMsg,
												buttons:Ext.Msg.OK,
												icon:Ext.Msg.ERROR
											});
										}
	            					});
	            				}
            				}
						}]
					});
					
					win_ = Ext.create('Ext.window.Window',{
						title : '手机号设置',
			            modal : true,
			            plain : true,
			            border:false,
						items:[Telform_]
					});
					
					win_.show();
					//headForm.getForm().findField("oldtelnum").setValue('11122222222233333');
					//headForm.getForm().findField("oldtelnum").setValue('11122222222233333');
				}
			},
			
			'maintop':{
				loginout:function(){
					Ext.MessageBox.confirm('提示','确定退出系统吗？',function(id){
						if(id=="yes"){
							Ext.Ajax.request({
								url:'/loginout',
								async:false,
								dataType: "json",
								success:function(response,opts){
									var message = Ext.decode(response.responseText);
									if(message.success){
										window.location='/';
									}else{
										Ext.Msg.show({
											title:"错误代码:"+message.errorCode,
											icon:Ext.Msg.ERROR,
											msg:message.errorMsg,
											buttons:Ext.Msg.OK
										});
									}
								},
								failure:function(response,opts){
									Ext.Msg.show({
										title:"系统错误",
										icon:Ext.Msg.ERROR,
										msg:response.responseText,
										buttons:Ext.Msg.OK
									});
								}
							});
						}
					});
				},
				// 隐藏顶部和底部的按钮事件
				hiddenTopBottom : function() {
					// 如果要操纵控件，最好的办法是根据相对路径来找到该控件，用down或up最好，尽量少用getCmp()函数。
					this.getView().down('maintop').hide();
					this.getView().down('mainbottom').hide();
					if (!this.showButton) { // 显示顶部和底部的一个控件，在顶部和底部隐藏了以后，显示在页面的最右上角
						this.showButton = Ext.widget('component', {
									glyph : 0xf013,
									view : this.getView(),
									floating : true,
									x : document.body.clientWidth - 32,
									y : 0,
									height : 4,
									width : 26,
									style : 'background-color:#cde6c7',
									listeners : {
										el : {
											click : function(el) {
												var c = Ext.getCmp(el.target.id); // 取得component的id值
												c.view.down('maintop').show();
												c.view.down('mainbottom').show();
												c.hide();
											}
										}
									}
								})
					};
					this.showButton.show();
				},
				// 如果窗口的大小改变了，并且顶部和底部都隐藏了，就要调整显示顶和底的那个控件的位置
				onMainResize : function() {
					if (this.showButton && !this.showButton.hidden) {
						this.showButton.setX(document.body.clientWidth - 32);
					}
				}
			}
		})
	},
	
	views : [
	'main.MainView','main.TreeView','main.TopView','main.WorkSpace'],
	stores : [
	'main.Store4Menu'
	],
	models : [
	'main.MenuModel'
	]
});