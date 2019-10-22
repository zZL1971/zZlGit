package com.webservice;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.directwebremoting.json.types.JsonObject;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.druid.util.StringUtils;
import com.main.bean.MaterialBean;
import com.main.controller.SaleController;
import com.main.domain.mm.MaterialBujian;
import com.main.manager.MyGoodsManager;
import com.main.manager.SalePrModManager;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.controller.MemoryCacheUtil;
import com.mw.framework.domain.SendTaskLog;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.DateTools;

@WebService(endpointInterface = "com.webservice.IRocoService", serviceName = "RocoService")
public class RocoService implements IRocoService {
	public String hellWord(String xml) {
		return "hello word! by ROCO";
	}

	/**
	 * 劳卡webService
	 * 
	 * @param type
	 *            接口 SALE_MODIFICATION_PRICE修改价格,SALE_MODIFICATION_LOAN修改借贷项
	 * @param paramXml
	 *            xml参数
	 * @return 返回信息 "S/E@消息"默认不成功 S:成功、E:失败
	 */
	public String rocoWS(String type, String paramXml) {

		String rStr = "E@失败";
		System.out.println("--------->webService 处理类型：" + type + "--------参数："
				+ paramXml);

		// 修改价格 (2016-05-19 不启用了)
		if ("SALE_MODIFICATION_PRICE".equals(type)) {
			System.out.println("----------修改价格----------");
			SalePrModManager spmm = SpringContextHolder
					.getBean("salePrModManager");
			Message msg = spmm.savePrModXML(paramXml);
			if (msg.getSuccess()) {
				rStr = "S@" + msg.getMsg();
			} else {
				rStr = "E@" + msg.getErrorMsg();
			}
		} else if ("SALE_MODIFICATION_LOAN".equals(type)) {
			System.out.println("----------修改借贷项----------");
			SalePrModManager spmm = SpringContextHolder
					.getBean("salePrModManager");
			Message msg = spmm.saveLoanAmount(paramXml);
			if (msg.getSuccess()) {
				rStr = "S@" + msg.getMsg();
			} else {
				rStr = "E@" + msg.getErrorMsg();
			}
		}
		System.out.println("--------->webService 返回信息：" + rStr);
		return rStr;
	}

	

	@Override
	public void resetTaskMap() {
		// TODO Auto-generated method stub
		MemoryCacheUtil.resetMap();
	}

