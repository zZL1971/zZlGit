Ext.define("SMSWeb.view.su.SysUserGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.SysUserGridView',
	store : 'su.Store4SysUser',
	enableKeyNav : true,
	columnLines : true,
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	dockedItems:[{
		xtype:'pagingtoolbar',
		store:'su.Store4SysUser',
		dock:'bottom',
		border:false,
		displayInfo:true
		}],
	columns : [
 	             {text:'登陆账户',dataIndex:'loginNo',width:150},
 	             {text:'姓名',dataIndex:'userName',width:100},
 	             {text:'工号',dataIndex:'userNo',width:100},
 	             {text:'性别',dataIndex:'sex',width:100},
 	             {text:'所属部门',dataIndex:'deptName',width:100},
 	             {text:'电话号码',dataIndex:'telphone',width:100},
 	             {text:'邮件',dataIndex:'email',width:100}
	           ],
			selType : 'rowmodel',
				plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
			{
				clicksToEdit : 1
			})]
});