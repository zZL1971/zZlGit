Ext.define("SMSWeb.view.main.WorkSpace",
				{
					extend : 'Ext.panel.Panel',
					alias : 'widget.WorkSpace',
					itemId:'test',
					border : false,
					layout:'vbox',
					autoScroll : true,
//					style:{
						//overflow:'scroll'
//					},
					bodyStyle : {
						background : '#F3F3F5',
						overflow:'scroll'
					},
					dockedItems : [ {
						xtype : 'toolbar',
						dock : 'top',
						items : [
								{
									text : '上一天',
									listeners : {
										render : function(p) {p.getEl().on('click',function(){
											var _dateField=Ext.ComponentQuery.query("datefield[itemId=queryDate]")[0];
											_dateField.setValue(new Date(_dateField.getValue().getTime()-(24*3600*1000)));
											//_dateField.setDay((_dateField.getDay()-1));
											//console.log(_dateField);
											query();
										})}
									}
								},
								{
									xtype : 'datefield',
									itemId : 'queryDate',
									format : 'Y-m-d',
									value : Ext.util.Format.date(Ext.Date.add(new Date(), Ext.Date.MONTH),"Y-m-d")
								},
								{
									text : '下一天',
									listeners : {
										render : function(p) {p.getEl().on('click',function(){
											var _dateField=Ext.ComponentQuery.query("datefield[itemId=queryDate]")[0];
											_dateField.setValue(new Date(_dateField.getValue().getTime()+(24*3600*1000)));
											query();
										})}
									}
								},
								{
									xtype : "combobox",
									fieldLabel : "流程类型",
									valueField : 'code',
									displayField : 'description',
									itemId : "ActType",
									emptyText : '请选择流程类型',
									//applyTo : 'combo',
									listeners : {
										'change' : function(me, newValue,
												oldValue) {
											var _subActIdComponent = Ext.ComponentQuery
													.query("dictcombobox[itemId=SubActId]")[0];
											var _mainActIdComponent = Ext.ComponentQuery
													.query("dictcombobox[itemId=MainActId]")[0];
											if ('main' == newValue) {
												_subActIdComponent.setVisible(false);
												_mainActIdComponent.setVisible(true);
											}
											if ('sub' == newValue) {
												_subActIdComponent.setVisible(true);
												_mainActIdComponent.setVisible(false);
											}
										}
									},
									store : new Ext.data.SimpleStore({
										fields : [ 'description', 'code' ],
										data : [ [ '主流程', 'main' ],
												[ '子流程', 'sub' ] ]
									}),
								}, {
									xtype : "dictcombobox",
									fieldLabel : "子流程环节",
									itemId : "SubActId",
									dict : "MATNR_HUAN_JIE",
									hidden : true
								}, {
									xtype : "dictcombobox",
									fieldLabel : "主流程环节",
									itemId : "MainActId",
									dict : "ORDER_HUAN_JIE",
									hidden : true
								}, {
									xtype : 'button',
									text : '查询',
									itemId:'queryBtn',
									handler : function() {
										// 查询
										query();
									}
								} ]
					} ],
					initComponent : function() {
						checkUser();
						var EfficiencyContainer = new Ext.Container(
								{
									itemId : "EfficiencyContainer",
									xtype : 'basic-panels',
									style : 'margin:0px;border:true;width:inherit;height:80px;',
									requires : [ 'Ext.layout.container.Table' ],
									hidden:true,
									layout : {
										type : 'table',
										columns : 3,
										tdAttrs : {
											style : 'padding: 20px;'
										}
									},
									defaults : {
										xtype : 'panel',
										width : 300,
										height : 110,
										bodyPadding : 0,
										style : 'text-align:left;',
										border : false
									},
									items : [
											{
												itemId : 'ComplateRate',
												// title : ' 当日订单',
												style : 'border-top:3px solid #E7EBED;',
												title : false,
												border : false,
												items : [
														{
															itemId : 'ComplateRateTitle',
															style:'height:30px',
															bodyStyle : 'width:100%;height:30px;border:0px;border-bottom:1px solid #F3F3F5;text-align:left;line-height:30px;padding-left:15px;color:rgb(103, 106, 108);font-weight:bold;',
															html : '当日订单'
														},
														{
															itemId : 'ComplateRateContent',
															height:70,
															bodyStyle : 'width:100%;height:70px;line-height: 70px;border:0px;padding-left: 40px;font-weight: bold;font-size: 30px;color: rgb(103, 106, 108);',
															html : '0/0'
														} ],
												listeners : {
													render : function(p) {p.getEl().on('click',function(p) {
																			var _store = new Ext.data.Store(
																					{
																						fields : [
																								{
																									name : 'orderCode',
																									type : 'string'
																								},
																								{
																									name : 'orderTotal',
																									type : 'numbercolumn',
																									format : '0.00'
																								},
																								{
																									name : 'orderStatus',
																									type : 'string'
																								},
																								{
																									name : 'tdFinStatus',
																									type : 'string'
																								},
																								{
																									name : 'assignee',
																									type : 'string'
																								},
																								{
																									name : 'actName',
																									type : 'string'
																								},
																								{
																									name : 'startTime',
																									type : 'date',
																									dateFormat : 'Y-m-d H:i:s'
																								},
																								{
																									name : 'endTime',
																									type : 'date',
																									dateFormat : 'Y-m-d H:i:s'
																								},
																								{
																									name : 'isExistErr',
																									type : 'string'

																								},
																								{
																									name : 'redoTime',
																									type : 'string'
																								} ],
																						remoteSort : false,
																						pageSize : 25,
																						proxy : {
																							type : 'ajax',
																							url : '/main/user/getOrderStatus',
																							method : 'GET',
																							extraParams : {
																								ActType:_actType,
																								ActName : _queryActName,
																								QueryDate : _queryDate,
																							},
																							reader : {
																								type : 'json',
																								root : 'content',
																								totalProperty : 'totalElements'
																							},
																							listeners : {
																								exception : Ext.ux.DataFactory.exception
																							}
																						},
																						listeners : {
																							beforeload : function(
																									store,
																									operation,
																									epts) {
																								// var
																								// formValues
																								// =
																								// form.getValues();
																								// Ext.apply(store.proxy.extraParams,
																								// formValues);
																							}
																						},

																						autoLoad : true
																					});
																			var _window = new Ext.Window(
																					{
																						title : '',
																						height : 750,
																						width : 1150,
																						items : [ {
																							xtype : 'grid',
																							border : false,
																							itemId : 'innerGrid',
																							columns : [
																									{
																										text : '订单编号',
																										dataIndex : 'orderCode',
																										align : 'left',
																										width : 150
																									},
																									{
																										text : '订单金额',
																										dataIndex : 'orderTotal',
																										align : 'right',
																										width : 120,
																										xtype : "numbercolumn",
																										format : '0.00'
																									},
																									{
																										text : '订单状态',
																										dataIndex : 'orderStatus',
																										align : 'left',
																										width : 80
																									},
																									{
																										text : '当日订单状态',
																										dataIndex : 'tdFinStatus',
																										align : 'left',
																										width : 100
																									},
																									{
																										text : '环节操作人',
																										dataIndex : 'assignee',
																										align : 'right',
																										width : 100
																									},
																									{
																										text : '环节名称',
																										dataIndex : 'actName',
																										align : 'left',
																										width : 100
																									},
																									{
																										text : '环节开始时间',
																										dataIndex : 'startTime',
																										align : 'center',
																										xtype : 'datecolumn',
																										format : 'Y-m-d H:i:s',
																										width : 150
																									},
																									{
																										text : '环节结束时间',
																										dataIndex : 'endTime',
																										align : 'center',
																										xtype : 'datecolumn',
																										format : 'Y-m-d H:i:s',
																										width : 150
																									},
																									{
																										text : '是否出错过',
																										dataIndex : 'isExistErr',
																										align : 'center',
																										width : 100
																									},
																									{
																										text : '重做次数',
																										dataIndex : 'redoTime',
																										align : 'right',
																										width : 80
																									}, ],
																							store : _store,
																							columnLines : true,
																							dockedItems : [ {
																								xtype : 'pagingtoolbar',
																								store : _store,
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
																												'render' : function(comboBox) {
																													var grid = comboBox.ownerCt.ownerCt.items.items[0];
																													comboBox.setValue(this.store.pageSize);
																												},
																												'select' : function(comboBox) {
																													var grid = comboBox.ownerCt.ownerCt.items.items[0];
																													grid.getStore().pageSize = comboBox.getValue();
																													grid.getStore().load(
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
																																		},
																																		{
																																			'id' : 1000,
																																			'name' : 1000
																																		},
																																		{
																																			'id' : 2000,
																																			'name' : 2000
																																		},
																																		{
																																			'id' : 4000,
																																			'name' : 4000
																																		} ]
																															}),
																											displayField : 'name',
																											valueField : 'id'
																										},
																										'条' ]
																							} ],

																						} ]
																					});
																			_window.show();
																		});
													}
												}
											},
											{
												itewmId : 'ErrorRate',
												style : 'border-top:3px solid #E7EBED;height:70px;',
												title : false,
												items : [
														{
															itemId : 'ErrorRateTitle',
															bodyStyle : 'width:100%;height:30px;border:0px;border-bottom:1px solid #F3F3F5;text-align:left;line-height:30px;padding-left:15px;color:rgb(103, 106, 108);font-weight:bold;',
															html : ' 当日错误'
														},
														{
															itemId : 'ErrorRateContent',
															height:70,
															bodyStyle : 'line-height: 70px;border:0px;padding-left: 40px;font-weight: bold;font-size: 30px;color: rgb(103, 106, 108);height:70px;',
															html : '0/0'
														} ],
														listeners : {
															render : function(p) {p.getEl().on('click',function(p) {
																					var _store = new Ext.data.Store(
																							{
																								fields : [
																										{
																											name : 'orderCode',
																											type : 'string'
																										},
																										{
																											name : 'orderTotal',
																											type : 'numbercolumn',
																											format : '0.00'
																										},
																										{
																											name : 'orderStatus',
																											type : 'string'
																										},
																										{
																											name : 'tdFinStatus',
																											type : 'string'
																										},
																										{
																											name : 'assignee',
																											type : 'string'
																										},
																										{
																											name : 'actName',
																											type : 'string'
																										},
																										{
																											name : 'startTime',
																											type : 'date',
																											dateFormat : 'Y-m-d H:i:s'
																										},
																										{
																											name : 'endTime',
																											type : 'date',
																											dateFormat : 'Y-m-d H:i:s'
																										},
																										{
																											name : 'isExistErr',
																											type : 'string'

																										},
																										{
																											name : 'redoTime',
																											type : 'string'
																										} ],
																								remoteSort : false,
																								pageSize : 25,
																								proxy : {
																									type : 'ajax',
																									url : '/main/user/getOrderStatus',
																									method : 'GET',
																									extraParams : {
																										ActName : _QueryActName,
																										QueryDate : _QueryDate,
																									},
																									reader : {
																										type : 'json',
																										root : 'content',
																										totalProperty : 'totalElements'
																									},
																									listeners : {
																										exception : Ext.ux.DataFactory.exception
																									}
																								},
																								listeners : {
																									beforeload : function(
																											store,
																											operation,
																											epts) {
																										// var
																										// formValues
																										// =
																										// form.getValues();
																										// Ext.apply(store.proxy.extraParams,
																										// formValues);
																									}
																								},

																								autoLoad : true
																							});
																					var _window = new Ext.Window(
																							{
																								title : '',
																								height : 750,
																								width : 1150,
																								items : [ {
																									xtype : 'grid',
																									border : false,
																									itemId : 'innerGrid',
																									columns : [
																											{
																												text : '订单编号',
																												dataIndex : 'orderCode',
																												align : 'left',
																												width : 150
																											},
																											{
																												text : '订单金额',
																												dataIndex : 'orderTotal',
																												align : 'right',
																												width : 120,
																												xtype : "numbercolumn",
																												format : '0.00'
																											},
																											{
																												text : '订单状态',
																												dataIndex : 'orderStatus',
																												align : 'left',
																												width : 80
																											},
																											{
																												text : '当日订单状态',
																												dataIndex : 'tdFinStatus',
																												align : 'left',
																												width : 100
																											},
																											{
																												text : '环节操作人',
																												dataIndex : 'assignee',
																												align : 'right',
																												width : 100
																											},
																											{
																												text : '环节名称',
																												dataIndex : 'actName',
																												align : 'left',
																												width : 100
																											},
																											{
																												text : '环节开始时间',
																												dataIndex : 'startTime',
																												align : 'center',
																												xtype : 'datecolumn',
																												format : 'Y-m-d H:i:s',
																												width : 150
																											},
																											{
																												text : '环节结束时间',
																												dataIndex : 'endTime',
																												align : 'center',
																												xtype : 'datecolumn',
																												format : 'Y-m-d H:i:s',
																												width : 150
																											},
																											{
																												text : '是否出错过',
																												dataIndex : 'isExistErr',
																												align : 'center',
																												width : 100
																											},
																											{
																												text : '重做次数',
																												dataIndex : 'redoTime',
																												align : 'right',
																												width : 80
																											}, ],
																									store : _store,
																									columnLines : true,
																									dockedItems : [ {
																										xtype : 'pagingtoolbar',
																										store : _store,
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
																														'render' : function(comboBox) {
																															var grid = comboBox.ownerCt.ownerCt.items.items[0];
																															comboBox.setValue(this.store.pageSize);
																														},
																														'select' : function(comboBox) {
																															var grid = comboBox.ownerCt.ownerCt.items.items[0];
																															grid.getStore().pageSize = comboBox.getValue();
																															grid.getStore().load(
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
																																				},
																																				{
																																					'id' : 1000,
																																					'name' : 1000
																																				},
																																				{
																																					'id' : 2000,
																																					'name' : 2000
																																				},
																																				{
																																					'id' : 4000,
																																					'name' : 4000
																																				} ]
																																	}),
																													displayField : 'name',
																													valueField : 'id'
																												},
																												'条' ]
																									} ],

																								} ]
																							});
																					_window.show();
																				});
															}
														}
													}
