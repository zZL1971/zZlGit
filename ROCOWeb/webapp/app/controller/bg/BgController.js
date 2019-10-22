Ext.define("SMSWeb.controller.bg.BgController", {
	extend : 'Ext.app.Controller',
	id : 'bgController',
	refs : [
		{
			ref : 'NewBgWindow',
			selector : 'NewBgWindow' 
		},
		{
			ref : 'BgGridView',
			selector : 'BgGridView'
		},
		{
			ref : 'NewSaleWindowForBg',
			selector : 'NewSaleWindowForBg' 
		},
		{
			ref : 'NewSaleItemWindowForBg',
			selector : 'NewSaleItemWindowForBg' 
		},
		{
			ref : 'NewSysBzWindow',
			selector : 'NewSysBzWindow' 
		}
	],
	init : function() {
		this.control({//保存主表
			
			//grid编辑事件
			'BgGridView':{
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var bgForm = Ext.getCmp("bgForm");
					var queryBgType = bgForm.getForm().findField("queryBgType").getValue();
			
					var record = grid.getStore().getAt(rowIndex);
					var id = record.get('pid');
					Ext.create('SMSWeb.view.bg.NewBgWindow',
							     {formId : id,title:'订单变更修改','queryBgType':queryBgType}).show(this,function(){
							     });
				}
			},
			
			//查询变更订单信息
			"BgMainView button[id=queryBg]":{
				click : function( bt, e, eOpts ) {
//					var bgForm = Ext.getCmp("bgForm");
//					var formValues = bgForm.getValues();
					var grid = Ext.getCmp("bgGrid");
				    var store = grid.getStore();
				    store.loadPage(1/*,{
				    	params:formValues,
				    	callback:function(r,options,success){
				            if(success){
				           }
				        }
				    }*/);
				}
			},
			
			//弹出新增修改窗口
			"BgMainView button[id=newBg]":{
				click : function( bt, e, eOpts ) {
					var bgForm = Ext.getCmp("bgForm");
					var queryBgType = bgForm.getForm().findField("queryBgType").getValue();
				    Ext.create('SMSWeb.view.bg.NewBgWindow',
				     {title:'新增订单变更','queryBgType':queryBgType}).show(this,function(){
				     });
				}
			},
			
			//弹窗增加行项目事件
			"NewBgWindow button[id=addBgItem]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					var bgForm = NewBgWindow.queryById("bgForm");
					var orderCode = bgForm.getForm().findField("orderCode").getValue();
					if(orderCode==null || orderCode==""){
						Ext.MessageBox.alert("提示信息","请先选择订单编号！");
						return;
					}
					Ext.create('SMSWeb.view.sale.NewSaleItemWindowForBg',
							     {title:'新增订单明细'}).show(this,function(){
									 var form = this.queryById("saleItemFormForBg");
									 form.getForm().findField("orderCode").setValue(orderCode);
									 var formValues = form.getValues();
									 var itemGrid = this.queryById("saleItemGridForBg");
								     var store = itemGrid.getStore();
								     store.load({
								    	params:formValues,
								    	callback:function(r,options,success){
								            if(success){
								           }
								        }
								     });
							     });
//				    var me = this;
//					var NewBgWindow = me.getNewBgWindow();
//					//head信息
//					//var saleForm = NewBgWindow.queryById("bgForm");
//					//var formValues = saleForm.getValues();
//					//物料清单信息
//					var itemGrid = NewBgWindow.queryById("bgGrid");
//					var itemStore = itemGrid.getStore();
//					var itemModel = itemStore.model;
//					var storeCount = itemStore.getCount();
//					var modelit = new itemModel();
//					itemStore.insert(storeCount, modelit);
				}
			},
			
			//弹窗删除行项目事件
			"NewBgWindow button[id=removeBgItem]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					Ext.MessageBox.confirm('提示信息','确定要删除所选产品明细？',
				    	function(btn){
					        if(btn=='yes'){
								var itemGrid = NewBgWindow.queryById("bgGrid");
							    var itemStore = itemGrid.getStore();
							    var sm = itemGrid.getSelectionModel();
							    var records = sm.getSelection();
							    var ids = [];
								Ext.Array.each(records, function(r) {
									ids.push(r.get('id'))
								});
								Ext.Ajax.request({
									url : 'main/bg/deleteBgItemByIds',
									params : {
										ids : ids
									},
									method : 'POST',
									success : function(response, opts) {
										itemStore.remove(sm.getSelection());
										var BgGridView = me.getBgGridView();
										BgGridView.getStore().loadPage(1);
										Ext.MessageBox.alert("提示信息","删除成功！");
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示信息","删除失败！");
									}
								});
				                /*if (itemStore.getCount() > 0) {
				                    sm.select(0);
				                }*/							
					        }else{
					          
					        }
				    });
				}
			},
			
			//弹窗保存变更订单
			"NewBgWindow button[id=saveBg]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					//head信息
					var bgForm = NewBgWindow.queryById("bgForm");
					var formValues = bgForm.getValues();
					
					var bgHeadForm = {};
					Ext.Object.each(formValues, function(key, value, myself) {
							bgHeadForm[key] = value;
					});
					var bgCode = bgForm.getForm().findField("bgCode").getValue();
					var orderCode = bgForm.getForm().findField("orderCode").getValue();
					var orderType = bgForm.getForm().findField("orderType").getValue();
					var clients = bgForm.getForm().findField("clients").getValue();
					var bgType = bgForm.getForm().findField("bgType").getValue();
					bgHeadForm['bgCode'] = bgCode;
					bgHeadForm['orderCode'] = orderCode;
					bgHeadForm['clients'] = clients;
					var errMsg = "";
					if(orderCode==""){
						errMsg += "订单编号必选<br/>";
					}
					if(clients==""){
						errMsg += "申请客户必选<br/>";
					}
					if(bgType==null || bgType==""){
						errMsg += "请选择变更类型<br/>";
					}
					var tel = bgForm.getForm().findField("tel").getValue();
					var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$|^(\(\d{3,4}-)|\d{3.4}-\)?\d{7,8}$/;
					if(tel!=null && Ext.String.trim(tel)!="" && !reg.test(tel)){
						errMsg += "联系电话不匹配<br/>";
					}
					if(errMsg!=""){
						bgForm.isValid();
						Ext.MessageBox.alert("提示信息",errMsg);
						return;
					}
					
					var itemGrid = NewBgWindow.queryById("bgGrid");
				    var itemStore = itemGrid.getStore();
				    
				    var gridValues = itemStore.getRange(0,itemStore.getCount());
				    var json = [];
				    for (var i = 0; i <itemStore.getCount(); i++) {
				    	var arrJson = gridValues[i].getData();
				    	json.push(arrJson);
				    }
			        var gridData = Ext.encode({
						bgItemList : json
					});
					if(bgForm.isValid()){
						if(json.length==0){
							if('OR3'==orderType||'OR4'==orderType){
							}else{
								Ext.MessageBox.alert("提示信息","至少一条行项目");
								return;
							}
						}
						Ext.Ajax.request({
							url : 'main/bg/save',
							params : bgHeadForm,
							method : 'POST',
							frame : true,
							jsonData : gridData,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								if(values.success){
									bgForm.getForm().setValues(values.data);
									var formValues = bgForm.getValues();
									if(values.data.orderStatus=="A"){
										Ext.getCmp("submitBg").show();
									}else if(values.data.orderStatus=="B"){
										Ext.getCmp("saveBg").hide();
										Ext.getCmp("addBgItem").hide();
										Ext.getCmp("removeBgItem").hide();
										Ext.getCmp("openSaleWindow").hide();
										Ext.getCmp("submitBg2").show();
										Ext.getCmp("resubmitBg").show();
										Ext.Object.each(formValues, function(key, value, myself) {
											bgForm.getForm().findField(key).setReadOnly(true);
										});
									}else if(values.data.orderStatus=="C"){
										Ext.getCmp("saveBg").hide();
										Ext.getCmp("addBgItem").hide();
										Ext.getCmp("removeBgItem").hide();
										Ext.getCmp("openSaleWindow").hide();
										Ext.Object.each(formValues, function(key, value, myself) {
											bgForm.getForm().findField(key).setReadOnly(true);
										});
									}
									if("2"==NewBgWindow.queryBgType || "3"==NewBgWindow.queryBgType){
										Ext.getCmp("submitBg2").hide();
										Ext.getCmp("resubmitBg").hide();
									}else if("1"==NewBgWindow.queryBgType){
										Ext.getCmp("saveBg").hide();
										Ext.getCmp("submitBg").hide();
										Ext.getCmp("addBgItem").hide();
										Ext.getCmp("removeBgItem").hide();
										Ext.getCmp("openSaleWindow").hide();
										Ext.Object.each(formValues, function(key, value, myself) {
											bgForm.getForm().findField(key).setReadOnly(true);
										});
									}
									
									itemStore.load({params:{'pid':values.data.id}});
									
									var BgGridView = me.getBgGridView();
									BgGridView.getStore().loadPage(1);
//									DWRHelper.addMessage("ddddddddddddddd"); 
//									dwr.engine.setActiveReverseAjax(true);
									
									Ext.MessageBox.alert("提示信息","保存成功");
								} else {
									Ext.MessageBox.alert("提示信息",values.errorMsg);
								}
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","保存失败");
							}
						});
					}
				}
			},
			
			//弹窗提交变更订单
			"NewBgWindow button[id=submitBg]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					//head信息
					var bgForm = NewBgWindow.queryById("bgForm");
					var id = bgForm.getForm().findField("id").getValue();
					showSysBzWin(id,"B","提交审核","submitBg");
					
				}
			},
			
			//弹窗退回变更订单
			"NewBgWindow button[id=resubmitBg]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					//head信息
					var bgForm = NewBgWindow.queryById("bgForm");
					var id = bgForm.getForm().findField("id").getValue();
					showSysBzWin(id,"A","退回起草","resubmitBg");
