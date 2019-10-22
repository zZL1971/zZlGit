Ext.define("SMSWeb.view.mm.myGoods.MyGoodsBujianMainFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.MyGoodsBujianMainFormView',// 面板的别名
			bodyStyle : "background-color: #D0DEF0;padding-top:10px;padding-bottom:5px"	,
			border : false,
			fieldDefaults : {
    	        width: 300,
				labelWidth : 80,
				labelAlign : "left",
				labelStyle : 'padding-left:10px;'
			},
			layout : {
					type : 'vbox',
					align : 'stretch'
				},
				items : [
					        {
								layout : 'hbox',
								xtype : 'fieldcontainer',
								items : [
									{
										xtype:'dictcombobox',
										fieldLabel : '订单类型',
										name:'ortype',
										dict:'ORDER_TYPE_BD',
										emptyText: '请选择...'
									},
									{
										xtype : 'textfield',
										maxLength:25,
										enforceMaxLength:true,
										fieldLabel : '产品描述',
										name :'maktx'
									}
								]
					        }
				]
			
		});