//											,
//											{
//												itemId : 'ExtRate',
//												title : false,
//												style : 'border-top:3px solid #E7EBED',
//												items : [
//														{
//															itemId : 'ExtRateTitle',
//															bodyStyle : 'width:100%;height:30px;border:0px;border-bottom:1px solid #F3F3F5;text-align:left;line-height:30px;padding-left:15px;color:rgb(103, 106, 108);font-weight:bold;',
//															html : '财务确认'
//														},
//														{
//															itemId : 'ExtRateContent',
//															bodyStyle : 'line-height: 70px;border:0px;padding-left: 40px;font-weight: bold;font-size: 30px;color: rgb(103, 106, 108);',
//															html : '0/0'
//														} ],
//											}
											]
								});

						var store1 = Ext.create('Ext.data.JsonStore', {
							fields : [ 'assignee', 'standardTotal', 'OR3Total',
									'OR4Total' ],
						// data : data
						});
						var store2 = Ext.create('Ext.data.JsonStore', {
							fields : [ 'assignee', 'priceTotal' ],
						// data : data2
						});
						var store3 = Ext.create('Ext.data.JsonStore', {
							fields : [ 'total', 'orderDay' ],
						// data : data3
						});
						/*
						 * 
						 */
						var RankList4Amount = new Ext.chart.Chart({
							animate : true,
							shadow : true,
							store : store1,
							itemId : 'RankList4AmountChart',
							legend : {
								position : 'bottom'
							},
							axes : [
									{
										type : 'Numeric',
										position : 'bottom',
										fields : [ 'standardTotal', 'OR3Total',
												'OR4Total' ],
										title : "当日标准订单单数前十排行",
										grid : true,
										label : {
											renderer : function(v) {
												if(v%1==0){
													return v;
												}else{
													return '';
												}
												
											}
										}
									}, {
										type : 'Category',
										position : 'left',
										fields : [ 'assignee' ],
										title : "操作员"
									} ],
							series : [ {
								type : 'bar',
								axis : 'bottom',
								gutter : 80,
								xField : 'assignee',
								yField : [ 'standardTotal', 'OR3Total',
										'OR4Total' ],
								stacked : true,
								tips : {
									trackMouse : true,
									width : 150,
									height : 20,
									renderer : function(storeItem, item) {
										this.setTitle(item.yField + ":"
												+ item.value[1]);
									}
								},
								label : {
									display : 'insideEnd',
									field : [ 'standardTotal', 'OR3Total',
											'OR4Total' ],
									color : '#FFF'
								}
							} ]
						});
						var RankList4Money = new Ext.chart.Chart({
							animate : true,
							shadow : true,
							store : store2,
							itemId : 'RankList4MoneyChart',
							legend : {
								position : 'bottom'
							},
							axes : [ {
								type : 'Numeric',
								position : 'bottom',
								fields : [ 'priceTotal' ],
								title : "当日效益前十排行",
								grid : true,
								label : {
									renderer : function(v) {
										if(v%100==0){
										return v;
										}else{
											return '';
										}
									}
								}
							}, {
								type : 'Category',
								position : 'left',
								fields : [ 'assignee' ],
								title : "操作员"
							} ],
							series : [ {
								type : 'bar',
								axis : 'bottom',
								gutter : 80,
								xField : 'assignee',
								yField : [ 'priceTotal' ],
								stacked : true,
								tips : {
									trackMouse : true,
									width : 100,
									height : 20,
									renderer : function(storeItem, item) {
										this.setTitle(storeItem.get('assignee')
												+ ":" + item.value[1]);
									}
								},
								label : {
									display : 'insideEnd',
									field : [ 'priceTotal' ],
									color : '#FFF'
								}
							} ]
						});

						var MonthSummary = Ext.create('Ext.chart.Chart', {
							animate : true,
							shadow : true,
							store : store3,
							itemId:'MonthSummaryChart',
							axes : [ {
								type : 'Numeric',
								position : 'left',
								fields : [ 'total' ],
								title : '当月统计单数'
							}, {
								type : 'Category',
								position : 'bottom',
								fields : [ 'orderDay' ],
								title : '当月统计日期'
							} ],
							series : [ {
								type : 'line',
								axis : 'left',
								gutter : 80,
								xField : 'orderDay',
								yField : [ 'total' ],
								tips : {
									trackMouse : true,
									width : 100,
									height : 20,
									renderer : function(klass, item) {
										this.setTitle(item.value[1]);
									}
								}
							} ]
						});

						var RanklistContainer = new Ext.Container(
								{
									itemId : 'RanklistContainer',
									xtype : 'basic-panels',
									style : 'width:inherit;margin:auto;margin-top:20px;padding-bottom:10px; ',
									requires : [ 'Ext.layout.container.Table' ],
									hidden:true,
									layout : {
										type : 'table',
										columns : 2,
										tdAttrs : {
											style : 'padding-right: 50px;'
										}
									},
									defaults : {
										xtype : 'panel',
										width : 550,
										height : 500,
										bodyPadding : 0,
										style : 'text-align:center;',
										border : false
									},
									items : [ RankList4Amount, RankList4Money ]
								});

						var SummaryContainer = new Ext.Container(
								{
									itemId : 'SummaryContainer',
									xtype : 'basic-panels',
									style : 'width:inherit;margin:auto;margin-top:10px;padding-bottom:10px; ',
									requires : [ 'Ext.layout.container.Table' ],
									hidden:true,
									layout : {
										type : 'table',
										columns : 1,
										tdAttrs : {
											style : 'padding-right: 230px;width:100%;'
										}
									},
									defaults : {
										xtype : 'panel',
										width:document.body.clientWidth*0.8,
										height : 400,
										bodyPadding : 0,
										style : 'text-align:center;',
										border : false
									},
									items : [ MonthSummary]
								});

						Ext.apply(this, {
							items : [ EfficiencyContainer, RanklistContainer,
									SummaryContainer ]
						});

						this.callParent(arguments);
					}

				});
