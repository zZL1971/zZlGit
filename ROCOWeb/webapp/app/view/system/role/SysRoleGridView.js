Ext.define("MeWeb.view.system.role.SysRoleGridView", {
			extend : 'Ext.tree.Panel',
			alias : 'widget.roleTree',
			xtype : 'role.Tree',
			store : 'system.role.SysRoleStore',
			useArrows : true,
			columnLines: true,
			rowLines:true,
			viewConfig:{
				/*getRowClass:function(record,index,rowParams,store){
					if(index%2!=0){
						return "ux-grid-column-level2";
					}else{
						return "";
					}
				},
				overCls:'ux-grid-row-over'*/
				stripeRows:true
			},
			//margin : '5 5 5 5',
			//title : '系统角色',
			dockedItems: [{
		        xtype: 'toolbar',
		        dock: 'top',
		        items: [{
		        	xtype:'button',
		        	itemId:'ua_save',
		        	text:'添加',
		        	iconCls:'table_add',
		        	handler: function(event, toolEl){
		        		
					}
		        }]
		    }],
			/*viewConfig : {
				loadMask : new Ext.LoadMask(this, {
							msg : '正在加载数据...'
						}),
				getRowClass : function(record, rowIndex, rowParams, store) {
					if (rowIndex % 2 == 0) {
						return '';
					} else {
						return 'x-grid-td';
					}
				}
			},*/
			rootVisible : false,
			columns : [{
						xtype : 'treecolumn',
						text : '角色名称',
						dataIndex : "descZhCn",
						width : 150,
						sortable : true
					}, {
						text : '操作',
						xtype : 'actioncolumn',
						align : 'center',
						width : 38,
						items : [{
							icon : '/resources/images/remarks1.png',
							tooltip : '授权管理',
							getClass : function(v, metadata, model, rowIndex,
									colIndex, store) {
								return "ux-actioncolumn-margin";
							},
							handler : function(tree, rowIndex, colIndex) {
								this.up('treepanel').fireEvent(
										'itemEditButtonClick', tree, rowIndex,
										colIndex);
							}
						}]
					}, {
						text : '备注',
						dataIndex : 'remark',
						flex : 1
					}, {
						text : "创建时间",
						dataIndex : 'createTime',
						width : 150,
						xtype : 'datecolumn',
						format : 'Y-m-d H:i:s'
					}, {
						text : "更新时间",
						dataIndex : 'updateTime',
						width : 150,
						xtype : 'datecolumn',
						format : 'Y-m-d H:i:s'
					}]
		});