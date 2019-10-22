Ext.apply(Ext.form.VTypes, { 
    phonecheck : function(val, field) { 
	    var str=val;
	    //var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	    var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$/;
	    return reg.test(str);
	},
    phonecheckText : "号码不匹配!"  
});

Ext.apply(Ext.form.VTypes, { 
	mobilephonecheck : function(val, field) { 
	    var str=val;
	    //var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	    var reg=/^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$/;
	    return reg.test(str);
	},
    phonecheckText : "号码不匹配!"  
});

Ext.apply(Ext.form.VTypes, { 
	postcodecheck : function(val, field) { 
	    var str=val;
	    //var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	    var reg=/^[1-9]\d{5}$/;
	    return reg.test(str);
	},
	postcodecheckText : "邮政编码格式不对，是6位数!"  
});

Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.sale.NewSaleWindowInnerContent', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewSaleWindowInnerContent',
			border : false,
//    		autoScroll:true,	 
			overflowY:'auto',
		    bodyStyle:{
	        	'background-color':'#f6f6f6'
	        },
	        requires : ["Ext.ux.form.TrieCombobox"],
			layout:'border',
//			overflowY:'auto',
			formId:null,
			editFlag:null,
			jdName:null,
			kunnr:null,
			orderType:null,
			pOrderCode:null,
			bodyPadding:'0 13 0 0',
			orderType:null,
			initComponent : function() {
	        	//add by mark on 20160612 start
				var expiredWindow=Ext.create('widget.window',{
	                closeAction: 'hide',
	                closable:true,
	                resizable:false,
	                itemId:'expiredWindow',
	                layout:'vbox',
	                title:'超期',
	                modal:true,
	                width:400,
	                height:230,
	                tbar:[{
						xtype : 'button',
						text : '保存',
						itemId: 'saveExpired',
						iconCls:'table_save',
						handler:saveExpired
	                }],
	                items:[{
	                	xtype:'dictcombobox',
	                	itemId:'expiredType',
	                	allowBlank:false,
	                	fieldLabel:'超期类型<span style="color:red;">*</span>',
	                	dict:'EXPIRED_ORD_TYPE',
						style:'margin-top:10px;margin-left:10px;margin-bottom:5px;'
	                },{
	                	xtype:'textarea',
	                	itemId:'expiredReason',
	                	allowBlank:false,
	                	fieldLabel:'超期原因<span style="color:red;">*</span>',
						style:'margin-left:10px;margin-bottom:20px;'
	                },{
	                	xtype:'hiddenfield',
	                	itemId:'assignee',
	                	value:CURR_USER_LOGIN_NO
	                },{
	                	xtype:'hiddenfield',
	                	itemId:'duration'
	                }]
				});
				//add by mark on 20160612 end
				var unit = Ext.create('Ext.ux.form.DictCombobox',{dict:'UNIT'});
				var saleFor = Ext.create('Ext.ux.form.DictCombobox',{dict:'SALE_FOR'});
				 
				var isStandardCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '是否标准产品',
						emptyText: '请选择...',
						name:'isStandard',
						dict:'MATERIAL_IS_STANDARD'
				});
				
				var stateAuditCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '审核状态',
						emptyText: '请选择...',
						name:'stateAudit',
						dict:'MATERIAL_STATE_AUDIT'
				});
				
				var materialMtartCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '产品类型',
						emptyText: '请选择...',
						name:'materialMtart',
						dict:'MATERIAL_MTART'
				});
				
				var kunnrRegionCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						//fieldLabel : '地区',
						//emptyText: '请选择...',
						name:'kunnrRegion',
						dict:'KUNNR_REGION'
				});
				
				var kunnrYsRegionCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						//fieldLabel : '运输区域',
						//emptyText: '请选择...',
						name:'kunnrYsRegion',
						dict:'KUNNR_YS_REGION'
				});
				
				var drawTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						//fieldLabel : '绘图类型',
						//emptyText: '请选择...',
						name:'drawType',
						dict:'MATERIAL_DRAW_TYPE'
				});
				var zzwgfgCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
					fieldLabel : '是否外购',
					emptyText: '请选择...',
					name:'zzwgfg',
					dict:'MM_ZZWGFG'
				});
				var cclbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzelb',
					 dict:'CCLB'
				 });
				var cczbCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'cczb',
					 dict:'CCZB'
				 });
				var ccbmCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzebm',
					 dict:'CCBM'
				 });
				var cczxCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					 emptyText: '请选择...',
					 name:'zzezx',
					 dict:'CCZX'
				 });
				var ccwtCombobox = Ext.create("Ext.ux.form.DictCombobox",{
					emptyText: '请选择...',
//					name:'zzezx',
					dict:'CCWT'
				});
				/*var saleForCombobox=Ext.create("Ext.ux.form.DictCombobox",{
					fieldLabel : '销售方式<font color=red>*</font>',
					emptyText: '请选择...',
					allowBlank: false,
					name:'saleFor',
					dict:'SALE_FOR',
					id:'saleFor',
					showDisabled:false
				});*/
				 var me = this;
				 var _formId = me.formId;
				 var _judge=false;
				 if(_formId != null ||_formId !=undefined){
					 var obj = Ext.ux.DataFactory.getFlowActivityId(me.formId);
					 //usertask_drawing订单审绘
					 //_judge=true显示数据字典“中文”,false时显示“繁体”
					 if(obj.taskdefId == 'usertask_drawing'){
						 _judge = true;
					 }
				 }
				 var jiaoQi = Ext.create('Ext.ux.form.DictCombobox',{dict:'JIAO_QI_TIAN_SHU',judge:_judge});
				 var jiaoQiB = Ext.create('Ext.ux.form.DictCombobox',{dict:'JIAO_QI_TIAN_SHU_B',judge:_judge});
				 
				 var form = Ext.widget('form',{
		    		    region:'north',  
			    	    itemId:'saleForm',
			    	    minHeight:240,
						bodyStyle : "background-color: #157FCC;",//padding-top:10px;padding-bottom:5px
						border : false,
						tbar : [{
							xtype : 'button',
							text : '保存',
							id : 'saveOrder',
							iconCls:'table_save',
							hidden:false
						},{
							xtype : 'button',
							text : '传输SAP',
							id : 'tranSap',
							iconCls:'lorry_go',
							hidden:true
						},'->',{
							xtype : 'button',
							text : '审批操作',
							margin:'0 30 0 0',
							id : 'checkSale',
							iconCls:'flow_opt',
							handler : function() {
								var bpm = "MainProductQuotation";
								if("buDan"==me.orderType || "OR3"==me.orderType || "OR4"==me.orderType){
									bpm = "NewCustomerServiceOrdProcess";
								}
								Ext.create('Ext.ux.window.FlowWindow',{itemId:'flowwindow',uuid:'form[itemId=saleForm] hiddenfield[name=id]',uuidNo:'form[itemId=saleForm] textfield[name=orderCode]','bpm':bpm,listeners:{
									//激活流程前
									boforeActivate:function(form){
										var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
										var _saleId = _saleIdField.getValue();
										var flag = saleBoforeActivate(_saleId);
										return flag;
									},
									//激活流程后
									afterActivate:function(){
										var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
										var _saleId = _saleIdField.getValue();
										var flag = saleAfterActivate(_saleId);
										return flag;
									},
									boforeCommit:function(form,flowtype,nextflowId){
										var _saleIdField = Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0];
										var _saleId = _saleIdField.getValue();
										var flag = flowValidate(_saleId,flowtype);
										return flag;
//										return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
									},
									afterCommit:function(){
										//提交后关闭窗口并重新查询订单信息
										Ext.ComponentQuery.query('window[itemId=newSaleWindow]')[0].close();
										var sale2Form = Ext.ComponentQuery.query('panel[id=sale2Form]')[0];
										if("undefined"!=typeof(sale2Form)){
											var formValues = sale2Form.getValues();
										    var grid = Ext.ComponentQuery.query('panel[id=sale2Grid]')[0];
										    var store = grid.getStore();
										    store.load({
										    	params:formValues,
										    	callback:function(r,options,success){
										            if(success){
										           }
										        }
										    });
										}
										return true;//必须返回为true window里面的commit才会继续触法 ，如果为false，方法将停止执行
									}
								}}).show();
							},
							hidden:false
						}],
						fieldDefaults : {
							labelWidth : 120,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
						},
						items : [{
						    xtype:'tabpanel',
						    itemId:'centerTabpanel',
//						    bodyStyle : "border-left:0;border-right:0;border-bottom:0;",
						    border : false,
						    items:[
						    	{	title: '订单信息',
						    		//background-color: #D0DEF0;
						    		border : false,
						    		bodyPadding: 5,
						    		autoScroll:true,
						    		layout: {
								        type: 'table',
								        columns: 4
								    },
								    margin:'0 40 0 0',
								    maxWidth:1400,
						    		height: 260,
						    		autoScroll:true,
						    		items:[
						    		{
										xtype:'hiddenfield',
										name : 'id'
							        },
							        {
										xtype:'hiddenfield',
										name : 'tcId'//TerminalClient的Id
							        },
							        {
										xtype:'hiddenfield',
										name : 'createUser'
							        },
							        {
							        	xtype:'hiddenfield',
							        	name : 'serialNumber'
							        },
							        {
										name : 'createTime',
										xtype:'datefield',
										format:'Y-m-d H:i:s',
										hidden:true
										//format :'Y-m-d\\TH:i:s'
							        },/*{
				                    	hidden:true,
				                    	name : 'sapCreateDate',
				                    	xtype:'datefield',
				                    	format:'Y-m-d H:i:s'
				                    },*/{
				                    	name : 'urgentType',
				                    	xtype:'hiddenfield',
				                    },{
										xtype:'hiddenfield',
										name : 'vgbel'//借贷项编号
							        },
							        {
										xtype:'hiddenfield',
										name : 'updateUser'
							        },
							        {
										xtype:'datefield',
										format:'Y-m-d H:i:s',
										name : 'updateTime',
										hidden:true
										//format :'Y-m-d\\TH:i:s'
							        },
							        {
										xtype:'hiddenfield',
										name : 'checkDrawUser'
							        },
							        {
										xtype:'hiddenfield',
										name : 'checkPriceUser'
							        },
							        {
										xtype:'hiddenfield',
										name : 'confirmFinanceUser'
							        },
							        {
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 4
									    },
								    	items : [{
									        xtype: 'textfield',
									        readOnly:true,
					                    	fieldStyle:'background-color: #FCFCFC; background-image: none;',
									        fieldLabel: '订单编号',
									        name: 'orderCode',
									        width: 300 
									    },{
											xtype : 'button',
											text : '...',
											itemId : 'orderCodeQueryButton',//iconCls:'table_save'
											hidden : true
										}]
						            },
						            {
							        	xtype:'dictcombobox',
										fieldLabel : '订单类型<font color=red>*</font>',
										emptyText: '请选择...',
										allowBlank: false,
										width: 300,
										name:'orderType',
										id:'orderType',
										dict:'ORDER_TYPE',
										listeners: {
											'change': function(obj,newValue,oldValue,eOpts) {
						            			if("undefined"==typeof(oldValue) || oldValue == null || oldValue == ""){
						            			}else{
						            				changeOrderType(me);
						            			}
								            }
								        }
							        },
						            {
					                    xtype:'datefield',
					                    fieldLabel: '订单日期',
					                    name: 'orderDate',
					                    format :'Y-m-d',
					                    width: 300,
					                    readOnly:true
					                } ,
							       {
								        xtype: 'displayfield',
								        fieldLabel: '生产预完工日期',
								        name: 'yuJiDate3',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
								        hidden:false,
								        format:'Y-m-d',
								        value: ''
							        },
					               /* {
								        xtype: 'textfield',
								        fieldLabel: 'SAP订单编号',
								        readOnly:true,
				                    	fieldStyle:'background-color: #FCFCFC; background-image: none;',
								        name: 'sapOrderCode',
								        width: 300 
								    },/*					                
                                    {
								        xtype: 'displayfield',
								        fieldLabel: '附件下载',
								        readOnly:true,
								        name: 'fujianpath',
				                    	fieldStyle:'background-color: #FCFCFC; background-image: none;',
								        name: 'fujianpath',
								        width: 300 ,
								    },*/ ,
						            {
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 3
									    },
								    	items : [{
								    		xtype: 'displayfield',
									        fieldLabel: '售达方（客户）<font color=red>*</font>',
									        name: 'shouDaFang',
									        labelWidth : 120,
									        width: 300,
									        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
									        value: '',
									        renderer : function(v) { 
										    	return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
										    }
								    	},{
											xtype : 'button',
											text : '...',
											itemId : 'shouDaFang_queryCust',//iconCls:'table_save'
											hidden : true
										}]
						            },{
							        	xtype:'displayfield',
										fieldLabel : '交期类型',
										//emptyText: '请选择...',
										width: 300,
										name:'jiaoQiStyle',
										hidden:true
										//showDisabled:_Jiaoqitianshu_showDisabled,
										//dict:'JIAO_QI_STYLE'
							        },{
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 3
									    },
								    	items : [
								    		{
									    		xtype: 'displayfield',
										        fieldLabel: '送达方（物流园）<font color=red>*</font>',
										        name: 'songDaFang',
										        labelWidth : 120,
										        width: 300,
										        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
										        value: '',
										        renderer : function(v) { 
											    	return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
											    }
									    	},{
											xtype : 'button',
											text : '...',
											itemId : 'songDaFang_queryCust'//,iconCls:'table_save'
										}]
						            },
						            {
								        xtype: 'textfield',
								        fieldLabel: '售达方名称',
								        readOnly:true,
				                    	fieldStyle:'background-color: #FCFCFC; background-image: none;',
								        name: 'kunnrName1',
								        width: 300
								    },
								    {
								        xtype: 'displayfield',
								        fieldLabel: '预计出货日期',
								        name: 'yuJiDate',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
								        value: ''
						            },
						            {
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 3
									    },
								    	items : [
									            {
										    		xtype: 'displayfield',
											        fieldLabel: '参考订单<font color=red>*</font>',
											        name: 'pOrderCode',
											        width: 300,
											        hidden:false
									            }/*,{
													xtype : 'button',
													text : '...',
													itemId : 'pOrderCodeButton',//iconCls:'table_save'
													hidden:true
												}*/]
						            },
								    {
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 3
									    },
								    	items : [{
						                    xtype:'textfield',
						                    fieldLabel: '设计师联系电话',
						                    name: 'designerTel',
						                    vtype:'phonecheck',
						                    width: 300
							            }]
						            },/*{
								        xtype: 'displayfield',
								        fieldLabel: '订单状态',
								        name: 'orderStatus',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
								        value: ''
						            },*/   
						            {
					                    xtype:'textfield',
					                    fieldLabel: '店面联系电话<font color=red>*</font>',
//										allowBlank: false,
					                    name: 'dianMianTel',
					                    vtype:'phonecheck',
					                    width: 300
					                }, {
								        xtype: 'displayfield',
								        fieldLabel: '计划完工日期',
								        name: 'yuJiDate2',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
								        //,
								        //value: '2015-5-10'
						            },
					                {
							        	xtype:'dictcombobox',
										fieldLabel : '是否样品',
										emptyText: '请选择...',
										//disabled:true,
										readOnly:true,
										width: 300,
										name:'isYp',
										dict:'YES_NO'
							        }
						            ,
						            {
										cascadeDict : 'dictcombobox[itemId=shopCls]',
										xtype : 'triecombobox',
										fieldLabel : '店分类<font color=red>*</font>',
/*										allowBlank : CURR_USER_LOGIN_NO
										.indexOf("_01") > -1 ? false
										: true,// 只有_02可以不填
*/										emptyText : '请选择...',
										readOnly:true,
										width: 300,
										name : 'shop',
										id:'shop',
										trie : 'SHOP'
									}
					                ,
					                {
										itemId:'shopCls',
										cascade : true,
										xtype : 'dictcombobox',
										fieldLabel : '1店/N店<font color=red>*</font>',
/*										allowBlank : CURR_USER_LOGIN_NO
										.indexOf("_01") > -1 ? false
										: true,// 只有_02可以不填
*/										emptyText : '请选择...',
										readOnly:true,
										width: 300,
										name : 'shopCls',
										dict : 'SHOP_CLS',
										id:'shopCls'
									}
					                ,
						            {
								        xtype: 'displayfield',
								        fieldLabel: '实际入库日期',
								        name: 'shiJiDate2',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
								        //,
								        //value: '2015-5-10'
						            },
						            {
							        	xtype:'dictcombobox',
										fieldLabel : '处理时效',
										emptyText: '请选择...',
										width: 300,
										colspan: 1,
										name:'handleTime',
										dict:'HANDLE_TIME'
							        }
						            ,{
					                    xtype:'textfield',
					                    fieldLabel: '设计师QQ号',
					                    name: 'qqNum',
					                    width: 300,
					                    hidden:false
						            }
						            ,
						            {
							        	xtype:'dictcombobox',
										fieldLabel : '交期天数',
										emptyText: '请选择...',
										width: 300,
										judge:_judge,
										name:'jiaoQiTianShu',
										//showDisabled:_Jiaoqitianshu_showDisabled,
										dict:'JIAO_QI_TIAN_SHU'
							        },
									{
								        xtype: 'displayfield',
								        fieldLabel: '实际出库日期',
								        name: 'shiJiDate',
								        width: 300,
								        fieldStyle : "background-color:white;height:20px;padding-left:4px;"
								        //,
								        //value: '2015-5-9'
								    }
						            ,
						          /*  saleForCombobox,*/
						            {
								    	border:false,
								    	layout: {
									        type: 'table',
									        columns: 3
									    },
								    	items : [{
								    		xtype: 'displayfield',
									        fieldLabel: '附件下载',
									        name: '',
									        labelWidth : 120,
									        width: 300,
									        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
									        value: '',
//									        renderer : function(v) {}
								    	},{
											xtype : 'button',
											text : '...',
											itemId : 'fujianDownloadButtonClick',//iconCls:'table_save'
											hidden : true,
											listeners: {
												'click': function() {
													var _formId = me.formId;
													Ext.create('SMSWeb.view.sale.BgFujianWindow', 
															{loadStatus:'2',title:'附件',type:'OR3',formId:_formId}).show();
									            }
									        }
										}]
						            },
						            {
							            xtype: 'textareafield',
							            name: 'remarks',
							            fieldLabel: '备注',
							            colspan: 2,
							            //value: 'Textarea value',
							            //fieldStyle : 'background-color: #D0DEF0;',
							            cols:100,
							            rows:2
									}
									,{
										xtype: 'displayfield',
							            name: 'remarks2',
							            fieldLabel: '季度折扣',
							            colspan: 2,
							            //value: 'Textarea value',
							            //fieldStyle : 'background-color: #D0DEF0;',
							            cols:20,
							            rows:2
									},{
					                    xtype: 'textfield',
					                    fieldLabel : '内部订单号',
										padding:'0 0 0 1',
										id:'zzaufnrField',
										name:'zzaufnr',
										hidden:"OR9"==me.orderType?false:true
				                    },
				                    {
					                    xtype:'dictcombobox',
					                    fieldLabel: '通知质检检查',
					                    name: 'isQc',
					                    colspan: 1,
					                    width: 250,
					                    dict : 'YES_NO',
					                    hidden:"buDan"==me.orderType?false:true
							        },{
							        	xtype:'dictcombobox',
										fieldLabel : '需打木架',
										width: 300,
										colspan: 1,
										name:'isMj',
										dict:'YES_NO',
										hidden:"buDan"==me.orderType?false:true
							        },{
					                    xtype:'dictcombobox',
					                    fieldLabel: '通知客服检查',
					                    name: 'isKf',
					                    id: 'isKf',
					                    width: 250,
					                    colspan: 1,
					                    dict : 'YES_NO',
					                    hidden:"buDan"==me.orderType?false:true
									}
						            ]
					            },
					            {	title: '订单金额',
					            	hidden:true,
					    		    layout: 'border',
					    		    height: 240,
						    		autoScroll:false,
					    		    frame:false,
						    		boyder:0,
						    	    items:[{region: 'west', 
							                xtype:'form',
							                frame:false,
								    		boyder:0,
							                bodyPadding: 5,
							                items:[{ xtype: 'fieldcontainer',
							                     layout: 'vbox',
							                     combineErrors: true,
							                     defaultType: 'textfield',
							                     items:[
														
														{
												        	xtype:'dictcombobox',
															fieldLabel : '支付方式',
															emptyText: '请选择...',
															width: 180,
															labelWidth:70,
															name:'payType',
															dict:'PAY_TYPE',
															value:'C',
															readOnly:true
												        },{
										                    xtype:'numberfield',
										                    fieldLabel: '订单总额',
										                    name: 'orderTotal',
										                    width: 180,
															labelWidth:70,
													        minValue: 0,
													        readOnly:true,
													        value:0,
													        listeners: {
																'change': function(obj,newValue,oldValue,eOpts) {
																	calculateFuFuanMoney(form,me.editFlag);
													            }
													        }
														},
												        {
												        	xtype:'dictcombobox',
															fieldLabel : '付款条件',
															emptyText: '请选择...',
															width: 180,
															labelWidth:70,
															name:'fuFuanCond',
															dict:'FU_FUAN_COND',
															readOnly:true,
															listeners: {
																'change': function(obj,newValue,oldValue,eOpts) {
																	calculateFuFuanMoney(form,me.editFlag);
													            }
													        }
												        },
														{
											                    xtype:'numberfield',
											                    fieldLabel: '付款金额',
											                    align:'right',
											                    name: 'fuFuanMoney',
											                    width: 180,
																labelWidth:70,
														        minValue: 0,
														        readOnly:true,
														        value:0
														},{
										                    xtype:'numberfield',
										                    fieldLabel: '借贷金额',
										                    align:'right',
										                    name: 'loanAmount',
										                    width: 180,
															labelWidth:70,
													        minValue: 0,
													        readOnly:true,
													        value:0
														},
														{
												        	xtype:'dictcombobox',
															fieldLabel : '活动类型',
															emptyText: '请选择...',
															width: 180,
															labelWidth:70,
															name:'huoDongType',
															dict:'HUO_DONG_TYPE',
															readOnly:true
												        }
														
							                            ]}]
						                	},{
						                		 region: 'center',
								                 layout:'fit',
							                	 scroll:true,
							                	 frame:false,
										    	 boyder:0,
								                 items:[{
								                	xtype:'grid',
								                	id:'saleItemPriceGridId',
													features : [
														{
															ftype : 'groupingsummary',
															groupHeaderTpl : "{columnName}: {groupValue}"
														},
														{
															ftype : 'summary',
															dock : 'bottom' 
														}
													],
								                	frame:false,
											    	boyder:0,
								 		            store: {//配置数据源
								 		                fields: ['id','posnr',"kwmeng","pr00","pr01","pr02","pr05","pr03","pr04","zr01","zr02","zr03","zr04","zr05","zr06"],//定义字段
//								 		                data:[],
								 		                proxy:{
									 		          		type:'ajax',
									 		          		url:'main/sale/findSaleItemPrByPid',
									 		          		reader:{
									 		          			type:'json',
									 		          			root:'content',
									 		          			totalProperty :'totalElements'
									 		          		},
									 		          		writer:{
									 		          			type:'json'
									 		          		},
									 		          		listeners:{  
									 		          	        exception:Ext.ux.DataFactory.exception
									 		          	    }
									 		          	}
								 		            },
								 		            columns: [//配置表格列
						 		                        {header: "序号",xtype:'rownumberer',width:40,align:'left'},
								 		           	    {header: "行号", width:50, dataIndex:'posnr', sortable: true,align:'right',summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return "总计";
							 		                    }},
//								 		                {header: "数量", width: 50, dataIndex:'kwmeng', sortable: true},
								 		                {header: "折后金额", width: 75, dataIndex:'pr00', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value;
								 		                    }
								 		                },
								 		                {header: "商品原价", width: 75, dataIndex:'pr01', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "赠送(活动)", width: 75, dataIndex:'pr02', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "免费产品", width: 75, dataIndex:'pr05', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},{header: "客服支持", width: 90, dataIndex:'zr06', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "产品折扣", width: 75, dataIndex:'pr03', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "活动折扣", width: 75, dataIndex:'pr04', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "运输费(含税)", width: 90, dataIndex:'zr01', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "返修费(含税)", width: 90, dataIndex:'zr02', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "安装服务费(含税)", width: 110, dataIndex:'zr03', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "设计费(含税)", width: 90, dataIndex:'zr04', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},
								 		                {header: "订单变更管理费(含税)", width: 140, dataIndex:'zr05', sortable: true,
								 		           	    	summaryType: 'sum',align:'right',
								 		                    summaryRenderer: function(value, summaryData, dataIndex) {
								 		                      return value.toFixed(2);
								 		                    }},{header: "",flex:1,menuDisabled:true,sortable:false}
								 		            ]
								                 }]
						                	}
						    	           ]
								},
								{	title: '客户信息',
					            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;"	,
						    		border : false,
						    		autoScroll:true,
						    		bodyPadding: 5,
						    		height: 240,
						    		margin:'0 40 0 0',
									maxWidth:1000,
						    		margin:'0 25 0 0',
						    		layout: {
								        type: 'table',
								        columns: 3
								    },
						    		items:[
									{
					                    xtype:'textfield',
					                    fieldLabel: '姓名<font color=red>*</font>',
										allowBlank: false,
					                    name: 'name1',
					                    width: 300
					                },{
					                    xtype:'textfield',
					                    fieldLabel: '联系电话<font color=red>*</font>',
					                    allowBlank: false,
					                    name: 'tel',
					                    //vtype:'phonecheck',
					                    width: 300
									},{
										xtype:'textfield',
					                    fieldLabel: '投诉次数',
					                    allowBlank: true,
					                    name: 'tousucishu',
					                    hidden:true,
					                    //vtype:'phonecheck',
					                    width: 300
									},{
										xtype:'textfield',
					                    fieldLabel: '问题情况',
					                    allowBlank: true,
					                    name: 'problem',
					                    hidden:true,
					                    //vtype:'phonecheck',
					                    width: 300
									},
					                {	allowBlank: false,
							        	xtype:'dictcombobox',
										fieldLabel : '性别<font color=red>*</font>',
										emptyText: '请选择...',
										width: 300,
										name:'sex',
										dict:'SEX'
							        },
									{	allowBlank: false,
								        xtype: 'numberfield',
								        anchor: '100%',
								        name: 'age',
								        fieldLabel: '年龄<font color=red>*</font>',
								        width: 300,
								        minValue: 0, //prevents negative numbers
								        maxValue: 180,
								
								        // Remove spinner buttons, and arrow key and mouse wheel listeners
								        hideTrigger: true,
								        keyNavEnabled: false,
								        mouseWheelEnabled: false
								    },{ allowBlank: false,
					                    xtype:'datefield',
					                    fieldLabel: '生日<font color=red>*</font>',
					                    name: 'birthday',
					                    format :'Y-m-d',
					                    width: 300
									},{
					                    xtype:'textfield',
					                    fieldLabel: '身份证号码',
					                    name: 'shenFenHao',
					                    width: 300
					                },/*{
					                    xtype:'textfield',
					                    fieldLabel: '专卖店编码',
					                    name: 'code',
					                    width: 300
					                },{
					                    xtype:'textfield',
					                    fieldLabel: '专卖店名称',
					                    name: 'name',
					                    width: 300
									},*/{allowBlank: false,
					                    xtype:'textfield',
					                    fieldLabel: '专卖店经手人<font color=red>*</font>',
					                    name: 'jingShouRen',
					                    width: 300
					                },{ allowBlank: false,
							        	xtype:'dictcombobox',
										fieldLabel : '安装户型<font color=red>*</font>',
										emptyText: '请选择...',
										width: 300,
										name:'huXing',
										dict:'HU_XING'
							        }/*,{
							        	xtype:'dictcombobox',
										fieldLabel : '是否为样板',
										emptyText: '请选择...',
										width: 300,
										name:'isYangBan',
										dict:'YES_NO'
							        }*/
					                ,{
							        	xtype:'dictcombobox',
										fieldLabel : '是否有电梯',
										emptyText: '请选择...',
										width: 300,
										name:'isDianTi',
										dict:'YES_NO'
							        },{
							        	xtype:'dictcombobox',
										fieldLabel : '是否劳卡安装',
										emptyText: '请选择...',
										width: 300,
										name:'isAnZhuang',
										dict:'YES_NO'
							        },
						            {
					                    xtype:'textfield',
					                    fieldLabel: '楼层',
					                    name: 'floor',
					                    width: 300
									},
							        {   allowBlank: false,
							        	xtype:'dictcombobox',
										fieldLabel : '订单金额范围<font color=red>*</font>',
										emptyText: '请选择...',
										width: 300,
										name:'orderPayFw',
										dict:'ORDER_PAY_FW',
										colspan: 2
							        },/*{ allowBlank: false,
							        	xtype:'dictcombobox',
										fieldLabel : '活动政策<font color=red>*</font>',
										emptyText: '请选择...',
										showDisabled : false,
										width: 300,
										name:'orderEvent',
										dict:'ORDER_EVENT',
										hidden:true,
										colspan: 2,
										cols:100,
							            rows:1
							        },*/{
							            xtype: 'textareafield',
							            name: 'address',
							            fieldLabel: '安装地址',
							            colspan: 3,
							            //value: 'Textarea value',
							            //fieldStyle : 'background-color: #D0DEF0;',
							            cols:100,
							            rows:1
									},{
							            xtype: 'textareafield',
							            name: 'custRemarks',
							            fieldLabel: '备注',
							            colspan: 3,
							            //value: 'Textarea value',
							            //fieldStyle : 'background-color: #D0DEF0;',
							            cols:100,
							            rows:2
									}]
								},
