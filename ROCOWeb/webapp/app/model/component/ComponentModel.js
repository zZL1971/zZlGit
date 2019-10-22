Ext.define("SMSWeb.model.component.ComponentModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{name:'stat',type:'string'},
		{name:'line',type:'string'},
		{name:'componentName',type:'string'},
		{ name: 'identifyCode', type: 'string' },
		{ name: 'dnDelivery', type: 'int' },
		{ name:'dwDelivery',type: 'int'},
		{ name:'repairOrderDelivery',type: 'int'},
		{ name: 'outSourceIdentifyCode', type: 'string' },
		{ name: 'materialCode', type: 'string' },
		{ name: 'updateUser', type: 'string' },
		{ name: 'updateTime', type:'date',dateFormat:'Y-m-d H:i:s' },
		{ name: 'createTime', type:'date',dateFormat:'Y-m-d H:i:s'}
	]
});