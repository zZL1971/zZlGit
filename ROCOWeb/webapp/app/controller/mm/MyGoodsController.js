Ext.define("SMSWeb.controller.mm.MyGoodsController", {
	extend : 'Ext.app.Controller',
	refs : [
	        {
				ref : 'MyGoodsMainView',
				selector : 'MyGoodsMainView' 
			},
			{
				ref : 'MyGoodsMainGridView',
				selector : 'MyGoodsMainGridView' 
			},
	        {
				ref : 'MaterialBase2SaleWindow',
				selector : 'MaterialBase2SaleWindow' 
			},
			{
				ref : 'MaterialMainGridBase2SaleView',
				selector : 'MaterialMainGridBase2SaleView' 
			},
			{
				ref : 'MaterialBZWindow',
				selector : 'MaterialBZWindow' 
			},{
				ref : 'NewSaleWindowInnerContent',
				selector : 'NewSaleWindowInnerContent' 
			},{
				ref : 'MaterialSJWindow',
				selector : 'MaterialSJWindow' 
			},{
				ref : 'MaterialBJWindow',
				selector : 'MaterialBJWindow' 
			},{
				ref : 'MyGoodsBujianMainView',
				selector : 'MyGoodsBujianMainView' 
			},{
				ref : 'MyGoodsBujianMainGridView',
				selector : 'MyGoodsBujianMainGridView' 
			},{
				ref : 'MaterialFileUploadWindow',
				selector : 'MaterialFileUploadWindow' 
			},{
				ref:'MaterialBGWindow',
				selector:'MaterialBGWindow'
			}
	],
	init : function(){
		this.control({
			//查询我的物品
			"MyGoodsMainView button[id=queryMyGoods]" : {
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsMainView();
					
//					var mainform = mainView.queryById('MyGoodsMainFormView_itemId');
//					var formValues = mainform.getValues();
					
					var maingrid = mainView.queryById('MyGoodsMainGridView_itemId');
				    var store = maingrid.getStore();
				    
				    store.loadPage(1);
//				    store.loadPage(1,{params:formValues});
				}
			},
			//查询我的物品 --客服补件
			"MyGoodsBujianMainView button[id=queryMyGoods]" : {
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsBujianMainView();
					
					var maingrid = mainView.queryById('MyGoodsBujianMainGridView_itemId');
				    var store = maingrid.getStore();
				    
				    store.loadPage(1);
				}
			},
			//查询标准产品列表
			"MyGoodsMainView button[id=queryMyGoodsBZ]":{
				click : function( bt, e, eOpts ) {
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询标准产品',type:'BZ'}).show();
				}
			},
			//销售道具
			"MyGoodsMainView button[id=newXS]":{
				click : function( bt, e, eOpts ) {
					var matnr ='102999995';
					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
							{loadStatus:'2',matnr : matnr,title:'销售道具'}).show();
				}
			},
			//五金散件
			"MyGoodsMainView button[id=newWJ]":{
				click : function( bt, e, eOpts ) {
					var matnr = '102999996';
					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
							{loadStatus:'2',matnr : matnr,title:'五金散件'}).show();
				}
			},
			//移门散件
			"MyGoodsMainView button[id=newYMSJ]":{
				click : function( bt, e, eOpts ) {
					var matnr = '102999997';
					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
							{loadStatus:'2',matnr : matnr,title:'移门散件'}).show();
				}
			},
			//柜身散件
			"MyGoodsMainView button[id=newGSSJ]":{
				click : function( bt, e, eOpts ) {
					var matnr = '102999994';
					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
							{loadStatus:'2',matnr : matnr,title:'柜身散件'}).show();
				}
			},
			//客服补购
			"MyGoodsBujianMainView button[id=newKFBG]":{
				click : function( bt, e, eOpts ) {
					var matnr = '102999998';
					Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
							{loadStatus:'2',matnr : matnr,title:'客服补购',type:'OR3'}).show();
				}
			},
			//免费订单
			"MyGoodsBujianMainView button[id=newMFDD]":{
				click : function( bt, e, eOpts ) {
					var matnr = '102999998';
					Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
							{loadStatus:'2',matnr : matnr,title:'免费订单',type:'OR4'}).show();
				}
			},
			//查询非标准产品
			"MaterialBase2SaleWindow button[itemId=queryMaterialBase2Sale]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					var _kunnr = mainView.kunnr;
					var _orderType = mainView.orderType;
					var mainform = mainView.queryById('MaterialMainFormBase2SaleView_itemId');
					var formValues = mainform.getValues();
					
					var maingrid = mainView.queryById('MaterialMainGridBase2SaleView_itemId');
				    var store = maingrid.getStore();
				    store.loadPage(1,{params:{kunnr:_kunnr}});
				    
