//补件界面（客服补购/免费订单）
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.sale.MaterialBJWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MaterialBJWindow',
			sourceShow:null,//显示来源
			saleHeadId:null,//销售单主表id
			saleItemId:null,//销售行id
			flowInfo:null,//审核信息
			//loadStatus从哪个页面进入
			// 2：我的物品 显示非标产品 ,3:订单
			loadStatus:null,
			formId:null,
			matnr:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth *  0.8,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			type:null,//OR3,OR4
			tbar : [
					{
						hidden:true,
						xtype : 'button',
						text : '保存',
						itemId : 'saveBJ',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('saveBJ');
						}
					},
					{
						hidden:true,
						xtype : 'button',
						text : '保存价格',
						itemId : 'updateSaleItem',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('updateSaleItem',"BJ");
						}
					}
					],
			initComponent : function() {
//				alert("BJ"+this.loadStatus);
				var me = this;
				var _formId = me.formId;
				var _loadStatus = me.loadStatus;
				
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
				
				var headForm;
				
				headForm = Ext.widget('form',{
					   region:'north',
					   title: '基本信息',
		               itemId: 'headForm_ItemId',
		               bodyStyle : "padding:5px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
			    	   border : false,
			    	   fieldDefaults: {
			    	        labelAlign: 'left',
			    	        width: 300,
				        	labelWidth: 110
			    	   },
			    	   items:[
								{xtype:'fieldset',title:'',
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
													 xtype     : 'hiddenfield',
													  name      : 'saleHeadId'
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
													width: 600
													//maxLength :80
												},
												/*{
													xtype     : 'textareafield',
													name      : 'miaoshu',
													fieldLabel: '产品描述',
													hidden:true
												},*/
												{
													xtype: 'checkboxgroup',
													fieldLabel: '产线',
													width:80,
													columns:3,
													allowBlank  : false,
													items:[
													       {boxLabel:'柜身补料组（ A ）',name:'bgdispo',inputValue:'A'},//BGDISPO
													       {boxLabel:'五金线（ H ）',name:'bgdispo',inputValue:'H'},
													       {boxLabel:'装饰件线（ C ）',name:'bgdispo',inputValue:'C'},
													       {boxLabel:'吸塑线（ D ）',name:'bgdispo',inputValue:'D'},
													       {boxLabel:'门板线（ E ）',name:'bgdispo',inputValue:'E'},
													       {boxLabel:'移门线（ G ）',name:'bgdispo',inputValue:'G'}
													],
													blankText:'至少选择一项',
													listeners:{
														change:function(newValue,oldValue,eOpts){
														}
													}
												},
												{
													xtype:'hiddenfield',
													fieldLabel : 'loadStatus',
													name : 'loadStatus',
													value:_loadStatus,
													readOnly : true
												}
								           
								           ]
								},
						        {xtype:'fieldset',title:'出错信息',
								    items:[
										{
											allowBlank: false,
											xtype     : 'textareafield',
											name      : 'zztsnr',
											fieldLabel: '投诉内容描述',
											width: 600
											//maxLength :80
										},
										{
											allowBlank: false,
											xtype     : 'textareafield',
											name      : 'zzxbmm',
											fieldLabel: '需补材料规格',
											width: 600
											//maxLength :80
										},
										{
											 xtype: 'datefield',
										 //       anchor: '100%',
										        fieldLabel: '投诉日期',
										        name: 'complaintTime',
										        value: new Date(), 
												  format: 'Y-m-d'
										},
										{
											xtype: 'fieldcontainer',
										    layout: 'hbox',
										    items: [
//											        {
//											            xtype     : 'dictcombobox',
//											            name      : 'zzezx1',
//											            dict:'CCZX',
//											            fieldLabel: '出错中心1',
//											            maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											            name      : 'zzebm1',
//											            dict:'CCBM',
//											            fieldLabel: '出错部门1',
//											            maxLength :10
//											        }
													{
														xtype:'textfield',
														name	:'barcode',
														fieldLabel:'孔位程序编号',
														width:600
													}
										    ]
										},
										{
											xtype: 'fieldcontainer',
										    layout: 'hbox',
										    items: [
//											        {
//											            xtype     : 'dictcombobox',
//											            name      : 'zzezx1',
//											            dict:'CCZX',
//											            fieldLabel: '出错中心1',
//											            maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											            name      : 'zzebm1',
//											            dict:'CCBM',
//											            fieldLabel: '出错部门1',
//											            maxLength :10
//											        }
													{
														xtype:'textfield',
														name	:'duty',
														fieldLabel:'责任人',
														width:600
													}
										    ]
										},
										{
											xtype: 'fieldcontainer',
										    layout: 'hbox',
										    items: [
//											        {
//											            xtype     : 'dictcombobox',
//											            name      : 'zzezx1',
//											            dict:'CCZX',
//											            fieldLabel: '出错中心1',
//											            maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											            name      : 'zzebm1',
//											            dict:'CCBM',
//											            fieldLabel: '出错部门1',
//											            maxLength :10
//											        }
													{
														xtype:'textfield',
														name	:'zzezx',
														fieldLabel:'出错中心',
														width:600
													}
										    ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
//											        {
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzezx2',
//											        	dict:'CCZX',
//											        	fieldLabel: '出错中心2',
//											        	maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzebm2',
//											        	dict:'CCBM',
//											        	fieldLabel: '出错部门2',
//											        	maxLength :10
//											        }
													{
														xtype:'textfield',
														name:'zzebm',
														fieldLabel:'出错部门',
														width:600
													}
											        ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
//											        {
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzecj1',
//											        	dict:'CCCJ',
//											        	fieldLabel: '出错车间1',
//											        	maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzescx1',
//											        	dict:'CCSCX',
//											        	fieldLabel: '出错生产线1',
//											        	maxLength :10
//											        }
													{
														xtype:'textfield',
														name:'zzecj',
														fieldLabel:'出错车间',
														width:600,
														hidden:true
													}
											        ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
//											        {
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzecj2',
//											        	dict:'CCCJ',
//											        	fieldLabel: '出错车间2',
//											        	maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzescx2',
//											        	dict:'CCSCX',
//											        	fieldLabel: '出错生产线2',
//											        	maxLength :10
//											        }
													{
														xtype:'textfield',
														name:'zzescx',
														fieldLabel:'出错生产线',
														width:600,
														hidden:true
													}
											        ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
//											        {
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzrgx1',
//											        	dict:'ZRGX',
//											        	fieldLabel: '责任工序1',
//											        	maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzelb1',
//											        	dict:'CCLB',
//											        	fieldLabel: '出错类别1',
//											        	maxLength :10
//											        }
													{
														xtype:'textfield',
														name:'zzrgx',
														fieldLabel:'责任工序',
														width:600,
														hidden:true
													}
											        ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
//											        {
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzrgx2',
//											        	dict:'ZRGX',
//											        	fieldLabel: '责任工序2',
//											        	maxLength :10
//											        },
//											        {
//											        	labelStyle : 'padding-left:15px;',
//											        	xtype     : 'dictcombobox',
//											        	name      : 'zzelb2',
//											        	dict:'CCLB',
//											        	fieldLabel: '出错类别2',
//											        	maxLength :10
//											        }
													{
														xtype:'textfield',
														name:'zzelb',
														fieldLabel:'出错类型',
														width:600,
														hidden:true
													}
											        ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
													{
														xtype:'textfield',
														name:'zzccz',
														fieldLabel:'出错组',
														width:600
													}
											       ]
										},{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
													{
														xtype:'textfield',
														name:'cpmc',
														fieldLabel:'产品名称',
														width:600
													}
											       ]
										},
										{
											xtype: 'fieldcontainer',
											layout: 'hbox',
											items: [
													{
														xtype:'textfield',
														name:'zzccwt',
														fieldLabel:'出错问题',
														width:600
													}
											       ]
										}
								    ]
								}
			    	          ]
			});
				var fileGrid;
				fileGrid = Ext.widget('grid',{
					itemId:'BJ_gridItem',
					title: '附件',
					border:false,
					viewConfig:{
					    enableTextSelection:true
					},
				    tbar: [
					        { xtype: 'button', text: '附件上传',iconCls:'table_add',itemId:'uploadFile',
            	        	    handler : function() {
             	        		   this.up('window').fireEvent('fileUploadButtonClick','BJ');
    							}
					        }
					       ,{ xtype: 'button', text: '附件无效',iconCls:'table_remove', itemId:'deleteFile', 
	            	    	   handler : function() {
	            	    		   this.up('window').fireEvent('fileDeleteButtonClick','BJ',this.up('grid'));
  							   }   
					    	}
					       ],
						   store : Ext.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
					       selModel:{selType:'checkboxmodel',injectCheckbox:0},
					       columns : [
							          {text:'id',dataIndex:'id',width:0,hidden:true},
					                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer'},
					                  {text:'文件名称',dataIndex:'uploadFileNameOld',width:200,sortable: false,menuDisabled:true},
					                  {text:'备注',dataIndex:'remark',width:200,sortable: false,menuDisabled:true},
					                  {text:'上传人',dataIndex:'createUser',width:80,sortable: false,menuDisabled:true},
					                  {text:'上传日期',dataIndex:'createTime',width:140,sortable: false,menuDisabled:true},
					                  {text:'是否有效',dataIndex:'statusdesc',width:80,sortable: false,menuDisabled:true},
					                  {text:'文件下载',width:100,xtype:'actioncolumn',align:'center',icon:'/resources/images/down.png'
					                	    ,handler : function(grid,rowIndex,colIndex) {
												this.up('window').fireEvent('fileDownloadButtonClick',this.up('grid'),rowIndex,colIndex);
											},sortable: false,menuDisabled:true
					                  }
					                  ]
				});
				/*报价清单 */
				var priceFileGrid;
				priceFileGrid = Ext.widget('grid',{
					itemId:'BJ_PRICE_gridItem',
					title: '报价清单',
					border:false,
					viewConfig:{
					    enableTextSelection:true
					},
				    tbar: [
					        { xtype: 'button', text: '报价清单上传',iconCls:'table_add',itemId:'uploadFile',hidden:true,
            	        	    handler : function() {
             	        		   this.up('window').fireEvent('fileUploadButtonClick','BJ_PRICE',_formId);
    							}
					        }
					       ,{ xtype: 'button', text: '报价清单无效',iconCls:'table_remove', itemId:'deleteFile', hidden:true,
	            	    	   handler : function() {
	            	    		   this.up('window').fireEvent('fileDeleteButtonClick','BJ_PRICE',this.up('grid'));
  							   }   
					    	}
					       ],
						   store : Ext.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
					       selModel:{selType:'checkboxmodel',injectCheckbox:0},
					       columns : [
							          {text:'id',dataIndex:'id',width:0,hidden:true},
					                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer'},
					                  {text:'文件名称',dataIndex:'uploadFileNameOld',width:200,sortable: false,menuDisabled:true},
					                  {text:'备注',dataIndex:'remark',width:200,sortable: false,menuDisabled:true},
					                  {text:'上传人',dataIndex:'createUser',width:80,sortable: false,menuDisabled:true},
					                  {text:'上传日期',dataIndex:'createTime',width:140,sortable: false,menuDisabled:true},
					                  {text:'是否有效',dataIndex:'statusdesc',width:80,sortable: false,menuDisabled:true},
					                  {text:'文件下载',width:100,xtype:'actioncolumn',align:'center',icon:'/resources/images/down.png'
					                	    ,handler : function(grid,rowIndex,colIndex) {
												this.up('window').fireEvent('fileDownloadButtonClick',this.up('grid'),rowIndex,colIndex);
											},sortable: false,menuDisabled:true
					                  }
					                  ]
				});
				//add by Mark on 2016-04-12--start
				//限制OR4订单上传到SAP后不能在上传报价单
				priceFileGrid.store.on("load",function(){
					var _orderType=priceFileGrid.up("window").type;
					if(_orderType && _orderType.toUpperCase()=="OR4"){
						 var _store = priceFileGrid.getStore();
						 var _uploadBtnHidden = _store && !!_store.getCount();
						 if(_uploadBtnHidden){
							 var btn=Ext.ComponentQuery.query('MaterialBJWindow grid[itemId=BJ_PRICE_gridItem] button[itemId=uploadFile]');
							 if(btn && btn.length>0)
							 btn[0].hide();
							 btn=Ext.ComponentQuery.query('MaterialBJWindow grid[itemId=BJ_PRICE_gridItem] button[itemId=deleteFile]');
							 if(btn && btn.length>0)
							 btn[0].hide();
						 }
					}
				});
				//add by Mark on 2016-04-12--end
				var filesTabpanel;
				filesTabpanel = Ext.widget('tabpanel',{
					title: '文件信息',
					itemId: 'filesTabpanel_ItemId',
					items:[fileGrid,priceFileGrid]
				});
				
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
						    //selModel:{selType:'checkboxmodel',injectCheckbox:0},
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
							bodyStyle:'overflow-y:auto;overflow-x:hidden',
							title: '销售价格信息',
							border : false,
							items:[
							       	salePriceForm,salePriceGrid
							       ]
						});
					}
					
				}
				
				
				if('2'==_loadStatus){
					tabpanel = Ext.widget('tabpanel',{
						border : false,
						region:'center',
						itemId: 'tabpanel_ItemId',
						items:[headForm,filesTabpanel]
					});
					priceFileGrid.tab.hide();
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							region:'center',
							border : false,
							itemId: 'tabpanel_ItemId',
							items:[headForm,filesTabpanel,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							region:'center',
							border : false,
							itemId: 'tabpanel_ItemId',
							items:[headForm,filesTabpanel]
						});
						priceFileGrid.tab.hide();
					}
				}
				
				/*var mainPanel = Ext.widget('panel',{
						itemId: 'mainPanel_ItemId',
						autoScroll: true,
						border : false,
						items:[
						       	tabpanel
						       ]
					});*/
				
				Ext.apply(me, {
					items : [tabpanel]
				});
				
				//加载数据
				if(_formId!=null){
					headForm.load({
						url : 'main/myGoods/getMaterialBujian',
						params : {
							id : _formId
						},
						method : 'GET',
						async:false,
						success:function(f,action){
							headForm.getForm().findField("bgdispo").setValue(action.result.data.bgdispo.split("/"));
							headForm.getForm().findField("loadStatus").setValue(_loadStatus);
							fileGrid.getStore().load({params:{'fileType':'BJ','pid':_formId}});
							priceFileGrid.getStore().load({params:{'fileType':'BJ_PRICE','pid':_formId}});
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
					filesTabpanel.setDisabled(true);
					Ext.Ajax.request({
						url : 'main/mm/queryMaterialHeadByMatnr2',
						method : 'GET',
						params : {
							'matnr' : me.matnr
						},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var _values = Ext.decode(response.responseText);
							
							if(_values.success){
								headForm.getForm().findField("miaoshu").setValue(_values.data.maktx);
//								headForm.getForm().findField("materialHeadId").setValue(_values.data.materialHeadId);
							}
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","加载数据失败!");
						}
					});
					
				}
				me.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me = this;
//					console.log(me.flowInfo);
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
									me.queryById('updateSaleItem').show();
									var bts = Ext.ComponentQuery.query("MaterialBJWindow grid[itemId=BJ_gridItem] button");
									if(bts!=null && bts.length>0){
			                        	for(var i=0;i<bts.length;i++){
			                        		bts[i].hide();
			                        	}
				                    }
									var bts = Ext.ComponentQuery.query("MaterialBJWindow grid[itemId=BJ_PRICE_gridItem] button");
									if(bts!=null && bts.length>0){
										for(var i=0;i<bts.length;i++){
											bts[i].show();
										}
									}
								//门店起草状态
								}else if("gp_customer_service"==_flowInfo.taskGroup && _flowInfo.assignee==true){
									me.queryById("saveBJ").show();
									headFormFields.each(function(field) {
										field.setReadOnly(false);
									});
									
								}else{
									var bts = Ext.ComponentQuery.query("MaterialBJWindow grid[itemId=BJ_gridItem] button");
									if(bts!=null && bts.length>0){
			                        	for(var i=0;i<bts.length;i++){
			                        		bts[i].hide();
			                        	}
				                    }
								}
							}
						}else{
							me.queryById("saveBJ").show();
							
						}
					}else{
						me.queryById("saveBJ").show();
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
