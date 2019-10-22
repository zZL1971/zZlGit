package com.webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.activation.DataHandler;
import javax.jws.WebService;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Task;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import com.main.controller.MaterialController;
import com.main.dao.MaterialPriceDao;
import com.main.domain.mm.MaterialFile;
import com.main.domain.mm.MaterialHead;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.sale.SaleItem;
import com.main.manager.MaterialManager;
import com.main.util.MyFileUtil;
import com.mw.framework.bean.Message;
import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.domain.SysActCTMapping;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.manager.SysMesSendManager;
import com.mw.framework.model.CXFOrderModel;
import com.mw.framework.model.CXFPriceModel;
import com.mw.framework.model.CXFWebFileModel;
import com.mw.framework.utils.DateTools;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
@WebService(endpointInterface="com.webservice.IFileUploadWebservice",serviceName="importFile2020")
@SuppressWarnings("unchecked")
public class FileUploadWebserviceImpl implements IFileUploadWebservice{

	//private static Logger logger=Logger.getLogger(FileUploadWebserviceImpl.class);
	@Override
	public String upload(CXFOrderModel info) {
		// TODO Auto-generated method stub
		List<CXFWebFileModel> cxfwebFile = info.getFile();
		String userName = "";
		CommonManager commonManager=SpringContextHolder.getBean("commonManager");
		JdbcTemplate jdbcTemplate=SpringContextHolder.getBean("jdbcTemplate");
		MaterialManager materialManager=SpringContextHolder.getBean("materialManager");
		MaterialPriceDao materialPriceDao=SpringContextHolder.getBean("materialPriceDao");
		SysMesSendManager sysMesSendManager=SpringContextHolder.getBean("sysMesSendManager");
		TaskService taskService=SpringContextHolder.getBean("taskService");
		Map<String,String[]> params = new HashMap<String,String[]>();
		Map<String, Object> message=new HashMap<String, Object>();
		String orderCode=info.getOrderCode();
		if(!"".equals(orderCode)&&orderCode!=null){
			params.clear();
			params.put("ICEQorderCodePosex",new String[]{orderCode});
			List<SaleItem> saleitems = commonManager.queryByRange(SaleItem.class, params);
			if(!(saleitems.size()>0)){
				message.put("return_code", "FAILTURE");
				message.put("return_msg", "未找到对应订单号，请检查订单号^_^");
				return JSONArray.fromObject(message).toString();
			}
			for (SaleItem saleItem : saleitems) {
				SysActCTMapping mapping = commonManager.getOne(saleItem.getSaleHeader().getId(),
						SysActCTMapping.class);
					List<Task> tasks = taskService.createTaskQuery()
					.processInstanceId(mapping.getProcinstid())
					//.taskAssignee(userName)
					.list();
				/*if(tasks.size()<=0){
					message.put("return_code", "FAILTURE");
					message.put("return_msg", userName+"不属于 ["+orderCode+"] 受理人");
					return JSONArray.fromObject(message).toString();
				}*/
				if(tasks.size()>0){
					String taskName = tasks.get(0).getName();
					if(!"订单审绘".equals(taskName)) {
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "当前流程是"+taskName+",请在订单审绘导出数据");
						return JSONArray.fromObject(message).toString();
					}
					userName = tasks.get(0).getAssignee();
					if(userName==null&&"".equals(userName)){
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "该订单未被受理");
						return JSONArray.fromObject(message).toString();
					}
				}else{
					mapping = commonManager.getOne(saleItem.getId(),
							SysActCTMapping.class);
					tasks = taskService.createTaskQuery()
					.processInstanceId(mapping.getProcinstid()).list();
					String taskName = tasks.get(0).getName();
					if(!"订单审绘重审".equals(taskName)) {
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "当前流程是"+taskName+",无法导出数据");
						return JSONArray.fromObject(message).toString();
					}
					userName = tasks.get(0).getAssignee();
					if(userName==null&&"".equals(userName)){
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "该订单未被受理");
						return JSONArray.fromObject(message).toString();
					}
				}
				
				for (CXFWebFileModel fileModel : cxfwebFile) {
					if("PDF".equals(fileModel.getFileExtension().toUpperCase())) {
						List<Map<String, Object>> checkFile = jdbcTemplate.queryForList("SELECT MF.ID FROM SALE_ITEM SI LEFT JOIN MATERIAL_FILE MF ON SI.MATERIAL_HEAD_ID=MF.PID "
								+ "WHERE SI.PID=? AND MF.FILE_TYPE=? AND MF.STATUS IS NULL AND SI.ID !=? AND MF.UPLOAD_FILE_NAME_OLD=?", saleItem.getSaleHeader().getId(),fileModel.getFileExtension().toUpperCase(),saleItem.getId(),fileModel.getFileName());
						if(checkFile.size()>0) {
							message.put("return_code", "FAILTURE");
							message.put("return_msg", fileModel.getFileName()+"文件与该订单中相同类型文件重复");
							return JSONArray.fromObject(message).toString();
						}
					}
					MaterialFile materialFile=new MaterialFile();
					MaterialHead materialHead = materialManager.getOne(saleItem.getMaterialHeadId(), MaterialHead.class);
					//再次上传需要将此类型的文件，全部设置为无效
					jdbcTemplate.update("UPDATE MATERIAL_FILE MF SET MF.STATUS='X' WHERE MF.PID='"+materialHead.getId()+"' AND MF.FILE_TYPE='"+fileModel.getFileExtension().toUpperCase()+"'");
					materialFile.setMaterialHead(materialHead);
					DataHandler handler = fileModel.getFile();
					if("XML".equals(fileModel.getFileExtension().toUpperCase())){
						RocoImos rocoImos=new RocoImos();
						String saleFor=null;
						try {
							saleFor = rocoImos.analysisSaleFor(handler.getInputStream());
							if(!saleFor.equals(materialHead.getSaleFor())){
								//xml 类型与行项对应销售分类不匹配  此为前端数据异常 返回信息200
								message.put("return_code", "FAILTURE");
								message.put("return_msg", "xml 类型与行项对应销售分类不匹配");
								return JSONArray.fromObject(message).toString();
							}else{
								if(!"C7VvwRuidni4P9wEa3Xbfu".equals(materialHead.getId())){
									SAXReader saxReader=new SAXReader();
									Document document = saxReader.read(handler.getInputStream());
									JSONArray array = rocoImos.validationXmlText(document, jdbcTemplate);
									boolean flg=true;
									if(array!=null){
										if(array.size()>0&&!"".equals(array)){
											for (int i = 0; i < array.size(); i++) {
												JSONObject obj=array.getJSONObject(i);
												flg=obj.getBoolean("failure");
												if(!flg){
													//返回解析的XML信息 obj.getString("infoCode")
													sysMesSendManager.sendUser("<font color=#FF8C00>订单："+orderCode+"</font>"+obj.getString("infoCode"),"admin" , userName, true);
/*													message.put("return_code", "FAILTURE");
													message.put("return_msg", obj.getString("infoCode"));
													return JSONArray.fromObject(message).toString();*/
												}
											}	
										}
									}
									if(flg){
										if(!info.getOrderCode().equals(fileModel.getFileName().substring(0, fileModel.getFileName().indexOf(".")))){
											//订单行项号 与 xml订单名称不匹配  return 
											message.put("return_code", "FAILTURE");
											message.put("return_msg", "订单行项号 与 xml订单名称不匹配");
											return JSONArray.fromObject(message).toString();
										}
										List<Element> elementList = document
												.selectNodes("//XML/Order/BuilderList/Set/Pname");
										for (Element element : elementList) {
											if("TTMK1Q".equals(element.getText()) ||"TTMK2".equals(element.getText()) ||"TTMK3".equals(element.getText()) ){
												materialHead.setImosPath("IMOS_01");
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
													materialHead.setImosPath("IMOS_01");
													break;
												}
											}
										}
										if(!"IMOS_01".equals(materialHead.getImosPath())) {
											List<Map<String,Object>> list=jdbcTemplate.queryForList("select ipc.imos_path,t2.amount  from imos_path_conf ipc  left join (select ilb.imos_path, count(1) as amount  from imos_load_balance ilb  where ilb.status <> 1  and ilb.order_code not in ( select orderid from mes_imos_fail mif  where mif.orderstatus=0) group by ilb.imos_path) t2 on ipc.imos_path=t2.imos_path where ipc.status=1 and ipc.special=2 order by  NVL(t2.amount,0)");
											materialHead.setImosPath(list.get(0).get("IMOS_PATH").toString());
										}
										saleFor=rocoImos.analysisSaleFor(handler.getInputStream());
										if("3".equals(saleFor)){
											List<Element> element = document
													.selectNodes("//XML/Order/BuilderList/Set/PVarString");
											String hightDesc = materialHead.getHeightDesc();
											String widthDesc = materialHead.getWidthDesc();
											String longDesc = materialHead.getLongDesc();
											for (Element ele : element) {
												String parText = ele.getText();
												Double height = 0.0;
												Double width = 0.0;
												Double depth =0.0;
												try{
													height = resolverText(parText,"Height");
													width = resolverText(parText,"Width");
													depth = resolverText(parText,"Depth");
												}catch(StringIndexOutOfBoundsException e){
													message.put("return_code", "FAILTURE");
													message.put("return_msg", "系统无法解析XML");
													return JSONArray.fromObject(message).toString();
												}
												if(Double.parseDouble(hightDesc)!=height||Double.parseDouble(widthDesc)!=width||Double.parseDouble(longDesc)!=depth){
													// 数据错误 2020系统 长宽高 尺寸有误 return
													message.put("return_code", "FAILTURE");
													message.put("return_msg", "数据错误 2020系统 长宽高 尺寸有误");
													return JSONArray.fromObject(message).toString();
												}
											}
											materialHead.setImosPath("IMOS_04");
										}
										materialHead.setSaleFor(saleFor);
									}
								}
							}
						} catch (IOException e) {
							// TODO Auto-generated catch block
							//系统 IO异常
							e.printStackTrace();
							message.put("return_code", "FAILTURE");
							message.put("return_msg", "系统 IO异常");
							return JSONArray.fromObject(message).toString();
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							//系统文本解析异常
							e.printStackTrace();
							message.put("return_code", "FAILTURE");
							message.put("return_msg", "系统文本解析异常");
							return JSONArray.fromObject(message).toString();
						}
					}
					String validateSql=  "select mf.upload_file_name_old,si.posex" +
							 "  from sale_item si, material_file mf, material_head mh" + 
							 " where si.pid = (select sh.id" + 
							 "                   From sale_header sh" + 
							 "                  where sh.order_code = '"+orderCode.substring(orderCode.length()-4)+"')" + 
							 "   and si.material_head_id = mh.id" + 
							 "   and mh.id = mf.pid" + 
							 "   and mf.file_type = 'PDF'" + 
							 "   and mf.status is null" + 
							 " and mf.upload_file_name_old = '"+fileModel.getFileName()+"'";
					List<Map<String, Object>> li = jdbcTemplate.queryForList(validateSql);
					if(li.size()> 0 && li != null){
						 String file_name = "";
						 String posex = "";
							for(Map<String,Object> set : li){
								 file_name = set.get("upload_file_name_old").toString();
								 posex = set.get("posex").toString();
							}
							sysMesSendManager.sendUser("<font color=#FF8C00>订单："+orderCode+"</font>"+"上传的"+file_name+"文件，与行项目"+posex+"的PDF文件重复，请检查！","admin" , userName, true);
					}
					String sql = "select T.DESC_ZH_CN,t.desc_en_us from sys_data_dict t left join sys_trie_tree t2 on t.trie_id = t2.id where t2.KEY_VAL = 'FILE_PATH' AND t.KEY_VAL='FILE' ";
					List<Map<String, Object>> baseFilePath = jdbcTemplate
							.queryForList(sql);
					if(baseFilePath!=null&&baseFilePath.size()>0){
						Map<String, Object> map = baseFilePath.get(0);
						String filePath = (String) map.get("DESC_EN_US");
						String kunnr = "";
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
								+ MyFileUtil.FILE_DIR + fileModel.getFileExtension();
						String uuid = UUID.randomUUID().toString().replace("-", "");
						String oldName = fileModel.getFileName();
						oldName = oldName.substring(oldName.lastIndexOf("."));
						materialFile.setUploadFileName(uuid + oldName);
						materialFile.setUploadFileNameOld(fileModel.getFileName());
						materialFile.setUploadFilePath(filePath);
						materialFile.setFileType(fileModel.getFileExtension().toUpperCase());
						boolean flag=true;
						try {
							 flag = MyFileUtil.fileUploadBy2020(materialFile,handler.getInputStream());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							//IO 异常
							message.put("return_code", "FAILTURE");
							message.put("return_msg", "SD系统异常 请联系管理员");
							return JSONArray.fromObject(message).toString();
						}
						if(flag){
							MaterialFile mat=commonManager.save(materialFile);
							mat.setCreateUser(userName);
							mat.setUpdateUser(userName);
							commonManager.save(mat);
							String updateSql = "update  MATERIAL_FILE set status='X' where  pid='"
									+ materialHead.getId() + "' and file_type='"+mat.getFileType()+"' and id!='"+mat.getId()+"'";
							jdbcTemplate.update(updateSql);
							message.put("return_code", "SUCCESS");
							message.put("return_msg", "上传成功");
							//return JSONArray.fromObject(message).toString();
						}else{
							//上传 文件 失败
							message.put("return_code", "FAILTURE");
							message.put("return_msg", "文件传输失败，请联系管理员");
							return JSONArray.fromObject(message).toString();
						}
					}else{
						// 上传路径未配置 请联系SD系统管理员
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "上传路径未配置 请联系SD系统管理员");
						return JSONArray.fromObject(message).toString();
					}
				}
				//开始保存报价清单数据
				List<CXFPriceModel> priceList = info.getPriceList();
				//删除原始数据
				List<MaterialPrice> oldMaterialPrice = materialPriceDao.findByPid(saleItem.getId());
				if(oldMaterialPrice.size()>0){
					for (MaterialPrice materialPrice : oldMaterialPrice) {
						materialPriceDao.delete(materialPrice.getId());
					}
				}
				if(priceList !=null&&priceList.size() > 0) {
					try {
						for (CXFPriceModel price : priceList) {
							MaterialPrice materialPrice = new MaterialPrice();
							materialPrice.setAmount(Integer.parseInt((price.getAmount()!=null?price.getAmount():"0")));
							materialPrice.setType(price.getType().toString());
							materialPrice.setName(price.getName());
							materialPrice.setColor(price.getColor());
							materialPrice.setWide(Integer.parseInt((price.getWide()!=null?price.getWide():"0")));
							materialPrice.setHigh(Integer.parseInt((price.getHigh()!=null?price.getHigh():"0")));
							materialPrice.setDeep(Integer.parseInt((price.getDeep()!=null?price.getDeep():"0")));
							materialPrice.setUnit(price.getUnit());
							materialPrice.setArea(Double.parseDouble((price.getArea()!=null?price.getArea():"0")));
							materialPrice.setUnitPrice(Double.parseDouble((price.getUnitPrice()!=null?price.getUnitPrice():"0")));
							materialPrice.setRebate(Double.parseDouble((price.getRebate()!=null?price.getRebate():"0")));
							materialPrice.setTotalPrice(Double.parseDouble((price.getTotalPrice()!=null?price.getTotalPrice():"0")));
							materialPrice.setNetPrice(Double.parseDouble((price.getNetPrice()!=null?price.getNetPrice():"0")));
							materialPrice.setLine(price.getLine());
							materialPrice.setPid(saleItem.getId());
							materialPrice.setCreateUser(userName);
							materialPrice.setUpdateUser(userName);
							commonManager.save(materialPrice);
						}
					}catch(Exception e) {
						e.printStackTrace();
						message.put("return_code", "FAILTURE");
						message.put("return_msg", "系统异常 请重新上传");
						return JSONArray.fromObject(message).toString();
					}
				}else {
					message.put("return_code", "FAILTURE");
					message.put("return_msg", "报价清单 数据为空");
					return JSONArray.fromObject(message).toString();
				}
				if(message.size() > 0) {
					if("SUCCESS".equals(message.get("return_code"))) {
						return JSONArray.fromObject(message).toString();
					}
				}
			}
		}else{
			// 订单行项号为null 2020系统异常 联系管理员
			message.put("return_code", "FAILTURE");
			message.put("return_msg", "订单行项号为null 2020系统异常 联系管理员");
			return JSONArray.fromObject(message).toString();
		}
		message.put("return_code", "FAILTURE");
		message.put("return_msg", "系统异常");
		return JSONArray.fromObject(message).toString();
	}

	public Double resolverText(String parText,String format) {
		String text=null;
		text = parText.substring(parText.indexOf(format));
		text = text.substring(0, text.indexOf("|"));
		text = text.substring(text.indexOf("=")+1);
		if(text!=null&&!"".equals(text)){
			return Double.parseDouble(text);
		}
		return 0.0;
	}

}
