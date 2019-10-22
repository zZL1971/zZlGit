Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.NewOrderDetail', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewOrderDetail',
			border : false,
			overflowY:'auto',
		    bodyStyle:{
	        	'background-color':'#f6f6f6'
	        },
			layout:'border',
			formId:null,
			editFlag:null,
			bodyPadding:'0 13 0 0',
			orderType:null,
			initComponent : function() {
				 var me = this;
				 var NewOrderDetailStore=Ext.create("SMSWeb.store.tm.NewOrderDetailStore");
				/*itemGrid订单子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						region: 'center', 
						alias : 'widget.NewOrderDetail',
						store: NewOrderDetailStore,
						itemId:'saleGrid',
						id:'orderGridDetail_id',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						autoScroll:true,
						minHeight:400, 
						title:'店面已下单未报价明细',
						frame:false,
						border:0,
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
			            columns: [//配置表格列
			                      {header: "大区", width: 200, dataIndex: 'bzirk', sortable: true},
			                      {header: "地区", width: 200, dataIndex: 'regio', sortable: true},
			                      {header: "店面", width: 200, dataIndex: 'kunnr', sortable: true},
			                      {header: "订单号", width: 200, dataIndex: 'orderCode', sortable: true},
			                      {header: "金额", width: 200, dataIndex: 'orderTotal', sortable: true},
			                  ],
						selType : 'cellmodel',
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
								{
									enableKeyNav : true,
									clicksToEdit : 2
						})],
					dockedItems:[{
						xtype:'pagingtoolbar',
						store:NewOrderDetailStore,
						dock:'bottom',
						displayInfo:true,
						displayMsg:"显示 {0} -{1}条，共{2} 条",
						border:false,
						items:['-','每页',{
							xtype:'combobox',
							editable : false,
							width:55,
							listeners:{
								 'render':function(comboBox){ 
								 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
								 	comboBox.setValue(this.store.pageSize);
								 },
							  	 'select':function(comboBox){ 
							  	 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
							  	 	grid.getStore().pageSize = comboBox.getValue();
							  	 	grid.getStore().load({params:{start:0,limit:comboBox.getValue()}});
							  	 }
							},
							store:Ext.create('Ext.data.Store',{
						        fields:['id','name'],
						        data:
						        [
						            {'id':25,'name':25},
						            {'id':50,'name':50},
						            {'id':100,'name':100},
						            {'id':200,'name':200},
						            {'id':500,'name':500}
						        ]
						    }),
						    displayField:'name',
						    valueField:'id'
						},'条']
					}]	
				});
				
				/*itemGrid订单子表信息 end*/
				//生成页面
				Ext.apply(this, {
					items : [itemGrid]
				});
				this.callParent(arguments);
			}
		});