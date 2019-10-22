Ext.define("SMSWeb.view.su.SysUserMainView", {
			extend : 'Ext.panel.Panel',
			alias : 'widget.SysUserMainView',// 面板的别名
			init : function() {},
			layout:'border',
			border:false,
			items:[{
				tbar : [{
						xtype : 'button',
						text : 'New',
						id : 'newSysUser',
						iconCls:'table_add'
						
					},{
						xtype : 'button',
						text : 'Delete',
						id : 'deleteSysUser',
						iconCls:'table_remove'
						},{
						xtype : 'button',
						text : 'Query',
						id : 'querySysUser',
						iconCls:'table_search'
					},{
						xtype : 'button',
						text : 'Guide',
						id : 'guideSysUser',
						iconCls : "guide"
					}]
					,
					    region:'north',
						xtype:'SysUserFormView'
					},{
						xtype:'SysUserGridView',
						region:'center'
			}]
		});