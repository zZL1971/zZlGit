Ext.define('Ext.ux.form.IsCascadeCombobox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	name : null,
	cascadeMenu : null,// 级联菜单
	mainQuery : true,// 判断该级联菜单是否为主级联，也就是第一层 default为true
	queryMode : 'local',
	forceSelection : true,
	typeAhead : true,
	nextNode : null,
	lazyInit:false,
	alias : 'widget.iscascadecombobox',
	store : null,
	initComponent : function() {
		var me = this;
			var store = Ext.create('Ext.data.Store', {
				storeId : me.name + "_" + me.cascadeMenu,
				fields : [ "id", "text" ],
				proxy : {
					type : 'ajax',
					url : '/core/trie/fourCascade/' + me.cascadeMenu + "/"
							+ me.mainQuery + "/",
					reader : {
						type : 'json',
						root : 'data'
					}
				},
				autoLoad : false
			});
			store.sync();
			store.load();
			if (me.nextNode != null) {
				me.listeners = {
					change : function(ths, newValue, oldValue, eOpts) {
						var combo_ = Ext.ComponentQuery.query(me.nextNode)[0];
						var parent = Ext.getCmp(combo_.id);
						//parent.clearValue();
						parent.store.load({
							params : {
								code : newValue
							}
						});
					}
				}
			}
			
		Ext.apply(this, {
				store : store
			});
		this.callParent(arguments);
	},
	valueField : 'id'
});
