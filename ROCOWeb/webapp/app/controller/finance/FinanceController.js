Ext.define('SMSWeb.controller.finance.FinanceController', {
	extend : 'Ext.app.Controller',
	views : ['finance.FinanceView','cust.NewCustWindow','cust.NewCustWindowInnerContent','bpm.TaskApprove'],
	requires : ["Ext.ux.form.DictCombobox", "Ext.ux.grid.UXGrid","Ext.ux.ButtonTransparent","Ext.ux.form.TableComboBox"],
	id:'financeController',
	refs : [{
		ref : 'FinanceView',
		selector : 'FinanceView' 
	}],
	init : function() {
		var me=this;
	}
});

function SMSWeb_controller_finance_FinanceController_showCustWin(code){
	console.log(code);
	Ext.create('SMSWeb.view.cust.NewCustWindow',
	     {formId : code,title:'客户信息',editFlag:false}).show(/*this,function(){}*/);
}

function SMSWeb_controller_finance_FinanceController_fileDownloadByOrderId(id,orderCode,shouDaFang){
	//alert("文件下载")
	var ids=new Array();
	ids[0]=id+':'+shouDaFang+"_"+orderCode;
	window.location.href = basePath+"main/sysFile/fileloadBatch/"+ids;
//	Ext.Ajax.request({
//		url : '/main/sysFile/fileDownloadByOrderId/'+id,
//		async : false,
//		method : 'GET',
//		dataType : "json",
//		contentType : "application/json",
//		callback : function(options, success, response) {
//			if (!success) {
//				Ext.Msg.show({
//					title : "错误代码:S-500",
//					icon : Ext.Msg.ERROR,
//					msg : "链接服务器失败，请稍后再试或联系管理员!",
//					buttons : Ext.Msg.OK
//				});
//			}
//		},
//		success : function(response, opts) {
//			var values = Ext.decode(response.responseText);
////			var ids=values.data;
////			for(var i=0;i<ids.length;i++){
////				console.log(ids[i]);
////				window.location.href = basePath+'main/sysFile/fileDownloadZip'+"?id="+ids[i]+"&cd="+new Date().getTime();  
////			}
//			
//		},
//		failure : function(response, opts) {
//			Ext.Msg.alert("can't", 'error');
//		}
//	});
	
}