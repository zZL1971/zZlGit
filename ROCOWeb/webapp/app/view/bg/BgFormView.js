//var bgTypeStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'BG_TYPE'},
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

//var bgTypeCombobox = new Ext.form.ComboBox({
//		 name: "bgType",
//         fieldLabel: '变更类型',
////         width: 300,
//         store: bgTypeStore,
//         displayField: 'descZhCn',
//         valueField: 'keyVal',
//         triggerAction: 'all',
//         emptyText: '请选择...',
////         allowBlank: false,
////         blankText: '请选择订单类型',
//         editable: false,
//         queryMode: 'local'
////        	 ,
////         listConfig: {  
////		       getInnerTpl: function() {  
////		           return '<div data-qtip="{descZhCn} ({keyVal})">{descZhCn} ({keyVal})</div>';  
////		       }  
////		  }  
//});

//var orderStatusStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'ORDER_STATUS'},
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

//var orderStatusCombobox = new Ext.form.ComboBox({
//		 name: "orderStatus",
//         fieldLabel: '订单状态',
////         width: 300,
//         store: orderStatusStore,
//         displayField: 'descZhCn',
//         valueField: 'keyVal',
//         triggerAction: 'all',
//         emptyText: '请选择...',
////         allowBlank: false,
////         blankText: '请选择订单类型',
//         editable: false,
//         queryMode: 'local'
////        	 ,
////         listConfig: {  
////		       getInnerTpl: function() {  
////		           return '<div data-qtip="{descZhCn} ({keyVal})">{descZhCn} ({keyVal})</div>';  
////		       }  
////		  }  
//});

var bgTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '变更类型',
		emptyText: '请选择...',
		name:'bgType',
		dict:'BG_TYPE'
});

var orderStatusCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '状态',
		emptyText: '请选择...',
		name:'orderStatus',
		dict:'ORDER_STATUS'
});
var orderType = Ext.create("Ext.ux.form.DictCombobox",{  
	fieldLabel : '订单类型',
	emptyText: '请选择...',
	name:'orderType',
	editable : false,
	dict:'ORDER_TYPE'
});
Ext.define("SMSWeb.view.bg.BgFormView", {
			extend : 'Ext.form.Panel',
			alias : 'widget.BgFormView',// 面板的别名
			id:'bgForm',
			bodyStyle : "padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
			border : false,
			autoScroll:true,
			fieldDefaults : {
				labelWidth : 80,
				labelAlign : "left",
				labelStyle : 'padding-left:5px;',
				width:235
			},
			layout: {
		        type: 'table',
		        columns: 5
		    },
			items : [
				{
                    xtype:'hiddenfield',
                    fieldLabel: 'queryBgType',
                    name: 'queryBgType',
                    value:queryBgType
                },
				{
                    xtype:'textfield',
                    fieldLabel: '变更单号',
                    name: 'bgCode'
                },
                {
                    xtype:'textfield',
                    fieldLabel: '订单编号',
                    name: 'orderCode'
                },
				{
                    xtype:'textfield',
                    fieldLabel: '申请客户',
                    name: 'clients'
                },
                bgTypeCombobox,
                {xtype:'textfield',fieldLabel:'创建人',name:'createUser'},
                {
                    xtype:'textfield',
                    fieldLabel: '联系人',
                    name: 'contacts'
                },
                {
                    xtype:'textfield',
                    fieldLabel: '联系电话',
                    name: 'tel'
                },
                orderStatusCombobox,
                {
                	xtype:'datefield',
                	fieldLabel:'审批日期从',
                	name:'updateTimeF',
                	format:'Y-m-d'
                },{
                	xtype:'datefield',
                	fieldLabel:'到',
                	name:'updateTimeT',
                	format:'Y-m-d'
                },
                orderType
			]
			
		});