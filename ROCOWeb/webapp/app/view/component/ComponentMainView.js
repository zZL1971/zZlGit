Ext.define("SMSWeb.view.component.ComponentMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.ComponentMainView',// 面板的别名
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
				        },
				        {
				        	xtype : 'button',
				        	text: '导入',
				        	iconCls: 'table_remove',
				        	id : 'excellupload'
				        }
					],
				    region:'north',
					xtype:'ComponentFormView'
			    }
			   ,
			    {
				    region:'center',
					xtype:'ComponentGridView'
				}
			 ]
		});