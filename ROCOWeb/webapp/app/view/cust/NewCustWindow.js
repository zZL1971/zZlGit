Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.cust.NewCustWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewCustWindow',
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal: true,
			formId: null,
			editFlag: null,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
//			bodyStyle:'overflow-y:auto;overflow-x:hidden',
//			items : [{
//						xtype:'NewCustWindowInnerContent',
//						region:'center',
//						formId:null
//				}],
			initComponent : function() {
				var me = this;
				Ext.apply(this, {
					items : [{
						xtype:'NewCustWindowInnerContent',
						region:'center',
						formId:me.formId,
						editFlag:me.editFlag
					}]
				});
				this.callParent(arguments);
			}
		});
