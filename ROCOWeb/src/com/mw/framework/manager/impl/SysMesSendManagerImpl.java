package com.mw.framework.manager.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.apache.axis.client.Call;
import org.apache.axis.encoding.XMLType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.alibaba.fastjson.JSONObject;
import com.main.domain.sys.SysJobPool;
import com.main.manager.SalePrModManager;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysMesSendDao;
import com.mw.framework.domain.SysMesSend;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.websocket.handler.SystemWebSocketHandler;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import nl.justobjects.pushlet.core.Session;
import nl.justobjects.pushlet.core.SessionManager;

@Service("sysMesSendManager")
@Transactional
public class SysMesSendManagerImpl extends CommonManagerImpl implements
		SysMesSendManager {

	@Autowired
	private SysMesSendDao sysMesSendDao;
	
	@Bean
    public SystemWebSocketHandler systemWebSocketHandler() {
        return new SystemWebSocketHandler();
    }

	@Override
	public void saveSysMesSend(String title, String content, String sendUser,
			Set<String> userSet) {
		Date curr = new Date();
		if (userSet != null && userSet.size() > 0) {
			List<SysMesSend> sysMesSendList = new ArrayList<SysMesSend>();
			for (String user : userSet) {
				SysMesSend sysMesSend = new SysMesSend();
				sysMesSend.setMsgType("1");
				sysMesSend.setMsgTitle(title);
				sysMesSend.setMsgBody(content);
				sysMesSend.setSendUser(sendUser);
				sysMesSend.setSendTime(curr);
				sysMesSend.setHasRead("0");
				sysMesSend.setReceiveUser(user);
				sysMesSendList.add(sysMesSend);
				systemWebSocketHandler().sendMessageToUser(sendUser, new TextMessage(content));
			}
			sysMesSendDao.save(sysMesSendList);
		}
	}

	/* (non-Javadoc)
	 * @see com.mw.framework.manager.SysMesSendManager#sendUser(java.lang.String, java.lang.String, java.lang.String, java.util.Set)
	 */
	@Override
	public void sendUser(String title, String content, String sendUser,
			Set<String> userSet) {
		Date curr = new Date();
		if (userSet != null && userSet.size() > 0) {
			List<SysMesSend> sysMesSendList = new ArrayList<SysMesSend>();
			for (String user : userSet) {
				SysMesSend sysMesSend = new SysMesSend();
				sysMesSend.setMsgType("1");
				sysMesSend.setMsgTitle(title);
				sysMesSend.setMsgBody(content);
				sysMesSend.setSendUser(sendUser);
				sysMesSend.setSendTime(curr);
				sysMesSend.setHasRead("0");
				sysMesSend.setReceiveUser(user);
				sysMesSendList.add(sysMesSend);
				systemWebSocketHandler().sendMessageToUser(user, new TextMessage(content));
			}
			sysMesSendDao.save(sysMesSendList);
		}
	}
 
	@Override
	public void sendAllUser(String title, String content, String sendUser) {
		LinkedHashMap<String,WebSocketSession> users = systemWebSocketHandler().getUsers();
		Date curr = new Date();
		List<SysMesSend> sysMesSendList = new ArrayList<SysMesSend>();
		
		//websocket用户列表
		for (Entry<String, WebSocketSession> user : users.entrySet()) {
			System.out.println("websocket-"+user.getKey());
			SysMesSend sysMesSend = new SysMesSend();
			sysMesSend.setMsgType("1");
			sysMesSend.setMsgTitle(title);
			sysMesSend.setMsgBody(content);
			sysMesSend.setSendUser(sendUser);
			sysMesSend.setSendTime(curr);
			sysMesSend.setHasRead("0");
			sysMesSend.setReceiveUser(user.getKey());
			sysMesSendList.add(sysMesSend);
		}
		
		//pushlet用户列表
		Session[] sessions = SessionManager.getInstance().getSessions();
		for (int i = 0; i < sessions.length; i++) {
			System.out.println("pushlet-"+sessions[i].getId());
			SysMesSend sysMesSend = new SysMesSend();
			sysMesSend.setMsgType("1");
			sysMesSend.setMsgTitle(title);
			sysMesSend.setMsgBody(content);
			sysMesSend.setSendUser(sendUser);
			sysMesSend.setSendTime(curr);
			sysMesSend.setHasRead("0");
			sysMesSend.setReceiveUser(sessions[i].getId());
			sysMesSendList.add(sysMesSend);
		}
		systemWebSocketHandler().sendMessageToUsers(new TextMessage(content));
		sysMesSendDao.save(sysMesSendList);
		
	}
	 
	@Override
	public void sendUser(String title, String content, String sendUser,String receiveUser,Boolean isRead) {
		SysMesSend sysMesSend = new SysMesSend();
		sysMesSend.setMsgType("1");
		sysMesSend.setMsgTitle(title);
		sysMesSend.setMsgBody(content);
		sysMesSend.setSendUser(sendUser);
		sysMesSend.setSendTime(new Date());
		sysMesSend.setHasRead(isRead?"1":"0");
		sysMesSend.setReceiveUser(receiveUser);
		
		sysMesSendDao.save(sysMesSend);
		systemWebSocketHandler().sendMessageToUser(receiveUser, new TextMessage(content));
	}
	@Override
	public void sendUser(String content, String sendUser, String receiveUser,Boolean isRead) {
		sendUser("推送消息", content, sendUser, receiveUser,isRead);
	}

	@Override
	public void sendUser(String title, String content, String sendUser,String receiveUser) {
		sendUser(title, content, sendUser, receiveUser,false);
	}

	@Override
	public void sendUser(String content, String sendUser, String receiveUser) {
		sendUser(content, sendUser, receiveUser,false);
	}

	/**
	 * 财务确认后数据处理
	 * 
	 * @param jdbcTemplate
	 * @param orderCode
	 */
	@Override
	public void sendMsg2Cus(JdbcTemplate jdbcTemplate,SysJobPool sysJobPool,String value) {
		try {
			String orderCode=sysJobPool.getZuonr();
			String sql = "select h.sap_order_code,h.order_code,h.shou_da_fang,h.order_total from sale_header h where h.order_code='"
					+orderCode + "'";
			List<Map<String, Object>> sap = jdbcTemplate.queryForList(sql);
			// 更新客户返点
			SalePrModManager spm = SpringContextHolder.getBean("salePrModManager");
			spm.updateCust(orderCode);
			// 订单金额
			String order_total = sap.get(0).get("ORDER_TOTAL").toString();

			// 剩余额度
			Double balanceMount = Double.parseDouble(value) - Double.parseDouble(order_total);
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");

			// 发送短信
			try {
				List<Map<String, String>> list = new ArrayList<Map<String, String>>();
				List<Map<String, Object>> _list = jdbcTemplate.queryForList(
						"select tel from sys_user where kunnr=(select sh.shou_da_fang from sale_header sh where order_code='"
								+ orderCode
								+ "') and money='1' and status='1' and (tel is not null) and instr(kunnr, 'LZ') = 0 ");
				if (_list != null && _list.size() > 0) {
					for (int i = 0; i < _list.size(); i++) {
						String telephone = _list.get(0).get("TEL").toString();
						String smsCode = "73966";
						String endpoint = "http://172.16.3.204:8080/axis/services/SMSService?wsdl";
						String operationName = "sendSMS";
						String targetNameSpace = "http://172.16.3.204:8080/axis/services/SMSService";
						org.apache.axis.client.Service service = new org.apache.axis.client.Service();
						Call call = (Call) service.createCall();
						call.setTargetEndpointAddress(endpoint);
						call.setOperationName(new QName(targetNameSpace, operationName));
						call.setReturnClass(String.class);
						call.addParameter("smsCode", XMLType.XSD_INT, ParameterMode.IN);
						call.addParameter("tel", XMLType.XSD_STRING, ParameterMode.IN);
						call.addParameter("params", XMLType.SOAP_ARRAY, ParameterMode.IN);
						call.setUseSOAPAction(true);
						call.setSOAPActionURI(targetNameSpace + operationName);
						String[] params = new String[] { sap.get(0).get("ORDER_CODE").toString(), order_total, value,
								df.format(balanceMount) };
						String callResultStr = (String) call
								.invoke(new Object[] { Integer.parseInt(smsCode), telephone, params });
						JSONObject resultObject = JSONObject.parseObject(callResultStr);
						String _result = resultObject.getString("errMsg");
						String messageContext="您好，您当前订单%s，共：%s元，扣款前余额：%s元，扣款后余额：%s元，请知悉！如有疑问请联系本司财务人员，谢谢！广东劳卡家具有限公司";
						if("OK".equals( _result)){
							jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,ORDER_CODE,status,mes_type) values( ? ,? ,systimestamp,?,'1',?)",new Object[]{telephone,String.format(messageContext, sysJobPool.getZuonr(),order_total,value,df.format(balanceMount)),sysJobPool.getZuonr(),smsCode});
						}else{
							jdbcTemplate.update("insert into mes_post_log(telephone,msg,send_time,ORDER_CODE,status,mes_type) values( ? ,? ,systimestamp,?,'0',?)",new Object[]{telephone,String.format(messageContext, sysJobPool.getZuonr(),order_total,value,df.format(balanceMount)),sysJobPool.getZuonr(),smsCode});
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
}
