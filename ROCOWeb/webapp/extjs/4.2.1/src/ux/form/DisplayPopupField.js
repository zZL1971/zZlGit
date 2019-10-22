Ext.define ('Ext.ux.form.DisplayPopupField', {
	extend:'Ext.form.field.Display',
	alias: 'widget.displaypopupfield',
	popupUrl:null,
	sourceRef:null,
	initComponent:function(){
		var me = this;
		me.fieldSubTpl= [
        '<div id="{id}" role="input" ',
        '<tpl if="fieldStyle"> style="{fieldStyle}"</tpl>', 
        ' class="{fieldCls}">{value}<input type="hidden" name="{name}" value="{value}"/></div>',
	        {
	            compiled: true,
	            disableFormats: true
	        }
	    ];
	    
	    if(me.sourceRef){
	    	me.renderer=function(){
	    		return "";
	    	}
	    }
	    
	    
	    Ext.apply(this,{   
	   		listeners:{
	    		afterrender:function(){
	    			if(me.sourceRef){
	    				this.codeEl = this.bodyEl.child('div').createChild("<a href='javascript:;'>"+me.value+"</a>");
	    				this.codeEl.on('click',   this.showSourceRef, this);
	    			}
	    			if(me.popupUrl){
	    				this.codeEl = this.bodyEl.child('div').createChild({ tag: 'input', type:'button',value:'...'});
	      				this.codeEl.on('click',   this.showPopup, this);
	    			}
	    		}
	   		}
	    });
	   
		this.callParent(arguments);
	},
	showPopup:function(){
		alert(this.bodyEl.child('div').getHTML());
		//me.bodyEl.child('div').append("888888888888");
	},
	showSourceRef:function(){
		var me = this;
		var ref = me.sourceRef.ref;
		ref = ref.substr(ref.indexOf('.'));
		Ext.create("app"+ref,me.sourceRef.params).show();
	}
});