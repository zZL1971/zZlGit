package com.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.mw.framework.bean.Constants.AND_OR;
import com.mw.framework.bean.Constants.CalculateType;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.utils.StringUtil;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import redis.clients.jedis.Transaction;

public class RocoImos {
	/**
	 * 炸单服务器传送文件
	 * 
	 * @param statement
	 * @param transaction
	 * @param orderCode
	 * @param orderId
	 * @param xmlFile
	 * @param imosPath
	 * @return
	 */
	public boolean sendImosFile(PreparedStatement statement,
			Transaction transaction, String orderCode, String orderId,
			String xmlFile, String imosPath) {
		boolean flag = false;
		try {
			xmlFile = xmlFile.replace('/', '\\');
			xmlFile = "\\\\" + xmlFile.substring(xmlFile.indexOf('@') + 1);
			statement.setString(1, UUID.randomUUID().toString());
			statement.setString(2, imosPath);
			statement.setString(3, xmlFile);
			statement.addBatch();
			// long result =
			transaction.lpush(imosPath, xmlFile);
			// if (result == 0) {
			// return false;
			// } else {
			// flag =
			transaction.hset(imosPath + "_map", xmlFile, orderCode);
			// .get() > 0;
			// }
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	/**
	 * XML文件处理
	 */
	public String xmlFileProcess(InputStream is) {

		try {
			// 先用saxReader 读取文件
			SAXReader saxReader = new SAXReader();
			Document doc = saxReader.read(is);
			int shapedCount = 0;
			int boxCount = 0;
			List<Element> list = doc.selectNodes("//XML/Order/BuilderList/Set");
			for (Element element : list) {

				Node pnameNode = element.selectSingleNode("Pname");
				Node pVarStringNode = element.selectSingleNode("PVarString");

				String pname = (pnameNode == null ? "" : pnameNode.getText());
				String pVarString = (pVarStringNode == null ? ""
						: pVarStringNode.getText());
				Map<String, String> pvarMap = new HashMap<String, String>();
				// 如果Pname或者PvarString 为空 那么判断下一个
				if (StringUtils.isEmpty(pname)
						|| StringUtils.isEmpty(pVarString)) {
					continue;
				}
				// 获取到pVar的哈希表
				pvarMap = getPVarMap(pVarString);
				// 判断是否含有异型
				if (shapedProcess(pname, pvarMap)) {
					shapedCount++;
				}
				// 判断是否含有box
				if (boxProcess(pname)) {
					boxCount++;
				}

				// 根据box数和异型板件数，去匹配获得级别判断
				String level = levelJudge(shapedCount, boxCount);
				// mh.setDifLevel(level);
				return level;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	/**
	 * 判断是否异型
	 * 
	 * @param doc
	 */
	private boolean shapedProcess(String pname, Map<String, String> map) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> list = jdbcTemplate.queryForList(
				"select * from XML_FILE_SHAPED_CONF T where PNAME=?",
				new Object[] { pname });
		Iterator<Map<String, Object>> mapIterable = list.iterator();
		Boolean bl = null;
		while (mapIterable.hasNext()) {
			Map<String, Object> map1 = mapIterable.next();
			// 计算方式 eg:<
			int calType = map1.get("CAL_TYPE") == null ? 0 : Integer
					.parseInt(map1.get("CAL_TYPE").toString());
			// 组合计算方式 eg:&
			String unionType = map1.get("UNION_TYPE") == null ? "" : map1.get(
					"UNION_TYPE").toString();
			// 判断值
			String value = map1.get("VALUE") == null ? "" : map1.get("VALUE")
					.toString();
			// 是否异型 0/1
			String shaped = map1.get("SHAPED").toString();
			// 判断的key
			String paraName = map1.get("PARA_NAME") == null ? "" : map1.get(
					"PARA_NAME").toString();
			// 如果初次判断，那么进行赋值
			if (bl == null) {
				bl = calculate(shaped, map.get(paraName), value, calType);
			} else {// 如果判断过，那么进行二次判断组合
				if (!StringUtils.isEmpty(unionType)) {
					switch (AND_OR.values()[Integer.parseInt(unionType)]) {
					case AND:
						bl = (bl && calculate(shaped, map.get(paraName), value,
								calType));
						break;
					case OR:
						bl = (bl || calculate(shaped, map.get(paraName), value,
								calType));
						break;

					default:
						break;
					}
				}
			}
		}
		return bl == null ? false : bl;
	}

	/**
	 * 判断是否box
	 * 
	 * @param doc
	 * @return
	 */
	private boolean boxProcess(String pname) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> list = jdbcTemplate
				.queryForList(
						"select * from XML_FILE_BOX_CONF where status=1 and box=1 and P_NAME=?",
						new Object[] { pname });
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断难度等级
	 * 
	 * @return
	 */
	private String levelJudge(int shapedCount, int boxCount) {
		return "1";
	}

	/**
	 * XML文件自动修改
	 * 
	 * @param doc
	 */
	private void modifyXml(Document doc, boolean flag) {

	}

	/**
	 * 获取pvar的键值对map
	 * 
	 * @param str
	 * @return
	 */
	private Map<String, String> getPVarMap(String str) {
		str = str.replace('|', ',');
		String[] split1 = str.split(",");
		Map<String, String> map = new HashMap<String, String>();
		for (int index = 0; index < split1.length; index++) {
			String[] split2 = split1[index].split(":=");
			if (split2.length > 1) {
				map.put(split2[0], split2[1]);
			}
		}
		return map;
	}

	/**
	 * 计算
	 * 
	 * @param flag
	 * @param value
	 * @param calType
	 * @return
	 */
	private Boolean calculate(String flag, String srcValue, String value,
			int calType) {
		Double srcV = 0.0;// Double.parseDouble(srcValue);
		Double Value = 0.0;// Double.parseDouble(value);
		if ("1".equals(flag)) {
			return true;
		} else {
			if (StringUtils.isEmpty(value) || StringUtils.isEmpty(srcValue)) {
				return false;
			}
			srcV = Double.parseDouble(srcValue);
			Value = Double.parseDouble(value);
			CalculateType caltype = CalculateType.values()[calType];
			switch (caltype) {
			case GTR:
				return srcV > Value;
			case LT:
				return srcV < Value;
			case LE:
				return srcV <= Value;
			case GE:
				return srcV >= Value;
			case EQ:
				return srcV == Value;
			default:
				break;
			}
		}
		return false;
	}

	/**
	 * 模块计算
	 * 
	 * @param pname
	 * @return
	 */
	private boolean modalProcess(String pname) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> list = jdbcTemplate
				.queryForList(
						"select * from XML_FILE_SHAPED_CONF where status=1 and MODAL=1 and PNAME=?",
						new Object[] { pname });
		if (list != null && list.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 用于IMOS炸单服务器负载均衡分配使用
	 * 
	 * @param isSpecial
	 *            是否特殊型录
	 * @return
	 */
	private String ImosLoadBalance(boolean isSpecial) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<Map<String, Object>> normalList = jdbcTemplate
				.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=0 order by  NVL(t2.amount,0)");
		List<Map<String, Object>> specialList = jdbcTemplate
				.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=1 order by  NVL(t2.amount,0)");
		if (isSpecial) {
			return specialList.get(0).get("IMOS_PATH").toString();
		} else {
			return normalList.get(0).get("IMOS_PATH").toString();
		}
	}

	/**
	 * XML型录判断 得出炸单服务器
	 * 
	 * @param pname
	 * @param pvarString
	 * @return String 表示炸单服务器
	 * @throws DocumentException
	 */
	public String XMLCatalog(InputStream is) throws DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(is);
		List<Element> elements = doc.selectNodes("//XML/Order/BuilderList/Set");
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<String> specialNodeText = new ArrayList<String>();
		specialNodeText.add("HINGEL1");
		specialNodeText.add("HINGEL2");
		specialNodeText.add("HINGEL3");
		specialNodeText.add("HINGEL4");
		specialNodeText.add("HINGEL5");
		specialNodeText.add("HINGER1");
		specialNodeText.add("HINGER2");
		specialNodeText.add("HINGER3");
		specialNodeText.add("HINGER4");
		specialNodeText.add("HINGER5");
		specialNodeText.add("1");
		specialNodeText.add("HPL");
		specialNodeText.add("HPR");
		specialNodeText.add("HPT");
		specialNodeText.add("HPB");

		specialNodeText.add("MDZKJ");
		specialNodeText.add("MDZKJQ2");
		specialNodeText.add("MDZKJ_F");
		specialNodeText.add("RW250");
		specialNodeText.add("HAND");
		specialNodeText.add("====");
		specialNodeText.add("MJ");
		specialNodeText.add("XP16-28");

		specialNodeText.add("XNCF");
		specialNodeText.add("XNZSB");
		specialNodeText.add("RW227");
		specialNodeText.add("RW228");
		specialNodeText.add("RW332");
		specialNodeText.add("RW335");
		specialNodeText.add("EW334");
		specialNodeText.add("RW333");
		specialNodeText.add("XP16-32");
		specialNodeText.add("YMWNK");
		specialNodeText.add("TM_DO");
		int flag = 0;
		for (Element element : elements) {
			if ((!specialNodeText.contains(element.selectSingleNode("Pname")
					.getText()) && !StringUtil.isEmpty(element
					.selectSingleNode("Pname").getText()))) {
				// 不包含CV:=A 普通形录 也不包含RK
				if (element.selectSingleNode("PVarString").getText().trim()
						.indexOf("CV:=A") == -1
						&& element.selectSingleNode("PVarString").getText()
								.trim().indexOf("RK") == -1
						&& ((flag & 1) != 1)) {
					flag += 1;
				} else if (element.selectSingleNode("PVarString").getText()
						.trim().indexOf("CV:=A") != -1
						&& (flag & 2) != 2) {
					// 包含CV:=A A形录
					flag += 2;
				} else if (element.selectSingleNode("PVarString").getText()
						.trim().indexOf("RK") != -1
						&& (flag & 4) != 4) {
					// 包含RK 新形录
					flag += 4;
				}

			}
		}
		// flag 1说明只含有普通型录 2说明含有A型录 其他说明两个都含有，那么要求重新画图

		switch (flag) {
		case 0:
		case 1:
			// 若为普通型录,也分不出这款普通型录是不是新型录,如果为新型录的话,默认208路径不用修改。
			/*
			 * if (imos_path.equals("IMOS_03")) { break; }
			 */

			return ImosLoadBalance(false);
		case 2:
			return ImosLoadBalance(true);
		case 4:
			List<Map<String, Object>> _list = jdbcTemplate
					.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=2 order by  NVL(t2.amount,0)");
			return _list.get(0).get("IMOS_PATH").toString();
		default:
			return null;
		}
	}

	/**
	 * 获取bom所有上层
	 * 
	 * @param parentId
	 * @param orderCodePosex
	 * @param list
	 * @return
	 */
	public List<Map<String, Object>> getBomUp(Object parentId,
			String orderCodePosex, List<Map<String, Object>> list,
			List<String> ids) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		if (parentId == null) {
			return list;
		} else {
			ids.add((String) parentId);
			List<Map<String, Object>> _list = jdbcTemplate.queryForList(
					"select * from imos_idbext ii where ii.orderid=? and id=?",
					new Object[] { orderCodePosex, parentId });
			list.addAll(_list);
			 for(int index=0;index<_list.size();index++){
				 if(!StringUtils.isEmpty(_list.get(0).get("PARENTID")))
				 getBomUp(_list.get(0).get("PARENTID"), orderCodePosex, list, ids);
			 }
			return list;
		}

	}
	
	/**
	 * 获取bom所有下层
	 * 
	 * @param parentId
	 * @param orderCodePosex
	 * @param list
	 * @return
	 */
	public List<Map<String, Object>> getBomDown(Object id,
			String orderCodePosex, List<Map<String, Object>> list,
			List<String> ids) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		if (id == null) {
			return list;
		} else {
			ids.add((String) id);
			List<Map<String, Object>> _list = jdbcTemplate.queryForList(
					"select * from imos_idbext ii where ii.orderid=? and parentid=?",
					new Object[] { orderCodePosex, id });
			list.addAll(_list);
			
			 for(int index=0;index<_list.size();index++){
				 getBomDown(_list.get(index).get("ID"), orderCodePosex, list, ids);
			 }
			return list;
		}

	}
	private static final char XML_P='P';
	private static final char XML_V='V';
	private static final char XML_H='H';
	private static final char XML_W='W';
	private static final char XML_C='C';
	/**
	 * 解析XML文本控制对应的KEY 根据KEY值获取对应的值
	 * @param descArray  xml文本
	 * @param key key值：P 对应Pname， H：对应Height， V对应：PVarString
	 * @return 返回获取KEY对应的value
	 */
	private static String resolveType(String desc,Character key){
		String value=null;
			switch (key) {
			case XML_P:
				value=desc.substring(desc.indexOf(":")+1, desc.indexOf("|")!=-1?desc.indexOf("|"):desc.length());
				break;
			case XML_V:
				value=desc.substring(desc.indexOf("|")+1);
				if(value.indexOf("Height")!=-1){
					value=value.substring(0, value.lastIndexOf("Height")-1);
				}
				break;
			case XML_H:
				value=desc.substring(desc.lastIndexOf("=")+1, desc.length());
				break;
			case XML_W:
				value=desc.substring(desc.lastIndexOf("=")+1, desc.length());
				break;
			default:
				value="NO";
				break;
			}
		return value;
	}
	
/**
 * 校验xml文件 =====规则使用ContainPool 容器池中对应的值
 * 匹配到容器池中对应的值则不让其上传
 * @param document
 * @return
 */
public JSONArray uploadXML(Document document,JdbcTemplate jdbcTemplate) {
	//XrmtpcMGa9L4DnQMGXa5Ew 对应数据字典 的 xml控制 查询
	String dataDict="SELECT SD.TYPE_KEY,SD.DESC_ZH_CN,SD.TYPE_DESC FROM  SYS_DATA_DICT SD WHERE SD.TRIE_ID='XrmtpcMGa9L4DnQMGXa5Ew' AND SD.STAT='1'";
	List<Map<String,Object>> dataList=jdbcTemplate.queryForList(dataDict);
	Map<String, Object> msg = new LinkedHashMap<String,Object>();
	JSONArray array = new JSONArray();
	List<Element> list = document.selectNodes("//XML/Order/BuilderList/Set");
	//开始分是否为组合条件
	/**
	 * 首先需要获取  数据源中对应的xml文本控制   在取得xml中对用部件信息  解析
	 */
	for (Map<String, Object> xmlList : dataList) {
		String descZhCn = (String) xmlList.get("DESC_ZH_CN");
		String typeKey = (String) xmlList.get("TYPE_KEY");
		String typeDesc = (String) xmlList.get("TYPE_DESC");
		String[] descSplit = descZhCn.split(",");
		Integer count=0;//匹配次数
		for (String descArray : descSplit) {
			/**
			 * 解析出部件名称
			 * 解析出高度值
			 * 解析出属性值
			 */
			String basePname=typeKey.indexOf(XML_P)!=-1?resolveType(descArray, XML_P):null;
			String basePVarString=typeKey.indexOf(XML_V)!=-1?resolveType(descArray, XML_V):null;
			String baseHeight=typeKey.indexOf(XML_H)!=-1?resolveType(descArray, XML_H):null;
			String baseWidth=typeKey.indexOf(XML_W)!=-1?resolveType(descArray, XML_W):null;
			for (Element ele : list) {
				Integer resolverCount=0;
				String pname = ele.selectSingleNode("Pname").getStringValue();
				String pvarString = ele.selectSingleNode("PVarString").getStringValue();
				String[] pvarStringArray = pvarString.split("\\|");
				String[] keys = typeKey.split(",");
				for (String key : keys) {
					switch (key.charAt(0)) {
					case XML_P:
						if(pname.equals(basePname)){
							resolverCount++;
						}
						break;
					case XML_V:
						int num=0;
						String[] basePvarArray = basePVarString.split("\\|");
						for (String base : basePvarArray) {
							for (String pvar : pvarStringArray) {
								if(base.equals(pvar))num++;
								if(num==basePvarArray.length){
									resolverCount++; 
									break;
								}
							}
						}
						break;
					case XML_H:
						for (String pvar : pvarStringArray) {
							if(pvar.startsWith("Height")){
								double pvarHeight = Double.parseDouble(pvar.substring(pvar.lastIndexOf("=")+1));
								double height=Double.parseDouble(baseHeight);
								if(pvarHeight<height){
									resolverCount++;
									break;
								}
							}
						}
						break;
					case XML_W:
						for (String pvar : pvarStringArray) {
							if(pvar.startsWith("Width")){
								double pvarHeight = Double.parseDouble(pvar.substring(pvar.lastIndexOf("=")+1));
								double width=Double.parseDouble(baseWidth);
								if(pvarHeight>width){
									resolverCount++;
									break;
								}
							}
						}
						break;
					default:
						break;
					}
				}
				if(resolverCount==keys.length){
					count++;
				}
			}
			try{
				if((descSplit.length>0&&count==descSplit.length)){
					msg.put("infoCode", typeDesc);
					msg.put("failure", "false");
					break;
				}
			}catch(Exception e){
				msg.put("infoCode", "null");
				msg.put("failure", "true");
				break;
			}
		}
	}
	if(!(msg.size()>0)){
		msg.put("infoCode", "null");
		msg.put("failure", "true");
	}
	array.add(msg);
	return array;
	
}
	/**
	 * 删除imos信息
	 * 
	 * @param ids
	 * @param orderCodePosex
	 */
	public void del(List<String> ids, String orderCodePosex) {
		StringBuilder sbBuilder = new StringBuilder();
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		for (int index = 0; index < ids.size(); index++) {
			sbBuilder.append(",'" + ids.get(index) + "'");
		}
		String str = sbBuilder.substring(1);
		String sql = "delete from imos_idbext where id in (" + str
				+ ") and orderid='" + orderCodePosex + "'";
		jdbcTemplate.update(sql);
	}

	/**
	 * 新增imos信息
	 */
	public void addList(List<Map<String, Object>> list) {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		List<String> _ids = new ArrayList<String>();
		String sql = "insert into imos_idbext(";
		StringBuilder sb1 = new StringBuilder();
		StringBuilder sb2 = new StringBuilder();
		List<String> keyList = new ArrayList<String>();
		if (list != null && list.size() > 0) {
			for (String key : list.get(0).keySet()) {
				keyList.add(key);
				sb1.append("," + key);
				sb2.append(",?");
			}
			sql = sql + sb1.substring(1) + ") values(" + sb2.substring(1) + ")";
			for (int index = 0; index < list.size(); index++) {
				if (!_ids.contains((String) (list.get(index).get("ID")))) {
					List<Object> paramsList = new ArrayList<Object>();
					// 加入参数
					for (int keyIndex = 0; keyIndex < keyList.size(); keyIndex++) {

						paramsList.add(list.get(index).get(
								keyList.get(keyIndex)));
					}
					System.out.println(list.get(index).get("ID"));
					System.out.println(list.get(index).get("ORDERID"));
					jdbcTemplate.update(sql, paramsList.toArray());
					_ids.add((String) (list.get(index).get("ID")));
				}
			}
		}
	}

	/**
	 * 分析销售方式
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	public String analysisSaleFor(InputStream is) throws IOException,
			DocumentException {
		SAXReader saxReader = new SAXReader();
		Document doc = saxReader.read(is);
		List<Element> list = doc
				.selectNodes("//XML/Order/BuilderList/Set/PVarString");

		for (int index = 0; index < list.size(); index++) {
			String val = list.get(index).getStringValue();
			if (val.toUpperCase().startsWith("CAT:=ROCO-CG")) {
				return "1";
			}else if(val.toUpperCase().startsWith("CAT:=ROCO-MM")){
				return "3";
			}else{
				return "0";
			}
		}
		return null;
	}
	
	/**
	 * 检测xml是否可以上传
	 * @param is
	 * @return
	 */
	public boolean validate(InputStream is,StringBuilder sb){
		boolean result=true;
		try{
			JdbcTemplate jdbcTemplate=SpringContextHolder.getBean("jdbcTemplate");
			SAXReader saxReader=new SAXReader();
			Document doc = saxReader.read(is);
			JSONArray array = this.validationXmlText(doc, jdbcTemplate);
			boolean flg=true;
			if(array!=null){
				if(array.size()>0&&!"".equals(array)){
					for (int i = 0; i < array.size(); i++) {
						JSONObject obj=array.getJSONObject(i);
						flg=obj.getBoolean("failure");
						sb.append(obj.getString("infoCode"));
					}	
				}
			}
			if(!flg){
				return false;
			}
			/*sb.setLength(0);
			String sql="select * from xml_file_upload_config t where t.status=1 and t.p_name=?";
			//获取Pname节点
			List<Element> list = doc.selectNodes("//XML/Order/BuilderList/Set/Pname");
			List<Map<String,Object>> resultList=null;
			for (Element element : list) {
				String pnameValue=element.getStringValue();
				Node node=element.getParent().selectSingleNode("PVarString");
				String value=node.getStringValue();
				resultList=jdbcTemplate.queryForList(sql,new Object[]{pnameValue});
				if(resultList!=null && resultList.size()>0){
					for (Map<String, Object> map : resultList) {
						String condition=""+(BigDecimal)map.get("CONDITION");
						String containsString=map.get("CONTAINS_STRING")==null?"":(String)map.get("CONTAINS_STRING");
						String ext1=map.get("EXT1")==null?"":(String)map.get("EXT1");
						String ext2=map.get("EXT2")==null?"":(String)map.get("EXT2");
						String judgetype=""+(BigDecimal)map.get("JUDGETYPE");
						//如果condition表明只要有这个pname 就不能上传
						if("1".equals(condition)){
							return false;
						}
						boolean flag=true;
						if(!StringUtils.isEmpty(ext1) || !StringUtils.isEmpty(ext2)){
							if(!StringUtils.isEmpty(ext1)){
								flag=value.contains(ext1);
							}
							if(!StringUtils.isEmpty(ext2)){
								flag=flag||value.contains(ext2);
							}
						}
						switch (Integer.parseInt(judgetype)) {
						case 1:
							if(flag && value.contains(containsString)){
								sb.append("含有"+pnameValue);
								return false;
							}
							break;
						case 0:
							if(flag &&  !value.contains(containsString)){
								sb.append("不含有"+pnameValue);
								return false;
							}
							break;
						default:
							break;
						}
					}
				}
			}*/
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				if(is!=null){
					is.close();
				}
			}catch(Exception e){
				
			}
		}
		return result;
	}

