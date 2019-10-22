//标准产品查询
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.sale.MaterialBase2SaleWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MaterialBase2SaleWindow',
			sourceShow:null,//显示来源
			formId:null,
			maximizable:true,
			height : 500,
			saleFor:null,
			width : document.body.clientWidth * 0.85,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			kunnr:null,
			orderType:null,
			layout:'border',
			type:null,//类型 BZ 标准,SJ 散件
			matnr:null,//物料编码
			queryForm:null,
			items:[
					{
						tbar : [
						     {
								xtype : 'button',
								text : '查询',
								itemId : 'queryMaterialBase2Sale',
								iconCls:'table_search'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '确定',
						    	 itemId : 'comfirm',
						    	 icon:'resources/images/tick.png'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '新增非标产品',
						    	 itemId : 'addMMFb',
						    	 hidden:true,
						    	 icon:'resources/images/add.png'
						     },
						     {
						    	 xtype : 'button',
						    	 text : '删除',
						    	 itemId : 'deleteMMFb',
						    	 hidden:true,
						    	 icon:'resources/images/delete.png'
						     }
						    ]
						,
					    region:'north',
					    itemId:'MaterialMainFormBase2SaleView_itemId',
						xtype:'MaterialMainFormBase2SaleView'
					},
					{
						xtype:'MaterialMainGridBase2SaleView',
						itemId:'MaterialMainGridBase2SaleView_itemId',
						region:'center'
					}
			      ],
			      listeners: {
			    	  show:function(){
			     			var me = this;
			     			var grid = me.queryById('MaterialMainGridBase2SaleView_itemId');
			     			var form = me.queryById('MaterialMainFormBase2SaleView_itemId');
			     			var formValues={};
			     			formValues = form.getValues();
			     			if("BZ"==me.type){
			     				//102999995，102999996，102999997，102999998
//			     				formValues['type']="BZ";
//			     				me.type='BZ';
			     				Ext.Array.each(grid.columns, function(column) {
									if("undefined"!=typeof(column.dataIndex) && "matnr"==column.dataIndex){
										column.show();
									}
								});
			     				form.getForm().findField("matnr").show();
			     				if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品 只查看可多选
//			     					var selModel =grid.getSelectionModel();
//				     				selModel.setSelectionMode("SINGLE");
				     				grid.down('actioncolumn').setText("查看")
				     				grid.down('actioncolumn').show();
				     				form.getForm().findField("type").setValue(me.type);
				     				form.getForm().findField("saleFor").setValue(me.saleFor);
				     				form.getForm().findField("matnr").setValue(me.queryForm);
				     				
			     				}else{
			     					var selModel =grid.getSelectionModel();
				     				selModel.setSelectionMode("SINGLE");
				     				grid.down('actioncolumn').show();
				     				form.getForm().findField("type").setValue(me.type);
				     				
			     				}
			     			
//			     				 
			     			}else if("RJS"==me.type||"CPDJ"==me.type){
			     				if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品 只查看可多选
				     				grid.down('actioncolumn').setText("查看")
				     				grid.down('actioncolumn').show();
				     				form.getForm().findField("type").setValue(me.type);
				     				form.getForm().findField("matnr").setValue(me.queryForm);
				     				form.getForm().findField("matnr").hide();
				     				form.getForm().findField("maktx").hide();
			     				}else{
			     					var selModel =grid.getSelectionModel();
				     				selModel.setSelectionMode("SINGLE");
				     				grid.down('actioncolumn').show();
				     				form.getForm().findField("type").setValue(me.type);
				     				
			     				}
			     			}else if("FB"==me.type){//非标产品
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
//			     					var selModel =grid.getSelectionModel();
//				     				selModel.setSelectionMode("SINGLE");
				     				grid.down('actioncolumn').setText("编辑")
				     				grid.down('actioncolumn').show();
				     				form.getForm().findField("type").setValue(me.type);
				     				form.getForm().findField("saleFor").setValue(me.saleFor);
				     				form.getForm().findField("maktx").setValue(me.queryForm);
			     				} 
//			     				form.getForm().findField("mtart").hide();
			     			}else{
			     				form.getForm().findField("matnr").show();
			     				Ext.Array.each(grid.columns, function(column) {
									if("undefined"!=typeof(column.dataIndex) && "matnr"==column.dataIndex){
										column.show();
									}
								});
//			     				formValues['type']="SJ";
//			     				formValues['matnr']=me.matnr;
			     				me.type='SJ';
			     				form.getForm().findField("type").setValue(me.type);
			     				form.getForm().findField("mtartSJ").setValue(me.matnr);
			     				if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品
			     					form.getForm().findField("matnr").setValue(me.queryForm);
			     				}
			     				
			     			}
			     			
			     			grid.getStore().loadPage(1,{params:{kunnr:me.kunnr}});
//			     			grid.getStore().loadPage(1,{params:formValues});
     								
			     			}
			     	  }
		}
	);