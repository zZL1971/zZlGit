Ext.define('MeWeb.store.system.menu.SysMenuStore', {
	extend : 'Ext.data.TreeStore',
	model : 'MeWeb.model.system.menu.SysMenuModel',
	proxy : {
		type : 'ajax',
		//actionMethods:'POST',数据查询时参数过大设置该值即可
		url:'/core/role/menu',
		reader:'json',
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad: true,
	root: {
        expanded: true,
        checked:false,
        descZhCn: "系统菜单"/*,
        Leader: "Lin",
        children: [
            { DeptName: "技术部", Leader: "Xia", leaf: false,children: [
            { DeptName: "ddddd", Leader: "Xia", leaf: true },
            { DeptName: "cccc", Leader: "Li", leaf: true }
        ] },
            { DeptName: "财务部", Leader: "Li", leaf: true }
        ]*/
    }
});