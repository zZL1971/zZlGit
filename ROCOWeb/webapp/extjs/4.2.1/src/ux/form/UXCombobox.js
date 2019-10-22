Ext.define('Ext.ux.form.UXCombobox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	name : null,
	dict:null,
	dataUrl:null,
	loadStatus:true,
	dataFields:["id","text"],
	queryMode:'local',
	forceSelection:true,
	typeAhead:true,
	alias : 'widget.uxcombobox',
	initComponent:function(){
		var me = this;
		
		if(me.dict){
			me.dataUrl = '/core/dd/list3/'+me.dict;
		}
	 
		var store = Ext.create('Ext.data.Store', {
			//storeId:me.name+"_"+me.dict,
			fields:me.dataFields,
	        proxy: {
	            type: 'ajax',
	            url: me.dataUrl,
	            reader: {
	                type: 'json',
	                root: 'data'
	            }
	        },
	        autoLoad:false
	    });
	    
	    if(me.loadStatus){
	    	store.sync();
			store.load();
	    }
		
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	},
	valueField:'id'
});