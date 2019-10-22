package com.mw.framework.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mw.framework.commons.BaseController;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.utils.DateTools;

@RestController
@RequestMapping(value="/report")
public class OrderReportController extends BaseController{

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value="/orderDetail",method=RequestMethod.GET)
	public JdbcExtGridBean finorderDetail(int page, int limit) {
		String startTime =this.getRequest().getParameter("startTime");
		String endTime =this.getRequest().getParameter("endTime");
		String kunnr = this.getRequest().getParameter("kunnr");
		String taskName = this.getRequest().getParameter("taskName");
		List<Object> params = new ArrayList<Object>();
		StringBuffer firstSql=new StringBuffer();
		firstSql.append("SELECT (SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID='7mogniLAodfBR2shSDCmrp' AND SD.KEY_VAL=CH.BZIRK) AS BZIRK,(SELECT SD.DESC_ZH_CN FROM SYS_DATA_DICT SD WHERE SD.TRIE_ID='NRkUPhxtReuk9ckmq3gSsB' AND SD.KEY_VAL=CH.REGIO) AS REGIO,SH.ORDER_CODE,SH.ORDER_TOTAL,"
				+ "CH.NAME1,CH.KUNNR FROM SALE_HEADER SH LEFT JOIN CUST_HEADER CH ON SH.SHOU_DA_FANG = CH.KUNNR"
				+ " LEFT JOIN ACT_CT_MAPPING CM ON SH.ID=CM.ID LEFT JOIN ACT_RU_TASK RT ON CM.PROCINSTID=RT.PROC_INST_ID_ where 1=1");
		if(kunnr!=null&&!"".equals(kunnr)){
			firstSql.append(" AND CH.KUNNR= ? ");
			params.add(kunnr);
		}
		if(startTime!=null&&!"".equals(startTime)
				&&endTime!=null&&!"".equals(endTime)){
			firstSql.append(" AND (SH.ORDER_DATE >= DATE'"+startTime+"' AND SH.ORDER_DATE <= DATE'"+endTime+"')");
		}
		if(taskName!=null&&!"".equals(taskName)){
			
			firstSql.append(" AND RT.NAME_ in ('确认报价','客户确认')");
			//params.add(taskName);
		}
		String COUNT_LIMIT = "SELECT COUNT(*)AS TOTAL FROM ("+firstSql.toString()+")";
		Map<String, Object> totalElements = jdbcTemplate.queryForMap(COUNT_LIMIT,params.toArray());
		int totalSize=((BigDecimal)totalElements.get("TOTAL")).intValue();
		
		 //分页
        StringBuffer pageSQL = new StringBuffer("select * from (");
        if (page - 1 == 0) {
        pageSQL.append(firstSql + " ) where rownum <= ?");
        params.add(limit);
        } else {
        pageSQL.append("select row_.*, rownum rownum_ from ( " + firstSql
        + ") row_ where rownum <= ?) where rownum_ > ?");
        params.add(limit * page);
        params.add((page - 1) * limit);
        }
       // 总页数=总记录数/每页条数(系数加1)
        int totalPages = (totalElements.size() + limit - 1) / limit;
        Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
        formats.put("orderDate", new SimpleDateFormat(DateTools.defaultFormat));
    
        List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL.toString(), params.toArray(), new MapRowMapper(
                true, formats));
        return new JdbcExtGridBean(totalPages, totalSize, limit, queryForList);
	}
}
