Ext.define('Ext.ux.grid.UXSupcanGrid', {
	extend : 'Ext.panel.Panel',
	alias : 'widget.uxsupcangrid',
	requires : ["Ext.ux.form.DictCombobox","Ext.ux.ButtonTransparent"],
	layout : 'border',
	instanceName:'AF',
	config:'Boder=none;Hue=Lilian;',
	searchParams: Ext.create('Ext.util.MixedCollection'),
	build:null,
	ztbar:null,
	onReady:function(id){
	
	},
	onEvent:function(id, Event, p1, p2, p3, p4){
		
	},
	listeners:{
		onReady:function(id){
			var me = this;
			if(id==me.instanceName){
				console.log('me.build'+me.build);
				var _build = eval(me.instanceName).func("Build", "/core/report/t/"+me.build);
			}
			me.onReady(id);
		},
		onEvent:function(id, Event, p1, p2, p3, p4){
			var me = this;
			if (Event == "Load") {
				var rows = eval(me.instanceName).func("getRows", "");
				var a = me.down('p')[0];
				a.getEl().setHTML("<div style='padding:0px;margin:0;color:blue;height:19px;line-height:21px;'>为您找到相关结果共<strong>"+rows+"</strong>个</div>");
			}
			me.onReady(id, Event, p1, p2, p3, p4);
			
		}
	},
	getSupcanGrid:function(){
		return eval(this.instanceName);
	},
	initComponent:function(){
		var me = this,formfields=[];
		
		//工具栏
		var toolbar = Ext.widget('toolbar',{
			items:[{
				xtype:'buttontransparent',
				glyph:0xf002,
				text : '查询',
				handler:function(){
					me.load();
				}
			}]
		});
		
		toolbar.add(me.ztbar);
		
		me.tbar = toolbar;
		
		Ext.Ajax.request({
			url:"/core/report/s/"+me.build,
			async:false,
			success:function(response,opts){
				formfields = Ext.decode(response.responseText);
			},
			failure:function(response,opts){
				Ext.Msg.alert("错误代码:"+response.status,response.responseText);
			}
		});
		
		//查询表单
		var north = {
			region : 'north',
			itemId:'reportFormView',
			xtype : 'searchform',
			hidden:formfields.length>0?false:true,
			border : false,
			bodyStyle : "background-color: #D0DEF0;padding:5px;",
			fieldDefaults : {
				labelWidth : 100,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;'
			},
			items : formfields
		};
		
		me.items=[north, {
			region : 'center',
			border:false,
			html:insertTreeListForExtjs(me.instanceName, me.config)
		}/*, {
			region : 'south',
			border : false,
			xtype : 'panel',
			itemId:'southpanel',
			minHeight:21,
			bodyStyle : {
				padding : '2px',
				margin : 0
			},
			html : '没有记录'
		}*/];
		this.callParent(arguments);
	},
	getSearchParamsSerialize:function(){
		var data= "";
		this.searchParams.eachKey(function(key,item){
			data += Ext.String.format("{0}={1}&", key, encodeURI(item));
		});
		return data;
	},
	load:function(config){
		var me = this;
		var form = me.getComponent("reportFormView");
		try{
			if(config){
				me.searchParams.removeAll();
				me.searchParams.addAll(config.params);
			}
			eval(me.instanceName).func("Load", "/core/report/d/"+me.build+"?"+me.getSearchParamsSerialize()+form.getSearchSerialize());
		}catch(e){
			var $agnt=navigator.userAgent.toLowerCase();
			if($agnt.indexOf("chrome")>0){
				window.open("supcan/install_chrome.htm");
			}else{
				window.location = "/supcan/binary/supcan.xpi";
			}
		}
		return me;
	}
});