	public JSONArray validationXmlText(Document document,JdbcTemplate jdbcTemplate) {
		JSONArray array= new JSONArray();
		Map<String, Object> msg = new LinkedHashMap<String,Object>();
		String sql="SELECT SX.TEXT,SX.TEXT_DESC,SX.COUNTER FROM SYS_XML_CONTROL_TEXT SX WHERE NVL(SX.STAT,1)<>0 ORDER BY SX.TEXT_CODE DESC";
		List<Map<String, Object>> baseList = jdbcTemplate.queryForList(sql);
		List<Element> list = document.selectNodes("//XML/Order/BuilderList/Set");
		for (Element ele : list) {
			String pname = ele.selectSingleNode("Pname").getStringValue();
			String pvarString = ele.selectSingleNode("PVarString").getStringValue();
			for (Map<String, Object> map : baseList) {
				String text = (String) map.get("TEXT");
				String textDesc = (String) map.get("TEXT_DESC");
				String counter = (String) map.get("COUNTER");
				String[] base = counter.split("@");
				for (int i = 0; i < base.length; i++) {
					int number = Integer.parseInt(base[i].substring(base[i].indexOf(":")+1));
					int num = resolverValidationXmlText(text, pname, pvarString, number,String.valueOf(i+1));
					if(number==num){
						if((i+1) == base.length) {
							msg.put("infoCode", textDesc);
							msg.put("failure", "false");
						}
					}
				}
				if(msg.size()>0) {
					array.add(msg);
					return array;
				}
			}
		}
		if(!(msg.size()>0)){
			msg.put("infoCode", "null");
			msg.put("failure", "true");
			array.add(msg);
			return array;
		}
		return null;
	}
	public static int resolverValidationXmlText(String dataText,String name,String pvars,Integer dataCount,String like){
		String[] val=dataText.split("@");
		String[] Pname=null;
		String[] PVarString=null;
		String[] Change=null;
		for (int i = 0; i < val.length; i++) {
			if(val[i].startsWith("P")){
				Pname = resolverStr(val, i,XML_P,like);
			}else if(val[i].startsWith("V")){
				PVarString = resolverStr(val, i,XML_V,like);
			}else if(val[i].startsWith("C")){
				Change = resolverStr(val, i,XML_C,like);
			}
		}
		int count=0;
		for (int i = 0; i < Pname.length; i++) {
			String realName = Pname[i].substring(Pname[i].indexOf(":")+1);
			if(realName.equals(name)||realName.equals("NULL")){
				count++;
			}
		}
		if(PVarString!=null) {
			for (int v = 0; v < PVarString.length; v++) {
				String $key = PVarString[v];
				$key=$key.substring($key.indexOf(":")+1);
				String $val=$key.substring($key.indexOf(":")+1, $key.length());
				$key=$key.substring(0, $key.indexOf(":"));
				if(!$val.matches("^[0-9]+$")) {
					if(pvars.indexOf($val) != -1) {
						String text = pvars.substring(pvars.indexOf($val));
						text = text.substring(0, text.indexOf("|"));
						text = text.substring(text.indexOf("=")+1);
						$val = text;
					}else {
						return 0;
					}
				}
				for (int c = 0; c < Change.length; c++) {
					if(Change[c].indexOf($key)!=-1){
						String where=Change[c].substring(Change[c].indexOf("(")+1, Change[c].indexOf(")"));
						//下面是截取竖杠 前面的字符串
						if(pvars.indexOf($key)!=-1) {
							String data=pvars.substring(pvars.indexOf($key));
							//获取 其对应的value值
							data=data.substring(0, data.indexOf("|")).substring(data.indexOf("=")+1);
							if("<".equals(where)){
								if(Double.parseDouble(data) < Double.parseDouble($val)){
									count++;
								}
							}else if(">".equals(where)){
								if(Double.parseDouble(data) > Double.parseDouble($val)){
									count++;
								}
							}else if("=".equals(where)){
								if(Double.parseDouble(data) == Double.parseDouble($val)){
									count++;
								}
							}else if("<=".equals(where)){
								if(Double.parseDouble(data) <= Double.parseDouble($val)){
									count++;
								}
							}else if(">=".equals(where)){
								if(Double.parseDouble(data) >= Double.parseDouble($val)){
									count++;
								}
							}
						}
					}else{
						String column = PVarString[v].substring(PVarString[v].indexOf(":")+1);
						String value=column.substring(column.indexOf(":")+1);
						column=column.substring(0, column.indexOf(":"));
						if(pvars.indexOf(column)!=-1){
							String data=pvars.substring(pvars.indexOf(column));
							//获取 其对应的value值
							data=data.substring(0, data.indexOf("|"));
							data=data.substring(data.indexOf("=")+1);
							if(value.equals(data)){
								//System.out.println("改变值匹配");
								count++;
							}
						}
					}
				}
			}
		}
		if(count==dataCount){
			return count;
		}
		return 0;
	}
	private static String[] resolverStr(String[] val, int i,char format,String like) {
		String[] oldVal=null;
		String newVal=val[i].substring(val[i].indexOf(format)+2, val[i].length());
		if(newVal.startsWith("[")){
			newVal=newVal.substring(1, newVal.length()-1);
			String[] forTime = newVal.split(",");
			StringBuilder builder=new StringBuilder();
			for (int j = 0; j < forTime.length; j++) {
				if(forTime[j].startsWith(like)){
					builder.append(forTime[j]+",");
				}
			}
			oldVal=builder.toString().substring(0, builder.length()-1).split(",");
		}
		return oldVal;
	}
}
