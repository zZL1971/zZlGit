Ext.define('Ext.ux.form.TrieCombobox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	name : null,
	trie:null,
	queryMode:'local',
	forceSelection:true,
	typeAhead:true,
	zvalidField:'KEY_VAL',
	alias : 'widget.triecombobox',
	cascadeDict:null,
	initComponent:function(){
		var me = this;
		var store = Ext.create('Ext.data.Store', {
			storeId:me.name+"_"+me.trie,
			fields:["id","text"],
	        proxy: {
	            type: 'ajax',
	            url: '/core/trie/nodes/'+me.trie+"/"+me.zvalidField+"/",
	            reader: {
	                type: 'json',
	                root: 'data'
	            }
	        },
	        autoLoad:false
	    });
		store.sync();
		store.load();
		
		
		if(me.cascadeDict!=null){
			me.listeners={
				change:function(ths, newValue, oldValue, eOpts ){
					var combo_ = Ext.ComponentQuery.query(me.cascadeDict)[0];
					combo_.store.load({params:{code:newValue,trie:me.trie}});
				}
			}
		}
		
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	},
	valueField:'id'
});