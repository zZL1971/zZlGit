var mtartCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'MATERIAL_MTART'});
var saleFor = Ext.create('Ext.ux.form.DictCombobox',{dict:'SALE_FOR'});
//var zzcpdj = Ext.create('Ext.ux.form.DictCombobox',{dict:'MM_ZZCPDJ'});
var series = Ext.create('Ext.ux.form.DictCombobox',{dict:'STYLE'});
var productSpace = Ext.create('Ext.ux.form.DictCombobox',{dict:'ZZAZDR'});
Ext.QuickTips.init();  
Ext.define("SMSWeb.view.cart.ShoppingCart2AddSaleView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.ShoppingCart2AddSaleView',
	store : 'cart.ShoppingCartStore',
	enableKeyNav : true,
	columnLines : true,
	id:'ShoppingCart2AddSaleView_itemId',
	viewConfig:{
		enableTextSelection:true
	},
	dockedItems:[{
	            	 xtype:'pagingtoolbar',
	            	 store:'cart.ShoppingCartStore',
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
	             }],
	             selModel:{selType:'checkboxmodel',injectCheckbox:0},
	             listeners:{select:{
	            	 fn:function(rowmodel,record,index,eOpts){
	            		 var mmId=record.data.matnr;
	            		 var win=Ext.ComponentQuery.query('MyGoodsWindow')[0];
	            		 if(win!=undefined){
	            			 if('OR4'==win.ortype || 'OR3'==win.ortype){
	            				 Ext.Ajax.request({
	            					 url:'/main/mm/getMMStock?mmId='+mmId,
	            					 async:false,
	            					 dataType: "json",
	            					 success:function(response,opts){
	            						 //订单信息
	            						 var data = Ext.decode(response.responseText).data;
	            						 record.data.cnt=data.stock;
	            						 record.commit();
	            					 },
	            					 failure:function(response,opts){
	            						 Ext.Msg.alert("can't",'error');
	            					 }
	            				 });
	            			 }
	            			 ;
	            		 }
	            	 }
	             }
	             ,
	             //双击显示图片
	             itemdblclick:function(dataview, record, item, index, e){
	            	 var id = record.get('id');
	            	 Ext.create('SMSWeb.view.mm.sale.MaterialPICWindow',
	            			 {formId : id,title:'产品图片',filepath:'main/mm/queryMaterialPic?picid='+id}).show();
	             }
	             },
	             columns : [
	                        {xtype:'rownumberer',width:30,align:'right'},
	                        {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
	                        {text : '编辑',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:40,hidden:false
	                        	,handler : function(grid, rowIndex, colIndex) {
	                        		this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
	                        	}
	                        },
	                        {text:'非标序号',dataIndex:'serialNumber',width:100},
	                        {text:'产品描述',dataIndex:'maktx',width:320},
	                        {text:'图片',dataIndex:'ispic',width:100},
	                        {text:'产品组',dataIndex:'saleFor',width:120,
	                        	renderer: function(value,meta,record){  
				 				var find = saleFor.getStore().findRecord("id",value); 
				 				if(find){
		 	            			return find.get('text');
		 	            		}
				                return value;  
				           }},
	                        {text:'主题系列',dataIndex:'series',width:120,
				        	   renderer: function(value, meta, record){  
				 				var find = series.getStore().find("id",value,0,false,true,true);
				 				if (find < 0){
				 			    	return value;
				 			    }else{
				 			    	var record = series.getStore().getAt(find);
				 			    	return record ? record.get('text') : '';
				 			    }
				           }},
	                        {text:'产品等级',dataIndex:'zzcpdj',width:120},
	                        {text:'产品空间',dataIndex:'productSpace',width:120,
	                        	renderer: function(value,meta,record){  
				 				var find = productSpace.getStore().findRecord("id",value); 
				 				if(find){
		 	            			return find.get('text');
		 	            		}
				                return value;  
				           }},
				           {text:'安装地址',dataIndex:'zzazdr',width:120},
	                        {text:'产品类型',dataIndex:'mtart',width:80,
	                        	renderer : function(value, meta, record) {
	                        		var find= mtartCombo.getStore().findRecord("id",value);
	                        		if(find){
	                        			return find.get('text');
	                        		}else{
	                        			return value;
	                        		}
	                        	}
	                        },
	                        {text:'体积',dataIndex:'volum',width:80},
	                        {text:'体积单位',dataIndex:'voleh',width:80},
	                        {text:'单位',dataIndex:'meins',width:80},
	                        {text:'规格',dataIndex:'groes',width:180},
	                        {text:'创建时间',dataIndex:'createTime',width:140}
	                        ]
	             
	             
});
