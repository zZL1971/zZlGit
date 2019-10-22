//显示一个标准产品
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.sale.MaterialBZWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MaterialBZWindow',
			requires:["Ext.ux.form.TrieCombobox"],
			sourceShow:null,//显示来源
			saleHeadId:null,//销售单主表id
			flowInfo:null,//审核信息
			shouDaFang:null,//售达方
			materialPropertyItemId:null,//物料属性价格id
			materialPrice:null,//物料属性价格
			propertyItemInfo:null,//物料属性价格描述
			propertyItems:null,//后台查询 组合属性数组
			saleItemId:null,//销售行id
			selStatus:null,//属性下拉标识，查看时不需啊去后台查价格
			//loadStatus从哪个页面进入
			// 2：我的物品 显示非标产品 ,3:订单
			loadStatus:null,
			//buttonStatus:0 查看，1：新增,2:修改
			buttonStatus:null,
			formId:null,
			myGoodsId:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'fit',
			
			tbar : [{
						hidden:true,
						xtype : 'button',
						text : '加入我的商品',
						itemId : 'add2MyGoods',
						iconCls : 'table_add'
					},//保存价格
					{
						hidden:true,
						xtype : 'button',
						text : '保存价格',
						itemId : 'updateSaleItem',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('updateSaleItem',"BZ");
						}
					},//保存附加信息
					{
						hidden:true,
						xtype : 'button',
						text : '保存',
						itemId : 'saveFj',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('saveFj',"BZ");
						}
					}
					],
			initComponent : function() {
//				alert("BZ"+this.loadStatus);
				var me = this;
				var _formId = me.formId;
				var _loadStatus = me.loadStatus;
				
				//获取审核信息
				if(me.saleHeadId!=null && me.saleHeadId.length>0){
					me.flowInfo = Ext.ux.DataFactory.getFlowActivityId(me.saleHeadId);
				}
				var jsonResult = [];
				
				//加载动态属性
				if("1"==me.buttonStatus){//新增
					Ext.Ajax.request({
						url : 'main/mm/queryDataDicts',
						method : 'GET',
						params : {
							'id' : _formId
						},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							jsonResult = Ext.decode(response.responseText);  
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
					});
				}else{//0: 查看，2:修改 
					//新增时没有属性
					if(me.propertyItemInfo==null || me.propertyItemInfo.length==0){
						jsonResult = [];
					}else{
						Ext.Ajax.request({
							url : 'main/mm/queryDataDicts',
							method : 'GET',
							params : {
								'id' : _formId
							},
							async:false,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								jsonResult = Ext.decode(response.responseText);  
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","加载数据失败！");
							}
						});
					}
				}
				
				/*组合动态属性start*/
				var _propertyItems = [];
				for(var i=0;i<jsonResult.length;i++){
//					var _infoDesc = jsonResult[i].infoDesc;
					var _name = jsonResult[i].name;
					var _code = jsonResult[i].code;
					var _items = jsonResult[i].items;
					
					var _dataDesc = [];
					for(var k=0;k<_items.length;k++){
						var _itemid1 = _items[k].code;
						var _itempropertyDesc1 = _items[k].name;
						var _price = _items[k].price;
						var _data1 = {
								price:_price,
								propertyDesc : _itempropertyDesc1,
								id : _itemid1
					    };
						_dataDesc.push(_data1);
					}
					
					var _infoStore = Ext.create('Ext.data.Store',{
						   fields : ['propertyDesc', 'id','price'],
						   data : _dataDesc
	        		});
					var _fieldDesc = {
				        	labelWidth: 110,
				        	xtype : 'combobox',
				        	fieldLabel: _name,
				        	name:_code,
//				        	name: _infoDesc,
//				        	code:_code,
				        	queryMode: 'local',
				        	emptyText: '请选择...',
				        	blankText: '请选择'+_name,
				        	allowBlank: false,
				        	editable : false,
				        	store : _infoStore,
						    displayField : 'propertyDesc',
						    valueField : 'id',
					}
					
					_propertyItems.push(_fieldDesc);
				}
				me.propertyItems = _propertyItems;
				/*组合动态属性end*/
				
