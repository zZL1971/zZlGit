Ext.define("SMSWeb.view.cart.ShoppingCartWindow", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.ShoppingCartWindow',// 面板的别名
	layout:'border',
	autoScroll:true,
	sourceShow:null,//显示来源
	border:false,
	items:[{
	    	   tbar : [{
	    	        	   xtype : 'button',
	    	        	   text : '查询',
	    	        	   itemId : 'querySale',
	    	        	   iconCls:'table_search'
	    	           }
//	    	   			,{
//	    	        	   xtype : 'button',
//	    	        	   text : '确定',
//	    	        	   itemId : 'comfirm',
//	    	        	   icon:'resources/images/tick.png'
//					   }
	    	           ,{
	    	        	   xtype : 'button',
	    	        	   text: '新增非标产品',
	    	        	   iconCls: 'table_add',
	    	        	   itemId : 'addSale',
	    	           },{
	    	        	   xtype : 'button',
	    	        	   text: '删除',
	    	        	   iconCls: 'table_remove',
	    	        	   itemId : 'deleteSale'
	    	           }],
	    	           region:'north',
	    	           itemId:'ShoppingCart2SaleView_itemId',
	    	           xtype:'ShoppingCart2SaleView'
	       },{
	    	   region:'center',
	    	   itemId:'ShoppingCart2AddSaleView_itemId',
	    	   id:'ShoppingCart2AddSaleView_itemId',
	    	   xtype:'ShoppingCart2AddSaleView'
	       }]
			,
	       listeners:{
		    	  show:function(){
		     			var me = this;
		     			var grid = me.queryById('ShoppingCart2AddSaleView_itemId');
		     			var form = me.queryById('ShoppingCart2SaleView_itemId');
		     			var formValues={};
		     			formValues = form.getValues();
		     				form.getForm().findField("serialNumber").show();
		     				me.queryById('addMMFb').show();
		     				me.queryById('deleteMMFb').show();
		     				Ext.Array.each(grid.columns, function(column) {
								if("undefined"!=typeof(column.dataIndex) && "serialNumber"==column.dataIndex){
									column.show();
								}
							});
		     				me.type='FB';
		     				if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品 只查看可多选
//		     					var selModel =grid.getSelectionModel();
//			     				selModel.setSelectionMode("SINGLE");
			     				grid.down('actioncolumn').setText("编辑")
			     				grid.down('actioncolumn').show();
			     				form.getForm().findField("type").setValue(me.type);
			     				form.getForm().findField("saleFor").setValue(me.saleFor);
			     				form.getForm().findField("maktx").setValue(me.queryForm);
		     				} 
//		     				form.getForm().findField("mtart").hide();
		     			
		     			grid.getStore().loadPage(1);
//		     			grid.getStore().loadPage(1,{params:formValues});
								
		     			}
		     	  }
});