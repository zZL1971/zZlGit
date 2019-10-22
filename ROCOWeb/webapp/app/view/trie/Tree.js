Ext.define('SRM.view.trie.Tree', {
    extend: 'Ext.tree.Panel',
    xtype: 'trie.Tree',
    alias : 'widget.trieTree',
    listeners:{
        load:function(){
          	var record = this.getStore().getNodeById('system');
            this.getSelectionModel().select(record);
        }
    },
    viewConfig:{
    	plugins :{
	    	ptype:'treeviewdragdrop',
	    	appendOnly:false   //只能拖着带非叶节点上
    	},
    	listeners:{//监听器
		    drop:function(node,data,overModel,dropPosition,options){
			    //ajax的操作把数据同步到后台数据库
			    //alert("把:"+data.records[0].get('text')+" 移动到："+overModel.get("text"));
			    Ext.Ajax.request({  
		            url: '/core/trie/save/drop/',  
		            method: 'POST',
		            async:false,
		            params: {  
		                trieid: data.records[0].getId(),  
		                overid: overModel.getId(),  
		                dropPosition: dropPosition  
		            },  
		            success: function (response) {  
		                //rst = Ext.JSON.decode(response.responseText);  
		                //Ext.Tools.Msg(rst.msg, rst.result);  
		            },  
		            failure: function (response) {  
		                //Ext.Tools.Msg('请求超时或网络故障,错误编号：' + response.status, 9);  
		            }  
		        });
		        return true;
		    }
    	}
    },
    title: '数据字典索引',
    rootVisible: false,
    lines: true,
    useArrows: true,
    store: 'trie.TreeStore'
 });