Ext.tip.QuickTipManager.init();
var Cust3Store=Ext.create("SMSWeb.store.cust.Cust3Store");
Ext.define('SMSWeb.view.cust.NewCustWindowForBank', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewCustWindowForBank',
			border : false,
			//layout:'fit',
			autoScroll: true,
			formId:null,
			width:1100,
			maximizable:true,
			height:500,
			modal:true,
			constrainHeader: true,
			closable : true,
			layout:'border',
			itemId : 'NewCustWindowForBank',
			tbar : [
			{
				xtype : 'button',
	            text: '查询交易明细',
	            iconCls: 'table_search',
	            itemId : 'findXinDai',
	            id:'findXinDai',
	            listeners:{
	            	click : function( bt, e, eOpts ) {
					 	var me =this;
						Cust3Store.load({params:{
							startDate:$("input[name='startDate1']").val(),
							endDate:$("input[name='endDate1']").val(),
							kunnr:$("input[name='kunnr']").val(),
							status_num:$("input[name='status_num']").val()
							}
						});
						console.log($("input[name='status_num']").val());
					}
	            }
	        }],
			
			initComponent : function() {
				 var me = this;
				 var stateAuditCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '审核状态',
						emptyText: '请选择...',
						name:'statusNum',
						dict:'BANK'
				});
				 var xinDai = Ext.widget('grid',{
					extend : 'Ext.grid.Panel',
					alias : 'widget.Cust3GridView',
					store : Cust3Store,
					itemId:'custXinDai',
					enableKeyNav : true,
					columnLines : true,
					border : false,
					style:'border-top:1px solid #C0C0C0;',
					selModel:{selType:'checkboxmodel',injectCheckbox:0},
					region:'center',
					viewConfig:{
					    enableTextSelection:true //可以复制单元格文字
					},
					tbar :[{
	                    xtype:'datefield',
	                    fieldLabel: '订单日期从',
	                    name: 'startDate1',
	                    id:'startDate1',
	                    format :'Y-m-d'
	                },{
	                    xtype:'datefield',
	                    fieldLabel: '到',
	                    name: 'endDate1',
	                    format :'Y-m-d',
	                    id:'endDate1'
	                },
	                {
			        	xtype:'dictcombobox',
						fieldLabel : '请选择银行',
//						editable: false,
						emptyText: '请选择...',
						width:180,
						name:'status_num',
						dict:'BANK'
			        }],
					columns : [ {xtype:'rownumberer',width:50,align:'right'},
					             {text:'付款方企业编号',dataIndex:'tradeCompanyId',width:120,menuDisabled:true},
					             {text:'付款账号',dataIndex:'tradeAccNo',width:150,menuDisabled:true},
					             {text:'银行种类',dataIndex:'statusNum',width:120,menuDisabled:true,renderer: function(value,metadata,record){  
						 				var stateAuditStore = stateAuditCombobox.getStore();
						 				var find = stateAuditStore.findRecord('id',value); 
						 				if(find){
				 	            			return find.get('text');
				 	            		}
						                return value;  
						           }},
					             {text:'交易日志号',dataIndex:'jrnNo',width:120,menuDisabled:true},
					             {text:'交易日期',dataIndex:'tradeTime',width:120,menuDisabled:true},
					             {text:'经销商编号',dataIndex:'dealerNum',width:120,menuDisabled:true},
					             {text:'金额',dataIndex:'tradeAmount',width:120,menuDisabled:true},
					             {text:'付款人',dataIndex:'payer',width:120,menuDisabled:true}
						           ],
		           dockedItems:[{
						xtype:'pagingtoolbar',
						store:Cust3Store,
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
				//生成页面
				Ext.apply(this, {
					items : [xinDai]
				});
				this.callParent(arguments);
			}
		});
