Ext.onReady(function() {
//	orderTypeCombobox.getStore().load(function(records, operation, success) {
//	});
//	jiaoQiTianShuStore.load(function(records, operation, success) {
//	});
//	handleTimeStore.load(function(records, operation, success) {
//	});
});

Ext.define("SMSWeb.controller.sale.SaleController", {
	extend : 'Ext.app.Controller',
	id : 'saleController',
	refs : [{
				ref : 'NewSaleWindowInnerContent',
				selector : 'NewSaleWindowInnerContent' 
			},{
				ref : 'NewMaterialBaseFBWindow',
				selector : 'NewMaterialBaseFBWindow' 
			},
			{
				ref : 'NewCustWindowForSale',
				selector : 'NewCustWindowForSale' 
			},
			{
				ref : 'Sale2GridView',
				selector : 'Sale2GridView' 
			},
			{
				ref : 'SysFileUploadWindow',
				selector : 'SysFileUploadWindow' 
			},
			{
				ref : 'MyGoodsWindow',
				selector : 'MyGoodsWindow' 
			},
			{
				ref : 'NewTcPanel',
				selector : 'NewTcPanel' 
			},
			{
				ref : 'NewSaleWindowForBg',
				selector : 'NewSaleWindowForBg' 
			},
			{
				ref : 'NewSaleWindowForHoleInfo',
				selector : 'NewSaleWindowForHoleInfo' 
			},
			{
				ref : 'NewSaleReportPanel',
				selector : 'NewSaleReportPanel' 
			},{
				ref : 'MyGoodsBujianWindow',
				selector : 'MyGoodsBujianWindow' 
			},
			{
				ref : 'NewSaleItemReportPanel',
				selector : 'NewSaleItemReportPanel' 
			},
			{
				ref : 'NewOrderCodeWindow',
				selector : 'NewOrderCodeWindow' 
			},{
				ref : 'SaleOutputDataPanel',
				selector : 'SaleOutputDataPanel'
			},{
				ref:'SaleCheckSheetMainView',
				selector:'SaleCheckSheetMainView'
			},{
				ref:'NewCustWindow',
				selector:'NewCustWindow'
			},{
				ref:'newSaleContentWindow',
				selector:'newSaleContentWindow'
			},{
				ref:'NewBGFileUploadBaseWindow',
				selector:'NewBGFileUploadBaseWindow'
			},{
				ref:'MaterialBGWindow',
				selector:'MaterialBGWindow'
			},{
				ref : 'MaterialBase2SaleWindow',
				selector : 'MaterialBase2SaleWindow' 
			},{
				ref : 'ShoppingCartWindow',
				selector : 'ShoppingCartWindow' 
			},{
				ref : 'NewSaleContentComplainidWindow',
				selector : 'NewSaleContentComplainidWindow' 
			}
			
	],
	init : function() {
//		var win=Ext.create("SMSWeb.view.sale.NewSaleContentWindow", {title:'新增订单'});
//		win.show();
		var meC=this;
		this.control({//保存主表
			
			//查询客户对帐信息，从SAP读取
			"SaleCheckSheetMainView button[id=csquerySale]":{
				click : function( bt, e, eOpts ) {
//					var sale2Form = Ext.getCmp("sale2Form");
//					var formValues = sale2Form.getValues();
			        //console.log("test666");
			        var viewForm = Ext.getCmp("SaleCheckSh22FormView")
			        //var shouDaFang = viewForm.getForm().findField("shouDaFang").getValue();
			        var startDate = viewForm.getForm().findField("startDate").rawValue;
			        if(startDate=="undefined" || startDate==''){
			        	Ext.MessageBox.alert("提示信息","开始日期为必填项！");
			        	return;
			        }
			        var endDate = viewForm.getForm().findField("endDate").rawValue;
			         if(endDate=="undefined" || endDate==''){
			        	Ext.MessageBox.alert("提示信息","结束日期为必填项！");
			        	return;
			        }
					var grid = Ext.getCmp("SaleCheckSheetGrid");
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
			
			//查询订单信息
			"Sale2MainView button[id=querySale]":{
				click : function( bt, e, eOpts ) {
//					var sale2Form = Ext.getCmp("sale2Form");
//					var formValues = sale2Form.getValues();
					var grid = Ext.getCmp("sale2Grid");
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
			
			"Sale2MainView button[id=newSale]" : {
				click : function() {
					Ext.create('SMSWeb.view.sale.NewSaleWindow',
							     {title:'新增订单'}).show(/*this,function(){}*/);
					}
			},
			"Sale2MainView button[id=addSale]" : {
				click : function() {
					var win=Ext.create("SMSWeb.view.sale.NewSaleContentWindow", {title:'新增订单'});
					win.show();
				}
 
			},
			"Sale2MainView button[id=changeData]" : {
				click : function() {
					Ext.Ajax.request({
						url:'main/sale/changeData',
						dataType : "json",
						contentType : 'application/json',
						success:function(response,opts){
							var value = Ext.decode(response.responseText);
							Ext.MessageBox.alert(value.errorCode,value.errorMsg);
						}
					});
				}
 
			},
			"Sale2MainView button[id=export]":{
				click:function(){
					var tmpgrid = Ext.getCmp('sale2Grid');
					Ext.MessageBox.confirm("温馨提示", "导出到Excel", function (btn) {
						if(btn=="yes"){
							//用grid导出excel
							ExportExcelByGrid(tmpgrid);
						}
					});
				}
			},
			"newSaleContentWindow button[action=saveAddSaleForm]" : {
				click : function(btn) {//新增订单界面保存
					var form=Ext.getCmp("addSaleForm").getForm();
					if(!form.isValid()){
						Ext.MessageBox.alert('提示信息','填写信息有误！');
						return;
					}
					var _checkFlag=Ext.getCmp('checkFlag');
					if("false"==_checkFlag.getValue()){
						Ext.MessageBox.alert('提示信息','研发单需要填写正确的内部订单号');
						return;
					}
					var saleItmes=Ext.getCmp("addSaleItemsGrid");
					var gridDatas=saleItmes.getStore().getRange();
					var formVals=form.getValues();
					var nval=formVals.orderType;
					if(formVals.isYp == undefined){
						formVals.isYp = '0';
					}
					if(formVals.urgentType=='UR2'){
						formVals.handleTime = '2'
					}else{
						formVals.handleTime = '1'
					}
					var azdz=":";
					var datas=new Array();
					for(var i=0;i<gridDatas.length;i++){
						var data=gridDatas[i].data;
						//判断订单行项的每一行是否含有空 的安装位置 只针对 OR1 OR7 OR8 OR9
						if("OR1"==nval||"OR7"==nval||"OR8"==nval||"OR9"==nval){
							if(data.remark==null||Ext.String.trim(data.remark)==""){
								if(azdz==":"){
									azdz=azdz+data.posex;
								}else{
									azdz=azdz+","+data.posex;
								}
							}
						}
					
						var item={
						    id:data.id,
							amount: data.amount,
							bujianId: data.bujianId,
							createTime:  data.createTime,
							createUser:  data.createUser,
							drawType:  data.drawType,
							isStandard:  data.isStandard,
							jdName:  data.jdName,
//							kitId:  data.,
							maktx:  data.maktx,
							materialHeadId:  data.materialHeadId,
							materialPrice:  data.materialPrice,
							materialPropertyItemInfo:  data.materialPropertyItemInfo,
							matnr:  data.matnr,
							mtart:  data.mtart,
							myGoodsId:  data.myGoodsId,
							orderCodePosex:  data.orderCodePosex,
							ortype:  data.ortype,
//							pdfId:  data.,
							posex:  data.posex,
//							priceId:  data.,
							sanjianHeadId:  data.sanjianHeadId,
							stateAudit:  data.stateAudit,
							status:  data.status,
							totalPrice:  data.totalPrice,
							touYingArea:  data.touYingArea,
							updateTime:  data.updateTime,
							updateUser:  data.updateUser,
							remark:data.remark,
							unit:data.unit
							//orderEvent:data.orderEvent
//							zzwgfg:  data.zzwgfg
						}
							
						datas.push(item);
					}
					if(azdz.length>1){
						Ext.MessageBox.alert('提示信息',"请填写安装位置，行号 "+azdz);
						return;
					}
						 
//							return;
					var saleHeader={
							'orderType':formVals.orderType,
							'designerTel':formVals.designerTel,
							'isYp':formVals.isYp,
							'urgentType':formVals.urgentType,
							'isMj':formVals.isMj,
							'isQc':formVals.isQc,
							'isKf':formVals.isKf,
							'remarks':formVals.remarks,
							'id':formVals.shId,
							'checkDrawUser':formVals.checkDrawUser,
							'checkPriceUser':formVals.checkPriceUser,
							'confirmFinanceUser':formVals.confirmFinanceUser,
							'createTime':formVals.createTime,
							'createUser':formVals.createUser,
							'dianMianTel':formVals.dianMianTel,
							'fuFuanCond':formVals.fuFuanCond,
							'fuFuanMoney':formVals.fuFuanMoney,
							'handleTime':formVals.handleTime,
							'huoDongType':formVals.huoDongType,
							'jiaoQiTianShu':formVals.jiaoQiTianShu,
							'kunnrName1':formVals.kunnrName1,
							'orderCode':formVals.orderCode,
							'serialNumber':formVals.serialNumber,
							'orderDate':formVals.orderDate,
//							'orderPayFw':formVals.orderPayFw,
							'orderStatus':formVals.orderStatus,
							'orderTotal':formVals.orderTotal,
							'pOrderCode':formVals.pOrderCode,
							'payType':formVals.payType,
							'sapOrderCode':formVals.sapOrderCode,
							'sapCreateDate':formVals.sapCreateDate,
							'vgbel':formVals.vgbel,
							'loanAmount':formVals.loanAmount,
							'shiJiDate':formVals.shiJiDate,
							'shiJiDate2':formVals.shiJiDate2,
							'shouDaFang':formVals.shouDaFang,
							'songDaFang':formVals.songDaFang,
							'zzaufnr':formVals.zzaufnr,
							//销售方式
							'saleFor':formVals.saleFor,
							'shop':formVals.shop,
							'shopCls':formVals.shopCls
							
					};
					var tc={
						'name1':formVals.name1,
						'tel':formVals.tel,
						'sex':formVals.sex,
						'custId':formVals.custId,
						'birthday':formVals.birthday,
						'huXing':formVals.huXing,
						'isYangBan':formVals.isYp,
						'orderPayFw':formVals.orderPayFw,
						'id':formVals.tcId,
						'age':formVals.age,
						'jingShouRen':formVals.jingShouRen,
						'address':formVals.azAddress,
						'anzhuanDay':formVals.anzhuanDay,
						'pOrderCode':formVals.pOrderCode,
						'problem':formVals.problem,
						'tousucishu':formVals.tousucishu//,
						//'orderEvent':formVals.orderEvent
					};
					var socl=null;
					if(KUNNRS.indexOf("ZB")!=-1){
					    socl=[{
							'pstlz':formVals.pstlz,
							'regio':formVals.regio,
							'mcod3':formVals.mcod3,
							'socAddress':formVals.azAddress,
							'id':formVals.socId
					}];
					}
					
					btn.setDisabled(true);
					
			        var gridData = Ext.encode({
						terminalClient : tc,//客户信息
						saleItemList : datas, //订单行项目
						saleOneCustList:socl//客户一次送达方
					});
						
					Ext.Ajax.request({
						url : 'main/sale/addSave',
						params : saleHeader,
						method : 'POST',
						frame : true,
						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){ 
//								console.info(values);
								Ext.Ajax.request({
									url : 'main/sale/querySaleById?id='+values.data.id,
									async:false,
									dataType: "json",
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);
										if(values.success){ 
										//console.info(values.data);
										var formData=values.data;
											form.setValues(formData);
											
										saleItmes.getStore().load({params:{'pid':formData.shId}
										
										/*,callback: function(records, operation, success) {
											alert();
										        calculationTotalPrice(saleForm,itemGrid,'delete');
										    }*/
										});
										var fd=Ext.getCmp("mr_fielDset");
										var uf=Ext.getCmp("uploadFile");
										var df=Ext.getCmp("deleteFile");
										if(uf&&df){
											uf.show();
											df.show();
										}
										if(fd){
											fd.show();
										}
										Ext.MessageBox.alert("温馨提示","保存成功");
										} else {
											Ext.MessageBox.alert("提示信息",values.errorMsg);
										}
										 
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
										 
									}
								});
								
							
							} else {
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
							btn.setDisabled(false);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","保存失败");
							btn.setDisabled(false);
						}
					});
				
					
				}
 
			},
			//补购保存按钮
			"newSaleContentWindow button[action=bgsaveAddSaleForm]" : {
				click : function(btn) {//新增订单界面保存
					var form=Ext.getCmp("addSaleForm").getForm();
					console.log(form.getValues)
					if(!form.isValid()){
						Ext.MessageBox.alert('提示信息','填写信息有误！');
						return;
					}
					var _checkFlag=Ext.getCmp('checkFlag');
					if("false"==_checkFlag.getValue()){
						Ext.MessageBox.alert('提示信息','研发单需要填写正确的内部订单号');
						return;
					}
					var saleItmes=Ext.getCmp("addSaleItemsGrid");
					var gridDatas=saleItmes.getStore().getRange();
					var formVals=form.getValues();
					var nval=formVals.orderType;
					if(formVals.isYp == undefined){
						formVals.isYp = '0';
					}
					var azdz=":";
					var datas=new Array();
					for(var i=0;i<gridDatas.length;i++){
						var data=gridDatas[i].data;
						if("OR1"==nval||"OR7"==nval||"OR8"==nval||"OR9"==nval){
							if(data.remark==null||Ext.String.trim(data.remark)==""){
								if(azdz==":"){
									azdz=azdz+data.posex;
								}else{
									azdz=azdz+","+data.posex;
								}
							}
						}
					
						var item={
						    id:data.id,
							amount: data.amount,
							bujianId: data.bujianId,
							createTime:  data.createTime,
							createUser:  data.createUser,
							drawType:  data.drawType,
							isStandard:  data.isStandard,
							jdName:  data.jdName,
//							kitId:  data.,
							maktx:  data.maktx,
							materialHeadId:  data.materialHeadId,
							materialPrice:  data.materialPrice,
							materialPropertyItemInfo:  data.materialPropertyItemInfo,
							matnr:  data.matnr,
							mtart:  data.mtart,
							myGoodsId:  data.myGoodsId,
							orderCodePosex:  data.orderCodePosex,
							ortype:  data.ortype,
//							pdfId:  data.,
							posex:  data.posex,
//							priceId:  data.,
							sanjianHeadId:  data.sanjianHeadId,
							stateAudit:  data.stateAudit,
							status:  data.status,
							totalPrice:  data.totalPrice,
							touYingArea:  data.touYingArea,
							updateTime:  data.updateTime,
							updateUser:  data.updateUser,
							remark:data.remark,
							unit:data.unit 
//							zzwgfg:  data.zzwgfg
						}
							
						datas.push(item);
					}
					if(azdz.length>1){
						Ext.MessageBox.alert('提示信息',"请填写安装位置，行号 "+azdz);
						return;
					}
						 
//							return;
					var saleHeader={
							'orderType':formVals.orderType,
							'designerTel':formVals.designerTel,
							'isYp':formVals.isYp,
							'remarks':formVals.remarks,
							'id':formVals.shId,
							'checkDrawUser':formVals.checkDrawUser,
							'checkPriceUser':formVals.checkPriceUser,
							'confirmFinanceUser':formVals.confirmFinanceUser,
							'createTime':formVals.createTime,
							'createUser':formVals.createUser,
							'dianMianTel':formVals.dianMianTel,
							'fuFuanCond':formVals.fuFuanCond,
							'fuFuanMoney':formVals.fuFuanMoney,
							'handleTime':formVals.handleTime,
							'huoDongType':formVals.huoDongType,
							'jiaoQiTianShu':formVals.jiaoQiTianShu,
							'kunnrName1':formVals.kunnrName1,
							'orderCode':formVals.orderCode,
							'orderDate':formVals.orderDate,
//							'orderPayFw':formVals.orderPayFw,
							'orderStatus':formVals.orderStatus,
							'orderTotal':formVals.orderTotal,
							'pOrderCode':formVals.pOrderCode,
							'payType':formVals.payType,
							'sapOrderCode':formVals.sapOrderCode,
							'sapCreateDate':formVals.sapCreateDate,
							'vgbel':formVals.vgbel,
							'loanAmount':formVals.loanAmount,
							'shiJiDate':formVals.shiJiDate,
							'shiJiDate2':formVals.shiJiDate2,
							'shouDaFang':formVals.shouDaFang,
							'songDaFang':formVals.songDaFang,
							'zzaufnr':formVals.zzaufnr,
							//销售方式
							'saleFor':formVals.saleFor,
							'shop':formVals.shop,
							'shopCls':formVals.shopCls
							
					};
					var tc={
						'name1':formVals.name1,
						'tel':formVals.tel,
						'sex':formVals.sex,
						'custId':formVals.custId,
						'orderType':formVals.orderType,
						'pOrderCode':formVals.pOrderCode,
						'id':formVals.tcId,
						'address':formVals.address,
						'tousucishu':formVals.tousucishu,
						'anzhuanDay':formVals.anzhuanDay,
						'problem':formVals.problem,
						'pOrderCode':formVals.pOrderCode,
					};
					var socl=null;
					if(KUNNRS.indexOf("ZB")!=-1){
					    socl=[{
							'pstlz':formVals.pstlz,
							'regio':formVals.regio,
							'mcod3':formVals.mcod3,
							'socAddress':formVals.azAddress,
							'id':formVals.socId
					}];
					}
					
					btn.setDisabled(true);
					
			        var gridData = Ext.encode({
//			        	terminalClient : tc,//客户信息
			        	saleBgHeader:tc,
						saleItemList : datas, //订单行项目
						saleOneCustList:socl//客户一次送达方
					});
						
					Ext.Ajax.request({
						url : 'main/sale/addBgSave',
						params : saleHeader,
						method : 'POST',
						frame : true,
						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){ 
//								console.info(values);
								
								Ext.Ajax.request({
									url : 'main/sale/queryBgSaleById?id='+values.data.id,
									async:false,
									dataType: "json",
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);
										if(values.success){ 
										//console.info(values.data);
										var formData=values.data;
											form.setValues(formData);
											
										saleItmes.getStore().load({params:{'pid':formData.shId}
										
										/*,callback: function(records, operation, success) {
											alert();
										        calculationTotalPrice(saleForm,itemGrid,'delete');
										    }*/
										});
//										var fd=Ext.getCmp("mr_fielDset");
										var fj =Ext.getCmp("centerTabpanel");
										var uf =Ext.getCmp("uploadFile");
										var df =Ext.getCmp("deleteFile");
										uf.show();
										df.show();
										if(fj){
											fj.show();
										}
										Ext.MessageBox.alert("温馨提示","保存成功,请上传附件");
										} else {
											Ext.MessageBox.alert("提示信息",values.errorMsg);
										}
										 
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
										 
									}
								});
								
							
							} else {
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
							btn.setDisabled(false);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","保存失败");
							btn.setDisabled(false);
						}
					});
				
					
				}
 
			},
			//addComplainidClick
			"NewSaleContentComplainidWindow":{
				addComplainidClick:function(){//addComplainidClick
					var me = this;
					var comwindow = me.getNewSaleContentComplainidWindow();
					var itemGrid = comwindow.queryById("complainid_ItemId");
					var itemStore = itemGrid.getStore();
					var model = Ext.create("SMSWeb.model.sale.SaleComplaintModel");
					itemStore.insert(store.getCount(),model);
					return;
				},
				deleteComplainidClick:function(){
					var me = this;
					var comwindow = me.getNewSaleContentComplainidWindow();
					var newWindow = me.getNewSaleContentWindow();
					var _formId = newWindow.formId;
					var newWindowitemGrid = newWindow.queryById("complainid_ItemId");
					var shId = "";
				    var salePriceGrid;
					var salePriceForm;
					if(comwindow.shId){
						saleItemId = comwindow.shId;
					}
					
					var grid = comwindow.queryById("complainid_ItemId");
					
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
									url : 'main/myGoods/deleteComplainid',
									params : {
										'saleItemId':shId,
										ids : ids
									},
									method : 'POST',
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);	
										
										if(values.success){
//											var headForm = comwindow.queryById("headForm_ItemId");
											var _formId = comwindow.shId;
											
											grid.getStore().remove(records);
											grid.getStore().load({params:{'pid':_formId}});
											newWindowitemGrid.getStore().load({params:{'pid':me.getNewSaleContentWindow().formId}});
											Ext.MessageBox.alert("提示","删除成功！");
											
											/*if(sjwindow.saleItemId){
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
											}*/
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
				//保存客诉信息
				saveComplainid:function(){
					var me = this;
					var comwindow = me.getNewSaleContentComplainidWindow();
					var newWindow = me.getNewSaleContentWindow();
					var _loadStatus = comwindow.loadStatus;
					var _formId = newWindow.formId;
					var shId = comwindow.shId;
					var headForm = comwindow.queryById("headForm_ItemId");
					var formValues=[];
//					formValues = headForm.getForm().getValues();
					var itemGrid = comwindow.queryById("complainid_ItemId");
					var newWindowitemGrid = newWindow.queryById("complainid_ItemId");
					var itemStore = itemGrid.getStore();
					if(itemStore.getCount()==0){
						Ext.MessageBox.alert("提示信息","必须有一条明细信息");
						comwindow.queryById("saveCom").setDisabled(false);
						return;
					}else{
						var itemStoreJson = [];
			    		var itemStoreValues = itemStore.getRange(0,itemStore.getCount());
			    		for (var i = 0; i <itemStore.getCount(); i++) {
			    			var arrJson = itemStoreValues[i].getData();
			    			itemStoreJson.push(arrJson);
			    		}
			    		
//			    		if('3'==_loadStatus){}
			    		//组合json数据
						var gridData = Ext.encode({
							 bjTime:formValues.complainday,
							 materialComplainid:itemStoreJson
						});
						
						var myMask = new Ext.LoadMask(comwindow,{msg:"请稍等..."}); 
//						myMask.show();
						
						Ext.Ajax.request({
							url : 'main/myGoods/saveBJ?Pid='+shId,
							jsonData : gridData,
							method : 'POST',
							frame:true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.success){
									if("newSaleContentWindow"==comwindow.sourceShow){
										itemGrid.getStore().load({params:{'pid':me.getNewSaleContentWindow().formId}});
										newWindowitemGrid.getStore().load({params:{'pid':me.getNewSaleContentWindow().formId}});
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
														salePriceGridStore.load({params:{'pid':bjwindow.saleItemId}});
														//刷新saleItemGird
														saleItemGrid.getStore().load({
															params:{'pid':bjwindow.saleHeadId},
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
//								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								if (myMask != undefined){ myMask.hide();}
//								bjwindow.queryById("saveSJ").setDisabled(false);
							},
							failure : function(response, opts) {
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								Ext.MessageBox.alert("提示","保存失败！");
								if (myMask != undefined){ myMask.hide();}
								bjwindow.queryById("saveSJ").setDisabled(false);
							}
						});
					}
				}
			},
			"MaterialBGWindow":{
				addButtonClick:function(){
					var me = this;
					var bjwindow = me.getMaterialBGWindow();
					var itemGrid = bjwindow.queryById("itemGrid_ItemId");
					var itemStore = itemGrid.getStore();
					var model = Ext.create("SMSWeb.model.sale.MaterialBJModel");
					itemStore.insert(store.getCount(),model);
					return;
				},
				deleteButtonClick:function(){
					var me = this;
					var sjwindow = me.getMaterialBGWindow();
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
				//保存费用化
				saveSanjian:function(){
					var me = this;
					var bjwindow = me.getMaterialBGWindow();
					var newWindow = me.getNewSaleContentWindow();
					bjwindow.queryById("saveSJ").setDisabled(true);
					var _loadStatus = bjwindow.loadStatus;
					var _flowInfo = bjwindow.flowInfo;
					var saleHeadId = bjwindow.saleHeadId;
					var _matnr = bjwindow.matnr;
					var _formId = bjwindow.formId;
					var shId = bjwindow.shId;
					var _shouDaFang = bjwindow.shouDaFang
					var formValues = {};
					var _orderType=newWindow.orderType;
					var headForm = bjwindow.queryById("headForm_ItemId");
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
					
					var itemGrid = bjwindow.queryById("itemGrid_ItemId");
					var itemStore = itemGrid.getStore();
					if(itemStore.getCount()==0){
						Ext.MessageBox.alert("提示信息","必须有一条明细信息");
						bjwindow.queryById("saveSJ").setDisabled(false);
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
										var validate = beforeSaveSJValidate(bjwindow);
										if(validate==false){
											bjwindow.queryById("saveSJ").setDisabled(false);
											return;
										}
										salePriceForm = bjwindow.queryById("salePriceForm_ItemId");
							    		var _amount = salePriceForm.getForm().findField("amount").getValue();
							    		var _totalPrice = salePriceForm.getForm().findField("totalPrice").getValue();
							    		//组合saliItem
							    		saleItemFormValue['id'] = bjwindow.saleItemId;
							    		saleItemFormValue['amount'] = _amount;
							    		saleItemFormValue['totalPrice'] = _totalPrice;
							    		
							    		//组合saliItemPrice
							    		salePriceGrid = bjwindow.queryById("salePriceGrid_ItemId");
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
							 bgOrderType:_orderType,
							 kunnr:_shouDaFang
						});
						
						var myMask = new Ext.LoadMask(bjwindow,{msg:"请稍等..."}); 
						myMask.show();
						
						Ext.Ajax.request({
							url : 'main/myGoods/saveSJ?Pid='+shId,
							jsonData : gridData,
							method : 'POST',
							frame:true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.success){
									if("newSaleContentWindow"==bjwindow.sourceShow){
										var _form = values.data.id;
										headForm.getForm().setValues(values.data);
										if(newWindow!=undefined){
											var grid = newWindow.queryById("addBgGrid");
											console.log(me.getMaterialBGWindow().formId);
											var sid = me.getMaterialBGWindow().formId
											if(grid){
												if(sid!=null){
													grid.getStore().load({params:{pid:me.getMaterialBGWindow().formId}});
												}else{
													grid.getStore().load({params:{pid:shId}});
												}
											}
											newWindow.queryById("addFYH").hide();
										}
										itemGrid.getStore().load({params:{'id':_form}});
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
														salePriceGridStore.load({params:{'pid':bjwindow.saleItemId}});
														//刷新saleItemGird
														saleItemGrid.getStore().load({
															params:{'pid':bjwindow.saleHeadId},
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
								bjwindow.queryById("saveSJ").setDisabled(false);
							},
							failure : function(response, opts) {
								headForm.getForm().findField("loadStatus").setValue(_loadStatus);
								Ext.MessageBox.alert("提示","保存失败！");
								if (myMask != undefined){ myMask.hide();}
								bjwindow.queryById("saveSJ").setDisabled(false);
							}
						});
						
					}
				}
			
			},
			//打开文件上传窗口
			"NewBGFileUploadBaseWindow":{
			fileUploadButtonClick:function(info){
			    var me = this;
			    var fileUploadWindow = me.getNewBGFileUploadBaseWindow();
			    var fileUploadForm = fileUploadWindow.queryById("fileUploadForm");
			    var newWindow = me.getNewSaleContentWindow();
			    var file = fileUploadForm.getForm().findField("file").getValue();
			    var fileType = fileUploadForm.getForm().findField("fileType").getValue();
			    var pids = fileUploadWindow.formId
			    
		    	if(fileUploadForm.getForm().isValid()){  
                	fileUploadForm.submit({  
                        url: 'main/mm/bgfileupload',  
                        waitMsg: '上传文件中...',  
                        success: function(form, action) {
                        	var values = Ext.decode(action.response.responseText);	
                        	if(values.success){
                        		//保存成功后刷新对应grid
	                            Ext.MessageBox.alert("提示","上传成功！");
	                            
	                            var fileType = fileUploadWindow.fileType;
	                            var pid = fileUploadWindow.formId;
	                            
	                            var grid = newWindow.queryById("BJ_fujiangridItem");
	                            grid.getStore().load({params:{'fileType':fileType,'pid':pid}});
	            				pid="";
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
			},
			"newSaleContentWindow button[action=resetAddSaleForm]" : {
				click : function() {//新增订单界面重置
					Ext.MessageBox.confirm("温馨提示", "真的要清空表单数据吗？", function (btn) {
						if(btn=="yes"){
							Ext.getCmp("addSaleForm").getForm().reset();
						}
					});
				}
			},"newSaleContentWindow button[action=TaskHistoric]" : {
				click : function(btn) {//新增订单界面流程记录
					var uuidVal=Ext.getCmp("addSaleForm").getForm().getValues().shId;
					var id="";
					if(!uuidVal){
						Ext.MessageBox.alert("提示信息","请先保存订单");
						return;
					}
					Ext.Ajax.request({
						url : '/core/bpm/startedFlow',
						async:false,
						params : {
							uuid:uuidVal 
						},
						method : 'GET',
						success : function(response, opts) {
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								if(jsonResult.data.docStatus==1){
								  id=jsonResult.data.procinstid;
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
						failure : function(response, opts) {
							Ext.Msg.alert("错误代码:"+response.status,response.responseText);
						}
					});
					
					
					var win=Ext.create("SMSWeb.view.bpm.TaskHistoric", {procinstid:id});
					win.show();
				}
			},
			"newSaleContentWindow button[action=bgsaleTaskSubmit]":{
				click:function(btn){
					var form=Ext.getCmp("addSaleForm").getForm(); 
					var formVals=form.getValues();
					var saleId=formVals.shId;
					var jdName=formVals.jdName;
					if(!saleId){
						Ext.MessageBox.alert("提示信息","请先保存订单");
						return;
					}
				var orderType=formVals.orderType;
				btn.setDisabled(true); 
				/**
				 *激活流程之前操作 
				 */
				var f= false;
				Ext.Ajax.request({
					url : 'main/myGoods/saleBdBoforeActivate',
					method : 'GET',
					params : {
						'saleHeadId' : saleId
					},
					async:false,
					dataType : "json",
					contentType : 'application/json',
					success : function(response, opts) {
						_values = Ext.decode(response.responseText);
						if(!_values.success){
							Ext.Msg.show({
								title:"错误提示["+_values.errorCode+"]:",
								icon:Ext.Msg.ERROR,
								msg:_values.errorMsg,
								buttons:Ext.Msg.OK
							});
						}else{
							f=true;
						}
					},
					failure : function(response, opts) {
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				
				if(f){
				if("客户起草"==jdName){
					
				}else{
//					  * @param currentflow 当前环节编号
//					  * @param nextflow 下一环节
//					  * @param mappingId 关联表单id
//					  * @param mappingNo 关联表单编号 
//					  * @param desc 代办意见
//					  * @param errType 出错类型
//					  * @param errDesc 原因描述
					//提交流程
					Ext.Ajax.request({
						url : '/core/bpm/saleFlowCmmit',
						async:false,
						params : {
							saleId:saleId,
							orderType:orderType
						},
						method : 'POST',
						success : function(response, opts) {
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								btn.setDisabled(false);
								Ext.MessageBox.alert("提示信息","提交成功！订单号："+jsonResult.msg);
								Ext.getCmp("sale2Grid").getStore().loadPage(1);
								btn.up("window").close();
								/*Ext.Ajax.request({
									url : 'main/sale/querySaleById?id='+saleId,
									async:false,
									dataType: "json",
									success : function(response, opts) {
										var values = Ext.decode(response.responseText);
										if(values.success){ 
											var formData=values.data;
												form.setValues(formData);
											var fd=Ext.getCmp("mr_fielDset");
											if(fd){
												fd.show();
											}
										} else {
											Ext.MessageBox.alert("提示信息",values.errorMsg);
										}
										 
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
										 
									}
								});*/
								 
							}else{
								Ext.Msg.show({
									title:"错误提示["+jsonResult.errorCode+"]:",
									icon:Ext.Msg.ERROR,
									msg:jsonResult.errorMsg,
									buttons:Ext.Msg.OK
								});
								 
								btn.setDisabled(false);
							}
							
						},
						failure : function(response, opts) {
							 
							btn.setDisabled(false);
							var jsonResult = Ext.decode(response.responseText);
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
				btn.setDisabled(false);
				
				}
			},
			"newSaleContentWindow button[action=saleTaskSubmit]" : {
				click : function(btn) {//新增订单界面提交
					var form=Ext.getCmp("addSaleForm").getForm(); 
					var formVals=form.getValues();
					var saleId=formVals.shId;
					if(!saleId){
						Ext.MessageBox.alert("提示信息","请先保存订单");
						return;
					}
					var saleItmes=Ext.getCmp("addSaleItemsGrid");
					var gridDatas=saleItmes.getStore().getRange();
					if(gridDatas.length==0){
						Ext.MessageBox.alert("提示信息","请选择产品！");
						return;
					}else{
						var pose=0;
						Ext.Array.each(gridDatas,function(da){
							if(parseInt(da.get('posex'))>pose){
								pose=parseInt(da.get('posex'));
							}
						});
						var bjNum=gridDatas.length*10;
						if(!(pose==bjNum)){
							if(!saleId){
								Ext.MessageBox.alert("提示信息","删除产品请先保存订单^_^");
								return;
							}
						}
					}
					var flg=false;
					var serial = [];
					var ortype;
					Ext.Array.each(gridDatas, function(r) {
						serial.push(r.get('serial'));
						ortype=r.get('ortype');
					});
					if("OR1"==ortype){
						if(serial.length>0){
							Ext.Ajax.request({
								url : 'main/mm/checkFileStauts',
								params : {
									ids : serial
								},
								async:false,
								method : 'POST',
								success : function(response, opts) {
									var values = Ext.decode(response.responseText);
									if(values.success){
										flg=true;
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
						var orderType=formVals.orderType;
					btn.setDisabled(true); 
					 /**
					 *激活流程之前操作 
					 */
					var f= saleBoforeActivate(saleId);
					if(f){
					
//						  * @param currentflow 当前环节编号
//						  * @param nextflow 下一环节
//						  * @param mappingId 关联表单id
//						  * @param mappingNo 关联表单编号 
//						  * @param desc 代办意见
//						  * @param errType 出错类型
//						  * @param errDesc 原因描述
						//提交流程
						Ext.Ajax.request({
							url : '/core/bpm/saleFlowCmmit',
							async:false,
							params : {
								saleId:saleId,
								orderType:orderType
							},
							method : 'POST',
							success : function(response, opts) {
								var jsonResult = Ext.decode(response.responseText);
								if(jsonResult.success){
									btn.setDisabled(false);
									Ext.MessageBox.alert("提示信息","提交成功！订单号："+jsonResult.msg);
									Ext.getCmp("sale2Grid").getStore().loadPage(1);
									btn.up("window").close();
									/*Ext.Ajax.request({
										url : 'main/sale/querySaleById?id='+saleId,
										async:false,
										dataType: "json",
										success : function(response, opts) {
											var values = Ext.decode(response.responseText);
											if(values.success){ 
												var formData=values.data;
													form.setValues(formData);
												var fd=Ext.getCmp("mr_fielDset");
												if(fd){
													fd.show();
												}
											} else {
												Ext.MessageBox.alert("提示信息",values.errorMsg);
											}
											 
										},
										failure : function(response, opts) {
											Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
											 
										}
									});*/
									 
								}else{
									Ext.Msg.show({
										title:"错误提示["+jsonResult.errorCode+"]:",
										icon:Ext.Msg.ERROR,
										msg:jsonResult.errorMsg,
										buttons:Ext.Msg.OK
									});
									 
									btn.setDisabled(false);
								}
								
							},
							failure : function(response, opts) {
								 
								btn.setDisabled(false);
								var jsonResult = Ext.decode(response.responseText);
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
					btn.setDisabled(false);
					}
					
				}
			},
			
			"newSaleContentWindow button[id=addOrderItemBZ]" : {
				click : function() {//标准产品
//					 alert("标准产品");
					var _form=Ext.getCmp("addSaleForm").getForm();
					//var _saleFor=_form.findField("saleFor").getValue();
					//,saleFor:_saleFor
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询标准产品',type:'BZ',sourceShow:"newSaleContentWindow"}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemRJS]" : {
				click : function() {//标准产品
//					 alert("标准产品");
					var _form=Ext.getCmp("addSaleForm").getForm();
					//var _saleFor=_form.findField("saleFor").getValue();
					//,saleFor:_saleFor
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询标准产品',type:'RJS',sourceShow:"newSaleContentWindow",queryForm:""}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemCPDJ]" : {
				click : function() {//标准产品
//					 alert("标准产品");
					var _form=Ext.getCmp("addSaleForm").getForm();
					//var _saleFor=_form.findField("saleFor").getValue();
					//,saleFor:_saleFor
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
							{title:'查询标准产品',type:'CPDJ',sourceShow:"newSaleContentWindow",queryForm:""}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemFB]" : {
				click : function() {//非标产品
//					 alert("非标产品");
//					Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
//							{loadStatus:"2",title:'新增非标产品',sourceShow:"newSaleContentWindow"}).show();
					var _form=Ext.getCmp("addSaleForm").getForm();
					//var _saleFor=_form.findField("saleFor").getValue(); ,saleFor:_saleFor
					var orderType=_form.findField("orderType").getValue();
					console.log(orderType);
					if(orderType == null || orderType == undefined || orderType == ""){
						Ext.MessageBox.alert("提示信息","请选择订单类型");
						return;
					}
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询个人非标产品',type:'FB',sourceShow:"newSaleContentWindow"}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemXSDJ]" : {
				click : function() {//销售道具
//					 alert("销售道具");
				/*	var matnr ='102999995';
//					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
//							{loadStatus:'2',matnr : matnr,title:'销售道具'}).show();
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:"销售道具查询",type:'SJ',matnr:matnr,sourceShow:"newSaleContentWindow"}).show();*/
					
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999995",title:'销售道具查询',sourceShow:"newSaleContentWindow"}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemWJSJ]" : {
				click : function() {//五金散件
//					 alert("五金散件");
		/*			var matnr = '102999996';
//					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
//							{loadStatus:'2',matnr : matnr,title:'五金散件'}).show();
					
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:"五金散件查询",type:'SJ',matnr:matnr,sourceShow:"newSaleContentWindow"}).show();*/
					
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999996",title:'五金散件查询',sourceShow:"newSaleContentWindow"}).show();
					
					/*var matnr = '102999996';
					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
							{loadStatus:'2',matnr : matnr,title:'五金散件',sourceShow:"newSaleContentWindow"}).show();*/
					
				}
			},"newSaleContentWindow button[id=addOrderItemYMSJ]" : {
				click : function() {//移门散件
//					 alert("移门散件");
	/*				var matnr = '102999997';
//					Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
//							{loadStatus:'2',matnr : matnr,title:'移门散件'}).show();
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:"移门散件查询",type:'SJ',matnr:matnr,sourceShow:"newSaleContentWindow"}).show();*/
					
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999997",title:'移门散件查询',sourceShow:"newSaleContentWindow"}).show();
		
				}
			},
			"newSaleContentWindow button[id=addOrderItemGSSJ]" : {
				click : function() {//柜身散件
//					 alert("柜身散件");
					/*var matnr = '102999994'; 
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:"柜身散件查询",type:'SJ',matnr:matnr,sourceShow:"newSaleContentWindow"}).show();*/
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999994",title:'柜身散件查询',sourceShow:"newSaleContentWindow"}).show();
		
				}
			},
			"newSaleContentWindow button[id=addOrderItemKFBG]" : {
				click : function() {//客服补够
//					 alert("客服补够");
					var matnr = '102999998';
					Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
							{loadStatus:'2',matnr : matnr,title:'客服补购',type:'OR3',sourceShow:"newSaleContentWindow"}).show();
				}
			},
			"newSaleContentWindow button[id=addOrderItemMFDD]" : {
				click : function() {//免费订单
//					 alert("免费订单");
					var matnr = '102999998';
					Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
							{loadStatus:'2',matnr : matnr,title:'免费订单',type:'OR4',sourceShow:"newSaleContentWindow"}).show();
				}
			},
			"newSaleContentWindow button[id=removeOrderItems]" : {
				click : function() {//删除
					Ext.MessageBox.confirm('提示信息','确定要删除所选产品明细？',
					    	function(btn){
						        if(btn=='yes'){
						        	var form=Ext.getCmp("addSaleForm").getForm();
						        	var saleId=form.getValues().shId;
									var itemGrid=Ext.getCmp("addSaleItemsGrid");
								    var itemStore = itemGrid.getStore();
								    var sm = itemGrid.getSelectionModel();
								    var records = sm.getSelection();
								    var ids = [];
								    var matnr = "";
								    //不允许删除行号
									Ext.Array.each(records, function(r) {
										var id=r.get('id');
										if("160000002" == r.get('matnr')){
											matnr=r.get('matnr');
										}
										if(id){
											ids.push(id);
										}
										
									});
									if(matnr){
										var wjsj = Ext.getCmp("addOrderItemWJSJ");
										wjsj.setDisabled(false);
										var xsdj = Ext.getCmp("addOrderItemXSDJ");
										xsdj.setDisabled(false);
										var ymsj = Ext.getCmp("addOrderItemYMSJ");
										ymsj.setDisabled(false);
										var cpdj = Ext.getCmp("addOrderItemCPDJ");
										cpdj.setDisabled(false);
									}else{
										var rjs = Ext.getCmp("addOrderItemRJS");
										rjs.setDisabled(false);
									}
									if(ids.length>0){
										Ext.Ajax.request({
											url : 'main/sale/deleteSaleItemById',
											params : {
												ids : ids,
												saleId:saleId
	//											,custCode : saleForm.getForm().findField("shouDaFang").getValue()
											},
											method : 'POST',
											success : function(response, opts) {
												itemStore.load({params:{'pid':saleId}});
	//											itemStore.remove(sm.getSelection());
	//											calculationTotalPrice(saleForm,itemGrid,'delete');
												/*var saleId = saleForm.getForm().findField("id").getValue();
												itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
															        calculationTotalPrice(saleForm,itemGrid,'delete');
															    }});
												var Sale2GridView = me.getSale2GridView();
												if( typeof(Sale2GridView) != "undefined" ){
						                    		Sale2GridView.getStore().loadPage(1);
						                    	}*/
												Ext.MessageBox.alert("提示信息","删除成功！");
												
											},
											failure : function(response, opts) {
												Ext.MessageBox.alert("提示信息","删除失败！");
											}
										});
									}else{
										itemStore.remove(sm.getSelection());
									}
					                /*if (itemStore.getCount() > 0) {
					                    sm.select(0);
					                }*/							
						        } 
					    });
				}
			},
			"BgFujianWindow":{
				fileDownloadButtonClick:function(grid,rowIndex,colIndex){
					var me = this;
					var record = grid.getStore().getAt(rowIndex);
					var id = record.data.id;
					window.location.href = basePath+'main/sysFile/bjFileDownload'+"?id="+id;  
				}
			},
			"newSaleContentWindow":{
				//文件下载
				fileDownloadButtonClick:function(grid,rowIndex,colIndex){
					var me = this;
					var record = grid.getStore().getAt(rowIndex);
					var id = record.data.id;
					window.location.href = basePath+'main/sysFile/bjFileDownload'+"?id="+id;  
				},
				bgfileUploadButtonClick:function(info,grid){
					var me = this;
					var newSaleContentWindow = me.getNewSaleContentWindow();
					var grid = newSaleContentWindow.queryById("BJ_fujiangridItem");
					var record = grid.getStore().getCount();
					if(record<=0){
						Ext.create('SMSWeb.view.sale.NewBGFileUploadBaseWindow',{fileType: 'BJ',formId:info,title:"附件文件上传"}).show();
					}else{
			        	Ext.Ajax.request({
							url : 'main/mm/queryExpenditureFuleByPId',
							params : {
								pid : info
							},
							method : 'POST',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.data){
									Ext.create('SMSWeb.view.sale.NewBGFileUploadBaseWindow',{fileType: 'BJ',formId:info,title:"附件文件上传"}).show();
								}else{
									Ext.MessageBox.alert("温馨提示","请先把文件无效！");
								}
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","更改失败！");
							}
						});
					}
				},
				bgfileDeleteButtonClick:function(info,grid){
					var me = this;
					var ids = [];
					var newSaleContentWindow = me.getNewSaleContentWindow();
					var formId = newSaleContentWindow.formId;
					var grid = newSaleContentWindow.queryById("BJ_fujiangridItem");
					var records = grid.getSelectionModel().getSelection();
					if(records.length<=0){
						Ext.MessageBox.alert("温馨提示","请选择要失效的文件！");
						return;
					}
					Ext.Array.each(records, function(r) {
								ids.push(r.get('id'))
							});
					if(ids.length>0){
				        Ext.MessageBox.confirm('提示信息','确定更改文件为无效？',
						    function(btn){
						        if(btn=='yes'){
						        	
						        	Ext.Ajax.request({
										url : 'main/mm/deleteExpenditureFuleByIds',
										params : {
											'type':'BJ',
											ids : ids
										},
										method : 'POST',
										success : function(response, opts) {
											var values = Ext.decode(response.responseText);	
											if(values.success){
//												    grid = Ext.getCmp("BJ_fujiangridItem");
						                            grid.getStore().load({params:{'pid':info}});
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
				fileDeleteButtonClick:function(info,grid){
					var me = this;
					var ids = [];
					var newSaleContentWindow = me.getNewSaleContentWindow();
					var formId = newSaleContentWindow.formId;
					var grid = newSaleContentWindow.queryById("BJ_fujiangridItem");
					var records = grid.getSelectionModel().getSelection();
					if(records.length<=0){
						Ext.MessageBox.alert("温馨提示","请选择要失效的文件！");
						return;
					}
					Ext.Array.each(records, function(r) {
								ids.push(r.get('id'))
							});
					if(ids.length>0){
				        Ext.MessageBox.confirm('提示信息','确定更改文件为无效？',
						    function(btn){
						        if(btn=='yes'){
						        	
						        	Ext.Ajax.request({
										url : 'main/mm/deleteExpenditureFuleByIds',
										params : {
											'type':info,
											ids : ids
										},
										method : 'POST',
										success : function(response, opts) {
											var values = Ext.decode(response.responseText);	
											if(values.success){
//												    grid = Ext.getCmp("BJ_fujiangridItem");
						                            grid.getStore().load({params:{'pid':formId}});
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
				//查看费用化
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

				},
				itemEditButtonClick:function(orderType,_shouDaFang,grid,rowIndex,colIndex,saleFor){
					var record = grid.getStore().getAt(rowIndex);
					var isStandard = record.data.isStandard;
					var mmId= record.data.materialHeadId;
					var matnr=record.data.matnr;
					var type="";
					var title="";
					var _canModify=false;
					//如果行项目已经确认的，那么不允许修改信息
					if(('A'!=record.data.stateAudit) && ('C'!=record.data.stateAudit)){
						//Ext.MessageBox.alert("提示","已确认的行项目不能修改");
						_canModify= true;
					}
					if("OR1"==orderType||"OR7"==orderType||"OR8"==orderType||"OR9"==orderType){
						if(isStandard==1){//标准
							type="BZ";
							title="标准产品";
						}else{//非标
							type="FB";
							title="查看个人非标产品";
						}
		        	}else if("OR2"==orderType){
		        		if(matnr == '102999996'){
			        		 type="SJ";
			        		 title="五金散件查询";
			        	}else if(matnr == '102999995'){
			        		 type="SJ";
			        		 title="销售道具查询";
			        	}else if(matnr == '102999997'){
			        		type="SJ";
			        		title="移门散件查询";
			        	}
		        	}else if("OR3"==orderType){
		        		 matnr = '102999998';
		        		 title="部件补购查询";
		        	}else if("OR4"==orderType){
		        		 matnr = '102999998';
		        		 title="免费补货查询";
		        	}
					
					if("BZ"==type){ 
						Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
								{loadStatus:'2',buttonStatus:'1',formId : mmId,title:'标准商品',sourceShow:"newSaleContentWindow"}).show();
						 
					}else if("FB"==type){//非标产品
						Ext.create('SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
									{loadStatus:"2",title:'编辑非标产品',sourceShow:"newSaleContentWindow",uuId:mmId,canModify:_canModify,saleFor:saleFor}).show();
					}else if("SJ"==type){
						/*Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
							    {title:title,type:type,matnr:matnr,queryForm:query,sourceShow:"newSaleContentWindow"}).show();*/
						var _sanjianHeadId = record.data.sanjianHeadId;
						var _saleItemId=record.data.id;
						var _saleHeadId=record.data.pId;
						Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
								{loadStatus:'2',matnr : matnr,formId:_sanjianHeadId,title:title
							,shouDaFang:_shouDaFang,saleItemId:_saleItemId,saleHeadId:_saleHeadId,sourceShow:"newSaleContentWindow"}).show();
					}
				
				}
			},
			
			"Sale2MainView button[id=newSale2]" : {
				click : function() {
					Ext.create('SMSWeb.view.sale.NewSaleContentWindow',
							     {title:'补单',"orderType":"buDan"}).show(/*this,function(){}*/);
					}
			},
			
			"Sale2MainView button[id=deleteSale]" : {
				click : function() {
					var me = this;
					var Sale2GridView = me.getSale2GridView();
					var records = Sale2GridView.getSelectionModel().getSelection();
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
											url : 'main/sale/deleteSaleByIds',
											params : {
												ids : ids
											},
											method : 'POST',
											success : function(response, opts) {
						                    	Sale2GridView.getStore().loadPage(1);
												Ext.MessageBox.alert("提示信息","未提交流程审批的订单删除成功！");
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
			
			
			//grid编辑事件
			'Sale2GridView':{
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var id = record.get('id');
					var orderType = record.get('orderType');
					var typeFlag = "";
					if("OR3"==orderType || "OR4"==orderType){
						typeFlag = "buDan";
					}
//					Ext.create('SMSWeb.view.sale.NewSaleContentWindow',{title:'修改订单'}).show(/*this,function(){}*/);
//					alert();
					Ext.create('SMSWeb.view.sale.NewSaleWindow',
							     {formId : id,title:'订单修改',"orderType":typeFlag}).show(
//							    	 this,function(){
//							    	 Ext.MessageBox.show({
//								           title: 'Please wait',
//								           msg: 'Loading...',
//								           progressText: 'Initializing...',
//								           width:300,
//								           progress:true,
//								           closable:false
//							         });
//							    	 var f = function(v){
//							            return function(){
//							                if(v == 12){
//							                    Ext.MessageBox.hide();
//							                }else{
//							                    var i = v/11;
//							                    Ext.MessageBox.updateProgress(i, Math.round(100*i)+'% completed');
//							                }
//							           };
//							       };
//							       for(var i = 1; i < 13; i++){
//							           setTimeout(f(i), i*100);
//							       }
//							     }
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
							var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
							if("undefined"==typeof(NewSaleWindowInnerContent)){
								return;
							}
							var	itemsGrid = NewSaleWindowInnerContent.queryById("saleGrid");
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
									var matnr=mainView.matnr;
									var unit='EA';//默认单位 传到SAP
									if(!matnr){
										//非散件
										matnr=r.get('matnr');
										unit=r.get("meins"); 
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
							return;
/*							if(ids.length>0){
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
							}*/
							
						}
						
					}
				}
			}
			},
/*			"MaterialBase2SaleWindow button[itemId=queryMaterialBase2Sale]" : {
				click : function() {
					var me = this;
					var mainView = me.getMaterialBase2SaleWindow();
					
//					var mainform = mainView.queryById('MaterialMainFormBase2SaleView_itemId');
//					var formValues = mainform.getValues();
					
					var maingrid = mainView.queryById('MaterialMainGridBase2SaleView_itemId');
				    var store = maingrid.getStore();
				    store.loadPage(1);
				    
//				    if("BZ"==mainView.type){
//				    	formValues['type']="BZ";
//				    }else{
//				    	formValues['type']="SJ";
//				    	formValues['matnr']=mainView.matnr;
//				    }
//				    store.loadPage(1,{params:formValues});
				}
			},*/
			"NewSaleWindowInnerContent button[id=addOrderItemBZ]":{
				click : function() {//标准产品
//					 alert("标准产品");
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var kunnr = NewSaleWindowInnerContent.kunnr;
//					var _form=Ext.getCmp("addSaleForm").getForm();
//					var _saleFor=_form.findField("saleFor").getValue();
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询标准产品',type:'BZ',sourceShow:"newSaleContentWindow",saleFor:'0',kunnr:kunnr}).show();
				}
			},
			"NewSaleWindowInnerContent button[id=addOrderItemFB]":{
				click : function() {//非标产品
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var value = saleForm.getValues();
					var kunnr = NewSaleWindowInnerContent.kunnr;
					var orderType = value.orderType;
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:'查询个人非标产品',type:'FB',sourceShow:"newSaleContentWindow"
						    	,saleFor:'0',kunnr:kunnr,orderType:orderType}).show();
				}
			},
			"NewSaleWindowInnerContent button[id=addOrderItemGSSJ]":{
				click : function() {//柜身散件
					var isBg = 1//辨识是否补购
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var _kunnr = NewSaleWindowInnerContent.kunnr;
//					 alert("柜身散件");
					/*var matnr = '102999994'; 
					Ext.create('SMSWeb.view.mm.sale.MaterialBase2SaleWindow',
						    {title:"柜身散件查询",type:'SJ',matnr:matnr,sourceShow:"newSaleContentWindow"}).show();*/
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999994",title:'柜身散件查询',sourceShow:"NewSaleWindowInnerContent"
								,isBg:isBg,kunnr:_kunnr}).show();
		
				}
			},
			"NewSaleWindowInnerContent button[id=addOrderItemXSDJ]":{
				click : function() {//销售道具
					var isBg = 1//辨识是否补购
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var value = saleForm.getValues();
					var _kunnr = NewSaleWindowInnerContent.kunnr;
					var _bgOrderType = value.orderType;
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999995",title:'销售道具查询',sourceShow:"NewSaleWindowInnerContent"
								,isBg:isBg,kunnr:_kunnr,bgOrderType:_bgOrderType}).show();
				}
			},
			"NewSaleWindowInnerContent button[id=addOrderItemYMSJ]":{
				click : function() {//移门散件
					var isBg = 1//辨识是否补购
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var _kunnr = NewSaleWindowInnerContent.kunnr;
					var _formId = NewSaleWindowInnerContent.formId;
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var value = saleForm.getValues();
					var _bgOrderType = value.orderType;
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999997",title:'移门散件查询',sourceShow:"NewSaleWindowInnerContent"
								,isBg:isBg,kunnr:_kunnr,bgOrderType:_bgOrderType}).show();
		
				}
			},
			"NewSaleWindowInnerContent button[id=addOrderItemWJSJ]":{
				click : function() {//五金散件
					var isBg = 1//辨识是否补购
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var _kunnr = NewSaleWindowInnerContent.kunnr;
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var value = saleForm.getValues();
					var _bgOrderType = value.orderType;
					Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
							{ortype:"OR2",matnr:"102999996",title:'五金散件查询',sourceShow:"NewSaleWindowInnerContent"
								,isBg:isBg,kunnr:_kunnr,bgOrderType:_bgOrderType}).show();
					
				}
			},
			//弹窗增加行项目事件
			"NewSaleWindowInnerContent button[id=addOrderItem]":{
				click : function( bt, e, eOpts ) {
					//订单类型
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var saleForm =NewSaleWindowInnerContent.queryById("saleForm");
					var  formId =NewSaleWindowInnerContent.formId;
					var orderType = saleForm.getForm().findField("orderType");
					var orderTypeValue= orderType.getValue();
					
					if(orderTypeValue!=null && orderTypeValue!=""){
						if("OR3"==orderTypeValue || "OR4"==orderTypeValue){
							Ext.create('SMSWeb.view.mm.myGoods.MyGoodsBujianWindow',
									{ortype:orderTypeValue,title:'我的商品',formId:formId}).show();
						}else{
							if("OR7"==orderTypeValue || "OR8"==orderTypeValue || "OR9"==orderTypeValue){
								orderTypeValue = "OR1";
							}
							Ext.create('SMSWeb.view.mm.myGoods.MyGoodsWindow',
									{ortype:orderTypeValue,title:'我的商品'}).show();
						}
					}else{
						Ext.MessageBox.alert("提示信息","请选择订单类型！");
					}
				}
			},
			
			//弹窗删除行项目事件
			"NewSaleWindowInnerContent button[id=removeOrderItem]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					Ext.MessageBox.confirm('提示信息','确定要删除所选产品明细？',
				    	function(btn){
					        if(btn=='yes'){
					        	var saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					        	var saleid =saleForm.getForm().findField("id").getValue();
								var itemGrid = NewSaleWindowInnerContent.queryById("saleGrid");
							    var itemStore = itemGrid.getStore();
							    var sm = itemGrid.getSelectionModel();
							    var records = sm.getSelection();
							    var ids = [];
								Ext.Array.each(records, function(r) {
									ids.push(r.get('id'));
								});
								Ext.Ajax.request({
									url : 'main/sale/deleteSaleItemByIds',
									params : {
										ids : ids,
										custCode : saleForm.getForm().findField("shouDaFang").getValue()
									},
									method : 'POST',
									success : function(response, opts) {
										itemStore.remove(sm.getSelection());
										calculationTotalPrice(saleForm,itemGrid,'delete');
										var saleLogisticsGrid=saleForm.queryById("saleLogisticsGridItemId");
				                		saleLogisticsGrid.getStore().load({params:{pid:saleid}});
										/*var saleId = saleForm.getForm().findField("id").getValue();
										itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
													        calculationTotalPrice(saleForm,itemGrid,'delete');
													    }});
										var Sale2GridView = me.getSale2GridView();
										if( typeof(Sale2GridView) != "undefined" ){
				                    		Sale2GridView.getStore().loadPage(1);
				                    	}*/
										
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
			
			//弹窗保存订单
			"NewSaleWindowInnerContent button[id=saveOrder]":{
				click : function( bt, e, eOpts ) {
					Ext.getCmp("saveOrder").setDisabled(true);
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					//head信息
					var saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					var itemGrid = NewSaleWindowInnerContent.queryById("saleGrid");
				    var itemStore = itemGrid.getStore();
				    var Sale2GridView = me.getSale2GridView();
					
					var validate = beforeSaveValidate(saleForm,itemGrid);
					if(validate==false){
						Ext.getCmp("saveOrder").setDisabled(false);
						return;
					}
					var submitInfo = generateSubmitInfo(saleForm,itemGrid);
					var saleHeadForm = submitInfo['saleHeadForm'];
					//console.info(saleHeadForm);
    				var terminalClientForm = submitInfo['terminalClientForm'];
    				var dataArr = submitInfo['dataArr'];
    				var dataArr2 = submitInfo['dataArr2'];
    				
				    
//				    var delRecords = itemStore.getRemovedRecords();
//					var delItems = [];
//					//delete item foreach to delItems
//					for(var i = 0; i < delRecords.length ; i++){
//						var record = delRecords[i].data;
//						var json_x = {};
//						Ext.Object.each(record, function(key, value, myself) {
//							if(key.indexOf(".")!=-1){
//								var keys = key.split(".");
//								json_x[keys[0]] = {id:value};
//							}else{
//								json_x[key] = value;
//							}
//							
//						});
//						delItems[i] = json_x;
//					}
					
			        var gridData = Ext.encode({
						terminalClient : terminalClientForm,saleItemList : dataArr,/*delItems:delItems,*/saleOneCustList:dataArr2
					});
			        
			        
			        //add by hzm 20170316 START
			        var orderType = saleForm.getForm().findField("orderType").getValue();
			        var pOrderCode = saleForm.getForm().findField("pOrderCode").getValue();
			        var hasshoudafang = saleForm.getForm().findField("shouDaFang").getValue().indexOf("LZ") > -1;
			        //参考订单为空，且订单类型为：部件补购单，提示客服填写参考订单号
					if(orderType == "OR3" && (pOrderCode==null || pOrderCode=="") && hasshoudafang){
						Ext.MessageBox.confirm("提示","部件补购单，请填好参考订单，方便带出客户地址！否，则继续保存。是，则编辑菜单。",function(btn){
							if(btn == "yes"){
								Ext.getCmp("saveOrder").setDisabled(false);
								return;
							}else{
								//加一句
								saveTabemetod(saleHeadForm,gridData,saleForm,itemStore,itemGrid,Sale2GridView);
							}
						})
					}else{ 
						        //加一句
								if(orderType == "OR4" && (pOrderCode==null || pOrderCode=="")){
									//Ext.MessageBox.alert("提示信息","请关联参考订单号！");
									//return;
									//delete by hzm 20170426
									saveTabemetod(saleHeadForm,gridData,saleForm,itemStore,itemGrid,Sale2GridView);
								}else{
									saveTabemetod(saleHeadForm,gridData,saleForm,itemStore,itemGrid,Sale2GridView);
								}
								
							
				    }
					//add by hzm 20170316 END
				}
			},
			
			//弹窗订单传输SAP
			"NewSaleWindowInnerContent button[id=tranSap]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					//add by Mark on 20160602 --start
					var _values;
					var _sale_item_price;
					var flag=true;
					var _saleId = NewSaleWindowInnerContent.query('form[itemId=saleForm] hiddenfield[name=id]')[0].getValue();
					Ext.Ajax.request({
						url:'main/sale/findItemsByPid',
						method:'get',
						params:{
							'pid':_saleId
						},
						async:false,
						dataType:"json",
						contentType:'application/json',
						success:function(response,opts){
							_values=Ext.decode(response.responseText);
						},
						failure:function(response,opts){
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
					});
					if(false == _values.success){
						flag = false;
						Ext.MessageBox.alert("提示",_values.errorMsg);
					}
					if(_values==null || _values.content.length<=0 ){
						
					}else
					{
						var x;
						for(x in _values.content){
							var _object=_values.content[x];
							/*if(("1"!=_object.isStandard || 'OR4'==_object.ortype) && Ext.isEmpty(_object.priceId)){
								flag=false;
								Ext.MessageBox.alert("提示","行号："+_object.posex+" 没有报价清单！");
								break;
							}else{*/
									Ext.Ajax.request({
									url:'main/mm/querySaleItemPrice',
									method:'get',
									params:{
										'pid':_object.id,
										'page':1,
										'start':0,
										'limit':25
									},
									async:false,
									dataType:"json",
									contentType:'application/json',
									success:function(response,opts){
										_sale_item_price=Ext.decode(response.responseText);
									},
									failure:function(response,opts){
										Ext.MessageBox.alert("提示","加载数据失败！");
									}
								});
							var _index;
						    for(_index in _sale_item_price){
						    	if("PR01"==_sale_item_price[_index].type && _sale_item_price[_index].total<=0 ){
								flag=false;
								Ext.MessageBox.alert("提示","行号："+_object.posex+" 没有填写价格！");
								break;
								}
							}
							//}
						}
					}
					
					Ext.Ajax.request({
					url : 'main/sale/validateCheckPrice',
					method : 'GET',
					params : {
						'saleHeadId' : _saleId,
						'flag':true
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
				if(false == _values.success){
					flag = false;
					Ext.MessageBox.alert("提示",_values.errorMsg);
				}
					if(!flag){
						return ;
					}
					//add by Mark on 20160602 --end
					//head信息
					Ext.getCmp("tranSap").setDisabled(true);
					var saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					var itemGrid = NewSaleWindowInnerContent.queryById("saleGrid");
				    var itemStore = itemGrid.getStore();
//					
//					var validate = beforeSaveValidate(saleForm,itemGrid);
//					if(validate==false){
//						return;
//					}
//					var submitInfo = generateSubmitInfo(saleForm,itemGrid);
//					var saleHeadForm = submitInfo['saleHeadForm'];
//    				var terminalClientForm = submitInfo['terminalClientForm'];
//    				var dataArr = submitInfo['dataArr'];
//    				var dataArr2 = submitInfo['dataArr2'];
//				    
//					
//			        var gridData = Ext.encode({
//						terminalClient : terminalClientForm,saleItemList : dataArr,saleOneCustList:dataArr2
//					});
					Ext.Ajax.request({
						url : 'main/sale/tranSap',
						params : {'saleId':saleForm.getForm().findField("id").getValue()},//saleHeadForm,
						method : 'POST',
						async:false,
						timeout:600000,
//						frame : true,
//						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){
								/*saleForm.getForm().findField("sapOrderCode").setValue(values.id);
								saleForm.getForm().findField("sapOrderCode").initValue();
								saleForm.getForm().findField("yuJiDate").setValue(values.yuJiDate);
								saleForm.getForm().findField("yuJiDate").initValue();
								Ext.getCmp("saveOrder").hide();
								Ext.getCmp("tranSap").hide();*/
								Ext.MessageBox.alert("提示信息",values.msg);
							} else {
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
							itemStore.load({params:{'pid':saleForm.getForm().findField("id").getValue()}});
							Ext.getCmp("tranSap").setDisabled(false);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","传输失败");
							Ext.getCmp("tranSap").setDisabled(false);
						}
					});
				}
			},
			//弹窗查询送达方列表
			"newSaleContentWindow button[itemId=songDaFang_queryCust]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.cust.NewCustWindowForSale',
				     {title:'送达方查询','custFlag':'songDaFang','iskf':'1'}).show(this,function(){
				    	 queryCust = "songDaFang";
				     });
				}
			},
			//弹窗查询送达方列表
			"NewSaleWindowInnerContent button[itemId=songDaFang_queryCust]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.cust.NewCustWindowForSale',
				     {title:'送达方查询','custFlag':'songDaFang'}).show(this,function(){
				    	 queryCust = "songDaFang";
				     });
				}
			},
			//售达方
			"NewSaleWindowInnerContent button[itemId=shouDaFang_queryCust]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.cust.NewCustWindowForSale',
				     {title:'售达方查询','custFlag':'shouDaFang','iskf':'0'}).show(this,function(){
				    	 queryCust = "shouDaFang";
				     });
				}
			},
			"newSaleContentWindow button[itemId=shouDaFang_queryCust]":{
				click : function( bt, e, eOpts ) {
					Ext.create('SMSWeb.view.cust.NewCustWindowForSale',
							{title:'售达方查询','custFlag':'shouDaFang','iskf':'1'}).show(this,function(){
								queryCust = "shouDaFang";
							});
				}
			},
			"NewSaleWindowInnerContent button[itemId=pOrderCodeButton]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.sale.NewSaleWindowForBg',
				     {title:'订单查询','saleFlag':'bdFlag'}).show(/*this,function(){}*/);
				}
			},
			"newSaleContentWindow button[itemId=pOrderCodeButton]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.sale.NewSaleWindowForBg',
				     {title:'订单查询','saleFlag':'bdFlag'}).show(/*this,function(){}*/);
				}
			},
			
			//修改单号按钮事件
			"NewSaleWindowInnerContent button[itemId=orderCodeQueryButton]":{
				click : function( bt, e, eOpts ) {
					Ext.create('SMSWeb.view.sale.NewOrderCodeWindow',
				     {title:'免费单号修改'}).show();
				}
			},
			
			//批量下载PDF文件
			"NewSaleWindowInnerContent button[id=batchPdfDownload]":{
				click:function(bt,e,eOpts){
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var _saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					var _saleGrid = NewSaleWindowInnerContent.queryById("saleGrid");
					Ext.MessageBox.confirm("温馨提示","批量下载pdf文件",function (btn){
	        			if(btn=="yes"){
	        				var _itemStore = _saleGrid.getStore();
	        				var _ordercode = _saleForm.getForm().findField("orderCode").getValue();//取得其它FORM的订单号
								var _itemCount = _itemStore.getCount();
								var _id='';
								for (var i = 0; i <_itemCount; i++) {
									var record = _itemStore.getAt(i);
									_id += record.get("pdfId")+',';
								}
								var _ids = _id.substring(0,_id.length-1);//去掉最后一个字符
								var url = basePath+'main/mm/bitfileDownload?ids='+_ids+'&tempordercode='+_ordercode;
							    window.open(url,"批量下载PDF文件",'fullscreen');
	        			}
	        		});
				}
			},
			
			//批量下载报价清单文件
			"NewSaleWindowInnerContent button[id=batchjgdDownload]":{
				click:function(bt,e,eOpts){
					var me = this;
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var _saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					var _saleGrid = NewSaleWindowInnerContent.queryById("saleGrid");
					Ext.MessageBox.confirm("温馨提示","批理下载报价清单",function (btn){
	        			if(btn=="yes"){
	        				var _itemStore = _saleGrid.getStore();
	        				var _ordercode = _saleForm.getForm().findField("orderCode").getValue();//取得其它FORM的订单号
							/*var _itemCount = _itemStore.getCount();
							var _id='';
							for (var i = 0; i <_itemCount; i++) {
								var record = _itemStore.getAt(i);
								_id += record.get("priceId")+',';
							}
							var _ids = _id.substring(0,_id.length-1);//去掉最后一个字符
*/							var url = basePath+'main/mm/batchFileDownload?tempordercode='+_ordercode;
						    window.open(url,"批量下载报价清单",'fullscreen');
	        			}
	        		});
				}
			},
			"NewOrderCodeWindow button[itemId=orderCodeUpdate]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewOrderCodeWindow = me.getNewOrderCodeWindow();
					var orderCodeForm = NewOrderCodeWindow.queryById("orderCodeForm");
					var updateCode = orderCodeForm.getForm().findField("updateCode").getValue();
					
					var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
					var saleForm = NewSaleWindowInnerContent.queryById("saleForm");
					var saleGrid = NewSaleWindowInnerContent.queryById("saleGrid");
					
					var saleId = saleForm.getForm().findField("id").getValue();
					if(Ext.String.trim(updateCode)==""){
						Ext.MessageBox.alert("提示信息","请输入修改后的单号");
					}else
						//add by Mark on 20160415--start
						if(Ext.String.trim(updateCode).length>16){
							Ext.MessageBox.alert("提示信息","单号长度不能大于16位");
						}
						//add by Mark on 20160415--end
						else{
						Ext.Ajax.request({
							url : 'main/sale/updateOrderCode',
							params : {'saleId':saleId,'updateCode':Ext.String.trim(updateCode)},
							method : 'POST',
							frame : true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								if(values.success){
									saleForm.getForm().findField("orderCode").setValue(updateCode);
									saleForm.getForm().findField("orderCode").initValue();
									Ext.MessageBox.alert("提示信息","保存成功");
								} else {
									Ext.MessageBox.alert("提示信息",values.errorMsg);
								}
								saleGrid.getStore().load({params:{'pid':saleId}});
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","网络异常");
							}
						});
					}
				}
			},
			
			
			"NewCustWindowForSale button[itemId=queryCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowForSale = me.getNewCustWindowForSale();
					//head信息
//					var custForm = NewCustWindowForSale.queryById("custForm");
//					var formValues = custForm.getValues();
					//物料清单信息
					var itemGrid = NewCustWindowForSale.queryById("custGrid");
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
			
			"NewCustWindowForSale button[id=confirmCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowForSale = me.getNewCustWindowForSale();
					var iskf = NewCustWindowForSale.iskf
					if(iskf==1){//客服补购查询客户编码
						var itemGrid = NewCustWindowForSale.queryById("custGrid");
						var store = itemGrid.getStore();
						var records = itemGrid.getSelectionModel().getSelection();
						if(records.length == 0) {
							Ext.MessageBox.alert('提示信息','请选择一条明细');
						}else if(records.length == 1){
							var record = records[0];
							var code = record.get('kunnr');
							var name1 = record.get('name1');
							var ktokd = record.get('ktokd');
							var kunnrS = record.get('kunnrS');
							
							var NewSaleContentWindow = me.getNewSaleContentWindow();
//							var	saleForm = newSaleWindowInnerContent.queryById("saleForm");
							var	saleForm = NewSaleContentWindow.queryById("addSaleForm");
							
							if(queryCust=="songDaFang"){
								saleForm.getForm().findField("songDaFang").setValue(code);
								saleForm.getForm().findField("songDaFang").initValue();
							}
							if(queryCust=="shouDaFang"){
								saleForm.getForm().findField("songDaFang").setValue(code);
								saleForm.getForm().findField("songDaFang").initValue();
								saleForm.getForm().findField("shouDaFang").setValue(code);
								saleForm.getForm().findField("shouDaFang").initValue();
								saleForm.getForm().findField("bgShouDaFang").setValue(code);
								saleForm.getForm().findField("bgShouDaFang").initValue();
								saleForm.getForm().findField("kunnrName1").setValue(name1);
								saleForm.getForm().findField("kunnrName1").initValue();
								saleForm.getForm().findField("songDaFang").setValue(kunnrS);
								saleForm.getForm().findField("songDaFang").initValue();
							}
							
							//售达方	选择 临时售达方类型Z900
							NewCustWindowForSale.close();
							
						}else{
							Ext.MessageBox.alert('提示信息','只能选择一条明细');
						}
					}else{
						var itemGrid = NewCustWindowForSale.queryById("custGrid");
						var store = itemGrid.getStore();
						var records = itemGrid.getSelectionModel().getSelection();
						if(records.length == 0) {
							Ext.MessageBox.alert('提示信息','请选择一条明细');
						}else if(records.length == 1){
//                    	Ext.Array.each(records, function(r) {
//							Ext.MessageBox.alert('提示信息',r.get('code'));
//						});
							var record = records[0];
							var code = record.get('kunnr');
							var name1 = record.get('name1');
							var ktokd = record.get('ktokd');
							var kunnrS = record.get('kunnrS');
							
							var NewSaleContentWindow = me.getNewSaleContentWindow();
							var	saleForm = NewSaleContentWindow.queryById("addSaleForm");
							
							if(queryCust=="songDaFang"){
								saleForm.getForm().findField("songDaFang").setValue(code);
								saleForm.getForm().findField("songDaFang").initValue();
							}
							if(queryCust=="shouDaFang"){
								saleForm.getForm().findField("shouDaFang").setValue(code);
								saleForm.getForm().findField("shouDaFang").initValue();
								saleForm.getForm().findField("kunnrName1").setValue(name1);
								saleForm.getForm().findField("kunnrName1").initValue();
								saleForm.getForm().findField("songDaFang").setValue(kunnrS);
								saleForm.getForm().findField("songDaFang").initValue();
							}
							
							var saleOneCustGrid = saleForm.queryById("saleOneCustGrid");
							var saleOneCustStore = saleOneCustGrid.getStore();
							//获取tabs
							var tabs = saleForm.queryById("centerTabpanel");
							//售达方	选择 临时售达方类型Z900
							if(queryCust=="shouDaFang"){
								if(ktokd=="Z900"){
									var flag = false;//不存在
									var count = saleOneCustStore.getCount();
									for (var i = 0; i <count; i++) {
										var record = saleOneCustStore.getAt(i);
										var kunnr = record.get("kunnr");
										if(kunnr==code){
											flag = true;//存在
											break;
										}
									}
									if(!flag){
										var data=[{
											'id':null,
											'createUser':'',
											'updateUser':'',
											'createTime':null,
											'updateTime':null,
											'kunnr':code,
											'anred':'',
											'saleOneCustName1':'',
											'street':'',
											'pstlz':'',
											'mcod3':'',
											'land1':'CN',
											'regio':'',
											'telf1':'',
											'ort02':'',
											'saleOneCustType':'临时售达方'
										}];
										saleOneCustStore.insert(count, data);
									}
									//临时送/售达方显示
									tabs.items.getAt(4).tab.show();
								}else{
									var ids = [];
									var count = saleOneCustStore.getCount();
									for (var i = 0; i < count; i++) {
										var record = saleOneCustStore.getAt(i);
										var saleOneCustType = record.get("saleOneCustType");
										if(saleOneCustType=="临时售达方"){//删除临时售达方
											var id = record.get("id");
											ids.push(id);
											Ext.Ajax.request({
												url : 'main/sale/deleteSaleOneCustByIds',
												params : {
													ids : ids
												},
												method : 'POST',
												success : function(response, opts) {
													saleOneCustStore.removeAt(i);
												},
												failure : function(response, opts) {
												}
											});
										}
									}
								}
							}
							
							//送达方	选择 临时送达方类型Z720
							if(queryCust=="songDaFang"){
								if(ktokd=="Z720"){
									var flag = false;//不存在
									var count = saleOneCustStore.getCount();
									for (var i = 0; i < count; i++) {
										var record = saleOneCustStore.getAt(i);
										var kunnr = record.get("kunnr");
										if(kunnr==code){
											flag = true;//存在
											break;
										}
									}
									if(!flag){
										var data=[{
											'id':null,
											'createUser':'',
											'updateUser':'',
											'createTime':null,
											'updateTime':null,
											'kunnr':code,
											'anred':'',
											'saleOneCustName1':'',
											'street':'',
											'pstlz':'',
											'mcod3':'',
											'land1':'CN',
											'regio':'',
											'telf1':'',
											'ort02':'',
											'saleOneCustType':'临时送达方'
										}];
										saleOneCustStore.insert(count, data);
									}
									//临时送/售达方显示
									tabs.items.getAt(4).tab.show();
								}else{
									var ids = [];
									var datas = [];
									var count = saleOneCustStore.getCount();
									for (var i = 0; i < count; i++) {
										var record = saleOneCustStore.getAt(i);
										var saleOneCustType = record.get("saleOneCustType");
										var id = record.get("id");
										if(saleOneCustType=="临时送达方"){//删除临时送达方
											if(id!=null && id!=""){
												ids.push(id);
											}
										}else{
											if(id==null || id==""){
												var data=[{
													'id':record.get("id"),
													'createUser':record.get("createUser"),
													'updateUser':record.get("updateUser"),
													'createTime':record.get("createTime"),
													'updateTime':record.get("updateTime"),
													'kunnr':record.get("kunnr"),
													'anred':record.get("anred"),
													'saleOneCustName1':record.get("saleOneCustName1"),
													'street':record.get("street"),
													'pstlz':record.get("pstlz"),
													'mcod3':record.get("mcod3"),
													'land1':record.get("land1"),
													'regio':record.get("regio"),
													'telf1':record.get("telf1"),
													'ort02':record.get("ort02"),
													'saleOneCustType':record.get("saleOneCustType")
												}];
												datas.push(data);
											}
										}
									}
									if(ids.length>0){
										Ext.Ajax.request({
											url : 'main/sale/deleteSaleOneCustByIds',
											params : {
												ids : ids
											},
											method : 'POST',
											success : function(response, opts) {
												saleOneCustStore.load({params:{'pid':saleForm.getForm().findField("id").getValue()},callback: function(records, operation, success) {
													if(datas.length>0){
														for(var k=0;k<datas.length;k++){
															saleOneCustStore.insert(saleOneCustStore.getCount()+k, datas[k]);
														}
													}
													var count = saleOneCustStore.getCount();
													if(count==0){
														//临时送/售达方隐藏
														tabs.items.getAt(4).tab.hide();
													}else{
														//临时送/售达方显示
														tabs.items.getAt(4).tab.show();
													}
												}});
											},
											failure : function(response, opts) {
											}
										});
									}else{
										saleOneCustStore.load({params:{'pid':saleForm.getForm().findField("id").getValue()},callback: function(records, operation, success) {
											if(datas.length>0){
												for(var k=0;k<datas.length;k++){
													saleOneCustStore.insert(saleOneCustStore.getCount()+k, datas[k]);
												}
											}
											var count = saleOneCustStore.getCount();
											if(count==0){
												//临时送/售达方隐藏
												tabs.items.getAt(4).tab.hide();
											}else{
												//临时送/售达方显示
												tabs.items.getAt(4).tab.show();
											}
										}});
									}
								}
							}
							NewCustWindowForSale.close();
							
						}else{
							Ext.MessageBox.alert('提示信息','只能选择一条明细');
						}
					}
				}
			},
			
			
			//文件上传窗口
			"SysFileUploadWindow":{
				fileUploadButtonClick:function(){
				    var me = this;
				    var fileUploadWindow = me.getSysFileUploadWindow();
				    var fileUploadForm = fileUploadWindow.queryById("fileUploadForm");
				    var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
	                if(fileUploadForm.getForm().isValid()){  
	                	fileUploadForm.submit({  
	                        url: 'main/sysFile/fileupload',  
	                        //waitMsg: '上传文件中...',  
	                        success: function(form, action) {
	                        	//保存成功后刷新对应grid
	                            Ext.MessageBox.alert("提示信息","上传成功！");
	                            
	                            var fileType =  fileUploadForm.getForm().findField("fileType").getValue();
	                            var foreignId = fileUploadForm.getForm().findField("foreignId").getValue();
                    			
	                            var sysFileGrid = NewSaleWindowInnerContent.queryById("sysFileGrid");
	                            sysFileGrid.getStore().load({params:{'foreignId':foreignId,'fileType':fileType}});
	            				
	                            fileUploadWindow.close();
	                        },
	                        failure : function(form, action) {
								Ext.MessageBox.alert("提示信息","上传失败！");
							}
	                    });  
	                }
				}
			},
			
			"NewSaleWindow":{
				//文件上传
				fileUploadButtonClick:function(info,foreignId){
					if(null!=foreignId && ""!=foreignId){
						Ext.create('SMSWeb.view.sys.SysFileUploadWindow',{fileType:info,formId : foreignId,title:info+'文件上传'}).show();
					}else{

					}
				},
				//文件下载
				fileDownloadButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var id = record.data.id;
					window.location.href = basePath+'main/sysFile/fileDownload'+"?id="+id;  
				},
				//文件删除
				fileDeleteButtonClick:function(info,grid){
					var ids = [];
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
						ids.push(r.get('id'))
					});
					if(ids.length>0){
						Ext.Ajax.request({
							url : 'main/sysFile/deleteSysFileByIds',
							params : {
								ids : ids
							},
							method : 'POST',
							success : function(response, opts) {
								grid.getStore().remove(records);
								Ext.MessageBox.alert("提示信息","删除成功！");
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","删除失败！");
							}
						});
					}
				},
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var me = this;
					var record = grid.getStore().getAt(rowIndex);
					var _headId = record.data.materialHeadId;//物料ID
					var _saleItemId = record.data.id;//销售明细id
					var _myGoodsId = record.data.myGoodsId;//我的物品id
					var _isStandard = record.data.isStandard;//是否标准
					var saleView = me.getNewSaleWindowInnerContent();
					var pOrderCode = saleView.pOrderCode;
					var jdName = saleView.jdName;
					var saleViewOrderType = saleView.orderType;
					var saleForm = saleView.queryById("saleForm");
					var shId = saleView.formId;
					var _shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
					var _matnr = record.data.matnr;//物料编码(散件通用码)
					var _sanjianHeadId = record.data.sanjianHeadId;//散件headID
					var _bujianId = record.data.bujianId;//补件ID
					var _ortype = record.data.ortype;
					var _orderCodePosex = record.data.orderCodePosex;//
					var _saleHeadId = saleForm.getForm().findField("id").getValue();
					if(true){//_saleItemId

						if("1"==_isStandard){
							//散件
							if("102999995"==_matnr||"102999996"==_matnr||"102999997"==_matnr||"102999994"==_matnr||"102999998"==_matnr){

								if("102999995"==_matnr){
									_title = "销售道具";
								}else if("102999996"==_matnr){
									_title = "五金散件";
								}else if("102999997"==_matnr){
									_title = "移门散件";
								}else if("102999994"==_matnr){
									_title = "柜身散件";
								}else if("102999998"==_matnr){
									_title = "费用化";
								}
								if("102999998"==_matnr){
									Ext.create('SMSWeb.view.sale.MaterialBGWindow', 
											{loadStatus:'3',matnr : '102999998',title:'费用化',sourceShow:"newSaleContentWindow",shId:_sanjianHeadId,saleHeadId:_saleHeadId,jdName:jdName,saleItemId:_saleItemId,source:'update'}).show();
								}
								else{
									
									Ext.create('SMSWeb.view.mm.sale.MaterialSJWindow', 
											{loadStatus:'3',matnr : _matnr,formId:_sanjianHeadId,title:_title
										,shouDaFang:_shouDaFang,saleItemId:_saleItemId,saleHeadId:_saleHeadId}).show();
								}

							}else{
								//标准产品
								var _propertyItemInfo;
//								var _materialPrice;
								Ext.Ajax.request({
									url : 'main/mm/getSaleItem',
									method : 'GET',
									params : {
										'id':_saleItemId
									},
									async:false,
									dataType : "json",
									contentType : 'application/json',
									success : function(response, opts) {
										var responseData = Ext.decode(response.responseText);  
										if(responseData.success){
											_propertyItemInfo = responseData.data.materialPropertyItemInfo;
//											_materialPrice = responseData.data.materialPrice;//materialPrice:_materialPrice,
										}
									},
									failure : function(response, opts) {
										Ext.MessageBox.alert("tips","加载数据失败！");
									}
								});

								Ext.create('SMSWeb.view.mm.sale.MaterialBZWindow', 
										{shouDaFang:_shouDaFang,saleItemId:_saleItemId,loadStatus:'3',formId : _headId,
									propertyItemInfo:_propertyItemInfo,title:'标准商品',saleHeadId:_saleHeadId,myGoodsId:_myGoodsId}).show(/*this,function(){}*/);

							}

						}else if("0"==_isStandard){
							/*var _saleForCombobox=Ext.getCmp("saleFor");*/
							var _saleFor=record.data.saleFor;
							if( "buDan"==saleViewOrderType){
								Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
										{shouDaFang:_shouDaFang,saleItemId:_saleItemId,loadStatus:"3",formId:_headId,title:'非标产品',saleHeadId:_saleHeadId,orderCodePosex:_orderCodePosex,myGoodsId:_myGoodsId,saleFor:_saleFor,saleViewOrderType:saleViewOrderType,pOrderCode:pOrderCode}).show(/*this,function(){}*/);
							}else{
								Ext.create('SMSWeb.view.mm.base.NewMaterialBaseWindow',
										{shouDaFang:_shouDaFang,saleItemId:_saleItemId,loadStatus:"3",formId:_headId,title:'非标产品',saleHeadId:_saleHeadId,orderCodePosex:_orderCodePosex,myGoodsId:_myGoodsId,saleFor:_saleFor}).show(/*this,function(){}*/);
							}
						}else{
							if("22999999999"==_matnr){
								//补件
/*								if("OR3"==_ortype){
									Ext.create('SMSWeb.view.sale.MaterialBGWindow', 
											{loadStatus:'3',matnr : '102999998',title:'费用化',sourceShow:"newSaleContentWindow",shId:shId,saleHeadId:_saleHeadId,jdName:jdName,saleItemId:_saleItemId}).show();

								}else if("OR4"==_ortype){
									Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
											{loadStatus:'3',matnr : _matnr,title:'免费订单',type:_ortype,formId:_bujianId
										,shouDaFang:_shouDaFang,saleItemId:_saleItemId,saleHeadId:_saleHeadId
											}).show();
								}*/

								//补件
								if("OR3"==_ortype){
									Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
											{loadStatus:'3',matnr : _matnr,title:'客服补购',type:_ortype,formId:_bujianId
										,shouDaFang:_shouDaFang,saleItemId:_saleItemId,saleHeadId:_saleHeadId
											}).show();

								}else if("OR4"==_ortype){
									Ext.create('SMSWeb.view.mm.sale.MaterialBJWindow', 
											{loadStatus:'3',matnr : _matnr,title:'免费订单',type:_ortype,formId:_bujianId
										,shouDaFang:_shouDaFang,saleItemId:_saleItemId,saleHeadId:_saleHeadId
											}).show();
								}
							
							}
						}
					}
				}
			},
			
			//查询我的物品
			"MyGoodsWindow button[itemId=query]":{
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsWindow();
					
					var maingrid = mainView.queryById('MyGoodsMainGridView_itemId');
					var store = maingrid.getStore();
					
					store.loadPage(1);
				}
			},
			//新增我的物品
			"MyGoodsWindow button[itemId=addMMSJ]":{
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsWindow();
					var _kunnr = mainView.kunnr;
					var _matnr = mainView.matnr;
					var _bgOrderType = mainView.bgOrderType;
					var _title="";
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
							{loadStatus:'2',matnr : _matnr,title:_title,sourceShow:"newSaleContentWindow"
								,kunnr:_kunnr,bgOrderType:_bgOrderType}).show();
				}
			},//删除我的物品散件
			"MyGoodsWindow button[itemId=deleteMMSJ]":{
				click : function() {
					var me = this;
					var ids = [];
					var mainView = me.getMyGoodsWindow();
					var grid = mainView.queryById('MyGoodsMainGridView_itemId');
		            var records = grid.getSelectionModel().getSelection();
		            //不允许删除行号
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
			//确定选择我的物品
			"MyGoodsWindow button[itemId=comfirm]":{
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsWindow();
					var isBg = mainView.isBg;
					var grid = mainView.queryById('MyGoodsMainGridView_itemId');
					var store = grid.getStore();
					var records = grid.getSelectionModel().getSelection();
					//打包IMOS数据
					if(records.length>0){
						if("newSaleContentWindow"==mainView.sourceShow){
//							添加到订单行项目   处理代码块
							var itemsGrid =Ext.getCmp("addSaleItemsGrid");
							Ext.Array.each(records, function(r) {
								var saleStore =itemsGrid.getStore();
								var storeCount = saleStore.getCount();
								var existFlag = false;
								for (var i = 0; i <storeCount; i++) {
									var record = saleStore.getAt(i);
									if(record.data.materialHeadId==r.get('id') ||record.data.matnr==r.get('matnr')){
										existFlag = true;
									}
								}
								if(!existFlag){
									var model = Ext.create("SMSWeb.model.sale.SaleModel");
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
									model.set('posex', (storeCount+1)*10);
									model.set('matnr', r.get('matnr'));
									model.set('unit', unit);//设置单位
									model.set('mtart', r.get('mtart'));
//									model.set('touYingArea', r.get('touYingArea'));
									model.set('maktx', r.get('maktx'));//产品描述
									model.set('materialHeadId', r.get('materialHeadId'));//物料id
									var isStandard = r.get('isStandard');
									if("1"==isStandard){
								        model.set('materialPropertyItemInfo', r.get('materialPropertyItemInfo'));
								        model.set('materialPrice', r.get('materialPrice'));
									}
									model.set('isStandard', isStandard);//是否标准产品
									model.set('myGoodsId', r.get('id'));//我的商品id --流程激活后删除该表
									model.set('sanjianHeadId', r.get('sanjianHeadId'));//散件headID
									model.set('remark',r.get('zzazdr'));//加入订单行项目时的订单类型
									model.set('ortype', r.get('ortype'));//加入订单行项目时的订单类型
									model.set('amount', 1);
									saleStore.add(model);
								}
							});
						}else{
							var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
	                    	var	saleGrid = NewSaleWindowInnerContent.queryById("saleGrid");
	                    	
							Ext.Array.each(records, function(r) {
								
								var saleStore = saleGrid.getStore();
								var storeCount = saleStore.getCount();
								var existFlag = false;
								for (var i = 0; i <storeCount; i++) {
									var record = saleStore.getAt(i);
									if(record.data.myGoodsId==r.get('id')){
										existFlag = true;
									}
								}
								if(!existFlag){
									var model = Ext.create("SMSWeb.model.sale.SaleModel");
									model.set('matnr', r.get('matnr'));
									model.set('mtart', r.get('mtart'));
	//								model.set('touYingArea', r.get('touYingArea'));
									model.set('maktx', r.get('maktx'));//产品描述
									model.set('materialHeadId', r.get('materialHeadId'));//物料id
									var isStandard = r.get('isStandard');
									if("1"==isStandard){
								        model.set('materialPropertyItemInfo', r.get('materialPropertyItemInfo'));
								        model.set('materialPrice', r.get('materialPrice'));
									}
									model.set('isStandard', isStandard);//是否标准产品
									model.set('myGoodsId', r.get('id'));//我的商品id --流程激活后删除该表
									model.set('sanjianHeadId', r.get('sanjianHeadId'));//散件headID
									
									model.set('ortype', r.get('ortype'));//加入订单行项目时的订单类型
									model.set('amount', 1);
		//							model.set('status', "未审批");
									//console.info(model);
									saleStore.insert(storeCount, model);
								}
							});
							var	saleForm = NewSaleWindowInnerContent.queryById("saleForm");
							calculationTotalPrice(saleForm,saleGrid,'add');
						}
						mainView.close();
					}else{
						
					}
					
				}
			},
			//查询我的物品--补件
			"MyGoodsBujianWindow button[itemId=query]":{
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsBujianWindow();
					
					var maingrid = mainView.queryById('MyGoodsBujianMainGridView_itemId');
					var store = maingrid.getStore();
					
					store.loadPage(1);
				}
			},
			//确定选择我的物品--补件
			"MyGoodsBujianWindow button[itemId=comfirm]":{
				click : function() {
					var me = this;
					var mainView = me.getMyGoodsBujianWindow();
					var grid = mainView.queryById('MyGoodsBujianMainGridView_itemId');
					var store = grid.getStore();
					
					var records = grid.getSelectionModel().getSelection();
					
					if(records.length>0){
						var NewSaleWindowInnerContent = me.getNewSaleWindowInnerContent();
                    	var	saleGrid = NewSaleWindowInnerContent.queryById("saleGrid");
                    	
						Ext.Array.each(records, function(r) {
							
							var saleStore = saleGrid.getStore();
							var storeCount = saleStore.getCount();
							var existFlag = false;
							for (var i = 0; i <storeCount; i++) {
								var record = saleStore.getAt(i);
								if(record.data.myGoodsId==r.get('id')){
									existFlag = true;
								}
							}
							if(!existFlag){
								var model = Ext.create("SMSWeb.model.sale.SaleModel");
								model.set('matnr', r.get('matnr'));
								model.set('mtart', r.get('mtart'));
								model.set('maktx', r.get('maktx'));//产品描述
								model.set('myGoodsId', r.get('id'));//我的商品id --流程激活后删除该表
								model.set('bujianId', r.get('bujianId'));//补件表ID
								model.set('materialHeadId', r.get('materialHeadId'));//费用化ID
								model.set('ortype', r.get('ortype'));//加入订单行项目时的订单类型
								model.set('amount', 1);
								saleStore.insert(storeCount, model);
							}
						});
						var	saleForm = NewSaleWindowInnerContent.queryById("saleForm");
						calculationTotalPrice(saleForm,saleGrid,'add');
						mainView.close();
					}else{
						
					}
					
				}
			},
			"NewSaleWindowForHoleInfo button[id=querySaleForHoleInfo]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleWindowForHoleInfo = me.getNewSaleWindowForHoleInfo();
					var itemGrid = NewSaleWindowForHoleInfo.queryById("saleGridForBg");
				    var store = itemGrid.getStore();
				    store.loadPage(1);
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
			
			"NewSaleWindowForHoleInfo button[id=confirmSaleForHoleInfo]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var cncbarcode=[];
					var NewSaleWindowForBg = me.getNewSaleWindowForHoleInfo();
					var itemGrid = NewSaleWindowForBg.queryById("saleGridForBg");
					var records = itemGrid.getSelectionModel().getSelection();
					if(records.length<=0){
						Ext.MessageBox.alert("tips","请选择其中一行含有正(反)面条码");
						return;
					}
					var cncbarcode1 = records[0].data.cncBarcode1;
					var cncbarcode2 = records[0].data.cncBarcode2;
					if(cncbarcode1!=""&&cncbarcode1!=null){
						cncbarcode.push(cncbarcode1);
					}
					if(cncbarcode2!=""&&cncbarcode2!=null){
						cncbarcode.push(cncbarcode2);
					}
					var id = NewSaleWindowForBg.sid;
					var orderid = NewSaleWindowForBg.orderid;
					if(cncbarcode.length>0){
	                	Ext.Ajax.request({
							url : 'main/sale/updateHoleInfo',
							method : 'GET',
							params : {
								'id':id,
								 'orderid':orderid,
								 'cncbarcode1':cncbarcode1,
								 'cncbarcode2':cncbarcode2
							},
							async:false,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var responseData = Ext.decode(response.responseText);
								if(responseData.data){
									Ext.MessageBox.alert("tips","修改成功!请重新打开窗口");
								}else{
									Ext.MessageBox.alert("tips","修改失败！");

								}
								NewSaleWindowForBg.close();
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("tips","加载数据失败！");
							}
						});
					}else{
						Ext.MessageBox.alert("提示","请选择有正(反)面条码的板！");
					}
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
                    	var id = record.get('id');
                    	var orderCode = record.get('orderCode');
//                    	alert(orderCode);alert(id);
                    	var NewSaleContentWindow = me.getNewSaleContentWindow();
                    	var	saleForm = NewSaleContentWindow.queryById("addSaleForm");
                    	Ext.Ajax.request({
							url : 'main/sale/findById',
							method : 'GET',
							params : {
								'id':id
							},
							async:false,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var responseData = Ext.decode(response.responseText);
//								alert(responseData.success);alert(responseData.data.shouDaFang);
								//重新设置值的字段
								saleForm.getForm().findField('name1').setValue(responseData.data.name1);
								saleForm.getForm().findField('tel').setValue(responseData.data.tel);
								saleForm.getForm().findField('sex').setValue(responseData.data.sex);
								saleForm.getForm().findField('azAddress').setValue(responseData.data.address);
								//补单关联订单的时候，送达方取客户主数据的送达方 --add by Mark Wong 20171205
								saleForm.getForm().findField("songDaFang").setValue(responseData.data["kunnrS"]);
								saleForm.getForm().findField("shouDaFang").setValue(responseData.data["shouDaFang"]);
								
								saleForm.getForm().findField("pOrderCode").setValue(responseData.data["orderCode"]);
								saleForm.getForm().findField("pOrderCode").initValue();
								
								//获取tabs

								
								
								NewSaleWindowForBg.close();
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("tips","加载数据失败！");
							}
						});
                    	
                    	
                    	
//                    	var NewBgWindow = me.getNewBgWindow();
//                    	if( typeof(NewBgWindow) != "undefined" ){
//                    		var saleForm = NewSaleWindow.queryById("saleForm");
//                    	} else {
//                    		var saleForm = Ext.getCmp("saleForm");
//                    	}
//                    	var form = NewBgWindow.queryById("bgForm");
//                    	var orderCode_old = form.getForm().findField("orderCode").getValue();
//                    	if(orderCode_old!=orderCode){
//                    		form.getForm().findField("orderCode").setValue(orderCode);
//							form.getForm().findField("orderCode").initValue();
//							form.getForm().findField("clients").setValue(shouDaFang);
//							form.getForm().findField("clients").initValue();
//							var grid = NewBgWindow.queryById("bgGrid");
//							var store = grid.getStore();
//							store.removeAll();
//                    	}
//						NewSaleWindowForBg.close();
						
                    }else{
                    	Ext.MessageBox.alert('提示信息','只能选择一条明细');
                    }
				}
			},
			
			
			"NewTcPanel button[itemId=query]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewTcPanel = me.getNewTcPanel();
					//head信息
//					var tcForm = NewTcPanel.queryById("tcForm");
//					var formValues = tcForm.getValues();
					//物料清单信息
					var itemGrid = NewTcPanel.queryById("tcGrid");
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
			
			
			"NewSaleReportPanel button[itemId=query]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleReportPanel = me.getNewSaleReportPanel();
					var saleReportForm = NewSaleReportPanel.queryById("saleReportForm");
					if(saleReportForm.isValid()){
						var itemGrid = NewSaleReportPanel.queryById("saleReportGrid");
					    var store = itemGrid.getStore();
					    store.loadPage(1/*,{
					    	params:formValues,
					    	callback:function(r,options,success){
					            if(success){
					           }
					        }
					    }*/);
					}else{
						Ext.MessageBox.alert('提示信息','日期格式出错！');
					}
				}
			},
			
			"NewSaleItemReportPanel button[itemId=query]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewSaleItemReportPanel = me.getNewSaleItemReportPanel();
					var saleItemReportForm = NewSaleItemReportPanel.queryById("saleItemReportForm");
					if(saleItemReportForm.isValid()){
						var itemGrid = NewSaleItemReportPanel.queryById("saleItemReportGrid");
					    var store = itemGrid.getStore();
					    store.loadPage(1/*,{
					    	params:formValues,
					    	callback:function(r,options,success){
					            if(success){
					           }
					        }
					    }*/);
					}else{
						Ext.MessageBox.alert('提示信息','日期格式出错！');
					}
				}
			},
			"NewMaterialBaseFBWindow  button[itemId=saveFBMaterial]":{
				click :function( bt, e, eOpts ) {
					meC.saveFB(bt);
					var me=this;
					var NewMaterialBaseFBWindow=me.getNewMaterialBaseFBWindow();
					var ShoppingCartWindow=me.getShoppingCartWindow();
					var slaeItem=Ext.getCmp("addSaleItemsGrid");
						if(slaeItem){
							slaeItem.getStore().reload();
						}
						var _grid=NewMaterialBaseFBWindow.queryById("FB_FileGridItem");
						if(_grid.getStore().count()==0){
							Ext.MessageBox.alert("提示信息","保存成功，请上传图纸文件^_^");
						}else{
							Ext.MessageBox.alert("提示","保存成功！");
							NewMaterialBaseFBWindow.close();
						}
						if(ShoppingCartWindow!=undefined&&ShoppingCartWindow!=null){
							var shoppingCart2AddSaleView=ShoppingCartWindow.queryById("ShoppingCart2AddSaleView_itemId");
							if(shoppingCart2AddSaleView!=undefined){
								shoppingCart2AddSaleView.getStore().reload();
							}
						}
						/*else{
							var cou=0;
							Ext.Object.each(_grid.getStore().data.items,function(key,value,mys){
								if(value.data.statusdesc=="有效"){
									cou++;
								}
							});
							if(cou>0){
								meC.saveFB(bt);
								Ext.MessageBox.alert("提示","保存成功！");
								NewMaterialBaseFBWindow.close();
							}else{
								Ext.Msg.alert('','必须要有一个有效的图纸才能保存');
								return;
							}
						}*/
					/*if("Y"==Ext.getCmp("saveFB-flag").getValue()){
						var slaeItem=Ext.getCmp("addSaleItemsGrid");
						if(slaeItem){
							slaeItem.getStore().reload();
						}
						Ext.MessageBox.alert("提示","保存成功！");
						NewMaterialBaseFBWindow.close();
					}*/
				}
			},
			//add by mark 
			'SaleOutputDataPanel button[itemId=query]' :{
				click : function(bt, e, eOpts) {
					var me = this;
					var SaleOutputDataPanel = me.getSaleOutputDataPanel();
					var saleItemReportForm = SaleOutputDataPanel
							.queryById("queryForm");
					if (saleItemReportForm.isValid()) {
						var itemGrid = SaleOutputDataPanel
								.queryById("SaleOutputDataGrid");
						var store = itemGrid.getStore();
						store.loadPage(1/*
										 * ,{ params:formValues,
										 * callback:function(r,options,success){
										 * if(success){ } } }
										 */);
					} else {
						Ext.MessageBox.alert('提示信息', '请填写好数据再查询');
					}
				}
			},
			'SaleOutputDataPanel button[itemId=syncShim]' :{
				click : function(bt, e, eOpts) {
					var me = this;
					var SaleOutputDataPanel = me.getSaleOutputDataPanel();
					var saleItemDays = SaleOutputDataPanel
							.queryById("days").getValue();
					Ext.Ajax.request({
						url:"core/report/syncShim",
						method:'POST',
						params:{days:saleItemDays},
						async:false,
						success:function(data,opts){
							console.log(opts);
						}
					});
					
				}
			},
			"BarCodePrint button[itemId=query]":{
				click: function(bt,e,eOpts){
					var me=this;
					var _barCodePrint=me.getBarCodePrint();
					var _store=_barCodePrint.queryById("BarCodeDataGrid").getStore();
					var _form=_barCodePrint.queryById("queryForm");
					_store.loadPage(1,{params:_form.getValues()});
				}
			},
			"BarCodePrint button[itemId=print]":{
				click: function(bt,e,eOpts){
					var me=this;
					var _barCodePrint=me.getBarCodePrint();
					var _barCodeGrid=_barCodePrint.queryById("BarCodeDataGrid");
					Ext.MessageBox.confirm("温馨提示", "打印标签", function (btn) {
						if(btn=="yes"){
							var _selecteds=_barCodeGrid.getSelectionModel().getSelection();
							var _ids=[];
							for(var _index=0;_index<_selecteds.length;_index++){
								_ids[_index]=_selecteds[_index].data.ID;
							}
							if(_ids.length==0){
								Ext.Msg.alert("提示","请选中需要打印的数据!");
								return;
							}
							Ext.Ajax.request({
								url:'main/mm/getBarCodeMsg',
								method:'POST',
								params:{ids:_ids},
								async:false,
								success:function(data,opts){
									var _responseText=Ext.decode(data.responseText);
									var _dataArray=_responseText.data;
									LODOP.PRINT_INIT("");
									LODOP.SET_PRINT_PAGESIZE (1,"8cm","5cm","");
									LODOP.SET_PRINT_STYLE("FontSize",6);
									for(var _index=0;_index<_dataArray.length;_index++){
										LODOP.NewPageA();
										LODOP.ADD_PRINT_TEXT(0,"3.8cm","1.3cm","0.2cm","补打标签");
										LODOP.ADD_PRINT_TEXT("0.5cm","4mm","2.6cm","0.2cm",_dataArray[_index].BARCODE);
										LODOP.ADD_PRINT_TEXT("0.5cm","5.7cm","2.0cm","0.0cm",_dataArray[_index].MATNAME);
										LODOP.ADD_PRINT_TEXT("1cm","4mm","3cm","0.2cm",_dataArray[_index].SIZZ);
										LODOP.ADD_PRINT_TEXT("1cm","5.7cm","1.4cm","0.2cm",_dataArray[_index].NAME);
										if(_dataArray[_index].CNC_BARCODE1)
											LODOP.ADD_PRINT_BARCODE("1.5cm","0.5cm","2cm","2cm","QRCode",_dataArray[_index].CNC_BARCODE1);
										if(_dataArray[_index].CNC_BARCODE2)
											LODOP.ADD_PRINT_BARCODE("1.5cm","3.4cm","2cm","2cm","QRCode",_dataArray[_index].CNC_BARCODE2);
										LODOP.ADD_PRINT_TEXT("3.8cm","4mm","6cm","0.2cm","订单:"+_dataArray[_index].ORDER_CODE);
										LODOP.ADD_PRINT_TEXT("4.2cm","4mm","6cm","0.2cm",_dataArray[_index].MAKTX);
									}
									LODOP.PREVIEW();
								}
							});
						}
					});
				}
			},
			"newSaleContentWindow button[itemId=gainCust]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.sale.NewCustWindow',
				     {title:'客户信息查询'}).show(/*this,function(){}*/);
				}
			},
			"NewCustWindow button[id=queryCustInfo]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindow = me.getNewCustWindow();
					var form = NewCustWindow.queryById("newCustFromWindow");
					var formValues = form.getValues();
					var itemGrid = NewCustWindow.queryById("newCustGridWindow");
				    var store = itemGrid.getStore();
				    store.load({
				    	params:formValues
				    });
				}
			},
			"NewCustWindow button[id=confirmCustInfo]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindow = me.getNewCustWindow();
					var itemGrid = NewCustWindow.queryById("newCustGridWindow");
				    var store = itemGrid.getStore();
					var records = itemGrid.getSelectionModel().getSelection();
					if(records.length>0){
						var saleContentWindow=me.getNewSaleContentWindow();
						var saleForm=saleContentWindow.queryById("addSaleForm");
						var form=saleForm.getForm();
						form.findField("name1").setValue(records[0].data.name);
						form.findField("custId").setValue(records[0].data.cust_id);
						form.findField("tel").setValue(records[0].data.tel);
						var sex=records[0].data.sex=="男"?"1":"2";
						form.findField("sex").setValue(sex);//azAddress
						form.findField("birthday").setValue(records[0].data.birthday);
						form.findField("huXing").setValue(records[0].data.huXing);
						form.findField("azAddress").setValue(records[0].data.address);
						NewCustWindow.close();
					}else{
						Ext.MessageBox.alert("提示信息","至少选中一条客户信息");
						return;
					}
				}
			},
			//add by mark
			"NewMaterialBaseFBWindow":{
				//文件上传
				fileUploadButtonClick:function(info,headId){
					var me = this;
					//var ss=meC.saveFBFlag; 
//					if(headId!=null && headId.length>0){
						 //meC.saveFB(me);
						//if("Y"==Ext.getCmp("saveFB-flag").getValue()){
							var form=Ext.getCmp("FBMaterialForm"); 
	    					id=form.getValues().id;
	    					
							Ext.create('SMSWeb.view.mm.base.MaterialFileUploadBaseWindow',{formId : id,title:"文件上传",sourceShow:"newSaleContentWindow"}).show();						
						//}
//					}
				},
				//文件下载
				fileDownloadButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var id = record.data.id;
					window.location.href = basePath+'main/mm/fileDownload'+"?id="+id;  
				},
				//文件删除
				fileDeleteButtonClick:function(info,grid,id){
					var me = this;
					var ids = [];
					var records = grid.getSelectionModel().getSelection();
					if(records.length<=0){
						Ext.MessageBox.alert("温馨提示","请选择要失效的文件！");
						return;
					}
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
												var grid = Ext.getCmp("FB_FileGridItem");
					                            grid.getStore().load({
					                            	params:{'pid':id},
													callback :function(r,options,success){
														if(success){
															var fileGrid = Ext.getCmp("FB_FileGridItem");
															fileGrid.getStore().filter("statusdesc","有效");
															if(fileGrid.getStore().getCount()<=0){
																Ext.getCmp("fileDestroy").setDisabled(true);
															}
															fileGrid.getStore().clearFilter();
														}
												}});
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
			}
			
			
		});
	},
	saveFB:function(btn){
		var me = this;
		var NewMaterialBaseFBWindow=me.getNewMaterialBaseFBWindow();
		var _kunnr = NewMaterialBaseFBWindow.kunnr;
		var _orderType = NewMaterialBaseFBWindow.orderType;
		var form=Ext.getCmp("FBMaterialForm");
		var fbWindow = Ext.getCmp("uploadFileCon");
		if(!form.isValid()){
			Ext.MessageBox.alert('提示信息','填写信息有误！');
			return;
		}
		var formValues=form.getValues();
		
		var _longDesc =formValues.longDesc
		var _widthDesc = formValues.widthDesc;
		var _heightDesc = formValues.heightDesc;
		var _groesDesc = "W"+ _widthDesc + "XH" + _heightDesc + "XD"+ _longDesc;
		//规格 
		formValues.groes=_groesDesc;
		
		//产品分类
		var _matkl =form.getForm().findField("matkl");
		var _matklFind = _matkl.getStore().findRecord("id",_matkl.getValue());
		var _matklDesc = _matklFind.get('text');

		var _color = form.getForm().findField("color");
		var _colorDesc = "";
		if(_color.getValue()){
			var _colorFind = _color.getStore().findRecord("id",_color.getValue());
			_colorDesc = _colorFind.get('text');
		}
		
		var _textureOfMaterial = form.getForm().findField("textureOfMaterial");
		var _textureOfMaterialDesc = "";
		if(_textureOfMaterial.getValue()){
			var _textureOfMaterialFind = _textureOfMaterial.getStore().findRecord("id",_textureOfMaterial.getValue());
			_textureOfMaterialDesc = _textureOfMaterialFind.get('text');
		}
		
		//描述
		var _maktx="";
		//此判断是用于判断当前非标产品销售分类是否为木门，木门与其他对应的描述有所不同，所以在此需要判断，判断条件为saleFor
		if(formValues.saleFor!=null&&formValues.saleFor=="3"){//&&formValues.saleFor=="3"
			_maktx =   _textureOfMaterialDesc +_matklDesc  + _colorDesc + _groesDesc;
		}else{
			_maktx = _matklDesc +_textureOfMaterialDesc + _colorDesc + _groesDesc;
		}
		formValues.maktx=_maktx;
		
		var materialHead={
				id:formValues.id,
				color: formValues.color,
				groes: formValues.groes,
				heightDesc: formValues.heightDesc,
				longDesc: formValues.longDesc,
				maktx: formValues.maktx,
				matkl: formValues.matkl,
				serialNumber: formValues.serialNumber,
				textureOfMaterial: formValues.textureOfMaterial,
				widthDesc: formValues.widthDesc,
				createTime:formValues.createTime,
				createUser:formValues.createUser,
				fileType:formValues.fileType,
				saleFor:formValues.saleFor
				};
		var saleItemFj={
						id: formValues.FBid,
						myGoodsId:formValues.id,
						zzazdr: formValues.zzazdr,
						materialHeadId:formValues.id,
						createTime:formValues.createTime,
						createUser:formValues.createUser
					};
		 
		//组合json数据
		var gridData = Ext.encode({
			 saleItemFj:saleItemFj, 
        	 materialHead : materialHead,
        	 kunnr:_kunnr,
        	 bgOrderType:_orderType
		}); 
		var myMask = new Ext.LoadMask(Ext.getBody(),{msg:"请稍等..."}); 
		myMask.show();
		Ext.Ajax.request({
			url : 'main/mm/saveBaseFB',
			jsonData : gridData,
			method : 'POST',
			frame:true,
			async:false,
			dataType : "json",
			contentType : 'application/json',
			success : function(response, opts) {
				var values = Ext.decode(response.responseText);
				if(values.success){ 
					Ext.Ajax.request({
						url : 'main/mm/queryMHAndFJById?id='+values.data.id,
						async:false,
						dataType: "json",
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){ 
								var formData=values.data;
								form.getForm().setValues(formData);
								Ext.getCmp("saveFB-flag").setValue("Y");
	//							Ext.getCmp("fbcolor").setValue(formData.color);
	//							Ext.getCmp("zzazdr").setValue(formData.zzazdr);
								var grid = Ext.getCmp("MaterialMainGridBase2SaleView");
								fbWindow.setDisabled(false);
								if(grid){
									grid.getStore().load();
								}
								
							} else {
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
							 
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","加载失败"+response.responseText);
							 
						}
					});
//					Ext.MessageBox.alert("提示","保存成功！");
				}else{
					Ext.MessageBox.alert("提示",values.errorMsg);
				}
				 
				if (myMask != undefined){
					myMask.hide();
				} 
			},
			failure : function(response, opts) { 
				Ext.MessageBox.alert("提示","保存失败！");
				if (myMask != undefined){ myMask.hide();} 
			}
		});
	
	},
	requires : ["Ext.ux.form.TableComboBox","Ext.ux.grid.UXGrid"],
	views : ['sale.Sale2MainView','sale.SaleCheckSheetMainView','sale.SaleCheckShFormView','sale.SaleCheckSheetGridView','sale.Sale2FormView','sale.Sale2GridView','sale.NewSaleWindowInnerContent',
	         'sale.NewTcPanel','cust.NewCustWindow','cust.NewCustWindowInnerContent','mm.myGoods.MyGoodsMainFormView',
	         'mm.myGoods.MyGoodsMainGridView','sale.NewSaleReportPanel','sale.NewSaleItemReportPanel',
	         'sale.NewSaleContentWindow','mm.base.NewMaterialBaseFBWindow','bpm.TaskHistoric','sale.SaleOutputDataPanel'],
	stores : ['sale.SaleStore','sale.SaleCheckSheetStore','sale.Sale2Store','mm.myGoods.Store4MyGoods','sale.SaleOutputStore'],
	models : ['sale.SaleModel','sale.SaleCheckSheetModel','sale.Sale2Model','sale.SaleOutputModel']
});

