Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleWindowForHoleInfo', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSaleWindowForHoleInfo',
			formId:null,
			saleFlag:null,
			sid:null,
			orderid:null,
			queryBgType:null,
			pOrderCode:null,
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
						id : 'querySaleForHoleInfo',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : '确定',
						id : 'confirmSaleForHoleInfo'
					}],
			initComponent : function() {
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
		/*	    		   	{
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
			                },*/
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '原订单编号',
			                    name: 'pOrderCode',
			                    value:me.pOrderCode
			                },
			                {
					        	xtype:'textfield',
								fieldLabel : '行项目编号',
								name:'rowNum',
					        },{
			                    xtype:'textfield',
			                    fieldLabel: '板件名称',
			                    name: 'name',
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '标识码',
			                    name: 'info1'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '完工长度',
			                    name: 'length'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '完工宽度',
			                    name: 'width'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '完工厚度',
			                    name: 'thickness'
			                }
						]
			    	   
				 });
				var saleStoreForBg = Ext.create("SMSWeb.store.sale.SaleStoreForHoleInfo");
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
									 {text:'编号',dataIndex:'id',width:80,menuDisabled:true},
									 {text:'订单编号',dataIndex:'orderid',width:150,menuDisabled:true},
									 {text:'名称',dataIndex:'name',width:150,menuDisabled:true},
									 {text:'正面条码',dataIndex:'cncBarcode1',width:180,menuDisabled:true},
									 {text:'反面条码',dataIndex:'cncBarcode2',width:180,menuDisabled:true},
									 {text:'完工厚度',dataIndex:'thickness',width:150,menuDisabled:true},
									 {text:'开料厚度',dataIndex:'cthickness',width:90,menuDisabled:true},
									 {text:'完工宽度',dataIndex:'width',width:90,menuDisabled:true},
									 {text:'开料宽度',dataIndex:'cwidth',width:80,menuDisabled:true},
									 {text:'完工长度',dataIndex:'length',width:120,menuDisabled:true},
									 {text:'开料长度',dataIndex:'clength',width:100,menuDisabled:true},
									 {text:'数量',dataIndex:'cnt',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
									 {text:'识别码',dataIndex:'info1',width:100,menuDisabled:true}
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
				
/*				//没有看金额权限
				if(IS_MONEY=="false"){
					Ext.Array.each(itemGrid.columns, function(column) {
						if("undefined"!=typeof(column.dataIndex) && ("orderTotal"==column.dataIndex || "fuFuanCond"==column.dataIndex
								|| "fuFuanMoney"==column.dataIndex || "payType"==column.dataIndex)){
							column.hidden=true;
						}
					});
				}*/
				this.callParent(arguments);
			}
		});
