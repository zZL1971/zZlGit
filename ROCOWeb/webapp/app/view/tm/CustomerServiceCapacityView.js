Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.CustomerServiceCapacityView', {
	extend : 'Ext.window.Window',
	alias : 'widget.CustomerServiceCapacityView',
	requires : [ "Ext.ux.ButtonTransparent" ],
	itemId : 'CustomerServiceCapacityView_ItemId',
	maximizable : true,
	id:'CustomerServiceCapacityId',
	saleId:null,
	height : 480,
	width : 1200,
	bodyStyle : {
		'background-color' : '#f6f6f6'
	},
	border : 0,
	assignee:null,
	ytime:null,
	modal : true,
	border : false,
	layout : 'border',
/*	tbar : [ {
		xtype : 'button',
		text : '保存',
		itemId : 'saveKf',
		iconCls : 'table_save'
	} ],*/
	initComponent : function() {
		var me = this;
		// 保存按钮不能使用 如果设置为不能修改
//		me.tbar[0].hidden = (this.canModify == null ? false : this.canModify);
		me.items = [ {
			xtype : 'form',
			id : 'Tm2stForm',
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
			}, 
			{
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [{
					xtype : 'textareafield',
					name : 'cccount',
					rows : 2,
					anchor : '80%',
					width:550,
					height:600,
					fieldLabel : '订单审绘退单数量'
				},{
					xtype : 'textareafield',
					name : 'zcount',
					rows : 2,
					width:600,
					height:600,
					anchor : '80%',
					fieldLabel : '客服审核订单数'
				} ]
			}
			]
		} ];
		me.listeners = {
			show : function() {
				if (me.assignee,me.ytime) {
					setTimeout(function() {
						loadData(me.assignee,me.ytime)
					}, 500);
				}
			}
		}
		function loadData(assignee,ytime) {
			if (!assignee) {
				return;
			}
			var myMask = new Ext.LoadMask(me, {
				msg : "请稍等..."
			});
			myMask.show();
			Ext.Ajax.request({
				url : 'main/mm/findCustomerServiceCapacity',
					params : {
						assignee : assignee,
						ytime:ytime
					},
				async : false,
				dataType : "json",
				success : function(response, opts) {
					debugger;
					var values = Ext.decode(response.responseText);
					if (values.success) {
						var formData = values.data;
						var form = Ext.getCmp("Tm2stForm").getForm();
						form.setValues(formData);
						//console.log(form.getValues());
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