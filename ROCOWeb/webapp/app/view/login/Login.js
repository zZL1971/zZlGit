Ext.define('SMSWeb.view.login.Login', {
	extend : 'Ext.window.Window',
	alias : 'widget.loginForm',
	title : '用户登录',
	relogin:relogin?relogin:false,
	loginUser:'',
	requires : ['Ext.form.*', 'SMSWeb.view.login.CheckCodeText'],
	seKey:null,
	initComponent : function() {
		var me = this;
		var publicKeyExponent,publicKeyModulus,reloginText;
		if(me.relogin){
			reloginText="<font color=#EE4000>登录超时，请重新输入密码</font>";
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
		}
		
		var checkcode = Ext.create('SMSWeb.view.login.CheckCodeText', {
				fieldLabel : '验证码:',
				name : 'checkcode',
				id : 'checkcode',
				allowBlank : true,
				isLoader : true,
				blankText : '请输入验证码',
				codeUrl : '/ImageServlet',
				maxLength : 4,
				minLength : 4,
				width : 270
			});
	
		var form = Ext.widget('form', {
			border : false,
			frame : false,
			itemId:'login_form',
			bodyPadding : 10,
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 85,
				labelStyle : 'font-weight:bold'
			},
			defaults : {
				margins : '0 0 10 0'
			},
			items : [{
						name:'debug',
						xtype:'hidden',
						value:'true'
					},{
						xtype : 'textfield',
						name:'username',
						fieldLabel : '登录账号',
						blankText : '账号不能为空',
						readOnly : me.relogin,
						fieldStyle:me.relogin?'background:#E6E6E6':'',
						value:me.loginUser,
						allowBlank : false,
						width : 270
					}, {
						xtype : 'textfield',
						name:'password',
						fieldLabel : '登录密码',
						//value:'123456',
						allowBlank : false,
						blankText : '密码不能为空',
						width : 270,
						inputType : 'password'
					}, checkcode,{
						title : "IP信息",
						xtype : "fieldset",
						bodyPadding : 5,
						hidden:true,
						collapsible : false,
						collapsed : false, // 默认收缩
						defaults : {
							labelSeparator : "：",
							labelWidth : 65,
							anchor : '100%',
							//width : 300,
							fieldStyle:'background:#E6E6E6'
						},
						defaultType : "textfield",
						items : [{
									fieldLabel : 'IP地址',
									name : 'ipaddr',
									readOnly : true
								}, {
									fieldLabel : '归属地',
									name : 'attribution',
									readOnly : true
								}, {
									fieldLabel : '运营商',
									name : 'operator',
									readOnly : true
								}]
					}],
			buttons : [{
				xtype:'displayfield',
				itemId:'displayError',
				value:reloginText
			}, '->',{
			 	xtype:'button',
			 	text : '登录',
				itemId : 'loginSubmit',
				disabled: true,
            	formBind: true,
            	listeners:{
            		click:function(){
            			var form = this.up('form').getForm();
            			var pwd = form.findField('password');
            			var e_,m_ ;
            			
            			if(me.seKey!=null){
            				e_ = me.seKey.exponent;
            				m_ = me.seKey.modulus;
            			}else{
            				e_ = publicKeyExponent;
            				m_ = publicKeyModulus;
            			}
            			
            			pwd.setValue(k(pwd.getValue(),e_,m_));
						form.submit({
							waitMsg : 'Please wait',
							waitTitle : 'Logging...',
							url : 'login',
							success : function(form, action) {
								
								if(me.relogin){
									if(action.result.lasturl){
										location.href =action.result.lasturl;
									}
									me.close();
									return;
								}
								
								// 登录成功后的操作，这里只是提示一下
								if(action.result.lasturl){
									location.href =action.result.lasturl;
								}else{
									location.href="/core/main/";
								}
								
								Ext.Msg.show({
									title:'tip',
									msg:"业务组件加载中...",
									icon:Ext.Msg.INFO
								});
							},
							failure : function(formx, action) {
								
								if(action.result.errorCode=="LO-ER-105"){
									var a = Ext.DomQuery.selectNode("img[@class=x-form-code]");
									a.click();
								}
								
								if(action.result.errorCode=="LO_VD_500"){
									var datas = action.result.data;
							        //创建Grid表格组件
							        /*var grid_ = Ext.create('Ext.grid.Panel',{
							            border:false,
							            region:true,
							            region:'center',
							            viewConfig: {
							                forceFit : true,
							                enableTextSelection:true,
							                stripeRows: true//在表格中显示斑马线
							            },
							            store: {//配置数据源
							                fields: ['userId','userName','createTime',"na","ia","session"],//定义字段
							                data:datas
							            },
							            columns: [//配置表格列
							           		{xtype:'rownumberer',width:30,align:'right'},
							                {header: "会话", width:300, dataIndex:'session', sortable: true},
							                {header: "登录账号", width: 80, dataIndex:'userId', sortable: true},
							                {header: "名称", width: 120, dataIndex:'userName', sortable: true},
							                {header: "外网IP地址", width: 120, dataIndex:'ia', sortable: true},
							                {header: "本地IP地址", width: 120, dataIndex:'na', sortable: true},
							                {header: "登录时间", width: 140, dataIndex:'createTime', sortable: true}
							            ]
							        });
									Ext.create('Ext.window.Window',{
										title : '该账号已被其他客户端登录',
							            width:700,
							            height:500,
							            layout:'border',
							            modal : true,
							            plain : true,
							            border:false,
										items:[grid_],
										listeners:{
											close:function(){
												if(me.relogin){
													if(action.result.lasturl){
														location.href =action.result.lasturl;
													}else{
														location.href="/core/main/";
													}
													me.close();
													return;
												}else{
													if(action.result.lasturl){
														location.href =action.result.lasturl;
													}else{
														location.href="/core/main/";
													}
												}
											}
										}
									}).show();*/
									
									/*Ext.Msg.show({
										title:'提示'+action.result.errorCode,
										msg:action.result.errorMsg,
										buttons:Ext.Msg.OK,
										icon:Ext.Msg.ERROR,
										fn:function(){
											
										}
									});*/
									
									if(me.relogin){
										if(action.result.lasturl){
											location.href =action.result.lasturl;
										}else{
											location.href="/core/main/";
										}
										me.close();
										return;
									}else{
										if(action.result.lasturl){
											location.href =action.result.lasturl;
										}else{
											location.href="/core/main/";
										}
									}
												
									return;
								}
								Ext.Msg.show({
									title:'提示'+action.result.errorCode,
									msg:action.result.errorMsg,
									buttons:Ext.Msg.OK,
									icon:Ext.Msg.ERROR,
									fn:function(){
										Ext.ComponentQuery.query('form[itemId=login_form] textfield[inputType=password]')[0].focus(true, true);
									}
								});
							}
						});
            		}
            	}
			}]
		});
		
/*		$.getScript("http://pv.sohu.com/cityjson",function(){
//				$.ajax({
//					url: "/ipInfo?ip="+returnCitySN.cip,
//					dataType:'json',
//					success: function(data) {
//						var ipconfig = data.region+"|"+data.operator+"|"+data.ip;
//						document.getElementById("clientIP").innerHTML = ipconfig;
//					},error: function(res){  
//		                alert('Error loading ip config '+res);  
//		            }
//				});
			
			form.load({
				url:"/ipInfo?ip="+returnCitySN.cip,
				success:function(f,action){
                },
                failure:function(form,action){
                	var result = Ext.decode(action.response.responseText);
                    Ext.Msg.alert('tip'+result.errorCode,"error info: "+result.errorMsg);  
                }
			});
			});*/
		
		 
		Ext.apply(this, {
					//height : 180,
					width : 310,
					//closeAction : 'hide',
					//closable : false,
					// iconCls: 'login',
					layout : 'fit',
					modal : true,
					autoScroll : false,
					plain : true,
					resizable : true,
					border : false,
					icon : '/resources/images/information.png',
					items : [form]
				});
		this.callParent(arguments);
	}
});