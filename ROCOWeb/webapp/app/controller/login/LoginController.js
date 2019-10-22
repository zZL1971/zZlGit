Ext.define('SMSWeb.controller.login.LoginController', {
	extend : 'Ext.app.Controller',
	init : function() {
		var me = this;
		me.control({
			'form textfield': {
                specialkey: function(field, e) {
                	if(e.getKey() === e.ENTER){
                		var form = field.up('form').getForm();
                		if(!form.isValid()){
                			var fields = form.getFields();
                			var fieldx = null;
                			fields.each(function(field) {
                				if(field.getErrors().length>0 && fieldx==null){
                					field.focus();
                					fieldx = field;
                					return true;
                				}
			                });
                		}else{
                			field.up('form').down("button[itemId=loginSubmit]").fireEvent('click');
                		}
                	}
                }
            }
		});
	}
});