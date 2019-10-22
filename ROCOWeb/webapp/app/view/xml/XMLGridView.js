Ext.define("SMSWeb.view.xml.XMLGridView", {
	extend : 'Ext.grid.Panel',
	alias : 'widget.XMLGridView',
	store : 'xml.XMLGridStore',
	id:'xmlGrid',
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
					 {text:"销售分类",dataIndex:'saleFor',width:100,value:'0',
							editor:{
								xtype:"dictcombobox",
								dict:"SALE_FOR",
								allowBlank : false
							},
							renderer: function(value,metadata,record){
								if(record.data.id=="") {
									record.data.saleFor="0";
									return saleFor.getStore().getById("0").get('text');
								}
					 	    return saleFor.getStore().getById(value).get('text');
					 }},
					 {text:'文本编码',dataIndex:'textCode',width:110,menuDisabled:true},
					 {text:'类型',dataIndex:'type',width:120,menuDisabled:true},
					 {text:'类型描述',dataIndex:'typeDesc',width:180,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
					 {text:'控制文本(双击添加内容)',dataIndex:'text',width:200,menuDisabled:true,listeners:{
						 dblclick:function(grid ,t ,line,cl){
							 Ext.create('SMSWeb.view.xml.AddXMLContextGrid',{
								 record:grid.getStore().getAt(line)
							 }).show();
						 }
					 }},
					 {text:'文本描述',dataIndex:'textDesc',width:200,menuDisabled:true,editor:{xtype:'textfield',selectOnFocus:true}},
					 {text:'最近修改人',dataIndex:'updateUser',width:100,menuDisabled:true},
					 {text:'最近修改日期',dataIndex:'updateTime',width:130,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
					 {text:'文本创建日期',dataIndex:'createTime',width:130,menuDisabled:true,xtype:'datecolumn',format :'Y-m-d'},
					 {text:'计数器',dataIndex:'counter',width:220,menuDisabled:true,hidden:true},
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
			store:'xml.XMLGridStore',
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
				url:"xmlcontrol/update",
				method:"POST",
				params:{
						id:id,
						name:fieldName,
						val:value
				},
				dataType:"JSON",
				contentType : 'application/json',
				success:function(response,opts){
					//var values=Ext.decode(response.responseText);
					var xmlGridView = Ext.ComponentQuery.query("XMLGridView")[0];
					
					//Ext.MessageBox.alert(values.errorCode,values.errorMsg);
					xmlGridView.getStore().loadPage(1);
				},
				failure:function(response,opts){
					Ext.Msg.alert("can't",'error');
				}
			});
		}
	}
});
