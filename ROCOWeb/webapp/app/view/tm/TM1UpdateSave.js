Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.TM1UpdateSave', {
	extend : 'Ext.window.Window',
	alias : 'widget.TM1UpdateSave',
	requires : [ "Ext.ux.ButtonTransparent" ],
	itemId : 'TM1UpdateSave_ItemId',
	maximizable : true,
	id:'TM1UpdateSave',
	saleId:null,
	height : 480,
	width : 1200,
	bodyStyle : {
		'background-color' : '#f6f6f6'
	},
	border : 0,
	dataRecord : null,
	orderCode:null,
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
		 var cczbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'cczb',
			 dict:'CCZB'
		 });
		 var cclbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'zzelb',
			 dict:'CCLB'
		 });
		 var zzezxCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'zzezx',
			 dict:'CCZX'
		 });
		 var problemCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'problem',
			 dict:'PROBLEMS'
		 });
		 var zzebmCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'zzebm',
			 dict:'CCBM'
		 });
		 var ccwtCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
			 name:'zzelb',
			 dict:'CCWT'
		 });
		 var wtlxCombobox = Ext.create("Ext.ux.form.DictCombobox",{
			 emptyText: '请选择...',
//			 name:'zzelb',
			 dict:'WTLX'
		 });
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
				},{
					xtype : 'textfield',
					name : 'orderCodePosex',
					fieldLabel : '原单行号',
					readOnly : true
				},{
					xtype : 'textfield',
					name : 'tousucishu',
					fieldLabel : '投诉次数',
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
					name : 'color',
					fieldLabel : '颜色'
					/*xtype : 'dictcombobox',
					name : 'zzelb',
					dict:'CCLB',
					fieldLabel : '问题类别',
					editor:cclbCombobox,
	 	             renderer : function(value, meta, record) {
	            		var find= cclbCombobox.getStore().findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }*/
				},{
					xtype : 'textfield',
					name : 'cabinetName',
					fieldLabel : '柜名'
				} ]
			}, {
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'dictcombobox',
					name : 'salefor',
					dict:'SALE_FOR',
					fieldLabel : '产品组',
					editor:cczbCombobox,
	 	             renderer : function(value, meta, record) {
	            		var find= cczbCombobox.getStore().findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }

				}, {
					xtype : 'textfield',
					name : 'cpmc',
					fieldLabel : '产品名称'
				},{
					xtype : 'dictcombobox',
					name : 'zzccz',
					fieldLabel : '出错组',
					dict:'CCZBN',
				},{
					xtype : 'dictcombobox',
					name : 'zzcclx',
					fieldLabel : '问题类型',
					dict:'WTLX',
					editor:wtlxCombobox,
					renderer : function(value, meta, record) {
		            		var find= wtlxCombobox.findRecord("id",value);
		            		if(find){
		            			return find.get('text');
		            		}else{
		            			return value;
		            		}
			            }
				} ]
			},{
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items : [ {
					xtype : 'dictcombobox',
					name : 'zzezx',
					fieldLabel : '出错中心',
					dict:'CCZX',
					editor:zzezxCombobox,
	 	             renderer : function(value, meta, record) {
	            		var find= zzezxCombobox.findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }
				}, {
					xtype : 'dictcombobox',
					name : 'zzebm',
					fieldLabel : '出错部门',
					dict:'CCBM',
					editor:zzebmCombobox,
	 	             renderer : function(value, meta, record) {
	            		var find= zzebmCombobox.findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }
				},{

					xtype : 'dictcombobox',
					name : 'problem',
					fieldLabel : '问题情况',
					dict:'PROBLEMS',
					editor:problemCombobox,
	 	             renderer : function(value, meta, record) {
	            		var find= cczbCombobox.getStore().findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }
				},{
					xtype : 'dictcombobox',
					name : 'zzelb',
					fieldLabel : '出错问题',
					dict:'CCWT',
					editor:ccwtCombobox,
					renderer : function(value, meta, record) {
	            		var find= ccwtCombobox.getStore().findRecord("id",value);
	            		if(find){
	            			return find.get('text');
	            		}else{
	            			return value;
	            		}
		            }
				} ]
			
			}, {
				xtype : 'textareafield',
				rows : 2,
				name : 'zztsnr',
				anchor : '80%',
//				maxLength : 20,
				emptyText : '投诉内容不得超过20个字..',
				fieldLabel : '投诉内容'
			},
			{
				xtype : 'textareafield',
				name : 'zzccwt',
				rows : 2,
				anchor : '80%',
				hidden:true,
				fieldLabel : '出错问题',
				maxLength : 20,
				emptyText : '出错问题不得超过20个字..'
			},
			{
				xtype : 'textareafield',
				name : 'saleId',
				value:me.saleId,
				hidden:true,
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
		function loadData(saleId) {
			if (!saleId) {
				return;
			}
			var myMask = new Ext.LoadMask(me, {
				msg : "请稍等..."
			});
			myMask.show();
			Ext.Ajax.request({
				url : 'main/mm/findNewOrderToCust?saleId=' + saleId,
				async : false,
				dataType : "json",
				success : function(response, opts) {
					var values = Ext.decode(response.responseText);
					if (values.success) {
						var formData = values.data;
						var form = Ext.getCmp("Tm2stForm").getForm();
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