Ext.define("SMSWeb.view.mm.base.MaterialMainBaseView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.MaterialMainBaseView',// 面板的别名
			layout:'border',
			border:false,
			items:[
					{
						tbar : [
						    /*{
								xtype : 'button',
								text : '新增',
								id : 'newMaterial',
								iconCls:'table_add'
								
							},{
								xtype : 'button',
								text : '删除',
								id : 'deleteMaterial',
								iconCls:'table_remove'
								},*/
							{
								xtype : 'button',
								text : '查询',
								id : 'queryMaterial',
								iconCls:'table_search'
							},
							{
								xtype : 'button',
								text : '同步SAP信息',
								id : 'syncMatnr',
								iconCls:'table_add'
							}
							]
							,
							    region:'north',
							    itemId:'MaterialMainFormBaseView_itemId',
								xtype:'MaterialMainFormBaseView'
							},
							{
								xtype:'MaterialMainGridBaseView',
								itemId:'MaterialMainGridBaseView_itemId',
								region:'center'
					}
			       ],
			       listeners: {
			    	   afterrender:function(){
				     			var me = this;
				     			var grid = me.queryById('MaterialMainGridBaseView_itemId');
				     		
				     			grid.getStore().load();
	     								
				     			}
				     	  }
		});