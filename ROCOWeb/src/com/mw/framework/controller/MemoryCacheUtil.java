package com.mw.framework.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.springframework.jdbc.core.JdbcTemplate;
import com.alibaba.druid.util.StringUtils;
import com.mw.framework.utils.StringUtil;
import com.mw.framework.context.SpringContextHolder;

public class MemoryCacheUtil {
	public static Map<String, Map<String, Map<String, String>>> cacheMap;

	public static void resetMap() {
		try {

			// 获取缓存器
			// MemcachedClient memcachedClient = SpringContextHolder
			// .getBean("memcachedClient");
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");

			String sql = "select a.assignee_ as assignee, b.group_id_ as group_id, a.id_ as task_id,a.create_time_ as create_time from act_ru_task a, act_ru_identitylink b, act_hi_actinst aha, act_ct_mapping acm where a.id_ = b.task_id_ and a.id_ = aha.task_id_ and aha.proc_inst_id_ = acm.procinstid   and aha.act_name_ <>'订单审绘重审' and b.group_id_ <>'gp_drawing'";

			List<Map<String, Object>> queryList = jdbcTemplate
					.queryForList(sql);

			// 从缓存器中获取数据，如果有则提取，没有则新建
			// 任务节点缓存结构为：Map<String,Map<String,List<String>>> 参数为
			// Map<assignee,Map<groupId,List<TaskId>>>
			// 将查询出来的结果直接覆盖缓存
			List<Map<String, Object>> gpDrawing = jdbcTemplate
					.queryForList("select a.task_id,a.assignee,'gp_drawing' as group_id,a.create_time_ as create_time from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' and a.task_id in(select d2.task_id from sale_header a1,sale_item b1,material_head c1, view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and   (c1.file_Type='1' or  c1.file_Type is null)  and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_");
			queryList.addAll(gpDrawing);
			List<Map<String, Object>> gpDrawingCAD = jdbcTemplate
					.queryForList("select a.task_id,a.assignee,'gp_drawing_cad' as group_id,a.create_time_ as create_time from view_bpm_activity_task a,sale_header b where a.uuid=b.id and a.group_id='gp_drawing' and a.task_id not in(select d2.task_id from sale_header a1,sale_item b1,material_head c1, view_bpm_activity_task d2  where a1.id=b1.pid and b1.material_head_id=c1.id and d2.uuid=a1.id and  (c1.file_Type='1' or c1.file_type is null) and d2.group_id='gp_drawing' group by d2.task_id,c1.file_Type   having count(*)>0) order by a.create_time_");
			queryList.addAll(gpDrawingCAD);
			Map<String, Map<String, Map<String, String>>> cacheMap = new HashMap<String, Map<String, Map<String, String>>>();
			for (Map<String, Object> map : queryList) {
				// 获取参数
				String assignee = (map.get("assignee") == null ? "Unclaimed"
						: (String) map.get("assignee"));
				String groupId = (String) map.get("group_id");
				String taskId = (String) map.get("task_id");
				Timestamp createTime = (Timestamp) map.get("create_time");
				// 赋值
				Map<String, Map<String, String>> assigneeTaskMap;
				Map<String, String> taskList;
				if (cacheMap.get(assignee) == null) {
					cacheMap.put(assignee,
							new HashMap<String, Map<String, String>>());
					cacheMap.get(assignee).put("gp_drawing_2020",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_shiftcount",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing_cad",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing_imos",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_valuation",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_customer_service",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_finance",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_store",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_store_customer",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_material",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_hole_examine",
							new HashMap<String, String>());
				}
				assigneeTaskMap = cacheMap.get(assignee);
				// if (cacheMap.get(assignee).get(groupId) == null) {
				// cacheMap.get(assignee).put(groupId,
				// new HashMap<String, String>());
				// }
				taskList = assigneeTaskMap.get(groupId);
				if(taskList!=null) {
					if (taskList.get(taskId) != null) {
						continue;
					} else {
						//jdbcTemplate.update("insert into task_msg7(task_id,group_id,assignee,status,create_time) values('"+taskId+"','"+groupId+"','"+assignee+"',1,sysdate)");
						taskList.put(taskId, createTime.getTime() + "");
					}
				}
			}
			// memcachedClient.set(Constants.CACHE_TASK_LIST,0,cacheMap);
			MemoryCacheUtil.cacheMap = cacheMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void endFlow(String srcTaskId, String groupId, boolean flag) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		String assignee = null;
		try {
			TaskService taskService = SpringContextHolder.getBean("taskService");
			Task task = taskService.createTaskQuery().taskId(srcTaskId).singleResult();
			List<Map<String, Object>> list = new java.util.ArrayList<Map<String, Object>>();
			if (groupId == null) {
				list = jdbcTemplate
						.queryForList("select b.group_id_ from act_ru_task a,act_hi_identitylink b where a.id_=b.task_id_ and a.id_='"
								+ srcTaskId + "'");
				if (list != null && list.size() > 0) {
					groupId = list.get(0).get("group_id_") == null ? "" : list
							.get(0).get("group_id_").toString();
					if ("gp_drawing".equals(groupId)) {
						String uuid = jdbcTemplate
								.queryForObject(
										"   select acm.id from act_ru_task aha,act_ct_mapping acm where aha.proc_inst_id_=acm.procinstid and aha.id_='"
												+ srcTaskId + "'", String.class);
						List<Map<String, Object>> fileCount = jdbcTemplate
								.queryForList("select * from (select a1.ID from sale_header a1, sale_item b1, material_head c1 where a1.id = b1.pid and b1.material_head_id = c1.id and (c1.file_Type = '1' or c1.file_type is null) group by a1.id, c1.file_Type having count(*) > 0) where id = '"
										+ uuid + "'");
						if (fileCount.size() > 0) {
							groupId = "gp_drawing";
						} else {
							groupId = "gp_drawing_cad";
						}
					}
				}
			}
			groupId = groupId.trim();

			if ((groupId != null && groupId.contains("gp_store"))
					|| (task == null || task.getAssignee() == null || StringUtils
							.isEmpty(task.getAssignee()))) {
				assignee = "Unclaimed";
			} else {
				assignee = task.getAssignee();
			}
			if (cacheMap != null) {
				if (cacheMap.get(assignee) != null
						&& cacheMap.get(assignee).size() > 0) {
					if (cacheMap.get(assignee).get(groupId) != null
							&& cacheMap.get(assignee).get(groupId).size() > 0) {
						if (cacheMap.get(assignee).get(groupId).containsKey(
								srcTaskId)) {
							cacheMap.get(assignee).get(groupId).remove(
									srcTaskId);
						}
					}
				}
			}
			if(cacheMap.get(assignee).get(groupId)!=null) {
				if (cacheMap.get(assignee).get(groupId).containsKey(srcTaskId)) {
					System.out.println("结束失败");
					endFlow(srcTaskId, groupId, flag);
				}
			}
//			list = jdbcTemplate
//					.queryForList("select * from task_msg where task_id ='"
//							+ srcTaskId + "'");
//			if (list == null || list.size() <= 0) {
//				jdbcTemplate
//						.update("insert into task_msg(task_id,group_id,status) values('"
//								+ srcTaskId + "','" + groupId + "'," + 2 + ")");
//			} else {
//				jdbcTemplate
//						.update("update task_msg set status=2 where task_id='"
//								+ srcTaskId + "'");
//			}
//			jdbcTemplate.update("insert into task_msg6(task_id,group_id,assignee,status,CREATE_TIME) values('"+srcTaskId+"','"+groupId+"','"+assignee+"',2,SYSDATE)");
			// if (flag) {
			// list = jdbcTemplate
			// .queryForList(" select * from send_task where task_id='"
			// + srcTaskId + "' and task_type=0");
			// if (list.size() == 0) {
			// jdbcTemplate
			// .update("insert into send_task(task_id,group_id,assignee,task_type,status) values('"
			// + srcTaskId
			// + "','"
			// + groupId
			// + "','239',0,1)");
			// } else {
			// jdbcTemplate
			// .update("update send_task set status=1,assignee='239' where task_id='"
			// + srcTaskId + "' and task_type=0");
			// }
			// }

		} catch (NullPointerException npe) {
			//jdbcTemplate.update("insert into task_msg5(task_id,group_id,assignee,status) values('"+srcTaskId+"','"+groupId+"','"+assignee+"',2)");
			MemoryCacheUtil.resetMap();
			npe.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void startFlow(String assignee,String srcTaskId, String groupId,
			long createTime) {
		JdbcTemplate jdbcTemplate = SpringContextHolder
		.getBean("jdbcTemplate");
		try {
			TaskService taskService = SpringContextHolder
					.getBean("taskService");
			srcTaskId = srcTaskId.trim();
			Task task = taskService.createTaskQuery().taskId(srcTaskId)
					.singleResult();
			if(assignee==null){
				if (task == null || task.getAssignee() == null
						|| StringUtil.isEmpty(task.getAssignee())) {
					assignee = "Unclaimed";
				} else {
					assignee = task.getAssignee();
				}
			}
			if (groupId == null || StringUtil.isEmpty(groupId)) {
				groupId = jdbcTemplate
						.queryForObject(
								"select b.group_id_ from act_ru_task a,act_ru_identitylink b where a.id_=b.task_id_ and a.id_='"
										+ srcTaskId + "'", String.class);
				if (groupId != null) {
					// 针对GP_drawing 和cad区分
					if ("gp_drawing".equals(groupId)) {
						// String
						// sql="   select acm.id from act_hi_actinst aha,act_ct_mapping acm where aha.proc_inst_id_=acm.procinstid and aha.act_id_='"+srcTaskId+"'";
						String uuid = jdbcTemplate
								.queryForObject(
										"   select acm.id from act_ru_task aha,act_ct_mapping acm where aha.proc_inst_id_=acm.procinstid and aha.id_='"
												+ srcTaskId + "'", String.class);
						List<Map<String, Object>> fileCount = jdbcTemplate
								.queryForList("select * from (select a1.ID from sale_header a1, sale_item b1, material_head c1 where a1.id = b1.pid and b1.material_head_id = c1.id and (c1.file_Type = '1' or c1.file_type is null) group by a1.id, c1.file_Type having count(*) > 0) where id = '"
										+ uuid + "'");
						if (fileCount.size() > 0) {
							groupId = "gp_drawing";
						} else {
							groupId = "gp_drawing_cad";
						}
					}
				}
			}
			if (groupId != null && !StringUtil.isEmpty(groupId)) {
				groupId = groupId.trim();

			}
			if (cacheMap != null) {
				if (cacheMap.get(assignee) != null
						&& cacheMap.get(assignee).size() > 0) {
					if (cacheMap.get(assignee).get(groupId).get(srcTaskId) == null) {
						cacheMap.get(assignee).get(groupId).put(srcTaskId,
								createTime + "");
					}
				} else {
					cacheMap.put(assignee,
							new HashMap<String, Map<String, String>>());
					cacheMap.get(assignee).put("gp_drawing_2020",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing_cad",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_shiftcount",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing_imos",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_valuation",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_customer_service",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_drawing",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_finance",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_store",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_store_customer",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_material",
							new HashMap<String, String>());
					cacheMap.get(assignee).put("gp_hole_examine",
							new HashMap<String, String>());
					cacheMap.get(assignee).put(groupId,
							new HashMap<String, String>());
					cacheMap.get(assignee).get(groupId).put(srcTaskId,
							createTime + "");
				}
			}
//			List<Map<String, Object>> list = jdbcTemplate
//					.queryForList("select * from task_msg4 where task_id ='"
//							+ srcTaskId + "'");
//			if (list == null || list.size() <= 0) {
//				jdbcTemplate
//						.update("insert into task_msg4(task_id,group_id,status) values('"
//								+ srcTaskId + "','" + groupId + "'," + 1 + ")");
//			} else {
//				jdbcTemplate
//						.update("update task_msg4 set status=1 where task_id='"
//								+ srcTaskId + "'");
//			}
//
//			list =
//			// new
//			// ArrayList<Map<String,Object>>();
//			jdbcTemplate
//					.queryForList("select * from task_msg2 where task_id ='"
//							+ srcTaskId + "'");
//			if (list == null || list.size() <= 0) {
//				jdbcTemplate
//						.update("insert into task_msg2(task_id,assignee) values('"
//								+ srcTaskId + "','" + assignee + "')");
//			} else {
//				jdbcTemplate.update("update task_msg2 set assignee='"
//						+ assignee + "' where task_id='" + srcTaskId + "'");
//			}

			// if (flag) {
			// list = jdbcTemplate
			// .queryForList(" select * from send_task where task_id='"
			// + srcTaskId + "' and task_type=1");
			// if (list.size() == 0) {
			// jdbcTemplate
			// .update("insert into send_task(task_id,group_id,assignee,task_type,status) values('"
			// + srcTaskId
			// + "','"
			// + groupId
			// + "','239',1,1)");
			// } else {
			// jdbcTemplate
			// .update("update send_task set status=1,assignee='239' where task_id='"
			// + srcTaskId + "' and task_type=1");
			// }
			// }
			// System.out.println(cacheMap.get(assignee).get(groupId).get(srcTaskId));
			if (!cacheMap.get(assignee).get(groupId).containsKey(srcTaskId)) {
				// System.out.println("fine");
				startFlow(assignee,srcTaskId, groupId, createTime);
			}
			// Check(srcTaskId, groupId);
		} catch (NullPointerException npe) {
			//jdbcTemplate.update("insert into task_msg5(task_id,group_id,assignee,status) values('"+srcTaskId+"','"+groupId+"','"+assignee+"',1)");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//jdbcTemplate.update("insert into task_msg5(task_id,group_id,assignee,status) values('"+srcTaskId+"','"+groupId+"','"+assignee+"',1)");
			e.printStackTrace();
		}
	}

	public static void Check(String taskId, String groupId) {
		try {
			TaskService taskService = SpringContextHolder
					.getBean("taskService");
			JdbcTemplate jdbcTemplate = SpringContextHolder
					.getBean("jdbcTemplate");
			Task task = taskService.createTaskQuery().taskId(taskId)
					.singleResult();
			String assignee = null;
			if (task == null || task.getAssignee() == null
					|| StringUtil.isEmpty(task.getAssignee())) {
				assignee = "Unclaimed";
			} else {
				assignee = task.getAssignee();
			}
			if (groupId == null || StringUtil.isEmpty(groupId)) {
				groupId = jdbcTemplate
						.queryForObject(
								"select b.group_id_ from act_ru_task a,act_ru_identitylink b where a.id_=b.task_id_ and a.id_='"
										+ taskId + "'", String.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
