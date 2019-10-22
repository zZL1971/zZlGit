Ext.define("SMSWeb.view.bg.BgMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.BgMainView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			items:[
			   {
				   tbar : [{
							xtype : 'button',
							text : '查询',
							id : 'queryBg',
							iconCls:'table_search'
						}
				   		,
						{
							xtype : 'button',
				            text: '增加',
				            iconCls: 'table_add',
				            id : 'newBg',
				            hidden: 1==queryBgType?true:false
				        }
					],
				    region:'north',
					xtype:'BgFormView'
			    }
			   ,
			    {
				    region:'center',
					xtype:'BgGridView'
				}
			 ]
		});