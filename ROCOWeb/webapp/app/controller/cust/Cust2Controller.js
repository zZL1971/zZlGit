Ext.define("SMSWeb.controller.cust.Cust2Controller", {
	extend : 'Ext.app.Controller',
	init : function() {
		this.control({
			//保存客户信息
			"Cust2MainView button[id=saveCust]":{
				click : function( bt, e, eOpts ) {
					var cust2Form = Ext.getCmp("cust2Form");
					var formValues = cust2Form.getValues();
					
					var itemGrid = Ext.getCmp("cust2Grid");
				    var itemStore = itemGrid.getStore();
				    
				    var contactsGrid = Ext.getCmp("custContactsGrid");
				    var contactsStore = contactsGrid.getStore();
				    
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
					if(cust2Form.isValid()){
						if(itemStore.getCount()==0){
							Ext.MessageBox.alert("提示信息","必须有一条折扣明细");
							return;
						}
						if(contactsStore.getCount()==0){
							Ext.MessageBox.alert("提示信息","必须有一条联系人");
							return;
						}
						Ext.Ajax.request({
							url : 'main/cust/save',
							params : formValues,
							method : 'POST',
							frame : true,
							jsonData : gridData,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								cust2Form.getForm().setValues(values.data);
								
								itemStore.load({params:{'pid':values.data.id}});
								contactsStore.load({params:{'pid':values.data.id}});
								Ext.MessageBox.alert("提示信息","保存成功");
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","保存失败");
							}
						});
					}
				}
			},
		
			"Cust2MainView button[id=addItem]":{
				click : function( bt, e, eOpts ) {
				    var itemGrid = Ext.getCmp("cust2Grid");
					var itemStore = itemGrid.getStore();
					var itemModel = itemStore.model;
					var storeCount = itemStore.getCount();
					var modelit = new itemModel();
//					var modelit = Ext.create('SMSWeb.model.cust.Cust2Model', {
//	                    startDate: Ext.Date.clearTime(new Date()),
//	                    endDate: Ext.Date.clearTime(new Date()),
//	                    zheKou: 0,
//	                    total: 0,
//	                    shengYu: 0
//	                });
					itemStore.insert(storeCount, modelit);
				}
			},
			
			"Cust2MainView button[id=removeItem]":{
				click : function( bt, e, eOpts ) {
					Ext.MessageBox.confirm('提示信息','确定要删除所选折扣明细？',
				    	function(btn){
					        if(btn=='yes'){
								var itemGrid = Ext.getCmp("cust2Grid");
							    var itemStore = itemGrid.getStore();
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
			
			"Cust2MainView button[id=addContacts]":{
				click : function( bt, e, eOpts ) {
				    var itemGrid = Ext.getCmp("custContactsGrid");
					var itemStore = itemGrid.getStore();
					var itemModel = itemStore.model;
					var storeCount = itemStore.getCount();
					var modelit = new itemModel();
					itemStore.insert(storeCount, modelit);
				}
			},
			
			"Cust2MainView button[id=removeContacts]":{
				click : function( bt, e, eOpts ) {
					Ext.MessageBox.confirm('提示信息','确定要删除所选联系人？',
				    	function(btn){
					        if(btn=='yes'){
								var itemGrid = Ext.getCmp("custContactsGrid");
							    var itemStore = itemGrid.getStore();
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
		});
	},
	views : ['cust.Cust2MainView','cust.Cust2FormView','cust.Cust2GridView','cust.CustContactsGridView'],
	stores : ['cust.Cust2Store','cust.CustContactsStore'],
	models : ['cust.Cust2Model','cust.CustContactsModel']
});

function tabChange(tabs, newTab, oldTab){
	if(newTab.title=="联系人"){
		Ext.getCmp("removeContacts").show();
		Ext.getCmp("addContacts").show();
		Ext.getCmp("removeItem").hide();
		Ext.getCmp("addItem").hide();
	}else{
		Ext.getCmp("removeContacts").hide();
		Ext.getCmp("addContacts").hide();
		Ext.getCmp("removeItem").show();
		Ext.getCmp("addItem").show();
	}
}