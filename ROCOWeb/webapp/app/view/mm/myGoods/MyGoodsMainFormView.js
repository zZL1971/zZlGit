Ext.define("SMSWeb.view.mm.myGoods.MyGoodsMainFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.MyGoodsMainFormView',// 面板的别名
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
										dict:'OR_TYPE',
										emptyText: '请选择...'
									},
									{
										xtype:'dictcombobox',
										fieldLabel : '产品类型',
										name:'mtart',
										dict:'MATERIAL_MTART',
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
					        },{
								layout : 'hbox',
								xtype : 'fieldcontainer',
								items : [
									{
										xtype : 'textfield',
										maxLength:25,
										enforceMaxLength:true,
										fieldLabel : '编号',
										name :'serialNumber'
									},
									{
										xtype : 'textfield',
										maxLength:25,
										enforceMaxLength:true,
										fieldLabel : '产品编号',
										name :'matnr'
									}
								]
					        
					        }
				]
			
		});