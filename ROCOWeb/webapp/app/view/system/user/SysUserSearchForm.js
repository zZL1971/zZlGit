Ext.define("MeWeb.view.system.user.SysUserSearchForm", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.userSearchForm',//别称
			xtype : 'user.SearchForm',//声明自定义组件名称
			itemId:'userSearchFormView',
			requires : ['Ext.ux.form.TableComboBox'],
			border : false,
			//bodyStyle : "background-color: #D0DEF0;border-bottom:none;border-top:none;",
			frame : false,
			//margin : '5 5 0 5',
			bodyPadding : '5 5 0 5',
			fieldDefaults : {
				labelAlign : 'left',
				labelWidth : 65,
				labelStyle : 'padding-left:5px;',
				width : 240
			},
			items:[{
				layout : 'hbox',
				xtype : 'fieldcontainer',
				items : [{
	                xtype: 'container',
	                flex: 1,
	                layout: 'hbox',
	                items: [{
	                    xtype:'textfield',
	                    fieldLabel: '登陆账号',
	                    name: 'loginNo'
	                }, {
	                    xtype:'textfield',
	                    fieldLabel: '用户编号',
	                    name: 'userNo'
	                }]
	            }]},
	            {
	            	layout : 'hbox',
					xtype : 'fieldcontainer',
					items : [{
		                xtype: 'container',
		                flex: 1,
		                layout: 'hbox',
		                items: [{
	                    xtype:'textfield',
	                    fieldLabel: '用户名',
	                    name: 'userName'
	                }, {
	                    xtype:'textfield',
	                    fieldLabel: '用户类型',
	                    name: 'userType'
	                }]
			}]
	}]
});