//补件查询
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.myGoods.MyGoodsBujianWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MyGoodsBujianWindow',
			formId:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			ortype:null,
			items:[
					{
						tbar : [
						     {
								xtype : 'button',
								text : '查询',
								itemId : 'query',
								iconCls:'table_search'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '确定',
						    	 itemId : 'comfirm',
						    	 icon:'resources/images/tick.png'
						     }
						    ]
						,
					    region:'north',
					    itemId:'MyGoodsBujianMainFormView_itemId',
						xtype:'MyGoodsBujianMainFormView'
					 },
					{
						xtype:'MyGoodsBujianMainGridView',
						itemId:'MyGoodsBujianMainGridView_itemId',
						region:'center'
					}
			      ]
			,
			listeners: {
				show : function(){
					var me = this;
					var form = me.queryById('MyGoodsBujianMainFormView_itemId');
	     			var formValues={};
	     			var formId = me.formId;
					var ortype = form.getForm().findField("ortype");
					ortype.setValue(me.ortype);
				    ortype.hide();
				    
					formValues = form.getValues();
					me.queryById('MyGoodsBujianMainGridView_itemId').down('actioncolumn').hide();;
					me.down('grid').getStore().loadPage(1);
				}
			}
		}
		
	);
