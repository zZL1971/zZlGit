package com.main.factory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;


public  class HoleFactoryImpl implements HoleProduct{
	/**
	 * 锁孔两面打穿板件
	 */
	@Override
	public Boolean holeWearPlate(List<Map<String, Object>> imosIdbwg,Double width,Double length,String grid) {
		List<Double> ipzlist = new ArrayList<Double>();
		boolean flg = true;
		for (Map<String, Object> imosmap : imosIdbwg) {
			String  ortype = (String)imosmap.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(imosmap.get("DIA")));
			double de = Double.parseDouble(String.valueOf(imosmap.get("DE")));
			//锁孔两面打穿板件
			if ("V".equals(ortype)&&dia==15.00&&de==14.20) {
				double ipz = Double.parseDouble(String.valueOf(imosmap.get("IP_Z")));
					ipzlist.add(ipz); 
			}
		}
		//锁孔两面打穿板件
		if (ipzlist.size()>0) {
			HashSet<Double> set=new HashSet<Double>(ipzlist);
			ipzlist.clear();
			ipzlist.addAll(set);
			if (ipzlist.size()>1) {
				if (ipzlist.contains(0.00)&&ipzlist.contains(36.00)) {
					return true;
				}
				flg=false;
			}
		}
		return flg;
	}
	
	public Boolean holecollide2(String ortype,Double ipy,Double epy,
			Double ipx,Double epx,Double ip_x,Double ip_y,
			Double dia,Double diatwo,Double ipz,Double ip_z,Double de,Double thickness,Double deone) {
		if("H".equals(ortype)){
			if (ipy==epy) {
				if(ipx>epx){
					if (ipx+3>ip_x&&ip_x>epx-3) {
						if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
							return false;
						}
					}
				}else{
					if (epx+3>ip_x&&ip_x>ipx-3) {
						if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
							return false;
						}
					}
				}
			}
			
			if (ipx==epx) {
				if (ipy>epy) {
					if (ipy+3>ip_y&&ip_y>epy-3) {
						if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
							return false;
						}
					}
				}else{
					if (epy+3>ip_y&&ip_y>ipy-3) {
						if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
							return false;
						}
					}
				}
			}
		}
		if("V".equals(ortype)){
			if(ipz==ip_z){
				if (ipy==epy) {
					if(ipx>epx){
						if (ipx+3>ip_x&&ip_x>epx-3) {
							if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
								return false;
							}
						}
					}else{
						if (epx+3>ip_x&&ip_x>ipx-3) {
							if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
								return false;
							}
						}
					}
				}
				if (ipx==epx) {
					if (ipy>epy) {
						if (ipy+3>ip_y&&ip_y>epy-3) {
							if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
								return false;
							}
						}
					}else{
						if (epy+3>ip_y&&ip_y>ipy-3) {
							if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
								return false;
							}
						}
					}
				}
			} 
			if(ipz!=ip_z&&de+deone>=thickness){
				if (ipy==epy) {
					if(ipx>epx){
						if (ipx+3>ip_x&&ip_x>epx-3) {
							if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
								return false;
							}
						}
					}else{
						if (epx+3>ip_x&&ip_x>ipx-3) {
							if (Math.abs(ip_y-ipy)<(((dia/2)+(diatwo/2))+2)) {
								return false;
							}
						}
					}
				}
				if (ipx==epx) {
					if (ipy>epy) {
						if (ipy+3>ip_y&&ip_y>epy-3) {
							if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
								return false;
							}
						}
					}else{
						if (epy+3>ip_y&&ip_y>ipy-3) {
							if (Math.abs(ip_x-ipx)<((dia/2)+(diatwo/2))+2) {
								return false;
							}
						}
					}
				}
			}
	}
		return true;
		
	}
	/**
	 * 槽孔相撞\孔孔相撞
	 */
	@Override
	public Boolean holeCollide(List<Map<String, Object>> imosIdbwg,Double thickness) {
		for (Map<String, Object> imosmap : imosIdbwg) {
			int machining = Integer.parseInt(String.valueOf(imosmap.get("MACHINING")));
			//槽孔相撞\孔孔相撞
			if (machining==2) {
				double ipx = Double.parseDouble(String.valueOf(imosmap.get("IP_X")));
				double ipy = Double.parseDouble(String.valueOf(imosmap.get("IP_Y")));
				double ipz = Double.parseDouble(String.valueOf(imosmap.get("IP_Z")));
				double epx = Double.parseDouble(String.valueOf(imosmap.get("EP_X")));
				double epy = Double.parseDouble(String.valueOf(imosmap.get("EP_Y")));
				double de = Double.parseDouble(String.valueOf(imosmap.get("DE")));
				double diatwo = Double.parseDouble(String.valueOf(imosmap.get("DIA")));
				for (Map<String, Object> imosmaps : imosIdbwg) {
					double machin = Double.parseDouble(String.valueOf(imosmaps.get("MACHINING")));
					if (machin==1) {
						double deone = Double.parseDouble(String.valueOf(imosmaps.get("DE")));
						double dia = Double.parseDouble(String.valueOf(imosmaps.get("DIA")));
						String  ortype = (String)imosmaps.get("ORTYPE");
						double ip_x = Double.parseDouble(String.valueOf(imosmaps.get("IP_X")));
						double ip_y = Double.parseDouble(String.valueOf(imosmaps.get("IP_Y")));
						double ip_z = Double.parseDouble(String.valueOf(imosmaps.get("IP_Z")));
						if(dia!=8.00&&dia!=5.00&&dia!=6.00){
							if(!holecollide2(ortype, ipy, epy, ipx, epx, ip_x, ip_y, dia, diatwo, ipz, ip_z, de, thickness, deone)) {
								return false;
							}
						}else if(dia==6.00){
							if(deone!=9.00&&deone!=10.00){
								if(!holecollide2(ortype, ipy, epy, ipx, epx, ip_x, ip_y, dia, diatwo, ipz, ip_z, de, thickness, deone)) {
									return false;
								}
							}
						}else if(dia==5.00){
							if(deone!=10.00){
								if(!holecollide2(ortype, ipy, epy, ipx, epx, ip_x, ip_y, dia, diatwo, ipz, ip_z, de, thickness, deone)) {
									return false;
								}
							}
						}else if (dia==8.00){
							if(deone!=6.00){
								if(!holecollide2(ortype, ipy, epy, ipx, epx, ip_x, ip_y, dia, diatwo, ipz, ip_z, de, thickness, deone)) {
									return false;
								}
							}
						}
					}
				}
				//槽位过深情况
			if (de>12) {
				return false;	
			}
		}
	}
	return true;
}
	
	
	/**
	 * 板件之外
	 */
	@Override
	public Boolean holeIsOut(List<Map<String, Object>> imosIdbwg, Double width,
			Double length,String grid) {
		for (Map<String, Object> imosmap : imosIdbwg) {
			String  ortype = (String)imosmap.get("ORTYPE");
			//孔在板件之外
			if ("V".equals(ortype)) {
				double dia = Double.parseDouble(String.valueOf(imosmap.get("DIA")));
				double de = Double.parseDouble(String.valueOf(imosmap.get("DE")));
				double ipx = Double.parseDouble(String.valueOf(imosmap.get("IP_X")));
				double ipy = Double.parseDouble(String.valueOf(imosmap.get("IP_Y")));
				double machin = Double.parseDouble(String.valueOf(imosmap.get("MACHINING")));
				if (dia!=8.00&&de!=6.00) {
					if (machin==1) {
						if ("1".equals(grid)) {
							if (ipx<0||ipy<0||ipx>length||ipy>width) {
								return false;
							}
						}
						if ("2".equals(grid)) {
							if (ipx<0||ipy<0||ipx>width||ipy>length) {					
								return false;
							}
						}
					}

				}
				if (dia==8.00) {
					if(de==6.00){
						if ("1".equals(grid)) {
							if (ipx-length>6.00||ipy-width>6.00) {
								return false;
							}
						}
						if ("2".equals(grid)) {
							if (ipx-width>6.00||ipy-length>6.00) {
								return false;
							}
						}
					}else{
						if (machin==1) {
							if ("1".equals(grid)) {
								if (ipx<0||ipy<0||ipx>length||ipy>width) {
									return false;
								}
							}
							if ("2".equals(grid)) {
								if (ipx<0||ipy<0||ipx>width||ipy>length) {					
									return false;
								}
							}
						}
					}
				}
				if (de==6.00) {
					if(dia==8.00){
					}else{
						if (machin==1) {
							if ("1".equals(grid)) {
								if (ipx<0||ipy<0||ipx>length||ipy>width) {
									return false;
								}
							}
							if ("2".equals(grid)) {
								if (ipx<0||ipy<0||ipx>width||ipy>length) {					
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 门铰引孔撞层板
	 */
	@Override
	public Boolean holeHinge(List<Map<String, Object>> imosIdbwg) {
		for (Map<String, Object> doorHingeMapOneForEach : imosIdbwg) {
			String  ortype = (String)doorHingeMapOneForEach.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(doorHingeMapOneForEach.get("DIA")));
			double de = Double.parseDouble(String.valueOf(doorHingeMapOneForEach.get("DE")));
			if ("V".equals(ortype)) { 
				if(dia==3.00&&de==5.00){
					double ipyOne = Double.parseDouble(String.valueOf(doorHingeMapOneForEach.get("IP_Y")));
					double ipxOne = Double.parseDouble(String.valueOf(doorHingeMapOneForEach.get("IP_X")));
					double ipzOne = Double.parseDouble(String.valueOf(doorHingeMapOneForEach.get("IP_Z")));
					for (Map<String, Object> doorHingeMapTwoForEach : imosIdbwg) {
						String  ortypetwo = (String)doorHingeMapTwoForEach.get("ORTYPE");
						double diatwo = Double.parseDouble(String.valueOf(doorHingeMapTwoForEach.get("DIA")));
						double detwo = Double.parseDouble(String.valueOf(doorHingeMapTwoForEach.get("DE")));
						//是否要加ortype.equals("V")，$dia==3.00&&$de==5.00 以后再看看
						if ("V".equals(ortypetwo)) {
							if (diatwo==3.00&&detwo==5.00) {
								double ipyTwo = Double.parseDouble(String.valueOf(doorHingeMapTwoForEach.get("IP_Y")));
								double ipxTwo = Double.parseDouble(String.valueOf(doorHingeMapTwoForEach.get("IP_X")));
								double ipzTwo = Double.parseDouble(String.valueOf(doorHingeMapTwoForEach.get("IP_Z")));
								if(ipyOne==ipyTwo&&ipzOne==ipzTwo){
									if(Math.abs(ipxOne-ipxTwo)==32){
										for (Map<String, Object> doorHingeMapThreeForEach : imosIdbwg) {
											double diaThree = Double.parseDouble(String.valueOf(doorHingeMapThreeForEach.get("DIA")));
											double deThree = Double.parseDouble(String.valueOf(doorHingeMapThreeForEach.get("DE")));
											if((diaThree==10&&deThree==10.5)||(diaThree==5&&deThree==8.5)){
												double ipxThree = Double.parseDouble(String.valueOf(doorHingeMapThreeForEach.get("IP_X")));
												double ipzThree = Double.parseDouble(String.valueOf(doorHingeMapThreeForEach.get("IP_Z")));
												double orzThree = Double.parseDouble(String.valueOf(doorHingeMapThreeForEach.get("OR_Z")));
												if (ipzThree==ipzTwo&&(orzThree==0||orzThree==180.00)) {
													if(ipxTwo>ipxOne){
														if (ipxOne-26<ipxThree&&ipxThree<ipxTwo+26) {
															return false;
														}
													}else{
														if (ipxTwo-26<ipxThree&&ipxThree<ipxOne+26) {
															return false;
														}
													}
												}
											}
										}
									}
								}
								
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	/**
	 * 水平位置出错
	 */
	@Override
	public Boolean holehorizontalPosition(List<Map<String, Object>> imosIdbwg,Double width,Double length,String grid) {
		for (Map<String, Object> imosmap : imosIdbwg) {
			String  ortype = (String)imosmap.get("ORTYPE");
			if ("H".equals(ortype)) {
				double ipx = Double.parseDouble(String.valueOf(imosmap.get("IP_X")));
				double ipy = Double.parseDouble(String.valueOf(imosmap.get("IP_Y")));
				double machin = Double.parseDouble(String.valueOf(imosmap.get("MACHINING")));
				double orx = Double.parseDouble(String.valueOf(imosmap.get("OR_X")));
				if (machin==1&&(orx==9.00||orx==0.00||orx==180.00||orx==-90.00)) {
					if ((ipx>=length-0.50&&ipx<=length+0.50)||(ipy>=length-0.50&&ipy<=length+0.50)||(ipx>=-0.50&&ipx<=0.50)||(ipy>=width-0.50&&ipy<=width+0.50)||(ipx>=width-0.50&&ipx<=width+0.50)||(ipy>=-0.50&&ipy<=0.50)) {
					}else{
						return false;
					}

				}

			}
		}
		return true;
	}
	
	/**
	 * 胶粒孔位置小于8.8
	 */
	@Override
	public Boolean colloidalParticlesHole(List<Map<String, Object>> imosIdbwg,
			Double width, Double length,String grid,Double thickness) {
		for (Map<String, Object> imosmap : imosIdbwg) {
			String  ortype = (String)imosmap.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(imosmap.get("DIA")));
			double de = Double.parseDouble(String.valueOf(imosmap.get("DE")));
			if ("V".equals(ortype)&&dia==10.00&&de==10.50) {
				double ipx = Double.parseDouble(String.valueOf(imosmap.get("IP_X")));
				double ipy = Double.parseDouble(String.valueOf(imosmap.get("IP_Y")));
				if(thickness==16.00) {
					if ((0<=ipx&&ipx<=7.80)||(0<=ipy&&ipy<=7.80)) {
						return false;
					}else{
						if ("1".equals(grid)) {
							if ((length-ipx<=7.80&&length-ipx>=0)||(width-ipy<=7.80&&width-ipy>=0)) {
								return false;
							}
						}
						if ("2".equals(grid)) {
							if ((length-ipy<=7.80&&length-ipy>=0)||(width-ipx<=7.80&&width-ipx>=0)) {
								return false;
							}
						}
					}
				}else {
					if ((0<=ipx&&ipx<=8.80)||(0<=ipy&&ipy<=8.80)) {
						return false;
					}else{
						if ("1".equals(grid)) {
							if ((length-ipx<=8.80&&length-ipx>=0)||(width-ipy<=8.80&&width-ipy>=0)) {
								return false;
							}
						}
						if ("2".equals(grid)) {
							if ((length-ipy<=8.80&&length-ipy>=0)||(width-ipx<=8.80&&width-ipx>=0)) {
								return false;
							}
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * 锁孔小于安全距离
	 */
	@Override
	public Boolean safeDistance(List<Map<String, Object>> imosIdbwg,
			Double width, Double length, String grid) {
		boolean flg = true;
		for (Map<String, Object> imosmap : imosIdbwg) {
			String  ortype = (String)imosmap.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(imosmap.get("DIA")));
			double de = Double.parseDouble(String.valueOf(imosmap.get("DE")));
			//锁孔两面打穿板件
			if ("V".equals(ortype)&&dia==15.00&&de==14.20) {
				double ipy = Double.parseDouble(String.valueOf(imosmap.get("IP_Y")));
				double ipx = Double.parseDouble(String.valueOf(imosmap.get("IP_X")));
				if(ipy!=13.00&&ipx!=13.00){
					if ("1".equals(grid)) {
						if (ipy<17.00||ipx<17.00||Math.abs(ipx-length)<17.00||Math.abs(ipy-width)<17.00) {
							return false;
						}
					}
					if ("2".equals(grid)) {
						if (ipy<17.00||ipx<17.00||Math.abs(ipx-width)<17.00||Math.abs(ipy-length)<17.00) {
							return false;
						}
					} 
				}

			}
		}
		return flg;
	}
	/**
	 * 	同一板件不能出现二合一和三合一
	 */
	@Override
	public Boolean connectHole(List<Map<String, Object>> imosIdbwg, Double width, Double length, String grid) {
		boolean flags = false;
		boolean flag = false;
		for (Map<String, Object> map : imosIdbwg) {
			String  ortype = (String)map.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
			double de = Double.parseDouble(String.valueOf(map.get("DE")));
			if("V".equals(ortype)) {
				if(dia==15.00&&de==14.20) {
					flag = true;
				}
				if(dia==20.00&&de==12.80) {
					flags = true;
				}
			}
		}
		if(flags&&flag) {
			return false;
		}
		
		return true;
	}
	/**
	 * 	同一边出现两种连接孔
	 */
	@Override
	public Boolean twoConnectHole(List<Map<String, Object>> imosIdbwg, Double width, Double length, String grid) {
		for (Map<String, Object> map : imosIdbwg) {
			String  ortype = (String)map.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
			double de = Double.parseDouble(String.valueOf(map.get("DE")));
			double ipx = Double.parseDouble(String.valueOf(map.get("IP_X")));
			double ipy = Double.parseDouble(String.valueOf(map.get("IP_Y")));
			//胶粒孔
			if("V".equals(ortype)&&dia==10.00&&de==10.50) {
				for (Map<String, Object> mapidbwg : imosIdbwg) {
					double keyHoleDia = Double.parseDouble(String.valueOf(mapidbwg.get("DIA")));
					double keyHoleDe = Double.parseDouble(String.valueOf(mapidbwg.get("DE")));
					double keyHoleIpx = Double.parseDouble(String.valueOf(mapidbwg.get("IP_X")));
					double keyHoleIpy = Double.parseDouble(String.valueOf(mapidbwg.get("IP_Y")));
					//锁孔
					if(keyHoleDia==15.00&&keyHoleDe==14.20) {
						if(ipx>=8.50&&ipx<=16.50) {
							if(keyHoleIpx>=24.50&&keyHoleIpx<=25.50) {
								return false; 
							}
						}else if(ipy>=8.50&&ipy<=16.50) {
							if(keyHoleIpy>=24.50&&keyHoleIpy<=25.50) {
								return false;
							}
						}else if(length-ipx>=8.50&&length-ipx<=16.50) {
							if(length-keyHoleIpx>=24.50&&length-keyHoleIpx<=25.50) {
								return false;
							}
						}else if (width-ipy>=8.50&&width-ipy<=16.50) {
							if(width-keyHoleIpy>=24.50&&width-keyHoleIpy<=25.50) {
								return false; 
							}
						}
					}else if(keyHoleDia==5.00&&keyHoleDe==8.50) {//二合一杆孔
						if(ipx>=8.50&&ipx<=16.50) {
							if(keyHoleIpx>=9.00&&keyHoleIpx<=17.00) {
								return false;
							}
						}else if(ipy>=8.50&&ipy<=16.50) {
							if(keyHoleIpy>=9.00&&keyHoleIpy<=17.00) {
								return false;
							}
						}else if(length-ipx>=9.00&&length-ipx<=17.00) {
							if(length-keyHoleIpx>=24.50&&length-keyHoleIpx<=25.50) {
								return false;
							}
						}else if(width-ipy>=9.00&&width-ipy<=17.00) {
							if(width-keyHoleIpy>=24.50&&width-keyHoleIpy<=25.50) {
								return false;
							}
						}
					
					}
				}
				
			}
		}
		return true;
	}
	/**
	 * 	中横撞门铰
	 */
	@Override
	public Boolean zhDoorHinge(List<Map<String, Object>> imosIdbwg) {
		for (Map<String, Object> map : imosIdbwg) {
			String  ortype = (String)map.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
			double de = Double.parseDouble(String.valueOf(map.get("DE")));
			double ipx = Double.parseDouble(String.valueOf(map.get("IP_X")));
			if("V".equals(ortype)&&dia==35.00&&de==12.00) {
				for(Map<String, Object> doorHingeMap : imosIdbwg) {
					String  doorHingOrtype = (String)doorHingeMap.get("ORTYPE");
					double doorHingDia = Double.parseDouble(String.valueOf(doorHingeMap.get("DIA")));
					double doorHingDe = Double.parseDouble(String.valueOf(doorHingeMap.get("DE")));
					double doorHingIpx = Double.parseDouble(String.valueOf(doorHingeMap.get("IP_X")));
					if("H".equals(doorHingOrtype)&&doorHingDia==6.00&&doorHingDe==35.00) {
						if(doorHingIpx>=ipx-29.00&&doorHingIpx<=ipx+29.00) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	/**
	 * 二合一层板有槽
	 */
	@Override
	public Boolean twoinoneGroove(List<Map<String, Object>> imosIdbwg) {
		for (Map<String, Object> map : imosIdbwg) {
			String  ortype = (String)map.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
			double de = Double.parseDouble(String.valueOf(map.get("DE")));
			int machining = Integer.parseInt(String.valueOf(map.get("MACHINING")));
			if("V".equals(ortype)&&dia==20.00&&de==12.80&&machining==2) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 拉手孔撞木框螺丝
	 */
	@Override
	public Boolean handleHoleknockScrew(List<Map<String, Object>> imosIdbwg) {
		for (Map<String, Object> map : imosIdbwg) {
			String  ortype = (String)map.get("ORTYPE");
			double dia = Double.parseDouble(String.valueOf(map.get("DIA")));
			double de = Double.parseDouble(String.valueOf(map.get("DE")));
			if(dia==6.00&&de==35.00&&"H".equals(ortype)) {
				for (Map<String, Object> screwmap : imosIdbwg) {
					String  screwOrtype = (String)screwmap.get("ORTYPE");
					double screwDia = Double.parseDouble(String.valueOf(screwmap.get("DIA")));
					double screwDe = Double.parseDouble(String.valueOf(screwmap.get("DE")));
					if(screwDia==5.00&&screwDe==9.00&&"V".equals(screwOrtype)) {
						double ipx = Double.parseDouble(String.valueOf(screwmap.get("IP_X")));
						if(ipx<=38.00) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	

}
