package com.webservice;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.jws.WebService;
import javax.xml.rpc.ServiceException;

import net.sf.json.JSONObject;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import sun.misc.BASE64Decoder;

import com.main.bean.MaterialBean;
import com.main.dao.SaveCustFb;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.sale.SaleItemFj;
import com.main.manager.MaterialManager;
import com.main.util.MyFileUtil;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.dao.SysDataDictDao;
import com.mw.framework.dao.SysTrieTreeDao;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.domain.SysTrieTree;
import com.mw.framework.domain.SysUser;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.GZipUtils;
@WebService(endpointInterface="com.webservice.IOrder2020",serviceName="saveBase2020")
public class Order2020Impl implements IOrder2020{
	@SuppressWarnings("unused")
	public String kitUpload(String data,String fileBase64) throws ServiceException, MalformedURLException{
		Map<String,Object> message=new HashMap<String,Object>();
		try{
			SysTrieTreeDao sysTrieTreeDao=SpringContextHolder.getBean("sysTrieTreeDao");
			SysDataDictDao sysDataDictDao=SpringContextHolder.getBean("sysDataDictDao");
			JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
			String desc="";
			if(fileBase64 == null && "".equals(fileBase64)){
				message.put("return_code", "FALL");
				message.put("return_msg", "请上传文件");
				return JSONObject.fromObject(message).toString();
			}
			JSONObject jsonObject =JSONObject.fromObject(data);
			//调用保存方法
			MaterialBean materialBean=new MaterialBean();
			Message result=null; 
			String msg="";
			String matkl="",series="",size="",spart="",material="",color="",zzazdr="",materialColor="",matnr="",productSpace="",fileName="",userId="",customer="",grade="",saleFor="",softdogId="";
			String widthDesc="",heightDesc="",longDesc="";
			//判断必须字段是否为空
			String changeSysDataDictDesc="";
			boolean isSaleFor = true;
			if("".equals(jsonObject.get("saleFor"))||jsonObject.get("saleFor")==null){
				msg="销售分类,";
			}else{
				saleFor=(String) jsonObject.get("saleFor");
				SysTrieTree tex = sysTrieTreeDao.findByKeyVal("SALE_FOR");
				List<SysDataDict> sysData = sysDataDictDao.findByTrieTreeId(tex.getId());
				for (SysDataDict sysDataDict : sysData) {
					if(saleFor.equals(sysDataDict.getDescZhCn())){
						saleFor = sysDataDict.getKeyVal();
						spart=sysDataDict.getTypeKey();
						isSaleFor=false;
						break;
					}
				}
			}
			if(isSaleFor){
				message.put("return_code", "FALL");
				message.put("return_msg", "销售分类未维护!");
				return JSONObject.fromObject(message).toString();
			}
			if("".equals(jsonObject.get("matkl"))||jsonObject.get("matkl")==null){
				msg="产品分类,";
			}else{
				changeSysDataDictDesc = ("1".equals(saleFor))?"MATERIAL_MATKL_CUP":("0".equals(saleFor))?"MATERIAL_MATKL":("3".equals(saleFor))?"MATERIAL_MATKL_MM":"";
				matkl=(String) jsonObject.get("matkl");
				desc +=matkl;
				//如果销分类为橱柜
				SysTrieTree tex = sysTrieTreeDao.findByKeyVal(changeSysDataDictDesc);
				List<SysDataDict> sysData = sysDataDictDao.findByTrieTreeId(tex.getId());
				for (SysDataDict sysDataDict : sysData) {
					if(matkl.equals(sysDataDict.getDescZhCn())){
						matkl = sysDataDict.getKeyVal();
						matnr = sysDataDict.getTypeDesc();
						break;
					}
				}
			}
			if("1302".endsWith(matkl)||"1303".endsWith(matkl)){
				message.put("return_code", "FALL");
				message.put("return_msg", "不能上传此产品分类,请选择其他产品分类!");
				return JSONObject.fromObject(message).toString();
			}
			if("".equals(matnr)){
				message.put("return_code", "FALL");
				message.put("return_msg", "产品分类未维护!");
				return JSONObject.fromObject(message).toString();
			}
			/*if(matkl==""&&matnr==""){
			}*/
			//			if("".equals(jsonObject.get("material"))||jsonObject.get("material")==null){
			//				msg+="材质,";
			//			}else{
			//				//材质保存在颜色字段里面
			//			}
			if("".equals(jsonObject.get("color"))||jsonObject.get("color")==null){
				if("3".equals(saleFor)){
					material="M10";//默认PVC饰面
					color="131";//默认卡其灰
					desc +="PVC饰面";
					desc +="卡其灰";
				}else{
					material="012";//默认原木微粒板
					color="034";//默认后简M族
					desc +="原木微粒板";
					desc +="后简M族";
				}
				materialColor="999999";
			}else{
				materialColor="0";
				boolean isColor =true;
				color=(String) jsonObject.get("color");
				String[]a=color.split("\\;");
				color=a[0];
				if(!color.contains("-")){
					message.put("return_code", "FALL");
					message.put("return_msg", "请上传正确的材质和颜色格式!");
					return JSONObject.fromObject(message).toString();
				}
				String[]b =color.split("-");
				if(b.length<2){
					message.put("return_code", "FALL");
					message.put("return_msg", "请上传正确的材质和颜色格式!");
					return JSONObject.fromObject(message).toString();
				}
				if(b[0]==""||b[0]==null||b[0].isEmpty()||b[1]==""||b[1]==null||b[1].isEmpty()){
					message.put("return_code", "FALL");
					message.put("return_msg", "请上传正确的材质和颜色格式!");
					return JSONObject.fromObject(message).toString();
				}
				material=b[0];//数据字典编码
				color=b[1];//中文颜色
				String material1="";
				//如果销分类为橱柜
				changeSysDataDictDesc = ("1".equals(saleFor))?"TEXTURE_OF_MATERIAL_CUP":("0".equals(saleFor))?"TEXTURE_OF_MATERIAL":("3".equals(saleFor))?"MM_TEXTURE":"";

				SysTrieTree tex = sysTrieTreeDao.findByKeyVal(changeSysDataDictDesc);
				if(tex==null){
					message.put("return_code", "FALL");
					message.put("return_msg", "请配置"+jsonObject.get("saleFor")+"对应的材质");
					return JSONObject.fromObject(message).toString();
				}
				Set<SysTrieTree> sysTrieTrees = tex.getChildren();
				for (SysTrieTree sysTrieTree : sysTrieTrees) {
					if(material.equals(sysTrieTree.getKeyVal())){
						material = sysTrieTree.getKeyVal();
						material1 = sysTrieTree.getDescZhCn();
						break;
					}
				}
				if("".equals(material1)){
					message.put("return_code", "FALL");
					message.put("return_msg", "材质未维护!");
					return JSONObject.fromObject(message).toString();
				}
				desc +=material1;
				desc +=color;
				List<Map<String, Object>> list1 = jdbcTemplate.queryForList("SELECT DD.DESC_ZH_CN,DD.KEY_VAL FROM SYS_DATA_DICT DD LEFT JOIN SYS_TRIE_TREE TT ON DD.TRIE_ID=TT.ID WHERE TT.KEY_VAL='"+material+"'");
				for (Map<String, Object> map : list1) {
					if(map.get("DESC_ZH_CN").equals(color)){
						color=map.get("KEY_VAL").toString();
						isColor=false;
						break;
					}
				}
				if(isColor){
					message.put("return_code", "FALL");
					message.put("return_msg", "颜色未维护!");
					return JSONObject.fromObject(message).toString();
				}
			}
			if("".equals(jsonObject.get("zzazdr"))||jsonObject.get("zzazdr")==null){
				zzazdr="其他";
				productSpace="99";
			}else{
				boolean isProductSpace =true;
				zzazdr=(String) jsonObject.get("zzazdr");
				SysTrieTree tex = sysTrieTreeDao.findByKeyVal("ZZAZDR");
				List<SysDataDict> sysData = sysDataDictDao.findByTrieTreeId(tex.getId());
				for (SysDataDict sysDataDict : sysData) {
					if(zzazdr.equals(sysDataDict.getDescZhCn())){
						zzazdr = sysDataDict.getDescZhCn();
						productSpace = sysDataDict.getKeyVal();
						isProductSpace=false;
						break;
					}
				}
				if(isProductSpace){
					zzazdr=(String) jsonObject.get("zzazdr");
					productSpace="99";
				}
			}
			if("".equals(jsonObject.get("customer"))||jsonObject.get("customer")==null){
				msg+="店面,";
			}else{
				customer=(String) jsonObject.get("customer");
			}
			if("".equals(jsonObject.get("series"))||jsonObject.get("series")==null){
				series="999";
			}else{
				boolean isSeries=true;
				series=(String) jsonObject.get("series");
				series=getSeries(series);
				SysTrieTree tex = sysTrieTreeDao.findByKeyVal("STYLE");
				List<SysDataDict> sysData = sysDataDictDao.findByTrieTreeId(tex.getId());
				for (SysDataDict sysDataDict : sysData) {
					if(series.equals(sysDataDict.getDescZhCn())){
						series = sysDataDict.getKeyVal();
						isSeries=false;
						break;
					}
				}
				if(isSeries){
					message.put("return_code", "FALL");
					message.put("return_msg", "主题系列未维护");
					return JSONObject.fromObject(message).toString();
				}
			}

			if("".equals(jsonObject.get("grade"))||jsonObject.get("grade")==null){
				grade="C";
			}else{
				grade =(String) jsonObject.get("grade");
				boolean isC=grade.contains("C");
				if(isC){
					grade="C";
				}else{
					boolean isB=grade.contains("B");
					if(isB){
						grade="B";
					}else{
						boolean isA=grade.contains("A");
						if(isA){
							grade="A";
						}else{
							grade="C";
						}
					}
				}
			}
			if("".equals(jsonObject.get("userId"))||jsonObject.get("userId")==null){
				msg+="上传人,";
			}else{
				userId=(String) jsonObject.get("userId");
			}
			String[] str = userId.split("_");
			int num = jdbcTemplate.queryForObject("select count(*) from cust_header ch left join cust_logistics cl on ch.id=cl.pid where ch.kunnr='"+str[0].toUpperCase()+"'and cl.spart='"+spart+"'", Integer.class);
			if(num==0){
				message.put("return_code", "FALL");
				message.put("return_msg", "没有上传此产品组权限,请选择其他产品分类!");
				return JSONObject.fromObject(message).toString();
			}
			if(userId.contains("SJB")){
				userId=userId.split("SJB")[0];
			}else{
				if("".equals(jsonObject.get("softdogId"))||jsonObject.get("softdogId")==null){
					msg+="2020加密狗Id,";
				}else{
					softdogId=(String) jsonObject.get("softdogId");
					String sql="select su.id from sys_user su where su.SOFTDOG_ID='"+softdogId+"'";
					List<Map<String, Object>> map = jdbcTemplate.queryForList(sql);
					if(!map.isEmpty()&&map.size()>0){
						String userName=(String)map.get(0).get("ID");
						if(!userName.equals(userId)){
							message.put("return_code", "FALL");
							message.put("return_msg", "加密锁或上传人错误!");
							return JSONObject.fromObject(message).toString();
						}
					}else{
						message.put("return_code", "FALL");
						message.put("return_msg", "请联系订单处理部绑定加密锁");
						return JSONObject.fromObject(message).toString();
					}
				}
			}

			if("".equals(jsonObject.get("size"))||jsonObject.get("size")==null){

			}else{
				if(jsonObject.get("size")!=null||jsonObject.get("size")!=""){
					String sizeJson=(String) jsonObject.get("size");
					String[] size1 = sizeJson.split("\\*");
					if(size1.length!=3){
						message.put("return_code", "FALL");
						message.put("return_msg", "请输入正确的尺寸");
						return JSONObject.fromObject(message).toString();
					}else{
						widthDesc=size1[0];
						heightDesc=size1[1];
						longDesc=size1[2];
					}
				}
			}
			//判断文件格式
			if("".equals(jsonObject.get("fileName"))||jsonObject.get("fileName")==null){
				msg+="文件名称,";
			}else{
				fileName=(String) jsonObject.get("fileName");
				String[] name = fileName.split("\\.");
				if(!"kit".equals(name[1])){
					message.put("return_code", "FALL");
					message.put("return_msg", "上传文件格式不对，请上传kit文件");
					return JSONObject.fromObject(message).toString();
				}
			}
			//判断参数传递完整性

			MaterialHead materialHead=new MaterialHead();
			if(msg!=""){
				msg=msg.substring(0, msg.length()-1)+"为必须字段";
				message.put("return_code", "FALL");
				message.put("return_msg", msg);
				return JSONObject.fromObject(message).toString();
			}else{
				//不为空，则保存并返回响应成功
				if(series.equals("false")){
					message.put("return_code", "FALL");
					message.put("return_msg", "主题系列填写错误");
					return JSONObject.fromObject(message).toString();
				}else{
					materialHead.setSeries(series);
				}
				materialHead.setColor(color);
				materialHead.setHeightDesc(heightDesc);
				materialHead.setLongDesc(longDesc);
				materialHead.setMatkl(matkl);
				materialHead.setId("");
				materialHead.setWidthDesc(widthDesc);
				String groes="W"+widthDesc+"XH"+heightDesc+"XD"+longDesc;
				materialHead.setGroes(groes);
				materialHead.setMaktx(desc+groes);
				materialHead.setSaleFor(saleFor);
				materialHead.setTextureOfMaterial(material);//材质
				materialHead.setCreateUser(userId);
				materialHead.setMatnr(matnr);
				materialHead.setZzymss(0);
				materialHead.setSpart(spart);
				materialHead.setMaterialColor(materialColor);
				materialHead.setZzcpdj(grade);//产品等级
				materialBean.setMaterialHead(materialHead);
				SaleItemFj saleItemFj=new SaleItemFj();
				saleItemFj.setZzazdr(zzazdr);//保存安装位置中文
				saleItemFj.setProductSpace(productSpace);//保存产品空间标识
				materialBean.setSaleItemFj(saleItemFj);
				//保存数据
				SaveCustFb saveCustFb=new SaveCustFb();
				result = saveCustFb.saveBaseFB(materialBean,userId);
			}
			if(result.getSuccess()){
				InputStream input = null;//这个已经有流了
				BASE64Decoder decoder =new BASE64Decoder();
				try {
					byte[] newBase = decoder.decodeBuffer(fileBase64);//解码
					byte[] bufferUn= GZipUtils.uncompress(newBase);//解压
					input = new ByteArrayInputStream(bufferUn);
				} catch (IOException e) {
					e.printStackTrace();
					message.put("return_code", "FALL");
					message.put("return_msg", "解析文件数据失败,请检查型录!");
					return JSONObject.fromObject(message).toString();
				}
				//设置保存kit文件
				MaterialManager materialManager = SpringContextHolder.getBean("materialManager");
				MaterialFile materialFile=new MaterialFile();
				materialFile.setMaterialHead(materialHead);

				MaterialHead materialHead1 = materialManager.getOne(materialFile
						.getMaterialHead().getId(), MaterialHead.class);
				String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
				List<Map<String, Object>> queryForList = jdbcTemplate
				.queryForList(sql);

				//如果为空
				if(queryForList==null||queryForList.size()<=0){
					//请先设置KIT上传路径
					message.put("return_code", "FALL");
					message.put("return_msg", "文件上传失败,上传路径为空");
					return JSONObject.fromObject(message).toString();
				}
				Map<String, Object> map = queryForList.get(0);
				String filePath = (String) map.get("DESC_EN_US");//路径
				SysUser sysUser = new SysUser();
				sysUser.setUserName(userId);//上传人
				String kunnr = customer;
				String SerialNumber = "0000000000";
				if (!StringUtils.isEmpty(materialHead.getSerialNumber())) {
					SerialNumber = materialHead.getSerialNumber();
				} else {
					SerialNumber = materialHead.getMatnr();
				}
				materialFile.setFileType("KIT");
				String format = DateTools.getNowDateYYMMDD();
				filePath += MyFileUtil.FILE_DIR + kunnr
				+ MyFileUtil.FILE_DIR + format
				+ MyFileUtil.FILE_DIR + SerialNumber
				+ MyFileUtil.FILE_DIR + materialFile.getFileType();
				String uuid = UUID.randomUUID().toString().replace("-", "");
				String oldName = fileName;
				oldName = oldName.substring(oldName.lastIndexOf("."));
				materialFile.setUploadFileName(uuid + oldName);
				materialFile.setUploadFileNameOld(fileName);
				materialFile.setUploadFilePath(filePath);
				boolean flag = MyFileUtil.fileUpload2020(materialFile,input);
				if (flag) {
					MaterialFile obj = materialManager.save(materialFile); //哪里的
					// 失效以前的文件
					//String updateSql = "update  MATERIAL_FILE set status='X' where  id!='"
					//		+ obj.getId()
					//		+ "' and pid='"
					//		+ materialHead.getId() + "'";
					//jdbcTemplate.update(updateSql);
					// 文件类型回写到非标产品
					String type = materialFile.getFileType();
					String mType = "";
					if("KIT".equals(type)|| "PDF".equals(type)){
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
					message.put("return_code", "SUCCESS");
					message.put("return_msg", "上传数据成功");
					return JSONObject.fromObject(message).toString();
				} else {
					message.put("return_code", "FALL");
					message.put("return_msg", "文件上传失败");
					return JSONObject.fromObject(message).toString();
				}
			}else{
				message.put("return_code", "FALL");
				message.put("return_msg", "保存数据失败");
				return JSONObject.fromObject(message).toString();
			}
		}catch(Exception e){
			e.getStackTrace();
			message.put("return_code", "FALL");
			message.put("return_msg", "上传失败，请联系管理员！");
			return JSONObject.fromObject(message).toString();
		}
	}
	/**
	 * 解析系列
	 * @param series
	 * @return
	 */
	public String getSeries(String series){
		String[] a=null;
		if(series.contains(";")){
			a=series.split(";");
			Map<String,String>map=new HashMap<String, String>();
			for(String str:a){
				map.put(str.substring(0,1), str.substring(1,str.length()));
			}
			if(map.get("A")!=null){
				series=map.get("A");
			}else{
				if(map.get("B")!=null){
					series=map.get("B");
				}else{
					if(map.get("C")!=null){
						series=map.get("C");
					}else{
						series="false";
					}
				}
			}
		}else{
			series=series.substring(1,series.length());
		}
		return series;
	}
}
