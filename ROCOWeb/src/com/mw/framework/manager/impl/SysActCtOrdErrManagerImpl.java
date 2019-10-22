package com.mw.framework.manager.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.mw.framework.dao.SysActCTOrdErrDao;
import com.mw.framework.domain.SysActCTOrdErr;
import com.mw.framework.manager.SysActCtOrdErrManager;

@Service
public class SysActCtOrdErrManagerImpl implements SysActCtOrdErrManager {

	@Autowired
	private SysActCTOrdErrDao sysActCTOrdErrDao;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Override
	public void saveSysActCtOrdErrLoger1(Task task, String mappingId, String errType,String tackit, String errDesc, String nextTaskName, String mappingNo,String nextTask, String logerType) {
				List<Map<String, Object>> list = jdbcTemplate.queryForList("select si.tackit from sale_item si where si.order_code_posex='"+mappingId+"'");
				if (list!=null&&list.size()>0&&list.get(0).get("tackit")!=null&&list.get(0).get("tackit").equals("1")) {
					tackit="0";
				}
				SysActCTOrdErr ordErr = null;
					ordErr = new SysActCTOrdErr();
					ordErr.setProcinstid(task.getProcessInstanceId());
					ordErr.setProdefid(task.getProcessDefinitionId());
					ordErr.setTaskid(task.getId());
					ordErr.setExecutionId(task.getExecutionId());
					ordErr.setTaskname(task.getName());
					ordErr.setTargetTaskName(nextTaskName);
					ordErr.setErrType(errType);
					ordErr.setTackit(tackit);
					ordErr.setErrDesc(errDesc);
					ordErr.setTargetTaskDefinitionKey(nextTask);
					ordErr.setTaskDefinitionKey(task.getTaskDefinitionKey());
					if(mappingNo!=null&&!"".equals(mappingNo)) {
						ordErr.setMappingSid(mappingNo);
					}else {
						//补购客服审核阶段没有单号
						ordErr.setMappingSid("1");
					}
					ordErr.setMappingId(mappingId);
					ordErr.setLogerType(logerType);
				ordErr.setLogerNum(ordErr.getLogerNum()+1);//记录此项 出错次数
				sysActCTOrdErrDao.save(ordErr);
	}
	@Override
	public void saveSysActCtOrdErrLoger(Task task, String mappingId, String errType,String tackit, String errRea, String errDesc, String nextTaskName, String mappingNo,String nextTask, String logerType) {
		// 出错纪录，完善目标节点 保存 节点出错记录 可记录多次 重复项记录次数
//				List<SysActCTOrdErr> sysActCtOrdErrList = sysActCTOrdErrDao.findBySysActCtOrdErrList(mappingId, errType, errRea);//
//				if(sysActCtOrdErrList.size()>1) {//保持记录一致 提交如果出现两次记录 就删除 其中一条记录
//					for(Iterator<SysActCTOrdErr> sysActCtOrdErrIterator=sysActCtOrdErrList.iterator();sysActCtOrdErrIterator.hasNext();) {
//						SysActCTOrdErr sysActCtOrdErr = sysActCtOrdErrIterator.next();
//						sysActCTOrdErrDao.delete(sysActCtOrdErr);
//						sysActCtOrdErrIterator.remove();
//						break;
//					}
//				}
				List<Map<String, Object>> list = jdbcTemplate.queryForList("select si.tackit from sale_item si where si.order_code_posex='"+mappingId+"'");
				if (list!=null&&list.size()>0&&list.get(0).get("tackit")!=null&&list.get(0).get("tackit").equals("1")) {
					tackit="0";
				}
				SysActCTOrdErr ordErr = null;
//				if(sysActCtOrdErrList.size()<=0) {
					ordErr = new SysActCTOrdErr();
					ordErr.setProcinstid(task.getProcessInstanceId());
					ordErr.setProdefid(task.getProcessDefinitionId());
					ordErr.setTaskid(task.getId());
					ordErr.setExecutionId(task.getExecutionId());
					ordErr.setTaskname(task.getName());
					ordErr.setTargetTaskName(nextTaskName);
					ordErr.setErrType(errType);
					ordErr.setTackit(tackit);
					ordErr.setErrRea(errRea);
					ordErr.setErrDesc(errDesc);
					ordErr.setTargetTaskDefinitionKey(nextTask);
					ordErr.setTaskDefinitionKey(task.getTaskDefinitionKey());
					if(mappingNo!=null&&!"".equals(mappingNo)) {
						ordErr.setMappingSid(mappingNo);
					}else {
						//补购客服审核阶段没有单号
						ordErr.setMappingSid("1");
					}
					ordErr.setMappingId(mappingId);
					ordErr.setLogerType(logerType);
//				}else {
//					ordErr = sysActCtOrdErrList.get(0);
//				}
				ordErr.setLogerNum(ordErr.getLogerNum()+1);//记录此项 出错次数
				sysActCTOrdErrDao.save(ordErr);
	}
	

}
