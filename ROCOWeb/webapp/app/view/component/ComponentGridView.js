Ext.define("SMSWeb.view.component.ComponentGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.ComponentGridView',
	store : 'component.ComponentGridStore',
	id:'componentGrid',
	enableKeyNav : true,
	columnLines : true,
	border : false,
	style:'border-top:1px solid #C0C0C0;',
	requires:["Ext.ux.form.DictCombobox","Ext.ux.grid.feature.Searching","Ext.ux.grid.FiltersFeature"],
	selModel:{selType:'checkboxmodel',injectCheckbox:0},
	viewConfig:{enableTextSelection:true},
	initComponent:function(){
		var me=this;
		var statOr_ = Ext.widget('dictcombobox',{name:'statOr',dict:'YES_NO'});
		var line_ = Ext.widget('dictcombobox',{name:'line',dict:'PRODUCTIONLINE'});
		var saleFor = Ext.widget('dictcombobox',{name:'saleFor',dict:'SALE_FOR'});
		me.columns = [  
					 {xtype:'rownumberer',width:30,align:'right'},
					 {text:'id',dataIndex:'id',width:0,hidden:true,menuDisabled:true},
					 {text:"是否启用",dataIndex:'stat',width:100,
						 editor:{
							 xtype:"dictcombobox",
							 dict:"YES_NO",
							 allowBlank : false
							 },
						 renderer: function(value,metadata,record){
							 if(record.data.id==""){
								 record.data.stat="1";
								 return statOr_.getStore().getById("1").get('text');
							 }
							return statOr_.getStore().getById(value).get('text');
						}},
					{text:"产线",dataIndex:'line',width:140,align:'center',
						 editor:{
							 xtype:"dictcombobox",
							 dict:"PRODUCTIONLINE",
							 allowBlank : false
							 },
						 renderer: function(value,metadata,record){
							return line_.getStore().getById(value).get('text');
					 }},
					 {text:"部件名称",dataIndex:'componentName',width:200,editor:{xtype:'textfield',allowBlank : false,selectOnFocus:true}},
					 {text:'标识码',dataIndex:'identifyCode',width:110,editor:{xtype:'textfield',allowBlank : false,selectOnFocus:true}},
					 {text:'对内交期',dataIndex:'dnDelivery',width:120,
						 editor : {
								xtype : 'numberfield',
								allowBlank : false,
								allowDecimals : false,//不允许输入小数
								allowNegative:false,//不允许输入负数
								minValue : 1
							}
					 },
					 {text:'对外交期',dataIndex:'dwDelivery',width:180,
						 editor : {
								xtype : 'numberfield',
								allowBlank : false,
								allowDecimals : false,//不允许输入小数
								allowNegative:false,//不允许输入负数
								minValue : 1
							}
					 },
					 {text:'补单交期',dataIndex:'repairOrderDelivery',width:200,
						 editor : {
								xtype : 'numberfield',
								allowBlank : false,
								allowDecimals : false,//不允许输入小数
								allowNegative:false,//不允许输入负数
								minValue : 1
							}
					 },
					 {text:'外购标识码',dataIndex:'outSourceIdentifyCode',width:200,editor:{xtype:'textfield',allowBlank : false,selectOnFocus:true}},
					 {text:'物料编码',dataIndex:'materialCode',width:200,editor:{xtype:'textfield',allowBlank : false,selectOnFocus:true}},
					 {text:'最近修改人',dataIndex:'updateUser',width:100},
					 {text:'最近修改日期',dataIndex:'updateTime',width:130,xtype:'datecolumn',format :'Y-m-d'},
					 {text:'文本创建日期',dataIndex:'createTime',width:130,xtype:'datecolumn',format :'Y-m-d'}
		           ]
		me.callParent(arguments);
	},
	selModel:{
		selType:'checkboxmodel',
		injectCheckbox:1
	},
	plugins: [
	          Ext.create('Ext.grid.plugin.CellEditing', {
	              clicksToEdit: 2
	          }) 	
	],
	multiSelect:true,
    dockedItems:[{
			xtype:'pagingtoolbar',
			store:'component.ComponentGridStore',
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
			},'条',{
	            text: '清空过滤数据',
	            iconCls:'table_remove',
	            handler: function () {
	                this.ownerCt.ownerCt.filters.clearFilters();
	            } 
	        }]
		}],
	listeners: {
        'render': function(xmlGrid) {
        },
        edit:function(editor, e, eOpts){
        	//console.log(editor);
        	//console.log(e);
        	var id= e.record.data.id;
        	if(id != undefined && id != ""){
        		this.updateGridData(e.field, e.value, id)
        	}
		}
    },
	updateGridData:function(fieldName,value,id){
		if(id!=""){
			Ext.Ajax.request({
				url:"delivery/updateComponent",
				method:"POST",
				params:{
						id:id,
						name:fieldName,
						val:value
				},
				dataType:"JSON",
				contentType : 'application/json',
				success:function(response,opts){
					var xmlGridView = Ext.ComponentQuery.query("ComponentGridView")[0];
					xmlGridView.getStore().loadPage(1);
				},
				failure:function(response,opts){
					Ext.Msg.alert("can't",'error');
				}
			});
		}
	}
});
