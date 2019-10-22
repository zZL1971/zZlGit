package com.webservice;

import java.util.ArrayList;
import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import com.mw.framework.domain.SendTaskLog;
@WebService(name = "RocoService")
public interface IRocoService {
	@WebMethod
	public String hellWord(@WebParam(name="xml")  String xml);
	/**
	 * 劳卡webService 
	 * @param type 接口  SALE_MODIFICATION_PRICE修改价格
	 * @param paramXml  xml参数
	 * @return 返回信息 "F"默认不成功，处理成功后要返回成功标示（不等于"F"）
	 */
	@WebMethod
	public String rocoWS(@WebParam(name="type") String type,@WebParam(name="paramXml")String paramXml);
	
	@WebMethod
	public void sendTask(@WebParam(name="srcTaskId")String srcTaskId, @WebParam(name="groupId")String groupId,@WebParam(name="taskCreateTime") long taskCreateTime);
	
	
	
	@WebMethod
	public void sendTaskList(@WebParam(name="taskIdMapList")ArrayList<SendTaskLog> taskIdMapList);
	
	@WebMethod
	public void resetTaskMap();
	
	/**
	 * 查询订单信息
	 * @param orderCode 订单编号
	 * @param sapOrderCode SAP订单编号
	 * @param sold2Party 售达方名称
	 * @param sold2PartyCode  售达方编号
	 * @param ship2Party  收达方 编号
	 * @param orderDateFrom  订单日期start  格式 yyyy-MM-dd
	 * @param orderDateTo	订单日期end  格式yyyy-MM-dd
	 * @param expectedShipDateFrom  预计出库日期start   格式yyyy-MM-dd
	 * @param expectedShipDateTo	预计出库日期end    格式yyyy-MM-dd
	 * @return  Json格式  包含订单信息 行项目价格信息以及行项目基本信息
	 */
	@WebMethod
	public String getSaleMessage(@WebParam(name="orderCode")String orderCode,@WebParam(name="sapOrderCode")String sapOrderCode,@WebParam(name="sold2Party")String sold2Party,@WebParam(name="sold2PartyCode")String sold2PartyCode,@WebParam(name="ship2Party")String ship2Party,@WebParam(name="orderDateFrom")String orderDateFrom,@WebParam(name="orderDateTo")String orderDateTo,@WebParam(name="expectedShipDateFrom")String expectedShipDateFrom,@WebParam(name="expectedShipDateTo")String expectedShipDateTo,@WebParam(name="cusTel")String cusTel);
	
	@WebMethod
	public String createBJOrder(@WebParam(name="complaintTime")String complaintTime,@WebParam(name="duty")String duty,@WebParam(name="matnr")String matnr,@WebParam(name="miaoshu")String miaoshu,@WebParam(name="barcode")String barcode,@WebParam(name="zzebm")String zzebm,@WebParam(name="zzecj")String zzecj,@WebParam(name="zzelb")String zzelb,@WebParam(name="zzescx")String zzescx,@WebParam(name="zzezx")String zzezx,@WebParam(name="zzrgx")String zzrgx,@WebParam(name="zztsnr")String zztsnr,@WebParam(name="zzxbmm")String zzxbmm,@WebParam(name="zzccz")String zzccz,@WebParam(name="zzccwt")String zzccwt,@WebParam(name="orderType")String orderType,@WebParam(name="account")String account);
}
