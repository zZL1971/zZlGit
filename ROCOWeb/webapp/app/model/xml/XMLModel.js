Ext.define("SMSWeb.model.xml.XMLModel",{
	extend:'Ext.data.Model',
	fields:[
		{ name: 'id', type: 'string' },
		{name:'stat',type:'string'},
		{name:'saleFor',type:'string'},
		{ name: 'textCode', type: 'string' },
		{ name: 'type', type: 'string' },
		{ name:'typeDesc',type: 'string'},
		{ name:'text',type: 'string'},
		{ name: 'textDesc', type: 'string'},
		{ name: 'updateUser', type: 'string' },
		{ name: 'counter', type: 'string' },
		{ name: 'updateTime', type:'date',dateFormat:'Y-m-d H:i:s' },
		{ name: 'createTime', type:'date',dateFormat:'Y-m-d H:i:s'}
	]
});