/**
 *
 */
package com.main.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.main.dao.CustItemDao;
import com.main.domain.cust.CustHeader;
import com.main.domain.cust.CustItem;
import com.main.domain.spm.SalePrModHeader;
import com.main.domain.spm.SalePrModItem;
import com.main.manager.SalePrModManager;
import com.main.model.spm.SPMModel;
import com.main.model.spm.SalePrModHeaderModel;
import com.main.model.spm.SalePrModItemModel;
import com.mw.framework.bean.Message;
import com.mw.framework.bean.OrderBy;
import com.mw.framework.commons.GenericController;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.utils.Arith;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.main.controller.SalePrModController.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2016-4-19
 *
 */
@Controller
@RequestMapping("/main/sd/pr/*")
public class SalePrModController extends GenericController<SalePrModHeader>{

	@Autowired
	private CommonManager commonManager;
	
	@Autowired
	private SalePrModManager salePrModManager;
	
	@Autowired
	private CustItemDao custItemDao;
	
	@Override
	protected String getAppName() {
		return "SalePrMod";
	}

	@Override
	protected String[] resultJsonExcludeField() {
		return null;
	}
	
    /**
     * 查询运算规则
     * @return
     */
	@RequestMapping(value = "/find/calCond", method = RequestMethod.GET)
    @ResponseBody
    public Message findCalCond(){
		
		List<Map<String,Object>> queryForList = jdbcTemplate.queryForList("SELECT ORDERBY,TYPE,TYPE_DSEC,PLUS_OR_MINUS,CONDITION,IS_TAKE_NUM FROM PRICE_CONDITION WHERE TYPE NOT IN ('ZR01','ZR02','ZR04') ORDER BY ORDERBY");
		if(queryForList==null||queryForList.size()<1){
			return new Message("查询不到数据！");
		}
		return new Message(queryForList);
	}
	
	/**
	 * 根据订单编号或者SAP编号查询订单抬头和订单行项目+定价条件
	 * @param baskd 订单编号
	 * @param vbeln SAP编号
	 * @return
	 */
	@RequestMapping(value = "/search/saleOrder", method = RequestMethod.POST)
    @ResponseBody
	public Message searchSaleOrder(String baskd,String vbeln){
		Message msg=null;
		Map<String,Object> returnMap=salePrModManager.searchSaleOrder(baskd,vbeln);
		SalePrModHeaderModel headerModel=(SalePrModHeaderModel) returnMap.get("header");
		Set<SalePrModItemModel> itemSet=(Set<SalePrModItemModel>) returnMap.get("items");
		if(headerModel==null||itemSet.size()<1||itemSet==null){
			msg=new Message("500","对不起，查询不到该订单！（该订单还没结束，或者不需要计算价格）");
		}else{
			msg=new Message(new SPMModel(headerModel,itemSet));
		}
		return msg;
	}
	
	/**
	 * 同步价格到SAP和保存修改价格、修改返点
	 * @param spmModel
	 * @return
	 */
	@RequestMapping("/mod/save")
	@ResponseBody
	public Message saveSalePrMod(@RequestBody SPMModel spmModel) {
		Message msg=null;
		
		SalePrModHeaderModel spmHeaderModel = spmModel.getSpmHeaderModel();
		spmHeaderModel.setTranState("FOS");
		Set<SalePrModItemModel> spmItemModelSet = spmModel.getSpmItemModelSet();
		if(spmHeaderModel!=null&&spmItemModelSet.size()>0){
			msg=salePrModManager.saveSalePrMod(spmHeaderModel,spmItemModelSet,true);
			return msg;
		}else{
			return new Message("ORD-SAVE-PRMOD-500","未知异常！");
		}
		
		
	}
	
