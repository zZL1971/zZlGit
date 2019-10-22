Ext.define('Ext.ux.form.CustomCombobox', {
	extend : 'Ext.form.field.ComboBox',
	queryMode:'local',
	valueField:'id',
	displayField:'text',
	dataUrl:null,
	dataFields:[],
	forceSelection:true,
	typeAhead: true,
	alias : 'widget.customcombobox',
	initComponent:function(){
		var me = this;
		
		var store = Ext.create('Ext.data.Store', {
			fields:me.dataFields,
	        proxy: {
	            type: 'ajax',
	            url: me.dataUrl,
	            reader: {
	                type: 'json',
	                root: 'data'
	            }
	        }
	    });
	    
	    store.sync();
		store.load();
	    
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	}
});