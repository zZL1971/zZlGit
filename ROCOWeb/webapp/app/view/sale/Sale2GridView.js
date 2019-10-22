Ext.define("SMSWeb.view.sale.Sale2GridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.Sale2GridView',
	store : 'sale.Sale2Store',
	id:'sale2Grid',
	enableKeyNav : true,
	columnLines : true,
	border : false,
	style:'border-top:1px solid #C0C0C0;',
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{enableTextSelection:true},
	columns : [  
				 {xtype:'rownumberer',width:30,align:'right'},
				 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
				 {text:'pid',dataIndex:'pid',width:0,hidden:true,menuDisabled:true},
				 {text:'订单编号',dataIndex:'orderCode',width:150,menuDisabled:true},
				 {text:'补购参考订单',dataIndex:'pOrderCode',width:150,menuDisabled:true},
				 {text:'订单类型',dataIndex:'orderType',width:90,menuDisabled:true,
					 renderer: function(value,metadata,record){  
					 				var orderTypeStore = Ext.data.StoreManager.lookup('orderType_ORDER_TYPE');
					 				var orderTypeStore = orderTypeCombobox.getStore();
					 				var find = orderTypeStore.findRecord('id',value,0,false,true,true);
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;
					           }},
				 {text:'预计出货日期',dataIndex:'yuJiDate',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
				 {text:'实际出货日期',dataIndex:'shiJiDate',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
				 {text:'订单日期',dataIndex:'orderDate',width:90,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
				 {text:'是否样品',dataIndex:'isYp',width:80,
					 renderer: function(value,metadata,record){  
			 				var fuFuanCondStore = Ext.data.StoreManager.lookup('isYp_YES_NO');
			 				var find = fuFuanCondStore.findRecord('id',value); 
			 				if(find){
	 	            			return find.get('text');
	 	            		}
			                return value;
			           }},
				 {text:'售达方',dataIndex:'shouDaFang',width:80,menuDisabled:true},
				 {text:'售达方名称',dataIndex:'kunnrName1',width:120,menuDisabled:true},
				 {text:'店面联系电话',dataIndex:'dianMianTel',width:100,menuDisabled:true},
				 {text:'设计师联系电话',dataIndex:'designerTel',width:110,menuDisabled:true},
				 {text:'订单总额',dataIndex:'orderTotal',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
				 {text:'付款条件',dataIndex:'fuFuanCond',width:90,menuDisabled:true,
					 renderer: function(value,metadata,record){  
					 				var fuFuanCondStore = Ext.data.StoreManager.lookup('fuFuanType_FU_FUAN_COND');
					 				var find = fuFuanCondStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;
					           }},
				 {text:'付款金额',dataIndex:'fuFuanMoney',width:90,menuDisabled:true,xtype: 'numbercolumn',align:'right'},
				 {text:'支付方式',dataIndex:'payType',width:90,menuDisabled:true,
					 renderer: function(value,metadata,record){  
					 				var payTypeStore = Ext.data.StoreManager.lookup('payType_PAY_TYPE');
					 				var find = payTypeStore.findRecord('id',value); 
					 				if(find){
			 	            			return find.get('text');
			 	            		}
					                return value;  
					           }},
				 {text:'客户姓名',dataIndex:'name1',width:100,menuDisabled:true},
				 {text:'联系方式',dataIndex:'tel',width:100,menuDisabled:true},
				 {text:'创建人',dataIndex:'createUser',width:100,menuDisabled:true},
				 {text:'创建时间',dataIndex:'createTime',width:150,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d H:i:s'},
				 {text:'当前环节名称',dataIndex:'jdName',width:120,menuDisabled:true}		 
	           ],
    dockedItems:[{
			xtype:'pagingtoolbar',
			store:'sale.Sale2Store',
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
	listeners: {
        'render': function(sale2Grid) {
			//没有看金额权限
			if(IS_MONEY=="false"){
				Ext.Array.each(sale2Grid.columns, function(column) {
					if("undefined"!=typeof(column.dataIndex) && ("orderTotal"==column.dataIndex || "fuFuanCond"==column.dataIndex
							|| "fuFuanMoney"==column.dataIndex || "payType"==column.dataIndex)){
						column.hidden=true;
					}
				});
			}
        },
        itemdblclick:function(dataview, record, item, index, e){
			var id = record.get('id');
			var orderType = record.get('orderType');
			var orderCode = record.get('orderCode');
			var typeFlag = "";
			var jdName=record.get("jdName");
			var otype="newSaleContent";
			if("OR3"==orderType || "OR4"==orderType){
				typeFlag = "buDan";
				if(""==orderCode){
					Ext.create('SMSWeb.view.sale.NewSaleContentWindow',
						     {formId : id,title:'订单修改',"orderType":typeFlag,jdName:jdName,otype:otype}).show();
					return;
				}else if("客户起草"==jdName||"客服起草"==jdName||"客服审核"==jdName){
					Ext.create('SMSWeb.view.sale.NewSaleContentWindow',
						     {formId : id,title:'订单修改',"orderType":typeFlag,jdName:jdName}).show();
					return;
				}
			}
			
			//当前环节为空或为起草，条用新界面
			if((!jdName||jdName=="起草")&&"OR3"!=orderType&&"OR4"!=orderType){
				//调用新页面
				var win=Ext.create("SMSWeb.view.sale.NewSaleContentWindow", {title:'修改订单',formId:id,otype:otype});
				win.show();
			}else{
				Ext.create('SMSWeb.view.sale.NewSaleWindow',
					     {formId : id,title:'订单修改',"orderType":typeFlag,jdName:jdName,otype:otype}).show();
			}
		}
    }
});
