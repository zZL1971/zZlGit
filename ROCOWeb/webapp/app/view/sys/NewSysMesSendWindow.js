Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sys.NewSysMesSendWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSysMesSendWindow',
			formId:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.7,
			modal:true,
			autoScroll:true,
			constrainHeader: true,
			closable : true,
			border : false,
//			layout:'border',
			initComponent : function() {
				 var me = this;
				 var form = Ext.widget('form',{
//		    		   region:'north',
			    	   itemId:'sysMesSendForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   fieldDefaults : {
							labelWidth : 100,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 2
					   },
			    	   items : [
			    		    {
			                    xtype:'textareafield',
			                    fieldLabel: '标题',
			                    name: 'msgTitle',
			                    colspan: 2,
			                    cols:100,
					            rows:1
			                },{
			                    xtype:'textareafield',
			                    fieldLabel: '内容',
			                    name: 'msgBody',
			                    colspan: 2,
			                    cols:100,
					            rows:3
							},
			                {
					        	xtype:'textfield',
								fieldLabel : '发送人',
								width: 300,
								name:'sendUser'
						    },{
			                    xtype:'datefield',
			                    fieldLabel: '发送时间',
			                    name: 'sendTime',
			                    format :'Y-m-d H:i:s',
			                    width: 300
							}
						]
			    	   
				 });
				
				//生成页面
				Ext.apply(this, {
					items : [form]
				});
				
				//加载数据
				if(me.formId!=null){
					form.load({
						url:'core/sysMesSend/findById',
						params : {
							id : me.formId
						},
						method : 'GET',
						success:function(f,action){
							var result = Ext.decode(action.response.responseText);
							//alert(result.success);alert(result.data.orderCode);
				        },
				        failure:function(form,action){
				        	var result = Ext.decode(action.response.responseText);
				            Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
				        }
					});
				}
				var formValues = form.getValues();
				Ext.Object.each(formValues, function(key, value, myself) {
					//console.log(key + ":" + value);
					form.getForm().findField(key).setReadOnly(true);
				});
				this.callParent(arguments);
			}
		});