//								{	title: '审核记录',
//									height: 300,
//									border : false,
//									autoScroll:true,
//						    		items:[
//									{
//										
//									}]
//								},
								{	title: '汇款凭证',
									height: 240,
									border : false,
									autoScroll:true,
									hidden:true,
						    		items:[
										/*文件上传 strat*/
										Ext.widget('grid',{
							            	//title: '汇款凭证',
							            	itemId:'sysFileGrid',
							            	border:false,
						            	    tbar: [
						            	           {xtype: 'button', text: '文件上传',iconCls:'table_add',hidden:true,id:'saleUploadFile',
						            	        	    handler : function() {
						            	        		   this.up('window').fireEvent('fileUploadButtonClick','',form.getForm().findField("id").getValue());
						   								}
						            	           }
							            	      ,{xtype: 'button', text: '文件删除',iconCls:'table_remove',hidden:true,id:'saleRemoveFile',
							            	    	   handler : function() {
							            	    		   this.up('window').fireEvent('fileDeleteButtonClick','',this.up('grid'));
						  								}  
							            	      	}
						            	    	  ],
										    store : Ext.create("SMSWeb.store.sys.SysFileStore"),
										    selModel:{selType:'checkboxmodel',injectCheckbox:0},
										    columns : [
											             {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
										               	 {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
											             {text:'文件名称',dataIndex:'uploadFileNameOld',width:200,menuDisabled:true},
											             {text:'备注',dataIndex:'remark',width:200,menuDisabled:true},
											             {text:'上传人',dataIndex:'createUser',width:100,menuDisabled:true},
											             {text:'上传日期',dataIndex:'createTime',width:100,menuDisabled:true},
											             {text:'文件下载',width:100,itemId:'fileDownloadItemIdItemId',xtype:'actioncolumn',menuDisabled:true,align:'center',icon:'/resources/images/down.png'
											            	    ,handler : function(grid,rowIndex,colIndex) {
																	this.up('window').fireEvent('fileDownloadButtonClick',this.up('grid'),rowIndex,colIndex);
																}
											             }
										               ]
							            })
										/*文件上传 end*/
									]
								},
								{	title: '临时送/售达方',
									height: 240,
									border : false,
									autoScroll:true,
									hidden:true,
						    		items:[
										/* strat*/
										Ext.widget('grid',{
							            	itemId:'saleOneCustGrid',
							            	border:false,
										    store : Ext.create("SMSWeb.store.sale.SaleOneCustStore"),
										    viewConfig:{
											    enableTextSelection:true //可以复制单元格文字
											},
										    //selModel:{selType:'checkboxmodel',injectCheckbox:0},
										    columns : [
											             {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
											             {text:'创建时间',dataIndex:'createTime',width:100,xtype: 'datecolumn',format :'Y-m-d H:i:s',hidden:true,menuDisabled:true},
											             {text:'创建者',dataIndex:'createUser',width:100,hidden:true,menuDisabled:true},
											             {text:'类型',dataIndex:'saleOneCustType',width:100,menuDisabled:true},
														 {text:'客户',dataIndex:'kunnr',width:100,menuDisabled:true},
														 {text:'称谓',dataIndex:'anred',width:100,menuDisabled:true,editor: {
												                allowBlank: false
												         }},
														 {text:'名称',dataIndex:'saleOneCustName1',width:100,menuDisabled:true,editor: {
												                allowBlank: false
												         }},
												         //{text:'街道',dataIndex:'street',width:150,menuDisabled:true,editor: {
												             //   allowBlank: false
												         //}},
												         {text:'收货地址',dataIndex:'socAddress',width:150,menuDisabled:true,editor: {
												                allowBlank: false
												         }},
												         {text:'邮编',dataIndex:'pstlz',width:100,menuDisabled:true,editor: {
//												                allowBlank: false
												         }},
												         {text:'城市',dataIndex:'mcod3',width:100,menuDisabled:true,editor: {
												                allowBlank: false
												         }},
												         /*{text:'国家',dataIndex:'land1',width:100,editor: {
												                allowBlank: false
												         }},*/
												         {text:'地区',dataIndex:'regio',width:100,menuDisabled:true,editor: kunnrRegionCombobox,
												        	 renderer: function(value,metadata,record){  
												 				var kunnrRegionStore = kunnrRegionCombobox.getStore();
												 				var find = kunnrRegionStore.findRecord('id',value); 
												 				if(find){
										 	            			return find.get('text');
										 	            		}
												                return value;  
												         	 }
												         },
												         {text:'移动电话',dataIndex:'telf1',width:100,menuDisabled:true,editor: {
												                allowBlank: false
												         }},
												         {text:'运输区域',dataIndex:'ort02',width:100,menuDisabled:true,editor: kunnrYsRegionCombobox,
												        	 renderer: function(value,metadata,record){  
												 				var kunnrYsRegionStore = kunnrYsRegionCombobox.getStore();
												 				var find = kunnrYsRegionStore.findRecord('id',value); 
												 				if(find){
										 	            			return find.get('text');
										 	            		}
												                return value;  
												         	 }
												         }
										               ],
								                selType : 'cellmodel',
												plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
														{
															enableKeyNav : true,
															clicksToEdit : 2
												})]
							            })
										/* end*/
									]
								},
								{	title: '物流信息',
									height: 240,
									border : false,
									autoScroll:true,
						    		items:[
										/* strat*/
										Ext.widget('grid',{
							            	itemId:'saleLogisticsGridItemId',
							            	store:Ext.create("SMSWeb.store.sale.SaleLogisticsStore"),
							            	columnLines : true,
											border : false,
											autoScroll:true,
										    viewConfig:{
											    enableTextSelection:true
											},
										    columns : [
									    			 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									    			 {text:'pid',dataIndex:'pid',width:0,hidden:true,menuDisabled:true},
										             {text:'产品组',dataIndex:'saleFor',width:100,menuDisabled:true,
									    				 editor:{
									    					 xtype:'dictcombobox',
									    					 dict:'SALE_FOR',
									    					 allowBlank : false
									    				 },
									    				 renderer: function(value,metadata,record){
													 	    return saleFor.getStore().getById(value).get('text');
									    				 }
									    			 },
										             {text:'生产预完工日期',dataIndex:'ppcDate',xtype:'datecolumn',width:180,menuDisabled:true,format :'Y-m-d'},
										             {text:'预计出货日期',dataIndex:'psDate',xtype:'datecolumn',width:180,menuDisabled:true,format :'Y-m-d'},
													 {text:'计划完工日期',dataIndex:'pcDate',xtype:'datecolumn',width:180,menuDisabled:true,format :'Y-m-d'},
													 {text:'实际入库日期',dataIndex:'pbDate',xtype:'datecolumn',width:180,menuDisabled:true,format :'Y-m-d'},
													 {text:'实际出库日期',dataIndex:'poDate',xtype:'datecolumn',width:180,menuDisabled:true,format :'Y-m-d'},
											         {text:'交期天数',dataIndex:'deliveryDay',width:130,menuDisabled:true,
														 editor:{
															 xtype:"dictcombobox",
															 dict:"JIAO_QI_TIAN_SHU",
															 judge:_judge,
															 allowBlank : false
														 },renderer: function(value,metadata,record){
														 	    //return jiaoQi.getStore().getById(value).get('text');
																 return value+"个工作日";
										    			}},
											         {text:'送达方',dataIndex:'kunnrS',width:120,menuDisabled:true,
										    				renderer : function(value,metadata,record) { 
													    	return "<a href=\"javascript:showCustWin('" + value + "')\">" + value + "</a>"; 
													    }}
										               ],
								                selType : 'cellmodel',
												plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
														{
															enableKeyNav : true,
															clicksToEdit : 2
												})],
												listeners: {
											        edit:function(editor, e, eOpts){
											        	var id= e.record.data.id;
											        	if(id != undefined && id != ""){
											        		this.updateGridData(e.field, e.value, id)
											        	}
													}
											    },
											    updateGridData:function(fieldName,value,id){
													if(id!=""){
														Ext.Ajax.request({
															url:"sale/log/update",
															method:"POST",
															params:{
																	id:id,
																	name:fieldName,
																	val:value
															},
															dataType:"JSON",
															contentType : 'application/json',
															success:function(response,opts){
																form.queryById("saleLogisticsGridItemId").getStore().reload();
															},
															failure:function(response,opts){
																Ext.Msg.alert("can't",'error');
															}
														});
													}
												}
							            })
										/* end*/
									]
								},{
									title: '客诉信息',
									height: 240,
									border : false,
									autoScroll:true,
						    		items:[
										/* strat*/
										Ext.widget('grid',{
							            	itemId:'complainid_ItemIds',
							            	store:Ext.create("SMSWeb.store.sale.SaleComplaintStore"),
							            	columnLines : true,
							            	bodyStyle : "padding-top:5px;padding-bottom:5px;padding-left:5px;padding-right:5px;",
								    		border : false,
								    		autoScroll:true,
								    		bodyPadding: 5,
								    		height: 240,
										    viewConfig:{
											    enableTextSelection:true
											},
										    columns : [
									    	   	  {text:'id',dataIndex:'id',width:0,hidden:true},
								                  {text : '序号',width:50,labelAlign:'left',xtype: 'rownumberer',menuDisabled:true},
								                  {text:'行号',dataIndex:'orderCodePosex',width:150,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
								                  {text:'投诉内容',dataIndex:'zztsnr',width:300,sortable: false,menuDisabled:true,editor:{xtype:'textareafield',selectOnFocus:true}},
								                  {text:'产品名称',dataIndex:'cpmc',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
								                  {text:'颜色',dataIndex:'color',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
								                  {text:'柜名',dataIndex:'cabinetName',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
								                  {text:'产品组',dataIndex:'salefor',width:100,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
								                  {text:'出错问题',dataIndex:'zzelb',width:100,sortable: false,menuDisabled:true, align:'left',editor:{xtype:'textfield',selectOnFocus:true},
							                	  editor:{
														 xtype:"dictcombobox",
														 dict:"CCWT",
														 allowBlank : true
														 },	
								                	  renderer: function(value,metadata,record){
											 				var Store = ccwtCombobox.getStore();
											 				var find = Store.findRecord('id',value,0,false,true,true); 
											 				if(find){
									 	            			return find.get('text');
									 	            		}
											                return value;  
											           }
								                  },
								                  {text:'出错中心',dataIndex:'zzezx',width:100,sortable: false,menuDisabled:true, align:'left',editor:{xtype:'textfield',selectOnFocus:true},
								                	  editor:{
															 xtype:"dictcombobox",
															 dict:"CCZX",
															 allowBlank : true
															 },	
									                	  renderer: function(value,metadata,record){
												 				var Store = cczxCombobox.getStore();
												 				var find = Store.findRecord('id',value,0,false,true,true); 
												 				if(find){
										 	            			return find.get('text');
										 	            		}
												                return value;  
												           }
									                  },
									               {text:'出错部门',dataIndex:'zzebm',width:100,sortable: false,menuDisabled:true, align:'left',editor:{xtype:'textfield',selectOnFocus:true},
								                	  editor:{
															 xtype:"dictcombobox",
															 dict:"CCBM",
															 allowBlank : true
															 },	
									                	  renderer: function(value,metadata,record){
												 				var Store = ccbmCombobox.getStore();
												 				var find = Store.findRecord('id',value,0,false,true,true); 
												 				if(find){
										 	            			return find.get('text');
										 	            		}
												                return value;  
												           }
										              },
										              {text:'出错组',dataIndex:'zzccz',width:180,sortable: false,menuDisabled:true, align:'left',editor:{xtype:'textfield',selectOnFocus:true},
														 editor:{
															 xtype:"dictcombobox",
															 dict:"CCZB",
															 allowBlank : true
															 },	
									                	  renderer: function(value,metadata,record){
												 				var Store = cczbCombobox.getStore();
												 				var find = Store.findRecord('id',value,0,false,true,true); 
												 				if(find){
										 	            			return find.get('text');
										 	            		}
												                return value;  
												           }
											           },
											           {text:'出错类型',dataIndex:'zzcclx',width:100,sortable: false,menuDisabled:true, align:'left',editor:{xtype:'textfield',selectOnFocus:true},
												        	  editor:{
																	 xtype:"dictcombobox",
																	 dict:"WTLX",
																	 allowBlank : true
																	}
													          },
								                  {text:'责任人',dataIndex:'duty',width:80,sortable: false,menuDisabled:true, align:'right',editor:{xtype:'textfield',selectOnFocus:true}},
								                  ]
							            })
										/* end*/
									]
								}
				            ]}
						]
				});
				
				/*itemGrid订单子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						region: 'center', 
						alias : 'widget.SaleGridView',
						store : Ext.create("SMSWeb.store.sale.SaleStore"),
						itemId:'saleGrid',
						id:'newSaleItemGridId',
						enableKeyNav : true,
						columnLines : true,
						border : false,
						autoScroll:true,
						minHeight:400, 
						frame:false,
						border:0,
						title: '产品明细',
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						tbar : [{
								xtype : 'button',
					            text: '增加产品',
					            iconCls: 'table_add',
					            id : 'addOrderItem',
					            hidden:true
					        },{
					        	xtype : 'button',
					            text: '标准产品',
					            iconCls: 'table_add',
					            id : 'addOrderItemBZ',
					            hidden:true
					        },{
					        	xtype : 'button',
					            text: '非标产品',
					            iconCls: 'table_add',
					            id : 'addOrderItemFB',
					            hidden:true
					        }, {
					        	xtype : 'button',
					            text: '移门散件',
					            iconCls: 'table_add',
					            id : 'addOrderItemYMSJ',
					            hidden:true
					        },
					        {
					        	xtype : 'button',
					            text: '五金散件',
					            iconCls: 'table_add',
					            id : 'addOrderItemWJSJ',
					            hidden:true
					        },{
					        	xtype : 'button',
					            text: '销售道具',
					            iconCls: 'table_add',
					            id : 'addOrderItemXSDJ',
					            hidden:true
					        },
					        {
					        	xtype : 'button',
					            id: 'removeOrderItem',
					            text: '删除产品',
					            iconCls: 'table_remove',
//					            disabled: true,
//					            hidden:true
					        },{
					        	//add by hzm 2016.12.12 start
					        	xtype : 'button',
					            id: 'batchPdfDownload',
					            text: '批量下载PDF文件',
					            iconCls: 'table_add',
					            hidden:true//目前没有使用
					        },{
					        	//add by hzm 2016.12.12 start
					        	xtype : 'button',
					            id: 'batchjgdDownload',
					            text: '批量下载报价清单',
					            iconCls: 'table_add',
					            hidden:true
					        }],//add by hzm 2016.12.12 end
					     tools : [{
							type : 'refresh',
							margin:'0 40 0 0',
							tooltip : 'Refresh form Data',
							handler : function(event, toolEl,panel) {
									panel.ownerCt.getStore().reload();
									Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
							}
						 }],
//						region:'center',
					//	selModel:{selType:'checkboxmodel',injectCheckbox:0},
					//	dockedItems:[{
					//		xtype:'pagingtoolbar',
					//		store:'cust.Cust2Strore',
					//		dock:'bottom',
					//		border:false,
					//		displayInfo:true
					//		}],
						columns : [
									 {text:'操作',locked:true,xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
											icon:'/resources/images/remarks1.png'
											,tooltip:'编辑'
											,handler:function(grid,rowIndex,colIndex){
												this.up('window').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
											}
									 }]},
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'行号',locked:true,dataIndex:'posex',width:50,menuDisabled:true},
									 {text:'SAP编号',locked:true,dataIndex:'sapCode',width:98,menuDisabled:true},
									 {text:'SAP行项编号',locked:true,dataIndex:'sapCodePosex',width:98,menuDisabled:true},
									 {text:'产品编号',locked:true,dataIndex:'matnr',width:98,menuDisabled:true},
									 {text:'类型',dataIndex:'mtart',width:70,menuDisabled:true,
										 renderer: function(value,metadata,record){  
								 				var materialMtartStore = materialMtartCombobox.getStore();
								 				var find = materialMtartStore.findRecord('id',value); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
									 },
									 {text:'绘图类型',dataIndex:'drawType',width:75,menuDisabled:true,
										 renderer: function(value,metadata,record){  
								 				var drawTypeStore = drawTypeCombobox.getStore();
								 				var find = drawTypeStore.findRecord('id',value,0,false,true,true); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
									 },
									 {text:'PDF文件',dataIndex:'pdfId',align:'center',width:70,  menuDisabled:true ,
							        	 renderer: function(value,metadata,record){  
								 			 	if(value){
								 			 		var url=basePath+"main/sysFile/fileDownload?id="+value;
								 			 		var str='<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\''+value+'\')">下载</a>';
								 			 		return str;
								 			 	}
								                return value;  
								         }	
							         }, 
							         {text:'报价清单',dataIndex:'priceId',align:'center',width:70, menuDisabled:true ,
							        	 renderer: function(value,metadata,record){  
								 			 	if(value && IS_MONEY == "true"){
								 			 		var url=basePath+"main/sysFile/fileDownload?id="+value;
								 			 		var str='<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\''+value+'\')">下载</a>';
								 			 		return str;
								 			 	}
								                return "";  
								         }
							         }, 
									 {text:'描述',dataIndex:'maktx',width:320,menuDisabled:true,editor: {
							                allowBlank: false
							         }},
						             {text:'总价',dataIndex:'totalPrice',width:85, align:'right',xtype: 'numbercolumn',menuDisabled:true,hidden:true},
//									 {text:'单价',dataIndex:'unitPrice',width:100, xtype: 'numbercolumn',editor: {
//							                xtype: 'numberfield',
//							                allowBlank: false,
//							                minValue: 0,
//							                maxValue: 150000
//							         }},
//						             {text:'折扣',dataIndex:'zheKou',width:100, xtype: 'numbercolumn',editor: {
//							                xtype: 'numberfield',
//							                allowBlank: false,
//							                minValue: 0,
//							                maxValue: 150000
//							         }},
//							         {text:'折扣价',dataIndex:'zheKouJia',width:100, xtype: 'numbercolumn',editor: {
//							                xtype: 'numberfield',
//							                allowBlank: false,
//							                minValue: 0,
//							                maxValue: 150000
//							         }},
							         {text:'数量',dataIndex:'amount',width:45,menuDisabled:true, xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                allowDecimals:false,//不允许输入小数
							                //allowNegative:false,//不允许输入负数
							                minValue: 1,
							                disabled:me.orderType == "OR2" || me.orderType == "OR03" || me.orderType == "OR4" || me.orderType == "OR3" || me.orderType == "OR4"
							                //,maxValue: 150000
							         },renderer: function(value){
									        return parseInt(value);
									 }},
									 {text : '单位',dataIndex : 'unit',width : 50,align : 'center',editor:unit,
							 	             renderer : function(value, meta, record) {
							            		var find= unit.getStore().findRecord("id",value);
							            		if(find){
							            			return find.get('text');
							            		}else{
							            			return value;
							            		}
								            }
										},
										{text : '产品组',dataIndex : 'saleFor',width : 50,align : 'center',editor:saleFor,
							 	             renderer : function(value, meta, record) {
							            		var find= saleFor.getStore().findRecord("id",value);
							            		if(find){
							            			return find.get('text');
							            		}else{
							            			return value;
							            		}
								            }
										},
//							         {text:'投影面积',dataIndex:'touYingArea',width:100,menuDisabled:true,align:'right'},
							         {text:'安装位置',dataIndex:'remark',width:200,align:'center',menuDisabled:true},
							         {text:'审核状态',dataIndex:'stateAudit',width:75,menuDisabled:true,align:'center',
							        	 renderer: function(value,metadata,record){  
								 				var stateAuditStore = stateAuditCombobox.getStore();
								 				var find = stateAuditStore.findRecord('id',value); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
							         },
//						             {text:'订单状态',dataIndex:'status',width:100,menuDisabled:true},
							         {text:'是否标准产品',dataIndex:'isStandard',width:95,menuDisabled:true,align:'center',
							        	 renderer: function(value,metadata,record){  
								 				var isStandardStore = isStandardCombobox.getStore();
								 				var find = isStandardStore.findRecord('id',value); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
							         },
							         {text:'是否外购',dataIndex:'zzwgfg',width:75,menuDisabled:true,align:'center',
							        	 renderer: function(value,metadata,record){  
								 				var isStandardStore = zzwgfgCombobox.getStore();
								 				var find = isStandardStore.findRecord('id',value); 
								 				if(find){
						 	            			return find.get('text');
						 	            		}
								                return value;  
								           }
							         },
							         {text:'物料id',dataIndex:'materialHeadId',width:0,hidden:true,menuDisabled:true},
							         {text:'我的物品id',dataIndex:'myGoodsId',width:0,hidden:true,menuDisabled:true},
							         {text:'费用化id',dataIndex:'expenditureHeadId',width:0,hidden:true,menuDisabled:true},
							         {text:'当前环节',dataIndex:'jdName',width:75,align:'center',menuDisabled:true},
							        
							         {text:'KIT文件',dataIndex:'kitId',align:'left',width:75,menuDisabled:true,hidden:true,
							        	 renderer: function(value,metadata,record){  
								 			 	if(value){
								 			 		var url=basePath+"main/sysFile/fileDownload?id="+value;
								 			 		var str='<a href="javascript:void(0)" onClick="fileDownloadButtonClick(\''+value+'\')">下载</a>';
								 			 		return str;
								 			 	} 
								 			 	return "";
								         }
							         }

						           ],
					//	selType: 'rowmodel',
					//	plugins: [rowEditing]
						selType : 'cellmodel',
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
								{
									enableKeyNav : true,
									clicksToEdit : 2
						})],
						listeners: {
					            'selectionchange': function(view, records) {
								
									//有ID的行号不允许删除
												
											if(records[0] &&!records[0]["data"]["id"]){
											Ext.getCmp("removeOrderItems")
													.setDisabled(
															!records.length);
											}
					            },
					            'edit': function(editor, e) {
									if(e.field=='amount')
									{
										var shouDaFang=form.getForm().findField("shouDaFang").getValue();
										Ext.Ajax.request({
											url : 'main/sale/calculationPrice?shouDaFang='+shouDaFang,
											method : 'POST',
											params : e.record.getData(),
											async:false,
											success : function(response, opts) {
												values = Ext.decode(response.responseText);
												e.store.getAt(e.rowIdx).set('totalPrice',values.data.totalPrice);
												var itemStore = itemGrid.getStore();
												var itemCount = itemStore.getCount();
												var allTotalPrice = 0;
												for (var i = 0; i <itemCount; i++) {
													var record = itemStore.getAt(i);
													if("QX" != record.get("stateAudit")){
														allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
													}
												}
												form.getForm().findField("orderTotal").setValue(allTotalPrice);
												form.getForm().findField("orderTotal").initValue();
											},
											failure : function(response, opts) {
												alert(response.responseText);
											}
										});
									}
					            }
					    }
				});
				/*itemGrid订单子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				//获取tabs
				var tabs = form.queryById("centerTabpanel");
				
				var orderTypeStore = Ext.data.StoreManager.lookup('orderType_ORDER_TYPE');
				
				
				var jiaoQiTianShuStore = Ext.data.StoreManager.lookup('jiaoQiTianShu_JIAO_QI_TIAN_SHU');
				
				
				/*************************/
				/****OR3,OR4订单类型******/
				/*************************/
				if("buDan"==me.orderType || "OR3"==me.orderType || "OR4"==me.orderType){
					orderTypeStore.filter([
					    {filterFn: function(item) { 
					    		if(item.get("id")==null || item.get("id")=="" || item.get("id")=="OR3" || item.get("id")=="OR4"){
					    			return true;
					    		}
					    	}
					    }
					]);
/*					jiaoQiTianShuStore.filter([
					    {filterFn: function(item) { 
					    		if(item.get("id")==null || item.get("id")=="" || item.get("id")=="2" || item.get("id")=="4" || item.get("id")=="14"){
					    			return false;
					    		}
					    	}
					    }
					]);*/
					jiaoQiTianShuStore.filter([
					    {filterFn: function(item) { 
					    		if("2"!=item.get("id") && "4"!=item.get("id") && "14"!=item.get("id")){
					    			return true;
					    		}
					    	}
					    }
					]);
					form.getForm().findField("shouDaFang").setValue("");
					form.getForm().findField("shouDaFang").initValue();
					
					form.getForm().findField("songDaFang").setValue("");
					form.getForm().findField("songDaFang").initValue();
					
					form.getForm().findField("orderDate").setValue("");
					form.getForm().findField("orderDate").initValue();
					
					form.getForm().findField("pOrderCode").show();
					form.getForm().findField("pOrderCode").setFieldLabel("参考订单");
//					form.queryById("pOrderCodeButton").show();
					
					form.queryById("shouDaFang_queryCust").show();
					form.queryById("fujianDownloadButtonClick").show();
					
                    form.getForm().findField("dianMianTel").setFieldLabel("店面联系电话");
                    form.getForm().findField("dianMianTel").allowBlank = true;
                    
                    form.getForm().findField("name1").setFieldLabel("姓名");
                    form.getForm().findField("name1").allowBlank = true;
                    
                    form.getForm().findField("tel").setFieldLabel("联系电话");
                    form.getForm().findField("tel").allowBlank = true;
                    
                    form.getForm().findField("sex").setFieldLabel("性别");
                    form.getForm().findField("sex").allowBlank = true;
                    
                    form.getForm().findField("age").setFieldLabel("年龄");
                    form.getForm().findField("age").allowBlank = true;
                    
                    form.getForm().findField("birthday").setFieldLabel("生日");
                    form.getForm().findField("birthday").allowBlank = true;
                    
                    form.getForm().findField("jingShouRen").setFieldLabel("专卖店经手人");
                    form.getForm().findField("jingShouRen").allowBlank = true;
                    
                    form.getForm().findField("huXing").setFieldLabel("安装户型");
                    form.getForm().findField("huXing").allowBlank = true;
                    
                    form.getForm().findField("orderPayFw").setFieldLabel("订单金额范围");
                    form.getForm().findField("orderPayFw").allowBlank = true;
					
					form.getForm().findField("fuFuanCond").setValue("2");//付款条件
					form.getForm().findField("fuFuanCond").initValue();
					
//					var terminalClientForm = {dianMianTel:'',designerTel:'',name1:'',tel:'',sex:'',age:'',birthday:'',shenFenHao:'',code:'',name:'',jingShouRen:'',
//						huXing:'',isYangBan:'',isAnZhuang:'',isDianTi:'',floor:'',address:'',orderPayFw:'',custRemarks:''};
//					Ext.Object.each(terminalClientForm, function(key, value, myself) {
//						if(form.getForm().findField(key) != null){
//							form.getForm().findField(key).setReadOnly(true);
//						}
//					});
					
//					var saleOneCustGrid = form.queryById("saleOneCustGrid");
//					Ext.Array.each(saleOneCustGrid.columns, function(column) {
//						if("undefined"!=typeof(column.editor)){
//							column.editor = null;
//						}
//					});
					
					//加载数据
					if(me.formId!=null){
						form.load({
							url:'main/sale/findById',
							params : {
								id : me.formId
							},
							method : 'GET',
							async:false,
							success:function(f,action){
								var result = Ext.decode(action.response.responseText);
								if(("undefined"!=typeof(result.data.orderCode) && "" != result.data.orderCode)
										&& ("undefined"==typeof(result.data.pOrderCode) || null == result.data.pOrderCode || "" == result.data.pOrderCode)
										&& "OR4" == result.data.orderType){
									form.queryById("orderCodeQueryButton").show();
								}
								form.getForm().findField("shouDaFang").setValue(result.data.shouDaFang);
								form.getForm().findField("shouDaFang").initValue();
								me.kunnr = result.data.shouDaFang
								
								form.getForm().findField("jiaoQiTianShu").setValue(result.data.jiaoQiTianShu);
								form.getForm().findField("jiaoQiTianShu").initValue();
								form.getForm().findField("songDaFang").setValue(result.data.songDaFang);
								form.getForm().findField("songDaFang").initValue();
								form.getForm().findField("kunnrName1").setValue(result.data.kunnrName1);
								form.getForm().findField("kunnrName1").initValue();
								me.pOrderCode = result.data.pOrderCode;
								var saleId = form.getForm().findField("id").getValue();
								itemGrid.getStore().load({params:{'pid':saleId},callback: function(records, operation, success) {
									reCalculate(form,itemGrid);
								}});
								var saleOneCustGrid = form.queryById("saleOneCustGrid");
								saleOneCustGrid.getStore().load({params:{'pid':saleId},callback: function(records, operation, success) {
									var saleOneCustCount = saleOneCustGrid.getStore().getCount();
			                        if(saleOneCustCount > 0){
			                        	//临时送/售达方显示
										tabs.items.getAt(4).tab.show();
			                        }else{
			                        	//临时送/售达方隐藏
										tabs.items.getAt(4).tab.hide();
			                        }
								}});
								var sysFileGrid = form.queryById("sysFileGrid");
								var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
//								saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
		                        sysFileGrid.getStore().load({params:{'foreignId':saleId,'fileType':''}});
		                        var complainidicsGrid=form.queryById("complainid_ItemIds");
		                		complainidicsGrid.getStore().load({params:{pid:me.formId}});
		                        if(me.editFlag){//me.editFlag==true能编辑
		                        	var sapOrderCode = "";//form.getForm().findField("sapOrderCode").getValue();
		                			var obj = Ext.ux.DataFactory.getFlowActivityId(me.formId);
//					                alert(obj.docStatus);
//					                alert(obj.taskdefId);
//					                alert(obj.taskName);
//					                alert(obj.taskGroup);
//					                alert(obj.assignee);
					                if(obj != null){
					                	var docStatus = obj.docStatus;
					                	if(docStatus=="0"){//未提交流程
					                		var createUser = form.getForm().findField("createUser").getValue();
					                		if(createUser!=CURR_USER_LOGIN_NO){//未提交前，登陆账号不等于订单创建人，只能查看
					                			var formValues = form.getValues();
												Ext.Object.each(formValues, function(key, value, myself) {
													form.getForm().findField(key).setReadOnly(true);
												});
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												var saleOneCustGrid = form.queryById("saleOneCustGrid");
												Ext.Array.each(saleOneCustGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												Ext.getCmp("saveOrder").hide();
												Ext.getCmp("tranSap").hide();
												Ext.getCmp("checkSale").hide();
												Ext.getCmp("addOrderItem").hide();
												Ext.getCmp("removeOrderItem").hide();
												Ext.getCmp("saleUploadFile").hide();
												Ext.getCmp("saleRemoveFile").hide();
												form.queryById("songDaFang_queryCust").hide();
//												form.queryById("pOrderCodeButton").hide();
												form.queryById("shouDaFang_queryCust").hide();
//												form.queryById("fujianDownloadButtonClick").hide();
												form.queryById("orderCodeQueryButton").hide();
					                		}
					                	}else if(docStatus=="1"){//已提交流程
					                		var taskdefId = obj.taskdefId;
					                    	var name = obj.taskName;
					                    	var taskGroup = obj.taskGroup;
					                    	var assignee = obj.assignee;
					                    	if(!assignee){//没有流程编辑权限，只能查看订单，并查看流程
					                    		var formValues = form.getValues();
												Ext.Object.each(formValues, function(key, value, myself) {
													form.getForm().findField(key).setReadOnly(true);
												});
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												var saleOneCustGrid = form.queryById("saleOneCustGrid");
												Ext.Array.each(saleOneCustGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});										
												Ext.getCmp("saveOrder").hide();
												Ext.getCmp("tranSap").hide();
												Ext.getCmp("checkSale").show();
												Ext.getCmp("addOrderItem").hide();
												Ext.getCmp("removeOrderItem").hide();
												Ext.getCmp("saleUploadFile").hide();
												Ext.getCmp("saleRemoveFile").hide();
												form.queryById("songDaFang_queryCust").hide();
//												form.queryById("pOrderCodeButton").hide();
												form.queryById("shouDaFang_queryCust").hide();
//												form.queryById("fujianDownloadButtonClick").hide();
												form.queryById("orderCodeQueryButton").hide();
												if("undefined"!=typeof(taskdefId) && taskdefId=="usertask1"){
					                				Ext.getCmp("checkSale").hide();//如果是客服起草环节，隐藏防止提交到下一环节（价格审核环节）
					                			}
												if(taskdefId =="usertask_drawing"||taskdefId =="gp_bg_material"){
													var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
								                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
												}
												//usertask_drawing
												//价格审核  或  确认报价  或  财务确认
												if(taskdefId=="usertask2" || taskdefId=="usertask3" || taskdefId=="usertask4" || taskdefId=="usertask_finance"|| taskdefId=="usertask_store_confirm"){//usertask_store_confirm
													var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
								                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
													//金额查看权限
													if(IS_MONEY=="true"){
														//订单金额信息显示
														Ext.getCmp("batchjgdDownload").show();
														tabs.items.getAt(1).tab.show();
														Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
														//行项目价格显示
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																column.show();
															}
														});
													}
												}
					                    	}else{//有流程编辑权限
					                    		if("undefined"!=typeof(taskdefId)){
					                    			if(taskdefId=="usertask1"){//客服起草时
					                    				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                    			}else if(taskdefId=="usertask_drawing"){//订单审绘
					                    				var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
								                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
					                    				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                    				Ext.getCmp("addOrderItemBZ").show();
														Ext.getCmp("addOrderItemXSDJ").show();
														Ext.getCmp("addOrderItemYMSJ").show();
														Ext.getCmp("addOrderItemWJSJ").show();
														Ext.getCmp("addOrderItemFB").show();
														form.getForm().findField("jiaoQiTianShu").setReadOnly(false);
														form.getForm().findField("orderType").setReadOnly(true);
														form.getForm().findField("isYp").setReadOnly(true);
					                    			}else if(name=="物料审核"){
					                    				Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
					                    			}else if(taskdefId=="usertask2"||"gp_valuation"==taskGroup){//价格审核
					                    				Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
//														form.queryById("pOrderCodeButton").hide();
														form.queryById("shouDaFang_queryCust").hide();
//														form.queryById("fujianDownloadButtonClick").hide();
														form.queryById("orderCodeQueryButton").hide();
														var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															form.getForm().findField(key).setReadOnly(true);
														});
														form.getForm().findField("orderType").setReadOnly(true);
//														form.getForm().findField("saleFor").setReadOnly(true);
														form.getForm().findField("jiaoQiTianShu").setReadOnly(true);
														form.getForm().findField("handleTime").setReadOnly(true);
														form.getForm().findField("isYp").setReadOnly(true);
														form.getForm().findField("payType").setReadOnly(false);
														form.getForm().findField("huoDongType").setReadOnly(false);
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														//金额查看权限
														if(IS_MONEY=="true"){
															Ext.getCmp("batchjgdDownload").show();
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
														var orderType = form.getForm().findField("orderType").getValue();
														//免费单
														if("OR4"==orderType){
															if(sapOrderCode==""){
//								                				Ext.getCmp("tranSap").show();
								                			}
														}
					                    			}else if(taskdefId=="usertask_store_confirm"||taskdefId=="usertask3"){//确认报价
					                    				var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															form.getForm().findField(key).setReadOnly(true);
														});
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														var saleOneCustGrid = form.queryById("saleOneCustGrid");
														Ext.Array.each(saleOneCustGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														Ext.getCmp("saveOrder").hide();
														Ext.getCmp("tranSap").hide();
														Ext.getCmp("checkSale").show();
														Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														Ext.getCmp("saleUploadFile").show();
														Ext.getCmp("saleRemoveFile").show();
														Ext.getCmp("batchPdfDownload").show();
														form.queryById("songDaFang_queryCust").hide();
//														form.queryById("pOrderCodeButton").hide();
														form.queryById("shouDaFang_queryCust").hide();
//														form.queryById("fujianDownloadButtonClick").hide();
														form.queryById("orderCodeQueryButton").hide();
														//金额查看权限
														if(IS_MONEY=="true"){
															Ext.getCmp("batchjgdDownload").show();
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															//汇款凭证显示
															tabs.items.getAt(3).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
					                    			}else if(taskdefId=="usertask_finance"||taskdefId=="usertask4"){//财务确认
					                    				var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
								                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
					                    				var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															//console.log(key + ":" + value);
															form.getForm().findField(key).setReadOnly(true);
														});
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														var saleOneCustGrid = form.queryById("saleOneCustGrid");
														Ext.Array.each(saleOneCustGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														Ext.getCmp("saveOrder").hide();
														if(sapOrderCode==""){
//							                				Ext.getCmp("tranSap").show();
							                			}
														Ext.getCmp("checkSale").show();
														Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														Ext.getCmp("saleUploadFile").hide();
														Ext.getCmp("saleRemoveFile").hide();
														form.queryById("songDaFang_queryCust").hide();
//														form.queryById("pOrderCodeButton").hide();
														form.queryById("shouDaFang_queryCust").hide();
//														form.queryById("fujianDownloadButtonClick").hide();
														form.queryById("orderCodeQueryButton").hide();
														//金额查看权限
														if(IS_MONEY=="true"){
															Ext.getCmp("batchjgdDownload").show();
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															//汇款凭证显示
															tabs.items.getAt(3).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
					                    			}
					                    		}
					                    	}
					                	}else if(docStatus=="2"){//流程结束
					                		var formValues = form.getValues();
					                		
											Ext.Object.each(formValues, function(key, value, myself) {
												//console.log(key + ":" + value);
												form.getForm().findField(key).setReadOnly(true);
											});
											Ext.Array.each(itemGrid.columns, function(column) {
												if("undefined"!=typeof(column.editor)){
													column.editor = null;
												}
											});
											var saleOneCustGrid = form.queryById("saleOneCustGrid");
											Ext.Array.each(saleOneCustGrid.columns, function(column) {
												if("undefined"!=typeof(column.editor)){
													column.editor = null;
												}
											});
											var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
					                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
											Ext.getCmp("saveOrder").hide();
				                			Ext.getCmp("tranSap").hide();
											Ext.getCmp("checkSale").show();
											Ext.getCmp("addOrderItem").hide();
											Ext.getCmp("removeOrderItem").hide();
											Ext.getCmp("saleUploadFile").hide();
											Ext.getCmp("saleRemoveFile").hide();
											form.queryById("songDaFang_queryCust").hide();
//											form.queryById("pOrderCodeButton").hide();
											form.queryById("shouDaFang_queryCust").hide();
//											form.queryById("fujianDownloadButtonClick").hide();
											form.queryById("orderCodeQueryButton").hide();
											//金额查看权限
											if(IS_MONEY=="true"){
												Ext.getCmp("batchjgdDownload").show();
												//订单金额信息显示
												tabs.items.getAt(1).tab.show();
												//汇款凭证显示
												tabs.items.getAt(3).tab.show();
												//行项目价格显示
												Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
														column.show();
													}
												});
											}
											
											var createUser = form.getForm().findField("createUser").getValue();
											var orderType = form.getForm().findField("orderType").getValue();
											//免费单 并且 当前登陆账号等于订单创建人
											if("OR4"==orderType && createUser==CURR_USER_LOGIN_NO){
												if(sapOrderCode==""){
//					                				Ext.getCmp("tranSap").show();
					                			}
											}
											//免费单
											if("OR4"==orderType){
												//汇款凭证隐藏
												tabs.items.getAt(3).tab.hide();
											}
					                	}
					                }
		                        }
							},
					        failure:function(form,action){
					        	var result = Ext.decode(action.response.responseText);
					            Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
					        }
						});
					}else{
						Ext.getCmp("checkSale").hide();
					}
					
				}
				/**************************/
				/******* 其它类型 *********/
				/*************************/
				else{
					orderTypeStore.filter([
					    {filterFn: function(item) { 
					    		if("OR3"!=item.get("id") && "OR4"!=item.get("id")){
					    			return true;
					    		}
					    	}
					    }
					]);
					
					jiaoQiTianShuStore.filter([
					    {filterFn: function(item) { 
					    		if("2"!=item.get("id") && "4"!=item.get("id") && "14"!=item.get("id")){
					    			return true;
					    		}
					    	}
					    }
					]);
					
					form.getForm().findField("fuFuanCond").setValue("1");//付款条件
					form.getForm().findField("fuFuanCond").initValue();
					tabs.items.getAt(5).tab.hide();
					//加载数据
					if(me.formId!=null){
						form.load({
							url:'main/sale/findById',
							params : {
								id : me.formId
							},
							method : 'GET',
							async:false,
							success:function(f,action){
								var result = Ext.decode(action.response.responseText);
								if(("undefined"==typeof(result.data.orderCode) || null == result.data.orderCode || "" == result.data.orderCode)
										/*&& "OR4" == result.data.orderType*/){
									form.queryById("orderCodeQueryButton").show();
								}
								form.getForm().findField("shouDaFang").setValue(result.data.shouDaFang);
								form.getForm().findField("shouDaFang").initValue();
								form.getForm().findField("songDaFang").setValue(result.data.songDaFang);
								form.getForm().findField("songDaFang").initValue();
								form.getForm().findField("kunnrName1").setValue(result.data.kunnrName1);
								form.getForm().findField("kunnrName1").initValue();
								
								var saleId = form.getForm().findField("id").getValue();
								/*me.formId*/
								itemGrid.getStore().load({params:{'pid':saleId},callback: function(records, operation, success) {
									reCalculate(form,itemGrid);
								}});
								var saleOneCustGrid = form.queryById("saleOneCustGrid");
								saleOneCustGrid.getStore().load({params:{'pid':saleId},callback: function(records, operation, success) {
									var saleOneCustCount = saleOneCustGrid.getStore().getCount();
			                        if(saleOneCustCount > 0){
			                        	//临时送/售达方显示
										tabs.items.getAt(4).tab.show();
			                        }else{
			                        	//临时送/售达方隐藏
										tabs.items.getAt(4).tab.hide();
			                        }
								}});
								
								var sysFileGrid = form.queryById("sysFileGrid");
		                        sysFileGrid.getStore().load({params:{'foreignId':saleId,'fileType':''}});
		                        
		                        
		                        if(me.editFlag){//me.editFlag==true能编辑
		                        	//var sapOrderCode = form.getForm().findField("sapOrderCode").getValue();
		                			var obj = Ext.ux.DataFactory.getFlowActivityId(me.formId);
		                			console.log(obj);
//					                alert(obj.docStatus);
//					                alert(obj.taskdefId);
//					                alert(obj.taskName);
//					                alert(obj.taskGroup);
//					                alert(obj.assignee);
					                if(obj != null){
					                	var docStatus = obj.docStatus;
					                	if(docStatus=="0"){//未提交流程
					                		var createUser = form.getForm().findField("createUser").getValue();
					                		if(createUser!=CURR_USER_LOGIN_NO){//未提交前，登陆账号不等于订单创建人，只能查看
					                			var formValues = form.getValues();
												Ext.Object.each(formValues, function(key, value, myself) {
													//console.log(key + ":" + value);
													form.getForm().findField(key).setReadOnly(true);
												});
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												var saleOneCustGrid = form.queryById("saleOneCustGrid");
												Ext.Array.each(saleOneCustGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												Ext.getCmp("saveOrder").hide();
												Ext.getCmp("tranSap").hide();
												Ext.getCmp("checkSale").hide();
												Ext.getCmp("addOrderItem").hide();
												Ext.getCmp("removeOrderItem").hide();
												Ext.getCmp("saleUploadFile").hide();
												Ext.getCmp("saleRemoveFile").hide();
												form.queryById("songDaFang_queryCust").hide();
					                		}else{
					                			form.getForm().findField("jiaoQiTianShu").setReadOnly(true);
			                    				form.getForm().findField("handleTime").setReadOnly(true);
												//form.getForm().findField("isYp").setReadOnly(false);
					                		}
					                	}else if(docStatus=="1"){//已提交流程
					                		var taskdefId = obj.taskdefId;
					                    	var name = obj.taskName;
					                    	var taskGroup = obj.taskGroup;
					                    	var assignee = obj.assignee;
					                    	if(!assignee){//没有流程编辑权限，只能查看订单，并查看流程
					                    		var formValues = form.getValues();
												Ext.Object.each(formValues, function(key, value, myself) {
													//console.log(key + ":" + value);
													form.getForm().findField(key).setReadOnly(true);
												});
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												var saleOneCustGrid = form.queryById("saleOneCustGrid");
												Ext.Array.each(saleOneCustGrid.columns, function(column) {
													if("undefined"!=typeof(column.editor)){
														column.editor = null;
													}
												});
												Ext.getCmp("saveOrder").hide();
												Ext.getCmp("tranSap").hide();
												Ext.getCmp("checkSale").show();
												Ext.getCmp("addOrderItem").hide();
												Ext.getCmp("removeOrderItem").hide();
												Ext.getCmp("saleUploadFile").hide();
												Ext.getCmp("saleRemoveFile").hide();
												form.queryById("songDaFang_queryCust").hide();
												if("undefined"!=typeof(taskdefId) && taskdefId=="usertask_store" ){
													form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                				Ext.getCmp("checkSale").hide();//如果是起草环节，隐藏防止提交到下一环节（审绘环节）
					                			}
												if(taskdefId=="usertask_drawing"){
					                				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                			}
												//订单审价  或  客户确认  或  财务确认
												if(taskdefId=="usertask_valuation" || taskdefId=="usertask_store_confirm" || taskdefId=="usertask_finance"){
													//金额查看权限
													if(IS_MONEY=="true"){
														//订单金额信息显示
														tabs.items.getAt(1).tab.show();
														Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
														//行项目价格显示
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																column.show();
															}
														});
													}
												}
					                    	}else{//有流程编辑权限
					                    		if("undefined"!=typeof(taskdefId)){
					                    			if(taskdefId=="usertask_store"){//订单起草
					                    				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                    				form.getForm().findField("jiaoQiTianShu").setReadOnly(true);
					                    				form.getForm().findField("handleTime").setReadOnly(true);
														//form.getForm().findField("isYp").setReadOnly(false);
					                    			}else if(taskdefId=="usertask_drawing"){//订单审绘
					                    				//tabs.items.getAt(5).tab.show();
								                		var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
								                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
					                    				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});;
					                    				form.getForm().findField("jiaoQiTianShu").getStore().load({params:{showDisabled:false}});
					                    				Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														form.getForm().findField("orderType").setReadOnly(true);
														//form.getForm().findField("jiaoQiTianShu").setReadOnly(false);
														form.getForm().findField("handleTime").setReadOnly(false);
//														form.getForm().findField("isYp").setReadOnly(true);
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor) && "maktx"!=column.dataIndex && "amount"!=column.dataIndex){
																column.editor = null;
															}
														});
														
														//add by hzm 20170313 start
														var formValues = form.getValues();
														var _saleId = formValues.id;
														//订单审绘提交时，判断订单行项目里KIT附近是否存在名字相同的，存在则报提示。
														//console.log("******判断订单行项目里KIT附近是否存在名字相同*******");
														Ext.Ajax.request({
																url : '/main/mm/MyKitCheck',
																async:false,
																params : {
																	saleId :_saleId 
																},
																method : 'GET',
																success : function(response, opts) {
																	var jsonResult = Ext.decode(response.responseText);
																	var realvalue = "";
																	var tempsz="";
																	if(jsonResult.success){ 
																		if(jsonResult.msg == 'false'){
																			var sz=[];
																			var kk = jsonResult.obj;
																			if(kk.length>0){
																				for(str in kk){
																					var tempGroup = kk[str];
																					for(temp in tempGroup){
																						var realstr =  tempGroup[temp];
																						realvalue = realvalue + "【"+realstr+"】";
																					}
																					sz.push(realvalue);
																					realvalue = "";
																				}
																				for (resz in sz){
																					tempsz = tempsz + sz[resz] + "具有相同的文件,请注意！";
																				}
																				Ext.MessageBox.alert("提示",tempsz);
																			}
																		}					
																	}else{
																									
																	}
																},
																failure : function(response, opts) {
																}
														});
														//add by hzm 20170313 end
														
					                    			}else if(taskdefId=="usertask_valuation"){//订单审价
					                    				Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														form.queryById("songDaFang_queryCust").hide();
														var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															//console.log(key + ":" + value);
															form.getForm().findField(key).setReadOnly(true);
														});
														form.getForm().findField("payType").setReadOnly(false);
														form.getForm().findField("fuFuanCond").setReadOnly(false);
														form.getForm().findField("huoDongType").setReadOnly(false);
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														//金额查看权限
														if(IS_MONEY=="true"){
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
					                    			}else if(taskdefId=="usertask_store_confirm"){//客户确认
					                    				var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															//console.log(key + ":" + value);
															form.getForm().findField(key).setReadOnly(true);
														});
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														var saleOneCustGrid = form.queryById("saleOneCustGrid");
														Ext.Array.each(saleOneCustGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														Ext.getCmp("saveOrder").hide();
														Ext.getCmp("tranSap").hide();
														Ext.getCmp("checkSale").show();
														Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														Ext.getCmp("saleUploadFile").show();
														Ext.getCmp("saleRemoveFile").show();
														Ext.getCmp("batchPdfDownload").show();
														form.queryById("songDaFang_queryCust").hide();
														//金额查看权限
														if(IS_MONEY=="true"){
															Ext.getCmp("batchjgdDownload").show();
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//汇款凭证显示
															tabs.items.getAt(3).tab.show();
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
					                    			}else if(taskdefId=="usertask_finance"){//财务确认
					                    				var formValues = form.getValues();
														Ext.Object.each(formValues, function(key, value, myself) {
															//console.log(key + ":" + value);
															form.getForm().findField(key).setReadOnly(true);
														});
														Ext.Array.each(itemGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														var saleOneCustGrid = form.queryById("saleOneCustGrid");
														Ext.Array.each(saleOneCustGrid.columns, function(column) {
															if("undefined"!=typeof(column.editor)){
																column.editor = null;
															}
														});
														Ext.getCmp("saveOrder").hide();
//														Ext.getCmp("tranSap").show();
														/*if(sapOrderCode==""){
							                			}*/
														Ext.getCmp("checkSale").show();
														Ext.getCmp("addOrderItem").hide();
														Ext.getCmp("removeOrderItem").hide();
														Ext.getCmp("saleUploadFile").hide();
														Ext.getCmp("saleRemoveFile").hide();
														form.queryById("songDaFang_queryCust").hide();
														//金额查看权限
														if(IS_MONEY=="true"){
															Ext.getCmp("batchjgdDownload").show();
															//订单金额信息显示
															tabs.items.getAt(1).tab.show();
															Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
															//汇款凭证显示
															tabs.items.getAt(3).tab.show();
															//行项目价格显示
															Ext.Array.each(itemGrid.columns, function(column) {
																if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
																	column.show();
																}
															});
														}
					                    			}
					                			}
					                    	}
					                    	
					                	}else if(docStatus=="2"){//流程结束
					                		var formValues = form.getValues();
					                		Ext.getCmp("batchPdfDownload").show();
					                		if(IS_MONEY=="true"){
												Ext.getCmp("batchjgdDownload").show();
					                		}
					                		tabs.items.getAt(5).tab.show();
					                		var saleLogisticsGrid=form.queryById("saleLogisticsGridItemId");
					                		saleLogisticsGrid.getStore().load({params:{pid:me.formId}});
											Ext.Object.each(formValues, function(key, value, myself) {
												//console.log(key + ":" + value);
												form.getForm().findField(key).setReadOnly(true);
											});
											Ext.Array.each(itemGrid.columns, function(column) {
												if("undefined"!=typeof(column.editor)){
													column.editor = null;
												}
											});
											var saleOneCustGrid = form.queryById("saleOneCustGrid");
											Ext.Array.each(saleOneCustGrid.columns, function(column) {
												if("undefined"!=typeof(column.editor)){
													column.editor = null;
												}
											});
											Ext.getCmp("saveOrder").hide();
				                			//Ext.getCmp("tranSap").show();
											Ext.getCmp("checkSale").show();
											Ext.getCmp("addOrderItem").hide();
											Ext.getCmp("removeOrderItem").hide();
											Ext.getCmp("saleUploadFile").hide();
											Ext.getCmp("saleRemoveFile").hide();
											form.queryById("songDaFang_queryCust").hide();
											//金额查看权限
											if(IS_MONEY=="true"){
												//订单金额信息显示
												tabs.items.getAt(1).tab.show();
												Ext.getCmp("saleItemPriceGridId").getStore().load({params:{'id':me.formId}});
												//汇款凭证显示
												tabs.items.getAt(3).tab.show();
												//行项目价格显示
												Ext.Array.each(itemGrid.columns, function(column) {
													if("undefined"!=typeof(column.dataIndex) && "totalPrice"==column.dataIndex){
														column.show();
													}
												});
											}
											Ext.each(me.queryById("saleLogisticsGridItemId").columns,function(column){
												console.log(column.editor);
												if("undefined"!=typeof(column.editor)){
													column.editor=null;
												}
											});
					                	}
					                }
		                        }
							},
					        failure:function(form,action){
					        	var result = Ext.decode(action.response.responseText);
					            Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
					        }
						});
					}else{
						Ext.getCmp("checkSale").hide();
						
						form.getForm().findField("orderDate").setValue(Ext.Date.format(new Date(), 'Y-m-d'));
						form.getForm().findField("orderDate").initValue();
						
						form.getForm().findField("shouDaFang").setValue(KUNNR);
						form.getForm().findField("shouDaFang").initValue();
						
						form.getForm().findField("kunnrName1").setValue(KUNNR_NAME1);
						form.getForm().findField("kunnrName1").initValue();
						
						form.getForm().findField("songDaFang").setValue(KUNNRS);
						form.getForm().findField("songDaFang").initValue();
						
						form.getForm().findField("dianMianTel").setValue(KUNNR_TEL);
						form.getForm().findField("dianMianTel").initValue();
						
						form.getForm().findField("jiaoQiTianShu").setReadOnly(true);
						form.getForm().findField("handleTime").setReadOnly(true);
						//form.getForm().findField("isYp").setReadOnly(false);
						
						//临时售达方类型Z900，临时送达方类型Z720
						if(KUNNR_TYPE=="Z900" || KUNNRS_TYPE=="Z720"){
							//临时送/售达方显示
							tabs.items.getAt(4).tab.show();
							var saleOneCustGrid = form.queryById("saleOneCustGrid");
	                    	var saleOneCustStore = saleOneCustGrid.getStore();
							if(KUNNR_TYPE=="Z900"){
								var data=[{
									'id':null,
									'createUser':'',
									'updateUser':'',
									'createTime':null,
									'updateTime':null,
									'kunnr':KUNNR,
									'anred':'',
									'saleOneCustName1':'',
									'street':'',
									'pstlz':'',
							        'mcod3':'',
							        'land1':'CN',
							        'regio':'',
							        'telf1':'',
							        'ort02':'',
							        'saleOneCustType':'临时售达方'
								}];
								var storeCount = saleOneCustStore.getCount();
								saleOneCustStore.insert(storeCount, data);
							}
							if(KUNNRS_TYPE=="Z720"){
								var data=[{
									'id':null,
									'createUser':'',
									'updateUser':'',
									'createTime':null,
									'updateTime':null,
									'kunnr':KUNNRS,
									'anred':'',
									'saleOneCustName1':'',
									'street':'',
									'pstlz':'',
							        'mcod3':'',
							        'land1':'CN',
							        'regio':'',
							        'telf1':'',
							        'ort02':'',
							        'saleOneCustType':'临时送达方'
								}];
								var storeCount = saleOneCustStore.getCount();
								saleOneCustStore.insert(storeCount, data);
							}
						}else{
							//临时送/售达方隐藏
							tabs.items.getAt(4).tab.hide();
						}
					}
				}
				
				if(me.editFlag==false){//me.editFlag==false不能编辑
					var formValues = form.getValues();
					Ext.Object.each(formValues, function(key, value, myself) {
						//console.log(key + ":" + value);
						form.getForm().findField(key).setReadOnly(true);
					});
					Ext.Array.each(itemGrid.columns, function(column) {
						if("undefined"!=typeof(column.editor)){
							column.editor = null;
						}
					});
					var saleOneCustGrid = form.queryById("saleOneCustGrid");
					Ext.Array.each(saleOneCustGrid.columns, function(column) {
						if("undefined"!=typeof(column.editor)){
							column.editor = null;
						}
					});
					Ext.getCmp("saveOrder").hide();
					Ext.getCmp("tranSap").hide();
					Ext.getCmp("checkSale").hide();
					Ext.getCmp("addOrderItem").hide();
					Ext.getCmp("removeOrderItem").hide();
					Ext.getCmp("saleUploadFile").hide();
					Ext.getCmp("saleRemoveFile").hide();
					form.queryById("songDaFang_queryCust").hide();
//					form.queryById("pOrderCodeButton").hide();
				}
				this.callParent(arguments);
			}
		});



