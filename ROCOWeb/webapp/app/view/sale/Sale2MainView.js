Ext.define("SMSWeb.view.sale.Sale2MainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.Sale2MainView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			items:[
			   {
				   tbar : [{
							xtype : 'button',
							text : '查询',
							id : 'querySale',
							iconCls:'table_search'
						},
						{
							xtype : 'button',
				            text: '增加',
				            iconCls: 'table_add',
				            id : 'newSale',
				            hidden: true
//				            hidden: "4"==queryType?true:false
				        },
				        {
				        	xtype : 'button',
				        	text: '切换数据',
				        	iconCls: 'table_add',
				        	id : 'changeData',
				        	hidden: true
				        },
						{
							xtype : 'button',
				            text: '增加(新)',
				            iconCls: 'table_add',
				            id : 'addSale',
				            hidden: ("4"==queryType||"2"==queryType)?true:false
				        },
				        {
							xtype : 'button',
				            text: '补单',
				            iconCls: 'table_add',
				            id : 'newSale2',
//				            hidden: ("4"==queryType||"2"==queryType)?true:false
//				            hidden:true
				            hidden: "1"==queryType || "4"==queryType?false:true
				        },
				        {
							xtype : 'button',
				            text: '删除',
				            iconCls: 'table_remove',
				            id : 'deleteSale'
				        },{
				        	xtype : 'buttontransparent',
				        	text : '导出',
				        	id:'export',
				        	icon:'/resources/images/down.png',
				        	//hidden: "1"==queryType ?false:true
				        	hidden:false
				        }
					],
				    region:'north',
					xtype:'Sale2FormView'
			    }
			   ,
			    {
				    region:'center',
					xtype:'Sale2GridView'
				}
			 ]
		});