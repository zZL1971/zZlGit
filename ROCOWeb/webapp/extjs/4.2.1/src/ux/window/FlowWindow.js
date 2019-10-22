Ext.define('Ext.ux.window.FlowWindow', {
	extend : 'Ext.window.Window',
	title : '流程引擎-BPM1.0',
	layout:'border',
	modal : true,
	width:900,
	height:500,
	bpm:'MainProductQuotation',
	itemId:null,
	uuid:null,
	uuids:null,
	uuidVal:null,
	uuidNo:null,
	uuidNos:null,
	uuidNoVal:null,
	subprocess:false,
	requires :["Ext.ux.form.UXCombobox","Ext.ux.ButtonTransparent","Ext.ux.IFrame"],
	listeners:{
		boforeActivate:function(form){
			return true;
		},
		afterActivate:function(){
			return true;
		},
		boforeCommit:function(form,flowtype,nextflowId){
			return true;
		},
		afterCommit:function(){
			return true;
		}
	},
	initComponent : function() {
		var me = this;
		//tab2 显示流程历史记录grid
		var flowGrid = Ext.create('Ext.ux.grid.UXGrid',{
			headerModule:'SYS_BPM_HI',
			domain:'BPMHistoricTaskInstance',
			title:'历史记录',
			dataRoot:'data',
			dataUrl:'/core/bpm/historic'
		});
		var currentFlow = Ext.widget('displayfield',{
			name:'flowStatusDly',
			fieldLabel:'当前环节'
		});
		//出错类型
		var errType = Ext.create('Ext.ux.form.DictCombobox',{
			fieldLabel:'出错类型',
			name:'errType',
			width:300,
			editable:false,
			hidden:true,
			disabled:true,
			showDisabled:false,
			allowBlank:false,
			afterLabelTextTpl: requiredLabel,
			dict:'ERROR_ORD_TYPE'
		});


//		//出错原因
//		var errRea = Ext.create('Ext.ux.form.DictCombobox',{
//			fieldLabel:'出错原因',
//			name:'errRea',
//			width:300,
//			editable:false,
//			hidden:true,
//			disabled:true,
//			showDisabled:false,
//			allowBlank:false,
//			afterLabelTextTpl: requiredLabel,
//			dict:'ERROR_ORD_REA'
//		});

		//原因描述
		var errDesc = Ext.widget('textareafield',{
			name:'errDesc',
			grow: true,
			anchor:'100%',
			allowBlank:false,
			disabled:true,
			hidden:true,
			afterLabelTextTpl: requiredLabel,
			fieldLabel:'原因描述'
		});
		//add by Mark for expired --20160611 start
		var creditField=Ext.create('Ext.form.field.Display',{
			fieldLabel:'信贷额度',
			name:'credit',
			hidden:true
		});
		var unProduction=Ext.create('Ext.form.field.Display',{
			fieldLabel:'正在财务确认',
			name:'unProduction',
			hidden:true
		});
		//add by Mark for expired --20160611 end
		//tab3显示流程历史记录treegrid
		/*var flowTreeGrid = Ext.create('Ext.ux.grid.UXTreeGrid',{
			header:'SYS_BPM_HI_TG',
			domain:'BPMHistoricTaskInstance',
			title:'历史记录TreeGrid',
			dataUrl:'/core/bpm/tree/historic'
		});*/

		/*var flowGraphics = Ext.create("Ext.ux.IFrame",{
          	title:"流程图",
          	closable: true,
          	layout: 'fit',
          	loadMask: 'Loading Business Components...',
          	border: false
		});*/

		var flowImage = Ext.widget('image',{
		});

		var flowGraphics = Ext.widget('panel',{
			autoScroll:true,
			title:"流程步骤",
			items:[flowImage]
		});

		/*var flowGraphics = Ext.widget('image',{
			title:"流程图",
			autoScroll:true,
			src:'/process/graphics?definitionId=mainProductQuotation:1:4'
		});*/
		var flowHole = Ext.widget('textareafield',{
			width:700,
			height:200,
			readOnly:true,
			hidden:true,
			fieldLabel:'孔位出错信息'

		});
		/*var flowGraphics = Ext.create("Ext.ux.IFrame", {
              title:"流程图",
              iconCls: '',
              layout: 'fit',
              loadMask: 'Loading Business Components...',
              border: false//,
		});*/

		/*var flowGraphics = Ext.widget("panel",{
          	title:"流程图",
          	border: false
		});*/

		//tab1 流程下一个环节操作（当前流程定义节点ID、当前流程实例ID、当前流程关联表单ID、当前环节、下一个环节、代办意见）
		var nextflow = Ext.create('Ext.ux.form.UXCombobox',{
			name:'nextflow',
			fieldLabel:'下一环节',
			afterLabelTextTpl: requiredLabel,
			dataUrl:'/core/bpm/taskFlow',
			width:300,
			allowBlank:false,
			typeAhead:false,
			editable:false,
			//readOnly:true,
			loadStatus:false,
			listeners:{
				change:function( ths, newValue, oldValue, eOpts){
					if(newValue!=undefined && newValue.indexOf('flow_rt_')!=-1){
						tackit.setValue("1");
						if(errType.hide){
							debugger;
							var record_ = ths.findRecord('id',newValue);
							var codeType=currentFlow.value+"##"+record_.raw.target;
							errType.getStore().getProxy().extraParams={codeType:codeType};
							errType.getStore().load();
//							errRea.getStore().getProxy().extraParams={codeType:record_.raw.target};
//							errRea.getStore().load();
							errType.show(true);
							errType.setDisabled(false);
//							errRea.show(true);
//							errRea.setDisabled(false);
							errDesc.show(true);
							errDesc.setDisabled(false);
							desc.hide(true);
							desc.setDisabled(true);
							desc.setValue("");
						}
					}else{
						tackit.setValue("0");
						if(tackit.getValue()!=undefined&&tackit.getValue()==1){
						}else{
							if(!errType.hidden){
								errType.hide(true);
								errType.setDisabled(true);
//								errRea.hide(true);
//								errRea.setDisabled(true);
								errDesc.hide(true);
								errDesc.setDisabled(true);
								errType.setValue("");
								errDesc.setValue("");
								desc.show(true);
								desc.setDisabled(false);
							}
						}
					}
				}
			}

		});
		var tackit=Ext.create('Ext.ux.form.DictCombobox',{
			name:'tackit',
			fieldLabel:'开罚单',
			width:200,
			hidden:true,
			dict:'YES_NO',
			disabled:false,
			menuDisabled:true,
			emptyText: '',
			listeners:{
				change:function( ths, newValue, oldValue, eOpts){
						if(newValue==1){
							if(errType.hide){
								debugger;
								var record_ = ths.findRecord('id',newValue);
								var codeType=currentFlow.value+"##"+record_.raw.target;
								errType.getStore().getProxy().extraParams={codeType:codeType};
								errType.getStore().load();
//								errRea.getStore().getProxy().extraParams={codeType:record_.raw.target};
//								errRea.getStore().load();
								errType.show(true);
								errType.setDisabled(false);
//								errRea.show(true);
//								errRea.setDisabled(false);
								errDesc.show(true);
								errDesc.setDisabled(false);
								desc.hide(true);
								desc.setDisabled(true);
								desc.setValue(" ");
							}
						}else{
							if(nextflow.getValue()!=undefined &&nextflow.getValue().indexOf('flow_rt_')!=-1){
							}else{
								if(!errType.hidden){
									errType.hide(true);
									errType.setDisabled(true);
//									errRea.hide(true);
//									errRea.setDisabled(true);
									errDesc.hide(true);
									errDesc.setDisabled(true);
									errType.setValue(" ");
									errDesc.setValue(" ");
									desc.show(true);
									desc.setDisabled(false);
								}
							}
						}
				}
			}
//		renderer: function(value,metadata,record){
//		var find=this.findRecord('id',value,true,false,false);
//		if(find){
//		return find.get('text');
//		}else{
//		return value;
//		} 
//		}
		});
		/*var currentFlow = Ext.create('Ext.ux.form.UXCombobox',{
			name:'flowStatus',
			fieldLabel:'当前环节',
			dataUrl:'/core/bpm/currentFlow',
			loadStatus:false,
			listeners:{
				change:function(me,newVal,oldVal,epts){
					flowStatus.getStore().load({params:{id:newVal}});
				}
			}
		});

		var processDefinition = Ext.create('Ext.ux.form.UXCombobox',{
			name:'processDefinitionId',
			fieldLabel:'流程实例编号',
			dataUrl:'/core/bpm/started',
			loadStatus:false,
			listeners:{
				change:function(me,newVal,oldVal,epts){
					currentFlow.getStore().load({params:{id:newVal}});
					flowGrid.getStore().load({params:{id:newVal}});
					//flowTreeGrid.getStore().load({params:{node:newVal}});
					//alert(definition.getValue())
					//flowGraphics.load("/core/bpm/jumpGraphics?definitionId="+definition.getValue()+"&processInstanceId="+newVal);
				}
			}
		});

		var definition = Ext.create('Ext.ux.form.UXCombobox',{
			name:'definitionId',
			fieldLabel:'流程定义编号',
			dataUrl:'/core/bpm/deployed/'+me.bpm,
			listeners:{
				change:function(me,newVal,oldVal,epts){
					processDefinition.getStore().load({params:{id:newVal}});
					//flowGraphics.load("/core/bpm/graphics?definitionId="+newVal);
				}
			}
		});*/

		/*var definition = Ext.widget('textfield',{
			name:'definitionId',
			fieldLabel:'流程定义编号'
		});
		var processDefinition = Ext.widget('textfield',{
			name:'processDefinitionId',
			fieldLabel:'流程实例编号'
		});*/
		var currentFlowId = Ext.widget('hiddenfield',{
			name:'currentflow',
			fieldLabel:'当前环节编号'
		});
		

		var mappingId = Ext.widget('hiddenfield',{
			name:'mappingId',
			fieldLabel:'关联表单编号',
			fieldBodyCls:'textfield_display',
			readOnly : true,
			allowBlank:false
		});

		var mappingNo = Ext.widget('hiddenfield',{
			name:'mappingNo',
			fieldLabel:'关联表单编号',
			fieldBodyCls:'textfield_display',
			readOnly : true,
			allowBlank:false
		});

		var mappingIdDly = Ext.widget('displayfield',{
			name:'mappingIdDly',
			afterLabelTextTpl: requiredLabel,
			fieldLabel:'关联表单UUID'
		});

		var mappingNoDly = Ext.widget('displayfield',{
			name:'mappingNoDly',
			afterLabelTextTpl: requiredLabel,
			fieldLabel:'关联表单编号'
		});

		var desc = Ext.widget('textareafield',{
			name:'desc',
			grow: true,
			anchor:'100%',
			allowBlank:false,
			afterLabelTextTpl: requiredLabel,
			fieldLabel:'代办意见'
		});

		var startFlow = Ext.widget('button',{
			text : '激活流程',
			iconCls : 'flow_add',
			xtype:'buttontransparent',
			//glyph:0xf085,
			hidden:me.subprocess,
			listeners:{
				click:function(){
					var boforeActivate = me.fireEvent("boforeActivate");
					if(boforeActivate){
						var uuidVal = me.uuidVal!=null?me.uuidVal:Ext.ComponentQuery.query(/*"textfield[name=keyVal]"*/me.uuid)[0].getValue();
						var uuidNoVal = me.uuidNoVal!=null?me.uuidNoVal:Ext.ComponentQuery.query(/*"textfield[name=keyVal]"*/me.uuidNo)[0].getValue();
						mappingId.setValue(uuidVal);
						mappingIdDly.setValue(uuidVal);

						mappingNo.setValue(uuidNoVal);
						mappingNoDly.setValue(uuidNoVal);
						if(uuidVal){

							Ext.Ajax.request({
								url : '/core/bpm/startFlow',
								params : {
									bpm:me.bpm,
									uuid:uuidVal,
									uuidNo:uuidNoVal
								},
								method : 'GET',
								success : function(response, opts) {
									var jsonResult = Ext.decode(response.responseText);
									if(jsonResult.success){
										currentFlowId.setValue(jsonResult.data.taskId);
										currentFlow.setValue(jsonResult.data.taskName);
										nextflow.getStore().load({params:{id:jsonResult.data.taskId}});
										flowGrid.getStore().load({params:{id:jsonResult.data.procinstid}});
										flowImage.setSrc("/core/bpm/graphics?definitionId="+jsonResult.data.prodefid);
										startFlow.hide(true);
										Ext.Msg.show({
											title:"操作提示:",
											icon:Ext.Msg.INFO,
											msg:"操作成功!",
											buttons:Ext.Msg.OK
										});

										var afterActivate = me.fireEvent("afterActivate");

									}else{
										Ext.Msg.show({
											title:"错误提示["+jsonResult.errorCode+"]:",
											icon:Ext.Msg.ERROR,
											msg:jsonResult.errorMsg,
											buttons:Ext.Msg.OK
										});
									}
								},
								failure : function(response, opts) {
									Ext.Msg.alert("错误代码:"+response.status,response.responseText);
								}
							});
						}else{
							Ext.Msg.show({
								title:"错误提示:",
								icon:Ext.Msg.ERROR,
								msg:"关联表单不存在，不能发起审批",
								buttons:Ext.Msg.OK
							});
						}
					}

				}
			}
		});

		var endFlow = Ext.widget('buttontransparent',{
			iconCls : 'flow_cancel',
			text : '提前结束流程(超级权限)',
			hidden:true,
			handler : function() {

				Ext.Ajax.request({
					url:'/core/bpm/endProcess/'+currentFlowId.getValue(),
					async:false,
					dataType: "json",
					success:function(response,opts){
						var jsonResult = Ext.decode(response.responseText);
						if(jsonResult.success){
							Ext.Msg.show({
								title:"操作提示:",
								icon:Ext.Msg.INFO,
								msg:"操作成功!",
								buttons:Ext.Msg.OK
							});
							var afterCommit = me.fireEvent("afterCommit");
							if(afterCommit){
								me.close();
							}
						}else{
							Ext.Msg.show({
								title:"错误提示["+jsonResult.errorCode+"]:",
								icon:Ext.Msg.ERROR,
								msg:jsonResult.errorMsg,
								buttons:Ext.Msg.OK
							});
						}
					},
					failure:function(response,opts){
						Ext.Msg.alert("CAN'T","ERROR");
					}
				});				
			}
		});

		var tabIndex = 0;
		if(me.uuids!=null){
			var uuidDefaultVal = me.uuids;
			var uuidNoDefaultVal = me.uuidNos;
		}else{
			var uuidDefaultVal = me.uuidVal!=null?me.uuidVal:Ext.ComponentQuery.query(me.uuid)[0].getValue();
			var uuidNoDefaultVal = me.uuidNoVal!=null?me.uuidNoVal:Ext.ComponentQuery.query(/*"textfield[name=keyVal]"*/me.uuidNo)[0].getValue();
		}
		if(uuidDefaultVal){
			mappingId.setValue(uuidDefaultVal);
			mappingIdDly.setValue(uuidDefaultVal);

			mappingNo.setValue(uuidNoDefaultVal);
			mappingNoDly.setValue(uuidNoDefaultVal);
			//初始化所有的值
			Ext.Ajax.request({
				url : '/core/bpm/startedFlow',
				params : {
					uuid:uuidDefaultVal
				},
				method : 'GET',
				async:false,
				success : function(response, opts) {
					var jsonResult = Ext.decode(response.responseText);
					console.log(jsonResult);
					if(jsonResult.success){
						if(jsonResult.data.docStatus==1){
							currentFlowId.setValue(jsonResult.data.taskId);
							currentFlow.setValue(jsonResult.data.taskName);
							flowHole.setValue(jsonResult.data.holemessage);
							if (jsonResult.data.taskGroup=="gp_hole_examine_error") {
								flowHole.hidden=false;
							}
							if(jsonResult.data.taskGroup=="gp_drawing"&&jsonResult.data.tackit=="1"){
								tackit.hidden=false;
								tackit.emptyText='是';
								tackit.disabled=true;
							}
							if (jsonResult.data.taskGroup=="gp_material"||jsonResult.data.taskGroup=="gp_drawing_2020"||jsonResult.data.taskGroup=="gp_drawing_imos"||
									jsonResult.data.taskGroup=="gp_shiftcount"||jsonResult.data.taskGroup=="gp_hole_examine_error"||
									jsonResult.data.taskGroup=="gp_hole_examine") {
								tackit.hidden=false;
								if(jsonResult.data.tackit=="1"){
									tackit.hidden=true;
								}
							}
							nextflow.getStore().load({params:{id:jsonResult.data.taskId}});
							//如果当前任务为未审核状态，且为确认报价，那么客户自己可以查看信贷信息 add by mark 20161202 --start
							if(jsonResult.data.assignee && jsonResult.data.taskGroup=="gp_store_customer"){
								creditField.show();
								unProduction.show();
								Ext.Ajax.request({
									url:'/main/sale/getCustHeaderBySaleId',
									params:{
										uuid:uuidDefaultVal
									},
									async:true,
									success:function(response,opts){
										var data=Ext.decode(response.responseText).data;
										creditField.setValue(data.credit);
										unProduction.setValue(data.unProduction);
									}
								});
							}
							//如果当前任务为未审核状态，且为确认报价，那么客户自己可以查看信贷信息 add by mark 20161202 --end
							//flowForm.tab.show(true);
						}

						if(jsonResult.data.docStatus==1 || jsonResult.data.docStatus==2){
							flowImage.setSrc("/core/bpm/graphics?definitionId="+jsonResult.data.prodefid);
							//设置按钮
							startFlow.hide(true);
							//endFlow.show(true);
						}

						if(jsonResult.data.docStatus>0){
							flowGrid.getStore().load({params:{id:jsonResult.data.procinstid}});
						}

						if(jsonResult.data.docStatus==2){
							tabIndex = 1;

						}
						//flowGraphics.load("http://www.baidu.com");
						//flowGraphics.load("/core/bpm/graphics?definitionId="+jsonResult.data.prodefid);
						//flowGraphics.load("http://127.0.0.1:8080/process/graphics?definitionId=myProcess:1:7508");

					}else{
						if(jsonResult.errorCode=="V-STARTED-CP"){
							Ext.Msg.show({
								title:"错误提示["+jsonResult.errorCode+"]:",
								icon:Ext.Msg.ERROR,
								msg:jsonResult.errorMsg,
								buttons:Ext.Msg.OK
							});
						}
					}
				},
				failure : function(response, opts) {
					Ext.Msg.alert("错误代码:"+response.status,response.responseText);
				}
			});
		}

		var flowForm = Ext.widget('form',{
			defaultType: "textfield",
			itemId:'flowForm',
			title : '我的工作台',
			//hidden:true,
			bodyPadding : '5 5 0 5',
			tbar : [startFlow,{
				xtype:'buttontransparent',
				//glyph:0xf00c,
				text : '确认提交审核',
				iconCls : 'flow_ok',
				formBind: true,
				listeners: {"click" : function() {
					if(!errDesc.hidden&&Ext.isEmpty(errDesc.getValue().trim())){
						Ext.Msg.alert("提示","原因描述不允许为空!");
						return;
					}
					var flowType = null;
					if(nextflow.getValue()!=undefined && nextflow.getValue().indexOf('flow_rt_')!=-1){
						flowType = 0;
					}else{
						flowType = 1;
					}
					if(nextflow.getValue().indexOf('flow16')!=-1){//客户确认报价阶段才提示填手机号,nextflow.getValue()=flow16是确认报价阶段
						//先行判断客户是否已维护短信发送号码，有则往下走，无则弹窗维护。
						if(USER_ROLE_ID.indexOf('5C2wEJEdUB6hknTTad5pgb')>-1){
							//判断是否已经有电话号码,如果有那么不进行保存电话操作  add by Ppn on 2017-11-22 
							var kunnr =  CURR_USER_KUNNR;
							var flag=false;
							var oldTel="";
							Ext.Ajax.request({
								url:'/mgettelnum',
								method: 'GET',
								async:false,
								success:function(res){
									var _result=JSON.parse(res.responseText);
									if(_result.data.Tnum){
										flag=true;
										oldTel=_result.data.Tnum;
									}
								}
							});
							if(!flag){
								var win_;

								var Telform_ = Ext.create('Ext.form.Panel',{
									fieldDefaults : {
										labelAlign : 'left',
										labelWidth : 85,
										labelStyle : 'font-weight:bold',
										msgTarget :'side'
									},
									bodyPadding : 10,
									defaults : {
										margins : '0 0 10 0'
									},
									items : [{
										xtype : 'textfield',
										name:'oldtelnum',
										fieldLabel : '原手机号码',
										allowBlank : true,
										width : 270,
										//disabled:true,
										readOnly:true,
										emptyText: oldTel,
										style:'color:red;'
									}, {
										xtype : 'textfield',
										itemId:'telnum',
										name:'telnum',
										fieldLabel : '新手机号码',
										allowBlank : false,
										blankText : '新手机号码不能为空',
										width : 270,
										regex: /^1[3,4,5,7,8]\d{9}$/,
										regexText: '号码不匹配'

									}, {
										xtype : 'textfield',
										itemId:'surenum',
										name:'surenum',
										fieldLabel : '重复新手机号码',
										allowBlank : false,
										blankText : '再次输入新号码不能为空',
										width : 270,
										regex: /^1[3,4,5,7,8]\d{9}$/,
										regexText: '号码不匹配'
									}],
									buttons:[{
										xtype:'button',
										text : '确认修改',
										listeners:{
											click:function(){

												var form = this.up('form').getForm();
												var oldtelnum = form.findField('oldtelnum').getValue();
												var telnum = form.findField('telnum').getValue();
												TEL_NUM = telnum;
												var surenum = form.findField('surenum').getValue();
												var kunnr =  CURR_USER_KUNNR;
												form.submit({
													url:'/mtel',
													params : {
														'telnum' : telnum,
														'cgg' : surenum,
														'kunnr' : kunnr
													},
													waitMsg : 'Please wait',
													waitTitle : '处理中...',
													success : function(form, action) {
														Ext.Msg.show({
															title:'提示',
															msg:action.result.msg,
															buttons:Ext.Msg.OK,
															icon:Ext.Msg.INFO
														});
														win_.close();
													},
													failure : function(formx, action) {
														Ext.Msg.show({
															title:'提示'+action.result.errorCode,
															msg:action.result.errorMsg,
															buttons:Ext.Msg.OK,
															icon:Ext.Msg.ERROR
														});
													}
												});
											}
										}
									}]
								});

								win_ = Ext.create('Ext.window.Window',{
									title : '请先维护手机号以便接收报价通知短信息！',
									modal : true,
									plain : true,
									border:false,
									items:[Telform_]
								});

								win_.show();
								return;
							}
						}
					}
					var boforeCommit = me.fireEvent("boforeCommit",this.up("form").getForm(),flowType,nextflow.getValue());
					console.log(this.up("form").getForm())
					if(boforeCommit){
						var orderCodePosex=mappingNoDly.value;
						var tackit_nextflow12=nextflow.getValue();
						var tackit_currentFlow=currentFlow.getValue();
						this.up("form").getForm().submit({
							url : '/core/bpm/commitFlow',
							success : function(form, action) {
								var jsonResult = Ext.decode(action.response.responseText);
								if(jsonResult.success){
									//刷新列表的数据
									var a_ = Ext.ComponentQuery.query('uxgrid[itemId=flowTaskGrid]');
									if(a_.length>0){
										a_[0].getStore().reload();
									}

									var afterCommit = me.fireEvent("afterCommit");
									if(afterCommit){
										var ticket=tackit.getValue();
										var message="";
										if(ticket!=undefined){
											if(ticket=="1"){
												var orderCodePosex=mappingNoDly.value;
												Ext.Ajax.request({
													url:'main/saleReport/getTicket',
													method: 'POST',
													params:{ticket:ticket,orderCodePosex:orderCodePosex,nextflow:tackit_nextflow12,tackit_currentFlow:tackit_currentFlow},
													dataType: 'json',
													async:false,
													success:function(res){
														message="罚单成功";
													},
													failure:function(res){
														message="罚单失败";
													}
												});
											}
										}
										Ext.Msg.show({
											title:"操作提示:",
											icon:Ext.Msg.INFO,
											msg:jsonResult.msg,
											buttons:Ext.Msg.OK
										});
										me.close();
									}
									

								}else{
									Ext.Msg.show({
										title:"错误提示["+jsonResult.errorCode+"]:",
										icon:Ext.Msg.ERROR,
										msg:jsonResult.errorMsg,
										buttons:Ext.Msg.OK
									});
								}

							},
							failure : function(form, action) {
								var jsonResult = Ext.decode(action.response.responseText);
								if(!jsonResult.success){
									Ext.Msg.show({
										title:"错误提示["+jsonResult.errorCode+"]:",
										icon:Ext.Msg.ERROR,
										msg:jsonResult.errorMsg,
										buttons:Ext.Msg.OK
									});
								}
							}
						});
					}

				}
				}
			},endFlow],
			items : [/*definition,processDefinition,*/mappingId,mappingNo,mappingIdDly,mappingNoDly,creditField,unProduction,currentFlowId,currentFlow,nextflow,tackit,errType,errDesc,desc,flowHole]
		});

		//tabpanel布局
		var tabpanel = Ext.widget('tabpanel',{
			plain: true,
			border:false,
			region: 'center'
		});
		if(tabIndex==0){
			tabpanel.add([flowForm,flowGrid/*,flowTreeGrid*/,flowGraphics]);
		}else{
			tabpanel.add([flowGrid,flowGraphics]);
		}
		tabpanel.setActiveTab(0);

		//添加回调事件

		Ext.apply(this, {
			items : [tabpanel]
		});

		this.callParent(arguments);
	}
});