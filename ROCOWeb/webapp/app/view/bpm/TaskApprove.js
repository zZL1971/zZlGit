Ext
		.define(
				'SMSWeb.view.bpm.TaskApprove',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.taskApprove',
					layout : 'border',
					id : 'taWin',
					minWidth : 1100,
					minHeight : 530,
					renderTo : Ext.getBody(),
					title : '审批',
					modal : true,
					resizable : false,
					v_assignees : null,
					v_taskIds : '',//任务号many
					uuidVal : '',//订单Id
					initComponent : function() {
						var me = this;

						me.width = document.body.clientWidth * 0.9;
						me.height = document.body.clientHeight * 0.9;

						var nextflow = Ext
								.create(
										'Ext.ux.form.UXCombobox',
										{
											name : 'nextflow',
											id : 'nextflow',
											fieldLabel : '下一环节',
											afterLabelTextTpl : requiredLabel,
											dataUrl : '/core/bpm/taskFlow',
											width : 250,
											labelAlign : 'right',
											allowBlank : false,
											typeAhead : false,
											editable : false,
											//readOnly:true,
											loadStatus : false,
											listeners : {
												change : function(ths,
														newValue, oldValue,
														eOpts) {
													if (newValue != undefined
															&& newValue
																	.indexOf('flow_rt_') != -1) {
														var record_ = ths
																.findRecord(
																		'id',
																		newValue);
														errType.getStore()
																.getProxy().extraParams = {
															codeType : record_.raw.target
														};
														errType.getStore()
																.load();

														errType.show(true);
														errType
																.setDisabled(false);
														errDesc.show(true);
														errDesc
																.setDisabled(false);
														desc.hide(true);
														desc.setDisabled(true);
														desc.setValue("");
													} else {
														errType.hide(true);
														errType
																.setDisabled(true);
														errDesc.hide(true);
														errDesc
																.setDisabled(true);

														errType.setValue("");
														errDesc.setValue("");

														desc.show(true);
														desc.setDisabled(false);
													}
												}
											}

										});
						//出错类型
						var errType = Ext.create('Ext.ux.form.DictCombobox', {
							name : 'errType',
							id : 'errType',
							fieldLabel : '出错类型',
							labelAlign : 'right',
							allowBlank : false,
							hidden : true,
							colspan : 2,
							//      afterLabelTextTpl: requiredLabel,
							width : 250,
							dict : 'ERROR_ORD_TYPE'
						});

						//原因描述
						var errDesc = Ext.widget('textareafield', {
							name : 'errDesc',
							id : 'errDesc',
							fieldLabel : '原因描述',
							labelAlign : 'right',
							width : 500,
							allowBlank : false,
							disabled : true,
							hidden : true,
							colspan : 3,
							height : 30,
							maxLength:40
						});
						var currentFlowId = Ext.widget('hiddenfield', {
							name : 'currentflow',
							fieldLabel : '当前环节编号'
						});
						var assignee = Ext.widget('hiddenfield', {
							name : 'assignee',
							fieldLabel : '当前环节编号'
						});
						var docStatus = Ext.widget('hiddenfield', {
							name : 'docStatus',
							fieldLabel : '当前环节编号'
						});
						var taskGroup = Ext.widget('hiddenfield', {
							name : 'taskGroup',
							fieldLabel : '当前环节编号'
						});

						var mappingId = Ext.widget('hiddenfield', {
							name : 'mappingId',
							fieldLabel : '关联表单编号',
							fieldBodyCls : 'textfield_display',
							readOnly : true,
							allowBlank : false
						});

						var mappingNo = Ext.widget('hiddenfield', {
							name : 'mappingNo',
							fieldLabel : '关联表单编号',
							fieldBodyCls : 'textfield_display',
							readOnly : true,
							allowBlank : false
						});
						var desc = Ext.widget('textareafield', {
							name : 'desc',
							id : 'desc',
							fieldLabel : '代办意见',
							labelAlign : 'right',
							width : 500,
							colspan : 3,
							allowBlank : false,
							height : 30
						});
						var idIndex = 0;
						var idVals, taskIdVals, assigneeVals;
						this.items = [ {
							region : 'west',
							minWidth : 300,
							width : 320,
							layout : 'border',
							border : false,

							items : [ {
								region : 'north',
								height : 180,
								bodyStyle : {
									padding : '5px',
									'font-weight' : 'bold'
								},
								xtype : 'form',
								id : 'ordForm',
								items : [ {
									xtype : 'textfield',
									name : 'custName',
									readOnly : true,
									id : 'custName',
									width : 300,
									fieldLabel : '客户',
									//                labelAlign:'right',
									value : '客户名称'
								}, {
									xtype : 'textfield',
									name : 'orderCode',
									id : 'orderCode',
									readOnly : true,
									width : 300,
									fieldLabel : '订单/SAP号',
									//                labelAlign:'right',
									value : '订单号'
								}, {
									xtype : 'hiddenfield',
									name : 'orderType',
									id : 'orderType',
									width : 300,
									fieldLabel : '订单类型编号',
									//                labelAlign:'right',
									value : '001'
								}, {
									xtype : 'hiddenfield',
									name : 'id',
									id : 'id',
									width : 300,
									fieldLabel : '订单id',
									//                labelAlign:'right',
									value : '90292948934903904'
								}, {
									xtype : 'textfield',
									name : 'orderTypeName',
									id : 'orderTypeName',
									readOnly : true,
									width : 300,
									fieldLabel : '订单类型',
									//                labelAlign:'right',
									value : '订单类型'
								}, {
									xtype : 'textfield',
									name : 'billName',
									id : 'billName',
									readOnly : true,
									width : 300,
									fieldLabel : '过账状态',
									//                labelAlign:'right',
									value : '过账状态'
								}, {
									xtype : 'textfield',
									name : 'fuFuanCondName',
									id : 'fuFuanCondName',
									readOnly : true,
									width : 300,
									//                        labelAlign:'right',
									fieldLabel : '付款类型',
									value : '付款类型'
								}, {
									xtype : 'textfield',
									name : 'fuFuanMoney',
									id : 'fuFuanMoney',
									readOnly : true,
									width : 300,
									//                        labelAlign:'right',
									fieldLabel : '订单付款总额',
									value : '订单付款总额'
								} ]
							}, {
								region : 'center',
								layout : 'fit',
								scroll : true,
								items : [ {
									xtype : 'uxgrid',
									id : 'flowHisGrid',
									headerModule : 'SYS_BPM_HI_APPROVE',
									domain : 'BPMHistoricTaskInstance',
									title : '流程历史记录 ',
									dataRoot : 'data',
									dataUrl : '/core/bpm/historic'
								} ]
							} ]
						},
								{
									region : 'center',
									xtype : 'panel',
									autoScroll : true,
									id : 'imsPanel'
								//                  html:'汇款凭证'
								},
								{
									region : 'south',
									height : 80,
									minHeight : 80,
									bodyStyle : {
										background : '#fffd',
										padding : '5px'
									},
									layout : {
										type : 'table',
										columns : 6
									},
									xtype : 'form',
									id : 'flowForm',
									items : [
											desc,
											errDesc,
											currentFlowId,
											mappingId,
											mappingNo

											,
											{
												xtype : 'button',
												width : 100,
												height : 30,
												text : '审批',
												id : 'taBtn',
												rowspan : 2,
												margin : '0 0 0 150',
												handler : function() {
													var ordForm = this.up()
															.up().down("form");
													var flowForm = Ext
															.getCmp('flowForm');//流程form
													var taWin = Ext
															.getCmp('taWin');//流程win
													if (flowForm.isValid()) {
														shenPiTask(ordForm,
																flowForm);
													}
													//                  console.info(flowForm.getValues());
												}
											},
											{
												xtype : 'button',
												width : 100,
												height : 30,
												rowspan : 2,
												text : '上一张单',
												margin : '0 0 0 50',
												handler : function() {
													if (idIndex == 0) {
														Ext.MessageBox.alert(
																"温馨提示",
																"这已经是第一张单！");
														return;
													}
													if (idIndex > 0) {
														idIndex -= 1;
													}
													doAction();

												}
											},
											{
												xtype : 'button',
												width : 100,
												height : 30,
												rowspan : 2,
												text : '下一张单',
												margin : '0 0 0 50',
												handler : function() {

													if ((idIndex + 1) == idVals.length) {
														//                    idIndex=idVals.length-1;
														Ext.MessageBox.alert(
																"温馨提示",
																"已经是最后一张单！");
														return;
													} else {
														idIndex += 1;
													}
													doAction();

												}
											}, nextflow, errType, docStatus,
											assignee, taskGroup ]
								} ];

						this.listeners = {
							show : function() {
								doAction();
							}
						};

						function findSaleById(idVal, ordForm) {
							Ext.Ajax
									.request( {
										url : '/main/sale/findSaleById?id=' + idVal,
										async : false,
										dataType : "json",
										success : function(response, opts) {
											//订单信息
										var data = Ext
												.decode(response.responseText).data;
										ordForm
												.getChildByElement("custName")
												.setValue(
														data.SHOU_DA_FANG
																+ "  "
																+ data.CUST_NAME);
										ordForm.getChildByElement("orderCode")
												.setValue(data.ORDER_SAP_CODE);
										ordForm.getChildByElement(
												"orderTypeName").setValue(
												data.ORDER_TYPE_NAME);
										ordForm.getChildByElement(
												"fuFuanCondName").setValue(
												data.FU_FUAN_COND_NAME);
										ordForm
												.getChildByElement(
														"fuFuanMoney")
												.setValue(data.FU_FUAN_MONEY.toFixed(3));
										ordForm.getChildByElement("billName")
												.setValue(data.BILL_NAME);
										ordForm.getChildByElement("id")
												.setValue(data.ID);
										ordForm.getChildByElement("orderType")
												.setValue(data.ORDER_TYPE);
										mappingId.setValue(data.ID);
										mappingNo.setValue(data.ORDER_CODE);
									},
									failure : function(response, opts) {
										Ext.Msg.alert("can't", 'error');
									}
									});
						}

						function showImg(uuId, panel) {
							var isShow = "N";
							var datas;
							panel.removeAll();
							Ext.Ajax
									.request( {
										url : '/main/sysFile/querySysFile?_dc=1460440399215&foreignId=' + uuId + '&fileType=&page=1&start=0&limit=25',
										dataType : "json",
										async : false,
										success : function(response, opts) {
											var cls = Ext
													.decode(response.responseText);
											if (cls.length > 0) {
												isShow = "Y";
												datas = cls;
											} else {
												Ext.Msg.show( {
													title : "温馨提示",
													icon : Ext.Msg.ERROR,
													msg : "该单还没有上传汇款凭证！",
													buttons : Ext.Msg.OK
												});
											}

										},
										failure : function(response, opts) {
											Ext.Msg.alert("can't", 'error');
										}
									});
							if (isShow == "Y") {
								var adds = new Array();
								for ( var i = 0; i < datas.length; i++) {
									var o = {
										width : '100%',
										height : '100%',
										xtype : 'uxiframe',
										src : '/core/ext/base/pic/' + datas[i].id
									};
									adds.push(o);
								}

								panel.add(adds);
							}
						}

						function loadNextTask(uuidVal, flowForm) {
							Ext.Ajax
									.request( {
										url : '/core/bpm/startedFlow',
										async : false,
										params : {
											uuid : uuidVal
										},
										method : 'GET',
										success : function(response, opts) {
											var jsonResult = Ext
													.decode(response.responseText);
											if (jsonResult.success) {
												if (jsonResult.data.docStatus == 1) {
													console
															.info(jsonResult.data);
													nextflow
															.getStore()
															.load(
																	{
																		params : {
																			id : jsonResult.data.taskId
																		}
																	});
													var flowGrid = Ext
															.getCmp("flowHisGrid");
													flowGrid
															.getStore()
															.load(
																	{
																		params : {
																			id : jsonResult.data.procinstid
																		}
																	});
													currentFlowId
															.setValue(jsonResult.data.taskId);
													docStatus
															.setValue(jsonResult.data.docStatus);
													assignee
															.setValue(jsonResult.data.assignee);
													taskGroup
															.setValue(jsonResult.data.taskGroup);
													jyBtn(
															jsonResult.data.docStatus,
															jsonResult.data.taskGroup);
													//              console.info(currentFlowId.getValue());
												}

											} else {
												Ext.Msg
														.show( {
															title : "错误提示["
																	+ jsonResult.errorCode
																	+ "]:",
															icon : Ext.Msg.ERROR,
															msg : jsonResult.errorMsg,
															buttons : Ext.Msg.OK
														});
											}
										},
										failure : function(response, opts) {
											Ext.Msg.alert("错误代码:"
													+ response.status,
													response.responseText);
										}
									});
						}

						function jyBtn(docStatus, taskGroup) {
							var btn = Ext.getCmp("taBtn");
							if ("2" == docStatus || "gp_finance" != taskGroup) {
								nextflow.setDisabled(true);
								errType.setDisabled(true);
								errDesc.setDisabled(true);
								desc.setDisabled(true);
								btn.setDisabled(true);
							} else {
								nextflow.setDisabled(false);
								errType.setDisabled(false);
								errDesc.setDisabled(false);
								desc.setDisabled(false);
								btn.setDisabled(false);
							}
						}
						function shenPiTask(ordForm, flowForm) {
							var orderType = ordForm.getChildByElement(
									"orderType").getValue();
							var _saleId = ordForm.getChildByElement("id")
									.getValue();
							var orderCode = ordForm.getChildByElement(
									"orderCode").getValue();
							var bpm = "MainProductQuotation";
							if ("buDan" == orderType || "OR3" == orderType
									|| "OR4" == orderType) {
								bpm = "NewCustomerServiceOrdProcess";
							}
							var flowType = null;
							if (nextflow.getValue() != undefined
									&& nextflow.getValue().indexOf('flow_rt_') != -1) {
								flowType = 0;
							} else {
								flowType = 1;
							}
							//提交前
							var flag = true;
							if ("1" == docStatus.getValue()
									&& assignee.getValue() == "true") {
								if ("gp_finance" == taskGroup.getValue()) {//当前环节是财务确认
									//flowtype=1是提交，flowtype=0是退回
									var _values;
									Ext.Ajax
											.request( {
												url : 'main/sale/validateTranSap',
												method : 'GET',
												params : {
													'saleHeadId' : _saleId,
													'flowtype' : flowType
												},
												async : false,
												dataType : "json",
												contentType : 'application/json',
												success : function(response,
														opts) {
													_values = Ext
															.decode(response.responseText);
												},
												failure : function(response,
														opts) {
													Ext.MessageBox.alert("提示",
															"加载数据失败！");
												}
											});
									//财务确认过后只是隐藏任务,但是不会commitFlow add by mark on 2017-04-18
									if (!_values.success) {
										flag = false;
										Ext.MessageBox.alert("提示",
												_values.errorMsg);
									} else if (flowType == 1) {
										flag = false;
										Ext.MessageBox.alert("提示", "加入队列成功");
									} else {
										flag = true;
									}
									//        if(false == _values.success){
									//          flag = false;
									//          Ext.MessageBox.alert("提示",_values.errorMsg);
									//        }
								}
							}
							if (flag) {
								//          * @param currentflow 当前环节编号
								//          * @param nextflow 下一环节
								//          * @param mappingId 关联表单id
								//          * @param mappingNo 关联表单编号 
								//          * @param desc 代办意见
								//          * @param errType 出错类型
								//          * @param errDesc 原因描述
								//提交流程
								Ext.Ajax
										.request( {
											url : '/core/bpm/commitFlow',
											async : false,
											params : {
												currentflow : currentFlowId
														.getValue(),
												nextflow : nextflow.getValue(),
												mappingId : mappingId
														.getValue(),
												mappingNo : mappingNo
														.getValue(),
												desc : desc.getValue(),
												errType : errType.getValue(),
												errDesc : errDesc.getValue()
											},
											method : 'POST',
											success : function(response, opts) {
												var jsonResult = Ext
														.decode(response.responseText);
												if (jsonResult.success) {
													Ext.MessageBox.alert(
															"温馨提示", "审批成功！");
													loadNextTask(_saleId,
															flowForm);//更新节点
													//刷新列表的数据
													var a_ = Ext.ComponentQuery
															.query('uxgrid[itemId=flowTaskGrid]');
													if (a_.length > 0) {
														a_[0].getStore()
																.reload();
													} else {
														Ext
																.getCmp(
																		"finance_grid_id")
																.getStore()
																.reload();
													}

												} else {
													Ext.Msg
															.show( {
																title : "错误提示["
																		+ jsonResult.errorCode
																		+ "]:",
																icon : Ext.Msg.ERROR,
																msg : jsonResult.errorMsg,
																buttons : Ext.Msg.OK
															});
												}

											},
											failure : function(response, opts) {
												var jsonResult = Ext
														.decode(response.responseText);
												if (!jsonResult.success) {
													Ext.Msg
															.show( {
																title : "错误提示["
																		+ jsonResult.errorCode
																		+ "]:",
																icon : Ext.Msg.ERROR,
																msg : jsonResult.errorMsg,
																buttons : Ext.Msg.OK
															});
												}
											}
										});
							}
						}
						function doAction() {
							var ids = me.uuidVal;
							var taskIds = me.v_taskIds;
							var assignees = me.v_assignees;
							if (!idVals) {
								idVals = ids.split(",");
								taskIdVals = taskIds.split(",");
								assigneeVals = assignees.split(",");
							}
							var id = idVals[idIndex];
							var taskId = taskIdVals[idIndex];
							var ag = assigneeVals[idIndex];
							//        console.info(id);
							var imsgPanel = Ext.getCmp("imsPanel");
							var ordForm = Ext.getCmp("ordForm");//订单form
							var flowForm = Ext.getCmp("flowForm");//流程form
							findSaleById(id, ordForm);
							showImg(id, imsgPanel);
							if (ag == "X") {//未领取
								lqrw(taskId, id, flowForm);
							} else {
								loadNextTask(id, flowForm);
							}

						}
						//领取任务
						function lqrw(taskId, id, flowForm) {
							var fla = "N";
							//领取任务
							Ext.MessageBox
									.confirm("提示",
											"确定受理该任务吗？",
											function(btn) {
												//Ext.Msg.alert("提示", "你点击了" + btn + "按钮");
											if (btn == "yes") {
												Ext.Ajax
														.request( {
															url : '/core/bpm/claim',
															params : {
																taskId : taskId
															},
															async : false,
															dataType : "json",
															success : function(
																	response,
																	opts) {
																var message = Ext
																		.decode(response.responseText);
																if (message.success) {
																	fla = "Y";
																	loadNextTask(
																			id,
																			flowForm);
																} else {
																	Ext.Msg
																			.show( {
																				title : "错误代码:"
																						+ message.errorCode,
																				icon : Ext.Msg.ERROR,
																				msg : message.errorMsg,
																				buttons : Ext.Msg.OK
																			});
																}
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
										});
							return fla;
						}
						this.callParent(arguments);
					}

				});
