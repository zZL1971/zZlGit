<%@ page language="java" import="java.util.*,com.mw.framework.domain.*,com.main.domain.cust.*,com.mw.framework.bean.Constants" pageEncoding="UTF-8"%>

<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String basePathWs = request.getServerName()+":"+request.getServerPort()+path+"/";
Object language = request.getSession().getAttribute("language");
Object currentUser = request.getSession().getAttribute("CURR_USER_ID");
Object flowResource = request.getAttribute("flowResource");
SysUser sysUser = (SysUser)request.getSession().getAttribute("CURR_USER");
//经销商的橱柜下单权限
Object zzcupboard=request.getSession().getAttribute(Constants.CURR_USER_KUNNR_CUP);
//经销商的木门下单权限 ZZTIMBER
Object zztimber=request.getSession().getAttribute(Constants.CURR_USER_KUNNR_TIMBER);

String userName = sysUser.getUserName();
String kunnr = "";
String kunnrs = "";
String name1 = "";
String tel = "";
String ktokd = "";
String ktokds = "";
String telnum = "";
boolean isMoney = false;
String userRole = "";
List<String> user_role_id = new ArrayList<String>();
if(sysUser!=null){
	isMoney = sysUser.isMoney();
	
	Set<SysRole> userRoleCla = sysUser.getRoles();

	Iterator<SysRole> uu = userRoleCla.iterator();
	for(int i = 0;i<userRoleCla.size()&& uu.hasNext() ;i++){
	   String id =  uu.next().getId();
	   user_role_id.add(id);
	  // System.out.println("id="+id);
	}
	
	CustHeader custHeader = (CustHeader)sysUser.getCustHeader();
	if(custHeader!=null){
		if(custHeader.getKunnr() != null && !custHeader.getKunnr().trim().equals(""))
			kunnr = custHeader.getKunnr();
		if(custHeader.getKunnrS() != null && !custHeader.getKunnrS().trim().equals("")){
			kunnrs = custHeader.getKunnrS();
			CustHeader custHeaderS = custHeader.getCustHeaderS();
			if(custHeaderS!=null){
				if(custHeaderS.getKtokd() != null && !custHeaderS.getKtokd().trim().equals("")){
					ktokds = custHeaderS.getKtokd();
				}
			}
		}
		if(custHeader.getName1() != null && !custHeader.getName1().trim().equals(""))
			name1 = custHeader.getName1();
		if(custHeader.getTel() != null && !custHeader.getTel().trim().equals(""))
			tel = custHeader.getTel();
		if(custHeader.getKtokd() != null && !custHeader.getKtokd().trim().equals(""))
			ktokd = custHeader.getKtokd();
		if(custHeader.getTelnum() != null && !custHeader.getTelnum().trim().equals(""))
			telnum = custHeader.getTelnum();
	}
}
%>
<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>${moduleTitle } - ROCO 门店接单系统v1.0</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<link rel="shortcut icon" href="/resources/images/favicon.ico" type="image/vnd.microsoft.icon">
	<link rel="icon" href="/resources/images/favicon.ico" type="image/vnd.microsoft.icon">
	<script type="text/javascript" src="/security/security.js"></script>
	<script type="text/javascript" src="/jquery/jquery-1.11.1.min.js"></script>
	<script type="text/javascript" src="/websocket/sockjs-0.3.4.min.js"></script>
	<script type="text/javascript">
		var CURR_USER_LOGIN_NO ="<%=currentUser%>";
		
		var CURR_USER_LOGIN_USER_NAME ="<%=userName%>";
		var CURR_USER_KUNNR ="<%=kunnr%>";
		//经销商橱柜下单权限和木门下单权限
		var CURR_USER_KUNNR_CUP="<%=zzcupboard%>";
		var CURR_USER_KUNNR_TIMBER="<%=zztimber%>";
		
		var FLOW_RESOURCE="<%=flowResource%>";
		var basePath = "<%=basePath%>";
		var KUNNR="<%=kunnr%>";
		var KUNNRS = "<%=kunnrs%>";
		var KUNNR_NAME1="<%=name1%>";
		var KUNNR_TEL="<%=tel%>";
		var KUNNR_TYPE="<%=ktokd%>";
		var TEL_NUM ="<%=telnum%>";
		var KUNNRS_TYPE="<%=ktokds%>";
		var IS_MONEY = "<%=isMoney%>";
		var USER_ROLE_ID="<%=user_role_id%>";
		
		RSAUtils.setMaxDigits(200);
		function k(k,x,y){var a=new RSAUtils.getKeyPair(x,"",y);var b=RSAUtils.encryptedString(a,k);return b}	
	 		
		var requiredLabel = "<span style='color:red;font-weight:bold' data-qtip='必输项'>*</span>";
		
		var Sys = {};
		var ua = navigator.userAgent.toLowerCase();
		var s,x;
		
		USER_AGENT = ua.toLowerCase();
		
        /*(s = ua.match(/msie ([\d.]+)/)) ? Sys.browser = "Internet Explorer "+s[1] :
        (s = ua.match(/firefox\/([\d.]+)/)) ? Sys.browser ="Firefox "+s[1] :
        (s = ua.match(/chrome\/([\d.]+)/)) ? Sys.browser ="Chrome " +s[1] :
        (s = ua.match(/opera.([\d.]+)/)) ? Sys.browser ="Opera " +s[1] :
        (s = ua.match(/baidubrowser\/([\d.]+)/)) ? Sys.browser ="百度浏览器 " +s[1] :
        (s = ua.match(/ucbrowser\/([\d.]+)/)) ? Sys.browser ="UC浏览器 " +s[1] :
        (s = ua.match(/version\/([\d.]+).*safari/)) ? Sys.browser = "Safari "+s[1] : 0;
        (s = ua.match(/trident/))?Sys.browser = "Internet Explorer "+ua.match(/rv:([\d.]+)/)[1] : 0;
        
        (x = ua.match(/windows nt ([\d.]+)/)) ? Sys.system = "Windows "+x[1]+(ua.indexOf("wow64")!=-1?"(x64)":"(x86)"):
        (x = ua.match(/iphone; cpu iphone os ([\d._]+)/i)) ? Sys.system ="iPhone "+x[1].replace(/_/g,".") :
        (x = ua.match(/ipad; cpu os ([\d._]+)/i)) ?Sys.system="iPad "+x[1].replace(/_/g,"."):
        (x = ua.match(/android ([\d.]+)/)) ? Sys.system ="Android " +x[1] :0;
        
		function systemCode(a){
			if(a.indexOf("Windows")>-1){
				
			}else if(a.indexOf("iPhone")>-1){
				
			}else if(a.indexOf("iPad")>-1){
				
			}else if(a.indexOf("Android")>-1){
				
			}
		}*/
	</script>
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/resources/ext-theme-neptune/ext-theme-neptune-all.css"/>
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/src/ux/grid/css/RangeMenu.css"/>
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/src/ux/grid/css/GridFilters.css"/>
	<link rel="stylesheet" type="text/css" href="/extjs/4.2.1/src/ux/css/TabScrollerMenu.css"/>
	<link rel="stylesheet" type="text/css" href="/resources/font-awesome/4.3.0/css/font-awesome.min.css"/>
	<script type="text/javascript" src="/extjs/4.2.1/bootstrap.js"></script>
	<script type="text/javascript" src="/extjs/4.2.1/locale/ext-lang-zh_CN.js"></script>
	<script type="text/javascript" src="/locale/core-lang-<%=language%>.js"></script>
	<script type="text/javascript" charset="UTF-8" src="/supcan/binary/dynaload.js?87"></script>
	<script type="text/javascript" charset="UTF-8" src="/supcan/binary/dynaload.init.js?87"></script>
	<script type="text/javascript" src="ajax-pushlet-client.js"></script>
	<!-- <script src="/js/CLodopfuncs.js?priority=1"></script>
	<script src="/js/CLodopfuncs.js?priority=0"></script>
    <script src="/js/LodopFuncs.js?20"></script>
	<object id="LODOP_OB" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA" width=0 height=0>
			<embed id="LODOP_EM" type="application/x-print-lodop" width=0 height=0></embed>
	</object>-->
	<script type="text/javascript">
	Ext.setGlyphFontFamily('FontAwesome');
	var basePathWs = '<%=basePathWs%>';
	//var LODOP=getLodop(document.getElementById('LODOP_OB'),document.getElementById('LODOP_EM'));
	
	</script>
	<script type="text/javascript" src="/app/index/${module }.js?20"></script>
	<script type="text/javascript" src="/js/gridToExcel.js?20"/></script>
	<style type="text/css">
		tr.z-c-grid-red .x-grid-td{
			background-color: rgba(255,0,0,0.4)
		}
	</style>
  </head>
  
  <body>
    <div id="div1"></div>
  </body>
</html>
