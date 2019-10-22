package com.mw.framework.interceptor;

import java.io.IOException;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.headers.Header;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysUser;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import sun.misc.BASE64Decoder;

public class AuthHeaderValidateInterceptor extends AbstractPhaseInterceptor<SoapMessage> {

	public AuthHeaderValidateInterceptor() {
		super(Phase.PRE_INVOKE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handleMessage(SoapMessage messgae) throws Fault {
		// TODO Auto-generated method stub
		long sysTimeLong = System.currentTimeMillis();
		long betweenValue = 60000;
		List<Header> headers = messgae.getHeaders();
		if(headers == null || headers.size() < 1) {
			throw new Fault(new Exception("SOAPHeader 头错误"));
		}
		Element authToken = null;
		for (Header header : headers) {
			QName qname = header.getName();
			String ns = qname.getNamespaceURI();
			String tagName = qname.getLocalPart();
			if(ns != null && ns.equals("http://www.tmp.com/auth/") && tagName != null && tagName.equals("RequestSOAPHeader")) {
				authToken = (Element) header.getObject();
				break;
			}
		}
		if(authToken == null) {
			throw new Fault(new Exception("无授权信息"));
		}
		BASE64Decoder decoder = new BASE64Decoder();
		String auth = authToken.getChildNodes().item(0).getTextContent();
		String decode = null ;
		try {
			decode = new String ( decoder.decodeBuffer(auth));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(decode == null){
			throw new Fault(new Exception("授权信息错误！"));
		}
		JSONArray array=JSONArray.fromObject("["+decode+"]");
		JSONObject obj = array.getJSONObject(0);
		String username = obj.getString("user_id");
		String time = obj.getString("time");
		SysUserDao sysUserDao = SpringContextHolder.getBean("sysUserDao");
		if(username==null||"".equals(username)) {
			throw new Fault(new Exception("授权信息错误！"));
		}
		if(time==null||"".equals(time)) {
			throw new Fault(new Exception("该请求已失效，请尝试重新传输"));
		}
		long timeLong = Long.parseLong(time);
		if((sysTimeLong-timeLong)>betweenValue&&(sysTimeLong-timeLong)<0) {
			throw new Fault(new Exception("该请求已失效，请尝试重新传输"));
		}
		SysUser user = sysUserDao.findOne(username);
		if(user == null) {
			throw new Fault(new Exception("授权信息错误！"));
		}
	}
}
