Ext.tip.QuickTipManager.init();
Ext
		.define(
				'SMSWeb.view.mm.base.NewMaterialBaseFBWindow',
				{
					extend : 'Ext.window.Window',
					alias : 'widget.NewMaterialBaseFBWindow',
					requires : [ "Ext.ux.ButtonTransparent",
							"Ext.ux.form.TrieCombobox","Ext.ux.form.DictCombobox"],
					itemId : 'newMaterialBaseFBWindow_ItemId',
					sourceShow : null,//显示来源
					uuId : null,//非标产品Id
					kunnr:null,
					ordeType:null,
					maximizable : true,
					canModify : null,//能否修改保存
					height : 480,
					width : 820,
					bodyStyle : {
						'background-color' : '#f6f6f6'
					},
					saleFor : '0',
					border : 0,
					modal : true,
					border : false,
					layout : 'border',
					tbar : [ {
						xtype : 'button',
						text : '保存',
						itemId : 'saveFBMaterial',
						iconCls : 'table_save'
					} ],
					initComponent : function() {
						var me = this;
						//保存按钮不能使用 如果设置为不能修改
						me.tbar[0].hidden = (this.canModify == null ? false
								: this.canModify);
						me.items = [
								{
									xtype : 'form',
									id : 'FBMaterialForm',
									region : 'north',
									bodyStyle : {
										'background-color' : '#f6f6f6'
									},
									bodyPadding : 5,
									border : false,
									fieldDefaults : {
										labelAlign : 'right',
										labelWidth : 100,
										msgTarget : 'qtip'
									},
									defaultType : 'textfield',
									layout : 'anchor',
									defaults : {
										anchor : '100%'
									},
									items : [ {
										xtype : 'fieldset',
										title : '基本信息',
										style : {
											'border-style' : 'solid',
											'border-color' : '#d5d5d5',
											'background-color' : '#ffffff'
										},
										collapsible : true,
										defaultType : 'textfield',
										layout : 'anchor',
										defaults : {
											anchor : '100%'
										},
										items : [
												{
													xtype : 'fieldcontainer',
													layout : 'hbox',
													combineErrors : true,
													defaultType : 'textfield',
													items : [
															{
																readOnly : true,
																fieldStyle : 'background:#E6E6E6',
																xtype : 'textfield',
																name : 'id',
																fieldLabel : 'id',
																hidden : true,
																maxLength : 32
															},
															/*{
																xtype : 'dictcombobox',
																name : 'saleFor',
																fieldLabel : '销售方式',
																dict : 'SALE_FOR',
																readOnly : true,
																hidden : true,
																value : this.saleFor,
																showDisabled : false
															},*/
															{
																xtype : 'textfield',
																name : 'serialNumber',
																fieldLabel : '编号',
																readOnly : true,
																hidden : true,
																fieldStyle : 'background:#E6E6E6'
															},
															{
																readOnly : true,
																fieldStyle : 'background:#E6E6E6',
																xtype : 'textfield',
																name : 'groes',
																hidden : true,
																fieldLabel : '规格',
																maxLength : 32
															} ]
												},
												{

													xtype : 'fieldcontainer',
													layout : 'hbox',
													combineErrors : true,
													defaultType : 'textfield',
													items : [
															{
																id:'saleItemId',
																xtype : 'dictcombobox',
																labelStyle : 'padding-left:15px;',
																fieldLabel : '产品组<font color=red>*</font>',
																name : 'saleFor',
																dict:'SALE_FOR',
																value:this.saleFor,
																allowBlank : false,
																showDisabled:false,
																emptyText : '请选择...',
																blankText : '请选择产品组',
																listeners:{
																	change:function(obj ,newValue ,oldValue ,Opts){
																		if(newValue!=oldValue){
																			me.saleFor = newValue;
																			var matklStore = me.queryById("matkl_Id").getStore();
																			var textrue = me.queryById("textrue_Id");
																			var matklPath = me.saleFor == "1" ? 'MATERIAL_MATKL_CUP'
																					: me.saleFor == "3"?'MATERIAL_MATKL_MM':'MATERIAL_MATKL';
																			var textruePath = me.saleFor == "1" ? 'TEXTURE_OF_MATERIAL_CUP'
																					: me.saleFor == "3"?'MM_TEXTURE':'TEXTURE_OF_MATERIAL';
																			matklStore.load({url:"/core/dd/list3/"+matklPath});
																			textrue.trie = textruePath;
																			textrue.reset();
																			textrue.getStore().load({url:"/core/trie/nodes/"+textruePath+"/KEY_VAL/"});
																		}
																	}
																}
															},
															{
																id:'matkl_Id',
																xtype : 'dictcombobox',
																labelStyle : 'padding-left:15px;',
																showDisabled:false,
																fieldLabel : '产品分类<font color=red>*</font>',
																name : 'matkl',
																value:me.orderType=="OR4"?'1999':me.orderType=='OR3'?'1999':'',
																dict : this.saleFor == "1" ? 'MATERIAL_MATKL_CUP'
																		: this.saleFor == "3"?'MATERIAL_MATKL_MM':'MATERIAL_MATKL',
																allowBlank : false,
																emptyText : '请选择...',
																blankText : '请选择产品分类'
															} ]

												},
												{

													xtype : 'fieldcontainer',
													layout : 'hbox',
													items : [
															{
																labelStyle : 'padding-left:15px;',
																xtype : 'numberfield',
																name : 'widthDesc',
																fieldLabel : '宽<font color=red>*</font>',
																allowBlank : false,
																maxLength : 10
															},
															{
																xtype : 'numberfield',
																name : 'heightDesc',
																fieldLabel : '高<font color=red>*</font>',
																allowBlank : false,
																maxLength : 10
															},
															{
																xtype : 'numberfield',
																name : 'longDesc',
																fieldLabel : '深<font color=red>*</font>',
																allowBlank : false,
																maxLength : 10
															}

													]

												},
												{

													xtype : 'fieldcontainer',
													layout : 'hbox',
													items : [
															{
																id:'textrue_Id',
																cascadeDict : 'dictcombobox[itemId=fbcolor]',
																allowBlank : false,
																fieldLabel : '材质<font color=red>*</font>',
																name : 'textureOfMaterial',
																showDisabled:false,
																xtype : 'triecombobox',
																trie : this.saleFor == "1" ? 'TEXTURE_OF_MATERIAL_CUP'
																		: this.saleFor == "3"?'MM_TEXTURE':'TEXTURE_OF_MATERIAL',
																emptyText : '请选择...',
																blankText : this.saleFor == "3" ?'请选择产品款式':'请选择材质'
															},
															{

																itemId :'fbcolor',
																cascade : true,
																allowBlank : false,
																labelStyle : 'padding-left:15px;',
																xtype : 'dictcombobox',
																fieldLabel : '颜色<font color=red>*</font>',
																showDisabled : false,
																name : 'color',
																id : 'fbcolor',
																//dict : 'COLOR',
																disabled : true,
																emptyText : '请选择...',
																blankText : '请选择颜色'
															},
															{
																itemId : 'createTime',
																xtype : 'textfield',
																name : 'createTime',
																hidden : true,
																fieldLabel : '创建时间'
															},
															{
																itemId : 'createUser',
																xtype : 'textfield',
																name : 'createUser',
																hidden : true,
																fieldLabel : '创建人'
															},
															{
																itemId : 'fileType',
																xtype : 'textfield',
																name : 'fileType',
																hidden : true,
																fieldLabel : '文件类型'
															},
															{
																itemId : 'saveFB-flag',
																xtype : 'textfield',
																id : 'saveFB-flag',
																name : 'saveFB-flag',
																hidden : true,
																fieldLabel : '保存标记'
															} ]

												}
												/* ,{
													   xtype: 'fieldcontainer',
													   layout: 'hbox',
													   items: [{
															labelStyle : 'padding-left:15px;',
												        	xtype:'dictcombobox',
															fieldLabel : '附件类型',
															name:'fileType',
															id:'fileType',
												//								flex:2,
															dict:'FILE_TYPE',
												//								editable:false,
															allowBlank: false,
															emptyText: '请选择...'
												        	}
														]
												 }
												 */,
												{
													xtype : 'textareafield',
													rows : 2,
													name : 'maktx',
													anchor : '80%',
													fieldStyle : 'background:#E6E6E6',
													readOnly : true,
													hidden : true,
													fieldLabel : '产品描述'
												},
												{
													xtype : 'textareafield',
													rows : 2,
													name : 'zzazdr',
													anchor : '80%',
													allowBlank : false,
													id : 'zzazdr',
													maxLength :20,
													emptyText : '安装位置不得超过20个字..',
													fieldLabel : '安装位置<font color=red>*</font>',
												},
												{
													readOnly : true,
													fieldStyle : 'background:#E6E6E6',
													xtype : 'textfield',
													name : 'FBid',
													hidden : true,
													fieldLabel : '附件ID',
													maxLength : 32
												} ]
									} ]

								},
								{
									xtype : 'grid',
									region : 'center',
									style : {
										margin : '-10px 5px 0 5px'
									},
									id : 'FB_FileGridItem',
									border : false,
									dockedItems : [ {
										xtype : 'toolbar',
										dock : 'left',
										autoScroll : true,
										style : {
											//		                	'border-style':'solid',
										//		                	'border-color':'#d5d5d5',
										'background-color' : '#F9F9FB'
									},
										items : [
												{
													xtype : 'button',
													text : '上传文件',
													iconCls : 'table_add',
													id:"uploadFileCon",
													height : 48,
													iconAlign : 'top',
													handler : function() {
														var id = me.uuId;
														/*if(!me.uuId){
															var form=Ext.getCmp("FBMaterialForm"); 
															id=form.getValues().id;
														}
														if(!id){
															Ext.MessageBox.alert('提示信息','请先保存产品信息！');
															return;
														}*/
														this
																.up('window')
																.fireEvent(
																		'fileUploadButtonClick',
																		"", id);
													}
												},
												{
													xtype : 'button',
													text : '文件失效',
													iconCls : 'table_remove',
													height : 48,
													id:'fileDestroy',
													iconAlign : 'top',
													handler : function() {
														var id = me.uuId;
														if (!me.uuId) {
															var form = Ext
																	.getCmp("FBMaterialForm");
															id = form
																	.getValues().id;
														}
														if (!id) {
															Ext.MessageBox
																	.alert(
																			'提示信息',
																			'请先保存产品信息！');
															return;
														}
														this
																.up('window')
																.fireEvent(
																		'fileDeleteButtonClick',
																		null,
																		this
																				.up('grid'),
																		id);
													}
												} ]
									} ],
									store : Ext
											.create("SMSWeb.store.mm.base.Store4MaterialBaseFile"),
									selModel : {
										selType : 'checkboxmodel',
										injectCheckbox : 0
									},
									columns : [
											{
												text : 'id',
												dataIndex : 'id',
												width : 0,
												hidden : true
											},
											{
												text : '序号',
												width : 50,
												labelAlign : 'left',
												xtype : 'rownumberer'
											},
											{
												text : '文件名称',
												dataIndex : 'uploadFileNameOld',
												width : 200,
												sortable : false,
												menuDisabled : true
											},
											{
												text : '备注',
												dataIndex : 'remark',
												width : 200,
												sortable : false,
												menuDisabled : true
											},
											{
												text : '是否有效',
												dataIndex : 'statusdesc',
												width : 80,
												sortable : false,
												menuDisabled : true
											},
											{
												text : '文件下载',
												width : 100,
												xtype : 'actioncolumn',
												align : 'center',
												icon : '/resources/images/down.png',
												handler : function(grid,
														rowIndex, colIndex) {
													this
															.up('window')
															.fireEvent(
																	'fileDownloadButtonClick',
																	this
																			.up('grid'),
																	rowIndex,
																	colIndex);
												},
												sortable : false,
												menuDisabled : true
											}, {
												text : '上传人',
												dataIndex : 'createUser',
												width : 80,
												sortable : false,
												menuDisabled : true
											}, {
												text : '上传日期',
												dataIndex : 'createTime',
												width : 140,
												sortable : false,
												menuDisabled : true
											} ]

								} ];
						me.listeners = {
							show : function() {
								if (me.uuId) {
									setTimeout(function() {
										loadData(me.uuId)
									}, 100);

									//							loadData(me.uuId);
								}else{
									if(me.uuId == "" ||undefined == me.uuId||me.uuId==null){
										me.queryById("uploadFileCon").setDisabled(true);
										me.queryById("fileDestroy").setDisabled(true);
									}
								}
							}
						}
						function loadData(uuId) {
							if (!uuId) {
								
								return;
							}
							var myMask = new Ext.LoadMask(me, {
								msg : "请稍等..."
							});
							myMask.show();
							Ext.Ajax.request( {
								url : 'main/mm/queryMHAndFJById?id=' + uuId,
								async : true,
								dataType : "json",
								success : function(response, opts) {
									var values = Ext
											.decode(response.responseText);
									if (values.success) {
										var formData = values.data;
										var form = Ext.getCmp("FBMaterialForm")
												.getForm();
										form.setValues(formData);
										/*form.findField("textureOfMaterial").value=formData.textureOfMaterial;
										form.findField("color").value=formData.color;*/
										Ext.getCmp("fbcolor").setValue(formData.color);
										//Ext.getCmp("zzazdr").setValue(formData.zzazdr);
										var grid = Ext
												.getCmp("FB_FileGridItem");
										grid.getStore().load( {
											params : {
												'pid' : formData.id
											},
											callback :function(r,options,success){
												if(success){
													var fileGrid = me.queryById("FB_FileGridItem");
													fileGrid.getStore().filter("statusdesc","有效");
													console.log(fileGrid.getStore().getCount());
													if(fileGrid.getStore().getCount()<=0){
														me.queryById("fileDestroy").setDisabled(true);
													}
													fileGrid.getStore().clearFilter();
												}
											}
										});
										//							var saleItmes=Ext.getCmp("addSaleItemsGrid");
										//							saleItmes.getStore().load({params:{'pid':formData.shId}
										/*,callback: function(records, operation, success) {
											alert();
										        calculationTotalPrice(saleForm,itemGrid,'delete');
										    }*/
										//							});
									} else {
										Ext.MessageBox.alert("提示信息",
												values.errorMsg);
									}

								},
								failure : function(response, opts) {
									Ext.MessageBox.alert("提示信息", "加载失败"
											+ response.responseText);

								}
							});
							if (myMask != undefined) {
								myMask.hide();
							}
						}
						Ext.each(me.items, function(continer) {
							allDisabled(continer, (me.canModify == null ? false
									: me.canModify));
						});
						me.callParent(arguments);
					}

				});

function allDisabled(continer, isdisabled) {
	if (continer != undefined && continer.items != undefined
			&& continer.items.length > 0) {
		Ext.each(continer.items, function(item) {
			allDisabled(item, isdisabled);
		});
	} else {
		if ('grid' != continer.xtype) {
			continer.disabled = isdisabled;
		} else {
			allDisabled(continer.dockedItems[0], isdisabled);
		}
	}
}