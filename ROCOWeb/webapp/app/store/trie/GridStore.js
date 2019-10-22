Ext.define("SRM.store.trie.GridStore",{
	extend:'Ext.data.Store',
	model:'SRM.model.trie.GridModel',
	remoteSort: true,
	listeners:{
		beforeload:function(store, operation, eOpts){
			//设置默认查询条件
			var selectedTrees = Ext.ComponentQuery.query("trieTree")[0].getSelectionModel().getSelection();
			var selectedTreeId = selectedTrees.length>0?selectedTrees[0].getId():"";
			if(selectedTreeId)
				Ext.apply(store.proxy.extraParams, {ICEQtrieTree__id:selectedTreeId});
		}
	},
	autoLoad:false
});