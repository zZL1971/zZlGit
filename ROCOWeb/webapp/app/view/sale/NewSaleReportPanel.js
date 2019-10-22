Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleReportPanel', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewSaleReportPanel',
			border : false,
			layout:'border',
			formId:null,
			editFlag:null,
			autoScroll:true,
			initComponent : function() {
	
				 var orderTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '订单类型',
						emptyText: '请选择...',
						name:'orderType',
						dict:'ORDER_TYPE'
				 });
				 
				 var orderHuanJieCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '订单环节',
						emptyText: '请选择...',
						name:'orderHuanJie',
						dict:'ORDER_HUAN_JIE'
				 });
				 
				 var dangQianHuanJieCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '当前环节',
						emptyText: '请选择...',
						name:'dangQianHuanJie',
						dict:'ORDER_HUAN_JIE'
				 });
				 
				 var orderStatusCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '是否取消',
						emptyText: '请选择...',
						name:'orderStatus',
						dict:'YES_NO'
				 });
				 
				 var me = this;
				 var form = Ext.widget('form',{
		    		   region:'north',
		    		   alias : 'widget.SaleReportForm',
			    	   itemId:'saleReportForm',
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
							labelWidth : 100,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;',
							width: 280
					   },
					   layout: {
					        type: 'table',
					        columns: 4
					   },
			    	   items : [
			    		    {
			                    xtype:'hiddenfield',
			                    fieldLabel: 'queryType',
			                    name: 'queryType',
			                    value:queryType
			                },
							{
			                    xtype:'textfield',
			                    fieldLabel: '订单编号',
			                    name: 'orderCode'
			                },
			                orderTypeCombobox
							,{
			                    xtype:'datefield',
			                    fieldLabel: '订单日期从',
			                    name: 'startDate',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '到',
			                    name: 'endDate',
			                    format :'Y-m-d'
				            },{
			                    xtype:'textfield',
			                    fieldLabel: '售达方',
			                    name: 'shouDaFang'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '售达方名称',
			                    name: 'kunnrName1'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '店面电话',
			                    name: 'dianMianTel'
			                },orderStatusCombobox,
			                orderHuanJieCombobox,
			                dangQianHuanJieCombobox,
			                {
			                    xtype:'datefield',
			                    fieldLabel: '环节开始时间',
			                    name: 'startTime',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '至',
			                    name: 'startTime2',
			                    format :'Y-m-d'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: 'SAP编号',
			                    name: 'sapOrderCode'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '环节处理人',
			                    name: 'assignee'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '环节结束时间',
			                    name: 'endTime',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '至',
			                    name: 'endTime2',
			                    format :'Y-m-d'
			                }
						]
			    	   
				 });
				 
				 Ext.define('SaleReportModel', {
					extend:'Ext.data.Model',
					fields:[
						{ name: 'id', type: 'string' },
						{ name: 'saleId', type: 'string' },
						
						{ name: 'orderCode', type: 'string' },
						{ name: 'orderType', type: 'string' },
						{ name: 'orderDate', type: 'date', dateFormat:'Y-m-d'},
						{ name: 'orderStatus', type: 'string' },
						{ name: 'shouDaFang', type: 'string' },
						{ name: 'dianMianTel', type: 'string' },
						
						{ name: 'pOrderCode', type: 'string' },
						{ name: 'sapOrderCode', type: 'string' },
					   	
				        { name: 'name1', type: 'string' },
				        { name: 'tel', type: 'string' },
				        { name:'createUser'},
						{ name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{ name: 'dqActName', type: 'string' },
						{ name: 'actName', type: 'string' },
						{ name: 'assignee', type: 'string' },
						{ name: 'orderStatus', type: 'string' },
						{ name:'startTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{ name:'endTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{ name:'snum',type:'int'},
						{ name:'tymj',type:'float'},
						{ name:'ymss',type:'float'},
						{ name:'ymfs',type:'float'},
						{ name:'processTime',type:'string'},
						{ name:'processTime',type:'string'},
						{ name:'processTime',type:'string'},
						{ name:'kunnrName1'}
					]
				});
				
				var saleReportStore = Ext.create('Ext.data.Store', {
					alias:'widget.SaleReportStore',
					model:'SaleReportModel',
					
					remoteSort:false,
					pageSize:25,
					proxy:{
						type:'ajax',
						url:'main/saleReport/list',
						reader:{
							type:'json',
							root:'content',
							totalProperty :'totalElements'
						},
						writer:{
							type:'json'
						},
						listeners:{  
					        exception:Ext.ux.DataFactory.exception
					    }
					},
					listeners: {
				        beforeload:function(store,operation,epts){
								var formValues = form.getValues();
				//				Ext.Object.each(formValues, function(key, value, myself) {
				//					    console.log(key + ":" + value);
				//				});
								Ext.apply(store.proxy.extraParams, formValues);
				        	}
				        },
					autoLoad:false
				});
				
				/*itemGrid子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.SaleReportGrid',
						store : saleReportStore,
						itemId:'saleReportGrid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						features:[{ftype:'groupingsummary'/*,startCollapsed:true*/,showSummaryRow:true,groupByText:'按当前进行分组',showGroupsText:'显示分组',groupHeaderTpl:"分组【{rows.length}】:{groupValue}"},{ftype: 'summary',dock:'bottom'}],
						style:'border-top:1px solid #C0C0C0;',
