Ext.define('Ext.ux.data.DictFactory',{
	dicts : Ext.create('Ext.util.MixedCollection'),
	getDict:function(config){
		var dict = [];
		var map = new SMSWeb.common.core.CodeHashmap().create();
		var key = config.dictKey == undefined ?config.table:config.dictKey;
		var url = config.dictKey == undefined ? "/search/ext/table/"+config.table+"/"+config.text:"/core/dd/list3/"+map.get(key);
		
		if(this.dicts.containsKey(key)){
 			return this.dicts.get(key);
 		}else{
 			Ext.Ajax.request({
 				url : url,
	        	method : 'GET',
	        	async : false, //同步
				success : function(response, options){
					var jsonResult = Ext.decode(response.responseText);
					dict = jsonResult.data;
				}
 			});
 			this.dicts.add(key,dict);
 		}
 		
 		return dict;
	},
	getDictDesc:function(config){
		var dict = this.getDict(config);
		var _val = null;
		//console.log('dict is '+dict);
		if(config.value){
			Ext.Array.each(dict,function(data){
				//console.log('dict key is '+data.id+' value is '+data.text);
				if(data.id==config.value){
						//console.log('dict key is '+value+' == is '+config.value);
					//console.log('dict key is '+data.id+' value is '+data.text);
					_val = data.text;
				}
			});
		}
		
		_val =config.value ==undefined?undefined:(_val==null?"<font color=red>"+config.value+"</font>":_val);
		return _val;
	}
});
Ext.DictFactory = Ext.create('Ext.ux.data.DictFactory');