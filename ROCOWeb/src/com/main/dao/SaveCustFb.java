package com.main.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.CycleDetectionStrategy;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.main.bean.MaterialBean;
import com.main.domain.cust.CustHeader;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MyGoods;
import com.main.domain.sale.SaleItemFj;
import com.main.manager.MaterialManager;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysUser;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.SerialNumberManager;
import com.mw.framework.util.JsonDateValueProcessor;


public class SaveCustFb {
	/**
	 * 保存 非标数据
	 * 
	 * @param mmMaterialBean
	 * @param result
	 * @return
	 */
	public Message saveBaseFB(MaterialBean materialBean,String username) {
		Message msg = null;
		try {
			MaterialHead materialHead = materialBean.getMaterialHead();
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			CommonManager commonManager = SpringContextHolder.getBean("commonManager");
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
			Map<String,String[]> params = new HashMap<String,String[]>();
//			String username = "lj75001_01";
			params.put("ICEQid",new String[]{username });
			List<SysUser> loginUser = commonManager.queryByRange(SysUser.class, params);
			CustHeader cust=new CustHeader();
			if(loginUser.size()>0){
				cust = loginUser.get(0).getCustHeader();
				materialBean.setSysUser(loginUser.get(0));
			}
			materialHead.setRowStatus("A");
			materialHead.setIsStandard("0");
			materialHead.setLoadStatus("2");
			materialHead.setMeins("EA");
			materialHead.setMtart("Z101");
			materialHead.setVkorg(cust.getVkorg());
			materialHead.setVtweg(cust.getVtweg());
			MyGoods myGoods = new MyGoods();
			myGoods.setId("不添加我的商品");
			materialBean.setMyGoods(myGoods);
			materialHead.setLoadStatus("2");
			//保存
			MaterialManager materialManager = SpringContextHolder.getBean("materialManager");
			MaterialHead obj = materialManager.saveBase2020(materialBean, username);
			//MaterialHead obj = materialManager.saveBase(materialBean);
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
					getJsonConfig("yyyy-MM-dd", strings)));

		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-saveBase-500", "保存失败！");
		}
		return msg;
	}
	/**
	 * 保存非标文件，已封装
	 * @param materialFile
	 * @param kunnr
	 * @return
	 */
//	public ResponseEntity<String> fileuploadFb(MaterialFile materialFile,String kunnr) {
//		String json = null;
//		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
//		HttpHeaders responseHeaders = new HttpHeaders();
//		try {
//			MaterialHead materialHead = materialManager.getOne(materialFile
//					.getMaterialHead().getId(), MaterialHead.class);
//			String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
//			List<Map<String, Object>> queryForList = jdbcTemplate
//					.queryForList(sql);
//			if (queryForList != null && queryForList.size() > 0) {
//				Map<String, Object> map = queryForList.get(0);
//				String filePath = (String) map.get("DESC_EN_US");
//				String SerialNumber = "0000000000";
//				if (!StringUtils.isEmpty(materialHead.getSerialNumber())) {
//					SerialNumber = materialHead.getSerialNumber();
//				} else {
//					SerialNumber = materialHead.getMatnr();
//				}
//				String format = DateTools.getNowDateYYMMDD();
//				filePath += MyFileUtil.FILE_DIR + kunnr
//						+ MyFileUtil.FILE_DIR + format
//						+ MyFileUtil.FILE_DIR + SerialNumber
//						+ MyFileUtil.FILE_DIR + materialFile.getFileType();
//
//				String uuid = UUID.randomUUID().toString().replace("-", "");
//				CommonsMultipartFile commonsMultipartFile = materialFile
//						.getFile();
//				String oldName = commonsMultipartFile.getOriginalFilename();
//				oldName = oldName.substring(oldName.lastIndexOf("."));
//
//				materialFile.setUploadFileName(uuid + oldName);
//				materialFile.setUploadFileNameOld(commonsMultipartFile
//						.getOriginalFilename());
//				materialFile.setUploadFilePath(filePath);
//
//				boolean flag = MyFileUtil.fileUpload(materialFile);
//				if (flag) {
//					MaterialFile obj = materialManager.save(materialFile);
//					// 失效以前的文件
//					//String updateSql = "update  MATERIAL_FILE set status='X' where  id!='"
//					//		+ obj.getId()
//					//		+ "' and pid='"
//					//		+ materialHead.getId() + "'";
//					//jdbcTemplate.update(updateSql);
//
//					// 文件类型回写到非标产品
//					String type = materialFile.getFileType();
//					String mType = "";
//					if("KIT".equals(type)|| "PDF".equals(type)){
//						//PDF上传需要失效之前的PDF
//						String updateSql = "update  MATERIAL_FILE set status='X' where  pid='"
//								+ materialHead.getId() + "' and file_type='"+type+"' and id!='"+obj.getId()+"'";
//						jdbcTemplate.update(updateSql);
//					}
//					if ("PDF".equals(type)) {
//						mType = "2";
//					} else {
//						mType = "1";
//					}
//					String updateSql = "update MATERIAL_HEAD mh set  mh.file_type='"
//							+ mType
//							+ "' where  mh.id='"
//							+ materialHead.getId() + "'";
//					jdbcTemplate.update(updateSql);
//					json = "{success: true}";
//				} else {
//					json = "{success: false,msg:'文件上传失败！'}";
//				}
//			} else {
//				json = "{success: false,msg:'请配置文件上传路径！'}";
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			json = "{success: false,msg:'文件上传失败！'}";
//			responseHeaders.setContentType(MediaType.TEXT_HTML);
//			return new ResponseEntity<String>(json, responseHeaders,
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		}
//		responseHeaders.setContentType(MediaType.TEXT_HTML);
//		return new ResponseEntity<String>(json, responseHeaders,
//				HttpStatus.OK);
//	}
	/**
	 * jsonconfig
	 * 
	 * @param format
	 * @param strings
	 * @return
	 */
	protected JsonConfig getJsonConfig(String format, String... strings) {
		JsonConfig config = new JsonConfig();

		config.setIgnoreDefaultExcludes(false);
		config.setCycleDetectionStrategy(CycleDetectionStrategy.LENIENT);
		// 只要设置这个数组，指定过滤哪些字段
		config.setExcludes(/* new String[]{"children","parent"} */strings);
		config.registerJsonValueProcessor(Date.class,
				new JsonDateValueProcessor(format));
		config.registerJsonValueProcessor(Timestamp.class,
				new JsonDateValueProcessor(format));

		config.registerJsonValueProcessor(Double.class, new JsonValueProcessor() {
			@Override
			public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
				if (value == null) {
					return "";
				}
				return value;
			}
			@Override
			public Object processArrayValue(Object value, JsonConfig jsonConfig) {
				return value;
			}
		});

		config.registerJsonValueProcessor(Integer.class, new JsonValueProcessor() {
			@Override
			public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
				if (value == null) {
					return "";
				}
				return value;
			}
			@Override
			public Object processArrayValue(Object value, JsonConfig jsonConfig) {
				return value;
			}
		});

		return config;
	}
}
