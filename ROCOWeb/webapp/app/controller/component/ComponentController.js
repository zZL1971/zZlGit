Ext.onReady(function() {
});
Ext.define("SMSWeb.controller.component.ComponentController", {
	extend : 'Ext.app.Controller',
	id : 'xmlController',
	refs : [
			{
				ref : 'ComponentMainView',
				selector : 'ComponentMainView' 
			},
			{
				ref : 'ComponentGridView',
				selector : 'ComponentGridView' 
			}
	],
	init : function() {
		this.control({
			"ComponentMainView button[id=query]":{
				click:function(){
					var grid = this.getComponentGridView();
					var store=grid.getStore();
					store.loadPage(1);
				}
			},
			"ComponentMainView button[id=add]":{
				click:function(){
					var model = Ext.create("SMSWeb.model.component.ComponentModel");
					model.set('component.id',this.getComponentGridView().getStore().getCount());
					this.getComponentGridView().getStore().insert(this.getComponentGridView().getStore().getCount(), model);
				}
			},
			"ComponentMainView button[id=deleteColumn]":{
				click:function(){
					var selection=this.getComponentGridView().getSelectionModel().getSelection();
					var store=this.getComponentGridView().getStore();
					Ext.MessageBox.confirm('提示信息','确认删除选择项吗？',function(btn){
						if(btn=='yes'){
							if(selection.length > 0){
								var ids=[];
								var num =0;
								Ext.each(selection,function(R){
									if(R.data.id != ""||undefined != R.data.id||null != R.data.id){
										ids[num]=R.data.id;
										num++;
									}
								});
								Ext.Ajax.request({
									url:"delivery/deletesComponent",
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
			"ComponentMainView button[id=save]":{
				click:function(){
					var selection=this.getComponentGridView().getSelectionModel().getSelection();
					var store=this.getComponentGridView().getStore();
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
							url:"delivery/saveComponent",
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
			},
			"ComponentMainView button[id=excellupload]":{
				click:function(){
					var materialPriceFileView=Ext.create("SMSWeb.view.component.ComponentFileUploadBaseWindow",{});
					materialPriceFileView.show();
				}
			}
		});
	},
	requires : ["Ext.ux.form.TableComboBox","Ext.ux.grid.UXGrid"],
	views : ['component.ComponentMainView','component.ComponentGridView','component.ComponentFormView'],
	stores : ['component.ComponentGridStore'],
	models : ['component.ComponentModel']
})