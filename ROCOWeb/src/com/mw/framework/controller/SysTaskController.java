/**
 *
 */
package com.mw.framework.controller;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;

/**
 * @Project MeWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.controller.SysTaskController.java
 * @Version 1.0.0
 * @author Chaly
 * @time 2015-5-11
 *
 */
@Controller
@RequestMapping("/core/task/*")
public class SysTaskController extends BaseController{
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private CommonManager commonManager;
	@RequestMapping(value={"/rear/{uid}"},method={RequestMethod.GET})
	@ResponseBody
	public Message findUserToRear(@PathVariable String uid){
		Message msg=null;
		if(!uid.isEmpty()&&uid.length()>0){
			String sql="SELECT * FROM SYS_TASK ST WHERE ST.ID='"+uid+"'";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			msg=new Message(list);
			return msg;
		}else{
			msg=new Message("rear-104","查询失败");
			return msg;
		}
	}
	@RequestMapping(value={"/save"},method={RequestMethod.POST})
	@ResponseBody
	@Transactional
	public Message saveUserToRear(String rearId,String[] id){
		Message msg=null;
		
		for (String uid : id) {
			if(!uid.isEmpty()&&uid.length()>0){
				SysUser user = commonManager.getById(uid, SysUser.class);
				if(user!=null&&rearId!=null){
					List<Map<String, Object>> booleanUser = jdbcTemplate.queryForList("SELECT * FROM SYS_TASK ST WHERE ST.ID='"+uid+"'");
					String saveSql="";
					if(booleanUser.size()>0){
						saveSql="UPDATE SYS_TASK SET REAR='"+rearId+"',UPDATE_TIME=sysdate,UPDATE_USER='"+getLoginUserId()+"' WHERE ID='"+uid+"'";
						msg=new Message("saveRear-200","更新成功");
					}else{
						saveSql="INSERT INTO SYS_TASK(ID,REAR,CREATE_TIME,CREATE_USER,UPDATE_TIME,UPDATE_USER) VALUES('"+uid+"','"+rearId+"',sysdate,'"+getLoginUserId()+"',sysdate,'"+getLoginUserId()+"')";
						msg=new Message("saveRear-200","保存成功");
					}
					jdbcTemplate.execute(saveSql);	
				}else{
					msg=new Message("saveRear-105","无法给"+uid+"授权,请联系管理员");
				}
			}else{
				msg=new Message("saveRear-101","保存失败");
			}
		}
		
		
		return msg;
	}
}
