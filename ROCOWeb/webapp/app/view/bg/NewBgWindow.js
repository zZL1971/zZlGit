Ext.apply(Ext.form.VTypes, { 
    phonecheck : function(val, field) { 
	    var str=val;
	    //var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
	    var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$|^(\(\d{3,4}-\)|\d{3.4}-)?\d{7,8}$/;
	    return reg.test(str);
	},
    phonecheckText : "号码不匹配!"  
});

Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.bg.NewBgWindow', {
			extend : 'Ext.window.Window',
			alias : 'widget.NewBgWindow',
			formId:null,
			maximizable:true,
			height : 700,
			width : document.body.clientWidth * 0.9,
			modal:true,
			autoScroll:true,
			constrainHeader: true,
			closable : true,
			border : false,
			queryBgType:null,
//			layout:'border',
			initComponent : function() {
				var bgTypeCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
						fieldLabel : '变更类型<font color=red>*</font>',
						emptyText: '请选择...',
						allowBlank: false,
						name:'bgType',
						dict:'BG_TYPE'
				});
				
//				var orderStatusCombobox = Ext.create("Ext.ux.form.DictCombobox",{  
//						fieldLabel : '订单状态',
//						emptyText: '请选择...',
//						name:'orderStatus',
//						dict:'ORDER_STATUS',
//						colspan: 2
//				});
				 var me = this;
				 var form = Ext.widget('form',{
					   tbar : [{
							xtype : 'button',
							text : '保存',
							id : 'saveBg',
							iconCls:'table_save'
						},{
							xtype : 'button',
							text : '提交',
							id : 'submitBg',
							iconCls:'table_tick',
							hidden:true
						},{
							xtype : 'button',
							text : '通过',
							id : 'submitBg2',
							iconCls:'table_tick',
							hidden:true
						},{
							xtype : 'button',
							text : '退回',
							id : 'resubmitBg',
							iconCls:'flow_cancel',
							hidden:true
						},{
							xtype : 'button',
							text : '审核记录',
							id : 'recordsCheck',
							iconCls:'func_default'
						}],
//		    		   region:'north',
			    	   itemId:'bgForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;'
					   },
					   layout: {
					        type: 'table',
					        columns: 2
					   },
			    	   items : [
			    		    {
								xtype:'hiddenfield',
								name : 'id'
//								,id : 'custHeaderId'
					        },
					        {
								xtype:'hiddenfield',
								name : 'createUser'
					        },
					        {
								name : 'createTime',
								xtype:'datefield',
								format:'Y-m-d H:i:s',
								hidden:true
								//format :'Y-m-d\\TH:i:s'
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
			                    xtype:'displayfield',
			                    fieldLabel: '变更单号',
			                    name: 'bgCode'
			                }, {
						    	border:false,
						    	layout: {
							        type: 'table',
							        columns: 2
							    },
						    	items : [{
						    		xtype: 'displayfield',
							        fieldLabel: '订单编号<font color=red>*</font>',
							        name: 'orderCode',
//							        labelWidth : 120,
							        width: 210,
							        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
							        value: '',
							        renderer : function(v) { 
								    	return "<a href=\"javascript:showSaleWin('" + this.value + "')\">" + this.value + "</a>"; 
								    }
						    	},{
									xtype : 'button',
									text : '...',
									id : 'openSaleWindow'//,iconCls:'table_save'
								}]
				            },{
						    	xtype: 'displayfield',
						        fieldLabel: '申请客户<font color=red>*</font>',
						        name: 'clients',
						        width: 210,
						        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
						        value: '',
						        renderer : function(v) { 
									    return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
							  	}
				            },
				            bgTypeCombobox,
				            {
				            	xtype:'textfield',
			                    fieldLabel: '订单类型',
			                    name: 'orderType',
			                    hidden:true
				            },
				            {
			                    xtype:'textfield',
			                    fieldLabel: '联系人',
			                    name: 'contacts'
			                },{
			                    xtype:'textfield',
			                    fieldLabel: '联系电话',
			                    name: 'tel',
			                    vtype:'phonecheck'
			                },
//			                orderStatusCombobox,
			                {
			                    xtype:'displayfield',
			                    fieldLabel: '状态',
			                    name: 'orderStatus',
			                    colspan: 2,
			                    value: '',
						        renderer : function(v) {
			                			if(v=="A"){
			                				return "起草";
			                			}else if(v=="B"){
			                				return "待审核";
			                			}else if(v=="C"){
			                				return "已审核";
			                			}
									    return ""; 
							  	}
			                },
			                {
					            xtype: 'textareafield',
					            name: 'reason',
					            fieldLabel: '原因',
					            colspan: 2,
					            //value: 'Textarea value',
					            //fieldStyle : 'background-color: #D0DEF0;',
					            cols:100
							},
							{
					            xtype: 'textareafield',
					            name: 'remarks',
					            fieldLabel: '备注',
					            colspan: 2,
					            //value: 'Textarea value',
					            //fieldStyle : 'background-color: #D0DEF0;',
					            cols:100
							}
						]
			    	   
				 });
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.BgGridView',
						store : Ext.create("SMSWeb.store.bg.Bg2Store"),
						itemId:'bgGrid',
						enableKeyNav : true,
						columnLines : true,
						title: '产品明细',
						tbar : [{
							xtype : 'button',
				            text: '增加产品',
				            iconCls: 'table_add',
				            id : 'addBgItem',
				            
				        }, {
				        	xtype : 'button',
				            id: 'removeBgItem',
				            text: '删除产品',
				            iconCls: 'table_remove',
				            disabled: true
				        }],
				        border:false,
				        viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
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
									 /*{text:'操作',xtype:'actioncolumn',align:'center',width:38,menuDisabled:true,items:[{
											icon:'/resources/images/remarks1.png'
											,tooltip:'编辑'
											,handler:function(grid,rowIndex,colIndex){
												this.up('window').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
											}
									 }]},*/
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
									 {text:'行号',dataIndex:'posex',width:100,menuDisabled:true},
									 {text:'产品编号',dataIndex:'matnr',width:100,menuDisabled:true},
									 {text:'类型',dataIndex:'mtart',width:100,menuDisabled:true},
									 {text:'描述',dataIndex:'maktx',width:300,menuDisabled:true},
							         {text:'数量',dataIndex:'amount',width:100,menuDisabled:true, xtype: 'numbercolumn',align:'right',
							        	 renderer: function(value){
									        return parseInt(value);
									 }},
							         {text:'投影面积',dataIndex:'touYingArea',width:100,menuDisabled:true,align:'right',hidden:true},
						             {text:'总价',dataIndex:'totalPrice',width:100, xtype: 'numbercolumn',menuDisabled:true,hidden:true},
