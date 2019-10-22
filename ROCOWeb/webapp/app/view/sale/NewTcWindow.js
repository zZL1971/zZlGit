Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewTcWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewTcWindow',
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
			    	   itemId:'bgForm',
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
			                    xtype:'textfield',
			                    fieldLabel: '姓名',
								allowBlank: false,
			                    name: 'name1',
			                    width: 300
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '联系电话',
			                    allowBlank: false,
			                    name: 'tel',
			                    width: 300
							},
			                {
					        	xtype:'dictcombobox',
								fieldLabel : '性别',
								emptyText: '请选择...',
								width: 300,
								name:'sex',
								dict:'SEX'
					        },
							{
						        xtype: 'numberfield',
						        anchor: '100%',
						        name: 'age',
						        fieldLabel: '年龄',
						        width: 300,
						        minValue: 0, //prevents negative numbers
						        maxValue: 200,
						
						        // Remove spinner buttons, and arrow key and mouse wheel listeners
						        hideTrigger: true,
						        keyNavEnabled: false,
						        mouseWheelEnabled: false
						    },{
			                    xtype:'datefield',
			                    fieldLabel: '生日',
			                    name: 'birthday',
			                    format :'Y-m-d',
			                    width: 300
							},{
			                    xtype:'textfield',
			                    fieldLabel: '身份证号码',
			                    name: 'shenFenHao',
			                    width: 300
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '专卖店编码',
			                    name: 'code',
			                    width: 300
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '专卖店名称',
			                    name: 'name',
			                    width: 300
							},{
			                    xtype:'textfield',
			                    fieldLabel: '专卖店经手人',
			                    name: 'jingShouRen',
			                    width: 300
			                },{
					        	xtype:'dictcombobox',
								fieldLabel : '安装户型',
								emptyText: '请选择...',
								width: 300,
								name:'huXing',
								dict:'HU_XING'
					        }/*,{
					        	xtype:'dictcombobox',
								fieldLabel : '是否为样板',
								emptyText: '请选择...',
								width: 300,
								name:'isYangBan',
								dict:'YES_NO'
					        }*/
			                ,{
					        	xtype:'dictcombobox',
								fieldLabel : '是否有电梯',
								emptyText: '请选择...',
								width: 300,
								name:'isDianTi',
								dict:'YES_NO'
					        },{
					        	xtype:'dictcombobox',
								fieldLabel : '是否劳卡安装',
								emptyText: '请选择...',
								width: 300,
								name:'isAnZhuang',
								dict:'YES_NO'
					        },
				            {
			                    xtype:'textfield',
			                    fieldLabel: '楼层',
			                    name: 'floor',
			                    width: 300
							},
					        {
					        	xtype:'dictcombobox',
								fieldLabel : '订单金额范围',
								emptyText: '请选择...',
								width: 300,
								name:'orderPayFw',
								dict:'ORDER_PAY_FW',
								colspan: 2
					        },{
					            xtype: 'textareafield',
					            name: 'address',
					            fieldLabel: '安装地址',
					            colspan: 2,
					            //value: 'Textarea value',
					            //fieldStyle : 'background-color: #D0DEF0;',
					            cols:100,
					            rows:1
							},{
					            xtype: 'textareafield',
					            name: 'custRemarks',
					            fieldLabel: '备注',
					            colspan: 2,
					            //value: 'Textarea value',
					            //fieldStyle : 'background-color: #D0DEF0;',
					            cols:100,
					            rows:3
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
						url:'main/sale/findTcById',
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
