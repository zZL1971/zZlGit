Ext.define("SRM.view.trie.Grid",{
	extend:'Ext.grid.Panel',
	title : '字典',//标题
	alias: 'widget.trieGrid',
	xtype:'trie.Grid',
	store:'trie.GridStore',
	autoScroll:true,
	requires:["Ext.ux.TreePicker","Ext.ux.grid.feature.Searching","Ext.ux.grid.FiltersFeature","Ext.ux.form.DictCombobox","Ext.ux.ToolbarDroppable","Ext.ux.BoxReorderer"],
	viewConfig:{
	    enableTextSelection:true
	},
	plugins: [
        Ext.create('Ext.grid.plugin.CellEditing', {
            clicksToEdit: 2
        }) 	
    ],
    features: [{
            ftype: 'searching',
            minChars: 1,
            minLength:1,
            width: 200,
            mode: 'remote',//远程还是本地store
            //position: 'top/bottom',//状态栏还是工具栏
            position: 'top',//状态栏还是工具栏
            iconCls: 'table_look',//图标
            //menuStyle: 'checkbox/radiobox',//单选还是多选
            menuStyle: 'radiobox',//单选还是多选
            showSelectAll: true,   //是否显示全选按钮
            checkIndexes: ["keyVal"],//默认是否选择所有的列
          	disableIndexes: ["trieTree.id", "orderBy", "createTime", "updateTime"]//禁止那些列参与查询
        },{
        	ftype: 'filters',
        	encode: true,
        	menuFilterText:'过滤器',
        	filters: [{  
	            type: 'boolean',  
	            dataIndex: 'visible'  
            }]
        }
    ],        
	//selType: 'cellmodel',
	frame : false,//面板渲染
	//columns :
	
	
	/*tbar :[
				{ xtype: 'button', text: 'Button 1' },'->'
	],*/	
	dockedItems :[{
				xtype:'pagingtoolbar',
				store:'trie.GridStore',
				dock:'bottom',
				displayInfo:true,
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
				            {'id':500,'name':500}//,
				            //{'id':1000,'name':1000}
				        ]
				    }),
				    displayField:'name',
				    valueField:'id'
				},'条',/*{
		            text: 'Encode: ' + (encode ? 'On' : 'Off'),
		            tooltip: 'Toggle Filter encoding on/off',
		            enableToggle: true,
		            handler: function (button, state) {
		                var encode = (grid.filters.encode !== true);
		                var text = 'Encode: ' + (encode ? 'On' : 'Off'); 
		                grid.filters.encode = encode;
		                grid.filters.reload();
		                button.setText(text);
		            } 
		        },
		        {
		            text: 'Local Filtering: ' + (local ? 'On' : 'Off'),
		            tooltip: 'Toggle Filtering between remote/local',
		            enableToggle: true,
		            handler: function (button, state) {
		                var local = (grid.filters.local !== true),
		                    text = 'Local Filtering: ' + (local ? 'On' : 'Off'),
		                    newUrl = local ? url.local : url.remote,
		                    store = grid.view.getStore();
		                 
		                // update the GridFilter setting
		                grid.filters.local = local;
		                // bind the store again so GridFilters is listening to appropriate store event
		                grid.filters.bindStore(store);
		                // update the url for the proxy
		                store.proxy.url = newUrl;
		
		                button.setText(text);
		                store.load();
		            } 
		        },
		        {
		            text: 'All Filter Data',
		            tooltip: 'Get Filter Data for Grid',
		            handler: function () {
		                var data = Ext.encode(grid.filters.getFilterData());
		                Ext.Msg.alert('All Filter Data',data);
		            } 
		        },*/{
		            text: '清空过滤数据',
		            iconCls:'table_remove',
		            handler: function () {
		                this.ownerCt.ownerCt.filters.clearFilters();
		            } 
		        }]
	},{
		xtype: 'trie.quickForm',
		dock: 'top',
		hidden:true,
		weight: 101,
        bodyStyle: {
            'background-color': '#E4E5E7'
        }
	},{
		xtype: 'toolbar',
	    dock: 'top',
	    items: [
	        {xtype:'button',id:'add',text:'添加',iconCls:'table_add'},
			{xtype:'button',id:'delete',text:'删除',iconCls:'table_remove'},
			{xtype:'button',id:'save',text:'保存',iconCls:'table_save'},
			{xtype:'button',id:'sort',text:'多列排序',iconCls:'table_look',hidden:true},'-',
			{xtype:'button',id:'simpledit',text:'快速表单',iconCls:'table_edit',disabled:false},
			{xtype:'button',id:'demo',text:'测试SSM',iconCls:'table_look',hidden:true},
			{xtype:'button',id:'demo2',text:'grid下拉不可编辑',iconCls:'table_look',hidden:true},
			{xtype:'button',id:'demo3',text:'流程测试',iconCls:'table_search',hidden:true},
			{xtype:'checkbox',id:'simpleditchk',boxLabel:'查询',hidden:true},'-','->'
	    ]
	},{
		xtype:'toolbar',
		dock:'top',
		itemId:'sorttbar',
		hidden:true,
		items:[
			{ xtype: 'button',text: 'Button 1' },'->'
		]
	}],
	//selType:'checkboxmodel',//设定选择模式
	selModel:{
		selType:'checkboxmodel',
		injectCheckbox:1
	},
	multiSelect:true,
	initComponent:function(){
		var me = this;
		var statOr_ = Ext.widget('dictcombobox',{name:'statOr',dict:'T_OR_F'});
		me.columns = [ //列模式
						{xtype:'rownumberer',width:30,align:'right'},
						{text:'操作',xtype:'actioncolumn',align:'center',width:38,items:[{
							icon:'/resources/images/remarks1.png'
							,tooltip:'编辑'
							,handler:function(grid,rowIndex,colIndex){
								this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
							}
							/*,handler: function(grid, rowIndex, colIndex) {
		                    	var record = grid.getStore().getAt(rowIndex);
								Ext.create('SRM.view.trie.DictForm',{dictId:record.getId()}).show(grid);
		                	}*/
						}]},
						{text:"是否启用",dataIndex:'stat',width:100,editor:statOr_,renderer: function(value,metadata,record){ 
							return statOr_.getStore().getById(value).get('text');
						}},
						{text:"字典编码",dataIndex:'keyVal',width:100,editor:{xtype:'textfield',selectOnFocus:true},filter:{type: 'string'}},
						{text:"类型",dataIndex:'type',width:100,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
						{text:"类型描述",dataIndex:'typeDesc',width:100,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
						{text:"类型值",dataIndex:'typeKey',width:100,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
						{text:"中文描述",dataIndex:'descZhCn',width:100,editor:{xtype:'textfield',selectOnFocus:true}},
						{text:"英文描述",dataIndex:'descEnUs',width:100,editor:{xtype:'textfield',selectOnFocus:true}},
						{text:"繁体描述",dataIndex:'descZhTw',width:100,
							editor:{
								xtype:'textfield',
								allowBlank:true,selectOnFocus:true
							}
						},
						{text:"排序号",dataIndex:'orderBy',width:100,field:{xtype:'numberfield',minValue: 0,selectOnFocus:true},filter:{type: 'numeric'}},
						{text:"父节点",dataIndex:'trieTree.id',itemId:'trieTreeId',width:100,flex:1,editor:{
							xtype:'treepicker',
							editable : false,
							displayField: 'descZhCn',
							//valueField:"ID",
							rootVisible: false,
							//minPickerHeight: 200,
							value:this.dataIndex,
							store: Ext.create('Ext.data.TreeStore',{
								fields: ['id','descZhCn'],
								storeId:'trieTreeStore',
								root: {
									text: '32323232',
									expanded: true
								},
								//model : 'SRM.model.trie.TreeModel',
								proxy: {
									type: 'ajax',
									url: '/core/trie/tree/root',
									reader: {
										type: 'json'
									},listeners:{  
								        exception:function( proxy, response, operation, eOpts ) { 
								           var res= Ext.JSON.decode(response.responseText);
								           if(!res.success){
								           		Ext.Msg.show({title: '【'+res.errorCode+'】-错误提示', msg: res.errorMsg, buttons: Ext.Msg.OK, icon: Ext.Msg.ERROR});
								           }
								        }  
								    }
								}
							})
						},renderer:function(value,metaData,record,rowIndex,columnIndex,stroe,view){
							//console.log(value);
							return Ext.data.StoreManager.lookup('trieTreeStore').getById(value).get("descZhCn");
						}},
						{text:"创建时间",dataIndex:'createTime',width:150,xtype: 'datecolumn',format:'Y-m-d H:i:s'},
						{text:"更新时间",dataIndex:'updateTime',width:150,xtype: 'datecolumn',format:'Y-m-d H:i:s'}
			];
		
		me.callParent(arguments);
	}
});