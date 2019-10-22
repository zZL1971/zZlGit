var mtartCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'MATERIAL_MTART'});

Ext.define("SMSWeb.view.mm.base.MaterialMainGridBaseView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.MaterialMainGridBaseView',
	store : 'mm.base.Store4MaterialBase',
	enableKeyNav : true,
	columnLines : true,
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{
	    enableTextSelection:true
	},
	dockedItems:[
				{
					xtype:'pagingtoolbar',
					store:'mm.base.Store4MaterialBase',
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
		         {text : '编辑',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:40
	            	    ,handler : function(grid, rowIndex, colIndex) {
							this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
						}
	             },
	             {text:'产品编号',dataIndex:'matnr',width:150},
 	             {text:'产品描述',dataIndex:'maktx',width:150},
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
	 	         {text:'条件类型',dataIndex:'kschl',width:100},
 	             {text:'销售组织',dataIndex:'vkorg',width:100},
 	             
 	             {text:'分销渠道',dataIndex:'vtweg',width:100},
 	             {text:'物料组',dataIndex:'matkl',width:100},
 	             {text:'物料组2',dataIndex:'matkl2',width:100},
 	             {text:'价格',dataIndex:'kbetr',width:100},
 	             {text:'等级价格',dataIndex:'kbetrDj',width:100},
 	             {text:'规格',dataIndex:'groes',width:100},
 	             
// 	             {text:'有效日期从',dataIndex:'datbi',width:100,xtype: 'datecolumn',format:'Y-m-d'},
// 	             {text:'有效日期到',dataIndex:'datab',width:100,xtype: 'datecolumn',format:'Y-m-d'},
 	             {text:'颜色',dataIndex:'extwg',width:100},
 	             {text:'单位',dataIndex:'meins',width:100},
 	             
 	             {text:'可配置物料',dataIndex:'kzkfg',width:0},
 	             {text:'可配置物料',dataIndex:'kzkfgdesc',width:100},
 	             {text:'毛重',dataIndex:'brgew',width:100},
 	             
 	             {text:'凈重',dataIndex:'ntgew',width:100},
 	             {text:'重量单位',dataIndex:'gewei',width:100},
 	             {text:'产品组',dataIndex:'spart',width:100},
 	             {text:'产品层次',dataIndex:'prdha',width:100},
 	             {text:'产品层次描述',dataIndex:'vtext',width:100},
 	             
 	             {text:'体积',dataIndex:'volum',width:100},
 	             {text:'体积单位',dataIndex:'voleh',width:100},
// 	             {text:'单位(货币)',dataIndex:'konwa',width:100},
// 	             {text:'单位(条件定价单位)',dataIndex:'kpein',width:100},
// 	             {text:'单位(条件单位)',dataIndex:'kmein',width:100},
// 	             {text:'条件计算类型',dataIndex:'krech',width:100},
 	             {text:'处理状态',dataIndex:'kbstat',width:100},
	             {text:'删除指标符',dataIndex:'loevmKo',width:100}
	           ]
});