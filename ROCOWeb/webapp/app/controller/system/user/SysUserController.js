Ext.define("MeWeb.controller.system.user.SysUserController", {
			extend : 'Ext.app.Controller',
			views : ['system.user.SysUserMainView',
					'system.user.SysUserGridView',
					"system.user.SysUserSearchForm"],// 引入页面所需展现层组件
			stores : ['system.user.SysUserStore'],// 引入页面所需数据层组件
			models : ['system.user.SysUserModel'],// 引入页面所需实体层组件
			refs : [{
						ref : 'userSearchForm',
						selector : 'userSearchForm'
					},{
						ref:'userGrid',
						selector : 'userGrid'
					},{
						ref:'SysUserMainView',
						selector : 'SysUserMainView'
					}],// 注入别称属性【alias】的组件，便于系统自动成get，set方法，类似于后台定义变量
			init : function() {
				var me = this;
				// 全局控制器事件
				me.control({
					"SysUserMainView button[id=query]" : {
						click : function(view, collndex, rowindex,
								item, e, record) {
							var me = this;
							var form = me.getUserSearchForm();
							me.getUserGrid().getStore().loadPage(1,{params:form.getSearchs()});
						}
					},
					"SysUserMainView button[id=add]" : {
						click : function(view, collndex, rowindex,
								item, e, record) {
							var me = this;
							Ext.create('MeWeb.view.system.user.SysUserWindow',{}).show(this);
						}
					},
					'userGrid':{
						itemEditButtonClick:function(grid,rowIndex,colIndex){
							var record = grid.getStore().getAt(rowIndex);
							alert(record.getId());
							//Ext.create('SRM.view.trie.DictForm',{dictId:record.getId()}).show(grid);
						}
					}
				});
			}
		});