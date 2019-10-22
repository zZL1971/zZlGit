Ext.apply(Ext.form.VTypes,{
	daterange : function(val, field) {
		var date = field.parseDate(val);
		if (!date) {
			return;
		}
		if (field.startDateField && (!this.dateRangeMax || (date.getTime() != this.dateRangeMax.getTime()))) {
			var start = Ext.getCmp(field.startDateField);
			start.setMaxValue(date);
			start.validate();
			this.dateRangeMax = date;
		} else if (field.endDateField&& (!this.dateRangeMin || (date.getTime() != this.dateRangeMin.getTime()))) {
			var end = Ext.getCmp(field.endDateField);
			end.setMinValue(date);
			end.validate();
			this.dateRangeMin = date;
		}
		/*
		 * Always return true since we're only using this vtype to set
		 * the min/max allowed values (these are tested for after the
		 * vtype test)
		 */
		return true;
	}
});

Ext.apply(Ext.form.VTypes, { 
    phonecheck : function(val, field) { 
	    var str=val;
//	    var reg=/(^[0-9]{3,4}\-[0-9]{7,8}$)|(^[0-9]{7,8}$)|(^\([0-9]{3,4}\)[0-9]{3,8}$)|(^0{0,1}13[0-9]{9}$)/;
		var reg=/^(^0\d{2}-?\d{8}$)|(^0\d{3}-?\d{8}$)|(^0\d{3}-?\d{7}$)|(^\(0\d{2}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{8}$)|(^\(0\d{3}\)-?\d{7}$)|^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,1,3,5-8])|(18[0-9])|166|198|199|(147))\d{8}$|^(\(\d{3,4}-\)|\d{3.4}-)?\d{7,8}$/;
	    return reg.test(str);
	},
    phonecheckText : "号码不匹配!"  
});

