Ext.onReady(function() {
});
Ext.define("SMSWeb.controller.delivery.DeliveryController", {
	extend : 'Ext.app.Controller',
	id : 'xmlController',
	refs : [
			{
				ref : 'DeliveryMainView',
				selector : 'DeliveryMainView' 
			},
			{
				ref : 'DeliveryGridView',
				selector : 'DeliveryGridView' 
			}
	],
	init : function() {
		this.control({
			"DeliveryMainView button[id=query]":{
				click:function(){
					var grid = this.getDeliveryGridView();
					var store=grid.getStore();
					store.loadPage(1);
				}
			},
			"DeliveryMainView button[id=add]":{
				click:function(){
					var model = Ext.create("SMSWeb.model.delivery.DeliveryModel");
					model.set('delivery.id',this.getDeliveryGridView().getStore().getCount());
					this.getDeliveryGridView().getStore().insert(this.getDeliveryGridView().getStore().getCount(), model);
				}
			},
			"DeliveryMainView button[id=deleteColumn]":{
				click:function(){
					var selection=this.getDeliveryGridView().getSelectionModel().getSelection();
					var store=this.getDeliveryGridView().getStore();
					Ext.MessageBox.confirm('提示信息','确定删除选择项吗？',function(btn){
						if(btn=='yes'){
							if(selection.length > 0){
								var ids=[];
								Ext.each(selection,function(R){
									ids[R.index]=R.data.id;
								});
								Ext.Ajax.request({
									url:"delivery/deletesLine",
									params:{ids:ids},
									method:"POST",
									dataType:"JSON",
									contentType : 'application/json',
									success:function(response,opts){
										var values=Ext.decode(response.responseText);
										Ext.MessageBox.alert(values.errorCode,values.errorMsg);
										store.loadPage(1);
									},
									failure:function(response,opts){
										Ext.Msg.alert("can't",'error');
									}
								});
							}else{
								Ext.MessageBox.alert("提示信息","请使用常规操作，~_~||| 谢谢");
							}
						}else{
							
						}
					});
				}
			},
			"DeliveryMainView button[id=save]":{
				click:function(){
					var selection=this.getDeliveryGridView().getSelectionModel().getSelection();
					var store=this.getDeliveryGridView().getStore();
					store.clearFilter();
					store.filterBy(function(record){
						return record.get("id")=="";
					});
					if(store.getCount()>0){
						var gridData=[];
						var num=0;
						store.each(function(R){
							gridData[num]=R.data;
							num++;
						});
						var data=Ext.encode({data:gridData});
						Ext.Ajax.request({
							url:"delivery/saveLine",
							jsonData:data,
							method:"POST",
							dataType:"JSON",
							contentType : 'application/json',
							success : function(response, opts) {
								var values=Ext.decode(response.responseText);
								Ext.MessageBox.alert(values.errorCode,values.errorMsg);
								store.loadPage(1);
							},
							failure : function(response, opts) {
								Ext.MessageBox.alert("提示信息","保存失败"+response.responseText);
							}
						});
					}else{
						Ext.MessageBox.alert("提示信息","未检测到新增数据");
					}
				}
			}
		});
	},
	requires : ["Ext.ux.form.TableComboBox","Ext.ux.grid.UXGrid"],
	views : ['delivery.DeliveryMainView','delivery.DeliveryGridView','delivery.DeliveryFormView'],
	stores : ['delivery.DeliveryGridStore'],
	models : ['delivery.DeliveryModel']
})