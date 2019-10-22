Ext.define("SMSWeb.model.sale.Sale2Model",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{ name: 'pid', type: 'string' },
		{ name: 'serialNumber', type: 'string' },
//		{ name: 'type', type: 'string' },
//		{ name: 'colour', type: 'string' },
//		{ name: 'unitPrice', type: 'float' },
//		{ name: 'zheKou', type: 'float' },
//		{ name: 'zheKouJia', type: 'float' },
//		{ name: 'amount', type: 'float' },
//		{ name: 'touYingArea', type: 'float' },
//		{ name: 'totalPrice', type: 'float' },
		
		{ name: 'orderCode', type: 'string' },
		{ name: 'orderType', type: 'string' },
		{ name:'shiJiDate',type:'date',dateFormat:'Y-m-d H:i:s'},
		{ name:'yuJiDate',type:'date',dateFormat:'Y-m-d H:i:s'},
		{ name: 'orderDate', type: 'date', dateFormat:'Y-m-d'/*renderer : Ext.util.Format.dateRenderer('Y-m-d')*/},//format: 'Y-m-d' 两个都可以用
		{ name: 'orderStatus', type: 'string' },
		{ name: 'shouDaFang', type: 'string' },
		{ name: 'dianMianTel', type: 'string' },
		{ name: 'designerTel', type: 'string' },
		{ name: 'orderTotal', type: 'float' },
		{ name: 'fuFuanCond', type: 'string' },
		{ name: 'fuFuanMoney', type: 'float' },
		{ name: 'payType', type: 'string' },
		
		{ name: 'pOrderCode', type: 'string' },
		{ name: 'sapOrderCode', type: 'string' },
	   	
        { name: 'name1', type: 'string' },
        { name: 'tel', type: 'string' },
        { name: 'jdName', type: 'string' },
        { name:'kunnrName1'},
        { name:'createUser'},
		{ name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{ name:'isYp',type:'string'}/*,
		{ name:'saleFor',type:'string'}*/
	]
});