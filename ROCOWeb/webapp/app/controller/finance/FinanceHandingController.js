Ext.define('SMSWeb.controller.finance.FinanceHandingController', {
	extend : 'Ext.app.Controller',
	views : ['finance.FinanceHandingView','cust.NewCustWindow','cust.NewCustWindowInnerContent','bpm.TaskApprove'],
	requires : ["Ext.ux.form.DictCombobox", "Ext.ux.grid.UXGrid","Ext.ux.ButtonTransparent","Ext.ux.form.TableComboBox"],
	id:'financeHandingController',
	refs : [{
		ref : 'FinanceHandingView',
		selector : 'FinanceHandingView' 
	}],
	init : function() {
		me=this;
	}
});	