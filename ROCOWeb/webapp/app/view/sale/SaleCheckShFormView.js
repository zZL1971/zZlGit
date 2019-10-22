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

var orderTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
		fieldLabel : '订单类型',
		emptyText: '请选择...',
		name:'orderType',
		dict:'ORDER_TYPE'
});

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

Ext.define("SMSWeb.view.sale.SaleCheckShFormView", {
			extend : 'Ext.form.Panel',
			alias : 'widget.SaleCheckShFormView',// 面板的别名
			id:'SaleCheckSh22FormView',
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
                    xtype:'hiddenfield',
                    fieldLabel: 'queryType',
                    name: 'queryType'
                    //value:queryType
                }
				,{
                    xtype:'textfield',
                    fieldLabel: '售达方',
                    name: 'shouDaFang',
                    hidden:true
                },{
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
			                    labelStyle : 'padding-left:15px;',
			                    fieldLabel: '信贷额度',
			                    name: 'xinDai',
			                    readOnly:true,
			                    fieldStyle:'text-align:right;'
				            }
			]
			
		});