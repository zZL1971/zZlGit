var mtartCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'MATERIAL_MTART'});

Ext.define("SMSWeb.view.mm.sale.MaterialMainGridBase2AddSaleView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.MaterialMainGridBase2AddSaleView',
	store : 'mm.sale.Store4MaterialBase2Sale',
	enableKeyNav : true,
	columnLines : true,
	viewConfig:{
	    enableTextSelection:true
	},
	dockedItems:[
			{
				xtype:'pagingtoolbar',
				store:'mm.sale.Store4MaterialBase2Sale',
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
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	columns : [
	             {text:'id',dataIndex:'id',width:0,hidden:true},
		         {text : '查看',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:40,hidden:true
	            	    ,handler : function(grid, rowIndex, colIndex) {
							this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
						}
	             },
	             {text:'产品编号',dataIndex:'matnr',width:180},
 	             {text:'产品描述',dataIndex:'maktx',width:300},
 	             {text:'产品类型',dataIndex:'mtart',width:100,
	 	            	editor:mtartCombo,
	 	             	renderer : function(value, meta, record) {
		            		var find= mtartCombo.getStore().findRecord("id",value);
		            		if(find){
		            			return find.get('text');
		            		}else{
		            			return value;
		            		}
		            	}
	 	         },
// 	             {text:'销售组织',dataIndex:'vkorg',width:100},
// 	             {text:'分销渠道',dataIndex:'vtweg',width:100},
// 	             {text:'物料组',dataIndex:'matkl',width:100},
// 	             {text:'产品组',dataIndex:'spart',width:100},
// 	             {text:'产品层次',dataIndex:'prdha',width:100},
// 	             {text:'毛重',dataIndex:'brgew',width:100},
// 	             {text:'凈重',dataIndex:'ntgew',width:100},
// 	             {text:'重量单位',dataIndex:'gewei',width:100},
 	             {text:'体积',dataIndex:'volum',width:100},
 	             {text:'体积单位',dataIndex:'voleh',width:100},
 	             {text:'规格',dataIndex:'groes',width:100},
 	             {text:'单位',dataIndex:'meins',width:100}
	           ]
});