//				    if("BZ"==mainView.type){
//				    	formValues['type']="BZ";
//				    }else{
//				    	formValues['type']="SJ";
//				    	formValues['matnr']=mainView.matnr;
//				    }
//				    store.loadPage(1,{params:formValues});
				}
			},//新增非标产品
			"MaterialBase2SaleWindow button[itemId=addMMFb]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					var _kunnr = mainView.kunnr;
					var _orderType = mainView.orderType;
					var _form=mainView.queryById("MaterialMainFormBase2SaleView_itemId").getForm();
					var _saleFor=_form.findField("saleFor").getValue();
					Ext.create('SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
							{loadStatus:"2",title:'新增非标产品',sourceShow:"newSaleContentWindow",saleFor:_saleFor,kunnr:_kunnr,orderType:_orderType}).show();
				}
			},//删除非标产品
			"MaterialBase2SaleWindow button[itemId=deleteMMFb]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					var grid = mainView.queryById('MaterialMainGridBase2SaleView_itemId');
					var records = grid.getSelectionModel().getSelection();
					Ext.MessageBox.confirm('提示信息','确定要删除所选订单？',
					    	function(btn){
						        if(btn=='yes'){
						        	if(records.length>0){
										var ids = [];
										Ext.Array.each(records, function(r) {
											var orderCode = r.get('orderCode');
											var id = r.get('id');
											if(orderCode == null || orderCode == ""){
												ids.push(id);
											}
										});
										if(ids.length>0){
											Ext.Ajax.request({
												url : 'main/mm/deleteFbIds',
												params : {
													ids : ids
												},
												method : 'POST',
												success : function(response, opts) {
													var values = Ext.decode(response.responseText);
													if(values.success){
														Ext.MessageBox.alert("提示信息","删除成功");
														grid.getStore().reload();
													}else{
														Ext.MessageBox.alert("提示信息",values.errorMsg);
													}
												},
												failure : function(response, opts) {
													Ext.MessageBox.alert("提示信息","网络异常！");
												}
											});
										}else{
											Ext.MessageBox.alert("提示信息","删除失败：<br/>订单已提交流程审批！");
										}
									}else{
										Ext.MessageBox.alert("提示信息","请选择要删除的订单！");
									}
						        }
						 	}
						);
				}
			},
			
			//确认
			"MaterialBase2SaleWindow button[itemId=comfirm]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					var grid = mainView.queryById('MaterialMainGridBase2SaleView_itemId');
					var records = grid.getSelectionModel().getSelection();
					var flg=false;
					var ids = [];
					Ext.Array.each(records, function(r) {
						ids.push(r.get('serialNumber'));
					});
					if(mainView.type=="FB"){
						if(ids.length>0){
							Ext.Ajax.request({
								url : 'main/mm/checkFileStauts',
								params : {
									ids : ids
								},
								async:false,
								method : 'POST',
								success : function(response, opts) {
									var values = Ext.decode(response.responseText);
									if(values.success){
										flg=true;
										//Ext.MessageBox.alert("提示信息",values.msg);
										//grid.getStore().reload();
									}else{
										Ext.MessageBox.alert(values.errorCode,values.errorMsg);
									}
								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("提示信息","网络异常！");
								}
							});	
						}
					}else{
						flg=true;
					}
					if(flg){
						if("newSaleContentWindow"==mainView.sourceShow){
	//						添加到订单行项目   处理代码块
	//						console.info(records);
							var itemsGrid =Ext.getCmp("addSaleItemsGrid");
							if(itemsGrid==undefined){
								return;
							}
							Ext.Array.each(records, function(r) {
								var saleStore =itemsGrid.getStore();
								var storeCount = saleStore.getCount();
								var existFlag = false;
								if("BZ"!=mainView.type){//工程单，标准产品可以重复下
									for (var i = 0; i <storeCount; i++) {
										var record = saleStore.getAt(i);
										if(record.data.materialHeadId==r.get('id')){
											existFlag = true;
										}
									}
								}
								if(!existFlag){
									var model = Ext.create("SMSWeb.model.sale.SaleModel");
	//								debugger;
									var matnr=mainView.matnr;
									var unit='EA';//默认单位 传到SAP
									if(!matnr){
										//非散件
										matnr=r.get('matnr');
										unit=r.get("meins"); 
									}
									if("160000002"==matnr){
										var wjsj = Ext.getCmp("addOrderItemWJSJ");
										wjsj.setDisabled(true);
										var xsdj = Ext.getCmp("addOrderItemXSDJ");
										xsdj.setDisabled(true);
										var ymsj = Ext.getCmp("addOrderItemYMSJ");
										ymsj.setDisabled(true);
									}else{
										var rjs = Ext.getCmp("addOrderItemRJS");
										rjs.setDisabled(true);
									}
									//获取最大的行号
									var _maxPosex=0;
									saleStore.each(function(){
										var _thisPosex=Number(this.get("posex"));
										_maxPosex=_maxPosex>_thisPosex?_maxPosex:_thisPosex;
									});
									//最大行号+10 为新行的行号
									model.set('posex', Number(_maxPosex)+Number(10));//(storeCount+1)*10);
									model.set('matnr', r.get('matnr'));
									model.set('unit', unit);//设置单位
									model.set('saleFor', r.get("saleFor"));//设置单位
									model.set('mtart', r.get('mtart'));
	//								model.set('touYingArea', r.get('touYingArea'));
									model.set('maktx', r.get('maktx'));//产品描述
									model.set('materialHeadId', r.get('id'));//物料id
									var isStandard = r.get('isStandard');
									if("1"==isStandard){
								        model.set('materialPropertyItemInfo', r.get('materialPropertyItemInfo'));
								        model.set('materialPrice', r.get('materialPrice'));
									}
									model.set('isStandard', isStandard);//是否标准产品
									model.set('myGoodsId', r.get('id'));//我的商品id --流程激活后删除该表
	//								model.set('sanjianHeadId', r.get('sanjianHeadId'));//散件headID
									model.set('remark', r.get('zzazdr'));//加入订单行项目时的订单类型
									model.set('ortype', r.get('ortype'));//加入订单行项目时的订单类型
									model.set('amount', 1);
									saleStore.add(model);
								}
							});
							mainView.close();
						}else{
							
						var ids = [];
						Ext.Array.each(records, function(r) {
							ids.push(r.get('id'))
						});
	//					console.log(records);
						if("BZ"==mainView.type){
							if(ids.length==1){
								var record = records[0];
								var headId = record.get("id");
								
								Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
										{loadStatus:'2',formId : headId,title:'标准商品'}).show();
							}
							
						}else if("SJ"==mainView.type){
							
							if(ids.length>0){
								var sjwindow = me.getMaterialSJWindow();
								var itemGrid = sjwindow.queryById("itemGrid_ItemId");
								
								Ext.Array.each(records, function(r) {
									
									var itemStore = itemGrid.getStore();
									var storeCount = itemStore.getCount();
									var existFlag = false;
									for (var i = 0; i <storeCount; i++) {
										var record = itemStore.getAt(i);
										if(record.data.matnr==r.get('matnr')){
											existFlag = true;
										}
									}
									if(!existFlag){
										
										var count = itemStore.getCount();
										var model = Ext.create("SMSWeb.model.mm.base.MaterialSJModel");
										var _price =r.get('kbetr');
										var _amount = 1;
										var _zhekou = 1;
										model.set('matnr',r.get('matnr'));
										model.set('miaoshu',r.get('maktx'));
										model.set('price',_price);
										model.set('amount',_amount);
										model.set('zhekou',_zhekou);
										model.set('materialHeadId',r.get('id'));
										model.set('unit',r.get('meins'));
										
										var _totalPrice = _amount * _price * _zhekou;
			                			  
			                			_totalPrice = _totalPrice.toFixed(2);
			                		    _totalPrice = parseFloat(_totalPrice);
			                			
			                		    model.set('totalPrice',_totalPrice);
										
										itemStore.insert(count, model);
									}
									
								});
								mainView.close();
							}
							
						}
						
					}
				}
			}
			},
			//选择一个标准产品
			'MaterialMainGridBase2SaleView':{
				editButtonClick:function(grid,rowIndex,colIndex){
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					
					if("BZ"==mainView.type){
						var record = grid.getStore().getAt(rowIndex);
						var headId = record.data.id;//主表ID
						var mainView = me.getMaterialBase2SaleWindow();
						if("newSaleContentWindow"==mainView.sourceShow){//下单界面新增产品 隐藏附加信息
							Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
									{loadStatus:'2',buttonStatus:'1',formId : headId,title:'标准商品',sourceShow:"newSaleContentWindow"}).show();
						}else{
							Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
									{loadStatus:'2',buttonStatus:'1',formId : headId,title:'标准商品'}).show();
						
						}
					}else if("FB"==mainView.type){//非标产品
						var record = grid.getStore().getAt(rowIndex);
						var headId = record.data.id;//主表ID
						var mainView = me.getMaterialBase2SaleWindow();
						var _form=mainView.queryById("MaterialMainFormBase2SaleView_itemId").getForm();
						var _saleFor=record.data.saleFor;
						if("newSaleContentWindow"==mainView.sourceShow){//下单界面新增产品
							Ext.create('SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
									{loadStatus:"2",title:'编辑非标产品',saleFor:_saleFor,sourceShow:"newSaleContentWindow",uuId:headId}).show();
//							Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
//									{loadStatus:'2',buttonStatus:'1',formId : headId,title:'非标商品',sourceShow:"newSaleContentWindow"}).show();
						} 
					}
				
				}
			},
			//保存标准产品，新增一条记录 到我的物品表
			"MaterialBZWindow button[itemId=add2MyGoods]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var _BZWindow = me.getMaterialBZWindow();
					//验证
					var validate = beforeSaveBZValidate(_BZWindow);
					if(validate==false){
						return;
					}
					_BZWindow.queryById("add2MyGoods").setDisabled(true);
					var _propertyItems = _BZWindow.propertyItems;
					
					var headForm = _BZWindow.queryById("headForm_ItemId");
					
					var _id = headForm.getForm().findField("id").getValue();
					var _kzkfg = headForm.getForm().findField("kzkfg").getValue();//是否vc
					var _materialName = headForm.getForm().findField("maktx").getValue();
					
					//是否有配置属性
					var _materialPropertyItemId="";
					var _materialDesc="";
					
					var _materialPrice=0;
					var _materialPropertyItemInfo="";
					var _formValue;
					if("X"==_kzkfg){
						if(_propertyItems!=null && _propertyItems.length>0){
							var topForm =  _BZWindow.queryById("topForm_ItemId");
							//Ext.util.MixedCollection
							var coll = topForm.getForm().getFields();
							
							var _flag = true;
							var _priceArray = [];
							var _priceDescArray = [];
							for(var i=0;i<coll.getCount();i++){
								var _field = coll.getAt(i);
								
								var _fvalue = _field.getValue();
								
								if(_fvalue){
									var _fname = _field.getName();
									var _labelName= _field.getFieldLabel();
									
									var _find = _field.getStore().findRecord("id",_fvalue);
									var _fvalueDesc = _find.get('propertyDesc');
									var _price = _find.get('price');
									_materialPrice += _price;
									var _txt1 = "";
									_txt1 = _price+":"+_fname+":"+_labelName+":"+_fvalue+":"+_fvalueDesc;
									_priceArray.push(_txt1);
									
									//颜色 + ：+ 红色
									var _fLabel= _field.getFieldLabel();
									var _record = _field.store.findRecord("id",""+_fvalue);
									var _selectDesc = _record ? _record.get('propertyDesc') : '';
			 	            	    _priceDescArray.push(_selectDesc);
								}else{
									_flag = false;
								}
							}
							
							if(_flag){
								//info0_id1,info1_id2
								_materialPropertyItemInfo = _priceArray.join(",");
								_materialDesc += _materialName+",";
								_materialDesc += _priceDescArray.join(",");
								
//								_materialPropertyItemId = _BZWindow.materialPropertyItemId;
//								_materialPrice = _BZWindow.materialPrice;
								
							}else{
								Ext.MessageBox.alert("提示","请选择属性！");
								_BZWindow.queryById("add2MyGoods").setDisabled(false);
								return;
							}
							
							_formValue = {
									materialHeadId:_id,
									materialDesc:_materialDesc,
//									materialPropertyItemId :_materialPropertyItemId,
									materialPropertyItemInfo :_materialPropertyItemInfo,
									materialPrice :_materialPrice
							};
						}else{
							
							_materialDesc += _materialName;
							_formValue = {
									materialHeadId:_id,
									materialDesc:_materialDesc
							};
						}
						
					}else{
						
						_materialDesc += _materialName;
						_formValue = {
								materialHeadId:_id,
								materialDesc:_materialDesc
						};
					}
					
					var form1Values = {};
					var form1 = _BZWindow.queryById("form1_ItemId");
			    	form1Values = form1.getForm().getValues();
			    	
					//组合json数据
					var gridData = Ext.encode({
						 saleItemFj:form1Values,
						 myGoods:_formValue
					});
					
					var myMask = new Ext.LoadMask(_BZWindow,{msg:"请稍等..."}); 
					myMask.show();
					//保存到我的物品
					Ext.Ajax.request({
						url : 'main/myGoods/save',
						jsonData : gridData,
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							_BZWindow.queryById("add2MyGoods").setDisabled(false);
							if(values.success){
								var maingrid = me.getMyGoodsMainGridView();
							    maingrid.getStore().load();
							    _BZWindow.close();
							    me.getMaterialBase2SaleWindow().close();
								Ext.MessageBox.alert("提示","保存成功！");
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){ myMask.hide();}
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
							_BZWindow.queryById("add2MyGoods").setDisabled(false);
						}
					});
				}
			},
			//新增非标产品
			"MyGoodsMainView button[id=newMyGoodsFB]":{
				click : function( bt, e, eOpts ) {
					Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
							{loadStatus:"2",title:'新增非标产品'}).show();
				}
			},
			
			//编辑商品
			'MyGoodsMainGridView':{
				editButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var _id = record.data.id;
					
					var _values;
					
					Ext.Ajax.request({
						url : 'main/myGoods/getMyGoods',
						method : 'GET',
						params : {
							'id' : _id
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
					});
//					console.log(_values);

					if(_values.success){
						var _headId = record.data.materialHeadId;//主表ID
						var _sanjianHeadId = record.data.sanjianHeadId;//散件ID
						var _isStandard = record.data.isStandard;//是否标准
						var _propertyItemInfo = record.data.materialPropertyItemInfo;//可配置属性描述信息
						var _ortype = record.data.ortype;//订单类型
//						var _bujianId = record.data.bujianId;//补件ID
						
						if(_isStandard){
							if("1"==_isStandard){
								var _matnr = record.data.matnr;
								//散件
								if("102999995"==_matnr||"102999996"==_matnr||"102999997"==_matnr||"102999994"==_matnr){
									var _title = "";
									if("102999995"==_matnr){
										_title = "销售道具";
									}else if("102999996"==_matnr){
										_title = "五金散件";
									}else if("102999997"==_matnr){
										_title = "移门散件";
									}else if("102999994"==_matnr){
										_title = "柜身散件";
									}
									
									Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
											{loadStatus:'2',matnr : _matnr,formId:_sanjianHeadId,title:_title}).show();
									
								}else{
									//标准产品
									Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
											{loadStatus:'2',formId : _headId,propertyItemInfo:_propertyItemInfo,title:'标准商品',myGoodsId:_id}).show();
									
								}
								
							}else if("0"==_isStandard){
								
								Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
										{loadStatus:"2",formId:_headId,title:'非标产品',myGoodsId:_id}).show();
							}
						}else{
							
						}
					}else{
						Ext.MessageBox.alert("提示","加载数据失败！请重新查询");
					}

				}
			},
			//编辑商品--客服补购
			'MyGoodsBujianMainGridView':{
				editButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var _id = record.data.id;
					
					var _values;
					
					Ext.Ajax.request({
						url : 'main/myGoods/getMyGoods',
						method : 'GET',
						params : {
							'id' : _id
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
					});
					
					if(_values.success){
						var _headId = record.data.materialHeadId;//主表ID
						var _ortype = record.data.ortype;//订单类型
						var _bujianId = record.data.bujianId;//补件ID
						
						var _matnr = record.data.matnr;
						//补件
						if("102999998"==_matnr){
							if("OR3"==_ortype){
								Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
										{loadStatus:'2',matnr : _matnr,title:'客服补购',type:'OR3',formId:_bujianId}).show();
							}else if("OR4"==_ortype){
								Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
										{loadStatus:'2',matnr : _matnr,title:'免费订单',type:'OR4',formId:_bujianId}).show();
							}
						}
						
					}else{
						Ext.MessageBox.alert("提示","加载数据失败！请重新查询");
					}
					
				}
			},
			//删除
			"MyGoodsMainView button[id=deleteMyGoods]" : {
				click : function() {
					var me = this;
					var ids = [];
		            var grid = me.getMyGoodsMainGridView();
		            var records = grid.getSelectionModel().getSelection();
		            Ext.Array.each(records, function(r) {
		                  ids.push(r.get('id'))
		                });
		            
		            if(ids.length>0){
		            	Ext.MessageBox.confirm('提示信息','确定要删除所选商品？',
							    function(btn){
							        if(btn=='yes'){
								              Ext.Ajax.request({
									                url : 'main/myGoods/deleteByIds',
									                params : {
									                  loadStatus:'2',
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
							        }else{
							          
							        }
							});
		            }
				}
			},
			//删除--客服补件
			"MyGoodsBujianMainView button[id=deleteMyGoods]" : {
				click : function() {
					var me = this;
					var ids = [];
		            var grid = me.getMyGoodsBujianMainGridView();
		            var records = grid.getSelectionModel().getSelection();
		            Ext.Array.each(records, function(r) {
		                  ids.push(r.get('id'))
		                });
		            
		            if(ids.length>0){
		            	Ext.MessageBox.confirm('提示信息','确定要删除所选商品？',
							    function(btn){
							        if(btn=='yes'){
								              Ext.Ajax.request({
									                url : 'main/myGoods/deleteByIds',
									                params : {
									                  loadStatus:'2',
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
							        }else{
							          
							        }
							});
		            }
				}
			},
			"MaterialSJWindow ":{
				//添加
				addButtonClick:function(){
					var me = this;
					var sjwindow = me.getMaterialSJWindow();
					var _kunnr = sjwindow.kunnr;
					var _matnr = sjwindow.matnr;
					
					var _title = "";
					if("102999995"==_matnr){
						_title = "销售道具查询";
					}else if("102999996"==_matnr){
						_title = "五金散件查询";
					}else if("102999997"==_matnr){
						_title = "移门散件查询";
					}else if("102999994"==_matnr){
						_title = "柜身散件查询";
					}else if("102999998"==_matnr){
						_title = "费用化查询";
					}
					if("102999998"==_matnr){
						var itemGrid = sjwindow.queryById("itemGrid_ItemId");
						var itemStore = itemGrid.getStore();
						var model = Ext.create("SMSWeb.model.mm.base.MaterialSJModel");
						itemStore.insert(store.getCount(),model);
						return;
					}
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:_title,type:'SJ',matnr:_matnr,kunnr:_kunnr}).show();
				},
				//删除
				deleteButtonClick:function(){
					var me = this;
					var sjwindow = me.getMaterialSJWindow();
					var saleItemId = "";
					var salePriceGrid;
					var salePriceForm;
					if(sjwindow.saleItemId){
						saleItemId = sjwindow.saleItemId;
						salePriceGrid = sjwindow.queryById("salePriceGrid_ItemId");
						salePriceForm = sjwindow.queryById("salePriceForm_ItemId");
					}
					
					var grid = sjwindow.queryById("itemGrid_ItemId");
					
					var ids = [];
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								if(r.get('id')){
									ids.push(r.get('id'));
								}
							});
					if(ids.length>0){
						
						Ext.MessageBox.confirm('提示信息','确定要删除？',
							    function(btn){
					        if(btn=='yes'){
					        	
					        	Ext.Ajax.request({
									url : 'main/myGoods/deleteMaterialSJ',
									params : {
										'saleItemId':saleItemId,
										ids : ids
									},
									method : 'POST',
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);	
										
										if(values.success){
											var headForm = sjwindow.queryById("headForm_ItemId");
											var _formId = headForm.getForm().findField("id").getValue();
											
											grid.getStore().remove(records);
											grid.getStore().load({params:{'id':_formId}});
											Ext.MessageBox.alert("提示","删除成功！");
											
											if(sjwindow.saleItemId){
												salePriceGrid.getStore().load({params:{'pid':sjwindow.saleItemId}});
												
												Ext.Ajax.request({
													url : 'main/mm/getSaleItem',
													method : 'GET',
													params : {
														'id' : sjwindow.saleItemId
													},
													async:false,
													dataType : "json",
													contentType : 'application/json',
													success : function(response, opts) {
														var _saleItem = Ext.decode(response.responseText);
														if(_saleItem.success){
															salePriceForm.getForm().findField("amount").setValue(_saleItem.data.amount);
															salePriceForm.getForm().findField("totalPrice").setValue(_saleItem.data.totalPrice);
														}
													}
												});
											}
										}else{
											Ext.MessageBox.alert("提示",values.errorMsg);
										}
										
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示","删除失败！");
									}
								});
					        	
					        }
						});
					}else{
						grid.getStore().remove(records);
					}
					
				},
				//保存
				saveSanjian:function(){
					var me = this;
					var sjwindow = me.getMaterialSJWindow();
					sjwindow.queryById("saveSJ").setDisabled(true);
					var _loadStatus = sjwindow.loadStatus;
					var _flowInfo = sjwindow.flowInfo;
					var _matnr = sjwindow.matnr;
					var _formId = sjwindow.formId;
					var _bgOrderType = sjwindow.bgOrderType;
					var _kunnr = sjwindow.kunnr;
					
					var formValues = {};
					var headForm = sjwindow.queryById("headForm_ItemId");
					formValues = headForm.getForm().getValues();
					
					var _matnr = headForm.getForm().findField("matnr").getValue();
					formValues['matnr'] = _matnr;
					
				    //销售价格
				    var saleItemFormValue={};
				    var salePriceGridJson = [];
				    var salePriceForm;
				    var salePriceGrid;
			    	var salePriceGridStore;
			    	
					var saleView;
					var saleItemGrid;
					var saleForm;
					var _shouDaFang;
					//销售价格end
					
					var itemGrid = sjwindow.queryById("itemGrid_ItemId");
					var itemStore = itemGrid.getStore();
					if(itemStore.getCount()==0){
						Ext.MessageBox.alert("提示信息","必须有一条明细信息");
						sjwindow.queryById("saveSJ").setDisabled(false);
						return;
					}else{
						
						var itemStoreJson = [];
			    		var itemStoreValues = itemStore.getRange(0,itemStore.getCount());
			    		
			    		for (var i = 0; i <itemStore.getCount(); i++) {
			    			var arrJson = itemStoreValues[i].getData();
			    			itemStoreJson.push(arrJson);
			    		}
			    		
			    		if('3'==_loadStatus){
							saleView = me.getNewSaleWindowInnerContent();
							saleItemGrid = saleView.queryById('saleGrid');
							saleForm = saleView.queryById("saleForm");
							
							if(_flowInfo.docStatus){
								if("1"==_flowInfo.docStatus){
									if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
										var validate = beforeSaveSJValidate(sjwindow);
										if(validate==false){
											sjwindow.queryById("saveSJ").setDisabled(false);
											return;
										}
										salePriceForm = sjwindow.queryById("salePriceForm_ItemId");
							    		
							    		var _amount = salePriceForm.getForm().findField("amount").getValue();
							    		var _totalPrice = salePriceForm.getForm().findField("totalPrice").getValue();
							    		//组合saliItem
							    		saleItemFormValue['id'] = sjwindow.saleItemId;
							    		saleItemFormValue['amount'] = _amount;
							    		saleItemFormValue['totalPrice'] = _totalPrice;
							    		
							    		//组合saliItemPrice
							    		salePriceGrid = sjwindow.queryById("salePriceGrid_ItemId");
							    		salePriceGridStore = salePriceGrid.getStore();
							    		var salePriceGridValues = salePriceGridStore.getRange(0,salePriceGridStore.getCount());
							    		
							    		for (var i = 0; i <salePriceGridStore.getCount(); i++) {
							    			var arrJson = salePriceGridValues[i].getData();
							    			salePriceGridJson.push(arrJson);
							    		}
										//送达方
										_shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
									}
								}
							}
						}
			    		//组合json数据
						var gridData = Ext.encode({
							 shouDaFang:_shouDaFang,
							 saleItem:saleItemFormValue,
				        	 saleItemPrices:salePriceGridJson,
							 materialSanjianHead:formValues,
							 materialSanjians:itemStoreJson,
							 bgOrderType:_bgOrderType,
							 kunnr:_kunnr
						});
						
						var myMask = new Ext.LoadMask(sjwindow,{msg:"请稍等..."}); 
						myMask.show();
						
						Ext.Ajax.request({
							url : 'main/myGoods/saveSJ',
							jsonData : gridData,
							method : 'POST',
							frame:true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.success){
									if("newSaleContentWindow"==sjwindow.sourceShow){
										var _formId = values.data.id;
										headForm.getForm().setValues(values.data);
										itemGrid.getStore().load({params:{'id':_formId}});
										var grid = me.getMyGoodsMainGridView();
										if(grid){
											grid.getStore().load();
										}
									
										Ext.MessageBox.alert("提示","保存成功！");
									}else{
										var _formId = values.data.id;
										headForm.getForm().setValues(values.data);
										
										itemGrid.getStore().load({params:{'id':_formId}});
										
										if('2'==_loadStatus){
											var grid = me.getMyGoodsMainGridView();
											grid.getStore().load();
										}else if('3'==_loadStatus){
											if("0"==_flowInfo.docStatus){
												
											}else if("1"==_flowInfo.docStatus){
												if("gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
													if("true"==IS_MONEY){
														salePriceGridStore.load({params:{'pid':sjwindow.saleItemId}});
														//刷新saleItemGird
														saleItemGrid.getStore().load({
															params:{'pid':sjwindow.saleHeadId},
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
													}
												}
											}
										}
										Ext.MessageBox.alert("提示","保存成功！");
									}
									
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								if (myMask != undefined){ myMask.hide();}
								sjwindow.queryById("saveSJ").setDisabled(false);
							},
							failure : function(response, opts) {
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								Ext.MessageBox.alert("提示","保存失败！");
								if (myMask != undefined){ myMask.hide();}
								sjwindow.queryById("saveSJ").setDisabled(false);
							}
						});
						
					}
				}
				/*,
				updateSaleItem:function(type){
					var me = this;
					var _window = me.getMaterialSJWindow();
					me.updateSaleItemPrice(_window);
				}*/
			},
			"MaterialBZWindow":{
				updateSaleItem:function(type){
					var me = this;
					var _window = me.getMaterialBZWindow();
					var saleView = me.getNewSaleWindowInnerContent();
					//add by hzm 20170214
//								var tmepElement = "";
//								var salePriceGridJson = [];
//								var salePriceGrid;
//								var salePriceGridStore;
//								var newWindow = me.getMaterialBZWindow();
//								var headForm = newWindow.queryById("headForm_ItemId");
//								formValues = headForm.getForm().getValues();
//								salePriceGrid = newWindow.queryById("salePriceGrid_ItemId");
//						    	salePriceGridStore= salePriceGrid.getStore();
//						    	var salePriceGridValues = salePriceGridStore.getRange(0,salePriceGridStore.getCount());//返回指定索引范围内的记录.
//						    		
//						    		for (var i = 0; i <salePriceGridStore.getCount(); i++) {
//						    			var arrJson = salePriceGridValues[i].getData();
//						    			salePriceGridJson.push(arrJson);
//						    		}
//						    	//工厂内部自用单OR8,对应销售价格定价条件:产品免费的"小计"和"总计"不能为0
//								if(saleView != undefined){
//									if(saleView.orderType == 'OR8'){
//										for(element in salePriceGridJson){
//												//console.log(salePriceGridJson[element].type);
//											   if(salePriceGridJson[element].type=='PR05'){//PR03指产品免费
//													if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//														tmepElement = 'PR05';
//												}
//											}
//										}
//									}	
//								}	
//								//保存前，有价格政策的客户，判断在“销售价格信息”栏的“产品折扣”或“活动折扣”上是否有值  add by hzm 20170210
//			                    //先查下价格政策表，显示要有哪些折扣类型，如：产品折扣、活动折扣。
//								Ext.Ajax.request({
//									url:'main/mm/getDiscountKunnr',
//									params:{
//									   'id' : _window.saleHeadId
//									},
//									method:'GET',
//						            async:false,			
//									success : function(response,opts){
//									    var json = Ext.decode(response.responseText);
//									    var jsonstore = json.data;
//									    //var tempstore = [];
//									    for(elementJ in jsonstore){
//									    	//将此销售订单在价格政策里的折扣类型，如hdzk和cpzk
//									    	//先查客户有哪些价格政策，再查明细里“产品折扣”，“活动折扣”行里，对应的小计数为0.
//									    	//tempstore.push(jsonstore[elementJ].DISCOUNT_STYLE2);
//									    	if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'CPZK'){
//									    		//循环销售订单里的两种折扣类型，进行总价判断
//									    		for(element in salePriceGridJson){
//													//console.log(salePriceGridJson[element].type);
//													if(salePriceGridJson[element].type=='PR03'){//PR03指产品折扣
//													   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//														   //Ext.MessageBox.alert("提示","请维护订单产品折扣！");
//														   tmepElement = 'PR03';
//													   }
//													}
//												}
//									    		
//									    	}else if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'HDZK'){
//									    		
//									    		//循环销售订单里的两种折扣类型，进行总价判断
//									    		for(element in salePriceGridJson){
//													//console.log(salePriceGridJson[element].type);
//													if(salePriceGridJson[element].type=='PR04'){//PR04指活动折扣
//													   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
//														   //Ext.MessageBox.alert("提示","请维护订单活动折扣！");
//														   tmepElement = 'PR04';
//													   }
//													}
//												}
//									    	}
//									    }
//									    
//									},
//									failure : function(response,opts){
//										
//									} 
//								});
//								
//								if(tmepElement == 'PR03'){
//									Ext.MessageBox.alert("提示","请维护订单产品折扣！");
//									return;
//								}else if(tmepElement == 'PR04'){
//									Ext.MessageBox.alert("提示","请维护订单活动折扣！");
//									return;
//								}else if(tmepElement == 'PR05'){
//									Ext.MessageBox.alert("提示","工厂内部自用单OR8,对应销售价格定价条件选错,请检查！");
//									return;
//								}
//					
								//add by hzm 20170214
					me.updateSaleItemPrice(_window);
				},
				saveFj:function(type){
					var me = this;
					var _BZWindow = me.getMaterialBZWindow();
					//验证
					var validate = beforeSaveBZValidate(_BZWindow);
					if(validate==false){
						return;
					}
					_BZWindow.queryById("saveFj").setDisabled(true);
					
					var form1 = _BZWindow.queryById("form1_ItemId");
			    	var form1Values = {};
			    	form1Values = form1.getForm().getValues();
			    	
			    	var _myGoodsId = form1.getForm().findField("myGoodsId").getValue();
					//组合json数据
					var gridData = Ext.encode({
						 saleItemFj:form1Values
					});
					
					var myMask = new Ext.LoadMask(_BZWindow,{msg:"请稍等..."}); 
					myMask.show();
					//保存到我的物品
					Ext.Ajax.request({
						url : 'main/myGoods/updateBZ',
						jsonData : gridData,
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							_BZWindow.queryById("saveFj").setDisabled(false);
							if(values.success){
								//加载附加信息
								var _myGoodsId = values.data.myGoodsId;
								loadfjForm(form1,_myGoodsId);
//							    _BZWindow.close();
								Ext.MessageBox.alert("提示","保存成功！");
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){ myMask.hide();}
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
							_BZWindow.queryById("saveFj").setDisabled(false);
						}
					});
			    	
				}
			},
			"MaterialBJWindow":{
				updateSaleItem:function(type){
					var me = this;
					var _window = me.getMaterialBJWindow();
					//add by hzm 20170214
								/*console.log("**********me.getMaterialBZWindow************");
								var tmepElement = "";
								var salePriceGridJson = [];
								var salePriceGrid;
								var salePriceGridStore;
								var newWindow = me.getMaterialBZWindow();
								var headForm = newWindow.queryById("headForm_ItemId");
								formValues = headForm.getForm().getValues();
								salePriceGrid = newWindow.queryById("salePriceGrid_ItemId");
						    	salePriceGridStore= salePriceGrid.getStore();
						    	var salePriceGridValues = salePriceGridStore.getRange(0,salePriceGridStore.getCount());返回指定索引范围内的记录.						    		
						    		for (var i = 0; i <salePriceGridStore.getCount(); i++) {
						    			var arrJson = salePriceGridValues[i].getData();
						    			salePriceGridJson.push(arrJson);
						    		}
								保存前，有价格政策的客户，判断在“销售价格信息”栏的“产品折扣”或“活动折扣”上是否有值  add by hzm 20170210			                    先查下价格政策表，显示要有哪些折扣类型，如：产品折扣、活动折扣。								Ext.Ajax.request({
									url:'main/mm/getDiscountKunnr',
									params:{
									   'id' : _window.saleHeadId
									},
									method:'GET',
						            async:false,			
									success : function(response,opts){
									    var json = Ext.decode(response.responseText);
									    console.log(json);
									    var jsonstore = json.data;
									    var tempstore = [];									    for(elementJ in jsonstore){
									    	console.log(jsonstore[elementJ].DISCOUNT_STYLE2);hdzk 对应  PR03,cpzk 对应  PR04									    	将此销售订单在价格政策里的折扣类型，如hdzk和cpzk									    	先查客户有哪些价格政策，再查明细里“产品折扣”，“活动折扣”行里，对应的小计数为0.									    	tempstore.push(jsonstore[elementJ].DISCOUNT_STYLE2);									    	if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'CPZK'){
									    		循环销售订单里的两种折扣类型，进行总价判断									    		for(element in salePriceGridJson){
													console.log(salePriceGridJson[element].type);													if(salePriceGridJson[element].type=='PR03'){PR03指产品折扣													   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
														   Ext.MessageBox.alert("提示","请维护订单产品折扣！");														   tmepElement = 'PR03';
													   }
													}
												}
									    		
									    	}else if(jsonstore[elementJ].DISCOUNT_STYLE2 == 'HDZK'){
									    		
									    		循环销售订单里的两种折扣类型，进行总价判断									    		for(element in salePriceGridJson){
													console.log(salePriceGridJson[element].type);													if(salePriceGridJson[element].type=='PR04'){PR04指活动折扣													   if(salePriceGridJson[element].subtotal == 0 || salePriceGridJson[element].subtotal == null || salePriceGridJson[element].subtotal == ""){
														   Ext.MessageBox.alert("提示","请维护订单活动折扣！");														   tmepElement = 'PR04';
													   }
													}
												}
									    	}
									    }
									    
									},
									failure : function(response,opts){
										
									} 
								});
								
								if(tmepElement == 'PR03'){
									Ext.MessageBox.alert("提示","请维护订单产品折扣！");
									return;
								}else if(tmepElement == 'PR04'){
									Ext.MessageBox.alert("提示","请维护订单活动折扣！");
									return;
								}*/
					
								//add by hzm 20170214
					
					me.updateSaleItemPrice(_window);
				},
				saveBJ:function(){
					var me = this;
					var window = me.getMaterialBJWindow();
					window.queryById("saveBJ").setDisabled(true);
					var _loadStatus = window.loadStatus;
					var _flowInfo = window.flowInfo;
					var _matnr = window.matnr;
					
					//验证
					var validate = beforeSaveBJValidate(window);
					if(validate==false){
						window.queryById("saveBJ").setDisabled(false);
						return;
					}
					
					var formValues = {};
					var headForm = window.queryById("headForm_ItemId");
					formValues = headForm.getForm().getValues();
					var _matnr = headForm.getForm().findField("matnr").getValue();
					var $bgdispo=formValues.bgdispo;
					var bgdispo="";
					console.log($bgdispo.length);
					if($bgdispo.length>1){
						$.each($bgdispo,function(key,val){
							bgdispo+=val+"/";
						});
						bgdispo=bgdispo.substring(0,bgdispo.length-1);
					}else{
						bgdispo = $bgdispo;
					}
					formValues['bgdispo'] = bgdispo;
					formValues['matnr'] = _matnr;
					formValues['type'] = window.type;
					//return;
					
		    		//组合json数据
					var gridData = Ext.encode({
						materialBujian:formValues
					});
					if("newSaleContentWindow"==window.sourceShow){
						//添加到订单类型   处理代码块
						alert("下单新增界面");
					}else{
						var myMask = new Ext.LoadMask(window,{msg:"请稍等..."}); 
						myMask.show();
						
						Ext.Ajax.request({
							url : 'main/myGoods/saveBJ',
							jsonData : gridData,
							method : 'POST',
							frame:true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.success){
									headForm.getForm().setValues(values.data);
									headForm.getForm().findField("bgdispo").setValue(values.data.bgdispo.split("/"));
									if('2'==_loadStatus){
										var grid = me.getMyGoodsBujianMainGridView();
										grid.getStore().load();
										window.queryById("filesTabpanel_ItemId").setDisabled(false);
									}else if('3'==_loadStatus){
									
									}
								    
									Ext.MessageBox.alert("提示","保存成功！"+"当前的产线为"+$bgdispo);
									//Ext.MessageBox.alert("提示信息","当前的产线为"+$bgdispo);
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								if (myMask != undefined){ myMask.hide();}
								window.queryById("saveBJ").setDisabled(false);
							},
							failure : function(response, opts) {
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								Ext.MessageBox.alert("提示","保存失败！");
								if (myMask != undefined){ myMask.hide();}
								window.queryById("saveBJ").setDisabled(false);
							}
						});
					}
				
				},
				//打开文件上传窗口
				fileUploadButtonClick:function(info){
					var me = this;
					var window = me.getMaterialBJWindow();
					var headForm = window.queryById("headForm_ItemId");
					var headId = headForm.getForm().findField("id").getValue();
					
					Ext.create('SMSWeb.view.mm.sale.MaterialFileUploadWindow',{fileType:info,formId:headId,title:"附件上传"}).show();
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
												var newWindow = me.getMaterialBJWindow();
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
				}
			},
			//文件上传窗口
			"MaterialFileUploadWindow":{
				fileUploadButtonClick:function(){
				    var me = this;
				    var fileUploadWindow = me.getMaterialFileUploadWindow();
				    var fileUploadForm = fileUploadWindow.queryById("fileUploadForm");
				    var newWindow = me.getMaterialBJWindow();
				    var file = fileUploadForm.getForm().findField("file").getValue();
				    var fileType = fileUploadForm.getForm().findField("fileType").getValue();
				    
			    	if(fileUploadForm.getForm().isValid()){  
	                	fileUploadForm.submit({  
	                        url: 'main/mm/fileupload2',  
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
			/*,
			"MaterialBZWindow form[itemId=topForm_ItemId] combobox":{
				change:function( field, newValue, oldValue, eOpts ){
					var me = this;
					var _BZWindow = me.getMaterialBZWindow();
					
					if(_BZWindow.selStatus){

						var headForm = _BZWindow.queryById("headForm_ItemId");
						var _id = headForm.getForm().findField("id").getValue();
						
						var _materialPropertyItemInfo = "";
						var topForm =  _BZWindow.queryById("topForm_ItemId");
						//Ext.util.MixedCollection
						var coll = topForm.getForm().getFields();
						var _flag = true;
						var _priceArray = [];
						
						for(var i=0;i<coll.getCount();i++){
							var _field = coll.getAt(i);
							var _fvalue = _field.getValue();
							
							if(_fvalue){
								var _fname = _field.getName();
								//info1 + _ + id
								_priceArray.push(_fname+"_"+_fvalue);
							}else{
								_flag = false;
							}
						}
						
						if(_flag){
							//info0_id1,info1_id2
							_materialPropertyItemInfo = _priceArray.join(",");
							//更改原价格数据
//							var priceGrid = _BZWindow.queryById("priceGrid_ItemId");
//							var mainTabpanel = _BZWindow.queryById("tabpanel_ItemId");
//							var pricePanel = _BZWindow.queryById("pricePanel_ItemId");
							
							Ext.Ajax.request({
								url : 'main/mm/getMaterialPropertyItem',
								method : 'GET',
								params : {
									'priceInfo' : _materialPropertyItemInfo,
									'pid':_id
								},
								async:false,
								dataType : "json",
								contentType : 'application/json',
								success : function(response, opts) {
									var values = Ext.decode(response.responseText);  
									
									if(values.success){
										var _materialPropertyItemId = values.data.id;
										var _materialPrice = values.data.price;
//										var _model = priceGrid.getStore().findRecord("type", "PR01");
										
//										_model.set("conditionValue", _materialPrice);
										
//										mainTabpanel.setActiveTab(pricePanel); 
//										Ext.MessageBox.alert("tips","该产品原价格是："+_materialPrice);
										_BZWindow.materialPrice= _materialPrice;
										_BZWindow.materialPropertyItemId = _materialPropertyItemId;
										
									}else{
										Ext.MessageBox.alert("提示",values.errorMsg);
									}
								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("提示","加载数据失败！");
								}
							});
						}
					}
					
				}
			}*/
		});
	},
	//标准产品&补件  --定价过程保存
	updateSaleItemPrice:function(_window){
		var me = this;
		_window.queryById("updateSaleItem").setDisabled(true);
		//验证
//		var validate = beforeSaveMaterialBZValidate(_BZWindow);
//		if(validate==false){
//			return;
//		}
	    //销售价格
	    var saleItemFormValue={};
	    var salePriceGridJson = [];
	    var salePriceForm;
	    var salePriceGrid;
    	var salePriceGridStore;
		
		salePriceForm = _window.queryById("salePriceForm_ItemId");
    	
    	var _amount = salePriceForm.getForm().findField("amount").getValue();
    	var _totalPrice = salePriceForm.getForm().findField("totalPrice").getValue();
    	//组合saliItem
    	saleItemFormValue['id'] = _window.saleItemId;
    	saleItemFormValue['amount'] = _amount;
    	saleItemFormValue['totalPrice'] = _totalPrice;
    	
    	//组合saliItemPrice
    	salePriceGrid = _window.queryById("salePriceGrid_ItemId");
    	salePriceGridStore= salePriceGrid.getStore();
    	var salePriceGridValues = salePriceGridStore.getRange(0,salePriceGridStore.getCount());
    	
    	for (var i = 0; i <salePriceGridStore.getCount(); i++) {
    		var arrJson = salePriceGridValues[i].getData();
    		salePriceGridJson.push(arrJson);
    	}
    	var saleView = me.getNewSaleWindowInnerContent();
		var saleItemGrid = saleView.queryById('saleGrid');
		var saleForm = saleView.queryById("saleForm");
		var _shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
		
    	//组合json数据
		var gridData = Ext.encode({
			 shouDaFang:_shouDaFang,
        	 saleItem:saleItemFormValue,
        	 saleItemPrices:salePriceGridJson
		});
    	//修改
		var myMask = new Ext.LoadMask(_window,{msg:"请稍等..."}); 
		myMask.show();
    	Ext.Ajax.request({
    		url : 'main/mm/updateSaleItem',
			jsonData : gridData,
			method : 'POST',
			frame:true,
			dataType : "json",
			contentType : 'application/json',
			success : function(response, opts) {
				var values = Ext.decode(response.responseText);	
				
				if(values.success){
					salePriceGridStore.load({params:{'pid':_window.saleItemId}});
					//刷新saleItemGird
					saleItemGrid.getStore().load({
								params:{'pid':_window.saleHeadId},
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
					
					Ext.MessageBox.alert("提示","保存成功！");
				}else{
					Ext.MessageBox.alert("提示",values.errorMsg);
				}
				if (myMask != undefined){ myMask.hide();}
				_window.queryById("updateSaleItem").setDisabled(false);
			},
			failure : function(response, opts) {
				Ext.MessageBox.alert("提示","保存失败！");
				if (myMask != undefined){ myMask.hide();}
				_window.queryById("updateSaleItem").setDisabled(false);
			}
		});
	},
	views : ['mm.myGoods.MyGoodsMainView','mm.myGoods.MyGoodsMainFormView','mm.myGoods.MyGoodsMainGridView'
	         ,'mm.sale.MaterialMainGridBase2SaleView','mm.sale.MaterialMainFormBase2SaleView'
	         ,'mm.myGoods.MyGoodsBujianMainView','mm.myGoods.MyGoodsBujianMainFormView','mm.myGoods.MyGoodsBujianMainGridView'],
	stores : ['mm.myGoods.Store4MyGoods','mm.sale.Store4MaterialBase2Sale','mm.myGoods.Store4MyGoodsBujian'],
	models : []
});

/**
 * 保存之前校验
 */
function beforeSaveMaterialBZValidate(newWindow){
	var errMsg = "";
	var mainTabpanel = newWindow.queryById("tabpanel_ItemId");
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
	
	return true;
}
/**
 * 补件保存前验证
 */
function beforeSaveBJValidate(newWindow){
	var errMsg = "";
	var headForm = newWindow.queryById("headForm_ItemId");
	headForm.isValid();
	
	var headFormFields = headForm.getForm().getFields();
	headFormFields.each(function(field) {
         Ext.Array.forEach(field.getErrors(), function(error) {
             errMsg += field.getFieldLabel()+":"+error+"<br/>"
         });
    });
	
	if(errMsg!=""){
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	
	return true;
}
/**
 * 散件保存前验证(价格审核折扣)
 */
function beforeSaveSJValidate(newWindow){
	  var errMsg="";
	  var itemGrid = newWindow.queryById("itemGrid_ItemId");
	  var itemStore = itemGrid.getStore();
	  
	  for (var i = 0; i <itemStore.getCount(); i++) {
		    var record = itemStore.getAt(i);
		    var _matnr = record.data.matnr;
		    var _zhekou = record.data.zhekou;
		    
		    if(_zhekou!=null || _zhekou!=""){
				  if(_zhekou>=0 && _zhekou <=1){
					  
				  }else{
					  errMsg +="产品编号:"+_matnr+ " 折扣填写错误!<br/>";
				  }
			}else{
				  errMsg +="产品编号:"+_matnr+" 请填写折扣!<br/>";
			}
	  }
	  
	  if(errMsg!=""){
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	  }
	
	  return true;
}
//附加信息加载
function loadfjForm(form1,myGoodsId){
	form1.load({
		url:'main/myGoods/getSaleItemFj',
		params : {
			myGoodsId : myGoodsId
		},
		method : 'GET',
		success:function(f,action){
			
		},
        failure:function(form,action){
        	
        }
	});
}
/**
 * 保存之前校验--标准产品
 */
function beforeSaveBZValidate(newWindow){
	var errMsg = "";
	var mainTabpanel = newWindow.queryById("tabpanel_ItemId");
	
	var form1 = newWindow.queryById("form1_ItemId");
	form1.isValid();
	var form1Fields = form1.getForm().getFields();
	form1Fields.each(function(field) {
         Ext.Array.forEach(field.getErrors(), function(error) {
             errMsg += field.getFieldLabel()+":"+error+"<br/>"
         });
    });
	
	if(errMsg!=""){
		mainTabpanel.setActiveTab(form1); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	return true;
}