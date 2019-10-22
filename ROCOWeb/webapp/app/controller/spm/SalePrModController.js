Ext.define("SMSWeb.controller.spm.SalePrModController", {
	extend : 'Ext.app.Controller',
	refs : [
	{
		ref: 'spmForm',
        selector: 'salePrModView > form:first'
	},{
		ref: 'spmGrid',
        selector: 'salePrModView > grid:first'
	},{
		ref: 'spmToolBaskd',
        selector: 'salePrModView textfield[name=bstkd]'
	},{
		ref: 'spmToolVbeln',
        selector: 'salePrModView textfield[name=vbeln]'
	},{
		ref: 'spmLookForm',
        selector: 'lookSalePrModView > form:first'
	},{
		ref: 'spmFormVbeln',
        selector: 'salePrModView textfield[name=abgru]'
	},{
		ref: 'spmLookGrid',
        selector: 'lookSalePrModView > grid:first'
	}],
	prices:[],
	init : function() {
		var me = this;
		//初始化价格条件
		Ext.Ajax.request({
			url : 'main/sd/pr/find/calCond',
			method : 'GET',
			frame : true,
			dataType : "json",
			contentType : 'application/json',
			success : function(response, opts) {
				var values = Ext.decode(response.responseText);
				if(values.success){
					me.prices=values.data;
				} else {
					Ext.MessageBox.alert("提示信息",values.errorMsg);
				}
			},
			failure : function(response, opts) {
				Ext.MessageBox.alert("提示信息","查询不到价格条件");
			}
		});
		
		this.control({
			
			//监控抬头的拒绝原因
			'salePrModView >form:first textfield[name=abgru]':{
				change:function(obj,newValue,oldValue,eOpts){
					if(oldValue==null||oldValue==undefined){//刚开始加载时旧值是null undefined 则为空字符串
						oldValue="";
					}
					if(newValue!=oldValue){//防止加载时计算订单总金额
						var items_=me.getSpmGrid().getStore().getRange();
						if(newValue!==null&&newValue!==undefined&&newValue!==''&&newValue!=='51'){//取消整个单
							for(i in items_){
								var item_=items_[i];
								item_.set('stateAudit','QX');
								item_.set('pr00','0');
								item_.set('abgru',newValue);
							}
							me.getSpmForm().getForm().findField("fuFuanMoney").setValue('0');
						}else{//恢复整单
							for(i in items_){
								var item_=items_[i];
								item_.set('abgru',newValue);
								calcGridItem(item_,me.prices);
							}
							calcFuFuanMoney(me);
						}
					}
				}
			},
			
			//提交表单
			'window[itemId=customWindow] button[itemId=saveForm]':{
				click: function( btn, e, eOpts ) {	
					var gridItems=me.getSpmGrid().getStore().getRange();
					var modItem=[];
					for( i in gridItems){
						var item=gridItems[i];
						if(item.dirty==true){
							modItem.push(item.data);
						}
					}
					if(modItem.length>0){
						Ext.Ajax.request({
							url : '/main/sd/pr/mod/save',
							async : false,
							jsonData : {
								spmHeaderModel : me.getSpmForm().getValues(),
								spmItemModelSet : modItem
							},
							method : 'POST',
							dataType : "json",
							contentType : "application/json",
							callback : function(options, success, response) {
								if (!success) {
									Ext.Msg.show({
										title : "错误代码:S-500",
										icon : Ext.Msg.ERROR,
										msg : "链接服务器失败，请稍后再试或联系管理员!",
										buttons : Ext.Msg.OK
									});
								}
							},
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								var msgs=values.data;
								var str="";
								for(i in msgs){
									str=str+msgs[i]+"<br/>";
								}
								Ext.Msg.alert("提示信息", str);
								btn.up('window').close();
							},
							failure : function(response, opts) {
								Ext.Msg.alert("can't", 'error');
							}
						});
					}else{
						Ext.Msg.alert("提示信息", '没有修改过价格');
					}
				}
			},
			//重置表单
			'window[itemId=customWindow] button[itemId=resetForm]':{
				click: function( btn, e, eOpts ) {
					me.getSpmForm().getForm().reset();
					me.getSpmGrid().getStore().removeAll();
				}
			},
			//根据订单编号或者SAP编号查询
			'salePrModView [itemId=spm_search]':{
				click: function( btn, e, eOpts ) {
					//request-->ajax-->result-->form
					if(me.getSpmToolBaskd().getValue()!=null||me.getSpmToolBaskd().getValue()!=""||
							me.getSpmToolVbeln().getValue()!=null||me.getSpmToolVbeln().getValue()!=""){
						Ext.Ajax.request({
							url : 'main/sd/pr/search/saleOrder',
							params : {
								baskd:me.getSpmToolBaskd().getValue()//,
								//vbeln:me.getSpmToolVbeln().getValue()
							},
							method : 'POST',
							frame : true,
							dataType : "json",
							contentType : 'application/json',
							success : function(response, opts) {
								var values = Ext.decode(response.responseText);
								if(values.success){
									me.getSpmForm().getForm().reset();
									me.getSpmGrid().getStore().removeAll();
									me.getSpmForm().getForm().setValues(values.data.spmHeaderModel);
									me.getSpmGrid().getStore().add(values.data.spmItemModelSet);
								} else {
									Ext.MessageBox.alert("提示信息",values.errorMsg);
								}
							},
							failure : function(response, opts) {
								var values = Ext.decode(response.responseText);
								Ext.MessageBox.alert("提示信息",values.errorMsg);
							}
						});
					}else{
						Ext.MessageBox.alert("提示信息","请输入订单编号或者SAP编号");
					}	
				}
			},
			'salePrModView > grid:first':{
				edit: me.calcItemPrice
			},
			//根据id查询修改记录
			'uxgrid[headerModule=TM_SALE_PRMOD_LOG]':{
				itemEditBtnClk:function(grid,rowIndex,colIndex){
					var record = grid.getStore().getAt(rowIndex);
					var height_ = document.body.clientHeight  * 0.95;
					var width_ = document.body.clientWidth*0.95;
					Ext.create('Ext.window.Window',{
						title : '查看',
						itemId:'customWindow',
			            width:width_,
			            height:height_,
			            layout:'fit',
			            modal : true,
			            plain : true,
			            border:false,
						items:[Ext.create('SMSWeb.view.spm.LookSalePrModView')],
						listeners:{
							close:function(){
								
							}
						}
					}).show();
					
					
					Ext.Ajax.request({
						url : 'main/sd/pr/findById',
						params : {
							id:record.get("id")
						},
						method : 'POST',
						frame : true,
						dataType : "json",
						contentType : 'application/json',
						success : function(response, opts) {
							var values = Ext.decode(response.responseText);
							if(values.success){
								me.getSpmLookForm().getForm().setValues(values.data.spmHeaderModel);
								me.getSpmLookGrid().getStore().add(values.data.spmItemModelSet);								
							} else {
								Ext.MessageBox.alert("提示信息",values.errorCode+"-->"+values.errorMsg);
							}
						},
						failure : function(response, opts) {
							Ext.MessageBox.alert("提示信息","查询不到该订单");
						}
					});
					
				}
			}
			
		});
		
		//var obj_ = Ext.ComponentQuery.query('uxgrid[headerModule=TM_SALE_PRMOD_LOG]');
		//console.log(obj_);
	},
	calcItemPriceFunc:function(record){
		var me = this;
		var total = 0;
		var num = record.get("kwmeng");
		for(x in me.prices){
			var subTotal=0;
			var pri_cond=me.prices[x]
			var type=pri_cond.TYPE.toLowerCase();//价格类型编码
			var type_dsec=pri_cond.TYPE_DSEC;//价格类型中文
			var pom=pri_cond.PLUS_OR_MINUS;//加减
			var cdn=pri_cond.CONDITION;//运算符
			var itn=pri_cond.IS_TAKE_NUM;//乘数量
			
			var pri=record.get(type);//价格
			console.log("["+type+type_dsec+"]:"+pri+"[加减]："+pom+"[运算符]："+cdn+"[乘数量]："+itn);
			//先运算
			if("1"==cdn){//加
				subTotal=pri;
			}else if("2"==cdn){//减
				subTotal=pri;
			}else if("3"==cdn){//乘
				subTotal=accMul(total,pri);
			}else if("4"==cdn){//除
				suTotal=accDiv(total,pri);
			}
			
			//乘数量
			if("1"==itn){
				subTotal=accMul(subTotal,num);
			}else{
				subTotal=subTotal;
			}
			
			//用运算结果加减
			if("1"==pom){//=加
				total=accAdd(total,subTotal);
			}else{
				total=accSub(total,subTotal);
			}
		}
		return total;
	},
	
	//编辑行项触发的方法
	calcItemPrice:function(editor, e){
		//console.log("编辑grid行");
		var me=this;
		var abgru=e.record.get("abgru");
		if(abgru==null||abgru==''||abgru==undefined){
			console.log("编辑abgru"+abgru);
			me.getSpmForm().getForm().findField("abgru").setRawValue('');
		}
		
		calcGridItem(e.record,me.prices);
		calcFuFuanMoney(me);
	},
	views : ['spm.SalePrModView']
});


