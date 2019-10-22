//var ktokdStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'KTOKD'},
//			reader:{
//				type:'json',
//				root:'content',
//				totalProperty :'totalElements'
//			},
//			writer:{
//				type:'json'
//			}
//		},
//	    // API only returns 25 by default.
//	    pageSize: 1000,
//	    autoLoad: true
//});
//
//var ktokdCombobox = new Ext.form.ComboBox({
//		 name: "ktokd",
//         fieldLabel: '类型',
//         //width: 235,
//         store: ktokdStore,
//         displayField: 'descZhCn',
//         valueField: 'keyVal',
//         triggerAction: 'all',
//         emptyText: '请选择...',
////         allowBlank: false,
////         blankText: '请选择类型',
////         editable: false,
//         queryMode: 'local'
////        	 ,
////         listConfig: {  
////		       getInnerTpl: function() {  
////		           return '<div data-qtip="{descZhCn} ({keyVal})">{descZhCn} ({keyVal})</div>';  
////		       }  
////		  }  
//});
Ext.define("SMSWeb.view.cust.CustFormView", {
			extend : 'Ext.form.Panel',
			alias : 'widget.CustFormView',// 面板的别名
			requires:["Ext.ux.form.DictCombobox"],
			itemId:'custSearchForm',
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
		        columns: 4
		    },
			items : [
				{
                    xtype:'textfield',
                    fieldLabel: '编号',
                    name: 'kunnr1'
                }, {
                    xtype:'textfield',
                    fieldLabel: '名称',
                    name: 'name1'
                },{
		        	xtype:'dictcombobox',
					fieldLabel : '类型',
//					editable: false,
					emptyText: '请选择...',
					name:'ktokd',
					dict:'KTOKD'
		        }, {
                    xtype:'textfield',
                    fieldLabel: '电话',
                    name: 'tel'
	            },{
                    xtype:'textfield',
                    fieldLabel: '销售区域',
                    name: 'bzirk'
                }, {
                    xtype:'textfield',
                    fieldLabel: '信贷额度',
                    name: 'xinDai'
                }
//                ,{
//                    xtype:'datefield',
//                    fieldLabel: '开始日期',
//                    name: 'startDate',
//                    format :'Y-m-d'
//                },{
//                    xtype:'datefield',
//                    fieldLabel: '结束日期',
//                    name: 'endDate',
//                    format :'Y-m-d'
//	            }
			]
			
		});