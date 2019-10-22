Ext.QuickTips.init();  
var mtartCombo = Ext.create('Ext.ux.form.DictCombobox',{dict:'MATERIAL_MTART'});
var unit = Ext.create('Ext.ux.form.DictCombobox',{dict:'UNIT'});
var saleFor = Ext.create('Ext.ux.form.DictCombobox',{dict:'SALE_FOR'});
var zzazdr = Ext.create('Ext.ux.form.DictCombobox',{dict:'ZZAZDR'});
Ext.define("SMSWeb.view.mm.sale.MaterialMainGridBase2SaleView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.MaterialMainGridBase2SaleView',
	store : 'mm.sale.Store4MaterialBase2Sale',
	enableKeyNav : true,
	columnLines : true,
	id:'MaterialMainGridBase2SaleView',
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
	listeners:{select:{
    	fn:function(rowmodel,record,index,eOpts){
    		var mmId=record.data.matnr;
    		var win=Ext.ComponentQuery.query('MyGoodsWindow')[0];
    		if(win!=undefined){
/*    		if('OR4'==win.ortype || 'OR3'==win.ortype){
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
    		}*/
//    		;
    		//start
//    				Ext.Ajax.request({
//						url : 'main/mm/findPicLog?picid='+mmId,
//						//jsonData : gridData,
//						method : 'GET',
//						frame:true,
//						dataType : "json",
//						contentType : 'application/json',
//						success : function(response, opts) {
//							var values = Ext.decode(response.responseText);
//							console.log("****values*****");
//							if(values == 1){
//								//加载附加信息
//								var picvalue = "有图片";
////								var _myGoodsId = values.data.myGoodsId;
////								loadfjForm(form1,_myGoodsId);
//								//Ext.MessageBox.alert("提示","有图片");
//							}else{
//								var picvalue = "无图片";
////								Ext.MessageBox.alert("提示",values.errorMsg);
//								//Ext.MessageBox.alert("提示","没有图片");
//							}
//						},
//						failure : function(response, opts) {
//							Ext.MessageBox.alert("提示","查询图片失败！");
//						}
//					});
    		//end
    	}
    	}
    }
	,
	 //双击显示图片
	 itemdblclick:function(dataview, record, item, index, e){
		console.log("*************itemdblclick*****************");
		var id = record.get('id');
		Ext.create('SMSWeb.view.mm.sale.MaterialPICWindow',
			     {formId : id,title:'产品图片',filepath:'main/mm/queryMaterialPic?picid='+id}).show();
	 }
	

	},
	columns : [
	             {text:'id',dataIndex:'id',width:0,hidden:true},
		         {text : '选择',xtype:'actioncolumn',align:'center',iconCls:'table_edit',width:40,hidden:true
	            	    ,handler : function(grid, rowIndex, colIndex) {
							this.up('grid').fireEvent('editButtonClick',grid,rowIndex,colIndex);
						}
	             },
	             {text:'非标序号',dataIndex:'serialNumber',width:100,hidden:true},
//	             {text:'产品编号',dataIndex:'matnr',width:100,hidden:true},
	             
	             {text:'产品编号',dataIndex:'matnr',id:'matnr',width:100,hidden:true},
	             
 	             {text:'产品描述',dataIndex:'maktx',width:320},
// 	             {text:'总下单数',dataIndex:'orderCount',width:80},
 	             {text:'库存',dataIndex:'cnt',width:100},
 	             {text:'图片',dataIndex:'ispic',width:100},
 	             {text:'产品类型',dataIndex:'mtart',width:80,
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
 	             {text:'体积',dataIndex:'volum',width:80},
 	             {text:'体积单位',dataIndex:'voleh',width:80},
 	             {text:'单位',dataIndex:'meins',width:80,
 	            	 	editor:unit,
	 	             	renderer : function(value, meta, record) {
		            		var find= unit.getStore().findRecord("id",value);
		            		if(find){
		            			return find.get('text');
		            		}else{
		            			return value;
		            		}
		            	}},
            	{text:'产品组',dataIndex:'saleFor',width:80,
            	 	editor:unit,
 	             	renderer : function(value, meta, record) {
	            		var find= saleFor.getStore().findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
	            	}},
 	             {text:'规格',dataIndex:'groes',width:180},
 	             {text:'安装位置',dataIndex:'zzazdr',width:180},
// 	             {text:'安装地址',dataIndex:'zzazdr',width:180,editor:unit,
//                 	renderer: function(value,meta,record){  
//		 				var find = zzazdr.getStore().findRecord("id",value); 
//		 				if(find){
//	            			return find.get('text');
//	            		}
//		                return value;  
//		           }},
 	             {text:'创建时间',dataIndex:'createTime',width:140}
 	            
	           ],
	           initComponent : function() {
		 
		 
					var me = this;
		    		var win=Ext.ComponentQuery.query('MyGoodsWindow')[0];
		    		//针对OR3 和OR4 显示库存
		    		if(win==undefined || ("OR3"!=win.ortype && "OR4"!=win.ortype)){
		    			me.columns[5].hidden=true;
		    		}else{
		    			me.columns[5].hidden=false;
		    		}
					me.callParent(arguments);
	           }
});