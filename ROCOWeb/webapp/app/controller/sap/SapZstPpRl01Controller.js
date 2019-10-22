Ext.onReady(function() {
//	orderTypeCombobox.getStore().load(function(records, operation, success) {
//	});
//	jiaoQiTianShuStore.load(function(records, operation, success) {
//	});
//	handleTimeStore.load(function(records, operation, success) {
//	});
});

Ext.define("SMSWeb.controller.sap.SapZstPpRl01Controller", {
	extend : 'Ext.app.Controller',
	id : 'sapZstPpRl01Controller',
	refs : [
		{
			ref : 'SapZstPpRl01MainView',
			selector : 'SapZstPpRl01MainView' 
		}
	],
	init : function() {
		this.control({//保存主表
			
			//查询订单信息
			"SapZstPpRl01MainView button[itemId=query]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var SapZstPpRl01MainView = me.getSapZstPpRl01MainView();
					var form = SapZstPpRl01MainView.queryById("sapZstPpRl01Form");
					var grid = SapZstPpRl01MainView.queryById("sapZstPpRl01Grid");
					var formValues = form.getValues();
				    var store = grid.getStore();
				    store.load({
				    	params:formValues,
				    	callback:function(r,options,success){
				            if(success){
				           }
				        }
				    });
				}
			},
			
			
			//同步SAP信息
			"SapZstPpRl01MainView button[itemId=sync]" : {
				click : function() {
					var me = this;
					var SapZstPpRl01MainView = me.getSapZstPpRl01MainView();
					var form = SapZstPpRl01MainView.queryById("sapZstPpRl01Form");
					var formValues = form.getValues();
					Ext.Ajax.request({
						url : 'main/sapZstPpRl01/sync',
						method : 'POST',
						params:formValues,
						success : function(response, opts) {
							var SapZstPpRl01MainView = me.getSapZstPpRl01MainView();
							var grid = SapZstPpRl01MainView.queryById("sapZstPpRl01Grid");
							if( typeof(grid) != "undefined" ){
	                    		grid.getStore().loadPage(1);
	                    	}
							Ext.MessageBox.alert("提示信息","同步成功！");
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","同步失败！");
						}
					});
				}
			},
			"SapZstPpRl01MainView button[itemId=syncJsmj]" : {
				click : function() {
					var me = this;
					Ext.Ajax.request({
						url : 'main/sapZstPpRl01/syncJsmj',
						method : 'POST',
						success : function(response, opts) {
							Ext.MessageBox.alert("提示信息","同步成功！");
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","同步失败！");
						}
					});
				}
			}
		});
	},
	views : ['sap.SapZstPpRl01MainView'],
	stores : ['sap.SapZstPpRl01Store'],
	models : ['sap.SapZstPpRl01Model']
});
