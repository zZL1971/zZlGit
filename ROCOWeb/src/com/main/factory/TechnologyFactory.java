package com.main.factory;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
public class TechnologyFactory {
	/**
	 * 
	* @Title: calculateCabinet 
	* @Description: TODO(计算柜子工艺路线) 
	* @param @param pf    设定文件 
	* @return JOSNArray    返回类型 
	* @throws
	 */
	public String calculateCabinet(ProductFactory pf){
		//JSONArray array=new JSONArray();
		//Map<String, String> map=new HashMap<String, String>();
		Map<String, String> vlaue = pf.getCarcase().calculateProcessRoute(pf);
		GainIdentifying identifying=new GainIdentifying();
		String cabinetSql = identifying.passIdentifyingGainValue(vlaue, pf.getImosIdbext());
		return cabinetSql;
		/*map.put("cabinetSql", cabinetSql);*/
		//return array;
	}
	class GainIdentifying {
		
		public String passIdentifyingGainValue(Map<String, String> map,Map<String, Object> imosIdbext) {
			// 生成update语句
						StringBuffer updateSql = new StringBuffer();
						// 存放结果集参
						updateSql.append("UPDATE IMOS_IDBEXT I SET ");// 初始化update语句
			/**
			 * 将存储的键值对编写成update语句
			 */
			Set<Entry<String, String>> sqlM = map.entrySet();
			if(sqlM.size()>0){
				for (Entry<String, String> entry : sqlM) {
					if(entry.getValue()!=null&&entry.getValue().trim().length()>0){
						updateSql.append("I." + entry.getKey().toUpperCase() + "='" + entry.getValue()+ "',");
					}else{
						return null;
					}	
				}
				if(updateSql.lastIndexOf(",")!=-1){
					String sql=updateSql.toString();
					updateSql.setLength(0);
					sql=sql.substring(0, sql.lastIndexOf(","));
					updateSql.append(sql);
				}
				/*double width = Double.parseDouble(String.valueOf(imosIdbext.get("CWIDTH")));
				double length = Double.parseDouble(String.valueOf(imosIdbext.get("CLENGTH")));
				CabinetDept1 dept1=new CabinetDept1(); CABINET_NAME='"+cabinetName+"'
				String cabinetName = dept1.calculateCabinet(width, length,map);*/
				updateSql.append(" WHERE I.ID='" + imosIdbext.get("ID")
						+ "' AND I.BARCODE='" + imosIdbext.get("BARCODE")
						+ "' AND I.ORDERID='" + imosIdbext.get("ORDERID") + "'");
				return updateSql.toString();
			}else{
				return null;
			}
			
		}

	}
}
