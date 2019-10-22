package com.main.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.directwebremoting.export.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.log.SysoCounter;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SysUser;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ZStringUtils;

/**
 * 报表控制器
 */
@Controller
@RequestMapping("/main/saleReport/*")
public class SaleReportController extends BaseController {
	@Autowired
	TaskService taskService;
    private static final Logger logger = LoggerFactory.getLogger(SaleReportController.class);

    /**
     * 列出所有流程模板
     */
    @RequestMapping(value = { "/query" }, method = RequestMethod.GET)
    public ModelAndView list(ModelAndView mav) {
        mav.setViewName("core/index");

        if ("1".equals(this.getRequest().getParameter("queryType"))) {
            mav.addObject("module", "SaleReportUserApp");
        } else {
            mav.addObject("module", "SaleReportApp");
        }
        return mav;
    }
    /**
     * 订单审绘产能报表投屏
     * @param groupId
     * @return
     */
    @RequestMapping(value="/getActUserdata",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
	public List<Map<String, Object>> getActUserdata(String groupId){
    	String today=new SimpleDateFormat("YYYY-MM-dd").format(new Date());
    	//所有信息
    	String str="";
    	
    	if(groupId.equals("1")){
    		str="oe.post1 !='物料审核'";
    	}else{
    		str="oe.post1 ='物料审核'";
    	}
    	String sql="select distinct oe.post1 ,oe.user_name,nvl(oe.cab_target,0) as cab_target,oe.rows_number from ord_examine oe where oe.rows_number !='0' and oe.log_date=date'"+today+"' and "+str+" order by rows_number desc";
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    	//产能前3名
    	String sql1="select * from ( select distinct oe.post1 ,oe.user_name,nvl(oe.cab_target,0) as cab_target,oe.rows_number from ord_examine oe where oe.rows_number !='0' and oe.log_date=date'"+today+"' and "+str+" order by rows_number desc ) where rownum<4";//
    	List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql1);
    	if(list.size()>0){
    		for (Map<String, Object> map : list) {
    			list1.add(map);
    		}
    	}
    	return list1;
    }
    /**
     * 遗留订单标识
     * @param ids
     * @return
     */
    @RequestMapping(value="/stayOrderPay",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Message stayOrderPay(@RequestParam(value = "ids") String [] ids){
    	Message msg=null;
    	for (String orderCode : ids) {
    		String sql="select count(*) from stay_order_pay so where so.order_code='"+orderCode+"'";
    		int count = jdbcTemplate.queryForObject(sql, Integer.class);
    		if(count==0){
    			jdbcTemplate.execute("insert into stay_order_pay values('"+orderCode+"','YES')");
    		}
		}
    	return new Message("true","处理成功");
    	
    }
    /**
     * 修改罚单
     * @param errType
     * @param tackit
     * @param errDesc
     * @param orderCodePosex
     * @param acoeId
     * @return
     */
    @RequestMapping(value="/saveOrdErrType",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public Message saveOrdErrType(String errType,String tackit,String errDesc,String orderCodePosex,String acoeId){
    	//修改权限控制
    	SysUser sysUser = (SysUser) this.getRequest().getSession().getAttribute("CURR_USER");
    	String userId = sysUser.getId();
    	if(!(userId.equals("admin")||userId.equals("cl00105")||userId.equals("cl00046")||userId.equals("cl00002")||userId.equals("cl00129")
    			||userId.equals("cl00147")||userId.equals("cl00044")||userId.equals("cl00075"))){
    		return new Message("true","无权限修改!");
    	}
//    	if(tackit.equals("1")){
    		try{
    			List<Map<String, Object>> list = jdbcTemplate.queryForList("select aot.assignee2,substr(aot.tackit_time,0,10) as tackit_time from act_ord_tackit aot where aot.tackit_status='Y'and aot.order_code_posex='"+orderCodePosex+"'");
    			if(list!=null&&list.size()>0){
    				String user1=list.get(0).get("assignee2").toString();//责任人
        			String tickit_time=list.get(0).get("tackit_time").toString();//罚单时间
            		//出错记录表取消罚单
        			jdbcTemplate.update("update act_ct_ord_err ac set ac.err_type='"+errType+"',ac.err_desc='"+errDesc+"'where ac.id='"+acoeId+"'");
        			//产能表取消罚单
        			jdbcTemplate.execute("update act_day_log al set al.punish_count=nvl(al.punish_count,0)-1 where al.user_name='"+user1+"' and al.log_date =date '"+tickit_time+"' and al.user_group='gp_drawing_main'");
        			//行项目表取消罚单
        			jdbcTemplate.execute("update sale_item si set si.tackit='0'where si.order_code_posex='"+orderCodePosex+"'");
            		//罚单环节表取消罚单
        			jdbcTemplate.execute("update act_ct_ord_err ao set ao.tackit='0'where ao.mapping_sid='"+orderCodePosex+"'and ao.tackit='1'");
            		jdbcTemplate.execute("update act_ord_tackit aot set aot.tackit_status='N',aot.update_time=sysdate,aot.update_user='"+userId+"' where aot.tackit_status='Y'and aot.order_code_posex='"+orderCodePosex+"'");
    			}else{
    				return new Message("true","责任人无数据,保存失败!");
    			}
        	}catch(Exception e){
        		e.getStackTrace();
        		return new Message("true","保存失败!");
        	}
//    	}else{
//    		return new Message("true","违法操作!");
//    	}
    	return new Message("true","保存成功");
    }
    
    /**
     * 罚单LED投屏产能查询
     * @return
     */
    @RequestMapping(value="/getUserdata",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
	public List<Map<String, Object>> getUserdata(String groupId){
    	String today=new SimpleDateFormat("YYYY-MM-dd").format(new Date());
    	//质量最后3名
    	String sql1="select * from (select a.*, rownum rn from (select cd.drawing_group,"
    			+ "cd.user_name, cd.order_count, cd.rows_number, cd.sj_count, cd.quality from CAPACITY_DAY cd "
    			+ "where cd.user_name is not null and cd.drawing_group in('GROUP_01', 'GROUP_02', 'GROUP_03', 'GROUP_04')"
    			+ "and cd.log_date = date '"+today+"' and cd.assignee !='cl00039' and cd.assignee !='cl00117' and cd.quality !='0%' order by CAST(substr(cd.quality,0,instr(cd.quality,'%')-1) AS number(10,2))asc) a where rownum <= 3)where rn > 0";
    	List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);
    	//产能前3名
    	String sql2="select * from (select a.*, rownum rn from (select cd.drawing_group,"
    			+ "cd.user_name, cd.order_count, cd.rows_number, cd.sj_count, cd.quality from CAPACITY_DAY cd "
    			+ "where cd.user_name is not null and cd.drawing_group in('GROUP_01', 'GROUP_02', 'GROUP_03', 'GROUP_04')"
    			+ "and cd.log_date = date '"+today+"' and cd.assignee !='cl00039' and cd.assignee !='cl00117' order by cd.rows_number desc ) a where rownum <= 3)where rn > 0";
    	List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql2);
    	for (Map<String, Object> map : list2) {
			list.add(map);
		}
    	//分组排名
    	String sql="select cd.drawing_group,cd.user_name,cd.order_count,cd.rows_number,cd.sj_count,cd.quality,cd.cab_target,cd.pun_count "
    			+ "from CAPACITY_DAY cd where cd.user_name is not null and cd.drawing_group ='"+groupId+"'"
    			+ "and cd.log_date = date'"+today+"' and cd.assignee !='cl00039' and cd.assignee !='cl00117' order by cd.rows_number desc";
    	List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql);
    	for (Map<String, Object> map : list1) {
			list.add(map);
		}
		return list;
	}
   /**
    * 开罚单
    * @param ticket 
    * @param orderCodePosex
    * @param nextflow 下一环节
    * @param tackit_currentFlow 当前环节
    */
    @RequestMapping(value="/getTicket",method={RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public void getTicket(String ticket,String orderCodePosex,String nextflow,String tackit_currentFlow){
    	SysUser sysUser = (SysUser) this.getRequest().getSession().getAttribute("CURR_USER");
    	String user = sysUser.getId();//发现人
    	List<Map<String, Object>> listmap = jdbcTemplate.queryForList("select distinct si.tackit as tackit from sale_item si where si.order_code_posex='"+orderCodePosex+"'");
    	if(orderCodePosex.length()<14) {
			return;
		}
    	if(listmap.size()>0&&listmap.get(0).get("tackit")!=null){
    		if(listmap.get(0).get("tackit").equals("1")){
    			return;
    		}
    	}
    	if(ticket!=null&&"1".equals(ticket)&&!"".equals(orderCodePosex)){
    		String sql="select distinct aha.assignee_  userId,max(to_char(aha.end_time_,'yyyy-mm-dd')) time from act_hi_actinst aha left join act_ct_mapping cm on cm.procinstid=aha.proc_inst_id_ left join sale_header sh on sh.id=cm.id where sh.order_code='"+orderCodePosex.substring(0, 13)+"' and aha.act_name_='订单审绘' group by aha.assignee_";
    		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
    		String userID = list.get(0).get("USERID").toString();//责任人
    		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
    		df.format(new Date());
    		jdbcTemplate.execute("update act_day_log al set al.punish_count=nvl(al.punish_count,0)+1 where al.assignee='"+userID+"' and al.log_date =date '"+df.format(new Date())+"' and al.user_group='gp_drawing_main'");
    		jdbcTemplate.execute("update sale_item si set si.tackit='1' where si.order_code_posex='"+orderCodePosex+"'");
    		List<Map<String, Object>> listTime = jdbcTemplate.queryForList("select to_char(max(acoe.create_time),'yyyy-mm-dd hh24:mi:ss') as tackit_start from act_ct_ord_err acoe where acoe.mapping_sid='"+orderCodePosex+"'");
    		String tackit_start=listTime.get(0).get("tackit_start").toString();
    		jdbcTemplate.execute("insert into act_ord_tackit values('"+orderCodePosex+"','Y','"+tackit_start+"','"+tackit_currentFlow+"','"+nextflow+"','"+user+"','"+userID+"','','')");
    	}
    }
    
    @RequestMapping(value = { "/query/matnr" }, method = RequestMethod.GET)
    public ModelAndView listMatnr(ModelAndView mav) {
        mav.setViewName("core/index");
        if ("1".equals(this.getRequest().getParameter("queryType"))) {
            mav.addObject("module", "SaleItemReportUserApp");
        } else {
            mav.addObject("module", "SaleItemReportApp");
        }

        return mav;
    }
    
    
    @RequestMapping(value = { "/list" }, method = RequestMethod.GET)
    @ResponseBody
    public JdbcExtGridBean queryForList(int page, int limit) {
        String orderCode = this.getRequest().getParameter("orderCode");
        String orderType = this.getRequest().getParameter("orderType");
        String startDate = this.getRequest().getParameter("startDate");
        String endDate = this.getRequest().getParameter("endDate");
        String shouDaFang = this.getRequest().getParameter("shouDaFang");
        String dianMianTel = this.getRequest().getParameter("dianMianTel");
        String name1 = this.getRequest().getParameter("name1");
        String tel = this.getRequest().getParameter("tel");
        String queryType = this.getRequest().getParameter("queryType");
        String startTime = this.getRequest().getParameter("startTime");
        String startTime2 = this.getRequest().getParameter("startTime2");
        String endTime = this.getRequest().getParameter("endTime");
        String endTime2 = this.getRequest().getParameter("endTime2");
        String kunnrName1 = this.getRequest().getParameter("kunnrName1");
        String orderHuanJie = this.getRequest().getParameter("orderHuanJie");
        String sapOrderCode = this.getRequest().getParameter("sapOrderCode");
        String assignee = this.getRequest().getParameter("assignee");
        String dangQianHuanJie = this.getRequest().getParameter("dangQianHuanJie");
        String orderStatus=this.getRequest().getParameter("orderStatus");
        String SQL_LIMIT="select count(1) as TOTAL from sale_report_view t where 1=1 ";
        String SQL_DATA="select t.* from sale_report_view t where 1=1 ";
        StringBuffer sb = new StringBuffer();
        // sql params
        List<Object> params = new ArrayList<Object>();
        if (!StringUtils.isEmpty(orderCode)) {
            sb.append(" and ORDER_CODE like ? ");
            params.add(StringHelper.like(String.valueOf(orderCode)));
        }
        if (!StringUtils.isEmpty(orderType)) {
            sb.append(" and ORDER_TYPE = ? ");
            params.add(orderType);
        }
        if (!StringUtils.isEmpty(startDate)) {
            sb.append(" and ORDER_DATE >= ? ");
            params.add(DateTools.strToDate(startDate, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(endDate)) {
            sb.append(" and ORDER_DATE <= ? ");
            params.add(DateTools.strToDate(endDate, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(shouDaFang)) {
            sb.append(" and SHOU_DA_FANG like ? ");
            params.add(StringHelper.like(String.valueOf(shouDaFang)));
        }
        if (!StringUtils.isEmpty(dianMianTel)) {
            sb.append(" and DIAN_MIAN_TEL like ? ");
            params.add(StringHelper.like(String.valueOf(dianMianTel)));
        }
        if (!StringUtils.isEmpty(name1)) {
            sb.append(" and NAME1 like ? ");
            params.add(StringHelper.like(String.valueOf(name1)));
        }
        if (!StringUtils.isEmpty(tel)) {
            sb.append(" and TEL like ? ");
            params.add(StringHelper.like(String.valueOf(tel)));
        }
        if (!StringUtils.isEmpty(startTime)) {
            sb.append(" and START_TIME >= ? ");
            params.add(DateTools.strToDate(startTime, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(startTime2)) {
            sb.append(" and START_TIME <= ? ");
            params.add(DateTools.strToDate(startTime2, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(endTime)) {
            sb.append(" and END_TIME >= ? ");
            params.add(DateTools.strToDate(endTime, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(endTime2)) {
            sb.append(" and END_TIME <= ? ");
            params.add(DateTools.strToDate(endTime2, DateTools.defaultFormat));
        }
        if (!StringUtils.isEmpty(kunnrName1)) {
            sb.append(" and KUNNR_NAME1 like ? ");
            params.add(StringHelper.like(String.valueOf(kunnrName1)));
        }
        if (!StringUtils.isEmpty(orderStatus)) {
        	if("1".equals(orderStatus))
        	{
        		sb.append(" and order_Status = 'QX' ");
        	}else
        	{
        		sb.append(" and order_Status is null ");
        	}
        }
        
        if (!StringUtils.isEmpty(orderHuanJie)) {
            if ("usertask_valuation".equals(orderHuanJie)) {// 价格审核
                sb.append(" and (ACT_ID = 'usertask_valuation' or  ACT_ID = 'usertask2') ");
            } else if ("usertask_finance".equals(orderHuanJie)) {// 财务确认
                sb.append(" and (ACT_ID = 'usertask_finance' or  ACT_ID = 'usertask4') ");
            } else if ("usertask_store_confirm".equals(orderHuanJie)) {// 客户确认
                sb.append(" and (ACT_ID = 'usertask_store_confirm' or  ACT_ID = 'usertask3') ");
            } else {
                sb.append(" and ACT_ID = ? ");
                params.add(orderHuanJie);
            }
        }
        if (!StringUtils.isEmpty(sapOrderCode)) {
            sb.append(" and SAP_ORDER_CODE like ? ");
            params.add(StringHelper.like(String.valueOf(sapOrderCode)));
        }
        if (!StringUtils.isEmpty(assignee)) {
            sb.append(" and ASSIGNEE like ? ");
            params.add(StringHelper.like(String.valueOf(assignee)));
        }
        if (!StringUtils.isEmpty(dangQianHuanJie)) {
            if ("usertask_valuation".equals(dangQianHuanJie)) {// 价格审核
                sb.append(" and (da_act_id = 'usertask_valuation' or  da_act_id = 'usertask2') ");
            } else if ("usertask_finance".equals(dangQianHuanJie)) {// 财务确认
                sb.append(" and (da_act_id = 'usertask_finance' or  da_act_id = 'usertask4') ");
            } else if ("usertask_store_confirm".equals(dangQianHuanJie)) {// 客户确认
                sb.append(" and (da_act_id = 'usertask_store_confirm' or  da_act_id = 'usertask3') ");
            } else {
                sb.append(" and da_act_id = ? ");
                params.add(dangQianHuanJie);
            }
        }

        // 查询自己处理的
        if ("1".equals(queryType)) {
            sb.append(" and assignee = '" + this.getLoginUserId() + "' ");
        }

       	sb.append(" order by ORDER_CODE,start_time ");

        // 获取总记录数
       	Map<String, Object> totalElements =
         jdbcTemplate.queryForMap(SQL_LIMIT+sb
         .toString(), params.toArray());
        int totalSize=((BigDecimal)totalElements.get("TOTAL")).intValue();
       	
         StringBuffer pageSQL = new StringBuffer("select * from (");
         if (page - 1 == 0) {
         pageSQL.append(SQL_DATA+sb + " ) where rownum <= ?");
         params.add(limit);
         } else {
         pageSQL.append("select row_.*, rownum rownum_ from ( " + SQL_DATA+sb
         + ") row_ where rownum <= ?) where rownum_ > ?");
         params.add(limit * page);
         params.add((page - 1) * limit);
         }

        // 总页数=总记录数/每页条数(系数加1)
         int totalPages = (totalElements.size() + limit - 1) / limit;

        // 多个时间字段转换
         Map<String, SimpleDateFormat> formatMap = new HashMap<String,
         SimpleDateFormat>();
         formatMap.put("createTime", new SimpleDateFormat("yyyy-MM"));
         formatMap.put("orderDate", new SimpleDateFormat("yyyy-MM-dd"));
         formatMap.put("endDate", new SimpleDateFormat("yyyy-MM-dd"));

        // System.out.println(pageSQL.toString());

        Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
        formats.put("orderDate", new SimpleDateFormat(DateTools.defaultFormat));
        // 获取当前分页数据
        List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL.toString(), params.toArray(), new MapRowMapper(
                true, formats));
        for (Map<String, Object> map : queryForList) {
            Object processTime = map.get("processTime");
            if (ZStringUtils.isNotEmpty(processTime)) {
                BigDecimal bd = (BigDecimal) processTime;
                long l = bd.longValue();
                map.put("processTime", DateTools.getProcessTime(l));
            }
        }
        // System.out.println(queryForList);
        return new JdbcExtGridBean(totalPages, totalSize, limit, queryForList);
    }

    @RequestMapping(value = { "/list/item" }, method = RequestMethod.GET)
    @ResponseBody
    public JdbcExtGridBean queryForListByItem(int page, int limit) {
    	 String orderCode = this.getRequest().getParameter("orderCode");
    	 String sapCode = this.getRequest().getParameter("sapCode");
         String matnr = this.getRequest().getParameter("matnr");
         String maktx = this.getRequest().getParameter("maktx");
         String materialMtart = this.getRequest().getParameter("materialMtart");
         String isStandard = this.getRequest().getParameter("isStandard");
         String startTime = this.getRequest().getParameter("startTime");
         String startTime2 = this.getRequest().getParameter("startTime2");
         String endTime = this.getRequest().getParameter("endTime");
         String endTime2 = this.getRequest().getParameter("endTime2");
         String queryType = this.getRequest().getParameter("queryType");
         String matnrHuanJie = this.getRequest().getParameter("matnrHuanJie");
         String dangQianHuanJie = this.getRequest().getParameter("dangQianHuanJie");
         String assignee = this.getRequest().getParameter("assignee");
         String stateAudit=this.getRequest().getParameter("stateAudit");
         String SQL_LIMIT="select count(1) as TOTAL from RP_ORD_SUB1 t where 1=1 ";
         String SQL_DATA="select t.* from RP_ORD_SUB1 t where 1=1 ";
         StringBuffer sb = new StringBuffer();
         // sql params
         List<Object> params = new ArrayList<Object>();

         if (!StringUtils.isEmpty(orderCode)) {
             sb.append(" and ORDER_CODE like ? ");
             params.add(StringHelper.like(String.valueOf(orderCode)));
         }
         if (!StringUtils.isEmpty(sapCode)) {
        	 sb.append(" and SAP_CODE like ? ");
        	 params.add(StringHelper.like(String.valueOf(sapCode)));
         }
         if (!StringUtils.isEmpty(matnr)) {
             sb.append(" and MATNR like ? ");
             params.add(StringHelper.like(String.valueOf(matnr)));
         }
         if (!StringUtils.isEmpty(maktx)) {
             sb.append(" and MAKTX like ? ");
             params.add(StringHelper.like(String.valueOf(maktx)));
         }
         if (!StringUtils.isEmpty(assignee)) {
             sb.append(" and assignee like ? ");
             params.add(StringHelper.like(String.valueOf(assignee)));
         }
         if (!StringUtils.isEmpty(materialMtart)) {
             sb.append(" and MTART = ? ");
             params.add(materialMtart);
         }
         if (!StringUtils.isEmpty(isStandard)) {
             sb.append(" and IS_STANDARD = ? ");
             params.add(isStandard);
         }
         if (!StringUtils.isEmpty(startTime)) {
             sb.append(" and Start_Time >= ? ");
             params.add(DateTools.strToDate(startTime, DateTools.defaultFormat));
         }
         if (!StringUtils.isEmpty(startTime2)) {
             sb.append(" and Start_Time <= ? ");
             params.add(DateTools.strToDate(startTime2, DateTools.defaultFormat));
         }
         if (!StringUtils.isEmpty(endTime)) {
             sb.append(" and END_TIME >= ? ");
             params.add(DateTools.strToDate(endTime, DateTools.defaultFormat));
         }
         if (!StringUtils.isEmpty(endTime2)) {
             sb.append(" and END_TIME <= ? ");
             params.add(DateTools.strToDate(endTime2, DateTools.defaultFormat));
         }
         if (!StringUtils.isEmpty(matnrHuanJie)) {
             sb.append(" and ACT_ID = ? ");
             params.add(matnrHuanJie);
         }
         if (!StringUtils.isEmpty(dangQianHuanJie)) {
             sb.append(" and dq_act_id = ? ");
             params.add(dangQianHuanJie);
         }
         if(!StringUtils.isEmpty(stateAudit))
         {
         	if("1".equals(stateAudit))
         	{
         		sb.append(" and STATE_AUDIT = 'QX' ");
         	}else
         	{
         		sb.append(" and (STATE_AUDIT !='QX' OR STATE_AUDIT IS NULL) ");
         	}
         }   
         // 查询自己处理的
         if ("1".equals(queryType)) {
             sb.append(" and assignee = '" + this.getLoginUserId() + "' ");
         }
         
         // 获取总记录数
         Map<String, Object> totalElements = jdbcTemplate.queryForMap(SQL_LIMIT+sb.toString(), params.toArray());
         int totalSize=((BigDecimal)totalElements.get("TOTAL")).intValue();
         
         //增加排序
        // sb.append(" order by order_code_posex,start_time ");

         
         //分页
         StringBuffer pageSQL = new StringBuffer("select * from (");
         if (page - 1 == 0) {
         pageSQL.append(SQL_DATA+sb + " ) where rownum <= ?");
         params.add(limit);
         } else {
         pageSQL.append("select row_.*, rownum rownum_ from ( " + SQL_DATA+sb
         + ") row_ where rownum <= ?) where rownum_ > ?");
         params.add(limit * page);
         params.add((page - 1) * limit);
         }

        // 总页数=总记录数/每页条数(系数加1)
         int totalPages = (totalElements.size() + limit - 1) / limit;

        // 多个时间字段转换
         Map<String, SimpleDateFormat> formatMap = new HashMap<String,
         SimpleDateFormat>();
         formatMap.put("createTime", new SimpleDateFormat(DateTools.fullFormat));
         formatMap.put("orderDate", new SimpleDateFormat("yyyy-MM-dd"));
         formatMap.put("endTime", new SimpleDateFormat(DateTools.fullFormat));

         Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
         formats.put("orderDate", new SimpleDateFormat(DateTools.defaultFormat));
         // 获取当前分页数据
         //System.out.println(pageSQL.toString());
         List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL.toString(), params.toArray(), new MapRowMapper(
                 true, formatMap));
         for (Map<String, Object> map : queryForList) {
             Object processTime = map.get("processTime");
             if (ZStringUtils.isNotEmpty(processTime)) {
                 BigDecimal bd = (BigDecimal) processTime;
                 long l = bd.longValue();
                 map.put("processTime", DateTools.getProcessTime(l));
             }
         }

         return new JdbcExtGridBean(totalPages, totalSize, limit, queryForList);
    }

}
