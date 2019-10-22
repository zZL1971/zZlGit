Ext.define('MeWeb.view.system.role.UAWin', {
	extend : 'Ext.window.Window',
	roleRecord:null,
	roleId:null,
	alias : 'widget.roleWin',
	requires : ['Ext.form.*','MeWeb.common.core.CodeCombobox'],
	initComponent : function() {
		var me = this;
		if(this.roleRecord!=null){
			me.title = '授权于【'+me.roleRecord.get("descZhCn")+"】";
			roleId = me.roleRecord.getId();
		}else{
			me.title = '授权';
		}
		
		var menuTree = Ext.createWidget('treepanel',{
			border:false,
			displayField:'descZhCn',
			rootVisible: true,
			useArrows:true,
			layout : 'fit',
			animate : true,
			autoscroll: true,
			containerScroll : true,
			lines: true,
			columnLines : true,
			dockedItems: [{
		        xtype: 'toolbar',
		        dock: 'top',
		        items: [{
		        	xtype:'button',
		        	itemId:'ua_save',
		        	text:'保存',
		        	iconCls:'table_save',
		        	handler: function(event, toolEl){
		        		var a_ = event.ownerCt.ownerCt.getChecked();
		        		var menus = [];
		        		
		        		Ext.Array.each(a_,function(item){
		        			menus.push(item.data.id);
		        		});
		        		
		        		Ext.Ajax.request({
							url:'/core/role/menu/save/',
							async:false,
							jsonData:{rid:roleId,menus:menus},
							method:'POST',
							dataType: "json",
							contentType:"application/json",
							callback:function(options,success,response){
								if(!success){
									Ext.Msg.show({
										title:"错误代码:S-500",
										icon:Ext.Msg.ERROR,
										msg:"链接服务器失败，请稍后再试或联系管理员!",
										buttons:Ext.Msg.OK
									});
								}
							},
							success:function(response,opts){
								var jsonResult = Ext.decode(response.responseText);
								if(jsonResult.success){
									Ext.Msg.alert("提示",jsonResult.msg);
								}else{
									Ext.Msg.show({
										title:"错误代码:"+jsonResult.errorCode,
										icon:Ext.Msg.ERROR,
										msg:jsonResult.errorMsg,
										buttons:Ext.Msg.OK
									});
								}
								
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					}
		        },{
					text: '展开',
					iconCls:'ux-toolbar-expand',
					handler: function(event, toolEl){
						event.ownerCt.ownerCt.expandAll();
					}
				},{
					text: '收拢',
					iconCls:'ux-toolbar-collapse',
					handler: function(event, toolEl){
						event.ownerCt.ownerCt.collapseAll();   
					}
				}]
		    }],
			store: Ext.create("MeWeb.store.system.menu.SysMenuStore"),
			viewConfig : {
				onCheckboxChange : function(e, t) {
					var item = e.getTarget(this.getItemSelector(), this
									.getTargetEl()), record;
					var me = this;
					if (item) {
						record = this.getRecord(item);
						var check = !record.get('checked');
						record.set('checked', check);
						if (check) {
							record.bubble(function(parentNode) {
								//if(!parentNode.isRoot()){
									parentNode.set('checked', true);
									parentNode.expand(false, true);
								//}
							});
							record.cascadeBy(function(node) {
								node.set('checked', true);
								node.expand(false, true);
							});
						} else {
							record.cascadeBy(function(node) {
								node.set('checked', false);
							});
							record.bubble(function(parentNode) {
								var a_ = parentNode.childNodes;
								var status =true;
								//var b_ = me.getNode(parentNode).firstChild.firstChild;
								//var checkbox=b_.getElementsByTagName('input')[0];
								//checkbox.className=checkbox.className.replace(' Diy-mask','')+' Diy-mask';
								
								Ext.Array.each(a_,function(name, index, countriesItSelf){
									var precord = me.getRecord(name);
									if(precord.get('checked')==true){
										status = false;
									}
								});
								
								if(status){
									//if(!parentNode.isRoot()){
										parentNode.set('checked', false);
									//}
								}
							});
						}
					}
				}
			},
			listeners:{
				load:function( store, node, records, successful, eOpts ){
					var a_ = this.up('window');
					var b_ = a_.roleRecord.getId();
					Ext.Ajax.request({
						url:'/core/role/menu/'+b_,
						async:false,
						method:'POST',
						success:function(response,opts){
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								Ext.Array.each(jsonResult.data,function(item){
									var node = store.getNodeById(item.id);
									node.set('checked', true);
								});
							}
						},
						failure:function(response,opts){
							Ext.Msg.alert("can't",'error');
						}
					});
				}/*,
				checkchange:function( node, checked, eOpts){
					
					
					if (checked) {
						node.cascadeBy(function(n) {
	                        n.set('checked', true);
	                    });
	                    
						node.bubble(function(parentNode) {
							//parentNode.findChild("checked",)
							
							var a_ = parentNode.childNodes;
							
							Ext.Array.each(a_,function(name, index, countriesItSelf){
								alert(name.checked+"|"+index);
							});
							
							parentNode.set('checked', true);
							parentNode.expand(false, true);
						});
	                    
	                }else {
	                	node.bubble(function(parentNode) {
							parentNode.set('checked',false);
							//parentNode.expand(false, false);
						});
	                    node.cascadeBy(function(n) {
	                        n.set('checked', false);
	                    });
	                }
				}*/
			}
		});
		
		/*var form = Ext.createWidget('form',{
			items:[{
				xtype:'codecombobox',
				name:'color'
			}]
		});*/
		
		//创建tabpanel
		var tab = Ext.createWidget("tabpanel",{
			plain: true,//不显示候选栏背景图片
			activeTab: 0,
			margin : '5 0 5 0',
			border:false,
			items:[{
		        id: "tab1",
		        title: '菜单授权',
		        border:false,
		        layout : 'fit',
		        items:[menuTree]
		    },{
		        id: "tab2",
		        title: '用户授权'
		    }/*,{
		        id: "tab3",
		        title: '数据字典表单测试代码',
		        items:[form]
		    }*/]
		});
		
		
		Ext.apply(this, {
					height : 480,
					width : 480,
					closable : true,
					layout : 'fit',
					modal : true,
					items:[tab]
				});
				
		this.callParent(arguments);
	}
});