Ext.define("SMSWeb.view.delivery.DeliveryMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.DeliveryMainView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			items:[
			   {
				   tbar : [{
							xtype : 'button',
							text : '查询',
							id : 'query',
							iconCls:'table_search'
						},
						{
							xtype : 'button',
				            text: '保存',
				            iconCls: 'table_save',
				            id : 'save'
				        },
						{
							xtype : 'button',
				            text: '增加',
				            iconCls: 'table_add',
				            id : 'add',
				        },
				        {
							xtype : 'button',
				            text: '删除',
				            iconCls: 'table_remove',
				            id : 'deleteColumn'
				        }
					],
				    region:'north',
					xtype:'DeliveryFormView'
			    }
			   ,
			    {
				    region:'center',
					xtype:'DeliveryGridView'
				}
			 ]
		});