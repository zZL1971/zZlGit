//散件界面
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.sale.MaterialSJWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MaterialSJWindow',
			sourceShow:null,//显示来源
			saleHeadId:null,//销售单主表id
			saleItemId:null,//销售行id
			flowInfo:null,//审核信息
			//loadStatus从哪个页面进入
			// 2：我的物品 显示非标产品 ,3:订单
			loadStatus:null,
			formId:null,
			kunnr:null,
			matnr:null,
			bgOrderType:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth *  0.82,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			tbar : [
					{
						hidden:true,
						xtype : 'button',
						text : '保存',
						itemId : 'saveSJ',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('saveSanjian');
						}
					}
					],
			initComponent : function() {
//				alert("SJ"+this.loadStatus);
				var me = this;
				var _formId = me.formId;
				var _loadStatus = me.loadStatus;
				var _kunnr = me.kunnr;
				//saleHeadId获取订单审核信息
				if(me.saleHeadId!=null && me.saleHeadId.length>0){
					me.flowInfo = Ext.ux.DataFactory.getFlowActivityId(me.saleHeadId);
				}
				
				var tabpanel;
				/*销售价格信息*/
				var salePriceForm;
				var salePriceGrid;
				var salePricePanel;
				/*销售价格信息*/
				
				/*if('2'==_loadStatus){
					tabpanel = Ext.widget('tabpanel',{
						itemId: 'tabpanel_ItemId',
						items:[headForm]
					});
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm]
						});
					}
				}*/
				var headForm;
				var itemGrid;
				
				headForm = Ext.widget('form',{
					   region:'north',
					   title: '',
		               itemId: 'headForm_ItemId',
		               bodyStyle : "padding:5px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
			    	   border : false,
			    	   fieldDefaults: {
			    	        labelAlign: 'left',
			    	        width: 300,
				        	labelWidth: 110
			    	   },
			    	   items:[
				    	          {
				    	        	  xtype     : 'hiddenfield',
				    	        	  name      : 'materialHeadId'
				    	          },
				    	          {
				    	        	  xtype     : 'hiddenfield',
				    	        	  name      : 'id'
				    	          },
								{
								    xtype     : 'displayfield',
								    name      : 'matnr',
								    fieldLabel: '产品编号',
								    maxLength :18
								},
								{
						        	xtype     : 'textareafield',
						        	name      : 'miaoshu',
						        	fieldLabel: '产品描述',
						        	width: 600,
						        	maxLength :80
						        },
						        {
									xtype:'hiddenfield',
									fieldLabel : 'loadStatus',
									name : 'loadStatus',
									value:_loadStatus,
									readOnly : true
								}
			    	          ]
			});
				
				itemGrid = Ext.widget('grid',{
					border : false,
					title: '明细信息',
					viewConfig:{
					    enableTextSelection:true
					},
					tbar : [
							{xtype: 'button', text: '添加',iconCls:'table_add',itemId:'add',
		    	        	    handler : function() {
		    	        		   this.up('window').fireEvent('addButtonClick');
									}
		    	            },
		        	        {xtype: 'button', text: '删除',iconCls:'table_remove',itemId:'delete', 
		        	    	   handler : function() {
		        	    		   this.up('window').fireEvent('deleteButtonClick');
									}  
		        	      	}
							],
					itemId: 'itemGrid_ItemId',
					features:[{ftype: 'summary',dock:'bottom'}],
					store : Ext.create("SMSWeb.store.mm.sale.Store4MaterialSJ"),
				       selModel:{selType:'checkboxmodel',injectCheckbox:0},
				       columns : [
				                  {text:'id',dataIndex:'id',width:0,hidden:true},
				                  {text:'产品编号',dataIndex:'matnr',width:150,sortable: false,menuDisabled:true
				                  },
				                  {text:'描述',dataIndex:'miaoshu',width:300,sortable: false,menuDisabled:true
				                  },
				                  {text:'单位',dataIndex:'meins',width:80,sortable: false,menuDisabled:true},
				                  {text:'数量',dataIndex:'amount',width:80,summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);},sortable: false,menuDisabled:true, xtype: 'numbercolumn',align:'right'
				                	  ,editor:{
				                		  allowDecimals:false,//不允许输入小数
				                		  minValue:1,
				                		  xtype:'numberfield',
				                		  allowBlank:true
				                	  },renderer: function(value){
									        return parseInt(value);
									  }
				                  },
				                  {text:'materialHeadId',dataIndex:'materialHeadId',width:0,sortable: false,menuDisabled:true
					                	,hidden:true  
					              },
				                  {text:'单价',dataIndex:'price',width:80,sortable: false,menuDisabled:true, xtype: 'numbercolumn',align:'right'
				                  },
				                  {text:'折扣',dataIndex:'zhekou',width:80,sortable: false, xtype: 'numbercolumn',menuDisabled:true,align:'right'
				                	  ,editor:{
				                		  xtype:'numberfield',
				                		  allowBlank:true
				                	  } 
				                  },
				                  {text:'总价',dataIndex:'totalPrice',width:100,sortable: false,summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总价：{0} </font>', value);}, xtype: 'numbercolumn',menuDisabled:true,align:'right'
				                  
				                  }
				                  
				                  ],
	                  selType : 'cellmodel',
	                  plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
	                		  {
			                	  enableKeyNav : true,
			                	  clicksToEdit : 2,
			                	  listeners: {
			                		  edit:function( editor, e, eOpts ){
			                			  var _amount = e.record.data.amount;
			                			  var _price = e.record.data.price;
			                			  var _zhekou = e.record.data.zhekou;
			                			  
			                			  var errMsg="";
			                			  if(_amount!=null || _amount!=""){
			                				  if(_amount >=1){
			                					  
			                				  }else{
			                					  errMsg += "数量填写错误!<br/>";
			                				  }
			                			  }else{
			                				  errMsg += "请填写数量!<br/>";
			                			  }
			                			  
			                			  if(_zhekou!=null || _zhekou!=""){
		                					  if(_zhekou>=0 && _zhekou <=1){
		                						  
		                					  }else{
		                						  errMsg += "折扣填写错误!<br/>";
		                					  }
		                				  }else{
		                					  errMsg += "请填写折扣!<br/>";
		                				  }
			                			  
	                					  if(errMsg!=""){
	                							Ext.MessageBox.alert('提示信息',errMsg);
	                							return;
	                					  }
			                			  
			                			  var _totalPrice = _amount * _price * _zhekou;
			                			  
			                			  _totalPrice = _totalPrice.toFixed(2);
			                			  _totalPrice = parseFloat(_totalPrice);
			                			  
		                	              e.record.set('totalPrice',_totalPrice);
//		                	              
		                	              if("3"==_loadStatus){
		                	            	    var _saleTotalPrice = 0;
		                	            	    var itemGridStore = itemGrid.getStore();
			                	          	    for (var i = 0; i <itemGridStore.getCount(); i++) {
			                	        	    	var _model = itemGridStore.getAt(i);
			                	        	    	var _totalPrice2 = _model.get("totalPrice");
			                	        	    	
			                	        	    	if( typeof _totalPrice2=='string' ){
//			                	        	    		console.log('is string');
			                	        	    		_totalPrice2 = parseFloat(_totalPrice2);
			                	        	    	}
			                	        	    	
			                	        	    	_saleTotalPrice += _totalPrice2;
			                	          	    }
			                	          	    
			                	          	    _saleTotalPrice = _saleTotalPrice.toFixed(2);
			                	          	    
			                	          	    _saleTotalPrice = parseFloat(_saleTotalPrice);
			                	          	    
			                	          	    var _type = salePriceGrid.getStore().findRecord("type","PR01");
			                	          	    if(_type!=null){
			                	          	    	_type.set("conditionValue",_saleTotalPrice);
			                	          	    	pricefunction();
			                	          	    }
		                	              }
			                		  }
			                	  }
	                		  })] 
				});
				if('3'==_loadStatus){
					if("true"==IS_MONEY){
						me.maximized=true;//打开窗体最大化设置
						var salepriceConditionStore = Ext.create("SMSWeb.store.mm.sale.Store4SaleItemPrice");
						var salecountCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'CONDITION'});//运算条件
						var saleplusOrMinusCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PLUS_OR_MINUS'});//加减
						var salepriceTypeCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PRICE_TYPE'});//价格类型
						var saleisTakeNumCombo  = Ext.create('Ext.ux.form.DictCombobox',{dict:'YES_NO'});//是否乘数量
						var linePriceSaleItem=Ext.create('SMSWeb.store.mm.sale.Store4MaterialPrice');
						salePriceForm = Ext.widget('form',{
								region:'north',
				               itemId: 'salePriceForm_ItemId',
				               bodyStyle : "padding:5px;padding-top:10px;",
					    	   border : false,
					    	   fieldDefaults: {
					    	        labelAlign: 'left',
					    	        labelWidth: 60
					    	   },
					    	   items:[
										{
											   xtype: 'fieldcontainer',
											   layout: 'hbox',
											   items: [
											           {
											        	   xtype     : 'numberfield',
											        	   name      : 'amount',
											        	   fieldLabel: '数量',
											        	   allowBlank: false,
											        	   minValue:0,
												        	   listeners:{
																	change:function(me, newValue, oldValue, eOpts ){
												        	   			if(newValue!=oldValue)
												        	   			{
												        	   				 pricefunction();
												        	   			}
																		
																	}
																}
											           },
											           {
											        	   labelStyle : 'padding-left:15px;',
											        	   xtype     : 'numberfield',
											        	   name      : 'totalPrice',
											        	   fieldLabel: '总价',
											        	   allowBlank: false,
											        	   minValue:0
											           }
											          ]
										
										}
							          ]
						});
						salePriceGrid = Ext.widget('grid',{
				    	    border : false,
			            	title: '定价条件',
			            	region:'center',
			            	itemId:'salePriceGrid_ItemId',
		        	    	store : salepriceConditionStore,
						    selModel:{selType:'checkboxmodel',injectCheckbox:0},
							viewConfig:{
							    enableTextSelection:true
							},
						    columns : [
							             {text:'id',dataIndex:'id',width:0,hidden:true},
							             {text:'类型',dataIndex:'type',width:180,sortable: false,menuDisabled:true,
//							            	    editor:salepriceTypeCombo,
								             	renderer : function(value, meta, record) {
							 	            		var find= salepriceTypeCombo.getStore().findRecord("id",value);
							 	            		if(find){
							 	            			return find.get('text');
							 	            		}else{
							 	            			return value;
							 	            		}
							 	            	}
							             },
							             {text:'加减',dataIndex:'plusOrMinus',width:80,sortable: false,menuDisabled:true,
//							 	            	editor:saleplusOrMinusCombo,
							 	            	renderer : function(value, meta, record) {
							 	            	   	var find= saleplusOrMinusCombo.getStore().findRecord("id",value);
							 	            		if(find){
							 	            			return find.get('text');
							 	            		}else{
							 	            			return value;
							 	            		}
							 	            	}
							             },
							             {text:'运算条件',dataIndex:'condition',width:80,sortable: false,menuDisabled:true,
//							 	            	editor:salecountCombo,
							 	            	renderer : function(value, meta, record) {
							 	            	   	var find= salecountCombo.getStore().findRecord("id",value);
							 	            		if(find){
							 	            			return find.get('text');
							 	            		}else{
							 	            			return value;
							 	            		}
							 	            	}
							             },
						 	             {text:'运算值',dataIndex:'conditionValue',width:100,align:'right',sortable: false,menuDisabled:true,
						 	            	editor:{
												xtype:'numberfield',
												allowBlank:true
											}
						 	             },
						 	             {text:'小计',dataIndex:'subtotal',width:100,align:'right',sortable: false,menuDisabled:true
//						 	            	,editor:{
//												xtype:'numberfield',
//												allowBlank:true
//											}
						 	             },
						 	             {text:'乘数量',dataIndex:'isTakeNum',width:80,sortable: false,menuDisabled:true,
//							 	            	editor:saleisTakeNumCombo,
							 	            	renderer : function(value, meta, record) {
							 	            	   	var find= saleisTakeNumCombo.getStore().findRecord("id",value);
							 	            		if(find){
							 	            			return find.get('text');
							 	            		}else{
							 	            			return value;
							 	            		}
							 	            	}
							             },
							             {text:'总计',dataIndex:'total',width:100,align:'right',sortable: false,menuDisabled:true
//							            	,editor:{
//												xtype:'numberfield',
//												allowBlank:true
//											}
						 	             },
						 	             {text:'排序',dataIndex:'orderby',width:100,hidden:true,sortable: false,menuDisabled:true,
						 	            	 editor:{
						 	            		 xtype:'numberfield',
						 	            		 allowBlank:true
						 	            	 }
						 	             }
							           ],
				            selType : 'cellmodel',
		  	        	  	plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
		  	        	  			{
		  	        	  				enableKeyNav : true,
		  	        	  				clicksToEdit : 2
		  	        	  	})] 
						});
						salePriceGrid.on('edit', function(editor, e) {
							pricefunction();
						});
						function pricefunction() {
							var amount=salePriceForm.getForm().findField("amount").getValue();
						    var materialBaseController = new SMSWeb.controller.mm.MaterialBaseController;
						    var totalGrid=materialBaseController.calculationPrice(salePriceGrid,amount,me.saleHeadId,me.matnr);
						    salePriceForm.getForm().findField("totalPrice").setValue(totalGrid);
						}
						var priceLine=Ext.widget('grid',{
							border : false,
							title : '报价清单',
							itemId:'priceLine_itemId',
							store:linePriceSaleItem,
							viewConfig : {
								enableTextSelection : true
							},
							selModel : {
								selType : 'checkboxmodel',
								injectCheckbox : 0
							},
							columns : [
									{
										text : 'id',
										dataIndex : 'id',
										width : 0,
										hidden : true
									},
									{
										text : 'pid',
										dataIndex : 'pid',
										width : 0,
										hidden : true
									},
									{
										text : '类型',
										dataIndex : 'type',
										width : 80,
										summaryType : 'count',
										summaryRenderer : function(value) {
											return Ext.String
													.format(
															'<font color=blue>小计</font>',
															value);
										}
									},
									{
										text : '名称',
										dataIndex : 'name',
										width : 100,
										sortable : false,
										align : 'center',
										menuDisabled : true
									},
									{
										text : '宽',
										dataIndex : 'wide',
										width : 70,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									},
									{
										text : '高',
										dataIndex : 'high',
										width : 70,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									},
									{
										text : '深',
										dataIndex : 'deep',
										width : 70,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									},
									{
										text : '颜色',
										dataIndex : 'color',
										width : 100,
										sortable : false,
										menuDisabled : true,
										align : 'center'
									},
									{
										text : '数量',
										dataIndex : 'amount',
										width : 50,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									},
									{
										text : '单位',
										dataIndex : 'unit',
										width : 80,
										sortable : false,
										menuDisabled : true,
										align:'center'
									} ,
									{
										text : '面积',
										dataIndex : 'area',
										width : 80,
										sortable : false,
										menuDisabled : true,
										align:'center',
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									} ,
									{
										text : '单价',
										dataIndex : 'unitPrice',
										width : 80,
										sortable : false,
										menuDisabled : true,
										align:'center',
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									} ,
									{
										text : '总价',
										dataIndex : 'totalPrice',
										width : 100,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										summaryType : 'sum',
										summaryRenderer : function(value) {
											return value;
										}
									},
									{
										text : '折扣',
										dataIndex : 'rebate',
										width : 50,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										editor : {
											xtype : 'numberfield',
											allowBlank : true
										}
									},
									{
										text : '净价',
										dataIndex : 'netPrice',
										width : 80,
										align : 'center',
										sortable : false,
										menuDisabled : true,
										summaryType : 'sum',
										summaryRenderer : function(value) {
											return value;
										}
									},
									{
										text : '产线',
										dataIndex : 'line',
										width : 70,
										align : 'center',
										sortable : false,
										menuDisabled : true
									}],
							selType : 'cellmodel',
							plugins : [ cellEditing = Ext
									.create(
											'Ext.grid.plugin.CellEditing',
											{
												enableKeyNav : true,
												clicksToEdit : 2
											}) ],
							listeners:{
								edit:function(editor, e, eOpts){
									var oldTotalprice=e.record.data.totalPrice;
									var newUnitprice=e.record.data.unitPrice;
									var oldUnitprice=e.record.raw.unitPrice;
									var newArea=e.record.data.area;
									var oldArea=e.record.raw.area;
									var newRebate=e.record.data.rebate;
									//如果面积修改了 就需要改变价格 单价*面积=总价 净价=总价*折扣
									if(newArea!=null&&newArea!=""){
										if(newArea!=oldArea||newUnitprice!=oldUnitprice){
											var totalPrice = newUnitprice * newArea;
											//总价计算 需要进行四舍五入
											var price=totalPrice-oldTotalprice;
											price = parseInt(price.toFixed(0));
											//净价计算
											var model=e.store.getAt(e.rowIdx);
											model.set("totalPrice",totalPrice);
											model.set("netPrice",totalPrice*newRebate);
											//e.record.commit();
											var store=salePriceGrid.getStore();
											store.filterBy(function(record){
												return record.get("type")=="PR01";
											});
											var model = store.getAt(0);
											model.set("conditionValue",model.get("conditionValue")+price);
											store.clearFilter();
											pricefunction();
										}
									}
								}
							},features : [ {
								ftype : 'groupingsummary',
								groupByText : '按当前进行分组',
								showGroupsText : '显示分组',
								groupHeaderTpl : "{columnName}: {groupValue}"
							}/*, {
								ftype : 'summary',
								dock : 'bottom'
							}*/ ],
						});
						salePricePanel = Ext.widget('panel',{
							itemId: 'salePricePanel_ItemId',
							bodyStyle:'overflow-y:auto;overflow-x:hidden;',
							title: '销售价格信息',
							border : false,
							//layout:'border',
							items:[
							       	salePriceForm,salePriceGrid
							       ]
						});
						salePricePanel.add(priceLine);
						salePricePanel.doLayout();
						/* 销售价格信息end */
						priceLine.getStore().load({params:{
							pid:me.saleItemId
						}});
					}
					
				}

				
				if('2'==_loadStatus){
					tabpanel = Ext.widget('tabpanel',{
						itemId: 'tabpanel_ItemId',
						region:'center',
						items:[itemGrid]
					});
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							region:'center',
							items:[itemGrid,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							region:'center',
							items:[itemGrid]
						});
					}
				}
				
				/*var mainPanel = Ext.widget('panel',{
						itemId: 'mainPanel_ItemId',
						autoScroll: true,
						border : false,
						items:[
						       	headForm,tabpanel
						       ]
					});*/
				
				Ext.apply(me, {
					items : [headForm,tabpanel]
				});
				
				//加载数据
				if(_formId!=null){
					headForm.load({
						url : 'main/myGoods/getMaterialSanjianHead',
						params : {
							id : _formId
						},
						method : 'GET',
						async:false,
						success:function(f,action){
							headForm.getForm().findField("loadStatus").setValue(_loadStatus);
							itemGrid.getStore().load({params:{'id':_formId}});
							
							if('3'==_loadStatus){
								if(me.saleItemId){
									if("true"==IS_MONEY){
										salepriceConditionStore.load({params:{'pid':me.saleItemId}});
										var _saleItem;
										Ext.Ajax.request({
											url : 'main/mm/getSaleItem',
											method : 'GET',
											params : {
												'id' : me.saleItemId
											},
											async:false,
											dataType : "json",
											contentType : 'application/json',
											success : function(response, opts) {
												_saleItem = Ext.decode(response.responseText);
												if(_saleItem.success){
													salePriceForm.getForm().findField("amount").setValue(_saleItem.data.amount);
													salePriceForm.getForm().findField("totalPrice").setValue(_saleItem.data.totalPrice);
												}else{
													Ext.MessageBox.alert("提示",_saleItem.errorMsg);
												}
											},
											failure : function(response, opts) {
												Ext.MessageBox.alert("提示","销售价格信息加载失败!");
											}
										});
									}
								}
							}
						},
		                failure:function(form,action){
		                	var result = Ext.decode(action.response.responseText);
		                	headForm.getForm().findField("loadStatus").setValue(_loadStatus);
		                    Ext.Msg.alert('提示',result.errorMsg);
		                }
					});
					
				}else{
					headForm.getForm().findField("matnr").setValue(me.matnr);
					Ext.Ajax.request({
						url : 'main/mm/queryMaterialHeadByMatnr',
						method : 'GET',
						params : {
							'matnr' : me.matnr,
							'kunnr' :_kunnr
						},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var _values = Ext.decode(response.responseText);
							
							if(_values.success){
								headForm.getForm().findField("miaoshu").setValue(_values.data.maktx);
								headForm.getForm().findField("materialHeadId").setValue(_values.data.materialHeadId);
							}
						}
					});
					
				}
				me.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me = this;
					var headForm = me.queryById('headForm_ItemId');
					var _loadStatus = me.loadStatus;
					if(me.flowInfo){
						var _flowInfo = me.flowInfo;
//						0 未审批1 审批中2 已审批
						if("0"!=_flowInfo.docStatus){
							
							var headFormFields = headForm.getForm().getFields();
							headFormFields.each(function(field) {
								field.setReadOnly(true);
							});
							
							var bts  = Ext.ComponentQuery.query("MaterialSJWindow grid[itemId=itemGrid_ItemId] button");
			     			if(bts!=null && bts.length>0){
			     				for(var i=0;i<bts.length;i++){
			     					bts[i].hide();
			     				}
			     			}
							//只读
							if('3'==_loadStatus){
								if("true"==IS_MONEY){
									var salePriceForm = me.queryById('salePriceForm_ItemId');
									var salePriceFormFields = salePriceForm.getForm().getFields();
									salePriceFormFields.each(function(field) {
										field.setReadOnly(true);
										field.setFieldStyle('background:#E6E6E6');
									});
								}
								//订单审价
								if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
									me.queryById("saveSJ").show();
									me.queryById("saveSJ").setText("保存价格");
								//门店起草状态
								}else if("gp_store"==_flowInfo.taskGroup && _flowInfo.assignee==true){
									me.queryById("saveSJ").show();
									var bts  = Ext.ComponentQuery.query("MaterialSJWindow grid[itemId=itemGrid_ItemId] button");
					     			if(bts!=null && bts.length>0){
					     				for(var i=0;i<bts.length;i++){
					     					bts[i].show();
					     				}
					     			}
					     			headFormFields.each(function(field) {
										field.setReadOnly(false);
									});
									
								}else{
									
								}
							}
						}else{
							me.queryById("saveSJ").show();
							
						}
					}else{
						me.queryById("saveSJ").show();
					}
					
					if('3'==_loadStatus){
						if("true"==IS_MONEY){
							var salePriceForm = me.queryById('salePriceForm_ItemId');
							var salePriceFormFields = salePriceForm.getForm().getFields();
							salePriceFormFields.each(function(field) {
								field.setReadOnly(true);
								field.setFieldStyle('background:#E6E6E6');
							});
						}
					}
					/*价格控制*/
					var itemGrid = me.queryById('itemGrid_ItemId');
					me.priceEditSanjianItemGrid1(_loadStatus,IS_MONEY,me.flowInfo,itemGrid);
					
					var salePriceGrid = me.queryById('salePriceGrid_ItemId');
					if(salePriceGrid!=null){
						me.salePriceGrid1(IS_MONEY,me.flowInfo,salePriceGrid);
					}
				},
				close:function(){
					var me = this;
					var _flowInfo = me.flowInfo;
					//审价
					if(me.loadStatus=="3" && "gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
						 Ext.getCmp("newSaleItemGridId").getStore().reload();
						 Ext.getCmp("saleItemPriceGridId").getStore().reload();
					}
				}
			}
			,
			//金额权限
			//_loadStatus哪个页面进入
			//IS_MONEY价格权限
			priceEditSanjianItemGrid1:function(_loadStatus,IS_MONEY,_flowInfo,grid){
				var me = this;
				if(_flowInfo){
					if("2"==_loadStatus){
						me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
					}else if("3"==_loadStatus){
						if("0"!=_flowInfo.docStatus){
							if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
								Ext.Array.each(grid.columns, function(column) {
									if("undefined"!=typeof(column.dataIndex) && "amount"==column.dataIndex){
										column.editor = null;
									}
								});
							}else if("gp_store"==_flowInfo.taskGroup && _flowInfo.assignee==true){
								me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
							}else{
								Ext.Array.each(grid.columns, function(column) {
									if("undefined"!=typeof(column.dataIndex) && "amount"==column.dataIndex){
										column.editor = null;
									}
								});
								me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
							}
						}else{
							me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
						}
					}
				}else{
					if("2"==_loadStatus){
						me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
					}else if("3"==_loadStatus){
						me.priceEditSanjianItemGridEdit(IS_MONEY,grid);
					}
				}
			},
			priceEditSanjianItemGridEdit:function(IS_MONEY,grid){
				if("false"==IS_MONEY){
					Ext.Array.each(grid.columns, function(column) {
						if("undefined"!=typeof(column.dataIndex) && "price"==column.dataIndex){
							column.hide();
						}else if("undefined"!=typeof(column.dataIndex) && "zhekou"==column.dataIndex){
							column.hide();
						}else if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
							column.hide();
						}
					});
				}else if("true"==IS_MONEY){
					Ext.Array.each(grid.columns, function(column) {
						if("undefined"!=typeof(column.dataIndex) && "price"==column.dataIndex){
							column.editor = null;
						}else if("undefined"!=typeof(column.dataIndex) && "zhekou"==column.dataIndex){
							column.editor = null;
						}else if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
							column.editor = null;
						}
					});
				}
			},
			//销售价格
			salePriceGrid1:function(IS_MONEY,_flowInfo,grid){
				var me = this;
				if(_flowInfo){
					if("0"!=_flowInfo.docStatus){
						if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
							
						}else{
							me.salePriceGridEdit(grid);
						}
					}else{
						me.salePriceGridEdit(grid);
					}
				}
			},
			salePriceGridEdit :function (grid){
				Ext.Array.each(grid.columns, function(column) {
					if("undefined"!=typeof(column.dataIndex) && "conditionValue"==column.dataIndex){
						column.editor = null;
					}
				});
			}
		});