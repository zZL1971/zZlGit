
var store=Ext.create("SMSWeb.store.sale.SaleExpenditureStore");
var comstore = Ext.create("SMSWeb.store.sale.SaleComplaintStore")
Ext.define(
				'SMSWeb.view.sale.NewSaleContentBGWindow',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.newSaleContentBGWindow',
					layout : 'border',
					minWidth : 1100,
					minHeight : 550,
					//		    maxWidth:2500,
					//		    maxHeight:1300,
					modal : true,
					shId : null,
					requires : [
							"Ext.ux.form.TrieCombobox"],
					bodyStyle : {
						'background-color' : '#f6f6f6'
					},
					border : 0,
					initComponent : function() {
						var me = this;
						me.height = document.body.clientHeight * 0.98;
						me.width = document.body.clientWidth * 0.99;

						var materialMtartCombobox = Ext.create(
								"Ext.ux.form.DictCombobox", {
									fieldLabel : '产品类型',
									emptyText : '请选择...',
									name : 'materialMtart',
									dict : 'MATERIAL_MTART'
								});
						var drawTypeCombobox = Ext.create(
								"Ext.ux.form.DictCombobox", {
									//fieldLabel : '绘图类型',
									//emptyText: '请选择...',
									name : 'drawType',
									dict : 'MATERIAL_DRAW_TYPE'
								});
						var isStandardCombobox = Ext.create(
								"Ext.ux.form.DictCombobox", {
									fieldLabel : '是否标准产品',
									emptyText : '请选择...',
									name : 'isStandard',
									dict : 'MATERIAL_IS_STANDARD'
								});
						var zzwgfgCombobox = Ext.create(
								"Ext.ux.form.DictCombobox", {
									fieldLabel : '是否外购',
									emptyText : '请选择...',
									name : 'zzwgfg',
									dict : 'MM_ZZWGFG'
								});
						var stateAuditCombobox = Ext.create(
								"Ext.ux.form.DictCombobox", {
									fieldLabel : '审核状态',
									emptyText : '请选择...',
									name : 'stateAudit',
									dict : 'MATERIAL_STATE_AUDIT'
								});
						me.items = [
								{
									xtype : 'form',
									region : 'north',
									bodyPadding : 5,
									maxHeight : 600,
									autoScroll : true,
									id : 'addSaleForm',
									minWidth : 1000,
									border : false,
									fieldDefaults : {
										labelAlign : 'right',
										labelWidth : 95,
										msgTarget : 'qtip'
									},
									bodyStyle : {
										'background-color' : '#f6f6f6'
									},
									items : [
											{
												xtype : 'fieldset',
												title : '基本信息',
												style : {
													'border-style' : 'solid',
													'border-color' : '#d5d5d5',
													'background-color' : '#ffffff'
												},
												collapsible : true,
												
												defaultType : 'textfield',
												layout : 'anchor',
												defaults : {
													anchor : '100%'
												},
												items : [
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		fieldLabel : '订单ID',
																		padding : '0 0 0 1',
																		name : 'shId',
																		hidden : true
																	},
																	{
																		xtype : 'tablecombobox',
																		fieldLabel : '订单类型<font color=red>*</font>',
																		emptyText : '请选择...',
																		allowBlank : false,
																		editable : false,
																		//									value:'OR1',
																		name : 'orderType',
																		id : 'orderType',
																		url : 'main/sale/getOrderByCust/' + me.shId,
																		listeners : {
																			'change' : function(
																					obj,
																					newValue,
																					oldValue,
																					eOpts) {
																				if (newValue != oldValue) {
																					var saleItmes = Ext
																							.getCmp("addSaleItemsGrid");
																					changeOrderTypeByaddSaleV(
																							oldValue,
																							newValue,
																							saleItmes);
																					/*var saleItmes=Ext.getCmp("addSaleItemsGrid");
																					var len=saleItmes.getStore().getRange();
																					if(len.length>0){
																						Ext.MessageBox.confirm('温馨提示','订单类型改变会清空行项目,确定吗?',
																						    	function(btn){
																							        if(btn=='yes'){ 
																							        	saleItmes.getStore().removeAll();
																							        	changeOrderTypeByaddSaleV(newValue);
																							        }else{
																					//															        	obj.setValue(oldValue);
																							        	return false;
																							        }
																						});
																					}else{
																						changeOrderTypeByaddSaleV(newValue);
																					}*/

																				}
																			}
																		}
																	},{
															    		xtype: 'displayfield',
																        fieldLabel: '参考订单<font color=red>*</font>',
																        name: 'pOrderCode',
																        width: 300,
																        hidden:"buDan"==me.orderType?false:true
															    	},																	{
																		xtype : 'button',
																		text : '...',
																		itemId : 'pOrderCodeButton',
																		hidden:"buDan"==me.orderType?false:true
																	 },
																	{
															        	xtype:'dictcombobox',
																		fieldLabel : '订单类型<font color=red>*</font>',
																		emptyText: '请选择...',
																		allowBlank: false,
																		width: 300,
																		name:'orderTypes',
																		dict:'ORDER_TYPE',
																		hidden:true
															        
																	},{
															        	xtype:'dictcombobox',
																		fieldLabel : '是否样品',
																		emptyText: '请选择...',
																		//disabled:true,
																		width: 300,
																		name:'isYp',
																		dict:'YES_NO',
																		hidden:"buDan"==me.orderType?false:true
																	 }, 
																	{
																		xtype : 'tablecombobox',
																		fieldLabel : '设计师<font color=red>*</font>',
																		emptyText : '请选择...',
																		allowBlank : false,
																		editable : false,
																		name : 'designerTel',
																		id : 'designerTel',
																		url : 'main/sale/getDesignerTel/' + me.shId,
																		listeners : {
																			'change' : function(
																					obj,
																					newValue,
																					oldValue,
																					eOpts) {
																				var find = obj
																						.getStore()
																						.findRecord(
																								"id",
																								newValue);
																				if (find) {
																					var val = find
																							.get('text');
																					Ext
																							.getCmp(
																									'jingShouRen')
																							.setValue(
																									val);
																				}
																			}
																		}

																	}
																	/*,{
																		xtype:'combobox',
																	    name: 'designerTel',
																	    fieldLabel: '设计师<font color=red>*</font>',
																	    allowBlank: false
																	}*/
																	,
																	{
																        	xtype:'dictcombobox',
																			fieldLabel : '问题出现',
																			emptyText: '请选择...',
																			//disabled:true,
																			width: 300,
																			name:'isYp',
																			dict:'PROBLEMS',
																			hidden:"buDan"==me.orderType?false:true
																		 }
																	,
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '销售分类<font color=red>*</font>',
																		emptyText : '请选择...',
																		name : 'saleFor',
																		dict : 'SALE_FOR',
																		disabled : false,
																		showDisabled : false,
																		id : 'saleFor',
																		hidden : true,
																		// 默认为普通单
																		value : '0',
																		listeners : {
																			'change' : function(
																					oldValue,
																					newValue,
																					options) {
																				cleanFormStore();
																			}
																		}

																	},
																/*	{
																		xtype : 'dictcombobox',
																		fieldLabel : '是否样品<font color=red>*</font>',
																		hidden : CURR_USER_LOGIN_NO
																				.indexOf("_01") > -1 ? false
																				: true, 只有_02可以不填,																		allowBlank : CURR_USER_LOGIN_NO
																				.indexOf("_01") > -1 ? false
																				: true,只有_02可以不填																		editable:false,																		emptyText : '请选择...',
																		name : 'isYp',
																		dict : 'YES_NO',
																		disabled : CURR_USER_LOGIN_NO
																				.indexOf("_01") > -1 ? false
																				: true
																	只有_01有权编辑样品选项,其它帐户不能编辑																	},*/
																	{
																		allowBlank : false,
																		xtype : 'dictcombobox',
																		fieldLabel : '订单金额范围<font color=red>*</font>',
																		emptyText : '请选择...',
																		//									editable:false,
																		name : 'orderPayFw',
																		dict : 'ORDER_PAY_FW',
																		hidden:"buDan"==me.orderType?true:false
																	} ]
														},{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items:[{
																	xtype : 'datefield',
																	fieldLabel : '安装日期',
																	name : 'anzhuanDay',
																	format : 'Y-m-d',
																	hidden:"buDan"==me.orderType?false:true
																	},	
																	 {
													                    xtype:'textfield',
													                    fieldLabel: '投诉次数',
//																		allowBlank: false,
													                    name: 'tousucishu',
													                    width: 300,
													                    hidden:"buDan"==me.orderType?false:true
													              }]
														},
														/*			                {
														 xtype: 'fieldcontainer',
														 layout: 'hbox',
														 combineErrors: true,
														 defaultType: 'textfield',
														 items: [{
														 //					    		xtype: 'displayfield',
														 fieldLabel: '售达方(客户)<font color=red>*</font>',
														 name: 'shouDaFang',
														 fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
														 value: '',
														 renderer : function(v) { 
														 return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
														 }
														 },{
														 //					    		xtype: 'displayfield',
														 fieldLabel: '送达方(物流园)<font color=red>*</font>',
														 name: 'songDaFang',
														 fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
														 value: '',
														 renderer : function(v) { 
														 return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
														 }
														 },
														 {
														 //						        xtype: 'displayfield',
														 fieldLabel: '售达方名称',
														 name: 'kunnrName1',
														 },
														 {
														 xtype:'datefield',
														 fieldLabel: '订单日期',
														 name: 'orderDate',
														 format :'Y-m-d',
														 readOnly:true
														 }]
														 },*/
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [ {
																xtype : 'dictcombobox',
																fieldLabel : '是否样品<font color=red>*</font>',
																hidden : CURR_USER_LOGIN_NO
																.indexOf("_01") > -1 ? false
																: true,// 只有_02可以不填,
																allowBlank : CURR_USER_LOGIN_NO
																		.indexOf("_01") > -1 ? false
																		: true,// 只有_02可以不填
																emptyText : '请选择...',
																name : 'isYp',
																dict : 'YES_NO',
																listeners : {
																	'change' : function(
																			obj,
																			newValue,
																			oldValue,
																			eOpts) {
																		if (newValue != oldValue) {
																			if(newValue=="1"){
																				Ext.getCmp("shop").show();
																				Ext.getCmp("shopCls").show();
																			}else{
																				Ext.getCmp("shop").hide();
																				Ext.getCmp("shopCls").hide();
																			}
																		}
																	}
																}
															},
															{
																cascadeDict : 'dictcombobox[itemId=shopCls]',
																xtype : 'triecombobox',
																fieldLabel : '店分类<font color=red>*</font>',
																hidden : true,
//																allowBlank : CURR_USER_LOGIN_NO
//																.indexOf("_01") > -1 ? false
//																: true,// 只有_02可以不填
																emptyText : '请选择...',
																name : 'shop',
																id:'shop',
																trie : 'SHOP'
															},
															{
																itemId:'shopCls',
																cascade : true,
																xtype : 'dictcombobox',
																fieldLabel : '1店/N店<font color=red>*</font>',
																hidden : true,
//																allowBlank : CURR_USER_LOGIN_NO
//																.indexOf("_01") > -1 ? false
//																: true,// 只有_02可以不填
																emptyText : '请选择...',
																name : 'shopCls',
																//dict : 'SHOP_CLS',
																id:'shopCls'
															}
															]
														},
														{
															xtype : 'textareafield',
															name : 'remarks',
															rows : 1,
															fieldLabel : '备注'
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		fieldLabel : '内部订单号',
																		padding : '0 0 0 1',
																		id : 'zzaufnrField',
																		name : 'zzaufnr',
																		hidden : true,
																		listeners : {
																			'blur' : function(
																					component,
																					eventObject,
																					opts) {
																				if (Ext
																						.isEmpty(component.getValue)) {
																					Ext
																							.getCmp(
																									'checkFlag')
																							.setValue(
																									'false');
																				}
																				Ext.Ajax
																						.request( {
																							url : 'main/sale/checkZzaufnr',
																							method : 'POST',
																							async : true,
																							params : {
																								zzaufnr : component
																										.getValue()
																							},
																							success : function(
																									response,
																									opts) {
																								var _data = Ext
																										.decode(response.responseText).data;
																								Ext
																										.getCmp(
																												'zzaufnrCheck')
																										.setValue(
																												_data.resultDesc);
																								Ext
																										.getCmp(
																												'checkFlag')
																										.setValue(
																												_data.status);
																							},
																							failure : function(
																									response,
																									opts) {
																								Ext
																										.getCmp(
																												'zzaufnrCheck')
																										.setValue(
																												'不存在');
																								Ext
																										.getCmp(
																												'checkFlag')
																										.setValue(
																												'false');
																							}
																						});
																			}
																		}
																	},
																	{
																		xtype : 'textfield',
																		id : 'zzaufnrCheck',
																		fieldLabel : '内部订单校验',
																		hidden : true,
																		readOnly : true
																	},
																	{
																		xtype : 'textfield',
																		id : 'checkFlag',
																		hidden : true,
																		fieldLabel : '校验结果',
																		value : 'true'
																	} ]
														} ]
											},
											{
												xtype : 'fieldset',
												title : '客户信息',
												style : {
													'border-style' : 'solid',
													'border-color' : '#d5d5d5',
													'background-color' : '#ffffff'
												},
												collapsible : true,
												defaultType : 'textfield',
												layout : 'anchor',
												defaults : {
													anchor : '100%'
												},
												items : [
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		fieldLabel : '客户ID',
																		padding : '0 0 0 1',
																		name : 'tcId',
																		hidden : true
																	},
																	{
																		fieldLabel : '客户ID',
																		padding : '0 0 0 1',
																		name : 'custId',
																		hidden : true
																	},
																	{
																		name : 'name1',
																		fieldLabel : '姓名<font color=red>*</font>',
																		allowBlank : false
																	},
																	{
																		xtype : 'button',
																		text : '...',
																		itemId : 'gainCust'//iconCls:'table_save'
																	 },
																	{
																		fieldLabel : '电话<font color=red>*</font>',
																		name : 'tel',
																		vtype : 'mobilephonecheck',
																		allowBlank : false
																	},
																	{
																		allowBlank : false,
																		xtype : 'dictcombobox',
																		//						        	editable:false,
																		fieldLabel : '性别<font color=red>*</font>',
																		emptyText : '请选择...',
																		name : 'sex',
																		dict : 'SEX'
																	},
/*																	{
																		allowBlank : false,
																		xtype : 'datefield',
																		fieldLabel : '生日<font color=red>*</font>',
																		name : 'birthday',
																		hidden:"buDan"==me.orderType?true:false,
																		validator : function(
																				value) {
																			var _year = value
																					.substr(
																							0,
																							4);
																			var _currYear = new Date()
																					.getFullYear();
																			if (_year <= (_currYear - 200)) {
																				return '年龄应该小于两百';
																			} else {
																				return true;
																			}

																		},
																		format : 'Y-m-d'
																	}*/
																	]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : false,
															defaultType : 'textfield',
															items : [
																	{
																		allowBlank : false,
																		xtype : 'dictcombobox',
																		padding : '0 0 0 1',
																		fieldLabel : '安装户型<font color=red>*</font>',
																		emptyText : '请选择...',
																		//									editable:false,
																		name : 'huXing',
																		dict : 'HU_XING',
																		hidden:"buDan"==me.orderType?true:false
																	},
																	{
																		xtype : 'textareafield',
																		name : 'azAddress',
																		rows : 1,
																		flex : 3,
																		fieldLabel : '客户地址<font color=red>*</font>',
																		allowBlank : false
																	} ]
														} ]
											},{
												xtype : 'fieldset',
												hidden : true,
												id : 'ZBAddress_fs',
												title : '收货地址',
												style : {
													'border-style' : 'solid',
													'border-color' : '#d5d5d5',
													'background-color' : '#ffffff'
												},
												collapsible : true,
												defaultType : 'textfield',
												layout : 'anchor',
												defaults : {
													anchor : '100%'
												},
												items : [
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		fieldLabel : '送达方（一次）ID',
																		padding : '0 0 0 1',
																		name : 'socId',
																		hidden : true
																	},
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '地区<font color=red>*</font>',
																		padding : '0 0 0 1',
																		//									editable:false,
																		name : 'regio',
																		id : 'regio',
																		dict : 'KUNNR_REGION',
																		emptyText : '请选择...'
																	//									hidden:true
																	},
																	{
																		fieldLabel : '城市<font color=red>*</font>',
																		padding : '0 0 0 0',
																		id : 'mcod3',
																		name : 'mcod3'
																	//									hidden:true
																	},
																	{
																		fieldLabel : '邮政编码',
																		padding : '0 0 0 -3',
																		vtype : 'postcodecheck',
																		name : 'pstlz'
																	//									hidden:true
																	} ]
														},
														{
															xtype : 'textareafield',
															name : 'address',
															id : 'address',
															rows : 1,
															hidden : true,
															fieldLabel : '详细收货地址<font color=red>*</font>'
														} ]

											},
											{

												xtype : 'fieldset',
												title : '默认信息',
												style : {
													'border-style' : 'solid',
													'border-color' : '#d5d5d5',
													'background-color' : '#ffffff'
												},
												collapsible : true,
												defaultType : 'textfield',
												id : 'mr_fielDset',
												hidden : true,
												layout : 'anchor',
												defaults : {
													anchor : '100%'
												},
												items : [
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		fieldLabel : '订单编号',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		padding : '0 0 0 -1',
																		name : 'orderCode'
																	},
																	{
																		fieldLabel : 'SAP订单编号',
																		name : 'sapOrderCode',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true
																	},
																	{
																		xtype : 'datefield',
																		fieldLabel : '订单日期',
																		name : 'orderDate',
																		format : 'Y-m-d',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true
																	},
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '活动类型',
																		name : 'huoDongType',
																		dict : 'HUO_DONG_TYPE',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true
																	},
																	{
																		hidden : true,
																		name : 'createUser'
																	},
																	{
																		hidden : true,
																		name : 'createTime',
																		xtype : 'datefield',
																		format : 'Y-m-d H:i:s'
																	},
																	{
																		hidden : true,
																		name : 'sapCreateDate',
																		xtype : 'datefield',
																		format : 'Y-m-d H:i:s'
																	},
																	{
																		hidden : true,
																		name : 'vgbel'//借贷项编号
																	},
																	{
																		hidden : true,
																		name : 'updateUser'
																	},
																	{
																		hidden : true,
																		xtype : 'datefield',
																		format : 'Y-m-d H:i:s',
																		name : 'updateTime'
																	},
																	{
																		hidden : true,
																		name : 'checkDrawUser'
																	},
																	{
																		hidden : true,
																		name : 'checkPriceUser'
																	},
																	{
																		hidden : true,
																		name : 'confirmFinanceUser'
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		xtype : 'numberfield',
																		fieldLabel : '订单总额',
																		name : 'orderTotal',
																		minValue : 0,
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		value : 0
																	/*listeners: {
																			'change': function(obj,newValue,oldValue,eOpts) {
																				calculateFuFuanMoney(form,me.editFlag);
																	    }
																	}*/
																	},
																	{
																		xtype : 'numberfield',
																		fieldLabel : '付款金额',
																		align : 'right',
																		name : 'fuFuanMoney',
																		minValue : 0,
																		padding : '0 0 0 3',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		value : 0
																	},
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '支付方式',
																		name : 'payType',
																		dict : 'PAY_TYPE',
																		padding : '0 0 0 2',
																		value : 'C',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true
																	},
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '付款条件',
																		name : 'fuFuanCond',
																		dict : 'FU_FUAN_COND',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true
																	/*listeners: {
																		'change': function(obj,newValue,oldValue,eOpts) {
																			calculateFuFuanMoney(form,me.editFlag);
																	}
																	}*/
																	} ]
														},
														{

															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		hidden : true,
																		fieldLabel : '参考订单',
																		name : 'pOrderCode'

																	},
																	{
																		fieldLabel : '售达方(客户)',
																		name : 'shouDaFang',
																		id : 'shouDaFang',
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		readOnly : true,
																		padding : '0 0 0 -2'
																	},
																	{

																		xtype : 'fieldcontainer',
																		layout : 'hbox',
																		combineErrors : true,
																		defaultType : 'textfield',
																		items : [
																				{
																					fieldLabel : '送达方(物流园)',
																					readOnly : true,
																					fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																					name : 'songDaFang'
																				},
																				{
																					fieldLabel : '售达方名称',
																					fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																					readOnly : true,
																					value : KUNNR_NAME1,
																					name : 'kunnrName1'
																				},
																				{
																					xtype : 'numberfield',
																					name : 'age',
																					fieldLabel : '年龄',
																					minValue : 0, //prevents negative numbers
																					//				                    	maxValue: 150,
																					padding : '0 0 0 2',
																					readOnly : true,
																					//				                    	hideTrigger: true,
																					fieldStyle : 'background-color: #FCFCFC; background-image: none;'
																				} ]

																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '交期天数',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		name : 'jiaoQiTianShu',
																		dict : 'JIAO_QI_TIAN_SHU'
																	},
																	{
																		xtype : 'dictcombobox',
																		fieldLabel : '处理时效',
																		emptyText : '请选择...',
																		value : '1',
																		name : 'handleTime',
																		dict : 'HANDLE_TIME'
																	},
																	{
																		fieldLabel : '专卖店经手人',
																		padding : '0 0 0 1',
																		name : 'jingShouRen',
																		id : 'jingShouRen'
																	},
																	{
																		fieldLabel : '店面联系电话',
																		name : 'dianMianTel',
																		padding : '0 0 0 -2',
																		vtype : 'phonecheck'
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															combineErrors : true,
															defaultType : 'textfield',
															items : [
																	{
																		hidden : true,
																		fieldLabel : '订单状态',
																		name : 'orderStatus'
																	},
																	{
																		xtype : 'numberfield',
																		fieldLabel : '借贷金额',
																		align : 'right',
																		name : 'loanAmount',
																		//				                    width: 300,
																		minValue : 0,
																		readOnly : true,
																		value : 0
																	},
																	{
																		fieldLabel : '预计出货日期',
																		xtype : 'datefield',
																		format : 'Y-m-d',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		name : 'yuJiDate'
																	},
																	{
																		fieldLabel : '实际货日期',
																		xtype : 'datefield',
																		format : 'Y-m-d',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		name : 'shiJiDate'
																	},
																	{
																		fieldLabel : '实际货日期2',
																		format : 'Y-m-d',
																		xtype : 'datefield',
																		readOnly : true,
																		fieldStyle : 'background-color: #FCFCFC; background-image: none;',
																		name : 'shiJiDate2'
																	} ]
														} ]

											},{
												xtype : 'fieldset',
												title : '文件信息',
												style : {
													'border-style' : 'solid',
													'border-color' : '#d5d5d5',
													'background-color' : '#ffffff'
												},
												AutoScroll :true,
												collapsible : true,
												hidden:"buDan"==me.orderType?false:true,
												defaultType : 'textfield',
												layout : 'anchor',
												defaults : {
													anchor : '100%'
												},items : [{
													xtype:'tabpanel',
												    itemId:'centerTabpanel',
//												    bodyStyle : "border-left:0;border-right:0;border-bottom:0;",
												    border : false,
												    items:[
												    	{
												    		xtype:'grid',
															itemId:'BJ_fujiangridItem',
															title: '附件',
															border:false,
															height: 150,
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
														
												    	},{
												    		itemId:'feiyonghuagridItem',
												    		title: '费用化',
											            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;"	,
												    		border : false,
												    		autoScroll:true,
												    		bodyPadding: 5,
												    		height: 800,

															tbar : [
																{
																	hidden:false,
																	xtype : 'button',
																	text : '保存',
																	itemId : 'saveSJ',
																	iconCls : 'table_save',
																	handler : function() {
												     	        		   this.up('window').fireEvent('saveSanjian');
																	}
																}],
												    		items:[{
												    		   xtype:'form',
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
																	    xtype     : 'displayfield',
																	    name      : 'matnr',
																	    value     :'102999998',
																	    fieldLabel: '物料号通用码',
																	    maxLength :18
																	},
																	{
															        	xtype     : 'displayfield',
															        	name      : 'miaoshu',
															        	fieldLabel: '物料描述',
															        	value     :'费用化通用码',
															        	width: 600,
															        	maxLength :80
															        },{
															        	xtype : 'grid',
																		title : '明细信息',
																		height:500,
																		style : {
																			'border-style' : 'solid',
																			'border-color' : '#d5d5d5',
																			'background-color' : '#ffffff'
																		},
																		AutoScroll :true,
																		defaultType : 'textfield',
																		layout : 'anchor',
																		defaults : {
																			anchor : '100%'
																		},
																		viewConfig:{
																		    enableTextSelection:true
																		},
																		tbar : [
																				{xtype: 'button', text: '添加',iconCls:'table_add',itemId:'addfeiyonhua',listeners:{
																					click:function(){
																						var model = Ext.create("SMSWeb.model.sale.SaleExpenditureModel");
																						store.insert(store.getCount(),model);
																					}
																				}
															    	        	   
															    	            },
															        	        {xtype: 'button', text: '删除',iconCls:'table_remove',itemId:'deletefeiyonhua', 
														
															        	      	}
																				],
																				itemId: 'itemGrid_ItemId',
																				features:[{ftype: 'summary',dock:'bottom'}],
																				store : store,
																			    selModel:{selType:'checkboxmodel',injectCheckbox:0},
																			       columns : [
																			    	   	  {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
																			    	   	  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
																		                  {text:'产品描述',dataIndex:'miaoshu',width:200,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'尺寸',dataIndex:'size',width:80,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'数量',dataIndex:'number',width:80,menuDisabled:true
																		                	  ,editor:{
																		                		  allowDecimals:false,//不允许输入小数
																		                		  minValue:1,
																		                		  xtype:'numberfield',
																		                		  allowBlank:true
																		                	  }
																		                  },
																		                  {text:'单位',dataIndex:'unit',width:80,editor:{xtype:'textfield',selectOnFocus:true}
																		                  },
																		                  {text:'产线',dataIndex:'chanxian',width:80,
																								 editor:{
																									 xtype:"dictcombobox",
																									 dict:"YES_NO",
																									 allowBlank : false
																									 },
																		                  },
																		                  {text:'价格(元)',dataIndex:'price',width:80,editor:{xtype:'textfield',selectOnFocus:true}},
																		                  ],
																		                  plugins: [
																		        	          Ext.create('Ext.grid.plugin.CellEditing', {
																		        	              clicksToEdit: 2
																		        	          }) 	
																		        	]
															        }]
												    		}]
												    	},{
												    		itemId:'complainidItem',
												    		title: '客诉报表',
											            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;"	,
												    		border : false,
												    		autoScroll:true,
												    		bodyPadding: 5,
												    		height: 500,

															tbar : [
																{
																	hidden:false,
																	xtype : 'button',
																	text : '保存',
																	itemId : 'saveSJ',
																	iconCls : 'table_save',
																	handler : function() {
												     	        		   this.up('window').fireEvent('saveSanjian');
																	}
																}],
												    		items:[{
												    		   xtype:'form',
															   title: '',
												               itemId: 'complainitemid',
												               bodyStyle : "padding:5px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
													    	   border : false,
													    	   fieldDefaults: {
													    	        labelAlign: 'left',
													    	        width: 300,
														        	labelWidth: 110
													    	   },
													    	   items:[
																	{allowBlank: false,
													                    xtype:'datefield',
													                    fieldLabel: '投诉日期',
													                    name: 'complainday',
													                    format :'Y-m-d',
													                    width: 300
																	},
																	{
															        	xtype : 'grid',
																		title : '投诉内容',
																		height:600,
																		style : {
																			'border-style' : 'solid',
																			'border-color' : '#d5d5d5',
																			'background-color' : '#ffffff'
																		},
																		AutoScroll :true,
																		defaultType : 'textfield',
																		layout : 'anchor',
																		defaults : {
																			anchor : '100%'
																		},
																		viewConfig:{
																		    enableTextSelection:true
																		},
																		tbar : [
																				{xtype: 'button', text: '添加',iconCls:'table_add',itemId:'add',listeners:{
																					click:function(){
																						var model = Ext.create("SMSWeb.model.sale.SaleComplaintModel");
																						comstore.insert(comstore.getCount(),model);
																					
																					}
																				}

															    	            },
															        	        {xtype: 'button', text: '删除',iconCls:'table_remove',itemId:'delete', 
															        	      	}
																				],
																				itemId: 'itemGrid_ItemId',
																				features:[{ftype: 'summary',dock:'bottom'}],
																				store : comstore,
																			    selModel:{selType:'checkboxmodel',injectCheckbox:0},
																			       columns : [
																		                  {text:'id',dataIndex:'id',width:0,hidden:true},
																		                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
																		                  {text:'行号',dataIndex:'ordercode',width:150,sortable: false,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'投诉内容',dataIndex:'content',width:300,sortable: false,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}
																		                  },
																		                  {text:'责任中心',dataIndex:'centre',width:80,sortable: false,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'责任部门',dataIndex:'department',width:80,sortable: false,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
																		                
																		                  {text:'责任组别',dataIndex:'group',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'出错问题',dataIndex:'errproblem',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'出错类型',dataIndex:'errtype',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'负责人',dataIndex:'principal',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'产品名称',dataIndex:'productname',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  {text:'备注',dataIndex:'remark',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
																		                  
																		                  ],
																		                  plugins: [
																		        	          Ext.create('Ext.grid.plugin.CellEditing', {
																		        	              clicksToEdit: 2
																		        	          }) 	
																		        	]
															        }]
												    		}]
												    	}]
												}]
												
											} ]
								},
								{
									xtype : 'grid',
									region : 'center',
									id : 'addSaleItemsGrid',
									hidden:"buDan"==me.orderType?true:false,
									store : Ext
											.create("SMSWeb.store.sale.SaleStore"),
									style : {
										margin : '-8px 5px 0 5px'//,
									//border:'1px solid #e1e1e1',
									//'border-top':'0'
									},
									//border:false,
									viewConfig : {
										enableTextSelection : true
									//可以复制单元格文字
									},
									dockedItems : [ {
										xtype : 'toolbar',
										dock : 'left',
										autoScroll : true,
										//					    tile:'产品信息',
										style : {
											//		                	'border-style':'solid',
										//		                	'border-color':'#d5d5d5',
										'background-color' : '#F9F9FB'
									},
									items : [ {
										xtype : 'button',
										text : '标准产品',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemBZ',
										hidden : false,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '非标产品',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemFB',
										hidden : false,
										margin : '12 0 0 0'
									}, {
										xtype : 'button',
										text : '销售道具',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemXSDJ',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '五金散件',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemWJSJ',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '移门散件',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemYMSJ',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '柜身散件',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemGSSJ',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '客服补够',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemKFBG',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										text : '免费订单',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemMFDD',
										hidden : true,
										margin : '8 0 0 0'
									}, {
										xtype : 'button',
										id : 'removeOrderItems',
										text : '删除产品',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_remove',
										disabled : true,
										hidden : false,
										margin : '12 0 0 0'
									} ]
									} ],
									//				     tools : [{
									//						type : 'refresh',
									//						tooltip : 'Refresh form Data',
									//						handler : function(event, toolEl,panel) {
									//								panel.ownerCt.getStore().reload();
									//						}
									//					 }], 
									columns : [
											{
												text : '操作',
												xtype : 'actioncolumn',
												align : 'center',
												width : 38,
												menuDisabled : true,
												items : [ {
													icon : '/resources/images/remarks1.png',
													align : 'center',
													tooltip : '编辑',
													handler : function(grid,
															rowIndex, colIndex) {
														var orderType = Ext
																.getCmp(
																		"orderType")
																.getValue();
														var saleFor = Ext
														.getCmp(
																"saleFor")
														.getValue();
														var _shouDaFang = Ext
																.getCmp(
																		"shouDaFang")
																.getValue();
														this
																.up('window')
																.fireEvent(
																		'itemEditButtonClick',
																		orderType,
																		_shouDaFang,
																		grid,
																		rowIndex,
																		colIndex,saleFor);
													}
												} ]
											},
											{
												text : 'id',
												dataIndex : 'id',
												width : 0,
												hidden : true,
												menuDisabled : true
											},
											{
												text : '行号',
												dataIndex : 'posex',
												width : 100,
												menuDisabled : true
											},
											{
												text : '产品编号',
												dataIndex : 'matnr',
												width : 100,
												menuDisabled : true
											},
											{
												text : '类型',
												dataIndex : 'mtart',
												width : 100,
												menuDisabled : true,
												renderer : function(value,
														metadata, record) {
													var materialMtartStore = materialMtartCombobox
															.getStore();
													var find = materialMtartStore
															.findRecord('id',
																	value);
													if (find) {
														return find.get('text');
													}
													return value;
												}
											},
											{
												text : '绘图类型',
												dataIndex : 'drawType',
												width : 100,
												menuDisabled : true,
												renderer : function(value,
														metadata, record) {
													var drawTypeStore = drawTypeCombobox
															.getStore();
													var find = drawTypeStore
															.findRecord('id',
																	value);
													if (find) {
														return find.get('text');
													}
													return value;
												}
											},
											{
												text : '描述',
												dataIndex : 'maktx',
												width : 300,
												menuDisabled : true
											},
											//								 {text:'单价',dataIndex:'unitPrice',width:100, xtype: 'numbercolumn',editor: {
											//						                xtype: 'numberfield',
											//						                allowBlank: false,
											//						                minValue: 0,
											//						                maxValue: 150000
											//						         }},
											//					             {text:'折扣',dataIndex:'zheKou',width:100, xtype: 'numbercolumn',editor: {
											//						                xtype: 'numberfield',
											//						                allowBlank: false,
											//						                minValue: 0,
											//						                maxValue: 150000
											//						         }},
											//						         {text:'折扣价',dataIndex:'zheKouJia',width:100, xtype: 'numbercolumn',editor: {
											//						                xtype: 'numberfield',
											//						                allowBlank: false,
											//						                minValue: 0,
											//						                maxValue: 150000
											//						         }},
											{
												text : '<font color="#2522C9">数量</font>',
												dataIndex : 'amount',
												width : 100,
												menuDisabled : true,
												xtype : 'numbercolumn',
												align : 'center',
												id : 'amountNF',
												editor : {
													xtype : 'numberfield',
													allowBlank : false,
													allowDecimals : false,//不允许输入小数
													//allowNegative:false,//不允许输入负数
													minValue : 1
												//,maxValue: 150000
												},
												renderer : function(value,
														metadata, record) {
													if(record.data.matnr=="130000189"||record.data.matnr=="130000174"){
														if(parseInt(value)<2){
															Ext.MessageBox.alert("提示",record.data.matnr+"：此产品为成双购买品");
															record.data.amount=2;
															return parseInt(2);
														}else if(parseInt(value)>2){
															if(parseInt(value)%2==0){
																record.data.amount=value;
																return parseInt(value);
															}else{
																Ext.MessageBox.alert("提示",record.data.matnr+"：此产品为成双购买品");
																record.data.amount=value+1;
																return parseInt(value+1);
															}
														}
													}
													record.data.amount=value;
													return parseInt(value);
												}
											},
											{
												text : '<font color="#2522C9">安装位置</font>',
												dataIndex : 'remark',
												width : 200,
												align : 'center',
												editor : {
													xtype : 'textareafield'
												}
											},
											//						         {text:'投影面积',dataIndex:'touYingArea',width:100,menuDisabled:true,align:'right'},
											{
												text : '总价',
												dataIndex : 'totalPrice',
												width : 100,
												xtype : 'numbercolumn',
												menuDisabled : true,
												hidden : true
											//						         	,editor: {
											//						                xtype: 'numberfield',
											//						                allowBlank: false,
											//						                minValue: 0,
											//						                maxValue: 15000000
											//						         }
											},
											//					             {text:'订单状态',dataIndex:'status',width:100,menuDisabled:true},
											{
												text : '是否标准产品',
												dataIndex : 'isStandard',
												width : 100,
												menuDisabled : true,
												align : 'center',
												renderer : function(value,
														metadata, record) {
													var isStandardStore = isStandardCombobox
															.getStore();
													var find = isStandardStore
															.findRecord('id',
																	value);
													if (find) {
														return find.get('text');
													}
													return value;
												}
											},
											{
												text : '是否外购',
												dataIndex : 'zzwgfg',
												width : 100,
												menuDisabled : true,
												align : 'center',
												renderer : function(value,
														metadata, record) {
													var isStandardStore = zzwgfgCombobox
															.getStore();
													var find = isStandardStore
															.findRecord('id',
																	value);
													if (find) {
														return find.get('text');
													}
													return value;
												}
											},
											{
												text : '物料id',
												dataIndex : 'materialHeadId',
												width : 0,
												hidden : true,
												menuDisabled : true
											},
											{
												text : '我的物品id',
												dataIndex : 'myGoodsId',
												width : 0,
												hidden : true,
												menuDisabled : true
											},
											{
												text : '审核状态',
												dataIndex : 'stateAudit',
												width : 100,
												menuDisabled : true,
												align : 'center',
												renderer : function(value,
														metadata, record) {
													var stateAuditStore = stateAuditCombobox
															.getStore();
													var find = stateAuditStore
															.findRecord('id',
																	value);
													if (find) {
														return find.get('text');
													}
													return value;
												}
											},
											{
												text : '当前环节',
												dataIndex : 'jdName',
												width : 100,
												menuDisabled : true
											},
											{
												text : 'PDF文件',
												dataIndex : 'pdfId',
												align : 'center',
												width : 70,
												renderer : function(value,
														metadata, record) {
													if (value) {
														var url = basePath
																+ "main/sysFile/fileDownload?id="
																+ value;
														var str = '<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\'' + value + '\')">下载</a>';
														return str;
													}
													return value;
												}
											},
											{
												text : 'KIT文件',
												dataIndex : 'kitId',
												align : 'center',
												width : 70,
												renderer : function(value,
														metadata, record) {
													if (value) {
														var url = basePath
																+ "main/sysFile/fileDownload?id="
																+ value;
														var str = '<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\'' + value + '\')">下载</a>';
														return str;
													}
													return value;
												}
											},
											{
												text : '报价清单',
												dataIndex : 'priceId',
												align : 'center',
												width : 70,
												renderer : function(value,
														metadata, record) {
													if (value
															&& IS_MONEY == "true") {
														var url = basePath
																+ "main/sysFile/fileDownload?id="
																+ value;
														var str = '<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\'' + value + '\')">下载</a>';
														return str;
													}
													return "";
												}
											} ],
									selType : 'cellmodel',
									plugins : [ cellEditing = Ext.create(
											'Ext.grid.plugin.CellEditing', {
												enableKeyNav : true,
												clicksToEdit : 2
											}) ],
									listeners : {
										'select' : function(view,
												records) {
										//有ID的行号不允许删除
											var form=Ext.getCmp("addSaleForm").getForm();
											var saleOrder=form.getValues().orderCode;
											if(saleOrder.length==0){
											Ext.getCmp("removeOrderItems")
													.setDisabled(
															false);
											}
										},
										'edit' : function(editor, e) {
											if (e.field == 'amount') {
												/*var sdf=shouDaFang.getValue();
												alert(sdf);
												Ext.Ajax.request({
													url : 'main/sale/calculationPrice?shouDaFang='+sdf,
													method : 'POST',
													params : e.record.getData(),
													async:false,
													success : function(response, opts) {
														values = Ext.decode(response.responseText);
														e.store.getAt(e.rowIdx).set('totalPrice',values.data.totalPrice);
														var itemStore = itemGrid.getStore();
														var itemCount = itemStore.getCount();
														var allTotalPrice = 0;
														for (var i = 0; i <itemCount; i++) {
															var record = itemStore.getAt(i);
															if("QX" != record.get("stateAudit")){
																allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
															}
														}
														form.getForm().findField("orderTotal").setValue(allTotalPrice);
														form.getForm().findField("orderTotal").initValue();
													},
													failure : function(response, opts) {
														alert(response.responseText);
													}
												});*/
											}
										}
									}
								} ];
						me.buttons = [ {
							text : '保&nbsp;&nbsp;存',
							width : 80,
							height : 30,
							margin : '0 0 0 40',
							action : 'saveAddSaleForm'
						//			            handler: function() {
								//			            	alert(1);
								//			                var form = this.up('form').getForm();
								//			                if (form.isValid()) {
								//			                    Ext.MessageBox.alert('Submitted Values', form.getValues(true));
								//			                }
								//			            }
								},/*{
																																			            text: '重&nbsp;&nbsp;置',
																																			            width: 80,
																																			            margin:'0 0 0 40',
																																			            height:30,
																																			            action:'resetAddSaleForm'
																																//			            handler: function() {
																																//			            	alert(2);
																																//			                this.up('form').getForm().reset();
																																//			            }
																																			        },*/{
									text : '提&nbsp;&nbsp;交',
									width : 80,
									margin : '0 0 0 40',
									height : 30,
									action : 'saleTaskSubmit'
								//			            handler: function() {
								//			            	alert(2);
								//			                this.up('form').getForm().reset();
								//			            }
								}, {
									text : '流程记录',
									width : 80,
									margin : '0 40 0 40',
									height : 30,
									action : 'TaskHistoric'
								//			            handler: function() {
								//			            	alert(2);
								//			                this.up('form').getForm().reset();
								//			            }
								} ]
						/**
						 * 清除form数据
						 */
						function cleanFormStore() {
							var form = Ext.getCmp("addSaleForm").getForm();
							var saleItmes = Ext.getCmp("addSaleItemsGrid");
							var saleId = form.getValues().shId;
							var itemStore = saleItmes.getStore();
							var records = itemStore.getRange();

							var ids = [];
							Ext.Array.each(records, function(r) {
								var id = r.get('id');
								if (id) {
									ids.push(id);
								}
							});
							if (ids.length > 0) {
								Ext.Ajax.request( {
									url : 'main/sale/deleteSaleItemById',
									params : {
										ids : ids,
										saleId : saleId
									},
									method : 'POST',
									success : function(response, opts) {
										itemStore.load( {
											params : {
												'pid' : saleId
											}
										});
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示信息", "删除失败！");
									}
								});
							} else {
								if (records.length > 0) {
									itemStore.removeAll();
								}

							}

						}
						function changeOrderType(me){
							var saleForm = me.queryById("saleForm");
							var itemGrid = me.queryById("saleGrid");
						    var itemStore = itemGrid.getStore();
						    
						    var orderType = saleForm.getForm().findField("orderType").getValue();
						    if("OR4" != orderType){
						    	saleForm.queryById("orderCodeQueryButton").hide();
						    }
						    
						    var ids = [];
						    var itemCount = itemStore.getCount();
							for (var i = 0; i <itemCount; i++) {
								var record = itemStore.getAt(i);
								if("undefined"!=typeof(record.get('id'))){
									ids.push(record.get('id'));
								}
							}
							if(ids.length==0){
								var saleId = saleForm.getForm().findField("id").getValue();
								itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
											        calculationTotalPrice(saleForm,itemGrid,'delete');
											    }});
								return;
							}
							Ext.Ajax.request({
								url : 'main/sale/deleteSaleItemByIds',
								params : {
									ids : ids,
									custCode : saleForm.getForm().findField("shouDaFang").getValue()
								},
								method : 'POST',
								success : function(response, opts) {
									//itemStore.remove(sm.getSelection());
									var saleId = saleForm.getForm().findField("id").getValue();
									itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
												        calculationTotalPrice(saleForm,itemGrid,'delete');
												    }});
