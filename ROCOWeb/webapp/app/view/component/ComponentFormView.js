Ext.define("SMSWeb.view.component.ComponentFormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.ComponentFormView',// 面板的别名
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
                    name: 'line'
                }
				,{
                    xtype:'textfield',
                    fieldLabel: '标识码',
                    name: 'identifyCode'
                },{
                    xtype:'dictcombobox',
                    fieldLabel: '是否启用',
                    name: 'stat',
                    dict:'YES_NO'
                },{
                    xtype:'textfield',
                    fieldLabel: '部件名称',
                    name: 'componentName'
                },{
                    xtype:'textfield',
                    fieldLabel: '物料编码',
                    name: 'materialCode'
                },{
                    xtype:'textfield',
                    fieldLabel: '外购标识码',
                    name: 'outSourceIdentifyCode'
                }
			]
			
		});