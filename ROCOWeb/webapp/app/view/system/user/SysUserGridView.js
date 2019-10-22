Ext.define("MeWeb.view.system.user.SysUserGridView", {
			extend : 'Ext.grid.Panel',
			alias : 'widget.userGrid',//别称
			xtype : 'user.Grid',//声明自定义组件名称
			store : 'system.user.SysUserStore',//引入数据层
			//useArrows : true,
			//columnLines: true,
			//bodyStyle : "border-left:none;border-right:none;",
			//selModel:{
			//	selType:'checkboxmodel',
			//	injectCheckbox:1
			//},
			//requires :["Ext.ux.form.DictCombobox"],
			//multiSelect:true,
			initComponent:function(){
			
				var me = this;
				//初始化grid表头数据
				/*Ext.Ajax.request({
					url:'/core/ext/grid/SYS_USER_QUERY',
					async:false,
					dataType: "json",
					success:function(response,opts){
						var cls = Ext.decode(response.responseText);
						me.columns= cls;
					},
					failure:function(response,opts){
						Ext.Msg.alert("can't",'error');
					}
				});*/
				
				
				me.columns=[ //列模式
					{xtype:'rownumberer',width:30,align:'right'},
					{text:'操作',xtype:'actioncolumn',align:'center',width:38,items:[{
						icon:'/resources/images/remarks1.png'
						,tooltip:'编辑'
						,handler:function(grid,rowIndex,colIndex){
							this.up('grid').fireEvent('itemEditButtonClick',grid,rowIndex,colIndex);
						}
					}]},
					{text:"登陆账号",dataIndex:'loginNo',width:100,editor:{xtype:'textfield',selectOnFocus:true},filter:{type: 'string'}},
					{text:"用户编号",dataIndex:'userNo',width:100,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
					{text:"用户名",dataIndex:'userName',width:100,editor:{xtype:'textfield',selectOnFocus:true},filterable:true},
					{text:"用户类型",dataIndex:'userType',width:100,editor:{xtype:'textfield',selectOnFocus:true}},
					{text:"用户组",dataIndex:'userGroup',width:100,editor:{xtype:'textfield',selectOnFocus:true}},
					{text:"性别",dataIndex:'sex',width:100,editor:Ext.create('Ext.ux.form.DictCombobox',{dict:'SEX',editable:false}),
					renderer:Ext.ux.DataFactory.getComboboxForColumnRenderer},
					{text:"创建时间",dataIndex:'createTime',width:150,xtype: 'datecolumn',format:'Y-m-d H:i:s'},
					{text:"更新时间",dataIndex:'updateTime',width:150,xtype: 'datecolumn',format:'Y-m-d H:i:s'}
				];
				
				me.plugins= [
			        Ext.create('Ext.grid.plugin.CellEditing', {
			        	pluginId : 'cellEditing',
			            clicksToEdit: 1
			        }) 	
			    ];
				me.callParent(arguments);
			}
		});