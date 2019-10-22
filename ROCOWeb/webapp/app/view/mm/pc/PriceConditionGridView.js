var countCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'CONDITION'});//运算条件
var plusOrMinusCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PLUS_OR_MINUS'});//加减
var priceTypeCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'PRICE_TYPE'});//价格类型
var isTakeNumCombo  = Ext.create('Ext.ux.form.DictCombobox',{dict:'YES_NO'});//是否乘数量
//alert(priceTypeCombo.store.count());
//alert(countCombo.store.count());
//alert(plusOrMinusCombo.store.count());
//alert(isTakeNumCombo.store.count());
Ext.define("SMSWeb.view.mm.pc.PriceConditionGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.PriceConditionGridView',
	store : Ext.create('SMSWeb.store.mm.pc.Store4PriceCondition'),
	enableKeyNav : true,
	columnLines : true,
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{
	    enableTextSelection:true
	},
	tbar: [
			{xtype: 'button', text: '保存',iconCls:'table_save',
				handler : function() {
				   this.up('grid').fireEvent('saveButtonClick');
				} 
			},
	        {xtype: 'button', text: '添加',iconCls:'table_add',
	        	handler : function() {
        		   this.up('grid').fireEvent('addButtonClick');
				} 
	        }
	       ,{xtype: 'button', text: '删除',iconCls:'table_remove', 
	    	   handler : function() {
	    		   this.up('grid').fireEvent('deleteButtonClick');
			   }   
	    	 }
	],

	columns : [
	             {text:'id',dataIndex:'id',width:0,hidden:true},
	             {text:'类型',dataIndex:'type',width:180,sortable: false,menuDisabled:true,
	            	    editor:priceTypeCombo,
	 	            	renderer : function(value, meta, record) {
	 	            		var find= priceTypeCombo.getStore().findRecord("id",value);
	 	            		if(find){
	 	            			return find.get('text');
	 	            		}else{
	 	            			return value;
	 	            		}
	 	            	}
	             },
	             {text:'加减',dataIndex:'plusOrMinus',width:80,sortable: false,menuDisabled:true,
	 	            	editor:plusOrMinusCombo,
	 	            	renderer : function(value, meta, record) {
	 	            		var find= plusOrMinusCombo.getStore().findRecord("id",value);
	 	            		if(find){
	 	            			return find.get('text');
	 	            		}else{
	 	            			return value;
	 	            		}
	 	            	}
	             },
	             {text:'运算条件',dataIndex:'condition',width:80,sortable: false,menuDisabled:true,
	 	            	editor:countCombo,
	 	            	renderer : function(value, meta, record) {
	 	            		var find= countCombo.getStore().findRecord("id",value);
	 	            		if(find){
	 	            			return find.get('text');
	 	            		}else{
	 	            			return value;
	 	            		}
	 	            	}
	             },
 	             {text:'运算值',dataIndex:'conditionValue',width:100,sortable: false,menuDisabled:true,
 	            	editor:{
						xtype:'numberfield',
						allowBlank:true
					}
 	             },
 	             {text:'小计',dataIndex:'subtotal',width:100,sortable: false,menuDisabled:true
 	            	
 	             },
 	             {text:'乘数量',dataIndex:'isTakeNum',width:100,sortable: false,menuDisabled:true,
	 	            	editor:isTakeNumCombo,
	 	            	renderer : function(value, meta, record) {
	 	            		var find= isTakeNumCombo.getStore().findRecord("id",value);
	 	            		if(find){
	 	            			return find.get('text');
	 	            		}else{
	 	            			return value;
	 	            		}
	 	            	}
	             },
	             {text:'总计',dataIndex:'total',width:100,sortable: false,menuDisabled:true
 	            	
 	             },
 	             {text:'排序',dataIndex:'orderby',width:100,sortable: false,menuDisabled:true,
 	            	 editor:{
 	            		 xtype:'numberfield',
 	            		 allowBlank:true
 	            	 }
 	             }
	           ],
    selType : 'cellmodel',
	plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
			{
				enableKeyNav : true,
				clicksToEdit : 2
	})],
	listeners: {
		afterrender:function(){
			var me = this;
			me.getStore().load();
		}
	}
});