Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleWindowForBg', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSaleWindowForBg',
			formId:null,
			saleFlag:null,
			queryBgType:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			autoScroll:true,
			layout:'border',
			tbar : [{
						xtype : 'button',
						text : '查询',
						id : 'querySaleForWin',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : '确定',
						id : 'confirmSaleForWin'
					}],
			initComponent : function() {
				 var fuFuanCondCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '付款条件',
						emptyText: '请选择...',
						name:'fuFuanType',
						dict:'FU_FUAN_COND'
				});
				
				var payTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '支付方式',
						emptyText: '请选择...',
						name:'payType',
						dict:'PAY_TYPE'
				});

				 var me = this;
				 var form = Ext.widget('form',{
					   bodyStyle : "padding-left:10px;padding-top:10px;",
		    		   region:'north',
			    	   itemId:'saleFormForBg',
			    	   bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   autoScroll:true,
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
			                    fieldLabel: 'saleFlag',
			                    name: 'saleFlag',
			                    value:me.saleFlag
			                },
			                {
			                    xtype:'hiddenfield',
			                    fieldLabel: 'queryBgType',
			                    name: 'queryBgType',
			                    value:me.queryBgType
			                },
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '订单编号',
			                    name: 'orderCode'
			                },
			                {
					        	xtype:'dictcombobox',
								fieldLabel : '订单类型',
								emptyText: '请选择...',
								name:'orderType',
								dict:'ORDER_TYPE'
					        },{
			                    xtype:'datefield',
			                    fieldLabel: '订单日期从',
			                    name: 'startDate',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '到',
			                    name: 'endDate',
			                    format :'Y-m-d'
				            },{
			                    xtype:'textfield',
			                    fieldLabel: '售达方',
			                    name: 'shouDaFang'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '售达方名称',
			                    name: 'kunnrName1'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '店面电话',
			                    name: 'dianMianTel'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '客户姓名',
			                    name: 'name1'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '联系电话',
			                    name: 'tel'
			                }/*,{
			                    xtype:'textfield',
			                    fieldLabel: 'SAP编号',
			                    name: 'sapOrderCode'
			                }*/
						]
			    	   
				 });
				var saleStoreForBg = Ext.create("SMSWeb.store.sale.SaleStoreForBg");
				/*itemGrid变更子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.SaleGridViewForBg',
						store : saleStoreForBg,
						itemId:'saleGridForBg',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:30,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true},
					//				 {text : '编辑',xtype:'actioncolumn',align:'center',id:'modifyMaterial',iconCls:'table_edit',width:40},
//									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
//											icon:'/resources/images/remarks1.png'
//											,tooltip:'编辑'
//											,handler:function(grid,rowIndex,colIndex){
//												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
//											}
//									 }]},
									 
									 {text:'订单编号',dataIndex:'orderCode',width:120,menuDisabled:true},
									 //{text:'SAP编号',dataIndex:'sapOrderCode',width:120,menuDisabled:true},
									 {text:'订单类型',dataIndex:'orderType',width:90,menuDisabled:true,
										 renderer: function(value,metadata,record){  
										 				var orderTypeStore = Ext.data.StoreManager.lookup('orderType_ORDER_TYPE');
										 				var find = orderTypeStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;
										           }},
									 {text:'订单日期',dataIndex:'orderDate',width:90,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d'},//,renderer : Ext.util.Format.dateRenderer('Y-m-d')
//									 {text:'订单状态',dataIndex:'orderStatus',width:100,menuDisabled:true},
									 {text:'售达方',dataIndex:'shouDaFang',width:80,menuDisabled:true},
									 {text:'售达方名称',dataIndex:'kunnrName1',width:120,menuDisabled:true},
									 {text:'店面联系电话',dataIndex:'dianMianTel',width:100,menuDisabled:true},
									 {text:'订单总额',dataIndex:'orderTotal',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
									 {text:'付款条件',dataIndex:'fuFuanCond',width:90,menuDisabled:true,
										 renderer: function(value,metadata,record){  
										 				var fuFuanCondStore = Ext.data.StoreManager.lookup('fuFuanType_FU_FUAN_COND');
//										 				var fuFuanCondStore = fuFuanCondCombobox.getStore();
										 				var find = fuFuanCondStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;
										           }},
									 {text:'付款金额',dataIndex:'fuFuanMoney',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
									 {text:'支付方式',dataIndex:'payType',width:90,menuDisabled:true,
										 renderer: function(value,metadata,record){  
										 				var payTypeStore = Ext.data.StoreManager.lookup('payType_PAY_TYPE');
//										 				var payTypeStore = payTypeCombobox.getStore();
										 				var find = payTypeStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;  
										           }},
									 
									 {text:'客户姓名',dataIndex:'name1',width:100,menuDisabled:true},
									 {text:'联系方式',dataIndex:'tel',width:100,menuDisabled:true},
									 {text:'创建人',dataIndex:'createUser',width:100,menuDisabled:true},
									 {text:'创建时间',dataIndex:'createTime',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'当前环节名称',dataIndex:'jdName',width:120,menuDisabled:true}
						           ],
			           dockedItems:[{
							xtype:'pagingtoolbar',
							store:saleStoreForBg,
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
				/*itemGrid变更子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				
				//没有看金额权限
				if(IS_MONEY=="false"){
					Ext.Array.each(itemGrid.columns, function(column) {
						if("undefined"!=typeof(column.dataIndex) && ("orderTotal"==column.dataIndex || "fuFuanCond"==column.dataIndex
								|| "fuFuanMoney"==column.dataIndex || "payType"==column.dataIndex)){
							column.hidden=true;
						}
					});
				}
				
//				var orderTypeStore = Ext.data.StoreManager.lookup('orderType_ORDER_TYPE');
//				//补单界面进来的
//				if(me.saleFlag=="bdFlag"){
//					orderTypeStore.filter([
//					    {filterFn: function(item) { 
//					    		if(item.get("id")==null || item.get("id")=="" || item.get("id")=="OR3" || item.get("id")=="OR4"){
//					    			return true;
//					    		}
//					    	}
//					    }
//					]);
//				}
				
				
				this.callParent(arguments);
			}
		});
