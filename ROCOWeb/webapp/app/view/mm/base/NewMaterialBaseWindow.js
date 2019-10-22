Ext.tip.QuickTipManager.init();
Ext.define(
				'SMSWeb.view.mm.base.NewMaterialBaseWindow',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.NewMaterialBaseWindow',
					requires : [ "Ext.ux.ButtonTransparent",
							"Ext.ux.form.TrieCombobox" ,
							"Ext.ux.form.IsCascadeCombobox"],
					itemId : 'newMaterialBaseWindow_ItemId',
					sourceShow : null,// 显示来源
					saleItemStateAudit : null,// 销售明细行项目审核状态
					saleHeadId : null,// 销售单主表id
					saleOrder : null,// 销售单编号
					flowInfo : null,// 审核信息(订单)
					flowInfo2 : null,// 审核信息(物料)
					shouDaFang : null,// 售达方
					saleViewOrderType:null,//订单类型
					saveStatus : null,
					pOrderCode:null,
					saleFor : null,
					saleItemId : null,// 销售明细id
					// loadStatus从哪个页面进入
					// 1:标准产品， 2：我的物品 显示非标产品 ,3:订单,4:流程审批
					loadStatus : null,
					formId : null,
					myGoodsId : null,// 我的商品id--加载附加信息
					orderCodePosex : null,// 订单编号+行项目
					maximizable : true,
					height : 500,
					width : document.body.clientWidth * 0.8,
					modal : true,
					constrainHeader : true,
					closable : true,
					border : false,
					layout : 'fit',
					tbar : [
							{
								hidden : true,
								xtype : 'button',
								text : '保存',
								itemId : 'saveMaterial',
								iconCls : 'table_save'
							},
							{
								xtype : 'button',
								text : '刷新',
								hidden : true,
								itemId:'ser',
								icon:'resources/images/refresh.png'
							},
							{
								hidden : true,
								xtype : 'button',
								text : '导出报价清单',
								itemId : 'downloadMaterialPrice',
								icon:'/resources/images/down.png'
							},
							{
								hidden : true,
								xtype : 'button',
								text : '导入价格数据',
								itemId : 'uploadMaterialPrice',
								icon:'/resources/images/down.png'
							},
							{
								hidden : true,
								xtype : 'button',
								text : '下载报价模板',
								itemId : 'downloadTemplate',
								icon:'/resources/images/down.png'
							},
							{
								hidden : true,
								xtype : 'button',
								text : '删除物料数据',
								itemId : 'deleteImos',
								iconCls : 'table_remove'
							},
							'->',
							{
								xtype : 'button',
								text : '审批操作',
								hidden : true,
								itemId : 'flowBtutton',
								iconCls : 'flow_opt',
								handler : function() {
									var _uuidVal = this.ownerCt.ownerCt.saleItemId;
									var _uuidNoVal = this.ownerCt.ownerCt.orderCodePosex;
									var _flowInfo = Ext.ux.DataFactory
											.getFlowActivityId(_uuidVal);
									var _FLAG;
									var _msg;

									// 物料审核 验证
									var _isGpMaterial = false;
									if ('gp_material' == this.ownerCt.ownerCt.sourceShow||'gp_bg_material' == this.ownerCt.ownerCt.sourceShow) {
										//								Ext.Ajax.request({
										//									url : '/main/mm/mmApproveCheck',
										//									async:false,
										//									params : {
										//										saleItemCode:_uuidNoVal 
										//									},
										//									method : 'GET',
										//									success : function(response, opts) {
										//										var jsonResult = Ext.decode(response.responseText);
										//										if(jsonResult.success){ 
										//											_FLAG=jsonResult.data.FLAG;
										//											_msg=jsonResult.data.MSG;
										//										}else{
										//											_msg=jsonResult.errorMsg;
										//											_FLAG="N";
										//// Ext.Msg.show({
										//// title:"错误提示["+jsonResult.errorCode+"]:",
										//// icon:Ext.Msg.ERROR,
										//// msg:jsonResult.errorMsg,
										//// buttons:Ext.Msg.OK
										//// });
										//										}
										//									},
										//									failure : function(response, opts) {
										//										Ext.Msg.alert("错误代码:"+response.status,response.responseText);
										//										
										//									}
										//								});
										_isGpMaterial = true;
									}

									Ext
											.create(
													'Ext.ux.window.FlowWindow',
													{
														itemId : 'flowwindow',
														uuidVal : _uuidVal,
														uuidNoVal : _uuidNoVal,
														isGpMaterial : _isGpMaterial,
														subprocess : true,
														listeners : {
															boforeCommit : function(
																	form,
																	flowtype,
																	nextflowId) {
																//如果是物料审核组，并且是提交 那么检测
																if ((_flowInfo.taskGroup == 'gp_material' || _flowInfo.taskGroup == 'gp_shiftcount'||_flowInfo.taskGroup == 'gp_bg_material')
																		&& "1" == flowtype) {
																	Ext.Ajax
																			.request( {
																				url : '/main/mm/mmApproveCheck',
																				async : false,
																				params : {
																					saleItemCode : _uuidNoVal
																				},
																				method : 'GET',
																				success : function(
																						response,
																						opts) {
																					var jsonResult = Ext
																							.decode(response.responseText);
																					if (jsonResult.success) {
																						_FLAG = jsonResult.data.FLAG;
																						_msg = jsonResult.data.MSG;
																					} else {
																						_msg = jsonResult.errorMsg;
																						_FLAG = "N";
																					}
																				},
																				failure : function(
																						response,
																						opts) {
																					Ext.Msg
																							.alert(
																									"错误代码:"
																											+ response.status,
																									response.responseText);

																				}
																			});
																}
																if ("N" == _FLAG) {
																	Ext.Msg
																			.alert(
																					"温馨提示",
																					_msg);
																	return false;
																}
																var _idField = Ext.ComponentQuery
																		.query('form[itemId=headForm_ItemId] hiddenfield[name=id]')[0];
																var _mmId = _idField
																		.getValue();
																//add by hzm 2016.12.01 start
																var flag = subflowValidate(
																		_uuidVal,
																		_uuidNoVal,
																		_mmId,
																		flowtype,
																		nextflowId);
																//add by hzm 2016.12.01 end
																return flag;
															},
															afterCommit : function() {
																// 提交后关闭窗口并重新查询
																Ext.ComponentQuery
																		.query('window[itemId=newMaterialBaseWindow_ItemId]')[0]
																		.close();
																return true;// 必须返回为true
																// window里面的commit才会继续触法
																// ，如果为false，方法将停止执行
															}
														}
													}).show();
								}
							} ],
					 	ser:function(me){
							var ser = me.queryById("ser");
							ser.show();
							ser.on('click',function(){
							var xmlFileStore=me.queryById("XML_gridItem").getStore();
							var pdfFileStore=me.queryById("PDF_gridItem").getStore();
							var priceLineStore=me.queryById("priceLine_itemId").getStore();
							//var fujiaFormStore=me.queryById("fujiaForm_ItemId").getForm();
							xmlFileStore.reload();
							pdfFileStore.reload();
							priceLineStore.reload();
							//fujiaFormStore.load();
							me.queryById("headForm_ItemId")
								.load( {
									url : 'main/mm/queryMaterialHeadById',
									params : {
										id : me.formId
									},
									method : 'GET',
									async : false,
									success : function(f, action) {
										var result = Ext
											.decode(action.response.responseText);
										var fujiaForm = me.queryById("fujiaForm_ItemId");
										fujiaForm.getForm().findField("imosPath").setDisabled(false);
										fujiaForm.getForm().findField("imosPath").setValue(
												result.data.imosPath);
										/*Ext.Ajax.request( {
											url : 'main/mm/getSaleItem',
											method : 'GET',
											params : {
												'id' : me.saleItemId
											},
											async : false,
											dataType : "json",
											contentType : 'application/json',
											success : function(response, opts) {
												var _saleItem = Ext
														.decode(response.responseText);
												if (_saleItem.success) {
													var _saleItemStateAudit = _saleItem.data.stateAudit;
													fujiaForm.getForm().findField("stateAudit")
															.setValue(_saleItemStateAudit);
												}
											},
											failure : function(response, opts) {
												Ext.MessageBox.alert("提示",
														"订单行项目信息加载失败!");
											}
										});*/
									}
								});
						});
					},
					initComponent : function() {
						// alert("FB"+this.loadStatus);
						var me = this;
						var _formId = me.formId;
						var pOrderCode = me.pOrderCode;
						var _loadStatus = me.loadStatus;
						var _orderCodePosex = me.orderCodePosex;
						if (_formId) {
							Ext.Ajax.request( {
								url : 'main/mm/queryMaterialHeadById',
								params : {
									id : _formId
								},
								method : 'GET',
								async : false,
								success : function(f, action) {
									var result = Ext.decode(f.responseText);
									if (!me.saleFor ||"1"==me.saleFor) {
										var _resultData=result.data.saleFor;
										if(_resultData){
											me.saleFor = result.data.saleFor;
										}else{
											me.saleFor='0';
										}
									}
							}
							});
						}

						// add by mark on 20160612 start
						var expiredWindow = Ext
								.create(
										'widget.window',
										{
											closeAction : 'hide',
											closable : true,
											resizable : false,
											itemId : 'expiredWindow',
											layout : 'vbox',
											title : '超期',
											modal : true,
											width : 400,
											height : 230,
											tbar : [ {
												xtype : 'button',
												text : '保存',
												itemId : 'saveExpired',
												iconCls : 'table_save',
												handler : saveExpired
											} ],
											items : [
													{
														xtype : 'dictcombobox',
														itemId : 'expiredType',
														allowBlank : false,
														fieldLabel : '超期类型<span style="color:red;">*</span>',
														dict : 'EXPIRED_ORD_TYPE',
														style : 'margin-top:10px;margin-left:10px;margin-bottom:5px;'
													},
													{
														xtype : 'textarea',
														itemId : 'expiredReason',
														allowBlank : false,
														fieldLabel : '超期原因<span style="color:red;">*</span>',
														style : 'margin-left:10px;margin-bottom:20px;'
													},
													{
														xtype : 'hiddenfield',
														itemId : 'assignee',
														value : CURR_USER_LOGIN_NO
													}, {
														xtype : 'hiddenfield',
														itemId : 'duration'
													} ]
										});
						// add by mark on 20160612 end
						if (_formId != null && '1' != _loadStatus) {
							// saleHeadId获取订单审核信息
							if (me.saleHeadId != null
									&& me.saleHeadId.length > 0) {
								me.flowInfo = Ext.ux.DataFactory
										.getFlowActivityId(me.saleHeadId);
							}
							// saleItemId获取物料审核信息
							if ('4' == _loadStatus) {
								me.flowInfo2 = Ext.ux.DataFactory
										.getFlowActivityId(me.saleItemId);
								/*
								 * var _values; Ext.Ajax.request({ url :
								 * 'main/mm/querySaleHeaderByid', method : 'GET', params : {
								 * 'id' : me.saleHeadId }, async:false, dataType :
								 * "json", contentType : 'application/json', success :
								 * function(response, opts) { _values =
								 * Ext.decode(response.responseText); }, failure :
								 * function(response, opts) {
								 * Ext.MessageBox.alert("提示","加载数据失败！"); } });
								 * 
								 * if(_values.success){ if("0"!=_values.msg){
								 * me.saleOrder = _values.data.saleOrder; } }
								 */
							}

						}
						// console.log(me.flowInfo);
						// console.log(me.flowInfo2);
						var _isStandardField;
						var _drawTypeField;
						var headForm;
						/**
						 * 附加信息
						 */
						var fujiaForm;
						var form1;
						//var dateForm;
						/* 文件 */
						var filesTabpanel;
						var kitGrid;
						var pdfGrid;
						var xmlGrid;
						var cncGrid;
						var pictureGrid;
						var priceFileGrid;
						/* 文件 */

						/* 属性配置 */
						var propertyGrid;
						var propertyCombo;
						/* 属性配置 */

						/* 销售价格信息 */
						var salePriceForm;
						var salePriceGrid;
						var salePricePanel;
						/* 销售价格信息 */

						/* 物料信息 */
						var wuliaoTabpanel;
						var imosIdbextGrid;
						var imosIdbwgGrid;
						/* 物料信息 */
						var imosIdbextGrid01;
						var imosIdbextGrid02;
						var imosIdbextGrid03;
						var imosIdbextGrid04;
						var imosIdbextGrid05;
						var imosIdbextGrid06;
						var imosIdbextGrid07;
						var imosIdbextGrid08;
						var imosIdbextGrid09;
						if ('1' == _loadStatus) {
							_isStandardField = {
								labelStyle : 'padding-left:15px;',
								xtype : 'hiddenfield',
								name : 'isStandard',
								fieldLabel : '是否标准',
								value : '1'
							};

							/* 属性配置 start */
							propertyCombo = Ext.create(
									'Ext.ux.form.TrieCombobox', {
										trie : 'MATERIAL_PROPERTY'
									});
							propertyGrid = Ext
									.widget(
											'grid',
											{
												title : '属性配置',
												itemId : 'property_gridItemId',
												viewConfig : {
													enableTextSelection : true
												},
												border : false,
												/*
												 * tbar: [ {xtype: 'button', text: '添加',iconCls :
												 * 'table_add', handler : function() {
												 * this.up('window').fireEvent('addPropertyButtonClick'); } },
												 * {xtype: 'button', text: '删除',iconCls :
												 * 'table_remove', handler : function() {
												 * this.up('window').fireEvent('deletePropertyButtonClick'); } },
												 * {xtype: 'button', text: '配置属性',iconCls : 'table_add',
												 * handler : function() {
												 * this.up('window').fireEvent('configPropertyButtonClick'); } } ],
												 */
												// selModel:{selType:'checkboxmodel',injectCheckbox:0},
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialPropertyBase"),
												columns : [
														{
															text : '数据字典',
															dataIndex : 'propertyCode',
															width : 200,
															sortable : false,
															menuDisabled : true,
															// editor:propertyCombo,
															renderer : function(
																	value,
																	meta,
																	record) {
																var find = propertyCombo
																		.getStore()
																		.findRecord(
																				"id",
																				value);
																if (find) {
																	return find
																			.get('text');
																} else {
																	return value;
																}
															}
														},
														{
															text : '排序',
															dataIndex : 'orderby',
															width : 100,
															hidden : true,
															sortable : false,
															menuDisabled : true
														},
														{
															text : 'infoDesc',
															dataIndex : 'infoDesc',
															width : 100,
															hidden : true,
															sortable : false,
															menuDisabled : true
														} ],
												selType : 'cellmodel',
												plugins : [ cellEditing = Ext
														.create(
																'Ext.grid.plugin.CellEditing',
																{
																	enableKeyNav : true,
																	clicksToEdit : 2
																}) ]
											});
							/* 属性配置 end */

							/* 展示图片 start */
							pictureGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'PICTURE_gridItem',
												title : '展示图片',
												border : false,
												tbar : [
														{
															xtype : 'button',
															text : '图片上传',
															iconCls : 'table_add',
															hidden : !("yf00003" == CURR_USER_LOGIN_NO),
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'PICTURE',
																				_formId);
															}
														},
														{
															xtype : 'button',
															text : '图片删除',
															iconCls : 'table_remove',
															hidden : !("yf00003" == CURR_USER_LOGIN_NO),
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'PICTURE',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														// {text:'是否有效',dataIndex:'statusdesc',width:80,sortable:
														// false,menuDisabled:true},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* 图片展示 end */
							pdfGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'PDF_gridItem',
												title : 'PDF文件',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : 'PDF文件上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'PDF',
																				_formId);
															}
														},
														{
															xtype : 'button',
															text : 'PDF文件无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'PDF',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});

							filesTabpanel = Ext.widget('tabpanel', {
								title : '文件信息',
								itemId : 'filesTabpanel_ItemId',
								items : [ pictureGrid, pdfGrid ]
							});

							headForm = Ext
									.widget(
											'form',
											{
												title : '产品信息',
												itemId : 'headForm_ItemId',
												bodyStyle : "padding:5px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
												border : false,
												fieldDefaults : {
													labelAlign : 'left',
													width : 300,
													labelWidth : 100
												},
												items : [

														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'matnr',
																		fieldLabel : '产品编号',
																		maxLength : 18
																	},
																	{
																		xtype : 'dictcombobox',
																		labelStyle : 'padding-left:15px;',
																		fieldLabel : '产品类型',
																		name : 'mtart',
																		dict : 'MATERIAL_MTART',
																		emptyText : '请选择...',
																		blankText : '请选择物料产品'
																	}

															]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'vkorg',
																		fieldLabel : '销售组织',
																		maxLength : 4
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'vtweg',
																		fieldLabel : '分销渠道',
																		maxLength : 4
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'matkl',
																		fieldLabel : '物料组',
																		maxLength : 15
																	},
																	{
																		labelStyle : 'padding-left:15px',
																		xtype : 'textfield',
																		name : 'matkl2',
																		fieldLabel : '物料组2',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																labelStyle : 'padding-left:15px;',
																xtype : 'textfield',
																name : 'extwg',
																fieldLabel : '颜色',
																maxLength : 15
															} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		fieldLabel : '可配置物料',
																		name : 'kzkfgdesc',
																		readOnly : true
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'numberfield',
																		name : 'kbetr',
																		fieldLabel : '价格',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'groes',
																		fieldLabel : '规格',
																		maxLength : 50
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'meins',
																		fieldLabel : '单位',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'kschl',
																		fieldLabel : '条件类型',
																		maxLength : 4
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'spart',
																		fieldLabel : '产品组',
																		maxLength : 25
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'prdha',
																		fieldLabel : '产品层次',
																		maxLength : 15
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'vtext',
																		fieldLabel : '产品层次描述',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'textfield',
																		name : 'konwa',
																		fieldLabel : '单位(货币)',
																		maxLength : 15
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'numberfield',
																		name : 'brgew',
																		fieldLabel : '毛重',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'numberfield',
																		name : 'ntgew',
																		fieldLabel : '凈重 ',
																		maxLength : 15
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'gewei',
																		fieldLabel : '重量单位 ',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'numberfield',
																		name : 'volum',
																		fieldLabel : '体积',
																		maxLength : 15
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'textfield',
																		name : 'voleh',
																		fieldLabel : '体积单位 ',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		fieldLabel : '材质',
																		name : 'textureOfMaterial',
																		xtype : 'triecombobox',
																		trie : 'TEXTURE_OF_MATERIAL',
																		emptyText : '请选择...',
																		blankText : '请选择材质'
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'numberfield',
																		name : 'price',
																		fieldLabel : '炸单价格',
																		maxLength : 15
																	} ]
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'kzkfg',
															name : 'kzkfg',
															readOnly : true
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																xtype : 'textareafield',
																name : 'maktx',
																fieldLabel : '产品描述',
																width : 600,
																maxLength : 80
															} ]
														},
														_isStandardField,
														{
															xtype : 'hiddenfield',
															fieldLabel : 'loadStatus',
															name : 'loadStatus',
															value : _loadStatus,
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'status',
															name : 'status',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'kbetrDj',
															name : 'kbetrDj',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'kpein',
															name : 'kpein',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'kmein',
															name : 'kmein',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'krech',
															name : 'krech',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'datab',
															name : 'datab',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'datbi',
															name : 'datbi',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'ID',
															name : 'id',
															readOnly : true
														}

												]
											});

						} else {
							_isStandardField = {
								labelStyle : 'padding-left:15px;',
								xtype : 'hiddenfield',
								name : 'isStandard',
								fieldLabel : '是否标准',
								value : '0'
							};

							/* 文件信息 start */
							/* kitGrid strat */
							kitGrid = Ext
									.widget(
											'grid',
											{
												title : 'KIT文件',
												itemId : 'KIT_gridItem',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : 'KIT文件上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'KIT',
																				_formId);
															}
														},
														{
															xtype : 'button',
															text : 'KIT文件无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'KIT',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															itemId : 'fileDownloadItemIdItemId',
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* kitGrid end */

							/* pdfGrid start */
							pdfGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'PDF_gridItem',
												title : 'PDF文件',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : 'PDF文件上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															handler : function() {
																this.up('window').fireEvent('fileUploadButtonClick','PDF',_formId);
															}
														},
														{
															xtype : 'button',
															text : 'PDF文件无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'PDF',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* pdfGrid end */

							/* xmlGrid start */
							xmlGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'XML_gridItem',
												title : 'XML文件',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : 'XML文件上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'XML',
																				_formId,me.saleFor);
															}
														},
														{
															xtype : 'button',
															text : 'XML文件无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'XML',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* xmlGrid end */

							/* cncGrid start */
							cncGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'CNC_gridItem',
												title : 'CNC文件',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : 'CNC文件上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'CNC',
																				_formId);
															}
														},
														{
															xtype : 'button',
															text : 'CNC文件无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'CNC',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* cncGrid end */
							/* 报价清单 start */
							priceFileGrid = Ext
									.widget(
											'grid',
											{
												itemId : 'PRICE_gridItem',
												title : '报价清单',
												border : false,
												viewConfig : {
													enableTextSelection : true
												},
												tbar : [
														{
															xtype : 'button',
															text : '报价清单上传',
															iconCls : 'table_add',
															itemId : 'uploadFile',
															hidden : true,
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileUploadButtonClick',
																				'PRICE',
																				_formId);
															}
														},
														{
															xtype : 'button',
															text : '报价清单无效',
															iconCls : 'table_remove',
															itemId : 'deleteFile',
															handler : function() {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDeleteButtonClick',
																				'PRICE',
																				this
																						.up('grid'));
															}
														} ],
												store : Ext
														.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
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
															text : '序号',
															width : 50,
															labelAlign : 'left',
															xtype : 'rownumberer'
														},
														{
															text : '文件名称',
															dataIndex : 'uploadFileNameOld',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '备注',
															dataIndex : 'remark',
															width : 200,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传人',
															dataIndex : 'createUser',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '上传日期',
															dataIndex : 'createTime',
															width : 140,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '是否有效',
															dataIndex : 'statusdesc',
															width : 80,
															sortable : false,
															menuDisabled : true
														},
														{
															text : '文件下载',
															width : 100,
															xtype : 'actioncolumn',
															align : 'center',
															icon : '/resources/images/down.png',
															handler : function(
																	grid,
																	rowIndex,
																	colIndex) {
																this
																		.up(
																				'window')
																		.fireEvent(
																				'fileDownloadButtonClick',
																				this
																						.up('grid'),
																				rowIndex,
																				colIndex);
															},
															sortable : false,
															menuDisabled : true
														} ]
											});
							/* 报价清单 end */
							/* 文件信息 end */

							filesTabpanel = Ext.widget('tabpanel', {
								title : '文件信息',
								itemId : 'filesTabpanel_ItemId',
								items : [ pdfGrid, kitGrid, xmlGrid, cncGrid,
										priceFileGrid ]
							});

							/* 销售价格信息start */
							if ('3' == _loadStatus || '4' == _loadStatus) {
								if ("true" == IS_MONEY
										|| "gp_drawing" == me.sourceShow) {
									me.maximized=true;
									var salepriceConditionStore = Ext
											.create("SMSWeb.store.mm.sale.Store4SaleItemPrice");
									var linePriceSaleItem=Ext.create('SMSWeb.store.mm.sale.Store4MaterialPrice');
									var salecountCombo = Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'CONDITION'
											});// 运算条件
									var saleplusOrMinusCombo = Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'PLUS_OR_MINUS'
											});// 加减
									var salepriceTypeCombo = Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'PRICE_TYPE'
											});// 价格类型
									var saleisTakeNumCombo = Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'YES_NO'
											});// 是否乘数量

									salePriceForm = Ext
											.widget(
													'form',
													{
														itemId : 'salePriceForm_ItemId',
														bodyStyle : "padding:5px;padding-top:10px;",
														border : false,
														fieldDefaults : {
															labelAlign : 'left',
															labelWidth : 80
														},
														items : [ {
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'numberfield',
																		name : 'amount',
																		fieldLabel : '数量',
																		allowBlank : false,
																		minValue : 0,
																		listeners : {
																			change : function(
																					me,
																					newValue,
																					oldValue,
																					eOpts) {
																				if (newValue != oldValue) {
																					pricefunction();
																				}

																			}
																		}
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'numberfield',
																		name : 'totalPrice',
																		id : 'totalPrice_id',
																		fieldLabel : '总价',
																		allowBlank : false,
																		minValue : 0
																	} ]

														} ]
													});
									salePriceGrid = Ext
											.widget(
													'grid',
													{
														border : false,
														title : '定价条件',
														viewConfig : {
															enableTextSelection : true
														},
														//id:'testGrid',
														itemId : 'salePriceGrid_ItemId',
														store : salepriceConditionStore,
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
																	text : '类型',
																	dataIndex : 'type',
																	width : 180,
																	sortable : false,
																	menuDisabled : true,
																	// editor:salepriceTypeCombo,
																	renderer : function(
																			value,
																			meta,
																			record) {
																		var find = salepriceTypeCombo
																				.getStore()
																				.findRecord(
																						"id",
																						value);

																		if (find) {
																			return find
																					.get('text');
																		} else {
																			return value;
																		}
																	}
																},
																{
																	text : '加减',
																	dataIndex : 'plusOrMinus',
																	width : 80,
																	sortable : false,
																	menuDisabled : true,
																	// editor:saleplusOrMinusCombo,
																	renderer : function(
																			value,
																			meta,
																			record) {
																		var find = saleplusOrMinusCombo
																				.getStore()
																				.findRecord(
																						"id",
																						value);
																		if (find) {
																			return find
																					.get('text');
																		} else {
																			return value;
																		}
																	}
																},
																{
																	text : '运算条件',
																	dataIndex : 'condition',
																	width : 80,
																	sortable : false,
																	menuDisabled : true,
																	// editor:salecountCombo,
																	renderer : function(
																			value,
																			meta,
																			record) {
																		var find = salecountCombo
																				.getStore()
																				.findRecord(
																						"id",
																						value);
																		if (find) {
																			return find
																					.get('text');
																		} else {
																			return value;
																		}
																	}
																},
																{
																	text : '运算值',
																	dataIndex : 'conditionValue',
																	width : 100,
																	align : 'right',
																	sortable : false,
																	menuDisabled : true,
																	editor : {
																		xtype : 'numberfield',
																		allowBlank : true
																	}
																},
																{
																	text : '小计',
																	dataIndex : 'subtotal',
																	width : 100,
																	align : 'right',
																	sortable : false,
																	menuDisabled : true
																// ,editor:{
																// xtype:'numberfield',
																// allowBlank:true
																// }
																},
																{
																	text : '乘数量',
																	dataIndex : 'isTakeNum',
																	width : 80,
																	sortable : false,
																	menuDisabled : true,
																	// editor:saleisTakeNumCombo,
																	renderer : function(
																			value,
																			meta,
																			record) {
																		var find = saleisTakeNumCombo
																				.getStore()
																				.findRecord(
																						"id",
																						value);
																		if (find) {
																			return find
																					.get('text');
																		} else {
																			return value;
																		}
																	}
																},
																{
																	text : '总计',
																	dataIndex : 'total',
																	width : 100,
																	align : 'right',
																	sortable : false,
																	menuDisabled : true
																// ,editor:{
																// xtype:'numberfield',
																// allowBlank:true
																// }
																},
																{
																	text : '排序',
																	dataIndex : 'orderby',
																	width : 100,
																	hidden : true,
																	sortable : false,
																	menuDisabled : true,
																	editor : {
																		xtype : 'numberfield',
																		allowBlank : true
																	}
																} ],
														selType : 'cellmodel',
														plugins : [ cellEditing = Ext
																.create(
																		'Ext.grid.plugin.CellEditing',
																		{
																			enableKeyNav : true,
																			clicksToEdit : 2
																		}) ]
													});
									salePriceGrid.on('edit',
											function(editor, e) {
												pricefunction()
											});
									function pricefunction() {
										var amount = salePriceForm.getForm()
												.findField("amount").getValue();
										var materialBaseController = new SMSWeb.controller.mm.MaterialBaseController;
										var totalGrid = materialBaseController
												.calculationPrice(
														salePriceGrid, amount,
														me.saleHeadId);
										salePriceForm.getForm().findField(
												"totalPrice").setValue(
												totalGrid);
									}
									salePricePanel = Ext
											.widget(
													'panel',
													{
														// autoScroll: true,
														itemId : 'salePricePanel_ItemId',
														bodyStyle : 'overflow-y:auto;overflow-x:hidden',
														title : '销售价格信息',
														border : false,
														items : [
																salePriceForm,
																salePriceGrid ]
													});
									var priceLine=Ext.widget('grid',{
										border : false,
										title : '报价清单',
										enableKeyNav : true,
										columnLines : true,
										region: 'center',
										autoScroll:true,
										minHeight:400, 
										frame:false,
										border:0,
										itemId:'priceLine_itemId',
										store:linePriceSaleItem,
										tools:[
											{
												type:'plus',
												tooltip:'增加',
												width:30,
												height:30,
												handler:function(e,t,p){
													var model = Ext.create("SMSWeb.model.mm.pc.ModelMaterialPrice");
													var store = p.ownerCt.getStore();
													model.set('pid',me.saleItemId);
													store.insert(0, model);
												}
											},
											{
												type:'minus',
												tooltip:'删除',
												width:30,
												height:30,
												handler:function(e,t,p){
													var store=salePriceGrid.getStore();
													var records = store.getModifiedRecords();
													if(records.length>0){
														Ext.MessageBox.alert("提示信息","请先保存，再修改数据");
														return;
													}
													var priceLineSelection = priceLine.getSelectionModel();
													if(priceLineSelection.getCount()>0){
														var priceStore = priceLine.getStore();
														var delsIdMetaData = false;
														var delsId = [];
														var delNum = 0;
														Ext.each(priceLineSelection.getSelection(),function(k,v){
															if(k.raw == undefined){
																var model = priceStore.getAt(v-delNum);
																var totalPrice = model.data.totalPrice;
																var store=salePriceGrid.getStore();
																store.filterBy(function(record){
																	return record.get("type")=="PR01";
																});
																var model = store.getAt(0);
																model.set("conditionValue",parseInt((model.get("conditionValue")-totalPrice).toFixed(0)));
																store.clearFilter();
																pricefunction();
																priceStore.remove(k);
																delNum ++;
															}else{
																delsIdMetaData = true;
															}
														});
														delNum = 0;
														if(delsIdMetaData){
															Ext.MessageBox.confirm('提示','是否需要删除源数据',function(btn){
																if(btn=='yes'){
																	Ext.each(priceLineSelection.getSelection(),function(k,v){
																		if(k.raw != undefined){
																			var model = priceStore.getAt(k.index-delNum);
																			var totalPrice = model.data.totalPrice;
																			var store=salePriceGrid.getStore();
																			store.filterBy(function(record){
																				return record.get("type")=="PR01";
																			});
																			var model = store.getAt(0);
																			model.set("conditionValue",parseInt((model.get("conditionValue")-totalPrice).toFixed(0)));
																			store.clearFilter();
																			pricefunction();
																			priceStore.remove(k);
																			delNum++;
																		}
																	});
																}
															});
														}
													}else{
														Ext.MessageBox.alert("提示信息","请至少选中一条数据");
													}
												}
											}
										],
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
													width : 120,
													editor : {
														xtype : 'textfield',
														allowBlank : true
													}
												},
												{
													text : '名称',
													dataIndex : 'name',
													width : 200,
													sortable : false,
													align : 'center',
													menuDisabled : true,
													editor : {
														xtype : 'textfield',
														allowBlank : true
													}
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
													width : 120,
													sortable : false,
													menuDisabled : true,
													align : 'center',
													editor : {
														xtype : 'textfield',
														allowBlank : true
													}
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
													align:'center',
													summaryType : 'count',
													summaryRenderer : function(value) {
														return Ext.String
																.format(
																		'<font color=blue>小计</font>',
																		value);
													},
													editor : {
														xtype : 'textfield',
														allowBlank : true
													}
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
														return value.toFixed(2);
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
													/*editor : {
														xtype : 'numberfield',
														allowBlank : true
													},*/
													summaryType : 'sum',
													summaryRenderer : function(value) {
														return value;
													}
												},
												{
													text : '产线',
													dataIndex : 'line',
													width : 90,
													align : 'center',
													sortable : false,
													menuDisabled : true,
													editor:{
														xtype:'dictcombobox',
														dict:'PRODUCTIONLINE',
														allowBlank:true
													}
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
											beforeedit:function(o){
												console.log(o);
												var store=salePriceGrid.getStore();
												var records = store.getModifiedRecords();
												if(records.length>0){
													Ext.MessageBox.alert("提示信息","请先保存，再修改数据");
													return false;
												}
												return true;
											},
											edit:function(editor, e, eOpts){
												var raw = e.record.raw;// 获取修改前的数据
												
												var data = e.record.data;//获取修改后的数据
												
												/**
												 * 原数据
												 */
												var oldTotalPrice = 0.0;
												var oldUnitPrice = 0.0;
												var oldArea = 0.0;
												var oldRebate = 0.0;
												
												/**
												 * 改后数据
												 */
												var newUnitPrice = data.unitPrice;
												var newArea = data.area;
												var newRebate = data.rebate;
												if(raw != undefined){
													oldTotalPrice = raw.totalPrice;
													oldUnitPrice = raw.unitPrice;
													oldArea = raw.area;
													oldRebate = raw.rebate;
												}
												
												if(oldArea != newArea || oldUnitPrice != newUnitPrice || oldRebate != newRebate){
													// 得出 最新的价格
													var totalPrice = (newUnitPrice * newArea);
													
													var realPrice = totalPrice - oldTotalPrice;
													
													realPrice = parseInt(realPrice.toFixed(0));//将差价进行四舍五入
													
													var model=e.store.getAt(e.rowIdx);//取得当前对应的Model
													model.set("totalPrice",parseInt(totalPrice.toFixed(0)));//设置总价
													model.set("netPrice",parseInt((totalPrice*newRebate).toFixed(0)));//设置净价
													
													// 获取到定价条件的PR01
													var store=salePriceGrid.getStore();
													store.filterBy(function(record){
														return record.get("type")=="PR01";
													});
													var model = store.getAt(0);
													model.set("conditionValue",model.get("conditionValue")+realPrice);
													store.clearFilter();
													pricefunction();
													//e.record.commit();
												}
												/*var raw = e.record.raw;
												var oldTotalprice=(e.record.data.totalPrice == "")?0.0:e.record.data.totalPrice;
												var newUnitprice=e.record.data.unitPrice==""?0.0:e.record.data.unitPrice;
												var oldUnitprice=0.0;
												var newArea=e.record.data.area==""?0.0:e.record.data.area;
												var oldArea=0.0;
												var newRebate=e.record.data.rebate==""?0:e.record.data.rebate;
												if(undefined !=raw){
													oldUnitprice = raw.unitPrice;
													oldArea = raw.area;
												}else{
													oldTotalprice = 0.0;
												}
												//如果面积修改了 就需要改变价格 单价*面积=总价 净价=总价*折扣
												if(newArea!=oldArea||newUnitprice!=oldUnitprice){
													var totalPrice = newUnitprice * newArea;
													//总价计算 需要进行四舍五入
													var price=totalPrice-oldTotalprice;
													price = parseInt(price.toFixed(0));
													//净价计算
													var model=e.store.getAt(e.rowIdx);
													model.set("totalPrice",price);
													model.set("netPrice",parseInt((totalPrice*newRebate).toFixed(2)));
													var store=salePriceGrid.getStore();
													store.filterBy(function(record){
														return record.get("type")=="PR01";
													});
													var model = store.getAt(0);
													model.set("conditionValue",model.get("conditionValue")+price);
													store.clearFilter();
													pricefunction();
													//e.record.commit();
												}*/
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
									salePricePanel.add(priceLine);
									salePricePanel.doLayout();
									/* 销售价格信息end */
									priceLine.getStore().load({params:{
										pid:me.saleItemId
									}});
								}
							}

							/* 物料信息 start */
							if ('3' == _loadStatus || '4' == _loadStatus) {
								if ("false" == IS_MONEY) {
									priceFileGrid.tab.hide();
								}
								/*
								 * imosIdbextGrid =
								 * Ext.create('Ext.ux.grid.UXGrid',{ itemId:
								 * 'imosIdbextGrid_ItemId',
								 * headerModule:'TM_CHILD_MATERIAL', title: '层次' });
								 */
								var isEdit = true;
								imosIdbextGrid01 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL01',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													viewConfig:{
														getRowClass:function(record, rowIndex, rowParams, store){
															var idSerie = record.data.idSerie;
															if(idSerie.startsWith("1_")){
																var name = record.data.name;
																name = name.substring(name.lastIndexOf("_")+1,name.length)
																if(name != "2111" && name != "2222" && name != "2121" && name != "1111"){
																	me.queryById("saveMaterial").hide();
																	isEdit = false;
																	return "z-c-grid-red";
																}
															}
														}
													},
													title : '柜身',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															if(isEdit){
																Ext.MessageBox.alert('提示信息','含有出错物料无法修改');
															}
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL01',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid02 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL02',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '配件',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL02',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid03 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL03',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '门板',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL03',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid04 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL04',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '线条',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL04',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid05 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL05',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '吸塑',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL05',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid06 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL06',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '背板',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL06',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid07 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													itemId : "imosIdbextGrid07_itemId",
													moduleData : 'TMG_CHILD_MATERIAL07',
													title : '移门',
													fileupload : true,
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													formParams : {
														mappingId : _formId,
														saleHeadId : me.saleHeadId,
														saleItemId : me.saleItemId,
														orderCodePosex : _orderCodePosex
													},
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name2_ = record_
																	.get('name2');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL07',
																				title : '【' + name2_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'ICEQname2' : name2_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								imosIdbextGrid08 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL08',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '五金',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name2');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL08',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQtyp' : '8',
																					'ICEQname2' : name_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});
								imosIdbextGrid09 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIAL09',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '门扇',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name2');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIAL09',
																				title : '【' + name_ + '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQtyp' : '8',
																					'ICEQname2' : name_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});
								/*
								 * 电器物料
								 */
								imosIdbextGridCG01 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIALCG01',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '电器',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIALCG01',
																				title : '【'
																						+ name_
																						+ '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								/*
								 * 电器物料
								 */
								imosIdbextGridCG02 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIALCG02',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '水槽/配件',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIALCG02',
																				title : '【'
																						+ name_
																						+ '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});

								/*
								 * 电器物料
								 */
								imosIdbextGridCG03 = Ext
										.create(
												'Ext.ux.grid.UXGrid',
												{
													moduleData : 'TMG_CHILD_MATERIALCG03',
													gridLoad : true,
													defaultQueryCondition : {
														'ICEQorderid' : _orderCodePosex
													},
													title : '石台',
													listeners : {
														itemEditButtonClick : function(
																grid, rowIndex,
																colIndex) {
															var record_ = grid
																	.getStore()
																	.getAt(
																			rowIndex);
															var info1_ = record_
																	.get('info1');
															var name_ = record_
																	.get('name');
															Ext
																	.create(
																			'Ext.ux.window.ListViewWindow',
																			{
																				moduleData : 'TM_CHILD_MATERIALCG03',
																				title : '【'
																						+ name_
																						+ '】-物料信息',
																				defaultQueryCondition : {
																					'ICEQorderid' : _orderCodePosex,
																					'ICEQinfo1' : info1_,
																					'pOrderCode':pOrderCode
																				},
																				listeners : {
																					close : function() {
																						grid
																								.getStore()
																								.reload();
																					}
																				}
																			})
																	.show();
														}
													}
												});
								
								/*
								 * 补单
								 */
								imosIdbextGridBd01 = Ext.create(
										'Ext.ux.grid.UXGrid',
										{
											moduleData : 'TM_CHILD_MATERIALBG01',
											gridLoad : true,
											defaultQueryCondition : {
												'ICEQorderid' : _orderCodePosex
											},
											title : '补购封边',
											listeners : {
												itemEditButtonClick : function(
														grid, rowIndex,
														colIndex) {
													var record_ = grid
															.getStore()
															.getAt(
																	rowIndex);
													var info1_ = record_
															.get('info1');
													var name_ = record_
															.get('name');
													Ext
															.create(
																	'Ext.ux.window.ListViewWindow',
																	{
																		moduleData : 'TM_CHILD_MATERIALCG03',
																		title : '【'
																				+ name_
																				+ '】-物料信息',
																		defaultQueryCondition : {
																			'ICEQorderid' : _orderCodePosex,
																			'ICEQinfo1' : info1_,
																			'pOrderCode':pOrderCode
																		},
																		listeners : {
																			close : function() {
																				grid
																						.getStore()
																						.reload();
																			}
																		}
																	})
															.show();
												}
											}
										});	
								/*
								 * imosIdbextGrid =
								 * Ext.create('Ext.ux.grid.UXSupcanGrid',{
								 * itemId:'imosIdbextGrid_ItemId',
								 * build:'TM_CHILD_MATERIAL', title: '层次', ztbar:{
								 * xtype:'buttontransparent', glyph:0xf002,
								 * itemId:'imosIdbextGrid_tbar_01', text : '查询2',
								 * listeners:{ click:function(){
								 * alert(this.ownerCt.ownerCt.getSupcanGrid()); } } }
								 * });
								 */

								/*
								 * imosIdbwgGrid = Ext.create('Ext.ux.grid.UXGrid',{
								 * itemId: 'imosIdbwgGrid_ItemId',
								 * headerModule:'IMOS_IDBWG', title: '孔位' });
								 */

								/* 物料信息 end */
							}
							wuliaoTabpanel = Ext
									.widget(
											'tabpanel',
											{
												title : '物料信息',
												itemId : 'wuliaoTabpanel_ItemId',
												items : [
														imosIdbextGrid01,
														imosIdbextGrid02,
														imosIdbextGrid03,
														imosIdbextGrid04,
														imosIdbextGrid05,
														imosIdbextGrid06,
														imosIdbextGrid07,
														imosIdbextGrid08,
														this.saleFor == "3" ? imosIdbextGrid09
																: null,
														this.saleFor != "0" ? imosIdbextGridCG01
																: null,
														this.saleFor != "0" ? imosIdbextGridCG02
																: null,
														this.saleFor != "0" ? imosIdbextGridCG03
																: null /* ,imosIdbwgGrid */]
											});

							headForm = Ext
									.widget(
											'form',
											{

												itemId : 'headForm_ItemId',
												bodyStyle : "padding:5px;padding-bottom:0px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
												border : false,
												fieldDefaults : {
													labelAlign : 'left',
													width : 300,
													labelWidth : 110
												},
												items : [
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																hidden : true,
																xtype : 'displayfield',
																fieldLabel : '订单编号',
																name : 'saleOrder'
															},{
																labelStyle : 'padding-left:15px;',
																xtype : 'displayfield',
																fieldLabel : 'SAP编号',
																name : 'sapCode'
															}/*,
															{
																xtype : this.saleFor=="3"?'iscascadecombobox':'dictcombobox',
																labelStyle : 'padding-left:15px;',
																fieldLabel : '工艺分类',
																name : 'processCls',
																dict : this.saleFor == null ? 'MATERIAL_MATKL'
																		: "0" == this.saleFor ? 'MATERIAL_MATKL':
																				"1"==this.saleFor?'MATERIAL_MATKL_CUP':'MATERIAL_MATKL_STO',
																allowBlank : this.saleFor=="3"?false:true,
																cascadeMenu:"MM_GYFL",
																nextNode:'iscascadecombobox[itemId=CPFL]',
																hidden:this.saleFor=="3"?false:true,
																showDisabled:false,
																emptyText : '请选择...',
																blankText : '请选择工艺分类'
															}*/]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [

																	{
																		xtype : 'displayfield',
																		name : 'serialNumber',
																		fieldLabel : '编号'
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'hiddenfield',
																		name : 'mtart',
																		value : 'Z001',
																		fieldLabel : '产品类型'
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'displayfield',
																		fieldLabel : 'SAP行项编号',
																		name : 'sapCodePosex'
																	}
															/*
															 * { xtype:'dictcombobox',
															 * labelStyle :
															 * 'padding-left:15px;',
															 * fieldLabel : '产品类型',
															 * name:'mtart',
															 * dict:'MATERIAL_MTART',
															 * allowBlank: false,
															 * emptyText: '请选择...',
															 * blankText: '请选择产品类型' }
															 */
															]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		readOnly : true,
																		fieldStyle : 'background:#E6E6E6',
																		xtype : 'textfield',
																		name : 'groes',
																		fieldLabel : '规格',
																		// allowBlank: false,
																		maxLength : 32
																	},
																	{
																		//itemId:'CPFL',
																		//nextNode:'iscascadecombobox[itemId=KSFL]',
																		xtype : /*this.saleFor=="3"?'iscascadecombobox':*/'dictcombobox',
																		labelStyle : 'padding-left:15px;',
																		fieldLabel : '产品分类',
																		name : 'matkl',
																		dict : this.saleFor == null ? 'MATERIAL_MATKL'
																				: "0" == this.saleFor ? 'MATERIAL_MATKL'
																						: "1" == this.saleFor ? 'MATERIAL_MATKL_CUP'
																								: "3"==this.saleFor?'MATERIAL_MATKL_MM':'MATERIAL_MATKL_STO',
																		allowBlank : false,
																		//cascadeMenu:"_",//必须给定Value 不然会加载失败
																		//mainQuery:false,
																		emptyText : '请选择...',
																		blankText : '请选择产品分类'
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'numberfield',
																		name : 'longDesc',
																		fieldLabel : '深',
																		allowBlank : false,
																		maxLength : 10
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'numberfield',
																		name : 'widthDesc',
																		fieldLabel : '宽',
																		allowBlank : false,
																		maxLength : 10
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		xtype : 'numberfield',
																		name : 'heightDesc',
																		fieldLabel : '高',
																		allowBlank : false,
																		maxLength : 10
																	},
																	{
																		labelStyle : 'padding-left:15px;',
																		xtype : 'dictcombobox',
																		fieldLabel : '附件类型',
																		name : 'fileType',
																		dict : 'FILE_TYPE',
																		// editable:false,
																		allowBlank : false,
																		emptyText : '请选择...',
																		blankText : '请选择绘图类型',
																		listeners : {
																			change : function(
																					box,
																					newValue,
																					oldValue,
																					eOpts) {
																				// console.log(newValue);
																				if (fujiaForm != null) {
																					var _drawTypeField1 = fujiaForm
																							.getForm()
																							.findField(
																									"drawType");
																					if ("1" == newValue) {
																						_drawTypeField1
																								.getStore()
																								.clearFilter(
																										true);
																						_drawTypeField1
																								.getStore()
																								.filter(
																										"id",
																										/(2|3|4|5)/);
																						var _drawTypeVal = _drawTypeField1
																								.getValue();
																						if ("1" == _drawTypeVal) {
																							_drawTypeField1
																									.setValue('');
																						}
																					} else if ("2" == newValue) {
																						_drawTypeField1
																								.getStore()
																								.clearFilter(
																										true);
																						_drawTypeField1
																								.getStore()
																								.filter(
																										"id",
																										/(1|2|5)/);
																						var _drawTypeVal = _drawTypeField1
																								.getValue();
																						if ("3" == _drawTypeVal
																								|| "4" == _drawTypeVal) {
																							_drawTypeField1
																									.setValue('');
																						}
																					}
																				}
																			}
																		}
																	}

															]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [
																	{
																		//itemId:'KSFL',
																		//id:'KSFL',
																		//nextNode:'iscascadecombobox[itemId=MCPYS]',
																		cascadeDict : 'dictcombobox[itemId=color]',
																		allowBlank : false,
																		fieldLabel : /*this.saleFor == "3" ?'产品款式<font color=red>*</font>':*/'材质<font color=red>*</font>',
																		name : 'textureOfMaterial',
																		xtype : /*this.saleFor == "3" ?'iscascadecombobox':*/'triecombobox',
																		trie : this.saleFor == null ? 'TEXTURE_OF_MATERIAL'
																				: "0" == this.saleFor ? 'TEXTURE_OF_MATERIAL'
																						: "1" == this.saleFor ? 'TEXTURE_OF_MATERIAL_CUP'
																								: "3"==this.saleFor?'MM_TEXTURE':'TEXTURE_OF_MATERIAL_STO',
																		// editable:false,
																		emptyText : '请选择...',
																		//cascadeMenu:"_",//必须给定Value 不然会加载失败
																		//mainQuery:false,
																		blankText : /*this.saleFor=="3"?'请选择款式':*/'请选择材质'
																	},
																	{
																		itemId :/*this.saleFor=="3"?'MCPYS':*/'color',
																		cascade : true,
																		allowBlank : false,
																		labelStyle : 'padding-left:15px;',
																		xtype : /*this.saleFor=="3"?'iscascadecombobox':*/'dictcombobox',
																		showDisabled : false,
																		fieldLabel : '颜色',
																		name : 'color',
																		dict : 'COLOR',
																		// editable:false,
																		//cascadeMenu:"_",//必须给定Value 不然会加载失败
																		//mainQuery:false,
																		emptyText : '请选择...',
																		blankText : '请选择颜色'
																	} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																hidden : false,
																xtype : 'dictcombobox',
																fieldLabel : '销售分类',
																name : 'saleFor',
																value : this.saleFor,
																dict : 'SALE_FOR',
																emptyText : '请选择...',
																showDisabled : true
															// blankText:
															// '请选择产品系列风格'
															},{
																labelStyle : 'padding-left:15px;',
																xtype : 'dictcombobox',
																name : 'isCnc',
																dict : 'YES_NO',
																//disabled:false,
																fieldLabel : '存在CNC文件',
																emptyText : '请选择...',
																blankText : '请选择是否含CNC文件'
															}]
															},
															{
																xtype : 'fieldcontainer',
																layout : 'hbox',
																items : [ {
																	readOnly : true,
																	xtype : 'dictcombobox',
																	fieldStyle : 'background:#E6E6E6',
																	name : 'series',
																	fieldLabel : '主题系列',
																	dict : 'STYLE',
																	maxLength : 32
																	
																}
//																,
//																{
//																	readOnly : true,
//																	xtype : 'textfield',
//																	labelStyle : 'padding-left:15px;',
//																	fieldStyle : 'background:#E6E6E6',
//																	name : 'zzcpdj',
//																	fieldLabel : '产品等级',
//																	maxLength : 32
//																}
																]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																hidden : true,
																allowBlank : true,
																xtype : 'dictcombobox',
																fieldLabel : '产品系列风格',
																name : 'style',
																dict : 'STYLE',

																emptyText : '请选择...',
																blankText : '请选择产品系列风格'
															} ]
														},
														{
															xtype : 'fieldcontainer',
															layout : 'hbox',
															items : [ {
																readOnly : true,
																fieldStyle : 'background:#E6E6E6',
																xtype : 'textareafield',
																name : 'maktx',
																fieldLabel : '产品描述',
																width : 600,
																maxLength : 80
															} ]
														},
														_isStandardField,
														{
															xtype : 'hiddenfield',
															name : 'meins',
															fieldLabel : '基本单位',
															value : 'EA'
														},
														{// 销售组织
															xtype : 'hiddenfield',
															fieldLabel : 'vkorg',
															name : 'vkorg',
															readOnly : true
														},
														{// 分销渠道
															xtype : 'hiddenfield',
															fieldLabel : 'vtweg',
															name : 'vtweg',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'loadStatus',
															name : 'loadStatus',
															value : _loadStatus,
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'status',
															name : 'status',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'propertyDesc',
															name : 'propertyDesc',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : 'ID',
															name : 'id',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : '创建人',
															name : 'createUser',
															readOnly : true
														},
														{
															fieldLabel : '创建时间',
															name : 'createTime',
															xtype : 'hiddenfield',
															// format:'Y-m-d',
															readOnly : true
														},
														{
															xtype : 'hiddenfield',
															fieldLabel : '更新人',
															name : 'updateUser',
															readOnly : true
														},
														{
															fieldLabel : '更新时间',
															name : 'updateTime',
															xtype : 'hiddenfield',
															// format:'Y-m-d',
															readOnly : true
														} ]
											});
							form1 = Ext.widget('form', {
								itemId : 'form1_ItemId',
								bodyStyle : "padding:5px;padding-top:0px;",
								border : false,
								fieldDefaults : {
									labelAlign : 'left',
									width : 300,
									labelWidth : 110
								},
								items : [ {
									xtype : 'fieldcontainer',
									layout : 'hbox',
									items : [ {
										allowBlank : false,
										xtype : 'textareafield',
										name : 'zzazdr',
										fieldLabel : '安装位置',
										height : 30,
										width : 600,
										maxLength : 20
									} ]
								}, {
									xtype : 'hiddenfield',
									name : 'id',
									readOnly : true
								}, {
									xtype : 'hiddenfield',
									name : 'saleItemId',
									readOnly : true
								}, {
									xtype : 'hiddenfield',
									name : 'myGoodsId',
									readOnly : true
								} ]
							});
						}
						 var _judge=false;
						 if(me.saleHeadId != null ||me.saleHeadId !=undefined){
							 var obj = Ext.ux.DataFactory.getFlowActivityId(me.saleHeadId);
							 //usertask_drawing订单审绘
							 //_judge=true显示数据字典“中文”,false时显示“繁体”
							 if(obj.taskdefId == 'usertask_drawing'){
								 _judge = true;
							 }
						 }
						/*dateForm = Ext.widget('form',{
							//title : '附加信息',
							itemId : 'dateForm_ItemId',
							bodyStyle : "padding:5px;padding-top:0px;",
							border : false,
							fieldDefaults : {
								labelAlign : 'left',
								width : 300,
								labelWidth : 110
							},
							items :[
								{
									xtype : 'fieldcontainer',
									layout : 'hbox',
									items : [
											{
												xtype : 'hiddenfield',
												fieldLabel : 'ID',
												name : 'id',
												readOnly : true
											},
											{
									        	xtype:'dictcombobox',
												fieldLabel : '交期天数',
												emptyText: '请选择...',
												blankText : '请选择交期天数',
												allowBlank : false,
												width: 300,
												judge:_judge,
												name:'deliveryDay',
												dict:'JIAO_QI_TIAN_SHU'
									        }]
								}
							]
						});*/
						/** 附加信息 */
						fujiaForm = Ext
								.widget(
										'form',
										{
											title : '附加信息',
											itemId : 'fujiaForm_ItemId',
											bodyStyle : "padding:5px;padding-top:10px;overflow-y:auto;overflow-x:hidden;",
											border : false,
											fieldDefaults : {
												labelAlign : 'left',
												width : 300,
												labelWidth : 110
											},
											items : [
													/*
													 * { xtype: 'fieldcontainer', layout: 'hbox',
													 * items: [ { xtype:'dictcombobox', name :
													 * 'zzcomt', fieldLabel: '颜色及材质',
													 * dict:'MM_ZZCOMT', emptyText: '请选择...',
													 * blankText: '请选择颜色及材质' }, ] },
													 */
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
																{
																	allowBlank : false,
																	xtype : 'dictcombobox',
																	fieldLabel : '审核状态',
																	name : 'stateAudit',
																	dict : 'MATERIAL_STATE_AUDIT',
																	emptyText : '请选择...'
																},
																{
																	allowBlank : false,
																	labelStyle : 'padding-left:15px;',
																	xtype : 'numberfield',
																	name : 'zztyar',
																	fieldLabel : '投影面积',
																	maxLength : 15,
																	maxValue:25,
																	minValue : 0,
																	minText : '小于0是想干嘛阿你'
																}/*,{
																	labelStyle : 'padding-left:15px;',
															        xtype: 'displayfield',
															        fieldLabel: '生产预完工日期',
															        name: 'ppcDate',
															        width: 300,
															        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
															        format:'Y-m-d',
															        value: ''
														     }*/]
													},
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
																{// allowBlank: false,
																	xtype : 'numberfield',
																	name : 'zzzkar',
																	fieldLabel : '板件展开面积',
																	maxLength : 15,
																	minValue : 0,
																	minText : '小于0是想干嘛阿你'
																},
																{
																	allowBlank : false,
																	labelStyle : 'padding-left:15px;',
																	xtype : 'numberfield',
																	name : 'zzymfs',
																	fieldLabel : '移门方数',
																	maxValue:17,
																	maxLength : 15,
																	minValue : 0,
																	minText : '小于0是想干嘛阿你',
																	listeners : {
																		'change' : function(
																				obj,
																				newValue,
																				oldValue,
																				eOpts) {
																			if (newValue) {
																				Ext
																						.getCmp("zzymss_id").allowBlank = true;
																			} else {
																				Ext
																						.getCmp("zzymss_id").allowBlank = false;
																			}
																		}
																	}
																}/*,{
														    	 	labelStyle : 'padding-left:15px;',
															        xtype: 'displayfield',
															        fieldLabel: '预计出货日期',
															        name: 'psDate',
															        width: 300,
															        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
															        value: ''
													            } */]
													},
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
																{
																	// allowBlank: false,
																	id : "zzymss_id",
																	xtype : 'numberfield',
																	name : 'zzymss',
																	fieldLabel : '移门扇数',
																	maxLength : 15,
																	minValue : 0,
																	minText : '小于0是想干嘛阿你'
																},
																{
																	labelStyle : 'padding-left:15px;',
																	xtype : 'dictcombobox',
																	name : 'zzcpdj',
																	fieldLabel : '产品等级',
																	dict : 'MM_ZZCPDJ',
																	emptyText : '请选择...',
																	blankText : '请选择产品等级'
																}/*,{
																	labelStyle : 'padding-left:15px;',
															        xtype: 'displayfield',
															        fieldLabel: '计划完工日期',
															        name: 'pcDate',
															        width: 300,
															        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
															        //,
															        //value: '2015-5-10'
													            }*/ ]
													},
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
																{// allowBlank: false,
																	xtype : 'numberfield',
																	name : 'zzxsfs',
																	fieldLabel : '吸塑方数',
																	maxLength : 15,
																	minValue : 0,
																	minText : '小于0是想干嘛阿你'
																},
																{
																	labelStyle : 'padding-left:15px;',
																	xtype : 'dictcombobox',
																	fieldLabel : '绘图评级',
																	name : 'drawGrade',
																	dict : 'MATERIAL_DRAW_GRADE',
																	emptyText : '请选择...',
																	blankText : '请选择绘图评级'
																}/*,{
													            	labelStyle : 'padding-left:15px;',
															        xtype: 'displayfield',
															        fieldLabel: '实际入库日期',
															        name: 'pbDate',
															        width: 300,
															        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
															        //,
															        //value: '2015-5-10'
													            }*/

														]
													},
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
																{
																	allowBlank : false,
																	xtype : 'dictcombobox',
																	fieldLabel : '绘图类型',
																	name : 'drawType',
																	dict : 'MATERIAL_DRAW_TYPE',
																	showDisabled:false,
																	emptyText : '请选择...',
																	blankText : '请选择绘图类型'
																},
																//imos炸单服务器 均衡负载，不需要手动选择炸单服务器
																{
																	labelStyle : 'padding-left:15px;',
																	xtype : 'dictcombobox',
																	fieldLabel : 'IMOS服务器',
																	showDisabled : false,
																	name : 'imosPath',
																	dict : 'IMOS_PATH',
																	emptyText : '请选择...',
																	disabled : true
																}/*,{
																	labelStyle : 'padding-left:15px;',
															        xtype: 'displayfield',
															        fieldLabel: '实际出库日期',
															        name: 'poDate',
															        width: 300,
															        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
															    } */]
													}/*,
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [
															{
													        	xtype:'dictcombobox',
																fieldLabel : '交期天数',
																emptyText: '请选择...',
																width: 300,
																judge:_judge,
																name:'deliveryDay',
																dict:'JIAO_QI_TIAN_SHU'
													        }]
													},*/
													/*{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [ {// allowBlank: false,
															xtype : 'dictcombobox',
															name : 'hasPec',
															fieldLabel : '是否含有异形',
															dict : 'YES_NO'
														} ]
													}*/
													/*,{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [ {
															allowBlank : false,
															xtype : 'dictcombobox',
															name : 'errRea',
															dict : 'ERROR_ORD_REA',
															fieldLabel : '出错原因',
															emptyText : '请选择...'
														} ]
													}*/,
													{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [ {
															allowBlank : false,
															xtype : 'dictcombobox',
															name : 'errType',
															dict : 'ERROR_ORD_TYPE1',
															fieldLabel : '出错类型',
															emptyText : '请选择...',
														} ]
													},{
														xtype : 'fieldcontainer',
														layout : 'hbox',
														items : [ {
															allowBlank : false,
															xtype : 'textareafield',
															name : 'errRea',
															fieldLabel : '原因描述'
//															width : 300,
//															fieldStyle : "background-color:white;height:20px;padding-left:4px;"
														} ]
													}
													]
										});

						/** 附加信息 */
						var panel1;
						var tabpanel;
						if ('1' == _loadStatus) {

							tabpanel = Ext.widget('tabpanel', {
								itemId : 'mainTabpanel_ItemId',
								items : [ headForm, filesTabpanel,
										propertyGrid, fujiaForm ]
							});

						} else if ('2' == _loadStatus) {
							panel1 = Ext.widget('panel', {
								title : '产品信息',
								itemId : 'panel1_ItemId',
								items : [ headForm, form1 ]
							});
							tabpanel = Ext.widget('tabpanel', {
								itemId : 'mainTabpanel_ItemId',
								items : [ panel1, filesTabpanel ]
							});
						} else if ('3' == _loadStatus) {

							panel1 = Ext.widget('panel', {
								title : '产品信息',
								itemId : 'panel1_ItemId',
								items : [ headForm, form1 ]
							});
							if ("true" == IS_MONEY
									|| "gp_drawing" == me.sourceShow) {

								tabpanel = Ext.widget('tabpanel', {
									itemId : 'mainTabpanel_ItemId',
									items : [ panel1, filesTabpanel,
											wuliaoTabpanel, salePricePanel,
											fujiaForm ]
								});

							} else {
								tabpanel = Ext.widget('tabpanel', {
									itemId : 'mainTabpanel_ItemId',
									items : [ panel1, filesTabpanel,
											wuliaoTabpanel, fujiaForm ]
								});
							}
						} else if ('4' == _loadStatus) {

							panel1 = Ext.widget('panel', {
								title : '产品信息',
								itemId : 'panel1_ItemId',
								items : [ headForm, form1 ]
							});
							/*panelDate = Ext.widget('panel',{
								title : '附加信息',
								itemId: 'panelDate_ItemId',
								items:[fujiaForm ,dateForm]
							});*/
							if ("true" == IS_MONEY
									|| "gp_drawing" == me.sourceShow) {
								tabpanel = Ext.widget('tabpanel', {
									itemId : 'mainTabpanel_ItemId',
									items : [ panel1, filesTabpanel,
											wuliaoTabpanel, salePricePanel,
											fujiaForm ]
								});
							} else {
								tabpanel = Ext.widget('tabpanel', {
									itemId : 'mainTabpanel_ItemId',
									items : [ panel1, filesTabpanel,
											wuliaoTabpanel, fujiaForm ]
								});
							}
						}

						Ext.apply(me, {
							items : [ tabpanel ]
						});

						// 加载数据
						if (_formId != null) {
							var _saleItem;
							
							if (me.saleItemId) {
								Ext.Ajax.request( {
									url : 'main/mm/getSaleItem',
									method : 'GET',
									params : {
										'id' : me.saleItemId
									},
									async : false,
									dataType : "json",
									contentType : 'application/json',
									success : function(response, opts) {
										_saleItem = Ext
												.decode(response.responseText);
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示",
												"订单行项目信息加载失败!");
									}
								});

								if (_saleItem.success) {
									var _saleItemStateAudit = _saleItem.data.stateAudit;
									me.saleItemStateAudit = _saleItemStateAudit;
									fujiaForm.getForm().findField("stateAudit")
											.setValue(_saleItemStateAudit);
								}
							}

							headForm
									.load( {
										url : 'main/mm/queryMaterialHeadById',
										params : {
											id : _formId
										},
										method : 'GET',
										async : false,
										success : function(f, action) {
											//根据登陆用户的用户组和角色,判断是否显示 add by hzm 2016.12,2 
											//var _flowInfo2=me.flowInfo2;
											//modify by mark 只有当前环节为子流成,且用户为组长的时候,才可以修改CNC文件需要

											if ("1" != _loadStatus
													&& (USER_ROLE_ID
															.indexOf('J9P74YJRDihFhBpsKcPzyo') > -1
															|| USER_ROLE_ID
																	.indexOf('4kCQ9vNoWkr19R9iM4797U') > -1
															|| USER_ROLE_ID
																	.indexOf('ADLafzqCpvpJo3YaVLtmWC') > -1 || USER_ROLE_ID
															.indexOf('FSePLXmhvYcQEsmCtc6E27') > -1)
													&& "callactivity1" == me.flowInfo.taskdefId) {
												me.queryById("saveMaterial")
														.show();
												headForm.getForm().findField(
														"isCnc").setReadOnly(
														false);
											}

											headForm.getForm().findField(
													"loadStatus").setValue(
													_loadStatus);
											headForm.getForm().findField(
											"sapCode").setValue(
													_saleItem.data.sapCode);
											headForm.getForm().findField(
											"sapCodePosex").setValue(
													_saleItem.data.sapCodePosex);
											var result = Ext
													.decode(action.response.responseText);
											var kunnr = result.data.createUser
													.substr(0, 7);//lj75002_01
											var createTime = result.data.createTime;
											// var saleFor=result.data.saleFor;
											// 如果本页面的salefor赋值了，那么需要忽略元数据中的saleFor
											// 在订单审绘需要修改
											headForm.getForm().findField(
											"matkl").value=result.data.matkl;
											headForm.getForm().findField(
											"textureOfMaterial").value=result.data.textureOfMaterial;
											headForm.getForm().findField(
											"color").value=result.data.color;
											headForm.getForm().findField(
													"saleFor").setValue(
													result.data.saleFor);
											headForm.getForm().findField(
											"saleFor").setReadOnly(true);
											var salePriceGrid = me
													.queryById('salePriceGrid_ItemId');

											if ('1' == _loadStatus) {
												fujiaForm.getForm().setValues(
														result.data);
												propertyGrid.getStore().load( {
													params : {
														'pid' : _formId
													}
												});
												pictureGrid.getStore().load( {
													params : {
														'fileType' : 'PICTURE',
														'pid' : _formId
													}
												});
												pdfGrid.getStore().load( {
													params : {
														'fileType' : 'PDF',
														'pid' : _formId
													}
												});
												// 标准产品只读
												var headFormFields = headForm
														.getForm().getFields();
												headFormFields
														.each(function(field) {
															if (field.getName() == 'price'
																	|| field
																			.getName() == 'textureOfMaterial'
																	|| field
																			.getName() == "matkl2") {
															} else {
																field
																		.setReadOnly(true);
																field
																		.setFieldStyle('background:#E6E6E6');
															}
														});

												var _kzkfg = headForm.getForm()
														.findField("kzkfg")
														.getValue();
												if ("X" == _kzkfg) {
													headForm
															.getForm()
															.findField(
																	"kzkfgdesc")
															.setValue("是");
												} else {
													headForm
															.getForm()
															.findField(
																	"kzkfgdesc")
															.setValue("否");
												}
												fujiaForm.getForm().findField(
														"stateAudit").hide();
												fujiaForm.getForm().findField(
														"imosPath").hide();
												fujiaForm.getForm().findField(
														"drawType").hide();
											} else {

												if (me.myGoodsId != null
														&& me.myGoodsId.length > 0) {
													loadfjForm(form1,
															me.myGoodsId);// 加载附加信息
													/*loadfjForm(dateForm,
															me.myGoodsId);*/
												} else {
													// 子流程没有传myGoodsId
													if (me.saleItemId) {
														if (_saleItem.success) {
															var _myGoodsId = _saleItem.data.myGoodsId;
															loadfjForm(form1,
																	_myGoodsId);// 加载附加信息
														}
													}
												}
												if (me.saleItemId) {
													Ext.Ajax.request({
														url:'core/ord/err/findByErrorInfo',
														params : {
															id:me.saleItemId
														},
														method : 'GET',
														frame : true,
														dataType : "json",
														contentType : 'application/json',
														success : function(response, opts) {
															var value = Ext.decode(response.responseText);
															if(value.success){
																var errType = value.data.ERR_TYPE;
																var errRea = value.data.ERR_REA;
																var errDesc = value.data.ERR_DESC;
																var fujiaForm = me.queryById("fujiaForm_ItemId").getForm();
																fujiaForm.findField("errType").setValue(errType);
																fujiaForm.findField("errRea").setValue(errRea);
//																fujiaForm.findField("errDesc").setValue(errDesc);
															}
														},
														failure : function(response, opts) {
															Ext.MessageBox.alert("提示信息","查询错误信息失败");
														}
													});
												}
												kitGrid.getStore().load( {
													params : {
														'fileType' : 'KIT',
														'pid' : _formId
													}
												});
												pdfGrid.getStore().load( {
													params : {
														'fileType' : 'PDF',
														'pid' : _formId
													}
												});
												xmlGrid.getStore().load( {
													params : {
														'fileType' : 'XML',
														'pid' : _formId
													}
												});
												cncGrid.getStore().load( {
													params : {
														'fileType' : 'CNC',
														'pid' : _formId
													}
												});
												priceFileGrid.getStore().load( {
													params : {
														'fileType' : 'PRICE',
														'pid' : _formId
													}
												});

												var _fileTypeValue = result.data.fileType;

												if ('2' == _loadStatus) {
													xmlGrid.tab.hide();
													cncGrid.tab.hide();
													priceFileGrid.tab.hide();

													setFileTypeStatus(me,
															_fileTypeValue);
												}

												if ('3' == _loadStatus
														|| '4' == _loadStatus) {
													// 附加信息
													// fujiaForm.getForm().setValues(result.data);
													var fujiaFormFields = fujiaForm
															.getForm()
															.getFields();
													fujiaFormFields
															.each(function(
																	field) {
																var _name1 = field
																		.getName();
																if ("stateAudit" != _name1) {
																	field
																			.setValue(result.data[_name1]);
																}
															});
													//modify by mark 
													//只有在查单以及流程审批的时候才需要CNC这个值的显示以及赋值
													headForm
															.getForm()
															.findField("isCnc")
															.setValue(
																	_saleItem.data.isCnc);

													if ("true" == IS_MONEY
															|| "gp_drawing" == me.sourceShow) {
														if (salePriceGrid != null
																&& "true" == IS_MONEY) {
															salePriceGrid
																	.getStore()
																	.load(
																			{
																				params : {
																					'pid' : me.saleItemId
																				}
																			});
														}
														if (salePriceForm != null) {
															if (_saleItem.success) {
																salePriceForm
																		.getForm()
																		.findField(
																				"amount")
																		.setValue(
																				_saleItem.data.amount);
																salePriceForm
																		.getForm()
																		.findField(
																				"totalPrice")
																		.setValue(
																				_saleItem.data.totalPrice);
															} else {
																Ext.MessageBox
																		.alert(
																				"提示",
																				_saleItem.errorMsg);
															}
														}
													}

													// 订单审核选择绘图类型
													// 附件类型 1：2020，2：pdf
													// 2:pdf--》绘图类型 ：2020绘图/Imos绘图
													// comboboxStore.filter("id",/^(?!Z007)/);
													var _drawTypeField1 = fujiaForm
															.getForm()
															.findField(
																	"drawType");
													if ("2" == _fileTypeValue) {
														_drawTypeField1
																.getStore()
																.filter("id",
																		/(1|2|5)/);
													} else if ("1" == _fileTypeValue) {
														_drawTypeField1
																.getStore()
																.filter("id",
																		/(2|3|4|5)/);
													}

													if ('3' == _loadStatus) {
														var _flowInfo = me.flowInfo;
														if ("0" == _flowInfo.docStatus) {// 未激活
															setFileTypeStatus(
																	me,
																	_fileTypeValue);
														} else if ("1" == _flowInfo.docStatus) {
															if ("gp_store" == _flowInfo.taskGroup) {// 起草
																setFileTypeStatus(
																		me,
																		_fileTypeValue);
															}
														}
													} else if ('4' == _loadStatus) {
														var _flowInfo2 = me.flowInfo2;
														if ("1" == _flowInfo2.docStatus) {
															if ("gp_store" == _flowInfo2.taskGroup) {// 起草
																setFileTypeStatus(
																		me,
																		_fileTypeValue);
															}
														}
													}
												}

											}
											;

										},
										failure : function(form, action) {
											var result = Ext
													.decode(action.response.responseText);
											headForm.getForm().findField(
													"loadStatus").setValue(
													_loadStatus);
											Ext.Msg
													.alert('提示',
															result.errorMsg);
										}
									});
							//delete by Mark 2016.12.30
							//add by hzm 2016.12.5
							//console.log(headForm.getForm().findField("isCnc"));
							//headForm.getForm().findField("isCnc").setValue(_saleItem.data.isCnc);
						} else {
							headForm.getForm().findField("loadStatus")
									.setValue(_loadStatus);
							filesTabpanel.setDisabled(true);
							if ('2' == _loadStatus) {
								xmlGrid.tab.hide();
								cncGrid.tab.hide();
								priceFileGrid.tab.hide();
							}

						}
						me.callParent(arguments);
					},
					listeners : {
						show : function() {
							var me = this;
							var _loadStatus = me.loadStatus;
							var headForm = me.queryById("headForm_ItemId");
							if (me.orderCodePosex != null) {
								var _saleOrder = headForm.getForm().findField(
										"saleOrder");
								_saleOrder.show();
								_saleOrder.setValue(me.orderCodePosex);
							}
							// 报价清单文件
							var bts = Ext.ComponentQuery
									.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
							if (bts != null && bts.length > 0) {
								for ( var i = 0; i < bts.length; i++) {
									bts[i].hide();
								}
							}
							// alert(me.sourceShow);
							// 订单流程审批状态
							if (me.flowInfo) {
								var _flowInfo = me.flowInfo;
								// 0 未审批1 审批中2 已审批
								if ("0" != _flowInfo.docStatus) {

									me.queryById("saveMaterial").hide();

									// 非标产品只读
									var headFormFields = headForm.getForm()
											.getFields();
									headFormFields.each(function(field) {
										field.setReadOnly(true);
									});

									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].hide();
										}
									}

									if ('3' == me.loadStatus) {
										if ("true" == IS_MONEY) {
											var salePriceForm = me
													.queryById('salePriceForm_ItemId');
											var salePriceFormFields = salePriceForm
													.getForm().getFields();
											salePriceFormFields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});
										}

										var fujiaForm = me
												.queryById('fujiaForm_ItemId');
										var fujiaFormFields = fujiaForm
												.getForm().getFields();
										fujiaFormFields.each(function(field) {
											field.setReadOnly(true);
										});
										var form1 = me
												.queryById("form1_ItemId");// 附加信息表单
										var form1Fields = form1.getForm()
												.getFields();
										// 订单审绘
										if ("gp_drawing" == _flowInfo.taskGroup
												&& _flowInfo.assignee == true) {
											// TODO
											me.ser(me);
											/*var xmlFileStore=me.queryById("XML_gridItem").getStore();
											var pdfFileStore=me.queryById("PDF_gridItem").getStore();
											var priceLineStore=me.queryById("priceLine_itemId").getStore();
											var task = {
													run:function(){
														xmlFileStore.reload();
														pdfFileStore.reload();
														priceLineStore.reload();
													},
													interval:10000
											};
											Ext.TaskManager.start(task);*/
											me.queryById("saveMaterial").show();
											headFormFields
													.each(function(field) {
														field
																.setReadOnly(false);
													});
											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].show();
												}
											}
											// 报价清单文件
											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].hide();
												}
											}

											fujiaFormFields
													.each(function(field) {
														field
																.setReadOnly(false);
													});

											var _stateAuditField = fujiaForm
													.getForm().findField(
															"stateAudit");
											// B已审绘
											// C出错返回
											// D柜体审核完成
											// E已审价
											// QX取消
											if ("E" == me.saleItemStateAudit
													|| "D" == me.saleItemStateAudit
													|| "QX" == me.saleItemStateAudit) {
												_stateAuditField
														.setReadOnly(true);
												_stateAuditField
														.setFieldStyle('background:#E6E6E6');

												/*me.queryById("saveMaterial")
														.hide();*/
												var bts = Ext.ComponentQuery
														.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
												if (bts != null
														&& bts.length > 0) {
													for ( var i = 0; i < bts.length; i++) {
														bts[i].hide();
													}
												}
											} else {
												_stateAuditField.getStore()
														.filter("id", /(B|C)/);
											}

											form1Fields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});
											/*if("cl00007"==_flowInfo.userId||"cl00156"){
												me.queryById("deleteImos").show();
											}*/
											
										} else if("bg_gp_drawing" == _flowInfo.taskGroup
												&& _flowInfo.assignee == true){
											// TODO
											me.ser(me);
											/*var ser = me.queryById("ser");
											ser.show();
											me.queryById("fujiaForm_ItemId").getForm().findField("imosPath").setDisabled(false);
											ser.on('click',function(){
												var xmlFileStore=me.queryById("XML_gridItem").getStore();
												var pdfFileStore=me.queryById("PDF_gridItem").getStore();
												var priceLineStore=me.queryById("priceLine_itemId").getStore();
												var fujiaFormStore=me.queryById("fujiaForm_ItemId").getForm();
												xmlFileStore.reload();
												pdfFileStore.reload();
												priceLineStore.reload();
												fujiaFormStore.load();
											});*/
											/*var xmlFileStore=me.queryById("XML_gridItem").getStore();
											var pdfFileStore=me.queryById("PDF_gridItem").getStore();
											var priceLineStore=me.queryById("priceLine_itemId").getStore();
											var task = {
													run:function(){
														xmlFileStore.reload();
														pdfFileStore.reload();
														priceLineStore.reload();
													},
													interval:10000
											};
											Ext.TaskManager.start(task);*/
											me.queryById("saveMaterial").show();
											headFormFields
													.each(function(field) {
														field
																.setReadOnly(false);
													});
											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].show();
												}
											}
											// 报价清单文件
											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].hide();
												}
											}

											fujiaFormFields
													.each(function(field) {
														field
																.setReadOnly(false);
													});

											var _stateAuditField = fujiaForm
													.getForm().findField(
															"stateAudit");
											// B已审绘
											// C出错返回
											// D柜体审核完成
											// E已审价
											// QX取消
											if ("E" == me.saleItemStateAudit
													|| "D" == me.saleItemStateAudit
													|| "QX" == me.saleItemStateAudit) {
												_stateAuditField
														.setReadOnly(true);
												_stateAuditField
														.setFieldStyle('background:#E6E6E6');

												me.queryById("saveMaterial")
														.hide();
												var bts = Ext.ComponentQuery
														.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
												if (bts != null
														&& bts.length > 0) {
													for ( var i = 0; i < bts.length; i++) {
														bts[i].hide();
													}
												}
											} else {
												_stateAuditField.getStore()
														.filter("id", /(B|C)/);
											}

											form1Fields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});
											me.queryById("deleteImos").show();
											/*if("cl00007"==_flowInfo.userId||"cl00156"){
												me.queryById("deleteImos").show();
											}*/
											// 订单审价
										} else if ("gp_valuation" == _flowInfo.taskGroup
												&& _flowInfo.assignee == true) {

											me.queryById("saveMaterial").show();
											if(me.queryById("priceLine_itemId").getStore().getCount()<=0){
												me.queryById("uploadMaterialPrice").show();
												me.queryById("downloadTemplate").show();
											}
											me.queryById("saveMaterial")
													.setText("保存价格");
											me.queryById("downloadMaterialPrice").show();
											headFormFields
													.each(function(field) {
														field
																.setFieldStyle('background:#E6E6E6');
													});
											fujiaFormFields
													.each(function(field) {
														if (field.getName() != 'stateAudit') {
															field
																	.setFieldStyle('background:#E6E6E6');
														}
													});
											fujiaForm.getForm().findField(
													"stateAudit").setReadOnly(
													false);

											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].show();
												}
											}
											form1Fields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});

											var _stateAuditField = fujiaForm
													.getForm().findField(
															"stateAudit");

											// C出错返回
											// D柜体审核完成
											// E已审价
											// QX取消
											if ("QX" == me.saleItemStateAudit) {
												_stateAuditField
														.setReadOnly(true);
												_stateAuditField
														.setFieldStyle('background:#E6E6E6');

												me.queryById("saveMaterial")
														.hide();
												var bts = Ext.ComponentQuery
														.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
												if (bts != null
														&& bts.length > 0) {
													for ( var i = 0; i < bts.length; i++) {
														bts[i].hide();
													}
												}
											} else {
												_stateAuditField
														.getStore()
														.filter("id", /(C|D|E)/);
											}
											// showPDF();
											// 门店起草状态
										} else if ("gp_store" == _flowInfo.taskGroup
												&& _flowInfo.assignee == true) {
											me.queryById("saveMaterial").show();
											headFormFields
													.each(function(field) {
														field
																.setReadOnly(false);
													});

											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].show();
												}
											}
											// 报价清单文件
											var bts = Ext.ComponentQuery
													.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
											if (bts != null && bts.length > 0) {
												for ( var i = 0; i < bts.length; i++) {
													bts[i].hide();
												}
											}

											fujiaFormFields
													.each(function(field) {
														field
																.setFieldStyle('background:#E6E6E6');
													});

											var _stateAuditField = fujiaForm
													.getForm().findField(
															"stateAudit");
											// B已审绘
											// C出错返回
											// D柜体审核完成
											// E已审价
											// QX取消
											if ("B" == me.saleItemStateAudit
													|| "E" == me.saleItemStateAudit
													|| "D" == me.saleItemStateAudit
													|| "QX" == me.saleItemStateAudit) {
												_stateAuditField
														.setReadOnly(true);
												_stateAuditField
														.setFieldStyle('background:#E6E6E6');

												me.queryById("saveMaterial")
														.hide();
												var bts = Ext.ComponentQuery
														.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
												if (bts != null
														&& bts.length > 0) {
													for ( var i = 0; i < bts.length; i++) {
														bts[i].hide();
													}
												}
											} else {

											}

										} else {
											headFormFields
													.each(function(field) {
														field
																.setFieldStyle('background:#E6E6E6');
													});
											fujiaFormFields
													.each(function(field) {
														field
																.setFieldStyle('background:#E6E6E6');
													});
											form1Fields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});
										}
										if("gp_store_customer" == _flowInfo.taskGroup
												&& _flowInfo.assignee == true){
											me.queryById("downloadMaterialPrice").show();
										}
										if (!("gp_valuation" == _flowInfo.taskGroup)||("gp_valuation" == _flowInfo.taskGroup)
												&& _flowInfo.assignee == false) {
											if(me.queryById("priceLine_itemId") != undefined || null != me.queryById("priceLine_itemId")){
												Ext.each(me.queryById("priceLine_itemId").columns,function(column){
													console.log(column.editor);
													if("undefined"!=typeof(column.editor)){
														column.editor=null;
													}
												});
											}
										}
									}

								} else {
									me.queryById("saveMaterial").show();
									if ('3' == me.loadStatus) {
										var fujiaForm = me
												.queryById('fujiaForm_ItemId');
										var fujiaFormFields = fujiaForm
												.getForm().getFields();
										fujiaFormFields
												.each(function(field) {
													field.setReadOnly(true);
													field
															.setFieldStyle('background:#E6E6E6');
												});
										if ("true" == IS_MONEY) {
											var salePriceForm = me
													.queryById('salePriceForm_ItemId');
											var salePriceFormFields = salePriceForm
													.getForm().getFields();
											salePriceFormFields
													.each(function(field) {
														field.setReadOnly(true);
														field
																.setFieldStyle('background:#E6E6E6');
													});
										}
									}
								}
							} else {
								me.queryById("saveMaterial").show();
							}
							//补购物料审核
							if("gp_bg_material" == _flowInfo.taskGroup&& _flowInfo.assignee == true){

								var bts = Ext.ComponentQuery
										.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid button");
								if (bts != null && bts.length > 0) {
									for ( var i = 0; i < bts.length; i++) {
										bts[i].show();
									}
								}
								var uxgrid_ = Ext.ComponentQuery
										.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid");
								if (bts != null && bts.length > 0) {
									for ( var i = 0; i < uxgrid_.length; i++) {
										uxgrid_[i].columns[1].show(true);
									}
								}

								var bts = Ext.ComponentQuery
										.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId] button");
								if (bts != null && bts.length > 0) {
									for ( var i = 0; i < bts.length; i++) {
										bts[i].hide();
									}
								}
								var uxgrid_ = Ext.ComponentQuery
										.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId]");
								if (bts != null && bts.length > 0) {
									for ( var i = 0; i < uxgrid_.length; i++) {
										uxgrid_[i].columns[1].hide(true);
									}
								}

