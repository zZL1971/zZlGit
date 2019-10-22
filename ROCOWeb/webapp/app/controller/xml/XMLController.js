Ext.onReady(function() {
});
Ext.define("SMSWeb.controller.xml.XMLController", {
	extend : 'Ext.app.Controller',
	id : 'xmlController',
	refs : [
			{
				ref : 'XMLMainView',
				selector : 'XMLMainView' 
			},
			{
				ref : 'XMLGridView',
				selector : 'XMLGridView' 
			},
			{
				ref : 'AddXMLContextGrid',
				selector : 'AddXMLContextGrid' 
			}
	],
	init : function() {
		this.control({
			"XMLMainView button[id=query]":{
				click:function(){
					var grid = this.getXMLGridView();
					var store=grid.getStore();
					store.loadPage(1);
				}
			},
			"XMLMainView button[id=add]":{
				click:function(){
					var model = Ext.create("SMSWeb.model.xml.XMLModel");
					model.set('xml.id',this.getXMLGridView().getStore().getCount());
					this.getXMLGridView().getStore().insert(this.getXMLGridView().getStore().getCount(), model);
				}
			},
			"XMLMainView button[id=deleteColumn]":{
				click:function(){
					var selection=this.getXMLGridView().getSelectionModel().getSelection();
					var store=this.getXMLGridView().getStore();
					if(selection.length > 0){
						var ids=[];
						Ext.each(selection,function(R){
							ids[R.index]=R.data.id;
						});
						Ext.Ajax.request({
							url:"xmlcontrol/deletes",
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
				}
			},
			"XMLMainView button[id=save]":{
				click:function(){
					var selection=this.getXMLGridView().getSelectionModel().getSelection();
					var store=this.getXMLGridView().getStore();
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
							url:"xmlcontrol/save",
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
			"AddXMLContextGrid button[id=addData]":{
				click:function(){
					var me = this;
					debugger;
					var selection=this.getAddXMLContextGrid().queryById('xmlgridId').getSelectionModel();
					if(selection.store.data.items.length<=0){
						Ext.MessageBox.alert("提示信息","至少选中一条明细");
						return;
					}
					var $num=0;//计数器
					var realPname="";
					var realPvarString="";
					var realChangeVal="";
					var realCounter="";
					Ext.each(selection.store.data.items,function(val){
						$num++;
						var pname=val.data.pname;
						var pvarString=val.data.pvarString;
						var changeVal=val.data.changeVal;
						if(pname=="")pname="NULL";
						var count=0;
						if(!(pvarString=="")){
							var text=pvarString.split(",");
							for(var i=0;i<text.length;i++){
								realPvarString+=($num+":"+text[i])+",";
								count++;
							}
						}
						if(!(changeVal=="")){
							realChangeVal+=($num+":"+changeVal)+",";
						}else{
							realChangeVal +=($num+":NULL,");
						}
						count++;
						realCounter+=($num+":"+count)+"@";
						realPname+=($num+":"+pname)+",";
					});
					var xmlDecs="";//"P:["+realPname+"]@"+"V:["+realPvarString+"]@"+"V:["+realChangeVal+"]"
					var counter="";
					var type="[";
					if(realPname!=""){
						xmlDecs+="P:["+realPname.substring(0,realPname.length-1)+"]";
						type+="P,";
					}
					if(realPvarString!=""){
						xmlDecs+="@V:["+realPvarString.substring(0,realPvarString.length-1)+"]";
						type+="V,";
					}
					if(realChangeVal!=""){
						xmlDecs+="@C:["+realChangeVal.substring(0,realChangeVal.length-1)+"]";
						if(realChangeVal.indexOf("NULL")==-1){
							type+="C,";
						}
					}
					if(realCounter!=""){
						counter+=realCounter.substring(0,realCounter.length-1);
					}
					var parent=this.getXMLGridView().getSelectionModel().getSelection();
					type=type.substring(0,type.length-1);
					Ext.each(parent,function(val){
						val.set("text",xmlDecs);
						val.set("type",type+"]");
						val.set("counter",counter);
						var id = val.data.id;
						if(id!=null || ""!=id || undefined!=id){
							me.getXMLGridView().updateGridData("text",xmlDecs,id);
							me.getXMLGridView().updateGridData("type",type+"]",id);
							me.getXMLGridView().updateGridData("counter",counter,id);
						}
					});
					this.getAddXMLContextGrid().close();
				}
			}
		});
	},
	requires : ["Ext.ux.form.TableComboBox","Ext.ux.grid.UXGrid"],
	views : ['xml.XMLMainView','xml.XMLGridView','xml.XMLFormView','xml.AddXMLContextGrid'],
	stores : ['xml.XMLGridStore'],
	models : ['xml.XMLModel']
})