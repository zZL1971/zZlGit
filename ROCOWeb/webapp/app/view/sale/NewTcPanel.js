Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewTcPanel', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewTcPanel',
			border : false,
			layout:'border',
			formId:null,
			editFlag:null,
			autoScroll:true,
			initComponent : function() {
	
				var sexCombobox = Ext.create('Ext.ux.form.DictCombobox',{dict:'SEX'});
				
				 var me = this;
				 var form = Ext.widget('form',{
					   bodyStyle : "padding-left:10px;padding-top:10px;",
		    		   region:'north',
			    	   itemId:'tcForm',
			    	   bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   autoScroll:true,
					   tbar : [{
							xtype : 'button',
							text : '查询',
							itemId : 'query',
							iconCls:'table_search'
						}],
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;',
							width: 235
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   },
			    	   items : [
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '姓名',
//								allowBlank: false,
			                    name: 'name1'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '联系电话',
//			                    allowBlank: false,
			                    name: 'tel'
							},{
					        	xtype:'dictcombobox',
								fieldLabel : '性别',
								emptyText: '请选择...',
								name:'sex',
								dict:'SEX'
					        },
							{
						        xtype: 'numberfield',
						        anchor: '100%',
						        name: 'age',
						        fieldLabel: '年龄',
						        minValue: 0, //prevents negative numbers
						        maxValue: 200,
						
						        // Remove spinner buttons, and arrow key and mouse wheel listeners
						        hideTrigger: true,
						        keyNavEnabled: false,
						        mouseWheelEnabled: false
						    },{
			                    xtype:'datefield',
			                    fieldLabel: '生日',
			                    name: 'birthday',
			                    format :'Y-m-d'
							},{
			                    xtype:'textfield',
			                    fieldLabel: '身份证号码',
			                    name: 'shenFenHao'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '专卖店编码',
			                    name: 'code'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '专卖店名称',
			                    name: 'name'
							}
						]
			    	   
				 });
				
				var tcStore =  Ext.create("SMSWeb.store.sale.TcStore");
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.TcGrid',
						store : tcStore,
						itemId:'tcGrid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
//						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:30,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
					//				 {text : '编辑',xtype:'actioncolumn',align:'center',id:'modifyMaterial',iconCls:'table_edit',width:40},
									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
											icon:'/resources/images/remarks1.png'
											,tooltip:'编辑'
											,handler:function(grid,rowIndex,colIndex){
//												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
												var record = grid.getStore().getAt(rowIndex);
												var id = record.get('id');
												Ext.create('SMSWeb.view.sale.NewTcWindow',
														     {formId : id,title:'终端客户信息'}).show(this,function(){
														     });
											}
									 }]},
									 
									 {text:'姓名',dataIndex:'name1',width:150,menuDisabled:true},
									 {text:'联系电话',dataIndex:'tel',width:100,menuDisabled:true},
									 {text:'性别',dataIndex:'sex',width:100,menuDisabled:true
							        	 ,editor: sexCombobox
								         ,renderer: function(value,metadata,record){
								 				var find= sexCombobox.store.findRecord("id",value);
							 	            	if(find){
							 	            		return find.get('text');
							 	            	}else{
							 	            		return value;
							 	            	}
								     }},
									 {text:'年龄',dataIndex:'age',width:100,menuDisabled:true},
									 {text:'生日',dataIndex:'birthday',width:100,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d'},//,renderer : Ext.util.Format.dateRenderer('Y-m-d')
									 {text:'身份证号码',dataIndex:'shenFenHao',width:100,menuDisabled:true},
									 {text:'专卖店编码',dataIndex:'code',width:100,menuDisabled:true},
									 {text:'专卖店名称',dataIndex:'name',width:100,menuDisabled:true},
									 
						           ],dockedItems:[{
										xtype:'pagingtoolbar',
										store:tcStore,
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
