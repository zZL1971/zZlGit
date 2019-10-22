Ext.define("SMSWeb.view.delivery.DeliveryFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.DeliveryFormView',// 面板的别名
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
					xtype:'dictcombobox',
                    fieldLabel: '产线',
                    dict:'PRODUCTIONLINE',
                    name: 'lineCode'
                }
			]
			
		});