//var orderTypeStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'ORDER_TYPE'},
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
//var orderTypeCombobox = new Ext.form.ComboBox({
//		 name: "orderType",
//         fieldLabel: '订单类型',
////         width: 300,
//         store: orderTypeStore,
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

//var fuFuanCondStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'FU_FUAN_COND'},
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
//var fuFuanCondCombobox = new Ext.form.ComboBox({
//		 name: "fuFuanType",
//         fieldLabel: '付款条件',
//         width: 300,
//         store: fuFuanCondStore,
//         displayField: 'descZhCn',
//         valueField: 'keyVal',
//         triggerAction: 'all',
//         emptyText: '请选择...',
////         allowBlank: false,
////         blankText: '请选择付款条件',
//         editable: false,
//         queryMode: 'local'
// });

//var payTypeStore = new Ext.data.Store({
//	    fields: ['keyVal','descZhCn'],
//	    proxy:{
//			type:'ajax',
//			url:'main/sale/queryDicd',
//			extraParams :{'keyVal':'PAY_TYPE'},
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
//var payTypeCombobox = new Ext.form.ComboBox({
//		 name: "payType",
//         fieldLabel: '支付方式',
//         width: 300,
//         store: payTypeStore,
//         displayField: 'descZhCn',
//         valueField: 'keyVal',
//         triggerAction: 'all',
//         emptyText: '请选择...',
////         allowBlank: false,
////         blankText: '请选择支付方式',
//         editable: false,
//         queryMode: 'local'
// });

var fuFuanCondCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '付款条件',
		emptyText: '请选择...',
		name:'fuFuanType',
		dict:'FU_FUAN_COND'
});

var payTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '支付方式',
		emptyText: '请选择...',
		name:'payType',
		dict:'PAY_TYPE'
});

var orderTypeCombobox = Ext.create("Ext.ux.form.TableComboBox",{  
		fieldLabel : '订单类型',
		emptyText: '请选择...',
//		editable : false,
		showDisabled:false,
		url : 'main/sale/getOrderByCust/' + null+'/'+null,
		name:'orderType'
});//SALE_STATUS
var orderStautsCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '订单类型',
		emptyText: '请选择...',
		name:'orderStatus',
		hidden:true,
		dict:'SALE_STATUS'
});//SALE_STATUS
var orderHuanJieCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '订单环节',
		emptyText: '请选择...',
		name:'orderHuanJie',
		dict:'ORDER_HUAN_JIE'
 });

var isYpCombobox=Ext.create("Ext.ux.form.DictCombobox",{
		fieldLabel:'是否样品',
		emptyText:'请选择...',
		name:'isYp',
		dict:'YES_NO'
});

var regioCombobox=Ext.create("Ext.ux.form.DictCombobox",{
	fieldLabel:'地区',
	emptyText:'请选择...',
	name:'regio',
	dict:'REGIO'
});

var bzirkCombobox=Ext.create("Ext.ux.form.DictCombobox",{
	fieldLabel:'大区',
	emptyText:'请选择...',
	name:'bzirk',
	dict:'BZIRK'
});
var saleFor=Ext.create("Ext.ux.form.DictCombobox",{
	fieldLabel:'产品组',
	emptyText:'请选择...',
	name:'saleFor',
	dict:'SALE_FOR'
});
//销售分类
/*var saleForCombobox=Ext.create("Ext.ux.form.DictCombobox",{
	fieldLabel:'销售分类',
	emptyText:'请选择...',
	name:'saleFor',
	dict:'SALE_FOR',
	showDisabled:false
});*/

Ext.define("SMSWeb.view.sale.Sale2FormView", {
			extend : 'Ext.ux.form.SearchForm',
			alias : 'widget.Sale2FormView',// 面板的别名
			id:'sale2Form',
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
                    xtype:'hiddenfield',
                    fieldLabel: 'queryType',
                    name: 'queryType',
                    value:queryType
                },
				{
                    xtype:'textfield',
                    fieldLabel: '订单编号',
                    name: 'orderCode'
                },
//				{
//		        	xtype:'dictcombobox',
//					fieldLabel : '订单类型',
//					emptyText: '请选择...',
//					name:'orderType',
//					dict:'ORDER_TYPE'
//		        }
                orderTypeCombobox,
                orderStautsCombobox
				,{
                    xtype:'datefield',
                    fieldLabel: '订单日期从',
                    name: 'startDate',
                    format :'Y-m-d'
                },{
                    xtype:'datefield',
                    fieldLabel: '到',
                    name: 'endDate',
                    format :'Y-m-d'
	            },{
                    xtype:'textfield',
                    fieldLabel: '售达方',
                    name: 'shouDaFang'
                },{
                    xtype:'textfield',
                    fieldLabel: '售达方名称',
                    name: 'kunnrName1'
                },{
                    xtype:'textfield',
                    fieldLabel: '店面电话',
                    name: 'dianMianTel'
                },{
                    xtype:'textfield',
                    fieldLabel: '客户姓名',
                    name: 'name1'
                },{
                    xtype:'textfield',
                    fieldLabel: '联系电话',
                    name: 'tel'
                },{
                    xtype:'textfield',
                    fieldLabel: '补购参考订单编号',
                    name: 'serialNumber'
                },orderHuanJieCombobox,isYpCombobox,{
                    xtype:'textfield',
                    fieldLabel: '安装地址',
                    name: 'address'
                },regioCombobox,bzirkCombobox,/*saleForCombobox,*/
                {
                    xtype:'datefield',
                    fieldLabel: '预计出货日期从',
                    name: 'yuJiDateF',
                    format :'Y-m-d'
                },
                {
                	xtype:'datefield',
                	fieldLabel:'到',
                	name:'yuJiDateT',
                	format:'Y-m-d'
                },
                saleFor,
				{
                    xtype:'textfield',
                    fieldLabel: 'sap号',
                    name: 'sapOrderCode'
                }
			]
			
		});