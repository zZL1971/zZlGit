/**
 * @Description	数据集合工厂
 * @author		张川(cr10210206@163.com)
 */
Ext.define('Ext.ux.StoreFactory',{

	//Store集合
	stores : Ext.create('Ext.util.MixedCollection'),

	/**
	 * 获取数据集合对象
	 * 
	 * @param	storeName	store名称
	 * @param	modelType	数据模型类型 1:'Ext.data.Store',2:'Ext.data.TreeStore'
	 * @param	rootName	树的根节点名称
	 * @param	modelName	model名称
	 * @param	autoLoad	是否自动加载数据
	 * @param	url			访问地址
	 * @param	params		访问地址的参数
	 * */
	getStore : function(map){
		//获取参数
		var storeName = map.storeName;
		var modelType = map.modelType == undefined || map.modelType == null ? 1 : map.modelType;
		var rootName = map.rootName == undefined || map.rootName == null ? zc.rootName : map.rootName;
		var modelName = map.modelName;
		var autoLoad = map.autoLoad == undefined || map.autoLoad == null ? false : map.autoLoad;
		var url = map.url;
		var params = map.params == undefined || map.params == null ? {} : map.params;

 		if(this.stores.containsKey(storeName)){//已存在则返回
 			return storeName;
 		}
 		
 		//声明Store对象
 		var newStore = null;
 		if(modelType == 1){
			newStore = Ext.define(zc.createStore(storeName),{//声明一个Ext.data.Store对象
				extend : 'Ext.data.Store',
				model : modelName,
				autoLoad : autoLoad,
				proxy : {
					type : 'ajax',
					url : zc.bp() + url,
					extraParams : params,
					reader : {
						type : 'json',
						root : 'rows'
					},
					writer : {
						type : 'json'
					}
				}
			});
 		}
 		else if(modelType == 2){
			newStore = Ext.define(zc.createStore(storeName),{//声明一个Ext.data.TreeStore对象
				extend : 'Ext.data.TreeStore',
				model : modelName,
				autoLoad : autoLoad,
			    root: {
			        id : '',
			        text : rootName,
			        expanded: true //是否展开
			    },
				proxy : {
					type : 'ajax',
					url : zc.bp() + url,
					extraParams : params,
					reader : {
						type : 'json',
						root : 'rows'
					},
					writer : {
						type : 'json'
					}
				}
			});
 		}
 		//将Store对象添加到Store集合中
		this.stores.add(storeName,newStore);
		
		//返回Store字符串
		return storeName;
	}
});
//实例化一个storeFactory对象
ux.storeFactory = Ext.create('Ext.ux.StoreFactory');