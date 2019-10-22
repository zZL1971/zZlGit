Ext.define('SMSWeb.controller.tm.TM1stController', {
	extend : 'Ext.app.Controller',
	views:['tm.OrderReportView','tm.NewOrderDetail','tm.TM1stView','tm.CustSoftDogUploadWindow'],
	requires : ["Ext.ux.form.DictCombobox", "Ext.ux.grid.UXGrid", "Ext.ux.grid.UXTreeGrid","Ext.ux.ButtonTransparent"],
	refs:[{
		ref : 'TMUpdateSave',
		selector : 'TMUpdateSave' 
	},{
		ref:'RearFrom',
		selector:'RearFrom'
	},{
		ref:'CustSoftDogUploadWindow',
		selector:'CustSoftDogUploadWindow'
	},{
		ref : 'TM1UpdateSave',
		selector : 'TM1UpdateSave' 
	},{
		ref : 'TM2OrdErrType',
		selector : 'TM2OrdErrType' 
	}],
	init : function() {
		var me = this;
		me.control({
			"TM2OrdErrType button[itemId=saveOrdErrType]":{
				click:function(bt, e, eOpts){
					var TM2OrdErrTypefrom=Ext.getCmp("TMoeOrdErrType");
					var TM2OrdErrType=me.getTM2OrdErrType();
					var orderCodePosex=TM2OrdErrType.orderCodePosex;
					var acoeId=TM2OrdErrType.acoeId;
					var form=TM2OrdErrTypefrom.getForm().getValues();
					if(Ext.isEmpty(form.errType.trim())||Ext.isEmpty(form.tackit.trim())||Ext.isEmpty(form.errDesc.trim())){
						Ext.Msg.alert("提示","不允许为空!");
						return;
					}
					var myMask = new Ext.LoadMask(TM2OrdErrTypefrom,{msg:"请稍等..."});
					Ext.Ajax.request({
						url : 'main/saleReport/saveOrdErrType',
						params : {
							errType : form.errType,tackit:form.tackit,errDesc:form.errDesc,orderCodePosex:orderCodePosex,acoeId:acoeId
						},
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							if(values.success){
								headForm.getForm().setValues(values.data); 
								Ext.MessageBox.alert("提示","保存成功！");
								
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){myMask.hide();}
							var grid = Ext.getCmp("gridview-1042");
							if(grid){
								grid.getStore().load();
							}
							TM2OrdErrType.close();
						},
						failure : function(response, opts) {
							m.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				}
			},
			"CustSoftDogUploadWindow":{
				fileUploadButtonClick:function(){
					var me = this;
					var fileUploadWindow = me.getCustSoftDogUploadWindow();
					var fileUploadForm = fileUploadWindow.queryById("fileUploadForm"); 
					var file = fileUploadForm.getForm().findField("file").getValue();
					var type=fileValidatePDForExecel(file);
					if(type){
						var fileType = fileUploadForm.getForm().findField("fileType").setValue(type);
						if(fileUploadForm.getForm().isValid()){  
							fileUploadForm.submit({  
								url: 'main/cust/softDogUpload', 
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
			"TM1UpdateSave button[itemId=saveKf]":{
				click : function(bt, e, eOpts) {
					var headForm=Ext.getCmp("Tm2stForm");
					var TMUpdateSave=me.getTM1UpdateSave();
					var form=headForm.getForm().getValues();
					var gridData = Ext.encode({
						materialComplainidFrom:form,
						tousucishu:form.tousucishu,
						saleId :form.saleId,
						problem:form.problem
					});
					var myMask = new Ext.LoadMask(headForm,{msg:"请稍等..."}); 
					myMask.show();
					Ext.Ajax.request({
						url : 'main/myGoods/updateComplainid',
						jsonData : gridData,
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							if(values.success){
								headForm.getForm().setValues(values.data); 
								Ext.MessageBox.alert("提示","保存成功！");
								
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){myMask.hide();}
							TMUpdateSave.close();
							var grid = Ext.getCmp("gridview-1061");
							if(grid){
								grid.getStore().load();
							}
						
							
						},
						failure : function(response, opts) {
							m.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				}
			},
			"TMUpdateSave button[itemId=saveKf]":{
				click : function(bt, e, eOpts) {
					var headForm=Ext.getCmp("Tm1stForm");
					var TMUpdateSave=me.getTMUpdateSave();
					var form=headForm.getForm().getValues();
					var gridData = Ext.encode({
						materialBujian:form
					});
					var myMask = new Ext.LoadMask(headForm,{msg:"请稍等..."}); 
					myMask.show();
					Ext.Ajax.request({
						url : 'main/myGoods/updateBujian',
						jsonData : gridData,
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							if(values.success){
								headForm.getForm().setValues(values.data); 
								Ext.MessageBox.alert("提示","保存成功！");
								
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){myMask.hide();}
							TMUpdateSave.close();
							var grid = Ext.getCmp("gridview-1065");
							if(grid){
								grid.getStore().load();
							}
						
							
						},
						failure : function(response, opts) {
							m.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				}
			},
			"RearFrom button[itemId=saveRear]":{
				'click':function(){
					var headForm=Ext.getCmp("RearFrom");
					var RearFrom=me.getRearFrom();
					var form=headForm.getForm().getValues();
					for(var data=0;data<form.length;data++){
						
					}
					var myMask = new Ext.LoadMask(headForm,{msg:"请稍等..."}); 
					myMask.show();
					Ext.Ajax.request({
						url : 'core/task/save',
						params : {rearId:form.rear,
									 id:form.uid},
						method : 'POST',
						frame:true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);	
							if(values.success){
								headForm.getForm().setValues(values.data); 
								Ext.MessageBox.alert("提示","保存成功！");
								
							}else{
								Ext.MessageBox.alert("提示",values.errorMsg);
							}
							if (myMask != undefined){myMask.hide();}
							RearFrom.close();	
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","保存失败！");
							if (myMask != undefined){ myMask.hide();}
						}
					});
				}
				
			}
		});
	}
});
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