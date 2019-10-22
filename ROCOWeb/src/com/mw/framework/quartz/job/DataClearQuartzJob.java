/**
 *
 */
package com.mw.framework.quartz.job;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.manager.MyGoodsManager;
import com.mw.framework.context.SpringContextHolder;

/**
 * 数据清理定时器
 *
 */
public class DataClearQuartzJob {
	
	private static final Logger logger = LoggerFactory.getLogger(DataClearQuartzJob.class);
    /**
     * 删除冗余数据，（商品没有下订单，我的商品界面只做标记删除，因为商品数据是在订单激活的时候才删除的）
     */
	public void run(){
	    MyGoodsManager myGoodsManager = SpringContextHolder.getBean("myGoodsManager");
	    logger.info("定时器执行---------删除冗余数据-----------");
	    myGoodsManager.deleteData();
	    
	    //批量处理时间为空问题
	    JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
	    String sql = 
	    	"update sale_header sh" +
	    	"      set sh.create_time =" + 
	    	"          (select aha.start_time_" + 
	    	"             from act_hi_actinst aha, act_ct_mapping acm" + 
	    	"            where acm.procinstid = aha.proc_inst_id_" + 
	    	"              and acm.id = sh.id" + 
	    	"              and aha.act_name_ = '开始'" + 
	    	"              )" + 
	    	"              where   CREATE_TIME IS NULL";

	    jdbcTemplate.update(sql);
	    
	    String tempsql = 
	    	"update sale_header sh" +
	    	"      set sh.create_time =" + 
	    	"          (select aha.start_time_" + 
	    	"             from act_hi_actinst aha, act_ct_mapping acm" + 
	    	"            where acm.procinstid = aha.proc_inst_id_" + 
	    	"              and acm.id = sh.id" + 
	    	"              and aha.act_name_ = '开始'" + 
	    	"              )" + 
	    	"              where substr(TO_CHAR(CREATE_tiME,'yyyy-mm-dd hh24:mi:ss'),12,8)='00:00:00'";
	    
	    jdbcTemplate.update(tempsql);
	}
}
