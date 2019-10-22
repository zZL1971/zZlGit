var queryType = "";
Ext.define('SMSWeb.view.bpm.TaskList', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.bpmtasklist',
	layout : 'border',
	border : false,
	requires : ['Ext.ux.form.SearchForm','Ext.ux.IFrame'],
	initComponent:function(){
		var me = this;
		var btnDate=new Date().getTime();
		var searchFields,configFields;
		var xmlName = FLOW_RESOURCE;
		var resData = null;
		try{
			resData = Ext.create('SMSWeb.data.'+xmlName);
			searchFields = resData.search;
			configFields = resData.config;
		}catch(e){
			//console.log('对应的配置文件未生成，系统默认从后台再次抓取数据，建议重新生成提升性能!');
			Ext.Ajax.request({
				url:'/core/ext/grid/execModuleData/'+xmlName,
				async:false,
				success:function(response,opts){
				},
				failure:function(response,opts){
					Ext.Msg.alert("can't",'error');
				}
			});
		}
		
		//console.log(resData);
		
		if(resData==null){
			//获取表单数据
			Ext.Ajax.request({
				url:'/core/ext/grid/search/'+xmlName,
				async:false,
				dataType: "json",
				success:function(response,opts){
					var cls = Ext.decode(response.responseText);
					searchFields= cls;
				},
				failure:function(response,opts){
					Ext.Msg.alert("can't",'error');
				}
			});
			
			//获取配置信息
			Ext.Ajax.request({
				url:'/core/ext/grid/config/'+xmlName,
				async:false,
				dataType: "json",
				success:function(response,opts){
					var cls = Ext.decode(response.responseText);
					configFields= cls.data;
				},
				failure:function(response,opts){
					Ext.Msg.alert("can't",'error');
				}
			});
		
		}
		
		//创建动态表单
		var searchForm = Ext.widget('searchform',{
			region : 'north',
			boder:false,
			bodyStyle:'border:none;',
			bodyPadding : '5 5 0 5',
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 85,
				labelStyle : 'padding-left:5px;',
				width : 240
			},
			items:searchFields
		});
		//创建动态表格
		var uxgrid =  Ext.widget('uxgrid',{
        	headerModule:xmlName,
        	moduleData:resData,
        	itemId:'flowTaskGrid',
        	selModelStatus:configFields.selModelStatus,
        	isApplyEdit:false,
        	//statusBar:'工作列表-BPM1.0',
        	//alias : 'widget.flowTaskGrid',
        	pageToolbar:true,
			title:"【"+configFields.title+'】 我的任务',
			region : 'center',
			listeners:{
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var orderType=record.get("orderType");
					var id=record.get("id");
					 
					var openStatus = true;
					if(record.get('assignee').length==0){
						//领取任务
						Ext.MessageBox.confirm("提示", "确定受理该任务吗？", function (btn) {
					        //Ext.Msg.alert("提示", "你点击了" + btn + "按钮");
							if(btn=="yes"){
								Ext.Ajax.request({
									url:'/core/bpm/claim',
									params:{
										taskId:record.get('taskId')
									},
									async:false,
									dataType: "json",
									success:function(response,opts){
										var message = Ext.decode(response.responseText);
										if(message.success){
										 
										if("OR3"!=orderType&&"OR4"!=orderType&&"Y"==configFields.showNewWin){
												//调用新页面
												var id=record.get("id");
												var win=Ext.create("SMSWeb.view.sale.NewSaleContentWindow", {title:'修改订单',shId:id});
												win.show();
										}else{
												var dataCell={};
												if(configFields.editRowDataCell){
													var dataCellIndex = configFields.editRowDataCell.split(",");
													for(var i=0;i<dataCellIndex.length;i++){
														dataCell[dataCellIndex[i]]=record.get(dataCellIndex[i]);
													}
												}
											
												var defaultConfig = {formId : record.getId(),title:'查看业务表单',loadStatus:"4",editFlag:true,flag:true};
												var extended_ = Ext.apply(defaultConfig, dataCell);
												
												//获取行编辑表单eidtRowForm
												Ext.create(configFields.eidtRowForm,extended_).show();
											}
											uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
											
										}else{
											Ext.Msg.show({
												title:"错误代码:"+message.errorCode,
												icon:Ext.Msg.ERROR,
												msg:message.errorMsg,
												buttons:Ext.Msg.OK
											});
										}
									},
									failure:function(response,opts){
										Ext.Msg.alert("can't",'error');
									}
								});
								
							}
					    });
					}else{

						if("OR3"!=orderType&&"OR4"!=orderType&&"Y"==configFields.showNewWin){
							//调用新页面
							var id=record.get("id");
							var win=Ext.create("SMSWeb.view.sale.NewSaleContentWindow", {title:'修改订单',shId:id});
							win.show();
						}else{
							
							var dataCell={};
							if(configFields.editRowDataCell){
								var dataCellIndex = configFields.editRowDataCell.split(",");
								for(var i=0;i<dataCellIndex.length;i++){
									if("OR3"==record.get(dataCellIndex[i])||"OR4"==record.get(dataCellIndex[i])){
										dataCell[dataCellIndex[i]]="buDan";
										if("gp_store"==configFields.taskType){
											configFields.eidtRowForm="SMSWeb.view.sale.NewSaleContentWindow";
										}
									}else{
										dataCell[dataCellIndex[i]]=record.get(dataCellIndex[i]);
									}
								}
							}
						
							var defaultConfig = {formId : record.getId(),title:'查看业务表单',loadStatus:"4",editFlag:true,flag:true,sourceShow:configFields.taskType,jdName:configFields.jdName,shouDaFang:record.data.shouDaFang};
							var extended_ = Ext.apply(defaultConfig, dataCell);
							
							//获取行编辑表单eidtRowForm
							Ext.create(configFields.eidtRowForm,extended_).show();
//							if(configFields.taskType == "gp_valuation"){
//						    
//											//Ext.MessageBox.alert("提示","要作判断！");
//											console.log("xn1");
//											Ext.Ajax.request({
//											url: 'main/mm/getDiscountKunnr2',
//											method : 'GET',
//										    params: {
//											   'kh' : record.data.shouDaFang,
//											   'createtime' : record.data.createTime,
//											   'ordercode' : record.data.orderCode
//										    },
//										    async : false,
//										    dataType :"json",
//										    contentType : 'application/json',
//										    success : function(response,opts){
//										    	 var haskh=Ext.decode(response.responseText);
//										    	 console.log(haskh);
//										    	 var _array=haskh.data;
//										    	 var elementA="";
//										    	
//										    	 for(element in _array){
//										    		 var tempStyle = null;
//										    		 tempStyle = _array[element].DESC_ZH_CN;
//										    		 var elTempsytle = null;
//										    		 elTempsytle = _array[element].DESC_ZH_CN2;
//										    		 elementA=elementA+tempStyle
//										    		 +":"+ elTempsytle+"<br/>";
//										    	 }
//										    	 
//										    	 if(haskh.success){
//										    		 Ext.MessageBox.alert("温馨提示","客户"+record.data.shouDaFang+"含折扣如下：<br/>"+elementA);
//										    	 }else{
//										    		 Ext.MessageBox.alert("提示",haskh.errorMsg);
//										    	 }
//										    },
//										    
//										    failure : function(response,opts){
//										    	console.log("***si*****");
//										    	 //var haskh=Ext.decode(response.responseText);
//										    	 Ext.MessageBox.alert("提示","查询客户折扣信息表出错！");
//										    }
//											
//										});
//							}
						   
						}
					}
					
					
				},
				itemShowPicButtonClick:function(grid,rowIndex,colIndex){
					var datas=grid.getStore().getRange(rowIndex);
					var ids="";
					var taskIds="";
					var assignees="";
					console.info(datas[0].data);
					for(var i=0;i<datas.length;i++){
						if(ids==""){
							ids=datas[i].data.id;
							taskIds=datas[i].data.taskId;
							var assignee=datas[i].data.assignee;
							if(!assignee){
								assignees="X";
							}
							
						}else{
							ids=ids+","+datas[i].data.id;
							var tId=datas[i].data.taskId;
							var assignee=datas[i].data.assignee;
							if(!assignee){
								assignee="X";
							}
							taskIds=taskIds+","+tId;
							assignees=assignees+","+assignee;
						}
						
					}
					var tsWin = Ext.widget('taskApprove',{uuidVal:ids,v_taskIds:taskIds,v_assignees:assignees});
					tsWin.show();
//					showImg(grid,rowIndex,colIndex);
				},
				actionColumnDisabled:function(view, rowIdx, colIdx, item, record){
					if(record.get('assignee').length==0){
						return true;
					}else{
						return false;
					}
				},
				editTaskButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex); 
					var orderType=record.get('orderType');
					var _saleId=record.get('id');
					var orderCode=record.get("orderCode");
					var bpm = "MainProductQuotation";
					if("buDan"==orderType || "OR3"==orderType || "OR4"==orderType){
						bpm = "NewCustomerServiceOrdProcess";
					}
					Ext.create('Ext.ux.window.FlowWindow',{
						uuid:'form[itemId=saleForm] hiddenfield[name=id]',
						uuidVal:_saleId,
						uuidNo:'form[itemId=saleForm] displayfield[name=orderCode]',
						uuidNoVal:orderCode,
						'bpm':bpm,
						listeners:{
						//激活流程前
						boforeActivate:function(form){
//							var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
//							var _saleId = _saleIdField.getValue();
							var flag = saleBoforeActivate(_saleId);
							return flag;
						},
						//激活流程后
						afterActivate:function(){
//							var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
//							var _saleId = _saleIdField.getValue();
							var flag = saleAfterActivate(_saleId);
							return flag;
						},
						boforeCommit:function(form,flowtype,nextflowId){
//							var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
//							var _saleId = _saleIdField.getValue();
							var flag = flowValidate(_saleId,flowtype);
							return flag;
//							return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
						},
						afterCommit:function(){
							//提交后关闭窗口并重新查询订单信息
//							Ext.ComponentQuery.query('window[itemId=newSaleWindow]')[0].close();
//							var sale2Form = Ext.ComponentQuery.query('panel[id=sale2Form]')[0];
//							if("undefined"!=typeof(sale2Form)){
//								var formValues = sale2Form.getValues();
//							    var grid = Ext.ComponentQuery.query('panel[id=sale2Grid]')[0];
//							    var store = grid.getStore();
//							    store.load({
//							    	params:formValues,
//							    	callback:function(r,options,success){
//							            if(success){
//							           }
//							        }
//							    });
//							}
							return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
						}
					}}).show();
				}
			}
		});
		
		
		//第一次的时候查询数据
		Ext.apply(uxgrid.getStore().proxy.extraParams,searchForm.getSearchs());
		uxgrid.getStore().reload();
		
		var toolbar = Ext.widget('toolbar',{
			items:[{
				xtype:'buttontransparent',
				glyph:0xf002,
				text : '查询',
				handler:function(){
					uxgrid.getStore().proxy.extraParams = {};
					Ext.apply(uxgrid.getStore().proxy.extraParams,searchForm.getSearchs());
					uxgrid.getStore().loadPage(1);
				}
			},{
					xtype : 'buttontransparent',
					text : '删除',
					glyph : 0xf00d,
					hidden:(configFields.curd==null?true:(configFields.curd.indexOf('D')>=0?false:true)),
					handler: function(event, toolEl){
						Ext.Msg.confirm('提示',"确定删除该订单？",function(id){
							if(id=="yes"){
						var selectedRows = uxgrid.getSelectionModel().getSelection();
						var ids = [];
						if( selectedRows.length>0){
							for(var i = 0; i < selectedRows.length ; i++){
								var orderCode = selectedRows[i].get('orderCode');
								var id = selectedRows[i].get('id');
								if(orderCode==null ||orderCode=="" ){
									ids.push(id);
								}
							}
						}
						if(ids.length==0){
							Ext.Msg.alert("提示",'请选择需要删除的数据');
							return;
						}
						Ext.Ajax.request({
							url:configFields.base+configFields.deleteMethod,
							params : {
								ids : ids
							},
							method:'POST',
							success:function(response,opts){
								var jsonResult = Ext.decode(response.responseText);
								if(jsonResult.success){
									Ext.MessageBox.alert("提示","删除成功");
								}else{
									Ext.MessageBox.alert("提示","删除失败");
								}
								uxgrid.getStore().reload();
								
							},
							failure:function(response,opts){
								Ext.Msg.alert("错误代码:"+response.status,response.responseText);
							}
						});
						
					}
						});
					}
			},{
				xtype:'buttontransparent',
				glyph:0xf024,
				text : '领取任务(随机)',
				hidden:configFields.receiveMode?(configFields.receiveMode=="A"||configFields.receiveMode=="B")?true:false:false,
				//id : 'add',
				//iconCls : 'table_add',
				handler:function(){
					var newBtnDate=new Date().getTime();
					Ext.Msg.confirm('提示',"确定现在立即领取随机任务吗？",function(id){
						if(id=="yes"){
							//if(window.event.keyCode==13){
								//Ext.Msg.alert("温馨提示","频率太快，疑似开挂！");
								//return ;
							//};
						
					if((newBtnDate-btnDate)<100){
						btnDate=newBtnDate;
						//Ext.Msg.alert("温馨提示","点击速度过快，请稍后再试！");
						return;
					}else{
						btnDate=newBtnDate;
					};
							Ext.Ajax.request({
								url:'/core/bpm/randomTask/'+configFields.taskType,//获取任务类型taskType
								params:{ptype:configFields.ptype},
								async:false,
								dataType: "json",
								success:function(response,opts){
									var message = Ext.decode(response.responseText);
									if(message.success){
										uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
									}else{
										Ext.Msg.show({
											title:"错误代码:"+message.errorCode,
											icon:Ext.Msg.ERROR,
											msg:message.errorMsg,
											buttons:Ext.Msg.OK
										});
									}
								},
								failure:function(response,opts){
									Ext.Msg.show({
										title:"系统错误",
										icon:Ext.Msg.ERROR,
										msg:response.responseText,
										buttons:Ext.Msg.OK
									});
								}
							});
						}
					});
				}
			},{
				xtype:'buttontransparent',
				glyph:0xf024,
				text : '批量审批',
				hidden:configFields.batchTask=="Y"?false:true,
				handler:function(){
					alert("此功能未完善！！！");
					return;
					var datas=uxgrid.getSelectionModel().getSelection();
					console.info(datas);
					var uuidVal=datas[0].get("id");
					var uuidNoVal=datas[0].get("orderCode");
//					/core/bpm/startedFlow?_dc=1460528774140&uuid=SXkSXcHPYGKaKHPLNkWiDc 当前节点
//					/core/bpm/taskFlow?_dc=1460528774172&id=7192154&page=1&start=0&limit=25 下一节点
					//tab1 流程下一个环节操作（当前流程定义节点ID、当前流程实例ID、当前流程关联表单ID、当前环节、下一个环节、代办意见）
					var currentFlow = Ext.widget('displayfield',{
						name:'flowStatusDly',
						fieldLabel:'当前环节'
					});
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
//							change:function( ths, newValue, oldValue, eOpts){
//								if(newValue!=undefined && newValue.indexOf('flow_rt_')!=-1){
//									var record_ = ths.findRecord('id',newValue);
//									console.log(record_);
//									errType.getStore().getProxy().extraParams={codeType:record_.raw.target};
//									errType.getStore().load();
//									
//									errType.show(true);
//									errType.setDisabled(false);
//									errDesc.show(true);
//									errDesc.setDisabled(false);
//									desc.hide(true);
//									desc.setDisabled(true);
//									desc.setValue("");
//								}else{
//									errType.hide(true);
//									errType.setDisabled(true);
//									errDesc.hide(true);
//									errDesc.setDisabled(true);
//									
//									errType.setValue("");
//									errDesc.setValue("");
//									
//									desc.show(true);
//									desc.setDisabled(false);
//								}
//							}
						}
						
					});
					Ext.Ajax.request({
						url : '/core/bpm/startedFlow',
						params : {
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
					
					
					Ext.create('Ext.window.Window', {
					    title: '批量审批',
					    height: 250,
					    width: 500,
					    layout: 'anchor',
					    items:[currentFlow,nextflow,{
					    		xtype:'dictcombobox',
					    		name:'errType',
								fieldLabel:'出错类型', 
								allowBlank:false,
								afterLabelTextTpl: requiredLabel,
								dict:'ERROR_ORD_TYPE'
					    	},{
						    	xtype:'textareafield',
						    	name:'desc',
						    	fieldLabel:'代办意见',
						    	grow: true,
								anchor:'90%',
								allowBlank:false
					    	}
					    ]
					}).show();
				}
			},
			{
				xtype:'buttontransparent',
				glyph:0xf201,
				text : '审批统计',
				hidden:true,
				handler:function(){
					
					var data =  [
				                {year: '艾波', comedy: 3400, action: 2389, drama: 1845, thriller: 2006},
				                {year: '张健成', comedy: 5670, action: 3890, drama: 1265, thriller: 2100},
				                {year: '小雪', comedy: 4210, action: 5041, drama: 2578, thriller: 2304},
				                {year: '李锦彬', comedy: 3891, action: 5607, drama: 2481, thriller: 2694},
				                {year: '郭志强', comedy: 3891, action: 5607, drama: 2481, thriller: 2694},
				                {year: '艾琪', comedy: 3891, action: 5607, drama: 2481, thriller: 2694}
				              ];
				     var data2 =  [
				                {year: '艾波', comedy: 1, action: 3, drama: 2, thriller: 1},
				                {year: '张健成', comedy: 3, action: 4, drama: 3, thriller: 3},
				                {year: '小雪', comedy: 4, action: 2, drama: 4, thriller: 5},
				                {year: '李锦彬', comedy: 5, action: 1, drama: 6, thriller: 8},
				                {year: '郭志强', comedy: 6, action: 1, drama: 5, thriller: 8},
				                {year: '艾琪', comedy: 7, action: 2, drama: 5, thriller: 5}
				              ];
					 
					 var store = Ext.create('Ext.data.JsonStore', {
				        fields: ['year', 'comedy', 'action', 'drama', 'thriller'],
				        data:data
				    });
				
				    var chart = Ext.create('Ext.chart.Chart',{
				            animate: true,
				            shadow: true,
				            store: store,
				            legend: {
				                position: 'bottom'
				            },
				            axes: [{
				                type: 'Numeric',
				                position: 'bottom',
				                fields: ['comedy', 'action', 'drama', 'thriller'],
				                title:false,
				                grid: true,
				                label: {
				                    renderer: function(v) {
				                        return v;
				                    }
				                }
				            }, {
				                type: 'Category',
				                position: 'left',
				                fields: ['year'],
				                title: false
				            }],
				            series: [{
				                type: 'bar',
				                axis: 'bottom',
				                gutter: 80,
				                xField: 'year',
				                yField: ['comedy', 'action', 'drama', 'thriller'],
				                stacked: true,
				                tips: {
				                    trackMouse: true,
				                    width: 65,
				                    height: 28,
				                    renderer: function(storeItem, item) {
				                        this.setTitle(item.value[1]);
				                    }
				                },
				                label: {
				                  display: 'insideEnd',
				                    field: ['comedy', 'action', 'drama', 'thriller'],
				                    color: '#FFF'
				                }
				            }]
				        });
					
					Ext.create('Ext.window.Window',{
						title : '订单审绘工作统计',
			            width:700,
			            height:500,
			            bodyStyle:'background:#fff',
			            layout:'border',
			            modal : true,
			            tbar:[{
			            	xtype:'buttontransparent',
							glyph:0xf201,
							text : '刷新',
							handler:function(){
								store.loadData(data2);
							}
			            }],
			            //plain : true,
			            border:false,
						items:chart
					}).show();
				}
			}]
		});
		
		if(CURR_USER_LOGIN_NO=="admin"){
			toolbar.add({
				xtype:'buttontransparent',
				glyph:0xf085,
				text : '更新模板',
				handler:function(){
					Ext.Ajax.request({
						url:'/core/ext/grid/execModuleData/'+xmlName,
						success:function(response,opts){
							window.location="/core/bpm/"+xmlName+"?t="+Math.random();
						},
						failure:function(response,opts){
							Ext.Msg.alert("can't",'error');
						}
					});
				}
			});
		}
		
		me.items=[{
			layout:'border',
			region : 'center',
			tbar : toolbar,
			items : [searchForm,uxgrid]
		}];
		
		//获取其他控制器
		if(configFields.controllers){
			var controllersplit = configFields.controllers.split(",");
			Ext.Array.each(controllersplit,function(name){
				//动态加载其他控制器
				Ext.require('SMSWeb.controller.'+name,function(){ 
				        var controller = Ext.create('SMSWeb.controller.'+name);
				        controller.init();                                
				    }
				);
			});
		}
		
		me.callParent(arguments);
		
	}
});

