package com.mw.framework.manager.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

import com.mw.framework.manager.DataManagerFactory;
import com.mw.framework.model.HoleInfo;
import com.mw.framework.model.PlateInfo;
import com.mw.framework.model.TecInfo;

public class CabinetManagerImpl implements DataManagerFactory {

	static class Utils{
		public static String removeRepeatChar(String s) {
	        if (s == null||"".equals(s)) {
	            return "";
	        }
	        StringBuffer sbf = new StringBuffer();
	        String[] info = s.split("@");
	        HashSet<String> set = new HashSet<String>();
			for (String string : info) {
				set.add(string);
			}
			for (String string : set) {
				sbf.append(string);
			}
	        return sbf.toString();
		}
		public static String resover(String gy_straightSlot){
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
				return sbf.substring(1);
			}
			return "";
		}
	}
	@Override
	public void calculateProcessRoute(List<PlateInfo> infos ,JdbcTemplate jdbcTemplate) {
		// TODO Auto-generated method stub
		List<TecInfo> list=new ArrayList<TecInfo>();
		for (PlateInfo plateInfo : infos) {
			String face="";//正面 A 反面B
			String gy_level_hole="";
			String gy_straightSlot = "";// 定义通槽
			String gy_groove = "";// 定义槽
			String gy_mill_outline = "";// 定义铣轮廓
			Integer gy_pore_num= 0 ;
			for (HoleInfo holeInfo : plateInfo.getHoleInfo()) {
				if(holeInfo.getMachining().equals(1)){
					if("V".equals(holeInfo.getOrType())){
						if(holeInfo.getOrX().equals(180.0)){
							face+="A@";
						}
						if(holeInfo.getOrX().equals(0.0)){
							face+="B@";
						}
						gy_level_hole+="V@";
					}else if("H".equals(holeInfo.getOrType())){
						gy_level_hole+="H@";
						if(plateInfo.getGrid().equals(1)){
							if(holeInfo.getIpY().equals(-0.1)||holeInfo.getIpY().equals(0.1)||holeInfo.getIpY().equals(0.0)||holeInfo.getIpY().equals(plateInfo.getWidth())||holeInfo.getIpY().equals(plateInfo.getWidth()-0.1)||holeInfo.getIpY().equals(plateInfo.getWidth()+0.1)){
								gy_level_hole+="D@";
							}
						}else if(plateInfo.getGrid().equals(2)){
							if(holeInfo.getIpX().equals(-0.1)||holeInfo.getIpX().equals(0.1)||holeInfo.getIpX().equals(0.0)||holeInfo.getIpX().equals(plateInfo.getWidth())||holeInfo.getIpX().equals(plateInfo.getWidth()-0.1)||holeInfo.getIpX().equals(plateInfo.getWidth()+0.1)){
								gy_level_hole+="D@";
							}
						}
					}
					if(holeInfo.getWgname()!=null&&!"".equals(holeInfo.getWgname())) {
						if(holeInfo.getWgname().indexOf("1Φ8X10,5")==-1) {
							gy_pore_num++;
						}
					}
				}else if(holeInfo.getMachining().equals(2)){
					if("H".equals(holeInfo.getOrType())){
						//侧边铣槽
						gy_mill_outline = "H2";
					}else if("V".equals(holeInfo.getOrType())){
						if(holeInfo.getDia().equals(5.5)){
							if(holeInfo.getDe()>=5.9&&holeInfo.getDe()<=9.5){
								//5.5锯槽
								gy_groove +="V5.5@";
								double val=plateInfo.getWidth()-holeInfo.getIpY();
								if((holeInfo.getIpY()>=9.0&&holeInfo.getIpY()<=11.5)||((val<=11.5)&&(val>=9.0))){
									gy_groove +="V11.2@";
								}
							}
						}
						if(holeInfo.getDia().equals(10.0)){
							if(holeInfo.getDe()>=5.6&&holeInfo.getDe()<=6.2){
								gy_groove +="V10@";
							}
							if(holeInfo.getDe()>=5.6&&holeInfo.getDe()<=6.3){
								double epWidth = plateInfo.getWidth()-holeInfo.getEpY();
								boolean troun=true;
								if(epWidth>=13.9&&epWidth<=14.1){
									if(holeInfo.getEpX()>=(plateInfo.getLength()-3)&&holeInfo.getIpX()<=3){
										if((holeInfo.getOrZ().equals(0.0)||holeInfo.getOrZ().equals(90.0))){
											if(holeInfo.getOrX().equals(0.0)&&holeInfo.getOrZ().equals(0.0)){
												gy_straightSlot+="@_SB"+(epWidth-5);
											}else{
												gy_straightSlot+="@_SA"+(epWidth-5);
											}
											troun=false;
										}
									}else if(holeInfo.getEpX()<=3&&holeInfo.getIpX()>=(plateInfo.getLength()-3)){
										if(holeInfo.getOrZ().equals(180.0)||holeInfo.getOrZ().equals(-180.0)||holeInfo.getOrZ().equals(-90.0)){
											if((holeInfo.getOrZ().equals(180.0)||holeInfo.getOrZ().equals(-180.0))&&holeInfo.getOrX().equals(180.0)){
												gy_straightSlot+="@_SA"+(epWidth-5);
											}else{
												gy_straightSlot+="@_SB"+(epWidth-5);
											}
											troun=false;
										}
									}
								}
								if(troun){
									if((holeInfo.getOrZ().equals(0.0)||holeInfo.getOrZ().equals(90.0))){
										if(holeInfo.getOrX().equals(0.0)&&holeInfo.getOrZ().equals(0.0)){
											gy_straightSlot+="@_TB"+(epWidth-5);
										}else{
											gy_straightSlot+="@_TA"+(epWidth-5);
										}
									}else if(holeInfo.getOrZ().equals(180.0)||holeInfo.getOrZ().equals(-180.0)||holeInfo.getOrZ().equals(-90.0)){
										if((holeInfo.getOrZ().equals(180.0)||holeInfo.getOrZ().equals(-180.0))&&holeInfo.getOrX().equals(180.0)){
											gy_straightSlot+="@_TA"+(epWidth-5);
										}else{
											gy_straightSlot+="@_TB"+(epWidth-5);
										}
									}
								}
							}
						}
					}
				}else if(holeInfo.getMachining().equals(3)){
					if("V".equals(holeInfo.getOrType())){
						// 内铣轮廓
						gy_mill_outline = "V3";
					}
				}else if(holeInfo.getMachining().equals(4)){
					if("V".equals(holeInfo.getOrType())){
						// 内铣轮廓
						gy_mill_outline = "V4";
					}
				}else if(holeInfo.getMachining()==0){
					if("H".equals(holeInfo.getOrType())){
						//侧边铣孔
						gy_mill_outline = "H0";
					}
				}
			}
			gy_groove = "".equals(Utils.removeRepeatChar(gy_groove))?"":Utils.removeRepeatChar(gy_groove);
			face = Utils.removeRepeatChar(face);
			gy_level_hole = Utils.removeRepeatChar(gy_level_hole);
			gy_straightSlot = Utils.resover(gy_straightSlot);
			//开始生成UPDATE 语句 
			StringBuffer sqlSbf=new StringBuffer("UPDATE IMOS_IDBEXT I SET ");
			sqlSbf.append("I.TEC_GROOVE='"+gy_groove+"',I.TEC_SIDE='"+face+"',"
					+ "I.TEC_PORE='"+gy_level_hole+"',"
							+ "I.TEC_TROUGH='"+gy_straightSlot+"',"
									+ "I.TEC_XLK='"+gy_mill_outline+"',"
											+ "I.TEC_PORE_NUMS='"+gy_pore_num+"' WHERE I.ID='"+plateInfo.getId()+"' AND I.ORDERID='"+plateInfo.getOrderId()+"'");
			//System.out.println(sqlSbf.toString());
			jdbcTemplate.execute(sqlSbf.toString());
		}
		/*StringBuffer pathName=new StringBuffer("G:\\ROCODocment\\工艺路线\\");
		Date date=new Date();
		String pattern="yyyy-MM-dd-HH-mm-ss";
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		String timer = format.format(date);
		
		HSSFWorkbook workbook=new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("工艺路线");
		for (int i = 0; i < list.size()+1; i++) {
			HSSFRow row = sheet.createRow(i);
			for (int j = 0; j < 8; j++) {
				if(i==0){
					HSSFCell cell = row.createCell(j);
					if(j==0){
						cell.setCellValue("ORDERID");
					}else if(j==1){
						cell.setCellValue("ID");
					}else if(j==2){
						cell.setCellValue("TEC_GROOVE");
					}else if(j==3){
						cell.setCellValue("TEC_SIDE");
					}else if(j==4){
						cell.setCellValue("TEC_PORE");
					}else if(j==5){
						cell.setCellValue("TEC_TROUGH");
					}else if(j==6){
						cell.setCellValue("TEC_XLK");
					}else if(j==7){
						cell.setCellValue("TEC_PORE_NUM");
					}
				}
				if(i>=1){
					HSSFCell cell = row.createCell(j);
					if(j==0){
						cell.setCellValue(list.get(i-1).getOrderId());
					}else if(j==1){
						cell.setCellValue(list.get(i-1).getId());
					}else if(j==2){
						cell.setCellValue(list.get(i-1).getTecGroove());
					}else if(j==3){
						cell.setCellValue(list.get(i-1).getTecSide());
					}else if(j==4){
						cell.setCellValue(list.get(i-1).getTecPore());
					}else if(j==5){
						cell.setCellValue(list.get(i-1).getTecTRough());
					}else if(j==6){
						cell.setCellValue(list.get(i-1).getTecXlk());
					}else if(j==7){
						cell.setCellValue(list.get(i-1).getTecPoreNum());
					}
				}
			}
		}
		pathName.append(list.get(0).getOrderId()+"-");
		pathName.append(timer).append(".xls");
		File file=new File(pathName.toString());
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileOutputStream outStream=null;
		try {
			outStream=new FileOutputStream(file);
			workbook.write(outStream);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			try {
				outStream.flush();
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	*/	System.out.println("-----------------------------------------^_^----------------------------------------------");
	}

}
