Ext.define('SMSWeb.controller.bpm.TaskListController', {
	extend : 'Ext.app.Controller',
	views : ['bpm.TaskList','bpm.TaskApprove'],
	requires : ["Ext.ux.form.DictCombobox", "Ext.ux.grid.UXGrid","Ext.ux.ButtonTransparent","Ext.ux.form.TableComboBox"],
	/*refs : [{
				ref : 'uxgrid',
				selector : 'uxgrid'
			}],*/
	init : function() {
		var me = this;
		/*me.control({
			'grid[itemId=flowTaskGrid]':{
				itemEditButtonClick : function(grid, rowIndex, colIndex) {
					var record = grid.getStore().getAt(rowIndex);
					//Ext.create('SMSWeb.view.sale.NewSaleWindow',{formId : record.getId(),title:'订单查看',editFlag:true}).show();
				}
			}
		});*/
	}
});