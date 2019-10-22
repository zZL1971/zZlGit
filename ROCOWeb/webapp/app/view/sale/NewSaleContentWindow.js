var store=Ext.create("SMSWeb.store.sale.SaleExpenditureStore");
var mtartCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'MATERIAL_MTART'});
var ortypetCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'OR_TYPE'});
Ext.define(
				'SMSWeb.view.sale.NewSaleContentWindow',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.newSaleContentWindow',
					layout : 'border',
					itemId:'newSaleContentWindow',
					minWidth : 1100,
					minHeight : 550,
					otype:null,//界面引导
					//		    maxWidth:2500,
					//		    maxHeight:1300,
					modal : true,
					shId : null,
					jdName:null,
					formId:null,
					shouDaFang:null,
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
						var falg = false;
						if(me.shId==null){
							me.shId=me.formId;
						}
						var _formId = me.formId;
						 if(_formId != null ||_formId !=undefined){
							 var obj = Ext.ux.DataFactory.getFlowActivityId(me.formId);
							 //usertask_drawing订单审绘
							 //_judge=true显示数据字典“中文”,false时显示“繁体”
							 /*if(obj.taskdefId == 'usertask_drawing'){
								 _judge = true;
							 }*/
							 if(obj.taskGroup=="gp_store"){
								 falg=true;
							 }
						 }
						var unit = Ext.create('Ext.ux.form.DictCombobox',{dict:'UNIT'});
						var saleFor = Ext.create('Ext.ux.form.DictCombobox',{dict:'SALE_FOR'});
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
						var cclbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
							 emptyText: '请选择...',
							 name:'zzelb',
							 dict:'CCLB'
						 });
						var cczbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
							 emptyText: '请选择...',
							 name:'cczb',
							 dict:'CCZB'
						 });
						var ccbmCombobox = Ext.create("Ext.ux.form.DictCombobox",{
							 emptyText: '请选择...',
							 name:'zzebm',
							 dict:'CCBM'
						 });
						var cczxCombobox = Ext.create("Ext.ux.form.DictCombobox",{
							 emptyText: '请选择...',
							 name:'zzezx',
							 dict:'CCZX'
						 });
						var ccwtCombobox = Ext.create("Ext.ux.form.DictCombobox",{
							emptyText: '请选择...',
//							name:'zzezx',
							dict:'CCWT'
						});

						/*				var states = Ext.create('Ext.data.Store', {
						 fields: ['abbr', 'name'],
						 data : [
						 {"abbr":"AL", "name":"Alabama"},
						 {"abbr":"AK", "name":"Alaska"},
						 {"abbr":"AZ", "name":"Arizona"}
						 //...
						 ]
						 });
						
						
						 var sjs=Ext.create('Ext.form.ComboBox', {
						 fieldLabel: '设计师',
						 store: states,
						 queryMode: 'local',
						 name:'designerTel',
						 displayField: 'name',
						 valueField: 'abbr',
						
						 });*/
						var custInfo = Ext.widget('fieldset',{
							xtype : 'fieldset',
							title : '客户信息',
							style : {
								'border-style' : 'solid',
								'border-color' : '#d5d5d5',
								'background-color' : '#ffffff'
							},
							collapsible : true,
							collapsed:"buDan"==me.orderType?true:false,
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
													allowBlank : "buDan"==me.orderType?true:false
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
													allowBlank : "buDan"==me.orderType?true:false
												},
												{
													allowBlank : "buDan"==me.orderType?true:false,
													xtype : 'dictcombobox',
													//						        	editable:false,
													fieldLabel : '性别<font color=red>*</font>',
													emptyText : '请选择...',
													name : 'sex',
													dict : 'SEX'
												},
												{
													allowBlank : false,
													xtype : 'datefield',
													id:'birthday',
													fieldLabel : '生日<font color=red>*</font>',
													name : 'birthday',
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
												} ]
									},
									{
										xtype : 'fieldcontainer',
										layout : 'hbox',
										combineErrors : true,
										defaultType : 'textfield',
										items : [
												{
													allowBlank : false,
													xtype : 'dictcombobox',
													id:'huXing',
													padding : '0 0 0 1',
													fieldLabel : '安装户型<font color=red>*</font>',
													emptyText : '请选择...',
													//									editable:false,
													name : 'huXing',
													dict : 'HU_XING'
												},
												{
													xtype : 'textareafield',
													name : 'azAddress',
													rows : 1,
													flex : 3,
													fieldLabel : '客户地址<font color=red>*</font>',
													allowBlank : true
												}]
									} ]
						});
						var shouhuoInfo = Ext.widget('fieldset',{
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

						});
						var baseInfo = Ext.widget('fieldset',{
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
													url : 'main/sale/getOrderByCust/' + me.shId+'/'+me.orderType,
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
															}
														}
													}
												},
												{
										    		xtype: 'textfield',
											        fieldLabel: '参考订单<font color=red>*</font>',
											        name: 'pOrderCode',
											        id: 'pbgOrderCode',
											        width: 300,
											        hidden:"buDan"==me.orderType?false:true
										    	},{
													xtype : 'button',
													text : '...',
													itemId : 'pOrderCodeButton',
													hidden:"buDan"==me.orderType?false:true
												 },
