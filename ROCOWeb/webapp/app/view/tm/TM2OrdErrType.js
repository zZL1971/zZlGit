Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.tm.TM2OrdErrType', {
	extend : 'Ext.window.Window',
	alias : 'widget.TM2OrdErrType',
	requires : [ "Ext.ux.ButtonTransparent" ],
	itemId : 'TM2OrdErrType_ItemId',
	maximizable : true,
	id:'TM2OrdErrType',
	height : 300,
	width : 500,
	bodyStyle : {
		'background-color' : '#f6f6f6'
	},
	border : 0,
	orderCodePosex:null,
	acoeId:null,
	modal : true,
	border : false,
	layout : 'border',
	tbar : [ {
		xtype : 'button',
		text : '保存',
		itemId : 'saveOrdErrType',
		iconCls : 'table_save'
	} ],
	initComponent : function() {
		var me = this;
		var ordErrType = Ext.create("Ext.ux.form.DictCombobox",{
			dict:'ERROR_ORD_TYPE'
		});
		var yes_no = Ext.create("Ext.ux.form.DictCombobox",{
			dict:'YES_NO'
		});
		me.items = [{
			xtype : 'form',
			id : 'TMoeOrdErrType',
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
			items:[{
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items:{
					xtype : 'dictcombobox',
					name : 'errType',
					dict:'ERROR_ORD_TYPE',
					fieldLabel : '出错类型',
					allowBlank: false,
					editable:false,
					emptyText:this.errTypeDesc,
					editor:ordErrType,
					renderer : function(value, meta, record) {
						var find= ordErrType.getStore().findRecord("id",value);
						if(find){
							return find.get('text');
						}else{
							return value;
						}
					}
				}
			},
			{
				xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items:{
					xtype : 'dictcombobox',
					name : 'tackit',
					dict:'YES_NO',
					fieldLabel : '取消罚单',
					allowBlank: false,
					editable:false,
					emptyText:this.tackit=='1'?'是':'否',
					editor:yes_no,
					renderer : function(value, meta, record) {
						var find= yes_no.getStore().findRecord("id",value);
						if(find){
							return find.get('text');
						}else{
							return value;
						}
					}
				}

			},
			{		xtype : 'fieldcontainer',
				layout : 'hbox',
				combineErrors : true,
				defaultType : 'textfield',
				items:{
					xtype : 'textfield',
					name : 'errDesc',
					fieldLabel : '原因描述',
					allowBlank: false,
					value:this.errDesc,
					editable:false,
				}

			}]
		}];
		me.callParent(arguments);
	}

});

