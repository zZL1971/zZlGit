Ext.define("MeWeb.store.system.user.SysUserStore",{
	extend:'Ext.data.Store',
	model:'MeWeb.model.system.user.SysUserModel',
	sortRoot:'userNo',
	remoteSort: true,//远程排序
	autoLoad:true
});