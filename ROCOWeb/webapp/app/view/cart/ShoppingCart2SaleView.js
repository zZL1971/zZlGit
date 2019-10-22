Ext
		.define(
				"SMSWeb.view.cart.ShoppingCart2SaleView",
				{
					extend : 'Ext.ux.form.SearchForm',
					alias : 'widget.ShoppingCart2SaleView',// 面板的别名
					bodyStyle : "background-color: #D0DEF0;padding-top:10px;padding-bottom:5px",
					border : false,
					fieldDefaults : {
						labelWidth : 80,
						labelAlign : "left",
						labelStyle : 'padding-left:10px;',
						width: 235
					},
					layout : {
						type : 'table',
						align : 'stretch'
					},
					items : [ {
						layout : 'hbox',
						xtype : 'fieldcontainer',
						items : [
								 {
									xtype : 'textfield',
									maxLength : 25,
									enforceMaxLength : true,
									fieldLabel : '非标序号',
									name : 'serialNumber',
								}, {
									xtype : 'textfield',
									maxLength : 25,
									enforceMaxLength : true,
									fieldLabel : '产品描述',
									name : 'maktx'
								}, {
									xtype : 'hiddenfield',
									enforceMaxLength : true,
									fieldLabel : 'type',
									name : 'type'
								}, {
									xtype : 'hiddenfield',
									maxLength : 25,
									enforceMaxLength : true,
									fieldLabel : 'mtartSJ',
									name : 'mtartSJ'
								},
								// 销售方式
								{
									xtype : 'hiddenfield',
									enforceMaxLength : true,
									fieldLabel : 'saleFor',
									name : 'saleFor'
								} 
						]
					} ]
				});