function showImg(grid,rowIndex,colIndex){
	//获取表单数据
	var record = grid.getStore().getAt(rowIndex);
	var isShow="N";
	var uuId=record.get('id');
	var code=record.get('orderCode');
	var datas;
	Ext.Ajax.request({
		url:'/main/sysFile/querySysFile?_dc=1460440399215&foreignId='+uuId+'&fileType=&page=1&start=0&limit=25',
		async:false,
		dataType: "json",
		success:function(response,opts){
			var cls = Ext.decode(response.responseText);
			if(cls.length>0){
				isShow="Y";
				datas=cls;
			}else{
				Ext.Msg.show({
					title:"温馨提示",
					icon:Ext.Msg.ERROR,
					msg:"该单还没有上传汇款凭证！单号："+code,
					buttons:Ext.Msg.OK
				});
			}
			 
		},
		failure:function(response,opts){
			Ext.Msg.alert("can't",'error');
		}
	});
	if(isShow=="Y"){
	var winPic_ = Ext.create('Ext.window.Window',{
		title:"显示图片(滑轮可放大缩小图片) 单号："+code,
        layout : 'fit',
        width:810,
        height:450, 
        bodyStyle:{
        	'overflow-y':'scroll',
        	 'margin': '0px',
        	 'padding': '0'
        } 
	});
	var adds=new Array();
	for(var i=0;i<datas.length;i++){
		var o={
					width:790,
					xtype:'uxiframe',
					src:'/core/ext/base/pic/'+datas[i].id
		};
		adds.push(o);
	}
	winPic_.add(adds);
	winPic_.show();
	};
}
