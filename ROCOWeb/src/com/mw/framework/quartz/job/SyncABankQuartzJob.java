package com.mw.framework.quartz.job;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.abc.AbchinaException;
import com.abc.DefaultAbchinaClient;
import com.abc.request.AbchinaApiPaymentDebitCardListRequest;
import com.abc.response.AbchinaDomainTokenAchieveResponse;
import com.abc.sign.TrxException;
import com.mw.framework.domain.CustAguasPassadas;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.DateTools;
public class SyncABankQuartzJob {
	//同步农行数据
	@Autowired
	private CommonManager commonManager;
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	//@SuppressWarnings("unchecked")
	public void run(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		System.out.println("开始同步农行接口，时间:"+df.format(new Date()));
		//获取昨天的日期
		Calendar   cal   =   Calendar.getInstance();  
		cal.add(Calendar.DATE,   -1);  
		String yesterday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());  
		DefaultAbchinaClient defaultAbchinaClient=null;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		try {
			defaultAbchinaClient = new DefaultAbchinaClient();
			//token 不要每次请求都进行获取，每天每个用户token的请求次数是有限制的，每一个token的持续时间是两个小时，可以写一个定时，快到两小时的时候，获取token
			String accessToken = "cdf5265d77987e5f9260832f6269f85b";//
			AbchinaDomainTokenAchieveResponse abchinaTokenAchieveResponse = defaultAbchinaClient.executeAchieveToken();
			accessToken = abchinaTokenAchieveResponse.getAppAccessToken();
			//实例化具体API对应的request类，类名称和接口对应 
			AbchinaApiPaymentDebitCardListRequest debitCardListRequest=new AbchinaApiPaymentDebitCardListRequest();
			int pageSize = Integer.MAX_VALUE;
			debitCardListRequest.setPageSize("50");//页面条数
			biaozhi:for (int i = 0; i < pageSize; i++) {
				Map<String,Object> abchinaApiNoticeAddResult=new HashMap<String,Object>();
				debitCardListRequest.setCurPage(String.valueOf(i));//当前页码
				debitCardListRequest.setTrBeginDate(yesterday);//交易开始日期
				debitCardListRequest.setTrEndDate(df.format(new Date()));//交易结束日期
//				if(abchinaApiNoticeAddResult==null||"".equals(abchinaApiNoticeAddResult)||abchinaApiNoticeAddResult.isEmpty()){
//					break;
//				}
				abchinaApiNoticeAddResult = defaultAbchinaClient.doGet(debitCardListRequest,accessToken);
				if(abchinaApiNoticeAddResult.size()>0){
					Set<Entry<String, Object>> entrys = abchinaApiNoticeAddResult.entrySet();
					for (Entry<String, Object> entry : entrys) {
						Map<String,Object> map = (Map<String, Object>) entry.getValue();
						Map<String,Object> val = (Map<String, Object>) map.get("Value");
						Integer code = (Integer) map.get("Code");
						if(code==0){
							//code==0同步接口成功
							System.out.println("同步农行接口成功");
							List<CustAguasPassadas> listData = (List<CustAguasPassadas>) val.get("List");
							
							//遍历农行返回集合
							if(listData.size()>0){
								list.add(abchinaApiNoticeAddResult);
								//遍历数据保存到数据库
								for (Map<String, Object> obj : list) {
									if(obj.size()>0){
										Set<Entry<String, Object>> entrys1 = obj.entrySet();
										for (Entry<String, Object> entry1 : entrys1) {
											Map<String,Object> map1 = (Map<String, Object>) entry1.getValue();
											Map<String,Object> val1 = (Map<String, Object>) map1.get("Value");
											List<CustAguasPassadas> list1 = (List<CustAguasPassadas>) val1.get("List");
											for (int j = 0; j < list1.size(); j++) {
												Map<String, Object> m=(Map<String, Object>) list1.get(j);
												String jrnNo = (String) m.get("JrnNo");
												//判断交易交易流水是否已更新
												String sql="SELECT COUNT(*) FROM CUST_TRADE_HEADER TH WHERE TH.JRN_NO= '"+jrnNo+"'";
												int count = jdbcTemplate.queryForObject(sql, Integer.class);
												if(count>0){
													//已更新
												}else{
													//没有更新，更新并保存到数据库
													String tradeCompanyId = (String) m.get("TradeCompanyId");
													String tradeAccNo = (String) m.get("TradeAccNo");
													jrnNo=(String) m.get("JrnNo");
													String tradeDate= (String) m.get("TradeDate");
													String dealerNum = (String) m.get("DealerNum");
													String timer = String.valueOf(m.get("TradeTime"));
													timer=timer.substring(timer.indexOf("(")+1, timer.lastIndexOf(")"));
													Date tradeTime= new Date(Long.parseLong(timer));
													String tradeRemark= (String) m.get("TradeRemark");
													String tradePostcript= (String) m.get("TradePostcript");
													String tradeDescription= (String) m.get("TradeDescription");
													String payer=(String)m.get("Payer");
													Double tradeAmount= Double.parseDouble(String.valueOf(m.get("TradeAmount")));
													CustAguasPassadas aguasPassadas=new CustAguasPassadas(tradeCompanyId, tradeAccNo, jrnNo, tradeDate, dealerNum,
															tradeTime,tradeRemark, tradePostcript, tradeDescription,tradeAmount,payer, null, "NH");
													commonManager.save(aguasPassadas);//保存到数据库
												}
											}
										}
									}
								}
							}else{
								break biaozhi;
							}
						}
					}
				}else{
					break;
				}
			}
		} catch (TrxException e) {
			e.printStackTrace();
		} catch (AbchinaException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("同步农行接口完毕");
	}
}