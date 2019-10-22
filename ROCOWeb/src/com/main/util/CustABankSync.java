package com.main.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.abc.AbchinaException;
import com.abc.DefaultAbchinaClient;
import com.abc.request.AbchinaApiPaymentDebitCardListRequest;
import com.abc.response.AbchinaDomainTokenAchieveResponse;
import com.abc.sign.TrxException;
import com.mw.framework.domain.CustAguasPassadas;
import com.mw.framework.utils.DateTools;

public class CustABankSync {
	@SuppressWarnings("unchecked")
	public static List<Map<String,Object>> syncABank(){
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
				debitCardListRequest.setTrBeginDate("2018-08-30");
				debitCardListRequest.setTrEndDate("2018-09-03");
				//debitCardListRequest.setTrBeginDate(DateTools.formatDate(new Date(), DateTools.defaultFormat));//交易起始日期
				abchinaApiNoticeAddResult = defaultAbchinaClient.doGet(debitCardListRequest,accessToken);
				if(abchinaApiNoticeAddResult.size()>0){
					Set<Entry<String, Object>> entrys = abchinaApiNoticeAddResult.entrySet();
					for (Entry<String, Object> entry : entrys) {
						Map<String,Object> map = (Map<String, Object>) entry.getValue();
						Map<String,Object> val = (Map<String, Object>) map.get("Value");
						Integer code = (Integer) map.get("Code");
						if(code==0){
							//code==0同步接口成功
							List<CustAguasPassadas> listData = (List<CustAguasPassadas>) val.get("List");
							if(listData.size()>0){
								list.add(abchinaApiNoticeAddResult);
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
		return list;

	}

}
