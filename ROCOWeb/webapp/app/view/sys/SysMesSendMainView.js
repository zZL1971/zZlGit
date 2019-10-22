Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sys.SysMesSendMainView', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.SysMesSendMainView',// 面板的别名
			layout:'border',
			autoScroll:true,
			border:false,
			tbar : [{
						xtype : 'buttontransparent',
						text : '查询',
						itemId : 'query',
						glyph : 0xf002
					},{
						xtype : 'buttontransparent',
						text : '全部标记为已读',
						itemId : 'send',
						glyph : 0xF046
					}],
			initComponent : function() {
				 var me = this;
				 
				 var hasReadCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
							fieldLabel : '是否已读',
							emptyText: '请选择...',
							name:'hasRead',
							dict:'IS_READ'
					});
				 
				 
				 var form = Ext.widget('form',{
		    		   region:'north',
			    	   itemId:'sysMesSendForm',
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
			                    fieldLabel: '标题',
			                    name: 'msgTitle'
			                },
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '发送人',
			                    name: 'createUser'
			                }
			                ,{
			                    xtype:'datefield',
			                    fieldLabel: '发送时间从',
			                    name: 'createTime',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '到',
			                    name: 'createTime2',
			                    format :'Y-m-d'
				            },hasReadCombobox
						]
			    	   
				 });
				 
				var sysMesSendStore = Ext.create("SMSWeb.store.sys.SysMesSendStore");
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.SysMesSendGrid',
						store : sysMesSendStore,
						itemId:'sysMesSendGrid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
//						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						columns : [
									 {xtype:'rownumberer',width:30,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
											icon:'/resources/images/remarks1.png'
											,tooltip:'编辑'
											,handler:function(grid,rowIndex,colIndex){
												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
											}
									 }]},
									 {text:'标题',dataIndex:'msgTitle',width:150,menuDisabled:true},
									 {text:'内容',dataIndex:'msgBody',width:600,menuDisabled:true},
									 {text:'发送人',dataIndex:'sendUser',width:100,menuDisabled:true},
									 {text:'发送时间',dataIndex:'sendTime',width:150,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'是否已读',dataIndex:'hasReaded',width:200,menuDisabled:true,
										 renderer: function(value,metadata,record){  
										 				var hasReadStore = hasReadCombobox.getStore();
										 				var find = hasReadStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;
										           }}
						           ],
						dockedItems:[{
							xtype:'pagingtoolbar',
							store:sysMesSendStore,
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
				/*itemGrid客户子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				
				this.callParent(arguments);
			}
		});
