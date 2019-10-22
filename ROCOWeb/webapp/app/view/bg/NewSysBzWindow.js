Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.bg.NewSysBzWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSysBzWindow',
			maximizable:true,
			height : 300,
			width : 500,
			formId:null,
			formStatus:null,
			formMsg:null,
			formStep:null,
			modal:true,
			autoScroll:true,
			constrainHeader: true,
			closable : true,
			border : false,
			maximizable : false,
//			layout:'border',
			initComponent : function() {
				 var me = this;
				 var form = Ext.widget('form',{
					   tbar : [{
							xtype : 'button',
							text : '确定',
							itemId : 'confirm',
							iconCls:'flow_ok'
						}],
//		    		   region:'north',
			    	   itemId:'sysBzForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 2
					   },
			    	   items : [
							{
					            xtype: 'textareafield',
					            name: 'sysBzForm_remarks',
					            fieldLabel: '备注',
					            colspan: 2,
					            //value: 'Textarea value',
					            //fieldStyle : 'background-color: #D0DEF0;',
					            cols:50,
					            rows:10
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