var queryCust;
function showCustWin(code){
	Ext.create('SMSWeb.view.cust.NewCustWindow',
	     {formId : code,title:'客户信息',editFlag:false}).show(/*this,function(){}*/);
}

function saveTabemetod(saleHeadForm,gridData,saleForm,itemStore,itemGrid,sale2GridView){
	Ext.Ajax.request({
						url : 'main/sale/save',
						params : saleHeadForm,
						method : 'POST',
						frame : true,
						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){
								if(("undefined"!=typeof(values.data.orderCode) && "" != values.data.orderCode)
										&& ("undefined"==typeof(values.data.pOrderCode) || null == values.data.pOrderCode || "" == values.data.pOrderCode)
										&& "OR4" == values.data.orderType){
									saleForm.queryById("orderCodeQueryButton").show();
								}
								console.log("orderCodeQueryButton2");
								saleForm.getForm().setValues(values.data);
								saleForm.getForm().findField("orderCode").setValue(values.data.orderCode);
								saleForm.getForm().findField("shouDaFang").setValue(values.data.shouDaFang);
					            saleForm.getForm().findField("songDaFang").setValue(values.data.songDaFang);
					            saleForm.getForm().findField("orderCode").initValue();
					            saleForm.getForm().findField("shouDaFang").initValue();
					            saleForm.getForm().findField("songDaFang").initValue();
					            saleForm.getForm().findField("kunnrName1").setValue(values.data.kunnrName1);
								saleForm.getForm().findField("kunnrName1").initValue();
								
//								var orderCode = saleForm.getForm().findField("orderCode").getValue();
//								var pOrderCode = saleForm.getForm().findField("pOrderCode").getValue();
//								var orderType = saleForm.getForm().findField("orderType").getValue();
//								if(orderCode != "" && pOrderCode == "" && "OR4" == orderType){
//									saleForm.queryById("orderCodeQueryButton").show();
//								}
								
								itemStore.load({params:{'pid':values.data.id},callback: function(records, operation, success) {
									reCalculate(saleForm,itemGrid);
								}});
								
								//获取tabs
								var tabs = saleForm.queryById("centerTabpanel");
								var saleOneCustGrid = saleForm.queryById("saleOneCustGrid");
								saleOneCustGrid.getStore().load({params:{'pid':values.data.id},callback: function(records, operation, success) {
									var saleOneCustCount = saleOneCustGrid.getStore().getCount();
			                        if(saleOneCustCount > 0){
			                        	//临时送/售达方显示
										tabs.items.getAt(4).tab.show();
			                        }else{
			                        	//临时送/售达方隐藏
										tabs.items.getAt(4).tab.hide();
			                        }
								}});
								
								
								Ext.MessageBox.alert("提示信息","保存成功");
								
								var sysFileGrid = saleForm.queryById("sysFileGrid");
								var saleLogisticsGrid=saleForm.queryById("saleLogisticsGridItemId");
		                		saleLogisticsGrid.getStore().load({params:{pid:values.data.id}});
								sysFileGrid.getStore().load({params:{'foreignId':values.data.id,'fileType':''}});
								if( typeof(Sale2GridView) != "undefined" ){
		                    		Sale2GridView.getStore().loadPage(1);
		                    	}
								
								Ext.getCmp("checkSale").show();
							} else {
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
							Ext.getCmp("saveOrder").setDisabled(false);
							
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","保存失败");
							Ext.getCmp("saveOrder").setDisabled(false);
						}
					});
}