	/**
	 * 根据修改流水的id查询流水记录
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findById", method = RequestMethod.POST)
    @ResponseBody
	public Message findSaleOrderById(String id){
		String xmlDate="<?xml version=\"1.0\" encoding=\"UTF-8\"?><data><header>"+
						"<vbeln>880025306</vbeln><bstkd>LJ91602160006</bstkd><auart>CR1</auart><vgbel>170000001</vgbel>"+
						"</header><items><item><old></old><posnr>10</posnr><mabnr>9996666669</mabnr><kwmeng>1</kwmeng>"+
						"<pr00>500</pr00><pr01>1000</pr01><pr02>100</pr02><pr03>0</pr03><pr04>0</pr04><pr05>0</pr05>"+
						"<zr01>0</zr01><zr02>0</zr02><zr03>0</zr03><zr04>0</zr04><zr05>0</zr05></item></items></data>";
		//Message msg=salePrModManager.saveLoanAmount(xmlDate);
		//importExcelXLSX();//导入返点
		SalePrModHeader spmHeader=commonManager.getOne(id, SalePrModHeader.class);
		SalePrModHeaderModel spmHeaderModel=new SalePrModHeaderModel();
		Date orderDate=spmHeader.getBstdk();//订单日期
		Date createDate=spmHeader.getCreateTime();
		org.springframework.beans.BeanUtils.copyProperties(spmHeader, spmHeaderModel,"items");
		spmHeaderModel.setBstdk(orderDate.toString());//设置订单日期
		spmHeaderModel.setCreateTime(createDate.toString());
		//处理订单行项目
		Set<SalePrModItemModel> itemModels=new HashSet<SalePrModItemModel>(); 
		Map<String, String[]> parameterMap=new HashMap<String, String[]>();
		parameterMap.put("ICEQsalePrModHeader__id", new String[]{id});//查询条件
		//排序查询
		List<SalePrModItem> itemList=commonManager.queryByRange(SalePrModItem.class, parameterMap,new OrderBy[]{new OrderBy("posnr","asc"),new OrderBy("old","asc")});
		for(SalePrModItem item:itemList){
			SalePrModItemModel itemModel=new SalePrModItemModel();
			org.springframework.beans.BeanUtils.copyProperties(item, itemModel,"salePrModHeader");
			itemModels.add(itemModel);
		}
		if(spmHeaderModel==null||itemModels.size()<1){
			return new Message("500","查询不到该订单");
		}else{
			SPMModel spmModel=new SPMModel(spmHeaderModel,itemModels);
			return new Message(spmModel);
		}
	}
	
	public void importExcelXLSX(){
		try {
			InputStream is = new FileInputStream("c:\\roco.xlsx");
			XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
			List<Object[]> update_params=new ArrayList<Object[]>();
			Set<CustItem> itemSet=new HashSet<CustItem>();
			String update_sql="UPDATE CUST_ITEM t  set t.FAN_DIAN_NAME=?,t.TOTAL=?,t.SHI_JI=?,YU_JI=?,t.SHENG_YU=?,t.ZHE_KOU=?,t.START_DATE=?,t.END_DATE=?  WHERE t.ID=?";
			 // 循环工作表Sheet
			 System.out.println("序号        客户编码         返点名称         返点总金额           实际已返总金额                  YUJI      SHENGYU        返点折扣          开始日期            过期日期      是否");
			for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
				XSSFSheet hssfSheet = xssfWorkbook.getSheetAt(numSheet);
				if (hssfSheet == null) {
					continue;
				}
				// 循环行Row
				 for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
					 XSSFRow xssfRow = hssfSheet.getRow(rowNum);
					 if (xssfRow != null) {
						String no=getHSSTextStringXLSX(xssfRow,0).toString();
						String kunnr=getHSSTextStringXLSX(xssfRow,1).toString();
						String fanDianName=getHSSTextStringXLSX(xssfRow,2).toString();
						Double total=Double.parseDouble(getHSSTextStringXLSX(xssfRow,3).toString());
						Double shiJi=Double.parseDouble(getHSSTextStringXLSX(xssfRow,4).toString());
						Double zheKou=Double.parseDouble(getHSSTextStringXLSX(xssfRow,5).toString());//返点折扣
						Date startDate=(Date) getHSSTextStringXLSX(xssfRow,6);
						Date endDate=(Date) getHSSTextStringXLSX(xssfRow,7);
						String status=getHSSTextStringXLSX(xssfRow,8).toString();
						
						//查询客户的ID
						String sql_pid="select t.id from CUST_HEADER t where t.kunnr=?";
						List<Map<String,Object>> pidList=jdbcTemplate.queryForList(sql_pid, kunnr);
						if(pidList.size()<1){
							System.out.println("没有该客户："+kunnr+"         ---->第几行："+no);
							System.out.println(1/0);
						}
						String cid=pidList.get(0).get("ID").toString();
						CustHeader custHeader=new CustHeader();
						custHeader.setId(cid);
						
						//获取某客户在财务确认环节的订单的活动折扣
						String sql_disco="select sh.shou_da_fang,nvl(sum(sip.total),0) as disco from sale_header sh"+
											" inner join  sale_item t on sh.id=t.pid"+  
											" inner join sale_item_price sip on  sip.sale_itemid=t.id"+
											" inner join act_ct_mapping m on m.id=sh.id"+
											" inner join act_ru_task rt on  m.procinstid=rt.proc_inst_id_"+
											" where  nvl(t.state_audit,'C')!='QX' and sip.type='PR04' and RT.TASK_DEF_KEY_ IN ('usertask4','usertask_finance') and sh.shou_da_fang=?  group by sh.shou_da_fang";
						List<Map<String,Object>> discoList=jdbcTemplate.queryForList(sql_disco, kunnr);
						Double yuji=0.0;
						if(discoList.size()>0){
							Map<String,Object> discoMap=discoList.get(0);
							yuji=Double.parseDouble(discoMap.get("DISCO").toString());
						}
						Double temp=Arith.sub(total,shiJi);
						Double shengyu=Arith.sub(temp,yuji);
						
						System.out.print(no+"   "+kunnr+"         "+fanDianName+"         "+total+"          "+shiJi+"       "+yuji+"        "+shengyu+"     "+zheKou+"         "+startDate+"         "+endDate+"     "+status);
						if(status.equals("1")){//更新状态为启用状态的
							String SQL_item="select  t.id from CUST_ITEM t where t.kunnr='"+kunnr+"' and t.status='1'";///获取要更新行的id
							List<Map<String,Object>> mapListItem=jdbcTemplate.queryForList(SQL_item);
							if(mapListItem.size()>0){//有就更新
								Map<String,Object> mapId=mapListItem.get(0);
								String id=mapId.get("ID").toString();//要更新的行项目id
								update_params.add(new Object[]{fanDianName,total,shiJi,yuji,shengyu,zheKou,startDate,endDate,id});
								System.out.println("---》更新");
							}else{//没有就插入
								CustItem item=new CustItem();
								item.setKunnr(kunnr);
								item.setStartDate(startDate);
								item.setEndDate(endDate);
								item.setZheKou(zheKou);
								item.setTotal(total);
								item.setStatus(status);
								item.setShiJi(shiJi);
								item.setYuJi(yuji);
								item.setShengYu(shiJi);
								item.setFanDianName(fanDianName);
								item.setCustHeader(custHeader);
								itemSet.add(item);
								System.out.println("---》插入");
							}
						}else{//不启用的就插入
								CustItem item=new CustItem();
								item.setKunnr(kunnr);
								item.setStartDate(startDate);
								item.setEndDate(endDate);
								item.setZheKou(zheKou);
								item.setTotal(total);
								item.setStatus(status);
								item.setShiJi(shiJi);
								item.setYuJi(yuji);
								item.setShengYu(shiJi);
								item.setFanDianName(fanDianName);
								item.setCustHeader(custHeader);
								itemSet.add(item);
								System.out.println("---》插入");
						}
						
					 }
				 }
			}
			custItemDao.save(itemSet);
			jdbcTemplate.batchUpdate(update_sql, update_params);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	public Object getHSSTextStringXLSX(XSSFRow row, int colNum) {  	   
		XSSFCell cell = row.getCell(colNum);
	      if (null != cell) {
	        switch (cell.getCellType()) {
	        case XSSFCell.CELL_TYPE_NUMERIC:
	        	if (DateUtil.isCellDateFormatted(cell)) {   
	                return cell.getDateCellValue();
	            } else {   
	                DecimalFormat df = new DecimalFormat("#.##");//转换成整型
	  	          	if(colNum==5||colNum==7){
	  	        	  return NumberToTextConverter.toText(cell.getNumericCellValue());  
	  	          	}
	  	          	return df.format(cell.getNumericCellValue());
	            }  
	        case XSSFCell.CELL_TYPE_STRING:
	          return cell.getStringCellValue().trim();
	        case XSSFCell.CELL_TYPE_BLANK: // 空值
	          return "";
	        case XSSFCell.CELL_TYPE_ERROR: // 故障
	          return "";
	        case XSSFCell.CELL_TYPE_FORMULA:
		      return "";
	        default:
	          return "";
	        }
	      } else {
	        return "";
	      }
	}
	
}
