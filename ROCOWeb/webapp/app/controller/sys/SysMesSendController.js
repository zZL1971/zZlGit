Ext.onReady(function() {
//	orderTypeCombobox.getStore().load(function(records, operation, success) {
//	});
//	jiaoQiTianShuStore.load(function(records, operation, success) {
//	});
//	handleTimeStore.load(function(records, operation, success) {
//	});
});

Ext.define("SMSWeb.controller.sys.SysMesSendController", {
	extend : 'Ext.app.Controller',
	id : 'sysMesSendController',
	requires : ["Ext.ux.form.DictCombobox","Ext.ux.ButtonTransparent"],
	refs : [
		{
			ref : 'SysMesSendMainView',
			selector : 'SysMesSendMainView' 
		},
		{
			ref : 'NewSysMesSendWindow',
			selector : 'NewSysMesSendWindow' 
		}
	],
	init : function() {
		this.control({//保存主表
			
			//查询订单信息
			"SysMesSendMainView button[itemId=query]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					var SysMesSendMainView = me.getSysMesSendMainView();
//					var form = SysMesSendMainView.queryById("sysMesSendForm");
//					var formValues = form.getValues();
					var grid = SysMesSendMainView.queryById("sysMesSendGrid");
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
			
			"SysMesSendMainView button[itemId=send]":{
				click : function( bt, e, eOpts ) {
					var me = this;
					
					Ext.Ajax.request({
						url : 'core/msg/isRead',
						method : 'POST',
						async:false,
						success : function(response, opts) {
						    var SysMesSendMainView = me.getSysMesSendMainView();
							var grid = SysMesSendMainView.queryById("sysMesSendGrid");
						    var store = grid.getStore();
						    store.loadPage(1);
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","网络异常！");
						}
					});
					
				}
			},
			
			//grid编辑事件
			'SysMesSendMainView grid':{
				itemEditButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var hasReaded = record.get('hasReaded');
					var id = record.get('id');
					if(hasReaded=="1"){
						Ext.create('SMSWeb.view.sys.NewSysMesSendWindow',
						     {formId : id,title:'信息查看'}).show(/*this,function(){}*/);
					}else{
						var ids = [];
						ids.push(id);
						Ext.Ajax.request({
							url : 'core/sysMesSend/updateSysMesSendByIds',
							params : {
								ids : ids
							},
							method : 'POST',
							success : function(response, opts) {
							    var store = grid.getStore();
							    store.loadPage(1);
								Ext.create('SMSWeb.view.sys.NewSysMesSendWindow',
								     {formId : id,title:'信息查看'}).show(/*this,function(){}*/);
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","网络异常！");
							}
						});
					}
				}
			}
			
		});
	},
	views : ['sys.SysMesSendMainView'],
	stores : ['sys.SysMesSendStore'],
	models : ['sys.SysMesSendModel']
});