//				var priceConditionStore = Ext.create("SMSWeb.store.mm.pc.Store4PriceCondition");
//				var priceTypeCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PRICE_TYPE'});//价格类型
				
				var topForm = Ext.widget('form',{
		               itemId: 'topForm_ItemId',
		               bodyStyle : "padding:5px;",
		               layout: {
		                   type: 'table',
		                   columns: 2
		               },
		               defaults: {height: 250},
		               fieldDefaults: {
			    	        labelAlign: 'right',
			    	        labelWidth: 100,
			    	        width:300
			    	   },
			    	   border : false,
			    	   items:[
									{xtype:'fieldset',title:'',
									    items:[
									        {xtype:'image',itemId:'img1',width:250,height:250}
									    ]
									},
									{   xtype: 'fieldset',
										border : false,
//										itemId: 'propertyItem_ItemId',
										items:_propertyItems
									}
			    	          ]
				});
				
				/*商品信息 strat*/
				var headForm = Ext.widget('form',{
					   title: '产品信息',
		               itemId: 'headForm_ItemId',
		               bodyStyle : "padding:5px;padding-top:10px;",
			    	   border : false,
			    	   fieldDefaults: {
			    	        labelAlign: 'left',
			    	        width: 300,
			    	        labelWidth: 100
			    	   },
			    	   items:[

								{
									xtype: 'fieldcontainer',
								    layout: 'hbox',
								    items: [
									        {
									            xtype     : 'textfield',
									            name      : 'matnr',
									            fieldLabel: '产品编号',
									            maxLength :18
									        },
									        {
									        	xtype:'dictcombobox',
									        	labelStyle : 'padding-left:15px;',
												fieldLabel : '产品类型',
												name:'mtart',
												dict:'MATERIAL_MTART',
												emptyText: '请选择...',
											    blankText: '请选择物料产品'
									        }
									        
								    ]
								},
							/*	{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'textfield',
									        	name      : 'vkorg',
									        	fieldLabel: '销售组织',
									        	maxLength :4
									        },
									        {
									        	labelStyle : 'padding-left:15px;',
									        	xtype     : 'textfield',
									        	name      : 'vtweg',
									        	fieldLabel: '分销渠道',
									        	maxLength:25
									        }
									        ]
								},*/
								{
									   xtype: 'fieldcontainer',
									   layout: 'hbox',
									   items: [
									           {
									        	   xtype     : 'textfield',
									        	   name      : 'matkl',
									        	   fieldLabel: '物料组',
									        	   maxLength:15
									           },
									           {
									        	   labelStyle : 'padding-left:15px;',
									        	   xtype     : 'textfield',
									        	   name      : 'extwg',
									        	   fieldLabel: '颜色',
									        	   maxLength:15
									           }
									           ]
								},
								{
									   xtype: 'fieldcontainer',
									   layout: 'hbox',
									   items: [
									           {
														xtype:'textfield',
														fieldLabel : '可配置物料',
														name : 'kzkfgdesc',
														readOnly : true
											   },
									           {
									        	   labelStyle : 'padding-left:15px;',
									        	   xtype     : 'numberfield',
									        	   name      : 'kbetr',
									        	   fieldLabel: '价格',
									        	   maxLength:15
									           }
									           ]
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'textfield',
									        	name      : 'groes',
									        	fieldLabel: '规格',
									        	maxLength:50
									        },
									        {
									        	labelStyle : 'padding-left:15px;',
									        	xtype     : 'textfield',
									        	name      : 'meins',
									        	fieldLabel: '单位',
									        	maxLength :15
									        }
									        ]
								},
								{
									xtype: 'fieldcontainer',
								    layout: 'hbox',
								    items: [
									        {
									            xtype     : 'textfield',
									            name      : 'kschl',
									            fieldLabel: '条件类型',
									            maxLength :4
									        },
									        {
									        	   labelStyle : 'padding-left:15px;',
									        	   xtype     : 'textfield',
									        	   name      : 'spart',
									        	   fieldLabel: '产品组',
									        	   maxLength:25
									         }
								    ]
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'textfield',
									        	name      : 'prdha',
									        	fieldLabel: '产品层次',
									        	maxLength:15
									        },
									        {
									        	labelStyle : 'padding-left:15px;',
									        	xtype     : 'textfield',
									        	name      : 'vtext',
									        	fieldLabel: '产品层次描述',
									        	maxLength:15
									        }
									        ]
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
										        {
										        	xtype     : 'textfield',
										        	name      : 'konwa',
										        	fieldLabel: '单位(货币)',
										        	maxLength:15
										        },
												{
													labelStyle : 'padding-left:15px;',
													xtype     : 'numberfield',
													name      : 'brgew',
													fieldLabel: '毛重',
													maxLength:15
												}
									        ]
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'numberfield',
									        	name      : 'ntgew',
									        	fieldLabel: '凈重 ',
									        	maxLength:15
									        },
									        {
									        	labelStyle : 'padding-left:15px;',
									        	xtype     : 'textfield',
									        	name      : 'gewei',
									        	fieldLabel: '重量单位 ',
									        	maxLength:15
									        }
									        ]
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'numberfield',
									        	name      : 'volum',
									        	fieldLabel: '体积',
									        	maxLength:15
									        },
									        {
									        	labelStyle : 'padding-left:15px;',
									        	xtype     : 'textfield',
									        	name      : 'voleh',
									        	fieldLabel: '体积单位 ',
									        	maxLength:15
									        }
									        ]
								},
								{
										xtype: 'fieldcontainer',
										layout: 'hbox',
										items: [
												{
														fieldLabel : '材质',
														name:'textureOfMaterial',
														xtype:'triecombobox',
														trie:'TEXTURE_OF_MATERIAL',
														emptyText: '请选择...',
													    blankText: '请选择材质'
												},
										        {
													   labelStyle : 'padding-left:15px;',
										        	   xtype     : 'numberfield',
										        	   name      : 'price',
										        	   fieldLabel: '炸单价格',
										        	   maxLength:15
												}
										        ]
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'kzkfg',
									name : 'kzkfg',
									readOnly : true
								},
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {
									        	xtype     : 'textareafield',
									        	name      : 'maktx',
									        	fieldLabel: '产品描述',
									        	width: 600,
									        	maxLength :80
									        }
									        ]
								},
								{
						        	   xtype     : 'hiddenfield',
						        	   name      : 'isStandard',
						        	   readOnly : true
						        },
								{
									xtype:'hiddenfield',
									fieldLabel : 'loadStatus',
									name : 'loadStatus',
									value:_loadStatus,
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'status',
									name : 'status',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'kbetrDj',
									name : 'kbetrDj',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'kpein',
									name : 'kpein',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'kmein',
									name : 'kmein',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'krech',
									name : 'krech',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'datab',
									name : 'datab',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'datbi',
									name : 'datbi',
									readOnly : true
								},
								{
									xtype:'hiddenfield',
									fieldLabel : 'ID',
									name : 'id',
									readOnly : true
								}
								
			    	          ]
				});
				/*商品信息 end*/
				
				/*销售价格信息start*/
				var salePriceForm;
				var salePriceGrid;
				var salePricePanel;
				
				if('3'==_loadStatus){
					if("true"==IS_MONEY){
						var salepriceConditionStore = Ext.create("SMSWeb.store.mm.sale.Store4SaleItemPrice");
						var salecountCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'CONDITION'});//运算条件
						var saleplusOrMinusCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PLUS_OR_MINUS'});//加减
						var salepriceTypeCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PRICE_TYPE'});//价格类型
						var saleisTakeNumCombo  = Ext.create('Ext.ux.form.DictCombobox',{dict:'YES_NO'});//是否乘数量
						
						salePriceForm = Ext.widget('form',{
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
							pricefunction()
						});
						function pricefunction() {
							var amount=salePriceForm.getForm().findField("amount").getValue();
						    var materialBaseController = new SMSWeb.controller.mm.MaterialBaseController;
						    var totalGrid=materialBaseController.calculationPrice(salePriceGrid,amount,me.saleHeadId);
						    salePriceForm.getForm().findField("totalPrice").setValue(totalGrid);
						}
						salePricePanel = Ext.widget('panel',{
							itemId: 'salePricePanel_ItemId',
							title: '销售价格信息',
							border : false,
							items:[
							       	salePriceForm,salePriceGrid
							       ]
						});
						
					}
				}
				/*销售价格信息end*/
				var form1 = Ext.widget('form',{
					   title: '附加信息',
		               itemId: 'form1_ItemId',
		               bodyStyle : "padding:5px;padding-top:10px;",
			    	   border : false,
			    	   fieldDefaults: {
			    	        labelAlign: 'left',
			    	        width: 300,
			    	        labelWidth: 100
			    	   },
			    	   items:[
								{
									xtype: 'fieldcontainer',
									layout: 'hbox',
									items: [
									        {	allowBlank: false,
									        	xtype     : 'textareafield',
									        	name      : 'zzazdr',
									        	fieldLabel: '安装位置',
									        	height:30,
									        	width: 600,
									        	maxLength :20
									        }
									        ]
								},
								{
						        	   xtype     : 'hiddenfield',
						        	   name      : 'id',
						        	   readOnly : true
						        },
						        {
						        	   xtype     : 'hiddenfield',
						        	   name      : 'saleItemId',
						        	   readOnly : true
						        },
						        {
						        	   xtype     : 'hiddenfield',
						        	   name      : 'myGoodsId',
						        	   readOnly : true
						        }
			    	          ]
				});
				
				var tabpanel;
				
				if('2'==_loadStatus){
					if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品 隐藏附加信息
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm,form1]
						});
					}
					
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm,form1,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							items:[headForm,form1]
						});
					}
				}
				
				var mainPanel = Ext.widget('panel',{
		               itemId: 'mainPanel_ItemId',
//		               autoScroll: true,
			    	   border : false,
			    	   bodyStyle:'overflow-y:auto;overflow-x:hidden;',
			    	   items:[topForm,tabpanel]
				});
			    	   
				Ext.apply(me, {
					items : [mainPanel]
				});
				
				//加载数据
				if(_formId!=null){
				   headForm.load({
						url:'main/mm/queryMaterialHeadById',
						params : {
							id : _formId
						},
						method : 'GET',
						async:false,
						success:function(f,action){
							headForm.getForm().findField("loadStatus").setValue(_loadStatus);
							
							if(me.myGoodsId!=null && me.myGoodsId.length>0){
								loadfjForm(form1,me.myGoodsId);//加载附加信息
							}
							
							var _kzkfg = headForm.getForm().findField("kzkfg").getValue();
							if("X"==_kzkfg){
								headForm.getForm().findField("kzkfgdesc").setValue("是");
							}else{
								headForm.getForm().findField("kzkfgdesc").setValue("否");
							}
							//标准产品只读
			     			var headFormFields = headForm.getForm().getFields();
			     			headFormFields.each(function(field) {
			     				field.setReadOnly(true);
			     				field.setFieldStyle('background:#E6E6E6');
			     		    });
							
							var _propertyItemInfo = me.propertyItemInfo;
							
							
							if(_propertyItemInfo!=null&&_propertyItemInfo.length>0){
								var splitArray = _propertyItemInfo.split(",");
								//Ext.util.MixedCollection
								var coll = topForm.getForm().getFields();
								
								for(var i=0;i<coll.getCount();i++){
									var _field = coll.getAt(i);
									_field.readOnly=true;
									var _fieldName = _field.getName();
									for(var k=0;k<splitArray.length;k++){
										var _infoTemp = splitArray[k].split(":");
										if(_fieldName == _infoTemp[1]){
											_field.setValue(_infoTemp[3]);
										}
									}	
								}
								me.selStatus = true;
							}else{
								me.selStatus = true;
							}
							
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
				}
				me.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me = this;
					var form1 = me.queryById("form1_ItemId");//附加信息表单
					Ext.Ajax.request({
						url : 'main/mm/queryMaterialFile',
						method : 'GET',
						params : {
							'pid' : me.formId,
							'fileType': 'PICTURE'
						},
//						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var _responseText = Ext.decode(response.responseText);
							if(_responseText!=null && _responseText.length>0){
								var _fileInfo = _responseText[0];
								me.queryById('img1').setSrc('/main/mm/fileDownload?id='+_fileInfo.id);
							}
						},
						failure : function(response, opts) {
//							Ext.MessageBox.alert("tips","加载数据失败！");image
						}
					});
					var _loadStatus = me.loadStatus;
					if(me.flowInfo){
						var _flowInfo = me.flowInfo;
//						0 未审批1 审批中2 已审批
						if("0"!=_flowInfo.docStatus){
							me.setTitle("标准产品");
							me.queryById('updateSaleItem').hide();
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
								
								var form1Fields = form1.getForm().getFields();
								//订单审价
								if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
									me.queryById('updateSaleItem').show();
									
									form1Fields.each(function(field) {
					     				field.setReadOnly(true);
					     				field.setFieldStyle('background:#E6E6E6');
					     		    });
								//起草
								}else if("gp_store"==_flowInfo.taskGroup && _flowInfo.assignee==true){
									me.queryById('saveFj').show();
								//其他环节	
								}else{
									form1Fields.each(function(field) {
					     				field.setReadOnly(true);
					     				field.setFieldStyle('background:#E6E6E6');
					     		    });
								}
							}
						}else{
							me.queryById('saveFj').show();
						}
					}else{
						if(me.myGoodsId!=null && me.myGoodsId.length>0){
							me.queryById('saveFj').show();
						}else{
							me.queryById('add2MyGoods').show();
						}
					}
					//价格权限判断
					if("false"==IS_MONEY){
						var headForm = me.queryById('headForm_ItemId');
						headForm.getForm().findField("kbetr").hide();
						headForm.getForm().findField("price").hide();
						me.queryById('updateSaleItem').hide();
						me.setTitle("标准产品");
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
					var salePriceGrid = me.queryById('salePriceGrid_ItemId');
					if(salePriceGrid!=null){
						me.salePriceGrid1(IS_MONEY,me.flowInfo,salePriceGrid);
					}
					if("newSaleContentWindow"==me.sourceShow){//下单界面新增产品
						//隐藏加入我的产品按钮和附加信息
						me.queryById('add2MyGoods').hide();
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
