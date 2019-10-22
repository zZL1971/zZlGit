Ext.define('Ext.ux.form.SearchForm', {
	extend : 'Ext.form.Panel',
	alias : 'widget.searchform',
	boder:false,
	bodyStyle:'border:none;',
	bodyPadding : '5 5 5 5',
	fieldDefaults : {
		labelAlign : 'left',
		labelWidth : 87,
		labelStyle : 'padding-left:5px;',
		width : 240
	},
	getQ:function(condition,key){
		var a_ = /^(I|E)(C|D|N)(EQ|GE|LE|GT|LT|NE|BT|CP|IS|OP|MQ)[A-Za-z0-9_.__]+$/;
		if(a_.exec(key)){
			return key;
		}else{
			return condition+key;
		}
	},
	getSearchs : function() {
		var me = this,searchs={},form=me.getForm(),fields=form.getFields(),values=form.getValues();
		
		/*fields.each(function(item,index,len){
			//alert(item.getName()+"|"+item.getValue()+"|"+item.getXType())
			var val = item.getValue();
			
			var key = item.getName();
			if(!Ext.isEmpty(val)){
				if(item.getXType()=="codecombobox" || item.getXType()=="combobox" || item.getXType()=="numberfield")
					searchs["ICEQ"+key] = val;
				else if(item.getXType()=="textfield")
					searchs["ICCP"+key] = val;
				else if(item.getXType()=="datefield")
					searchs["IDGT"+key] = val;
			}
		});*/
		Ext.Object.each(values,function(key,value){
			if(!Ext.isEmpty(value)){
				//console.log(key+"|"+value);
				var item = form.findField(key);
				
				if(item.getXType()=="codecombobox" || item.getXType()=="dictcombobox" || item.getXType()=="hidden" || item.getXType()=="hiddenfield" || item.getXType()=="combobox" || item.getXType()=="numberfield"|| item.getXType()=="tablecombobox")
					searchs[me.getQ("ICEQ",key)] = value;
				else if(item.getXType()=="textfield")
					searchs["ICCP"+key] = value;
				else if(item.getXType()=="datefield"){
					if(value instanceof Array && value.length%2==0){
						for (var index = 0; index < value.length; index++) {
							if(index%2==0){
								var val_ = searchs["IDGE"+key];
								if(!Ext.isEmpty(value[index]) && Ext.isEmpty(value[index+1])){
									if(val_!=null){
										searchs[me.getQ("IDGE",key)] = val_+","+value[index];
									}else{
										searchs[me.getQ("IDGE",key)] = value[index];
									}
									
								}else if(Ext.isEmpty(value[index]) && !Ext.isEmpty(value[index+1])){
									if(val_!=null){
										searchs[me.getQ("IDGE",key)] = val_+","+value[index+1];
									}else{
										searchs[me.getQ("IDLE",key)] = value[index+1];
									}
								}else if(!Ext.isEmpty(value[index]) && !Ext.isEmpty(value[index+1])){
									if(val_!=null){
										searchs[me.getQ("IDGE",key)] = val_+","+value[index]+","+value[index+1];
									}else{
										searchs[me.getQ("IDBT",key)] = value[index]+","+value[index+1];
									}
									
								}
							}
						}
					}else{
						searchs["IDEQ"+key] = value;
					}
				}
			}
		});
		return searchs;
	},
	getSearchSerialize:function(){
		var s = this.getSearchs();
		var data= "";
		Ext.Object.each(s, function(key, value, myself){
			data += Ext.String.format("{0}={1}&", key, encodeURI(value));
		});
		return data;
	}
});