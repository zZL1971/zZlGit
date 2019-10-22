Ext.define("SMSWeb.model.mm.base.MaterialSJModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id',type: 'string' },
	   	{name:'meins',type: 'string' },
	   	{name:'matnr',type: 'string' },
		{name:'miaoshu',type: 'string' },
		{name:'amount' ,type:'int'},
		{name:'price',type: 'float' },
		{name:'zhekou',type: 'float' },
		{name:'totalPrice',type: 'float' },
		{name:'materialHeadId',type: 'string' }
	]
});