//									Ext.MessageBox.alert("提示信息","删除成功！");
								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("提示信息","网络异常！");
								}
							});
						}
						function changeOrderTypeByaddSaleV(oldValue, nval,
								saleItmes) {
							var form = Ext.getCmp("addSaleForm").getForm();
							var saleId = form.getValues().shId;
							var itemStore = saleItmes.getStore();
							var records = itemStore.getRange();
							if ("undefined" == typeof (oldValue)
									|| oldValue == null || oldValue == "") {
								if (records.length > 0) {
									itemStore.removeAll();
								}
							} else {
								cleanFormStore();
							}

							var bz = Ext.getCmp("addOrderItemBZ");
							bz.hide();
							var fb = Ext.getCmp("addOrderItemFB");
							fb.hide();
							var xsdj = Ext.getCmp("addOrderItemXSDJ");
							xsdj.hide();
							var wjsj = Ext.getCmp("addOrderItemWJSJ");
							wjsj.hide();
							var ymsj = Ext.getCmp("addOrderItemYMSJ");
							ymsj.hide();
							var gssj = Ext.getCmp("addOrderItemGSSJ");
							gssj.hide();
							var kfbg = Ext.getCmp("addOrderItemKFBG");
							kfbg.hide();
							var mfdd = Ext.getCmp("addOrderItemMFDD");
							mfdd.hide();
							var _saleFor = Ext.getCmp("saleFor");
							_saleFor.hide();
							Ext.getCmp("zzaufnrField").hide();
							Ext.getCmp("zzaufnrField").setValue("");
							Ext.getCmp("zzaufnrCheck").setValue("");
							Ext.getCmp("checkFlag").setValue("true");
							var amountEditor = Ext.getCmp("amountNF")
									.getEditor();
							if ("OR1" == nval || "OR7" == nval
									|| "OR8" == nval || "OR9" == nval) {
								Ext.getCmp("addOrderItemBZ").show();
								Ext.getCmp("addOrderItemFB").show();
								//有厨柜下单权限的经销商才能显示出来
								if ("X" == CURR_USER_KUNNR_CUP||"X"==CURR_USER_KUNNR_TIMBER) {
									_saleFor.show();
								}
								amountEditor.enable();
								if ("OR9" == nval) {
									Ext.getCmp("zzaufnrField").show();
									Ext.getCmp("checkFlag").setValue("false");
									Ext.getCmp("zzaufnrCheck").show();
								}
							} else if ("OR2" == nval) {
								ymsj.show();
								gssj.show();
								amountEditor.disable();
							} else if ("OR3" == nval) {
								wjsj.show();
								amountEditor.disable();
							} else if ("OR4" == nval) {
								xsdj.show();
								amountEditor.disable();
							} else if ("OR3" == nval) {
								kfbg.show();
								amountEditor.enable();
							} else if ("OR4" == nval) {
								mfdd.show();
								amountEditor.enable();
							}

						}

						me.listeners = {
							show : function() {
								loadData();
								showAddress();
							}
						};
						//收货地址
						function showAddress() {
							if (KUNNRS.indexOf("ZB") != -1) {
								var addr = Ext.getCmp("ZBAddress_fs");
								addr.show();
								//		        		Ext.getCmp("address").allowBlank=false;
								Ext.getCmp("mcod3").allowBlank = false;
								Ext.getCmp("regio").allowBlank = false;
							}
						}
						//加载数据
						function loadData() {
							var id = me.shId;
							if (!id) {
								return;
							}
							Ext.getCmp("mr_fielDset").show();
							Ext.Ajax.request( {
								url : 'main/sale/querySaleById?id=' + id,
								async : true,
								dataType : "json",
								success : function(response, opts) {
									var values = Ext
											.decode(response.responseText);
									if (values.success) {
										var formData = values.data;
										var form = Ext.getCmp("addSaleForm")
												.getForm();
										form.setValues(formData);
										if (formData.orderCode) {
											Ext.getCmp("orderType")
													.setReadOnly(true);
										}
										var saleItmes = Ext
												.getCmp("addSaleItemsGrid");
										saleItmes.getStore().load( {
											params : {
												'pid' : formData.shId
											}
										/*,callback: function(records, operation, success) {
											alert();
										        calculationTotalPrice(saleForm,itemGrid,'delete');
										    }*/
										});

									} else {
										Ext.MessageBox.alert("提示信息",
												values.errorMsg);
									}

								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("提示信息", "加载失败"
											+ response.responseText);

								}
							});

						}

						me.callParent(arguments);
					}
				});