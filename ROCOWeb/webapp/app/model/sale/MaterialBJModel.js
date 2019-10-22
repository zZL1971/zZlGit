Ext.define("SMSWeb.model.sale.MaterialBJModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id',type: 'string' },
	   	{name:'meins',type: 'string' },
	   	{name:'fyhmeins',type: 'string' },
	   	{name:'matnr',type: 'string' },
		{name:'miaoshu',type: 'string' },
		{name:'amount' ,type:'int'},
		{name:'price',type: 'float' },
		{name:'zhekou',type: 'float' },
		{name:'totalPrice',type: 'float' },
		{name:'materialHeadId',type: 'string' },
		{name:'chanxian',type: 'string' },
		{name:'chanmiaoshu',type: 'string' },
		{name:'wsize',type: 'string' },
	]
});