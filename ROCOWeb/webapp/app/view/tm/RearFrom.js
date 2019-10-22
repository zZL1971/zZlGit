Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.RearFrom', {
	extend : 'Ext.window.Window',
	alias : 'widget.RearFrom',
	requires : [ "Ext.ux.ButtonTransparent" ],
	itemId : 'RearFrom_ItemId',
	maximizable : true,
	id:'RearFrom_Id',
	height : 150,
	width : 300,
	bodyStyle : {
		'background-color' : '#f6f6f6'
	},
	border : 0,
	dataRecord :null,
	modal : true,
	border : false,
	layout : 'border',
	tbar : [ {
		xtype : 'button',
		text : '保存',
		itemId : 'saveRear',
		iconCls : 'table_save'
	} ],
	initComponent : function() {
		var me = this;
		// 保存按钮不能使用 如果设置为不能修改
		me.tbar[0].hidden = (this.canModify == null ? false : this.canModify);
		me.items = [ {
			xtype : 'form',
			id : 'RearFrom',
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
			/*defaults : {
				anchor : '100%'
			},*/
			items : [{
				xtype : 'textfield',
				name : 'uid',
				value:me.dataRecord,
				hidden:true
			},{
				xtype : 'dictcombobox',
				dict:'TASK_GROUP',
				name : 'rear',
				//multiSelect:true,
				emptyText : '必须选择地区',
				fieldLabel : '地区'
			}
			]
		} ];
		me.listeners = {
			show : function() {
				if (me.dataRecord) {
					loadData(me.dataRecord);
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
				url : 'core/task/rear/'+orderCode,
				async : false,
				dataType : "json",
				success : function(response, opts) {
					var values = Ext.decode(response.responseText);
					if (values.success) {
						var formData = values.data;
						var form = Ext.getCmp("RearFrom").getForm();
						var list=new Array(formData.length);
						for(var data=0;data<formData.length;data++){
							list[data]=formData[data].REAR;
							
						}
						form.findField("rear").setValue(list);
						//form.setValues(formData);
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