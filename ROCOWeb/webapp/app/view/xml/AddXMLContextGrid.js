var store=Ext.create('SMSWeb.store.xml.XMLInfoStore');
Ext.define('SMSWeb.view.xml.AddXMLContextGrid',{
	extend:'Ext.window.Window',
	width:900,
	height:500,
	record:null,
	resizable:false,
	alias:'widget.AddXMLContextGrid',
	title:'添加内容',
	border : false,
	style:'border-top:1px solid #C0C0C0;',
	tbar:[
	      {
			xytpe:'button',
			text:'添加',
			id:'addData'
	      },{
	    	xytpe:'button',
	  		text:'添加一行',
	  		id:'addColumn',
	  		listeners:{
	  			click:function(){
	  				var model = Ext.create("SMSWeb.model.xml.XMLInfoModel");
	  				if(store.data.length>=15)return;
	  				store.insert(store.data.length,model);
	  			}
	  		}
		  },{
				xytpe:'button',
				text:'',
				id:'help',
				border:false,
				tooltip:'帮助',
				icon:'resources/images/information.png',
				margin:'0 0 0 720',
		     }
	],
	items:[
	       {
	    	xtype:'gridpanel',
			border:false,
			id:'xmlgridId',
			enableKeyNav : true,
			columnLines : true,
			columns : [
					 {xtype:'rownumberer',width:30,align:'right'},	 
					 {text:'部件名称',dataIndex:'pname',width:180,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
					 {text:'附加条件',dataIndex:'pvarString',width:470,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true},filterable:true,
						 renderer: function(value,metadata,record){
							if(value.indexOf("\t")!=-1){
								var val="";
								while(value.indexOf("\t")!=-1){
									val += value.substring(0,value.indexOf("\t"))+",";
									val = val.replace("=", "");
									value = value.substring(value.indexOf("\t")+1);
								}
								value = value.replace("=", "");
								val += value;
								record.data.pvarString=val;
								if(val!="") return val;
							}
							value = value.replace("=","");
							record.data.pvarString = value;
					 	    return value;
					 }},
					 {text:'改变值',dataIndex:'changeVal',width:220,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true},filterable:true}
					 
		         ],
	     	viewConfig:{enableTextSelection:true},
	     	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	     	plugins: [
	     	          Ext.create('Ext.grid.plugin.CellEditing', {
	     	              clicksToEdit: 2
	     	          }) 	
	     	],
	     	multiSelect:true,
		    store: store
	       }
	],
	initComponent:function(){
		var me=this;
		me.callParent(arguments);
	},
	listeners:{
		show:function(){
			store.loadData({});
			var record = this.record;
			var dataText = record.data.text;
			var counter =record.data.counter;
			var base = counter.split("@");
			var val=dataText.split("@");
			var XML_P = 'P';
			var XML_V = 'V';
			var XML_C = 'C';
			for(var i=0;i<base.length;i++){
				var Pname=null;
				var PVarString=null;
				var Change=null;
				for (var j = 0; j < val.length; j++) {
					if(val[j].startsWith("P")){
						Pname = this.resolverStr(val, j,XML_P,""+(i+1));
					}else if(val[j].startsWith("V")){
						PVarString = this.resolverStr(val, j,XML_V,""+(i+1));
					}else if(val[j].startsWith("C")){
						Change = this.resolverStr(val, j,XML_C,""+(i+1));
					}
				}
				var model = Ext.create("SMSWeb.model.xml.XMLInfoModel");
				model.set("pname",Pname.substring(Pname.indexOf(":")+1));
				
				var newPvar = this.resolverIndex(PVarString ,i);
				var newChange= this.resolverIndex(Change ,i);
				model.set("pvarString", newPvar);
				model.set("changeVal",newChange=="NULL"?"":newChange);
				store.insert(store.data.length,model);
				console.log(Pname);
				console.log(PVarString);
				console.log(Change);
			}
			console.log(dataText);
		}
	},
	resolverStr:function( val, i, format, like){
		var oldVal="";
		var newVal=val[i].substring(val[i].indexOf(format)+2, val[i].length);
		if(newVal.startsWith("[")){
			newVal=newVal.substring(1, newVal.length - 1);
			var forTime = newVal.split(",");
			var builder = "";
			for (var j = 0; j < forTime.length; j++) {
				if(forTime[j].startsWith(like)){
					builder += forTime[j]+",";
				}
			}
			oldVal=builder.substring(0, builder.length - 1);
		}
		return oldVal;
	},
	resolverIndex:function(PVarString , i){
		var pvar=PVarString.split(",");
		var newPvar="";
		for(var v=0;v<pvar.length;v++){
			if(pvar[v].startsWith(""+(i+1))){
				newPvar += pvar[v].substring(pvar[v].indexOf(":")+1)+",";
			}
		}
		newPvar = newPvar.substring(0 ,newPvar.length-1);
		return newPvar;
	}
});