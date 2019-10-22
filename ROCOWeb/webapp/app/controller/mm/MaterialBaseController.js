var pr04Total=0;
Ext.define("SMSWeb.controller.mm.MaterialBaseController", {
	extend : 'Ext.app.Controller',
	refs : [{
				ref : 'NewMaterialBaseWindow',
				selector : 'NewMaterialBaseWindow' 
			},
			{
				ref : 'MaterialMainBaseView',
				selector : 'MaterialMainBaseView' 
			},
			{
				ref : 'MaterialMainGridBaseView',
				selector : 'MaterialMainGridBaseView' 
			},
			{
				ref : 'MaterialFileUploadBaseWindow',
				selector : 'MaterialFileUploadBaseWindow' 
			},
			{
				ref : 'MateriaPropertyWindow',
				selector : 'MateriaPropertyWindow' 
			},
			{
				ref : 'MyGoodsMainGridView',
				selector : 'MyGoodsMainGridView' 
			},
			{
				ref : 'NewSaleWindowInnerContent',
				selector : 'NewSaleWindowInnerContent' 
			},
			{
				ref : 'MaterialBase2SaleWindow',
				selector : 'MaterialBase2SaleWindow' 
			}
	],
	init : function() {
		
		this.control({
			//同步 SAP
			"MaterialMainBaseView button[id=syncMatnr]" : {
				click : function() {
					var me = this;
					var myMask = new Ext.LoadMask(Ext.getBody(),{msg:"请稍等..."}); 
					myMask.show();
					
					var mainView = me.getMaterialMainBaseView();
					var maingrid = mainView.queryById('MaterialMainGridBaseView_itemId');
					
					Ext.Ajax.request({
						url : 'main/mm/syncMatnr',
						method : 'GET',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							
							if(values.success){
								maingrid.getStore().load();
								Ext.MessageBox.alert("提示","同步成功！");
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){ myMask.hide();}
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","同步失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				}
			},
			//新增商品
			"MaterialMainBaseView button[id=newMaterial]" : {
				click : function() {
					Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
							     {loadStatus:"1",title:'新增商品'}).show();
				}
			},
			//删除商品
			"MaterialMainBaseView button[id=deleteMaterial]" : {
				click : function() {
					var me = this;
					var ids = [];
					var grid = me.getMaterialMainGridBaseView();
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								ids.push(r.get('id'))
							});
					if(ids.length>0){
						Ext.Ajax.request({
							url : 'main/mm/deleteMaterialHeadByIds',
							params : {
								ids : ids
							},
							method : 'POST',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								
								if(values.success){
									grid.getStore().remove(records);
									grid.getStore().load();
									Ext.MessageBox.alert("提示","删除成功！");
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
								
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","删除失败！");
							}
						});
					}
				}
			},
			//查询商品
			"MaterialMainBaseView button[id=queryMaterial]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialMainBaseView();
					
//					var mainform = mainView.queryById('MaterialMainFormBaseView_itemId');
//					var formValues = mainform.getValues();
					
					var maingrid = mainView.queryById('MaterialMainGridBaseView_itemId');
				    var store = maingrid.getStore();
				    
				    store.loadPage(1);
//				    store.loadPage(1,{params:formValues});
				}
			},
			//编辑商品
			'MaterialMainGridBaseView':{
				editButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var headId = record.data.id;//主表ID
					Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow', 
								{loadStatus:"1",formId : headId,title:'标准产品'}).show();
				}
			},
			//审核状态Imos绘图--删除物料数据
			"NewMaterialBaseWindow button[itemId=deleteImos]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var newWindow = me.getNewMaterialBaseWindow();
					
					if(newWindow.orderCodePosex!=null && newWindow.orderCodePosex!=""){
						Ext.Ajax.request({
							url : 'main/mm/deleteImos',
							params : {orderId:newWindow.orderCodePosex},
							method : 'GET',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								
								if(values.success){
									
									var uxgrid_ = Ext.ComponentQuery.query("NewMaterialBaseWindow tabpanel[itemId=wuliaoTabpanel_ItemId] uxgrid");
						           
						        	for(var i=0;i<uxgrid_.length;i++){
						            	uxgrid_[i].getStore().reload();
					                }
									
									Ext.MessageBox.alert("提示","删除成功！");
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","删除失败！");
							}
						});
					}
				}
			},
			//保存商品主表
			"NewMaterialBaseWindow button[itemId=saveMaterial]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var newWindow = me.getNewMaterialBaseWindow();
					if("newSaleContentWindow"==newWindow.sourceShow){
						//添加到订单类型   处理代码块
//						alert("下单新增界面");
					}else{
					//newWindow.queryById("saveMaterial").setDisabled(true);
					var _loadStatus = newWindow.loadStatus;
					var _flowInfo = newWindow.flowInfo;//订单审核信息
					var _flowInfo2 = newWindow.flowInfo2;//物料审核信息
//					var _saveStatus;
					//验证
					var validate = beforeSaveMaterialValidate(newWindow);
					if(validate==false){
						newWindow.queryById("saveMaterial").setDisabled(false);
						return;
					}
					//head信息
					var headForm = newWindow.queryById("headForm_ItemId");
					var formValues={};
					
				    //销售价格
				    var saleItemFormValue={};
				    saleItemFormValue['id'] = newWindow.saleItemId;
				    
				    var salePriceGridJson = [];
				    var salePriceForm;
				    var salePriceGrid;
			    	var salePriceGridStore;
			    	
			    	//产线价格保存
			    	var priceLineGrid;
		    		var priceLineGridStore;
		    		var priceLineGridJson = [];
			    	
			    	var saleView;
					var saleItemGrid;
					var saleForm;
					var _shouDaFang;
					var errRea;
					var errType;
					var errDesc;
//				    var propertyGrid;
				    
				    var itemGrid;
			    	var itemGridJson = [];
			    	//附加信息
			    	var form1;
			    	var form1Values={};
				    if('1'==_loadStatus){
				    	var _id = headForm.getForm().findField("id").getValue();
				    	var _price = headForm.getForm().findField("price").getValue();
				    	var _textureOfMaterial = headForm.getForm().findField("textureOfMaterial").getValue();
				    	var _matkl2=headForm.getForm().findField("matkl2").getValue();
						formValues['id'] = _id;
						formValues['price'] = _price;
						formValues['loadStatus'] = _loadStatus;
						formValues['price'] = _price;//炸弹价格
						formValues['textureOfMaterial'] = _textureOfMaterial;//材质
						formValues['matkl2']=_matkl2;
				    	//附加信息
						var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
				    	var fujiaFormFields = fujiaForm.getForm().getFields();
		    			fujiaFormFields.each(function(field) {
		    				var _name1 = field.getName();
		    				formValues[_name1] = field.getValue();
		     		    });
				    	
				    }else if('3'==_loadStatus){//更新saliItem，saliItemPrice表
				    	saleView = me.getNewSaleWindowInnerContent();
			    		saleItemGrid = saleView.queryById('saleGrid');
			    		saleForm = saleView.queryById("saleForm");
			    		var _isCnc=headForm.getForm().findField("isCnc");
			    		pingDesc(headForm);//拼 规格描述，物料描述
				    	formValues = headForm.getForm().getValues();
				    	var _serialNumber = headForm.getForm().findField("serialNumber").getValue();
				    	formValues['serialNumber'] = _serialNumber;
				    	
				    	var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
				    	var fujiaFormFields = fujiaForm.getForm().getFields();
		    			fujiaFormFields.each(function(field) {
		    				var _name1 = field.getName();
		    				if("errType"==_name1){
		    					errType = field.getValue();
		    				}else if("errRea"==_name1){
		    					errRea = field.getValue();
		    				}else if("errDesc"==_name1){
		    					errDesc = field.getValue();
		    				}else{
		    					formValues[_name1] = field.getValue();
		    				}
		     		    });
				    	if("0"==_flowInfo.docStatus){
				    		//附加信息
				    		form1 = newWindow.queryById("form1_ItemId");
					    	form1Values = form1.getForm().getValues();
				    	}else if("1"==_flowInfo.docStatus){
				    	
				    		//订单审汇提交附加信息
				    		if("gp_drawing"==_flowInfo.taskGroup && _flowInfo.assignee==true){
				    			
				    			/*var _drawType = fujiaForm.getForm().findField("drawType").getValue();
				    			if(_drawType==null || _drawType==""){
				    				var mainTabpanel = newWindow.queryById("mainTabpanel_ItemId");
				    				mainTabpanel.setActiveTab(fujiaForm); 
				    				Ext.MessageBox.alert("提示","请选择绘图类型！");
				    				newWindow.queryById("saveMaterial").setDisabled(false);
				    				return;
				    			}*/
				    			var errMsg = "";
				    			fujiaForm.isValid();
				    			var fujiaFormFieldsValid = fujiaForm.getForm().getFields();
				    			fujiaFormFieldsValid.each(function(field) {
				    		         Ext.Array.forEach(field.getErrors(), function(error) {
				    		             errMsg += field.getFieldLabel()+":"+error+"<br/>"
				    		         });
				    		    });
				    			if(errMsg!=""){
				    				var mainTabpanel = newWindow.queryById("mainTabpanel_ItemId");
				    				mainTabpanel.setActiveTab(fujiaForm); 
				    				Ext.MessageBox.alert('提示信息',errMsg);
				    				newWindow.queryById("saveMaterial").setDisabled(false);
				    				return;
				    			}
				    			
				    	    //订单审价
				    		}else if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
				    			if("true"==IS_MONEY){
					    			salePriceForm = newWindow.queryById("salePriceForm_ItemId");
						    		
						    		var _amount = salePriceForm.getForm().findField("amount").getValue();
						    		var _totalPrice = salePriceForm.getForm().findField("totalPrice").getValue();
						    		//组合saliItem
						    		saleItemFormValue['amount'] = _amount;
						    		saleItemFormValue['totalPrice'] = _totalPrice;
						    		
						    		//组合saliItemPrice
						    		salePriceGrid = newWindow.queryById("salePriceGrid_ItemId");
						    		salePriceGridStore= salePriceGrid.getStore();
						    		var salePriceGridValues = salePriceGridStore.getRange(0,salePriceGridStore.getCount());
						    		
						    		for (var i = 0; i <salePriceGridStore.getCount(); i++) {
						    			var arrJson = salePriceGridValues[i].getData();
						    			salePriceGridJson.push(arrJson);
						    		}
						    		
						    		priceLineGrid = newWindow.queryById("priceLine_itemId");
						    		priceLineGridStore = priceLineGrid.getStore();
						    		var priceLineGridValues = priceLineGridStore.getRange(0,priceLineGridStore.getCount());
						    		for (var i = 0; i<priceLineGridStore.getCount();i++) {
						    			var arrJson = priceLineGridValues[i].getData();
						    			priceLineGridJson.push(arrJson);
						    		}
						    		_shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
						    		
					    		}
				    		}else if("gp_store"==_flowInfo.taskGroup && _flowInfo.assignee==true){
				    			//附加信息
					    		form1 = newWindow.queryById("form1_ItemId");
						    	form1Values = form1.getForm().getValues();
				    		}else if(!_isCnc.isDisabled()){
				    			//如果行项目处于物料审核,且user为组长,那么允许修改
				    			saleItemFormValue['isCnc'] = _isCnc.getValue();
				    		}
				    	}
				    }else if('2'==_loadStatus){
				    	pingDesc(headForm);//拼 规格描述，物料描述
				    	formValues = headForm.getForm().getValues();
				    	var _serialNumber = headForm.getForm().findField("serialNumber").getValue();
				    	formValues['serialNumber'] = _serialNumber;
				    	//附加信息
				    	form1 = newWindow.queryById("form1_ItemId");
				    	form1Values = form1.getForm().getValues();
				    }else if('4'==_loadStatus){
				    	var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
				    	if("gp_drawing_2020"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){
				    		var _id = headForm.getForm().findField("id").getValue();
							formValues['id'] = _id;
							var _imosPath = fujiaForm.getForm().findField("imosPath").getValue();
							formValues['imosPath'] = _imosPath;
							formValues['loadStatus'] = _loadStatus;
							formValues['flowStatus'] = "gp_drawing_2020";
				    	}else if("gp_store"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){//子流程-客户起草
				    		
				    		pingDesc(headForm);//拼 规格描述，物料描述
					    	formValues = headForm.getForm().getValues();
					    	var _serialNumber = headForm.getForm().findField("serialNumber").getValue();
					    	formValues['serialNumber'] = _serialNumber;
					    	formValues['flowStatus'] = "gp_store";
					    	//附加B
					    	var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
					    	var fujiaFormFields = fujiaForm.getForm().getFields();
			    			fujiaFormFields.each(function(field) {
			    				var _name1 = field.getName();
			    				formValues[_name1] = field.getValue();
			     		    });
			    			
			    			//附加信息
					    	form1 = newWindow.queryById("form1_ItemId");
					    	form1Values = form1.getForm().getValues();
					    	//数量
					    	if("true"==IS_MONEY){
				    			salePriceForm = newWindow.queryById("salePriceForm_ItemId");
					    		
					    		var _amount = salePriceForm.getForm().findField("amount").getValue();
					    		//组合saliItem
					    		saleItemFormValue['amount'] = _amount;
				    		}
				    		
				    	}else if("gp_drawing"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){//子流程-重新审汇
				    		
				    		pingDesc(headForm);//拼 规格描述，物料描述
					    	formValues = headForm.getForm().getValues();
					    	var _serialNumber = headForm.getForm().findField("serialNumber").getValue();
					    	formValues['serialNumber'] = _serialNumber;
					    	formValues['flowStatus'] = "gp_drawing";
				    		
				    		var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
					    	var fujiaFormFields = fujiaForm.getForm().getFields();
			    			fujiaFormFields.each(function(field) {
			    				var _name1 = field.getName();
			    				formValues[_name1] = field.getValue();
			     		    });
				    		
			    			var _drawType = fujiaForm.getForm().findField("drawType").getValue();
			    			if(_drawType==null || _drawType==""){
			    				var mainTabpanel = newWindow.queryById("mainTabpanel_ItemId");
			    				mainTabpanel.setActiveTab(fujiaForm); 
			    				Ext.MessageBox.alert("提示","请选择绘图类型！");
			    				newWindow.queryById("saveMaterial").setDisabled(false);
			    				return;
			    			}
			    			//数量
			    			if("true"==IS_MONEY||"gp_drawing"==newWindow.sourceShow){
				    			salePriceForm = newWindow.queryById("salePriceForm_ItemId");
					    		
					    		var _amount = salePriceForm.getForm().findField("amount").getValue();
					    		//组合saliItem
					    		saleItemFormValue['amount'] = _amount;
				    		}
				    		
				    	}else if("gp_material"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){//add by hzm 2016.12.3
				    		var isCncValue = headForm.getForm().findField("isCnc").getValue();
				    		saleItemFormValue['isCnc'] = isCncValue;
				    	}else if("gp_bg_material"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){
				    		var isCncValue = headForm.getForm().findField("isCnc").getValue();
				    		saleItemFormValue['isCnc'] = isCncValue;
				    	}else if("gp_drawing_imos"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){//add by hzm 2017.04.19 IMOS绘图时允许修改CNC
				    		var isCncValue = headForm.getForm().findField("isCnc").getValue();
				    		saleItemFormValue['isCnc'] = isCncValue;
				    		pingDesc(headForm);//拼 规格描述，物料描述
				    		formValues['loadStatus'] = "5";
				    		formValues['flowStatus'] = "gp_drawing_imos";
				    	}
				    }
				    
					//组合json数据
					var gridData = Ext.encode({
						 saleItemFj:form1Values,
						 shouDaFang:_shouDaFang,
			        	 materialHead : formValues,
			        	 saleItem:saleItemFormValue,
			        	 saleItemPrices:salePriceGridJson,
			        	 materialPrices:priceLineGridJson,
			        	 errType:errType,
			        	 errRea:errRea,
			        	 errDesc:errDesc
			        	 
					});
					