var _queryDate;
var _queryActName;
var _actType;
function query() {
	if(Ext.ComponentQuery.query("button[itemId=queryBtn]")[0].isDisabled()){
		Ext.MessageBox.alert("提示","没有权限");
		return ;
	}
	var queryDate = Ext.ComponentQuery.query("datefield[itemId=queryDate]")[0]
			.getValue();
	if (Ext.isEmpty(queryDate)) {
		Ext.MessageBox.alert("提示", "请填写查询日期");
		return;
	}
	var _queryYear=queryDate.getFullYear();
	var _queryMonth=(queryDate.getMonth() + 1)>9?(''+(queryDate.getMonth() + 1)):'0'+(queryDate.getMonth() + 1);
	var _queryDay=queryDate.getDate()>9?''+queryDate.getDate():'0'+queryDate.getDate();
	_queryDate=_queryYear+"-"+_queryMonth+"-"+_queryDay;

	var _SubActName = Ext.ComponentQuery.query("dictcombobox[itemId=SubActId]")[0]
			.getRawValue();
	var _MainActName = Ext.ComponentQuery
			.query("dictcombobox[itemId=MainActId]")[0].getRawValue();
	
	_actType = Ext.ComponentQuery.query("combobox[itemId=ActType]")[0].getValue();

	if (Ext.isEmpty(_actType)) {
		Ext.MessageBox.alert("提示", "请选择流程类型");
		return;
	}
	if ('main' == _actType && Ext.isEmpty(_MainActName)) {
		Ext.MessageBox.alert("提示", "请选择主流程节点");
		return;
	} else if ('sub' == _actType && Ext.isEmpty(_SubActName)) {
		Ext.MessageBox.alert("提示", "请选择子流程节点");
		return;
	}

	_queryActName = ('main' == _actType) ? _MainActName : _SubActName;
	
	cleanAll();
	
	loadEfficiencyContainer(_actType, _queryActName, _queryDate);
	loadCharts(_actType,'order', _queryActName, _queryDate, 10);
	loadCharts(_actType,'price', _queryActName, _queryDate, 10);
	loadSummaryChart(_actType,_queryActName,_queryYear,_queryMonth);
	Ext.ComponentQuery.query("container[itemId=EfficiencyContainer]")[0].show();
	Ext.ComponentQuery.query("container[itemId=RanklistContainer]")[0].show();
	Ext.ComponentQuery.query("container[itemId=SummaryContainer]")[0].show();

}
function loadEfficiencyContainer(actType, actName, queryDate) {
	Ext.Ajax.request({
				url : '/main/user/getEfficiencyReport',
				method : 'GET',
				params : {
					ActType : actType,
					ActName : actName,
					QueryDate : queryDate
				},
				success : function(response) {
					var _data = Ext.decode(response.responseText).data;
					var _total = _data.total;
					var _error = _data.error;
					var _really = _data.really;
					if(!Ext.isEmpty(_data) && !Ext.isEmpty(_total) && !Ext.isEmpty(_error) && !Ext.isEmpty(_really)){
					Ext.ComponentQuery.query("[itemId=ComplateRateContent]")[0].body.update('<div style="height:100%;">'+_really + '/' + _total + '</div>');
					Ext.ComponentQuery.query("[itemId=ErrorRateContent]")[0].body.update( '<div style="height:100%;">'+_error + '/' + _really + '</div>');
					}
				}
			});
}
function checkUser(){
	Ext.Ajax.request({
		url:'/main/user/checkUser',
		method:'GET',
		success:function(response){
			var _data=Ext.decode(response.responseText).data;
			if('fail'==_data.result){
				Ext.ComponentQuery.query("button[itemId=queryBtn]")[0].disable();
			}
		}
	});
}

