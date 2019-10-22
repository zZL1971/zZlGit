package com.main.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
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
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.main.domain.sap.SapZstPpRl01;
import com.main.domain.spm.SalePrModHeader;
import com.main.domain.spm.SalePrModItem;
import com.main.domain.sys.SysFile;
import com.main.manager.SysFileManager;
import com.main.model.cust.CustContactsModel;
import com.main.model.cust.SalePriceProlicyModel;
import com.main.model.spm.SPMModel;
import com.main.model.spm.SalePrModHeaderModel;
import com.main.model.spm.SalePrModItemModel;
import com.main.util.MyFileUtil;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.OrderBy;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SalePriceProlicy;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.FieldFunction;
import com.mw.framework.utils.JcifsUtils;
import com.mw.framework.utils.ZStringUtils;
import com.mw.framework.utils.ZipUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

/**
 *
 */
@Controller
@RequestMapping("/main/sd/price/*")
public class PricePolicyController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(PricePolicyController.class);
	
	@Autowired
	private CommonManager commonManager;

	//private Class<SalePriceProlicy> entityClass;

	@RequestMapping(value = {"/delete"}, method = {RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public Message delete(@RequestParam(value = "ids") String[] ids){
		Message msg = null;
		try {
			commonManager.delete(ids, SalePriceProlicy.class);
			msg = new Message("ok");
		} catch (Exception e) {
			//e.printStackTrace();
			msg = new Message("TT-D-500", e.getLocalizedMessage());
		}
		return msg;
	}
	
	/**
	 * 根据id查询价格政策记录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
    @ResponseBody
	public Message findPricePolicyId(@PathVariable String id){
		Message msg = null;
		try {
			
			Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
	        formats.put("createTime", new SimpleDateFormat(DateTools.fullFormat));
	        formats.put("updateTime", new SimpleDateFormat(DateTools.fullFormat));
			String sql="select * from SALE_PRICE_PROLICY where id='"+id+"'";
			List<Map<String, Object>> query = jdbcTemplate.query(sql,new MapRowMapper(true, formats));
//			if(query.size()>0){
//				Map<String, Object> map=query.get(0);
//				SalePriceProlicyModel contacts = new SalePriceProlicyModel();
//				
//				contacts.setId(map.get("id").toString());
//				contacts.setKunnr(map.get("kunnr")==null?"":map.get("kunnr").toString());
//				contacts.setName1(map.get("name1")==null?"":map.get("name1").toString());
//			
//				contacts.setDiscount(map.get("discount")==null?Double.parseDouble("0.0"):Double.parseDouble(map.get("discount").toString()));
//				contacts.setIsable(map.get("isable")==null?"":map.get("isable").toString());
//				contacts.setSaleorder(map.get("saleOrder")==null?"":map.get("saleOrder").toString());
//				contacts.setStarttime(map.get("startTime")==null?"":map.get("startTime").toString());
//				contacts.setEndtime(map.get("endTime")==null?"":map.get("endTime").toString());
//				contacts.setRebatype(map.get("discountStyle")==null?"":map.get("discountStyle").toString());
//				
//				contacts.setCreateUser(map.get("createUser").toString());
//				contacts.setCreateTime(map.get("createTime").toString());
//				contacts.setUpdateUser(map.get("updateUser").toString());
//				contacts.setUpdateTime(map.get("updateTime").toString());
//				msg = new Message(contacts);
//			}
			
			msg=new Message(query.get(0));
			
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("C-TT-500", "系统未找到价格配置!");
		}
		return msg;
	}
	
	//hzm
	/**
	 * price plolicy submit
	 * @param dataDic
	 * @return
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public Message savepricepolicy(@Valid SalePriceProlicy priceprolicy,BindingResult result){
		Message msg = null;
		if(priceprolicy!=null){
			try {
				String endTime=this.getRequest().getParameter("endTime");
				String startTime=this.getRequest().getParameter("startTime");
				priceprolicy.setEndtime(DateTools.stringToDate(endTime));
				priceprolicy.setStarttime(DateTools.stringToDate(startTime));
//				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
//				String id = priceprolicy.getId().trim();
//				String kunnr = priceprolicy.getKunnr().toString();
//				String name1 = priceprolicy.getName1().toString();
//				String isable = priceprolicy.getIsable().toString();
//				String saleorder = priceprolicy.getSaleorder().toString();
//				Date starttime= priceprolicy.getStarttime();
//				Date endtime = priceprolicy.getEndtime();
//				String discountstype = priceprolicy.getRebatype().toString();
//				Double discount = priceprolicy.getDiscount();
				
//				if(id == null || (id.equals(""))){//新增
//					
//					SalePriceProlicy  salepriceprolicy = new SalePriceProlicy(kunnr,name1,discount,
//							isable,saleorder,starttime,endtime,discountstype);
//						
//						commonManager.save(salepriceprolicy);
//				}else{//有ID说明是修改
//					String sql = "update SALE_PRICE_PROLICY aa set aa.DISCOUNT_STYLE = '"+discountstype+"',aa.kunnr = '"+kunnr+"',aa.name1 = '"+name1
//					+"',aa.start_time = '"+starttime+"',aa.end_time = '"+endtime+"',aa.discount = "+discount+",aa.isable='"+isable+"',aa.sale_order='"+saleorder
//					+"' where aa.id = '"+id+"'";
//					jdbcTemplate.update(sql);
//				}
				commonManager.save(priceprolicy);
				msg = new Message("OK");
			} catch (Exception e) {
				msg = new Message("DD-S-500",e.getLocalizedMessage());
			}
		}else{
			msg = new Message("DD-S-000","SalePriceProlicy is null");
		}
		return msg;
	}
	
	
}