//					Ext.Ajax.request({
//							url : 'main/bg/submit',
//							params : {"id":id,"status":"A"},
//							method : 'POST',
//							frame : true,
//							dataType : "json",
//							contentType : 'application/json',
//							success : function(response, opts) {
//								var values = Ext.decode(response.responseText);
//								bgForm.getForm().findField("orderStatus").setValue(values.data.orderStatus);
//								bgForm.getForm().findField("orderStatus").initValue();
//								
//								var BgGridView = me.getBgGridView();
//								BgGridView.getStore().loadPage(1);
//								
//								Ext.getCmp("saveBg").show();
//								Ext.getCmp("addBgItem").show();
//								Ext.getCmp("removeBgItem").show();
//								Ext.getCmp("openSaleWindow").show();
//								Ext.getCmp("submitBg").show();
//								Ext.getCmp("submitBg2").hide();
//								Ext.getCmp("resubmitBg").hide();
//								
//								Ext.MessageBox.alert("提示信息","退回成功");
//							},
//							failure : function(response, opts) {
//								Ext.MessageBox.alert("提示信息","退回失败");
//							}
//						});
					
				}
			},
			
			//弹窗通过变更订单
			"NewBgWindow button[id=submitBg2]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					//head信息
					var bgForm = NewBgWindow.queryById("bgForm");
					var id = bgForm.getForm().findField("id").getValue();
					showSysBzWin(id,"C","审核通过","submitBg2");
				}
			},
			
			
			"NewBgWindow button[id=openSaleWindow]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.sale.NewSaleWindowForBg',
				     {title:'订单查询','saleFlag':'bgFlag','queryBgType':queryBgType}).show(this,function(){
				     });
				}
			},
			
			//查询变更订单审核记录
			"NewBgWindow button[id=recordsCheck]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewBgWindow = me.getNewBgWindow();
					//head信息
					var bgForm = NewBgWindow.queryById("bgForm");
					var id = bgForm.getForm().findField("id").getValue();
					Ext.create('SMSWeb.view.sys.ListSysBzWindow',
					     {title:'审核记录'}).show(this,function(){
					    	var grid = this.queryById("sysBzGrid");
						    var store = grid.getStore();
						    store.load({
						    	params:{"zid":id},
						    	callback:function(r,options,success){
						            if(success){
						           }
						        }
						    });
					});
				}
			},

			"NewSaleWindowForBg button[id=querySaleForWin]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleWindowForBg = me.getNewSaleWindowForBg();
					//head信息
