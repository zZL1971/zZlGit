Ext.define('Ext.ux.DataFactory',{
	getFlowActivityId:function(uuid){
		var activity;
		if(uuid){
			Ext.Ajax.request({
				url : '/core/bpm/startedFlow',
				params : {
					uuid:uuid,
					xtype:'search'
				},
				method : 'GET',
				async:false,
				success : function(response, opts) {
					var jsonResult = Ext.decode(response.responseText);
					if(jsonResult.success){
						activity = jsonResult.data;
					}
				},
				failure : function(response, opts) {
					Ext.Msg.alert("错误代码:"+response.status,response.responseText);
				}
			});
		}
		
		return activity;
	},
	getComboboxForColumnRenderer:function(value,metaData,record,rowIndex,colIndex,store,view){
		//console.log(metaData.column.editor.store);
		var rowIndex = metaData.column.editor.store.find("id",value,0,false,true,true);
		
		//console.log("getComboboxForColumnRenderer:"+value+"--rowIndex-->"+rowIndex);
	    if (rowIndex < 0){
	    	return value;
	    }else{
	    	var record = metaData.column.editor.store.getAt(rowIndex);
	    	return record ? record.get('text') : '';
	    }
	},
	exception:function( proxy, response, operation, eOpts ) {   
           var res= Ext.JSON.decode(response.responseText);
           if(!res.success){
           	   	if(res.errorCode=="UT-403"){
           	   		var a_  =parent.top.Ext.getCmp('relogin');
	           		if(a_){
	           			a_.fireEvent("click");
	           		}else{
	           			window.top.location="/core/main/";
	           		}
           	   	}else{
           	   		Ext.Msg.show({title: '【'+res.errorCode+'】-错误提示', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
           	   	}
           		
           		
           }
	}
});
Ext.ux.DataFactory = Ext.create('Ext.ux.DataFactory');