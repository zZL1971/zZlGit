package com.main.manager.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.tree.DefaultElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.main.bean.MaterialBean;
import com.main.dao.CustItemDao;
import com.main.dao.MaterialPriceDao;
import com.main.dao.PriceConditionDao;
import com.main.dao.SaleHeaderDao;
import com.main.dao.SaleItemDao;
import com.main.dao.SaleItemPriceDao;
import com.main.dao.SaleLogisticsDao;
import com.main.dao.SalePrModHeaderDao;
import com.main.domain.cust.CustItem;
import com.main.domain.mm.MaterialPrice;
import com.main.domain.mm.PriceCondition;
import com.main.domain.sale.SaleHeader;
import com.main.domain.sale.SaleItem;
import com.main.domain.sale.SaleItemPrice;
import com.main.domain.sale.SaleLogistics;
import com.main.domain.spm.SalePrModHeader;
import com.main.domain.spm.SalePrModItem;
import com.main.manager.MaterialManager;
import com.main.manager.SalePrModManager;
import com.main.model.spm.SalePrModHeaderModel;
import com.main.model.spm.SalePrModItemModel;
import com.mw.framework.bean.Message;
import com.mw.framework.manager.impl.CommonManagerImpl;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.sap.jco3.SAPConnect;
import com.mw.framework.utils.Arith;
import com.mw.framework.utils.BeanUtils;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.NumberUtils;
import com.mw.framework.utils.XMLUtil;
import com.mw.framework.utils.ZStringUtils;
import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoTable;

import roco.price.plugin.EditPriceDealbase;

@Service("salePrModManager")
@Transactional
public class SalePrModManagerImpl extends CommonManagerImpl implements SalePrModManager {
	
	@Autowired
	protected JdbcTemplate jdbcTemplate;
	@Autowired
	private SalePrModHeaderDao spmDao;
	@Autowired
	private PriceConditionDao priceConditionDao;
	@Autowired
	private SaleItemDao saleItemDao;
	@Autowired
	private MaterialPriceDao materialPriceDao;
	@Autowired
	private SaleHeaderDao saleHeaderDao;
	@Autowired
	private CustItemDao custItemDao;
	@Autowired
	private SaleItemPriceDao saleItemPriceDao;
	@Autowired
	private SaleLogisticsDao saleLogisticsDao;
	@Autowired
	private MaterialManager materialManager;
	/**
	 * 根据订单编号或者SAP编号查询销售订单
	 */
	@Override
	public Map<String, Object> searchSaleOrder(String bstkd, String vbeln) {
		// TODO Auto-generated method stub
		Map<String, Object> orderMap=new HashMap<String, Object>();
		//修改销售订单价钱抬头
		SalePrModHeaderModel spmHeader=new SalePrModHeaderModel();
		//销售订单行项目价格明细
		Set<SalePrModItemModel> smpItemSet=new HashSet<SalePrModItemModel>();
		List<Object> params = new ArrayList<Object>();
		StringBuffer sale_header_sql = new StringBuffer("select t.* from sale_cust_view t where 1=1 ");
		StringBuffer sb=new StringBuffer();
		if(!ZStringUtils.isEmpty(bstkd)){
			sb.append(" and t.order_code= ?");
			params.add(bstkd);
		}
		if(!ZStringUtils.isEmpty(vbeln)){
			sb.append(" and t.sap_order_code= ?");
			params.add(vbeln);
		}
		Map<String, SimpleDateFormat> formats = new HashMap<String, SimpleDateFormat>();
        formats.put("orderDate", new SimpleDateFormat(DateTools.defaultFormat));
        //List<Map<String, Object>> queryForListaa=jdbcTemplate.queryForList(sale_header_sql+sb.toString(), params.toArray());
		List<Map<String, Object>> queryForList = jdbcTemplate.query(sale_header_sql+sb.toString(), params.toArray()
				,new MapRowMapper(true, formats));
		if(queryForList.size()>0){
			
			Map<String, Object> saleHeader=queryForList.get(0);//销售单抬头
			String id=saleHeader.get("id").toString();//销售单抬头Id
			spmHeader.setId(saleHeader.get("id").toString());
			
			Object vbeln_=saleHeader.get("sapOrderCode");// SAP编号
			spmHeader.setVbeln(vbeln_==null?"":vbeln_.toString());
			//订单编号
			Object bstkd_=saleHeader.get("orderCode");
			spmHeader.setBstkd(bstkd_==null?"":bstkd_.toString());
			//订单类型
			Object auart=saleHeader.get("orderType");
			spmHeader.setAuart(auart==null?"":auart.toString());
			//订单日期
			Object bstdk=saleHeader.get("orderDate");
			spmHeader.setBstdk(bstdk==null?"":bstdk.toString());
			//售达方
			Object kunnr=saleHeader.get("shouDaFang");
			spmHeader.setKunnr(kunnr==null?"":kunnr.toString());
			//售达方名称
			Object name1=saleHeader.get("kunnrName1");
			spmHeader.setName1(name1==null?"":name1.toString());
			//订单总金额
			double netwr=saleHeader.get("orderTotal")==null?0:Double.parseDouble(saleHeader.get("orderTotal").toString());
			spmHeader.setNetwr(netwr);
			//原来的订单总金额
			spmHeader.setoNetwr(netwr);
			//付款条件
			Object zterm=saleHeader.get("fuFuanCond");
			spmHeader.setZterm(zterm==null?"":zterm.toString());
			//付款金额
			double fuFuanMoney=saleHeader.get("fuFuanMoney")==null?0:Double.parseDouble(saleHeader.get("fuFuanMoney").toString());
			spmHeader.setFuFuanMoney(fuFuanMoney);
			//原来的付款金额
			spmHeader.setoFuFuanMoney(fuFuanMoney);
			//支付方式
			Object zzysfg=saleHeader.get("payType");
			spmHeader.setZzysfg(zzysfg==null?"":zzysfg.toString());
			//客户名称
			Object zzname=saleHeader.get("name1");
			spmHeader.setZzname(zzname==null?"":zzname.toString());
			//联系方式
			Object zzphon=saleHeader.get("tel");
			spmHeader.setZzphon(zzphon==null?"":zzphon.toString());
			//拒绝原因
			Object abgru=saleHeader.get("abgru");
			spmHeader.setAbgru(abgru==null?"":abgru.toString());
			//借贷金额
			double loanAmount=ZStringUtils.isEmpty(saleHeader.get("loanAmount"))?0:Double.parseDouble(saleHeader.get("loanAmount").toString());
			spmHeader.setLoanAmount(loanAmount);
			//借贷编码vgbel
			Object vgbel=saleHeader.get("vgbel");
			spmHeader.setVgbel(vgbel==null?"":vgbel.toString());
			
			String sale_item_pri_sql="select * from sale_prmod_item_flow_view  where 1=1 and pid='"+id+"' and act_id='endevent1' order by posex";
			List<Map<String, Object>>  itemPriList= jdbcTemplate.queryForList(sale_item_pri_sql);
			for(Map<String, Object> pri:itemPriList){
				SalePrModItemModel smpItem=new SalePrModItemModel();
				smpItem.setId(pri.get("id").toString());
				// 行项目
				Object posnr=pri.get("posex");
				smpItem.setPosnr(posnr==null?"":posnr.toString());
				// 物料编码
				Object mabnr=pri.get("matnr");
				smpItem.setMabnr(mabnr==null?"":mabnr.toString());
				// 订单数量
				Object kwmeng=pri.get("amount");
				smpItem.setKwmeng(kwmeng==null?0:Integer.parseInt(kwmeng.toString()));
				// 物料描述
				Object arktx=pri.get("maktx");
				smpItem.setArktx(arktx==null?"":arktx.toString());
				//是否被取消
				String stateAudit=pri.get("state_audit")==null?"":pri.get("state_audit").toString();
				smpItem.setStateAudit(stateAudit);
				if(stateAudit.equals("QX")&&pri.get("abgru")==null){//行项目取消并且拒绝原因为空，默认拒绝原因默认是56
					smpItem.setAbgru("56");
					smpItem.setOldAbgru("56");//老的拒绝原因
				}else{
					String abgru_=pri.get("abgru")==null?"":pri.get("abgru").toString();
					smpItem.setAbgru(abgru_);
					smpItem.setOldAbgru(abgru_);//老的拒绝原因
				}
				smpItem.setPr00(Double.parseDouble(pri.get("total_price").toString()));// 付款金额(计算后的金额)
				smpItem.setPr01(Double.parseDouble(pri.get("pr01").toString()));// 商品原价(含税)
				smpItem.setPr02(Double.parseDouble(pri.get("pr02").toString()));// 赠送（活动）
				smpItem.setPr03(Double.parseDouble( pri.get("pr03").toString()));// 产品折扣
				smpItem.setPr04(Double.parseDouble( pri.get("pr04").toString()));// 活动折扣
				smpItem.setPr05(Double.parseDouble( pri.get("pr05").toString()));// 产品免费（统计用）
				smpItem.setZr01(Double.parseDouble(pri.get("zr01").toString()));// 运输费(含税)
				smpItem.setZr02(Double.parseDouble(pri.get("zr02").toString()));// 返修费(含税)
				smpItem.setZr03(Double.parseDouble(pri.get("zr03").toString()));// 安装服务费(含税)
				smpItem.setZr04(Double.parseDouble(pri.get("zr04").toString()));// 设计费(含税)
				smpItem.setZr05(Double.parseDouble(pri.get("zr05").toString()));// 订单变更管理费(含税)
				smpItem.setZr07(Double.parseDouble(pri.get("zr07").toString()));// 订单价格调整(含税)
				smpItemSet.add(smpItem);
			}
		}
		orderMap.put("header", spmHeader);
		orderMap.put("items", smpItemSet);
		
		return orderMap;
	}

