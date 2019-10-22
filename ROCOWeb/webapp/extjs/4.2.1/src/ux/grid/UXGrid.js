Ext.define("Ext.ux.grid.UXGrid", {
			extend : 'Ext.grid.Panel',
			alias : 'widget.uxgrid',//别称
			//xtype : 'uxgrid',//声明自定义组件名称
			//store : 'system.user.SysUserStore',//引入数据层
			headerUrl:null,
			headerModule:null,
			moduleData:null,
			domain:null,
			dataUrl:null,
			pOrderCode:null,
			//useArrows : true,
			columnLines: true,
			pageToolbar:false,
			selModelStatus:false,
			cellEdit:false,
			gridLoad:false,
			gridCurd:false,
			isApplyEdit:true,
			gridBuffered:false,
			gridSorters:null,
			gridPageSize:25,
			gridGroupField:null,
			gridSum:null,//grid汇总
			defaultQueryCondition:{},
			searchForm:null,
			formParams:{},
			uniqueRowId:'id',
			dataRoot:"content",
			bodyStyle : "border-left:none;border-right:none;",
			multiSelect:true,
			viewConfig:{
			    enableTextSelection:true //可以复制单元格文字
			},
			/*features: [{
		        ftype : 'groupingsummary',
		        groupHeaderTpl : '{name}',
		        hideGroupedHeader : false,
		        enableGroupingMenu : false
		    }],*/
			setCurd:function(bool){
				var me = this;
				me.cellEdit = bool;
				if(me.gridCurd==true){
					if(bool){
						//获取所有curd操作按钮并设置为隐藏
						me.down('toolbar').show(true);
					}else{
						me.down('toolbar').hide(true);
					}
				}
			},
			urgentTypeRenderer:function(value,metaData,row){
				console.log(value);
			},
			initComponent:function(){
				var me = this;
				var configFields={};
				if(typeof(me.moduleData)==="string"){
					try{
						me.moduleData = Ext.create('SMSWeb.data.'+me.moduleData);
					}catch(e){
						//console.log(e);
						//alert(me.moduleData);
						//console.log(me.moduleData+'对应的配置文件未生成，系统默认从后台再次抓取数据，建议重新生成提升性能!');
						Ext.Ajax.request({
							url:'/core/ext/grid/execModuleData/'+me.moduleData,
							async:false,
							success:function(response,opts){
								//console.log('数据加载完成!');
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					}
				}
				
				var dataModule_ = me.headerModule==null?(typeof(me.moduleData)==="object"?me.moduleData.xmlName:me.moduleData):me.headerModule;
				var dataModuleValids_ = [];
				
				if(me.gridCurd){
					me.selModelStatus = true;
					me.cellEdit =true;
					var toolbar=Ext.widget('toolbar',{});
					if(me.moduleData!=null && typeof(me.moduleData)==="object"){
						configFields = me.moduleData.config;
					}else{
						//获取配置信息
						Ext.Ajax.request({
							url:'/core/ext/grid/config/'+dataModule_,
							async:false,
							dataType: "json",
							success:function(response,opts){
								var cls = Ext.decode(response.responseText);
								configFields= cls.data;
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					}
					
					if(me.moduleData!=null && typeof(me.moduleData)==="object"){
						dataModuleValids_ = me.moduleData.validations;
					}else{
						//获取配置信息
						Ext.Ajax.request({
							url:'/core/ext/grid/vaild/'+dataModule_,
							async:false,
							dataType: "json",
							success:function(response,opts){
								var cls = Ext.decode(response.responseText);
								dataModuleValids_ = cls;
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					}
					
					toolbar.add([{
						xtype : 'buttontransparent',
						text : '查询',
						glyph : 0xf002,
						handler: function(event, toolEl){
							event.ownerCt.ownerCt.getStore().loadPage(1);
						}
					},'-',{
						xtype : 'buttontransparent',
						text : '新增',
						glyph : 0xf067,
						handler: function(event, toolEl){
							var store_ = event.ownerCt.ownerCt.getStore();
							var json = {};
							if(configFields.insertRow && store_.getCount()>0){
								var cells_ = configFields.insertRow.split(",");
								var record_ = store_.getAt(store_.getCount()-1);
								for(var i=0;i<cells_.length;i++){
									json[cells_[i]]=record_.get(cells_[i]);
								}
							}
							event.ownerCt.ownerCt.getStore().insert(0,json);
						}
					},{
						xtype : 'buttontransparent',
						text : '删除',
						glyph : 0xf00d,
						handler: function(event, toolEl){
							var selectedRows = event.ownerCt.ownerCt.getSelectionModel().getSelection();
							var dataArr = [];
							
							if(configFields.updatedesc && selectedRows.length>0){
								for(var i = 0; i < selectedRows.length ; i++){
									var json = {};
									var cells_ = configFields.updatedesc.split(",");
									for(var j=0;j<cells_.length;j++){
										json[cells_[j]]=selectedRows[i].get(cells_[j]);
									}
									dataArr[i] = json;
								}
							}
							
							if(dataArr.length==0){
								Ext.Msg.alert("提示",'请选择需要删除的数据');
								return;
							}
							
							var data = Ext.encode({listmap:dataArr});
							
							Ext.Ajax.request({
								url:'/core/ext/grid/delete/'+dataModule_,
								jsonData:data,
								method:'POST',
								headers:{
									'Content-Type': "application/json;charset=utf-8"
								},
								success:function(response,opts){
									var jsonResult = Ext.decode(response.responseText);
									if(jsonResult.success){
										event.ownerCt.ownerCt.getStore().reload();
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
									Ext.Msg.alert("错误代码:"+response.status,response.responseText);
								}
							});
							
						}
					},{
						xtype : 'buttontransparent',
						text : '保存',
						glyph : 0xf0c7,
						handler: function(event, toolEl){
							var modify = event.ownerCt.ownerCt.getStore().getModifiedRecords();
							var dataArr = [];
							//var validerror=[];
							var errorMsg = "";
							//获取列名
							var columnHeader = Ext.create('Ext.util.MixedCollection');
							Ext.Array.each(event.ownerCt.ownerCt.columns,function(col){
								if(col.dataIndex)
									columnHeader.add(col.dataIndex,col.text);
							});
							
							for(var i = 0; i < modify.length; i++){
								var record = modify[i].data;
								var bool_ = true;
								
								for(var j=0;j<dataModuleValids_.length;j++){
									var regx_ = new RegExp(dataModuleValids_[j].regexp);
									var cao_ = new Boolean(regx_.test(record[dataModuleValids_[j].field]));
									if(cao_.toString()=="false"){
										//errorMsg+= "<font color=red>输入的</font>"+columnHeader.get(dataModuleValids_[j].field)+record[dataModuleValids_[j].field]+":"+dataModuleValids_[j].error+"\t";
										//bool_ = false;
									}
								}
								
								if(errorMsg.length>0){
									errorMsg+="<br/>";
								}
								
								if(bool_){
									dataArr[i] = record;
								}
							}
							if(errorMsg.length>0){
								
								Ext.Msg.show({
									title:"错误提示:",
									maxWidth:1000,
									icon:Ext.Msg.ERROR,
									msg:errorMsg,
									buttons:Ext.Msg.OK
								});
								return;
							}
							
							if(dataArr.length==0){
								Ext.Msg.alert("提示",'请编辑需要修改的数据后，在点击保存按钮进行保存!');
								return;
							}
							
							var data = Ext.encode({listmap:dataArr});
							
							Ext.Ajax.request({
								url:'/core/ext/grid/save/'+dataModule_,
								jsonData:data,
								method:'POST',
								headers:{
									'Content-Type': "application/json;charset=utf-8"
								},
								success:function(response,opts){
									var jsonResult = Ext.decode(response.responseText);
									if(jsonResult.success){
										event.ownerCt.ownerCt.getStore().reload();
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
									Ext.Msg.alert("错误代码:"+response.status,response.responseText);
								}
							});
						}
					},{
						xtype : 'buttontransparent',
						text : '查询孔位条码',
						glyph : 0xf002,
						handler: function(event, toolEl){
							var selectedRows = event.ownerCt.ownerCt.getSelectionModel().getSelection();
							if(selectedRows.length == 0) {
		                        Ext.MessageBox.alert('提示信息','请选择一条明细');
							}else{
								var sid = selectedRows[0].data.id;
								var orderid = selectedRows[0].data.orderid;
/*								function newwin(){
									var win = new Ext.window('SMSWeb.view.sale.NewSaleWindowForHoleInfo',
											{title:'孔位信息查询','sid':sid,'orderid':orderid,'pOrderCode':me.pOrderCode});
									win.show();
								}*/
								Ext.create('SMSWeb.view.sale.NewSaleWindowForHoleInfo',
										{title:'孔位信息查询','sid':sid,'orderid':orderid,'pOrderCode':me.pOrderCode}).show();
								me.ownerCt.close();
								
							}
						}
					}]);
					
					me.tbar=toolbar;
					
				}
				
				if(me.fileupload){
					if(me.tbar){
						me.tbar.add('-',{
							xtype : 'buttontransparent',
							text : '导入Execl',
							glyph : 0xf085,
							handler: function(event, toolEl){
								
								Ext.create('Ext.ux.window.FileWindow',{fileType:'EXECL',formParams:Ext.apply(me.formParams,{moduleName:dataModule_}),listeners:{
									afterCommit:function(){
										me.getStore().reload();
										return true;
									}
								}}).show();
							}
						});
					}else{
						me.tbar = Ext.widget('toolbar',{});
						me.tbar.add({
							xtype : 'buttontransparent',
							text : '导入Execl',
							glyph : 0xf085,
							handler: function(event, toolEl){
								Ext.create('Ext.ux.window.FileWindow',{fileType:'EXECL',formParams:Ext.apply(me.formParams,{moduleName:dataModule_}),listeners:{
									afterCommit:function(){
										me.getStore().reload();
										return true;
									}
								}}).show();
							}
						});
					}
				}
				
				if(me.selModelStatus){
					me.selModel = {
						selType:'checkboxmodel',
						injectCheckbox:1
					};
				}
				
				if(me.gridBuffered){
					me.plugins= ['bufferedrenderer',
				        Ext.create('Ext.grid.plugin.CellEditing', {
				        	pluginId : 'cellEditing',
				            clicksToEdit: 1,
				            listeners: {
							    beforeEdit: function (editor, e) {
							        return me.cellEdit;
			                	}
			             	}
				        }) 	
				    ];
				}else{
					if(me.isApplyEdit){
						me.plugins= [
					        Ext.create('Ext.grid.plugin.CellEditing', {
					        	pluginId : 'cellEditing',
					            clicksToEdit: 1,
					            listeners: {
								    beforeEdit: function (editor, e) {
								        return me.cellEdit;
				                	}
				             	}
					        }) 	
					    ];
					}
				}
				
				if(me.moduleData!=null && typeof(me.moduleData)==="object"){
					me.columns = me.moduleData.columns;
				}else{
					//初始化grid表头数据
					Ext.Ajax.request({
						url:'/core/ext/grid/'+dataModule_,
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
				}
				
				
				//me.columns= Ext.require('/core/ext/grid/'+me.headerModule);
				
				
				
				var fields = null;
				if(me.moduleData!=null && typeof(me.moduleData)==="object"){
					fields = me.moduleData.model;
				}else{
					Ext.Ajax.request({
						url:'/core/ext/grid/model'+(me.domain!=null?'/model/'+me.domain:'/view/'+dataModule_),
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
				}
				
				
				//初始化model
				var newModel = Ext.define(dataModule_,{
					extend : 'Ext.data.Model',
					fields : fields
				});
				
				//初始化store
				var store = Ext.create("Ext.data.Store",{
					model:dataModule_,
					//fields:fields,
					buffered:me.gridBuffered,
					remoteGroup:me.gridBuffered,
					leadingBufferZone: 200,
					//groupField:me.gridGroupField?me.gridGroupField:null,
        			pageSize: me.gridBuffered?100:me.gridPageSize,
			        proxy:{
						type:'ajax',
						//url:'core/user/list',//hibernate查询
						//url:'core/ext/grid/page/'+me.headerModule,//jdbc查询
						url:me.dataUrl!=null?me.dataUrl:'core/ext/grid/page/'+dataModule_,
				        headers:{
							"Content-Type":"application/json; charset=utf-8"        
				        },
						reader:{
							type:'json',
							root:me.dataRoot,
							idProperty:me.uniqueRowId,
							totalProperty :'totalElements'
						},
						listeners:{  
					        exception:Ext.ux.DataFactory.exception
					    }
					},
					listeners:{
						beforeload:function(store,operation, eOpts){
							//alert(store.proxy.extraParams);
							//console.log(store.proxy.extraParams);
							//var searchForm = me.up('panel');
							//alert(searchForm);
							//if(me.searchForm){
							//	Ext.apply(store.proxy.extraParams,me.searchForm.getSearchs());
							//}
							//alert(me.searchForm);
							Ext.apply(store.proxy.extraParams,me.defaultQueryCondition);
							
							//console.log(store.proxy.extraParams);
						}
					},
					autoLoad:me.gridLoad
					//data:[{"groupId":"gp_drawing","orderType":"OR1","orderCode":"TEST150014","assignee":"","uuid":"3Kvc3q16eWyxskqtLanxcy"},{"groupId":"gp_drawing","orderType":"OR1","orderCode":"TEST150018","assignee":"admin","uuid":"UoFHMKk2capcUrccgoSBK2"}]
				});
				
				if(me.gridSorters){
					store.sort(me.gridSorters);
				}
				if(me.gridGroupField){
					//store.groupField = me.gridGroupField;
					me.features=[{ftype:'groupingsummary'/*,startCollapsed:true*/,showSummaryRow:true,groupByText:'按当前进行分组',showGroupsText:'显示分组',groupHeaderTpl:"分组【{rows.length}】:{groupValue}"},{ftype: 'summary',dock:'bottom'}]

//					me.features=[{ftype:'groupingsummary',groupHeaderTpl:"{columnName}: {groupValue}"}];
				}else if(me.gridSum){
					me.features=[{
						ftype:'summary',
						dock:'bottom'
					}]
				}
				
				if(me.initSelectionModel){
					store.on('load',function(me1,records, successful, eOpts ){
						Ext.Ajax.request({
							url:me.initSelectionModelDataUrl,
							async:false,
							method:'POST',
							dataType: "json",
							success:function(response,opts){
								var jsonResult = Ext.decode(response.responseText);
								var rds = [];
								if(jsonResult.success){
									Ext.Array.each(jsonResult.data,function(item){
										rds.push(new Ext.create(dataModule_,item));
									});
									
									me.getSelectionModel().select(rds,true,false);
								}
							},
							failure:function(response,opts){
								Ext.Msg.alert("can't",'error');
							}
						});
					});
				}
				
				me.store=store;
				
				if(me.pageToolbar){
					me.dockedItems=[{
						xtype:'pagingtoolbar',
						store:store,
						dock:'bottom',
						displayInfo:true,
						displayMsg:"显示 {0} -{1}条，共{2} 条"+(me.statusBar?"【"+me.statusBar+"】":""),
						border:false,
						items:['-','每页',{
							xtype:'combobox',
							editable : false,
							width:55,
							listeners:{
								 'render':function(comboBox){ 
								 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
								 	comboBox.setValue(me.store.pageSize);
								 },
							  	 'select':function(comboBox){ 
							  	 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
							  	 	grid.getStore().pageSize = comboBox.getValue();
							  	 	grid.getStore().load({params:{start:0,limit:comboBox.getValue()}});
							  	 }
							},
							store:Ext.create('Ext.data.Store',{
						        fields:['id','name'],
						        data:
						        [
						            {'id':25,'name':25},
						            {'id':50,'name':50},
						            {'id':100,'name':100},
						            {'id':200,'name':200},
						            {'id':500,'name':500}
						        ]
						    }),
						    displayField:'name',
						    valueField:'id'
						},'条']
					}];
				}
				
					
				me.callParent(arguments);
			}
		});