//								fujiaFormFields
//										.each(function(field) {
//											field
//													.setFieldStyle('background:#E6E6E6');
//										});
								// 重新审绘
							
							}

							// 物料审批状态
							if (me.flowInfo2) {
								// 非标产品只读
								var headFormFields = headForm.getForm()
										.getFields();
								headFormFields.each(function(field) {
									field.setReadOnly(true);
									field.setFieldStyle('background:#E6E6E6');
								});

								var bts = Ext.ComponentQuery
										.query("NewMaterialBaseWindow grid button");
								if (bts != null && bts.length > 0) {
									for ( var i = 0; i < bts.length; i++) {
										bts[i].hide();
									}
								}

								var fujiaForm = me
										.queryById('fujiaForm_ItemId');
								var fujiaFormFields = fujiaForm.getForm()
										.getFields();
								fujiaFormFields.each(function(field) {
									field.setReadOnly(true);
								});

								var form1 = me.queryById("form1_ItemId");// 附加信息表单
								var form1Fields = form1.getForm().getFields();
								form1Fields.each(function(field) {
									field.setReadOnly(true);
									field.setFieldStyle('background:#E6E6E6');
								});

								// 2020绘图
								var flowInfo2 = me.flowInfo2;
								if ("gp_drawing_2020" == flowInfo2.taskGroup
										&& flowInfo2.assignee == true) {
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=XML_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									// 选择IMOS服务器
									var imosPathField = fujiaForm.getForm()
											.findField("imosPath");
									imosPathField.setReadOnly(false);
									me.queryById("saveMaterial").show();

									fujiaFormFields
											.each(function(field) {
												if (field.getName() != 'imosPath') {
													field
															.setFieldStyle('background:#E6E6E6');
												}
											});
									// imos绘图
								} else if ("gp_drawing_imos" == flowInfo2.taskGroup
										&& flowInfo2.assignee == true) {
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var uxgrid_ = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < uxgrid_.length; i++) {
											uxgrid_[i].columns[1].show(true);
										}
									}

									fujiaFormFields
											.each(function(field) {
												field
														.setFieldStyle('background:#E6E6E6');
											});
									me.queryById("deleteImos").show();
									// 物料审核
								} else if (("gp_material" == flowInfo2.taskGroup||"gp_bg_material" == flowInfo2.taskGroup)
										&& flowInfo2.assignee == true) {
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var uxgrid_ = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < uxgrid_.length; i++) {
											uxgrid_[i].columns[1].show(true);
										}
									}

									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].hide();
										}
									}
									var uxgrid_ = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId]");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < uxgrid_.length; i++) {
											uxgrid_[i].columns[1].hide(true);
										}
									}

									fujiaFormFields
											.each(function(field) {
												field
														.setFieldStyle('background:#E6E6E6');
											});
									// 重新审绘
								} else if ("gp_drawing" == flowInfo2.taskGroup
										&& flowInfo2.assignee == true) {
									me.ser(me);
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=XML_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=KIT_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=PDF_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=CNC_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									me.queryById("saveMaterial").show();

									headFormFields
											.each(function(field) {
												if (field.getName() == 'groes'
														|| field.getName() == 'maktx') {

												} else {
													field.setReadOnly(false);
													field
															.setFieldStyle('background:transparent');
												}
											});

									fujiaFormFields.each(function(field) {
										field.setReadOnly(false);
									});
									var _stateAuditField = fujiaForm.getForm()
											.findField("stateAudit");
									// B已审绘
									// C出错返回
									// E已审价
									// QX取消
									if ("E" == me.saleItemStateAudit
											|| "D" == me.saleItemStateAudit
											|| "QX" == me.saleItemStateAudit) {
										_stateAuditField.setReadOnly(true);
										_stateAuditField
												.setFieldStyle('background:#E6E6E6');

										me.queryById("saveMaterial").hide();
										var bts = Ext.ComponentQuery
												.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
										if (bts != null && bts.length > 0) {
											for ( var i = 0; i < bts.length; i++) {
												bts[i].hide();
											}
										}
									} else {
										_stateAuditField.getStore().filter(
												"id", /(B|C)/);
									}
									// 移门算料
								} else if ("gp_shiftcount" == flowInfo2.taskGroup
										&& flowInfo2.assignee == true) {
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									var uxgrid_ = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid[itemId=imosIdbextGrid07_itemId]");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < uxgrid_.length; i++) {
											uxgrid_[i].columns[1].show(true);
										}
									}
									fujiaFormFields
											.each(function(field) {
												field
														.setFieldStyle('background:#E6E6E6');
											});
									// 客服起草--子流程起草状态
								} else if ("gp_store" == flowInfo2.taskGroup
										&& flowInfo2.assignee == true) {
									me.queryById("saveMaterial").show();
									headFormFields
											.each(function(field) {
												if (field.getName() == 'groes'
														|| field.getName() == 'maktx') {

												} else {
													field.setReadOnly(false);
													field
															.setFieldStyle('background:transparent');
												}
											});

									var form1 = me.queryById("form1_ItemId");// 附加信息表单
									var form1Fields = form1.getForm()
											.getFields();
									form1Fields
											.each(function(field) {
												field.setReadOnly(false);
												field
														.setFieldStyle('background:transparent');
											});

									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].show();
										}
									}
									// 报价清单文件
									var bts = Ext.ComponentQuery
											.query("NewMaterialBaseWindow grid[itemId=PRICE_gridItem] button");
									if (bts != null && bts.length > 0) {
										for ( var i = 0; i < bts.length; i++) {
											bts[i].hide();
										}
									}

									fujiaFormFields
											.each(function(field) {
												field.setReadOnly(true);
												field
														.setFieldStyle('background:#E6E6E6');
											});

									var _stateAuditField = fujiaForm.getForm()
											.findField("stateAudit");
									// B已审绘
									// C出错返回
									// D柜体审核完成
									// E已审价
									// QX取消
									if ("B" == me.saleItemStateAudit
											|| "E" == me.saleItemStateAudit
											|| "D" == me.saleItemStateAudit
											|| "QX" == me.saleItemStateAudit) {
										_stateAuditField.setReadOnly(true);
										_stateAuditField
												.setFieldStyle('background:#E6E6E6');

										me.queryById("saveMaterial").hide();
										var bts = Ext.ComponentQuery
												.query("NewMaterialBaseWindow tabpanel[itemId=filesTabpanel_ItemId] grid button");
										if (bts != null && bts.length > 0) {
											for ( var i = 0; i < bts.length; i++) {
												bts[i].hide();
											}
										}
									} else {

									}
								} else {
									fujiaFormFields
											.each(function(field) {
												field
														.setFieldStyle('background:#E6E6E6');
											});
								}
								showPDF();
							}
							// console.log(me.flowInfo);
							// console.log(me.flowInfo2);
							if ('3' == me.loadStatus || '4' == me.loadStatus) {
								me.queryById("flowBtutton").show();
							} else if ('1' == me.loadStatus) {
								me.queryById("saveMaterial").show();
							}

							/* 价格控制 */
							if ('3' == me.loadStatus) {

								var salePriceGrid = me
										.queryById('salePriceGrid_ItemId');
								if (salePriceGrid != null) {
									me.salePriceGrid1(IS_MONEY, me.flowInfo,
											salePriceGrid);
								}

							} else if ('4' == me.loadStatus) {

								var salePriceForm = me
										.queryById('salePriceForm_ItemId');
								if (salePriceForm != null) {
									var totalPriceField = salePriceForm
											.getForm().findField("totalPrice");
									totalPriceField.setReadOnly(true);
									totalPriceField
											.setFieldStyle('background:#E6E6E6');
									var amountField = salePriceForm.getForm()
											.findField("amount");
									amountField.setReadOnly(true);
									amountField
											.setFieldStyle('background:#E6E6E6');
								}

								var salePriceGrid = me
										.queryById('salePriceGrid_ItemId');
								if (salePriceGrid != null) {

									Ext.Array
											.each(
													salePriceGrid.columns,
													function(column) {
														if ("undefined" != typeof (column.dataIndex)
																&& "conditionValue" == column.dataIndex) {
															column.editor = null;
														}
													});
								}

								updateSaleItemAmount(me, me.flowInfo2);
							}
							// 订单重审 定价条件查看权限 2016-06-02
							if ("gp_drawing" == me.sourceShow) {
								var salePricePanel = me
										.queryById('salePricePanel_ItemId');
								salePricePanel.show();
								if (IS_MONEY != "true") {
									var salePriceGrid = me
											.queryById('salePriceGrid_ItemId');
									salePriceGrid.hide();
									Ext.getCmp("totalPrice_id").hide();
								}
							}
							// 判断订单行项目当前状态
							/*
							 * console.log(me.saleItemStateAudit);
							 * if(me.saleItemStateAudit){ var _saleItemStateAudit =
							 * me.saleItemStateAudit; // _saleItemStateAudit = "QX"; //
							 * console.log(_saleItemStateAudit); //取消 状态
							 * if("QX"==_saleItemStateAudit){
							 * me.queryById("saveMaterial").hide(); var fujiaForm =
							 * me.queryById('fujiaForm_ItemId'); if(fujiaForm!=null){
							 * fujiaForm.getForm().findField("stateAudit").setReadOnly(true);
							 * fujiaForm.getForm().findField("stateAudit").setFieldStyle('background:#E6E6E6'); }
							 * //其他状态 }else{
							 *  } }
							 */
							// add by Mark on 20160413--start
							// 物料审核与孔位审核不能上传或失效图纸
							// if(("gp_hole_examine"==me.flowInfo2.taskGroup||"gp_material"==me.flowInfo2.taskGroup)
							// && flowInfo2.assignee==true){
							// var bts = Ext.ComponentQuery.query("NewMaterialBaseWindow
							// grid[itemId=PDF_gridItem] button[itemId=uploadFile]");
							// console.log(bts.length);
							// if(bts &&bts.length>0)
							// bts[0].hide();
							// bts=Ext.ComponentQuery.query("NewMaterialBaseWindow grid[itemId=PDF_gridItem]
							// button[itemId=deleteFile]");
							// if(bts && bts.length>0)
							// bts[0].hide();
							// }
							// add by Mark on 20160413--end
						},
						close : function() {
							var me = this;
							var _flowInfo = me.flowInfo;
							// 审价
						if (me.loadStatus == "3"
								&& "gp_valuation" == _flowInfo.taskGroup
								&& _flowInfo.assignee == true) {
							Ext.getCmp("newSaleItemGridId").getStore().reload();
							Ext.getCmp("saleItemPriceGridId").getStore()
									.reload();
						}
					}
					},
					// 销售价格
					salePriceGrid1 : function(IS_MONEY, _flowInfo, grid) {
						var me = this;
						if (_flowInfo) {
							if ("0" != _flowInfo.docStatus) {
								if ("gp_valuation" == _flowInfo.taskGroup
										&& _flowInfo.assignee == true) {

								} else {
									me.salePriceGridEdit(grid);
								}
							} else {
								me.salePriceGridEdit(grid);
							}
						}
					},
					salePriceGridEdit : function(grid) {
						Ext.Array.each(grid.columns, function(column) {
							if ("undefined" != typeof (column.dataIndex)
									&& "conditionValue" == column.dataIndex) {
								column.editor = null;
							}
						});
					}
				});
