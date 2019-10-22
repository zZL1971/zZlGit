package com.main.manager.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.main.bean.BgBean;
import com.main.dao.BgHeaderDao;
import com.main.dao.BgItemDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.dao.SysBzDao;
import com.main.dao.SysJobPoolDao;
import com.main.domain.bg.BgHeader;
import com.main.domain.bg.BgItem;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleLogistics;
import com.main.domain.sys.SysBz;
import com.main.domain.sys.SysJobPool;
import com.main.manager.BgManager;
import com.main.manager.SalePrModManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.FlowManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.manager.impl.CommonManagerImpl;
import com.mw.framework.utils.ZStringUtils;

import net.sf.json.JSONObject;

@Service("bgManager")
@Transactional
public class BgManagerImpl extends CommonManagerImpl implements BgManager {

	@Autowired
	private BgHeaderDao bgHeaderDao;

	@Autowired
	private BgItemDao bgItemDao;

	@Autowired
	private SysBzDao sysBzDao;

	@Autowired
	private SerialNumberManager serialNumberManager;

	@Autowired
	protected JdbcTemplate jdbcTemplate;

	@Autowired
	private FlowManager flowManager;

	@Autowired
	private CommonManager commonManager;

	@Autowired
	private SaleItemDao saleItemDao;

	@Autowired
	private SaleHeaderDao saleHeaderDao;

	@Autowired
	TaskService taskService;

	@Autowired
	private SysMesSendManager sysMesSendManager;

	@Autowired
	private SalePrModManager salePrModManager;

	@Autowired
	private SysJobPoolDao sysJobPoolDao;
	
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	
	@Override
	public BgHeader save(BgHeader bgHeader, BgBean bgBean) {
		String id = bgHeader.getId();
		if (ZStringUtils.isEmpty(id)) {
			String bgCode = bgHeader.getBgCode();
			if (ZStringUtils.isEmpty(bgCode)) {
				String curSerialNumberFull = "BG"
						+ serialNumberManager.curSerialNumberFull("BG", 6);
				bgHeader.setBgCode(curSerialNumberFull);
			}
		}

		List<BgItem> bgItemList = bgBean.getBgItemList();

		Set<BgItem> bgItemSet = new HashSet<BgItem>();
		for (BgItem bgItem : bgItemList) {
			bgItem.setStatus("1");// 状态为1时正常，为0时表示已经删除
			bgItem.setBgHeader(bgHeader);
			bgItemSet.add(bgItem);
		}
		bgHeader.setBgItemSet(bgItemSet);
		bgHeader.setOrderStatus("A");
		BgHeader save = bgHeaderDao.save(bgHeader);
		return save;

	}

	@Override
	public BgHeader save(BgHeader bgHeader, SysBz sysBz) {
		sysBzDao.save(sysBz);
		BgHeader save = bgHeaderDao.save(bgHeader);
		return save;

	}

