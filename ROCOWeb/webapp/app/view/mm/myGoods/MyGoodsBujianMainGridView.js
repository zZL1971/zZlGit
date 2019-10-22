var ortypetCombo2 = Ext.create('Ext.ux.form.DictCombobox',{dict:'ORDER_TYPE_BD'});
var ortypetCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'OR_TYPE'});
Ext.define("SMSWeb.view.mm.myGoods.MyGoodsBujianMainGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.MyGoodsBujianMainGridView',
	store : 'mm.myGoods.Store4MyGoodsBujian',
	enableKeyNav : true,
	columnLines : true,
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{
	    enableTextSelection:true
	},
	dockedItems:[
					{
						xtype:'pagingtoolbar',
						store:'mm.myGoods.Store4MyGoodsBujian',
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
					}
		],
	columns : [
	             {text:'id',dataIndex:'id',width:0,hidden:true},
		         {text : '编辑/查看',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:80
	            	    ,handler : function(grid, rowIndex, colIndex) {
							this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
						}
	             },
	             {text:'订单类型',dataIndex:'ortype',width:120,
	 	            	editor:ortypetCombo2,
	 	             	renderer : function(value, meta, record) {
		            		var find= ortypetCombo2.getStore().findRecord("id",value);
		            		if(find){
		            			return find.get('text');
		            		}else{
		            			return value;
		            		}
		            	}
	 	         },
	             {text:'产品编号',dataIndex:'matnr',width:150},
 	             {text:'产品描述',dataIndex:'maktx',width:250},
 	             {text:'投诉内容描述',dataIndex:'zztsnr',width:250},
 	             {text:'描述',dataIndex:'textdesc',width:100},
	             {text:'创建日期',dataIndex:'createTime',width:150},
	             {text:'materialHeadId',dataIndex:'materialHeadId',width:100,hidden:true}
	           ]
});