//						             {text:'订单状态',dataIndex:'status',width:100,menuDisabled:true},
							         {text:'物料id',dataIndex:'materialHeadId',width:0,hidden:true,menuDisabled:true},
							         {text:'我的物品ID',dataIndex:'myGoodsId',width:0,hidden:true,menuDisabled:true},
							         {text:'是否标准产品',dataIndex:'isStandard',width:0,hidden:true,menuDisabled:true}
						           ],
					//	selType: 'rowmodel',
					//	plugins: [rowEditing]
						selType : 'cellmodel',
//						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
//								{
//									enableKeyNav : true,
//									clicksToEdit : 2
//						})],
						listeners: {
					            'selectionchange': function(view, records) {
									Ext.getCmp("removeBgItem").setDisabled(!records.length);
					            }
					    }
				});
				/*itemGrid客户子表信息 end*/
				
				//生成页面
				Ext.apply(this, {
					items : [form,itemGrid]
				});
				
				//加载数据
				if(me.formId!=null){
					form.load({
						url:'main/bg/findById',
						params : {
							id : me.formId
						},
						method : 'GET',
						success:function(f,action){
							var result = Ext.decode(action.response.responseText);
							//alert(result.success);alert(result.data.orderCode);
							
							form.getForm().findField("orderCode").setValue(result.data.orderCode);
							form.getForm().findField("orderCode").initValue();
							
							form.getForm().findField("clients").setValue(result.data.clients);
							form.getForm().findField("clients").initValue();
							
							var bgId = form.getForm().findField("id").getValue();
							itemGrid.getStore().load({params:{'pid':bgId/*me.formId*/}});
				        
							var formValues = form.getValues();
							if(result.data.orderStatus=="A"){
								Ext.getCmp("submitBg").show();
							}else if(result.data.orderStatus=="B"){
								Ext.getCmp("saveBg").hide();
								Ext.getCmp("addBgItem").hide();
								Ext.getCmp("removeBgItem").hide();
								Ext.getCmp("openSaleWindow").hide();
								Ext.getCmp("submitBg2").show();
								Ext.getCmp("resubmitBg").show();
								Ext.Object.each(formValues, function(key, value, myself) {
									form.getForm().findField(key).setReadOnly(true);
								});
							}else if(result.data.orderStatus=="C"){
								Ext.getCmp("saveBg").hide();
								Ext.getCmp("addBgItem").hide();
								Ext.getCmp("removeBgItem").hide();
								Ext.getCmp("openSaleWindow").hide();
								Ext.Object.each(formValues, function(key, value, myself) {
									form.getForm().findField(key).setReadOnly(true);
								});
							}
							if("2"==queryBgType || "3"==queryBgType){
								Ext.getCmp("submitBg2").hide();
								Ext.getCmp("resubmitBg").hide();
							}else if("1"==queryBgType){
								Ext.getCmp("saveBg").hide();
								Ext.getCmp("submitBg").hide();
								Ext.getCmp("addBgItem").hide();
								Ext.getCmp("removeBgItem").hide();
								Ext.getCmp("openSaleWindow").hide();
								Ext.Object.each(formValues, function(key, value, myself) {
									form.getForm().findField(key).setReadOnly(true);
								});
							}
						},
				        failure:function(form,action){
				        	var result = Ext.decode(action.response.responseText);
				            Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
				        }
					});
				}
				this.callParent(arguments);
			}
		});
