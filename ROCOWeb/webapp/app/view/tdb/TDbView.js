Ext
		.define(
				'SMSWeb.view.tdb.TDbView',
				{
					extend : 'Ext.panel.Panel',
					alias : 'widget.tdbview',
					layout : 'border',
					border : false,
					requires : [ 'Ext.ux.form.SearchForm',
							"Ext.ux.form.TableComboBox",
							"Ext.ux.form.CustomCombobox",
							"Ext.ux.ButtonTransparent",
							"Ext.ux.form.DictCombobox",
							"Ext.ux.form.TrieCombobox" ],
					initComponent : function() {
						var me = this;

						var columns = [
								{
									xtype : 'actioncolumn',
									text : '操作',
									width : 40,
									align : 'center',
									items : [ {
										icon : '/resources/images/remarks1.png',
										handler : function(grid, rowIndex,
												colIndex) {
											me.createForm(grid, searchForm,
													grid.getStore().getAt(
															rowIndex));
										}
									} ]
								},
								{
									text : '单号',
									dataIndex : 'sn',
									align : 'left',
									width : 140,
									groupable : false,
									summaryType : 'count',
									summaryRenderer : function(value) {
										return Ext.String
												.format(
														'<font color=blue>共{0} 个任务</font>',
														value);
									}
								},
								{
									text : '是否样品',
									groupable : false,
									dataIndex : 'isYp',
									align : 'left',
									minWidth : 80,
									editor : Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'YES_NO'
											}),
									renderer : Ext.ux.DataFactory.getComboboxForColumnRenderer
								},
								{
									text : '订单类型',
									groupable : false,
									dataIndex : 'orderType',
									align : 'left',
									minWidth : 80,
									editor : Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'ORDER_TYPE'
											}),
									renderer : Ext.ux.DataFactory.getComboboxForColumnRenderer
								},
								{
									text : '销售分类',
									groupable : false,
									dataIndex : 'saleFor',
									align : 'left',
									minWidth : 80,
									editor : Ext.create(
											'Ext.ux.form.DictCombobox', {
												dict : 'SALE_FOR'
											}),
									renderer : Ext.ux.DataFactory.getComboboxForColumnRenderer
								}, {
									text : '订单日期',
									groupable : false,
									dataIndex : 'orderDate',
									xtype : 'datecolumn',
									format : 'Y-m-d'
								}, {
									text : '售达方',
									groupable : false,
									dataIndex : 'shouDaFang',
									align : 'left',
									minWidth : 80
								}, {
									text : '地区',
									dataIndex : 'regio',
									width : 75,
									align : 'left'
								}, {
									text : '售达方名称',
									groupable : false,
									dataIndex : 'name1',
									align : 'left',
									minWidth : 90
								}, {
									text : '任务名称',
									dataIndex : 'name',
									align : 'left',
									minWidth : 80
								}, {
									text : '受理人',
									dataIndex : 'assignee',
									width : 75,
									align : 'left'
								},{ 
									text:'加急指派类型',dataIndex:'urgentTypeDesc',align:'center',
									renderer:function(value,row,record){
										if (record.data.urgentType.indexOf('UR')!=-1){
											row.style="background-color:red;color:white;"
								}
										return value;
								}
									},
								{
									text : '创建时间',
									groupable : false,
									dataIndex : 'createTime',
									flex : 1,
									xtype : 'datecolumn',
									format : 'Y-m-d H:i:s',
									minWidth : 160
								}, {
									text : '领取时间',
									groupable : false,
									dataIndex : 'claimTime',
									flex : 1,
									xtype : 'datecolumn',
									format : 'Y-m-d H:i:s',
									minWidth : 160
								}, {
									text : '任务编号',
									groupable : false,
									dataIndex : 'id',
									align : 'left'
									//minWidth : 80
								}, {
									text : '任务分组',
									dataIndex : 'groupId',
									align : 'left'
									//minWidth : 80
								/*,editor:Ext.create('Ext.ux.form.TableComboBox',{
												table:'SYS_GROUP',
												datatext:'NAME',
												dataid:'ID'
											})
											renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer*/
								}];

						var fields = [ {
							name : 'sn'
						}, {
							name : 'orderType'
						}, {
							name : 'isYp'
						}, {
							name : 'orderDate',
							type : 'date',
							dateFormat : 'Y-m-d H:i:s'
						}, {
							name : 'shouDaFang'
						}, {
							name : 'name1'
						}, {
							name : 'id'
						}, {
							name : 'name'
						}, {
							name : 'groupId'
						},{
							name :'saleFor'
						}, {
							name : 'assignee'
						}, {
							name : 'regio'
						}, {
							name : 'createTime',
							type : 'date',
							dateFormat : 'Y-m-d H:i:s'
						}, {
							name : 'claimTime',
							type : 'date',
							dateFormat : 'Y-m-d H:i:s'
						}, {
							name:'urgentType'
						}, {
							name:'urgentTypeDesc'
						}];

						var dataUrl = "/core/bpm/taskDistribution/datalist";

						var store = Ext
								.create(
										"Ext.data.Store",
										{
											fields : fields,
											groupField : 'name',
											proxy : {
												type : 'ajax',
												url : dataUrl,
												headers : {
													"Content-Type" : "application/json; charset=utf-8"
												},
												reader : {
													type : 'json',
													root : 'content',
													idProperty : 'uuid',
													totalProperty : 'totalElements'
												},
												listeners : {
													exception : Ext.ux.DataFactory.exception
												}
											},
											autoLoad : true
										});
						var sm = Ext.create('Ext.selection.CheckboxModel');
						var grid = Ext.widget('grid', {
							xtype : 'grid',
							loadMask : true,
							plugins : 'bufferedrenderer',
							selModel : sm,
							columnLines : true,
							viewConfig : {
								enableTextSelection : true
							//可以复制单元格文字
							},
							features : [ {
								ftype : 'groupingsummary',
								groupByText : '按当前进行分组',
								showGroupsText : '显示分组',
								groupHeaderTpl : "{columnName}: {groupValue}"
							}, {
								ftype : 'summary',
								dock : 'bottom'
							} ],
							store : store,
							region : 'center',
							columns : columns
						});

						var searchForm = Ext.widget('searchform', {
							//xtype:'searchform',
							region : 'north',
							items : [ {
								flex : '1',
								layout : 'hbox',
								xtype : 'container',
								items : [ {
									fieldLabel : '订单编号',
									name : 'sn',
									xtype : 'textfield'
								}, {
									fieldLabel : '售达方',
									name : 'shouDaFang',
									xtype : 'textfield'
								}, {
									fieldLabel : '受理人',
									name : 'assignee',
									xtype : 'textfield'
								}, {
									fieldLabel : '任务分组',
									table : 'SYS_GROUP',
									datatext : 'NAME',
									dataid : 'ID',
									name : 'groupId',
									xtype : 'tablecombobox'
								}, {
									fieldLabel : '加急指派类型',
									name : 'urgentType',
									xtype : 'dictcombobox',
									dict : 'URGENT_TYPE'
								}]
							} ]
						});

						me.tbar = [
								{
									text : '查询',
									handler : function() {
										grid.getStore().load( {
											params : searchForm.getSearchs()
										});
									}
								},
								{
									text : '批量重排',
									handler : function() {
										if (grid.getSelectionModel()
												.getSelection().length > 0) {
											me.createForm2(grid, searchForm,
													grid.getSelectionModel()
															.getSelection());
										} else {
											Ext.Msg.alert('提示', '至少选中一条记录');
											return false;
										}
									}
								},{
									xtype : 'buttontransparent',
									text : '导出',
									id : 'export',
									icon:'/resources/images/down.png',
									handler:function(){
										Ext.MessageBox.confirm("温馨提示", "导出到Excel", function (btn) {
											if(btn=="yes"){
												ExportExcelByGrid(grid);
								}
										});
									}		
								},
								{
									hidden : true,
									text : '结束流程',
									handler : function() {
										if (grid.getSelectionModel()
												.getSelection().length > 0) {
											Ext.MessageBox
													.confirm(
															'提示信息',
															'确定要结束所选订单流程？',
															function(btn) {
																if (btn == 'yes') {
																	var taskId = "";
																	var ses = grid
																			.getSelectionModel()
																			.getSelection();
																	for ( var i = 0; i < ses.length; i++) {
																		var id = ses[i].data.id;
																		taskId = taskId
																				+ id
																				+ ","
																	}
																	Ext.Ajax
																			.request( {
																				url : '/core/bpm/endProcess/' + taskId,
																				async : false,
																				method : 'GET',
																				dataType : "json",
																				contentType : "application/json",
																				callback : function(
																						options,
																						success,
																						response) {
																					if (!success) {
																						Ext.Msg
																								.show( {
																									title : "错误代码:S-500",
																									icon : Ext.Msg.ERROR,
																									msg : "链接服务器失败，请稍后再试或联系管理员!",
																									buttons : Ext.Msg.OK
																								});
																					}
																				},
																				success : function(
																						response,
																						opts) {
																					var values = Ext
																							.decode(response.responseText);
																					Ext.MessageBox
																							.alert(
																									"提示信息",
																									values.data)
																					grid
																							.getStore()
																							.load(
																									{
																										params : searchForm
																												.getSearchs()
																									});
																					window
																							.close();
																				},
																				failure : function(
																						response,
																						opts) {
																					Ext.Msg
																							.alert(
																									"can't",
																									'error');
																				}
																			});
																} else {
																}
															});

										} else {
											Ext.Msg.alert('提示', '至少选中一条记录');
											return false;
										}
									}
								} ];

						me.items = [ searchForm, grid ];

						me.callParent(arguments);
					},

					createForm2 : function(grid, searchForm, selectedDatas) {
						var assignees = [];
						var taskModels = [];
						for ( var i = 0; i < selectedDatas.length; i++) {
							var dataModel = selectedDatas[i].data;
							assignees.push(dataModel.assignee);
							taskModels.push(dataModel);
						}
						var record = selectedDatas[0].data;
						var form = Ext
								.widget(
										'form',
										{
											bodyPadding : '5 5 0 5',
											fieldDefaults : {
												labelAlign : 'left',
												labelWidth : 90,
												anchor : '100%',
												flex : 1

											},
											items : [ {
												xtype : 'customcombobox',
												name : 'assignee',
												fieldLabel : '转交人',
												dataUrl : '/core/bpm/groupusers2/'
														+ record.groupId
														+ "?userId="
														+ assignees,
												dataFields : [ "id", "userName" ],
												displayField : 'userName'
											} ],
											buttons : [ {
												text : '确认',
												handler : function() {
													var form = this.up('form')
															.getForm();
													var assignee_ = form
															.findField(
																	"assignee")
															.getValue();
													Ext.Ajax
															.request( {
																url : '/core/bpm/taskDistribution/save2',
																async : false,
																jsonData : {
																	tasks : taskModels,
																	assignee : assignee_
																},
																method : 'POST',
																dataType : "json",
																contentType : "application/json",
																callback : function(
																		options,
																		success,
																		response) {
																	if (!success) {
																		Ext.Msg
																				.show( {
																					title : "错误代码:S-500",
																					icon : Ext.Msg.ERROR,
																					msg : "链接服务器失败，请稍后再试或联系管理员!",
																					buttons : Ext.Msg.OK
																				});
																	}
																},
																success : function(
																		response,
																		opts) {
																	grid
																			.getStore()
																			.load(
																					{
																						params : searchForm
																								.getSearchs()
																					});
																	window
																			.close();
																},
																failure : function(
																		response,
																		opts) {
																	Ext.Msg
																			.alert(
																					"can't",
																					'error');
																}
															});
												}
											} ]
										});

						var window = Ext.widget('window', {
							title : '任务重排',
							modal : true,
							items : [ form ]
						});

						window.show();
					},

					createForm : function(grid, searchForm, record) {
						var form = Ext
								.widget(
										'form',
										{
											bodyPadding : '5 5 0 5',
											fieldDefaults : {
												labelAlign : 'left',
												labelWidth : 90,
												anchor : '100%',
												flex : 1

											},
											items : [
													{
														fieldLabel : '订单编号',
														name : 'sn',
														xtype : 'textfield',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														fieldLabel : '订单类型',
														name : 'orderType',
														xtype : 'dictcombobox',
														dict : 'ORDER_TYPE',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														fieldLabel : '订单日期',
														name : 'orderDate',
														xtype : 'datefield',
														format : 'Y-m-d',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														name : 'id',
														xtype : 'textfield',
														fieldLabel : '任务编号',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														name : 'shouDaFang',
														xtype : 'textfield',
														fieldLabel : '售达方',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														name : 'name1',
														xtype : 'textfield',
														fieldLabel : '售达方名称',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														name : 'groupId',
														xtype : 'textfield',
														fieldLabel : '任务分组',
														fieldStyle : 'background:#E6E6E6',
														readOnly : true
													},
													{
														xtype : 'customcombobox',
														name : 'nextUser',
														fieldLabel : '转交人',
														value: record.data.assignee,
														dataUrl : '/core/bpm/groupusers/'
																+ record
																		.get('groupId')
																+ "?userId="
																+ record
																		.get('assignee'),
														dataFields : [ "id",
																"userName" ],
														displayField : 'userName'
													},
													{
														xtype:'dictcombobox',
														name:'urgentType',
														fieldLabel:'加急指派类型',
														dict:'URGENT_TYPE'
												}],
											buttons : [ {
												text : '确认',
												handler : function() {
													form
															.submit( {
																url : '/core/bpm/taskDistribution/save',
																success : function(
																		form,
																		action) {
																	grid
																			.getStore()
																			.load(
																					{
																						params : searchForm
																								.getSearchs()
																					});
																	window
																			.close();
																},
																failure : function(
																		formx,
																		action) {

																}
															});
												}
											} ]
										});

						form.loadRecord(record);

						var window = Ext.widget('window', {
							title : '任务重排',
							modal : true,
							items : [ form ]
						});

						window.show();
					}
				});
