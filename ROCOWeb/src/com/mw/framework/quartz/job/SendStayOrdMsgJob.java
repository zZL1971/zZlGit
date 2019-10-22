package com.mw.framework.quartz.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.JSONObject;
import org.springframework.jdbc.core.JdbcTemplate;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.utils.MessageUtils;

/**
 * 发送遗留订单短信
 * @author RH
 *
 */
public class SendStayOrdMsgJob {
	public void run(){
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		String sql="select * from send_stay_ord_msg_job_view t";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list!=null&&list.size()>0){
			for (Map<String, Object> map : list) {
				//SEND_STAY_ORD_MSG
				String orderCode = map.get("order_code").toString();
				String start_time = map.get("start_time").toString();
				String total_time = map.get("total_time").toString();
				String status = map.get("status").toString();
				String kunnr = map.get("shou_da_fang").toString();
				try{
					if("Y".equals(status)){
						int count=jdbcTemplate.queryForObject("select count(*) from SEND_STAY_ORD_MSG sm where sm.order_code='"+orderCode+"'and sm.start_time='"+start_time+"'and sm.send_status ='Y'", Integer.class);
						if(count==0){
							List<Map<String, Object>> _list = jdbcTemplate
									.queryForList("select tel from sys_user where kunnr='"+kunnr+"' and money='1' and status='1' and (tel is not null) and rownum=1");
							if (_list != null && _list.size() > 0) {
								String telephone = _list.get(0).get("TEL").toString();
								Map<String, String> parameters = new HashMap<String, String>();
								parameters.put("order", orderCode);
								Boolean result = MessageUtils.sendMsg("300674",telephone, JSONObject.toJSONString(parameters));
								if(result!=false){
									jdbcTemplate.execute("insert into send_stay_ord_msg values('"+orderCode+"','Y','"+start_time+"','"+total_time+"')");
								}else{
									jdbcTemplate.execute("insert into send_stay_ord_msg values('"+orderCode+"','N','"+start_time+"','"+total_time+"')");
								}
							}
//							return;
						}
					}
				}catch(Exception e){
					e.getStackTrace();
					jdbcTemplate.execute("insert into send_stay_ord_msg values('"+orderCode+"','N','"+start_time+"','"+total_time+"')");
				}
			}
		}
	}
}
