Ext.define("SMSWeb.controller.mm.PriceConditionController", {
	extend : 'Ext.app.Controller',
	refs : [
	        {
				ref : 'PriceConditionGridView',
				selector : 'PriceConditionGridView' 
			}
	],
	init : function() {
		
		this.control({
			"PriceConditionGridView":{
				addButtonClick:function(){
					var me = this;
					var PriceConditionGridView = me.getPriceConditionGridView();
					var store = PriceConditionGridView.getStore();
					var model = Ext.create("SMSWeb.model.mm.pc.PriceConditionModel");
					var count = store.getCount();
					store.insert(count, model);
				},
				deleteButtonClick:function(){
					var me = this;
					var ids = [];
					var grid = me.getPriceConditionGridView();
					var records = grid.getSelectionModel().getSelection();
					Ext.Array.each(records, function(r) {
								if(r.get('id')){
									ids.push(r.get('id'))
								}
							});
					if(ids.length>0){
						Ext.Ajax.request({
							url : 'main/mm/deletePriceConditionByIds',
							params : {
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
						grid.getStore().remove(records);
					}
				},
				saveButtonClick:function(){
					var me = this;
					var PriceConditionGridView = me.getPriceConditionGridView();
					
					var store = PriceConditionGridView.getStore();
					var gridValues = store.getRange(0,store.getCount());
					var json = [];
				    for (var i = 0; i <store.getCount(); i++) {
				    	var arrJson = gridValues[i].getData();
				    	json.push(arrJson);
				    }
					
					var gridData = Ext.encode({
						priceConditions : json
					});
			        
			        if(json.length>0){
						Ext.Ajax.request({
							url : 'main/mm/savePriceCondition',
							jsonData : gridData,
							method : 'POST',
							frame:true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);	
								if(values.success){
									store.load();
									Ext.MessageBox.alert("提示","保存成功！");
								}else{
									Ext.MessageBox.alert("提示",values.errorMsg);
								}
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示","保存失败！");
							}
						});
					}
					
				}
			}
			
		});
	},
	views : ['mm.pc.PriceConditionGridView'],
	stores : [],
	models : []
});