Ext.define("SMSWeb.view.mm.myGoods.MyGoodsMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.MyGoodsMainView',// 面板的别名
			layout:'border',
			border:false,
			items:[
					{
						tbar : [{
								xtype : 'button',
								text : '标准产品',
								id : 'queryMyGoodsBZ',
								iconCls:'table_search'
								
							},
							{
								xtype : 'button',
								text : '非标产品',
								id : 'newMyGoodsFB',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '销售道具',
								id : 'newXS',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '五金散件',
								id : 'newWJ',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '移门散件',
								id : 'newYMSJ',
								iconCls:'table_add'
							},
							{
								xtype : 'button',
								text : '柜身散件',
								id : 'newGSSJ',
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
							    itemId:'MyGoodsMainFormView_itemId',
								xtype:'MyGoodsMainFormView'
							},
							{
								xtype:'MyGoodsMainGridView',
								itemId:'MyGoodsMainGridView_itemId',
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