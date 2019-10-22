Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.print.BarCodePrint', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.BarCodePrint',
	border : false,
	layout:'border',
	formId:null,
	requires : ['Ext.ux.form.SearchForm'],
	initComponent : function() {
		var head = document.head || document.getElementsByTagName("head")[0] || document.documentElement;
		var oscript = document.createElement("script");
		oscript.src ="/js/CLodopfuncs.js?priority=0";
		head.insertBefore( oscript,head.firstChild );


		oscript = document.createElement("script");
		oscript.src ="/js/CLodopfuncs.js?priority=1";
		head.insertBefore( oscript,head.firstChild );

		
		oscript = document.createElement("object");
		oscript.id = "LODOP_OB";
		oscript.classid = "clsid:2105C259-1E0C-4534-8141-A753534CB4CA";
		oscript.width = 0;
		oscript.height = 0;
		var _objectChilld = document.createElement("embed");
		_objectChilld.id = "LODOP_EM";
		_objectChilld.type = "application/x-print-lodop";
		_objectChilld.width = 0;
		_objectChilld.height = 0;
		oscript.insertBefore( _objectChilld,oscript.firstChild );
		head.insertBefore( oscript,head.firstChild );
		console.log(head);
		
		 var form = Ext.widget('searchform',{
	  		region:'north',
			id : 'queryForm',
			bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",
			border : false,
			tbar : [ {
				xtype : 'button',
				text : '查询',
				itemId : 'query',
				iconCls : 'table_search',
				hidden : false
			} ,{
				xtype : 'buttontransparent',
				text : '打印',
				itemId:'print',
				icon:'/resources/images/down.png'
			}],
			fieldDefaults : {
				labelWidth : 100,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;'
			},
			layout : {
				type : 'table',
				columns : 4
			},
			items : [ {
				xtype : 'textfield',
				fieldLabel : '关联订单',
				allowBlank : true,
				name : 'linkOrderCode',
				width : 300
			}, {
				xtype:'textfield',
				fieldLabel:'订单编号',
				allowBlank:true,
				name:'orderCode',
				width:300
			},{
				xtype:'datefield',
				fieldLabel:'创建日期从',
				allowBlank:true,
				name:'createTimeFrom',
				width:300,
				format : 'Y-m-d'
			},{
				xtype:'datefield',
				fieldLabel:'到',
				name:'createTimeTo',
				width:300,
				format : 'Y-m-d'
			} ]

		});

		var grid = Ext.widget('grid',{
			extend : 'Ext.grid.Panel',
			store : 'print.BarCodePrintStore',
			id:'BarCodeDataGrid',
			enableKeyNav : true,
			columnLines : true,
			border : false,
			style:'border-top:1px solid #C0C0C0;',
			region:'center',
			selModel:{selType:'checkboxmodel',injectCheckbox:0},
			viewConfig:{
			    enableTextSelection:true //可以复制单元格文字
			},
			columns : [ {
				xtype : 'rownumberer',
				width : 30,
				align : 'right'
			}, {
				text : 'id',
				dataIndex : 'id',
				width : 0,
				hidden : true
			}, {
				text : '订单编号',
				dataIndex : 'ORDER_CODE',
				align : 'left',
				width : 150
			},{
				text:'关联订单',
				dataIndex:'P_ORDER_CODE',
				align:'left',
				width : 150
			}, {
				text:'售达方',
				dataIndex:'SHOU_DA_FANG',
				align : 'left',
				width : 100
			},{
				text:'售达方名称',
				dataIndex:'NAME',
				align:'left',
				width:100
			},{
				text : 'SAP订单编号',
				dataIndex : 'SAP_ORDER_CODE',
				align : 'left',
				width : 120
			},{
				text:'创建日期',
				dataIndex : 'ORDER_DATE',
				align:'left',
				width:120
			}],
			dockedItems : [ {
				xtype : 'pagingtoolbar',
				store : 'print.BarCodePrintStore',
				dock : 'bottom',
				displayInfo : true,
				displayMsg : "显示 {0} -{1}条，共{2} 条",
				border : false,
				items : [ '-', '每页', {
					xtype : 'combobox',
					editable : false,
					width : 55,
					listeners : {
						'render' : function(comboBox) {
							var grid = comboBox.ownerCt.ownerCt.items.items[0];
							comboBox.setValue(this.store.pageSize);
						},
						'select' : function(comboBox) {
							var grid = comboBox.ownerCt.ownerCt.items.items[0];
							grid.getStore().pageSize = comboBox.getValue();
							grid.getStore().load({
								params : {
									start : 0,
									limit : comboBox.getValue()
								}
							});
						}
					},
					store : Ext.create('Ext.data.Store', {
						fields : [ 'id', 'name' ],
						data : [ {
							'id' : 25,
							'name' : 25
						}, {
							'id' : 50,
							'name' : 50
						}, {
							'id' : 100,
							'name' : 100
						}, {
							'id' : 200,
							'name' : 200
						}, {
							'id' : 500,
							'name' : 500
						} ]
					}),
					displayField : 'name',
					valueField : 'id'
				}, '条' ]
			} ]
		});
		
		// 生成页面
		Ext.apply(this, {
			items : [ form,grid ]
		});

		this.callParent(arguments);
	}

});