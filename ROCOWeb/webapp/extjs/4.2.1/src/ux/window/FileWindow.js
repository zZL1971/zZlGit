Ext.define('Ext.ux.window.FileWindow', {
	extend : 'Ext.window.Window',
	title : '文件上传',
	layout : 'border',
	modal : true,
	height : 200,
	width : 450,
	isImport:true,
	formParams:{},
	fileType:'FILE',
	listeners : {
		boforeCommit : function(form, flowtype, nextflowId) {
			return true;
		},
		afterCommit : function() {
			return true;
		}
	},
	initComponent : function() {
		var me = this;

		var form = Ext.widget('form', {
			region : 'center',
			itemId : 'fileWindowForm',
			border : false,
			bodyStyle : "padding:5px;",
			defaults : {
				anchor : '100%',
				msgTarget : 'side',
				labelWidth : 50
			},
			buttons : [{
				text : '上传',
				handler : function(grid, rowIndex, colIndex) {
					var form_ = this.up("form").getForm();
					var url_ = "";
					if(me.isImport){
						url_ ="main/mm/execlImp";
					}else{
						url_ : 'main/mm/execlImp2';
					}
					
					if (form_.isValid()) {
						form_.submit({
									url : url_,
									waitMsg : '上传文件中...',
									params:me.formParams,
									success : function(form, action) {
										var values = Ext.decode(action.response.responseText);
										Ext.MessageBox.alert("提示",values.msg);
										var afterCommit = me.fireEvent("afterCommit");
										if(afterCommit){
											me.close();
										}
									},
									failure : function(form, action) {
										var values = Ext.decode(action.response.responseText);
										Ext.MessageBox.alert("提示"+values.errorCode, values.errorMsg);
									}
								});
					}
				}
			}],
			items : [/*{
						value : me.mappingId,
						xtype : 'hiddenfield',
						name : 'mappingId'
					}, */{
						value : me.fileType,
						xtype : 'hiddenfield',
						name : 'fileType'
					}, {
						xtype : 'filefield',
						fieldLabel : '文件',
						emptyText : '请选择一个文件',
						name : 'file',
						allowBlank : false,
						buttonText : '选择文件',
						blankText : '选择文件'
					}, {
						xtype : 'textarea',
						fieldLabel : '备注',
						name : 'remark'
					}]
		});

		Ext.apply(me, {
					items : [form]
				});

		this.callParent(arguments);
	}
});