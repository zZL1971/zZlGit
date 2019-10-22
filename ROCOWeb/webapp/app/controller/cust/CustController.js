Ext.define("SMSWeb.controller.cust.CustController", {
	extend : 'Ext.app.Controller',
	refs : [{
				ref : 'CustFormView',
				selector : 'CustFormView' 
			},
			{
				ref : 'CustGridView',
				selector : 'CustGridView' 
			},
			{
				ref : 'NewCustWindowInnerContent',
				selector : 'NewCustWindowInnerContent' 
			},
			{
				ref : 'NewCustWindowForSale',
				selector : 'NewCustWindowForSale' 
			},
			{
				ref : 'CustMainView',
				selector : 'CustMainView' 
			},{
				ref : 'NewCustWindowForBank',
				selector : 'NewCustWindowForBank'
			},{
				ref : 'CustFileUploadBaseWindow',
				selector : 'CustFileUploadBaseWindow'
			},{
				ref : 'CustZhekouFileUploadBaseWindow',
				selector : 'CustZhekouFileUploadBaseWindow'
			}
	],
	init : function() {
		this.control({
			//查询客户主数据
			"CustMainView button[itemId=queryCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
//					var CustFormView = me.getCustFormView();
//					var formValues = CustFormView.getValues();
					var CustGridView = me.getCustGridView();
				    var store = CustGridView.getStore();
				    store.loadPage(1/*,{
				    	params:formValues,
				    	callback:function(r,options,success){
				            if(success){
				           }
				        }
				    }*/);
				}
			},
			//更新没有投入sap生产的售达方信息
			"CustMainView button[id=cusModBtn]":{
				click:function(bt,e,opts){
					var me=this;
					var kunnrF=Ext.ComponentQuery.query("tablecombobox[itemId=kunnrF]")[0];
					var kunnrT=Ext.ComponentQuery.query("tablecombobox[itemId=kunnrT]")[0];
					var kunnr=Ext.ComponentQuery.query("tablecombobox[itemId=kunnrR]")[0];
					if(!kunnr.value){
						Ext.MessageBox.alert("提示","请选择经销商编码");
						return false;
					}
					if(!kunnrF.value){
						Ext.MessageBox.alert("提示","请选择原送达方编码");
						return false;
					}
					if(!kunnrT.value){
						Ext.MessageBox.alert("提示","请选择目标送达方编码");
						return false;
					}
					Ext.Ajax.request({
						url:'main/cust/modifyBatch',
						params:{
							"kunnrF":kunnrF.value,
							"kunnrT":kunnrT.value,
							"kunnr":kunnr.value
						},
						method:'POST',
						success:function(res){
							if("OK"==res.statusText){
								Ext.MessageBox.alert("提示","修改成功 !");
							}else{
								Ext.MessageBox.alert("提示","修改失败 !");
							}
						}
					});
				}
			},
			//新增客户主数据
			"CustMainView button[id=newCust]" : {
				click : function() {
					Ext.create('SMSWeb.view.cust.NewCustWindow',
							     {title:'新增客户'}).show(this,function(){
							     });
				}
			},
//			"CustMainView":{
//				//文件上传
//				fileUploadButtonClick:function(info,headId){
//				}
//			},
			"CustZhekouFileUploadBaseWindow":{
				//上传Excel绑定客户级别折扣
				custZhekoufileUploadButtonClick:function(){
					var me = this;
					var fileUploadWindow = me.getCustZhekouFileUploadBaseWindow();
					var fileUploadForm = fileUploadWindow.queryById("fileUploadForm"); 
					var file = fileUploadForm.getForm().findField("file").getValue();
					var type=fileValidatePDForExecel(file);
					if(type){
						var fileType = fileUploadForm.getForm().findField("fileType").setValue(type);
						if(fileUploadForm.getForm().isValid()){  
							fileUploadForm.submit({  
								url: 'main/cust/custZhekouUpdateFile', 
								method : 'POST',
								waitMsg: '上传文件中...',
								dataType: 'json',
								success: function(form, action) {
									console.log(action);
									var values = Ext.decode(action.response.responseText);
									if(values.success){
										var pid = fileUploadWindow.formId;
//										Ext.MessageBox.alert("提示","上传成功！");
										
//									}else{
										Ext.MessageBox.alert("提示",values.msg);
										fileUploadWindow.close();
									}
									
								},
								failure : function(form, action) {
									var values = Ext.decode(action.response.responseText);
									if(values.msg){
										var pid = fileUploadWindow.formId;
										Ext.MessageBox.alert("提示",values.msg);
										fileUploadWindow.close();
									}else{
										Ext.MessageBox.alert("提示","上传失败！");
									}
								}
							});  
						}
					}
				}
			},
			"CustFileUploadBaseWindow":{
				//上传Excel绑定银行账号
				fileUploadButtonClick:function(){
					var me = this;
					var fileUploadWindow = me.getCustFileUploadBaseWindow();
					var fileUploadForm = fileUploadWindow.queryById("fileUploadForm"); 
					var file = fileUploadForm.getForm().findField("file").getValue();
					var type=fileValidatePDForExecel(file);
					if(type){
						var fileType = fileUploadForm.getForm().findField("fileType").setValue(type);
						if(fileUploadForm.getForm().isValid()){  
							fileUploadForm.submit({  
								url: 'main/cust/BankUpdateFile', 
								method : 'POST',
								waitMsg: '上传文件中...',
								dataType: 'json',
								success: function(form, action) {
									console.log(action);
									var values = Ext.decode(action.response.responseText);
									if(values.success){
										var pid = fileUploadWindow.formId;
//										Ext.MessageBox.alert("提示","上传成功！");
										
//									}else{
										Ext.MessageBox.alert("提示",values.msg);
										fileUploadWindow.close();
									}
									
								},
								failure : function(form, action) {
									var values = Ext.decode(action.response.responseText);
									if(values.msg){
										var pid = fileUploadWindow.formId;
										Ext.MessageBox.alert("提示",values.msg);
										fileUploadWindow.close();
									}else{
										Ext.MessageBox.alert("提示","上传失败！");
									}
								}
							});  
						}
					}
				}
			},
			//同步SAP客户信息
			"CustMainView button[id=syncCust]" : {
				click : function() {
					Ext.getCmp("syncCust").setDisabled(true);
					var me = this;
					Ext.Ajax.request({
						url : 'main/cust/syncCust',
						method : 'POST',
						timeout:101066,
						success : function(response, opts) {
							var CustGridView = me.getCustGridView();
							if( typeof(CustGridView) != "undefined" ){
	                    		CustGridView.getStore().loadPage(1);
	                    	}
							Ext.MessageBox.alert("提示信息","同步成功！");
							Ext.getCmp("syncCust").setDisabled(false);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","同步失败！");
							Ext.getCmp("syncCust").setDisabled(true);
						}
					});
				}
			},
			
			//同步农行客户信息
			"CustMainView button[id=syncBank]" : {
				click : function() {
					Ext.getCmp("syncBank").setDisabled(true);
					
					var me = this;
					Ext.Ajax.request({
						url : 'main/cust/syncBank',
						method : 'POST',
						success : function(response, opts) {
							console.log(response);
							var obj=$.parseJSON(response.responseText);
							console.log(obj);
							if(!obj.success){
								Ext.MessageBox.alert("提示信息",obj.errorMsg);
								Ext.getCmp("syncBank").setDisabled(false);
	                    	}else{
	                    		Ext.MessageBox.alert("提示信息",obj.errorMsg);
	                    		Ext.getCmp("syncBank").setDisabled(true);
	                    	}
						},
					});
				}
			},
			//同步建行客户信息
			"CustMainView button[id=syncjhBank]" : {
				click : function() {
					Ext.getCmp("syncjhBank").setDisabled(true);
					var me = this;
					Ext.Ajax.request({
						url : 'main/cust/syncJHBank',
						method : 'POST',
						success : function(response, opts) {
							console.log(response);
							var obj=$.parseJSON(response.responseText);
							console.log(obj);
							if(obj.success){
								Ext.MessageBox.alert("提示信息",obj.msg);
								Ext.getCmp("syncjhBank").setDisabled(false);
	                    	}else{
	                    		Ext.MessageBox.alert("提示信息",obj.msg);
	                    		Ext.getCmp("syncjhBankf").setDisabled(true);
	                    	}
						},
					});
				}
			},
			
			//同步sap获取会计凭证信息
			"CustMainView button[id=doco]" : {
				click : function() {
					Ext.getCmp("doco").setDisabled(true);
					var me = this;
					Ext.Ajax.request({
						url : 'main/cust/add_doco',
						method : 'POST',
						success : function(response, opts) {
							console.log(response);
							var obj=$.parseJSON(response.responseText);
							//console.log(obj);
							if(!obj.success){
								Ext.MessageBox.alert("提示信息",obj.errorMsg);
								Ext.getCmp("doco").setDisabled(false);
	                    	}else{
	                    		Ext.MessageBox.alert("提示信息",obj.errorMsg);
	                    		Ext.getCmp("doco").setDisabled(true);
	                    	}
						},
					});
				}
			},
			//查询农行数据
//			"NewCustWindowForBank button[id=findXinDai]":{
//				click : function( bt, e, eOpts ) {
//				 	var me =this;
//				 	var NewCustWindowForBank=me.getNewCustWindowForBank();
//				 	var itemGrid=NewCustWindowForBank.queryById("custXinDai");
//				 	var NewCustWindowInnerContent=me.getNewCustWindowInnerContent();
//				 	var custForm=NewCustWindowInnerContent.queryById("custForm");
//					var itemStore = itemGrid.getStore();
//					var kunnr=custForm.getForm().findField("kunnr").value;
//					itemStore.load({params:{
//						startDate:$("input[name='startDate']").val(),
//						endDate:$("input[name='endDate']").val(),
//						kunnr:kunnr
//						}
//					});
//				}
//			},
			//grid编辑事件
			'CustGridView':{
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var id = record.get('pid');
					//Ext.create('SRM.view.trie.DictForm',{dictId:record.getId()}).show(grid);
					Ext.create('SMSWeb.view.cust.NewCustWindow',
							     {formId : id,title:'修改客户'}).show(this,function(){
							    	 //oneCustAfterLoad(code);
					});
				}
			},
			
			//弹窗增加折扣明细事件
			"NewCustWindowInnerContent button[id=addItem]":{
				click : function( bt, e, eOpts ) {
				    var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					var itemGrid = NewCustWindowInnerContent.queryById("custGrid");
					var itemStore = itemGrid.getStore();
					var itemModel = itemStore.model;
					var storeCount = itemStore.getCount();
					var modelit = new itemModel();
					modelit.set('status',0);
					itemStore.insert(storeCount, modelit);
				}
			},
			//导出客户主数据
			"CustMainView button[id=export]":{
				click:function(){
					var tmpgrid = Ext.getCmp('custGrid');
					Ext.MessageBox.confirm("温馨提示", "导出到Excel", function (btn) {
						if(btn=="yes"){
							//用grid导出excel
							ExportExcelByGrid(tmpgrid);
						}
					});
				}
			},
			//弹窗删除折扣明细事件
			"NewCustWindowInnerContent button[id=removeItem]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					Ext.MessageBox.confirm('提示信息','确定要删除所选折扣明细？',
				    	function(btn){
					        if(btn=='yes'){
								var itemGrid = NewCustWindowInnerContent.queryById("custGrid");
							    var itemStore = itemGrid.getStore();
							    if(itemStore.getCount()==1){
									Ext.MessageBox.alert("提示信息","必须有一条折扣明细，删除失败");
									return false;
								}
							    var sm = itemGrid.getSelectionModel();
							    var records = sm.getSelection();
							    var ids = [];
								Ext.Array.each(records, function(r) {
									ids.push(r.get('id'))
								});
								Ext.Ajax.request({
									url : 'main/cust/deleteCustItemByIds',
									params : {
										ids : ids
									},
									method : 'POST',
									success : function(response, opts) {
										itemStore.remove(sm.getSelection());
										var CustGridView = me.getCustGridView();
										if( typeof(CustGridView) != "undefined" ){
				                    		CustGridView.getStore().loadPage(1);
				                    	}
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
			
			//弹窗激活折扣明细事件
			"NewCustWindowInnerContent button[id=activeItem]":{
				click : function( bt, e, eOpts ) {
				    var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					var itemGrid = NewCustWindowInnerContent.queryById("custGrid");
					var itemStore = itemGrid.getStore();
					var existOneFlag = false;//有一条激活的标志
					for (var i = 0; i <itemStore.getCount(); i++) {
				    	var record = itemStore.getAt(i);
				    	if(record.get("status")==1){
				    		existOneFlag = true;
				    		break;
				    	}
				    }
					if(existOneFlag){
						Ext.MessageBox.alert("提示信息","激活失败：<br/>只能有一条激活的折扣明细！");
						return;
					}
					var sm = itemGrid.getSelectionModel();
					var records = sm.getSelection();
					Ext.Array.each(records, function(r) {
						r.set("status",1);
					});
					Ext.MessageBox.alert("提示信息","激活成功，请保存！");
				}
			},
			
			//弹窗注销折扣明细事件
			"NewCustWindowInnerContent button[id=activeDisItem]":{
				click : function( bt, e, eOpts ) {
				    var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					var itemGrid = NewCustWindowInnerContent.queryById("custGrid");
					var sm = itemGrid.getSelectionModel();
					var records = sm.getSelection();
					Ext.Array.each(records, function(r) {
						r.set("status",0);
					});
					Ext.MessageBox.alert("提示信息","注销成功，请保存！");
				}
			},
			
			//保存客户主数据
			"NewCustWindowInnerContent button[itemId=saveCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					//head信息
					var custForm = NewCustWindowInnerContent.queryById("custForm");
					var formValues = custForm.getValues();
					//grid信息
					var itemGrid = NewCustWindowInnerContent.queryById("custGrid");
					var itemStore = itemGrid.getStore();
					
					var contactsGrid = NewCustWindowInnerContent.queryById("custContactsGrid");
				    var contactsStore = contactsGrid.getStore();
				    
				    
				    var bool = beforeSaveValidate(custForm,itemGrid,contactsGrid,NewCustWindowInnerContent);
				    if(!bool){
				    	return;
				    }
				    var custHeadForm = {};
				    Ext.Object.each(formValues, function(key, value, myself) {
						custHeadForm[key] = value;
					});
				    
				    var kunnrS = custForm.getForm().findField("kunnrS").getValue();
				    var NH = custForm.getForm().findField("nh").getValue();
				    var JH = custForm.getForm().findField("jh").getValue();
				    var kunnr=custForm.getForm().findField("kunnr").getValue();
					if(kunnrS!=''){
						custHeadForm['kunnrS'] = kunnrS;
					}
					if(NH!=''){
						custHeadForm['nh'] = NH;
					}
					if(JH!=''){
						custHeadForm['jh'] = JH;
					}
					if(kunnr!=''){
						custHeadForm['kunnr'] = kunnr;
					}

					
				    
				    /*var gridValues = itemStore.getRange(0,itemStore.getCount());
				    var json = [];
				    for (var i = 0; i <itemStore.getCount(); i++) {
				    	var arrJson = gridValues[i].getData();
				    	json.push(arrJson);
				    }*/
				    
				    //从表记录转换成json
				  	var modifyRecords = itemStore.getUpdatedRecords();
				    var newRecords = itemStore.getNewRecords();
				    var recordsLen = modifyRecords.length;
				    var dataArr = [];
					
					for(var i = 0; i < recordsLen ; i++){
						var record = modifyRecords[i].data;
						var json_x = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json_x[keys[0]] = {id:value};
							}else{
								json_x[key] = value;
							}
							
						});
						dataArr[i] = json_x;
					}
					
					for(var i = 0; i < newRecords.length ; i++){
						var record = newRecords[i].data;
						var json_x = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json_x[keys[0]] = {id:value};
							}else{
								json_x[key] = value;
							}
							
						});
						dataArr[recordsLen+i] = json_x;
					}
				    
				    /*var contactsValues = contactsStore.getRange(0,contactsStore.getCount());
				    var json2 = [];
				    for (var i = 0; i <contactsStore.getCount(); i++) {
				    	var arrJson = contactsValues[i].getData();
				    	json2.push(arrJson);
				    }*/
					
					//从表记录转换成json
				  	var modifyRecords_contacts = contactsStore.getUpdatedRecords();
				    var newRecords_contacts = contactsStore.getNewRecords();
				    var recordsLen_contacts = modifyRecords_contacts.length;
				    var dataArr_contacts = [];
					
					for(var i = 0; i < recordsLen_contacts ; i++){
						var record = modifyRecords_contacts[i].data;
						var json_x = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json_x[keys[0]] = {id:value};
							}else{
								json_x[key] = value;
							}
							
						});
						dataArr_contacts[i] = json_x;
					}
					
					for(var i = 0; i < newRecords_contacts.length ; i++){
						var record = newRecords_contacts[i].data;
						var json_x = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json_x[keys[0]] = {id:value};
							}else{
								json_x[key] = value;
							}
							
						});
						dataArr_contacts[recordsLen_contacts+i] = json_x;
					}
			        
			        var gridData = Ext.encode({
						 custItemList : dataArr, custContactsList : dataArr_contacts
					});
			        
					Ext.Ajax.request({
						url : 'main/cust/save',
						params : custHeadForm,
						method : 'POST',
						frame : true,
						jsonData : gridData,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var obj=$.parseJSON(response.responseText);
							console.log(obj.errorCode);
							if("SAVE-BINDING"==obj.errorCode){
								Ext.MessageBox.alert("提示信息",obj.errorMsg);
								return;
							}
							var values = Ext.decode(response.responseText);
							custForm.getForm().setValues(values.data);
							itemStore.load({params:{'pid':values.data.id}});
							contactsStore.load({params:{'pid':values.data.id}});
							var CustGridView = me.getCustGridView();
							if( typeof(CustGridView) != "undefined" ){
	                    		CustGridView.getStore().loadPage(1);
	                    	}
							Ext.MessageBox.alert("提示信息","保存成功");
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","保存失败");
						}
					});
				}
			},
			
			//弹窗增加联系人事件
			"NewCustWindowInnerContent button[id=addContacts]":{
				click : function( bt, e, eOpts ) {
				    var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					//联系人信息
					var itemGrid = NewCustWindowInnerContent.queryById("custContactsGrid");
					var itemStore = itemGrid.getStore();
					var itemModel = itemStore.model;
					var storeCount = itemStore.getCount();
					var modelit = new itemModel();
					itemStore.insert(storeCount, modelit);
				}
			},
			
			//弹窗删除折扣明细事件
			"NewCustWindowInnerContent button[id=removeContacts]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
					Ext.MessageBox.confirm('提示信息','确定要删除所选联系人？',
				    	function(btn){
					        if(btn=='yes'){
								var itemGrid = NewCustWindowInnerContent.queryById("custContactsGrid");
							    var itemStore = itemGrid.getStore();
							    if(itemStore.getCount()==1){
									Ext.MessageBox.alert("提示信息","必须有一条联系人明细，删除失败");
									return false;
								}
							    var sm = itemGrid.getSelectionModel();
							    var records = sm.getSelection();
							    var ids = [];
								Ext.Array.each(records, function(r) {
									ids.push(r.get('id'))
								});
								Ext.Ajax.request({
									url : 'main/cust/deleteCustContactsByIds',
									params : {
										ids : ids
									},
									method : 'POST',
									success : function(response, opts) {
										itemStore.remove(sm.getSelection());
										var CustGridView = me.getCustGridView();
										if( typeof(CustGridView) != "undefined" ){
				                    		CustGridView.getStore().loadPage(1);
				                    	}
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
			
			//弹窗查询送达方列表
			"NewCustWindowInnerContent button[itemId=songDaFang_queryCust]":{
				click : function( bt, e, eOpts ) {
				    Ext.create('SMSWeb.view.cust.NewCustWindowForSale',
				     {title:'送达方查询'}).show(this,function(){
				    	 queryCust = "songDaFang";
				     });
				}
			},
			//弹窗新增银行账号框
//			"NewCustWindowInnerContent button[itemId=add_tradeId]":{
//				click : function( bt, e, eOpts ) {
//					console.log("123456778");
//				    Ext.create('SMSWeb.view.cust.NewCustWindowForBank',
//				     {title:'绑定客户银行账号'}).show(this,function(){
//				    	 
//				     });
//				}
//			},
			//绑定银行账号中保存按钮点击事件
			"NewCustWindowForBank button[itemId=saveNewCustWindowForBank]":{
				click : function(bt,e,epts){
					var me=this;
					var NewCustWindowForBank=me.getNewCustWindowForBank();
					var custForm=NewCustWindowForBank.queryById("NewCustWindowForBank");
					var cust_tradeId01=$("input[name='cust_tradeId01']").val();
					var cust_tradeId02=$("input[name='cust_tradeId02']").val();
					var NewCustWindowInnerContent=me.getNewCustWindowInnerContent();
				 	var custForm1=NewCustWindowInnerContent.queryById("custForm");
					var kunnr=custForm1.getForm().findField("kunnr").value;
					//console.log(kunnr);
						Ext.Ajax.request({
							url:'main/cust/saveForBankId',
							params:{
								cust_tradeId01:cust_tradeId01,
								cust_tradeId02:cust_tradeId02,
								kunnr:kunnr
							},
							dataType:'json',
							method:'POST',
							success:function(response, opts){
								var obj=$.parseJSON(response.responseText);
								console.log(obj);
								if(obj.success){
									Ext.MessageBox.alert('提示信息',obj.msg);
		                    	}
							}
						});
				}
			},
			
			//查询客户信息
			"NewCustWindowForSale button[itemId=queryCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowForSale = me.getNewCustWindowForSale();
					var CustFormView=me.getCustFormView();
					//head信息
					//var custForm = NewCustWindowForSale.queryById("custForm");
					var custSearchForm=CustFromView.queryById("custSearchForm");
					//var formValues = custForm.getValues();
					var fromValues=custSearchForm.getValues();
					//物料清单信息
					var itemGrid = NewCustWindowForSale.queryById("custGrid");
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
			
			"NewCustWindowForSale button[id=confirmCust]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var NewCustWindowForSale = me.getNewCustWindowForSale();
					
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
                    	
                    	var NewCustWindowInnerContent = me.getNewCustWindowInnerContent();
                    	var	custForm = NewCustWindowInnerContent.queryById("custForm");
                    		
                    	if(queryCust=="songDaFang"){
                    		if(code=="9999"){
                    			Ext.MessageBox.alert('提示信息',"送达方不能选择'临时售达方'");
                    			return;
                    		}else{
                    			custForm.getForm().findField("kunnrS").setValue(code);
								custForm.getForm().findField("kunnrS").initValue();
                    		}
                    	}
						NewCustWindowForSale.close();
						
                    }else{
                    	Ext.MessageBox.alert('提示信息','只能选择一条明细');
                    }
				}
			}

		});
	},
	views : ['cust.CustMainView','cust.CustFormView','cust.CustGridView','cust.NewCustWindowInnerContent','cust.CustFileUploadBaseWindow','cust.CustZhekouFileUploadBaseWindow'],
	stores : ['cust.CustStore'],
	models : ['cust.CustModel']
});

