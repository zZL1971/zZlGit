Ext.define("SMSWeb.model.sale.SaleModelForHoleInfo",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{ name: 'orderid', type: 'string' },
		{ name: 'thickness', type: 'string' },
		{ name: 'cthickness', type: 'string' },
		{ name: 'width', type: 'string' },
		{ name: 'cwidth', type: 'float' },
		{ name: 'length', type: 'string' },
		{ name: 'clength', type: 'float' },
		{ name: 'cnt', type: 'string' },
		{ name: 'name', type: 'string' },
        { name: 'info1', type: 'string' },
        { name: 'cncBarcode1', type: 'string'},
        { name: 'cncBarcode2', type: 'string'}
	]
});