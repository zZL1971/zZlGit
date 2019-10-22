Ext.define("SMSWeb.controller.print.PrintController", {
	extend : 'Ext.app.Controller',
	id : 'printController',
	refs : [{
				ref:'BarCodePrint',
				selector:'BarCodePrint'
			}
	],
	views : ['print.BarCodePrint'],
	models: ['print.BarCodePrintModel'],
	stores: ['print.BarCodePrintStore'],
	requires : ["Ext.ux.form.DictCombobox", "Ext.ux.grid.UXGrid","Ext.ux.ButtonTransparent","Ext.ux.form.TableComboBox"],
	init : function() {
		var meC=this;
		this.control({
			"BarCodePrint button[itemId=query]":{
				click: function(bt,e,eOpts){
					var me=this;
					var _barCodePrint=me.getBarCodePrint();
					var _store=_barCodePrint.queryById("BarCodeDataGrid").getStore();
					var _form=_barCodePrint.queryById("queryForm");
					_store.loadPage(1,{params:_form.getValues()});
				}
			},
			"BarCodePrint button[itemId=print]":{
				click: function(bt,e,eOpts){
					var me=this;
					var _barCodePrint=me.getBarCodePrint();
					var _barCodeGrid=_barCodePrint.queryById("BarCodeDataGrid");
					Ext.MessageBox.confirm("温馨提示", "打印标签", function (btn) {
						if(btn=="yes"){
							var _selecteds=_barCodeGrid.getSelectionModel().getSelection();
							var _ids=[];
							for(var _index=0;_index<_selecteds.length;_index++){
								_ids[_index]=_selecteds[_index].data.ID;
							}
							if(_ids.length==0){
								Ext.Msg.alert("提示","请选中需要打印的数据!");
								return;
							}
							Ext.Ajax.request({
								url:'main/mm/getBarCodeMsg',
								method:'POST',
								params:{ids:_ids},
								async:false,
								success:function(data,opts){
									var _responseText=Ext.decode(data.responseText);
									var _dataArray=_responseText.data;
									if(_responseText.success){
									LODOP.PRINT_INIT("");
									LODOP.SET_PRINT_PAGESIZE (1,"8cm","5cm","");
									LODOP.SET_PRINT_STYLE("FontSize",6);
									for(var _index=0;_index<_dataArray.length;_index++){
										LODOP.NewPageA();
										LODOP.ADD_PRINT_TEXT("0.5cm","3.8cm","1.3cm","0.2cm","补打标签");
										LODOP.ADD_PRINT_TEXT("1cm","10mm","2.6cm","0.2cm",_dataArray[_index].BARCODE);
										LODOP.ADD_PRINT_TEXT("1cm","5.7cm","2.0cm","0.0cm",_dataArray[_index].MATNAME);
										LODOP.ADD_PRINT_TEXT("1.5cm","10mm","3cm","0.2cm",_dataArray[_index].SIZZ);
										LODOP.ADD_PRINT_TEXT("1.5cm","5.5cm","2.5cm","0.2cm",_dataArray[_index].NAME);
										if(_dataArray[_index].CNC_BARCODE1)
											LODOP.ADD_PRINT_BARCODE("2cm","1cm","2.5cm","2.5cm","QRCode",_dataArray[_index].CNC_BARCODE1);
										if(_dataArray[_index].CNC_BARCODE2)
											LODOP.ADD_PRINT_BARCODE("2cm","5cm","2.5cm","2.5cm","QRCode",_dataArray[_index].CNC_BARCODE2);
										LODOP.ADD_PRINT_TEXT("4.3cm","10mm","6cm","0.2cm","订单:"+_dataArray[_index].ORDER_CODE);
										LODOP.ADD_PRINT_TEXT("4.7cm","10mm","6cm","0.2cm",_dataArray[_index].MAKTX);
									}
									LODOP.PREVIEW();
									}else{
										Ext.MessageBox.alert(_responseText.errorCode,_responseText.errorMsg);
									}
								}
							});
						}
					});
				}
			}
		});
	}
});


