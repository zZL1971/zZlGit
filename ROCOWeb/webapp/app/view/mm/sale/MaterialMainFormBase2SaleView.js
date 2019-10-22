Ext
		.define(
				"SMSWeb.view.mm.sale.MaterialMainFormBase2SaleView",
				{
					extend : 'Ext.ux.form.SearchForm',
					alias : 'widget.MaterialMainFormBase2SaleView',// 面板的别名
					bodyStyle : "background-color: #D0DEF0;padding-top:10px;padding-bottom:5px",
					border : false,
					fieldDefaults : {
						labelWidth : 80,
						labelAlign : "left",
						labelStyle : 'padding-left:10px;'
					},
					layout : {
						type : 'vbox',
						align : 'stretch'
					},
					items : [ {
						layout : 'hbox',
						xtype : 'fieldcontainer',
						items : [
						//											{
								//												xtype:'dictcombobox',
								//												fieldLabel : '产品类型',
								//												name:'mtart',
								//												dict:'MATERIAL_MTART',
								//												emptyText: '请选择...'
								//											},
								{
									xtype : 'textfield',
									maxLength : 25,
									enforceMaxLength : true,
									fieldLabel : '产品编号',
									name : 'matnr',
									hidden : true
								}, {
									xtype : 'textfield',
									maxLength : 25,
									enforceMaxLength : true,
									fieldLabel : '非标序号',
									name : 'serialNumber',
									hidden : true
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
								} ]
					} ]
				});