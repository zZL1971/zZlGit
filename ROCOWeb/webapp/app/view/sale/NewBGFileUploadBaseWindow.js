Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewBGFileUploadBaseWindow', {
	extend : 'Ext.window.Window',
	alias : 'widget.NewBGFileUploadBaseWindow',
	formId:null,
	fileType:null,
	height : 200,
	width : 450,
	modal:true,
	constrainHeader: true,
	layout:'fit',
	initComponent : function() {
		var me = this;
		var form = Ext.widget('form',{
			region:'center', 
			itemId:'fileUploadForm',
			border : false,
			bodyStyle : "padding:5px;",
			defaults: {
				anchor: '100%',
				msgTarget: 'side',
				labelWidth: 50
			},
			buttons: [{ text: '上传',id:'update',handler : function(grid, rowIndex, colIndex) {
				this.up('window').fireEvent('fileUploadButtonClick');
			} 
			}],
			items:[{
				value:me.formId,
				xtype: 'hiddenfield',
				name:'pid'
			},
			{
				value:me.fileType,
				xtype: 'hiddenfield',
				name:'fileType'
			},
			{
				value:me.saleFor,
				xtype: 'hiddenfield',
				name:'saleFor'
			},
			{
				xtype: 'filefield',
				fieldLabel: '文件',
				emptyText: '请选择一个文件',
				name: 'file',
				allowBlank: false,
				buttonText: '选择文件',
				blankText: '选择文件'
			},{
				xtype: 'textarea',
				fieldLabel: '备注',
				name:'remark'
			}
			],
		});
		Ext.apply(me, {
			items : [form]
		});
		this.callParent(arguments);
	}
});