/**
 * 保存之前校验
 */
function beforeSaveValidate(saleForm,itemGrid){
	saleForm.isValid();
	var tabs = saleForm.queryById("centerTabpanel");
	var errMsg = "";
	var orderType = saleForm.getForm().findField("orderType").getValue();
	var shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
	var songDaFang = saleForm.getForm().findField("songDaFang").getValue();
	var dianMianTel = saleForm.getForm().findField("dianMianTel").getValue();
	var designerTel = saleForm.getForm().findField("designerTel").getValue();
	var pOrderCode = saleForm.getForm().findField("pOrderCode").getValue();
	if(orderType==null || Ext.String.trim(orderType)==""){
		errMsg += "请选择订单类型<br/>";
	}else{
		if(orderType=="OR3" || orderType=="OR4"){
			if(pOrderCode==null || pOrderCode==""){
//				errMsg += "请选择参考订单<br/>";
			}
		}
	}
	if(Ext.String.trim(shouDaFang)==""){
		errMsg += "请选择售达方<br/>";
	}
	/*if(Ext.String.trim(songDaFang)==""){
		errMsg += "请选择送达方<br/>";
	}*/
	/*if("undefined"!=typeof(orderType) && orderType!="OR3" && orderType!="OR4"){
		if(Ext.String.trim(dianMianTel)==""){
			errMsg += "店面联系电话必填<br/>";
		}
	}*/
	//	var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$|^(\(\d{3,4}-)|\d{3.4}-\)?\d{7,8}$/;
	var reg2 = /^\+?[1-9][0-9]*$/;
	//	var reg3 = /^(\d{4})\-(\d{2})\-(\d{2})$/;
	var reg4 =  /^[0-9]\d{5}$/;//邮编
	var reg5=/^[\u4e00-\u9fa5]{2,6}$/;//经手人中文检测
	
	if(dianMianTel!=null && Ext.String.trim(dianMianTel)!="" && !reg.test(dianMianTel)){
		errMsg += "店面联系电话不匹配<br/>";
	}
	if(designerTel!=null && Ext.String.trim(designerTel)!="" && !reg.test(designerTel)){
		errMsg += "设计师联系电话不匹配<br/>";
	}
	if(errMsg!=""){
		tabs.setActiveTab(0); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	var name1 = saleForm.getForm().findField("name1").getValue();
	var tel = saleForm.getForm().findField("tel").getValue();
	var age = saleForm.getForm().findField("age").getValue();
	var birthday = saleForm.getForm().findField("birthday").getValue();
	var sex = saleForm.getForm().findField("sex").getValue();
	var jingShouRen = saleForm.getForm().findField("jingShouRen").getValue();
	var huXing = saleForm.getForm().findField("huXing").getValue();
	var orderPayFw = saleForm.getForm().findField("orderPayFw").getValue();
	//var orderEvent = saleForm.getForm().findField("orderEvent").getValue();
	//var address = saleForm.getForm().findField("address").getValue();
	
	/*if(orderType="OR3"){
		if(address == ""){
			errMsg += "部件补购单，安装地址为必填<br/>";	
		}
	}*/
	
	if("undefined"!=typeof(orderType) && orderType!="OR3" && orderType!="OR4"){
		if(name1==""){
			errMsg += "姓名必填<br/>";
		}
		if(tel==""){
			errMsg += "联系电话必填<br/>";
		}
		if(age==null || age.length==0){
			errMsg += "年龄必填<br/>";
		}
		if(birthday==null || birthday.length==0){
			errMsg += "生日必填<br/>";
		}
		if(sex==null || Ext.String.trim(sex)==""){
			errMsg += "请选择性别<br/>";
		}
		if(jingShouRen==null || Ext.String.trim(jingShouRen)==""){
			errMsg += "专卖店经手人必填<br/>";
		}
		//else if(!reg5.test(jingShouRen)){
		//		errMsg+="专卖店经手人必须填写中文";
		//}
		if(huXing==null || Ext.String.trim(huXing)==""){
			errMsg += "请选择安装户型<br/>";
		}
		if(orderPayFw==null || Ext.String.trim(orderPayFw)==""){
			errMsg += "请选择订单金额范围<br/>";
		}
		/*if(orderEvent==null || Ext.String.trim(orderEvent)==""){
			errMsg += "请选择活动政策<br/>";
		}*/
	}
	
	if(tel!=null && Ext.String.trim(tel)!="" && !reg.test(tel)){
		errMsg += "联系电话不匹配<br/>";
	}
	
//	if(age!=null && !reg2.test(age)){
//		errMsg += "年龄不匹配<br/>";
//	}
//	if(birthday!=null && birthday!="" && !reg3.test(birthday)){
//		errMsg += "生日不匹配<br/>";
//	}
//	if(birthday!=null && birthday!="" && Ext.Date.format(birthday, 'Y-m-d')==""){
//		errMsg += "生日不匹配<br/>";
//	}
	if(errMsg!=""){
		tabs.setActiveTab(2); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	
	var saleOneCustGrid = saleForm.queryById("saleOneCustGrid");
	var saleOneCustStore = saleOneCustGrid.getStore();
	var columns = saleOneCustGrid.columns;
	
	for (var i = 0; i <saleOneCustStore.getCount(); i++) {
    	var record = saleOneCustStore.getAt(i);
    	//临时售达方
    	if(record.data.saleOneCustType=="临时售达方"){
    		Ext.Array.each(columns, function(column, index, myself) {
    			var dataIndex = column.dataIndex;
    			if(dataIndex=='id' || dataIndex=='createTime' || dataIndex=='createUser' ||dataIndex=='type' || dataIndex=='kunnr' || dataIndex=='ort02'){//运输区域不必填
    				
    			}else{
    				 if(record.data[dataIndex]==""){
    					 errMsg += "临时售达方的'" + column.text + "'必填<br/>";
    				 }else{
    					 if(dataIndex=='pstlz' && !reg4.test(record.data[dataIndex])){//邮编
    						 errMsg += "临时售达方的'" + column.text + "'的格式不正确（例必须6位数）<br/>";
    					 }
    				 }
    				 //console.log(column.text);
    			}
			});
    	}
    	//临时送达方
    	if(record.data.saleOneCustType=="临时送达方"){
    		Ext.Array.each(columns, function(column, index, myself) {
    			var dataIndex = column.dataIndex;
    			if(dataIndex=='id' || dataIndex=='createTime' || dataIndex=='createUser' || dataIndex=='type' || dataIndex=='kunnr'|| dataIndex=='pstlz'){
    				
    			}else{
    				 if(record.data[dataIndex]=="" && dataIndex != "socAddress"){
    					 errMsg += "临时送达方的'" + column.text + "'必填<br/>";
    				 }else{
    					 if(dataIndex=='pstlz' && !reg4.test(record.data[dataIndex])){//邮编
    						 errMsg += "临时送达方的'" + column.text + "'的格式不正确（例必须6位数）<br/>";
    					 }
    				 }
    			}
			});
    	}
    }
	if(errMsg!=""){
		tabs.setActiveTab(5); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	var itemStore = itemGrid.getStore();
	if(itemStore.getCount()==0){
		Ext.MessageBox.alert("提示信息","必须有一条产品明细");
		return false;
	}
	if(saleForm.isValid()==false){
		Ext.MessageBox.alert("提示信息","表单信息有误！");
		return false;
	}
	return true;
}

/**
 * 生成提交后台信息
 */
function generateSubmitInfo(saleForm,itemGrid){
	var submitInfo = {};
	var formValues = saleForm.getValues();
//	var test = Ext.Object.getValues(formValues);
//	Ext.MessageBox.alert('提示信息',test);
	var saleHeadForm = {};
	var terminalClientForm = {name1:'',sex:'',age:'',birthday:'',shenFenHao:'',tel:'',code:'',name:'',jingShouRen:'',
		huXing:'',isYangBan:'',isAnZhuang:'',isDianTi:'',floor:'',address:'',orderPayFw:'',custRemarks:''};
	Ext.Object.each(formValues, function(key, value, myself) {
//					    console.log(key + ":" + value);
		if("undefined"==typeof(terminalClientForm[key])){
			if(value!=""){
				saleHeadForm[key] = value;
			}
		}else{
			terminalClientForm[key] = value;
		}
	});
	var tcId = saleForm.getForm().findField("tcId").getValue();
	var tousucishu = saleForm.getForm().findField("tousucishu").getValue();
	var problem = saleForm.getForm().findField("problem").getValue();
	if(tcId!=''){
		terminalClientForm['id'] = tcId;
	}
	if(tousucishu!=''){
		terminalClientForm['tousucishu'] = tousucishu;
	}
	if(problem!=''){
		terminalClientForm['problem'] = problem;
	}
	var orderCode = saleForm.getForm().findField("orderCode").getValue();
	var shouDaFang = saleForm.getForm().findField("shouDaFang").getValue();
	var songDaFang = saleForm.getForm().findField("songDaFang").getValue();
	//var sapOrderCode = saleForm.getForm().findField("sapOrderCode").getValue();
	var pOrderCode = saleForm.getForm().findField("pOrderCode").getValue();
	saleHeadForm['orderCode'] = orderCode;
	saleHeadForm['shouDaFang'] = shouDaFang;
	saleHeadForm['songDaFang'] = songDaFang;
	/*if(sapOrderCode!=""){
		saleHeadForm['sapOrderCode'] = sapOrderCode;
	}*/
	if(pOrderCode!=""){
		saleHeadForm['pOrderCode'] = pOrderCode;
	}
	
	var itemStore = itemGrid.getStore();
    /*var json = [];
    var gridValues = itemStore.getRange(0,itemStore.getCount());
    
    for (var i = 0; i <itemStore.getCount(); i++) {
    	var arrJson = gridValues[i].getData();
    	json.push(arrJson);
    }*/
	
	var itemCount = itemStore.getCount();
	var allTotalPrice = 0;
	for (var i = 0; i <itemCount; i++) {
		var record = itemStore.getAt(i);
		if("QX" != record.get("stateAudit")){
			allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
		}
	}
	saleForm.getForm().findField("orderTotal").setValue(allTotalPrice);
	saleForm.getForm().findField("orderTotal").initValue();
	saleHeadForm['orderTotal'] = allTotalPrice;
				    
    //从表记录转换成json
  	var modifyRecords = itemStore.getUpdatedRecords();
    var newRecords = itemStore.getNewRecords();
    var recordsLen = modifyRecords.length;
    var dataArr = [];
	for(var i = 0; i < recordsLen ; i++){
		var record = modifyRecords[i].data;
		var json_x = {};
		Ext.Object.each(record, function(key, value, myself) {
			if(key!="pdfId"&&key!="kitId"&&key!="priceId"&&key!="zzwgfg"){
				if(key.indexOf(".")!=-1){
					var keys = key.split(".");
					json_x[keys[0]] = {id:value};
				}else{
					json_x[key] = value;
				}
			}
		});
		dataArr[i] = json_x;
	}
	for(var i = 0; i < newRecords.length ; i++){
		var record = newRecords[i].data;
		var json_x = {};
		Ext.Object.each(record, function(key, value, myself) {
			if(key!="pdfId"&&key!="kitId"&&key!="priceId"&&key!="zzwgfg"){
				if(key.indexOf(".")!=-1){
					var keys = key.split(".");
					json_x[keys[0]] = {id:value};
				}else{
					json_x[key] = value;
				}
			}
		});
		dataArr[recordsLen+i] = json_x;
	}
	
	var saleOneCustGrid = saleForm.queryById("saleOneCustGrid");
	var saleOneCustStore = saleOneCustGrid.getStore();
	var dataArr2 = [];
	for (var i = 0; i <saleOneCustStore.getCount(); i++) {
    	var record = saleOneCustStore.getAt(i);
    	var json_x = {};
		Ext.Object.each(record.data, function(key, value, myself) {
			if(key.indexOf(".")!=-1){
				var keys = key.split(".");
				json_x[keys[0]] = {id:value};
			}else{
				json_x[key] = value;
			}
			
		});
		dataArr2[i] = json_x;
    }
    
    submitInfo['saleHeadForm'] = saleHeadForm;
    submitInfo['terminalClientForm'] = terminalClientForm;
    submitInfo['dataArr'] = dataArr;
    submitInfo['dataArr2'] = dataArr2;
    return submitInfo;
}

/**
 * 计算金额（先计算行项目金额，再计算总金额）
 * @param {Object} saleForm
 * @param {Object} itemGrid
 * @param {Object} flag
 */
function calculationTotalPrice(saleForm,itemGrid,flag){
    var itemStore = itemGrid.getStore();
    var json = [];
    if(!flag || flag=="all"){
    	var gridValues = itemStore.getRange(0,itemStore.getCount());
	    for (var i = 0; i <itemStore.getCount(); i++) {
	    	var arrJson = gridValues[i].getData();
	    	var json_x = {};
			Ext.Object.each(arrJson, function(key, value, myself) {
				if(key!="pdfId"&&key!="kitId"&&key!="priceId"&&key!="zzwgfg"){
					if(key.indexOf(".")!=-1){
						var keys = key.split(".");
						json_x[keys[0]] = {id:value};
					}else{
						json_x[key] = value;
					}
				}
				
			});
			json[i] = json_x;
//	    	json.push(arrJson);
	    }
    }else if(flag=="add"){
    	var newRecords = itemStore.getNewRecords();
    	for(var i = 0; i < newRecords.length ; i++){
			var record = newRecords[i].data;
			var json_x = {};
			Ext.Object.each(record, function(key, value, myself) {
				if(key!="pdfId"&&key!="kitId"&&key!="priceId"&&key!="zzwgfg"){
					if(key.indexOf(".")!=-1){
						var keys = key.split(".");
						json_x[keys[0]] = {id:value};
					}else{
						json_x[key] = value;
					}
				}
				
			});
			json[i] = json_x;
		}
    }else if(flag=="delete"){
//    	var removedRecords = itemStore.getRemovedRecords();
//    	for(var i = 0; i < removedRecords.length ; i++){
//			var record = removedRecords[i].data;
//			var json_x = {};
//			Ext.Object.each(record, function(key, value, myself) {
//				if(key.indexOf(".")!=-1){
//					var keys = key.split(".");
//					json_x[keys[0]] = {id:value};
//				}else{
//					json_x[key] = value;
//				}
//				
//			});
//			json[i] = json_x;
//		}
    	var itemCount = itemStore.getCount();
		var allTotalPrice = 0;
		for (var i = 0; i <itemCount; i++) {
			var record = itemStore.getAt(i);
			if("QX" != record.get("stateAudit")){
				allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
			}
		}
		saleForm.getForm().findField("orderTotal").setValue(allTotalPrice);
		saleForm.getForm().findField("orderTotal").initValue();
		return;
    }
    var gridData = Ext.encode({
		saleItemList : json
	});
    
	var shouDaFang=saleForm.getForm().findField("shouDaFang").getValue();
	Ext.Ajax.request({
		url : 'main/sale/calculationTotalPrice',
		params : {
			'shouDaFang':shouDaFang
		},
		method : 'POST',
		frame : true,
		jsonData : gridData,
		dataType : "json",
		contentType : 'application/json',
//		async:false,
		success : function(response, opts) {
			values = Ext.decode(response.responseText);
//			alert(values.data.totalPrice);
			var itemCount = itemStore.getCount();
			var allTotalPrice = 0;
			for (var i = 0; i <itemCount; i++) {
				var record = itemStore.getAt(i);
				values.data.saleItemList.forEach(function(element, index, array) {
					if(record.data.id==element.id || record.data.materialHeadId==element.materialHeadId){
						record.set("totalPrice", element.totalPrice);
					}
				});
				if("QX" != record.get("stateAudit")){
					allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
				}
			}
			saleForm.getForm().findField("orderTotal").setValue(allTotalPrice);
			saleForm.getForm().findField("orderTotal").initValue();
		},
		failure : function(response, opts) {
			alert(response.responseText);
		}
	});
}

/**
 * 两个浮点数求和
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accAdd(num1,num2){
   var r1,r2,m;
   try{
       r1 = num1.toString().split('.')[1].length;
   }catch(e){
       r1 = 0;
   }
   try{
       r2=num2.toString().split(".")[1].length;
   }catch(e){
       r2=0;
   }
   m=Math.pow(10,Math.max(r1,r2));
   // return (num1*m+num2*m)/m;
   return Math.round(num1*m+num2*m)/m;
}
/**
 * 两个浮点数相减
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accSub(num1,num2){
   var r1,r2,m;
   try{
       r1 = num1.toString().split('.')[1].length;
   }catch(e){
       r1 = 0;
   }
   try{
       r2=num2.toString().split(".")[1].length;
   }catch(e){
       r2=0;
   }
   m=Math.pow(10,Math.max(r1,r2));
   n=(r1>=r2)?r1:r2;
   return (Math.round(num1*m-num2*m)/m).toFixed(n);
}
/**
 * 两数相除
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accDiv(num1,num2){
   var t1,t2,r1,r2;
   try{
       t1 = num1.toString().split('.')[1].length;
   }catch(e){
       t1 = 0;
   }
   try{
       t2=num2.toString().split(".")[1].length;
   }catch(e){
       t2=0;
   }
   r1=Number(num1.toString().replace(".",""));
   r2=Number(num2.toString().replace(".",""));
   return (r1/r2)*Math.pow(10,t2-t1);
}
/**
 * 两数相乘
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accMul(num1,num2){
    var m=0,s1=num1.toString(),s2=num2.toString(); 
    try{m+=s1.split(".")[1].length}catch(e){};
    try{m+=s2.split(".")[1].length}catch(e){};
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}
/**
 *验证非标产品，文件
 */
function fileExistValidate(records){
	var flag = false;
	var ids = [];

	Ext.Array.each(records, function(r) {
		var _isStandard = r.get('isStandard');
		if("0"==_isStandard){
			ids.push(r.get('materialHeadId'));
		}
	});
	if(ids.length>0){
		var values;
		Ext.Ajax.request({
			url : 'main/mm/fileExistValidate',
			method : 'GET',
			params : {
				'ids' : ids
			},
			async:false,
			dataType : "json",
			contentType : 'application/json',
			success : function(response, opts) {
				values = Ext.decode(response.responseText);
			},
			failure : function(response, opts) {
				Ext.MessageBox.alert("提示","获取数据失败!");
			}
		});
		
//		console.log(values);
		if(values.success){
			flag = true;
		}else{
			Ext.MessageBox.alert("提示",values.errorMsg);
		}
	}else{
		flag = true;
	}
	return flag;
}
//文件下载
function fileDownloadButtonClick(id){
	window.location.href = basePath+'main/mm/fileDownload'+"?id="+id;  
}

//文件下载
function bitfileDownloadButtonClick(id){
	window.location.href = basePath+'main/mm/bitfileDownload'+"?id="+id;  
}

