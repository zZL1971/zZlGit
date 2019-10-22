Ext.define("SMSWeb.view.mm.myGoods.MyGoodsBujianMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.MyGoodsBujianMainView',// 面板的别名
			layout:'border',
			border:false,
			items:[
					{
						tbar : [
							{
								xtype : 'button',
								text : '客服补购',
								id : 'newKFBG',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '免费订单',
								id : 'newMFDD',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '删除',
								id : 'deleteMyGoods',
								iconCls:'table_remove'
							},
							{
								xtype : 'button',
								text : '查询',
								id : 'queryMyGoods',
								iconCls:'table_search'
							}]
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
			       ],
		    listeners: {
				afterrender:function(){
					var me = this;
					me.down('grid').getStore().load();
				}
			}
		});