Ext.define("SMSWeb.view.system.role.SysRoleForm", {
			extend : 'Ext.form.Panel',
			alias : 'widget.sysRoleForm',// 面板的别名
			bodyPadding:5,
			autoScroll:true,
			fieldDefaults : {
				bodyStyle :"background-color:#D0DEF0;border:1px solid #D0DEF0;",
				labelWidth : 80,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;',
				flex : 1
			},
			items : [
					{
						name : 'id',
						xtype:'hiddenfield'
					},
					{
						layout : 'hbox',
							xtype : 'fieldcontainer',
							items : [{
								fieldLabel : 'Name',
								allowBlank:false,
								maxLength:27,
								enforceMaxLength:true,
								labelAlign:'left',
								name : 'name'
							}]
					},{
						layout : 'hbox',
						xtype : 'fieldcontainer',
						items : [{
							xtype : 'textfield',
							fieldLabel : '备注',
							allowBlank:false,
							maxLength:27,
							enforceMaxLength:true,
							labelAlign:'left',
							name : 'remark'
						}]
					}
					]
});
