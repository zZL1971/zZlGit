Ext.define('Ext.ux.form.TableComboBox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	table:null,
	datatext:"text",
	dataid:null,
	showKey:false,
	columns:null,
	url:null,//数据路劲
	queryMode:'local',
	alias : 'widget.tablecombobox',
	initComponent:function(){
		var _url;
		if(!this.url){
			_url = '/core/ext/base/dd/table/'+this.table+"/"+this.datatext;
		}else{
			_url=this.url;
		}
		
		var fields = ["id","text"];
		if(this.columns!=null){
			
			var a_ = this.columns.split(",");
			for(var i=0;i<a_.length;a_++){
				fields.push(a_[i]);
			}
		}
		
		var store = Ext.create('Ext.data.Store', {
			fields:fields,
	        proxy: {
	            type: 'ajax',
	            url: _url,
	            reader: {
	                type: 'json',
	                root: 'data'
	            }
	        }
	    });
	    
	   
	    if(!this.url){
	    	store.sync();
	    	store.load({params:{columns:this.columns,id:this.dataid,showKey:this.showKey}});
		}else{
			store.load();
		}
	    
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	},
	valueField:'id'
});