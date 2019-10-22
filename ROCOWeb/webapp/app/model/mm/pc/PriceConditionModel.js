Ext.define("SMSWeb.model.mm.pc.PriceConditionModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime',type:'date'},
		{name:'updateTime',type:'date'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		{name:'type'},
		{name:'typeDsec'},
		{name:'plusOrMinus'},
		{name:'condition'},
		{name:'conditionValue'},
		{name:'subtotal'},
		{name:'isTakeNum'},
		{name:'total'},
		{name:'orderby'}
	]
});