Ext.tip.QuickTipManager.init();
Ext.define('SMSWeb.view.cust.NewCustWindowInnerContent', {
			extend : 'Ext.panel.Panel',
			alias : 'widget.NewCustWindowInnerContent',
			requires:["Ext.ux.form.DictCombobox"],
			id:"selectCust",
			border : false,
			//layout:'fit',
			autoScroll: true,
			formId:null,
			editFlag: null,
			autoScroll:true,
			initComponent : function() {
//					var ktokdStore = new Ext.data.Store({
//					    fields: ['keyVal','descZhCn'],
//					    proxy:{
//							type:'ajax',
//							url:'main/sale/queryDicd',
//							extraParams :{'keyVal':'KTOKD'},
//							reader:{
//								type:'json',
//								root:'content',
//								totalProperty :'totalElements'
//							},
//							writer:{
//								type:'json'
//							}
//						},
//					    // API only returns 25 by default.
//					    pageSize: 1000,
//					    autoLoad: true
//				});
//
//				var ktokdCombobox = new Ext.form.ComboBox({
//						 name: "ktokd",
//				         fieldLabel: '类型',
//				//         width: 300,
//				         store: ktokdStore,
//				         displayField: 'descZhCn',
//				         valueField: 'keyVal',
//				         triggerAction: 'all',
//				         emptyText: '请选择...',
//				         allowBlank: false,
//				         blankText: '请选择类型',
//				         editable: false,
//				         queryMode: 'local'
//				//        	 ,
//				//         listConfig: {  
//				//		       getInnerTpl: function() {  
//				//		           return '<div data-qtip="{descZhCn} ({keyVal})">{descZhCn} ({keyVal})</div>';  
//				//		       }  
//				//		  }  
//				});
	
				var checkDrawUserStore = new Ext.data.Store({
					    fields: ['id','text'],
					    proxy:{
							type:'ajax',
							url:'main/cust/queryUser',
							//extraParams :{'keyVal':'KTOKD'},
							reader:{
								type:'json',
								root:'content',
								totalProperty :'totalElements'
							},
							writer:{
								type:'json'
							}
						},
					    // API only returns 25 by default.
					    //pageSize: 1000,
					    autoLoad: true
				});
				
				var checkDrawUserCombobox = new Ext.form.ComboBox({
						 name: "checkDrawUser",
				         fieldLabel: '审绘员',
				         store: checkDrawUserStore,
				         displayField: 'text',
				         valueField: 'id',
				         triggerAction: 'all',
				         emptyText: '请选择...',
				         labelStyle : 'padding-left:15px;',
				         //allowBlank: false,
				         //blankText: '请选择审绘员',
				         editable: false,
				         queryMode: 'local'
				});
	
				var fanDianNameCombobox = Ext.create('Ext.ux.form.DictCombobox',{dict:'FAN_DIAN_NAME'});
				 var me = this;
				 var form = Ext.widget('form',{
			    	   itemId:'custForm',
			    	   bodyStyle : "padding-left:10px;padding-top:5px;padding-bottom:5px",//background-color: #D0DEF0;
					   border : false,
					   tbar : [{
							xtype : 'button',
							text : '保存',
							itemId : 'saveCust',
							iconCls:'table_save',
							hidden:false
						},{
							xtype : 'button',
							text : '查询账号明细',
							itemId : 'add_tradeId',
							iconCls:'table_save',
							listeners:{
								'click':function(){
									Ext.create('SMSWeb.view.cust.NewCustWindowForBank',
										     {title:'客户银行信息明细'}).show(this,function(){
										    	 
										     });
								}
							}
						},{
							xtype : 'button',
							text : '查询信贷明细',
							itemId : 'add_custId',
							iconCls:'table_save',
							listeners:{
								'click':function(){
									Ext.create('SMSWeb.view.cust.NewCustWindowForXindai',
										     {title:'客户信贷信息明细'}).show(this,function(){
										    	 
										     });
								}
							}
						}
						],
					   fieldDefaults : {
							labelWidth : 80,
							labelAlign : "left",
							labelStyle : 'padding-left:5px;',
							width:235
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
//					        {
//								xtype:'textfield',
//								name : 'rowStatus'
//					        },
					        {
								xtype:'hiddenfield',
								name : 'bukrs'
					        },
					        {
								xtype:'hiddenfield',
								name : 'vkorg'
					        },
					        {
								xtype:'hiddenfield',
								name : 'vtweg'
					        },
					        {
								xtype:'hiddenfield',
								name : 'spart'
					        },
					        {
								xtype:'hiddenfield',
								name : 'sortl'
					        },
					        {
								xtype:'hiddenfield',
								name : 'building'
					        },
					        {
								xtype:'hiddenfield',
								name : 'street'
					        },
					        {
								xtype:'hiddenfield',
								name : 'pstlz'
					        },
					        {
								xtype:'hiddenfield',
								name : 'mcod3'
					        },
					        {
								xtype:'hiddenfield',
								name : 'land1'
					        },
					        {
								xtype:'hiddenfield',
								name : 'regio'
					        },
					        {
								xtype:'hiddenfield',
								name : 'ort02'
					        },
					        {
								xtype:'hiddenfield',
								name : 'akont'
					        },
					        {
								xtype:'hiddenfield',
								name : 'vkbur'
					        },
					        {
								xtype:'hiddenfield',
								name : 'vkgrp'
					        },
					        {
								xtype:'hiddenfield',
								name : 'waers'
					        },
					        {
								xtype:'hiddenfield',
								name : 'kalks'
					        },
					        {
								xtype:'hiddenfield',
								name : 'taxkd'
					        },
					        
					        
							{
			                    xtype:'textfield',
			                    fieldLabel: '编号',
								allowBlank: false,
			                    name: 'kunnr',
			                    id: 'kunnr',
			                    readOnly:true
			                }, {
			                    xtype:'textfield',
			                    labelStyle : 'padding-left:15px;',
			                    fieldLabel: '名称',
			                    allowBlank: false,
			                    name: 'name1',
			                    readOnly:true
			                },
			                {
					        	xtype:'dictcombobox',
								fieldLabel : '类型',
								emptyText: '请选择...',
				         		allowBlank: false,
								name:'ktokd',
								dict:'KTOKD',
								readOnly:true
					        },
					        {
			                    xtype:'textfield',
			                    labelStyle : 'padding-left:15px;',
			                    fieldLabel: '电话',
			                    name: 'tel',
			                    vtype:'phonecheck'
			                },
			                
			                {
			                    xtype:'textfield',
			                    //labelStyle : 'padding-left:15px;',
			                    fieldLabel: '销售区域',
			                    name: 'bzirk',
			                    readOnly:true
			                },{
			                    xtype:'textfield',
			                    labelStyle : 'padding-left:15px;',
			                    fieldLabel: '农行账号',
			                    name: 'nh'
			                    
			                }, {
			                    xtype:'textfield',
			                   // labelStyle : 'padding-left:15px;',
			                    fieldLabel: '信贷额度',	
			                    name: 'xinDai',
			                    readOnly:true,
			                    fieldStyle:'text-align:right;'
				            },{
			                    xtype:'textfield',
			                    labelStyle : 'padding-left:15px;',
			                    fieldLabel: '建行账号',
			                    name: 'jh'
			                    
			                },{
						    	border:false,
						    	layout: {
							        type: 'table',
							        columns: 2
							    },
						    	items : [{
						    		xtype: 'displayfield',
						    		//labelStyle : 'padding-left:15px;',
							        fieldLabel: '送达方',
							        name: 'kunnrS',
							        fieldStyle : "background-color:white;height:20px;padding-left:4px;"	,
							        value: ''
//							        	,
//							        renderer : function(v) { 
//								    	return "<a href=\"javascript:showCustWin('" + this.value + "')\">" + this.value + "</a>"; 
//								    }
						    	},{
									xtype : 'button',
									text : '...',
									itemId : 'songDaFang_queryCust'
								}]
				            },
				            checkDrawUserCombobox
						]
			    	   
				 });
				
				/*itemGrid客户子表信息 start*/
				var itemGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.Cust2GridView',
						store : Ext.create("SMSWeb.store.cust.Cust2Store"),
						itemId:'custGrid',
						enableKeyNav : true,
						columnLines : true,
						title: '折扣明细',
						border : false,
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
						tbar : [{
									xtype : 'button',
						            text: '增加折扣明细',
						            iconCls: 'table_add',
						            id : 'addItem',
						            hidden:false
						            
						        }, {
						        	xtype : 'button',
						            id: 'removeItem',
						            text: '删除折扣明细',
						            iconCls: 'table_remove',
						            disabled: true,
						            hidden:false
						        }, {
						        	xtype : 'button',
						            id: 'activeItem',
						            text: '激活',
						            iconCls: 'active',
						            disabled: true,
						            hidden:false
						        }, {
						        	xtype : 'button',
						            id: 'activeDisItem',
						            text: '注销',
						            iconCls: 'active_dis',
						            disabled: true,
						            hidden:false
						        }],
						//region:'center',
					//	selModel:{selType:'checkboxmodel',injectCheckbox:0},
					//	dockedItems:[{
					//		xtype:'pagingtoolbar',
					//		store:'cust.Cust2Strore',
					//		dock:'bottom',
					//		border:false,
					//		displayInfo:true
					//		}],
						columns : [
									 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true,editor:{}},
						             {text:'开始日期',dataIndex:'startDate',width:100,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d',editor: {
							                xtype: 'datefield',
							                allowBlank: false,
							                format: 'Y-m-d',//'m/d/Y'
							                minValue: '2006-01-01'
							         }},
							         {text:'结束日期',dataIndex:'endDate',width:100,menuDisabled:true,xtype: 'datecolumn',format :'Y-m-d',editor: {
							                xtype: 'datefield',
							                allowBlank: false,
							                format: 'Y-m-d',//'m/d/Y'
							                minValue: '2006-01-01'
							         }},
							         {text:'返点名称',dataIndex:'fanDianName',width:100,menuDisabled:true
							        	 ,editor: fanDianNameCombobox
							         ,renderer: function(value,metadata,record){
							 				var find= fanDianNameCombobox.store.findRecord("id",value);
						 	            	if(find){
						 	            		return find.get('text');
						 	            	}else{
						 	            		return value;
						 	            	}
							         }},
						             {text:'折扣',dataIndex:'zheKou',width:60,menuDisabled:true,xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0,
							                maxValue: 1
							         }},
						             {text:'待返点总金额',dataIndex:'total',width:100,menuDisabled:true,xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0,
							                maxValue: 100000000
							         }},
							         {text:'预计已返点金额',dataIndex:'yuJi',width:110,menuDisabled:true,xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0,
							                maxValue: 100000000
							         }},
							         {text:'实际已返点金额',dataIndex:'shiJi',width:110,menuDisabled:true,xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0,
							                maxValue: 100000000/*,
							                hidden:true*/
							         }},
						             {text:'待返剩余金额',dataIndex:'shengYu',width:130,menuDisabled:true,xtype: 'numbercolumn',align:'right',editor: {
							                xtype: 'numberfield',
							                allowBlank: false,
							                minValue: 0,
							                maxValue: 100000000
							         }},
							         {text:'是否激活',dataIndex:'status',width:100,menuDisabled:true,
							        	renderer: function(value,metadata,record){
							 				if(value=="1"){
					 	            			return "是";
					 	            		}else if(value=="0"){
					 	            			return "否";
					 	            		}
							                return value;  
							         }}
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
									Ext.getCmp("removeItem").setDisabled(!records.length);
									Ext.getCmp("activeItem").setDisabled(!records.length);
									Ext.getCmp("activeDisItem").setDisabled(!records.length);
					            },
								'beforeedit': function( editor, context, eOpts ) {
					            	var startDateEditor = {
							                xtype: 'datefield',
							                allowBlank: false,
							                format: 'Y-m-d',//'m/d/Y'
							                minValue: '2006-01-01'
							        };
					            	var endDateEditor = {
							                xtype: 'datefield',
							                allowBlank: false,
							                format: 'Y-m-d',//'m/d/Y'
							                minValue: '2006-01-01'
							        };
					            	var startDate = context.record.get("startDate");
					            	var endDate = context.record.get("endDate");
					            	if(startDate!=null && startDate!=""){
					            		endDateEditor['minValue'] = Ext.Date.format(startDate, 'Y-m-d');
					            	}
					            	if(endDate!=null && endDate!=""){
					            		startDateEditor['maxValue'] = Ext.Date.format(endDate, 'Y-m-d');
					            	}
					            	if(context.rowIdx>0){
					            		var store = context.grid.getStore();
					            		var perRecord = store.getAt(context.rowIdx-1);
					            		var perEndDate = perRecord.get("endDate");
					            		startDateEditor['minValue'] = Ext.Date.format(perEndDate, 'Y-m-d');
					            		if(context.rowIdx != store.getCount()-1){
					            			var nextRecord = store.getAt(context.rowIdx+1);
					            			var nextStartDate = nextRecord.get("startDate");
					            			endDateEditor['maxValue'] = Ext.Date.format(nextStartDate, 'Y-m-d');
					            		}
					            	}
									Ext.Array.each(context.grid.columns, function(column) {
										if(column.dataIndex=="startDate"){
											column.setEditor(startDateEditor);
											//column.editor=startDateEditor;
										}
										if(column.dataIndex=="endDate"){
											column.setEditor(endDateEditor);
											//column.editor=endDateEditor;
										}
									});
					            }
					    }
				});
				/*itemGrid客户子表信息 end*/
				
				var contactsGrid = Ext.widget('grid',{
						extend : 'Ext.grid.Panel',
						alias : 'widget.CustContactsGridView',
						store : Ext.create("SMSWeb.store.cust.CustContactsStore"),
						itemId:'custContactsGrid',
						enableKeyNav : true,
						columnLines : true,
						title: '联系人',
						border : false,
						viewConfig:{
						    enableTextSelection:true //可以复制单元格文字
						},
//						tbar : [{
//									xtype : 'button',
//						            text: '增加联系人',
//						            iconCls: 'table_add',
//						            id : 'addContacts'
//						            ,hidden:false
//						            
//						        }, {
//						        	xtype : 'button',
//						            id: 'removeContacts',
//						            text: '删除联系人',
//						            iconCls: 'table_remove',
//						            disabled: true
//						            ,hidden:false
//						        }],
						//region:'center',
						columns : [
									 {text:'id',dataIndex:'id',width:0,menuDisabled:true,hidden:true},
									 {text:'联系人',dataIndex:'parnr',width:100,menuDisabled:true
//										 ,editor: {
//							                allowBlank: false
//							         	}
									 },
									 {text:'名字',dataIndex:'namev',width:100,menuDisabled:true
//										 ,editor: {
//							                allowBlank: true
//							         	}
									 },
									 {text:'部门',dataIndex:'abtnr',width:100,menuDisabled:true
//										 ,editor: {
//							                allowBlank: true
//							         	}
									 },
									 {text:'电话',dataIndex:'telNumber',width:100,menuDisabled:true
//										 ,editor: {
//							                allowBlank: false
//							         	}
									 },
									 {text:'称谓',dataIndex:'anred',width:100,menuDisabled:true
//										 ,editor: {
//							                allowBlank: true
//							         	}
									 }
						           ],
						selType : 'cellmodel',
						plugins : [cellEditing = Ext.create('Ext.grid.plugin.CellEditing',
								{
									enableKeyNav : true,
									clicksToEdit : 2
						})]
//						,
//						listeners: {
//					            'selectionchange': function(view, records) {
//									Ext.getCmp("removeContacts").setDisabled(!records.length);
//					            }
//					    }
				});
				
				//生成页面
				Ext.apply(this, {
					//autoScroll: true,
					items : [
						form,
						{
							xtype:'tabpanel',
						    itemId:'centerTabpanel',
			//			    bodyStyle : "border-left:0;border-right:0;border-bottom:0;",
						    border : false,
						    plain : false,
						    listeners: {
						        beforetabchange: function(tabs, newTab, oldTab) {
						           //tabChange(tabs, newTab, oldTab);
						        }
						    },
						    items:[
						    	itemGrid,contactsGrid
						    ]
						}]
				});
				
				
				//加载数据
				if(me.formId!=null){
					form.load({
						url:'main/cust/findById',
						params : {
							id : me.formId
						},
						method : 'GET',
						success:function(f,action){
							var custId = form.getForm().findField("id").getValue();
//							alert(custId);alert(me.formId);
							itemGrid.getStore().load({params:{'pid':custId/*me.formId*/}});
							contactsGrid.getStore().load({params:{'pid':custId}});
							//form.getForm().findField("tradeAccno").setValue("");
						},
				        failure:function(form,action){
				        	var result = Ext.decode(action.response.responseText);
				            Ext.Msg.alert('提示'+result.errorCode,"失败原因是: "+result.errorMsg);  
				        }
					});
				}
				if(me.editFlag==false){
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
					Ext.Array.each(contactsGrid.columns, function(column) {
						if("undefined"!=typeof(column.editor)){
							column.editor = null;
						}
					});
					//Ext.getCmp("saveCust").hidden=true;
//					Ext.getCmp("addContacts").hidden=true;
//					Ext.getCmp("removeContacts").hidden=true;
					Ext.getCmp("addItem").hidden=true;
					Ext.getCmp("removeItem").hidden=true;
					Ext.getCmp("activeItem").hidden=true;
					Ext.getCmp("activeDisItem").hidden=true;
					
				}
				
				//LJ开头的帐户，_02,_03,_04外部客户不能查看折扣明细
				var front = CURR_USER_LOGIN_NO.substr(0,2);//前两位置LJ
				var after = CURR_USER_LOGIN_NO.substr(CURR_USER_LOGIN_NO.length-3,CURR_USER_LOGIN_NO.length);//不等于_01
				if(front =='lj' && (after != '_01') ){
					Ext.ComponentQuery.query('#custGrid')["0"].hidden = true;
				}
				
				this.callParent(arguments);
			}
		});

function tabChange(tabs, newTab, oldTab){
	if(newTab.title=="联系人"){
		Ext.getCmp("removeContacts").show();
		Ext.getCmp("addContacts").show();
		Ext.getCmp("removeItem").hide();
		Ext.getCmp("addItem").hide();
	}else{
		Ext.getCmp("removeContacts").hide();
		Ext.getCmp("addContacts").hide();
		Ext.getCmp("removeItem").show();
		Ext.getCmp("addItem").show();
	}
}

