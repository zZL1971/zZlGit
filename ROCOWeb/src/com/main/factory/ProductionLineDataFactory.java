package com.main.factory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.context.SpringContextHolder;
import com.mw.framework.manager.DataManagerFactory;
import com.mw.framework.manager.impl.CabinetManagerImpl;
import com.mw.framework.model.HoleInfo;
import com.mw.framework.model.PlateInfo;


public class ProductionLineDataFactory {
	private List<PlateInfo> plateInfo;

	protected class ImosDataManage	{
		String orderCodePosex;
		Boolean flg;
		public ImosDataManage(String orderCodePosex,Boolean flg){
			this.flg=flg;
			this.orderCodePosex=orderCodePosex;
		}
		public  void getImosIdbextData(){
			JdbcTemplate jdbcTemplate=SpringContextHolder.getBean("jdbcTemplate");
			if(flg){
				List<PlateInfo> list=new LinkedList<PlateInfo>();
				String imosIdbextSqlData="SELECT I.WIDTH,I.ID,I.LENGTH,I.BARCODE,I.ORDERID,I.GRID FROM IMOS_IDBEXT I LEFT JOIN SALE_ITEM SI" + 
						"    ON I.ORDERID = SI.ORDER_CODE_POSEX WHERE SI.ID='"+orderCodePosex+"' AND I.BARCODE IS NOT NULL AND I.INFO1 LIKE 'A%' ORDER BY I.ID ASC";
				List<Map<String, Object>> pLlist = jdbcTemplate.queryForList(imosIdbextSqlData);
				if(pLlist.size()>0) {
					for (Map<String, Object> map : pLlist) {
						PlateInfo in=new PlateInfo(Double.parseDouble(String.valueOf(map.get("WIDTH"))), Double.parseDouble(String.valueOf(map.get("LENGTH"))), (String)map.get("ID"), (String)map.get("ORDERID"), (String)map.get("BARCODE"), Integer.parseInt(String.valueOf(map.get("GRID"))));
						list.add(in);
					}
				}
				if(list.size()>0) {
					for (PlateInfo plateInfo : list) {
						 List<Map<String, Object>> holeList = jdbcTemplate.queryForList("SELECT I.IP_X,I.IP_Y,I.OR_X,I.OR_Y,I.MACHINING,I.DE,I.DIA,I.EP_X,I.EP_Y,I.OR_Z,I.ORTYPE,I.WGNAME FROM IMOS_IDBWG I "
								+ "WHERE I.ORDERID='"
								+ plateInfo.getOrderId()
								+ "' "
								+ "AND I.ID='" + plateInfo.getId() + "' ORDER BY I.MACHINING ASC");
						 List<HoleInfo> infos=new LinkedList<HoleInfo>();
						 for (Map<String, Object> map : holeList) {
							Double ipX = Double.parseDouble(String.valueOf(map.get("IP_X")));
							Double ipY = Double.parseDouble(String.valueOf(map.get("IP_Y")));
							Double orX = Double.parseDouble(String.valueOf(map.get("OR_X")));
							Double orY = Double.parseDouble(String.valueOf(map.get("OR_Y")));
							Integer machining = Integer.parseInt(String.valueOf(map.get("MACHINING")));
							Double de = Double.parseDouble(String.valueOf(map.get("DE")));
							Double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
							Double epX = Double.parseDouble(String.valueOf(map.get("EP_X")));
							Double epY = Double.parseDouble(String.valueOf(map.get("EP_Y")));
							Double orZ = Double.parseDouble(String.valueOf(map.get("OR_Z")));
							String orType = (String) map.get("ORTYPE");
							String wgname = (String) map.get("WGNAME");
							HoleInfo holeInfo=new HoleInfo(ipX, ipY, orX, orY, machining, de, dia, epX, epY, orZ, orType, wgname);
							infos.add(holeInfo);
						}
						 plateInfo.setHoleInfo(infos);
						
					}
				}
				ProductionLineDataFactory.this.setPlateInfo(list);
			}
		}
	}
	public List<PlateInfo> getPlateInfo() {
		return plateInfo;
	}
	public void setPlateInfo(List<PlateInfo> plateInfo) {
		this.plateInfo = plateInfo;
	}
	public void execute(String orderId,JdbcTemplate jdbcTemplate) {
		ProductionLineDataFactory dataFactory=new ProductionLineDataFactory();
		DataManagerFactory factory=new CabinetManagerImpl();
		ProductionLineDataFactory.ImosDataManage dataManage=dataFactory.new ImosDataManage(orderId, true);
		dataManage.getImosIdbextData();
		factory.calculateProcessRoute(dataFactory.getPlateInfo(),jdbcTemplate);
	}
}
