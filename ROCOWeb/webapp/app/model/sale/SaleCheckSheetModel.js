Ext.define("SMSWeb.model.sale.SaleCheckSheetModel",{
	extend:'Ext.data.Model',
	fields:[

		{ name: 'postdate', type: 'date', dateFormat:'Y-m-d'/*renderer : Ext.util.Format.dateRenderer('Y-m-d')*/},
		{ name: 'sapordercode', type: 'string' },     
        { name: 'textorder', type: 'string' },
        { name: 'receiptamount', type: 'float' },
        
        { name: 'placeamount', type: 'float' },
        { name: 'balanceamount', type: 'float' },
        
        { name: 'orderreason', type: 'string' },
        { name: 'bezei', type: 'string' },
        { name: 'releasedate', type: 'date', dateFormat:'Y-m-d'/*renderer : Ext.util.Format.dateRenderer('Y-m-d')*/}
        
	]
});