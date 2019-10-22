Ext.define("SMSWeb.controller.su.SysUserController", {
	extend : 'Ext.app.Controller',
	refs : [{
				ref : 'NewSysUserForm',
				selector : 'NewSysUserForm'},
			{
				ref : 'SysUserGridView',
				selector : 'SysUserGridView'
				}
			],
	init : function() {
		this.control({
			/**
			 * 新增SysUser
			 */
			"SysUserMainView button[id=newSysUser]" : {
				click : function(b) {
					var win = Ext.create('SMSWeb.view.su.NewSysUserWindow').show();
					win.setTitle('Add SysUser');
				}
			},
			"NewSysUserWindow button[id=saveSysUser]" : {
					click : function() {
						var me = this;
						var newSysUserForm = me.getNewSysUserForm();
						var newFormValues = newSysUserForm.getForm().getValues();
						if(newSysUserForm.getForm().isValid()){
							Ext.Ajax.request({
								url : 'core/su/save',
								params : newFormValues,
								method : 'POST',
								frame:true,
								dataType : "json",
								contentType : 'application/json',
								success : function(response, opts) {
									Ext.MessageBox.alert("tips","   Save Successful");
									var grid = me.getSysUserGridView();
									if (grid) {
										grid.getStore().loadPage(1);
									}
									me.getNewBomWindow().close();
								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("tips","   Save Failure");
								}
							});
						}
					}
			}
		});
	},
	views : ['su.SysUserMainView','su.SysUserFormView','su.SysUserGridView',
	         'su.NewSysUserForm','su.NewSysUserWindow'],
	stores : ['su.Store4SysUser'],
	models : ['su.SysUserModel']
});