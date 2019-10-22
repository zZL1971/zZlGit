package com.main.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.main.bean.MaterialBean;
import com.main.bean.MmDataDictBean;
import com.main.dao.CustHeaderDao;
import com.main.dao.CustItemDao;
import com.main.dao.MaterialFileDao;
import com.main.dao.MaterialPriceDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleLogisticsDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialItem;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.MaterialProperty;
import com.main.domain.mm.MaterialPropertyItem;
import com.main.domain.mm.MyGoods;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleBgfile;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sale.SaleLogistics;
import com.main.manager.MaterialManager;
import com.main.manager.SaleManager;
import com.main.manager.SalePrModManager;
import com.main.util.ExcellField;
import com.main.util.MyFileUtil;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.core.MyMapRowMapper;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysMultipartFile;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.ExtDataField;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.util.StringHelper;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.ExcelUtil;
import com.mw.framework.utils.FreemarkerGenerialExcel;
import com.mw.framework.utils.JcifsUtils;
import com.mw.framework.utils.StringUtil;
import com.mw.framework.utils.ZStringUtils;
import com.mw.framework.utils.ZipUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoFunction;
import com.webservice.RocoImos;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

/**
 *
 */
@Controller
@RequestMapping("/main/mm/*")
public class MaterialController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(MaterialController.class);
	@Autowired
	private MaterialManager materialManager;
	@Autowired
	private CommonManager commonManager;
	@Autowired
	private SaleManager saleManager;
	@Autowired
	private SysDataDictDao sysDataDictDao;
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	@Autowired
	private CustHeaderDao custHeaderDao;
	
	
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private CustItemDao custItemDao;
	@Autowired
	private  MaterialFileDao materialFileDao;
	/**
	 * jedis连接池
	 */
	@Autowired
	private JedisPool jedisPool;
	@Autowired
	private MaterialPriceDao materialPriceDao;
	private static final String INSERT_XML_LIST="insert into xml_request_list(id,request_time,imos_path,file_name,frequency,status) values( ?, sysdate,?,?,1,2)";

	protected boolean importExecl(MultipartFile multipartFile,
			HttpServletRequest request) throws IOException {
		logger.info("解析EXECL");
		String orderCodePosex = request.getParameter("orderCodePosex");
		if (orderCodePosex != null && orderCodePosex.trim().length() > 0) {

		} else {
			return false;
		}

		Workbook workbook = null;
		if (multipartFile.getOriginalFilename().endsWith(".xlsx")) {
			workbook = new XSSFWorkbook(multipartFile.getInputStream());
		} else {
			workbook = new HSSFWorkbook(multipartFile.getInputStream());
		}

		String moduleName = request.getParameter("moduleName");
		// 获取xml文件
		Document doc = super.getXMLForGrid(moduleName);
		Element element = (Element) doc.selectSingleNode("//grid/config");
		Attribute tname = element.attribute("curdtname");

		// 清理数据
		jdbcTemplate.update("delete " + tname.getText()
				+ " where orderid=? and ARTICLE_ID='ROCO_YM'",
				new Object[] { orderCodePosex });

		// 获取表头数据信息
		Map<String, ExtDataField> tableColumns = super.getTableColumns(
				tname.getText(), false);

		Sheet sheet = workbook.getSheetAt(0);
		Row rowheader = sheet.getRow(1);

		// sql
		StringBuffer fields = new StringBuffer();
		StringBuffer fieldIndexs = new StringBuffer();
		for (int cellNum = 0; cellNum < rowheader.getLastCellNum(); cellNum++) {
			String headercell = rowheader.getCell(cellNum).getStringCellValue()
					.trim();
			ExtDataField extDataField = tableColumns.get(headercell);
			if (extDataField != null) {
				fields.append(extDataField.getColumname() + ",");
				fieldIndexs.append("?,");
			}
		}

		String insertsql = "insert into "
				+ tname.getText()
				+ "("
				+ (fields.length() > 0 ? fields
						.deleteCharAt(fields.length() - 1) : "")
				+ ") values("
				+ (fieldIndexs.length() > 0 ? fieldIndexs
						.deleteCharAt(fieldIndexs.length() - 1) : "") + ")";
		logger.info(insertsql);

		List<Object[]> dataList = new ArrayList<Object[]>();

		for (int rowNum = 2; rowNum <= sheet.getLastRowNum(); rowNum++) {
			Row row = sheet.getRow(rowNum);
			List<Object> rowList = new ArrayList<Object>();
			for (int cellNum = 0; cellNum < row.getLastCellNum(); cellNum++) {
				Cell cell = row.getCell(cellNum);
				Cell headCell = rowheader.getCell(cellNum);
				if (headCell != null) {
					Object value = null;
					String headercell = rowheader.getCell(cellNum)
							.getStringCellValue().trim();
					ExtDataField extDataField = tableColumns.get(headercell);
					if (extDataField != null) {
						if (cell != null) {

							if (extDataField.getType().equals("string")) {
								cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							}

							switch (cell.getCellType()) {
							case HSSFCell.CELL_TYPE_NUMERIC:
								value = cell.getNumericCellValue();
								break;
							case HSSFCell.CELL_TYPE_STRING:
								value = cell.getStringCellValue();
								break;
							case HSSFCell.CELL_TYPE_BOOLEAN:
								break;
							case HSSFCell.CELL_TYPE_FORMULA:
								value = cell.getNumericCellValue();
								break;
							case HSSFCell.CELL_TYPE_BLANK:
								value = null;
								break;
							default:
								value = null;
								break;
							}

						}
						if (extDataField.getColumname().equals("ORDERID")) {
							value = orderCodePosex;
						}

						if (extDataField.getColumname().equals("ARTICLE_ID")) {
							value = "ROCO_YM";
						}

						rowList.add(value);

						System.out.print(value + "[" + extDataField.getName()
								+ "-" + extDataField.getType() + "-"
								+ (cell != null ? cell.getCellType() : "")
								+ "]\t");
					}
				}
			}
			dataList.add(rowList.toArray());
		}

		jdbcTemplate.batchUpdate(insertsql, dataList);
		return true;
	}

	@RequestMapping(value = "/execlImp", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Message> uploadFile(
			SysMultipartFile sysMultipartFile, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		Message msg = null;

		long startTime = System.currentTimeMillis(); // 获取开始时间

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) { // 判断request是否有文件上传

			// 获取配置信息
			Map<String, String[]> parameterMap = new LinkedHashMap<String, String[]>();
			parameterMap.put("ICEQtrieTree__keyVal",
					new String[] { "FILE_PATH" });
			List<SysDataDict> queryByRange = commonManager.queryByRange(
					SysDataDict.class, parameterMap);

			String smbPath = null;
			if (queryByRange.size() > 0) {
				smbPath = queryByRange.get(0).getDescEnUs();
			} else {
				msg = new Message("FILE-UP-407", "未配置文件服务器存储路径");
				return new ResponseEntity<Message>(msg, headers, HttpStatus.OK);
			}

			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> ite = multiRequest.getFileNames();
			while (ite.hasNext()) {
				MultipartFile file = multiRequest.getFile(ite.next());
				if (file != null) {
					// 解析execl
					boolean importExecl = importExecl(file, request);
					// 操作成功上传服务器备档文件
					if (importExecl) {
						String oldName = file.getOriginalFilename();
						oldName = oldName.substring(oldName.lastIndexOf("."));
						sysMultipartFile.setSmbFileName(UUID.randomUUID()
								.toString().replace("-", "")
								+ oldName);
						sysMultipartFile.setUpFileName(file
								.getOriginalFilename());
						String format = DateTools.getNowDateYYMMDD();
						sysMultipartFile.setSmbPath(smbPath
								+ Constants.FILE_DIR
								+ sysMultipartFile.getFileType()
								+ Constants.FILE_DIR + format);
						boolean uploadFile = JcifsUtils.uploadFile(
								sysMultipartFile.getSmbPath(),
								sysMultipartFile.getSmbFileName(), file);
						// 保存操作记录
						if (uploadFile) {
							commonManager.save(sysMultipartFile);
						} else {
							msg = new Message("FILE-UP-503",
									"Execl存储至文件服务器失败(我读书少,你别骗我!)");
						}
					} else {
						msg = new Message("FILE-UP-502",
								"Execl数据解析失败(我读书少,你别骗我!)");
					}
				} else {
					msg = new Message("FILE-UP-501", "未上传文件(我读书少,你别骗我!)");
				}
			}
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("上传文件共使用时间：" + (endTime - startTime));
		msg = new Message("Execl导入成功!<br/>耗时" + (endTime - startTime)
				+ "毫秒(耗时适仅参考)");
		return new ResponseEntity<Message>(msg, headers, HttpStatus.OK);
	}

	/**
	 * 仅上传
	 * 
	 * @param sysMultipartFile
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/execlImp2", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<Message> uploadFile2(
			SysMultipartFile sysMultipartFile, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		Message msg = null;

		long startTime = System.currentTimeMillis(); // 获取开始时间

		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) { // 判断request是否有文件上传

			// 获取配置信息
			Map<String, String[]> parameterMap = new LinkedHashMap<String, String[]>();
			parameterMap.put("ICEQtrieTree__keyVal",
					new String[] { "FILE_PATH" });
			List<SysDataDict> queryByRange = commonManager.queryByRange(
					SysDataDict.class, parameterMap);

			String smbPath = null;
			if (queryByRange.size() > 0) {
				smbPath = queryByRange.get(0).getDescEnUs();
			} else {
				msg = new Message("FILE-UP-407", "未配置文件服务器存储路径");
				return new ResponseEntity<Message>(msg, headers, HttpStatus.OK);
			}

			MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
			Iterator<String> ite = multiRequest.getFileNames();
			while (ite.hasNext()) {
				MultipartFile file = multiRequest.getFile(ite.next());
				if (file != null) {

					String oldName = file.getOriginalFilename();
					oldName = oldName.substring(oldName.lastIndexOf("."));
					sysMultipartFile.setSmbFileName(UUID.randomUUID()
							.toString().replace("-", "")
							+ oldName);
					sysMultipartFile.setUpFileName(file.getOriginalFilename());
					String format = DateTools.getNowDateYYMMDD();
					sysMultipartFile.setSmbPath(smbPath + Constants.FILE_DIR
							+ sysMultipartFile.getFileType()
							+ Constants.FILE_DIR + format);
					boolean uploadFile = JcifsUtils.uploadFile(
							sysMultipartFile.getSmbPath(),
							sysMultipartFile.getSmbFileName(), file);
					// 保存操作记录
					if (uploadFile) {
						commonManager.save(sysMultipartFile);
					} else {
						msg = new Message("FILE-UP-503",
								"Execl存储至文件服务器失败(我读书少,你别骗我!)");
					}

				} else {
					msg = new Message("FILE-UP-501", "未上传文件(我读书少,你别骗我!)");
				}
			}
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("上传文件共使用时间：" + (endTime - startTime));
		msg = new Message("文件上传成功!<br/>耗时" + (endTime - startTime)
				+ "毫秒(耗时适仅参考)");
		return new ResponseEntity<Message>(msg, headers, HttpStatus.OK);
	}

	/**
	 * 物料主数据
	 */
	@RequestMapping(value = { "/base" }, method = RequestMethod.GET)
	public ModelAndView list(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "MaterialBaseApp");
		return mav;
	}

	/**
	 * 定价条件
	 */
	@RequestMapping(value = { "/listPriceCondition" }, method = RequestMethod.GET)
	public ModelAndView listPriceCondition(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "PriceConditionApp");
		return mav;
	}

	/**
	 * 保存 物料主数据
	 * 
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBase", method = RequestMethod.POST)
	@ResponseBody
	public Message saveBase(@RequestBody MaterialBean materialBean,
			BindingResult result, String iscnc) {
		Message msg = null;
		try {
			// 物料查找销售组织和分销渠道
			SysUser loginUser = super.getLoginUser();
			materialBean.setSysUser(loginUser);

			MaterialHead obj = materialManager.saveBase(materialBean);

			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "materialFileSet",
					"materialItemSet", "materialPriceConditionSet",
					"materialPropertySet", "materialPropertyItemSet" };

			msg = new Message(JSONObject.fromObject(obj,
					super.getJsonConfig("yyyy-MM-dd", strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-saveBase-500", e.getMessage());
		}
		return msg;
	}

	/**
	 * 通过id查询主表materialHead
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryMHAndFJById", method = RequestMethod.GET)
	@ResponseBody
	public Message queryMHAndFJById(String id) {
		Message msg = null;
		try {
			MaterialHead findOne = materialManager.getOne(id,
					MaterialHead.class);

			if (findOne != null) {
				Map<String, Object> map = new HashMap<String, Object>();
				List<Map<String, Object>> queryForList = jdbcTemplate
						.queryForList("select * from SALE_ITEM_FJ f where f.MATERIAL_HEAD_ID='"
								+ id + "'");
				map.put("id", findOne.getId());
				map.put("color", findOne.getColor());
				map.put("groes", findOne.getGroes());
				map.put("heightDesc", findOne.getHeightDesc());
				map.put("longDesc", findOne.getLongDesc());
				map.put("maktx", findOne.getMaktx());
				map.put("matkl", findOne.getMatkl());
				map.put("serialNumber", findOne.getSerialNumber());
				map.put("textureOfMaterial", findOne.getTextureOfMaterial());
				map.put("widthDesc", findOne.getWidthDesc());
				map.put("fileType", findOne.getFileType());
				map.put("createTime", findOne.getCreateTime());
				map.put("createUser", findOne.getCreateUser());
				map.put("saleFor", findOne.getSaleFor());
				if (queryForList.size() > 0) {
					map.put("FBid", queryForList.get(0).get("ID"));
					map.put("zzazdr", queryForList.get(0).get("ZZAZDR"));
					map.put("materialHeadId",
							queryForList.get(0).get("MATERIAL_HEAD_ID"));
				}

				msg = new Message(map);
			} else {
				msg = new Message("MM-queryMaterialHeadById-500", "数据加载失败!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-queryMaterialHeadById-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 保存 非标数据
	 * 
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveBaseFB", method = RequestMethod.POST)
	@ResponseBody
	public Message saveBaseFB(@RequestBody MaterialBean materialBean,
			BindingResult result) {
		Message msg = null;
		try {
			MaterialHead materialHead = materialBean.getMaterialHead();
			if (materialHead.getId() != null) {
				List<Map<String, Object>> ql = jdbcTemplate
						.queryForList("SELECT COUNT(v.id) sale_num  FROM sale_view V WHERE V.jd_name!='起草' and V.id IN (SELECT s.pid FROM SALE_ITEM S WHERE S.MATERIAL_HEAD_ID='"
								+ materialHead.getId() + "')");
				String num = ql.get(0).get("SALE_NUM").toString();
				if (new Integer(num) > 0) {
					return msg = new Message("SALE-V-500",
							"该产品已经下单并不是起草状态，不能修改！");
				}
			}
			// 物料查找销售组织和分销渠道
			SysUser loginUser = super.getLoginUser();
			if("OR3".equals(materialBean.getbgOrderType())||"OR4".equals(materialBean.getbgOrderType())) {
				if(!"".equals(materialBean.getKunnr())&&materialBean.getKunnr()!=null) {
					CustHeader bgcust = custHeaderDao.finByKunnr(materialBean.getKunnr());
					materialHead.setVkorg(bgcust.getVkorg());
					materialHead.setVtweg(bgcust.getVtweg());
				}else {
					return msg = new Message("SALE-V-500",
							"缺少售达方编码,无法录单！");
				}
			}else {
				CustHeader cust = loginUser.getCustHeader();
				materialHead.setVkorg(cust.getVkorg());
				materialHead.setVtweg(cust.getVtweg());
			}
			materialBean.setSysUser(loginUser);
			materialHead.setRowStatus("A");
			materialHead.setIsStandard("0");
			materialHead.setLoadStatus("2");
			materialHead.setMeins("EA");
			materialHead.setMtart("Z101");
			MyGoods myGoods = new MyGoods();
			myGoods.setId("不添加我的商品");
			materialBean.setMyGoods(myGoods);
			MaterialHead obj = materialManager.saveBase(materialBean);
			if (obj != null) {
				// 更新行项目的安装位置
				String sql = "update sale_item sl set sl.remark='"
						+ materialBean.getSaleItemFj().getZzazdr()
						+ "',sl.maktx='"
						+ materialBean.getMaterialHead().getMaktx()
						+ "' where sl.material_head_id='" + obj.getId() + "'";
				jdbcTemplate.update(sql);
			}
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "materialFileSet",
					"materialItemSet", "materialPriceConditionSet",
					"materialPropertySet", "materialPropertyItemSet" };
			msg = new Message(JSONObject.fromObject(obj,
					super.getJsonConfig("yyyy-MM-dd", strings)));
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-saveBase-500", "保存失败！");
		}
		return msg;
	}

	/**
	 * 根据ids删除 标记删除 deleteFbIds
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteFbIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteFb(String[] ids) {
		Message msg = null;
		try {
			String message = "";
			if (ids != null) {
				for (String id : ids) {
					String qSql = "select count(1) rn from sale_item i where i.material_head_id ='"
							+ id + "'";
					List<Map<String, Object>> queryForList = jdbcTemplate
							.queryForList(qSql);
					String num = queryForList.get(0).get("RN").toString();
					if ("0".equals(num)) {
						String sql = "update Material_Head h set h.row_status='D' where h.id='"
								+ id + "'";
						jdbcTemplate.update(sql);
					} else {
						message = "已经下单不能删除";
					}
				}
			}
			if (!"".equals(message)) {
				msg = new Message("MM-deleteFb-500", "删除失败-产品已经下单，不能删除");
			} else {
				msg = new Message("删除成功");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}

	/**
	 * 保存默认定价条件
	 * 
	 * @param materialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/savePriceCondition", method = RequestMethod.POST)
	@ResponseBody
	public Message savePriceCondition(@RequestBody MaterialBean materialBean,
			BindingResult result) {
		Message msg = null;
		try {
			Set<PriceCondition> conditions = materialBean.getPriceConditions();
			commonManager.save(conditions);
			msg = new Message(true);

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-savePriceCondition-500", "保存失败！");
		}
		return msg;
	}

	/**
	 * 保存标准产品属性
	 * 
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/saveMaterialPropertyItem", method = RequestMethod.POST)
	@ResponseBody
	public Message saveMaterialPropertyItem(
			@RequestBody MaterialBean materialBean, BindingResult result) {
		Message msg = null;
		try {
			MaterialHead head = materialManager
					.saveMaterialPropertyItem(materialBean);
			if (!StringUtils.isEmpty(head.getId())) {
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "materialFileSet",
						"materialItemSet", "materialPriceConditionSet",
						"materialPropertySet", "materialPropertyItemSet" };
				msg = new Message(JSONObject.fromObject(head,
						super.getJsonConfig("yyyy-MM-dd", strings)));
			} else {
				msg = new Message("MM-saveMaterialPropertyItem-500", "保存失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-saveMaterialPropertyItem-500", "保存失败！");
		}

		return msg;
	}

	/**
	 * 通过id查询主表materialHead
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryMaterialHeadById", method = RequestMethod.GET)
	@ResponseBody
	public Message queryMaterialHeadById(String id) {
		Message msg = null;
		try {
			MaterialHead findOne = materialManager.getOne(id,
					MaterialHead.class);

			if (findOne != null) {
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "materialFileSet",
						"materialItemSet", "materialPriceConditionSet",
						"materialPropertySet", "materialPropertyItemSet" };
				JSONObject fromObject = JSONObject.fromObject(findOne,
						super.getJsonConfig("yyyy-MM-dd", strings));
				msg = new Message(fromObject);
			} else {
				msg = new Message("MM-queryMaterialHeadById-500", "数据加载失败!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-queryMaterialHeadById-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 查询主表MaterialPropertyItem
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/getMaterialPropertyItem", method = RequestMethod.GET)
	@ResponseBody
	public Message getMaterialPropertyItem(String priceInfo, String pid) {
		Message msg = null;
		try {
			if (!StringUtils.isEmpty(priceInfo)) {
				String[] split = priceInfo.split(",");
				StringBuffer sb = new StringBuffer(
						" select * from material_property_item  where 1=1 and status is null ");
				for (String str : split) {
					String[] itemsplit = str.split("_");
					sb.append(" and ").append(itemsplit[0]);
					sb.append(" = '").append(itemsplit[1]).append("' ");
				}
				sb.append("  and pid=? ");
				List<Map<String, Object>> queryForList = jdbcTemplate
						.queryForList(sb.toString(), pid);
				if (queryForList != null && queryForList.size() > 0) {
					Map<String, Object> map = queryForList.get(0);
					JSONObject obj = new JSONObject();
					obj.put("id", map.get("ID"));
					obj.put("price", map.get("PRICE"));
					msg = new Message(JSONObject.fromObject(obj));
				} else {
					msg = new Message("MM-getMaterialPropertyItem-500",
							"数据加载失败!");
				}
			} else {
				msg = new Message("MM-getMaterialPropertyItem-500", "数据加载失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-getMaterialPropertyItem-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 查询列表界面grid 标准产品/非标产品
	 * 
	 * @param bomNo
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialGrid", method = RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean queryMaterialGrid(int page, int limit) {
		SysUser loginUser = super.getLoginUser();
		// 销售组织
		String custVkorg = "";
		// 分销渠道
		String custVtweg = "";
		String vkorg="";
		String vtweg="";
		String orderType = this.getRequest().getParameter("orderType");// 订单类型
		String kunnr = this.getRequest().getParameter("kunnr");// 订单类型
		if("OR3".equals(orderType)||"OR4".equals(orderType)) {
			CustHeader bgCustHeader = custHeaderDao.finByKunnr(kunnr);
			if(bgCustHeader!=null) {
				custVkorg = bgCustHeader.getVkorg();
				custVtweg = bgCustHeader.getVtweg();
				vkorg = bgCustHeader.getVkorg();
				vtweg = bgCustHeader.getVtweg();
			}
		}else {
			// 客户
			CustHeader custHeader = loginUser.getCustHeader();
			if (custHeader != null) {
				custVkorg = custHeader.getVkorg();
				custVtweg = custHeader.getVtweg();
				 vkorg = this.getRequest().getParameter("vkorg");// 销售组织
				
				 vtweg = this.getRequest().getParameter("vtweg");// 分销渠道
			}
		}
		// 是否标准 1标准，0非标
		String isStandard = this.getRequest().getParameter("isStandard");
		// 是否是"我的商品"页面请求,如果是"true"表示是"我的商品"页面请求,就过滤掉已冻结和已删除的数据
		String is4Sale = this.getRequest().getParameter("is4Sale");

		String matnr = this.getRequest().getParameter("matnr");// 物料编码
		String mtart = this.getRequest().getParameter("mtart");// 物料类型
		String type = this.getRequest().getParameter("type");// BZ标准，SJ散件
		String mtartSJ = this.getRequest().getParameter("mtartSJ");// SJ散件 物料编码

		
		String maktx = this.getRequest().getParameter("maktx");// 物料说明
		String matkl = this.getRequest().getParameter("matkl");// 物料组

		String saleFor=this.getRequest().getParameter("saleFor");//销售类型

		// sql params
		List<Object> params = new ArrayList<Object>();
		String SQL_DATA = "SELECT T.*,decode(nvl(mf.id,0),'0','无','有') as ispic,(select f.zzazdr from SALE_ITEM_FJ f where f.material_head_id=t.ID and t.IS_STANDARD='0' and rownum=1) zzazdr FROM MATERIAL_VIEW T,material_file mf WHERE 1=1  and mf.pid(+) = t.ID ";
		/*
		 * if("FB".equals(type)){ SQL_DATA = "SELECT T.*," +
		 * "(select sum(l.amount) from sale_header sh,sale_item l where sh.id=l.pid and nvl(sh.order_status,'C')<>'QX' and l.material_head_id=t.id) order_Count "
		 * + " FROM MATERIAL_VIEW T WHERE 1=1 "; }
		 */
		String SQL_LIMIT = "SELECT count(*) as TOTAL FROM MATERIAL_VIEW T WHERE 1=1 ";

		StringBuffer sb = new StringBuffer();
		if ("true".equals(is4Sale)) {
			if("OR3".equals(orderType)||"OR4".equals(orderType)) {
				sb.append(" and  NVL(t.KBSTAT,'B') in ('B','A')");
				sb.append(" and t.LOEVM_KO is null ");
			}else {
				sb.append(" and t.LOEVM_KO is null ");
				sb.append(" and t.KBSTAT is null ");
			}
		}
		if ("BZ".equals(type)) {
			/*//橱柜的标准产品不能被查出来
			if(saleFor.equals("0")){
				sb.append(" and t.MTART in('Z101','Z008') AND t.MATKL NOT IN ('1501') and t.matnr not in('22999999995','22999999996','22999999997','22999999998','22999999999')  and instr(t.matnr,'14')<>1");//and instr(t.matnr,'14')<>1  加上此条件则橱柜	
			}else if(saleFor.equals("1")){
			}*/
			sb.append(" and t.MTART in('Z101') AND t.MATKL NOT IN ('1501','1998') and t.matnr not in('102999994','102999995','102999996','102999997','102999998') ");//and instr(t.matnr,'14')<>1  加上此条件则橱柜
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
			/*if(!StringUtils.isEmpty(saleFor) &&!"0".equals(saleFor)){
				sb.append(" and t.sale_for=?");
				params.add(saleFor);
			}*/
			String serialNumber = this.getRequest()
					.getParameter("serialNumber");// 物料组
			if (!"".equals(serialNumber) || serialNumber != null) {
				sb.append(" and t.serial_Number like ? ");
				params.add(StringHelper.like(String.valueOf(serialNumber)));
			}

		} else if("RJS".equals(type)) {
			sb.append(" and t.MTART in('Z101','Z008') and t.matnr in('106501001','106501002')  ");//and instr(t.matnr,'14')<>1  加上此条件则橱柜
			sb.append(" and t.vkorg = ? and t.vtweg = ? ");
			params.add(custVkorg);
			params.add(custVtweg);
		}else if("CPDJ".equals(type)) {
			StringBuilder sub = new StringBuilder("and t.matnr in (");
			List<Map<String, Object>> fpItems = jdbcTemplate.queryForList("SELECT SD.KEY_VAL,SD.TYPE_KEY FROM SYS_TRIE_TREE ST RIGHT JOIN SYS_DATA_DICT SD ON ST.ID = SD.TRIE_ID WHERE ST.KEY_VAL=? AND SD.STAT=? ", "F_P_ITEM","1");
			for (int i = 0;i<fpItems.size();i++) {
				String keyVal = ZStringUtils.resolverStr(fpItems.get(i).get("KEY_VAL"));
				String typeKey = ZStringUtils.resolverStr(fpItems.get(i).get("TYPE_KEY"));
				sub.append("'");
				if(super.getLoginUserId().indexOf("_01")>-1) {
					sub.append(keyVal);
				}else if("Y".equals(typeKey)){
					sub.append(keyVal);
				}
				sub.append("'");
				if(i != fpItems.size()-1) {
					sub.append(",");
				}
			}
			sub.append(")");
			sb.append(" and t.MTART in('Z101','Z008')");//and instr(t.matnr,'14')<>1  加上此条件则橱柜
			sb.append(sub.toString());
			sb.append(" and t.vkorg = ? and t.vtweg = ? ");
			params.add(custVkorg);
			params.add(custVtweg);
		} else if ("SJ".equals(type)) {
			// 数据字典查找物料组配置
			String sjMatkl = "";
			if ("102999995".equals(mtartSJ)) {// 22999999996销售道具
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("102999996".equals(mtartSJ)) {// 22999999997五金
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("102999997".equals(mtartSJ)) {// 22999999998移门散件
				sjMatkl = "'" + mtartSJ + "'";
			} else if ("102999994".equals(mtartSJ)) {// 22999999995柜身散件
				sjMatkl = "'" + mtartSJ + "'";
			} else {// 散件 移门，柜身
				sjMatkl = "'102999997','102999994'";
			}
			// System.out.println("sql1-------------->" + SQL_DATA + sb);
			// 没有配置散件对应的物料组 返回空数据
			if (StringUtils.isEmpty(sjMatkl)) {
				return new JdbcExtGridBean(0, 0, 0, new ArrayList());

			} else {
				String sql = "select t.DESC_ZH_CN, t.KEY_VAL  from sys_data_dict t  inner join sys_trie_tree t2  on t.trie_id = t2.id  where t2.KEY_VAL in ("
						+ sjMatkl + ") AND T.STAT !='0' ";
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

			sb.append("  and t.MTART !='Z101' and t.vkorg = ? and t.vtweg = ?");
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

	/**
	 * 定价条件查询
	 * 
	 * @param pid
	 * @param fileType
	 * @return
	 */
	@RequestMapping(value = "/queryPriceConditionList", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryPriceConditionList(String pid, String type) {
		List lists = null;
		String[] strings = null;
		if (StringUtils.isEmpty(pid)) {
			if ("0".equals(type)) {// 物料新增 时带出默认定价过程
				lists = materialManager.queryPriceCondition();
				strings = new String[] { "hibernateLazyInitializer", "handler",
						"fieldHandler", "sort", "id" };
			} else {// 查询默认定价过程
				strings = new String[] { "hibernateLazyInitializer", "handler",
						"fieldHandler", "sort" };
				lists = materialManager.queryPriceCondition();
			}
		} else {// 主表id查询定价过程
			// lists = materialManager.queryMaterialPriceCondition(pid);
			// strings = new String[] {"hibernateLazyInitializer",
			// "handler","fieldHandler","sort","materialHead"};
		}
		// System.out.println(JSONArray.fromObject(lists,
		// super.getJsonConfig(strings)));
		return JSONArray.fromObject(lists, super.getJsonConfig(strings));

	}
	@RequestMapping(value = "/queryBgFile", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryBgFile(String pid, String fileType) {
		if ("PICTURE".equals(fileType)) {// 图片
			List<MaterialFile> queryForList = materialManager
					.queryMaterialFile(pid, fileType);
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "materialHead", "file" };
			return JSONArray.fromObject(queryForList,
					super.getJsonConfig(strings));
		}else {// 非标文件（kit,pdf，xml,cnc，报价清单）
			StringBuffer sb = new StringBuffer(
					"select t.* from SALE_BG_FILE_view t where t.pid='");
			sb.append(pid);
			if (fileType != null) {
				sb.append("' and t.file_Type = '").append(fileType);
			}
			sb.append("' order by nvl(status,'Z') desc,create_time desc ");
			//System.out.println(sb.toString());
			List<Map<String, Object>> queryForList = jdbcTemplate.query(
					sb.toString(), new MapRowMapper(true));
			return JSONArray.fromObject(queryForList);
		}
	}

	/**
	 * 查询非标产品 文件类MaterialFile
	 * 
	 * @param pid
	 *            = materialHead.id
	 * @param fileType
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialFile", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialFile(String pid, String fileType) {
		if ("PICTURE".equals(fileType)) {// 图片
			List<MaterialFile> queryForList = materialManager
					.queryMaterialFile(pid, fileType);
			String[] strings = new String[] { "hibernateLazyInitializer",
					"handler", "fieldHandler", "sort", "materialHead", "file" };
			return JSONArray.fromObject(queryForList,
					super.getJsonConfig(strings));
		} else if ("BJ".equals(fileType) || "BJ_PRICE".equals(fileType)) {// 补购
																			// 上传文件(附件，报价清单)
			// 补件查询
			StringBuffer sb = new StringBuffer(
					"select t.* from material_file_view t where t.mapping_id='");
			sb.append(pid).append("' and t.file_Type = '").append(fileType);
			sb.append("' order by t.create_Time desc ");

			List<Map<String, Object>> queryForList = jdbcTemplate.query(
					sb.toString(), new MapRowMapper(true));
			return JSONArray.fromObject(queryForList);
		} else {// 非标文件（kit,pdf，xml,cnc，报价清单）
			StringBuffer sb = new StringBuffer(
					"select t.* from material_file_view t where t.pid='");
			sb.append(pid);
			if (fileType != null) {
				if("KIT".equals(fileType)){
					if (fileType != null) {
						sb.append("' and t.file_Type in ('KIT','DWG') ");
						sb.append(" order by nvl(status,'Z') desc,create_time desc ");
					}
				}else{
						sb.append("' and t.file_Type = '").append(fileType);
						sb.append("' order by nvl(status,'Z') desc,create_time desc ");
				}
			}else {
				sb.append("' order by nvl(status,'Z') desc,create_time desc ");
			}
			//System.out.println(sb.toString());
			List<Map<String, Object>> queryForList = jdbcTemplate.query(
					sb.toString(), new MapRowMapper(true));
			return JSONArray.fromObject(queryForList);
		}
	}

	/**
	 * 查询标准产品 属性
	 * 
	 * @param pid
	 *            = materialHead.id
	 * @param fileType
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialProperty", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialProperty(String pid) {
		List<MaterialProperty> list = materialManager
				.queryMaterialProperty(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "materialHead" };
		// System.out.println(JSONArray.fromObject(list,
		// super.getJsonConfig(strings)));
		return JSONArray.fromObject(list, super.getJsonConfig(strings));

	}

	/**
	 * 查询标准产品 属性明细
	 * 
	 * @param pid
	 *            = materialHead.id
	 * @param fileType
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialPropertyItem", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialPropertyItem(String pid) {
		MaterialHead obj = materialManager.getOne(pid, MaterialHead.class);
		String propertyDesc = obj.getPropertyDesc();

		StringBuffer sb = new StringBuffer(
				" select * from  MATERIAL_PROPERTY_ITEM_VIEW where pid='");
		sb.append(pid).append("'  ");
		if (!StringUtils.isEmpty(propertyDesc)) {
			String[] split = propertyDesc.split(",");
			for (String str : split) {
				String[] split2 = str.split(":");
				if ("info0".equalsIgnoreCase(split2[0])) {
					sb.append(" and key0='").append(split2[1]).append("' ");
				} else if ("info1".equalsIgnoreCase(split2[0])) {
					sb.append(" and key1='").append(split2[1]).append("' ");
				} else if ("info2".equalsIgnoreCase(split2[0])) {
					sb.append(" and key2='").append(split2[1]).append("' ");
				} else if ("info3".equalsIgnoreCase(split2[0])) {
					sb.append(" and key3='").append(split2[1]).append("' ");
				} else if ("info4".equalsIgnoreCase(split2[0])) {
					sb.append(" and key4='").append(split2[1]).append("' ");
				} else if ("info5".equalsIgnoreCase(split2[0])) {
					sb.append(" and key5='").append(split2[1]).append("' ");
				}
			}

			List<Map<String, Object>> queryForList = jdbcTemplate.query(
					sb.toString(), new MyMapRowMapper());

			List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
			for (Map<String, Object> map : queryForList) {

				Set<Entry<String, Object>> entrySet = map.entrySet();
				Map<String, Object> newMap = new HashMap<String, Object>();

				for (Entry<String, Object> entry : entrySet) {
					if (!StringUtils.isEmpty(entry.getValue())) {
						newMap.put(entry.getKey(), entry.getValue());
					}
				}
				lists.add(newMap);
			}
			return JSONArray.fromObject(lists);
		} else {
			return new JSONArray();
		}
	}

	/**
	 * 根据选中属性明细 查询对应价格
	 * 
	 * @param propertyItem
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/queryMaterialPropertyItem2", method = RequestMethod.GET)
	@ResponseBody
	public Message queryMaterialPropertyItem2(
			MaterialPropertyItem propertyItem, String pid) {
		Message msg = null;
		try {
			StringBuffer sb = new StringBuffer(
					"select * from MATERIAL_PROPERTY_ITEM t where 1=1 and t.status is null and pid=? ");
			List<Object> params = new ArrayList<Object>();
			params.add(pid);
			if (!StringUtils.isEmpty(propertyItem.getInfo0())) {
				sb.append(" and t.Info0=? ");
				params.add(propertyItem.getInfo0());
			}
			if (!StringUtils.isEmpty(propertyItem.getInfo1())) {
				sb.append(" and t.Info1=? ");
				params.add(propertyItem.getInfo1());
			}
			if (!StringUtils.isEmpty(propertyItem.getInfo2())) {
				sb.append(" and t.Info2=? ");
				params.add(propertyItem.getInfo2());
			}
			if (!StringUtils.isEmpty(propertyItem.getInfo3())) {
				sb.append(" and t.Info3=? ");
				params.add(propertyItem.getInfo3());
			}
			if (!StringUtils.isEmpty(propertyItem.getInfo4())) {
				sb.append(" and t.Info4=? ");
				params.add(propertyItem.getInfo4());
			}
			if (!StringUtils.isEmpty(propertyItem.getInfo5())) {
				sb.append(" and t.Info5=? ");
				params.add(propertyItem.getInfo5());
			}
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(
					sb.toString(), params.toArray());
			if (queryForList.size() > 0) {
				Map<String, Object> map = queryForList.get(0);
				JSONObject obj = new JSONObject();
				// obj.put("id", map.get("ID"));
				obj.put("price", map.get("PRICE"));
				obj.put("num", map.get("NUM"));
				msg = new Message(JSONObject.fromObject(obj));
			} else {
				msg = new Message("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("error", "加载数据失败！");
		}
		return msg;
	}

	/**
	 * 根据materialHead.id查找对应MmMaterialItem
	 * 
	 * @param pid
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialItem", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialItem(String pid) {
		List<MaterialItem> list = materialManager.queryMaterialItem(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "materialHead" };

		return JSONArray.fromObject(list, super.getJsonConfig(strings));
	}
	
	/**
	 * 查询是否唯一有效文件
	 * @param pid
	 * @return
	 */
	@RequestMapping(value = "/queryExpenditureFuleByPId", method = RequestMethod.POST)
	@ResponseBody
	public Message queryExpenditureFuleByPId(String pid) {
		Message msg = null;
		String sql ="SELECT * FROM SALE_BG_FILE WHERE  PID ='"+pid+"' AND STATUS IS NULL ";
		List<Map<String, Object>> fileList = jdbcTemplate.queryForList(sql);
		if(fileList.size()> 0) {
			msg = new Message(false);
		}else {
			msg = new Message(true);
		}
		return msg;
	}
	/**
	 * 根据id数组 删除费用化附件 标记删除 Status设置为X
	 * 
	 * @param ids
	 * @return @
	 */
	@RequestMapping(value = "/deleteExpenditureFuleByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteExpenditureFuleByIds(String[] ids, String type) {
		Message msg = null;
		try {
			if ("PICTURE".equals(type)) {
				for (int i = 0; i < ids.length; i++) {
					MaterialFile obj = materialManager.getOne(ids[i],
							MaterialFile.class);
					if (obj != null) {
						String uploadFilePath = obj.getUploadFilePath();
						String uploadFileName = obj.getUploadFileName();
						SmbFile file = new SmbFile(uploadFilePath
								+ MyFileUtil.FILE_DIR + uploadFileName);
						if (file != null && file.exists()) {
							file.delete();
						}
						materialManager.delete(ids[i], MaterialFile.class);
					}
				}
			} else {
				materialManager.deleteByIds("SALE_BG_FILE", ids, jdbcTemplate);
			}
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-delete-500", "删除失败!");
		}
		return msg;
	}
	
	/**
	 * 根据id数组 删除MmMaterialFile 标记删除 Status设置为X
	 * 
	 * @param ids
	 * @return @
	 */
	@RequestMapping(value = "/deleteMaterialFileByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteMaterialFileByIds(String[] ids, String type) {
		Message msg = null;
		try {
			if ("PICTURE".equals(type)) {
				for (int i = 0; i < ids.length; i++) {
					MaterialFile obj = materialManager.getOne(ids[i],
							MaterialFile.class);
					if (obj != null) {
						String uploadFilePath = obj.getUploadFilePath();
						String uploadFileName = obj.getUploadFileName();
						SmbFile file = new SmbFile(uploadFilePath
								+ MyFileUtil.FILE_DIR + uploadFileName);
						if (file != null && file.exists()) {
							file.delete();
						}
						materialManager.delete(ids[i], MaterialFile.class);
					}
				}
			} else {
				materialManager.deleteByIds("MATERIAL_FILE", ids, jdbcTemplate);
			}
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-delete-500", "删除失败!");
		}
		return msg;
	}

	/**
	 * 非标产品文件上传 spring MVC 文件上传
	 * 
	 * @param myfiles
	 * @throws IOException
	 */
	@RequestMapping(value = "/fileupload", method = RequestMethod.POST)
	public ResponseEntity<String> fileupload(@Valid MaterialFile materialFile,
			BindingResult result,@RequestParam String saleFor) {

		String json = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		SaleItemDao saleItemDao=SpringContextHolder.getBean("saleItemDao");
		// 判断前台输入的值类型是否跟后台的匹配
		if("XML".equals(materialFile.getFileType())){
			RocoImos rocoImos=new RocoImos();
			String sale;
			try {
				sale = rocoImos.analysisSaleFor(materialFile.getFile()
						.getInputStream());
				if(!saleFor.equals(sale)){
					json="{success :false,msg:'xml文件与该订单不对应'}";
					responseHeaders.setContentType(MediaType.TEXT_HTML);
					return new ResponseEntity<String>(json, responseHeaders,
							HttpStatus.OK);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
		if (result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			// 显示错误信息
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getField() + "|"
						+ fieldError.getDefaultMessage());
			}
			json = "{success: false,msg:'文件上传失败！'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		} else {
			try {
				MaterialHead materialHead = materialManager.getOne(materialFile
						.getMaterialHead().getId(), MaterialHead.class);
				
				List<Map<String,Object>> saleItemList=jdbcTemplate.queryForList("select si.id from sale_item si where si.material_head_id =?",new Object[]{materialHead.getId()});
				SaleItem saleItem = null;
				if(saleItemList!=null && saleItemList.size()>0){
					System.out.println(saleItemList.get(0).get("ID").toString());
					saleItem=saleItemDao.getOne(saleItemList.get(0).get("ID").toString());
				}
			
				if ("XML".equals(materialFile.getFileType())) {
					//XML文本修改校验  add by Mark --start 
					RocoImos rocoImos=new RocoImos();
					StringBuilder sb=new StringBuilder();
					if(!"C7VvwRuidni4P9wEa3Xbfu".equals(materialHead.getId()) &&!rocoImos.validate(materialFile.getFile()
							.getInputStream(),sb)){
						json = "{success: false,msg:'"+sb.toString()+"'}";
						responseHeaders.setContentType(MediaType.TEXT_HTML);
						return new ResponseEntity<String>(json,
								responseHeaders,
								HttpStatus.INTERNAL_SERVER_ERROR);
					}
					//XML文本修改校验 add by Mark --end
					//XML文本信息读取 add by Mark --start
//					MaterialBean mBean=rocoImos.getXmlData(materialFile.getFile().getInputStream());
//					Set<String> saleItemCopySet=new HashSet<String>();
//					saleItemCopySet.add("jiaoQiTianShu");
//					saleItemCopySet.add("jiaoQiTianShuInner");
//					FieldFunction.copyValueIn(saleItem, mBean.getSaleItem(),saleItemCopySet);
//					saleItemCopySet.clear();
//					saleItemCopySet.add("zzYmss");
//					saleItemCopySet.add("zzYmfs");
//					FieldFunction.copyValueIn(materialHead, mBean.getMaterialHead(),saleItemCopySet);
					
					//Xml文本信息读取add by Mark --start
					String orderid = "";
					String imos_path = "";
					String sql = "select t4.order_code,t3.posex,t3.order_code_posex,nvl(t2.imos_path,'0') as imos_path from "
							+ " material_head t2 inner join SALE_ITEM t3 on t2.id=t3.material_head_id"
							+ " inner join sale_header t4 on t3.pid=t4.id where t3.order_code_posex is not null and nvl(t3.state_audit,'C')<>'QX' and t2.id='"
							+ materialFile.getMaterialHead().getId() + "'";
					List<Map<String, Object>> queryForList = jdbcTemplate
							.queryForList(sql);
					if (queryForList != null && queryForList.size() > 0) {
						Map<String, Object> map = queryForList.get(0);
						orderid = map.get("ORDER_CODE_POSEX").toString();
						imos_path = map.get("IMOS_PATH").toString()=="0"?"":map.get("IMOS_PATH").toString();
					}
					CommonsMultipartFile commonsMultipartFile = materialFile
							.getFile();
					String oldFileName = commonsMultipartFile
							.getOriginalFilename();
					String newFileName = orderid + ".xml";
					if (!newFileName.equalsIgnoreCase(oldFileName)) {
						json = "{success: false,msg:'文件名不匹配！'}";
						responseHeaders.setContentType(MediaType.TEXT_HTML);
						return new ResponseEntity<String>(json,
								responseHeaders,
								HttpStatus.INTERNAL_SERVER_ERROR);
					}
					// 如果是XML，那么解析XML信息，分析出型录
					SAXReader saxReader = new SAXReader();
					Document doc = saxReader.read(materialFile.getFile()
							.getInputStream());
					List<Element> elementList = doc
							.selectNodes("//XML/Order/BuilderList/Set/Pname");
					//List<String> specialNodeText=new ArrayList<String>();
					boolean flag=false;
					String imosPath=null;
					for (Element element : elementList) {
						if("TTMK1Q".equals(element.getText()) ||"TTMK2".equals(element.getText()) ||"TTMK3".equals(element.getText()) ){
							flag=true;
							imosPath="IMOS_01";
							break;
						}
						String PVAR_STRING = element.getParent().selectSingleNode("PVarString").getText();
						String[] pvarSpli = PVAR_STRING.split("\\|");
						for (String val : pvarSpli) {
							if(("DOORLR".equals(element.getText())&&"SPUL:=45".equals(val))
									||("DOORTB".equals(element.getText())&&"SPUL:=45".equals(val))
									||("DOORLR_J".equals(element.getText())&&"SPUL:=45".equals(val))
									||("DOORLR".equals(element.getText())&&"SPUL:=46".equals(val))
									||("DOORTB".equals(element.getText())&&"SPUL:=46".equals(val))
									||("DOORLR_J".equals(element.getText())&&"SPUL:=46".equals(val))){
								flag=true;
								imosPath="IMOS_03";
								break;
							}
						}
					}
					if(flag){
						materialHead.setImosPath(imosPath);
					}else{
						materialHead.setImosPath(ImosLoadBalance(true));
					}
					
					//String saleId=jdbcTemplate.queryForObject("select si.pid from sale_item si where si.material_head_id='"+materialHead.getId()+"' and (si.state_audit !='QX' or si.state_audit is null)",String.class);
					List<Map<String, Object>> saleI = jdbcTemplate.queryForList("select si.pid from sale_item si where si.material_head_id='"+materialHead.getId()+"' and (si.state_audit !='QX' or si.state_audit is null)");
					String $saleId=null;
					if(saleI.size()>0){
						$saleId=(String) saleI.get(0).get("PID");
					}
					SaleHeader saleHeader=commonManager.getById($saleId, SaleHeader.class);
					//分出销售分类
					String _saleFor=rocoImos.analysisSaleFor(materialFile.getFile()
							.getInputStream());
					if("3".equals(_saleFor)){
						materialHead.setImosPath("IMOS_04");
					}
					saleHeader.setSaleFor(_saleFor);
					materialHead.setSaleFor(_saleFor);
					//判断是否为木门""
					if ("3".equals(_saleFor)) {
						// 是木门解析XML,木门高，宽，深与客户输入匹配
						List<Element> element = doc
								.selectNodes("//XML/Order/BuilderList/Set/PVarString");
						String hight_desc = materialHead.getHeightDesc();
						String width_desc = materialHead.getWidthDesc();
						String long_desc = materialHead.getLongDesc();
						StringBuffer sbf=new StringBuffer();
						StringBuffer json_data=new StringBuffer();
						boolean  biaosi=false;
						for (Element ele : element) {
							String parText = ele.getText();
							String[] parList = parText.split("\\|");
							bs:for (int i = 0; i < parList.length; i++) {
								System.out.println(parList[i].substring(0,
										parList[i].indexOf(":")));
								if (parList[i].indexOf("Height") != -1) {
									String height = parList[i].substring(
											parList[i].indexOf(":")+2,
											parList[i].length());
									if (!height.equals(hight_desc)) {
										json_data.append("高度尺寸与客户输入不一样！原高度尺寸："+hight_desc+",图纸高度尺寸："+height);
									}
								} else if (parList[i].indexOf("Width") != -1) {
									String width = parList[i].substring(
											parList[i].indexOf(":")+2,
											parList[i].length());
									if (!width.equals(width_desc)) {
										json_data.append(",宽度尺寸与客户输入不一样！原宽度尺寸："+width_desc+",图纸宽度尺寸："+width);
									}

								} else if (!"Depth".equals(parList[i].substring(0,
										parList[i].indexOf(":")))&&parList[i].substring(0,
												parList[i].indexOf(":")).indexOf(
														"Depth")==0) {
									String depth = parList[i].substring(
											parList[i].indexOf(":") + 2,
											parList[i].length());
									if (!depth.equals(long_desc)) {
										json_data.append(",长度尺寸与客户输入不一样！原长度尺寸："+long_desc+",图纸长度尺寸："+depth);
									}
								}
							}
							if (json_data.length()>0) {
								sbf.append(json_data);
								json = "{success: false,msg:'" + sbf.toString()
										+ "'}";
								responseHeaders
										.setContentType(MediaType.TEXT_HTML);
								return new ResponseEntity<String>(json,
										responseHeaders,
										HttpStatus.INTERNAL_SERVER_ERROR);
							}
						}

					}
				}
				
				String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
				List<Map<String, Object>> queryForList = jdbcTemplate
						.queryForList(sql);

				if (queryForList != null && queryForList.size() > 0) {
					Map<String, Object> map = queryForList.get(0);
					String filePath = (String) map.get("DESC_EN_US");
					SysUser sysUser = (SysUser) this.getRequest().getSession()
							.getAttribute("CURR_USER");
					String kunnr = "";
					if (sysUser.getKunnr() != null
							&& !"".equals(sysUser.getKunnr())) {
						kunnr = sysUser.getKunnr();
					} else {
						kunnr = "0000000";
					}
					String SerialNumber = "0000000000";

					if (!StringUtils.isEmpty(materialHead.getSerialNumber())) {
						SerialNumber = materialHead.getSerialNumber();
					} else {
						SerialNumber = materialHead.getMatnr();
					}
					String format = DateTools.getNowDateYYMMDD();
					filePath += MyFileUtil.FILE_DIR + kunnr
							+ MyFileUtil.FILE_DIR + format
							+ MyFileUtil.FILE_DIR + SerialNumber
							+ MyFileUtil.FILE_DIR + materialFile.getFileType();

					String uuid = UUID.randomUUID().toString().replace("-", "");
					CommonsMultipartFile commonsMultipartFile = materialFile
							.getFile();
					String oldName = commonsMultipartFile.getOriginalFilename();
					oldName = oldName.substring(oldName.lastIndexOf("."));

					materialFile.setUploadFileName(uuid + oldName);
					materialFile.setUploadFileNameOld(commonsMultipartFile
							.getOriginalFilename());
					materialFile.setUploadFilePath(filePath);
					boolean flag = MyFileUtil.fileUpload(materialFile);
					if (flag) {
						//如果是上传标准产品的图片,上传一个产品的图片,需要将同一产品编号的标准产品的图片全部都更新
						if("PICTURE".equals(materialFile.getFileType())){
							List<Map<String,Object>> _list=jdbcTemplate.queryForList("select id from material_head mh where mh.matnr=? and is_standard=?",new Object[]{materialHead.getMatnr(),materialHead.getIsStandard()});
							if(_list!=null && _list.size()>0){
								Iterator<Map<String,Object>> ite=_list.iterator();
								while(ite.hasNext()){
									Map<String,Object> _map=ite.next();
									MaterialHead mh=new MaterialHead();
									MaterialFile mf=new MaterialFile();
									mf.setUploadFileName(materialFile.getUploadFileName());
									mf.setUploadFileNameOld(materialFile.getUploadFileNameOld());
									mf.setFileType(materialFile.getFileType());
									mf.setUploadFilePath(materialFile.getUploadFilePath());
									mh.setId(_map.get("ID").toString());
									mf.setMaterialHead(mh);
									materialManager.save(mf);
								}
							}
						}else{
							//commonManager.save(saleItem);
							//commonManager.save(materialHead);
							MaterialFile obj = materialManager.save(materialFile);
						}
						// 失效以前的文件
						//String updateSql = "update  MATERIAL_FILE set status='X' where  id!='"
						//		+ obj.getId()
						//		+ "' and pid='"
						//		+ materialHead.getId()
						//		+ "' and file_type='XML'";
						//jdbcTemplate.update(updateSql);*/
						json = "{success: true,msg:'"
								+ materialHead.getImosPath() + "'}";
					} else {
						json = "{success: false,msg:'文件上传失败！'}";
					}
				} else {
					json = "{success: false,msg:'请配置文件上传路径！'}";
				}

			} catch (Exception e) {
				e.printStackTrace();
				json = "{success: false,msg:'文件上传失败！'}";
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<String>(json, responseHeaders,
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
	}

	/**
	 * 非标产品文件上传 2016-04-27 spring MVC 文件上传
	 * 
	 * @param myfiles
	 * @throws IOException
	 */
	@RequestMapping(value = "/fileuploadFb", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> fileuploadFb(
			@Valid MaterialFile materialFile, BindingResult result) {
		Date date=new Date();
		date.setTime(System.currentTimeMillis());
		logger.info("fileuploadFb 开始", ""+date);
		String json = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		if(materialFile.getFileType().equalsIgnoreCase("kit")||materialFile.getFileType().equalsIgnoreCase("zip")||materialFile.getFileType().equalsIgnoreCase("rar")){
			SysUser sysUser = (SysUser) this.getRequest().getSession().getAttribute("CURR_USER");
			int num = jdbcTemplate.queryForObject("select count(sh.order_code) from sale_header sh left join sale_item si on si.pid=sh.id where si.material_head_id='"+materialFile.getMaterialHead().getId()+"'", Integer.class);
			if(num==0){
				if(!(sysUser.getId().equals("admin")||sysUser.getId().contains("kf")||sysUser.getId().contains("cl"))){
//					String kunnr = sysUser.getCustHeader().getKunnr();
//					if(!(kunnr.equals("LJ75001")||kunnr.equals("LJ75002"))){
						json = "{success: false,msg:'请使用一键传单功能传输"+materialFile.getFileType()+"文件!'}";
						responseHeaders.setContentType(MediaType.TEXT_HTML);
						return new ResponseEntity<String>(json, responseHeaders,
								HttpStatus.OK);
//					}
				}
			}
		}
		if(!materialFile.getFileType().equalsIgnoreCase("dwg")){
			jdbcTemplate.update("update material_head mh set mh.zzcpdj='B',mh.series='009'where mh.id='"+materialFile.getMaterialHead().getId()+"'");
		}
		// 判断前台输入的值类型是否跟后台的匹配
		if (result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			// 显示错误信息
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getField() + "|"
						+ fieldError.getDefaultMessage());
			}
			json = "{success: false,msg:'文件上传失败！'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		} else {
			try {
				MaterialHead materialHead = materialManager.getOne(materialFile
						.getMaterialHead().getId(), MaterialHead.class);
				String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
				List<Map<String, Object>> queryForList = jdbcTemplate
						.queryForList(sql);

				if (queryForList != null && queryForList.size() > 0) {
					Map<String, Object> map = queryForList.get(0);
					String filePath = (String) map.get("DESC_EN_US");
					SysUser sysUser = (SysUser) this.getRequest().getSession()
							.getAttribute("CURR_USER");
					String kunnr = "";
					if (sysUser.getKunnr() != null
							&& !"".equals(sysUser.getKunnr())) {
						kunnr = sysUser.getKunnr();
					} else {
						kunnr = "0000000";
					}
					String SerialNumber = "0000000000";

					if (!StringUtils.isEmpty(materialHead.getSerialNumber())) {
						SerialNumber = materialHead.getSerialNumber();
					} else {
						SerialNumber = materialHead.getMatnr();
					}
					String format = DateTools.getNowDateYYMMDD();
					filePath += MyFileUtil.FILE_DIR + kunnr
							+ MyFileUtil.FILE_DIR + format
							+ MyFileUtil.FILE_DIR + SerialNumber
							+ MyFileUtil.FILE_DIR + materialFile.getFileType();

					String uuid = UUID.randomUUID().toString().replace("-", "");
					CommonsMultipartFile commonsMultipartFile = materialFile
							.getFile();
					String oldName = commonsMultipartFile.getOriginalFilename();
					oldName = oldName.substring(oldName.lastIndexOf("."));

					materialFile.setUploadFileName(uuid + oldName);
					materialFile.setUploadFileNameOld(commonsMultipartFile
							.getOriginalFilename());
					materialFile.setUploadFilePath(filePath);

					boolean flag = MyFileUtil.fileUpload(materialFile);
					if (flag) {
						MaterialFile obj = materialManager.save(materialFile);
						// 失效以前的文件
						//String updateSql = "update  MATERIAL_FILE set status='X' where  id!='"
						//		+ obj.getId()
						//		+ "' and pid='"
						//		+ materialHead.getId() + "'";
						//jdbcTemplate.update(updateSql);

						// 文件类型回写到非标产品
						String type = materialFile.getFileType();
						String mType = "";
						if("KIT".equals(type)|| "PDF".equals(type)||"DWG".equals(type)){
							//PDF上传需要失效之前的PDF
							String updateSql = "update  MATERIAL_FILE set status='X' where  pid='"
										+ materialHead.getId() + "' and file_type='"+type+"' and id!='"+obj.getId()+"'";
							jdbcTemplate.update(updateSql);
						}
						if ("PDF".equals(type)) {
							mType = "2";
						} else {
							mType = "1";
						}
						String updateSql = "update MATERIAL_HEAD mh set  mh.file_type='"
								+ mType
								+ "' where  mh.id='"
								+ materialHead.getId() + "'";
						jdbcTemplate.update(updateSql);
						json = "{success: true}";
					} else {
						json = "{success: false,msg:'文件上传失败！'}";
					}
				} else {
					json = "{success: false,msg:'请配置文件上传路径！'}";
				}

			} catch (Exception e) {
				e.printStackTrace();
				json = "{success: false,msg:'文件上传失败！'}";
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<String>(json, responseHeaders,
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			date.setTime(System.currentTimeMillis());
			logger.info("fileuploadFb 结束", ""+date);
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
	}
	/**
	 * spring MVC 文件上传
	 * 
	 * @param myfiles
	 * @throws IOException
	 */
	@RequestMapping(value = "/bgfileupload", method = RequestMethod.POST)
	public ResponseEntity<String> bgfileupload(@Valid SaleBgfile saleBglFile,
			BindingResult result){
		String json = null;
		try {
			String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(sql);

			if (queryForList != null && queryForList.size() > 0) {
				Map<String, Object> map = queryForList.get(0);
				String filePath = (String) map.get("DESC_EN_US");

				String format = DateTools.getNowDateYYMMDD();
				filePath += MyFileUtil.FILE_DIR + saleBglFile.getFileType()
						+ MyFileUtil.FILE_DIR + format;
				// logger.info(filePath);
				String uuid = UUID.randomUUID().toString().replace("-", "");
				CommonsMultipartFile commonsMultipartFile = saleBglFile
						.getFile();
				String oldName = commonsMultipartFile.getOriginalFilename();
				oldName = oldName.substring(oldName.lastIndexOf("."));

				saleBglFile.setUploadFileName(uuid + oldName);
				saleBglFile.setUploadFileNameOld(commonsMultipartFile
						.getOriginalFilename());
				saleBglFile.setUploadFilePath(filePath);

				boolean flag = MyFileUtil.bgUpload(saleBglFile);
				if (flag) {
					SaleBgfile obj = materialManager.save(saleBglFile);
//					 int a =jdbcTemplate.update("update sale_bg_header bh set bh.fujianpath ='"+saleBglFile.getId()+"' where bh.sale_id='"+saleBglFile.getPid()+"'");
//					System.out.println(a);
					json = "{success: true}";
				} else {
					json = "{success: false,msg:'文件上传失败！'}";
				}
			} else {
				json = "{success: false,msg:'请配置文件上传路径！'}";
			}

		} catch (Exception e) {
			e.printStackTrace();
			json = "{success: false,msg:'文件上传失败！'}";
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
	}

	/**
	 * spring MVC 文件上传
	 * 
	 * @param myfiles
	 * @throws IOException
	 */
	@RequestMapping(value = "/fileupload2", method = RequestMethod.POST)
	public ResponseEntity<String> fileupload2(@Valid MaterialFile materialFile,
			BindingResult result) {
		String json = null;
		try {
			String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
			List<Map<String, Object>> queryForList = jdbcTemplate
					.queryForList(sql);

			if (queryForList != null && queryForList.size() > 0) {
				Map<String, Object> map = queryForList.get(0);
				String filePath = (String) map.get("DESC_EN_US");

				String format = DateTools.getNowDateYYMMDD();
				filePath += MyFileUtil.FILE_DIR + materialFile.getFileType()
						+ MyFileUtil.FILE_DIR + format;
				// logger.info(filePath);
				String uuid = UUID.randomUUID().toString().replace("-", "");
				CommonsMultipartFile commonsMultipartFile = materialFile
						.getFile();
				String oldName = commonsMultipartFile.getOriginalFilename();
				oldName = oldName.substring(oldName.lastIndexOf("."));

				materialFile.setUploadFileName(uuid + oldName);
				materialFile.setUploadFileNameOld(commonsMultipartFile
						.getOriginalFilename());
				materialFile.setUploadFilePath(filePath);

				boolean flag = MyFileUtil.fileUpload(materialFile);
				if (flag) {
					MaterialFile obj = materialManager.save(materialFile);
					json = "{success: true}";
				} else {
					json = "{success: false,msg:'文件上传失败！'}";
				}
			} else {
				json = "{success: false,msg:'请配置文件上传路径！'}";
			}

		} catch (Exception e) {
			e.printStackTrace();
			json = "{success: false,msg:'文件上传失败！'}";
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders, HttpStatus.OK);
	}

	/**
	 * 针对imos炸单失败文件下载
	 * 
	 * @param id
	 *            mes_imos_fail id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/fileDownload2/{resource}")
	public Message fileDownload2(@PathVariable String resource,
			HttpServletRequest request, HttpServletResponse response) {
		String materialFileId = jdbcTemplate
				.queryForObject(
						" select mf.id from material_file mf where mf.pid=(select si.material_head_id from sale_Item si where si.order_code_posex=(select t.orderid from mes_imos_fail t where t.id='"
								+ resource
								+ "')) and mf.file_type='XML' and nvl(mf.status,'A')!='X' and rownum=1 ",
						String.class);
		return fileDownload(materialFileId, request, response);
	}

	/**
	 * 非标产品下载
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/fileDownload")
	public Message fileDownload(String id, HttpServletRequest request,
			HttpServletResponse response) {
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Message msg = null;
		try {
			MaterialFile obj = materialManager.getOne(id, MaterialFile.class);
			if (obj != null) {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("multipart/form-data");
				StringBuffer fileName=new StringBuffer();
				System.out.println(fileName);
				if(obj.getFileType().equalsIgnoreCase("kit")){
					List<Map<String, Object>> list = jdbcTemplate.queryForList("select si.order_code_posex from sale_item si,material_file mf where mf.pid=si.material_head_id and mf.id='"+obj.getId()+"'");
					if(list!=null&&list.size()>0&&list.get(0)!=null){
						fileName=fileName.append("#"+list.get(0).get("order_code_posex")+"#");
					}
				}
				fileName=fileName.append(obj.getUploadFileNameOld());
				response.setHeader("Content-Disposition",
						"attachment;fileName="
								+ new String(fileName.toString()
										.replace(",", "及").getBytes("gbk"),
										"ISO8859-1"));

				String uploadFilePath = obj.getUploadFilePath();
				String uploadFileName = obj.getUploadFileName();
				inputStream = new BufferedInputStream(new SmbFileInputStream(
						new SmbFile(uploadFilePath + MyFileUtil.FILE_DIR
								+ uploadFileName)));

				outputStream = response.getOutputStream();
				byte[] b = new byte[2048];
				int length;
				while ((length = inputStream.read(b)) > 0) {
					outputStream.write(b, 0, length);
				}
			} else {
				msg = new Message("MM-fileDownload-500", "文件下载失败!");
			}
		} catch (IOException e) {
			// e.printStackTrace();
			msg = new Message("MM-fileDownload-500", "文件下载失败!");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-fileDownload-500", "文件下载失败!");
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * PDF文件批量下载
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/bitfileDownload")
	public Message bitfileDownload(HttpServletRequest request,
			HttpServletResponse response) {
		OutputStream os = null;
		BufferedInputStream bis = null;
		Message msg = null;
		try {
			String ids = request.getParameter("ids");
			String orderCode = request.getParameter("tempordercode");
			String[] fileId = ids.split(",");
			String fileytpe = "";// 下载文件类型

			List<String> files = new ArrayList<String>();
			List<Map<String, SmbFile>> fileList = new ArrayList<Map<String, SmbFile>>();
			Map<String, SmbFile> fileMap = new HashMap<String, SmbFile>();

			// 读取DB中文件的位置,以及文件名称,放入列表中
			for (String id : fileId) {
				String path = "";
				MaterialFile obj = materialManager.getOne(id,
						MaterialFile.class);
				if (obj != null) {
					String sql = " select aa.posex from sale_item aa,material_file mf where aa.material_head_id = mf.pid and mf.id = '"
							+ id + "'";
					String posex = jdbcTemplate.queryForObject(sql,
							String.class);
					String uploadFilePath = obj.getUploadFilePath();
					String uploadFileName = obj.getUploadFileName();
					String uploadFileNameOld = obj.getUploadFileNameOld();
					path = uploadFilePath + MyFileUtil.FILE_DIR
							+ uploadFileName;
					files.add(path);
					fileytpe = uploadFileNameOld.substring(
							uploadFileNameOld.length() - 4,
							uploadFileNameOld.length());// 取字符后3位， 如pdf,xls
					fileMap.put(posex + "." + fileytpe, new SmbFile(path));
				}
			}
			fileList.add(fileMap);
			ZipUtils.downloadZip(fileList, orderCode, response, request)
					.getOutputStream();
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("下载失败!");
		} finally {
			try {
				if (os != null) {
					os.close();
				}
				if (bis != null) {
					bis.close();
				}
			} catch (Exception e) {
			}
		}

		return msg;
	}

	@RequestMapping("/batchFileDownload")
	public Message batchFileDownload(HttpServletRequest request,
			HttpServletResponse response) {
		Message msg = null;
		FreemarkerGenerialExcel generialExcel=new FreemarkerGenerialExcel();
		String orderCode = request.getParameter("tempordercode");
		List<SaleHeader> saleHeader = saleHeaderDao.findByCode(orderCode);
		if(saleHeader.size()<=0) {
			msg = new Message("下载失败!");
			return msg;
		}
		Set<SaleItem> saleItemSet = saleHeader.get(0).getSaleItemSet();
		List<File> excellFile=new ArrayList<File>();
		for (SaleItem saleItem : saleItemSet) {
			if(saleItem.getStateAudit()!=null&&!"QX".equals(saleItem.getStateAudit())) {
				File file = generialExcel.downFreemarkerExcel(saleItem.getOrderCodePosex());
				if(file!=null) {
					String rename = file.getPath();
					rename = rename.substring(0, rename.lastIndexOf(File.separator));
					StringBuffer filePath=new StringBuffer();
					filePath.append(rename).append(File.separator).append(saleItem.getOrderCodePosex()).append(".xls");
					File newFile = new File(filePath.toString());
					file.renameTo(newFile);
					excellFile.add(newFile);
					file.delete();
				}
			}
		}
		ZipUtils.resolverZip(excellFile, orderCode, response, request);
		return null;
	}
	/**
	 * 删除 产品（标准产品 或 非标产品） 标记删除 Status设置为X 根据id数组 删除materialHead
	 * 
	 * @param ids
	 * @return @
	 */
	@RequestMapping(value = "/deleteMaterialHeadByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteMaterialHeadByIds(String[] ids) {
		Message msg = null;
		try {
			materialManager.deleteMaterialHeadByIds(ids);
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-delete-500", "删除失败!");
		}
		return msg;
	}

	/**
	 * 删除 定价条件(标记删除)标记删除 Status设置为X 根据id数组 删除
	 * 
	 * @param ids
	 * @return @
	 */
	@RequestMapping(value = "/deletePriceConditionByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deletePriceConditionByIds(String[] ids) {
		Message msg = null;
		try {
			materialManager.delete(ids, PriceCondition.class);
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-delete-500", "删除失败!");
		}
		return msg;
	}

	/**
	 * 删除商品属性 标记删除 Status设置为X
	 * 
	 * @param ids
	 * @return
	 */
	@RequestMapping(value = "/deleteMaterialPropertyByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteMaterialPropertyByIds(String[] ids, String pid) {
		Message msg = null;
		try {
			materialManager.deleteMaterialPropertyByIds(ids, pid);
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-delete-500", "删除失败!");
		}
		return msg;
	}

	/**
	 * 接口查询物料清单数据
	 * 
	 * @param bomNo
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return @
	 */
	@RequestMapping(value = "/queryMaterialdetail", method = RequestMethod.GET)
	@ResponseBody
	public JSONObject queryMaterialdetail(int start, int page, int limit,
			String sort) {
		// String[] strings = new String[] {"hibernateLazyInitializer",
		// "handler","fieldHandler","sort","materialHead" };
		// Page<MaterialItem> all = mmMaterialItemDao.findAll(new
		// PageRequest(page - 1, limit, null));
		// System.out.println(JSONObject.fromObject(all,
		// super.getJsonConfig(strings)));
		// return JSONObject.fromObject(all, super.getJsonConfig(strings));
		return null;
	}

	/**
	 * 标准产品查询动态属性
	 * 
	 * @RequestMapping(value = "/queryMaterialPropertyDesc", method =
	 *                       RequestMethod.GET)
	 * @ResponseBody public JSONArray queryMaterialPropertyDesc(String id){ try
	 *               { String sql =
	 *               "select * from  material_bz_view t where  1=1 and t.pid =? order by t.orderby "
	 *               ;
	 * 
	 *               String sql2 =
	 *               "select d.desc_zh_cn,d.key_val as id from material_property_item t left join sys_data_dict d on t.propertyInfo  = d.key_val left join sys_trie_tree t1 on t1.id = d.trie_id "
	 *               +
	 *               " where t.status is null and t.pid = ? and t1.key_val = ? group by d.desc_zh_cn,d.key_val "
	 *               ;
	 * 
	 *               List<Map<String, Object>> lists =
	 *               jdbcTemplate.queryForList(sql,id);
	 * 
	 *               List<MmDataDictBean> newLists = null; if(lists!=null &&
	 *               lists.size()>0){ newLists = new
	 *               ArrayList<MmDataDictBean>();
	 * 
	 *               Map<String,String> propertyMap = new
	 *               HashMap<String,String>();
	 * 
	 *               String headPropertyDesc =
	 *               (String)lists.get(0).get("HEAD_PROPERTY_DESC");
	 *               if(StringUtils.isEmpty(headPropertyDesc)){ return new
	 *               JSONArray(); }else{ String[] split =
	 *               headPropertyDesc.split(","); for (String str : split) {
	 *               String[] split2 = str.split(":");
	 *               propertyMap.put(split2[1],split2[0]); } } for (Map<String,
	 *               Object> map : lists) {
	 * 
	 *               String infoDesc = (String)map.get("INFO_DESC"); String name
	 *               = (String)map.get("PROPERTY_DESC"); String code =
	 *               (String)map.get("PROPERTY_CODE"); String keyId =
	 *               (String)map.get("ID");
	 * 
	 * 
	 *               String val = propertyMap.get(code);
	 *               if(StringUtils.isEmpty(val)){ return new JSONArray(); }
	 *               MmDataDictBean bean = new MmDataDictBean();
	 *               bean.setName(name); bean.setInfoDesc(val);
	 *               bean.setCode(code);
	 * 
	 *               String replace = sql2.replace("propertyInfo", val);
	 *               List<Map<String, Object>> queryForList =
	 *               jdbcTemplate.queryForList(replace,id,code);
	 * 
	 *               if(queryForList!=null && queryForList.size()>0){
	 *               List<MmDataDictBean> items = new
	 *               ArrayList<MmDataDictBean>();
	 * 
	 *               for (Map<String, Object> map2 : queryForList) { String
	 *               itemDesc = (String)map2.get("DESC_ZH_CN"); String itemId =
	 *               (String)map2.get("ID");
	 * 
	 *               MmDataDictBean itemBean = new MmDataDictBean();
	 *               itemBean.setId(itemId); itemBean.setName(itemDesc);
	 *               items.add(itemBean);
	 * 
	 *               } bean.setItems(items);
	 * 
	 *               newLists.add(bean); }else{ return new JSONArray(); }
	 * 
	 *               } return JSONArray.fromObject(newLists); }else{ return new
	 *               JSONArray(); } } catch (Exception e) { e.printStackTrace();
	 *               return new JSONArray(); } }
	 */
	/**
	 * combobox查询 SysDataDicd
	 * 
	 * @return
	 */
	@RequestMapping(value = "/queryBySysDataDicd", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryBySysDataDicd(String keyVal) {
		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select t.* from  sys_data_dict_view t where t.TRIE_KEY_VAL = '"
						+ keyVal + "' ");
		return JSONArray.fromObject(queryForList);
	}

	/**
	 * 查询属性
	 * 
	 * @param start
	 * @param page
	 * @param limit
	 * @param sort
	 * @return
	 */
	@RequestMapping(value = "/queryDataDicts", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryDataDicts(String id) {
		JSONArray jsonArray = queryPropertyView(id);
		return jsonArray;
	}

	/**
	 * 销售价格查询
	 * 
	 * @param pid
	 * @param fileType
	 * @return
	 */
	@RequestMapping(value = "/querySaleItemPrice", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray querySaleItemPrice(String pid) {
		List<SaleItemPrice> lists = saleManager.querySaleItemPrice(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "saleItem" };
		// System.out.println(JSONArray.fromObject(lists,
		// super.getJsonConfig(strings)));
		return JSONArray.fromObject(lists, super.getJsonConfig(strings));
	}

	/**
	 * 查询SaleItem
	 * 
	 * @param pid
	 * @param fileType
	 * @return
	 */
	@RequestMapping(value = "/getSaleItem", method = RequestMethod.GET)
	@ResponseBody
	public Message getSaleItem(String id) {
		Message msg = null;
		try {
			SaleItem findOne = commonManager.getOne(id, SaleItem.class);
			if (findOne != null) {
				String[] strings = new String[] { "hibernateLazyInitializer",
						"handler", "fieldHandler", "sort", "saleHeader",
						"saleItemPrices" };
				msg = new Message(JSONObject.fromObject(findOne,
						super.getJsonConfig("yyyy-MM-dd", strings)));
			} else {
				msg = new Message("MM-getSaleItem-500", "销售价格信息加载失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-getSaleItem-500", "销售价格信息加载失败!");
		}
		return msg;
	}

	/**
	 * 更新订单SaleItem 改数量和价格 更新订单SaleItemPrice 定价过程
	 */
	@RequestMapping(value = "/updateSaleItem", method = RequestMethod.POST)
	@ResponseBody
	public Message updateSaleItem(@RequestBody MaterialBean materialBean,
			BindingResult result) {
		Message msg = null;
		try {
			SaleItem obj = materialManager.updateSaleItem(materialBean);
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-updateSaleItem-500", "保存失败!");
		}
		return msg;
	}

	/**
	 * 查询配置属性
	 * 
	 * @param id
	 *            , materialHead.id
	 * @return
	 */
	public JSONArray queryPropertyView(String id) {

		List<Map<String, Object>> lists = jdbcTemplate
				.queryForList(
						" select * from MATERIAL_PROPERTY_DICT_VIEW t WHERE t.pid=? order by t.orderby ",
						id);
		List<MmDataDictBean> newlists = null;

		if (lists != null && lists.size() > 0) {

			newlists = new ArrayList<MmDataDictBean>();
			Map<String, MmDataDictBean> map = new LinkedHashMap<String, MmDataDictBean>();

			for (Map<String, Object> obj : lists) {

				String code = (String) obj.get("PROPERTY_CODE");
				if (StringUtils.isEmpty(map.get(code))) {
					String name = (String) obj.get("PROPERTY_DESC");
					String infoDesc = (String) obj.get("INFO_DESC");

					MmDataDictBean bean = new MmDataDictBean();
					bean.setName(name);
					bean.setCode(code);
					map.put(code, bean);
				}
			}

			Set<Entry<String, MmDataDictBean>> entrySet = map.entrySet();
			for (Iterator iterator = entrySet.iterator(); iterator.hasNext();) {
				Entry<String, MmDataDictBean> entry = (Entry<String, MmDataDictBean>) iterator
						.next();
				String key = entry.getKey();
				MmDataDictBean value = entry.getValue();
				List<MmDataDictBean> items = new ArrayList<MmDataDictBean>();

				for (Map<String, Object> obj : lists) {
					String keyId = (String) obj.get("PROPERTY_CODE");
					if (key.equals(keyId)) {
						MmDataDictBean itembean = new MmDataDictBean();
						itembean.setName((String) obj.get("DESC_ZH_CN"));
						itembean.setCode((String) obj.get("DICTID"));
						String price = (String) obj.get("DESC_EN_US");
						if (StringUtils.isEmpty(price)) {
							itembean.setPrice(0.0);
						} else {
							itembean.setPrice(Double.valueOf(price));
						}
						items.add(itembean);
					}
				}

				value.setItems(items);
				newlists.add(value);
			}
			// System.out.println(JSONArray.fromObject(newlists));
			return JSONArray.fromObject(newlists);
		} else {
			// System.out.println("数据字典不存在");
			return new JSONArray();
		}
	}

	/**
	 * syncMatnr(同步物料数据)
	 */
	@RequestMapping(value = { "/syncMatnr" }, method = RequestMethod.GET)
	@ResponseBody
	public Message syncMatnr() {
		Message msg = null;
		try {
			materialManager.syncMatnr();
			msg = new Message("同步成功！");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-syncMatnr-500", "同步失败！");
		}
		return msg;
	}

	/**
	 * 获取订单head
	 */
	@RequestMapping(value = { "/querySaleHeaderByid" }, method = RequestMethod.GET)
	@ResponseBody
	public Message querySaleHeaderByid(String id) {
		Message msg = null;
		try {
			SaleHeader obj = commonManager.getOne(id, SaleHeader.class);
			if (obj != null) {
				JSONObject jo = new JSONObject();
				jo.put("saleOrder", obj.getOrderCode());
				msg = new Message(JSONObject.fromObject(jo));
			} else {
				msg = new Message("0");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-querySaleHeader-500", "获取数据失败！");
		}
		return msg;
	}

	/**
	 * 订单审汇gp_drawing
	 */
	@RequestMapping(value = { "/gpdrawingValidate" }, method = RequestMethod.GET)
	@ResponseBody
	public Message gpdrawingValidate(String saleHeadId) {
		Message msg = null;
		Connection conn=null;
		PreparedStatement statement=null;
		Transaction transaction=null;
		Jedis jedis=null;
		try {
			//初始化
			conn=jdbcTemplate.getDataSource().getConnection();
			statement=conn.prepareStatement(INSERT_XML_LIST);
			PreparedStatement statement2=conn.prepareStatement("select 1 from dual");
			jedis=jedisPool.getResource();
			//开启redis事务
			transaction=jedis.multi();
			
			StringBuffer sb = null;
			Map<String, String> errorMap = new LinkedHashMap<String, String>();
			List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(saleHeadId);
			sb = new StringBuffer();
			if(saleLogisticsList.size()<=0) {
				sb.append("未生成物流信息<br/>");
			}
			/*for (SaleLogistics saleLogistics : saleLogisticsList) {
				if(saleLogistics.getDeliveryDay()==null||"".equals(saleLogistics.getDeliveryDay())) {
					sb.append("订单："+saleLogistics.getSaleHeader().getOrderCode()+"-->>"+saleLogistics.getSaleFor()+"未选择交期<br/>");
				}
			}*/
/*			//三期 校验 imos 服务器  要一致
			List<Map<String, Object>> imosOnly = jdbcTemplate.queryForList("SELECT COUNT(MH.ID) AS ID_S,MH.IMOS_PATH FROM SALE_ITEM SI LEFT JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID=MH.ID WHERE SI.PID='"+saleHeadId+"' AND MH.IS_STANDARD='0' GROUP BY MH.IMOS_PATH");
			if(imosOnly.size()>1) {
				sb.append("IMOS 服务器 需选择一致");
			}*/
			if(sb.length()>0)errorMap.put("jiaoQiTianShu", sb.toString());

			String sql = "select t.state_audit,t.order_code_posex,t.posex,t3.order_code,t2.sale_for,t2.serial_number,t.id,t.material_head_id,t.is_standard,t2.draw_type,t2.imos_path,t2.file_type,t2.zzwgfg,t2.zztyar,t2.zzzkar,t2.zzymfs,t2.zzymss,t2.zzxsfs,t2.draw_type,t3.order_type,"
				+ "(select m.id from (select mf.id,mf.pid from MATERIAL_FILE mf where mf.file_type='XML' and nvl(mf.status,'C')!='X'  order by mf.create_time desc) m where  m.pid=t2.id and rownum=1) xml_id,"
				+ // xml文件 空则不存在
				"(select count(1) as s from (select mf.id, mf.pid from MATERIAL_FILE mf where mf.file_type = 'PDF' and nvl(mf.status, 'C') != 'X') m where m.pid = t2.id) pdf_count,"
				+ // pdf文件 空则不存在
				"(select m.id from (select mf.id,mf.pid from MATERIAL_FILE mf where mf.file_type='KIT' and nvl(mf.status,'C')!='X'  order by mf.create_time desc) m where  m.pid=t2.id and rownum=1) kit_id"
				+ // kit文件 空则不存在
				" from SALE_ITEM t"
				+ " inner join material_head t2 on t.material_head_id = t2.id "
				+ "inner join sale_header t3 on t3.id =t.pid "
				+ " where T.PARENT_ID IS NULL AND t.state_audit!='QX' AND t.is_standard='0' and t.pid='"
				+ saleHeadId + "' order by to_number(t.posex)";
			String sqlitem = "SELECT SI.POSEX,SI.STATE_AUDIT FROM SALE_ITEM SI INNER JOIN MATERIAL_HEAD MH ON SI.MATERIAL_HEAD_ID = MH.ID INNER JOIN SALE_HEADER SH ON SH.ID = SI.PID WHERE SI.PID = '"+saleHeadId+"' AND SI.PARENT_ID IS NULL AND SI.IS_STANDARD = '0'";
			List<Map<String, Object>> stateaudit = jdbcTemplate.queryForList(sqlitem);
			boolean fla = true;
			for (Map<String, Object> map : stateaudit) {
				if(StringUtil.isEmpty(map.get("STATE_AUDIT"))) {
					sb = new StringBuffer();
					sb.append("行请先审绘!!");
					errorMap.put("" + map.get("POSEX"), sb.toString());
					fla=false;
					Set<Entry<String, String>> entrySet = errorMap.entrySet();
					sb = new StringBuffer();
					for (Iterator iterator = entrySet.iterator(); iterator
							.hasNext();) {
						Entry<String, String> entry = (Entry<String, String>) iterator
								.next();
						String key = entry.getKey();
						sb.append("订单明细行号：").append(entry.getKey());
						sb.append("  ").append(entry.getValue())
						.append("<br/>");
						
					}
					msg = new Message("false", sb.toString());
					break;
				}
			}
			if(fla) {
				List<Map<String, Object>> query = jdbcTemplate.queryForList(sql);
				String orderId = "";
				String xmlPath = "";
				if (errorMap.isEmpty()) {
					for (Map<String, Object> map : query) {
						sb = new StringBuffer();
						if(("OR3".equals(map.get("order_type"))||"OR4".equals(map.get("order_type")))&&"2".equals(map.get("draw_type"))) {
							break;
						}
						/****************** 附加信息 **********************/
						if (StringUtils.isEmpty(map.get("zztyar"))) {
							sb.append("投影面积必填！");
						}
						// if(StringUtils.isEmpty(map.get("zzzkar"))){
						// sb.append("板件展开面积必填！");
						// }
						if (StringUtils.isEmpty(map.get("zzymfs"))) {
							sb.append("移门方数必填！");
						}
						if (StringUtils.isEmpty(map.get("zzymss"))) {
							sb.append("移门扇数必填！");
						}
						// if(StringUtils.isEmpty(map.get("zzxsfs"))){
						// sb.append("吸塑方数必填！");
						// }
						/****************** 附加信息 **********************/
						if (StringUtils.isEmpty(map.get("DRAW_TYPE"))) {
							sb.append("画图类型必填！");
						}
						// 对应数据字典
						// fileType 1:2020附件 2：pdf附件
						// drawType 1:2020绘图，2：imos绘图，3：标准2020,4：A单
						// 画图类型为必选
						// 3：标准2020,4：A单 ,5:单独移门，IMOS服务器必选，必须要一个有效的xml
						// 1:2020绘图，2：imos绘图，IMOS服务器可选，xml不验证
						List<MaterialFile> fileLists = null;
						// String materialHeadId =
						// (String)map.get("materialHeadId");
						if ("1".equals(map.get("FILE_TYPE"))
								&& ("5".equals(map.get("DRAW_TYPE"))
										|| "4".equals(map.get("DRAW_TYPE")) || "3"
										.equals(map.get("DRAW_TYPE")))) {
							// t.status is null为有效文件
							// fileLists =
							// materialManager.queryMaterialFile(materialHeadId,
							// "XML");
							// if(fileLists==null||fileLists.size()==0){
							// sb.append("请上传xml文件！");
							// }
							if(!"2".equals((String)map.get("SALE_FOR"))){
								//石台不需要上传XML
								String xml = (String) map.get("XML_ID");
								if (xml == null || "null".equals(xml)
										|| (StringUtils.isEmpty(xml))) {
									sb.append("请上传xml文件！");
								}
							}
							if (StringUtils.isEmpty(map.get("IMOS_PATH"))) {
								sb.append("请选择IMOS服务器,并保存！");
							}
						}
						
						// fileLists =
						// materialManager.queryMaterialFile(materialHeadId, "PDF");
						// if(fileLists==null||fileLists.size()==0){
						// sb.append("请上传pdf文件！");
						// }
						int pdfCount=0;
						if(!"1".equals((String)map.get("SALE_FOR"))){
							pdfCount =  map.get("PDF_COUNT")==null?0:Integer.parseInt(map.get("PDF_COUNT").toString());
							if (pdfCount!=1) {
								sb.append("pdf文件必需为一个！");
							}
						}else{
							//saleFor=1   表明为橱柜,那么可以上传多个图纸
							if(pdfCount!=2){
								//sb.append();
							}
						}
						if ("1".equals("" + map.get("FILE_TYPE"))) {
							// fileLists =
							// materialManager.queryMaterialFile(materialHeadId,
							// "KIT");
							// if(fileLists==null||fileLists.size()==0){
							// sb.append("请上传kit文件！");
							// }
							String kit = (String) map.get("KIT_ID");
							if (kit == null || "null".equals(kit)) {
								sb.append("请上传kit文件！");
							}
						}
						String materialHeadId = ZStringUtils.resolverStr(map.get("MATERIAL_HEAD_ID"));
						List<MaterialFile> materialFiles = materialFileDao.queryMaterialFile(materialHeadId, "XML");
						if(materialFiles.size()>0) {
							MaterialFile materialFile = materialFiles.get(0);
							String uploadFilePath = materialFile.getUploadFilePath();
							SmbFile smbFile=new SmbFile(uploadFilePath+File.separator+materialFile.getUploadFileName());
							if(smbFile.exists()) {
								SmbFileInputStream fileInputStream=new SmbFileInputStream(smbFile);
								SAXReader reader=new SAXReader();
								Document document = reader.read(fileInputStream);
								RocoImos imos=new RocoImos();
								JSONArray array = imos.validationXmlText(document, jdbcTemplate);
								boolean flg=true;
								if(array!=null) {
									for (int i = 0; i < array.size(); i++) {
										JSONObject obj=array.getJSONObject(i);
										flg=obj.getBoolean("failure");
										if(!flg){
											//返回解析的XML信息 obj.getString("infoCode")
											sb.append("订单："+map.get("ORDER_CODE_POSEX")+"行项："+map.get("POSEX")+":"+obj.getString("infoCode")+"<br/>");
										}
									}
								}
							}
						}
						if (!StringUtils.isEmpty(sb.toString())) {
							errorMap.put("" + map.get("POSEX"), sb.toString());
						}
					}
				}
				if (errorMap.isEmpty()) {
					// 验证通过
					// 绘图类型3：标准2020,4：A单 ---推送炸单
					for (Map<String, Object> map : query) {
						
						if (("5".equals(map.get("DRAW_TYPE")))
								|| ("1".equals(map.get("FILE_TYPE"))
										&& "4".equals(map.get("DRAW_TYPE")) || "3"
										.equals(map.get("DRAW_TYPE")))) {
							// 以下状态是不需要重新炸单
							if ("D".equals(map.get("STATE_AUDIT"))
									|| "E".equals(map.get("STATE_AUDIT"))
									|| "QX".equals(map.get("STATE_AUDIT"))) {
								continue;
							}
							orderId = map.get("ORDER_CODE_POSEX").toString();
							String materialHeadId = (String) map
									.get("MATERIAL_HEAD_ID");
							// List<MaterialFile> fileLists =
							// materialManager.queryMaterialFile(materialHeadId,
							// "XML");
							// MaterialFile materialFile = fileLists.get(0);
							// String uploadFilePath =
							// materialFile.getUploadFilePath();
							// String uploadFileName =
							// materialFile.getUploadFileName();
							
							List<Map<String, Object>> mfs = jdbcTemplate
									.queryForList("select * from( select mf.id xml_id,mf.pid,mf.upload_file_path,mf.upload_file_name from MATERIAL_FILE mf where mf.file_type='XML' and nvl(mf.status,'C')!='X' and mf.pid='"
											+ materialHeadId
											+ "'  order by mf.create_time desc) m where rownum=1");
							String uploadFilePath = mfs.get(0)
									.get("UPLOAD_FILE_PATH").toString();
							String uploadFileName = mfs.get(0)
									.get("UPLOAD_FILE_NAME").toString();
							xmlPath = uploadFilePath + MyFileUtil.FILE_DIR
									+ uploadFileName;
							
							// 获取IMOS路径
							String imosPath = map.get("IMOS_PATH").toString();
							SysDataDict sysDataDict = sysDataDictDao
									.findByKeyVal(imosPath);
							// SysDataDict sysDataDict=
							// dataDictDaoImpl.getSysDataDict("IMOS_PATH",
							// imosPath);
							
							// 推送IMOS服务器炸单
							RocoImos rocoImos = new RocoImos();
							if (!rocoImos.sendImosFile(statement,transaction,orderId,uploadFileName.split(".xml")[0], xmlPath,
									sysDataDict.getKeyVal())) {
								sb = new StringBuffer();
								sb.append("推送炸单失败！");
								errorMap.put("" + map.get("posex"), sb.toString());
							} else {
								// 插入一条记录，记录单子丢进哪个炸单服务器
								if (jdbcTemplate.queryForList(
										"select * from imos_load_balance ilb where ilb.order_code='"
												+ orderId + "'").size() == 0) {
									statement2.executeUpdate
									("insert into imos_load_balance(create_time,order_code,imos_path,status) values(sysdate,'"
											+ orderId
											+ "','"
											+ imosPath
											+ "',0)");
								} else {
									statement2.executeUpdate
									("update imos_load_balance ilb set ilb.status=0 where ilb.order_code='"
											+ orderId + "'");
								}
								
								statement2.executeUpdate("delete imos_idbext  where  orderid='"
										+ orderId + "'");
								statement2.executeUpdate("delete imos_idbwg  where  orderid='"
										+ orderId + "'");
							}
							
						}
						System.out.print("推送炸单：" + map.get("ORDER_CODE_POSEX"));
					}
					// 所有炸单推送成功
					if (errorMap.isEmpty()) {
						msg = new Message("true");
						//所有成功才会执行这条命令
						transaction.exec();
						statement.executeBatch();
						conn.commit();
					} else {
						Set<Entry<String, String>> entrySet = errorMap.entrySet();
						sb = new StringBuffer();
						for (Iterator iterator = entrySet.iterator(); iterator
								.hasNext();) {
							Entry<String, String> entry = (Entry<String, String>) iterator
									.next();
							String key = entry.getKey();
							sb.append("订单明细行号：").append(entry.getKey());
							sb.append("  ").append(entry.getValue())
							.append("<br/>");
						}
						msg = new Message("false", sb.toString());
					}
				} else {
					Set<Entry<String, String>> entrySet = errorMap.entrySet();
					sb = new StringBuffer();
					for (Iterator iterator = entrySet.iterator(); iterator
							.hasNext();) {
						Entry<String, String> entry = (Entry<String, String>) iterator
								.next();
						String key = entry.getKey();
						if ("jiaoQiTianShu".equals(key)) {
							sb.append(entry.getValue()).append("<br/>");
						} else {
							sb.append("订单明细行号：").append(entry.getKey());
							sb.append("  ").append(entry.getValue())
							.append("<br/>");
						}
					}
					msg = new Message("false", sb.toString());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-gpdrawingValidate-500", "获取数据失败！");
		}finally{
			//关闭所有会话和事务
			try{
				//数据连接关闭
				if(conn!=null){
					conn.close();
				}
				//关闭事务
				if(statement!=null){
					statement.close();
				}
				//将redis连接返回到jedis连接池中
				if(jedis!=null){
					jedisPool.returnResource(jedis);
				}
				//关闭redis事务
				if(transaction!=null){
					transaction.close();
					//jedis.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * gp_drawing_2020 2020绘图 检查xml文件
	 */
	@RequestMapping(value = { "/gpdrawing2020Validate" }, method = RequestMethod.GET)
	@ResponseBody
	public Message gpdrawing2020Validate(String materialHeadId) {
		Message msg = null;
		Connection conn=null;
		PreparedStatement statement=null;
		Transaction transaction=null;
		Jedis jedis=null;
		try {
			//初始化
			conn=jdbcTemplate.getDataSource().getConnection();
			statement=conn.prepareStatement(INSERT_XML_LIST);
			PreparedStatement statement2=conn.prepareStatement("select 1 from dual");
			jedis=jedisPool.getResource();
			//开启redis事务
			transaction=jedis.multi();
			
			
			StringBuffer sb = null;
			sb = new StringBuffer();
			// t.status is null为有效文件
			String sql = "select t.id,t4.order_code,t2.imos_path,t3.order_code_posex,t3.posex,t.upload_file_name,t.upload_file_path from material_file t "
					+ " inner join material_head t2 on t.pid=t2.id"
					+ " inner join SALE_ITEM t3 on t2.id=t3.material_head_id"
					+ " inner join sale_header t4 on t3.pid=t4.id"
					+ " where t.file_type='XML'  and t.status is null  and t.pid=?  order by t.create_time desc";
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(
					sql, materialHeadId);
			if (queryForList == null || queryForList.size() == 0) {
				sb.append("请上传xml文件！");
				msg = new Message("false", sb.toString());
			} else {
				Map<String, Object> map = queryForList.get(0);
				if (StringUtils.isEmpty(map.get("IMOS_PATH"))) {
					sb.append("请选择IMOS服务器,并保存！");
					msg = new Message("false", sb.toString());
				} else {
					String imosPathVal = map.get("IMOS_PATH").toString();
					SysDataDict sysDataDict = sysDataDictDao
							.findByKeyVal(imosPathVal);
					String imosPath = sysDataDict.getKeyVal();
					String orderId = map.get("ORDER_CODE_POSEX").toString();
					String uploadFilePath = map.get("UPLOAD_FILE_PATH")
							.toString();
					String uploadFileName = map.get("UPLOAD_FILE_NAME")
							.toString();
					String xmlPath = uploadFilePath + MyFileUtil.FILE_DIR
							+ uploadFileName;

					// 推送IMOS服务器炸单
					RocoImos rocoImos = new RocoImos();
					if (!rocoImos.sendImosFile(statement,transaction,orderId,uploadFileName.split(".xml")[0], xmlPath, imosPath)) {
						sb.append("推送炸单失败！");
						msg = new Message("false", sb.toString());
					} else {
						// 插入一条记录，记录单子丢进哪个炸单服务器
						if (jdbcTemplate.queryForList(
								"select * from imos_load_balance ilb where ilb.order_code='"
										+ orderId + "'").size() == 0) {
							statement2.executeUpdate("insert into imos_load_balance(create_time,order_code,imos_path,status) values(sysdate,'"
											+ orderId
											+ "','"
											+ imosPathVal
											+ "',0)");
						} else {
							statement2.executeUpdate("update imos_load_balance ilb set ilb.status=0 where ilb.order_code='"
											+ orderId + "'");
						}
						statement2.executeUpdate("delete imos_idbext  where  orderid='"
										+ orderId + "'");
						statement2.executeUpdate("delete imos_idbwg  where  orderid='"
										+ orderId + "'");
					}
				}

				if (!StringUtils.isEmpty(sb.toString())) {
					msg = new Message("false", sb.toString());
				} else {
					//成功才会执行这条命令
					//提交事务
					transaction.exec();
					statement.executeBatch();
					conn.commit();
					
					msg = new Message("true");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-gpdrawing2020Validate-500", "获取数据失败！");
		} finally{
			//关闭所有会话和事务
			try{
				//数据连接关闭
				if(conn!=null){
					conn.close();
				}
				//关闭事务
				if(statement!=null){
					statement.close();
				}
				//将redis连接返回到jedis连接池中
//				if(jedis!=null){
//					jedisPool.returnResource(jedis);
//				}
				//关闭redis事务
				if(transaction!=null){
					transaction.close();
					//jedis.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * gp_drawing_imos IMOS 环节提交炸单
	 */
	@RequestMapping(value = { "/gpdrawingImosValidate" }, method = RequestMethod.GET)
	@ResponseBody
	public Message gpdrawingImosValidate(String saleItemId,
			String materialHeadId) {
		List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList("select t.ORDER_CODE_POSEX from sale_item t where t.id='"
						+ saleItemId + "' ");
		if (queryForList != null && queryForList.size() > 0) {
			Map<String, Object> map = queryForList.get(0);
			String orderId = (String) map.get("ORDER_CODE_POSEX");
			jdbcTemplate.update("delete imos_idbext  where  orderid='"
					+ orderId + "'");
			jdbcTemplate.update("delete imos_idbwg  where  orderid='" + orderId
					+ "'");
		}
		return null;
	}

	/**
	 * gp_drawing 重新审绘--重走子流程
	 */
	@RequestMapping(value = { "/gpdrawingMaterialValidate" }, method = RequestMethod.GET)
	@ResponseBody
	public Message gpdrawingMaterialValidate(String saleItemId,
			String materialHeadId) {
		Message msg = null;
		Connection conn=null;
		PreparedStatement statement=null;
		Transaction transaction=null;
		Jedis jedis=null;
		try {
			//初始化
			conn=jdbcTemplate.getDataSource().getConnection();
			statement=conn.prepareStatement(INSERT_XML_LIST);
			PreparedStatement statement2=conn.prepareStatement("select 1 from dual");
			jedis=jedisPool.getResource();
			//开启redis事务
			transaction=jedis.multi();
			
			StringBuffer sb = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select t.order_code_posex,t.material_head_id,t2.draw_type,t2.imos_path,t2.file_type,t2.imos_path from SALE_ITEM t ");
			sql.append(" inner join material_head t2 on t.material_head_id = t2.id ");
			sql.append(" where t.is_standard='0'  and t.id ='");
			sql.append(saleItemId);
			sql.append("' and t.material_head_id = '");
			sql.append(materialHeadId);
			sql.append("' ");

			String orderId = "";
			String xmlPath = "";

			List<Map<String, Object>> query = jdbcTemplate.query(
					sql.toString(), new MapRowMapper(true));
			if (query != null && query.size() > 0) {
				Map<String, Object> map = query.get(0);

				sb = new StringBuffer();

				List<MaterialFile> fileLists = null;
				fileLists = materialManager.queryMaterialFile(materialHeadId,
						"PDF");
				if (fileLists == null || fileLists.size() == 0) {
					sb.append("请上传pdf文件！");
				}
				if ("1".equals("" + map.get("fileType"))) {
					fileLists = materialManager.queryMaterialFile(
							materialHeadId, "KIT");
					if (fileLists == null || fileLists.size() == 0) {
						sb.append("请上传kit文件！");
					}
				}
				// 验证通过
				if (!StringUtils.isEmpty(sb.toString())) {
					msg = new Message("false", sb.toString());
					return msg;
				}

				// 3：标准2020,4：A单 xml推送
				// 1:2020绘图，2：imos绘图，重走子流程
				// 对应数据字典
				// fileType 1:2020附件 2：pdf附件
				// drawType 1:2020绘图，2：imos绘图，3：标准2020,4：A单
				if (("5".equals(map.get("drawType")))
						|| ("1".equals(map.get("fileType")) && ("4".equals(map
								.get("drawType")) || "3".equals(map
								.get("drawType"))))) {
					// t.status is null为有效文件
					List<Map<String, Object>> queryForList = jdbcTemplate
							.queryForList(" select t.id,t.upload_file_name,t.upload_file_path from material_file t where t.file_type='XML' and t.status is null and t.pid='"
									+ map.get("materialHeadId")
									+ "' order by create_time desc");
					if (queryForList == null || queryForList.size() == 0) {
						sb.append("请上传xml文件！");
					} else {
						// sb.append("");
						orderId = map.get("orderCodePosex").toString();

						Map<String, Object> fileObj = queryForList.get(0);
						// MaterialFile obj =
						// materialManager.getOne(saleItem.getMaterialHeadId(),
						// MaterialFile.class);
						String uploadFilePath = fileObj.get("UPLOAD_FILE_PATH")
								.toString();
						String uploadFileName = fileObj.get("UPLOAD_FILE_NAME")
								.toString();
						xmlPath = uploadFilePath + MyFileUtil.FILE_DIR
								+ uploadFileName;

						// 获取IMOS路径
						String imosPath = map.get("imosPath").toString();
						SysDataDict sysDataDict = sysDataDictDao
								.findByKeyVal(imosPath);
						// SysDataDict sysDataDict=
						// dataDictDaoImpl.getSysDataDict("IMOS_PATH",
						// imosPath);

						// 推送IMOS服务器炸单
						RocoImos rocoImos = new RocoImos();
						if (!rocoImos.sendImosFile(statement,transaction,orderId, uploadFileName.split(".xml")[0],xmlPath,
								sysDataDict.getKeyVal())) {
							sb.append("推送炸单失败！");
						} else {
							// 插入一条记录，记录单子丢进哪个炸单服务器
							if (jdbcTemplate.queryForList(
									"select * from imos_load_balance ilb where ilb.order_code='"
											+ orderId + "'").size() == 0) {
								statement2.executeUpdate("insert into imos_load_balance(create_time,order_code,imos_path,status) values(sysdate,'"
												+ orderId
												+ "','"
												+ imosPath
												+ "',0)");
							} else {
								statement2.executeUpdate("update imos_load_balance ilb set ilb.status=0 where ilb.order_code='"
												+ orderId + "'");
							}
							statement2.executeUpdate("delete imos_idbext  where  orderid='"
											+ orderId + "'");
							statement2.executeUpdate("delete imos_idbwg  where  orderid='"
											+ orderId + "'");
						}
					}
				}

				if (!StringUtils.isEmpty(sb.toString())) {
					msg = new Message("false", sb.toString());
				} else {
					//成功才会执行这条命令
					//提交事务
					transaction.exec();
					statement.executeBatch();
					conn.commit();
					
					msg = new Message("true");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-gpdrawingMaterialValidate-500", "获取数据失败！");
		} finally{
			//关闭所有会话和事务
			try{
				//数据连接关闭
				if(conn!=null){
					conn.close();
				}
				//关闭事务
				if(statement!=null){
					statement.close();
				}
				//将redis连接返回到jedis连接池中
//				if(jedis!=null){
//					jedisPool.returnResource(jedis);
//				}
				//关闭redis事务
				if(transaction!=null){
					transaction.close();
					jedis.close();
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return msg;
	}

	/**
	 * gp_store 子流程-客户起草
	 */
	@RequestMapping(value = { "/gpstoreValidate" }, method = RequestMethod.GET)
	@ResponseBody
	public Message gpstoreValidate(String saleItemId, String materialHeadId) {
		Message msg = null;
		try {
			StringBuffer sb = null;
			StringBuffer sql = new StringBuffer();
			sql.append("select t.order_code_posex,t.material_head_id,t2.draw_type,t2.imos_path,t2.file_type,t2.imos_path from SALE_ITEM t ");
			sql.append(" inner join material_head t2 on t.material_head_id = t2.id ");
			sql.append(" where t.is_standard='0'  and t.id ='");
			sql.append(saleItemId);
			sql.append("' and t.material_head_id = '");
			sql.append(materialHeadId);
			sql.append("' ");

			List<Map<String, Object>> query = jdbcTemplate.query(
					sql.toString(), new MapRowMapper(true));
			if (query != null && query.size() > 0) {
				Map<String, Object> map = query.get(0);

				sb = new StringBuffer();

				List<MaterialFile> fileLists = null;
				fileLists = materialManager.queryMaterialFile(materialHeadId,
						"PDF");

				// 附件类型2020，只上传kit文件(订单审绘时再上传一个pdf文件)
				// 附件类型pdf，只上传pdf文件
				if ("1".equals("" + map.get("fileType"))) {
					fileLists = materialManager.queryMaterialFile(
							materialHeadId, "KIT");
					if (fileLists == null || fileLists.size() == 0) {
						sb.append("请上传kit文件！");
					}
				} else if ("2".equals("" + map.get("fileType"))) {
					if (fileLists == null || fileLists.size() == 0) {
						sb.append("请上传pdf文件！");
					}
				}

				if (!StringUtils.isEmpty(sb.toString())) {
					msg = new Message("false", sb.toString());
				} else {
					msg = new Message("true");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-gpstoreValidate-500", "获取数据失败！");
		}
		return msg;
	}

	/**
	 * 通过物料编码查询实体
	 */
	@RequestMapping(value = "/queryMaterialHeadByMatnr", method = RequestMethod.GET)
	@ResponseBody
	public Message queryMaterialHeadByMatnr(String matnr,String kunnr) {
		Message msg = null;
		String custVkorg= "";
		String custVtweg= "";
		try {
			if(kunnr!=null&&!"".equals(kunnr)) {
			 CustHeader bgCustHeader = custHeaderDao.finByKunnr(kunnr);
			 	// 销售组织
				custVkorg = bgCustHeader.getVkorg();
				// 分销渠道
				custVtweg = bgCustHeader.getVtweg();
			}else {
				SysUser loginUser = super.getLoginUser();
				// 客户
				CustHeader custHeader = loginUser.getCustHeader();
				// 销售组织
				custVkorg = custHeader.getVkorg();
				// 分销渠道
				custVtweg = custHeader.getVtweg();
			}

			JSONObject obj = new JSONObject();
			obj.put("matnr", matnr);

			StringBuffer sb = new StringBuffer();
			sb.append("select * from MATERIAL_head t where matnr = ? and t.vkorg = ? and t.vtweg = ? ");
			// sql params
			List<Object> params = new ArrayList<Object>();
			params.add(matnr);
			params.add(custVkorg);
			params.add(custVtweg);

			List<Map<String, Object>> lists = jdbcTemplate.queryForList(
					sb.toString(), params.toArray());

			if (lists != null && lists.size() > 0) {
				Map<String, Object> map = lists.get(0);
				obj.put("maktx", map.get("MAKTX"));
				obj.put("materialHeadId", map.get("ID"));
				msg = new Message(JSONObject.fromObject(obj));
			} else {
				msg = new Message("false", "物料编码" + matnr + "不存在!");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-queryMaterialHeadByMatnr-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 通过物料编码查询实体
	 */
	@RequestMapping(value = "/queryMaterialHeadByMatnr2", method = RequestMethod.GET)
	@ResponseBody
	public Message queryMaterialHeadByMatnr2(String matnr) {
		Message msg = null;
		try {
			JSONObject obj = new JSONObject();
			obj.put("matnr", matnr);

			StringBuffer sb = new StringBuffer();
			sb.append("select * from MATERIAL_head t where matnr = ? ");
			// sql params
			List<Object> params = new ArrayList<Object>();
			params.add(matnr);

			List<Map<String, Object>> lists = jdbcTemplate.queryForList(
					sb.toString(), params.toArray());

			if (lists != null && lists.size() > 0) {
				Map<String, Object> map = lists.get(0);
				obj.put("maktx", map.get("MAKTX"));
				msg = new Message(JSONObject.fromObject(obj));
			} else {
				obj.put("maktx", "补件通用码");
				msg = new Message(JSONObject.fromObject(obj));
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-queryMaterialHeadByMatnr-500", "数据加载失败!");
		}
		return msg;
	}

	/**
	 * 审核状态Imos绘图--删除物料数据
	 */
	@RequestMapping(value = "/deleteImos", method = RequestMethod.GET)
	@ResponseBody
	public Message deleteImos(String orderId) {
		Message msg = null;
		try {
			jdbcTemplate.update("delete imos_idbext  where  orderid='"
					+ orderId + "'");
			jdbcTemplate.update("delete imos_idbwg  where  orderid='" + orderId
					+ "'");
			msg = new Message("ok");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-deleteImos-500", "删除失败!");
		}
		return msg;
	}

	/**
	 * 物料审核验证
	 * 
	 * @param saleItemCode
	 *            订单行号
	 * @return
	 */
	@RequestMapping(value = { "/mmApproveCheck" }, method = RequestMethod.GET)
	@ResponseBody
	public Message approveCheck(final String saleItemCode) {
		Message message = null;
		try {
			// if("LJ752031602830010".equals(saleItemCode)){
			// Map<String ,Object> map=new HashMap<String, Object>();
			// map.put("FLAG", "Y");
			// map.put("MSG", null);
			// map.put("SALE_ITEM_CODE", "LJ752031602830010");
			// message=new Message(map);
			// }else{
			final String callProcedureSql = "{call P_IMOS_IDBEXT_M_CHECK(?, ?, ?)}";
			List<SqlParameter> params = new ArrayList<SqlParameter>();
			params.add(new SqlInOutParameter("SALE_ITEM_CODE", Types.VARCHAR));
			params.add(new SqlOutParameter("FLAG", Types.VARCHAR));
			params.add(new SqlOutParameter("MSG", Types.VARCHAR));
			Map<String, Object> map = jdbcTemplate.call(
					new CallableStatementCreator() {
						@Override
						public CallableStatement createCallableStatement(
								Connection conn) throws SQLException {
							CallableStatement cstmt = conn
									.prepareCall(callProcedureSql);
							cstmt.registerOutParameter(1, Types.VARCHAR);
							cstmt.registerOutParameter(2, Types.VARCHAR);
							cstmt.registerOutParameter(3, Types.VARCHAR);
							cstmt.setString(1, saleItemCode);
							return cstmt;
						}
					}, params);
			// System.out.println(map.toString());
			/*
			 * Map<String, String> rMap=jdbcTemplate.execute(new
			 * CallableStatementCreator() {
			 * 
			 * @Override public CallableStatement createCallableStatement(
			 * Connection con) throws SQLException { String storedProc =
			 * "{call P_IMOS_IDBEXT_M_CHECK(#SALE_ITEM_CODE#,?,?)}";// 调用的sql
			 * CallableStatement cs = con.prepareCall(storedProc);
			 * cs.setNString("SALE_ITEM_CODE", saleItemCode);// 设置输入参数的值
			 * cs.registerOutParameter("FLAG",Types.VARCHAR);// 注册输出参数的类型
			 * cs.registerOutParameter("MSG",Types.VARCHAR);// 注册输出参数的类型 return
			 * cs; } }, new CallableStatementCallback<Map<String, String>>() {
			 * public Map<String, String>
			 * doInCallableStatement(CallableStatement cs) throws SQLException,
			 * DataAccessException { cs.execute(); Map<String, String> map=new
			 * HashMap<String, String>(); map.put("SALE_ITEM_CODE",
			 * cs.getNString("SALE_ITEM_CODE"));
			 * map.put("FLAG",cs.getNString("FLAG"));
			 * map.put("MSG",cs.getNString("MSG")); return map;// 获取输出参数的值 } });
			 */
			message = new Message(map);
			// }
		} catch (Exception e) {
			message = new Message("MM-APPROVE-CHECK-ER",
					e.getLocalizedMessage());
			e.printStackTrace();
		}
		return message;
	}
	

	/**订单审绘提交流程时，判断行项目 里的KIT文件是否有重复
	 * 
	 * @param orderCodePosex
	 *        订单号
	 * @return
	 */
	@RequestMapping(value = { "/MyKitCheck" }, method = RequestMethod.GET)
	@ResponseBody
	public Message MyKitCheck(String saleId ) {
		Message message = null;
		String sql = "";
		String tempsql = "";
		List<Map<String,Object>> li = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> elementLt = new ArrayList<Map<String,Object>>();
		List<Object> KitLi = new ArrayList<Object>();
		try { 
			     //查询订单里有相同名字的KIT文件数量大于等于2的KIT名字
				 sql = 
					 "select mf.upload_file_name_old  as upload_file_name_old ,count(*) as num" +
					 "  from material_file mf" + 
					 " where mf.pid IN" + 
					 "       (select si.material_head_id" + 
					 "          from sale_item si " + 
					 "         where si.pid = '"+saleId +
					 "' and si.state_audit <> 'QX' and si.is_standard = '0')" + 
					 "   and mf.file_type in('KIT','PDF') and mf.status is null " + 
					 "   group by mf.upload_file_name_old  having count(*) >1 ";

				//System.out.println("MyKitCheckSql="+sql);
				li = jdbcTemplate.queryForList(sql);
				//Map<String,String> mp = new HashMap<String,String>();
				
				for(Map<String,Object> set : li){
					String file_name = set.get("upload_file_name_old").toString();
					//查询KIT文件名重复且数量大于等于2的销售订单行项目
					 tempsql =
						 "select si.order_code_posex" +
						 "  from sale_item si" + 
						 " where si.material_head_id in" + 
						 "       (select mf.pid" + 
						 "          from material_file mf" + 
						 "         where mf.upload_file_name_old = '"+file_name+"')" + 
						 "   and si.pid = '"+saleId+"'";
					 elementLt = jdbcTemplate.queryForList(tempsql);
					 Set<String> Kitset = new HashSet<String>();
					 for(Map<String,Object> el : elementLt){
						  String order_code_posex = el.get("order_code_posex").toString();
						  Kitset.add(order_code_posex);
					 }
					 //将每组有重复KIT的订单行加入到数组KitLi
					 KitLi.add(Kitset);
				}
				
				if(KitLi != null){
					message = new Message(KitLi,"false");
				}
		} catch (Exception e) {
			message = new Message("MM-APPROVE-CHECK-ER",
					e.getLocalizedMessage());
			e.printStackTrace();
		}
		return message;
	}
	
	/**
	 * 上传PDF文件时判断有没上传重复
	 * @param ordercode
	 * @return
	 */
	@RequestMapping(value = { "/judgeHasDoublePdf" }, method = RequestMethod.GET)
	@ResponseBody
	public Message judgeHasDoublePdf(String ordercode,String pdfname) {
		Message message = null;
		String sql = "";
		//String tempsql = "";
		List<Map<String,Object>> li = new ArrayList<Map<String,Object>>();
		//List<Map<String,Object>> elementLt = new ArrayList<Map<String,Object>>();
		//List<Object> KitLi = new ArrayList<Object>();
			 sql=  "select mf.upload_file_name_old,si.posex" +
				 "  from sale_item si, material_file mf, material_head mh" + 
				 " where si.pid = (select sh.id" + 
				 "                   From sale_header sh" + 
				 "                  where sh.order_code = '"+ordercode+"')" + 
				 "   and si.material_head_id = mh.id" + 
				 "   and mh.id = mf.pid" + 
				 "   and mf.file_type = 'PDF'" + 
				 "   and mf.status is null" + 
				 " and mf.upload_file_name_old = '"+pdfname+"'";
			 li = jdbcTemplate.queryForList(sql);
				//Map<String,String> mp = new HashMap<String,String>();
			 if(li.size()> 0 && li != null){
				 //Map<String,String> map = new HashMap<String,String>();
				 String file_name = "";
				 String posex = "";
					for(Map<String,Object> set : li){
						 file_name = set.get("upload_file_name_old").toString();
						 posex = set.get("posex").toString();
						 //map.put(file_name, posex);
					}
				
				 message = new Message("false","上传的"+file_name+"文件，与行项目"+posex+"的PDF文件重复，请检查！");	
				 //message.setSuccess(false);
				 return message;
			 }
			 
		message = new Message("success");
		return message;
	}

	@RequestMapping(value = { "/checkMaterial" }, method = RequestMethod.GET)
	@ResponseBody
	public Message CheckMaterial() {
		Message msg = null;
		String uuid = this.getRequest().getParameter("saleitemId");
		String sql = "select t.* from imos_idbext t where t.article_id in (select bl.article_id from mm_black_list bl where status=0 and create_user is not null)  and t.orderid=(select order_code_posex from sale_item where id='"
				+ uuid + "' and rownum=1)";
		String sql2 = "select t.* from imos_idbext t where t.render in (select bl.article_id from mm_black_list bl where status=0 and create_user is not null)  and t.orderid=(select order_code_posex from sale_item where id='"
				+ uuid + "' and rownum=1)";
		String sql3 = "select t.* from imos_idbext t where t.info1 in (select ibl.info1 from info1_black_list ibl where status=0 ) and t.orderid=(select order_code_posex from sale_item where id='"
				+ uuid + "' and rownum=1)";
		String sql4 = "update imos_idbext t set t.render = (select bl.target_id from mm_black_list bl where status = 0 and bl.create_user is null and bl.article_id=t.article_id) where t.render in (select bl.article_id from mm_black_list bl where status = 0 and bl.create_user is null) and t.orderid = (select order_code_posex from sale_item where id = '"
			+ uuid + "' and rownum = 1)";
		String sql5 = "update imos_idbext t set t.article_id = (select bl.target_id from mm_black_list bl where status = 0 and bl.create_user is null and bl.article_id=t.render) where t.article_id in (select bl.article_id from mm_black_list bl where status = 0 and bl.create_user is null) and t.orderid = (select order_code_posex from sale_item where id = '"
			+ uuid + "' and rownum = 1)";
		List<Map<String, Object>> queryList = jdbcTemplate.queryForList(sql);
		queryList.addAll(jdbcTemplate.queryForList(sql2));
		queryList.addAll(jdbcTemplate.queryForList(sql3));
		jdbcTemplate.update(sql4);
		jdbcTemplate.update(sql5);
		if (queryList.size() > 0) {
			msg = new Message("物料中，含有禁用的物料"
					+ (queryList.get(0).get("article_id") == null ? queryList
							.get(0).get("render") : queryList.get(0).get(
							"article_id")) + "，请核查！");
			msg.setSuccess(false);
			return msg;
		}
		msg = new Message("success");
		return msg;
	}

	@RequestMapping(value = { "/findPicLog" }, method = RequestMethod.GET)
	@ResponseBody
	public int findPicLog(String picid) {
		Message msg = null;
		int a = 0;
		try {
			String sql = "select (to_char(m.upload_file_path) || '/' || to_char(m.upload_file_name))  as filepath"
					+ "  from material_file m"
					+ " where m.pid = (select mm.id from material_head mm  where mm.matnr = '"
					+ picid + "')";

			List<Map<String, Object>> piccount = jdbcTemplate.queryForList(sql);
			if (piccount.size() > 0) {
				return a = 1;
			}
			// msg=new Message("图片显示成功","成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("faile!");
		}
		return a;
	}

	@RequestMapping(value = { "/queryMaterialPic" }, method = RequestMethod.GET)
	@ResponseBody
	public void queryMaterialPic(String picid, HttpServletRequest request,
			HttpServletResponse response) {
		Message msg = null;
		try {
			String sql = "select (to_char(m.upload_file_path) || '/' || to_char(m.upload_file_name))  as filepath"
					+ "  from material_file m"
					+ " where m.pid = '"
					+ picid
					+ "'";

			/*
			 * String sql =
			 * "select (to_char(m.upload_file_path) || '/' || to_char(m.upload_file_name))  as filepath"
			 * + "  from material_file m" +
			 * " where m.pid = 'Xh6KkaiYA5tw6Z194nNLRm'";
			 */

			String filepath = jdbcTemplate.queryForObject(sql, String.class);

			if (filepath != null || !(filepath.equals(""))) {

				// 读取本地图片输入流
				SmbFile smbFile = new SmbFile(filepath);
				smbFile.connect();
				// InputStream inputStream = new FileInputStream();
				int length = smbFile.getContentLength();// 得到文件的大小
				byte buffer[] = new byte[length];
				SmbFileInputStream in = new SmbFileInputStream(smbFile);
				// 建立smb文件输入流
				in.read(buffer);
				in.close();
				// 设置发送到客户端的响应内容类型
				response.setContentType("image/*");
				OutputStream out = response.getOutputStream();
				out.write(buffer);
				response.setContentLength(length);
				// 关闭响应输出流
				out.close();

			}
			// msg=new Message("图片显示成功","成功");
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("图片显示失败", "显示图片失败!");
		}
		return;
	}

	@RequestMapping(value = { "/checkHasCNC" }, method = RequestMethod.GET)
	@ResponseBody
	public Message checkHasCNC(String saleItemId, String temp) {
		Message msg = null;
		// 炸单后，判断订单行的项目是否有barcode,如果有，则is_cnc为1，否则is_cnc为0，（1为有，0为无）
		SaleItem saleItem = commonManager.getOne(saleItemId, SaleItem.class);
		if (!com.alibaba.druid.util.StringUtils.isEmpty(saleItem.getIsCnc())
				&& "0".equals(saleItem.getIsCnc())) {
			return new Message("success");
		}
		if (com.alibaba.druid.util.StringUtils.isEmpty(saleItem.getIsCnc())) {
			String sql = "select b.barcode from imos_idbext b where b.typ = '3' and (b.barcode <> ''or b.barcode is not null) and  b.orderid = (select i.order_code_posex from sale_item i where i.id  = '"
					+ saleItemId + "')";
			List<Map<String, Object>> barcode = jdbcTemplate.queryForList(sql);
			if (barcode.size() > 0) {
				saleItem.setIsCnc("1");
				commonManager.save(saleItem);
			} else {
				return new Message("success");
			}
		}
		//long startTime1 = System.currentTimeMillis();// 获取当前时间
		Boolean judge = judgeHasCNC(temp);

		if (!judge) {
			msg = new Message(temp + "在服务器不存在CNC文件，请核查！");
			//long endTime1 = System.currentTimeMillis();
			return msg;
		}
		//long endTime1 = System.currentTimeMillis();
		msg = new Message("success");
		return msg;
	}

	/**
	 * 判断文件服务器是否存在CNC文件
	 * 
	 * @param temp
	 *            order_code_posex
	 * @return
	 */
	private Boolean judgeHasCNC(String temp) {
		InputStream in = null;
		//String[] tempFile = { "BHX050/", "FT2/", "PTP160/", "skipper100/" };
		String[] tempFile = { "PTPNEW160/"};
		try {
			// 创建远程文件对象
			String path;
			for (int i = 0; i < tempFile.length; i++) {
				path = tempFile[i];
				//第一种路径
				String remotePhotoUr2 = "smb://administrator:123qweasdzxc.@172.16.3.254/mpr/"
						+ path + temp;
				SmbFile remoteFile = new SmbFile(remotePhotoUr2);
				if (remoteFile.exists()
						&& remoteFile.isDirectory()
						&& (new SmbFile(remotePhotoUr2 + "/").listFiles().length > 0)) {
					return true;
				}
				
				//第二种路径
				String remotePhotoUr3= remotePhotoUr2+"_BAK";
				SmbFile remoteFile3 = new SmbFile(remotePhotoUr3);
				if (remoteFile3.exists()
						&& remoteFile3.isDirectory()
						&& (new SmbFile(remotePhotoUr3 + "/").listFiles().length > 0)) {
					return true;
				}
			}
		} catch (Exception e) {
			String msg = "下载远程文件出错：" + e.getLocalizedMessage();
			//System.out.println(msg);
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception e) {
			}
		}
		return false;
	}

	@RequestMapping(value = { "/getMMStock" }, method = RequestMethod.GET)
	@ResponseBody
	public Message getMMStock(String mmId) {
		Message msg = null;
		try {
			// 40300000014
			JCoDestination connect = SAPConnect.getConnect();
			JCoFunction function = connect.getRepository().getFunction(
					"ZRFC_MM_WL01");
			function.getImportParameterList().setValue("S_MATNR", mmId);
			function.execute(connect);
			// JCoParameterList list=function.getExportParameterList();
			Map<String, Object> map = new HashMap<String, Object>();
			com.sap.conn.jco.JCoParameterList list = function
					.getTableParameterList();
			com.sap.conn.jco.JCoTable table = list.getTable("EX_T_WL01");
			if (table.getNumRows() > 0) {
				table.firstRow();
				Object Matnr = table.getValue("MATNR");
				BigDecimal Stock = (BigDecimal) table.getValue("LABST");
				int value = Stock.intValue();
				map.put("matnr", Matnr);
				map.put("stock", value);
			} else {
				map.put("matnr", mmId);
				map.put("stock", 0);
			}
			msg = new Message(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;

	}

	/**
	 * 用于IMOS炸单服务器负载均衡分配使用
	 * 
	 * @param isSpecial
	 *            是否特殊型录
	 * @return
	 */
	public String ImosLoadBalance(boolean isSpecial) {
//		List<Map<String, Object>> normalList = jdbcTemplate
//				.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=0 order by  NVL(t2.amount,0)");
//		List<Map<String, Object>> specialList = jdbcTemplate
//				.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=1 order by  NVL(t2.amount,0)");
//		if (isSpecial) {
//			return specialList.get(0).get("IMOS_PATH").toString();
//		} else {
//			return normalList.get(0).get("IMOS_PATH").toString();
//		}
		List<Map<String,Object>> list=jdbcTemplate.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=2 order by  NVL(t2.amount,0)");
		return list.get(0).get("IMOS_PATH").toString();
	}

	@RequestMapping(value = { "/getBarCodeMsg" }, method = RequestMethod.POST)
	@ResponseBody
	public Message getBarCodeMsg(String[] ids) {
		Message msg = null;
		try {
			String saleHeaderIdS = StringUtil.arrayToString(ids);
			List<String> orderids = new ArrayList<String>();
			List<String> barcodes=new ArrayList<String>();
			List<Map<String, Object>> orderidList = jdbcTemplate
					.queryForList("select bj.barcode from material_bujian bj where bj.id in (select bujian_id from sale_item si where si.pid in  ("
							+ saleHeaderIdS
							+ ") ) and bj.barcode is not null ");
			if (orderidList != null && orderidList.size() > 0) {
				for (Map<String, Object> map : orderidList) {
					String[] barCodeArray=map.get("BARCODE").toString().split(" ");
					for(int index=0;index<barCodeArray.length;index++){
						if(barCodeArray!=null &&!com.alibaba.druid.util.StringUtils.isEmpty(barCodeArray[index].trim())){
							orderids.add(barCodeArray[index].substring(0, barCodeArray[index].length()-4));
							barcodes.add(barCodeArray[index]);
						}
					}
				}
				List<Map<String, Object>> resultList = jdbcTemplate
						.queryForList("select distinct barcode,name,(trim(to_char(CLENGTH,'9999.999')) || ' X'||trim(to_char(CWIDTH,'9999.999')) ||' X' ||trim(to_char(CTHICKNESS,'9999.999'))) as sizz,II.MATNAME,ii.cnc_barcode1,ii.cnc_barcode2, (select si.maktx from sale_item si where si.order_code_posex=ii.orderid) as maktx ,((select ch.name1 from cust_header ch where ch.kunnr=(select sh.shou_da_fang from sale_header sh where sh.id=(select si.pid from sale_item si where si.order_code_posex=ii.orderid)) ) ||substr((select sh.order_code from sale_header sh where sh.id=(select si.pid from sale_item si where si.order_code_posex=ii.orderid)),-4,4)) as order_code from imos_idbext ii where orderid in ("
								+ StringUtil.arrayToString(orderids.toArray(new String[]{}))
								+ ") and barcode in ("+StringUtil.arrayToString(barcodes.toArray(new String[]{}))+") ");
				msg = new Message(resultList);
			} else {
				msg = new Message("Error", "获取数据失败");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("Error", "获取数据失败");
		}
		return msg;

	}
	 
	@RequestMapping(value={"/getBarCodeList"},method=RequestMethod.GET)
	@ResponseBody
	public JdbcExtGridBean getBarCodeList(int page,int limit){
		String linkOrderCode=this.getRequest().getParameter("linkOrderCode");
		String orderCode=this.getRequest().getParameter("orderCode");
		String createTimeFrom=this.getRequest().getParameter("createTimeFrom");
		String createTimeTo=this.getRequest().getParameter("createTimeTo");
		
		int minIndex=(page-1)*limit+1;
		int maxIndex=page*limit;
		String sql="select sh.id ,sh.order_code,sh.sap_order_code,sh.order_date,(select ch.name1 from cust_header ch where ch.kunnr=sh.shou_da_fang and rownum=1) as name1,sh.shou_da_fang,sh.p_order_code,rownum as rn from sale_header sh where sh.id in ( select acm.id from act_ct_mapping acm where acm.procinstid in ( select aha.proc_inst_id_ from act_hi_actinst aha where instr(aha.proc_def_id_,'my')=1 and aha.act_name_='结束' ) )  ";
		List<Object> params=new ArrayList<Object>();
		StringBuilder filters=new StringBuilder();
		if(!com.alibaba.druid.util.StringUtils.isEmpty(linkOrderCode)){
			filters.append(" and p_order_code like  ? ");
			params.add(StringHelper.like(linkOrderCode));
		}
		if(!com.alibaba.druid.util.StringUtils.isEmpty(orderCode)){
			filters.append(" and order_code like ? ");
			params.add(StringHelper.like(orderCode));
		}
		if(!com.alibaba.druid.util.StringUtils.isEmpty(createTimeFrom)){
			filters.append(" and order_date >= ?");
			params.add(DateTools.strToDate(createTimeFrom, DateTools.defaultFormat));
		}
		if(!com.alibaba.druid.util.StringUtils.isEmpty(createTimeTo)){
			filters.append(" and order_date <= ?");
			params.add(DateTools.strToDate(createTimeTo, DateTools.defaultFormat));
		}
		int totalElements=jdbcTemplate.queryForObject("select count(1) as s from ("+sql+filters.toString()+")",params.toArray(), Integer.class);
		int totalPages=totalElements/limit;
		String limits=" where rn>= ? and rn < =?";
		params.add(minIndex);
		params.add(maxIndex);
		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
		//formats.put("orderCode", new SimpleDateFormat(DateTools.defaultFormat));
		List<Map<String,Object>> queryList=jdbcTemplate.queryForList("select id,order_code,sap_order_code,p_order_code,name1 as name,to_char(order_date,'yyyy-mm-dd') as order_date,shou_da_fang from ("+sql+filters.toString()+")"+limits,params.toArray());
		return new JdbcExtGridBean(totalPages, totalElements, limit, queryList);
	}
	@RequestMapping(value = { "/barcode" }, method = RequestMethod.GET)
	public ModelAndView BarCode(ModelAndView mav) {
		mav.setViewName("core/index");
		mav.addObject("module", "BarCodePrintApp");
		return mav;
	}
	

	/**
	 * 
	 * @Description 查询补单总数与退回数的订单号
	 * @author zzl
	 * @version v1.0
	 * @date 2019年4月17日
	 */
	@RequestMapping(value="/findCustomerServiceCapacity",method=RequestMethod.POST)
	@ResponseBody
	public Message findCustomerServiceCapacity() {
		Message msg=null;
		String cccount = null;
		String zcount = null;
		String ytime=this.getRequest().getParameter("ytime");
		String ytimes = ytime.substring(0,10);
		String assignee=this.getRequest().getParameter("assignee");
		StringBuffer tecStr = new StringBuffer();
		StringBuffer zStr = new StringBuffer();
		Map<String,Object> msgMap=new HashMap<String,Object>();
		String ccountSql ="select distinct ah.act_name_,ao.mapping_sid,ah.assignee_,ah.start_time_" + 
				"  from (select aco.procinstid,aco.mapping_sid" + 
				"  from act_ct_ord_err aco" + 
				"  where aco.taskname = '订单审绘'" + 
				"  and aco.target_task_name = '客服审核')ao" + 
				"  left join act_ord_curr_node11 ah " + 
				"  on ah.proc_inst_id_  = ao.procinstid" + 
				"  left join act_ct_mapping ac " + 
				"  on ac.procinstid = ah.proc_inst_id_" + 
				"  left join sale_header sh" + 
				"  on sh.id = ac.id" + 
				"  where ah.act_name_ = '客服审核'" + 
				"  and nvl(sh.order_status,'A')<>'QX'" + 
				"  and ah.assignee_=(select su.id from sys_user su where su.user_name='"+assignee+"' and su.id like 'kf%')" + 
				"  and to_char(ah.start_time_,'yyyy-mm-dd') ='"+ytimes+"'";
		
		String zcountSql = "select distinct sh.order_code,aoc.act_name_,aoc.assignee_,aoc.start_time_" + 
				" from act_ord_curr_node11 aoc" + 
				" left join act_ct_mapping ac" + 
				" on ac.procinstid = aoc.proc_inst_id_" + 
				" left join sale_header sh " + 
				" on sh.id = ac.id" + 
				" where aoc.act_name_ = '客服审核'" + 
				" and nvl(sh.order_status,'A')<>'QX'" + 
				" and aoc.assignee_=(select su.id from sys_user su where su.user_name='"+assignee+"' and su.id like 'kf%')" + 
				" and to_char(aoc.start_time_,'yyyy-mm-dd')='"+ytimes+"' order by sh.order_code";
		
		List<Map<String, Object>> ccountList = jdbcTemplate.queryForList(ccountSql);
		List<Map<String, Object>> zcountList = jdbcTemplate.queryForList(zcountSql);
		if(ccountList.size()>0) {
			for (Map<String, Object> map : ccountList) {
				tecStr.append(map.get("MAPPING_SID")+",");
			}
		}
		if(zcountList.size()>0) {
			for (Map<String, Object> map : zcountList) {
				zStr.append(map.get("ORDER_CODE")+",");
			}
		}
		
		if(tecStr.length()>0) {
			cccount = tecStr.substring(0,tecStr.length()-1);
		}
		if(zStr.length()>0) {
			zcount = zStr.substring(0,zStr.length()-1);
		}
		
		msgMap.put("cccount", cccount);
		msgMap.put("zcount", zcount);
		msg=new Message(msgMap);
		return msg;
	}
	/**
	 * 查询单条客服投诉汇总记录
	 * @param orderCode 根据订单编号查询
	 * @return 返回客服投诉汇总信息Json
	 */
	@RequestMapping(value="/findNewOrderToCust",method=RequestMethod.GET)
	@ResponseBody
	public Message findNewOrderToCust(String saleId) {
		Message msg=null;
		String sql="select  sh.order_code,mc.order_code_posex,mc.zzelb,mc.zzccz,mc.salefor,mc.color,mc.zzcclx,mc.zzezx,mc.zzebm,mc.cabinet_name,mc.zztsnr,mc.cpmc,mc.duty,tc.tousucishu,tc.problem,mc.id from sale_header sh left join material_complainid mc on mc.pid = sh.id left join terminal_client tc on tc.sale_id=sh.id where mc.id='"+saleId+"'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size()>0){
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("id", list.get(0).get("ID"));
			map.put("orderCode",list.get(0).get("ORDER_CODE"));
			map.put("orderCodePosex",list.get(0).get("ORDER_CODE_POSEX"));
			map.put("zzelb",list.get(0).get("ZZELB"));
//			map.put("zzccz",list.get(0).get("ZZCCZ"));
			map.put("zztsnr",list.get(0).get("ZZTSNR"));
			map.put("zzezx",list.get(0).get("ZZEZX"));
			map.put("tousucishu",list.get(0).get("TOUSUCISHU"));
			String zzebm = (String)list.get(0).get("ZZEBM");
			String zzccz = (String)list.get(0).get("ZZCCZ");
			String wtlx = (String)list.get(0).get("ZZCCLX");
			if(!StringUtils.isEmpty(zzccz)) {
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(zzccz);
				if(isNum.matches()) {
					map.put("zzccz",list.get(0).get("ZZCCZ"));
				}else {
					String zzbmSql = "select sd.key_val as keyval from sys_data_dict sd where sd.trie_id = (select st.id from sys_trie_tree st where st.key_val='CCZBN') and sd.desc_zh_cn='"+zzccz+"'";
					List<Map<String, Object>> zzbmList = jdbcTemplate.queryForList(zzbmSql);
					if(zzbmList.size()>0){
						map.put("zzccz",zzbmList.get(0).get("KEYVAL"));
					}
				}
			}
			if(!StringUtils.isEmpty(zzebm)) {
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(zzebm);
				if(isNum.matches()) {
					map.put("zzebm",list.get(0).get("ZZEBM"));
				}else {
					String zzbmSql = "select sd.key_val as keyval from sys_data_dict sd where sd.trie_id = (select st.id from sys_trie_tree st where st.key_val='CCBM') and sd.desc_zh_cn='"+zzebm+"'";
					List<Map<String, Object>> zzbmList = jdbcTemplate.queryForList(zzbmSql);
					if(zzbmList.size()>0){
						map.put("zzebm",zzbmList.get(0).get("KEYVAL"));
					}
				}
			}
			if(!StringUtils.isEmpty(wtlx)) {
				Pattern pattern = Pattern.compile("[0-9]*");
				Matcher isNum = pattern.matcher(wtlx);
				if(isNum.matches()) {
					map.put("zzcclx",list.get(0).get("ZZCCLX"));
				}else {
					String zzbmSql = "select sd.key_val as keyval from sys_data_dict sd where sd.trie_id = (select st.id from sys_trie_tree st where st.key_val='WTLX') and sd.desc_zh_cn='"+wtlx+"'";
					List<Map<String, Object>> zzbmList = jdbcTemplate.queryForList(zzbmSql);
					if(zzbmList.size()>0){
						map.put("zzcclx",zzbmList.get(0).get("KEYVAL"));
					}
				}
			}
			map.put("cpmc",list.get(0).get("CPMC"));
			map.put("duty",list.get(0).get("DUTY"));
			map.put("salefor",list.get(0).get("SALEFOR"));
			map.put("cabinetName",list.get(0).get("CABINET_NAME"));
			map.put("color",list.get(0).get("COLOR"));
			map.put("duty",list.get(0).get("DUTY"));
			map.put("problem",list.get(0).get("PROBLEM"));
			msg=new Message(map);
		}else{
			msg=new Message("ToCust-0010","获取数据失败");
		}
		return msg;
	}
	
	/**
	 * 查询单条客服投诉汇总记录
	 * @param orderCode 根据订单编号查询
	 * @return 返回客服投诉汇总信息Json
	 */
	@RequestMapping(value="/findOrderToCust",method=RequestMethod.GET)
	@ResponseBody
	public Message findOrderToCust(String orderCode){
		Message msg=null;
		String sql="select * from ("
				+ "select t.id,t.zztsnr,t.zzezx,t.zzebm,t.zzccz,t.zzccwt,t.duty,sh.order_code "
				+ "from material_bujian t "
				+ "left join sale_item si on t.id = si.bujian_id  "
				+ "left join sale_header sh on si.pid = sh.id) da_ "
				+ "where da_.order_code='"+orderCode+"'";
		List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
		if(list.size()>0){
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("id", list.get(0).get("ID"));
			map.put("orderCode",list.get(0).get("ORDER_CODE"));
			map.put("zztsnr",list.get(0).get("ZZTSNR"));
			map.put("zzezx",list.get(0).get("ZZEZX"));
			map.put("zzebm",list.get(0).get("ZZEBM"));
			map.put("zzccz",list.get(0).get("ZZCCZ"));
			map.put("zzccwt",list.get(0).get("ZZCCWT"));
			map.put("duty",list.get(0).get("DUTY"));
			msg=new Message(map);
		}else{
			msg=new Message("ToCust-0010","获取数据失败");
		}
		return msg;
	}
	/**
	 * 校验用户选择文件时必须要有一个有效文件
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="/checkFileStauts",method={RequestMethod.GET,RequestMethod.POST})
	@ResponseBody
	public Message checkFileStauts(String[] ids){
		Message msg=null;
		StringBuffer sbf =new StringBuffer();
		boolean flg=true;
		if(ids.length>0){
			for (int i = 0; i < ids.length; i++) {
				List<Map<String, Object>> list = jdbcTemplate
				.queryForList("SELECT MH.SERIAL_NUMBER, COUNT(MF.STATUS)AS STATUS FROM MATERIAL_HEAD MH  LEFT JOIN MATERIAL_FILE MF ON MH.ID = MF.PID WHERE MH.Serial_Number = '"
						+ ids[i]
						+ "' AND MF.FILE_TYPE IN ('KIT','PDF') GROUP BY MH.SERIAL_NUMBER,MF.STATUS");
				if(list.size()>0){
					for (Map<String, Object> map : list) {
						//String stauts = (String) map.get("STATUS");
						//String serialNumber = (String) map.get("SERIAL_NUMBER");
						int fileNum = Integer.parseInt(String.valueOf(map.get("STATUS"))) ;
						if(fileNum==0){
							msg=new Message("");
							return msg;
						}else{
							flg=false;
							sbf.append("产品:"+ids[i]+"中必须有一个有效文件。");
						}
					}
				}else{
					flg=false;
					sbf.append("产品:"+ids[i]+"必须上传一个文件。");
				}
			}
		}
		if(flg){
			return msg;
		}else{
			msg=new Message("101-data-error",sbf.toString());
			return msg;
		}
		
	}
	@RequestMapping(value="/queryMaterialPrice",method= RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialPrice(String pid) {
		List<MaterialPrice> list = materialPriceDao.findByPid(pid);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "saleItem" };
		return JSONArray.fromObject(list ,super.getJsonConfig(strings));
	}
	/**
	 * 导出报价清单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/downloadMaterialPrice")
	@ResponseBody
	public Message downloadMaterialPrice(HttpServletRequest request,
			HttpServletResponse response) {
		Message msg=null;
		String orderCodePosex = request.getParameter("orderCodePosex");
		FreemarkerGenerialExcel generialExcel=new FreemarkerGenerialExcel();
		msg = generialExcel.downFreemarkerExcel(orderCodePosex ,response, request);
		return msg;
	}
	/**
	 * 导入报价清单
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value="/uploadMaterialPrice",method=RequestMethod.POST)
	public ResponseEntity<String> uploadMaterialPrice(MultipartFile file,String orderCodePosex) {
		String json = null;
		HttpHeaders responseHeaders = new HttpHeaders();
		SalePrModManager salePrModManager = SpringContextHolder.getBean("salePrModManager");
		if(orderCodePosex==null||"".equals(orderCodePosex)) {
			json = "{success: false,msg:'单号匹配有误'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
		SaleItemDao saleItemDao = SpringContextHolder.getBean("saleItemDao");
		List<SaleItem> saleItemList = saleItemDao.findByOrderCodePosex(orderCodePosex);
		if(saleItemList.size()<=0) {
			json = "{success: false,msg:'单号匹配有误'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
		SaleItem saleItem = saleItemList.get(0);
		SaleHeader saleHeader = saleItem.getSaleHeader();
		String fileName = file.getOriginalFilename();
		double price=0.0d;
		try {
			String fileType = null;
			if(fileName.indexOf(".")!=-1) {
				fileType = fileName.substring(fileName.indexOf(".")+1, fileName.length());
			}
			if(fileType==null||!(fileType.equals("xls")||fileType.equals("xlsx"))) {
				json = "{success: false,msg:'请以Excell 文件格式 传输'}";
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<String>(json, responseHeaders,
						HttpStatus.OK);
			}
			List<CustItem> custMap = custItemDao.findItemsByCode1(saleHeader.getShouDaFang(),saleHeader.getOrderDate());
			if(custMap.size()>0) {
				Set<SaleItemPrice> ItemPrices = saleItem.getSaleItemPrices();
				for (SaleItemPrice saleItemPrice : ItemPrices) {
					if("PR04".equals(saleItemPrice.getType())) {
						//jdbcTemplate.update("UPDATE CUST_ITEM CI SET CI.SHENG_YU =?,CI.YU_JI=? WHERE CI.ID=?",new Object[] {custMap.get(0).getShengYu()+saleItemPrice.getTotal(),custMap.get(0).getYuJi()-saleItemPrice.getTotal(),custMap.get(0).getId()});
					}
				}
			}
			//salePrModManager.reCalculate(new ArrayList<SaleItemPrice>(ItemPrices), saleHeader.getShouDaFang(), saleHeader.getOrderDate(),"");
			List<MaterialPrice> rmMaterialPriceList = materialPriceDao.findByPid(saleItem.getId());
			materialPriceDao.delete(rmMaterialPriceList);
			InputStream is = file.getInputStream();
			Workbook workbook = ExcelUtil.createWorkBook(is);
			List<MaterialPrice> materialPriceList = new ArrayList<MaterialPrice>();
			Sheet sheet = workbook.getSheetAt(0);
			Map<String,Integer> fieldMap = new LinkedHashMap<String, Integer>();
			Field[] fields = new MaterialPrice().getClass().getDeclaredFields();//获取 物料 价格 属性 字段 
			Row titleRow = sheet.getRow(0);
			for (int i = 0; i < fields.length; i++) {
				Cell cell = titleRow.getCell(i);
				if(cell != null) {
					String cellVal = cell.getStringCellValue();
					for (int j = 0; j < fields.length; j++) {
						boolean flag = fields[j].isAnnotationPresent(ExcellField.class);//判断 是否含有注解
						if(flag) {
							ExcellField excellField = fields[j].getAnnotation(ExcellField.class);//获取 解析 注解 属性值
							String annotCellVal = excellField.cellVal();
							if(annotCellVal!=null&&annotCellVal.equals(cellVal.trim())) {
								fieldMap.put(fields[j].getName(), i);//通过注解 解析得出 对应 Excell 索引
								break;
							}
						}
					}
				}
			}
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if(row==null) {
					continue;
				}
				MaterialPrice materialPrice = new MaterialPrice();
				materialPrice.setType(row.getCell(fieldMap.get("type")).getStringCellValue());
				materialPrice.setName(row.getCell(fieldMap.get("name")).getStringCellValue());
				materialPrice.setWide((int)row.getCell(fieldMap.get("wide")).getNumericCellValue());
				materialPrice.setHigh((int)row.getCell(fieldMap.get("high")).getNumericCellValue());
				materialPrice.setDeep((int)row.getCell(fieldMap.get("deep")).getNumericCellValue());
				materialPrice.setColor(row.getCell(fieldMap.get("color")).getStringCellValue());
				materialPrice.setAmount((int)row.getCell(fieldMap.get("amount")).getNumericCellValue());
				materialPrice.setUnit(row.getCell(fieldMap.get("unit")).getStringCellValue());
				materialPrice.setArea(row.getCell(fieldMap.get("area")).getNumericCellValue());
				materialPrice.setUnitPrice(row.getCell(fieldMap.get("unitPrice")).getNumericCellValue());
				materialPrice.setTotalPrice(row.getCell(fieldMap.get("totalPrice")).getNumericCellValue());
				materialPrice.setRebate(row.getCell(fieldMap.get("rebate")).getNumericCellValue());
				price += row.getCell(fieldMap.get("netPrice")).getNumericCellValue();
				materialPrice.setNetPrice(row.getCell(fieldMap.get("netPrice")).getNumericCellValue());
				materialPrice.setLine(row.getCell(fieldMap.get("line")).getStringCellValue());
				materialPrice.setPid(saleItem.getId());
				materialPriceList.add(materialPrice);
			}
			if(materialPriceList.size()<=0) {
				json = "{success: false,msg:'请检查文件中是否存在数据'}";
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<String>(json, responseHeaders,
						HttpStatus.OK);
			}
			saleItem.setTotalPrice(price);
			//MaterialBean materialBean=new MaterialBean();
			//materialBean.setSaleItem(saleItem);
			Set<SaleItemPrice> saleItemPriceSet = saleItem.getSaleItemPrices();
			for (SaleItemPrice saleItemPrice : saleItemPriceSet) {
				if("PR01".equals(saleItemPrice.getType())) {
					saleItemPrice.setConditionValue(price);
					saleItemPrice.setSubtotal(price);
					saleItemPrice.setTotal(price*saleItem.getAmount());
				}
			}
			//materialBean.setShouDaFang(saleHeaderDao.findOne(saleItem.getSaleHeader().getId()).getShouDaFang());
			//materialBean.setSaleItemPrices(new ArrayList<SaleItemPrice>(saleItemPriceSet));
			materialPriceDao.save(materialPriceList);
			saleItem.setSaleItemPrices(saleItemPriceSet);
			commonManager.save(saleItem);
			//materialManager.updateSaleItem(materialBean);
			json = "{success: true,msg:'导入成功'}";
			
		}catch(Exception e) {
			e.printStackTrace();
			json = "{success: false,msg:'"+e.getMessage()+"'}";
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
		responseHeaders.setContentType(MediaType.TEXT_HTML);
		return new ResponseEntity<String>(json, responseHeaders,
				HttpStatus.OK);
	}
	/**
	 * 下载报价清单模板
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value="/downloadTemplate")
	@ResponseBody
	public Message downloadTemplate(HttpServletResponse response, HttpServletRequest request) {
		Message msg = null;
		//InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			String orderCodePosex = super.getRequest().getParameter("orderCodePosex");
			String fileName = "报价模板.xlsx";
			URL url = Thread.currentThread().getContextClassLoader().getResource("");
			String reponFileName =orderCodePosex+".xlsx";
			response.setCharacterEncoding("UTF-8");
			response.setContentType("multipart/form-data");
			response.setHeader("Content-Disposition","attachment;fileName="+ new String(reponFileName.replace(",", "及").getBytes("gbk"),"ISO8859-1"));
			String path = url.getPath().substring(0, url.getPath().indexOf("WEB-INF"));
			path = path.substring(1, path.length());
			File file = new File(path+"upload/template/"+fileName);
			if(!file.exists()) {
			}
			Workbook workbook= ExcelUtil.createWorkBook(new FileInputStream(file));
			List<Map<String, Object>> imosMaterialPriceDataList = jdbcTemplate.queryForList("SELECT COUNT(1) AS AMOUNT,ROUND(I.AREA,2) AS AREA,SUBSTR(I.INFO1,0,1)AS LINE,I.LENGTH,NVL(I.MATNAME,'') AS COLOR,I.NAME,I.THICKNESS,I.WIDTH FROM IMOS_IDBEXT I WHERE I.ORDERID =? AND I.INFO1 IS NOT NULL GROUP BY  I.AREA,I.INFO1,I.LENGTH,I.MATNAME,I.NAME,I.ORDERID,I.THICKNESS,I.WIDTH ORDER BY I.INFO1 ",orderCodePosex);
			if(imosMaterialPriceDataList.size()>0) {
				Sheet sheet = workbook.getSheetAt(0);
				for(int i = 1 ;i <= imosMaterialPriceDataList.size();i++) {
					Row row = sheet.createRow(i);
					row.createCell(0).setCellValue("");
					row.createCell(1).setCellValue((String)imosMaterialPriceDataList.get(i-1).get("NAME"));
					Float width = (Float)imosMaterialPriceDataList.get(i-1).get("WIDTH");
					row.createCell(2).setCellValue(width);
					Float length = (Float)imosMaterialPriceDataList.get(i-1).get("LENGTH");
					row.createCell(3).setCellValue(length);
					row.createCell(4).setCellValue((Float)imosMaterialPriceDataList.get(i-1).get("THICKNESS"));
					row.createCell(5).setCellValue((imosMaterialPriceDataList.get(i-1).get("COLOR")==null)?"":(String)imosMaterialPriceDataList.get(i-1).get("COLOR"));
					Object amount = imosMaterialPriceDataList.get(i-1).get("AMOUNT");
					BigDecimal amountDecimal = new BigDecimal(0);
					if(amount instanceof BigDecimal) {
						amountDecimal =new BigDecimal(String.valueOf(amount));
					}
					row.createCell(6).setCellValue(amountDecimal.intValue());
					row.createCell(7).setCellValue("件");
					/*Object area = imosMaterialPriceDataList.get(i-1).get("AREA");
					BigDecimal areaDecimal = new BigDecimal(0);
					if(area instanceof BigDecimal) {
						areaDecimal =new BigDecimal(String.valueOf(area));
					}*/
					row.createCell(8).setCellFormula("ROUND(((C"+(i+1)+"*"+"D"+(i+1)+")*0.000001)*"+"G"+(i+1)+",2)");//setCellValue(areaDecimal.doubleValue()*amountDecimal.intValue());
					row.createCell(9).setCellValue(1.1);
					row.createCell(10).setCellValue(105);
					String totalFormula="ROUNDUP(I"+(i+1)+"*J"+(i+1)+"*K"+(i+1)+",0)";
					row.createCell(11).setCellFormula(totalFormula);
					row.createCell(12).setCellValue(1);
					row.createCell(13).setCellFormula("ROUNDUP(L"+(i+1)+"*M"+(i+1)+",0)");
					row.createCell(14).setCellValue("");
				}
			}
			outputStream = response.getOutputStream();
			workbook.write(outputStream);
		}catch(IOException e) {
			e.printStackTrace();
		}finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return msg;
	}
}
