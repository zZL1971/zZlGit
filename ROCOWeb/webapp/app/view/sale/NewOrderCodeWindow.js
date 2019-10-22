Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewOrderCodeWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewOrderCodeWindow',
			formId:null,
			maximizable:true,
			height : 150,
			width : 400,
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
			    	   itemId:'orderCodeForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   tbar : [{
							xtype : 'button',
							text : '保存',
							itemId : 'orderCodeUpdate',
							iconCls:'table_save',
							hidden:false
					   }],
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
			                    fieldLabel: '修改后的单号',
								allowBlank: false,
			                    name: 'updateCode',
			                    width: 300,
			                    maxLength:16
			                }
						]
			    	   
				});
				
				//生成页面
				Ext.apply(this, {
					items : [form]
				});
				
				this.callParent(arguments);
			}
		});