//					if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
//						
//					var tmepElement = "";
//	
//					//console.log("*********OR8*************");
//					//工厂内部自用单OR8,对应销售价格定价条件:产品免费的"小计"和"总计"不能为0
//					if(saleView != undefined){
//						if(saleView.orderType == 'OR8'){
//							for(element in salePriceGridJson){
//									//console.log(salePriceGridJson[element].type);
//								   if(salePriceGridJson[element].type=='PR05'){//PR03指产品免费
//										if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//											tmepElement = 'PR05';
//									}
//								}
//							}
//						}	
//					}							
//					//console.log("*********YX******");
//					//console.log(formValues.createTime);
//					//保存前，有价格政策的客户，判断在“销售价格信息”栏的“产品折扣”或“活动折扣”上是否有值  add by hzm 20170210
//                    //先查下价格政策表，显示要有哪些折扣类型，如：产品折扣、活动折扣。
//					Ext.Ajax.request({
//						url:'main/mm/getDiscountKunnr',
//						params:{
//						    'id' : newWindow.saleHeadId
//						},
//						method:'GET',
//			            async:false,			
//						success : function(response,opts){
//						    var json = Ext.decode(response.responseText);
//						    var jsonstore = json.data;
//						    for(elementJ in jsonstore){
//						    	//console.log(jsonstore[element].DISCOUNT_STYLE2);//hdzk 对应  PR03,cpzk 对应  PR04
//						    	//将此销售订单在价格政策里的折扣类型，如hdzk和cpzk
//						    	//先查客户有哪些价格政策，再查明细里“产品折扣”，“活动折扣”行里，对应的小计数为0.
//						    	//tempstore.push(jsonstore[elementJ].DISCOUNT_STYLE2);
//						    	if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'CPZK'){
//						    		//循环销售订单里的两种折扣类型，进行总价判断
//						    		for(element in salePriceGridJson){
//										//console.log(salePriceGridJson[element].type);
//										if(salePriceGridJson[element].type=='PR03'){//PR03指产品折扣
//										   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//											   //Ext.MessageBox.alert("提示","请维护订单产品折扣！");
//											   tmepElement = 'PR03';
//										   }
//										}
//									}
//						    		
//						    	}else if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'HDZK'){
//						    		
//						    		//循环销售订单里的两种折扣类型，进行总价判断
//						    		for(element in salePriceGridJson){
//										//console.log(salePriceGridJson[element].type);
//										if(salePriceGridJson[element].type=='PR04'){//PR04指活动折扣
//										   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//											   //Ext.MessageBox.alert("提示","请维护订单活动折扣！");
//											   tmepElement = 'PR04';
//										   }
//										}
//									}
//						    	}
//						    }
//						    
//						},
//						failure : function(response,opts){
//						} 
//					});
//					
//					if(tmepElement == 'PR03'){
//						Ext.MessageBox.alert("提示","请维护订单产品折扣！");
//						return;
//					}else if(tmepElement == 'PR04'){
//						Ext.MessageBox.alert("提示","请维护订单活动折扣！");
//						return;
//					}else if(tmepElement == 'PR05'){
//						Ext.MessageBox.alert("提示","工厂内部自用单OR8,对应销售价格定价条件选错,请检查！");
//						return;
//					}
//					}
					
					var myMask = new Ext.LoadMask(newWindow,{msg:"请稍等..."}); 
					myMask.show();
					 
					Ext.Ajax.request({
						url : 'main/mm/saveBase',
						jsonData : gridData,
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){
								
								var _formId = values.data.id;
//								priceGridStore.load({params:{'pid':_formId}});
								//刷新grid
								if('1'==_loadStatus){
									var mainView = me.getMaterialMainBaseView();
									var maingrid = mainView.queryById('MaterialMainGridBaseView_itemId');
									maingrid.getStore().loadPage(1);
									
								}else{
									//modify by hzm 2016.12.5  修改CNC选项，保存后，界面内容没了，把下句注释掉就有了
									//headForm.getForm().setValues(values.data);
									
									newWindow.queryById("filesTabpanel_ItemId").setDisabled(false);
									if('2'==_loadStatus){
										var grid = me.getMyGoodsMainGridView();
										grid.getStore().load();
										//加载附加信息
										var _myGoodsId = values.data.myGoodsId;
										loadfjForm(form1,_myGoodsId);
										
										setFileTypeStatus(newWindow,values.data.fileType);
									}else if('3'==_loadStatus){
										if("0"==_flowInfo.docStatus){
											//刷新saleItemGird
											saleItemGrid.getStore().load({
												params:{'pid':newWindow.saleHeadId},
												callback: function(records, operation, success) {}
											});
											//加载附加信息
											var _myGoodsId = values.data.myGoodsId;
											loadfjForm(form1,_myGoodsId);
											
											setFileTypeStatus(newWindow,values.data.fileType);
										}else if("1"==_flowInfo.docStatus){//订单审价
											if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
												if("true"==IS_MONEY){
													salePriceGridStore.load({params:{'pid':newWindow.saleItemId}});
													//刷新saleItemGird
													saleItemGrid.getStore().load({
														params:{'pid':newWindow.saleHeadId},
														callback: function(records, operation, success) {
															var itemCount = saleItemGrid.getStore().getCount();
															var allTotalPrice = 0;
															for (var i = 0; i <itemCount; i++) {
																var record = saleItemGrid.getStore().getAt(i);
																if("QX" != record.get("stateAudit")){
																	allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
																}
															}
															var fuFuanCond = saleForm.getForm().findField("fuFuanCond").getValue();
															var fuFuanMoney = 0;
															if(fuFuanCond!=null){
																if(fuFuanCond=="1"){
																	fuFuanMoney = allTotalPrice;
																}else if(fuFuanCond=="2"){
																	fuFuanMoney = 0;
																}else if(fuFuanCond=="3"){
																	fuFuanMoney = accMul(allTotalPrice,0.5);
																}else if(fuFuanCond=="4"){
																	fuFuanMoney = accMul(allTotalPrice,0.3);
																}
															}
															saleForm.getForm().findField("orderTotal").setValue(allTotalPrice);
															saleForm.getForm().findField("orderTotal").initValue();
															saleForm.getForm().findField("fuFuanMoney").setValue(fuFuanMoney);
															saleForm.getForm().findField("fuFuanMoney").initValue();
														}
													});
													newWindow.queryById("priceLine_itemId").getStore().reload();
												}
											//订单审绘
											}else if("gp_drawing"==_flowInfo.taskGroup && _flowInfo.assignee==true){
												saleItemGrid.getStore().load({
													params:{'pid':newWindow.saleHeadId},
													callback: function(records, operation, success) {}
												});
											//起草
											}else if("gp_store"==_flowInfo.taskGroup && _flowInfo.assignee==true){
												//刷新saleItemGird
												saleItemGrid.getStore().load({
													params:{'pid':newWindow.saleHeadId},
													callback: function(records, operation, success) {}
												});
												//加载附加信息
												var _myGoodsId = values.data.myGoodsId;
												loadfjForm(form1,_myGoodsId);
												
												setFileTypeStatus(newWindow,values.data.fileType);
											}
										}
									}else if('4'==_loadStatus){
										//起草
										if("gp_store"==_flowInfo2.taskGroup && _flowInfo2.assignee==true){
											setFileTypeStatus(newWindow,values.data.fileType);
										}
									}
								}
							    
								Ext.MessageBox.alert("提示","保存成功！");
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							headForm.getForm().findField("loadStatus").setValue(_loadStatus);
							if (myMask != undefined){ myMask.hide();}
							newWindow.queryById("saveMaterial").setDisabled(false);
						},
						failure : function(response, opts) {
							console.log(response);
							headForm.getForm().findField("loadStatus").setValue(_loadStatus);
							Ext.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
							newWindow.queryById("saveMaterial").setDisabled(false);
						}
					});
			        
				}}
			},
			//文件上传窗口
			"MaterialFileUploadBaseWindow":{
				fileUploadButtonClick:function(){
				    var me = this;
				    var fileUploadWindow = me.getMaterialFileUploadBaseWindow();
				    var fileUploadForm = fileUploadWindow.queryById("fileUploadForm"); 
				    var file = fileUploadForm.getForm().findField("file").getValue();
				    if("newSaleContentWindow"==fileUploadWindow.sourceShow){
				    	 var type=fileValidatePDFOrKIT(file);
				    	 if(type){
				    		 var fileType = fileUploadForm.getForm().findField("fileType").setValue(type);
						    	if(fileUploadForm.getForm().isValid()){  
				                	fileUploadForm.submit({  
				                        url: 'main/mm/fileuploadFb',
				                        waitMsg: '上传文件中...',  
				                        success: function(form, action) {
				                        	var values = Ext.decode(action.response.responseText);	
				                        	if(values.success){
				                        		//保存成功后刷新对应grid
			                        		    var pid = fileUploadWindow.formId;
					                            Ext.MessageBox.alert("提示","上传成功！");
					                           
					                            Ext.Ajax.request({
					        						url : 'main/mm/queryMHAndFJById?id='+pid,
					        						async:false,
					        						dataType: "json",
					        						success : function(response, opts) {
					        							var values = Ext.decode(response.responseText);
					        							if(values.success){ 
					        							var formData=values.data;
				        							    var form=Ext.getCmp("FBMaterialForm"); 
					        							form.getForm().setValues(formData);
					        							Ext.getCmp("fileDestroy").setDisabled();
					        							Ext.getCmp("saveFB-flag").setValue("Y");
					        							}
					        						},
					        						failure : function(response, opts) {
					        							Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
					        						}
					        					});
					                            
					                            var grid = Ext.getCmp("FB_FileGridItem");
					                            grid.getStore().load({params:{'pid':pid}});
					            				
					                            fileUploadWindow.close();
				                        	}else{
				                        		Ext.MessageBox.alert("提示",values.msg);
				                        	}
				                        	
				                        },
				                        failure : function(form, action) {
				                        	var values = Ext.decode(action.response.responseText);	
				                        	if(values.msg){
				                        		Ext.MessageBox.alert("提示",values.msg);
				                        	}else{
				                        		Ext.MessageBox.alert("提示","上传失败！");
				                        	}
										}
				                    });  
				                }
						    
				    	 }
				    }else{
			    	var newWindow = me.getNewMaterialBaseWindow();
				    var fileType = fileUploadForm.getForm().findField("fileType").getValue();
				    //check pdf   updated by Mark  on 2017-08-31 --start
				    var _filePath=fileUploadForm.getForm().findField("file").getValue();
				    var _splits=_filePath.split('\\');
				    var pdfname=_splits[_splits.length-1];
				     
				     var saleheadid = newWindow.saleHeadId;
				    var obj = Ext.ux.DataFactory.getFlowActivityId(saleheadid);
				    var judge=false;
				    if(obj!=null){
				    	var taskdefId = obj.taskdefId;
				    	//控制PDF文件是否重复控制，只针对订单审绘环节
				    	if(taskdefId == 'usertask_drawing'){
				    		judge=true;
				    	}
					    var name = obj.taskName;
				    }
				    
				    
				    var pid = fileUploadWindow.formId;
				    var orderCodePosex = newWindow.orderCodePosex;
				    var flag=false;
				    if(orderCodePosex){
				    var ordercode = orderCodePosex.substr(0,13);
				    
				    flag = fileValidate(fileType,file,ordercode,pdfname,judge);
				    }
				    //check pdf   updated by Mark  on 2017-08-31 --end
				    if(flag || !orderCodePosex){
				    	if(fileUploadForm.getForm().isValid()){  
		                	fileUploadForm.submit({  
		                        url: 'main/mm/fileupload',  
		                        waitMsg: '上传文件中...',  
		                        success: function(form, action) {
		                        	var values = Ext.decode(action.response.responseText);	
		                        	if(values.success){
		                        		//保存成功后刷新对应grid
			                            Ext.MessageBox.alert("提示","上传成功！");
			                            
			                            var fileType = fileUploadWindow.fileType;
			                            var pid = fileUploadWindow.formId;
			                            
			                            var grid = newWindow.queryById(fileType +"_gridItem");
			                            grid.getStore().load({params:{'fileType':fileType,'pid':pid}});
			            				//上传了XML文件之后,更新imosPath的显示
			                            var form= newWindow.queryById("fujiaForm_ItemId");
			                            form.getForm().findField("imosPath").setValue(values.msg);
			                            
			                            fileUploadWindow.close();
		                        	}else{
		                        		Ext.MessageBox.alert("提示",values.msg);
		                        	}
		                        	
		                        },
		                        failure : function(form, action) {
		                        	var values = Ext.decode(action.response.responseText);	
		                        	if(values.msg){
		                        		Ext.MessageBox.alert("提示",values.msg);
		                        	}else{
		                        		Ext.MessageBox.alert("提示","上传失败！");
		                        	}
								}
		                    });  
		                }
				    }
				    }
				}
			},
			"NewMaterialBaseWindow":{
				//文件上传
				fileUploadButtonClick:function(info,headId,_saleFor){
					var me = this;
					var newWindow = me.getNewMaterialBaseWindow();
					var headForm = newWindow.queryById("headForm_ItemId");
					headId = headForm.getForm().findField("id").getValue();
					if(headId!=null && headId.length>0){
						var _title;
						if("PICTURE"==info){
							_title = "图片文件上传";
						}else if("PRICE"==info){
							_title = "报价清单上传";
						}else{
							_title = info+"文件上传";
						}
						Ext.create('SMSWeb.view.mm.base.MaterialFileUploadBaseWindow',{fileType:info,formId : headId,title:_title,saleFor:_saleFor}).show();						
					}
				},
				//文件下载
				fileDownloadButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var id = record.data.id;
					window.location.href = basePath+'main/mm/fileDownload'+"?id="+id;  
				},
				//文件删除
				fileDeleteButtonClick:function(info,grid){
					var me = this;
					var ids = [];
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								ids.push(r.get('id'))
							});
					if(ids.length>0){
				        Ext.MessageBox.confirm('提示信息','确定更改文件为无效？',
						    function(btn){
						        if(btn=='yes'){
						        	
						        	Ext.Ajax.request({
										url : 'main/mm/deleteMaterialFileByIds',
										params : {
											'type':info,
											ids : ids
										},
										method : 'POST',
										success : function(response, opts) {
											var values = Ext.decode(response.responseText);	
											
											if(values.success){
												grid.getStore().remove(records);
												var newWindow = me.getNewMaterialBaseWindow();
												var headForm = newWindow.queryById("headForm_ItemId");
												var _id = headForm.getForm().findField("id").getValue();

												grid.getStore().load({params:{'fileType':info,'pid':_id}});
												Ext.MessageBox.alert("提示","更改成功！");
											}else{
												Ext.MessageBox.alert("提示",values.errorMsg);
											}
										},
										failure : function(response, opts) {
											Ext.MessageBox.alert("提示","更改失败！");
										}
									});
						        	
						        }else{
						          
						        }
						});
					}
				},
				//定价条件增加
				priceAddButtonClick:function(){
					var me = this;
					var newWindow = me.getNewMaterialBaseWindow();
					var grid = newWindow.queryById("priceGrid_ItemId");
					var store = grid.getStore();
					var count = store.getCount();
					 	
					var model = Ext.create("SMSWeb.model.mm.pc.PriceConditionModel");
					store.insert(count, model);
				},
				//定价条件删除
				priceDeleteButtonClick:function(){
					var me = this;
					var ids = [];
					var newWindow = me.getNewMaterialBaseWindow();
					var grid = newWindow.queryById("priceGrid_ItemId");
					
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								if(r.get('id')){
									ids.push(r.get('id'))
								}
							});
					
					if(ids.length>0){
						Ext.Ajax.request({
							url : 'main/mm/deleteMaterialPriceConditionByIds',
							params : {
								ids : ids
							},
							method : 'POST',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								
								if(values.success){
									grid.getStore().remove(records);
									//head信息
									var headForm = newWindow.queryById("headForm_ItemId");
									var _id = headForm.getForm().findField("id").getValue();
									grid.getStore().load({params:{'pid':_id}});
									Ext.MessageBox.alert("提示","删除成功！");
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","删除失败！");
							}
						});
					}else{
						grid.getStore().remove(records);
					}
				},
				//属性添加
				addPropertyButtonClick:function(){
					 var me = this;
					 var newWindow = me.getNewMaterialBaseWindow();
					 var grid = newWindow.queryById("property_gridItemId");
					 var store = grid.getStore();
					 var model = Ext.create("SMSWeb.model.mm.base.MaterialPropertyBaseModel");
					 var count = store.getCount();
					 if(count<6){
						model.set("orderby",count+1);
						store.insert(count, model);
					 }
					 
				},
				//属性删除
				deletePropertyButtonClick:function(){
					var me = this;
					var ids = [];
					var newWindow = me.getNewMaterialBaseWindow();
					//head信息
					var headForm = newWindow.queryById("headForm_ItemId");
					var _id = headForm.getForm().findField("id").getValue();
					
					var grid = newWindow.queryById("property_gridItemId");
					
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								if(r.get('id')){
									ids.push(r.get('id'))
								}
							});
					
					if(ids.length>0){
						Ext.Ajax.request({
							url : 'main/mm/deleteMaterialPropertyByIds',
							params : {
								ids : ids,
								pid : _id
							},
							method : 'POST',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								
								if(values.success){
									headForm.getForm().findField("propertyDesc").setValue("");
									
									grid.getStore().remove(records);
									grid.getStore().load({params:{'pid':_id}});
									Ext.MessageBox.alert("提示","删除成功！");
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
								
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","删除失败！");
							}
						});
					}else{
						grid.getStore().remove(records);
					}
					
				},
				//属性配置
				configPropertyButtonClick:function(){
					var me = this;
					var newWindow = me.getNewMaterialBaseWindow();
					var headForm = newWindow.queryById("headForm_ItemId");
					var _id = headForm.getForm().findField("id").getValue();
					var _propertyDesc = headForm.getForm().findField("propertyDesc").getValue();
					
					var jsonResult;
					Ext.Ajax.request({
						url : 'main/mm/queryDataDicts',
						method : 'GET',
						params : {
							'id' : _id
						},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							jsonResult = Ext.decode(response.responseText);  
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
					});

					if(jsonResult.length==0){
						Ext.MessageBox.alert("提示","该产品没有配置属性！");
					}else{
						Ext.create('SMSWeb.view.mm.base.MateriaPropertyWindow', 
								{jsonResult:jsonResult,formId : _id,propertyDesc:_propertyDesc,title:'属性配置'}).show();
					}
					
				}
			},
			//属性配置功能  事件
			"MateriaPropertyWindow tabpanel grid":{
				selectionchange : function( model, selected, eOpts ){
					var me = this;
					
					var newWindow = me.getNewMaterialBaseWindow();
					var headForm = newWindow.queryById("headForm_ItemId");
					var _pid = headForm.getForm().findField("id").getValue();
					
					var propertyView = me.getMateriaPropertyWindow();
					
					if(propertyView.status){
						var southGrid = propertyView.queryById('southGrid');
						
						var grids = propertyView.query('tabpanel>grid');
						
			            var arrayInfor = new Array();//盛放每组选中的CheckBox值的对象
			            var bCheck = true;//是否全选
						
						//循环遍历动态生成tabpanel标签内的表格的选中的数据，如果有一个没有选中则清空数据
						for(var i=0;i<grids.length;i++){
							var selectedRows = grids[i].getSelectionModel().getSelection();
							var _itemId = grids[i].getItemId();
							
							var temp = new Array();
							
							for(var x = 0; x < selectedRows.length ;x++){
								var _id= selectedRows[x].get('id');
								var _name = selectedRows[x].get('name');
								var key = _itemId+"_"+_id+"_"+_name;
								temp.push(key);
							}
							arrayInfor.push(temp);
							
			                if (temp.join() == ""){
			                    bCheck = false;
			                }
						}
						
						if (bCheck) {
							var zuheDate = me.doExchange(arrayInfor);
//							console.log(zuheDate);
							southGrid.getStore().removeAll();
							if (zuheDate.length > 0) {
								for(var i=0;i<zuheDate.length;i++){
									var cell_array = zuheDate[i].split(",");

									var _model = {};
									var _model2 = {};
									for(var k=0; k<cell_array.length;k++){
										var _keyArray = cell_array[k].split("_");
										var _key = _keyArray[0];
										var _keyItemid = _keyArray[1];
										var _keyItemName = _keyArray[2];
										_model[_key] = _keyItemid;
										_model2[_key] = _keyItemid;
										_model[_key+"desc"] = _keyItemName;
									}
									//查询
									var _text;
									
									Ext.Ajax.request({
										url : 'main/mm/queryMaterialPropertyItem2?pid='+_pid,
										method : 'GET',
										params : _model2,
										async:false,
										dataType : "json",
										contentType : 'application/json',
										success : function(response, opts) {
											_text = Ext.decode(response.responseText);
										},
										failure : function(response, opts) {
											_text = Ext.decode(response.responseText);
										}
									});
									
									if(_text.success){
										if("0"==_text.msg){
											_model.price=0;
											_model.num=0;
										}else{
											_model.price = _text.data.price;
											_model.num = _text.data.num;
//											_model.id = _text.data.id;
										}
										southGrid.getStore().insert(southGrid.getStore().getCount(),_model);
									}else{
										Ext.MessageBox.alert("提示",_text.errorMsg);
										propertyView.close();
									}
								}
							}
						}else{
							southGrid.getStore().removeAll();
						}
					}
				}
			},
			//保存属性
		    "MateriaPropertyWindow button[itemId=saveMateriaProperty]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var MateriaPropertyWindow = me.getMateriaPropertyWindow();
					
					
					var formId = MateriaPropertyWindow.formId;
					
					var grid = MateriaPropertyWindow.queryById("southGrid");
					var store = grid.getStore();
				    var gridValues = store.getRange(0,store.getCount());
				    
				    var itemValues = [];
				    for (var i = 0; i <store.getCount(); i++) {
				    	var _json = gridValues[i].getData();
//				    	console.log(_json);
				    	var _newjson = {};
				    	for(var _key in _json){  
			            	if (_key.indexOf('desc')<0){
			            		_newjson[_key] = _json[_key];
			            	}
				        } 
//				    	console.log(_newjson);
				    	itemValues.push(_newjson);
				    }
				    
				    var _propertyDescString = MateriaPropertyWindow.propertyDesc;
				    
				    var head = {id:formId,propertyDesc:_propertyDescString};
					var gridData = Ext.encode({
						 materialHead : head
			        	,materialPropertyItems : itemValues
					});
