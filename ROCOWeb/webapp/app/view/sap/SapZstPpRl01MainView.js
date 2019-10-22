Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sap.SapZstPpRl01MainView', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.SapZstPpRl01MainView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			tbar : [{
						xtype : 'button',
						text : '查询',
						itemId : 'query',
						iconCls:'table_search'
					},{
						xtype : 'button',
			            text: '同步SAP日历信息',
			            iconCls: 'table_add',
			            itemId : 'sync'
			        },{
						xtype : 'button',
			            text: '同步SAP吸塑标示',
			            iconCls: 'table_add',
			            itemId : 'syncJsmj'
			        }
					],
			initComponent : function() {
				 var me = this;
				 
				 var form = Ext.widget('form',{
		    		   region:'north',
			    	   itemId:'sapZstPpRl01Form',
			    	   bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   },
			    	   items : [
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '工厂',
			                    name: 'werks'
			                },
			                {
			                    xtype:'textfield',
			                    fieldLabel: '年份',
			                    name: 'zyear'
			                }
			                ,{
			                    xtype:'datefield',
			                    fieldLabel: '日期从',
			                    name: 'date',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '到',
			                    name: 'date2',
			                    format :'Y-m-d'
				            }
						]
			    	   
				 });
				 
				var sapZstPpRl01Store = Ext.create("SMSWeb.store.sap.SapZstPpRl01Store");
				
				/*itemGrid子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						store : sapZstPpRl01Store,
						itemId:'sapZstPpRl01Grid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
//						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						columns : [
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'工厂',dataIndex:'werks',width:100,menuDisabled:true},
									 {text:'周',dataIndex:'week',width:100,menuDisabled:true},
									 {text:'日期',dataIndex:'werksDate',width:100,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d'},
									 {text:'星期几',dataIndex:'libai',width:100,menuDisabled:true},
									 {text:'文本',dataIndex:'text',width:100,menuDisabled:true},
									 {text:'是否为工作日',dataIndex:'work',width:100,menuDisabled:true,
										 renderer: function(value,metadata,record){ 
										 				if(value==''){
										 					return '否';
										 				}else if(value=='X'){
										 					return '是';
										 				}
										                return value;
										           }}
						           ],
						dockedItems:[{
							xtype:'pagingtoolbar',
							store:sapZstPpRl01Store,
							dock:'bottom',
							displayInfo:true,
							displayMsg:"显示 {0} -{1}条，共{2} 条",
							border:false,
							items:['-','每页',{
								xtype:'combobox',
								editable : false,
								width:55,
								listeners:{
									 'render':function(comboBox){ 
									 	var grid = comboBox.ownerCt.ownerCt.items.items[0];
									 	comboBox.setValue(this.store.pageSize);
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
						}]
				});
				/*itemGrid子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				
				this.callParent(arguments);
			}
		});
