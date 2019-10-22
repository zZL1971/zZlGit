Ext.define('SMSWeb.view.login.CheckCodeText',{ 
    extend:'Ext.form.field.Text',  
    alias: 'widget.checkcodetext',   
    codeUrl:Ext.BLANK_IMAGE_URL,  //生成验证码对应的URL
    isLoader:true,
    validateOnChange:true,
 	checkcodeWidth:this.checkcodeWidth||75, //设置验证码宽度
 	initComponent:function(){
   		Ext.apply(this,{   
	   		listeners:{
	    		afterrender:function(){
	      			this.codeEl   = this.bodyEl.createChild({ tag: 'img', title:'点击刷新', src:   Ext.BLANK_IMAGE_URL});   
	      			//this.inputEl.setWidth(this.width-this.labelWidth-(this.checkcodeWidth+10)); //这里减掉验证码的宽度，无效
	      			this.codeEl.on('click',   this.loadCodeImg, this);   
	      			this.codeEl.addCls('x-form-code');   
	      			if (this.isLoader) this.loadCodeImg();   
	    		},
	    		resize:function(){
	    			var textWidth = this.width-this.labelWidth-(this.checkcodeWidth+10);//这里减掉验证码的宽度,在afterrender设置无效
					this.setFieldStyle({width:textWidth,float:'left'});	 
	    		}
	   		}
    	}); 
        this.callParent(arguments);   
 },

  
    loadCodeImg: function() {   
        this.codeEl.set({ src:   this.codeUrl + '?id=' + Math.random() }); 
    }   
});