	/**
	 * 更新价格和返点金额
	 * 查询出以前的价格->当前的价格+以前的价格->保存到记录表
	 */
	@Override
	public Message saveSalePrMod(SalePrModHeaderModel spmHeaderModel,Set<SalePrModItemModel> spmItemModelSet,boolean toSap) {
		// TODO Auto-generated method stub
		
		double fuFuanMoney=spmHeaderModel.getFuFuanMoney();//付款总金额
		double netwr=spmHeaderModel.getNetwr();//订单金额
		String headerId=spmHeaderModel.getId();//订单Id
		String headerAbgru=spmHeaderModel.getAbgru();//抬头拒绝原因
		String orderDate=spmHeaderModel.getBstdk();//订单日期
		String kunnr=spmHeaderModel.getKunnr();//客户编码
		//将订单抬头从model转为domain
		SalePrModHeader spmHeader=new SalePrModHeader();
		org.springframework.beans.BeanUtils.copyProperties(spmHeaderModel, spmHeader,"id");
		spmHeader.setBstdk(DateTools.strToDate(orderDate, "yyyy-MM-dd"));
		//将价格明细从model转为domain
		Set<SalePrModItem> spmItemSet=new HashSet<SalePrModItem>();
		for(SalePrModItemModel smpItemModel:spmItemModelSet){
			SalePrModItem spmItem=new SalePrModItem();
			org.springframework.beans.BeanUtils.copyProperties(smpItemModel, spmItem);
			spmItemSet.add(spmItem);
		}
		
		//保存记录
		Set<SalePrModItem> itemOldSet=new HashSet<SalePrModItem>();//存放价格改变前的
		double oDiscoTotal=0;
		for(SalePrModItem item:spmItemSet){
			String sale_item_pri_sql="select * from sale_prmod_item_view  where 1=1 and id='"+item.getId()+"' order by posex";
			Map<String, Object> oItemMap=jdbcTemplate.queryForMap(sale_item_pri_sql);//获取价格改变前的值
			String oItemId=oItemMap.get("id").toString();
			String pr04_total_sql="select t.total from sale_item_price t where  t.type='PR04' and  t.sale_itemid='"+oItemId+"'";
			Map<String,Object> prObj=jdbcTemplate.queryForMap(pr04_total_sql);
			oDiscoTotal=oDiscoTotal+Double.parseDouble(prObj.get("TOTAL").toString());//活动折扣总额
			SalePrModItem oSpmItem=new SalePrModItem(
					oItemMap.get("posex").toString(),// 行项目 
					oItemMap.get("matnr").toString(),// 物料编码 
					Integer.parseInt(oItemMap.get("amount").toString()), // 订单数量
					oItemMap.get("maktx").toString(), // 物料描述
					oItemMap.get("abgru")!=null?oItemMap.get("abgru").toString():"", // 拒绝原因
							oItemMap.get("state_audit")!=null?oItemMap.get("state_audit").toString():"",//是否取消
					Double.parseDouble(oItemMap.get("total_price").toString()), // 付款金额(计算后的金额)
					Double.parseDouble(oItemMap.get("pr01").toString()), // 商品原价(含税)
					Double.parseDouble(oItemMap.get("pr02").toString()), // 赠送（活动）
					Double.parseDouble(oItemMap.get("pr03").toString()), // 产品折扣
					Double.parseDouble(oItemMap.get("pr04").toString()), // 活动折扣
					Double.parseDouble(oItemMap.get("pr05").toString()), // 产品免费（统计用）
					Double.parseDouble(oItemMap.get("zr01").toString()), // 运输费(含税)
					Double.parseDouble(oItemMap.get("zr02").toString()), // 返修费(含税)
					Double.parseDouble(oItemMap.get("zr03").toString()), // 安装服务费(含税)
					Double.parseDouble(oItemMap.get("zr04").toString()), // 设计费(含税)
					Double.parseDouble(oItemMap.get("zr05").toString()),// 订单变更管理费(含税)
					Double.parseDouble(oItemMap.get("zr06").toString()),//客服支持
					Double.parseDouble(oItemMap.get("zr07").toString()),//订单价格调整(含税)
					"1");//如果是原来的就为“1”，空就是改价格后的
			oSpmItem.setSalePrModHeader(spmHeader);//设置外键
			itemOldSet.add(oSpmItem);
			item.setId(null);//id自动生成，设置id为空
			item.setSalePrModHeader(spmHeader);//设置外键
		}
		spmItemSet.addAll(itemOldSet);//合并改前改后行项目
		spmHeader.setItems(spmItemSet);
			
		
		Map<String,Object> custItemMap=new HashMap<String, Object>();
		custItemMap.put("node", "end");//表示正处于结束环节
		custItemMap.put("kunnr", kunnr);//客户编码
		custItemMap.put("oDiscoTotal", oDiscoTotal);//旧折扣金额
		custItemMap.put("orderDate", orderDate);//订单日期
		
		//检验将要更新价格和返点金额是否会成功
		Message msg=upDatePriIsOk(spmItemModelSet,custItemMap);
		if(msg!=null){//不成功
			String str=msg.getErrorCode()+msg.getErrorMsg();
			Set<String> mSet=new HashSet<String>();
			mSet.add(str);
			if(!toSap){//SAP传过来的
				return new Message(msg.getErrorCode(),msg.getErrorMsg());
			}
			return new Message(mSet);
		}
		
		//同步到SAP
		Set<Message> sapMsgSet=null;
		if(toSap){//判读是否要传到SAP
			System.out.println("同步到SAP");
			Map<String,Object> sapReturnMap=tranToSap(spmHeaderModel,spmItemModelSet);
			boolean sapSuccess=(Boolean) sapReturnMap.get("sapSuccess");
			sapMsgSet=(Set<Message>) sapReturnMap.get("msgSet");
			if(!sapSuccess){//传输到SAP不成功
				return new Message(sapMsgSet);
			}
		}
				
		//更新
		System.out.println("同步到SAP成功，操作本地数据库！");
		String update_header="update sale_header set fu_fuan_money=?,order_total=?,abgru=?  where id=?";//更新 sale_header(fuFuanMoney 付款总金额 netwr 订单总金额 abgru 拒绝原因 ) 根据 id
		jdbcTemplate.update(update_header, new Object[]{fuFuanMoney,netwr,headerAbgru,headerId});
		
		//保存记录
		spmDao.save(spmHeader);
		
		//更新价格
		upDatePri(spmItemModelSet,custItemMap);//更新涉及价格相关的表和返点表
		
		return new Message(sapMsgSet);
	}