function loadCharts(actType,orderByType, actName, queryDate, chartLimit) {
	Ext.Ajax.request({
		url : '/main/user/getRankList',
		method : 'GET',
		params : {
			ActType:actType,
			OrderByType : orderByType,
			ActName : actName,
			QueryDate : queryDate,
			ChartLimit : chartLimit
		},
		success : function(response) {
			var _data = Ext.decode(response.responseText).content;
			if(!Ext.isEmpty(_data)){
			if ("order" == orderByType) {
				Ext.ComponentQuery.query("[itemId=RankList4AmountChart]")[0]
						.getStore().loadData(_data);
			}
			if ("price" == orderByType) {
				Ext.ComponentQuery.query("[itemId=RankList4MoneyChart]")[0]
						.getStore().loadData(_data);
			}
			}
		}
	});
}
function loadSummaryChart(actType,actName,year,month){
	Ext.Ajax.request({
		url:'/main/user/getActSummary',
		method:'GET',
		params:{
			ActType:actType,
			ActName:actName,
			Year:year,
			Month:month
		},
		success:function(response){
			var _data=Ext.decode(response.responseText).content;
			Ext.ComponentQuery.query("[itemId=MonthSummaryChart]")[0].getStore().loadData(_data);
		}
	});
}
function cleanAll(){
	Ext.ComponentQuery.query("[itemId=RankList4AmountChart]")[0].getStore().removeAll();
	Ext.ComponentQuery.query("[itemId=RankList4MoneyChart]")[0].getStore().removeAll();
	Ext.ComponentQuery.query("[itemId=MonthSummaryChart]")[0].getStore().removeAll();
	Ext.ComponentQuery.query("[itemId=ComplateRateContent]")[0].body.update('0/0');
	Ext.ComponentQuery.query("[itemId=ErrorRateContent]")[0].body.update('0/0');
}