//			        console.log(gridData);
					var myMask = new Ext.LoadMask(MateriaPropertyWindow,{msg:"请稍等..."}); 
					myMask.show();
					Ext.Ajax.request({
						url : 'main/mm/saveMaterialPropertyItem',
						method : 'POST',
						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						async:false,
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							
							if(values.success){
								var newWindow = me.getNewMaterialBaseWindow();
								
								var headForm = newWindow.queryById("headForm_ItemId");
								headForm.getForm().findField("propertyDesc").setValue(values.data.propertyDesc);
								
								var propertyGrid = newWindow.queryById("property_gridItemId");
								var propertyGridStore= propertyGrid.getStore();
								propertyGridStore.load({params:{'pid':formId}});
								
								store.removeAll();
								var _propertyItem ;
								Ext.Ajax.request({
									url : 'main/mm/queryMaterialPropertyItem',
									method : 'GET',
									params : {
										'pid' : formId
									},
									async:false,
									dataType : "json",
									contentType : 'application/json',
									success : function(response, opts) {
										_propertyItem = Ext.decode(response.responseText);  
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示","加载数据失败！");
									}
								});
								store.loadData(_propertyItem);
								Ext.MessageBox.alert("提示","保存成功！");
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){ myMask.hide();}
						},
						failure : function(response, opts) {
							var t = Ext.decode(response.responseText);
							Ext.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				    
				}
			}
			,"NewMaterialBaseWindow button[itemId=downloadMaterialPrice]":{
				click:function( bt, e, eOpts ){
					var me = this;
					var materialBaseWindow = me.getNewMaterialBaseWindow();
					var headForm = materialBaseWindow.queryById("headForm_ItemId");
					var orderCodePosex = headForm.getForm().findField("saleOrder").getValue();
					window.location.href=basePath + "main/mm/downloadMaterialPrice?orderCodePosex="+orderCodePosex;
				}
			}
			,"NewMaterialBaseWindow button[itemId=uploadMaterialPrice]":{
				click:function( bt, e, eOpts ){
					var me = this;
					var materialBaseWindow = me.getNewMaterialBaseWindow();
					var headForm = materialBaseWindow.queryById("headForm_ItemId");
					var orderCodePosex = headForm.getForm().findField("saleOrder").getValue();
					var materialPriceFileView=Ext.create("SMSWeb.view.mm.sale.MaterialPriceFileUploadBaseWindow",{formId:orderCodePosex});
					materialPriceFileView.show();
				}
			}
			,"NewMaterialBaseWindow button[itemId=downloadTemplate]":{
				click:function( bt, e, eOpts ){
					var me = this;
					var materialBaseWindow = me.getNewMaterialBaseWindow();
					var headForm = materialBaseWindow.queryById("headForm_ItemId");
					var orderCodePosex = headForm.getForm().findField("saleOrder").getValue();
					window.location.href=basePath + "main/mm/downloadTemplate?orderCodePosex="+orderCodePosex;
				}
			}
		});
	},
	//组合数组
	doExchange: function (doubleArrays) {
        var len = doubleArrays.length;
        if (len >= 2) {
            var arr1 = doubleArrays[0];
            var arr2 = doubleArrays[1];
            var len1 = doubleArrays[0].length;
            var len2 = doubleArrays[1].length;
            var newlen = len1 * len2;
            var temp = new Array(newlen);
            var index = 0;
            for (var i = 0; i < len1; i++) {
                for (var j = 0; j < len2; j++) {
                    temp[index] = arr1[i] + "," + arr2[j];
                    index++;
                }
            }
            var newArray = new Array(len - 1);
            newArray[0] = temp;
            if (len > 2) {
                var _count = 1;
                for (var i = 2; i < len; i++) {
                    newArray[_count] = doubleArrays[i];
                    _count++;
                }
            }
            return this.doExchange(newArray);
        }else {
            return doubleArrays[0];
        }
    },
    
    calculationPrice: function (grid,num,_saleHeadId,matnr) {
    	var store = grid.getStore();
    	var totalGrid=0;
    	var subtotalGrid=0;
    	var subtotal=0;
    	var total=0;
    	var shengYu=0;
    	var zheKou=0;
    	//销售道具
    	if("102999995"!=matnr){
			Ext.Ajax.request({
				url : 'main/sale/getCustItem?saleHeadId='+_saleHeadId,
				method : 'POST',
				async:false,
				success : function(response, opts) {
					var values = Ext.decode(response.responseText);
					shengYu=values.data.shengYu;
					zheKou=values.data.zheKou;
				},
				failure : function(response, opts) {
					alert(response.responseText);
				}
			});
    	}
	    for (var i = 0; i <store.getCount(); i++) {
	    	var model=store.getAt(i);
	    	
	    	var plusOrMinus=model.get("plusOrMinus");
	    	var condition=model.get("condition");
	    	var conditionValue=model.get("conditionValue");
	    	var isTakeNum=model.get("isTakeNum");
			if(model.get("type")=='PR04')
			{
//				conditionValue=zheKou; 2016-06-02 注销 界面可以修改折扣
				pr04Id=model.get("id");
				Ext.Ajax.request({
					url : 'main/sale/getPR04?id='+pr04Id,
					method : 'POST',
					async:false,
					success : function(response, opts) {
						var values = Ext.decode(response.responseText);
						pr04Total=values.data.PR04;
					},
					failure : function(response, opts) {
						alert(response.responseText);
					}
				});
			}
			subtotal=this.conOperation(subtotalGrid,condition,conditionValue);
			
			if(isTakeNum==1)
			{
				
				total=subtotal*num;
			}else
			{
				total=subtotal;
			}
			if(model.get("type")=='PR04')
			{
				if(shengYu<total-pr04Total)					
				{
					conditionValue=0.0;
					subtotal=0.0;
					total=0.0;
				}
			}
			if(plusOrMinus==1)
			{
				totalGrid=totalGrid+total;
				subtotalGrid=subtotalGrid+subtotal;
			}else
			{
				totalGrid=totalGrid-total;
				subtotalGrid=subtotalGrid-subtotal;
			}
			model.set("conditionValue",conditionValue);
			subtotal = parseFloat(subtotal.toFixed(2));
			model.set("subtotal",subtotal);
			
			total = parseFloat(total.toFixed(2));
			model.set("total",total);		
	    }
	    
	    totalGrid = parseFloat(totalGrid.toFixed(2));
	    return 	totalGrid;
    },
    conOperation: function (subtotal,condition,conditionValue) {
    	if(condition==1)
    	{
    		return conditionValue;
    	}else if(condition==2){
    		return conditionValue;
    	}else if(condition==3){
    		return subtotal*conditionValue;
    	}else if(condition==4){
    		return subtotal/conditionValue;
    	}
    },
	views : ['mm.base.MaterialMainBaseView','mm.base.MaterialMainGridBaseView','mm.base.MaterialMainFormBaseView','mm.sale.MaterialPriceFileUploadBaseWindow'],
	stores : ['mm.base.Store4MaterialBase'],
	models : []
});

