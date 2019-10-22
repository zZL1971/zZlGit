package com.main.controller;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.validation.Valid;

import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.main.bean.MaterialBean;
import com.main.dao.CustHeaderDao;
import com.main.dao.MaterialComplainidDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.TerminalClientDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.TerminalClient;
import com.main.domain.mm.MaterialBujian;
import com.main.domain.mm.MaterialComplainid;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.MaterialSanjianHead;
import com.main.domain.mm.MaterialSanjianItem;
import com.main.domain.mm.MyGoods;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemFj;
import com.main.manager.MaterialManager;
import com.main.manager.MyGoodsManager;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.core.MyMapRowMapper;
import com.mw.framework.dao.SysUserDao;
import com.mw.framework.domain.SysUser;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.util.StringHelper;
/**
 * 我的物品
 * @author zjc
 *
 */
@Controller
@RequestMapping("/main/myGoods/*")
public class MyGoodsController  extends BaseController{
	
	private static final Logger logger = LoggerFactory.getLogger(MyGoodsController.class);
	@Autowired
	private MyGoodsManager myGoodsManager;
	@Autowired
    private MaterialManager materialManager;
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private TerminalClientDao terminalClientDao;
	@Autowired
	private CustHeaderDao custHeaderDao;
	@Autowired
	private MaterialComplainidDao materialComplainidDao;

    /**
     * 我的物品
     */
	@RequestMapping(value = {"/"}, method = RequestMethod.GET)
    public ModelAndView list(ModelAndView mav) {
        mav.setViewName("core/index");
        mav.addObject("module","MyGoodsApp");
        return mav;
    }
	/**
	 * 我的物品补件
	 */
	@RequestMapping(value = {"/bujian"}, method = RequestMethod.GET)
	public ModelAndView list2(ModelAndView mav) {
	    mav.setViewName("core/index");
	    mav.addObject("module","MyGoodsBujianApp");
	    return mav;
	}
	