/*												 {
													xtype : 'textfield',
													fieldLabel : '设计师电话<font color=red>*</font>',
													emptyText : '请选择...',
													allowBlank : false,
													editable : false,
													name : 'designerTel',
												 },*/
												{
													xtype : 'tablecombobox',
													fieldLabel : '设计师<font color=red>*</font>',
//													fieldLabel : '设计师',
													emptyText : '请选择...',
													allowBlank : "buDan"==me.orderType?true:false,
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

												},
												/*{
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

												},*/
											/*	{
													xtype : 'dictcombobox',
													fieldLabel : '是否样品<font color=red>*</font>',
													hidden : CURR_USER_LOGIN_NO
															.indexOf("_01") > -1 ? false
															: true, 只有_02可以不填,allowBlank : CURR_USER_LOGIN_NO
															.indexOf("_01") > -1 ? false
															: true,只有_02可以不填
															editable:false,	emptyText : '请选择...',
													name : 'isYp',
													dict : 'YES_NO',
													disabled : CURR_USER_LOGIN_NO
															.indexOf("_01") > -1 ? false
															: true
												只有_01有权编辑样品选项,其它帐户不能编辑			},*/
												{
													allowBlank : false,
													id:'orderPayFw',
													xtype : 'dictcombobox',
													fieldLabel : '订单金额范围<font color=red>*</font>',
													emptyText : '请选择...',
													//									editable:false,
													name : 'orderPayFw',
													dict : 'ORDER_PAY_FW'
												}/*,{
													allowBlank : false,													xtype : 'dictcombobox',
													id:'orderEvent',
													hidden:true,
													fieldLabel : '活动政策<font color=red>*</font>',
													emptyText : '请选择...',
													name : 'orderEvent',
													dict : 'ORDER_EVENT'
												}*/]
									},{
										xtype : 'fieldcontainer',
										layout : 'hbox',
										combineErrors : true,
										defaultType : 'textfield',
										items:[{
												allowBlank : true,
												xtype : 'datefield',
												fieldLabel : '安装日期',
												name : 'anzhuanDay',
												format : 'Y-m-d',
												hidden:"buDan"==me.orderType?false:true
												},	
												 {	
													allowBlank : true,
								                    xtype:'textfield',
								                    allowNegative : false,
								                    regex:/^[0-9]+$/,
								                    fieldLabel: '投诉次数<font color=red>*</font>',
//													allowBlank: false,
								                    name: 'tousucishu',
								                    width: 300,
								                    id:'tousucishu',
								                    hidden:true
								              },
						
											{	
								            	allowBlank : "buDan"==me.orderType?false:true,
									        	xtype:'dictcombobox',
												fieldLabel : '问题出现<font color=red>*</font>',
												emptyText: '请选择...',
												//disabled:true,
												width: 300,
												name:'problem',
												id:'bgproblem',
												dict:'PROBLEMS',
												hidden:"buDan"==me.orderType?false:true
											 },
											 {
												allowBlank : "buDan"==me.orderType?false:true,
											 	xtype: 'displayfield',
										        fieldLabel: '售达方（客户）<font color=red>*</font>',
										        name: 'bgShouDaFang',
										        id:'bgShouDaFang',
										        labelWidth : 120,
										        width: 300,
										        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
										        value: '',
										        hidden:"buDan"==me.orderType?false:true,
										        renderer : function(v) { 
											    	return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
											    }
										    },{
													xtype : 'button',
													hidden:"buDan"==me.orderType?false:true,
													text : '...',
													itemId : 'shouDaFang_queryCust',//iconCls:'table_save'
//													hidden : true
												}
											 ]
									},
									{
										xtype : 'fieldcontainer',
										layout : 'hbox',
										combineErrors : true,
										defaultType : 'textfield',
										items : [ {
											xtype : 'dictcombobox',
											fieldLabel : '是否样品<font color=red>*</font>',
											hidden : CURR_USER_LOGIN_NO
											.indexOf("_01") > -1 ||CURR_USER_LOGIN_NO
											.indexOf("kf")>-1? false
											: true,// 只有_02可以不填,
											allowBlank : CURR_USER_LOGIN_NO
													.indexOf("_01") > -1 ||CURR_USER_LOGIN_NO
													.indexOf("kf")>-1? false
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
//											allowBlank : CURR_USER_LOGIN_NO
//											.indexOf("_01") > -1 ? false
//											: true,// 只有_02可以不填
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
//											allowBlank : CURR_USER_LOGIN_NO
//											.indexOf("_01") > -1 ? false
//											: true,// 只有_02可以不填
											emptyText : '请选择...',
											name : 'shopCls',
											//dict : 'SHOP_CLS',
											id:'shopCls'
										}
										]
									},
									{
										xtype : 'fieldcontainer',
										layout : 'hbox',
										combineErrors : true,
										defaultType : 'textfield',
										items:[
											{
												allowBlank : true,
							                    xtype:'dictcombobox',
							                    allowNegative : false,
//							                    regex:/^[0-9]+$/,
							                    fieldLabel: '处理时效',
	//											allowBlank: false,
							                    name: 'urgentType',
							                    width: 250,
							                    id:'urgentType',
							                    dict:'HANDLE_TIME_B',
							                    hidden:true
						              
										},{
											allowBlank : true,
						                    xtype:'dictcombobox',
						                    allowNegative : false,
						                    fieldLabel: '需打木架',
						                    name: 'isMj',
						                    id: 'isMj',
						                    width: 250,
						                    dict : 'YES_NO',
						                    hidden:true
										},{
											allowBlank : true,
						                    xtype:'dictcombobox',
						                    allowNegative : false,
						                    fieldLabel: '通知质检检查',
						                    name: 'isQc',
						                    id: 'isQc',
						                    width: 250,
						                    dict : 'YES_NO',
						                    hidden:true
										},{
											allowBlank : true,
						                    xtype:'dictcombobox',
						                    allowNegative : false,
						                    fieldLabel: '通知客服检查',
						                    name: 'isKf',
						                    id: 'isKf',
						                    width: 250,
						                    dict : 'YES_NO',
						                    hidden:true
										
										}]
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
						});
						var defaultInfo = Ext.widget('fieldset',{

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
							collapsed:true,
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
													fieldLabel : '补购订单编号',
													readOnly : true,
													fieldStyle : 'background-color: #FCFCFC; background-image: none;',
													padding : '0 0 0 -1',
													name : 'serialNumber'
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
												/*{
													hidden : true,
													name : 'sapCreateDate',
													xtype : 'datefield',
													format : 'Y-m-d H:i:s'
												},*/
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
													name : 'pOrderCodes'

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
															},{
																xtype : 'button',
																text : '...',
																itemId : 'songDaFang_queryCust',
//																hidden:"buDan"==me.orderType?false:true
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
													dict : 'HANDLE_TIME',
													hidden:true
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

						});
						
						var fujianInfo = Ext.widget('tabpanel',{
							xtype:'tabpanel',
						    itemId:'centerTabpanel',
						    hidden:"buDan"==me.orderType?false:true,
//						    hidden:true,
//						    bodyStyle : "border-left:0;border-right:0;border-bottom:0;",
						    border : false,
						    items:[{
						    	xtype:'grid',
						    	itemId:'BJ_fujiangridItem',
								title: '附件',
								border:false,
								height: 150,
								viewConfig:{
								    enableTextSelection:true
								},
							    tbar: [
								        { xtype: 'button', text: '附件上传',iconCls:'table_add',id:'uploadFile',hidden:true,
/*											listeners:{
												click:function(){
													var form=Ext.getCmp("addSaleForm").getForm();
													Ext.create('SMSWeb.view.sale.NewBGFileUploadBaseWindow',{fileType: 'BJ',formId:form.getValues().shId,title:"附件文件上传"}).show();
												}
											}*/
								        	handler : function() {
								        			var form=Ext.getCmp("addSaleForm").getForm();
								        			var shId =  form.getValues().shId;
									    		   this.up('window').fireEvent('bgfileUploadButtonClick',shId);
												   }   
								        }
								       ,{ xtype: 'button', text: '附件无效',iconCls:'table_remove', id:'deleteFile', hidden:true,
								    	   handler : function() {
								    		    var form=Ext.getCmp("addSaleForm").getForm();
							        			var shId =  form.getValues().shId;
								    		   this.up('window').fireEvent('bgfileDeleteButtonClick',shId);
											   }   
								    	}
								       ],
									   store : Ext.create("SMSWeb.store.sale.Store4BGMaterialBaseFile"),
								       selModel:{selType:'checkboxmodel',injectCheckbox:0},
								       columns : [
										          {text:'id',dataIndex:'id',width:0,hidden:true},
								                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer'},
								                  {text:'文件名称',dataIndex:'uploadFileNameOld',width:200,sortable: false,menuDisabled:true},
								                  {text:'备注',dataIndex:'remark',width:200,sortable: false,menuDisabled:true},
								                  {text:'上传人',dataIndex:'createUser',width:80,sortable: false,menuDisabled:true},
								                  {text:'上传日期',dataIndex:'createTime',width:140,sortable: false,menuDisabled:true},
								                  {text:'是否有效',dataIndex:'statusdesc',width:80,sortable: false,menuDisabled:true},
								                  {text:'文件下载',width:100,xtype:'actioncolumn',align:'center',icon:'/resources/images/down.png',
								                	  handler : function(grid,rowIndex,colIndex) {
															this.up('window').fireEvent('fileDownloadButtonClick',this.up('grid'),rowIndex,colIndex);
														},sortable: false,menuDisabled:true
								                  	}
								                  ]
						    },{
						    	xtype:'grid',
					    		title: '费用化',
//					    		hidden:(undefined==obj||falg)?true:!obj.assignee,
					    		hidden:true,
					    		id : 'addBgGrid',
				            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;",
					    		border : false,
					    		autoScroll:true,
					    		bodyPadding: 5,
					    		height: 300,
								tbar : [
//							        { xtype: 'button', text: '保存',iconCls:'table_save',itemId:'saveBg',
//										listeners:{
//											click:function(){
////												saveFeiYongHua();
//											}
//										}
//								   },
							       { xtype: 'button', text: '填写费用化包',iconCls:'table_add', itemId:'addFYH',
										listeners:{
											click:function(){
												Ext.create('SMSWeb.view.sale.MaterialBGWindow', 
														{loadStatus:'2',matnr : '102999998',title:'费用化',sourceShow:"newSaleContentWindow",
														shId:me.formId,source:'add',shouDaFang:me.shouDaFang}).show();	
												}
										}
							    	}
									],
								   store : Ext.create("SMSWeb.store.sale.Store4MyBJ"),
							       selModel:{selType:'checkboxmodel',injectCheckbox:0},
							       columns : [

							             {text:'id',dataIndex:'id',width:0,hidden:true},
								         {text : '编辑/查看',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:80
							            	    ,
//							            	    handler : function(grid, rowIndex, colIndex) {
//													this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
//												}
							            		listeners:{
													click:function(grid, rowIndex, colIndex){
														var record = grid.getStore().getAt(0);
														var id = record.data.sanjianHeadId;
														Ext.create('SMSWeb.view.sale.MaterialBGWindow', 
																{loadStatus:'2',matnr : '102999998',title:'费用化',sourceShow:"newSaleContentWindow",shId:id,formId:me.formId,source:'update'}).show();																}
												}
							             },
							             {text:'订单类型',dataIndex:'ortype',width:150,
							 	            	editor:ortypetCombo,
							 	             	renderer : function(value, meta, record) {
								            		var find= ortypetCombo.getStore().findRecord("id",value);
								            		if(find){
								            			return find.get('text');
								            		}else{
								            			return value;
								            		}
								            	}
							 	         },
							             {text:'编号',dataIndex:'serialNumber',width:100},
							             {text:'产品编号',dataIndex:'matnr',width:150},
							             {text:'产品类型',dataIndex:'mtart',width:100,
							 	            	editor:mtartCombo,
							 	             	renderer : function(value, meta, record) {
								            		var find= mtartCombo.getStore().findRecord("id",value);
								            		if(find){
								            			return find.get('text');
								            		}else{
								            			return value;
								            		}
								            	}
							 	         },
						 	             {text:'产品描述',dataIndex:'maktx',width:250},
						 	             {text:'描述',dataIndex:'textdesc',width:100},
							             {text:'是否标准状态',dataIndex:'isStandard',width:0,hidden:true},
							             {text:'创建日期',dataIndex:'createTime',width:150},
							             {text:'materialHeadId',dataIndex:'materialHeadId',width:100,hidden:true},
							             {text:'materialPropertyItemInfo',dataIndex:'materialPropertyItemInfo',width:100,hidden:true},
							             {text:'sanjianHeadId',dataIndex:'sanjianHeadId',width:100,hidden:true}
							           
							       ]
							
						    },{
						    	xtype:'grid',
					    		title: '客诉报表',
					    		hidden:(undefined==obj||falg)?true:!obj.assignee,
					    		id : 'complainidItem',
				            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;",
					    		border : false,
					    		autoScroll:true,
					    		bodyPadding: 5,
					    		height: 300,
								tbar : [
//							        { xtype: 'button', text: '保存',iconCls:'table_save',itemId:'saveBg',
//										listeners:{
//											click:function(){
////												saveFeiYongHua();
//											}
//										}
//								   },
							       { xtype: 'button', text: '新增(修改)客诉报表',iconCls:'table_add', itemId:'addComplainid',
										listeners:{
											click:function(){
												Ext.create('SMSWeb.view.sale.NewSaleContentComplainidWindow', 
														{loadStatus:'2',title:'客诉报表',sourceShow:"newSaleContentWindow",shId:me.formId,source:'update'}).show();																}
										}
							    	}
									],
									itemId: 'complainid_ItemId',
									features:[{ftype: 'summary',dock:'bottom'}],
									store : Ext.create("SMSWeb.store.sale.SaleComplaintStore"),
								    selModel:{selType:'checkboxmodel',injectCheckbox:0},
							       columns : [
							    	   	  {text:'id',dataIndex:'id',width:0,hidden:true},
						                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
						                  {text:'行号',dataIndex:'orderCodePosex',width:150,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
						                  {text:'投诉内容',dataIndex:'zztsnr',width:300,sortable: false,menuDisabled:true,editor:{xtype:'textareafield',selectOnFocus:true}},
						                  {text:'产品名称',dataIndex:'cpmc',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
						                  /*{text:'颜色',dataIndex:'color',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
						                  {text:'柜名',dataIndex:'cabinetName',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
						                  {text:'产品组',dataIndex:'salefor',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},*/
						                  {text:'出错问题',dataIndex:'zzelb',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
					                	  editor:{
												 xtype:"dictcombobox",
												 dict:"CCWT",
												 allowBlank : true
												 },	
						                	  renderer: function(value,metadata,record){
									 				var Store = ccwtCombobox.getStore();
									 				var find = Store.findRecord('id',value,0,false,true,true); 
									 				if(find){
							 	            			return find.get('text');
							 	            		}
									                return value;  
									           }
						                  },
						                  {text:'出错中心',dataIndex:'zzezx',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
						                	  editor:{
													 xtype:"dictcombobox",
													 dict:"CCZX",
													 allowBlank : true
													 },	
							                	  renderer: function(value,metadata,record){
										 				var Store = cczxCombobox.getStore();
										 				var find = Store.findRecord('id',value,0,false,true,true); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;  
										           }
							                  },
							               {text:'出错部门',dataIndex:'zzebm',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
							                	  editor:{
														 xtype:"dictcombobox",
														 dict:"CCBM",
														 allowBlank : true
														 },	
								                	  renderer: function(value,metadata,record){
											 				var Store = ccbmCombobox.getStore();
											 				var find = Store.findRecord('id',value,0,false,true,true); 
											 				if(find){
									 	            			return find.get('text');
									 	            		}
											                return value;  
											           }
								                  },
								           {text:'出错组',dataIndex:'zzccz',width:180,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
												 editor:{
													 xtype:"dictcombobox",
													 dict:"CCZB",
													 allowBlank : true
													 },	
							                	  renderer: function(value,metadata,record){
										 				var Store = cczbCombobox.getStore();
										 				var find = Store.findRecord('id',value,0,false,true,true); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;  
										           }
							                  },
									      {text:'出错类型',dataIndex:'zzcclx',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
								        	  editor:{
													 xtype:"dictcombobox",
													 dict:"WTLX",
													 allowBlank : true
													}
									          },
						                  {text:'责任人',dataIndex:'duty',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
						                  ]
							
						    }]
						});
						var defaultItems=[
							custInfo,
							shouhuoInfo,
							baseInfo,
							defaultInfo,
							fujianInfo
						];
						if(me.orderType=="buDan"||me.orderType=="OR3"||me.orderType=="OR4"){
							defaultItems=[];
							defaultItems=[
								baseInfo,
								shouhuoInfo,
								custInfo,
								defaultInfo,
								fujianInfo
								]};
						me.items = [
								{
									xtype : 'form',
									region : 'north',
									bodyPadding : 5,
									maxHeight : 500,
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
									items:defaultItems
								},
								{
									xtype : 'grid',
									region : 'center',
									id : 'addSaleItemsGrid',
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
										text : '软件锁',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemRJS',
										hidden : true,
										margin : '8 0 0 0'
									},{
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
										text : '成品道具',
										height : 48,
										iconAlign : 'top',
										iconCls : 'table_add',
										id : 'addOrderItemCPDJ',
										margin : '8 0 0 0'
									},{
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
														var saleFor = grid.getStore().getAt(rowIndex).data.saleFor;
														var _shouDaFang = Ext
																.getCmp(
																		"shouDaFang")
																.getValue();
														this.up('window').fireEvent(
																		'itemEditButtonClick',
																		orderType,
																		_shouDaFang,
																		grid,
																		rowIndex,
																		colIndex,saleFor);
														//TODO 把销售分类去除
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
												text : 'SAP编号',
												dataIndex : 'sapCode',
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
												},
												renderer : function(value,
														metadata, record) {
													if(me.orderType=="buDan"){
														return parseInt(1);
													}
													var isPlus = false;
													$.ajax({
														url:'/control/product/number',
														data:{
															matnr:record.get('matnr')
														},
														type : 'GET',
														async: false,
														success:function(response){
															isPlus = response.isPlus;
														},
														failure : function(response, opts) {
															Ext.Messagebox.alert('提示信息','添加失败');
														}
													});
													if(isPlus){
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
												text : '单位',
												dataIndex : 'unit',
												width : 50,
												align : 'center',
												editor:unit,
								 	             renderer : function(value, meta, record) {
								            		var find= unit.getStore().findRecord("id",value);
								            		if(find){
								            			return find.get('text');
								            		}else{
								            			return value;
								            		}
									            }
											},
											{
												text : '产品组',
												dataIndex : 'saleFor',
												width : 50,
												align : 'center',
								 	            renderer : function(value, meta, record) {
								            		var find= saleFor.getStore().findRecord("id",value);
								            		if(find){
								            			return find.get('text');
								            		}else{
								            			return value;
								            		}
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
									action : 'saveAddSaleForm',
									},{
									text : '提&nbsp;&nbsp;交',
									width : 80,
									margin : '0 0 0 40',
									height : 30,
									action : 'saleTaskSubmit',
									hidden:"buDan"!=me.orderType?false:true,
								}, {
									text : '流程记录',
									width : 80,
									margin : '0 40 0 40',
									height : 30,
									action : 'TaskHistoric',
									hidden:"buDan"!=me.orderType?false:true,
								} ,
								{
									text : '提&nbsp;&nbsp;交',
									width : 80,
									id:'bgSubmit',
									margin : '0 0 0 40',
									height : 30,
									action : 'bgsaleTaskSubmit',
									hidden:"buDan"==me.orderType?false:true
								}, 
								{
									text : '操作审批',
									width : 80,
									margin : '0 40 0 40',
									height : 30,
									action : 'bgTaskHistoric',
									hidden:"buDan"==me.orderType?false:true,
									handler : function() {
										var bpm = "MainProductQuotation";
										var form=Ext.getCmp("addSaleForm").getForm();
										var ordeCode = form.getValues().orderCode;
										var shId = form.getValues().shId;
										var orderType = form.getValues().orderType;
										if("buDan"==me.orderType || "OR3"==me.orderType || "OR4"==me.orderType){
											bpm = "NewCustomerServiceOrdProcess";
										}
										Ext.create('Ext.ux.window.FlowWindow',{itemId:'flowwindow',uuids:shId,uuidNos:ordeCode,orderType:orderType,'bpm':bpm,listeners:{
											//激活流程前
											boforeActivate:function(form){
												var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
												var _saleId = _saleIdField.getValue();
												var flag = saleBoforeActivate(_saleId);
												return flag;
											},
											//激活流程后
											afterActivate:function(){
												var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
												var _saleId = _saleIdField.getValue();
												var flag = saleAfterActivate(_saleId);
												return flag;
											},
											boforeCommit:function(form,flowtype,nextflowId){
												var _saleId = shId;
												var flag = flowValidate(_saleId,flowtype);
												return flag;
//																return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
											},
											afterCommit:function(){
												//提交后关闭窗口并重新查询订单信息
												Ext.ComponentQuery.query('window[itemId=newSaleContentWindow]')[0].close();
												var sale2Form = Ext.ComponentQuery.query('panel[id=sale2Form]')[0];
												if("undefined"!=typeof(sale2Form)){
													var formValues = sale2Form.getValues();
												    var grid = Ext.ComponentQuery.query('panel[id=sale2Grid]')[0];
												    var store = grid.getStore();
												    store.load({
												    	params:formValues,
												    	callback:function(r,options,success){
												            if(success){
												           }
												        }
												    });
												}
												return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
											}
										}}).show();
									},		
								}]
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
							var rjs = Ext.getCmp("addOrderItemRJS");
							rjs.hide();
							var cpdj = Ext.getCmp("addOrderItemCPDJ");
							cpdj.hide();
							/*var _saleFor = Ext.getCmp("saleFor");
							_saleFor.hide();*/
							Ext.getCmp("zzaufnrField").hide();
							Ext.getCmp("zzaufnrField").setValue("");
							Ext.getCmp("zzaufnrCheck").setValue("");
							Ext.getCmp("checkFlag").setValue("true");
							var pbg = Ext.getCmp("pbgOrderCode");
							var tousu = Ext.getCmp("tousucishu");
							var amountEditor = Ext.getCmp("amountNF")
									.getEditor();
							if ("OR1" == nval || "OR7" == nval
									|| "OR8" == nval || "OR9" == nval) {
								Ext.getCmp("addOrderItemBZ").show();
								Ext.getCmp("addOrderItemFB").show();
								//有厨柜下单权限的经销商才能显示出来
								if ("X" == CURR_USER_KUNNR_CUP||"X"==CURR_USER_KUNNR_TIMBER) {
									/*_saleFor.show();*/
								}
								amountEditor.enable();
								if ("OR9" == nval) {
									Ext.getCmp("zzaufnrField").show();
									Ext.getCmp("checkFlag").setValue("false");
									Ext.getCmp("zzaufnrCheck").show();
								}
							} /*else if ("OR2" == nval) {
								ymsj.show();
								gssj.show();
								amountEditor.disable();
							}*//* else if ("OR3" == nval) {
								wjsj.show();
								amountEditor.disable();
							}*/ /*else if ("OR4" == nval) {
								xsdj.show();
								amountEditor.disable();
							}*/ else if ("OR3" == nval) {
								kfbg.show();
								pbg.allowBlank=true;
								tousu.allowBlank=true;
								tousu.hide();
								amountEditor.enable();
							} else if ("OR4" == nval) {
								mfdd.show();
								if(me.formId!=null){
									tousu.show();
								}
//								tousu.allowBlank=false;
								pbg.allowBlank=false;
								amountEditor.enable();
							} else if ("OR2" == nval) {
								rjs.show();
								wjsj.show();
								ymsj.show();
								xsdj.show();
								amountEditor.disable();
								cpdj.show();
								/*if(CURR_USER_LOGIN_NO
										.indexOf("_01") > -1){		
								}*/
							} 

						}
						
						me.listeners = {
							show : function() {
								if(me.orderType=="buDan"){
									Ext.getCmp("birthday").destroy();
									Ext.getCmp("orderPayFw").destroy();
//									Ext.getCmp("orderEvent").destroy();
									Ext.getCmp("huXing").destroy();
									Ext.getCmp("address").destroy();
									Ext.getCmp("addSaleItemsGrid").hide();
								}
								if(obj!=null){
									
								}
								loadData();
								showAddress();
							}
						};
						//收货地址
						function showAddress() {
							if (KUNNRS.indexOf("ZB") != -1||KUNNRS.indexOf("LZ") != -1) {
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
							var saleid = me.formId;
							var bjItmes=me.queryById("BJ_fujiangridItem");
							var newWindowitemGrid = me.queryById("complainid_ItemId");
							/*if("gp_customer_service"==obj.taskGroup){
								me.queryById("bgSubmit").hide();
							}*/
							if(obj!=null){
								var assignee = obj.assignee;
								if(assignee){
									if("gp_customer_service"==obj.taskGroup){//客服审核
//										Ext.getCmp("bgAdd").show();
										me.queryById("bgSubmit").hide();
										Ext.getCmp("uploadFile").show();
										Ext.getCmp("deleteFile").show();
										Ext.getCmp("urgentType").show();
										Ext.getCmp("isMj").show();
										Ext.getCmp("isQc").show();
										Ext.getCmp("isKf").show();
										Ext.getCmp("tousucishu").allowBlank=false;
										bjItmes.show();
										newWindowitemGrid.getStore().load({params:{'pid':saleid}});
										me.queryById("addFYH").show();
										var form=Ext.getCmp("addSaleForm").getForm();
										var formValues = form.getValues();
										Ext.Object.each(formValues, function(key, value, myself) {
											form.findField(key).setReadOnly(true);
										});
										form.findField("orderType").setReadOnly(false);
										form.findField("pOrderCode").setReadOnly(false);
										form.findField("urgentType").setReadOnly(false);
										form.findField("isMj").setReadOnly(false);
										form.findField("isQc").setReadOnly(false);
										form.findField("isKf").setReadOnly(false);
										form.findField("isYp").setReadOnly(false);
										form.findField("tousucishu").setReadOnly(false);
										form.findField("bgproblem").setReadOnly(false);
										form.findField("anzhuanDay").setReadOnly(false);
									}
								}
//								me.queryById("bgShouDaFang").hide();
								
							}
							if("buDan"==me.orderType){
								if (saleid==null) {
									return;
								}
								Ext.getCmp("mr_fielDset").show();
								var fyhGrid = me.queryById("addBgGrid");
								var fygStore = fyhGrid.getStore();
								fygStore.load({params:{pid:me.formId}});
								var conunt = fygStore.getCount();
								var form=Ext.getCmp("addSaleForm").getForm();
//								Ext.getCmp("anzhuanDay").allowBlank=false;
//								form.findField("anzhuanDay").allowBlank=false;
								form.findField("problem").allowBlank=false;
//								Ext.getCmp("problem").allowBlank=false;
								Ext.Ajax.request({
									url : 'main/myGoods/queryMyBjList?pid=' + id,
									async : true,
									dataType : "json",
									success:function(response, opts){
										var values = Ext.decode(response.responseText);
										if(values.length>0){
											me.queryById("addFYH").hide();
										}
									}
								});
								Ext.getCmp("uploadFile").show();
								Ext.getCmp("deleteFile").show();
								
								bjItmes.getStore().load({params:{'fileType':'BJ','pid':saleid}});
							}
							
							Ext.Ajax.request( {
								url : 'main/sale/querySaleById?id=' + ("newSaleContent"==me.otype?saleid:id),
								async : true,
								dataType : "json",
								success : function(response, opts) {
									var values = Ext
											.decode(response.responseText);
									if (values.success) {
										var formData = values.data;
										var form = Ext.getCmp("addSaleForm").getForm();
										form.setValues(formData);
										if (formData.orderCode) {
											Ext.getCmp("orderType").setReadOnly(true);
											me.queryById("bgSubmit").hide();
										}
										var saleItmes = Ext
												.getCmp("addSaleItemsGrid");
										saleItmes.getStore().load( {
											params : {
												'pid' : formData.shId
											},
											callback: function(records, operation, success) {
												if(records.length>0){
													var base = records[0].data;
													var matnr = base.matnr;
													if("160000002"==matnr){
														var wjsj = Ext.getCmp("addOrderItemWJSJ");
														wjsj.setDisabled(true);
														var xsdj = Ext.getCmp("addOrderItemXSDJ");
														xsdj.setDisabled(true);
														var ymsj = Ext.getCmp("addOrderItemYMSJ");
														ymsj.setDisabled(true);
														var cpdj = Ext.getCmp("addOrderItemCPDJ");
														cpdj.setDisabled(true);
													}else{
														var rjs = Ext.getCmp("addOrderItemRJS");
														rjs.setDisabled(true);
													}
												}
											}
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

						/**
						 * 审核提交前的操作
						 */
						function flowValidate(_saleId,flowtype){
							var _flowInfo = Ext.ux.DataFactory.getFlowActivityId(_saleId);
							var flag = true;
							if("1"==_flowInfo.docStatus && _flowInfo.assignee==true){
								//客服审核环节
								if("gp_customer_service"==_flowInfo.taskGroup){
									//1是提交，0是退回
									if(flowtype=="1"){
										var _values;
										Ext.Ajax.request({
											url:'main/sale/findBgInfoByid',
											method:'get',
											params:{
												'saleHeadId':_saleId
											},
											async:false,
											dataType : "json",
											contentType : 'application/json',
											success : function(response, opts) {
												_values = Ext.decode(response.responseText);
											},
											failure : function(response, opts) {
												Ext.MessageBox.alert("提示","加载数据失败！");
											}
										})
										if(false == _values.success){
											flag = false;
											Ext.MessageBox.alert("提示",_values.errorMsg);
										}
									}
									if(!flag){
										return false;
									}
								}
							}
						}

						me.callParent(arguments);
					}
				});
