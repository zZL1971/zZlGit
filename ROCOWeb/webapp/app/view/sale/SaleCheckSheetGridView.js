Ext.define("SMSWeb.view.sale.SaleCheckSheetGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.SaleCheckSheetGridView',
	store : 'sale.SaleCheckSheetStore',
	id:'SaleCheckSheetGrid',
	enableKeyNav : true,
	columnLines : true,
	border : false,
	style:'border-top:1px solid #C0C0C0;',
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{enableTextSelection:true},
	columns : [  
				 {xtype:'rownumberer',width:30,align:'right'},
				 {text:'过帐日期',dataIndex:'postdate',width:90,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},//1
				 {text:'凭证号或订单号',dataIndex:'sapordercode',width:120,menuDisabled:true},//2
				 {text:'文本',dataIndex:'textorder',width:150,menuDisabled:true},//3
				 
				 {text:'收款金额',dataIndex:'receiptamount',width:150,menuDisabled:true,xtype: 'numbercolumn',align:'right'},//4
				 
				 {text:'下单金额',dataIndex:'placeamount',width:150,menuDisabled:true,xtype: 'numbercolumn',align:'right'},//5
				 {text:'余额',dataIndex:'balanceamount',width:150,menuDisabled:true,xtype: 'numbercolumn',align:'right'},//6
				 {text:'订单原因',dataIndex:'bezei',width:300,menuDisabled:true},//7
				 {text:'财务释放日期',dataIndex:'releasedate',width:90,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d',hidden : true}//8
	           ],
    dockedItems:[{
			xtype:'pagingtoolbar',
			store:'sale.SaleCheckSheetStore',
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
				        console.log("yy");
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
//	selType: 'rowmodel',
//	plugins: [rowEditing]
//	selType : 'cellmodel',
//	plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
//			{
//				enableKeyNav : true,
//				clicksToEdit : 2
//	})],
	
});