/*
 * 查看绩效排行榜详细情况 var
 * _QueryDate=Ext.ComponentQuery.query("datefield[itemId=queryDate]")[0].getValue();
 * var
 * _SubActName=Ext.ComponentQuery.query("dictcombobox[itemId=SubActId]")[0].getRawValue();
 * var
 * _MainActName=Ext.ComponentQuery.query("dictcombobox[itemId=MainActId]")[0].getRawValue();
 * var
 * _ActType=Ext.ComponentQuery.query("combobox[itemId=ActType]")[0].getValue();
 * var _orderByType='order'; var _QueryActName; if("main"==_ActType){
 * _QueryActName=_MainActName; }else if("sub"==_ActType){
 * _QueryActName=_SubActName; }else{ Ext.MessageBox.alert('通知','请选择流程类型和环节');
 * return ; } var _store = new Ext.data.Store( { fields:[{ name : 'assignee',
 * type:'string' }, { name:'standardTotal', type:'string' }, { name :
 * 'OR3Total', type:'string' }, { name : 'OR4Total', type:'string' }, {
 * name:'priceTotal', type:'numbercolumn', format:'0.00' }], remoteSort:false,
 * pageSize:25, proxy:{ type:'ajax', url:'/main/user/getRankList', method :
 * 'GET', extraParams : { OrderByType:_orderByType, ActName:_QueryActName,
 * QueryDate:(_QueryDate.getFullYear()+"-"+(_QueryDate.getMonth()+1)+"-"+_QueryDate.getDate()), },
 * reader:{ type:'json', root:'content', totalProperty :'totalElements' },
 * listeners:{ exception:Ext.ux.DataFactory.exception } }, listeners: {
 * beforeload:function(store,operation,epts){ // var formValues =
 * form.getValues(); // Ext.apply(store.proxy.extraParams, formValues); } },
 * autoLoad:true }); var _window = new Ext.Window( { title: 'Hello', height:
 * 800, width: 600, items: [{ xtype: 'grid', border: false, itemId:'innerGrid',
 * columns: [{ text: '操作人', dataIndex: 'assignee',align:'left'}, { text: '标准订单',
 * dataIndex: 'standardTotal' ,align:'right'}, { text: 'OR3订单', dataIndex:
 * 'OR3Total',align:'right' }, { text: 'OR4订单', dataIndex:
 * 'OR4Total',align:'right'}, { text: '总额', dataIndex:
 * 'priceTotal',align:'right',xtype:'numbercolumn',format:'0.00' } ], store:
 * _store }] }); //_store.load();
 * console.log(Ext.ComponentQuery.query("grid[itemId=innerGrid]")[0].getStore());
 * _window.show();
 * 
 * 
 */

