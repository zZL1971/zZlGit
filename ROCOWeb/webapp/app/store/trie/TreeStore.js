Ext.define('SRM.store.trie.TreeStore', {
	extend : 'Ext.data.TreeStore',
	//storeId:'trieTreeStore',
	model : 'SRM.model.trie.TreeModel',
	/*root: {
		name:'sadf',
	    expanded: false
	},*/
	proxy : {
		type : 'ajax',
		url:'/core/trie/tree/root',
		reader:'json',
		/*reader:{
			type: 'json',
			root: 'root'
		}*/
		listeners:{  
	        exception:Ext.ux.DataFactory.exception
	    }
	},
	autoLoad: true
});