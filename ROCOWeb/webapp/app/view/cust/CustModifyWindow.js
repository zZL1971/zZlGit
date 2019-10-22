Ext.define("SMSWeb.view.cust.CustModifyWindow", {
	extend : 'Ext.window.Window',
	alias : 'widget.CustModifyWindow',
	title : '修改客户信息',
	height : 200,
	width : 400,
	layout : 'fit',
	modal:true,
	initComponent : function() {
		var me = this;
		var _form;
		var submit = function() {
			var _orderCode=_form.getForm().findField("orderCode").getValue();
			var _kunnrT=_form.getForm().findField("kunnrT").getValue();
			var _kunnrF=_form.getForm().findField("kunnrF").getValue();
			if(_kunnrT){
				if(!_orderCode && !kunnr){
					Ext.Msg.alert("提示","请输入订单号或者售达方");
					return;
				}
			}else{
				Ext.Msg.alert("提示","请选择目标送达方");
				return;
			}
			_form.getForm().submit({
				waitMsg : '正在提交数据',
				waitTitle : '提示',
				url : 'main/cust/modifyBatch',
				method : 'POST',
				success : function(form, action, opts) {
					Ext.Msg.alert('提示', '保存成功');
					_form.getForm().reset();
					me.close();
				},
				failure : function(form, action) {
					Ext.Msg.alert('提示', action.result.errorMsg);
				}
			});
		};
		_form = Ext.create('Ext.form.Panel', {
			bodyPadding : 5,
			width : 350,

			layout : {
				type : 'table',
				columns : 1
			},
			items : [ {
				xtype : 'textfield',
				fieldLabel : '订单编号',
				name : 'orderCode',
				width : 250,
				itemId : 'orderCode'
			}, {
				xtype : 'tablecombobox',
				fieldLabel : "经销商编码",
				table : "cust_header",
				dataid : "kunnr",
				datatext : "name1",
				name : "kunnr",
				width : 250,
				itemId : 'kunnrR',
				showKey : true
			}, {
				xtype : 'tablecombobox',
				fieldLabel : "原物流编号",
				name : "kunnrF",
				table : "cust_header",
				dataid : "kunnr",
				datatext : "name1",
				width : 250,
				itemId : 'kunnrF',
				showKey : true
			}, {
				xtype : 'tablecombobox',
				fieldLabel : "目标物流编号",
				name : "kunnrT",
				table : "cust_header",
				dataid : "kunnr",
				datatext : "name1",
				itemId : 'kunnrT',
				width : 250,
				showKey : true
			} ],
			buttons : [ {
				text : '提交',
				handler : submit
			} ]

		});

		Ext.apply(this, {
			items : [ _form ]
		});
		this.callParent(arguments);
	}

});