Ext.define("SMSWeb.model.cart.ShoppingCartModel", {
	extend:'Ext.data.Model',
	fields:[
	   	{name:'id'},
		{name:'createUser'},
		{name:'createTime'},
//		{name:'updateTime'},
		{name:'createtime1'},
		{name:'updatetime1'},
	    {name:'updateUser'},
		{name:'rowStatus'},
		
		
		{name:'longDesc'},
		{name:'widthDesc'},
		{name:'heightDesc'},
		{name:'isStandard'},
		
		
		{name:'kschl'},//条件类型
		{name:'vkorg'},//销售组织
		{name:'vtweg'},//分销渠道
		{name:'matnr'},//物料
		{name:'maktx'},//物料描述
		
		{name:'kbetr'},//金额
		{name:'kbetrDj'},//等级价格
		{name:'konwa'},//单位（货币）
		{name:'kpein'},//单位（条件定价单位）
		{name:'kmein'},//单位（条件单位）
		
		{name:'krech'},//条件计算类型
		{name:'datbi',type:'date'},//有效日期从
		{name:'datab',type:'date'},//有效日期到 
		{name:'matkl'},//物料组
		{name:'matkl2'},//物料再分组
		{name:'extwg'},//外部物料组    颜色
		
		{name:'mtart'},//物料类型
		{name:'meins'},//单位
		{name:'brgew'},//毛重
		{name:'ntgew'},//净重
		{name:'gewei'},//重量单位
		
		{name:'volum'},//体积
		{name:'voleh'},//体积单位
		{name:'spart'},//产品组
		{name:'prdha'},//产品层次
		{name:'vtext'},//产品层次描述
		
		{name:'groes'},//规格
		{name:'kzkfg'},//可配置物料
		{name:'kzkfgdesc'},//可配置物料
		{name:'ispic'},//是否有图片
		{name:'kbstat'},//冻结状态,'A'为冻结,空为未冻结
		{name:'loevmKo'},//删除状态,'X'为已删除,空为未删除
		{name:'serialNumber'},//非标序号
//		{name:'orderCount'},//总下单数
		{name:'productSpace'},//产品空间
		{name:'zzazdr'},//安装位置
		{name:'saleFor'},
		{name:'series'},//主题系列
		{name:'zzcpdj'}//产品等级
		
	]
});