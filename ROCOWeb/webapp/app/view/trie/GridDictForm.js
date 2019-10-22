Ext.define('SRM.view.trie.GridDictForm', {
	extend : 'Ext.ux.form.SearchForm',
	xtype : 'trie.quickForm',
    alias : 'widget.quickForm',
	requires : ['Ext.layout.container.HBox', 'Ext.form.field.Date'],
	layout : 'hbox',
	initComponent : function() {
		this.items = [{
					xtype : 'component',
					width : 30,
					height : 24
				},{
					xtype : 'component',
					width : 24,
					height : 24
				},{
					xtype : 'component',
					cls : 'quick-from-new',
					itemId:'quick_status',
					width : 38,
					height : 24
				}, {
					xtype : 'hiddenfield',
					name : 'id'
				}, {
					xtype : 'textfield',
					name : 'keyVal',
					fieldCls:'quick-from-text-left',
					allowBlank : false,
					emptyText : 'Add a new dict'
					,width:100
				}, {
					xtype : 'textfield',
					name : 'type',
					selectOnFocus:true,
					fieldCls:'quick-from-text-right',
					width:100
				}, {
					xtype : 'textfield',
					name : 'descZhCn',
					fieldCls:'quick-from-text-right',
					allowBlank : false,
					width:100
				}, {
					xtype : 'textfield',
					name : 'descEnUs',
					fieldCls:'quick-from-text-right',
					width:100
				}, {
					xtype : 'textfield',
					name : 'descZhTw',
					fieldCls:'quick-from-text-right',
					width:100
				}, {
					xtype : 'numberfield',
					minValue:0,
					name : 'orderBy',
					fieldCls:'quick-from-text-right',
					width:100
				},{
					xtype : 'treepicker',
					name:'trieTree.id',
					fieldCls:'quick-from-text-right',
					allowBlank : false,
					editable : false,
					rootVisible: false,
					displayField: 'descZhCn',
					store: Ext.create('Ext.data.TreeStore',{
						fields: ['id','descZhCn'],
						root: {
							text: 'wwww',
							expanded: true
						},
						proxy: {
							type: 'ajax',
							url: '/core/trie/tree/root',
							reader:'json',
							listeners:{  
						        exception:function( proxy, response, operation, eOpts ) { 
						           var res= Ext.JSON.decode(response.responseText);
						           if(!res.success){
						           		Ext.Msg.show({title: '【'+res.errorCode+'】-错误提示', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
						           }
						        }  
						    }
						}
					})
				}];

		this.callParent(arguments);
	}

});