//计算某个行项目
function calcGridItem(item,prices){
	var total=0;
	var reject=item.get("abgru");//拒绝原因
	//console.log("拒绝原因:"+item.get("abgru"));
	if(reject!==null&&reject!==undefined&&reject!==''&&reject!=='51'){//该行项目被拒绝了
		item.set('stateAudit','QX');
		item.set('pr00','0');
	}else{
		if(item.get("stateAudit")=='QX'){
			item.set('stateAudit','E');
		}
		var num=item.get("kwmeng");//行项目数量
		for(x in prices){//运算规则
			var subTotal=0;
			var pri_cond=prices[x]
			var type=pri_cond.TYPE.toLowerCase();//价格类型编码
			var type_dsec=pri_cond.TYPE_DSEC;//价格类型中文
			var pom=pri_cond.PLUS_OR_MINUS;//加减
			var cdn=pri_cond.CONDITION;//运算符
			var itn=pri_cond.IS_TAKE_NUM;//乘数量
			
			var pri=item.get(type);//价格
			//先运算
			if("1"==cdn){//加
				subTotal=pri;
			}else if("2"==cdn){//减
				subTotal=pri;
			}else if("3"==cdn){//乘
				subTotal=accMul(total,pri);
			}else if("4"==cdn){//除
				suTotal=accDiv(total,pri);
			}
			
			//乘数量
			if("1"==itn){
				subTotal=accMul(subTotal,num);
			}else{
				subTotal=subTotal;
			}
			
			//用运算结果加减
			if("1"==pom){//=加
				total=accAdd(total,subTotal);
			}else{
				total=accSub(total,subTotal);
			}
		}
		item.set("pr00",total);//行项折后金额		
	}
}

