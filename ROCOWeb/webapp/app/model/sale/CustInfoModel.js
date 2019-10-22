Ext.define("SMSWeb.model.sale.CustInfoModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'cust_id', type: 'string' },
		
		{ name: 'name', type: 'string' },
		{ name: 'sex', type: 'string' },
		//{ name: 'orderDate', type: 'date',dateFormat:'Y-m-d'/*renderer : Ext.util.Format.dateRenderer('Y-m-d')*/},//format: 'Y-m-d' 两个都可以用
		{ name: 'tel', type: 'string' },
		{ name: 'address', type: 'string' },
		{ name: 'jingShouRen', type: 'string' },
		{ name: 'huXing', type: 'string' },
		{ name: 'isYangBan', type: 'string' },
		{ name: 'isAnZhuang', type: 'string' },
		{ name: 'floor', type: 'string' },
		{ name: 'orderPayFw', type: 'string' },
        { name: 'custRemarks', type: 'string' },
        { name:'birthday',type:'string'}
      /*  { name: 'tel', type: 'string' },
        { name: 'jdName', type: 'string' },
        { name: 'kunnrName1', type: 'string' },
        {name:'createUser'},*/
		//{name:'createTime',type:'date',dateFormat:'Y-m-d H:i:s'}
	]
});