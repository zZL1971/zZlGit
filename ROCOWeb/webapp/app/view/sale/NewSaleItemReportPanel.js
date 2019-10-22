Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleItemReportPanel', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewSaleItemReportPanel',
			border : false,
			layout:'border',
			formId:null,
			editFlag:null,
			autoScroll:true,
			initComponent : function() {
	
//				 var materialMtartCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
//						fieldLabel : '产品类型',
//						emptyText: '请选择...',
//						name:'materialMtart',
//						dict:'MATERIAL_MTART'
//				 });
				 
//				 var isStandardCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
//						fieldLabel : '是否标准产品',
//						emptyText: '请选择...',
//						name:'isStandard',
//						dict:'MATERIAL_IS_STANDARD'
//				 });
				 
				 var matnrHuanJieCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '商品环节',
						emptyText: '请选择...',
						name:'matnrHuanJie',
						dict:'MATNR_HUAN_JIE'
				 });
				 
				 var dangQianHuanJieCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '当前环节',
						emptyText: '请选择...',
						name:'dangQianHuanJie',
						dict:'MATNR_HUAN_JIE'
				 });
				 
				 var stateAuditCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '是否取消',
						emptyText: '请选择...',
						name:'stateAudit',
						dict:'YES_NO'
				 });
				 var drawType = Ext.create("Ext.ux.form.DictCombobox",{
					 dict:'MATERIAL_DRAW_TYPE'
				 });
				 var me = this;
				 var form = Ext.widget('form',{
		    		   region:'north',
		    		   alias : 'widget.SaleItemReportForm',
			    	   itemId:'saleItemReportForm',
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
			                {
			                	xtype:'textfield',
			                	fieldLabel: 'SAP编号',
			                	name: 'sapCode'
			                },
							{
			                    xtype:'textfield',
			                    fieldLabel: '产品编号',
			                    name: 'matnr'
			                },
//			                materialMtartCombobox,
//			                isStandardCombobox,
			                matnrHuanJieCombobox,
			                dangQianHuanJieCombobox
			                ,{
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
			                    xtype:'datefield',
			                    fieldLabel: '环节结束时间',
			                    name: 'endTime',
			                    format :'Y-m-d'
			                },{
			                    xtype:'datefield',
			                    fieldLabel: '至',
			                    name: 'endTime2',
			                    format :'Y-m-d'
			                },
			                {
			                    xtype:'textfield',
			                    fieldLabel: '环节处理人',
			                    name: 'assignee'
			                },
			                {
			                    xtype:'textfield',
			                    fieldLabel: '产品描述',
			                    name: 'maktx'
			                },stateAuditCombobox
						]
			    	   
				 });
				 
				 Ext.define('SaleItemReportModel', {
					extend:'Ext.data.Model',
					fields:[
						{name:'id'},
						{name:'createUser'},
						{name:'updateUser'},
						{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{name:'updateTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{name:'posex',type:'int'},
						{ name: 'matnr', type: 'string' },
						{ name: 'mtart', type: 'string' },
						{ name: 'maktx', type: 'string' },
				        { name: 'amount', type: 'float' },
				        { name: 'touYingArea', type: 'float' },
				        { name: 'totalPrice', type: 'float' },
				        { name: 'status', type: 'string' },
				        { name: 'materialHeadId', type: 'string' },
				        { name: 'isStandard', type: 'string' },
				        { name: 'materialPropertyItemInfo', type: 'string' },
				        { name: 'materialPrice', type: 'float' },
				        { name: 'myGoodsId', type: 'string' },
				        { name: 'sanjianHeadId', type: 'string' },
				        { name: 'bujianId', type: 'string' },
				        { name: 'ortype', type: 'string' },
				        { name: 'orderCodePosex', type: 'string' },
				        { name: 'stateAudit', type: 'string' },
				        { name: 'drawType', type: 'string' },
				        { name: 'orderCode', type: 'string' },
				        { name: 'sapCode', type: 'string' },
				        { name: 'actName', type: 'string' },
				        { name: 'dqActName', type: 'string' },
						{ name: 'assignee', type: 'string' },
						{ name: 'zzymfs', type: 'float' },
						{ name: 'zzymss', type: 'float' },
						{ name: 'zztyar', type: 'float' },
						{ name: 'zzzkar', type: 'float' },
						{ name:'startTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{ name:'endTime',type:'date',dateFormat:'Y-m-d H:i:s'},
						{ name:'processTime',type:'string'},
				        
					]
				});
				
				var saleItemReportStore = Ext.create('Ext.data.Store', {
					alias:'widget.SaleItemReportStore',
					model:'SaleItemReportModel',
					proxy:{
						type:'ajax',
						url:'main/saleReport/list/item',
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
						alias : 'widget.SaleItemReportGrid',
						store : saleItemReportStore,
						itemId:'saleItemReportGrid',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						style:'border-top:1px solid #C0C0C0;',
//						selModel:{selType:'checkboxmodel',injectCheckbox:0},
						region:'center',
						features:[{ftype:'groupingsummary'/*,startCollapsed:true*/,showSummaryRow:true,groupByText:'按当前进行分组',showGroupsText:'显示分组',groupHeaderTpl:"分组【{rows.length}】:{groupValue}"},{ftype: 'summary',dock:'bottom'}],
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						columns : [  {xtype:'rownumberer',width:50,align:'right'},
									 {text:'id',dataIndex:'id',width:0,hidden:true},
//									 {text:'操作',xtype:'actioncolumn',align:'center',width:38,items:[{
//											icon:'/resources/images/remarks1.png'
//											,tooltip:'编辑'
//											,handler:function(grid,rowIndex,colIndex){
//												this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
//											}
//									 }]},
									 {text:'订单编号',dataIndex:'orderCode',width:120,summaryType:'count',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 {text:'SAP编号',dataIndex:'sapCode',width:100},
									 {text:'行号',dataIndex:'posex',width:100},
									 {text:'绘图类型',dataIndex:'drawType',width:100,renderer:function(value ,metadata, record){
										 var drawTypeStore = drawType.getStore();
										 var find = drawTypeStore.findRecord('id',value,0,false,true,true); 
							 				if(find){
					 	            			return find.get('text');
					 	            		}
							                return value; 
									 }},
									 {text:'产品编号',dataIndex:'matnr',width:100},
//									 {text:'产品类型',dataIndex:'mtart',width:100,
//										 renderer: function(value,metadata,record){  
//								 				var materialMtartStore = materialMtartCombobox.getStore();
//								 				var find = materialMtartStore.findRecord('id',value); 
//								 				if(find){
//						 	            			return find.get('text');
//						 	            		}
//								                return value;  
//								           }
//									 },
									 {text:'产品描述',dataIndex:'maktx',width:300},
//									 {text:'是否标准产品',dataIndex:'isStandard',width:120,
//							        	 renderer: function(value,metadata,record){  
//								 				var isStandardStore = isStandardCombobox.getStore();
//								 				var find = isStandardStore.findRecord('id',value); 
//								 				if(find){
//						 	            			return find.get('text');
//						 	            		}
//								                return value;  
//								           }
//							         },
									 
									 
									 {text:'创建人',dataIndex:'createUser',width:110},
									 {text:'创建时间',dataIndex:'createTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'当前环节',dataIndex:'dqActName',width:120},
									 {text:'环节名称',dataIndex:'actName',width:120},
									 {text:'环节处理人',dataIndex:'assignee',width:120},
									 {text:'环节开始时间',dataIndex:'startTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'环节结束时间',dataIndex:'endTime',width:150,xtype:'datecolumn',format :'Y-m-d H:i:s'},
									 {text:'环节处理时间',dataIndex:'processTime',width:150,align:'right'},
									 {text:'移门方数',dataIndex:'zzymfs',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 {text:'移门扇数',dataIndex:'zzymss',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 {text:'投影面积',dataIndex:'zztyar',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 {text:'展开面积',dataIndex:'zzzkar',width:120,align:'right',summaryType:'sum',summaryRenderer:function(value){return Ext.String.format('<font style="font-weight: bold;">总数：{0} </font>', value);}},
									 
						           ],
						dockedItems:[{
							xtype:'pagingtoolbar',
							store:saleItemReportStore,
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