/**
 * 审核提交前操作
 * 
 * @returns
 */
function subflowValidate(_id, _idVal, _mmId, flowtype, nextflowId) {

	var _flowInfo = Ext.ux.DataFactory.getFlowActivityId(_id);
	var flag = true;
	if ("1" == _flowInfo.docStatus && _flowInfo.assignee == true) {

		// gp_drawing_2020 2020绘图 检查xml文件
		// gp_drawing_imos IMOS绘图
		// gp_material 物料审核
		// 当前环节为
		if ("gp_drawing_2020" == _flowInfo.taskGroup) {
			// 1是提交，0是退回
			if (flowtype == "1") {
				/*
				 * var _imosPathField =
				 * Ext.ComponentQuery.query('form[itemId=fujiaForm_ItemId]
				 * dictcombobox[name=imosPath]')[0]; var _imosPathFieldValue =
				 * _imosPathField.getValue(); if(_imosPathFieldValue==null ||
				 * _imosPathFieldValue==""){
				 * Ext.MessageBox.alert("提示","请选择IMOS服务器,并保存！"); return false; }
				 */

				var nextflowField = Ext.ComponentQuery
						.query("form[itemId=flowForm] uxcombobox[name=nextflow]")[0];
				var nextflowFieldValue = nextflowField.getValue();
				// 炸单
				// 提交Imos不需要验证xml
				if ("flow5" == nextflowFieldValue) {
					var _values;
					Ext.Ajax.request( {
						url : 'main/mm/gpdrawing2020Validate',
						method : 'GET',
						params : {
							'materialHeadId' : _mmId
						},
						async : false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示", "加载数据失败！");
							flag = false;
						}
					});
					if (false == _values.success) {
						flag = false;
						Ext.MessageBox.alert("提示", _values.errorMsg);
					}

				}

			}

		} else if ("gp_drawing_imos" == _flowInfo.taskGroup) {
			// 1是提交，0是退回
			if (flowtype == "1") {
				if ("flow6" == nextflowId) {
					Ext.Ajax.request( {
						//url:'main/mm/checkMaterial',
						url : 'main/mm/checkHasCNC',
						method : 'GET',
						params : {
							'saleItemId' : _id,
							'temp' : _idVal
						},
						async : false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							flag = false;
							Ext.MessageBox.alert("提示", "加载数据失败！");
						}
					});

					if (_values.msg != "success") {
						flag = false;
						Ext.MessageBox.alert("提示", _values.msg);
					}
				}
				// Ext.Ajax.request({
				// url : 'main/mm/gpdrawingImosValidate',
				// method : 'GET',
				// params : {
				// 'saleItemId' : _id,
				// 'materialHeadId' : _mmId
				// },
				// async:false,
				// dataType : "json",
				// contentType : 'application/json',
				// success : function(response, opts) {
				//						
				// },
				// failure : function(response, opts) {
				// Ext.MessageBox.alert("提示","加载数据失败！");
				// flag = false;
				// }
				// });
			}
			// 重新审绘 --->重走子流程
		} else if ("gp_drawing" == _flowInfo.taskGroup) {
			// 1是提交，0是退回
			if (flowtype == "1") {
				var _values;
				Ext.Ajax.request( {
					url : 'main/mm/gpdrawingMaterialValidate',
					method : 'GET',
					params : {
						'saleItemId' : _id,
						'materialHeadId' : _mmId
					},
					async : false,
					dataType : "json",
					contentType : 'application/json',
					success : function(response, opts) {
						_values = Ext.decode(response.responseText);
					},
					failure : function(response, opts) {
						Ext.MessageBox.alert("提示", "加载数据失败！");
						flag = false;
					}
				});
				if (false == _values.success) {
					flag = false;
					Ext.MessageBox.alert("提示", _values.errorMsg);
				}
			}
			// add by Mark on 20160414 finally --start
			// 审核状态为已审绘的审绘环节订单，不能退回至起草
			else if (flowtype == "0") {
				var _kitfiles = Ext.ComponentQuery
						.query("NewMaterialBaseWindow grid[itemId=KIT_gridItem]")[0]
						.getStore().data.items;
				// console.log(_kitfiles.getStore().data.items);
				var _fujiaform = Ext.ComponentQuery
						.query("NewMaterialBaseWindow form[itemId=fujiaForm_ItemId]")[0];
				var _fileType = Ext.ComponentQuery
						.query("NewMaterialBaseWindow form[itemId=headForm_ItemId] dictcombobox[name=fileType]")[0];
				var _stateAuditValue = _fujiaform.getForm().findField(
						"stateAudit").getValue();
				if ("A" != _stateAuditValue && "C" != _stateAuditValue
						&& "1" == _fileType.getValue()) {
					var _innerFlag = false;
					var _kitfileIndex;
					for (_kitfileIndex in _kitfiles) {
						if (_kitfiles[_kitfileIndex].data.status == '') {
							_innerFlag = true;
						}
					}
					if (!_innerFlag) {
						flag = false;
						Ext.MessageBox.alert("提示", "请上传kit文件!");
					}
				}
			}
			// add by Mark on 20160414 finally--end
			// 客户起草--子流程起草状态
		} else if ("gp_store" == _flowInfo.taskGroup) {
			// 1是提交，0是退回
			if (flowtype == "1") {
				var _values;
				Ext.Ajax.request( {
					url : 'main/mm/gpstoreValidate',
					method : 'GET',
					params : {
						'saleItemId' : _id,
						'materialHeadId' : _mmId
					},
					async : false,
					dataType : "json",
					contentType : 'application/json',
					success : function(response, opts) {
						_values = Ext.decode(response.responseText);
					},
					failure : function(response, opts) {
						Ext.MessageBox.alert("提示", "加载数据失败！");
						flag = false;
					}
				});
				if (false == _values.success) {
					flag = false;
					Ext.MessageBox.alert("提示", _values.errorMsg);
				}
			}
		} else if ("gp_hole_examine" == _flowInfo.taskGroup
				|| "gp_material" == _flowInfo.taskGroup||"gp_bg_material" == _flowInfo.taskGroup) {
			if (flowtype == "1") {

				var _pdffiles = Ext.ComponentQuery
						.query("NewMaterialBaseWindow grid[itemId=PDF_gridItem]")[0]
						.getStore().data.items;
				var _index;
				var _innerflag = false;
				for (_index in _pdffiles) {
					if ("X" == _pdffiles[_index].data.status) {
					} else {
						_innerflag = true;
					}
				}
				if (!_innerflag) {
					Ext.MessageBox.alert("提示", "请上传有效pdf文件！");
				}
				flag = _innerflag;
				if ("flow19" != nextflowId) {
					Ext.Ajax.request( {
						url : 'main/mm/checkMaterial',
						method : 'GET',
						params : {
							'saleitemId' : _id
						},
						async : false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							flag = false;
							Ext.MessageBox.alert("提示", "加载数据失败！");
						}
					});
					if (_values.msg != "success") {
						flag = false;
						Ext.MessageBox.alert("提示", _values.msg);
					}
				}
			}

			//add by hzm  start 2016.11.30
			if ("gp_hole_examine" == _flowInfo.taskGroup) {
				if (nextflowId == "flow21" || nextflowId == "flow20") {
					Ext.Ajax.request( {
						//url:'main/mm/checkMaterial',
						url : 'main/mm/checkHasCNC',
						method : 'GET',
						params : {
							'saleItemId' : _id,
							'temp' : _idVal
						},
						async : false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							flag = false;
							Ext.MessageBox.alert("提示", "加载数据失败！");
						}
					});

					if (_values.msg != "success") {
						flag = false;
						Ext.MessageBox.alert("提示", _values.msg);
					}
				}
			}else if("gp_material" == _flowInfo.taskGroup){
				if(nextflowId=="flow21"){
					Ext.Ajax.request({
						//url:'main/mm/checkMaterial',
					    url:'main/mm/checkHasCNC',
						method:'GET',
						params:{
							'saleItemId':_id,
							'temp':_idVal
							},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							flag = false;
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
				    });
				
					if(_values.msg!="success"){
						flag=false;
						Ext.MessageBox.alert("提示",_values.msg);
					}
				}
			}

		} else if ("gp_shiftcount" == _flowInfo.taskGroup) {//add by hzm  start 2016.12.01
			// 移门算料     下流程为:孔位审核则nextflowId="flow25" , 移门审核则nextflowId="flow23" ,结束审核则nextflowId="flow21"
				if(nextflowId=="flow15"){
					Ext.Ajax.request({
						//url:'main/mm/checkMaterial',
					    url:'main/mm/checkHasCNC',
						method:'GET',
						params:{
							'saleItemId':_id,
							'temp':_idVal
							},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_values = Ext.decode(response.responseText);
						},
						failure : function(response, opts) {
							flag = false;
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
				    });
				
					if(_values.msg!="success"){
						flag=false;
						Ext.MessageBox.alert("提示",_values.msg);
					}
				}
		}
		//add by hzm  end 2016.12.01
	}
	// add by mark --end
	// add by mark for expired on 20160618--start
	if (flag) {
		if (flowtype == "1") {
			Ext.Ajax.request( {
				url : 'main/user/checkActExpired',
				method : 'GET',
				async : false,
				dataType : "json",
				contentType : 'application/json',
				params : {
					assignee : CURR_USER_LOGIN_NO,
					// taskId:_flowInfo.taskId,
					procinstId : _flowInfo.procinstid,
					actId : _flowInfo.taskdefId,
					checkType : 'taskId'
				},
				success : function(response, opts) {
					_values = Ext.decode(response.responseText);
				},
				failure : function(response, opts) {
					Ext.MessageBox.alert("提示", "加载数据失败！");
				}
			});
			if (_values.data.hasexpired == true) {
				Ext.ComponentQuery
						.query('window[itemId=expiredWindow] hiddenfield[itemId=duration]')[0]
						.setValue(_values.data.duration);
				Ext.ComponentQuery.query('window[itemId=expiredWindow]')[0]
						.show();
				return false;
				;
			}

		}
	}
	// add by mark for expired on 20160618--end
	return flag;
}

