Ext.define("MeWeb.controller.system.role.SysRoleController", {
			extend : 'Ext.app.Controller',
			views : ['system.role.SysRoleMainView',
					'system.role.SysRoleGridView','system.role.UAWin'],
			stores : ['system.role.SysRoleStore','system.menu.SysMenuStore'],
			models : ['system.role.SysRoleModel'],
			refs : [{
						ref : 'roleTree',
						selector : 'roleTree'
					},{
						ref : 'sysRoleForm',
						selector : 'sysRoleForm'
					},{
						ref : 'sysRoleWindow',
						selector : 'sysRoleWindow'
					}],
			init : function() {
				var me = this;
				me.control({
					'roleTree':{
						itemEditButtonClick:function(tree,rowIndex,colIndex){
							var z_ = me.getRoleTree();
							var record = tree.getStore().getAt(rowIndex);
							Ext.create("MeWeb.view.system.role.UAWin",{roleRecord:record}).show(this);
						}
					}
				});
			}
		});