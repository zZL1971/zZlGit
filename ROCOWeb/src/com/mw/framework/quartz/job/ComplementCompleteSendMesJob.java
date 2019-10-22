package com.mw.framework.quartz.job;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.rpc.ServiceException;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONObject;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.DateTools;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;
/**
 * 订单配套完工“下单系统提示及短信提醒“
 * @author Chaly
 *
 */
public class ComplementCompleteSendMesJob {
	private static final Logger logger = LoggerFactory.getLogger(ComplementCompleteSendMesJob.class);
	
	@SuppressWarnings("deprecation")
	public void run(){
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		long systemTime=System.currentTimeMillis();
		JCoDestination connect = SAPConnect.getConnect();
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat zhFormat=new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		try {
			JCoFunction function = connect.getRepository().getFunction("ZRFC_SD_COST_PROCESS");
			JCoTable itTab = function.getTableParameterList().getTable("IT_TAB");
			function.getImportParameterList().setValue("T_ERDAT", DateTools.getDateAndTime(new Date(), DateTools.YYMMDDFormat));
			function.execute(connect);
			for (int i = 0; i < itTab.getNumRows(); i++,itTab.nextRow()) {
				StringBuilder dateBuil=new StringBuilder();
				dateBuil.append(itTab.getString("ERDAT")+" "+itTab.getString("DDUZEIT"));
				Date timeOut = format.parse(dateBuil.toString());
				Date time=new Date(systemTime);
				if(timeOut.getHours()==(time.getHours()-1)){//timeOut.getHours()>(time.getHours()-1)&&timeOut.getHours()<time.getHours()
					Integer zdays=itTab.getInt("ZDAYS");//获取超期天数
					SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
					Date zhuangZaiDate = dateFormat.parse(itTab.getString("ERDAT"));
					int day = zhuangZaiDate.getDay();
					int month = zhuangZaiDate.getMonth();
					if((zdays.intValue()==0&&(time.getDay()==day&&time.getMonth()==month))||zdays.intValue()>=1){
						//do someing
						//SimpleDateFormat zhFormat=new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
						Calendar calendar=Calendar.getInstance(Locale.CHINESE);
						calendar.setTime(timeOut);
						calendar.set(Calendar.DATE,calendar.get(Calendar.DATE)+3);
						Date date = calendar.getTime();
						//获取 短信发送方 电话号码
						String tele = itTab.getString("TELF1");
						double kkjetr = itTab.getDouble("KKJETR");//获取扣款金额
						double syjejr = itTab.getDouble("SYJETR");//获取剩余金额
						String ddbaos = itTab.getString("DDBAOS");//获取包数
						String name1 = itTab.getString("NAME1");//获取经销商名称
						double balance=syjejr-kkjetr;//剩余金额减去扣款金额得出余额
						name1=name1!=null?name1:"";
						String smsCode = "184594";
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
								name1,zhFormat.format(timeOut),
								ddbaos, zhFormat.format(date), String.valueOf(syjejr) };
						String callResultStr = (String) call
								.invoke(new Object[] {
										Integer.parseInt(smsCode), tele,
										params });
						
						JSONObject resultObject = JSONObject
								.parseObject(callResultStr);
						
						String _result = resultObject.getString("errMsg");
						if("OK".equals(_result)){
							itTab.getString("ZJIAN");
							jdbcTemplate.execute("INSERT INTO SYS_MESSAGE_SEND VALUES( '"+itTab.getString("ZJIAN")+"','"+itTab.getString("VBELN")+"', "
            + "'"+itTab.getString("BSTNK")+"','"+itTab.getString("KUNNR")+"','"+itTab.getString("TELF1")+"','"+itTab.getString("NAME")+"','"+itTab.getString("DDBAOS")+"',"
            +"'"+itTab.getString("ERDAT")+"','"+itTab.getString("DDUZEIT")+"','"+itTab.getString("BLDAT")+"','"+itTab.getString("ERZET")+"',"
            +"'"+itTab.getString("ZDAYS")+"','"+itTab.getString("KKJETR")+"','"+itTab.getString("SYJETR")+"','"+itTab.getString("NAME1")+"')");
							logger.info("ComplementCompleteSendMesJob---发送成功");
						}else{
							logger.info("ComplementCompleteSendMesJob---发送失败");
						}
					}
				}
			}
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}