	@Override
	public Message submit(String id, String status, String title, String step,
			String remarks, SysUser sysUser) {

		JSONObject obj = new JSONObject();

		BgHeader bgHeader = bgHeaderDao.findOne(id);
		SysBz sysBz = new SysBz();
		sysBz.setZid(id);
		sysBz.setText(title);
		sysBz.setRemark(remarks);
		sysBz.setCreateTime(new Date());
		sysBz.setCreateUser(sysUser.getLoginNo());

		obj.put("orderStatus", status);
		String msgStr = "";
		if (status.equals("A")) {
			msgStr = "退回成功";
		} else if (status.equals("B")) {
			//msgStr = "提交成功";
			//当前环节为结束的  不能提交
			//提交成功则保存提交的时候的当前环节
			List<Map<String,Object>> _list=jdbcTemplate.queryForList("select ar4.act_name_ as act_name from act_ord_curr_node4 ar4 where ar4.order_code=? ",new Object[]{bgHeader.getOrderCode()});
			if(_list!=null && _list.size()>0){
				String jdName=_list.get(0).get("ACT_NAME")==null?"":(String)_list.get(0).get("ACT_NAME");
				if("结束".equals(jdName)){
					msgStr="当前环节为结束，不能在此申请取消，谢谢！";
					status="A";
				}else{
					msgStr = "提交成功";
					bgHeader.setJdName(jdName);
				}
			}
		} else if (status.equals("C")) {
			msgStr = "通过成功";
			StringBuffer sb = new StringBuffer();
			sb.append("select t3.assignee_,t.PROCINSTID,t.act_id  from sale_view t "

					+ " left join act_ct_mapping t2 on t2.id = t.id  "
					+ " left join act_hi_actinst t3 on t2.procinstid = t3.PROC_INST_ID_ "
					+ " where t.order_code='"
					+ bgHeader.getOrderCode()
					+ "'  order by t3.start_time_");
			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(sb.toString());
			Set<String> userSet = new HashSet<String>();
			String taskId = "";
			String groupId = "";
			if (queryForList != null && queryForList.size() > 0) {
				for (Map<String, Object> map : queryForList) {
					Object object = map.get("ASSIGNEE_");
					if (ZStringUtils.isNotEmpty(object)) {
						userSet.add(object.toString());
					}

					Object object2 = map.get("PROCINSTID");
					if (ZStringUtils.isNotEmpty(object)) {
						taskId = object2.toString();
					}
				}
				Map<String, Object> map = queryForList
						.get(queryForList.size() - 1);
				Object object3 = map.get("ACT_ID");
				if (ZStringUtils.isNotEmpty(object3)) {
					groupId = object3.toString();
				}
			}
			// if (userSet != null && userSet.size() > 0) {
			// List<SysMesSend> sysMesSendList = new ArrayList<SysMesSend>();
			// for (String user : userSet) {
			// SysMesSend sysMesSend = new SysMesSend();
			// sysMesSend.setMsgType("1");
			// sysMesSend.setMsgTitle("订单" + bgHeader.getOrderCode()
			// + "变更");
			// sysMesSend.setMsgBody("变更单号为" + bgHeader.getBgCode()
			// + "，请查看变更单。");
			// sysMesSend.setSendUser(this.getLoginUserId());
			// sysMesSend.setSendTime(new Date());
			// sysMesSend.setHasRead("0");
			// sysMesSend.setReceiveUser(user);
			// sysMesSendList.add(sysMesSend);
			// }
			// commonManager.save(sysMesSendList);
			// }

			if ("QX".equals(bgHeader.getBgType())) {
				// 更新返点
				boolean isSapDisable = false;
				salePrModManager.cancelOrder(bgHeader.getBgCode());

				Set<BgItem> bgItems = bgHeader.getBgItemSet();
				for (BgItem bgItem : bgItems) {

					// 订单审绘子流程
					if ("callactivity1".equals(groupId)) {
						String sql = "select procinstid from act_ct_mapping where id='"
								+ bgItem.getSaleItemId() + "'";
						List<Map<String, Object>> queryForIdList = jdbcTemplate
								.queryForList(sql);
						if (queryForIdList.size() > 0) {
							Map<String, Object> map = queryForIdList.get(0);
							Object object = map.get("PROCINSTID");
							if (ZStringUtils.isNotEmpty(object)) {
								RuntimeService runtimeService = SpringContextHolder
										.getBean("runtimeService");
								// ProcessInstance processInstance =
								// runtimeService.createProcessInstanceQuery().processInstanceId(object.toString()).singleResult();
								Execution execution = runtimeService
										.createExecutionQuery()
										.processInstanceId(object.toString())
										.activityId("receivetask1")
										.singleResult();

								// Execution
								// execution=runtimeService.createExecutionQuery().processInstanceId(object.toString()).singleResult();
								if (execution != null) {
									runtimeService.signal(execution.getId());
								}
								Task task = taskService.createTaskQuery()
										.executionId(object.toString())
										.singleResult();
								if (task != null) {
									// task.setAssignee(super.getLoginUserId());
									// taskService.saveTask(task);
									Message jump = flowManager.jump(
											task.getId(), "endevent1",
											bgHeader.getReason());
									if (!jump.getSuccess()) {
										throw new ActivitiException(
												jump.getErrorMsg());
									}
								}
							}

						}

					}
					SaleItem findOne = saleItemDao.findOne(bgItem
							.getSaleItemId());
					findOne.setStateAudit("QX");
					SaleHeader saleHeader = findOne.getSaleHeader();
					if(saleHeader != null) {
						String saleFor = jdbcTemplate.queryForObject("SELECT MH.SALE_FOR FROM SALE_ITEM SI LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID WHERE SI.ORDER_CODE_POSEX=?", String.class,findOne.getOrderCodePosex());
						Integer nums = jdbcTemplate.queryForObject("SELECT COUNT(1) AS NUMS FROM SALE_ITEM SI LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID WHERE SI.PID=? AND NVL(SI.STATE_AUDIT,'E')<>'QX' AND MH.SALE_FOR=? ",Integer.class,saleHeader.getId(),saleFor);
						if(nums <= 1) {
							SaleLogistics saleLogistics = saleLogisticsDao.findBySaleHeaderIdAndSaleFor(saleHeader.getId(), saleFor);
							if(saleLogistics != null) {
								saleLogisticsDao.delete(saleLogistics);
							}
						}
					}
					if(findOne.getSapCode()!=null&&!"".equals(findOne.getSapCode())) {
						isSapDisable = true;
					}
					//父行取消子行删除
					List<SaleItem> saleItemList=saleItemDao.queryByNativeQuery("select * from Sale_Item where parent_Id='"+findOne.getId()+"'");
					for (SaleItem saleItem : saleItemList) {
						saleItemDao.delete(saleItem.getId());
					}
				}

				// 查询是否当前单据所有行项目是否都为取消状态，是则结束主流程
				SaleHeader saleHeader = saleHeaderDao.findByCode(
						bgHeader.getOrderCode()).get(0);
				//订单下面的行项目是否全为取消，是则为false，不是则为true
				boolean flag = false;
				//订单下面的所有行项目的总价格
				double orderTotalPrice = 0.0;
				//根据订单的付款方式进行付款金额的计算
				double discount = 0.0;
				//订单下所有的 行项目
				List<SaleItem> saleItems = saleItemDao
						.findItemsByPid(saleHeader.getId());
				
				if (saleHeader != null) {
					for (SaleItem saleItem : saleItems) {
						if (!("QX".equals(saleItem.getStateAudit()))) {
							flag = true;
							orderTotalPrice += saleItem.getTotalPrice();
						}
					}
					SysJobPool sysJobPool = sysJobPoolDao.findByZuonr(saleHeader.getOrderCode());//订单取消需要改变 过账池中的金额
					if("usertask_finance".equals(groupId)) {
						if(sysJobPool!=null) {
							sysJobPool.setNetwr(orderTotalPrice);
						}
					}
					//如果所有行项目都是取消，就将主流程关闭，且订单状态改为取消
					if (!flag) {
						if(sysJobPool!=null) {
							sysJobPool.setIsFreeze("1");
						}
						String sql = "select procinstid from act_ct_mapping where id='"
								+ saleHeader.getId() + "'";
						List<Map<String, Object>> queryForIdList = jdbcTemplate
								.queryForList(sql);

						if (queryForIdList.size() > 0) {
							Task task = taskService
									.createTaskQuery()
									.executionId(
											queryForIdList.get(0).get(
													"PROCINSTID")
													+ "").singleResult();
							if (task != null) {
								String sql1="update act_hi_actinst aha set aha.end_time_=sysdate where aha.id_=(select id_ from (select aha.id_ from sale_header sh left join "
										+ "act_ct_mapping cm on cm.id=sh.id left join act_hi_actinst aha on aha.proc_inst_id_=cm.procinstid where sh.id='"+saleHeader.getId()+"' order by aha.start_time_ desc)a where rownum=1)";
								jdbcTemplate.update(sql1);
								Message jump = flowManager.jump(task.getId(),
										"endevent1", bgHeader.getReason());
								if (!jump.getSuccess()) {
									throw new ActivitiException(
											jump.getErrorMsg());
								}
								saleHeader.setOrderStatus("QX");
								// jdbcTemplate.update("update sale_header set order_Status='QX' where order_code='"+bgHeader.getOrderCode()+"'");
								sysMesSendManager.sendUser("订单整单取消", "订单"
										+ bgHeader.getOrderCode() + ",流程实例编号："
										+ taskId + "，已整单取消。管理员请知悉!",
										sysUser.getId(), "admin");

							}/*
							 * else{ sysMesSendManager.sendUser("订单整单取消","订单" +
							 * bgHeader.getOrderCode()+ "，任务不存在!管理员请知悉!", this
							 * .getLoginUserId(),"admin"); throw new
							 * ActivitiException("任务不存在!"); }
							 */
						} else {
							// sysMesSendManager.sendUser("订单整单取消","订单" +
							// bgHeader.getOrderCode()+ "，未发现对应的流程实例，管理员请知悉!",
							// sysUser.getId(),"admin");
							throw new ActivitiException("订单"
									+ bgHeader.getOrderCode()
									+ ",未发现对应的流程实例，管理员请知悉!");
						}
						// }

					}

					// SaleHeader saleHeader =
					// saleHeaderDao.findByCode(bgHeader.getOrderCode()).get(0);
					/*
					 * Set<SaleItem> saleItems=saleHeader.getSaleItemSet(); int
					 * saleItemCount=0; for (SaleItem saleItem : saleItems) {
					 * if(!"QX".equals(saleItem.getStateAudit())) {
					 * saleItemCount++; } } if(saleItemCount==0) { Task task =
					 * taskService
					 * .createTaskQuery().executionId(taskId).singleResult();
					 * if(task!=null) {
					 * //task.setAssignee(super.getLoginUserId());
					 * //taskService.saveTask(task); Message jump =
					 * flowManager.jump(task.getId(), "endevent1",
					 * bgHeader.getReason()); if(!jump.getSuccess()){ throw new
					 * ActivitiException(jump.getErrorMsg()); }
					 * jdbcTemplate.update(
					 * "update sale_header set order_Status='QX' where order_code='"
					 * +bgHeader.getOrderCode()+"'"); }else { throw new
					 * Exception(); }
					 * 
					 * 
					 * }
					 */

					// double fuFuanMoney = 0;
					// fuFuanMoney=saleManager.getFuFuanMoney(saleHeader);
					// saleHeader.setFuFuanMoney(fuFuanMoney);

					// SaleManager saleManager2 =
					// SpringContextHolder.getBean("saleManager");
					// saleManager2.save(saleHeader);

					// 更新付款金额
					// jdbcTemplate.update("update sale_header sh set
					// sh.fu_fuan_money =(
					// decode(sh.fu_fuan_cond, '1', 1, '2', 0, '3', 0.5, '4',
					// 0.3) *(select sum(sl.total_price) from sale_item sl where
					// sl.pid = sh.id and nvl(sl.state_audit, 'C') <> 'QX'))
					// where sh.order_code = '"+bgHeader.getOrderCode()+"'");
					if (saleHeader.getFuFuanCond() != null) {
						switch (Integer.parseInt(saleHeader.getFuFuanCond()
								.trim())) {
						case 1:
							discount = 1;
							break;
						case 2:
							discount = 0;
							break;
						case 3:
							discount = 0.5;
							break;
						case 4:
							discount = 0.3;
							break;
						}
						saleHeader.setFuFuanMoney(discount * orderTotalPrice);
						saleHeader.setOrderTotal(orderTotalPrice);
						
					}

				}
				//如果订单是有SAP订单号的，那么提醒操作人员在SAP中进行修改
				if(isSapDisable){
					msgStr+="! 已有SAP订单生成，请同时取消SAP对应的行号";
				}
			}
		}

		// bgHeader.setOrderStatus(status);
		// bgManager.save(bgHeader, sysBz);

		commonManager.save(sysBz);
		obj.put("msg", msgStr);
		Message msg = new Message(obj);
		bgHeader.setOrderStatus(status);
		return msg;
	}

}
