Ext.define("MeWeb.common.cmo.CmoCommon",{
	upload:function(records,picture) {
					Ext.create('Ext.window.Window', {
					id : 'uploadding',
					closeable : true,
					title : 'upload photo',
					width : 500,
					border : false,
					frame : true,
					modal : true,
					layout : 'fit',
						items : [{
						xtype : 'form',
						frame : true,
						buttonAlign : 'center',
						bodyPadding : 5,
						defaultType : 'textfield',
						items: [{
        					xtype: 'filefield',
      						name: 'myfiles',
       		 			    fieldLabel: 'Photo',
        			        labelWidth: 50,
       		                msgTarget: 'side',
       						allowBlank: false,
       						anchor: '100%',
        					buttonText: 'Select Photo...'
   		 				 }],
				 		buttons: [{
        					text: 'Upload',
        					handler: function() {
            				var form = this.up('form').getForm();
            				if(form.isValid()){
               					 form.submit({
                    			 url: 'sms/cmo/fileupload',
                   				 waitMsg: 'Uploading your photo...',
                    			 success: function(response, opts) {
                    			var	jsonResult = Ext.decode(opts.response.responseText).msg;
                    			 	//Ext.Msg.alert('Success', 'Your photo "' + jsonResult + '" has been uploaded.');
                    				Ext.Msg.alert('Success', 'Your photo has been uploaded.');
                    			 	records.set(picture,jsonResult);
                    			 	var win = Ext.getCmp('uploadding');// 查找组件
									win.close(); // 关闭窗口
									
                   		 		  }
                                 });
            					}
       						 }
    					}]
					}]
				}).show();
				
				}
				
		
})