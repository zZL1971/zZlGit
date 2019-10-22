Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.mm.sale.MaterialFileUploadWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.MaterialFileUploadWindow',
			formId:null,
			fileType:null,
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
				       buttons: [{ text: '上传',handler : function(grid, rowIndex, colIndex) {
													this.up('window').fireEvent('fileUploadButtonClick');
											  } 
				    	   		}],
			    	   items:[
								{
									value:me.formId,
								    xtype: 'hiddenfield',
								    name:'mappingId'
								},
								{
									value:me.fileType,
									xtype: 'hiddenfield',
									name:'fileType'
								},
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
				
				Ext.apply(me, {
					items : [form]
				});
				this.callParent(arguments);
			}
		});