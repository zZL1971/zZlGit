Ext.define('SRM.controller.trie.TrieTreeController', {
    extend: 'Ext.app.Controller',
    views:['trie.MainLayout',"trie.Tree","trie.Grid","trie.GridDictForm"],
    stores:['trie.TreeStore','trie.GridStore'],
    loadMask:null,
    refs:[{
    	ref : 'trieTree',
		selector : 'trieTree'
    },{
    	ref : 'trieDictWin',
		selector : 'trieDictWin'
    },{
    	ref : 'trieTrieWin',
		selector : 'trieTrieWin'
    },{
    	ref : 'trieGrid',
		selector : 'trieGrid'
    },{
    	ref : 'quickForm',
		selector : 'quickForm'
    },{
    	
    	ref : 'sorttbar',
		selector : 'sorttbar'
    }],
    menu:null,
    init:function(){
    	var me = this;
		me.control({
			'quickForm textfield': {
                specialkey: me.handleSpecialKey
            },
            'trieTree tool[type=plus]':{
            	click:function(a){
            		Ext.create('SRM.view.trie.TrieForm',{}).show(this);
            	}
            },
			'trieTree':{
				itemclick:function(tree,record,item,index,e,options){
					me.getTrieGrid().getStore().loadPage(1,{params: {ICEQtrieTree__id:record.get('id')} ,callback:function(records, operation, success){
						me.getQuickForm().getForm().findField('orderBy').setValue(operation.resultSet.total+1);
					}});
					me.getQuickForm().getForm().findField('trieTree.id').setValue(record.get('id'));
					//me.getQuickForm().query("treepicker")[0].getStore().reload();
					//Ext.data.StoreManager.get("trieTreeStore").reload();
				},
				'itemcontextmenu' : function(view, record, item, index, e,eOpts) {
					this.getMenu(view,record,item,e);
				}
			},
			'trieDictWin button[id=dictFormSubmit]':{
				click:function(a){
					//this.getTrieTree().setTitle("111");
					//this.getTrieGrid().setTitle("222");
					//this.getTrieDictForm().setTitle("333");
					//alert(this.getTrieTreeSelectionStartId());
					var win = this.getTrieDictWin();
					var form = win.getComponent("trieDictForm");
					var selectedTreeId = this.getTrieTreeSelectionStartId();
					var store = this.getTrieGridStore();
            
					form.submit({
						waitMsg : '请稍候',
						waitTitle : '正在保存数据...',
						url : '/core/dd/save',
						success : function(form, action) {
							store.load({params: {ICEQtrieTree__id:selectedTreeId} });
							win.close();
						},
						failure : function(form, action) {
							
						}
					});
				}				
			},
			'trieTrieWin button[id=trieFormSubmit]':{
				click:function(a){
					var win = this.getTrieTrieWin();
					var form = win.getComponent("trieTrieForm");
					var store = this.getTrieTree();
					form.submit({
						waitMsg : '请稍候',
						waitTitle : '正在保存数据...',
						url : '/core/trie/save',
						success : function(form, action) {
							if(action.result.success){
								store.getStore().reload();
								
								me.getQuickForm().query("treepicker")[0].getStore().reload();
								Ext.data.StoreManager.get("trieTreeStore").reload();
								//TODO DD
								win.close();
							}else{
								alert(action.result.errormsg);
							}
							
						},
						failure : function(form, action) {
							if(action.result.errorMsg.indexOf('.UK_')!=-1){
								Ext.Msg.show({
									title:"错误代码:"+action.result.errorCode,
									icon:Ext.Msg.ERROR,
									msg:"字典编码已存在",
									buttons:Ext.Msg.OK
								});
							}
						}
					});
				}				
			},
			'trieGrid':{
				columnresize: me.syncTaskFormFieldWidth
				,itemEditButtonClick:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					Ext.create('SRM.view.trie.DictForm',{dictId:record.getId()}).show(grid);
				}
			},
			'trieGrid button[id=sort]':{
				click:function(a){
					var me = this;
					var grid =me.getTrieGrid();
					var a_ = grid.getComponent('sorttbar');
					a_.isHidden()?a_.show():a_.hide();
				}
			},
			'trieGrid button[id=demo2]':{//表格下拉单元格不给编辑
				click:function(a){
					var me = this;
					var grid =me.getTrieGrid();
					grid.down('#trieTreeId').getEditor().disable();
				}
			},
			'trieGrid button[id=demo3]':{//表格下拉单元格不给编辑
				click:function(a){
					Ext.create('Ext.ux.window.FlowWindow',{uuid:'textfield[name=keyVal]'}).show();
					//var flowId = Ext.ux.DataFactory.getFlowActivityId('UoFHMKk2capcUrccgoSBK2');
					//alert(flowId);
				}
			},
			'trieGrid button[id=demo]':{
				click:function(a){
					
					var data = Ext.encode({sshs:[{key:"searson",value:"agio_order_season"},{key:"collection",value:"agio_collection"}]});
					
					/*Ext.Ajax.request({
						url:'/core/dd/list2/',
						jsonData:data,
						method:'POST',
						dataType: "json",
						contentType: "application/json",
						success:function(response,opts){
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								
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
					});*/
					
					
					Ext.Ajax.request({
						url:'/core/dd/list3/agio_order_season',
						method:'GET',
						dataType: "json",
						contentType: "application/json",
						success:function(response,opts){
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success){
								
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
			},
			'trieGrid button[id=simpledit]':{
				click:function(a){
					var me = this;
					var a_ = me.getQuickForm();
					a_.isHidden()?a_.show():a_.hide();
				}
			},
			'trieGrid checkbox[id=simpleditchk]':{
				change:function(a){
					var me = this;
					var a_ = me.getQuickForm();
					var b_= a_.getComponent('quick_status');
					if(b_.hasCls('quick-from-new')){
						b_.removeCls('quick-from-new').addCls('quick-from-search');
					}else{
						b_.removeCls('quick-from-search').addCls('quick-from-new');
					}
				}
			},
			'trieGrid button[id=delete]':{
				click:function(a){
					//var grid = a.ownerCt.ownerCt;
					//var store = grid.store;
					//var tree = a.ownerCt.ownerCt.ownerCt.items.items[0];
					//var selectedTrees = this.getTrieTree().getSelectionModel().getSelection();
					//var selectedTreeId = selectedTrees.length>0?selectedTrees[0].getId():"";
					
					var selectedTreeId = this.getTrieTreeSelectionStartId();
					var store = this.getTrieGridStore();
					var selectedRows = this.getTrieGrid().getSelectionModel().getSelection();
					
					var ids = [];
					for(var i = 0; i < selectedRows.length ; i++){
						ids[i]=selectedRows[i].getId();
					}
					
					if(ids.length==0){
						return;
					}
					
					this.showLoadMask();
					Ext.Ajax.request({
						url:'/core/dd/del',
						async:false,
						params:{ids:ids},
						method:'POST',
						dataType: "json",
						success:function(response,opts){
							store.load({params: {ICEQtrieTree__id:selectedTreeId} });
						},
						failure:function(response,opts){
							Ext.Msg.alert("can't",'error');
						}
					});
					
					this.hideLoadMask();
					
				}
			},
			'trieGrid button[id=add]':{
				click:function(a){
					//var grid = a.ownerCt.ownerCt;
					//var tree = a.ownerCt.ownerCt.ownerCt.items.items[0];
					
					var selectedTreeId = this.getTrieTreeSelectionStartId();
					if(selectedTreeId.length>0){
						var model = Ext.create("SRM.model.trie.GridModel");
						model.set('trieTree.id',selectedTreeId);
						this.getTrieGridStore().insert(0, model);
					}else{
						Ext.Msg.alert("提示","请选择数据字典索引");
					}
					
				}
			},
			'trieGrid button[id=save]':{
				click:function(a){
					//var grid = a.ownerCt.ownerCt;
					//var tree = a.ownerCt.ownerCt.ownerCt.items.items[0];
					//var selectedTrees = tree.getSelectionModel().getSelection();
					//var selectedTreeId = selectedTrees.length>0?selectedTrees[0].getId():"";
					var selectedTreeId = this.getTrieTreeSelectionStartId();
					var store = this.getTrieGridStore();
					var modifyRecords = store.getUpdatedRecords();
					var recordsLen = modifyRecords.length;
					
					var newRecords = store.getNewRecords();
					
					var dataArr = [];
					for(var i = 0; i < recordsLen ; i++){
						var record = modifyRecords[i].data;
						var json = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json[keys[0]] = {id:value};
							}else{
								json[key] = value;
							}
							
						});
						dataArr[i] = json;
					}
					
					for(var i = 0; i < newRecords.length ; i++){
						var record = newRecords[i].data;
						var json = {};
						Ext.Object.each(record, function(key, value, myself) {
							if(key.indexOf(".")!=-1){
								var keys = key.split(".");
								json[keys[0]] = {id:value};
							}else{
								json[key] = value;
							}
							
						});
						dataArr[recordsLen+i] = json;
					}
					
					var data = Ext.encode({dicts:dataArr,trie:{"keyVal":"ddd"}});
					
					if(dataArr.length==0){
						Ext.Msg.alert("提示",'你在跟我开玩笑吗？');
						return;
					}
					
					
			
			/*$.ajax({
		        url:'/core/dd/save3',
		        type : "POST",
		        datatype:"json",
                contentType: "application/json; charset=utf-8",
		        data : $.toJSON(dataArr),
		        success : function(data, stats) {
		            if (stats == "success") {
		              //   window.location.href="/yc"
		            }
		        },
		        error : function(data) {
		            alert("请求失败");
		        }
		    });
					
					return;*/
					this.showLoadMask();
					Ext.Ajax.request({
						url:'/core/dd/save2',
						//params:{language:"zh_CN"},
						jsonData:data,
						method:'POST',
						dataType: "json",
						contentType: "application/json",
						success:function(response,opts){
							var jsonResult = Ext.decode(response.responseText);
							if(jsonResult.success)
								store.load({params: {ICEQtrieTree__id:selectedTreeId}});
							else{
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
					
					this.hideLoadMask();
				}
			}
		});
	},
	getMenu:function(view,record,item,e){
    	// 禁用浏览器的右键相应事件
		e.preventDefault();
		e.stopEvent();
		var me = this;
		//var grid = this.getTrieGrid();
		
    	//if(this.menu!=null){
    	//	this.menu.showAt(e.getXY());
    	//}else{
		
		//return;
			this.menu = new Ext.menu.Menu({
				// 控制右键菜单位置
				float : true,
				items : [{
					text : "添加子节点",
					icon : 'resources/images/add.png',
					handler : function() {
						Ext.create('SRM.view.trie.TrieForm',{pid:record.getId(),orderby:record.childNodes.length+1}).show(this);
					}
				}, {
					text : "修改当前节点",
					icon : 'resources/images/look.png',
					hidden:record.getId()=="system"?true:false,
					handler : function() {
						Ext.create('SRM.view.trie.TrieForm',{trieId:record.getId()}).show(this);
					}
				}, {
					text : "删除当前节点",
					hidden:record.raw.leaf==true?false:true,
					icon : 'resources/images/delete.png',
					handler : function() {
						if(confirm("确定删除当前节点吗?"))
						Ext.Ajax.request({
							url:'/core/trie/delete',
							async:false,
							params:{trieid:record.getId()},
							success:function(response,opts){
								var jsonResult = Ext.decode(response.responseText);
								if(jsonResult.success){
									me.getTrieTree().getStore().reload();
									me.getTrieGrid().getStore().loadPage(1,{params: {ICEQtrieTree__id:record.parentNode.raw.id} ,callback:function(records, operation, success){
										me.getQuickForm().getForm().findField('orderBy').setValue(operation.resultSet.total+1);
									}});
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
				}/*, {
					text : "查看",
					icon : 'resources/images/look.png',
					handler : function() {
						
					}
				}*/]
			}).showAt(e.getXY());// 让右键菜单跟随鼠标位置
    	//}
    },
    handleSpecialKey: function(field, e) {
    	 var me = this,
            grid =me.getTrieGrid(),
			chkedItem = grid.down('#simpleditchk');
        if(e.getKey() === e.ENTER) {
        	if(chkedItem.getValue()){
        		this.queryQuickFrom();
        	}else{
        		this.newQuickForm();
        	}
            
        }
    },
    queryQuickFrom:function(){
    	var me = this,
    		store = me.getTrieGridStore(),
    		selectedTreeId = me.getTrieTreeSelectionStartId(),
    		form = me.getQuickForm(),
    		basicForm = form.getForm();
    		
    	var dd = Ext.create('SRM.model.trie.GridModel');
		basicForm.updateRecord(dd);
    		
    	store.load({params:dd.getData()});
    },
    newQuickForm: function() {
        var me = this,
            form = me.getQuickForm(),
            basicForm = form.getForm(),
            formEl = form.getEl(),
            titleField = form.getForm().findField('keyVal'),
            selectedTreeId = this.getTrieTreeSelectionStartId(),
            store = this.getTrieGridStore();
            grid = this.getTrieGrid();
            
        if(!titleField.getValue()) {
            return;
        }
        
        form.items.each(function(item) {
            var inputEl = item.getEl().down('input')
            if(inputEl) {
                inputEl.blur();
            }
        });
        
        if(form.isValid()){
	        formEl.mask('saving . . .');
	        
	        form.submit({
				url : '/core/dd/save',
				success : function(f, action) {
					
					
					form.getForm().findField('id').setValue(action.result.msg);
					
					var dd = Ext.create('SRM.model.trie.GridModel');
					basicForm.updateRecord(dd);
					//store.insert(0,dd);
					form.getForm().reset();
					store.load({params: {ICEQtrieTree__id:selectedTreeId} ,callback:function(records, operation, success) {
						form.getForm().findField('orderBy').setValue(operation.resultSet.total+1);
					}});
	                
					form.getForm().findField('trieTree.id').setValue(selectedTreeId);
	                formEl.unmask();
	                titleField.focus();
					
					
	                
					//store.load({params: {id:selectedTreeId} });
				},
				failure : function(form, action) {
					Ext.Msg.show({
						title:"错误代码:"+action.result.errorCode,
						icon:Ext.Msg.ERROR,
						msg:action.result.errorMsg,
						buttons:Ext.Msg.OK,
						fn:function(){
							selectText();
							titleField.focus();
						}
					});
					formEl.unmask();
				}
			});
        }else{
        	var a = form.hasInvalidField();
        	
        	alert(a);
        }
		
        
        
        /*dictForm.save({
        	success:function(f, operation) {
        		titleField.reset();
                titleField.focus();
                formEl.unmask();
        	},
        	failure:function(task, operation) {
        		var error = operation.getError(),
                    msg = Ext.isObject(error) ? error.status + ' ' + error.statusText : error;

                Ext.MessageBox.show({
                    title: 'Add Task Failed',
                    msg: msg,
                    icon: Ext.Msg.ERROR,
                    buttons: Ext.Msg.OK
                });
                formEl.unmask();
        	}
        });*/
        
    },
    syncTaskFormFieldWidth: function(headerContainer, column, width) {
        var field = this.getQuickForm().query('[name=' + column.dataIndex + ']')[0];
        if (field) {
            field.setWidth(width - 0);
        }
    },
	getTrieGridStore:function(){
		return this.getTrieGrid().getStore();
	},
	getTrieTreeSelection:function(){
		return this.getTrieTree().getSelectionModel().getSelection();
	},
	getTrieTreeSelectionStartId:function(){
		var selectedTrees = this.getTrieTreeSelection();
		var selectedTreeId = selectedTrees.length>0?selectedTrees[0].getId():"";
		return selectedTreeId;
	},
	showLoadMask:function(){
		if(this.loadMask==null){
			this.loadMask = new Ext.LoadMask(Ext.getBody(), {msg : '系统正在执行您的操作，请稍等...',removeMask : true});
		}
		
		this.loadMask.show();
	},
	hideLoadMask:function(){
		if(this.loadMask!=null){
			this.loadMask.hide();
		}
	}
});