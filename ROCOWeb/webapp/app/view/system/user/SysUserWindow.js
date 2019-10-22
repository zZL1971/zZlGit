Ext.tip.QuickTipManager.init();
Ext.define('MeWeb.view.system.user.SysUserWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.sysUserWindow',
			formId:null,
			maximizable:true,
			width:400,
			modal:true,
			title:'系统用户',
			constrainHeader: true,
			layout:'fit',
			border:false,
			initComponent : function() {
				var me = this;
				
				var from = Ext.create("MeWeb.view.system.user.SysUserForm");
				
				me.items=[from]	
				me.callParent(arguments);
			}
		});