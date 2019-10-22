Ext.define("SMSWeb.model.delivery.DeliveryModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{name:'stat',type:'string'},
		{name:'lineCode',type:'string'},
		{ name: 'dnDelivery', type: 'int' },
		{ name: 'dwDelivery', type: 'int' },
		{ name:'zoRepairOrder',type: 'int'},
		{ name:'zmRepairOrder',type: 'int'},
		{ name: 'zmDelivery', type: 'int'},
		{ name: 'isOutSource', type: 'string' },
		{ name: 'isMaterial', type: 'string' },
		{ name: 'updateUser', type: 'string' },
		{ name: 'updateTime', type:'date',dateFormat:'Y-m-d H:i:s' },
		{ name: 'createTime', type:'date',dateFormat:'Y-m-d H:i:s'}
	]
});