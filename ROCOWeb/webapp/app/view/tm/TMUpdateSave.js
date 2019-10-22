Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.TMUpdateSave', {
	extend : 'Ext.window.Window',
	alias : 'widget.TMUpdateSave',
	requires : [ "Ext.ux.ButtonTransparent" ],
	itemId : 'TMUpdateSave_ItemId',
	maximizable : true,
	id:'TMUpdateSave',
	height : 480,
	width : 800,
	bodyStyle : {
		'background-color' : '#f6f6f6'
	},
	border : 0,
	dataRecord : null,
	modal : true,
	border : false,
	layout : 'border',
	tbar : [ {
		xtype : 'button',
		text : '保存',
		itemId : 'saveKf',
		iconCls : 'table_save'
	} ],
	initComponent : function() {
		var me = this;
		// 保存按钮不能使用 如果设置为不能修改
		me.tbar[0].hidden = (this.canModify == null ? false : this.canModify);
		me.items = [ {
			xtype : 'form',
			id : 'Tm1stForm',
			region : 'north',
			bodyStyle : {
				'background-color' : '#f6f6f6'
			},
			bodyPadding : 5,
			border : false,
			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 100,
				msgTarget : 'qtip'
			},
			defaultType : 'textfield',
			layout : 'anchor',
			defaults : {
				anchor : '100%'
			},
			items : [ {
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'textfield',
					name : 'id',
					fieldLabel : 'Id',
					readOnly : true,
					hidden:true
				} ]
			},{
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'textfield',
					name : 'orderCode',
					fieldLabel : '订单编号',
					readOnly : true
				} ]
			}, {
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'textfield',
					name : 'duty',
					fieldLabel : '责任人'
				}, {
					xtype : 'textfield',
					name : 'zzccz',
					fieldLabel : '出错组'
				} ]
			}, {
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'textfield',
					name : 'zzebm',
					fieldLabel : '出错部门'

				}, {
					xtype : 'textfield',
					name : 'zzezx',
					fieldLabel : '出错中心'
				} ]
			}, {
				xtype : 'textareafield',
				rows : 2,
				name : 'zztsnr',
				anchor : '80%',
				maxLength : 20,
				emptyText : '投诉内容不得超过20个字..',
				fieldLabel : '投诉内容'
			},

			{
				xtype : 'textareafield',
				name : 'zzccwt',
				rows : 2,
				anchor : '80%',
				fieldLabel : '出错问题',
				maxLength : 20,
				emptyText : '出错问题不得超过20个字..'
			}

			]
		} ];
		me.listeners = {
			show : function() {
				if (me.dataRecord) {
					setTimeout(function() {
						loadData(me.dataRecord)
					}, 500);
				}
			}
		}
		function loadData(orderCode) {
			if (!orderCode) {
				return;
			}
			var myMask = new Ext.LoadMask(me, {
				msg : "请稍等..."
			});
			myMask.show();
			Ext.Ajax.request({
				url : 'main/mm/findOrderToCust?orderCode=' + orderCode,
				async : false,
				dataType : "json",
				success : function(response, opts) {
					var values = Ext.decode(response.responseText);
					if (values.success) {
						var formData = values.data;
						var form = Ext.getCmp("Tm1stForm").getForm();
						form.setValues(formData);
						console.log(form.getValues());
					} else {
						Ext.MessageBox.alert("提示信息", values.errorMsg);
					}

				},
				failure : function(response, opts) {
					Ext.MessageBox
							.alert("提示信息", "加载失败" + response.responseText);

				}
			});
			if (myMask != undefined) {
				myMask.hide();
			}
		}
		Ext.each(me.items,
				function(continer) {
					allDisabled(continer, (me.canModify == null ? false
							: me.canModify));
				});

		me.callParent(arguments);
	}

});

function allDisabled(continer, isdisabled) {
	if (continer != undefined && continer.items != undefined
			&& continer.items.length > 0) {
		Ext.each(continer.items, function(item) {
			allDisabled(item, isdisabled);
		});
	} else {
		if ('grid' != continer.xtype) {
			continer.disabled = isdisabled;
		} else {
			allDisabled(continer.dockedItems[0], isdisabled);
		}
	}
}