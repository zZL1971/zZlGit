Ext.define('Ext.ux.form.DictCombobox', {
	extend : 'Ext.form.field.ComboBox',
	fieldLabel : null,
	name : null,
	dict:null,
	queryMode:'local',
	forceSelection:true,
	valType:null,
	codeType:null,//数据字典类型字段
	cascade:false,
	localData:null,
	showDisabled:null,
	ccbm:null,
	judge:null,//判断显示内容
	//typeAhead:true,
	alias : 'widget.dictcombobox',
	initComponent:function(){
		var me = this;
		var store;
		if(me.localData){
			var _localDate=Ext.decode(me.localData);
			var jsondata=[];
			Ext.Array.each(_localDate, function(name, index, countriesItSelf) {
			    jsondata.push({"id":name,"text":name});
			});
			store = Ext.create('Ext.data.Store', {
			   fields: ['id', 'text'],
			    data : jsondata
			});

		}else{
			var vt = me.valType!=null?("?valType="+me.valType):"";
			var ct = me.codeType!=null?((vt?"&":"?")+"codeType="+me.codeType):"";
			var _showDisabled=me.showDisabled!=null?((vt?"&":"?")+"showDisabled="+me.showDisabled):"";
			var _ccbm=me.ccbm!=null?((vt?"&":"?")+"ccbm="+me.ccbm):"";
			var _judge=me.judge!=null?((vt?"&":"?")+"judge="+me.judge):"";
			var url = '/core/dd/list3/'+me.dict+vt+ct+_showDisabled+_judge+_ccbm;
			//console.log("dict-store-data-url:"+url);
			if(me.cascade){
				url = '/core/dd/cascadelist'+vt+_showDisabled;
			}
			
			//alert(vt+"|"+me.valType);
			store = Ext.create('Ext.data.Store', {
				storeId:me.name+"_"+me.dict,
				fields:["id","text"],
		        proxy: {
		            type: 'ajax',
		            url:url,
		            reader: {
		                type: 'json',
		                root: 'data'
		            }
		        }
		    });
			store.sync();
			if(!me.cascade){
				store.load();
			}
		}
		
		
		
	    Ext.apply(this,{store:store});
		this.callParent(arguments);
	},
	valueField:'id'
});