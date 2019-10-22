Ext.define("SMSWeb.controller.report.ReportController", {
	extend : 'Ext.app.Controller',
	refs:[{
    	ref : 'reportView',
		selector : 'reportView'
    },{
    	ref : 'reportFormView',
		selector : 'reportFormView'
    }],
	init : function() {
		this.control({
			"reportView button[id=queryReport]" : {
				click:function(view,collndex,rowindex,item,e,record){
					var me = this;
					var form = me.getReportView().getComponent("reportFormView");
					
					try{
						AF.func("Load", "/core/report/d/"+reportNo+"?"+form.getSearchSerialize());
					}catch(e){
						var $agnt=navigator.userAgent.toLowerCase();
						if($agnt.indexOf("chrome")>0){
							window.open("supcan/install_chrome.htm");
						}else{
							window.location = "/supcan/binary/supcan.xpi";
						}
						
					}
					
				}
			},
			"reportView button[id=mergecell]" : {
				click:function(view,collndex,rowindex,item,e,record){
					for(col=AF.func("GetNextValidCol", ""); col!=""; col=AF.func("GetNextValidCol", col)) {
						var userProps = AF.func("GetColUserProps",col);
						var userProp = userProps.split(",");
						for(var i=0;i<userProp.length;i++){
							if(userProp[i]=="mergecell"){
								var mergeCellStatus = AF.func("GetColProp",col+"\r\nmergecell");
								if(mergeCellStatus=="true"){
									AF.func("mergeSame", "col="+AF.func("GetColProp",col+"\r\nname"));
								}
							}
						}
					}
				}
			},
			"reportView button[id=demerge]" : {
				click:function(view,collndex,rowindex,item,e,record){
					AF.func("demerge","");
				}
			}
		})
	}
});