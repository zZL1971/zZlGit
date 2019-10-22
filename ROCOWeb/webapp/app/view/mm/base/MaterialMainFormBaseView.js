Ext.define("SMSWeb.view.mm.base.MaterialMainFormBaseView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.MaterialMainFormBaseView',// 面板的别名
			bodyStyle : "background-color: #D0DEF0;padding-top:10px;padding-bottom:5px"	,
			border : false,
			fieldDefaults : {
    	        width: 300,
				labelWidth : 80,
				labelAlign : "left",
				labelStyle : 'padding-left:10px;'
			},
			layout : {
					type : 'vbox',
					align : 'stretch'
				},
				items : [
					        {
								layout : 'hbox',
								xtype : 'fieldcontainer',
								items : [
									{
										xtype:'dictcombobox',
										fieldLabel : '产品类型',
										name:'mtart',
										dict:'MATERIAL_MTART',
										emptyText: '请选择...'
									},
									{
										xtype : 'textfield',
										maxLength:25,
										enforceMaxLength:true,
										fieldLabel : '产品编号',
										name :'matnr'
									},
									{
										xtype : 'textfield',
										maxLength:25,
										enforceMaxLength:true,
										fieldLabel : '产品描述',
										name :'maktx'
									}
								]
					        },
					        {
					        	layout : 'hbox',
					        	xtype : 'fieldcontainer',
					        	items : [
					        	         {
					        	        	 xtype : 'textfield',
					        	        	 fieldLabel : '销售组织',
					        	        	 name :'vkorg'
					        	         },
					        	         {
					        	        	 xtype : 'textfield',
					        	        	 fieldLabel : '分销渠道',
					        	        	 name :'vtweg'
					        	         },
					        	         {
					        	        	 xtype : 'textfield',
					        	        	 fieldLabel : '物料组',
					        	        	 name :'matkl'
					        	         }
					        	         ]
					        }
				]
			
		});