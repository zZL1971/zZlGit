Ext.define("SMSWeb.view.cust.CustGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.CustGridView',
	store : 'cust.CustStore',
	itemId:'custSearchGrid',
	id:'custGrid',
	enableKeyNav : true,
	columnLines : true,
	border : false,
	style:'border-top:1px solid #C0C0C0;',
//	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{
	    enableTextSelection:true //可以复制单元格文字
	},
	columns : [  {xtype:'rownumberer',width:50,align:'right'},
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

				 {text:'编号',dataIndex:'kunnr',width:100,menuDisabled:true},
				 {text:'名称',dataIndex:'name1',width:100,menuDisabled:true},
				 {text:'农行账号',dataIndex:'nh',width:100,menuDisabled:true},
				 {text:'建行账号',dataIndex:'jh',width:100,menuDisabled:true},
				 {text:'类型',dataIndex:'ktokd',width:200,menuDisabled:true,
					 renderer: function(value,metadata,record){
					 				var ktokdStore = Ext.data.StoreManager.lookup('ktokd_KTOKD');
					 				var find = ktokdStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;  
					           }},
				 
				 {text:'电话',dataIndex:'telNumber',width:100,menuDisabled:true},
				 {text:'销售区域',dataIndex:'bzirk',width:100,menuDisabled:true},
				 {text:'信贷额度',dataIndex:'xinDai',width:100,menuDisabled:true},
				 {text:'送达方',dataIndex:'kunnrS',width:100,menuDisabled:true},
//	             {text:'开始日期',dataIndex:'startDate',width:100,xtype: 'datecolumn',renderer : Ext.util.Format.dateRenderer('Y-m-d')},
//	             {text:'结束日期',dataIndex:'endDate',width:100,xtype: 'datecolumn',renderer : Ext.util.Format.dateRenderer('Y-m-d')},
	             {text:'折扣',dataIndex:'zheKou',width:100, xtype: 'numbercolumn'},
	             {text:'是否启用',dataIndex:'rowStatus',width:100,align: 'center',
	            	 editor:Ext.create('Ext.ux.form.DictCombobox',{
	             		dict:'YES_NO'
	     			}),
	     			renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer}
//				 ,
//	             {text:'总金额',dataIndex:'total',width:100, xtype: 'numbercolumn'},
//	             {text:'剩余金额',dataIndex:'shengYu',width:100, xtype: 'numbercolumn'}
	           ],
   dockedItems:[{
			xtype:'pagingtoolbar',
			store:'cust.CustStore',
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