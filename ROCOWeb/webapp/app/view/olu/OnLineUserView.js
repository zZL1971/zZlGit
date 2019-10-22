Ext.define('SMSWeb.view.olu.OnLineUserView', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.tm1stview',
	layout : 'border',
	border : false,
	requires : ['Ext.ux.form.SearchForm',"Ext.ux.form.TableComboBox","Ext.ux.form.DisplayPopupField"],
	initComponent:function(){
		var me = this;
		var searchFields,formFields,configFields;
		var xmlName = FLOW_RESOURCE;
		
		//获取查询表单数据
		Ext.Ajax.request({
			url:'/core/ext/grid/search/'+xmlName,
			async:false,
			dataType: "json",
			success:function(response,opts){
				var cls = Ext.decode(response.responseText);
				searchFields= cls;
			},
			failure:function(response,opts){
				Ext.Msg.alert("can't",'error');
			}
		});
		
		//获取配置信息
		Ext.Ajax.request({
			url:'/core/ext/grid/config/'+xmlName,
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
		//创建查询动态表单
		var searchForm = Ext.widget('searchform',{
			region : 'north',
			frame : false,
			boder:false,
			bodyStyle:'border:none;',
			bodyPadding : '5 5 0 5',
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 85,
				labelStyle : 'padding-left:5px;',
				width : 240
			},
			items:searchFields,
			listeners:{
				triggerClick:function(){
					alert(1);
				}
			}
		});
		
		/*var triggers = searchForm.down('triggerfield');
		if(triggers.size>0){
			triggers[0].on('specialkey', function(f, e){
	            if (e.getKey() == e.ENTER) {
	                alert(1);
	            }
	        });
		}*/
		
		var uxgrid = null;
		//根据配置文件获取表格类型
		if(configFields.xtype=="uxgrid"){
			//创建查询动态表格
			uxgrid =  Ext.widget('uxgrid',{
	        	headerModule:xmlName,
	        	domain:'OnLineUserModel',
	        	dataUrl:'/core/online/user/list',
	        	pageToolbar:false,
	        	selModelStatus:true,
				region : 'center',
				listeners:{
					itemEditButtonClick:function(grid,rowIndex,colIndex){
						var record = grid.getStore().getAt(rowIndex);
						//获取行编辑表单
						me.createWindowForm(me,formFields,record.getId(),uxgrid,searchForm,configFields);
					},
					itemEditButtonClick2:function(grid,rowIndex,colIndex){
						var record = grid.getStore().getAt(rowIndex);
						//授权操作
						me.createWindowGrantAuthor(me,record,configFields,xmlName);
					}
				}
			});
			
			//第一次的时候查询数据
			uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
		}else if(configFields.xtype=="uxtreegrid"){
			uxgrid = Ext.widget('uxtreegrid',{
				headerModule:xmlName,
				region : 'center',
				selModelStatus:true,
				sortRoot:configFields.sortRoot?configFields.sortRoot:"",
				listeners:{
					itemEditButtonClick:function(grid,rowIndex,colIndex){
						var record = grid.getStore().getAt(rowIndex);
						//获取行编辑表单
						me.createWindowForm(me,formFields,record.getId(),uxgrid,searchForm,configFields);
					},
					itemEditButtonClick2:function(grid,rowIndex,colIndex){
						var record = grid.getStore().getAt(rowIndex);
						//授权操作
						me.createWindowGrantAuthor(me,record,configFields,xmlName);
					}
				}
			});
			uxgrid.getStore().load({params:searchForm.getSearchs()});
			
		}else{
			if(configFields.xtype){
				Ext.Msg.show({title:"错误提示",icon:Ext.Msg.ERROR,msg:"该模板暂不支持"+configFields.xtype+"类型的列表组件引用!",buttons:Ext.Msg.OK});
			}else{
				Ext.Msg.show({title:"错误提示",icon:Ext.Msg.ERROR,msg:"未定义该模板暂支持configFields.xtype类型的列表组件引用!",buttons:Ext.Msg.OK});
			}
			
		}
		
		//设置网页标题属性
		document.title=configFields.title+"-"+document.title;
		
		var toolbar = Ext.widget('toolbar',{
				items:[{
						xtype : 'buttontransparent',
						text : '查询',
						tooltip : '查询',
						glyph : 0xf002,
						handler:function(){
							if(configFields.xtype=="uxgrid"){
								uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
							}else if(configFields.xtype=="uxtreegrid"){
								uxgrid.getStore().load({params:searchForm.getSearchs()});
							}
						}
					},{
						xtype : 'buttontransparent',
						text : '剔除用户',
						//iconCls : 'table_remove',
						glyph : 0xf00d,
						handler:function(){
							var selectedRows = uxgrid.getSelectionModel().getSelection();
							
							var ids = [];
							for(var i = 0; i < selectedRows.length ; i++){
								ids[i]=selectedRows[i].get('session');
							}
							
							if(ids.length==0){
								Ext.Msg.alert("提示","请选选中需要删除的数据!");
								return;
							}
							
							Ext.Ajax.request({
								url:configFields.base+'delete',
								async:false,
								params:{ids:ids},
								method:'POST',
								dataType: "json",
								success:function(response,opts){
									if(configFields.xtype=="uxgrid"){
										uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
									}else if(configFields.xtype=="uxtreegrid"){
										uxgrid.getStore().load({params:searchForm.getSearchs()});
									}
								},
								failure:function(response,opts){
									Ext.Msg.alert("can't",'error');
								}
							});
						}
					}]
				});
				
		if(CURR_USER_LOGIN_NO=="admin"){
			toolbar.add({
				xtype:'buttontransparent',
				glyph:0xf085,
				text : '更新模板',
				handler:function(){
					window.location="/core/online/user/?cache=up";
				}
			});
		}
		me.items=[{
			layout:'border',
			region : 'center',
			tbar :toolbar ,
			items : [searchForm,uxgrid]
		}];
		
		//获取其他控制器
		if(configFields.controllers){
			var controllersplit = configFields.controllers.split(",");
			Ext.Array.each(controllersplit,function(name){
				//动态加载其他控制器
				Ext.require('SMSWeb.controller.'+name,function(){ 
				        var controller = Ext.create('SMSWeb.controller.'+name);
				        controller.init();                                
				    }
				);
			});
		}
		
		me.callParent(arguments);
	},
	createWindowForm:function(me,fields,uuid,uxgrid,searchForm,configFields){
		var children = Ext.create('Ext.util.MixedCollection');
		//创建表单
		var dataform = Ext.widget('form',{
			bodyPadding : '5 5 0 5',
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 90,
				anchor : '100%',
				flex : 1
			},
			items:fields,
			tbar : [{
				xtype : 'buttontransparent',
				text : '保存',
				glyph : 0xf0c7,
				//iconCls : 'table_save',
				formBind: true,
				handler : function(btn, e) {
					var me = this;
					var params={};
					children.eachKey(function(key,item){
						params[key] = item;
					});
					var data = Ext.encode(params);
					
					me.up('form').submit({
						waitMsg : '请稍候',
						waitTitle : '正在保存数据...',
						url : configFields.base+'save',
						success : function(form, action) {
							var jsonResult = Ext.decode(action.response.responseText);
							if(jsonResult.success){
								if(configFields.xtype=="uxgrid"){
									uxgrid.getStore().loadPage(1,{params:searchForm.getSearchs()});
								}else{
									uxgrid.getStore().load({params:searchForm.getSearchs()});
								}
								
								win.close();
							}else{
								Ext.Msg.show({
									title:"错误提示["+jsonResult.errorCode+"]:",
									icon:Ext.Msg.ERROR,
									msg:jsonResult.errorMsg,
									buttons:Ext.Msg.OK
								});
							}
						},
						failure : function(form, action) {
							var jsonResult = Ext.decode(action.response.responseText);
							Ext.Msg.show({
								title:"错误提示["+jsonResult.errorCode+"]:",
								icon:Ext.Msg.ERROR,
								msg:jsonResult.errorMsg,
								buttons:Ext.Msg.OK
							});
						}
					});
				}
			}]
		});
		
		if(uuid){
			dataform.load({
				url:configFields.base+uuid,
				//method:'POST',
				success:function(f,action){
					var result = Ext.decode(action.response.responseText);
					if(configFields.xtype=="uxtreegrid"){
						f.findField('parent.id').setValue(result.data.pid);
					}
					Ext.Object.each(result.data,function(key, value, myself){
						 //
						//console.log(key +":"+ ":" + value.constructor());
						//alert(key + ":" + value);
						if(value instanceof Array){
							children.add(key,value);
						}
					});
					//alert(0);
						
					//f.findField('icon').setValue(result.data.icon);
                    //Ext.Msg.alert('提示',"加载成功");
                },
                failure:function(form,action){
                	var result = Ext.decode(action.response.responseText);
                    Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
                }
			});
		}
		
		//创建win
		var win = Ext.create('Ext.window.Window',{
			width:500,
			title:configFields.title,
			autoScroll : true,
			modal:true,
			items:[dataform]
		}).show();
	},
	createGantModuleuxGrid:function(configFields,item,record){
		var uxgridModule =  Ext.widget('uxgrid',{
        	headerModule:item.headerModule,
        	selModelStatus:true,
        	gridLoad:true,
        	title:item.title,
        	initSelectionModel:true,//启用默认选中事件
        	initSelectionModelDataUrl:configFields.base+item.module+'/'+record.getId(),//数据初始化选中
        	tbar:[{
        		xtype : 'buttontransparent',
				text : '保存',	
				glyph : 0xf0c7,
				//iconCls : 'table_save',
				handler : function(btn, e) {
					var selectedRows = uxgridModule.getSelectionModel().getSelection();
					
					var ids = [];
					for(var i = 0; i < selectedRows.length ; i++){
						ids[i]=selectedRows[i].getId();
					}
					Ext.Ajax.request({
						url:configFields.base+'/'+item.module+'/save',
						async:false,
						params:{id:record.getId(),child:ids},
						method:'POST',
						dataType: "json",
						success:function(response,opts){
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								Ext.Msg.show({
									title:"操作提示:",
									icon:Ext.Msg.INFO,
									msg:"操作成功!",
									buttons:Ext.Msg.OK
								});
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
        	}]
		});
		
		return uxgridModule;
	},
	createWindowGrantAuthor:function(meheader,record,configFields,xmlName){
		//获取gantModule tab项
		var gantmodule=null;
		Ext.Ajax.request({
			url:'/core/ext/grid/gantmodule/'+xmlName,
			async:false,
			dataType: "json",
			success:function(response,opts){
				var cls = Ext.decode(response.responseText);
				gantmodule= cls;
			},
			failure:function(response,opts){
				Ext.Msg.alert("can't",'error');
			}
		});
		
		var tab = Ext.widget('tabpanel',{
			border:false
		});
		
		Ext.Array.each(gantmodule,function(item){
			if(item.xtype=="uxgrid"){
				tab.add(meheader.createGantModuleuxGrid(configFields,item,record));
			}else if(item.xtype="uxtreegrid"){
				var treeGrid = Ext.create('Ext.ux.grid.UXTreeGrid',{
					headerModule:item.headerModule,
					title:item.title,
					rootVisible:true,
					gridLoad:true,
					uxapi:{
						recordId:record.getId(),
						tbarSave:configFields.base+'/'+item.module+'/save',
						selectionModel:configFields.base+item.module+'/'+record.getId()
					}
				});
				tab.add(treeGrid);
			}else{
				Ext.Msg.alert("提示","该模板暂不支持"+item.xtype+"类型的授权引用!");
			}
		});
		
		tab.setActiveTab(0);
		
		var win = Ext.create('Ext.window.Window',{
			height : 480,
			width : 680,
			closable : true,
			layout : 'fit',
			modal : true,
			title:'授权于【'+record.get(configFields.displayfield)+'】',
			items:[tab]
		}).show();
	}
});
