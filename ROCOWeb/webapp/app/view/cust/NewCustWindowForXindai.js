Ext.tip.QuickTipManager.init();
var CustXindaiStore=Ext.create("SMSWeb.store.cust.CustXindaiStore");
Ext.define('SMSWeb.view.cust.NewCustWindowForXindai', {
	extend : 'Ext.window.Window',
	alias : 'widget.NewCustWindowForXindai',
	border : false,
	//layout:'fit',
	autoScroll: true,
	formId:null,
	width:1100,
	maximizable:true,
	height:500,
	modal:true,
	constrainHeader: true,
	closable : true,
	layout:'border',
	itemId : 'NewCustWindowForXindai',
	id:'NewCustWindowForXindai',
	tbar:[{
		xtype : 'button',
		text: '查询信贷明细',
		iconCls: 'table_search',
		itemId : 'findXindai',
		id:'findXindai',
		listeners:{
			click : function( bt, e, eOpts ) {
				var me =this;
				var bool = checkXindai();
				if(!bool){
					CustXindaiStore.load({params:{
						startDate:$("input[name='startDate1']").val(),
						endDate:$("input[name='endDate1']").val(),
						kunnr:$("input[name='kunnr']").val()
					}
					});
				}else{
					return;
				}
			}
		}
	},{
    	xtype : 'buttontransparent',
    	text : '导出',
    	id:'export1',
    	icon:'/resources/images/down.png',
    	//hidden: "1"==queryType ?false:true
    	hidden:false,
    	listeners:{
    		click:function(){
				var tmpgrid = Ext.getCmp('custXindai');
				Ext.MessageBox.confirm("温馨提示", "导出到Excel", function (btn) {
					if(btn=="yes"){
						//用grid导出excel
						ExportExcelByGrid(tmpgrid);
					}
				});
			}
    	}
    }],
	initComponent : function() {
		var me = this;

		var xinDai = Ext.widget('grid',{
			extend : 'Ext.grid.Panel',
			alias : 'widget.CustXindaiView',
			store : CustXindaiStore,
			itemId:'custXindai',
			id:'custXindai',
			enableKeyNav : true,
			columnLines : true,
			border : false,
			style:'border-top:1px solid #C0C0C0;',
			selModel:{selType:'checkboxmodel',injectCheckbox:0},
			region:'center',
			viewConfig:{
				enableTextSelection:true //可以复制单元格文字
			},
			tbar :[{
				xtype:'datefield',
				fieldLabel: '订单日期从',
				name: 'startDate1',
				id:'startDate1',
				format :'Y-m-d',
				allowBlank: false,
			},{
				xtype:'datefield',
				fieldLabel: '到',
				name: 'endDate1',
				format :'Y-m-d',
				id:'endDate1',
				allowBlank: false,
			}],
			columns : [ {xtype:'rownumberer',width:50,align:'right'},
			            {text:'过账日期',dataIndex:'skDate',width:120,menuDisabled:true},
			            {text:'凭证号/订单号',dataIndex:'orderNum',width:150,menuDisabled:true},
			            {text:'项目文本',dataIndex:'xmText',width:120,menuDisabled:true},
			            {text:'收款金额',dataIndex:'skMoney',width:120,menuDisabled:true},
			            {text:'下单金额',dataIndex:'xdMoney',width:120,menuDisabled:true},
			            {text:'余额',dataIndex:'yeMoney',width:120,menuDisabled:true}
			            ],
			            dockedItems:[{
			            	xtype:'pagingtoolbar',
			            	store:CustXindaiStore,
			            	dock:'bottom',
			            	displayInfo:true,
			            	displayMsg:"显示 {0} -{1}条，共{2} 条",
			            	border:false,
			            	items:['-','每页',{
			            		xtype:'combobox',
			            		editable : false,
			            		width:55,
			            		listeners:{
			            			'render':function(comboBox){ 
			            				var grid = comboBox.ownerCt.ownerCt.items.items[0];
			            				comboBox.setValue(this.store.pageSize);
			            			},
			            			'select':function(comboBox){ 
			            				var grid = comboBox.ownerCt.ownerCt.items.items[0];
			            				grid.getStore().pageSize = comboBox.getValue();
			            				grid.getStore().load({params:{start:0,limit:comboBox.getValue()}});
			            			}
			            		},
			            		store:Ext.create('Ext.data.Store',{
			            			fields:['id','name'],
			            			data:
			            				[
			            				 {'id':25,'name':25},
			            				 {'id':50,'name':50},
			            				 {'id':100,'name':100},
			            				 {'id':200,'name':200},
			            				 {'id':500,'name':500}
			            				 ]
			            		}),
			            		displayField:'name',
			            		valueField:'id'
			            	},'条']
			            }]
		});
		//生成页面
		Ext.apply(this, {
			items : [xinDai]
		});
		this.callParent(arguments);
	}
});

function checkXindai(){
	var errMsg = "";
	var startDate=$("input[name='startDate1']").val();
	var endDate=$("input[name='endDate1']").val();
	var kunnr=$("input[name='kunnr']").val();
	if(kunnr==null || Ext.String.trim(kunnr)==""){
		errMsg += "编号不可为空<br/>";
	}
	if(startDate==null || Ext.String.trim(startDate)==""){
		errMsg += "请选择开始时间<br/>";
	}
	if(endDate==null || Ext.String.trim(endDate)==""){
		errMsg += "请选择结束时间<br/>";
	}
	if(errMsg!=""){
		Ext.MessageBox.alert('提示信息',errMsg);
		return true;
	}
}
