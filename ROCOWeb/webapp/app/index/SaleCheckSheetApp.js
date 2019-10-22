Ext.QuickTips.init();
Ext.Loader.setConfig({
			enabled : true
		});
var queryType=3;//按个人
Ext.Loader.setPath('Ext.ux', '/extjs/4.2.1/src/ux');// 修正IE路径识别bug by ab
//Ext.util.CSS.swapStyleSheet("theme", "extjs/4.2.1/resources/css/ext-all-gray.css");
Ext.application({
			name : 'SMSWeb',
			appFolder : "app",
			launch : function() {
	console.log("CGG22");

				Ext.create('Ext.container.Viewport', {
							layout : 'fit',
							padding : "0 0 0 0",
							items : {
								xtype : 'SaleCheckSheetMainView'
							}
						});
				
				
	            //获取客户信贷
				Ext.Ajax.request({
							url:'main/cust/findxdById',
							async:false,
							params : {
									id:CURR_USER_KUNNR //比如 ：LJ75002
							},
							method : 'GET',
							success : function(response, opts) {
							var jsonResult = Ext.decode(response.responseText);
							Ext.getCmp("SaleCheckSh22FormView").getForm().findField("xinDai").setValue(jsonResult.data.xinDai);
							//console.log("555");
							if(jsonResult.success){ 
														
								_FLAG=jsonResult.data.FLAG;
								_msg=jsonResult.data.MSG;
							}else{
								_msg=jsonResult.errorMsg;
								_FLAG="N";
							}
							},
							failure : function(response, opts) {
									Ext.Msg.alert("错误代码:"+response.status,response.responseText);
							}
				});
			},
			controllers : ['sale.SaleController']
		});
