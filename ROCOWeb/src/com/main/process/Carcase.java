package com.main.process;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.main.abstrac.TechnologyAbstract;
import com.main.factory.ProductFactory;

public class Carcase extends TechnologyAbstract {
	/**
	 * 计算柜身工艺路线
	 */
	@SuppressWarnings("unused")
	@Override
	public Map<String, String> calculateProcessRoute(ProductFactory pf) {
		// 判断单条imos信息的数据是否为空 null值不做处理
		Map<String, Object> imosIdbext = pf.getImosIdbext();
		List<Map<String, Object>> imosIdbwg = pf.getImosIdbwg();
		String intClength = String.valueOf(imosIdbext.get("LENGTH"));
		double clength = Double.parseDouble(intClength);
		String intWidth = String.valueOf(imosIdbext.get("WIDTH"));
		double cwidth = Double.parseDouble(intWidth);
		Map<String, String> identifi = new HashMap<String, String>();
		identifi.put("tec_side@gy_face", "");
		identifi.put("tec_groove@gy_groove", "");
		identifi.put("tec_pore@gy_level_hole", "");
		identifi.put("tec_xlk@gy_mill_outline", "");
		identifi.put("tec_trough@gy_straightSlot", "");
		Map<String, String> face = new HashMap<String, String>();
		face.put("tec_side@gy_faceside1", "");
		face.put("tec_side@gy_faceside2", "");
		String gy_face = null;// 定义面
		String gy_level_hole = "";// 定义多面水平孔
		String gy_mill_outline = null;// 定义铣轮廓
		String gy_groove = null;// 定义槽
		String gy_straightSlot = "";// 定义通槽
		boolean flg = false;
		List<Double> ipx = pf.getIpXList();
		List<Double> ipy = pf.getIpYList();
		if((ipy.contains(clength)||ipy.contains(0.0))&&(ipx.contains(cwidth)||ipx.contains(0.0))){
			// 多面水平孔
			gy_level_hole += "@D";
		}else{
			flg = true;
		}
		if(flg){
			if(ipy.contains(cwidth)){
				gy_level_hole += "@D";
			}
		}
		String face1 = face.get("tec_side@gy_faceside1");
		String face2 = face.get("tec_side@gy_faceside2");
		if ("180.0".equals(face1) && "0.0".equals(face2)) {
			gy_face = "AB";
		} else if ("180.0".equals(face1)) {
			gy_face = "A";
		} else if ("0.0".equals(face2)) {
			gy_face = "B";
		}

		List<Integer> maching = pf.getMachiningList();
		List<Double> epy = pf.getEpYList();
		List<Double> epx = pf.getEpXList();
		List<Double> d = pf.getDeList();
		List<Double> di = pf.getDiaList();
		List<Double> or = pf.getOrZList();
		boolean troun=true;
		if(maching.contains(2)){
			if(di.contains(10.0)){
				for (Double d1 : d) {
					if(d1>=5.9&&d1<=6.1){
						for (Double e1 : epy) {
							double epWidth = cwidth-e1;
							if(epWidth>=13.9&&epWidth<=14.1){
								for (Double e2 : epx) {
									if(or.contains(0.0)&&e2>=(clength-3)){
										//正面通槽
										gy_straightSlot +="@_SA"+(epWidth-5);
										troun=false;
									}
									if(or.contains(180.0)&&e2<=3){
										//反面通槽
										gy_straightSlot +="@_SB"+(epWidth-5);
										troun=false;
									}
									if(troun){
										if(or.contains(0.0)&&e2<=clength){
											//正面不通槽
											gy_straightSlot +="@_TA"+(epWidth-5);
										}
										if(or.contains(180.0)&&e2>=3){
											//反面不通槽
											gy_straightSlot +="@_TB"+(epWidth-5);
										}
									}
								}
								
							}
						}
					}
				}
			}
		}
		identifi.put("tec_side@gy_face", gy_face);
		if (imosIdbext != null) {
			for (Map<String, Object> idbwgAlone : imosIdbwg) {
				double or_x = Double.parseDouble(String.valueOf(idbwgAlone
						.get("OR_X")));
				int machining = Integer.parseInt(String.valueOf(idbwgAlone
						.get("MACHINING")));
				String ortype = String.valueOf(idbwgAlone.get("ORTYPE"));
				double ip_y = Double.parseDouble(String.valueOf(idbwgAlone
						.get("IP_Y")));
				double de = Double.parseDouble(String.valueOf(idbwgAlone
						.get("DE")));
				double dia = Double.parseDouble(String.valueOf(idbwgAlone
						.get("DIA")));
				double ep_y = Double.parseDouble(String.valueOf(idbwgAlone
						.get("EP_Y")));
				double ep_x = Double.parseDouble(String.valueOf(idbwgAlone
						.get("EP_X")));
				double or_z = Double.parseDouble(String.valueOf(idbwgAlone
						.get("OR_Z")));
				if (1 == machining) {
					if ("V".equals(ortype)) {
						if (180.0 == or_x) {
							face.put("tec_side@gy_faceside1", "180.0");
						}
						if (0.0 == or_x) {
							face.put("tec_side@gy_faceside2", "0.0");
						}
						// 垂直孔
						gy_level_hole += "@V";
					}
					if (ortype.contains("H")) {
						
						gy_level_hole += "@H";
					}
					// 无槽
					gy_groove = "V0";
				}
				if (2 == machining) {
					gy_groove = "";
					if ("V".equals(ortype)) {
						if (5.5 == dia) {
							if (de == 6.0 || de == 9.9 || de == 9.5) {
								// 锯槽
								gy_groove = "V5.5";
							}
						}
						if (10.0 == dia) {
							if (de >=5.9&&de<=6.2) {
								// 铣槽
								gy_groove = "V10";
							}
							
						}
						if (ip_y == 11.2) {
							// 弧形槽
							gy_groove = "V11.2";
						}
					}
					if ("H".equals(ortype)) {
						// 侧边铣槽
						gy_groove = "H2";
					}
				}
				if (0 == machining) {
					// 侧边铣孔
					if ("H".equals(ortype)) {
						// 侧边铣孔
						gy_mill_outline = "H0";
					}
				}
				if (3 == machining) {
					// 铣轮廓
					if ("V".equals(ortype)) {
						// 内铣轮廓
						gy_mill_outline = "V3";
					}
				}
				if (4 == machining) {
					// 铣轮廓
					if ("V".equals(ortype)) {
						// 内铣轮廓
						gy_mill_outline = "V4";
					}
				}
			}
			
			identifi.put("tec_groove@gy_groove", gy_groove);
			if (gy_level_hole.trim().length() > 0) {
				StringBuffer sbf = new StringBuffer();
				String[] hole = gy_level_hole.split("@");
				HashSet<String> set = new HashSet<String>();
				for (String string : hole) {
					set.add(string);
				}
				for (String string : set) {
					sbf.append(string);
				}
				identifi.put("tec_pore@gy_level_hole", sbf.toString());
			}

			identifi.put("tec_xlk@gy_mill_outline", gy_mill_outline);

			if (gy_straightSlot.trim().length() > 0) {
				StringBuffer sbf = new StringBuffer();
				String[] hole = gy_straightSlot.split("@");
				HashSet<String> set = new HashSet<String>();
				for (String string : hole) {
					set.add(string);
				}
				for (String string : set) {
					sbf.append(string);
				}
				identifi.put("tec_trough@gy_straightSlot", sbf.toString()
						.substring(1));
			}
			class CabinetFactory {
				/**
				 * 
				 * @param key
				 * @param value
				 * @return
				 */
				public String calculateCabinet(Map<String, String> args,
						Map<String, Object> imosIdbext) {
					Map<String, String> judge = args;
					String tec_side = judge.get("tec_side@gy_face");
					String tec_groove = judge.get("tec_groove@gy_groove");
					String tec_xlk = judge.get("tec_xlk@gy_mill_outline");
					String tec_trough = judge.get("tec_trough@gy_straightSlot");
					String tec_pore = judge.get("tec_pore@gy_level_hole");
					double cwidth = Double.parseDouble(String
							.valueOf(imosIdbext.get("WIDTH")));
					double clength = Double.parseDouble(String
							.valueOf(imosIdbext.get("LENGTH")));

					String cabinetName = null;
					Cabinet cabinet = new Cabinet(tec_side, tec_groove,
							tec_xlk, tec_trough, tec_pore, cwidth, clength);

					cabinetName=convertData(cabinet);
					return cabinetName;
				}

				private String convertData(Cabinet cabinet) {
					try {
						Class<?> cls =cabinet.getClass();
						Method[] methods = cls.getDeclaredMethods();
						Set<String> cabinetNameSet=new HashSet<String>();
						for (int i = 0; i < methods.length; i++) {
							if(!methods[i].getName().startsWith("get")&&!methods[i].getName().startsWith("set")){
								String name = (String) methods[i].invoke(cabinet, null);
								if(name!=null){
									cabinetNameSet.add(name);
								}
							}
						}
						if(cabinetNameSet.size()>=2){
							//开始进行分机台
							System.out.println("我有两台机台，需要给我分机台");
						}
						
					}  catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return null;
					/*String flg = "";
					flg = cabinet.cabinetABL2202();
					cabinet.cabinetHBX5002();
					cabinet.cabinetPTP1602();
					cabinet.cabinetShouPeiZhuan2();
					cabinet.cabinetCNCLaCao2();
					cabinet.cabinetDiaoLuoLaCao2();
					cabinet.cabinetFT21();
					cabinet.cabinetSkipper1301();
					cabinet.cabinetSkipper1001();
					cabinet.cabinetPTP1601_1();
					cabinet.cabinetPTP1601_2();
					cabinet.cabinetHBX0501();
					cabinet.cabinetHBX2001();
					cabinet.cabinetShouPaiZhuan1();
					cabinet.cabinetCNCLaCao1();
					return cabinet.cabinetDiaoLuoLaCao1();*/
					
				}
			}
			Map<String, String> map = new HashMap<String, String>();
			Set<Entry<String, String>> args = identifi.entrySet();
			/**
			 * 针对孔位
			 */
			CabinetFactory factory = new CabinetFactory();
			StringBuffer sbf = new StringBuffer();
			for (Entry<String, String> entry : args) {
				String key = entry.getKey();
				String value = entry.getValue();
				if (key != null) {
					boolean hole = Pattern.matches(
							"^[a-z]{3}_[a-z]{4}@[a-z]{2}_[a-z]{5}_[a-z]{4}$",
							key);
					if (hole) {
						if (!"".equals(value)) {
							sbf.append(value);
						}
					}
					/*
					 * boolean result = Pattern.matches(
					 * "^[a-z]{3}_[a-z]{4}@[a-z]{2}_[a-z]{4}_(1|2|3|4)?$", key);
					 * if(result){ if(!"".equals(value)){ sbf.append(value); } }
					 */
					if (value != null && !"".equals(value)) {
						key = key.substring(0, key.indexOf("@"));
						map.put(key, value);
					}

				}

			}
			if (map.size() > 0) {
				String cabinet = factory.calculateCabinet(identifi, imosIdbext);
				if(cabinet!=null){
					map.put("cabinet_name", cabinet);
				}
				return map;
			}else{
				return null;
			}
		}else{
			return null;
		}
	}
}
