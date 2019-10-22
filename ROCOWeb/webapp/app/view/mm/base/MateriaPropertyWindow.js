Ext.define("SMSWeb.view.mm.base.MateriaPropertyWindow", {
			extend : 'Ext.window.Window',
			alias : 'widget.MateriaPropertyWindow',
			jsonResult:null,
			formId:null,
			propertyDesc:null,
			propertyItem:null,
			status:null,
			maximizable:true,
			height : 500,
			width : document.body.clientWidth * 0.75,
			modal:true,
			constrainHeader: true,
			closable : true,
			layout:'border',
			tbar : [{
				xtype : 'button',
				text : '保存',
				itemId : 'saveMateriaProperty',
				iconCls : 'table_save'
			}],
			initComponent : function() {
				var me = this;
				var tabpanel = Ext.widget('tabpanel',{
					region:'center'
				});
				/*动态获取配置属性 start*/
				var jsonResult = me.jsonResult;
				var gridcolumns = [];
				var gridfields = [];
				var _propertyDesc = [];//保存字段关系
				var _propertyItem=[] ;//已存在的价格				
				
				if(me.propertyDesc!=null && me.propertyDesc.length>0){
					Ext.Ajax.request({
						url : 'main/mm/queryMaterialPropertyItem',
						method : 'GET',
						params : {
							'pid' : me.formId
						},
						async:false,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							_propertyItem = Ext.decode(response.responseText);  
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示","加载数据失败！");
						}
					});
					
					var _propertyDesc1 = me.propertyDesc;
					//PC info0:PC,info1:CL
					var _propertyDescArray = _propertyDesc1.split(",");
					
					var coll = new Ext.util.MixedCollection();
					for(var i=0;i<_propertyDescArray.length;i++){
						var _descArr = _propertyDescArray[i];
						var _infoArr = _descArr.split(":");
						coll.add(_infoArr[1],_infoArr[0]);
					}
					
					for(var i=0;i<jsonResult.length;i++){
						var id = jsonResult[i].code;
						var name = jsonResult[i].name;
						var items = jsonResult[i].items;
						var itemsValue = [];
//						var _info = "info"+i;
						var _info = coll.getByKey(id);
						
						for(var j=0;j<items.length;j++){
							itemsValue.push(items[j]);
						}
						
						
					    var column = {text:name+"id",dataIndex:_info,sortable: false,menuDisabled:true,hidden:true};//
					    var column_desc = {text:name,dataIndex:_info+"desc",sortable: false,menuDisabled:true};
						gridcolumns.push(column);
						gridcolumns.push(column_desc);
						var field = {name: _info,  type: 'string'};
						var field_desc = {name: _info+"desc",  type: 'string'};
						gridfields.push(field);
						gridfields.push(field_desc);
						
						var grid = Ext.widget('grid',{
							border:false,
							title: name,
			            	itemId: _info,
			            	store : Ext.create("Ext.data.Store",{
						    	fields:[
						    	        {name: 'id', type: 'string'},
						    	        {name: 'name',  type: 'string'}
						    	        ],
						        data:itemsValue
					    	}),
					    	selModel:{selType:'checkboxmodel',injectCheckbox:0},
					    	columns : [
					    	           {text:id,dataIndex:'id',width:200,sortable: false,menuDisabled:true,hidden:true},//
					    	           {text:name,dataIndex:'name',width:200,sortable: false,menuDisabled:true}
					    	           ]
			            });
						tabpanel.add(grid);
						tabpanel.setActiveTab(grid);
					}
					/*动态创建grid end*/
					me.propertyItem = _propertyItem;
					
					gridfields.push({name: 'price'});
					gridfields.push({name: 'num'});
//					gridfields.push({name: 'id'});
					
					gridcolumns.push({text:'价格',dataIndex:'price',width:100,editor:{
							    			xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0
							                },sortable: false,menuDisabled:true
									});
					gridcolumns.push({text:'数量',dataIndex:'num',width:100,editor:{
							    			xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0
							                },sortable: false,menuDisabled:true
									});
					
					var southStore = Ext.create("Ext.data.Store",{
						fields:gridfields,
						data:_propertyItem
			    	});   
					
					var southGrid = Ext.widget('grid',{
						height:"50%",
						border:false,
						itemId:'southGrid',
						region:'south',
						store :southStore,
						columns : gridcolumns,
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
	  	        	  			{
	  	        	  				enableKeyNav : true,
	  	        	  				clicksToEdit : 2
	  	        	  	})]
					});
					
					Ext.apply(me, {
						items : [tabpanel,southGrid]
					});
					
				}else{
					
					for(var i=0;i<jsonResult.length;i++){
						var id = jsonResult[i].code;
						var name = jsonResult[i].name;
						var items = jsonResult[i].items;
						var itemsValue = [];
						for(var j=0;j<items.length;j++){
							itemsValue.push(items[j]);
						}
						var _info = "info"+i;
						_propertyDesc.push(_info+":"+id);
						
					    var column = {text:name+"id",dataIndex:_info,sortable: false,menuDisabled:true,hidden:true};//
					    var column_desc = {text:name,dataIndex:_info+"desc",sortable: false,menuDisabled:true};
						gridcolumns.push(column);
						gridcolumns.push(column_desc);
						var field = {name: _info,  type: 'string'};
						var field_desc = {name: _info+"desc",  type: 'string'};
						gridfields.push(field);
						gridfields.push(field_desc);
						
						var grid = Ext.widget('grid',{
							border:false,
							title: name,
			            	itemId: _info,
			            	store : Ext.create("Ext.data.Store",{
						    	fields:[
						    	        {name: 'id', type: 'string'},
						    	        {name: 'name',  type: 'string'}
						    	        ],
						        data:itemsValue
					    	}),
					    	selModel:{selType:'checkboxmodel',injectCheckbox:0},
					    	columns : [
					    	           {text:id,dataIndex:'id',width:200,sortable: false,menuDisabled:true,hidden:true},//
					    	           {text:name,dataIndex:'name',width:200,sortable: false,menuDisabled:true}
					    	           ]
			            });
						tabpanel.add(grid);
						tabpanel.setActiveTab(grid);
					}
					/*动态创建grid end*/
					me.propertyDesc = _propertyDesc.join(",");
					
					gridfields.push({name: 'price'});
					gridfields.push({name: 'num'});
//					gridfields.push({name: 'id'});
					
					gridcolumns.push({text:'价格',dataIndex:'price',width:100,editor:{
							    			xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0
							                },sortable: false,menuDisabled:true
									});
					gridcolumns.push({text:'数量',dataIndex:'num',width:100,editor:{
							    			xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0
							                },sortable: false,menuDisabled:true
									});
					me.propertyItem = _propertyItem;
					
					var southStore = Ext.create("Ext.data.Store",{
						fields:gridfields,
						data:_propertyItem
			    	});   
					
					var southGrid = Ext.widget('grid',{
						height:"50%",
						border:false,
						itemId:'southGrid',
						region:'south',
						store :southStore,
						columns : gridcolumns,
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
	  	        	  			{
	  	        	  				enableKeyNav : true,
	  	        	  				clicksToEdit : 2
	  	        	  	})]
					});
					
					Ext.apply(me, {
						items : [tabpanel,southGrid]
					});
				}
				this.callParent(arguments);
			},
		    listeners: {
		    	show:function(){
					var me = this;
					if(me.propertyItem!=null && me.propertyItem.length>0){
						me.status = false;
						var southGrid = me.queryById("southGrid");
						var southStore = southGrid.getStore();
						
						var coll = new Ext.util.MixedCollection();
						
						var southStoreRecords = southStore.getRange(0,southStore.getCount());
						for(var i=0;i<southStoreRecords.length;i++) {
							var _json = southStoreRecords[i].getData();
							for(var _key in _json){  
								
								if ((_key.indexOf('desc')<0)&&(_key.indexOf('info')>-1)){
									var _array = coll.getByKey(_key);
									if(_array){
										var flag = Ext.Array.contains(_array,_json[_key]);
										if(!flag){
											_array.push(_json[_key]);
											coll.add(_key, _array);
										}
									}else{
										_array = new Array();
										_array.push(_json[_key]);
										coll.add(_key, _array);
									}
//									console.log(_array);
								}
							}
						}
						//grid选中项

						var tabpanel = me.down("tabpanel");
						var grids = tabpanel.query('grid');
						for(var k=0;k<grids.length;k++){
							var item = coll.getByKey(grids[k].itemId);
							var store = grids[k].getStore();
							var records = store.getRange(0,store.getCount());
							
							tabpanel.setActiveTab(grids[k]);
							
							for(var j=0;j<item.length;j++) {
								for(var i=0;i<records.length;i++) {
									var record=records[i];
									if(record.get('id')==item[j]) {
										grids[k].getSelectionModel().select(record,true);
									}
								}
							}
						}
						
						me.status = true;
					}else{
						me.status = true;
					}
				}
			}
		});