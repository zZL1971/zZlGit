Ext.define("SMSWeb.view.cust.CustMainView", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.CustMainView',// 面板的别名
	layout : 'border',
	border : false,
		requires:["Ext.ux.form.TableComboBox","Ext.ux.ButtonTransparent"],
	//bodyStyle:'overflow-y:auto;overflow-x:hidden',
	autoScroll : true,
	items : [ {
		tbar : [ {
			xtype : 'button',
			text : '查询',
			itemId : 'queryCust',
			iconCls : 'table_search'
		},
		//						{
				//							xtype : 'button',
				//				            text: '增加',
				//				            iconCls: 'table_add',
				//				            id : 'newCust'
				//				        },
				{
					xtype : 'button',
					text : '同步SAP信息',
					iconCls : 'table_add',
					id : 'syncCust'
				},{
		        	xtype : 'buttontransparent',
		        	text : '导出',
		        	id:'export',
		        	icon:'/resources/images/down.png',
		        	//hidden: "1"==queryType ?false:true
		        	hidden:false
		        },
//				{
//					xtype : 'button',
//					text : '同步农行信息',
//					iconCls : 'table_add',
//					id : 'syncBank'
//				},
//		        {
//					xtype : 'button',
//					text : '同步建行信息',
//					iconCls : 'table_add',
//					id : 'syncjhBank'
//				},
//		        {
//					xtype : 'button',
//					text : '会计凭证',
//					iconCls : 'table_add',
//					id : 'doco'
//				},
				{
					xtype : 'button',
					text : '绑定银行账号',
					iconCls : 'table_add',
					id : 'updateBank',
					listeners:{
						click:function(){
							Ext.create('SMSWeb.view.cust.CustFileUploadBaseWindow',{formId : id,title:"文件上传"}).show();
						}
					}
				},
				{
					xtype : 'button',
					text : '绑定客户折扣',
					iconCls : 'table_add',
					id : 'updateCustZhekou',
					listeners:{
						click:function(){
							Ext.create('SMSWeb.view.cust.CustZhekouFileUploadBaseWindow',{formId : id,title:"文件上传"}).show();
						}
					}
				}
//				,{
//					xtype : 'tablecombobox',
//					fieldLabel : "经销商编码",
//					name : "kunnr",
//					table : "cust_header",
//					dataid : "kunnr",
//					datatext : "name1",
//					width : 250,
//					itemId : 'kunnrR',
//					showKey : true
//				},'-', {
//					xtype : 'tablecombobox',
//					fieldLabel : "物流编码",
//					name : "kunnrFrom",
//					table : "cust_header",
//					dataid : "kunnr",
//					datatext : "name1",
//					width : 250,
//					itemId : 'kunnrF',
//					showKey : true
//				}, '-->', {
//					xtype : 'tablecombobox',
//					fieldLabel : "物流编码",
//					name : "kunnrTo",
//					table : "cust_header",
//					dataid : "kunnr",
//					datatext : "name1",
//					itemId : 'kunnrT',
//					width : 250,
//					showKey : true
//				}, {
//			xtype : 'button',
//			text : '更改客户信息',
//			id : 'cusModBtn'
//		}
				],
		region : 'north',
		xtype : 'CustFormView'
	}, {
		region : 'center',
		xtype : 'CustGridView'
	} ]
});