	/**
	 * 更新涉及价格相关的表和返点表(CUST_ITEM)：
	 * sale_header(fuFuanMoney 付款总金额) 根据 id
	 * sale_item(totalPrice 付款行项金额 )  根据 id
	 * sale_item_price(conditionValue 运算值  subtotal 小计  total 总计 ) 根据 sale_itemid 和type
	 * CUST_ITEM 返点表 ： total（待返点总金额）-yuJi（预计已返点金额）（确认报价~财务确认环节）-shiJi（实际已返点金额）（结束环节）=shengYu（预计剩余金额）
	 * @param itemSet
	 */
	public Message upDatePri(Set<SalePrModItemModel> itemModelSet,Map<String,Object> custItemMap){		
		//更新价格
		List<Object[]> update_item_params=new ArrayList<Object[]>();//批量更新sale_item的参数
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SELECT * FROM PRICE_CONDITION ORDER BY ORDERBY");//计算顺序
		double discoTotal=0;//活动折扣金额总计
		for (SalePrModItemModel itemModel : itemModelSet) {
			List<Object[]> update_pri_params=new ArrayList<Object[]>();//批量更新sale_item_price的参数
			String itemId = itemModel.getId();
			List<Map<String,Object>> typeMapList=calcuPrSubtotal(itemModel,queryForList);
			for(Map<String,Object> typeMap:typeMapList){
				String type=typeMap.get("type").toString();//价格类型 -> pr01... zr01...
				double conditionValue=Double.parseDouble(typeMap.get("value").toString());
				double subtotal=Double.parseDouble(typeMap.get("subtotal").toString());
				double total=Double.parseDouble(typeMap.get("total").toString());
				if(type.equals("PR04")){//活动折扣要更新返点 CUST_ITEM
					discoTotal=discoTotal+total;
				}
				update_pri_params.add(new Object[]{conditionValue,subtotal,total,itemId,type});
			}
			String update_pr_item = "update sale_item_price t  set t.condition_value=?,t.subtotal=?,t.total=? where t.sale_itemid=? and t.TYPE=?";
			jdbcTemplate.batchUpdate(update_pr_item, update_pri_params);//更新sale_item_price(conditionValue 运算值  subtotal 小计  total 总计 )
			update_item_params.add(new Object[]{itemModel.getPr00(),itemModel.getStateAudit(),itemModel.getAbgru(),itemId});//sale_item(totalPrice 付款行项金额 )  根据 id
		}
		String update_item="update sale_item set total_price=?,state_audit=?,abgru=?  where  id=?";
		jdbcTemplate.batchUpdate(update_item, update_item_params);//sale_item(totalPrice 付款行项金额 state_audit状态  abgru 拒绝原因 )  根据 id
		
		
		double oDiscoTotal=Double.parseDouble(custItemMap.get("oDiscoTotal").toString());//原来折扣
		//如果custItemMap 不为空则更新返点金额
		if(custItemMap!=null&&oDiscoTotal!=discoTotal){
			String node=custItemMap.get("node").toString();//环节
			String kunnr=custItemMap.get("kunnr").toString();//客户编码
			
			String orderDate=custItemMap.get("orderDate").toString();//订单日期
			double differ=discoTotal-oDiscoTotal;//差额
			String cust_item_sql="select t.* from CUST_ITEM  t where t.kunnr='"+kunnr+"'  and   to_date('"+orderDate+"','yyyy-mm-dd') between t.start_date and t.end_date  and t.status='1'";
			List<Map<String,Object>> custMapList=jdbcTemplate.queryForList(cust_item_sql);
			if(custMapList.size()<1){
				System.out.println("客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");
				return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");		
			}
			Map<String,Object> custMap= custMapList.get(0);
			String cId=custMap.get("ID").toString();
			double total=Double.parseDouble(custMap.get("TOTAL").toString());
			double yuJi=Double.parseDouble(custMap.get("YU_JI").toString());
			double shiJi=Double.parseDouble(custMap.get("SHI_JI").toString());
			double shengYu=0;
			if(node.equals("end")){//结束环节 修改实际返点金额
				shiJi=shiJi+differ;
				shengYu=total-yuJi-shiJi;	
				String update_cust_item="update CUST_ITEM set SHI_JI="+shiJi+",SHENG_YU="+shengYu+"  where  id='"+cId+"'";
				jdbcTemplate.update(update_cust_item);
			}else if(node.equals("sure")){//确认报价环节~财务确认环节
				yuJi=yuJi+differ;
				shengYu=total-yuJi-shiJi;
				String update_cust_item="update CUST_ITEM set YU_JI="+yuJi+",SHENG_YU="+shengYu+"  where  id='"+cId+"'";
				jdbcTemplate.update(update_cust_item);
			}
		}
		
		return null;
	}
	
	/**
	 * 检验将要更新价格和返点金额是否会成功
	 * @param itemModelSet
	 * @param custItemMap
	 * @return
	 */
	public Message upDatePriIsOk(Set<SalePrModItemModel> itemModelSet,Map<String,Object> custItemMap){		
		//更新价格
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SELECT * FROM PRICE_CONDITION ORDER BY ORDERBY");//计算顺序
		double discoTotal=0;//活动折扣金额总计
		for (SalePrModItemModel itemModel : itemModelSet) {
			List<Map<String,Object>> typeMapList=calcuPrSubtotal(itemModel,queryForList);
			for(Map<String,Object> typeMap:typeMapList){
				String type=typeMap.get("type").toString();//价格类型 -> pr01... zr01...
				double total=Double.parseDouble(typeMap.get("total").toString());
				if(type.equals("PR04")){//活动折扣要更新返点 CUST_ITEM
					discoTotal=discoTotal+total;
				}
			}
		}
		
		double oDiscoTotal=Double.parseDouble(custItemMap.get("oDiscoTotal").toString());//原来折扣
		//更新返点金额
		if(custItemMap!=null&&discoTotal!=oDiscoTotal){
			String node=custItemMap.get("node").toString();//环节
			String kunnr=custItemMap.get("kunnr").toString();//客户编码	
			String orderDate=custItemMap.get("orderDate").toString();//订单日期
			double differ=discoTotal-oDiscoTotal;//差额
			String cust_item_sql="select t.* from CUST_ITEM  t where t.kunnr='"+kunnr+"'  and   to_date('"+orderDate+"','yyyy-mm-dd') between t.start_date and t.end_date  and t.status='1'";
			List<Map<String,Object>> custMapList=jdbcTemplate.queryForList(cust_item_sql);
			if(custMapList.size()<1){
				System.out.println("客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");
				return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");		
			}
			Map<String,Object> custMap=custMapList.get(0);
			double total=Double.parseDouble(custMap.get("TOTAL").toString());
			double yuJi=Double.parseDouble(custMap.get("YU_JI").toString());
			double shiJi=Double.parseDouble(custMap.get("SHI_JI").toString());
			double shengYu=0;
			if(node.equals("end")){//结束环节 修改实际返点金额
				shiJi=shiJi+differ;
				shengYu=total-yuJi-shiJi;
				if(shiJi<0){
					System.out.println("实际待反金额小于0！");
					return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，实际待反金额小于0！");				
				}
				if(shengYu<0){
					System.out.println("剩余待反金额小于0！");
					return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，剩余待反金额小于0！");
				}
			}else if(node.equals("sure")){//确认报价环节~财务确认环节
				yuJi=yuJi+differ;
				shengYu=total-yuJi-shiJi;
				if(yuJi<0){
					return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，预计待反金额小于0！");
				}
				if(shengYu<0){
					return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，剩余待反金额小于0！");
				}
			}
		}
	  
		return null;
	}
	
	/**
	 * 物料的各个价格类型的价格
	 * @param prModItemModel
	 * @param queryForList
	 * @return
	 */
	@Override
	public List<Map<String,Object>> calcuPrSubtotal(SalePrModItemModel prModItemModel,List<Map<String, Object>> queryForList){
		List<Map<String,Object>> listMap=new ArrayList<Map<String,Object>>();
		double total = 0;// 物料付款金额
		double subTotal = 0;
		int snum_ = prModItemModel.getKwmeng();// 下单数量
		for (Map<String, Object> map2 : queryForList) {
			String type = (String) map2.get("TYPE");// 价格类型
			String pms = (String) map2.get("PLUS_OR_MINUS");// 加减
			String cdn = (String) map2.get("CONDITION");// 运算符
			String itn = (String) map2.get("IS_TAKE_NUM");// 乘数量
			Object obj = BeanUtils.getValue(prModItemModel, type.toLowerCase());// 根据字段获取值
			if (obj != null) {
				double pr_ = (Double) obj;
				// 先运算
				if ("1".equals(cdn)) {// 加
					subTotal = pr_;
				} else if ("2".equals(cdn)) {// 减
					subTotal = pr_;
				} else if ("3".equals(cdn)) {// 乘
					subTotal = Arith.mul(total,pr_);
				} else if ("4".equals(cdn)) {// 除
					subTotal = Arith.div(total,pr_);
				}
				double update_subtotal = subTotal;
				// 乘数量
				if ("1".equals(itn)) {
					subTotal = Arith.mul(subTotal,snum_);
				} else {
					subTotal = subTotal * 1;
				}
				double update_total = subTotal;
				Map<String,Object> priMap=new HashMap<String, Object>();
				DecimalFormat df = new DecimalFormat("######0.00");
				priMap.put("type", type);
				priMap.put("value",df.format(pr_));
				priMap.put("subtotal", df.format(update_subtotal));
				priMap.put("total", df.format(update_total));
				listMap.add(priMap);
				// 用运算结果加减
				if ("1".equals(pms)) {
					total = Arith.add(total,subTotal);
				} else {
					total = Arith.sub(total,subTotal);
				}
			}
		}
		return listMap;
	}

