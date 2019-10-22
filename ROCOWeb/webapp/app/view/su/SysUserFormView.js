Ext.define("SMSWeb.view.su.SysUserFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.SysUserFormView',// 面板的别名
			id:'bomFormView',
			bodyStyle : "background-color: #D0DEF0;padding-top:10px;padding-bottom:5px"	,
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
			items : [{
					layout : 'hbox',
					xtype : 'fieldcontainer',
					items : [{
									xtype : 'textfield',
									maxLength:18,
									enforceMaxLength:true,
									fieldLabel : '姓名',
									name :'userName'
					}]
			}]
			
		});