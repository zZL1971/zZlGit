Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.system.role.SysRoleWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.sysRoleWindow',
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
						id : 'saveSysRole',
						iconCls : 'table_save'
					}],
			items : [{
				xtype:'sysRole.Form'
			}]
		});