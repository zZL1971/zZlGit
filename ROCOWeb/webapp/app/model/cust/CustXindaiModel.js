Ext.define("SMSWeb.model.cust.CustXindaiModel",{
	extend:'Ext.data.Model',
	fields:[
        {name:'skDate',type:'String'},
        {name:'orderNum',type:'String'},
        {name:'xmText',type:'String'},
        {name:'skMoney',type:'double'},
        {name:'xdMoney',type: 'double'},
        {name :'yeMoney' ,type:'double'}
	]
});