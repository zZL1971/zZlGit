Ext.define("SMSWeb.view.su.NewSysUserForm", {
			extend : 'Ext.form.Panel',
			alias : 'widget.NewSysUserForm',// 面板的别名
			id:'NewSysUserForm',
			border : false,
			bodyPadding:'5px',
			bodyStyle : "background-color: #D0DEF0;border-top:0px",
				fieldDefaults: {
				labelWidth: 105,
				labelAlign: "left",
				flex:1,
				width:320,
				labelStyle:'padding:2px;'
		    },
		    defaultType : 'textfield',
			defaults : {
				blankText : "必填字段，不能为空",
				allowBlank : false
			},
			items : [
					{
						name : 'id',
						xtype:'hiddenfield'
					},{
						xtype:'textfield',
						fieldLabel : '登陆账户',
						name : 'loginNo'
					},{
						fieldLabel : '密码',
						name : 'password'
					},{
						fieldLabel : '姓名',
						name : 'userName'
					},{  
			            allowBlank : true,
			            fieldLabel : '人员编号',
			            name : 'userNo'
			         },{
			            xtype : 'radiogroup',
			            fieldLabel : '性别',
			            items : [{
			                  width:100,
			                  boxLabel : '男',
			                  name : 'sex',
			                  inputValue : '0',
			                  checked : true
			                }, {
			                  width:100,
			                  boxLabel : '女',
			                  name : 'sex',
			                  inputValue : '1'
			                }]
			          },{  
			            allowBlank : true,
			            fieldLabel : '所属部门',
			            name : 'deptName'
			          },{  
			            allowBlank : true,
			            fieldLabel : '电话号码',
			            name : 'telphone'
			          },{  
			            allowBlank : true,
			            fieldLabel : '邮件',
			            name : 'email'
			          }
					]
});
