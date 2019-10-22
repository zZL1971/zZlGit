/**
 *
 */
package com.mw.framework.activiti.tasklistener.csord;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.activiti.engine.task.IdentityLink;
import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.main.dao.SaleHeaderDao;
import com.main.domain.sale.SaleHeader;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.redis.RedisUtils;
import com.mw.framework.sap.jco3.SAPConnect;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 * 财务确认节点监听器
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.activiti.tasklistener.csord.FinanceCompleteListener.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-24
 *
 */
public class FinanceCompleteListener implements TaskListener {
	private static final long serialVersionUID = 1L;

	@Override
	public void notify(DelegateTask delegatetask) {
		String uuid = (String) delegatetask.getVariable("uuid");
		RedisUtils redisUtils=SpringContextHolder.getBean("redisUtils");
		SaleHeaderDao saleHeaderDao = SpringContextHolder.getBean("saleHeaderDao");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");

		String uSql="UPDATE SALE_HEADER H SET H.SAP_CREATE_DATE=SYSDATE WHERE H.ID='"+uuid+"'";
		jdbcTemplate.update(uSql);
//		new Exception("财务确认节点-后台推送数据到SAP，如没有需要请删除这个异常信息");
        //流程缓存
		String taskId=delegatetask.getId();
		Set<IdentityLink> set=delegatetask.getCandidates();
		String groupId = null;
		for (IdentityLink identityLink : set) {
			groupId=identityLink.getGroupId();
		}
		Object assignee = delegatetask.getAssignee();
		String nextflow = (String) delegatetask.getVariable("nextflow");
		if (nextflow.startsWith("flow_rt_")) {
			String value = "0";
			// 订单金额
			SaleHeader saleHeader = saleHeaderDao.findOne(uuid);
			String order_code=saleHeader.getOrderCode();
			if(saleHeader==null) {
				return;
			}
			String order_total = String.valueOf((saleHeader.getOrderTotal()!=null?saleHeader.getOrderTotal():0.0));
			// 查询信贷
			JCoDestination connect = SAPConnect.getConnect();
			JCoFunction function2;
			try {
				function2 = connect.getRepository().getFunction(
						"ZRFC_SD_XD01");
				function2.getImportParameterList().setValue("P_KKBER", "3000");
				JCoTable sKunnrTable = function2.getTableParameterList()
						.getTable("S_KUNNR");
				sKunnrTable.appendRow();
				sKunnrTable.setValue("SIGN", "I");
				sKunnrTable.setValue("OPTION", "EQ");
				sKunnrTable.setValue("CUSTOMER_VENDOR_LOW", saleHeader.getShouDaFang());
				function2.execute(connect);
				JCoTable table4 = function2.getTableParameterList().getTable(
						"IT_TAB1");
				if (table4.getNumRows() > 0) {
					table4.firstRow();
					for (int i = 0; i < table4.getNumRows(); i++, table4
							.nextRow()) {
						if (i == 0) {
							value = table4.getValue("OBLIG_S").toString() == "" ? "0"
									: table4.getValue("OBLIG_S").toString();// 剩余信贷额度
						}
					}
				}
				// 剩余额度
				Double balanceMount = Double.parseDouble(value)
						- Double.parseDouble(order_total);
				java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
				// 发送短信
					try {
						List<Map<String, Object>> _list = jdbcTemplate
								.queryForList("select tel from sys_user where kunnr=(select sh.shou_da_fang from sale_header sh where id='"
										+ uuid
										+ "') and money='1' and status='1' and (tel is not null) and rownum=1");
						if (_list != null && _list.size() > 0) {
							String telephone = _list.get(0).get("TEL").toString();
							String smsCode = "300677";
							String endpoint = "http://172.16.3.204:8080/axis/services/SMSService?wsdl";
							String operationName = "sendSMS";
							String targetNameSpace = "http://172.16.3.204:8080/axis/services/SMSService";
							Service service = new Service();
							Call call = (Call) service.createCall();
							call.setTargetEndpointAddress(endpoint);
							call.setOperationName(new QName(targetNameSpace,
									operationName));
							call.setReturnClass(String.class);
							call.addParameter("smsCode", XMLType.XSD_INT,
									ParameterMode.IN);
							call.addParameter("tel", XMLType.XSD_STRING,
									ParameterMode.IN);
							call.addParameter("params", XMLType.SOAP_ARRAY,
									ParameterMode.IN);
							call.setUseSOAPAction(true);
							call.setSOAPActionURI(targetNameSpace + operationName);
							String[] params = new String[] {
									value, order_code };
							String callResultStr = (String) call
									.invoke(new Object[] {
											Integer.parseInt(smsCode), telephone,
											params });
							JSONObject resultObject = JSONObject
									.parseObject(callResultStr);
							String _result = resultObject.getString("errMsg");
							if ("OK".equals(_result)) {
								jdbcTemplate
										.update(
												"insert into finance_back_msg values( ? ,systimestamp,'Y',?,?)",
												new Object[] {
														order_code,telephone,
														"您好,您账上余额是:"+ value+ "元，余额不足推"+order_code+"单,订单已退回，请补款后重新上传汇款单并提交到财务确认环节,如有疑问请联系本司财务人员。"});
								} else {
								jdbcTemplate
										.update(
												"insert into finance_back_msg values( ? ,systimestamp,'N',?,?)",
												new Object[] {
														order_code,telephone,
														"短信发送失败！"});
							}
						}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (JCoException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			}
		redisUtils.endTask(assignee, groupId, taskId);
//		MemoryCacheUtil.endFlow(taskId,groupId,true);
	}

}