// 显示PDF
function showPDF() {
	var bts = Ext.ComponentQuery
			.query("NewMaterialBaseWindow grid[itemId=PDF_gridItem] button");
	if (bts != null && bts.length > 0) {
		for ( var i = 0; i < bts.length; i++) {
			bts[i].show();
		}
	}
}
// 更改数量
function updateSaleItemAmount(me, flowInfo2) {
	if (flowInfo2) {
		if ("0" != flowInfo2.docStatus) {
			if (flowInfo2.assignee == true) {
				// 起草，重新审绘更改数量
				if ("gp_drawing" == flowInfo2.taskGroup
						|| "gp_store" == flowInfo2.taskGroup) {
					var salePriceForm = me.queryById('salePriceForm_ItemId');
					if (salePriceForm != null) {
						var amountField = salePriceForm.getForm().findField(
								"amount");
						amountField.setReadOnly(false);
						amountField.setFieldStyle('background:transparent');
					}
				}
			}
		}
	}
}

function saveExpired() {
	var _values;
	var _expiredType = Ext.ComponentQuery
			.query('window[itemId=expiredWindow] dictcombobox[itemId=expiredType]')[0];
	var _expiredReason = Ext.ComponentQuery
			.query('window[itemId=expiredWindow] textarea[itemId=expiredReason]')[0];
	var _expiredAssignee = Ext.ComponentQuery
			.query('window[itemId=expiredWindow] hiddenfield[itemId=assignee]')[0]
			.getValue();
	var _saleId = Ext.ComponentQuery
			.query('window[itemId=newMaterialBaseWindow_ItemId]')[0].saleItemId;
	var _duration = Ext.ComponentQuery
			.query('window[itemId=expiredWindow] hiddenfield[itemId=duration]')[0]
			.getValue();
	var _flowInfo = Ext.ux.DataFactory.getFlowActivityId(_saleId);
	if (!_expiredType.isValid()) {
		_expiredType.focus();
	} else if (!_expiredReason.isValid()) {
		_expiredReason.focus();
	} else {
		Ext.Ajax.request( {
			url : 'main/user/saveExpired',
			params : {
				'expiredType' : _expiredType.getValue(),
				'expiredReason' : _expiredReason.getValue(),
				'assignee' : _expiredAssignee,
				'procinstId' : _flowInfo.procinstid,
				'actId' : _flowInfo.taskdefId,
				'actName' : _flowInfo.taskName,
				'duration' : _duration
			},
			method : 'POST',
			async : false,
			dataType : "json",
			contentType : 'application/json',
			success : function(response, opts) {
				_values = Ext.decode(response.responseText);
			},
			failure : function(response, opts) {
				Ext.MessageBox.alert("提示信息", "网络异常");
			}
		});
		Ext.ComponentQuery.query('window[itemId=expiredWindow]')[0].hide();
		Ext.ComponentQuery.query('window[itemId=flowwindow] buttontransparent')[0]
				.fireEvent("click");
	}
}

