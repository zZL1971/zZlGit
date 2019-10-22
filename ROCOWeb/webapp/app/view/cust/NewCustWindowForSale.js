Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.cust.NewCustWindowForSale', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewCustWindowForSale',
			formId:null,
			maximizable:true,
			height : 400,
			width : document.body.clientWidth * 0.6,
			modal:true,
			custFlag:null,
			constrainHeader: true,
			iskf:null,
			closable : true,
			border : false,
			layout:'border',
			autoScroll:true,
			tbar : [{
						xtype : 'button',
						text : '查询',
						itemId : 'queryCust',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : '确定',
						id : 'confirmCust'
					}],
			initComponent : function() {
//				var ktokdStore = new Ext.data.Store({
//					    fields: ['keyVal','descZhCn'],
//					    proxy:{
//							type:'ajax',
//							url:'main/sale/queryDicd',
//							extraParams :{'keyVal':'KTOKD'},
//							reader:{
//								type:'json',
//								root:'content',
//								totalProperty :'totalElements'
//							},
//							writer:{
//								type:'json'
//							}
//						},
//					    // API only returns 25 by default.
//					    pageSize: 1000,
//					    autoLoad: true
//				});
//
//				var ktokdCombobox = new Ext.form.ComboBox({
//						 name: "ktokd",
//				         fieldLabel: '类型',
//				//         width: 300,
//				         store: ktokdStore,
//				         displayField: 'descZhCn',
//				         valueField: 'keyVal',
//				         triggerAction: 'all',
//				         emptyText: '请选择...',
//				         allowBlank: false,
//				         blankText: '请选择类型',
//				         editable: false,
//				         queryMode: 'local'
//				//        	 ,
//				//         listConfig: {  
//				//		       getInnerTpl: function() {  
//				//		           return '<div data-qtip="{descZhCn} ({keyVal})">{descZhCn} ({keyVal})</div>';  
//				//		       }  
//				//		  }  
//				});
				
				 var me = this;
				 var form = Ext.widget('form',{
		    		   region:'north',
			    	   itemId:'custForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   autoScroll:true,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;',
							width : 235
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   },
			    	   items : [
			    		   	{
			                    xtype:'hiddenfield',
			                    fieldLabel: 'custFlag',
			                    name: 'custFlag',
			                    value:me.custFlag
			                },
			    		    {
			                    xtype:'textfield',
			                    fieldLabel: '编号',
			                    name: 'kunnr'
			                }, {
			                    xtype:'textfield',
			                    fieldLabel: '名称',
			                    name: 'name1'
			                },{
					        	xtype:'dictcombobox',
								fieldLabel : '类型',
								emptyText: '请选择...',
								name:'ktokd',
								dict:'KTOKD'
					        }, {
			                    xtype:'textfield',
			                    fieldLabel: '电话',
			                    name: 'tel'
				            },{
			                    xtype:'textfield',
			                    fieldLabel: '销售区域',
			                    name: 'bzirk'
			                }, {
			                    xtype:'textfield',
			                    fieldLabel: '信贷额度',
			                    name: 'xinDai'
			                }
//			                ,{
//			                    xtype:'datefield',
//			                    fieldLabel: '开始日期',
//			                    name: 'startDate',
//			                    format :'Y-m-d'
//			                },{
//			                    xtype:'datefield',
//			                    fieldLabel: '结束日期',
//			                    name: 'endDate',
//			                    format :'Y-m-d'
//				            }
						]
			    	   
				 });
				 
				var custStore = Ext.create("SMSWeb.store.cust.CustStore");
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.CustGridView',
						store : custStore,
						itemId:'custGrid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:30,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'pid',dataIndex:'pid',width:0,hidden:true,menuDisabled:true},
//									 {text : '编辑',xtype:'actioncolumn',align:'center',id:'modifyMaterial',iconCls:'table_edit',width:40},
//									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,items:[{
//											icon:'/resources/images/remarks1.png'
//											,tooltip:'编辑'
//											,handler:function(grid,rowIndex,colIndex){
//												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
//											}
//									 }]},
					
									 {text:'编号',dataIndex:'kunnr',width:100,menuDisabled:true},
									 {text:'名称',dataIndex:'name1',width:100,menuDisabled:true},
									 {text:'类型',dataIndex:'ktokd',width:200,menuDisabled:true,
										 renderer: function(value,metadata,record){ 
										 				var ktokdStore = Ext.data.StoreManager.lookup('ktokd_KTOKD');
										 				var find = ktokdStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;  
										           }},
									 {text:'电话',dataIndex:'tel',width:100,menuDisabled:true},
									 {text:'销售区域',dataIndex:'bzirk',width:100,menuDisabled:true},
									 {text:'信贷额度',dataIndex:'xinDai',width:100,menuDisabled:true},
									 {text:'送达方',dataIndex:'kunnrS',width:100,menuDisabled:true},
//						             {text:'开始日期',dataIndex:'startDate',width:100,xtype: 'datecolumn',renderer : Ext.util.Format.dateRenderer('Y-m-d')},
//						             {text:'结束日期',dataIndex:'endDate',width:100,xtype: 'datecolumn',renderer : Ext.util.Format.dateRenderer('Y-m-d')},
						             {text:'折扣',dataIndex:'zheKou',width:100, xtype: 'numbercolumn'}
//									 ,
//						             {text:'总金额',dataIndex:'total',width:100, xtype: 'numbercolumn'},
//						             {text:'剩余金额',dataIndex:'shengYu',width:100, xtype: 'numbercolumn'}
						           ],
           				dockedItems:[{
							xtype:'pagingtoolbar',
							store:custStore,
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
				
				var ktokdStore = Ext.data.StoreManager.lookup('ktokd_KTOKD');
				if(me.custFlag=="songDaFang"){
//					ktokdStore.filter("id",/Z710|Z720/);
					ktokdStore.filter([
					    {filterFn: function(item) { 
					    		if(item.get("id")==null || item.get("id")=="" || item.get("id")=="Z710" || item.get("id")=="Z720"){
					    			return true;
					    		}
					    	}
					    }
					]);
				}else if(me.custFlag=="shouDaFang"){
					ktokdStore.filter([
					    {filterFn: function(item) { 
					    		if("Z710"!=item.get("id") && "Z720"!=item.get("id")){
					    			return true;
					    		}
					    	}
					    }
					]);
				}
				
				this.callParent(arguments);
			}
		});
