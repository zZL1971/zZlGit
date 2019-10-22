//散件界面
Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleContentComplainidWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSaleContentComplainidWindow',
			sourceShow:null,//显示来源
			saleHeadId:null,//销售单主表id
			saleItemId:null,//销售行id
			flowInfo:null,//审核信息
			jdName:null,//订单环节
			//loadStatus从哪个页面进入
			// 2：我的物品 显示非标产品 ,3:订单
			loadStatus:null,
			formId:null,
			matnr:null,
			maximizable:true,
			source:null,
			height : 500,
			shId:null,
			width : document.body.clientWidth *  0.82,
			modal:true,
			constrainHeader: true,
			closable : true,
			border : false,
			layout:'border',
			tbar : [
					{
//						hidden:true,
						xtype : 'button',
						text : '保存',
						itemId : 'saveCom',
						iconCls : 'table_save',
						handler : function() {
	     	        		   this.up('window').fireEvent('saveComplainid');
						}
					}
					],
			initComponent : function() {
//				alert("SJ"+this.loadStatus);
				var me = this;
				var _formId = me.shId;
				var _loadStatus = me.loadStatus;
				var _sourceShow = me.sourceShow;
				var jdName = me.jdName;
				//saleHeadId获取订单审核信息
				if(me.saleHeadId!=null && me.saleHeadId.length>0){
					me.flowInfo = Ext.ux.DataFactory.getFlowActivityId(me.saleHeadId);
				}
				
				 var cczxCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzezx',
					 dict:'CCZX'
				 });
				 var ccbmCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'',
					 dict:'',
				 });
				 var ccbmComstore = Ext.create('Ext.data.Store', {
					 	autoLoad:false,
						fields:["id","text"],
				        /*proxy: {
				            type: 'ajax',
				            url:"/core/dd/list3/CCBM",
				            reader: {
				                type: 'json',
				                root: 'data'
				            }
				        }*/
				    });
				 var cczbComstore = Ext.create('Ext.data.Store',{
					 autoLoad:false,
					 fields:["id","text"],
				 });
				 var cclxComstore = Ext.create('Ext.data.Store',{
					 autoLoad:false,
					 fields:["id","text"],
				 });
					 
				 
				/* var saleForCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'salefor',
					 dict:'SALE_FOR'
				 });*/
				 var cclbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzelb',
					 dict:'CCLB'
				 });
				 var ccwtCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzelb',
					 dict:'CCWT'
				 });
				 
				 var cczbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'cczb',
					 dict:'CCZB'
				 });
				 
				 
				var tabpanel;
				var salePricePanel;
				var headForm;
				var itemGrid;
				
				itemGrid = Ext.widget('grid',{
					border : false,
					title: '投诉内容',
					viewConfig:{
					    enableTextSelection:true
					},
					tbar : [
							{xtype: 'button', text: '添加',iconCls:'table_add',itemId:'add',
		    	        	    handler : function() {
		    	        		   this.up('window').fireEvent('addComplainidClick');
									}
		    	            },
		        	        {xtype: 'button', text: '删除',iconCls:'table_remove',itemId:'delete', 
		        	    	   handler : function() {
		        	    		   this.up('window').fireEvent('deleteComplainidClick');
									}  
		        	      	}
							],
					itemId: 'complainid_ItemId',
					features:[{ftype: 'summary',dock:'bottom'}],
					store : Ext.create("SMSWeb.store.sale.SaleComplaintStore"),
				       selModel:{selType:'checkboxmodel',injectCheckbox:0},
				       columns : [
					    	   	  {text:'id',dataIndex:'id',width:0,hidden:true},
				                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
				                  {text:'行号',dataIndex:'orderCodePosex',width:150,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
				                  {text:'投诉内容',dataIndex:'zztsnr',width:300,sortable: false,menuDisabled:true,editor:{xtype:'textareafield',selectOnFocus:true}},
				                  {text:'产品名称',dataIndex:'cpmc',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
//				                  {text:'颜色',dataIndex:'color',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
//				                  {text:'柜名',dataIndex:'cabinetName',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
				                  /*{text:'产品组',dataIndex:'salefor',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
				                	  editor:{
											 xtype:"dictcombobox",
											 dict:"SALE_FOR",
											 allowBlank : true
											 },	
					                	  renderer: function(value,metadata,record){
								 				var store = saleForCombobox.getStore();
								 				var find = store.findRecord('id',value,0,false,true,true);
								 				var ccbmStore = ccbmCombobox.getStore();
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
				                  },*/
				                  {text:'出错中心',dataIndex:'zzezx',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
				                	  editor:{
											 xtype:"dictcombobox",
											 dict:"CCZX",
											 allowBlank : true,
											 },	
					                	  renderer: function(value,metadata,record){
								 				var store = cczxCombobox.getStore();
								 				var find = store.findRecord('id',value,0,false,true,true);
								 				var ccbmStore = ccbmCombobox.getStore();
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
					                  },
					                  {text:'出错部门',dataIndex:'zzebm',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
					                	  editor:{
												 xtype:"combobox",
							                     store: ccbmComstore,
//							                     id:"ccbmid",
							                     anchor: '100%',
							                     listeners : {
														'change' : function(
																obj,
																newValue,
																oldValue,
																eOpts) {
//															ccbmComstore.load({params:{'ccbm':1}});
														}
													}
												},
						                	  renderer: function(value,metadata,record){
									 				var store = ccbmCombobox.getStore();
									 				var find = store.findRecord('id',value,0,false,true,true); 
									 				if(find){
							 	            			return find.get('text');
							 	            		}
									                return value;  
									           }
						                  },
						          {text:'出错组',dataIndex:'zzccz',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
				                	  editor:{
											 xtype:"combobox",
						                     store: cczbComstore,
//								                     id:"ccbmid",
						                     anchor: '100%',
						                     listeners : {
													'change' : function(
															obj,
															newValue,
															oldValue,
															eOpts) {
//																ccbmComstore.load({params:{'ccbm':1}});
													}
												}
											},
					                	  renderer: function(value,metadata,record){
								 				var store = ccbmCombobox.getStore();
								 				var find = store.findRecord('id',value,0,false,true,true); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
						          },
						          {text:'出错问题',dataIndex:'zzelb',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
						        	  editor:{
											 xtype:"dictcombobox",
											 dict:"CCWT",
											 allowBlank : true,
											 },	
					                	  renderer: function(value,metadata,record){
								 				var store = ccwtCombobox.getStore();
								 				var find = store.findRecord('id',value,0,false,true,true);
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
						          },
						          {text:'问题类型',dataIndex:'zzcclx',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true},
						        	  editor:{
											 xtype:"combobox",
						                     store: cclxComstore,
//								                     id:"ccbmid",
						                     anchor: '100%',
						                     listeners : {
													'change' : function(
															obj,
															newValue,
															oldValue,
															eOpts) {
//																ccbmComstore.load({params:{'ccbm':1}});
													}
												}
											},
					                	  renderer: function(value,metadata,record){
								 				var store = ccbmCombobox.getStore();
								 				var find = store.findRecord('id',value,0,false,true,true); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
						          },
						          {text:'责任人',dataIndex:'duty',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
				                  ],
	                  selType : 'cellmodel',
	                  plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
	                		  {
	                	  			clicksToEdit:1,
	                	  			listeners:{
	                	  				edit:function(edit,e){
	                	  					var ccbm = e.record.get('zzezx')
	                	  					var cczb = e.record.get('zzebm')
	                	  					var cclx = e.record.get('zzelb')
	                	  					/*if(me.ccbm!=ccbm){
	                	  						e.record.set("zzebm","");
	                	  					}
	                	  					 e.record.commit();*/
	                	  					ccbmComstore.setProxy({
	                	  						type: 'ajax',
									            url:"/core/dd/list5/CCBM?ccbm="+ccbm,
									            reader: {
									                type: 'json',
									                root: 'data'
									            }
	                	  					});
	                	  					cczbComstore.setProxy({
	                	  						type: 'ajax',
									            url:"/core/dd/list5/CCZBN?cczb="+cczb,
									            reader: {
									                type: 'json',
									                root: 'data'
									            }
	                	  					});
	                	  					cclxComstore.setProxy({
	                	  						type: 'ajax',
	                	  						url:"/core/dd/list5/WTLX?cclx="+cclx,
	                	  						reader: {
	                	  							type: 'json',
	                	  							root: 'data'
	                	  						}
	                	  					});
	                	  					ccbmComstore.load({params:{'ccbm':ccbm}});
	                	  					cczbComstore.load({params:{'cczb':cczb}});
	                	  					cclxComstore.load({params:{'cclx':cclx}});
	                	  					
	                	  				}
	                	  			}
			                	})] 
				});
				if('2'==_loadStatus){
					tabpanel = Ext.widget('tabpanel',{
						itemId: 'tabpanel_ItemId',
						region:'center',
						items:[itemGrid]
					});
				}else if('3'==_loadStatus){
					if("true"==IS_MONEY){
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							region:'center',
							items:[itemGrid,salePricePanel]
						});
					}else{
						tabpanel = Ext.widget('tabpanel',{
							itemId: 'tabpanel_ItemId',
							region:'center',
							items:[itemGrid]
						});
					}
				};
				Ext.apply(me, {
					items : [tabpanel]
				});
				
				
				//加载数据
				if(me.source=="update"){
					Ext.Ajax.request({
						url : 'main/myGoods/getComplainidDay',
						params : {
							pid : _formId
						},
						method : 'GET',
						frame:true,
						contentType : 'application/json',
						dataType : "json",
						success:function(response, opts){
							var value =  Ext.decode(response.responseText);
							var formData = value[0];
						},
		                failure:function(form,action){
		                	var result = Ext.decode(action.response.responseText);
//		                	headForm.getForm().findField("loadStatus").setValue(_loadStatus);
		                    Ext.Msg.alert('提示',result.errorMsg);
		                }
					});
					
				}
				me.callParent(arguments);
			},
			listeners: {
				show:function(){
					var me = this;
					var shId = me.shId;
					var headForm = me.queryById('headForm_ItemId');
					var itemGrid = me.queryById('complainid_ItemId');
					var _loadStatus = me.loadStatus;
					var jdName = me.jdName;
					if(shId!=null){
						itemGrid.getStore().load({params:{'pid':shId}});
					}else{
					}
					
				},
				close:function(){
					var me = this;
					var _flowInfo = me.flowInfo;
					//审价
					if(me.loadStatus=="3" && "gp_valuation"==_flowInfo.taskGroup && _flowInfo.assignee==true){
						 Ext.getCmp("newSaleItemGridId").getStore().reload();
						 Ext.getCmp("saleItemPriceGridId").getStore().reload();
					}
				}
			},
		});