var queryCust;

/**
 * 保存前校验
 * @param {Object} custForm
 * @param {Object} itemGrid
 * @param {Object} contactsGrid
 * @param {Object} NewCustWindowInnerContent
 * @return {TypeName} 
 */
function beforeSaveValidate(custForm,itemGrid,contactsGrid,NewCustWindowInnerContent){
	custForm.isValid();
	var errMsg = "";
	var kunnr = custForm.getForm().findField("kunnr").getValue();
	var name1 = custForm.getForm().findField("name1").getValue();
	var ktokd = custForm.getForm().findField("ktokd").getValue();
	var tel = custForm.getForm().findField("tel").getValue();
	var nh=custForm.getForm().findField('nh').getValue();
	var jh=custForm.getForm().findField('jh').getValue();
	if(kunnr==null || Ext.String.trim(kunnr)==""){
		errMsg += "编号必填<br/>";
	}
	if(name==null || Ext.String.trim(name1)==""){
		errMsg += "名称必填<br/>";
	}
	if(ktokd==null || Ext.String.trim(ktokd)==""){
		errMsg += "请选择类型<br/>";
	}
	//校验农行账号6228400087018597462
	if(Ext.String.trim(nh)!="未绑定"&&Ext.String.trim(nh)!=""){
		if(nh.length==19){
			var nh1=nh.substring(0,8);
			if(nh1!="62284000"){
				errMsg+="请输入正确的农行账号<br/>"
			}
		}else{
			errMsg+="请输入正确的农行账号<br/>"
		}
	}
	//校验建行账号
	if(Ext.String.trim(jh)!="未绑定"&&Ext.String.trim(jh)!=""){
		if(jh.length==20||jh.length==27){
			var jh1=jh.substring(0,20);
			if(jh1!="44050155150700000084"){
				errMsg+="请输入正确的建行账号<br/>"
			}
		}else{
			errMsg+="请输入正确的建行账号<br/>"
		}
	}
	
	
//	var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$|^(\(\d{3,4}-\)|\d{3.4}-)?\d{7,8}$/;

	if(tel!=null && Ext.String.trim(tel)!="" && !reg.test(tel)){
		errMsg += "电话不匹配<br/>";
	}
	if(errMsg!=""){
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	
	var tabs = NewCustWindowInnerContent.queryById("centerTabpanel");
	var itemStore = itemGrid.getStore();
	var contactsStore = contactsGrid.getStore();
	
//	if(itemStore.getCount()==0){
//		tabs.setActiveTab(0);
//		Ext.MessageBox.alert("提示信息","必须有一条折扣明细");
//		return false;
//	}
	
	for (var i = 0; i <itemStore.getCount(); i++) {
    	var record = itemStore.getAt(i);
    	if(record.data.startDate==null || record.data.startDate==""){
    		errMsg += "折扣明细第"+(i+1)+"行的开始日期必填<br/>";
    	}
    	if(record.data.endDate==null || record.data.endDate==""){
    		errMsg += "折扣明细第"+(i+1)+"行的结束日期必填<br/>";
    	}
    }
	if(errMsg!=""){
		tabs.setActiveTab(0); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	
//	if(contactsStore.getCount()==0){
//		tabs.setActiveTab(1);
//		Ext.MessageBox.alert("提示信息","必须有一条联系人");
//		return false;
//	}
	
	for (var i = 0; i <contactsStore.getCount(); i++) {
    	var record = contactsStore.getAt(i);
    	if(record.data.parnr==""){
    		errMsg += "联系人明细第"+(i+1)+"行的联系人必填<br/>";
    	}
    	if(record.data.telf1==""){
    		errMsg += "联系人明细第"+(i+1)+"行的电话必填<br/>";
    	}
    }
	if(errMsg!=""){
		tabs.setActiveTab(1); 
		Ext.MessageBox.alert('提示信息',errMsg);
		return false;
	}
	return custForm.isValid();
}
function fileValidatePDForExecel(file){
	if(file==null||file==""){
		Ext.MessageBox.alert("提示","请选择文件！");
		return false;
	}
	var text = file.substr(file.lastIndexOf(".")).toLowerCase();
	var b;
	var val;

	b=/.(xls|xlsx)$/;
	val = text.match(b);
	if(val!=null){
		return text;	
	} 


	if(val==null){
		Ext.MessageBox.alert("提示","文件格式不正确！");
		return false;
	} 
}


function oneCustAfterLoad(code){
	
}