	/**
	 * 同步到SAP
	 * @param spmHeaderModel
	 * @param spmItemModelSet
	 * @return
	 */
	public Map<String,Object> tranToSap(SalePrModHeaderModel spmHeaderModel,Set<SalePrModItemModel> spmItemModelSet){
		Boolean sapSuccess=null;//同步SAP是否成功的标记
		String vbeln = spmHeaderModel.getVbeln();// SAP编码
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SELECT * FROM PRICE_CONDITION ORDER BY ORDERBY");//计算顺序
		JCoDestination connect = SAPConnect.getConnect();
		JCoFunction functionPri;// 价格创建
		Set<String> successMsgSet=new HashSet<String>();
		Set<String> errorMsgSet=new HashSet<String>();
		try {
			functionPri = connect.getRepository().getFunction("ZRFC_SD_CHANGE_CANCEL");// SAP创建接口
			JCoTable imModTable = functionPri.getTableParameterList().getTable("IM_T_PRICE");// 同步修改价格的到SAP的表
			JCoTable imReTable = functionPri.getTableParameterList().getTable("IT_ZSDT007");// 同步取消的到SAP  IT_ZSDT007
			
			if(ZStringUtils.isNotEmpty(spmHeaderModel.getAbgru())&&!spmHeaderModel.getAbgru().equals("51")){//如果抬头有拒绝原因，那么表示整个订单都取消
				List<SaleLogistics> saleLogisticsList = saleLogisticsDao.findBySaleHeaderId(spmHeaderModel.getId());
				if(saleLogisticsList.size()>0) {
					for (SaleLogistics saleLogistics : saleLogisticsList) {
						imReTable.appendRow();
						imReTable.setValue("VBELN", saleLogistics.getSapCode());// SAP编码
						imReTable.setValue("ABGRU", spmHeaderModel.getAbgru());//拒绝原因
					}
				}
			}else{
				for (SalePrModItemModel itemModel : spmItemModelSet) {
					// TODO 此处需要修改
					//更新状态
					String nowState=itemModel.getAbgru()==null?"":itemModel.getAbgru();
					String oldState=itemModel.getOldAbgru()==null?"":itemModel.getOldAbgru();
					SaleItem saleItem = saleItemDao.findOne(itemModel.getId());
					SaleHeader saleHeader = saleItem.getSaleHeader();
					
					if(!nowState.equals(oldState)){//行项目状态被修改过
						imReTable.appendRow();
						imReTable.setValue("VBELN", saleItem.getSapCode());// SAP编码
						imReTable.setValue("POSNR", saleItem.getSapCodePosex());// 行项目
						imReTable.setValue("ABGRU", itemModel.getAbgru());//拒绝原因
					}
					
					//被修改价钱				
					List<Map<String,Object>> prTypeMapList=calcuPrSubtotal(itemModel, queryForList);//物料的各个价格类型的价格
					for(Map<String,Object> prTypeMap:prTypeMapList){
						/*if("OR4".equals(saleHeader.getOrderType())){
							continue;
						}*/
						String type=prTypeMap.get("type").toString();//价格类型 -> pr01... zr01...
						
						imModTable.appendRow();
						imModTable.setValue("VBELN", saleItem.getSapCode());// SAP编码
						imModTable.setValue("POSNR", saleItem.getSapCodePosex());// 行项
						imModTable.setValue("MATNR", itemModel.getMabnr());//物料编码
						imModTable.setValue("KSCHL", type);// 价格类型pr01... zr01...
						imModTable.setValue("KBETR", prTypeMap.get("subtotal").toString());// 价格类型小计（还没有乘以数量）
						imModTable.setValue("WAERS", "CNY");// 币别码
						imModTable.setValue("KPEIN", "1");// 条件定价单位
					}	
				}
			}
			
			int row=imModTable.getNumRows();
			if(row>0||imReTable.getNumRows()>0){	
				functionPri.execute(connect);//SAP---执行
				JCoTable exTReturn = functionPri.getTableParameterList().getTable("ET_RETURN");
				if (exTReturn.getNumRows() > 0) {
					exTReturn.firstRow();
					for (int i = 0; i < exTReturn.getNumRows(); i++, exTReturn.nextRow()) {
                        Object type = exTReturn.getValue("TYPE");
                        Object message = exTReturn.getValue("MESSAGE");
                        Object logNo = exTReturn.getValue("LOG_NO");
                        Object messageV1 = exTReturn.getValue("MESSAGE_V1");
                        if (type != null && "S".equals(type.toString())) {// 成功
                        	System.out.println("成功");
                        	if(sapSuccess==null){
                        		sapSuccess=true;
                        	}
                            System.out.println("logNo:"+logNo);
                        	System.out.println(type+"-"+messageV1+":"+message);
                            successMsgSet.add(message.toString());
                        } else {// 失败信息
                        	sapSuccess=false;
                        	System.out.println("失败");
                        	System.out.println("logNo:"+logNo);
                        	System.out.println(type+"-"+messageV1+":"+message);
                        	errorMsgSet.add(message.toString());
                        }
					}
				}
			}
		} catch (JCoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Map<String,Object> returnMap=new HashMap<String, Object>();
		if(sapSuccess){
			returnMap.put("sapSuccess", sapSuccess);
			returnMap.put("msgSet", successMsgSet);
			return returnMap;
		}else{
			returnMap.put("sapSuccess", sapSuccess);
			returnMap.put("msgSet", errorMsgSet);
			return returnMap;
		}
	}
	
	/*
	 * 借贷项
	 */
	public Message saveLoanAmount(String xml){
		System.out.println(xml);
		try {
			Document doc  = DocumentHelper.parseText(xml);
			
			Element element = (Element) doc.selectSingleNode("//data/header");
			//header
			//SalePrModHeaderModel header = XMLUtil.xmlStrToBean(element.asXML(),SalePrModHeaderModel.class);
			Map<String, Object> headerXMLMap=XMLUtil.xmlStrToMap(element.asXML());
			String bstkd=headerXMLMap.get("bstkd").toString();//订单号
			String vbeln=headerXMLMap.get("vbeln").toString();//SAP编号
			String auart=headerXMLMap.get("auart").toString();//借贷类型
			String vgbel=headerXMLMap.get("vgbel").toString();//借贷编号
//			
//			if(ZStringUtils.isEmpty(vbeln)||ZStringUtils.isEmpty(bstkd)){
//				return new Message("ORD-SPM-500","前端接单平台->订单号不能为空。");
//			}
			Map<String,Object> resultMap=searchSaleOrder(bstkd,vbeln);//根据订单编号或者SAP编号查询销售订单
			
			//抬头
			SalePrModHeaderModel spmHeaderModel=(SalePrModHeaderModel) resultMap.get("header");
			if(ZStringUtils.isEmpty(spmHeaderModel)||spmHeaderModel==null||spmHeaderModel.getId()==null){
				return new Message("ORD-SPM-500","前端接单平台->没有该订单:"+vbeln+"，不能产生借贷项。");
			}
			String headerId=spmHeaderModel.getId();
			String orderDate=spmHeaderModel.getBstdk();//订单日期bstdk
			spmHeaderModel.setVgbel(vgbel);//借贷编号	
			System.out.println("对应的数据库ID:"+spmHeaderModel.getId());
			//SAP修改的行项目
			List<DefaultElement> selectNodes = doc.selectNodes("//data/items/item");
						
			Set<SalePrModItem> xmlItemSet=new HashSet<SalePrModItem>();
			
			Double loanAmount=0.00;//借贷总金额
			Double pr04Total=0.00;//活动总金额（返点）
			
			Map<String,SalePrModItemModel> addMap=new HashMap<String, SalePrModItemModel>();
			Map<String,SalePrModItemModel> oldMap=new HashMap<String, SalePrModItemModel>();
			Set<SalePrModItemModel> modelSet=new HashSet<SalePrModItemModel>();
			for (DefaultElement defaultElement : selectNodes) {
				System.out.println(defaultElement.asXML());
				SalePrModItemModel  prXMLItemModel= XMLUtil.xmlStrToBean(defaultElement.asXML(),SalePrModItemModel.class);
				String mabnr=prXMLItemModel.getMabnr();//借贷项物料编码
				//借贷项的通用码对应的物料描述
				if(ZStringUtils.isEmpty(mabnr)){
					prXMLItemModel.setArktx("借贷物料");//物料描述
				}else if("69999999999".equals(mabnr)){
					prXMLItemModel.setArktx("产成品借贷项通用码");
				}else if("69999999998".equals(mabnr)){
					prXMLItemModel.setArktx("半成品借贷项通用码");
				}else if("69999999997".equals(mabnr)){
					prXMLItemModel.setArktx("材料借贷项通用码");
				}else if("69999999996".equals(mabnr)){
					prXMLItemModel.setArktx("销售道具借贷项通用码");
				}else if("69999999995".equals(mabnr)){
					prXMLItemModel.setArktx("样品借贷项通用码");
				}else if("69999999994".equals(mabnr)){
					prXMLItemModel.setArktx("费用借贷项通用码");
				}
				
				if("1".equals(prXMLItemModel.getOld())){//改变的原值 old为1；
					oldMap.put(prXMLItemModel.getPosnr(), prXMLItemModel);
				}
				modelSet.add(prXMLItemModel);
			}
			
			for (SalePrModItemModel itemModel : modelSet) {
				Double pr00=itemModel.getPr00();//行项目借贷金额
				Double pr04_=itemModel.getPr04();//行项目返点金额
				if(ZStringUtils.isEmpty(itemModel.getOld())){//新增是old为空；	
					
					loanAmount=loanAmount+pr00;//累加借贷金额
					pr04Total=pr04Total+pr04_;//累加活动金额（返点）
					
					SalePrModItem spmItem=new SalePrModItem();
					org.springframework.beans.BeanUtils.copyProperties(itemModel, spmItem);
					if(auart.equals("CR1")){//贷项 要变负
						spmItem.setPr00(0-spmItem.getPr00());
					}
					xmlItemSet.add(spmItem);	
				}else if("1".equals(itemModel.getOld())){//改变前的原值 old为1；
//					SalePrModItem spmItem=new SalePrModItem();
//					org.springframework.beans.BeanUtils.copyProperties(itemModel, spmItem);
//					if(auart.equals("CR1")){//贷项 要变负
//						spmItem.setPr00(0-spmItem.getPr00());
//					}
//					xmlItemSet.add(spmItem);
				}else if("2".equals(itemModel.getOld())){//没有变化的 old为2
					
				}else if("3".equals(itemModel.getOld())){//取消 old为3；  要减去借贷总金额 和活动金额（返点）
					SalePrModItem spmItem=new SalePrModItem();
					org.springframework.beans.BeanUtils.copyProperties(itemModel, spmItem);
					spmItem.setPr00(0-pr00);//取消为负
					spmItem.setOld("3");
					xmlItemSet.add(spmItem);
					loanAmount=loanAmount-pr00;//借贷总金额
					pr04Total=pr04Total-pr04_;//活动金额（返点）
				}else if("4".equals(itemModel.getOld())){//改变 old为4
					SalePrModItemModel oldItemModel=oldMap.get(itemModel.getPosnr());
					Double pr00Differ=pr00-oldItemModel.getPr00();//差值
					Double pr04_differ=pr04_-oldItemModel.getPr04();
					loanAmount=loanAmount+pr00Differ;//借贷总金额
					pr04Total=pr04Total+pr04_differ;//活动金额（返点）
					
					SalePrModItem spmItem=new SalePrModItem();
					org.springframework.beans.BeanUtils.copyProperties(itemModel, spmItem);
					if(auart.equals("CR1")){//贷项 要变负
						spmItem.setPr00(0-spmItem.getPr00());
					}
					xmlItemSet.add(spmItem);
					
					SalePrModItem oldSpmItem=new SalePrModItem();
					org.springframework.beans.BeanUtils.copyProperties(oldItemModel, oldSpmItem);
					if(auart.equals("CR1")){//贷项 要变负
						oldSpmItem.setPr00(0-oldSpmItem.getPr00());
					}
					xmlItemSet.add(oldSpmItem);
				}		
			}
			
			//更新返点
			if(pr04Total!=0.00||pr04Total!=0){
				String kunnr=spmHeaderModel.getKunnr();//客户编码
				//根据客户编码查询有效时间内的返点
				String cust_item_sql="select t.* from CUST_ITEM  t where t.kunnr='"+kunnr+"'  and   to_date('"+orderDate+"','yyyy-mm-dd') between t.start_date and t.end_date  and t.status='1'";
				System.out.println(cust_item_sql);
				List<Map<String,Object>> custMapList=jdbcTemplate.queryForList(cust_item_sql);
				if(custMapList.size()<1){
					System.out.println("客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");
					return new Message("ORD-UPDATE-PRI","客户："+kunnr+"在日期："+orderDate+"内，没有有效返点金额。");		
				}
				Map<String,Object> custMap= custMapList.get(0);
				String cId=custMap.get("ID").toString();
				double total=Double.parseDouble(custMap.get("TOTAL").toString());
				double yuJi=Double.parseDouble(custMap.get("YU_JI").toString());
				double shiJi=Double.parseDouble(custMap.get("SHI_JI").toString());
				double shengYu=0;
				if(auart.equals("CR1")){//贷项
					shiJi=shiJi-pr04Total;	
				}else if(auart.equals("DR1")){//借项
					yuJi=yuJi+pr04Total;
				}
				shengYu=total-yuJi-shiJi;//剩余返点=总返点-预计返点-实际返点
				String update_cust_item="update CUST_ITEM set SHI_JI="+shiJi+",SHENG_YU="+shengYu+"  where  id='"+cId+"'";
				jdbcTemplate.update(update_cust_item);//更新返点
			}
			
			
			//计算抬头 新订单总金额和新付款金额
			String zterm=spmHeaderModel.getZterm();//付款条件
			double ztermValue=0;
			if(zterm.equals("1")){
				ztermValue=1;//全额付款
			}else if(zterm.equals("2")){
				ztermValue=0;//信贷付款
			}else if(zterm.equals("3")){
				ztermValue=0.5;//预付款50%
			}else if(zterm.equals("4")){
				ztermValue=0.3;//预付款30%
			}
			
			Double orderTotal=spmHeaderModel.getNetwr();//原订单总金额
			Double totalAmount=spmHeaderModel.getLoanAmount();//原借贷总金额
			if(auart.equals("CR1")){//贷项
				orderTotal=orderTotal-loanAmount;//贷时    新订单总金额=原订单总金额-信贷金额
				spmHeaderModel.setNetwr(orderTotal);
				totalAmount=totalAmount-loanAmount;		
			}else if(auart.equals("DR1")){//借项
				orderTotal=orderTotal+loanAmount;//新订单总金额=原订单总金额+信贷金额
				spmHeaderModel.setNetwr(orderTotal);
				totalAmount=totalAmount+loanAmount;
			}
			spmHeaderModel.setLoanAmount(totalAmount);//设置记录的借贷金额
			Double fuFuanMoney=Arith.mul(ztermValue,orderTotal);//付款金额=付款条件*新订单总金额
			
			//更新Sale_header表的订单总金额和付款金额借贷金额 和借贷项编码vgbel
			String update_header="update sale_header set fu_fuan_money=?,order_total=?,loan_amount=?,vgbel=?  where id=?";//更新 sale_header(fuFuanMoney 付款总金额 abgru 拒绝原因 ) 根据 id
			jdbcTemplate.update(update_header, new Object[]{fuFuanMoney,orderTotal,totalAmount,vgbel,headerId});
			
			spmHeaderModel.setFuFuanMoney(fuFuanMoney);
			spmHeaderModel.setTranState("SAP");
			
			SalePrModHeader spmHeader=new SalePrModHeader();
			org.springframework.beans.BeanUtils.copyProperties(spmHeaderModel, spmHeader,"id","createUser","createTime");
			
			for(SalePrModItem spmItem:xmlItemSet){//设置外键
				spmItem.setSalePrModHeader(spmHeader);
			}
			spmHeader.setoNetwr(spmHeader.getNetwr()-totalAmount);//原来的订单金额=当前的订单金额-借贷项金额
			spmHeader.setBstdk(DateTools.strToDate(orderDate, "yyyy-MM-dd"));
			spmHeader.setItems(xmlItemSet);
			spmDao.save(spmHeader);
		} catch (Exception e) {
			e.printStackTrace();
			return new Message("ORD-SPM-500","前端接单平台："+e.getLocalizedMessage());
		}
		return  new Message("前端接单平台：价格修改成功");
	}
	
	/**
	 * 保存SAP传来的修改价格
	 */
	public Message savePrModXML(String xml){
		try {
			Document doc  = DocumentHelper.parseText(xml);
			
			Element element = (Element) doc.selectSingleNode("//data/header");
			//header
			//SalePrModHeaderModel header = XMLUtil.xmlStrToBean(element.asXML(),SalePrModHeaderModel.class);
			Map<String, Object> headerXMLMap=XMLUtil.xmlStrToMap(element.asXML());
			String bstkd=headerXMLMap.get("bstkd").toString();//订单号
			String vbeln=headerXMLMap.get("vbeln").toString();//SAP编号
			
			//过掉特殊订单
			if("794A15007".equals(bstkd.trim())){
				return  new Message("前端接单平台：特殊订单，价格修改成功");
			}
			
			Map<String,Object> resultMap=searchSaleOrder(bstkd,vbeln);//根据订单编号或者SAP编号查询销售订单
			SalePrModHeaderModel spmHeaderModel=(SalePrModHeaderModel) resultMap.get("header");//原抬头
			Set<SalePrModItemModel> smpItemSet=(Set<SalePrModItemModel>) resultMap.get("items");//原行项目
			if(spmHeaderModel==null){
				return new Message("ORD-SPM-500","前端接单平台->没有该订单:"+vbeln+"，不能修改价格。");
			}else if(spmHeaderModel!=null&&smpItemSet.size()<1){
				String task_status_sql="select c.id,c.order_code,a.name_  from act_ru_task a,act_ct_mapping b,sale_header c where a.proc_inst_id_=b.procinstid and b.id=c.id and c.id='"+spmHeaderModel.getId()+"'";
				Map<String, Object> taskStatusMap=jdbcTemplate.queryForMap(task_status_sql);//查询订单处于某个环节
				String name_=taskStatusMap.get("NAME_").toString();
				return new Message("ORD-SPM-500","前端接单平台->订单:"+vbeln+"正处于："+name_+"环节，尚未结束，不能修改价格。");
			}
			System.out.println("对应的数据库ID:"+spmHeaderModel.getId());
			//SAP修改的行项目
			List<DefaultElement> selectNodes = doc.selectNodes("//data/items/item");
			
			List<Map<String, Object>> queryForList = jdbcTemplate.queryForList("SELECT * FROM PRICE_CONDITION ORDER BY ORDERBY");//计算顺序
			
			Set<SalePrModItemModel> xmlItemModelSet=new HashSet<SalePrModItemModel>();
			
			for (DefaultElement defaultElement : selectNodes) {
				System.out.println(defaultElement.asXML());
				SalePrModItemModel  prXMLItemModel= XMLUtil.xmlStrToBean(defaultElement.asXML(),SalePrModItemModel.class);
				System.out.println(prXMLItemModel.toString());
				String posnr=prXMLItemModel.getPosnr();//SAP修改的行项目
				String mabnr=prXMLItemModel.getMabnr();//SAP修改的物料编码
				
				for(SalePrModItemModel itemModel:smpItemSet){
					String oPosnr=itemModel.getPosnr();//原行项目
					String oMabnr=itemModel.getMabnr();//原物料编码
					if(posnr.equals(oPosnr)&&mabnr.equals(oMabnr)){//SAP传来的行项目与数据库的行项目相对应，则1、计算折扣额度，2、更新数据库的行项目定价条件
						//1、将折扣额度装换成折扣
						Map<String, Object> discoMap=calcuPrDisco(prXMLItemModel,queryForList);
						Object p03=discoMap.get("PR03");
						double pr03=Double.parseDouble(p03==null?"0":p03.toString());//产品折扣
						Object p04=discoMap.get("PR04");
						double pr04=Double.parseDouble(p04==null?"0":p04.toString());//活动折扣
						System.out.println("产品折扣:"+pr03);
						System.out.println("活动折扣:"+pr04);
							
						//2、将本地的定价条件更新为SAP传过来的
						itemModel.setPr00(prXMLItemModel.getPr00());
						itemModel.setPr01(prXMLItemModel.getPr01());
						itemModel.setPr02(prXMLItemModel.getPr02());
						itemModel.setPr03(pr03);//产品折扣
						itemModel.setPr04(pr04);//活动折扣
						itemModel.setPr05(prXMLItemModel.getPr05());
						itemModel.setZr01(prXMLItemModel.getZr01());
						itemModel.setZr02(prXMLItemModel.getZr02());
						itemModel.setZr03(prXMLItemModel.getZr03());
						itemModel.setZr04(prXMLItemModel.getZr04());
						itemModel.setZr05(prXMLItemModel.getZr05());
						
						xmlItemModelSet.add(itemModel);
					}
				}
				
				//数据库没有对应行项目和物料
				if(xmlItemModelSet.size()<1){
					return new Message("ORD-SPM-500","前端接单平台->没有该物料:"+posnr+":"+mabnr+"，不能修改价格。");
				}	
				
			}	
			
			
			double netwr_=0;//订单总金额
			for(SalePrModItemModel newItemModel:smpItemSet){
				netwr_=netwr_+newItemModel.getPr00();//折后金额总金额(订单金额金额)
			}
			
			String zterm=spmHeaderModel.getZterm();//付款条件
			double ztermValue=0;
			if(zterm.equals("1")){
				ztermValue=1;//全额付款
			}else if(zterm.equals("2")){
				ztermValue=0;//信贷付款
			}else if(zterm.equals("3")){
				ztermValue=0.5;//预付款50%
			}else if(zterm.equals("4")){
				ztermValue=0.3;//预付款30%
			}
			
			spmHeaderModel.setNetwr(netwr_);//订单金额金额
			spmHeaderModel.setFuFuanMoney(Arith.mul(ztermValue,netwr_));//付款金额=付款条件*订单金额
			spmHeaderModel.setTranState("SAP");
			Message msg=saveSalePrMod(spmHeaderModel,xmlItemModelSet,false);//修改价格，但是不需要同步到SAP
			if(msg!=null){
				return new Message("ORD-SPM-500","前端接单平台->"+msg.getErrorMsg());
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new Message("ORD-SPM-500","前端接单平台："+e.getLocalizedMessage());
		}
		return  new Message("前端接单平台：价格修改成功");
	}
	
	/**
	 * 根据折扣额度反推折扣（现在只有产品折扣pr03     活动折扣pr04）
	 * @param prModItemModel
	 * @param queryForList
	 * @return
	 */
	public Map<String,Object> calcuPrDisco(SalePrModItemModel prModItemModel,List<Map<String, Object>> queryForList){
		double total = 0;// 物料付款金额
		double subTotal = 0;
		int snum_ = prModItemModel.getKwmeng();// 下单数量
		Map<String,Object> priMap=new HashMap<String, Object>();
		for (Map<String, Object> map2 : queryForList) {
			double discount=0;//折扣
			
			String type = (String) map2.get("TYPE");// 价格类型
			String pms = (String) map2.get("PLUS_OR_MINUS");// 加减
			String cdn = (String) map2.get("CONDITION");// 运算符
			String itn = (String) map2.get("IS_TAKE_NUM");// 乘数量
			Object obj = BeanUtils.getValue(prModItemModel, type.toLowerCase());// 根据字段获取值
			if (obj != null) {
				double pr_ = (Double) obj;
				// 先运算
				if ("1".equals(cdn)) {// 加
					subTotal = pr_;
				} else if ("2".equals(cdn)) {// 减
					subTotal = pr_;
				} else if ("3".equals(cdn)) {// 乘(反推折扣)如 100元  的折扣额度为20  那么折扣为 20/100=0.2 （现在只有产品折扣pr03     活动折扣pr04）
					subTotal= pr_;
					discount= Arith.div(pr_,total);// 折扣额度/当前价=折扣
				} else if ("4".equals(cdn)) {//除（暂时没有用到。。。）
					subTotal = pr_;
					discount= Arith.mul(total, pr_);
				}
				// 乘数量
				if ("1".equals(itn)) {
					subTotal = Arith.mul(subTotal,snum_);
				} else {
					subTotal = subTotal * 1;
				}
			
				priMap.put(type, discount);
				
				// 用运算结果加减
				if ("1".equals(pms)) {
					total = Arith.add(total,subTotal);
				} else {
					total = Arith.sub(total,subTotal);
				}
			}
		}
		return priMap;
	}

	@Override
	public Message addItemPrice(String saleId) {
		try {
			List<SaleItem> saleItems = saleItemDao.findItemsByPid(saleId);
			if(saleItems.size()==0){
				return new Message("PR-ITEMPRRICE-500","该订单没有行项目");
			}
			List<PriceCondition> priceConditions = priceConditionDao.queryPriceCondition();
			//客户折扣 审价流程节点创建 不可能存在 QX（取消）订单 
			/*String cust_item_sql="select t.zhe_kou,h.id,h.order_date  from CUST_ITEM t,sale_header h where t.kunnr=h.shou_da_fang and nvl(h.order_status,'C')!='QX' and nvl(h.order_date,sysdate) between t.start_date and t.end_date and t.status = '1' and h.id='"+saleId+"'";
			List<Map<String,Object>> custMap=jdbcTemplate.queryForList(cust_item_sql);*/
			SaleHeader saleHeader = saleHeaderDao.findOne(saleId);
			List<CustItem> custMap = custItemDao.findItemsByCode1(saleHeader.getShouDaFang(),saleHeader.getOrderDate());
			String saleSql="select order_type from sale_header h where h.id='"+saleId+"'";
			List<Map<String,Object>> saleMap=jdbcTemplate.queryForList(saleSql);
			
			String orderType=saleMap.get(0).get("ORDER_TYPE").toString();
			//订单
			for (SaleItem item : saleItems) {
				Set<SaleItemPrice> itemPrices = item.getSaleItemPrices();
				if("1".equals(item.getIsStandard())&&itemPrices.size()>0) {
					continue;
				}
				if(!"E".equals(item.getStateAudit())&&!"QX".equals(item.getStateAudit())) {
					Set<SaleItemPrice> saleItemPrices = new HashSet<SaleItemPrice>();
					if(itemPrices.size()>0) {
						if(item.getId()!=null&&!"".equals(item.getId())) {
							jdbcTemplate.update("delete sale_item_price sp where sp.sale_itemid='"+item.getId()+"'");
						}
					}
					double price=Arith.div(item.getTotalPrice(), item.getAmount());
					/*if(item.getSaleItemPrices().size()>0){
					Set<SaleItemPrice> ItemPrices = item.getSaleItemPrices();
					for (SaleItemPrice oldPrices : ItemPrices) {
						if("PR04".equals(oldPrices.getType())) {
							if(custMap.size()>0) {//如果该经销商有折扣明细 则重新计算折扣 删除之前的折扣明细
								Double totalPrice = oldPrices.getTotal();
								CustItem oldCustItem = custMap.get(0);
								oldCustItem.setShengYu(NumberUtils.add(oldCustItem
								         .getShengYu(), totalPrice));
								oldCustItem.setYuJi(NumberUtils.subtract(oldCustItem.getYuJi(),(Double)totalPrice));
								custItemDao.save(oldCustItem);
							}
							
						}
						oldPrices.setSaleItem(null);
					}
					try {
						if(ItemPrices!=null&&ItemPrices.size()>0) {
							Set<SaleItemPrice> set =new HashSet<SaleItemPrice>();
							item.setSaleItemPrices(set);
							//saleItemPriceDao.delete(ItemPrices);
						}
					}catch(Exception e) {
						System.out.println(item.getOrderCodePosex());
					}
					
				}*/
					for (PriceCondition priceCondition : priceConditions) {
						SaleItemPrice saleItemPrice = new SaleItemPrice();
						saleItemPrice.setType(priceCondition.getType());
						saleItemPrice.setPlusOrMinus(priceCondition.getPlusOrMinus());
						saleItemPrice.setIsTakeNum(priceCondition.getIsTakeNum());
						saleItemPrice.setOrderby(priceCondition.getOrderby());
						saleItemPrice.setCondition(priceCondition.getCondition());
						saleItemPrice.setConditionValue(priceCondition.getConditionValue());
						saleItemPrice.setSubtotal(priceCondition.getSubtotal());
						saleItemPrice.setTotal(priceCondition.getTotal());
						if(!"OR4".equals(orderType)){
							//原价格
							if ("1".equals(item.getIsStandard())&& "PR01".equals(priceCondition.getType())) {
								double total=price;
								if("1".equals(priceCondition.getIsTakeNum())){
									total=Arith.mul(item.getAmount(),price);
								}
								item.setStateAudit("E");
								saleItemPrice.setConditionValue(price);
								saleItemPrice.setSubtotal(price);
								saleItemPrice.setTotal(total);
								//产品折扣  OR4道具订单不参与折扣
							}else if ("0".equals(item.getIsStandard())&& "PR01".equals(priceCondition.getType())) {
								List<MaterialPrice> materialPrices = materialPriceDao.findByPid(item.getId());
								double fbprice=0.0;
								for (MaterialPrice materialPrice : materialPrices) {
									if(materialPrice.getTotalPrice() != null && 0.0!=materialPrice.getTotalPrice()) {
										fbprice += materialPrice.getTotalPrice();
									}
								}
								double total = fbprice;
								if("1".equals(priceCondition.getIsTakeNum())){
									total=Arith.mul(item.getAmount(),total);
								}
								price = total;
								item.setTotalPrice(total);
								saleItemPrice.setConditionValue(fbprice);
								saleItemPrice.setSubtotal(fbprice);
								saleItemPrice.setTotal(total);
							}else if (!"OR4".equals(orderType)&& "PR04".equals(priceCondition.getType())&&!"102999995".equals(item.getMatnr())){
								if(custMap.size()>0){
									if(!StringUtils.isEmpty(custMap.get(0).getZheKou())){
										Map<String,Object> custItemMap = new HashMap<String, Object>();
										custItemMap.put("zheKou", new BigDecimal(custMap.get(0).getZheKou()));
										custItemMap.put("shengYu", new BigDecimal(custMap.get(0).getShengYu()));
										custItemMap.put("yuJi", new BigDecimal(custMap.get(0).getYuJi()));
										custItemMap.put("shiJi", new BigDecimal(custMap.get(0).getShiJi()));
										String data = JSONObject.toJSONString(custItemMap);
										EditPriceDealbase dealbase = new EditPriceDealbase(data,false);
										dealbase.setOrderPrice(new BigDecimal(price));
										dealbase.calcudate();
										double priceZK=Double.parseDouble(custMap.get(0).getZheKou().toString());
										double total=(dealbase.getCalcudateAmount()!=null)?dealbase.getCalcudateAmount().doubleValue():0.0;
										/*if("1".equals(priceCondition.getIsTakeNum())){
											total=Arith.mul(item.getAmount(),total);
										}*/
										saleItemPrice.setConditionValue(priceZK);
										saleItemPrice.setSubtotal(total);
										saleItemPrice.setTotal(total);	
										item.setTotalPrice(price-total);
										// 获取售达方折扣
										/*SaleHeader saleHeader = saleHeaderDao.getOne(saleId);
										List<CustItem> custItems =  custItemDao.findItemsByCode1(saleHeader.getShouDaFang(),saleHeader.getOrderDate());*/
										CustItem custItem = custMap.get(0);
										custItem.setShengYu(dealbase.getSurplusAmount().doubleValue());
										custItem.setYuJi(dealbase.getPredictAmount().doubleValue());
										custItemDao.save(custItem);
									}
								}
							}
						}else {
							//原价格
							if ("1".equals(item.getIsStandard())&& "PR01".equals(priceCondition.getType())) {
								double total=price;
								if("1".equals(priceCondition.getIsTakeNum())){
									total=Arith.mul(item.getAmount(),price);
								}
								item.setStateAudit("E");
								saleItemPrice.setConditionValue(price);
								saleItemPrice.setSubtotal(price);
								saleItemPrice.setTotal(total);
								//产品折扣  OR4道具订单不参与折扣
							}else if ("0".equals(item.getIsStandard())&& "PR01".equals(priceCondition.getType())) {
								List<MaterialPrice> materialPrices = materialPriceDao.findByPid(item.getId());
								double fbprice=0.0;
								for (MaterialPrice materialPrice : materialPrices) {
									if(materialPrice.getTotalPrice() != null && 0.0!=materialPrice.getTotalPrice()) {
										fbprice += materialPrice.getTotalPrice();
									}
								}
								double total = fbprice;
								price = total;
								if("1".equals(priceCondition.getIsTakeNum())){
									total=Arith.mul(item.getAmount(),total);
								}
								item.setTotalPrice(total);
								saleItemPrice.setConditionValue(fbprice);
								saleItemPrice.setSubtotal(fbprice);
								saleItemPrice.setTotal(total);
							}else if (!"OR4".equals(orderType)&& "PR04".equals(priceCondition.getType())){
								if(custMap.size()>0){
									if(!StringUtils.isEmpty(custMap.get(0).getZheKou())){
										double priceZK=Double.parseDouble(custMap.get(0).getZheKou().toString());
										double total=Arith.mul(price,priceZK);
										if("1".equals(priceCondition.getIsTakeNum())){
											total=Arith.mul(item.getAmount(),total);
										}
										saleItemPrice.setConditionValue(priceZK);
										saleItemPrice.setSubtotal(price*priceZK);
										saleItemPrice.setTotal(total);	
										item.setTotalPrice(item.getTotalPrice()-total);
										// 获取售达方折扣
										/*SaleHeader saleHeader = saleHeaderDao.getOne(saleId);
									List<CustItem> custItems =  custItemDao.findItemsByCode1(saleHeader.getShouDaFang(),saleHeader.getOrderDate());*/
										CustItem custItem = custMap.get(0);
										if (custItem.getShengYu() != null && total != 0.0
												&& total <= custItem.getShengYu()) {
											custItem.setShengYu(NumberUtils.subtract(custItem
													.getShengYu(), total));
											custItem.setYuJi(NumberUtils.add(custItem.getYuJi(),(Double)total));
											custItemDao.save(custItem);
										}
									}
								}
							}else if ("PR05".equals(priceCondition.getType())) {
								List<MaterialPrice> materialPrices = materialPriceDao.findByPid(item.getId());
								double fbprice=0.0;
								for (MaterialPrice materialPrice : materialPrices) {
									if(materialPrice.getTotalPrice() != null && 0.0!=materialPrice.getTotalPrice()) {
										fbprice += materialPrice.getTotalPrice();
									}
								}
								double total = fbprice;
								price = total;
								if("1".equals(priceCondition.getIsTakeNum())){
									total=Arith.mul(item.getAmount(),total);
								}
								item.setTotalPrice(total);
								saleItemPrice.setConditionValue(fbprice);
								saleItemPrice.setSubtotal(fbprice);
								saleItemPrice.setTotal(total);
							}
							item.setTotalPrice(0.0);
						}
						saleItemPrice.setSaleItem(item);
						saleItemPrices.add(saleItemPrice);
					}
					item.setSaleItemPrices(saleItemPrices);
					/*MaterialBean materialBean =new MaterialBean();
					List<SaleItemPrice> SaleItemPrice = new ArrayList<SaleItemPrice>(saleItemPrices);
					materialBean.setSaleItemPrices(SaleItemPrice);
					materialBean.setShouDaFang(saleHeader.getShouDaFang());
					materialBean.setSaleItem(item);
					materialManager.updateSaleItem(materialBean);*/
					saleItemDao.save(item);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new Message("PR-ITEMPRRICE-500","订单行项目销售价格信息添加失败！"+e.getMessage());
		}
		return new Message("订单行项目销售价格信息添加成功");
	}

	/**
	 * 财务确认更新返点
	 * @param id  订单id
	 */
	public void updateCust(String orderCode){
    	String SQL_PR04="select sh.shou_da_fang as kunnr,to_char(sh.order_date,'YYYY-MM-DD') as orderDate ,nvl(sum(sip.total),0) as disco from sale_header sh"+
    	 				"  inner join  sale_item t on sh.id=t.pid  inner join sale_item_price sip on  sip.sale_itemid=t.id"+
    	 				"  where order_code=? and nvl(t.state_audit,'C')!='QX'  and sip.type='PR04' group by  sh.shou_da_fang,sh.order_date";
    	List<Map<String,Object>> custMapList=jdbcTemplate.queryForList(SQL_PR04, orderCode);
    	if(custMapList.size()>0){
    		Map<String,Object> custMap=custMapList.get(0);
    		BigDecimal  disco=(BigDecimal)custMap.get("DISCO");
        	double dis=disco.doubleValue();//该订单的返点金额
        	String kunnr=custMap.get("KUNNR").toString();//客户编码
        	String orderDate=custMap.get("ORDERDATE").toString();//订单日期
    		String cust_item_sql="select t.* from CUST_ITEM  t where t.kunnr='"+kunnr+"'  and   to_date('"+orderDate+"','yyyy-mm-dd') between t.start_date and t.end_date  and t.status='1'";
    		List<Map<String,Object>> custItemMapList=jdbcTemplate.queryForList(cust_item_sql);
    		if(custItemMapList.size()>0){
    			Map<String,Object> custItemMap=custItemMapList.get(0);
        		String cId=custItemMap.get("ID").toString();
        		//double total=Double.parseDouble(custItemMap.get("TOTAL").toString());
        		//double yuJi=Double.parseDouble(custItemMap.get("YU_JI").toString());
        		double shiJi=Double.parseDouble(custItemMap.get("SHI_JI").toString());
        		//double shengYu=0;
        		//yuJi=Arith.sub(yuJi, dis);//预计待反金额-返点
        		shiJi=Arith.add(shiJi, dis);;//实际
        		//double temp=Arith.sub(total, yuJi);
	    		//shengYu=Arith.sub(temp, shiJi);
        		String update_cust_item="update CUST_ITEM set SHI_JI="+shiJi+" where  id='"+cId+"'";
        		//System.out.println(update_cust_item);
        		jdbcTemplate.update(update_cust_item);
    		}	
    	}
    	
     }
	
	/**
	 * 取消订单时要减去返点金额
	 * @param orderCode
	 * @param itemNo
	 */
	@Override
	public void cancelOrder(String bgCode){
		
		String SQL_PR04="select sh.shou_da_fang as kunnr,to_char(sh.order_date,'YYYY-MM-DD') as orderDate ,nvl(sum(sip.total),0) as disco from sale_header sh"+
         " inner join  sale_item t on sh.id=t.pid  inner join sale_item_price sip on  sip.sale_itemid=t.id"+
         " where  sh.id in ("+
         " SELECT m.id FROM act_ru_task RT, act_ct_mapping M "+
          " WHERE M.PROCINSTID = RT.PROC_INST_ID_ AND M.ID = SH.ID"+
          " AND (RT.PROC_DEF_ID_ LIKE 'mainProductQuotation%' or RT.PROC_DEF_ID_ LIKE 'myProcess%')"+
          " )"+//AND RT.TASK_DEF_KEY_ IN ('usertask_finance','usertask4')
          " AND t.id in ( select bi.sale_item_id from bg_header bh,bg_item bi where bh.id=bi.pid and bh.bg_code=? )"+ 
         " and sip.type='PR04' group by  sh.shou_da_fang,sh.order_date";
		
		List<Map<String, Object>> custMapList = jdbcTemplate.queryForList(SQL_PR04,bgCode);
		if(custMapList.size()>0){
    		Map<String,Object> custMap=custMapList.get(0);
    		BigDecimal  disco=(BigDecimal)custMap.get("DISCO");
        	double dis=disco.doubleValue();//要减的返点金额
        	String kunnr=custMap.get("KUNNR").toString();//客户编码
        	String orderDate=custMap.get("ORDERDATE").toString();//订单日期
    		String cust_item_sql="select t.* from CUST_ITEM  t where t.kunnr='"+kunnr+"'  and   to_date('"+orderDate+"','yyyy-mm-dd') between t.start_date and t.end_date  and t.status='1'";
    		List<Map<String,Object>> custItemMapList=jdbcTemplate.queryForList(cust_item_sql);
    		if(custItemMapList.size()>0){
    			Map<String,Object> custItemMap=custItemMapList.get(0);
        		String cId=custItemMap.get("ID").toString();
        		Map<String,Object> custItemMapPrice = new HashMap<String, Object>();
        		custItemMapPrice.put("zheKou", new BigDecimal(custItemMap.get("ZHE_KOU").toString()));
        		custItemMapPrice.put("shengYu", new BigDecimal(custItemMap.get("SHENG_YU").toString()));
        		custItemMapPrice.put("yuJi", new BigDecimal(custItemMap.get("YU_JI").toString()));
        		custItemMapPrice.put("shiJi", new BigDecimal(custItemMap.get("SHI_JI").toString()));
        		EditPriceDealbase dealbase = new EditPriceDealbase(JSONObject.toJSONString(custItemMapPrice),true);
        		dealbase.setOrderPrice(new BigDecimal(dis*-1));
        		dealbase.uncalcudate();
	    		String update_cust_item="update CUST_ITEM set YU_JI="+dealbase.getPredictAmount()+",SHENG_YU="+dealbase.getSurplusAmount()+"  where  id='"+cId+"'";
	    		jdbcTemplate.update(update_cust_item);
    		}	
    	}
		
	}

	@Override
	public void reCalculate(List<SaleItemPrice> saleItemPriceList ,String kunnr ,Date orderDate,String status) {
		// TODO Auto-generated method stub
		List<CustItem> custItemList = custItemDao.findItemsByCode1(kunnr, orderDate);
		if(custItemList.size()<=0) {
			return;
		}
		CustItem custItem = custItemList.get(0);
		for (SaleItemPrice saleItemPrice : saleItemPriceList) {
			String type = saleItemPrice.getType();
			if(!"PR04".equals(type)) {
				continue;
			}
			Double total = saleItemPrice.getTotal();
			custItem.setShengYu(Arith.add(custItem.getShengYu(), total));
			custItem.setYuJi(Arith.sub(custItem.getYuJi(), total));
		}
		custItemDao.save(custItem);
	}
}
