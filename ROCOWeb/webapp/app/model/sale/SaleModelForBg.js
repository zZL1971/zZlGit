Ext.define("SMSWeb.model.sale.SaleModelForBg",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		
		{ name: 'orderCode', type: 'string' },
		{ name: 'orderType', type: 'string' },
		{ name: 'orderDate', type: 'date',dateFormat:'Y-m-d'/*renderer : Ext.util.Format.dateRenderer('Y-m-d')*/},//format: 'Y-m-d' 两个都可以用
		{ name: 'orderStatus', type: 'string' },
		{ name: 'shouDaFang', type: 'string' },
		{ name: 'dianMianTel', type: 'string' },
		{ name: 'orderTotal', type: 'float' },
		{ name: 'fuFuanCond', type: 'string' },
		{ name: 'fuFuanMoney', type: 'float' },
		{ name: 'payType', type: 'string' },
		{ name: 'sapOrderCode', type: 'string' },
	   	
        { name: 'name1', type: 'string' },
        { name: 'tel', type: 'string' },
        { name: 'jdName', type: 'string' },
        { name: 'kunnrName1', type: 'string' },
        {name:'createUser'},
		{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'},
		{ name: 'orderEvent', type: 'string' },
	]
});