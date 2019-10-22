Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSaleWindow',
			itemId:'newSaleWindow',
			maximizable:true,
		    minWidth:850,
		    minHeight:500,
			autoScroll:true,
//		    overflowY:'auto',
//			overflowX:'hidden',
			modal: true,
			formId: null,
			orderCode:null,
			editFlag: true,
			orderType:null,
			jdName:null,
			constrainHeader: true,
			closable : true,
			layout:'fit',
			autoScroll:true,
			/*items : [{
						xtype:'NewCustWindowInnerContent',
						region:'center',
						formId:formId
				}],*/
			initComponent : function() {
				var me = this;
				me.height = document.body.clientHeight  * 0.98;
				me.width = document.body.clientWidth * 0.99;
				var myMask = new Ext.LoadMask(Ext.getBody(),{msg:"请稍等...",shadow:true,modal: true}); 
				myMask.show();
				Ext.apply(this, {
					items : [{
						xtype:'NewSaleWindowInnerContent',
						formId:me.formId,
						editFlag:me.editFlag,
						orderType:me.orderType,
						orderCode:me.orderCode,
						jdName:me.jdName
					}]
				});
				this.callParent(arguments);
				if (myMask != undefined){ myMask.hide();}
			}
		});
