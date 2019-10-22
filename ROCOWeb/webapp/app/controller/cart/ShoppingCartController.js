Ext.define("SMSWeb.controller.cart.ShoppingCartController", {
	extend : 'Ext.app.Controller',
	refs : [
	        {
	        	ref : 'ShoppingCart2SaleView',
	        	selector : 'ShoppingCart2SaleView' 
	        },{
	        	ref : 'ShoppingCartWindow',
	        	selector : 'ShoppingCartWindow' 
	        },{
	        	ref : 'ShoppingCart2AddSaleView',
	        	selector : 'ShoppingCart2AddSaleView' 
	        },{
	        	ref : 'MaterialMainGridBase2SaleView',
	        	selector : 'MaterialMainGridBase2SaleView' 
	        }, {
	        	ref : 'MaterialBase2SaleWindow',
	        	selector : 'MaterialBase2SaleWindow' 
	        }
	        ],
	        init : function(){
	        	this.control({
	        		//查询
	        		"ShoppingCartWindow button[itemId=querySale]" : {
	        			click : function() {
	        				var me = this;
	        				var mainView = me.getShoppingCartWindow();
	        				var maingrid = mainView.queryById('ShoppingCart2AddSaleView_itemId');
	        				var store = maingrid.getStore();
	        				store.loadPage(1);
//	        				formValues['type']="FB";
//	        				formValues['matnr']=mainView.matnr;
//	        				store.loadPage(1,{params:formValues});
	        			}
	        		},
	        		//确认
//	        		"ShoppingCartWindow button[itemId=comfirm]" : {
//	        			click : function() {
//	        				debugger;
//	        				var me = this;
//	        				var mainView = me.getShoppingCartWindow();
//	        				var grid = mainView.queryById('ShoppingCart2AddSaleView_itemId');
//	        				var records = grid.getSelectionModel().getSelection();
//	        				var flg=false;
//	        				var ids = [];
//	        				Ext.Array.each(records, function(r) {
//	        					ids.push(r.get('serialNumber'));
//	        				});
//	        				mainView.type="FB";
//	        				if(mainView.type=="FB"){
//	        					if(ids.length>0){
//	        						Ext.Ajax.request({
//	        							url : 'main/mm/checkFileStauts',
//	        							params : {
//	        								ids : ids
//	        							},
//	        							async:false,
//	        							method : 'POST',
//	        							success : function(response, opts) {
//	        								var values = Ext.decode(response.responseText);
//	        								if(values.success){
//	        									flg=true;
//	        									//Ext.MessageBox.alert("提示信息",values.msg);
//	        									//grid.getStore().reload();
//	        								}else{
//	        									Ext.MessageBox.alert(values.errorCode,values.errorMsg);
//	        								}
//	        							},
//	        							failure : function(response, opts) {
//	        								Ext.MessageBox.alert("提示信息","网络异常！");
//	        							}
//	        						});	
//	        					}
//	        				}else{
//	        					flg=true;
//	        				}
//	        				if(flg){
//	        					if("newSaleContentWindow"==mainView.sourceShow){
//	        						//						添加到订单行项目   处理代码块
//	        						var itemsGrid =Ext.getCmp("addSaleItemsGrid");
//	        						Ext.Array.each(records, function(r) {
//	        							var saleStore =itemsGrid.getStore();
//	        							var storeCount = saleStore.getCount();
//	        							var existFlag = false;
//	        							if("BZ"!=mainView.type){//工程单，标准产品可以重复下
//	        								for (var i = 0; i <storeCount; i++) {
//	        									var record = saleStore.getAt(i);
//	        									if(record.data.materialHeadId==r.get('id')){
//	        										existFlag = true;
//	        									}
//	        								}
//	        							}
//	        							if(!existFlag){
//	        								var model = Ext.create("SMSWeb.model.sale.SaleModel");
//	        								//								debugger;
//	        								var matnr=mainView.matnr;
//	        								var unit='EA';//默认单位 传到SAP
//	        								if(!matnr){
//	        									//非散件
//	        									matnr=r.get('matnr');
//	        									unit=r.get("meins"); 
//	        								}
//	        								//获取最大的行号
//	        								var _maxPosex=0;
//	        								saleStore.each(function(){
//	        									var _thisPosex=Number(this.get("posex"));
//	        									_maxPosex=_maxPosex>_thisPosex?_maxPosex:_thisPosex;
//	        								});
//	        								//最大行号+10 为新行的行号
//	        								model.set('posex', Number(_maxPosex)+Number(10));//(storeCount+1)*10);
//	        								model.set('matnr', r.get('matnr'));
//	        								model.set('unit', unit);//设置单位
//	        								model.set('mtart', r.get('mtart'));
//	        								//								model.set('touYingArea', r.get('touYingArea'));
//	        								model.set('maktx', r.get('maktx'));//产品描述
//	        								model.set('materialHeadId', r.get('id'));//物料id
//	        								var isStandard = r.get('isStandard');
//	        								if("1"==isStandard){
//	        									model.set('materialPropertyItemInfo', r.get('materialPropertyItemInfo'));
//	        									model.set('materialPrice', r.get('materialPrice'));
//	        								}
//	        								model.set('isStandard', isStandard);//是否标准产品
//	        								model.set('myGoodsId', r.get('id'));//我的商品id --流程激活后删除该表
//	        								//								model.set('sanjianHeadId', r.get('sanjianHeadId'));//散件headID
//	        								model.set('remark', r.get('zzazdr'));//加入订单行项目时的订单类型
//	        								model.set('ortype', r.get('ortype'));//加入订单行项目时的订单类型
//	        								model.set('amount', 1);
//	        								saleStore.add(model);
//	        							}
//	        						});
//	        						mainView.close();
//	        					}else{
//
//	        						var ids = [];
//	        						Ext.Array.each(records, function(r) {
//	        							ids.push(r.get('id'))
//	        						});
//	        						if("BZ"==mainView.type){
//	        							if(ids.length==1){
//	        								var record = records[0];
//	        								var headId = record.get("id");
//
//	        								Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
//	        										{loadStatus:'2',formId : headId,title:'标准商品'}).show();
//	        							}
//	        						}else if("SJ"==mainView.type){
//	        							if(ids.length>0){
//	        								var sjwindow = me.getMaterialSJWindow();
//	        								var itemGrid = sjwindow.queryById("itemGrid_ItemId");
//	        								Ext.Array.each(records, function(r) {
//	        									var itemStore = itemGrid.getStore();
//	        									var storeCount = itemStore.getCount();
//	        									var existFlag = false;
//	        									for (var i = 0; i <storeCount; i++) {
//	        										var record = itemStore.getAt(i);
//	        										if(record.data.matnr==r.get('matnr')){
//	        											existFlag = true;
//	        										}
//	        									}
//	        									if(!existFlag){
//	        										var count = itemStore.getCount();
//	        										var model = Ext.create("SMSWeb.model.mm.base.MaterialSJModel");
//	        										var _price =r.get('kbetr');
//	        										var _amount = 1;
//	        										var _zhekou = 1;
//	        										model.set('matnr',r.get('matnr'));
//	        										model.set('miaoshu',r.get('maktx'));
//	        										model.set('price',_price);
//	        										model.set('amount',_amount);
//	        										model.set('zhekou',_zhekou);
//	        										model.set('materialHeadId',r.get('id'));
//	        										model.set('unit',r.get('meins'));
//	        										var _totalPrice = _amount * _price * _zhekou;
//	        										_totalPrice = _totalPrice.toFixed(2);
//	        										_totalPrice = parseFloat(_totalPrice);
//	        										model.set('totalPrice',_totalPrice);
//	        										itemStore.insert(count, model);
//	        									}
//	        								});
//	        								mainView.close();
//	        							}
//	        						}
//	        					}
//	        				}
//	        			}
//	        		},
	        		//新增非标产品
	        		"ShoppingCartWindow button[itemId=addSale]" : {
	        			click : function() {
//	        				var me = this;
//	        				var mainView = me.getMaterialBase2SaleWindow();
//	        				var _form=mainView.queryById("MaterialMainFormBase2SaleView_itemId").getForm();
//	        				var _saleFor=_form.findField("saleFor").getValue();
	        				var _saleFor='0';
	        				Ext.create('SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
	        						{loadStatus:"2",title:'新增非标产品',sourceShow:"newSaleContentWindow",saleFor:_saleFor}).show();
	        			}
	        		},
	        		//删除非标产品
	        		"ShoppingCartWindow button[itemId=deleteSale]" : {
	        			click : function() {
	        				var me = this;
	        				debugger;
	        				var mainView = me.getShoppingCartWindow();
	        				var grid = mainView.queryById('ShoppingCart2AddSaleView_itemId');
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
	        		//选择一个标准产品
	        		'ShoppingCart2AddSaleView':{
	        			editButtonClick:function(grid,rowIndex,colIndex){
	        				var me = this;
	        				var mainView = me.getMaterialBase2SaleWindow();
	        				var record = grid.getStore().getAt(rowIndex);
	        				var _saleFor=record.data.saleFor;
	        				var headId = record.data.id;//主表ID
	        				Ext.create('SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
	        						{loadStatus:"2",title:'编辑个人非标产品',saleFor:_saleFor,sourceShow:"newSaleContentWindow",uuId:headId}).show();
	        			}
	        		}
	        	})
	        },
	        views : ['cart.ShoppingCart2SaleView','cart.ShoppingCartWindow','cart.ShoppingCart2AddSaleView','mm.sale.MaterialBase2SaleWindow'],
	        stores : ['cart.ShoppingCartStore','mm.sale.Store4MaterialBase2Sale'],
	        models : ['cart.ShoppingCartModel','mm.base.MaterialBaseModel']
});
