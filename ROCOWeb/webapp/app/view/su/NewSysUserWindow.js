Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.su.NewSysUserWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewSysUserWindow',
			height : 400,
			formId:null,
			maximizable:true,
			width : 500,
			modal:true,
			constrainHeader: true,
			layout:'fit',
			tbar : [{
						xtype : 'button',
						text : 'Save',
						id : 'saveSysUser',
						iconCls : 'table_save'
					}],
			items : [{
				xtype:'NewSysUserForm'
			}]
		});