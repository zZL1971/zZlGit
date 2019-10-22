package com.main.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.domain.cust.CustHeader;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SysUser;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.StringHelper;

@Controller
@RequestMapping(value="/main/cart/*")
public class ShoppingCartController extends BaseController{
	@RequestMapping(value = { "/queryMaterialGrid" }, method = RequestMethod.GET)
	public ModelAndView query(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module","ShoppingCartApp");
		return mav;
	}
	/**
	 * 购物车界面grid 非标产品
	 * 
	 * @param bomNo
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return @
	 */
	@RequestMapping(value = "/goods", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryMaterialGrid(int page, int limit) {
		SysUser loginUser = super.getLoginUser();
		// 客户
		CustHeader custHeader = loginUser.getCustHeader();
		// 销售组织
		String custVkorg = "";
		// 分销渠道
		String custVtweg = "";
		if (custHeader != null) {
			custVkorg = custHeader.getVkorg();
			custVtweg = custHeader.getVtweg();
		}

		// 是否标准 1标准，0非标
		String isStandard = this.getRequest().getParameter("isStandard");
		isStandard="0";
		// 是否是"我的商品"页面请求,如果是"true"表示是"我的商品"页面请求,就过滤掉已冻结和已删除的数据
		String is4Sale = this.getRequest().getParameter("is4Sale");
		is4Sale="true";
		String matnr = this.getRequest().getParameter("matnr");// 物料编码
		String mtart = this.getRequest().getParameter("mtart");// 物料类型
		String type = this.getRequest().getParameter("type");// BZ标准，SJ散件
		type="FB";
		String mtartSJ = this.getRequest().getParameter("mtartSJ");// SJ散件 物料编码
		String vkorg = this.getRequest().getParameter("vkorg");// 销售组织
		
		String vtweg = this.getRequest().getParameter("vtweg");// 分销渠道
		
		String maktx = this.getRequest().getParameter("maktx");// 物料说明
		String matkl = this.getRequest().getParameter("matkl");// 物料组
		String orderType = this.getRequest().getParameter("orderType");// 订单类型
		String saleFor=this.getRequest().getParameter("saleFor");//销售类型
		saleFor="0";

		// sql params
		List<Object> params = new ArrayList<Object>();
		String SQL_DATA = "SELECT distinct T.*,decode(nvl(mf.id,0),'0','无','有') as ispic,(select f.zzazdr from SALE_ITEM_FJ f where f.material_head_id=t.ID and t.IS_STANDARD='0' and rownum=1) zzazdr,(select f.product_space from SALE_ITEM_FJ f where f.material_head_id=t.ID and t.IS_STANDARD='0' and rownum=1) product_space FROM MATERIAL_VIEW T,material_file mf WHERE 1=1  and mf.pid(+) = t.ID ";
		/*
		 * if("FB".equals(type)){ SQL_DATA = "SELECT T.*," +
		 * "(select sum(l.amount) from sale_header sh,sale_item l where sh.id=l.pid and nvl(sh.order_status,'C')<>'QX' and l.material_head_id=t.id) order_Count "
		 * + " FROM MATERIAL_VIEW T WHERE 1=1 "; }
		 */
		String SQL_LIMIT = "SELECT count(*) as TOTAL FROM MATERIAL_VIEW T WHERE 1=1 ";

		StringBuffer sb = new StringBuffer();
		if ("true".equals(is4Sale)) {
			sb.append(" and t.KBSTAT is null ");
			sb.append(" and t.LOEVM_KO is null ");
		}
		if ("BZ".equals(type)) {
			//橱柜的标准产品不能被查出来
			if(saleFor.equals("0")){
				sb.append(" and t.MTART in('Z101','Z008') AND t.MATKL NOT IN ('1501') and t.matnr not in('22999999995','22999999996','22999999997','22999999998','22999999999')  and instr(t.matnr,'14')<>1");//and instr(t.matnr,'14')<>1  加上此条件则橱柜	
			}else if(saleFor.equals("1")){
				sb.append(" and t.MTART in('Z101','Z008') AND t.MATKL NOT IN ('1501') and t.matnr not in('22999999995','22999999996','22999999997','22999999998','22999999999')  ");//and instr(t.matnr,'14')<>1  加上此条件则橱柜
			}
			sb.append(" and t.vkorg = ? and t.vtweg = ? ");
			params.add(custVkorg);
			params.add(custVtweg);
		} else if ("FB".equals(type)) {
			// andt.STATUS='A' 没有下过单(非起草状态订单)的非标产品
			sb.append(" and t.ROW_STATUS='A' and t.MTART ='Z101' ");
			sb.append(" and t.vkorg = ? and t.vtweg = ? and t.create_user=? ");

			params.add(custVkorg);
			params.add(custVtweg);
			params.add(super.getLoginUserId());
			//销售类型
			if(!StringUtils.isEmpty(saleFor) &&!"0".equals(saleFor)){
				sb.append(" and t.sale_for=?");
				params.add(saleFor);
			}
			String serialNumber = this.getRequest()
					.getParameter("serialNumber");// 物料组
			if (!"".equals(serialNumber) || serialNumber != null) {
				sb.append(" and t.serial_Number like ? ");
				params.add(StringHelper.like(String.valueOf(serialNumber)));
			}

		} else if ("SJ".equals(type)) {
			// 数据字典查找物料组配置
			String sjMatkl = "";
			if ("22999999996".equals(mtartSJ)) {// 22999999996销售道具
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("22999999997".equals(mtartSJ)) {// 22999999997五金
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("22999999998".equals(mtartSJ)) {// 22999999998移门散件
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("22999999995".equals(mtartSJ)) {// 22999999995柜身散件
				sjMatkl = "'" + mtartSJ + "'";
			} else {// 散件 移门，柜身
				sjMatkl = "'22999999998','22999999995'";
			}
			// System.out.println("sql1-------------->" + SQL_DATA + sb);
			// 没有配置散件对应的物料组 返回空数据
			if (StringUtils.isEmpty(sjMatkl)) {
				return new JdbcExtGridBean(0, 0, 0, new ArrayList());

			} else {
				String sql = "select t.DESC_ZH_CN, t.KEY_VAL  from sys_data_dict t  inner join sys_trie_tree t2  on t.trie_id = t2.id  where t2.KEY_VAL in ("
						+ sjMatkl + ") ";
				List<Map<String, Object>> lists = jdbcTemplate
						.queryForList(sql);

				StringBuffer sbMatkl = new StringBuffer();
				if (lists != null && lists.size() > 0) {

					for (Map<String, Object> map : lists) {
						String str = (String) map.get("KEY_VAL");
						sbMatkl.append("'").append(str).append("',");
					}

					String s1 = sbMatkl.toString();
					s1 = s1.substring(0, s1.length() - 1);

					// modify by mark on 20161205 --start
					// 物料组 再分
					// sb.append(" and t.matkl in ( ");
					// sb.append(s1);
					// sb.append(" ) ");
					sb.append(" and (t.matkl2 ='" + orderType + "' ");
					// sb.append(s1);
					// sb.append(" ) ");
					sb.append(" or (t.matkl2 is null and t.matkl in (");
					sb.append(s1);
					sb.append(" )))");
					// modify by mark on 20161205 --end

				} else {
					return new JdbcExtGridBean(0, 0, 0, new ArrayList());
				}
			}

			sb.append("  and t.MTART !='Z101' and t.vkorg = ? and t.vtweg = ? ");
			params.add(custVkorg);
			params.add(custVtweg);
		}

		if (!StringUtils.isEmpty(isStandard)) {
			sb.append(" and IS_STANDARD = ? ");
			params.add(isStandard);
		}
		if (!StringUtils.isEmpty(matnr)) {
			sb.append(" and matnr like ? ");
			params.add(StringHelper.like(String.valueOf(matnr)));
		}
		if (!StringUtils.isEmpty(mtart)) {
			sb.append(" and mtart = ? ");
			params.add(mtart);
		}
		if (!StringUtils.isEmpty(vkorg)) {
			sb.append(" and t.vkorg = ? ");
			params.add(vkorg);
		}
		if (!StringUtils.isEmpty(vtweg)) {
			sb.append(" and t.vtweg = ? ");
			params.add(vtweg);
		}
		if (!StringUtils.isEmpty(maktx)) {
			sb.append(" and t.maktx like ? ");
			params.add(StringHelper.like(String.valueOf(maktx)));
		}
		if (!StringUtils.isEmpty(matkl)) {
			sb.append(" and t.matkl = ? ");
			params.add(matkl);
		}

		// 获取总记录数
		Map<String, Object> totalElements = jdbcTemplate.queryForMap(SQL_LIMIT
				+ sb.toString(), params.toArray());
		//System.out.println(SQL_LIMIT + sb.toString());

		// int totalSize = Integer.valueOf();
		int totalSize = ((BigDecimal) totalElements.get("TOTAL")).intValue();

		sb.append(" order by t.create_time desc");
		StringBuffer pageSQL = new StringBuffer("select * from (");

		if (page - 1 == 0) {
			pageSQL.append(SQL_DATA + sb + " ) where rownum <= ? ");
			params.add(limit);
		} else {
			pageSQL.append("select row_.*, rownum rownum_ from ( " + SQL_DATA
					+ sb + ") row_ where rownum <= ?) where rownum_ > ? ");
			params.add(limit * page);
			params.add((page - 1) * limit);
		}
		// System.out.println("sql--------->" + pageSQL.toString());
		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalSize + limit - 1) / limit;
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(
				pageSQL.toString(), params.toArray(), new MapRowMapper(true));
		/*System.out.println(pageSQL.toString());
		for (int i = 0; i < params.size(); i++) {
			System.out.println(params.get(i));
		}*/
		return new JdbcExtGridBean(totalPages, totalSize, limit, queryForList);
		

	}

}
