Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.component.ComponentFileUploadBaseWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.ComponentFileUploadBaseWindow',
			height : 200,
			width : 450,
			modal:true,
			constrainHeader: true,
			layout:'fit',
			initComponent : function() {
				var me = this;
				var form = Ext.widget('form',{
		    		   region:'center', 
			    	   itemId:'fileUploadForm',
			    	   border : false,
			    	   bodyStyle : "padding:5px;",
				       defaults: {
				            anchor: '100%',
				            msgTarget: 'side',
				            labelWidth: 50
				       },
				       buttons: [{ text: '上传',
				    	   		   id:'upload'
				    	   		}],
			    	   items:[
				                {
						            xtype: 'filefield',
						            fieldLabel: '文件',
						            emptyText: '请选择一个文件',
						            name: 'file',
						            allowBlank: false,
						            buttonText: '选择文件',
								    blankText: '选择文件'
						        },{
						            xtype: 'textarea',
						            fieldLabel: '备注',
						            name:'remark'
						        }
			    	          ]
				 });
				Ext.getCmp("upload").on('click',function(){
					var flg=false;
					if(form.getForm().isValid()){
						form.submit({  
	                        url: 'delivery/uploadComponent',  
	                        waitMsg: '上传文件中...',
	                        async: false,
	                        success: function(form, action) {
	                        	var values = Ext.decode(action.response.responseText);
	                        	if(values.success){
	                        		me.close();
	                        	}
	                        	Ext.MessageBox.alert("提示信息",values.msg);
	                        },
	                        failure : function(form, action) {
	                        	var values = Ext.decode(action.response.responseText);	
	                        	Ext.MessageBox.alert("提示信息",values.msg);
							}
	                    });
					}
				});
				Ext.apply(me, {
					items : [form]
				});
				this.callParent(arguments);
			}
		});