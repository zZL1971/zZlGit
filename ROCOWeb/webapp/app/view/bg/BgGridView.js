Ext.define("SMSWeb.view.bg.BgGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.BgGridView',
	store : 'bg.BgStore',
	id:'bgGrid',
	enableKeyNav : true,
	columnLines : true,
	border : false,
	style:'border-top:1px solid #C0C0C0;',
//	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{
	    enableTextSelection:true //可以复制单元格文字
	},
	columns : [  {xtype:'rownumberer',width:30,align:'right'},
				 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
				 {text:'pid',dataIndex:'pid',width:0,hidden:true,menuDisabled:true},
//				 {text : '编辑',xtype:'actioncolumn',align:'center',id:'modifyMaterial',iconCls:'table_edit',width:40},
				 {text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
						icon:'/resources/images/remarks1.png'
						,tooltip:'编辑'
						,handler:function(grid,rowIndex,colIndex){
							this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
						}
				 }]},

//				 {text:'品号',dataIndex:'serialNumber',width:100},
//				 {text:'类型',dataIndex:'type',width:100},
//				 {text:'颜色',dataIndex:'colour',width:100},
//				 {text:'单价',dataIndex:'unitPrice',width:100,xtype: 'numbercolumn'},
//				 {text:'折扣',dataIndex:'zheKou',width:100, xtype: 'numbercolumn'},
//				 {text:'折扣价',dataIndex:'zheKouJia',width:100, xtype: 'numbercolumn'},
//				 {text:'数量',dataIndex:'amount',width:100, xtype: 'numbercolumn'},
//				 {text:'投影面积',dataIndex:'touYingArea',width:100, xtype: 'numbercolumn'},
//				 {text:'总价',dataIndex:'totalPrice',width:100, xtype: 'numbercolumn'},
				 
				 
				 {text:'变更单号',dataIndex:'bgCode',width:100,menuDisabled:true},
				 {text:'订单编号',dataIndex:'orderCode',width:150,menuDisabled:true},
				 {text:'订单类型',dataIndex:'orderType',width:100,menuDisabled:true,
					 renderer: function(value,metadata,record){
					 				var orderType = Ext.data.StoreManager.lookup('orderType_ORDER_TYPE');
					 				var find = orderType.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;
					           }},
				 {text:'订单日期',dataIndex:'orderDate',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d H:i:s'},
				 {text:'申请客户',dataIndex:'clients',width:100,menuDisabled:true},
				 {text:'变更类型',dataIndex:'bgType',width:100,menuDisabled:true,
					 renderer: function(value,metadata,record){
					 				var bgTypeStore = Ext.data.StoreManager.lookup('bgType_BG_TYPE');
					 				var find = bgTypeStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;
					           }},
				 {text:'审批时间',dataIndex:'updateTime',width:150,xtype:'datecolumn',format:'Y-m-d H:i:s'},
				 /*{text:'审批人',dataIndex:'updateUser',width:120},*/
				 {text:'联系人',dataIndex:'contacts',width:100,menuDisabled:true},
				 {text:'联系电话',dataIndex:'tel',width:100,menuDisabled:true},
				 {text:'创建人',dataIndex:'createUser',width:100,menuDisabled:true},
				 {text:'创建时间',dataIndex:'createTime',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d H:i:s'},
				 {text:'状态',dataIndex:'orderStatus',width:100,menuDisabled:true,
					 renderer: function(value,metadata,record){  
					 				var orderStatusStore = Ext.data.StoreManager.lookup('orderStatus_ORDER_STATUS');
					 				var find = orderStatusStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;
					           }},
				{text:'取消原因',dataIndex:'reason',width:160,menuDisabled:true}
					           
	           ],
   dockedItems:[{
			xtype:'pagingtoolbar',
			store:'bg.BgStore',
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
//	           ,
//	selType: 'rowmodel',
//	plugins: [rowEditing]
//	selType : 'cellmodel',
//	plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
//			{
//				enableKeyNav : true,
//				clicksToEdit : 2
//	})],
//	listeners: {
//            'selectionchange': function(view, records) {
//				Ext.getCmp("removeItem").setDisabled(!records.length);
//            }
//  }
});