//						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:50,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
//									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
//											icon:'/resources/images/remarks1.png'
//											,tooltip:'编辑'
//											,handler:function(grid,rowIndex,colIndex){
//												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
//											}
//									 }]},
									 {text:'订单编号',dataIndex:'orderCode',width:120,summaryType:'count',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 {text:'SAP编号',dataIndex:'sapOrderCode',width:120},
									 {text:'订单类型',dataIndex:'orderType',width:90,
										 renderer: function(value,metadata,record){  
										 				var orderTypeStore = orderTypeCombobox.getStore();
										 				var find = orderTypeStore.findRecord('id',value); 
										 				if(find){
								 	            			return find.get('text');
								 	            		}
										                return value;
										           }},
									 {text:'订单日期',dataIndex:'orderDate',width:90,xtype:'datecolumn',format :'Y-m-d'},//,renderer : Ext.util.Format.dateRenderer('Y-m-d')
									 {text:'售达方',dataIndex:'shouDaFang',width:80},
									 {text:'售达方名称',dataIndex:'kunnrName1',width:80},
									 {text:'店面电话',dataIndex:'dianMianTel',width:110},
									 {text:'客户姓名',dataIndex:'name1',width:100},
									 {text:'联系电话',dataIndex:'tel',width:110},
									 {text:'创建人',dataIndex:'createUser',width:110},
									 {text:'创建时间',dataIndex:'createTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'订单环节',dataIndex:'actName',width:120/*,summaryType:'count'*/},
									 {text:'当前环节',dataIndex:'dqActName',width:120},
									 {text:'订单状态',dataIndex:'orderStatus',width:80},
									 {text:'环节处理人',dataIndex:'assignee',width:120},
									 {text:'环节开始时间',dataIndex:'startTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'环节结束时间',dataIndex:'endTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'环节处理时间',dataIndex:'processTime',width:150,align:'right'},
									 {text:'总行数',dataIndex:'snum',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">{0} </font>', value);}},
									 {text:'投影面积',dataIndex:'tymj',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">{0} </font>', value);}},
									 {text:'移门扇数',dataIndex:'ymss',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">{0} </font>', value);}},
									 {text:'移门方数',dataIndex:'ymfs',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">{0} </font>', value);}},
						           ],
					dockedItems:[{
						xtype:'pagingtoolbar',
						store:saleReportStore,
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
						            {'id':500,'name':500},
						            {'id':1000,'name':1000},
						            {'id':2000,'name':2000},
						            {'id':4000,'name':4000}
						        ]
						    }),
						    displayField:'name',
						    valueField:'id'
						},'条']
					}],
				});
				/*itemGrid子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				this.callParent(arguments);
			}
		});