/**
 * 非标文件上传验证文件类型 KIT和PDF
 * @param file
 * @returns
 */
function fileValidatePDFOrKIT(file){
	if(file==null||file==""){
		Ext.MessageBox.alert("提示","请选择文件！");
    	return false;
	}
    var text = file.substr(file.lastIndexOf(".")).toLowerCase();
    var b;
    var val;
     
	b=/.(kit|rar|zip)$/;
	val = text.match(b);
	if(val!=null){
	    return "KIT";
	} 
   
	b=/.(pdf)$/;
	val = text.match(b);
	if(val!=null){
	    return "PDF";	
	}
    b=/.(dwg)$/;
    val = text.match(b);
    if(val!=null){
	    return "DWG";	
	}
    if(val==null){
    	Ext.MessageBox.alert("提示","文件格式不正确！");
    	return false;
    } 
}

function fileValidate(fileType,file,ordercode,pdfname,judge){
	//judge为true,证明是订单审绘环节，要控制PDF是否重复上传
	if(judge == true){
			var temp=null;
			Ext.Ajax.request({
											url : 'main/mm/judgeHasDoublePdf',
											params : {
												'ordercode' : ordercode,
												'pdfname' :pdfname
											},
											method : 'GET',
											async:false,
											success : function(response, opts) {
												var jsonResult = Ext.decode(response.responseText);
												if(jsonResult.success){
												}else{
													temp = jsonResult.errorMsg;
												}
											},
											failure : function(response, opts) {
											}
					    });	
		//----------
		if(temp!=null){
			Ext.MessageBox.alert("提示",temp);
			return false;
		}
	}
	
	if(file==null||file==""){
		Ext.MessageBox.alert("提示","请选择文件！");
    	return false;
	}
    var text = file.substr(file.lastIndexOf(".")).toLowerCase();
    var b;
    if("PICTURE"==fileType){
    	b=/.(jpg|png|gif|bmp|jpeg)$/;
    }else if("KIT"==fileType){
    	b=/.(kit|rar|zip)$/;
    }else if("PDF"==fileType){
    	b=/.(pdf)$/;
    }else if("XML"==fileType){
    	b=/.(xml)$/;
    }else if("CNC"==fileType){
    	b=/.(cnc)$/;
    }
    var val = text.match(b);
    if(val==null){
    	Ext.MessageBox.alert("提示","文件格式不正确！");
    	return false;
    }else{
    	return true;
    }
}