	@Override
	public void sendTaskList(ArrayList<SendTaskLog> taskIdMapList) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		for (SendTaskLog task : taskIdMapList) {
			String taskId = task.getTaskId();
			String groupId = task.getGroupId();
			// String assignee=map.get("ASSIGNEE").toString();
			long taskCreateTime=Long.parseLong(task.getTaskCreateTime());
			int taskType = task.getTaskType();
			int status = task.getStatus();

			// List<Map<String,Object>>
			// list=jdbcTemplate.queryForList("select * from act_ru_task where id_='"+taskId+"'");

			if (status == 0
			// || list==null || list.size()== 0
			) {

			} else {
				// 查看任务是结束还是创建
				switch (taskType) {
				case 0:
					// 结束
					MemoryCacheUtil.endFlow(taskId, groupId, false);
					break;
				case 1:
					// 创建
					MemoryCacheUtil.startFlow(null,taskId, groupId, taskCreateTime);
					break;
				default:
					continue;
				}
			}
			jdbcTemplate
					.update(" update send_task set status =0 where task_id='"
							+ taskId + "' and task_type='" + taskType + "'");
		}

	}

	
	@Override
	public String getSaleMessage(String orderCode, String sapOrderCode,
			String sold2Party, String sold2PartyCode, String ship2Party,
			String orderDateFrom, String orderDateTo,
			String expectedShipDateFrom, String expectedShipDateTo,String cusTel) {
		// TODO Auto-generated method stub
		try {
			boolean flag=false;
			StringBuilder sql = new StringBuilder(
					"select sh.id,sh.order_code from sale_header sh where 1=1 and sh.order_code is not null ");
			List<Object> params = new ArrayList<Object>();
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");
			//com.main.dao.SaleHeaderDao saleHeaderDao=SpringContextHolder.getBean("saleHeaderDao");
			com.main.controller.SaleController saleController = SpringContextHolder
					.getBean("saleController");
 
			if (orderCode != null && !com.alibaba.druid.util.StringUtils.isEmpty(orderCode)) {
				flag=true;
				sql.append(" and sh.order_code like  ? ");
				params.add(com.mw.framework.util.StringHelper.like(String.valueOf(orderCode)));
			}
			if (sapOrderCode != null && !com.alibaba.druid.util.StringUtils.isEmpty(sapOrderCode)) {
				flag=true;
				sql.append(" and sh.sap_order_code like ? ");
				params.add(com.mw.framework.util.StringHelper.like(String.valueOf(sapOrderCode)));
			}

			if (sold2Party != null && !com.alibaba.druid.util.StringUtils.isEmpty(sold2Party)) {
				flag=true;
				List<Map<String, Object>> kunnrList = jdbcTemplate
						.queryForList(" select kunnr from cust_header where name1 like '%"
								+ sold2Party + "%'");
				if (kunnrList != null && kunnrList.size() > 0) {
					sql.append(" and sh.shou_da_fang in (''  ");
					for (Map<String, Object> map : kunnrList) {
						sql.append(", ? ");
						params.add(map.get("KUNNR"));
					}
					sql.append(" )");
				}
			}

			if (sold2PartyCode != null && !com.alibaba.druid.util.StringUtils.isEmpty(sold2PartyCode)) {
				flag=true;
				sql.append(" and sh.shou_da_fang like  ? ");
				params.add(com.mw.framework.util.StringHelper.like(String.valueOf(sold2PartyCode)));
			}
			if (ship2Party != null && !com.alibaba.druid.util.StringUtils.isEmpty(ship2Party)) {
				flag=true;
				sql.append(" and sh.song_da_fang like  ? ");
				params.add(com.mw.framework.util.StringHelper.like(String.valueOf(ship2Party)));
			}
			if (orderDateFrom != null && !com.alibaba.druid.util.StringUtils.isEmpty(orderDateFrom)) {
				flag=true;
				sql.append(" and sh.order_date>= ? ");
				params.add(com.mw.framework.utils.DateTools.strToDate(orderDateFrom,
						com.mw.framework.utils.DateTools.defaultFormat));
			}
			if (orderDateTo != null && !com.alibaba.druid.util.StringUtils.isEmpty(orderDateTo)) {
				flag=true;
				sql.append(" and sh.order_date<= ? ");
				params.add(com.mw.framework.utils.DateTools.strToDate(orderDateTo,
						com.mw.framework.utils.DateTools.defaultFormat));
			}

			if (expectedShipDateFrom != null
					&& !com.alibaba.druid.util.StringUtils.isEmpty(expectedShipDateFrom)) {
				flag=true;
				sql.append(" and sh.yu_ji_date>= ? ");
				params.add(com.mw.framework.utils.DateTools.strToDate(expectedShipDateFrom,
						com.mw.framework.utils.DateTools.defaultFormat));
			}
			if (expectedShipDateTo != null
					&& !com.alibaba.druid.util.StringUtils.isEmpty(expectedShipDateTo)) {
				flag=true;
				sql.append(" and sh.yu_ji_date<= ? ");
				params.add(com.mw.framework.utils.DateTools.strToDate(expectedShipDateTo,
						com.mw.framework.utils.DateTools.defaultFormat));
			}
			if(cusTel!=null && !com.alibaba.druid.util.StringUtils.isEmpty(cusTel)) {
				flag=true;
				sql.append(" and sh.id in ( select tc.sale_id from terminal_client tc where tc.tel = ? )");
				params.add(cusTel);
			}
			if(!flag){
				return "你要传参给我啊";
			}

			List<Map<String, Object>> queryResultList = jdbcTemplate
					.queryForList(sql.toString(), params.toArray());
			Map<String, Message> map = new HashMap<String, Message>();
			for (Map<String, Object> resultMap : queryResultList) {
				String saleId = resultMap.get("ID").toString();
				orderCode = resultMap.get("ORDER_CODE").toString();
				//Map<String,Object> innerMap=new HashMap<String, Object>();
				//innerMap.put("data", saleHeaderDao.getOne(saleId));
				Message msg = saleController.findById(saleId);
				msg.put("saleItemPrices", saleController.findSaleItemPrByPid(
						saleId).getContent());
				msg.put("SaleItems", saleController.findItemsByPid(saleId)
						.getContent());
				msg.remove("success");
				map.put(orderCode, msg);
			}
			return net.sf.json.JSONObject.fromObject(map).toString();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void sendTask(String srcTaskId, String groupId, long taskCreateTime) {
		// TODO Auto-generated method stub
		MemoryCacheUtil.startFlow(null,srcTaskId, groupId, taskCreateTime);
	}
	
	@Override
	public String createBJOrder(String complaintTime, String duty,
			String matnr, String miaoshu, String barcode,String zzebm, String zzecj,
			String zzelb, String zzescx, String zzezx, String zzrgx,
			String zztsnr, String zzxbmm, String zzccz,String zzccwt,String orderType,String account) {
		Message msg = null;
		MyGoodsManager myGoodsManager = SpringContextHolder
				.getBean("myGoodsManager");
        ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest().getSession().setAttribute(Constants.CURR_USER_ID,account);
		MaterialBean materialBean = new MaterialBean();
		MaterialBujian materialBujian = new MaterialBujian();
		materialBujian.setComplaintTime(complaintTime);
		materialBujian.setDuty(duty);
		materialBujian.setMatnr(matnr);
		materialBujian.setMiaoshu(miaoshu);
		materialBujian.setBarcode(barcode);
		materialBujian.setZzebm(zzebm);
		materialBujian.setZzecj(zzecj);
		materialBujian.setZzelb(zzelb);
		materialBujian.setZzescx(zzescx);
		materialBujian.setZzezx(zzezx);
		materialBujian.setZzrgx(zzrgx);
		materialBujian.setZztsnr(zztsnr);
		materialBujian.setZzxbmm(zzxbmm);
		materialBujian.setType(orderType);
		materialBujian.setZzccz(zzccz);
		materialBujian.setZzccwt(zzccwt);
		materialBean.setMaterialBujian(materialBujian);
		
		//materialBujian.setCreateUser(createUser);
		MaterialBujian obj = myGoodsManager.saveBJ(materialBean,null);//Chaly
		if (obj != null) {
			msg = new Message("200","保存成功!");
			msg.setSuccess(true);
		} else {
			msg = new Message("500", "保存失败!");
			msg.setSuccess(false);
		}
		return JSONObject.fromObject(msg).toString();
	}
}
