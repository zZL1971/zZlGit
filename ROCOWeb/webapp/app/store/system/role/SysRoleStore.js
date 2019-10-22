Ext.define('MeWeb.store.system.role.SysRoleStore', {
	extend : 'Ext.data.TreeStore',
	model : 'MeWeb.model.system.role.SysRoleModel',
	proxy : {
		type : 'ajax',
		url:'/core/role/tree',
		reader:'json',
		root: {
            expanded: true
        },
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad: true,
	root: {
        expanded: true/*,
        DeptName: "总公司",
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