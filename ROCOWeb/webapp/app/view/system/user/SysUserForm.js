Ext.define("MeWeb.view.system.user.SysUserForm", {
	extend : 'Ext.form.Panel',
	alias : 'widget.sysUserForm',// 面板的别名
	bodyPadding : '5 5 0 5',
	fieldDefaults : {
		labelAlign : 'left',
		labelWidth : 90,
		anchor : '100%',
		flex : 1
	},
	requires : ['Ext.ux.form.DisplayPopupField'],
	boder : false,
	defaultType : "textfield",
	defaults : {
		margins : '0'
	},
	tbar : [{
				xtype : 'button',
				text : '保存',
				iconCls : 'table_save',
				handler : function(btn, e) {
					var me = this;
					me.up('form').submit({
						waitMsg : '请稍候',
						waitTitle : '正在保存数据...',
						url : '/core/user/save',
						success : function(form, action) {
							alert(43);
						},
						failure : function(form, action) {

						}
					});
				}
			}],
	items : [{
				name : 'id',
				fieldLabel : '登陆账号',
				allowBlank : false
			}, {
				name : 'userName',
				fieldLabel : '用户名',
				allowBlank : false
			}, {
				name : 'password',
				inputType : 'password',
				fieldLabel : '密码',
				allowBlank : false
			}, {
				name : 'userType',
				fieldLabel : '用户类型',
				hidden : true
			}, {
				name : 'sex',
				fieldLabel : '性别',
				hidden : true
			}/*, {
				name : 'sex3',
				fieldLabel : '测试',
				xtype:'displaypopupfield',
				value:'测试中华人民共和国万八丹V587',
				popupUrl:'xxxxxxx',
				sourceRef:{
					ref:'SMSWeb.view.cust.NewCustWindow',
					params:{formId :"",title:'客户信息',editFlag:true}
				}
			}*/, {
				name : 'userNo',
				fieldLabel : '用户编号',
				hidden : true
			}, {
				name : 'email',
				fieldLabel : '邮箱',
				vtype : 'email',
				allowBlank : false
			}, {
				name : 'kunnr',
				fieldLabel : 'SAP客户编码'
			}, {
				title : "系统信息",
				xtype : "fieldset",
				bodyPadding : 5,
				collapsible : true,
				collapsed : false, // 默认收缩
				defaults : {
					labelSeparator : "：",
					labelWidth : 65,
					anchor : '100%',
					fieldStyle : 'background:#E6E6E6'
				},
				defaultType : "textfield",
				items : [{
							fieldLabel : '创建人',
							name : 'createUser',
							readOnly : true
						}, {
							fieldLabel : '创建时间',
							name : 'createTime',
							xtype : 'datefield',
							format : 'Y-m-d H:i:s',
							readOnly : true
						}, {
							fieldLabel : '更新人',
							name : 'updateUser',
							readOnly : true
						}, {
							fieldLabel : '更新时间',
							name : 'updateTime',
							xtype : 'datefield',
							format : 'Y-m-d H:i:s',
							readOnly : true
						}]
			}]
});
