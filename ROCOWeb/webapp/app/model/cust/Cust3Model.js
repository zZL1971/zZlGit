Ext.define("SMSWeb.model.cust.Cust3Model",{
	extend:'Ext.data.Model',
	fields:[
		
		
        {name:'tradeCompanyId',type:'String'},
        {name:'tradeAccNo',type:'String'},
        {name:'jrnNo',type:'String'},
        {name:'dealerNum',type:'String'},
        {name:'tradeTime',type: 'String'},
        {name :'tradeAmount' ,type:'double'},
        { name: 'payer', type: 'string' },	
        { name: 'statusNum', type: 'string' },	
        
	]
});