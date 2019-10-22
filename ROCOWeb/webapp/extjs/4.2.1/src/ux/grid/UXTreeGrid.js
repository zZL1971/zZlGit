Ext.define("Ext.ux.grid.UXTreeGrid", {
			extend : 'Ext.tree.Panel',
			alias : 'widget.uxtreegrid',
			xtype : 'uxtreegrid',
			useArrows : true,
			columnLines : true,
			rowLines : true,
			rootVisible : false,
			gridLoad:false,
			viewConfig : {
				enableTextSelection:true, //可以复制单元格文字
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
			initComponent:function(){
				var me = this;
				if(me.selModelStatus){
					me.selModel = {
						selType:'checkboxmodel',
						injectCheckbox:1
					};
				}
				//初始化grid表头数据
				Ext.Ajax.request({
					url:'/core/ext/grid/treemodel/'+me.headerModule,
					async:false,
					dataType: "json",
					success:function(response,opts){
						var cls = Ext.decode(response.responseText);
						me.columns= cls;
					},
					failure:function(response,opts){
						Ext.Msg.alert("can't",'error');
					}
				});
				
				var fields = null;
				
				Ext.Ajax.request({
					url:'/core/ext/grid/model'+(me.domain!=null?'/sys/'+me.domain:(me.model!=null?"/model/"+me.domain:'/view/'+me.headerModule)),
					async:false,
					dataType: "json",
					success:function(response,opts){
						var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								fields = jsonResult.data;
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
				
				//初始化treeStore
				var store = Ext.create("Ext.data.TreeStore",{
					fields:fields,
					sortRoot:me.sortRoot,
			        proxy:{
						type:'ajax',
						url:'core/ext/grid/tree/'+me.headerModule,
				        reader:'json',
						listeners:{  
					        exception:Ext.ux.DataFactory.exception
					    }
					},
					autoLoad: me.gridLoad,
					root: {  
        				expanded: true,
        				children:[]
					}
				});
				
				if(me.gridLoad){
					store.load();
				}
				
				var toolbar = Ext.widget('toolbar',{
				
				});
				
				if(me.uxapi){
					toolbar.add({
		        		xtype : 'buttontransparent',
						glyph : 0xf0c7,
						text : '保存',	
						handler : function(event, toolEl) {
							var a_ = event.ownerCt.ownerCt.getChecked();
			        		var role = [];
			        		
			        		Ext.Array.each(a_,function(item){
			        			role.push(item.data.id);
			        		});
			        		
			        		Ext.Ajax.request({
								url:me.uxapi.tbarSave,
								async:false,
								params:{id:me.uxapi.recordId,child:role},
								method:'POST',
								dataType: "json",
								success:function(response,opts){
									var jsonResult = Ext.decode(response.responseText);
									if(jsonResult.success){
										Ext.Msg.show({title:"操作提示:",icon:Ext.Msg.INFO,msg:"操作成功!",buttons:Ext.Msg.OK});
									}else{
										Ext.Msg.show({
											title:"错误提示["+jsonResult.errorCode+"]:",
											icon:Ext.Msg.ERROR,
											msg:jsonResult.errorMsg,
											buttons:Ext.Msg.OK
										});
									}
		                    		
								},
								failure:function(response,opts){
									var jsonResult = Ext.decode(response.responseText);
									Ext.Msg.show({
											title:"错误提示["+jsonResult.errorCode+"]:",
											icon:Ext.Msg.ERROR,
											msg:jsonResult.errorMsg,
											buttons:Ext.Msg.OK
										});
								}
							});
						}
					});
					
					store.on('load',function(store, node, records, successful, eOpts ){
						Ext.Ajax.request({
							url:me.uxapi.selectionModel,
							async:false,
							method:'POST',
							success:function(response,opts){
								var jsonResult = Ext.decode(response.responseText);
								if(jsonResult.success){
									Ext.Array.each(jsonResult.data,function(item){
										var node = store.getNodeById(item.id);
										node.set('checked', true);
									});
									
									me.expandAll();
								}
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					});
				}else{
					store.on('load',function(store, node, records, successful, eOpts ){
						//me.expandAll();
					});
				}
				
				toolbar.add([{
					xtype : 'buttontransparent',
					text: '展开',
					glyph : 0xf07c,
					handler: function(event, toolEl){
						event.ownerCt.ownerCt.expandAll();
					}
				},{
					xtype : 'buttontransparent',
					text: '收拢',
					glyph : 0xf07b,
					handler: function(event, toolEl){
						event.ownerCt.ownerCt.collapseAll();   
					}
				}]);
				
				me.tbar= toolbar;
				
				me.store=store;
				me.callParent(arguments);
			}
		});