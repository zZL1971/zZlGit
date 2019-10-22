Ext.tip.QuickTipManager.init();
Ext
		.define(
				'SMSWeb.view.sale.SaleOutputDataPanel',
				{
					extend : 'Ext.panel.Panel',
					alias : 'widget.SaleOutputDataPanel',
					border : false,
					layout : 'border',
					formId : null,
					editFlag : null,
					autoScroll : true,
					initComponent : function() {
						var form = Ext
								.widget(
										'searchform',
										{
											region : 'north',
											alias : 'widget.CustDiscountReportForm',
											id : 'queryForm',
											bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",
											border : false,
											tbar : [
													{
														xtype : 'button',
														text : '查询',
														itemId : 'query',
														iconCls : 'table_search',
														hidden : false
													},
													{
														xtype : 'buttontransparent',
														text : '导出',
														icon : '/resources/images/down.png',
														handler : function() {
															var _params = '';
															Ext.MessageBox
																	.confirm(
																			"温馨提示",
																			"导出到Excel",
																			function(
																					btn) {
																				if (btn == "yes") {
																					Ext.Array
																							.each(
																									grid.columns,
																									function(
																											content,
																											index,
																											Self) {
																										if (true != content.hidden
																												&& 'rownumberer' != content.xtype) {
																											_params += '&'
																													+ content.dataIndex
																													+ '='
																													+ content.text;
																										}
																									});
																					var _search = form
																							.getSearchs();
																					for ( var key in _search) {
																						_params += "&query"
																								+ key
																								+ '='
																								+ _search[key];
																					}
																					var _requestUrl = this.location.href
																							.split(basePath)[1];
																					_params += '&requestUrl=' + _requestUrl;
																					var url = basePath
																							+ 'core/report/exportExcel2/exportSaleOutputExcel?1=1'
																							+ _params;
																					window
																							.open(
																									url,
																									"导出Excel",
																									'fullscreen');
																					//var url=
																				}
																			});
														}
													},{
														xtype : 'button',
														text : '同步出库信息',
														itemId : 'syncShim',
														hidden : CURR_USER_LOGIN_NO.indexOf("lj")!=-1?true:false
													},{
														xtype : 'numberfield',
														fieldLabel :'同步天数',
														id:'days',
														hidden : CURR_USER_LOGIN_NO.indexOf("lj")!=-1?true:false
													} ],
											fieldDefaults : {
												labelWidth : 100,
												labelAlign : "left",
												labelStyle : 'padding-left:5px;'
											},
											layout : {
												type : 'table',
												columns : 4
											},
											items : [
													{
														xtype : 'textfield',
														fieldLabel : '用户',
														allowBlank : true,
														value : CURR_USER_KUNNR,
														hidden : true,
														name : 'userKunnr',
														width : 250
													},
													{
														xtype : 'textfield',
														fieldLabel : '售达方',
														allowBlank : true,
														hidden : ((!Ext
																.isEmpty(CURR_USER_KUNNR)) && ('admin' != CURR_USER_LOGIN_NO)),
														name : 'kunnr',
														width : 250
													},
													{
														xtype : 'textfield',
														fieldLabel : '售达方名称',
														allowBlank : true,
														hidden : ((!Ext
																.isEmpty(CURR_USER_KUNNR)) && ('admin' != CURR_USER_LOGIN_NO)),
														name : 'kunnrName',
														width : 250
													},
													{
														xtype : 'textfield',
														fieldLabel : '客户姓名',
														allowBlank : true,
														hidden : ((!Ext
																.isEmpty(CURR_USER_KUNNR)) && ('admin' != CURR_USER_LOGIN_NO)),
														name : 'custName',
														width : 250
													},
													{
														xtype : 'textfield',
														fieldLabel : '客户电话',
														allowBlank : true,
														hidden : ((!Ext
																.isEmpty(CURR_USER_KUNNR)) && ('admin' != CURR_USER_LOGIN_NO)),
														name : 'custPhone',
														width : 250
													}, {
														xtype : 'dictcombobox',
														dict : 'REGIO',
														fieldLabel : '地区',
														allowBlank : true,
														name : 'regio',
														width : 250
													}, {
														xtype : 'dictcombobox',
														dict : 'BZIRK',
														fieldLabel : '大区',
														allowBlank : true,
														name : 'bzirk',
														width : 250
													}, /*{
														xtype : 'textfield',
														fieldLabel : 'SAP单号',
														allowBlank : true,
														name : 'sapCode',
														width : 250
													}, */{
														xtype : 'textfield',
														fieldLabel : '订单编号',
														allowBlank : true,
														name : 'orderCode',
														width : 250
													}, {
														xtype : 'textfield',
														fieldLabel : '行号',
														allowBlank : true,
														name : 'posex',
														width : 250
													}, {
														xtype : 'datefield',
														fieldLabel : '出库日期',
														name : 'startDate',
														format : 'Y-m-d'
													}, {
														xtype : 'datefield',
														fieldLabel : '到',
														name : 'endDate',
														format : 'Y-m-d'
													} ]

										});

						var grid = Ext
								.widget(
										'grid',
										{
											extend : 'Ext.grid.Panel',
											alias : 'widget.SaleOutputDataGrid',
											store : 'sale.SaleOutputStore',
											id : 'SaleOutputDataGrid',
											enableKeyNav : true,
											columnLines : true,
											border : false,
											features : [
													{
														ftype : 'groupingsummary'/*,startCollapsed:true*/,
														showSummaryRow : true,
														groupByText : '按当前进行分组',
														showGroupsText : '显示分组',
														groupHeaderTpl : "分组【{rows.length}】:{groupValue}"
													}, {
														ftype : 'summary',
														dock : 'bottom'
													} ],
											style : 'border-top:1px solid #C0C0C0;',
											region : 'center',
											viewConfig : {
												enableTextSelection : true
											//可以复制单元格文字
											},
											columns : [
													{
														xtype : 'rownumberer',
														width : 30,
														align : 'right'
													},
													{
														text : 'id',
														dataIndex : 'id',
														width : 0,
														hidden : true
													},
													{
														text : '名称',
														dataIndex : 'name',
														align : 'left',
														width : 100
													},
													{
														text : '售达方',
														dataIndex : 'kunnr',
														align : 'left',
														hidden : true,
														width : 70
													},
													{
														text : '售达方名称',
														dataIndex : 'kunnrName',
														align : 'left',
														hidden : true,
														width : 80
													},
													{
														text : '客户姓名',
														dataIndex : 'custName',
														align : 'left',
														hidden : true,
														width : 80
													},
													{
														text : '联系方式',
														dataIndex : 'custPhone',
														align : 'left',
														hidden : true,
														width : 120
													},
													{
														text : 'Sap订单号',
														dataIndex : 'sapCode',
														align : 'left',
														hidden : true,
														width : 120
													},
													{
														text : '订单号',
														dataIndex : 'orderCode',
														align : 'left',
														width : 150
													},
													{
														text : '行号',
														dataIndex : 'posex',
														align : 'left',
														width : 70
													},
													{
														text : '产品名称',
														dataIndex : 'productName',
														align : 'left',
														width : 80
													},
													{
														text : '安装地址',
														dataIndex : 'custAddress',
														align : 'left',
														hidden : true,
														width : 310
													},
													{
														text : '出库日期',
														dataIndex : 'outputTime',
														align : 'left',
														width : 150
													},
													{
														text : '通用码',
														dataIndex : 'generalSum',
														align : 'right',
														hidden : true
														//width : 80
													},
													{
														text : '柜体',
														dataIndex : 'carcaseSum',
														align : 'right',
														width : 70
														//summaryType : 'sum'
													},
													{
														text : '门板',
														dataIndex : 'shutterSum',
														align : 'right',
														width : 70
														//summaryType : 'sum'
													},
													{
														text : '背板',
														dataIndex : 'backboardSum',
														align : 'right',
														width : 70
														//summaryType : 'sum'
													},
													{
														text : '配件',
														dataIndex : 'partsSum',
														align : 'right',
														width : 70
														//summaryType : 'sum'
													},
													{
														text : '五金',
														dataIndex : 'metalsSum',
														align : 'right',
														width : 70,
														summaryType : 'sum'
													},
													{
														text : '移门',
														dataIndex : 'sliddoorSum',
														align : 'right',
														width : 70
														//summaryType : 'sum'
													},
													{
														text : '吸塑门板',
														dataIndex : 'plasticSum',
														align : 'right',
														width : 100,
														summaryType : 'sum'
													},
													{
														text : '装饰件',
														dataIndex : 'decorationSum',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													},
													{
														text : '工匠',
														dataIndex : 'artizanSum',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													},
													{
														text : '道具',
														dataIndex : 'stagepSum',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													},
													{
														text : '电器',
														dataIndex : 'electricSum',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													},
													{
														text : '石台',
														dataIndex : 'stoneSum',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													}, {
														text : '总计',
														dataIndex : 'total',
														align : 'right',
														width : 80
														//summaryType : 'sum'
													} ],
											dockedItems : [ {
												xtype : 'pagingtoolbar',
												store : 'sale.SaleOutputStore',
												dock : 'bottom',
												displayInfo : true,
												displayMsg : "显示 {0} -{1}条，共{2} 条",
												border : false,
												items : [
														'-',
														'每页',
														{
															xtype : 'combobox',
															editable : false,
															width : 55,
															listeners : {
																'render' : function(
																		comboBox) {
																	var grid = comboBox.ownerCt.ownerCt.items.items[0];
																	comboBox
																			.setValue(this.store.pageSize);
																},
																'select' : function(
																		comboBox) {
																	var grid = comboBox.ownerCt.ownerCt.items.items[0];
																	grid
																			.getStore().pageSize = comboBox
																			.getValue();
																	grid
																			.getStore()
																			.load(
																					{
																						params : {
																							start : 0,
																							limit : comboBox
																									.getValue()
																						}
																					});
																}
															},
															store : Ext
																	.create(
																			'Ext.data.Store',
																			{
																				fields : [
																						'id',
																						'name' ],
																				data : [
																						{
																							'id' : 25,
																							'name' : 25
																						},
																						{
																							'id' : 50,
																							'name' : 50
																						},
																						{
																							'id' : 100,
																							'name' : 100
																						},
																						{
																							'id' : 200,
																							'name' : 200
																						},
																						{
																							'id' : 500,
																							'name' : 500
																						} ]
																			}),
															displayField : 'name',
															valueField : 'id'
														}, '条' ]
											} ]
										});

						// 生成页面
						Ext.apply(this, {
							items : [ form, grid ]
						});

						this.callParent(arguments);
					}

				});