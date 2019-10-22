/**
 * @Description	数据模型工厂
 * @author		艾波 14.07.11
 */
Ext.define('Ext.ux.ModelFactory',{
 	
 	//数据模型的集合
 	models : Ext.create('Ext.util.MixedCollection'),
 	
 	//数据字段集合
 	fields : Ext.create('Ext.util.MixedCollection'),
 	
 	validations: Ext.create('Ext.util.MixedCollection'),
 	
 	/**
 	 * 根据数据模型
 	 * 
 	 * @param	modelName	model名称
 	 * */
 	getModel : function(modelName){

 		if(this.models.containsKey(modelName)){//已存在则返回
 			return modelName;
 		}
 		//获取字段集合
		var fieldArr = [],validArr = [];
		if(this.fields.containsKey(modelName)){////已存在则返回
			fieldArr = this.fields.containsKey(modelName);
		}else {
	        Ext.Ajax.request({//使用Ajax获取字段集合
	        	url :'/core/ext/grid/model/'+modelName,
	        	async : false, //同步
				success : function(response, options){
					var text = response.responseText;
					fieldArr = Ext.JSON.decode(text).data;
				}
	        });
	        this.fields.add(modelName,fieldArr);
		}
		//声明Model对象
		var newModel = Ext.define(modelName,{
			extend : 'Ext.data.Model',
			fields : fieldArr,
			validations : validArr
		});
		
		//将Model对象添加到Model集合中
		this.models.add(modelName,newModel);
		
		//返回Model字符串
		return modelName;
 	}
});
//实例化一个modelFactory对象
Ext.ux.modelFactory = Ext.create('Ext.ux.ModelFactory');