//计算订单付款总金额和订单总金额
function calcFuFuanMoney(root){
	var saleItems=root.getSpmGrid().getStore().getRange();
	var orderTotal=0;//订单总金额=行项目的折后金额累加
	for( i in saleItems){
		var item=saleItems[i];
		//gridTotal=accAdd(gridTotal,item.data.pr00);
		orderTotal=accAdd(orderTotal,item.data.pr00);
	}
	
	var loanAmount=root.getSpmForm().getForm().findField("loanAmount").getValue();//借贷金额
	
	//付款条件
	var zterm=root.getSpmForm().getForm().findField("zterm").getValue();
	var ztermValue=0;
	if(zterm=="1"||zterm==1){
		ztermValue=1;
	}else if(zterm=="2"||zterm==2){
		ztermValue=0;
	}else if(zterm=="3"||zterm==3){
		ztermValue=0.5;
	}else if(zterm=="4"||zterm==4){
		ztermValue=0.3;
	}
	//console.log("orderTotal:"+orderTotal);
	//console.log("loanAmount:"+loanAmount);
	var newOrderTotal=accAdd(orderTotal,loanAmount);
	//console.log("orderTotal:"+newOrderTotal)
	root.getSpmForm().getForm().findField("netwr").setValue(newOrderTotal);//订单金额
	root.getSpmForm().getForm().findField("fuFuanMoney").setValue(accMul(ztermValue,newOrderTotal).toFixed(2));//付款金额=付款条件*订单金额

}



/**
 * 两个浮点数求和
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accAdd(num1,num2){
   var r1=0,r2=0,m;
   try{
	   if(num1.toString().split('.').length>1){
		   r1 = num1.toString().split('.')[1].length;
	   }
   }catch(e){
	   console.log(e);
   }
   try{
	   if(num2.toString().split(".").length>1){
		   r2=num2.toString().split(".")[1].length;
	   }  
   }catch(e){
	   console.log(e);
   }
   m=Math.pow(10,Math.max(r1,r2));
   // return (num1*m+num2*m)/m;
   return (Math.round(num1*m+num2*m)/m).toFixed(2);
}
/**
 * 两个浮点数相减
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accSub(num1,num2){
   var r1=0,r2=0,m;
   try{
	   if(num1.toString().split('.').length>1){
		   r1 = num1.toString().split('.')[1].length;
	   }
   }catch(e){
	   console.log(e);
   }
   try{
	   if(num2.toString().split(".").length>1){
		   r2=num2.toString().split(".")[1].length;
	   }
   }catch(e){
	   console.log(e);
   }
   m=Math.pow(10,Math.max(r1,r2));
   n=(r1>=r2)?r1:r2;
   return (Math.round(num1*m-num2*m)/m).toFixed(n);
}
/**
 * 两数相除
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accDiv(num1,num2){
   var t1=0,t2=0,r1,r2;
   try{
	   if(num1.toString().split('.').length>1){
		   t1 = num1.toString().split('.')[1].length;
	   }
   }catch(e){
	   console.log(e);
   }
   try{
	   if(num2.toString().split(".").length>1){
		   t2=num2.toString().split(".")[1].length;
	   }      
   }catch(e){
	   console.log(e);
   }
   r1=Number(num1.toString().replace(".",""));
   r2=Number(num2.toString().replace(".",""));
   return (r1/r2)*Math.pow(10,t2-t1);
}
/**
 * 两数相乘
 * @param {Object} num1
 * @param {Object} num2
 * @return {TypeName} 
 */
function accMul(num1,num2){
    var m=0,s1=num1.toString(),s2=num2.toString(); 
    try{
    	if(s1.split(".").length>1){//有小数点
    		m+=s1.split(".")[1].length
    	}
	}catch(e){
		console.log(e)
	};
    try{
    	if(s2.split(".").length>1){
    		m+=s2.split(".")[1].length
    	}
	}catch(e){
		console.log(e)
	};
    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);
}