//					var form = NewSaleWindowForBg.queryById("saleFormForBg");
//					var formValues = form.getValues();
					//物料清单信息
					var itemGrid = NewSaleWindowForBg.queryById("saleGridForBg");
				    var store = itemGrid.getStore();
				    store.loadPage(1/*,{
				    	params:formValues,
				    	callback:function(r,options,success){
				            if(success){
				           }
				        }
				    }*/);
				}
			},
			
			"NewSaleWindowForBg button[id=confirmSaleForWin]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleWindowForBg = me.getNewSaleWindowForBg();
					var itemGrid = NewSaleWindowForBg.queryById("saleGridForBg");
				    var store = itemGrid.getStore();
				    var records = itemGrid.getSelectionModel().getSelection();
                    if(records.length == 0) {
                        Ext.MessageBox.alert('提示信息','请选择一条明细');
                    }else if(records.length == 1){
//                    	Ext.Array.each(records, function(r) {
//							alert(r.get('code'));
//						});
                    	var record = records[0];
                    	var orderCode = record.get('orderCode');
                    	var orderType = record.get('orderType');
                    	var shouDaFang = record.get('shouDaFang');
                    	
                    	
                    	var NewBgWindow = me.getNewBgWindow();
//                    	if( typeof(NewBgWindow) != "undefined" ){
//                    		var saleForm = NewSaleWindow.queryById("saleForm");
//                    	} else {
//                    		var saleForm = Ext.getCmp("saleForm");
//                    	}
                    	var form = NewBgWindow.queryById("bgForm");
                    	var orderCode_old = form.getForm().findField("orderCode").getValue();
                    	if(orderCode_old!=orderCode){
                    		form.getForm().findField("orderCode").setValue(orderCode);
							form.getForm().findField("orderCode").initValue();
							form.getForm().findField("orderType").setValue(orderType);
							form.getForm().findField("orderType").initValue();
							form.getForm().findField("clients").setValue(shouDaFang);
							form.getForm().findField("clients").initValue();
							var grid = NewBgWindow.queryById("bgGrid");
							var store = grid.getStore();
							store.removeAll();
                    	}
						NewSaleWindowForBg.close();
						
                    }else{
                    	Ext.MessageBox.alert('提示信息','只能选择一条明细');
                    }
				}
			},
			
			
			"NewSaleItemWindowForBg button[id=querySaleItemForWin]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleItemWindowForBg = me.getNewSaleItemWindowForBg();
					//head信息
					var form = NewSaleItemWindowForBg.queryById("saleItemFormForBg");
					var formValues = form.getValues();
