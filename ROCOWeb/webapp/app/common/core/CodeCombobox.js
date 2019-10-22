//键值对


Ext.define('MeWeb.common.core.CodeCombobox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	name : null,
	queryMode:'local',
	forceSelection:true,
	typeAhead:true,
	alias : 'widget.codecombobox',
	requires:["MeWeb.common.core.CodeHashmap"],
	initComponent:function(){
		var map = new MeWeb.common.core.CodeHashmap().create();
		var store = Ext.create('Ext.data.Store', {
			storeId:'code_'+this.name,
			fields:["id","text"],
	        proxy: {
	            type: 'ajax',
	            url: '/core/dd/list3/'+map.get(this.name),
	            reader: {
	                type: 'json',
	                root: 'data'
	            }
	        },
	        autoLoad:true
	    });
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	},
	valueField:'id'
});