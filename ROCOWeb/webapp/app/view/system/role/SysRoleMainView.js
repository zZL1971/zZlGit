Ext.define("MeWeb.view.system.role.SysRoleMainView", {
	extend : 'Ext.panel.Panel',
	alias : 'widget.SysRoleMainView',
	layout : 'border',
	border : false,
	items : [{
				xtype : 'role.Tree',
				region : 'center'
			}]
});