//					var paraMap = {}
//					Ext.Object.each(formValues, function(key, value, myself) {
//							paraMap[key] = value;
//					});
//					paraMap["orderCode"] = form.getForm().findField("orderCode").getValue();
					//物料清单信息
					var itemGrid = NewSaleItemWindowForBg.queryById("saleItemGridForBg");
				    var store = itemGrid.getStore();
				    store.load({
				    	params:formValues,
				    	callback:function(r,options,success){
				            if(success){
				           }
				        }
				    });
				}
			},
			
			"NewSaleItemWindowForBg button[id=confirmSaleItemForWin]":{
				click : function() {
					var me = this;
					var NewSaleItemWindowForBg = me.getNewSaleItemWindowForBg();
					var itemGrid = NewSaleItemWindowForBg.queryById("saleItemGridForBg");
				    var store = itemGrid.getStore();
					
					var records = itemGrid.getSelectionModel().getSelection();
					if(records.length>0){
						var NewBgWindow = me.getNewBgWindow();
                    	var	bgGrid = NewBgWindow.queryById("bgGrid");
						Ext.Array.each(records, function(r) {
							
							var bgStore = bgGrid.getStore();
							var storeCount = bgStore.getCount();
							var existFlag = false;
							for (var i = 0; i <storeCount; i++) {
								var record = bgStore.getAt(i);
								if(record.data.saleItemId==r.get('id')){
									existFlag = true;
								}
							}
							if(!existFlag){
								var model = Ext.create("SMSWeb.model.bg.Bg2Model");
								
								model.set('posex', r.get('posex'));
								model.set('matnr', r.get('matnr'));
								model.set('mtart', r.get('mtart'));
								model.set('maktx', r.get('maktx'));//产品描述
								
								
								model.set('serialNumber', r.get('serialNumber'));
								model.set('name', r.get('name'));
								model.set('spec', r.get('spec'));
								model.set('area', r.get('area'));
								model.set('unit', r.get('unit'));
								model.set('colour', r.get('colour'));
								model.set('colourDesc', r.get('colourDesc'));
								model.set('itemDesc', r.get('itemDesc'));
								model.set('remark', r.get('remark'));
								model.set('isSale', r.get('isSale'));
								model.set('type', r.get('type'));
								model.set('itemDesc', r.get('itemDesc'));
								model.set('unitPrice', r.get('unitPrice'));
								model.set('zheKou', r.get('zheKou'));
								model.set('zheKouJia', r.get('zheKouJia'));
								model.set('amount', r.get('amount'));
								model.set('touYingArea', r.get('touYingArea'));
								model.set('totalPrice', r.get('totalPrice'));
								model.set('status', r.get('status'));
								model.set('materialHeadId', r.get('materialHeadId'));
								model.set('isStandard', r.get('isStandard'));
								model.set('myGoodsId', r.get('myGoodsId'));
								model.set('saleItemId', r.get('id'));
								bgStore.insert(storeCount, model);
							}
						});
						NewSaleItemWindowForBg.close();
					}else{
						
					}
					
				}
			},
			
			
			"NewSysBzWindow button[itemId=confirm]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSysBzWindow = me.getNewSysBzWindow();
					var sysBzForm = NewSysBzWindow.queryById("sysBzForm");
					var remarks = sysBzForm.getForm().findField("sysBzForm_remarks").getValue();

					var NewBgWindow = me.getNewBgWindow();
					var newBgWindowForm = NewBgWindow.queryById("bgForm");
					var orderType = newBgWindowForm.getForm().findField("orderType").getValue();
					var itemGrid = NewBgWindow.queryById("bgGrid");
				    var itemStore = itemGrid.getStore();
				    var i = itemStore.getCount();
				    if(i == 0){
/*				    	Ext.MessageBox.alert("提示信息","至少一条行项目");
						return;*/
				    }
				    
					Ext.Ajax.request({
							url : 'main/bg/submit',
							params : {"id":NewSysBzWindow.formId,
									"status":NewSysBzWindow.formStatus,
									"title":NewSysBzWindow.formMsg,
									"step":NewSysBzWindow.formStep,
									"remarks":remarks},
							method : 'POST',
							frame : true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								
								var NewBgWindow = me.getNewBgWindow();
								var bgForm = NewBgWindow.queryById("bgForm");
								bgForm.getForm().findField("orderStatus").setValue(values.data.orderStatus);
								bgForm.getForm().findField("orderStatus").initValue();
								
								var BgGridView = me.getBgGridView();
								BgGridView.getStore().loadPage(1);
								
								var formValues = bgForm.getValues();
								if(values.data.orderStatus=="A"){
									Ext.getCmp("saveBg").show();
									Ext.getCmp("addBgItem").show();
									Ext.getCmp("removeBgItem").show();
									Ext.getCmp("openSaleWindow").show();
									Ext.getCmp("submitBg").show();
									Ext.getCmp("submitBg2").hide();
									Ext.getCmp("resubmitBg").hide();
								}else if(values.data.orderStatus=="B"){
									Ext.getCmp("saveBg").hide();
									Ext.getCmp("addBgItem").hide();
									Ext.getCmp("removeBgItem").hide();
									Ext.getCmp("openSaleWindow").hide();
									Ext.getCmp("submitBg").hide();
									Ext.getCmp("submitBg2").show();
									Ext.getCmp("resubmitBg").show();
									Ext.Object.each(formValues, function(key, value, myself) {
										bgForm.getForm().findField(key).setReadOnly(true);
									});
								}else if(values.data.orderStatus=="C"){
									Ext.getCmp("saveBg").hide();
									Ext.getCmp("addBgItem").hide();
									Ext.getCmp("removeBgItem").hide();
									Ext.getCmp("openSaleWindow").hide();
									Ext.getCmp("submitBg").hide();
									Ext.getCmp("submitBg2").hide();
									Ext.getCmp("resubmitBg").hide();
									Ext.Object.each(formValues, function(key, value, myself) {
										bgForm.getForm().findField(key).setReadOnly(true);
									});
								}
								if("2"==NewBgWindow.queryBgType || "3"==NewBgWindow.queryBgType){
									Ext.getCmp("submitBg2").hide();
									Ext.getCmp("resubmitBg").hide();
								}else if("1"==NewBgWindow.queryBgType){
									Ext.getCmp("saveBg").hide();
									Ext.getCmp("submitBg").hide();
									Ext.getCmp("addBgItem").hide();
									Ext.getCmp("removeBgItem").hide();
									Ext.getCmp("openSaleWindow").hide();
									Ext.Object.each(formValues, function(key, value, myself) {
										bgForm.getForm().findField(key).setReadOnly(true);
									});
								}
								
								Ext.MessageBox.alert("提示信息",values.data.msg);
								NewSysBzWindow.close();
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","网络异常");
							}
						});
				}
			}
			
		});
	},
	views : ['bg.BgMainView','bg.BgFormView','bg.BgGridView','sale.NewSaleWindowInnerContent','cust.NewCustWindowInnerContent'],
	stores : ['bg.BgStore'],
	models : ['bg.BgModel']
});

function showSaleWin(code){
	Ext.create('SMSWeb.view.sale.NewSaleWindow',
	     {formId : code,title:'订单查看',editFlag:false}).show();
}

function showCustWin(code){
	Ext.create('SMSWeb.view.cust.NewCustWindow',
	     {formId : code,title:'客户信息',editFlag:false}).show();
}

function showSysBzWin(id,status,msg,step){
	Ext.create('SMSWeb.view.bg.NewSysBzWindow',
	     {formId : id,formStatus:status,formMsg:msg,formStep:step,title:'备注'}).show(/*this,function(){}*/);
}