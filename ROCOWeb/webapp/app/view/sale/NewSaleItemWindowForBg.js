Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleItemWindowForBg', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSaleItemWindowForBg',
			formId:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			autoScroll:true,
//			layout:'border',
			tbar : [{
						xtype : 'button',
						text : '查询',
						id : 'querySaleItemForWin',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : '确定',
						id : 'confirmSaleItemForWin'
					}],
			initComponent : function() {
				 var me = this;
				 var form = Ext.widget('form',{
					   bodyStyle : "padding-left:10px;padding-top:10px;",
//		    		   region:'north',
			    	   itemId:'saleItemFormForBg',
			    	   bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   },
			    	   items : [
			    		    {
			                    xtype:'hiddenfield',
			                    fieldLabel: '订单编号',
			                    name: 'orderCode'
			                },
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '品号',
			                    name: 'serialNumber'
			                },
			                {
					        	xtype:'textfield',
								fieldLabel : '类型',
								name:'type'
					        },{
			                    xtype:'textfield',
			                    fieldLabel: '描述',
			                    name: 'itemDesc'
			                },{
			                    xtype:'numberfield',
			                    fieldLabel: '数量',
			                    name: 'amount',
			                    renderer: function(value){
									        return parseInt(value);
								}
				            }
						]
			    	   
				 });
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.SaleGridViewForBg',
						store : Ext.create("SMSWeb.store.sale.SaleItemStoreForBg"),
						itemId:'saleItemGridForBg',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
						selModel:{selType:'checkboxmodel',injectCheckbox:0},
//						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'行号',dataIndex:'posex',width:100,menuDisabled:true},
									 {text:'产品编号',dataIndex:'matnr',width:100,menuDisabled:true},
									 {text:'类型',dataIndex:'mtart',width:100,menuDisabled:true},
									 {text:'描述',dataIndex:'maktx',width:300,menuDisabled:true},
							         {text:'数量',dataIndex:'amount',width:100,menuDisabled:true, xtype: 'numbercolumn',align:'right',
							        	 renderer: function(value){
									        return parseInt(value);
									 }},
							         {text:'投影面积',dataIndex:'touYingArea',width:100,menuDisabled:true,align:'right',hidden:true},
						             {text:'总价',dataIndex:'totalPrice',width:100, xtype: 'numbercolumn',menuDisabled:true,hidden:true},
//						             {text:'订单状态',dataIndex:'status',width:100,menuDisabled:true},
							         {text:'物料id',dataIndex:'materialHeadId',width:0,hidden:true,menuDisabled:true},
							         {text:'materialPropertyItemInfo',dataIndex:'materialPropertyItemInfo',width:0,hidden:true,menuDisabled:true},
							         {text:'是否标准产品',dataIndex:'isStandard',width:0,hidden:true,menuDisabled:true},
							         {text:'销售明细ID',dataIndex:'saleItemId',width:0,hidden:true,menuDisabled:true}
						           ]
				});
				/*itemGrid客户子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				
				this.callParent(arguments);
			}
		});
