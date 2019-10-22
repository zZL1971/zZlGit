Ext.define("SMSWeb.model.cust.CustModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{ name: 'pid', type: 'string' },
		{ name: 'kunnr', type: 'string' },
		{ name: 'name1', type: 'string' },
		{ name: 'ktokd', type: 'string' },
		{ name: 'telNumber', type: 'string' },
		{ name: 'bzirk', type: 'string' },
		{ name: 'xinDai', type: 'string' },
		{ name: 'kunnrS', type: 'string' },
//	   	{ name: 'startDate', type: 'date', renderer : Ext.util.Format.dateRenderer('Y-m-d')},//format: 'Y-m-d' 两个都可以用
//        { name: 'endDate', type: 'date', renderer : Ext.util.Format.dateRenderer('Y-m-d')},//format: 'Y-m-d'
        { name: 'zheKou', type: 'float' },
        { name: 'rowStatus', type: 'string' },
        {name:'nh',type:'String'},
        {name:'jh',type:'String'}
//		,
//        { name: 'total', type: 'float' },
//        { name: 'shengYu', type: 'float' }
	]
});