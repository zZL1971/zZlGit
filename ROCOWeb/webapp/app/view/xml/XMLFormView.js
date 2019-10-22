Ext.define("SMSWeb.view.xml.XMLFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.XMLFormView',// 面板的别名
			id:'xmlForm',
			bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
			border : false,
			autoScroll:true,
			fieldDefaults : {
				labelWidth : 80,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;',
				width: 235
			},
			layout: {
		        type: 'table',
		        columns: 5
		    },
			items : [
				{
                    xtype:'textfield',
                    fieldLabel: '文本编码',
                    name: 'textCode'
                }
				,{
                    xtype:'textfield',
                    fieldLabel: '类型',
                    name: 'type'
                },{
                    xtype:'dictcombobox',
                    fieldLabel: '销售分类',
                    name: 'saleFor',
                    dict:'SALE_FOR'
                },{
                    xtype:'textfield',
                    fieldLabel: '文本描述',
                    name: 'textDesc'
                }
			]
			
		});