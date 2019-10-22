Ext.define("SMSWeb.model.print.BarCodePrintModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'ID', type: 'string' },
		{ name: 'P_ORDER_CODE',type:'string'},
		{ name: 'SAP_ORDER_CODE',type:'string'},
		{ name: 'ORDER_CODE',type:'string'},
		{ name: 'ORDER_DATE',type:'string'},
		{ name: 'NAME',type:'string'},
		{ name: 'SHOU_DA_FANG',type:'string'}
	]
});