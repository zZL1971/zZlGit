Ext.define('Ext.ux.TreePickerT', {
	extend : 'Ext.form.field.Picker',
	alias : 'widget.departmentfield',
	displayField : "name",
	valueField : "id",
	editable : false,
	submitValue : false,
	emptyText : "--选择部门--",
	hasHandler : false,// 自己操作的时候就不进行自动加载值了
	initComponent : function() {// 添加隐藏域
		var self = this;
		self.hiddenField = Ext.create('Ext.form.field.Hidden', {
					name : self.name,
					listeners : {
						change : function(cmp, newValue, oldValue, eOpts) {
							if (!parseInt(newValue) && self.hasHandler == false) {
								self.setRawValue(newValue);
								cmp.setRawValue("");
							}
						}
					}
				});
		Ext.apply(self, {
					name : self.name + "Display"
				});
		self.callParent();
		self.on('added', function(c, container, pos, eOpts) {
					self.ownerCt.add(self.hiddenField);
				}, this);
	},
	createPicker : function() {// 创建picker选择框
		var self = this;
		var picker = Ext.create('Ext.tree.Panel', {
			rootVisible : false,
			floating : true,
			displayField : self.displayField,
			height : 200,
			store : Ext.create("Ext.data.TreeStore", {
				fields : [{
							name : self.displayField
						}, {
							name : self.valueField
						}],
				autoLoad : true,
				autoDestroy : true,
				root : {
					name : "组织机构"
				},
				proxy : {
					type : 'ajax',
					url : "DepartmentServlet?action=getDepartmentByParent&parentId=0"
				}
			}),
			listeners : {
				scope : self,
				itemclick : function(view, record, node, rowIndex, e) {
					self.hasHandler = true;
					self.picker.hide();
					self.setRawValue(record.get(self.displayField));
					self.hiddenField.setValue(record.get(self.valueField));
					self.fireEvent('select', self, record);
				}
			}
		});
		return picker;
	}
});