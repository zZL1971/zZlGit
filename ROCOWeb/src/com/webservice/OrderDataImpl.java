package com.webservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jws.WebService;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.utils.DateTools;
@WebService(endpointInterface="com.webservice.OrderDataTo",serviceName="OrderData")
public class OrderDataImpl implements OrderDataTo {

	@Override
	public String getOrderStatus(String custId) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> orderInfo = jdbcTemplate
				.queryForList("" +
						"SELECT SH.ORDER_CODE,SH.ORDER_STATUS,SH.SHI_JI_DATE,SH.SHI_JI_DATE2,SH.YU_JI_DATE,SH.YU_JI_DATE2,SH.YU_JI_DATE3 FROM SALE_HEADER SH " +
						"LEFT JOIN TERMINAL_CLIENT TC ON SH.ID=TC.SALE_ID " +
						"WHERE TC.CUST_ID='"+custId+"' AND SH.ORDER_CODE IS NOT NULL ORDER BY SH.ORDER_DATE DESC");
		Map<String,Object> map=new HashMap<String,Object>();
		Map<String,Object> info=new HashMap<String,Object>();
		if(orderInfo.size()>0){
			String orderCode = (String) orderInfo.get(0).get("ORDER_CODE");
			info.put("order_code",orderCode);
			String	orderDate=null;
			/**
			 * 获取生产预计完工日期
			 */
			if(orderInfo.get(0).get("YU_JI_DATE3")!=null){
				orderDate = DateTools.getSelfFomat(DateTools.fullFormat,
						(Timestamp) orderInfo.get(0).get("YU_JI_DATE3"));
				
			}
			info.put("scywg_date",orderDate);
			String	shiJiDate1=null;
			/**
			 * 获取实际出库天数
			 */
			if(orderInfo.get(0).get("SHI_JI_DATE")!=null){
				shiJiDate1 =  DateTools.getSelfFomat(DateTools.fullFormat,
						(Timestamp) orderInfo.get(0).get("SHI_JI_DATE"));
				
			}
			info.put("sjck_date",shiJiDate1);
			String orderStatus = (String) orderInfo.get(0).get("ORDER_STATUS");
			info.put("order_stats",orderStatus);
			String	shiJiDate2=null;
			/**
			 * 获取实际入库日期
			 */
			if(orderInfo.get(0).get("SHI_JI_DATE2")!=null){
				shiJiDate2= DateTools.getSelfFomat(DateTools.fullFormat,
						(Timestamp)orderInfo.get(0).get("SHI_JI_DATE2"));
			}
			info.put("sjrk_date",shiJiDate2);
			String	yuJiDate=null;
			/**
			 * 预计出货
			 */
			if(orderInfo.get(0).get("YU_JI_DATE")!=null){
				yuJiDate = DateTools.getSelfFomat(DateTools.fullFormat,
						(Timestamp)orderInfo.get(0).get("YU_JI_DATE"));
			}
			info.put("yjch_date",yuJiDate);
			String	yuJiDate1=null;
			/**
			 * 计划完工日期
			 */
			if(orderInfo.get(0).get("YU_JI_DATE2")!=null){
				yuJiDate1 = DateTools.getSelfFomat(DateTools.fullFormat,
						(Timestamp)orderInfo.get(0).get("YU_JI_DATE2"));
				
			}
			info.put("jhwg_date",yuJiDate1);
			map.put("return_data",info);
			map.put("return_code","SUCCESS");
			map.put("return_msg","获取成功");
			
			return JSONObject.fromObject(map).toString();
		}else{
			map.put("return_code","FALIAT");
			map.put("return_msg","您还没有创建订单哦，赶快去创建属于您的第一个订单吧！");
			return JSONObject.fromObject(map).toString();
		}
	}

	@Override
	public String wechatUserJurisdiction(String userId) {
		// TODO Auto-generated method stub
		String msg=null;
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		String sql="SELECT WM.DESC_ZH_CN,WM.PIC,WM.BINDTAP,WM.ORDER_BY FROM WECHAT_ROLE WR LEFT JOIN WECHAT W ON WR.WECHAT_ROLE_ID = W.ID LEFT JOIN WECHAT_ROLE_MENU WRM ON WRM.WECHAT_ROLE_ID = W.ID RIGHT JOIN WECHAT_MENU WM ON WRM.WECHAT_MENU_ID = WM.ID WHERE WR.WECHAT_USER_ID='"+userId+"'";
		List<Map<String, Object>> list = jdbcTemplate.query(sql, new MapRowMapper(true));
		List<Map<String, String>> listMap=new ArrayList<Map<String,String>>();
		if(list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				Set<Entry<String, Object>> entryMap = list.get(i).entrySet();
				Map<String, String> lmap=new HashMap<String, String>();
				for (Entry<String, Object> entry : entryMap) {
					lmap.put("\""+entry.getKey()+"\"", "\""+String.valueOf(entry.getValue())+"\"");
				}
				listMap.add(lmap);
			}
			return listMap.toString().replace('=', ':');
		}else{
			msg= "[msg:'error',list:{}]";
		}
		return msg;
	}

}