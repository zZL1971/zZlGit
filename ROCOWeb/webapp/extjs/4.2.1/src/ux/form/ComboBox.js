Ext.define('Ext.ux.form.ComboBox', {
	setDefaultValues:function(form,rec){
		var fields = form.getForm().getFields(),record,val;
		fields.each(function(item,index,len){
			if(item.xtype=="codecombobox" || item.xtype == "tablecombobox"){
				if(rec!=null){
					Ext.Object.each(rec,function(key,value){
						if(key==item.name){
							val = value;
						}
					});
				}else{
					record = form.getRecord();
					val = record.get(item.name);
				}
				
				form.getForm().findField(item.name).value=val;
			}
		});
	}
});