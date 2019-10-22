package com.mw.framework.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mw.framework.context.SpringContextHolder;

public class MessageUtils {
	public static boolean sendMsg(String smsCode,String tel, String msg) {
		try {
			//if(true){
				//jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,status) values( ? ,? ,systimestamp,0)",new Object[] { tel, msg });
				//return true;
			//}
			//List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			Map<String, String> map = new HashMap<String, String>();
			//jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,status) values( ? ,? ,systimestamp,0)",new Object[] { tel, msg });
			
			String endpoint = "http://172.16.3.204:8080/axis/services/SMSService?wsdl";
			String operationName = "sendSMS";
			String targetNameSpace = "http://172.16.3.204:8080/axis/services/SMSService";
			Service service = new Service();
			Call call = (Call) service.createCall();
			call.setTargetEndpointAddress(endpoint);
			call.setOperationName(new QName(targetNameSpace, operationName));
			call.setReturnClass(String.class);
			call.addParameter("smsCode", XMLType.XSD_INT,
					ParameterMode.IN);
			call.addParameter("tel", XMLType.XSD_STRING,
					ParameterMode.IN);
			call.addParameter("params", XMLType.SOAP_ARRAY,
					ParameterMode.IN);
			call.setUseSOAPAction(true);
			call.setSOAPActionURI(targetNameSpace + operationName);
			JSONObject obj=JSONObject.parseObject(msg);
			List<String> valuesList=new ArrayList<String>();
			for(String key:obj.keySet()){
				valuesList.add(obj.getString(key));
			}
			String[] params=new String[valuesList.size()];
			valuesList.toArray(params);
			String callResultStr = (String) call.invoke(new Object[] { Integer.parseInt(smsCode),tel,params });
			JSONObject resultObject=JSONObject.parseObject(callResultStr);
			String _result= resultObject.getString("errMsg");
//			if(result==null){
//				return false;
//			}else{
				
				//JSONObject success=(JSONObject) _result.get("msg");
				return "OK".equals(_result);
//			}
			//return Boolean.parseBoolean(result);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