/**
 * 计算付款金额（通过订单总额，付款条件）
 */
function calculateFuFuanMoney(form,editFlag){
	if(editFlag==false){
		return;
	}
	var orderTotal = form.getForm().findField("orderTotal").getValue();//订单总额
	var fuFuanCond = form.getForm().findField("fuFuanCond").getValue();//付款条件
	if(fuFuanCond==null || fuFuanCond==""){
		return;
	}
	var fuFuanMoney = 0;
	if(fuFuanCond=="1"){
		fuFuanMoney = orderTotal;
	}else if(fuFuanCond=="2"){
		fuFuanMoney = 0;
	}else if(fuFuanCond=="3"){
		fuFuanMoney = accMul(orderTotal,0.5);
	}else if(fuFuanCond=="4"){
		fuFuanMoney = accMul(orderTotal,0.3);
	}
	form.getForm().findField("fuFuanMoney").setValue(fuFuanMoney);
}
/**
 * 审核提交前操作
 * @returns
 */

function flowValidate(_saleId,flowtype){
	var _flowInfo = Ext.ux.DataFactory.getFlowActivityId(_saleId);
	var flag = true;
	if("1"==_flowInfo.docStatus && _flowInfo.assignee==true){
//	              订单环节
//		gp_store 门店 
//		gp_drawing 订单审绘 
//		gp_valuation 订单审价 
//		gp_finance 财务确认 
//		gp_store_customer 客户确认
		
		//当前环节为订单审汇（补单没有这个环节）
		if("gp_drawing"==_flowInfo.taskGroup||"bg_gp_drawing"==_flowInfo.taskGroup){
			//1是提交，0是退回
			if(flowtype=="1"){
				var _values;
				Ext.Ajax.request({
					url : 'main/mm/gpdrawingValidate',
					method : 'GET',
					params : {
						'saleHeadId' : _saleId
					},
					async:false,
					dataType : "json",
					contentType : 'application/json',
					success : function(response, opts) {
						_values = Ext.decode(response.responseText);
					},
					failure : function(response, opts) {
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				if(false == _values.success){
					flag = false;
					Ext.MessageBox.alert("提示",_values.errorMsg);
				}
			}
			//add by Mark on 20160414 finally --start
			//审核状态为已审绘的审绘环节订单，不能退回至起草
			if(flowtype=="0"){
				var _values;
				Ext.Ajax.request({
					url:'main/sale/findItemsByPid',
					method:'get',
					params:{
						'pid':_saleId
					},
					async:false,
					dataType:"json",
					contentType:'application/json',
					success:function(response,opts){
						_values=Ext.decode(response.responseText);
					},
					failure:function(response,opts){
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				if(false == _values.success){
					flag = false;
					Ext.MessageBox.alert("提示",_values.errorMsg);
				}else
				if(_values==null || _values.content.length<=0 ){
				}else{
					var x;
					for(x in _values.content){
						var _object=_values.content[x];
						//只要是非标产品的并且审核状态不是取消的，未开始的，出错返回的，文件类型是客户2020绘图的，及kitId不为空的
						if("2"!=_object.saleFor&&"1"!=_object.isStandard &&"QX"!=(_object.stateAudit.toUpperCase())&& "A"!= (_object.stateAudit.toUpperCase()) &&"C"!=(_object.stateAudit.toUpperCase()) && ("1"==_object.fileType) &&Ext.isEmpty(_object.kitId)){
							flag=false;
							Ext.MessageBox.alert("提示","行号："+_object.posex+" 请上传kit文件！");
							break;
						}
					}
				}
			}
			//add by Mark on 20160414 finally --end
		}else if("gp_valuation"==_flowInfo.taskGroup){//当前环节是订单审价
			//1是提交，0是退回
			if(flowtype=="1"){
				//add by mark 审价提交检测报价清单 on 2016-06-02--start
				var _values;
				Ext.Ajax.request({
					url:'main/sale/findItemsByPid',
					method:'get',
					params:{
						'pid':_saleId
					},
					async:false,
					dataType:"json",
					contentType:'application/json',
					success:function(response,opts){
						_values=Ext.decode(response.responseText);
					},
					failure:function(response,opts){
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				if(false == _values.success){
					flag = false;
					Ext.MessageBox.alert("提示",_values.errorMsg);
				}
				if(_values==null || _values.content.length<=0 ){
					
				}else
				{
					var x;
					for(x in _values.content){
						var _object=_values.content[x];
						if(("QX"!=_object.stateAudit)&&("1"!=_object.isStandard || 'OR4'==_object.ortype) && Ext.isEmpty(_object.priceId)){
							if('OR4'==_object.ortype||'OR3'==_object.ortype){
								flag=false;
								Ext.MessageBox.alert("提示","行号："+_object.posex+" 没有报价清单！");
								break;
							}
						}//else{
								Ext.Ajax.request({
								url:'main/mm/querySaleItemPrice',
								method:'get',
								params:{
									'pid':_object.id,
									'page':1,
									'start':0,
									'limit':25
								},
								async:false,
								dataType:"json",
								contentType:'application/json',
								success:function(response,opts){
									_sale_item_price=Ext.decode(response.responseText);
								},
								failure:function(response,opts){
									Ext.MessageBox.alert("提示","加载数据失败！");
								}
							});
						var _index;
					    for(_index in _sale_item_price){
						if(("QX"!=_object.stateAudit)&&("PR01"==_sale_item_price[_index].type && _sale_item_price[_index].total<=0 )){
							flag=false;
							Ext.MessageBox.alert("提示","行号："+_object.posex+" 没有填写价格！");
							break;
							}
						}
						//}
					}
				}
				if(!flag){
					return false;
				}
				//add by Mark on 20160602 --end
				
				Ext.Ajax.request({
					url : 'main/sale/validateCheckPrice',
					method : 'GET',
					params : {
						'saleHeadId' : _saleId
					},
					async:false,
					dataType : "json",
					contentType : 'application/json',
					success : function(response, opts) {
						_values = Ext.decode(response.responseText);
					},
					failure : function(response, opts) {
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				if(false == _values.success){
					flag = false;
					Ext.MessageBox.alert("提示",_values.errorMsg);
				}
			}
		}else if("gp_finance"==_flowInfo.taskGroup){//当前环节是财务确认
			//flowtype=1是提交，flowtype=0是退回
			Ext.Ajax.request({
				url : 'main/sale/validateTranSap',
				method : 'GET',
				params : {
					'saleHeadId' : _saleId,
					'flowtype' : flowtype
				},
				async:false,
				dataType : "json",
				contentType : 'application/json',
				success : function(response, opts) {
					_values = Ext.decode(response.responseText);
				},
				failure : function(response, opts) {
					Ext.MessageBox.alert("提示","加载数据失败！");
				}
			});
			//财务确认过后只是隐藏任务,但是不会commitFlow add by mark on 2017-04-18
        	if(!_values.success){
        		flag=false;
        		Ext.MessageBox.alert("提示",_values.errorMsg);
        	}else if(flowtype==1){
        		flag=false;
        		Ext.MessageBox.alert("提示","加入队列成功");
	        }else{
	        	flag=true;
	        }
//        if(false == _values.success){
//          flag = false;
//          Ext.MessageBox.alert("提示",_values.errorMsg);
//        }
		}else if("gp_customer_service"==_flowInfo.taskGroup){//客服起草环节（只有补单才有这个环节）
			Ext.Ajax.request({
				url : 'main/sale/validateCurrKFQC',
				method : 'GET',
				params : {
					'saleHeadId' : _saleId
				},
				async:false,
				dataType : "json",
				contentType : 'application/json',
				success : function(response, opts) {
					_values = Ext.decode(response.responseText);
				},
				failure : function(response, opts) {
					Ext.MessageBox.alert("提示","加载数据失败！");
				}
			});
			if(false == _values.success){
				flag = false;
				Ext.MessageBox.alert("提示",_values.errorMsg);
			}
		}else if("gp_store"==_flowInfo.taskGroup){//当前环节是门店 起草环节
			flag = saleBoforeActivate(_saleId);
		}else if("gp_store_customer"==_flowInfo.taskGroup){  //客户确认环节查看单子是否有变更未审核情况
			if(flowtype=="1"){
				Ext.Ajax.request({
					url:'main/bg/existUnCheck',
					method:'GET',
					async:false,
					dataType : "json",
					contentType : 'application/json',
					params:{
						'saleHeaderId':_saleId
					},
					success:function(response,opts){
						_values = Ext.decode(response.responseText);
					},
					failure:function(response,opts){
						Ext.MessageBox.alert("提示","加载数据失败！");
					}
				});
				if(_values.data.exist==true){
					flag=false;
					Ext.MessageBox.alert("提示","您的订单中还有未审核变更项");
				}
			}
		}
	}
	if(flag){
		if(flowtype=="1"){
			Ext.Ajax.request({
				url:'main/user/checkActExpired',
				method:'GET',
				async:false,
				dataType : "json",
				contentType : 'application/json',
				params:{
					assignee:CURR_USER_LOGIN_NO ,
					//taskId:_flowInfo.taskId,
					procinstId:_flowInfo.procinstid,
					actId:_flowInfo.taskdefId,
					checkType:'taskId'
				},
				success:function(response,opts){
					_values = Ext.decode(response.responseText);
				},
				failure:function(response,opts){
					Ext.MessageBox.alert("提示","加载数据失败！");
				}
			});
			if(_values.data.hasexpired==true){
				Ext.ComponentQuery.query('window[itemId=expiredWindow] hiddenfield[itemId=duration]')[0].setValue(_values.data.duration);
				Ext.ComponentQuery.query('window[itemId=expiredWindow]')[0].show();
				flag=false;
			}
		}
	}
	//add by mark for expired on 20160618 --start
	return flag;
}
/**
 *激活流程之后操作 
 */
function saleAfterActivate(_saleId){
	var flag = true;
	//删除我的商品表数据
	Ext.Ajax.request({
		url : 'main/myGoods/saleAfterActivate',
		method : 'GET',
		params : {
			'saleHeadId' : _saleId
		},
		async:false,
		dataType : "json",
		contentType : 'application/json',
		success : function(response, opts) {
			
		},
		failure : function(response, opts) {
			
		}
	});
	return flag;
}
/**
 *激活流程之前操作 
 */
function saleBoforeActivate(_saleId){
	var flag = true;

	var _values;
	Ext.Ajax.request({
		url : 'main/myGoods/saleBoforeActivate',
		method : 'GET',
		params : {
			'saleHeadId' : _saleId
		},
		async:false,
		dataType : "json",
		contentType : 'application/json',
		success : function(response, opts) {
			_values = Ext.decode(response.responseText);
		},
		failure : function(response, opts) {
			Ext.MessageBox.alert("提示","加载数据失败！");
		}
	});
	
	if(false == _values.success){
		flag = false;
		Ext.MessageBox.alert("提示",_values.errorMsg);
	}

	return flag;
}
/**
 * 改变订单类型
 */
function changeOrderType(me){
	var saleForm = me.queryById("saleForm");
	var itemGrid = me.queryById("saleGrid");
    var itemStore = itemGrid.getStore();
    
    var orderType = saleForm.getForm().findField("orderType").getValue();
    if("OR4" != orderType){
    	saleForm.queryById("orderCodeQueryButton").hide();
    }
    
    var ids = [];
    var itemCount = itemStore.getCount();
	for (var i = 0; i <itemCount; i++) {
		var record = itemStore.getAt(i);
		if("undefined"!=typeof(record.get('id'))){
			ids.push(record.get('id'));
		}
	}
	if(ids.length==0){
		var saleId = saleForm.getForm().findField("id").getValue();
		itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
					        calculationTotalPrice(saleForm,itemGrid,'delete');
					    }});
		return;
	}
	Ext.Ajax.request({
		url : 'main/sale/deleteSaleItemByIds',
		params : {
			ids : ids,
			custCode : saleForm.getForm().findField("shouDaFang").getValue()
		},
		method : 'POST',
		success : function(response, opts) {
			//itemStore.remove(sm.getSelection());
			var saleId = saleForm.getForm().findField("id").getValue();
			itemStore.load({params:{'pid':saleId},callback: function(records, operation, success) {
						        calculationTotalPrice(saleForm,itemGrid,'delete');
						    }});
//			Ext.MessageBox.alert("提示信息","删除成功！");
		},
		failure : function(response, opts) {
			Ext.MessageBox.alert("提示信息","网络异常！");
		}
	});
}

/**
 * 重新计算订单金额与付款金额
 */
function reCalculate(form,itemGrid){
	var itemStore = itemGrid.getStore();
	var itemCount = itemStore.getCount();
	var allTotalPrice = 0;
	for (var i = 0; i <itemCount; i++) {
		var record = itemStore.getAt(i);
		if("QX" != record.get("stateAudit")){
			allTotalPrice = accAdd(allTotalPrice,record.get("totalPrice"));
		}
	}
	form.getForm().findField("orderTotal").setValue(allTotalPrice);
	form.getForm().findField("orderTotal").initValue();
	
}

function saveExpired(){
	console.log("enter");
	var _values;
	var _expiredType=Ext.ComponentQuery.query('window[itemId=expiredWindow] dictcombobox[itemId=expiredType]')[0];
	var _expiredReason=Ext.ComponentQuery.query('window[itemId=expiredWindow] textarea[itemId=expiredReason]')[0];
	var _expiredAssignee=Ext.ComponentQuery.query('window[itemId=expiredWindow] hiddenfield[itemId=assignee]')[0].getValue();
	var _saleId=Ext.ComponentQuery.query('form[itemId=saleForm] hiddenfield[name=id]')[0].getValue();
	var _duration=Ext.ComponentQuery.query('window[itemId=expiredWindow] hiddenfield[itemId=duration]')[0].getValue();
	var _flowInfo = Ext.ux.DataFactory.getFlowActivityId(_saleId);
	if(!_expiredType.isValid()){
		_expiredType.focus();
	}else
	if(!_expiredReason.isValid() || Ext.isEmpty(_expiredReason.getValue().trim())){
		_expiredReason.focus();
	}else{
	
	Ext.Ajax.request({
		url : 'main/user/saveExpired',
		params : {
			'expiredType':_expiredType.getValue(),
			'expiredReason':_expiredReason.getValue(),
			'assignee':_expiredAssignee,
			'procinstId':_flowInfo.procinstid,
			'actId':_flowInfo.taskdefId,
			'actName':_flowInfo.taskName,
			'duration':_duration
		},
		method : 'POST',
		async:false,
		dataType : "json",
		contentType : 'application/json',
		success:function(response,opts){
			_values = Ext.decode(response.responseText);
		},
		failure : function(response, opts) {
			Ext.MessageBox.alert("提示信息","网络异常");
		}
	});
    Ext.ComponentQuery.query('window[itemId=expiredWindow]')[0].hide();
    Ext.ComponentQuery.query('window[itemId=flowwindow] buttontransparent')[0].fireEvent("click");
	}
}