function returnGridJson(newWindow,itemId){
	var itemGridJson = [];
	//item属性
	var itemGrid = newWindow.queryById(itemId);
	var itemGridStore= itemGrid.getStore();
	
	var itemGridValues = itemGridStore.getRange(0,itemGridStore.getCount());
	
	for (var i = 0; i <itemGridStore.getCount(); i++) {
		var arrJson = itemGridValues[i].getData();
		itemGridJson.push(arrJson);
	}
	return itemGridJson;
}
/**
 * 保存之前校验
 */
function beforeSaveMaterialValidate(newWindow){
	var _loadStatus = newWindow.loadStatus;
	var errMsg = "";
	var mainTabpanel = newWindow.queryById("mainTabpanel_ItemId");
	var headForm = newWindow.queryById("headForm_ItemId");
	headForm.isValid();
	
	var headFormFields = headForm.getForm().getFields();
	headFormFields.each(function(field) {
         Ext.Array.forEach(field.getErrors(), function(error) {
             errMsg += field.getFieldLabel()+":"+error+"<br/>"
         });
    });
	
	var form1 = newWindow.queryById("form1_ItemId");
	if("1"!=_loadStatus){
		form1.isValid();
		var form1Fields = form1.getForm().getFields();
		form1Fields.each(function(field) {
	         Ext.Array.forEach(field.getErrors(), function(error) {
	             errMsg += field.getFieldLabel()+":"+error+"<br/>"
	         });
	    });
	}
	if(errMsg!=""){
		mainTabpanel.setActiveTab(headForm); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	
	if("3"==_loadStatus){
		if("true"==IS_MONEY){
			var salePricePanel = newWindow.queryById("salePricePanel_ItemId");
			var salePriceForm = newWindow.queryById("salePriceForm_ItemId");
			salePriceForm.isValid();
			
			var salePriceFormFields = salePriceForm.getForm().getFields();
			salePriceFormFields.each(function(field) {
				Ext.Array.forEach(field.getErrors(), function(error) {
					errMsg += field.getFieldLabel()+":"+error+"<br/>"
				});
			});
			
			if(errMsg!=""){
				mainTabpanel.setActiveTab(salePricePanel); 
				Ext.MessageBox.alert('提示信息',errMsg);
				return false;
			}
		}
	}
	//add by Mark on 20160412--start
	//审绘移门方数填写后移门扇数必填
	if("非标产品"==newWindow.title){
		var fujiaForm = newWindow.queryById("fujiaForm_ItemId");
		var ymfs=fujiaForm.getForm().findField("zzymfs").getValue();
		var ymss=fujiaForm.getForm().findField("zzymss").getValue();
		if(ymfs>0 && ymss<=0){
			Ext.MessageBox.alert("提示信息","移门扇数填写错误！");
			return false;
		}else
		if(ymss>0 && ymfs<=0){
			Ext.MessageBox.alert("提示信息","移门方数填写错误！");
			return false;
		}
	}
	//add by Mark on 20160412--end
	return true;
}
//非标 拼规格，描述信息
function pingDesc(headForm){
	var _longDesc = headForm.getForm().findField("longDesc").getValue();
	var _widthDesc = headForm.getForm().findField("widthDesc").getValue();
	var _heightDesc = headForm.getForm().findField("heightDesc").getValue();
	var _groesDesc = "W"+ _widthDesc + "XH" + _heightDesc + "XD"+ _longDesc;
	//规格 
	headForm.getForm().findField("groes").setValue(_groesDesc);
	
	//产品分类
	var _matkl = headForm.getForm().findField("matkl");
	var _matklFind = _matkl.getStore().findRecord("id",_matkl.getValue());
	var _matklDesc = _matklFind.get('text');
	
	var _color = headForm.getForm().findField("color");
	var _colorDesc = "";
	if(_color.getValue()){
		var _colorFind = _color.getStore().findRecord("id",_color.getValue());
		_colorDesc = _colorFind.get('text');
	}
	//工艺分类
/*	var _processCls=headForm.getForm().findField("processCls");
	var _processClsDesc="";
	if(_processCls.getValue()){
		var _processClsFind=_processCls.getStore().findRecord("id",_processCls.getValue());
		_processClsDesc=_processClsFind.get("text");
	}*/
	var _textureOfMaterial = headForm.getForm().findField("textureOfMaterial");
	var _textureOfMaterialDesc = "";
	if(_textureOfMaterial.getValue()){
		var _textureOfMaterialFind = _textureOfMaterial.getStore().findRecord("id",_textureOfMaterial.getValue());
		_textureOfMaterialDesc = _textureOfMaterialFind.get('text');
	}
	
	//描述
	var _maktx;
	var _saleFor=headForm.getForm().findField("saleFor").getValue();
	if("2"!=_saleFor){//Chaly加上了对木门的判断
		//石台不需要更新描述
		_maktx= _matklDesc +_textureOfMaterialDesc + _colorDesc + _groesDesc;
		headForm.getForm().findField("maktx").setValue(_maktx);
	}
	if("3"==_saleFor){//Chaly加上了对木门的判断
		//mumen1需要更新描述
		_maktx= _textureOfMaterialDesc +_matklDesc+ _colorDesc + _groesDesc;
		headForm.getForm().findField("maktx").setValue(_maktx);
	}
	
}
//附件类型2020，只上传kit文件(订单审汇后上传一个pdf文件)
//附件类型pdf，只上传pdf文件
function setFileTypeStatus(window,fileTypeVal,flowInfo){
	var filesTabpanel = window.queryById("filesTabpanel_ItemId");
	var pdfGrid = filesTabpanel.queryById("PDF_gridItem");
	var kitGrid = filesTabpanel.queryById("KIT_gridItem");
	
	if("1"==fileTypeVal){
		pdfGrid.setDisabled(true);
		kitGrid.setDisabled(false);
	}else if("2"==fileTypeVal){
		pdfGrid.setDisabled(false);
		kitGrid.setDisabled(true);
	}
}