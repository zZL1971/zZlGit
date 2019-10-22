Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.OrderReportView', {
			extend : 'Ext.window.Window',
			alias : 'widget.OrderReportView',
			id:"orderReportView_id",
			maximizable:true,
		    minWidth:850,
		    minHeight:500,
			autoScroll:true,
			modal: true,
			formId: null,
			editFlag: true,
			constrainHeader: true,
			closable : true,
			startTime:null,
			endTime:null,
			kunnr:null,
			orderTotal:null,
			bzirk:null,
			layout:'fit',
			items : [{
				xtype:'NewOrderDetail'
			}],
			initComponent : function() {
				var me = this;
				me.height = document.body.clientHeight  * 0.68;
				me.width = document.body.clientWidth * 0.79;
				Ext.apply(this);
				this.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me=this;
					console.log("aaaa");
					var grid=Ext.getCmp("orderGridDetail_id");
					grid.getStore().load({params:{'kunnr':me.kunnr,'startTime':me.startTime,'startTime':me.startTime
						,'endTime':me.endTime,"taskName":"客户确认","orderTotal":me.orderTotal,"bzirk":me.bzirk}});
				}
			}
		});