	/**
	 * 保存  我的物品--标准商品
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Message  save(@RequestBody MaterialBean materialBean,BindingResult result) {
		Message msg = null;
		try {
		    MyGoods obj = myGoodsManager.saveBZ(materialBean);
			String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
			msg = new Message(JSONObject.fromObject(obj, super.getJsonConfig("yyyy-MM-dd",strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MYGOODS-SAVE-500", "保存失败!");
		}

		return msg;
	}
	/**
	 * 更新 --标准商品 附加信息
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/updateBZ", method = RequestMethod.POST)
	@ResponseBody
	public Message  updateBZ(@RequestBody MaterialBean materialBean,BindingResult result) {
	    Message msg = null;
	    try {
	        SaleItemFj saleItemFj = materialBean.getSaleItemFj();
	        
	        SaleItemFj obj = myGoodsManager.save(saleItemFj);
	        
	        String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
	        msg = new Message(JSONObject.fromObject(obj, super.getJsonConfig("yyyy-MM-dd",strings)));
	    } catch (Exception e) {
	        e.printStackTrace();
	        msg = new Message("MYGOODS-SAVE-500", "保存失败!");
	    }
	    
	    return msg;
	}
	/**
	 * 保存客诉信息
	 * @param Pid
	 * @param materialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveComplainid", method = RequestMethod.POST)
	@ResponseBody
	public Message saveComplainid(String Pid, @RequestBody MaterialBean materialBean, BindingResult result) {
		Message msg = null;
		System.out.println(materialBean);
		
		String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
		msg = new Message(JSONObject.fromObject( super.getJsonConfig("yyyy-MM-dd",strings)));
		return msg;
	}
	
	/**
	 * 保存散件
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveSJ", method = RequestMethod.POST)
	@ResponseBody
	public Message  saveSJ(String Pid, @RequestBody MaterialBean materialBean, BindingResult result) {
	    Message msg = null;
        try {
            //物料查找销售组织和分销渠道
            SysUser loginUser = super.getLoginUser();
            materialBean.setSysUser(loginUser);
            List<Map<String, Object>> list = jdbcTemplate.queryForList("select * from my_goods_sanjian");
            for (Map<String, Object> map : list) {
            	String matnr = map.get("matnr").toString();
            	int num = Integer.parseInt(map.get("num").toString());
            	List<MaterialSanjianItem> materialSanjians = materialBean.getMaterialSanjians();
            	for (MaterialSanjianItem materialSanjianItem : materialSanjians) {
            		if(materialSanjianItem.getMatnr().equals(matnr)){
            			int amount = materialSanjianItem.getAmount();
            			if(amount<num||amount%num!=0){
            				msg = new Message("MYGOODS-SAVESJ-500", "产品编号:"+matnr+"的起订数为:"+num+"n(n>0)");
            				return msg;
            			}
            		}
            		
				}
			}
            MaterialSanjianHead obj = myGoodsManager.saveSJ(materialBean,Pid);
            if(obj!=null){
                String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort","materialSanjianItemSet"};
                msg = new Message(JSONObject.fromObject(obj, super.getJsonConfig("yyyy-MM-dd",strings)));
            }else{
                MaterialSanjianHead temp = materialBean.getMaterialSanjianHead();
                msg = new Message("MYGOODS-SAVESJ-500", "产品编号"+temp.getMatnr()+"不存在,保存失败,请同步SAP!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = new Message("MYGOODS-SAVESJ-500", "保存失败!");
        }
	    return msg;
	}
	/**
	 * 保存补件
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBJ", method = RequestMethod.POST)
	@ResponseBody
	public Message  saveBJ(String Pid,@RequestBody MaterialBean materialBean, BindingResult result) {
	    Message msg = null;
	    try {
	    	//System.out.println(this.getRequest().getParameter("ComplaintTime"));
	        //Timestamp ComplaintTime=new Timestamp(new SimpleDateFormat("yyyy-MM-dd").parse(this.getRequest().getParameter("ComplaintTime")).getTime());
	       // materialBean.getMaterialBujian().setComplaintTime(ComplaintTime);
	         List<MaterialComplainid> obj = myGoodsManager.saveKS(materialBean,Pid);
	        if(obj!=null){
	        	MaterialComplainid MaterialComplainid = obj.get(0);
	        	MaterialComplainid.getComplaintTime();
	            String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
                msg = new Message(MaterialComplainid.getComplaintTime());
	        }else{
	            msg = new Message("MYGOODS-saveBJ-500", "保存失败!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        msg = new Message("MYGOODS-saveBJ-500", "保存失败!");
	    }
	    return msg;
	}
	
	
	/**
	  * 回写费用化信息
	 * @return
	 */
	@RequestMapping(value = "/queryMyBjList", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMyBjList(String pid) {
		List<Map<String, Object>> querForList = new ArrayList<Map<String,Object>>();
		SaleHeader saleHead = saleHeaderDao.findOne(pid);
		Set<SaleItem> saleItem = saleHead.getSaleItemSet();
		if(saleItem.isEmpty()) {
			return JSONArray.fromObject(querForList);
		}else {
			String myGoodIdSql = "SELECT SI.MY_GOODS_ID FROM SALE_ITEM SI WHERE SI.PID='"+pid+"' AND SI.POSEX='10'";
			String myGoodId = jdbcTemplate.queryForObject(myGoodIdSql, String.class);
			if(myGoodId!=null&&!"".equals(myGoodId)) {
				StringBuffer sb = new StringBuffer(
						"SELECT T.* FROM MY_GOODS_VIEW T WHERE 1=1 AND T.ID='"+myGoodId+"'");
				querForList = jdbcTemplate.query(sb.toString(), new MapRowMapper(true));
			}
		}

		return JSONArray.fromObject(querForList);
	}
	
	/**
	 * 查询列表界面grid 我的物品
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "/queryMyGoodsList", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryMyGoodsList(int page, int limit)  {
		SysUserDao sysUserDao = SpringContextHolder.getBean("sysUserDao");
		SysUser loginUser = super.getLoginUser();
		String vkorg="";
		String vtweg="";
		 String kunnr = this.getRequest().getParameter("kunnr");
		 String bgOrderType = this.getRequest().getParameter("bgOrderType");
		if("OR3".equals(bgOrderType)||"OR4".equals(bgOrderType)) {
			CustHeader bgCustHeader = custHeaderDao.finByKunnr(kunnr);
			//销售组织
			 vkorg = bgCustHeader.getVkorg();
			//分销渠道
			 vtweg = bgCustHeader.getVtweg();
		}else {
			//客户
			SysUser sysUser = sysUserDao.getOne(getLoginUserId());
			CustHeader custHeader = sysUser.getCustHeader();
			//销售组织
			vkorg = custHeader.getVkorg();
			//分销渠道
			vtweg = custHeader.getVtweg();
		}
		//是否标准  1标准，0非标      
		String isStandard = this.getRequest().getParameter("isStandard");

		String serialNumber = this.getRequest().getParameter("serialNumber");//非标产品 流水号
        String matnr = this.getRequest().getParameter("matnr");//物料编码
        String mtart = this.getRequest().getParameter("mtart");//物料类型
        String ortype = this.getRequest().getParameter("ortype");//订单类型，订单加产品时的必要条件
        String maktx = this.getRequest().getParameter("maktx");//物料说明


		StringBuffer sb = new StringBuffer(
				"SELECT T.* FROM MY_GOODS_VIEW T WHERE 1=1 ");
		sb.append(" and t.vkorg = ? and t.vtweg = ? and t.create_user = ?  ");
		// sql params
		List<Object> params = new ArrayList<Object>();
		params.add(vkorg);
		params.add(vtweg);
		params.add(getLoginUserId());

		if (!StringUtils.isEmpty(isStandard)) {
			sb.append(" and IS_STANDARD = ? ");
			params.add(isStandard);
		}
		if (!StringUtils.isEmpty(serialNumber)) {
			sb.append(" and SERIAL_NUMBER like ? ");
			params.add(StringHelper.like(String.valueOf(serialNumber)));
		}
		if (!StringUtils.isEmpty(matnr)) {
            sb.append(" and matnr like ? ");
            params.add(StringHelper.like(String.valueOf(matnr)));
        }
        if (!StringUtils.isEmpty(mtart)) {
            sb.append(" and mtart = ? ");
            params.add(mtart);
        }
        if (!StringUtils.isEmpty(ortype)) {
            sb.append(" and ortype = ? ");
            params.add(ortype);
        }
        if (!StringUtils.isEmpty(maktx)) {
            sb.append(" and maktx like ? ");
            params.add(StringHelper.like(String.valueOf(maktx)));
        }

		sb.append(" order by T.CREATE_TIME  ");
		// 获取总记录数
		List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sb
				.toString(), params.toArray());

		StringBuffer pageSQL = new StringBuffer("select * from (");
		if (page - 1 == 0) {
			pageSQL.append(sb + " ) where rownum <= ? ");
			params.add(limit);
		} else {
			pageSQL.append("select row_.*, rownum rownum_ from ( " + sb
					+ ") row_ where rownum <= ?) where rownum_ > ? ");
            params.add(limit*page);
            params.add((page - 1)*limit);
		}

		// 总页数=总记录数/每页条数(系数加1)
		int totalPages = (totalElements.size() + limit - 1) / limit;
		// 获取当前分页数据
		List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
				.toString(), params.toArray(), new MapRowMapper(true));

		return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
				queryForList);
	}
	/**
	 * 查询列表界面grid 我的物品 客服补件
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "/queryMyGoodsBujianList", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryMyGoodsBujianList(int page, int limit)  {
	    
	    SysUser loginUser = super.getLoginUser();
	    
	    String ortype = this.getRequest().getParameter("ortype");//订单类型，订单加产品时的必要条件
	    String maktx = this.getRequest().getParameter("maktx");//物料说明
	    
	    
	    StringBuffer sb = new StringBuffer(
	    "SELECT T.* FROM MATERIAL_BUJIAN_VIEW T WHERE 1=1 ");
	    sb.append(" and t.create_user = ?  ");
	    // sql params
	    List<Object> params = new ArrayList<Object>();
	    params.add(loginUser.getId());
	   
	    if (!StringUtils.isEmpty(ortype)) {
	        sb.append(" and ortype = ? ");
	        params.add(ortype);
	    }
	    if (!StringUtils.isEmpty(maktx)) {
	        sb.append(" and maktx like ? ");
	        params.add(StringHelper.like(String.valueOf(maktx)));
	    }
	    
	    sb.append(" order by T.CREATE_TIME  ");
	    // 获取总记录数
	    List<Map<String, Object>> totalElements = jdbcTemplate.queryForList(sb
	            .toString(), params.toArray());
	    
	    StringBuffer pageSQL = new StringBuffer("select * from (");
	    if (page - 1 == 0) {
	        pageSQL.append(sb + " ) where rownum <= ? ");
	        params.add(limit);
	    } else {
	        pageSQL.append("select row_.*, rownum rownum_ from ( " + sb
	                + ") row_ where rownum <= ?) where rownum_ > ? ");
	        params.add(limit*page);
	        params.add((page - 1)*limit);
	    }
	    
	    // 总页数=总记录数/每页条数(系数加1)
	    int totalPages = (totalElements.size() + limit - 1) / limit;
	    // 获取当前分页数据
	    List<Map<String, Object>> queryForList = jdbcTemplate.query(pageSQL
	            .toString(), params.toArray(), new MapRowMapper(true));
	    
	    return new JdbcExtGridBean(totalPages, totalElements.size(), limit,
	            queryForList);
	}
	
	/**
	 * 删除 我的物品
	 * 根据id数组 
	 * @param ids
	 * @return
	 * @
	 */
	@RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteByIds(String[] ids,String loadStatus) {
		Message msg = null;
		try {
		    //我的商品 删除标记删除   "2".equals(loadStatus)
		    //订单激活后物理删除       "3".equals(loadStatus)
		    myGoodsManager.deleteByIds(ids, loadStatus);
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MYGOODS-DELETE-500", "删除失败!");
		}
		return msg;
	}
	/**
	 * 订单激活后操作
	 */
	@RequestMapping(value = "/saleAfterActivate", method = RequestMethod.GET)
	@ResponseBody
	public Message saleAfterActivate(String saleHeadId) {
	    
        List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("select t2.MY_GOODS_ID from sale_header t inner join sale_item t2 on t.id = t2.pid where t.id='"+saleHeadId+"' AND T2.MAKTX<>'费用化'");
        List<String> list = new ArrayList<String>();
        for (Map<String, Object> map : queryForList) {
            list.add((String) map.get("MY_GOODS_ID"));
        }
        String[] array = list.toArray(new String[list.size()]);
        if(array.length>0) {
        	this.deleteByIds(array,"3");
        }
	    return null;
	}
	
	/**
	 * 补购订单激活前
	 * @return
	 */
    @RequestMapping(value = { "/saleBdBoforeActivate" }, method = RequestMethod.GET)
    @ResponseBody
	public Message saleBdBoforeActivate(String saleHeadId) {
        Message msg = null;
        StringBuffer sb = null;
		String sql ="SELECT * FROM SALE_BG_FILE WHERE  PID ='"+saleHeadId+"' AND STATUS IS NULL ";
		List<Map<String, Object>> fileList = jdbcTemplate.queryForList(sql);
		sb = new StringBuffer();
		if(fileList.size()<=0) {
			sb.append("请上传文件!");
		}
		 //错误信息
        if(!StringUtils.isEmpty(sb.toString())){
            msg = new Message("false",sb.toString());
        }
        msg = new Message("true");
        return msg;
    }
	
	/**
     *  订单激活前操作
     */
    @RequestMapping(value = { "/saleBoforeActivate" }, method = RequestMethod.GET)
    @ResponseBody
    public Message saleBoforeActivate(String saleHeadId){
        Message msg = null;
        try {
            StringBuffer sb = null;
            
            Map<String,String> errorMap = new LinkedHashMap<String,String>();
            
            List<Map<String, Object>> query = jdbcTemplate.query("select t.posex,t.id,t.material_head_id,t.is_standard,t2.draw_type,t2.file_type," +
            		"(select m.id from (select mf.id,mf.pid from MATERIAL_FILE mf where mf.file_type='PDF' and nvl(mf.status,'C')!='X'  order by mf.create_time desc) m where  m.pid=t2.id and rownum=1) pdf_id, " +
            		"(select m.id from (select mf.id,mf.pid from MATERIAL_FILE mf where mf.file_type='KIT' and nvl(mf.status,'C')!='X'  order by mf.create_time desc) m where  m.pid=t2.id and rownum=1) kit_id " +
            		" from SALE_ITEM t inner join material_head t2 on t.material_head_id = t2.id" 
                    +" where t.is_standard='0' and t.pid='"+saleHeadId+"' order by t.posex",new MapRowMapper(true));
            for (Map<String, Object> map : query) {
                sb = new StringBuffer();
//                String materialHeadId = (String)map.get("materialHeadId");
                //对应数据字典 fileType
                //1:2020附件-->kit,pdf文件
                //2：pdf附件 -->pdf文件
                String fileType = (String) map.get("fileType");
//                List<MaterialFile> fileLists = null;
                
              //附件类型2020，只上传kit文件(订单审绘时再上传一个pdf文件)
              //附件类型pdf，只上传pdf文件
                if("1".equals(fileType)){
                   /* fileLists = materialManager.queryMaterialFile(materialHeadId, "KIT");
                    if(fileLists==null||fileLists.size()==0){
                        sb.append("请上传kit文件!");
                    }*/
                	if(map.get("kitId")==null||"null".equals(map.get("kitId"))){
                        sb.append("请上传kit文件!");
                    }
                    
                }else if("2".equals(fileType)){
                  /*  fileLists = materialManager.queryMaterialFile(materialHeadId, "PDF");
                    if(fileLists==null||fileLists.size()==0){
                        sb.append("请上传pdf文件!");
                    }*/
                	if(map.get("pdfId")==null||"null".equals(map.get("pdfId"))){
                        sb.append("请上传pdf文件!");
                    }
                }else{
                	  sb.append("请上传文件!");
                }
                //错误信息
                if(!StringUtils.isEmpty(sb.toString())){
                    errorMap.put(""+map.get("posex"), sb.toString());
                }
            }
            
            if(errorMap.isEmpty()){
            	//计算行项目价格 （标准）
            	jdbcTemplate.update("UPDATE SALE_ITEM S SET S.TOTAL_PRICE=S.AMOUNT*DECODE(S.SANJIAN_HEAD_ID,NULL,NVL((SELECT H.kbetr FROM Material_Head H WHERE H.IS_STANDARD=1 AND H.ID=S.MATERIAL_HEAD_ID),0),NVL((SELECT SUM(I.TOTAL_PRICE) FROM MATERIAL_SANJIAN_ITEM I WHERE I.PID=S.SANJIAN_HEAD_ID),0)) WHERE S.IS_STANDARD=1 AND  S.PID='"+saleHeadId+"'");
            	//计算订单总价 （标准）
            	jdbcTemplate.update("UPDATE SALE_HEADER H SET H.ORDER_TOTAL=(SELECT SUM(S.TOTAL_PRICE) FROM SALE_ITEM S WHERE S.PID=H.ID) WHERE H.ID='"+saleHeadId+"'");
            	//激活流程后，删除我的商品
            	saleAfterActivate(saleHeadId);
            	msg = new Message("true");
            }else{
                Set<Entry<String, String>> entrySet = errorMap.entrySet();
                sb = new StringBuffer();
                for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
                    Entry<String, String> entry = (Entry<String, String>) iterator.next();
                    sb.append("订单明细行号：").append(entry.getKey());
                    sb.append("  ").append(entry.getValue()).append("<br/>");
                }
                msg = new Message("false",sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg = new Message("MM-saleBoforeActivate-500", "获取数据失败！");
        }
        return msg;
    }
	/**
	 * 查询myGoods
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getMyGoods", method = RequestMethod.GET)
	@ResponseBody
	public Message getMyGoods(String id)  {
		Message msg = null;
		try {
			MyGoods findOne = myGoodsManager.getOne(id, MyGoods.class);
			if(findOne!=null){
			    msg = new Message(JSONObject.fromObject(findOne));
            }else{
                msg = new Message("MM-getMyGoods-500", "数据加载失败!");
            }
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MYGOODS-getMyGoods-500", "数据加载失败!");
		}
		return msg;
	}
	
	//获取客诉日期
	@RequestMapping(value = "/getComplainidDay", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getComplainidDay(String pid) {
		List<Map<String, Object>> query = null;
		Message msg = null;
		String sql ="select to_char(mb.complaint_time, 'yyyy-mm-dd')complainTtime from material_complainid mb left join sale_header sh on sh.id= mb.pid where 1=1";
		query = jdbcTemplate.query(sql+" and mb.pid='"+pid+"'", new MapRowMapper(true));
		Map<String, Object> map = new HashMap<String, Object>();
		if(query.size()>0) {
			Map<String, Object> materialComplainid = query.get(0);
			map.put("complainday", materialComplainid.get("complainttime"));
		}

		
		return JSONArray.fromObject(map);
		
	}
	//客诉报表明细
	@RequestMapping(value = "/getComplainid", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getComplainid(String pid) {
		List<Map<String, Object>> query = null;
		String sql ="select mb.* from material_complainid mb left join sale_header sh on sh.id= mb.pid where 1=1";
		query = jdbcTemplate.query(sql+" and mb.pid='"+pid+"'", new MapRowMapper(true));
		return JSONArray.fromObject(query);
	}
	/**
	 * 费用化明细
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getExpenditure", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getExpenditure(String id) {
		List<Map<String, Object>> query = null;
	    String sql = "select t.* from SALE_EXPENDITURE_ITEM t inner join SALE_EXPENDITURE_HEAD h on h.id = t.pid where 1=1 ";
	    String expendituresql = "SELECT MY.EXPENDITURE_HEAD_ID FROM MY_GOODS_FYH_VIEW MY WHERE MY.ID=(SELECT BG.MY_GOODS_ID FROM SALE_BG_HEADER BG WHERE BG.SALE_ID ='"+id+"')";
	    List<Map<String, Object>> sanjiaid = jdbcTemplate.query(expendituresql,new MyMapRowMapper());
	    query = jdbcTemplate.query(sql+" and t.pid ='"+id+"'", new MapRowMapper(true));
	    if(query.size()<=0) {
	    	if(sanjiaid.size()>0) {
	    		String sjId = (String) sanjiaid.get(0).get("expenditureHeadId");
	    		query=jdbcTemplate.query(sql+" and t.pid ='"+sjId+"'", new MapRowMapper(true));
	    	}
	    }
	    
	    return JSONArray.fromObject(query);
	}
	/**
	 * 散件明细
	 */
	@RequestMapping(value = "/getMaterialSanjianItemList", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray getMaterialSanjianItemList(String id)  {
	    String sql = "select t.*,mh.meins from material_sanjian_item t inner join material_sanjian_head h on h.id = t.pid inner join material_head mh on t.material_head_id = mh.id where 1=1 ";
	    
	    List<Map<String, Object>> query = jdbcTemplate.query(sql+" and t.pid ='"+id+"'", new MapRowMapper(true));
	    
	    return JSONArray.fromObject(query);
//	    List<MaterialSanjianItem> list = myGoodsManager.getMaterialSanjianItemList(id);
//      String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort","createTime","updateTime","materialSanjianHead" };
//      return JSONArray.fromObject(list, super.getJsonConfig(strings));
	}
	/**
     * 删除散件明细
     */
     @RequestMapping(value = "/deleteMaterialSJ", method = RequestMethod.POST)
     @ResponseBody
     public Message deleteMaterialSJ(String[] ids,String saleItemId)  {
            Message msg = null;
            try {
                myGoodsManager.deleteMaterialSanjianItem(ids, saleItemId);
                
                msg = new Message("ok");
            } catch (Exception e) {
                e.printStackTrace();
                msg = new Message("MM-delete-500", "删除失败!");
            }
            return msg;
      }
     
 	/**
      * 删除散件明细
      */
      @RequestMapping(value = "/deleteComplainid", method = RequestMethod.POST)
      @ResponseBody
      public Message deleteComplainid(String[] ids,String saleItemId)  {
             Message msg = null;
             try {
                 myGoodsManager.deleteComplainid(ids, saleItemId);
                 
                 msg = new Message("ok");
             } catch (Exception e) {
                 e.printStackTrace();
                 msg = new Message("MM-delete-500", "删除失败!");
             }
             return msg;
       }
     
     /**
      * 查询散件head信息
      * @param id
      * @return
      */
     @RequestMapping(value = "/getMaterialSanjianHead", method = RequestMethod.GET)
     @ResponseBody
     public Message getMaterialSanjianHead(String id)  {
         Message msg = null;
         try {
             MaterialSanjianHead findOne = myGoodsManager.getOne(id, MaterialSanjianHead.class);
             if(findOne!=null){
                 String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort","materialSanjianItemSet"  };
                 msg = new Message(JSONObject.fromObject(findOne, super.getJsonConfig("yyyy-MM-dd",strings)));
             }else{
                 msg = new Message("MM-getMaterialSanjianHead-500", "数据加载失败!");
             }
             
         } catch (Exception e) {
             e.printStackTrace();
             msg = new Message("MYGOODS-getMaterialSanjianHead-500", "数据加载失败!");
         }
         return msg;
     }
     
     /**
      * 查询补件信息
      * @param id
      * @return
      */
     @RequestMapping(value = "/getMaterialBujian", method = RequestMethod.GET)
     @ResponseBody
     public Message getMaterialBujian(String id)  {
         Message msg = null;
         try {
             MaterialBujian findOne = myGoodsManager.getOne(id, MaterialBujian.class);
             if(findOne!=null){
                 String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
                 msg = new Message(JSONObject.fromObject(findOne, super.getJsonConfig("yyyy-MM-dd",strings)));
             }else{
                 msg = new Message("MM-getMaterialBujian-500", "数据加载失败!");
             }
             
         } catch (Exception e) {
             e.printStackTrace();
             msg = new Message("MYGOODS-getMaterialBujian-500", "数据加载失败!");
         }
         return msg;
     }
     /**
      * 查询附加信息
      * @param id
      * @return
      */
     @RequestMapping(value = "/getSaleItemFj", method = RequestMethod.GET)
     @ResponseBody
     public Message getSaleItemFj(String myGoodsId)  {
         Message msg = null;
         try {
             Set<String>set = new HashSet<String>();
             set.add(myGoodsId);
//             List<SaleItemFj> lists = myGoodsManager.createQueryByIn(SaleItemFj.class, "myGoodsId", set);
             String sql="select * from sale_item_fj f where nvl(f.create_time,sysdate)=(select nvl(Max(i.create_time),sysdate) from sale_item_fj i where i.my_goods_id=f.my_goods_id ) and f.my_goods_id='"+myGoodsId+"'";
             List<Map<String, Object>> lists=jdbcTemplate.queryForList(sql);
             if(lists!=null && lists.size()>0){
                 String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
                 SaleItemFj findOne = new SaleItemFj();
                 Map<String, Object> map = lists.get(0);
                 findOne.setId(String.valueOf(map.get("ID")));
                 findOne.setCreateTime((Date)map.get("create_time"));
                 findOne.setCreateUser(String.valueOf(map.get("CREATE_USER")));
                 findOne.setMaterialHeadId(String.valueOf(map.get("MATERIAL_HEAD_ID")));
                 findOne.setMyGoodsId(String.valueOf(map.get("MY_GOODS_ID")));
                 findOne.setRowStatus(String.valueOf(map.get("ROW_STATUS")));
                 findOne.setSaleItemId(String.valueOf(map.get("SALE_ITEM_ID")));
                 findOne.setStatus(String.valueOf(map.get("STATUS")));
                 findOne.setZzazdr(String.valueOf(map.get("zzazdr")));
                 findOne.setPpcDate((Date)map.get("PPC_DATE"));
                 findOne.setPsDate((Date)map.get("PS_DATE"));
                 findOne.setPcDate((Date)map.get("PC_DATE"));
                 findOne.setPbDate((Date)map.get("PB_DATE"));
                 findOne.setPoDate((Date)map.get("PO_DATE"));
                 findOne.setDeliveryDay(String.valueOf(map.get("DELIVERY_DAY")));
                 
                 msg = new Message(JSONObject.fromObject(findOne, super.getJsonConfig("yyyy-MM-dd",strings)));
             }else{
                 msg = new Message("MM-getSaleItemFj-500", "数据加载失败!");
             }
             
         } catch (Exception e) {
             e.printStackTrace();
             msg = new Message("MYGOODS-getSaleItemFj-500", "数据加载失败!");
         }
         return msg;
     }
     
     /**
      * 保存新客服投诉汇总表的信息
      * @param materialBean 根据id修改
      * @return
      */
     @RequestMapping(value="/updateComplainid",method=RequestMethod.POST)
 	@ResponseBody
     public Message updateComplainid(@RequestBody MaterialBean materialBean) {
  		Message msg = null;
  		MaterialComplainid newMaterialComplainid = new MaterialComplainid();
  		List<MaterialComplainid> MaterialComplainidList=new ArrayList<MaterialComplainid>();
  		try {
  			String ComplainidId = materialBean.getMaterialComplainidFrom().getId();
  			String touSuCiShu = materialBean.getTousucishu();
  			String problem = materialBean.getProblem();
  			MaterialComplainid materialcomplainid = materialComplainidDao.findOne(ComplainidId);
  			TerminalClient terinalClient =null;
  			if(ComplainidId==null||"".equals(ComplainidId)) {
  				terinalClient = terminalClientDao.findBysaleid(materialBean.getSaleId());
  				newMaterialComplainid.setZzccz(materialBean.getMaterialComplainidFrom().getZzccz());
  				newMaterialComplainid.setColor(materialBean.getMaterialComplainidFrom().getColor());
  				newMaterialComplainid.setSalefor(materialBean.getMaterialComplainidFrom().getSalefor());
  				newMaterialComplainid.setCabinetName(materialBean.getMaterialComplainidFrom().getCabinetName());
  				newMaterialComplainid.setCpmc(materialBean.getMaterialComplainidFrom().getCpmc());
  				newMaterialComplainid.setZzelb(materialBean.getMaterialComplainidFrom().getZzelb());
  				newMaterialComplainid.setZztsnr(materialBean.getMaterialComplainidFrom().getZztsnr());
  				newMaterialComplainid.setZzebm(materialBean.getMaterialComplainidFrom().getZzebm());
  				newMaterialComplainid.setZzezx(materialBean.getMaterialComplainidFrom().getZzezx());
  				newMaterialComplainid.setDuty(materialBean.getMaterialComplainidFrom().getDuty());
  				newMaterialComplainid.setZzcclx(materialBean.getMaterialComplainidFrom().getZzcclx());
  				newMaterialComplainid.setOrderCodePosex(materialBean.getMaterialComplainidFrom().getOrderCodePosex());
  				newMaterialComplainid.setPid(materialBean.getSaleId());
  				materialComplainidDao.save(newMaterialComplainid);
  				MaterialComplainidList.add(newMaterialComplainid);
  				terinalClient.setTousucishu(touSuCiShu);
  				terinalClient.setProblem(problem);
  				terminalClientDao.save(terinalClient);
  			}else {
  				terinalClient = terminalClientDao.findBysaleid(materialcomplainid.getPid());
  				if(materialcomplainid!=null) {
  					materialcomplainid.setZzccz(materialBean.getMaterialComplainidFrom().getZzccz());
  					materialcomplainid.setColor(materialBean.getMaterialComplainidFrom().getColor());
  					materialcomplainid.setSalefor(materialBean.getMaterialComplainidFrom().getSalefor());
  					materialcomplainid.setCabinetName(materialBean.getMaterialComplainidFrom().getCabinetName());
  					materialcomplainid.setCpmc(materialBean.getMaterialComplainidFrom().getCpmc());
  					materialcomplainid.setZzelb(materialBean.getMaterialComplainidFrom().getZzelb());
  					materialcomplainid.setZztsnr(materialBean.getMaterialComplainidFrom().getZztsnr());
  					materialcomplainid.setZzebm(materialBean.getMaterialComplainidFrom().getZzebm());
  					materialcomplainid.setZzezx(materialBean.getMaterialComplainidFrom().getZzezx());
  					materialcomplainid.setDuty(materialBean.getMaterialComplainidFrom().getDuty());
  					materialcomplainid.setZzcclx(materialBean.getMaterialComplainidFrom().getZzcclx());
  					materialcomplainid.setOrderCodePosex(materialBean.getMaterialComplainidFrom().getOrderCodePosex());
  					materialComplainidDao.save(materialcomplainid);
  					terinalClient.setTousucishu(touSuCiShu);
  					terinalClient.setProblem(problem);
  	  				terminalClientDao.save(terinalClient);
  				}
  				MaterialComplainidList.add(materialcomplainid);
  			}
 	        if(MaterialComplainidList.size()>0){
 	            //String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
 	        	materialComplainidDao.save(MaterialComplainidList);
 	            msg = new Message("MYGOODS-updateBujian-200", "保存成功!");
 	        }else{
 	            msg = new Message("MYGOODS-updateBujian-500", "保存失败!");
 	        }
  	    } catch (Exception e) {
  	        e.printStackTrace();
  	        msg = new Message("MYGOODS-updateBujian-500", "保存失败!");
  	    }
  		return msg;
  	}
     
     /**
      * 保存客服投诉汇总表的信息
      * @param materialBean 根据id修改
      * @return
      */
     @RequestMapping(value="/updateBujian",method=RequestMethod.POST)
 	@ResponseBody
 	public Message updateBujian(@RequestBody MaterialBean materialBean){
 		Message msg = null;
 		try {
 	        /*MaterialBujian obj = myGoodsManager.saveBJ(materialBean);
 	        if(obj!=null){
 	            String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
 	            msg = new Message(JSONObject.fromObject(obj, super.getJsonConfig("yyyy-MM-dd",strings)));
 	        }else{
 	            msg = new Message("MYGOODS-updateBujian-500", "保存失败!");
 	        }*/
/* 			MaterialBujian material_bujian = materialBean.getMaterialBujian();//zzebm	zzezx	zztsnr	zzccwt
			String sql="UPDATE MATERIAL_BUJIAN SET DUTY='"+material_bujian.getDuty()+"',ZZCCZ='"+material_bujian.getZzccz()+"'" +
					",ZZEBM='"+material_bujian.getZzebm()+"',ZZEZX='"+material_bujian.getZzezx()+"'" +
							",ZZTSNR='"+material_bujian.getZztsnr()+"',ZZCCWT='"+material_bujian.getZzccwt()+"' " +
									" where id='"+material_bujian.getId()+"'";*/
 			//插个眼
 			String sql="";
			int flg = jdbcTemplate.update(sql);
	        /*MaterialBujian obj = myGoodsManager.saveBJ(materialBean);*/
	        if(flg>0){
	            //String[] strings = new String[] {"hibernateLazyInitializer", "handler","fieldHandler","sort"};
	            msg = new Message("MYGOODS-updateBujian-200", "保存成功!");
	        }else{
	            msg = new Message("MYGOODS-updateBujian-500", "保存失败!");
	        }
 	    } catch (Exception e) {
 	        e.printStackTrace();
 	        msg = new Message("MYGOODS-updateBujian-500", "保存失败!");
 	    }
 		return msg;
 	}
}
