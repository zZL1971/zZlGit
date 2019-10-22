/**
 *
 */
package com.mw.framework.activiti.tasklistener;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;
import jcifs.smb.SmbFileOutputStream;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.springframework.jdbc.core.JdbcTemplate;

import com.main.factory.HoleProduct;
import com.main.factory.HoleProudctFactory;
import com.mw.framework.context.SpringContextHolder;

/**
 * 
 * 监听炸单结束后
 * 
 */
@SuppressWarnings("serial")
public class TaskReceiveCompleteListener implements ExecutionListener {
	String holemessag;
	@Override
	public void notify(DelegateExecution delegateExecution) throws Exception {
		JdbcTemplate jdbcTemplate = SpringContextHolder.getBean("jdbcTemplate");
		String drawType = (String) delegateExecution.getVariable("drawType");
		String uuid=(String) delegateExecution.getVariable("subuuid");
		holemessag="";
		if (!holeIsBad(jdbcTemplate,uuid)) {
			drawType=null;
			jdbcTemplate.update("UPDATE SALE_ITEM SI SET SI.HOLE_MESSAGE ='"+holemessag+"' WHERE SI.Id='"+uuid+"'");
		}
			delegateExecution.setVariable("drawType", drawType==null?"33":drawType);
			//如果不是审孔出错就备份MPR文件
			if(drawType!=null){
				//备份
				String sql = "SELECT SI.ORDER_CODE_POSEX  FROM SALE_ITEM SI WHERE SI.ID='"+uuid+"'";
				String sourcesql = "SELECT SD.Desc_En_Us FROM SYS_DATA_DICT SD WHERE SD.Desc_Zh_Cn='MPR文件路径'";//254
				String targetsql = "SELECT SD.Desc_En_Us FROM SYS_DATA_DICT SD WHERE SD.Desc_Zh_Cn='MPR备份路径'";//252
				String fileName = jdbcTemplate.queryForObject(sql, String.class);
				String sourcerul = jdbcTemplate.queryForObject(sourcesql, String.class);
				String source=sourcerul+"/mpr/ptp160/"+fileName+"/";
				String target=sourcerul+"/mpr/ptpnew160/"+fileName+"/";
				String targeturl = jdbcTemplate.queryForObject(targetsql, String.class);
				//根据日期生成文件夹
				Date date = new Date();
				String d = new SimpleDateFormat("yyyy/MM/dd").format(date);
				String year=d.substring(0, 4);
				String MM = d.substring(5, 7);
				String dat = d.substring(8,10);
				
				String  targettwo= targeturl+"/审孔专用备份/ptpnew160/"+year+"/"+MM+"/"+dat+"/"+fileName+"/";
				//如果选择的是A单就copyFile
					copyFile(source,target);
					copyFile(source,targettwo);
			}
			deleteMesasink(jdbcTemplate,uuid);
	}
	

	/**
	 * 台面水槽安装件
	 * @param jdbcTemplate
	 * @param uuid
	 */
	private void deleteMesasink(JdbcTemplate jdbcTemplate, String uuid) {
		
		String sql="SELECT I.INFO1,I.INFO4,I.TYP,I.ID,I.ORDERID,I.PARENTID FROM SALE_ITEM SI LEFT JOIN IMOS_IDBEXT I ON I.ORDERID = SI.ORDER_CODE_POSEX WHERE SI.ID = '"+uuid+"' AND I.INFO1 IN('CG02','CG03')";
		List<Map<String, Object>> imosIdbextList = jdbcTemplate.queryForList(sql);
		if(imosIdbextList.size()>0) {
			boolean flg = false;
			for (Map<String, Object> imosIdbext : imosIdbextList) {
				 String info1 = (String) imosIdbext.get("INFO1");
				 Double typ = Double.parseDouble(String.valueOf(imosIdbext.get("TYP")));
				if("CG02".equals(info1)&&8==typ) {
					 String parentid = (String) imosIdbext.get("PARENTID");
					for(Map<String, Object> imosIdbext1 :imosIdbextList) {
						String mesasinkInfo1 = (String) imosIdbext1.get("INFO1");
						String mesasinkInfo4 = (String) imosIdbext1.get("INFO4");
						Double mesasinkTyp = Double.parseDouble(String.valueOf(imosIdbext1.get("TYP")));
						if("CG03".equals(mesasinkInfo1)) {
							if("CG03".equals(mesasinkInfo1)&&"Z53".equals(mesasinkInfo4)&&3.0==mesasinkTyp) {
								 flg = true;
							}
						}
					}
					if(!flg) {
						if(parentid!=null&&!"".equals(parentid)) {
							for(Map<String, Object> imosIdbext2 : imosIdbextList) {
								String deleteInfo1 = (String) imosIdbext2.get("INFO1");
								Double deletetyp = Double.parseDouble(String.valueOf(imosIdbext2.get("TYP")));
								String deleteParentid = (String) imosIdbext2.get("PARENTID");
								String orderId = (String) imosIdbext2.get("ORDERID");
								String id = (String) imosIdbext2.get("ID");
								if("CG03".equals(deleteInfo1)&&8.0==deletetyp&&deleteParentid.equals(parentid)) {
									jdbcTemplate.update("delete imos_idbext i where i.orderid='"+orderId+"' and i.id='"+id+"'");
								}
								if("CG02".equals(deleteInfo1)&&8.0==deletetyp) {
									jdbcTemplate.update("update imos_idbext i set i.info1 = 'H01' WHERE I.ORDERID='"+orderId+"' AND I.ID='"+id+"'");
									
								}
							}
						}
					}
				}
			}
		}
	}



	/**
	 * 孔位出错判断
	 * @param jdbcTemplate
	 * @param uuid
	 * @return
	 */
	private boolean holeIsBad(JdbcTemplate jdbcTemplate, String uuid) {
		Boolean flg=true;
		HoleProudctFactory prouctFactory = new HoleProudctFactory();
		//获取工厂
		HoleProduct holeProduct=prouctFactory.getHoleProduct();
		List<Map<String, Object>> imosIdbext = jdbcTemplate.queryForList("SELECT I.WIDTH,I.LENGTH,I.THICKNESS,I.NC_FLAG,I.ID,I.CNC_BARCODE1,I.CNC_BARCODE2,I.GRID FROM SALE_ITEM SI LEFT JOIN IMOS_IDBEXT I ON I.ORDERID = SI.ORDER_CODE_POSEX WHERE SI.ID = '"+uuid+"'AND I.BARCODE IS NOT NULL");
		if (imosIdbext.size()>0) {
			for (Map<String, Object> mapid : imosIdbext) {
				double width = Double.parseDouble(String.valueOf(mapid.get("WIDTH")));
				double length = Double.parseDouble(String.valueOf(mapid.get("LENGTH")));
				double thickness = Double.parseDouble(String.valueOf(mapid.get("THICKNESS")));
				String id = (String)mapid.get("ID");
				String grid = (String) mapid.get("GRID");
//				String ncflag = (String) mapid.get("NC_FLAG");
				double ncflag = Double.parseDouble(String.valueOf(mapid.get("NC_FLAG")));
				String cncbarcodenoe= (String) mapid.get("CNC_BARCODE1");
				String cncbarcodetwo= (String) mapid.get("CNC_BARCODE2");
				if (cncbarcodenoe!=null||cncbarcodetwo!=null) {
					if(thickness!=0&&ncflag!=0) {
						List<Map<String, Object>> imosIdbwg = jdbcTemplate.queryForList("SELECT I.ORTYPE,I.ID,I.EP_X,I.EP_Y,I.DIA,I.DE,I.IP_X,I.IP_Y,I.IP_Z,I.MACHINING,I.OR_X,I.OR_Z FROM SALE_ITEM SI LEFT JOIN IMOS_IDBWG I ON I.ORDERID = SI.ORDER_CODE_POSEX WHERE SI.ID = '"+uuid+"'AND I.ID='"+id+"'");
						//槽孔相撞\孔孔相撞
						if (!holeProduct.holeCollide(imosIdbwg,thickness)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——槽孔相撞\\孔孔相撞\n";
							flg=false;
						}
						//门铰引孔撞层板
						if (!holeProduct.holeHinge(imosIdbwg)) {
							holemessag+="板件号1："+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——门铰引孔撞层板\n";
							flg=false;
						}
						//板件之外
						if (!holeProduct.holeIsOut(imosIdbwg, width, length,grid)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——板件之外\n";
							flg=false;
						}
						
						//锁孔两面打穿板件
						if (!holeProduct.holeWearPlate(imosIdbwg,width,length,grid)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——锁孔两面打穿板件\n";
							flg=false;
						}
						//锁孔小于安全距离
						if (!holeProduct.safeDistance(imosIdbwg,width,length,grid)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——锁孔小于安全距离\n";
							flg=false;
						}
						
						//胶粒孔位置小于8.5
						if (!holeProduct.colloidalParticlesHole(imosIdbwg, width, length,grid,thickness)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+",宽:"+width+",长:"+ length+"——胶粒孔位置小于8.8\n";
							flg=false;
						}
						//水平位置
						if (!holeProduct.holehorizontalPosition(imosIdbwg, width, length,grid)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——水平位置\n";
							flg=false;
						}
						//	同一边出现两种连接孔
						if (!holeProduct.twoConnectHole(imosIdbwg, width, length,grid)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——同一边出现两种连接孔\n";
							flg=false;
						}
						//二合一层板有槽
						if (!holeProduct.twoinoneGroove(imosIdbwg)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——二合一层板有槽\n";
							flg=false;
						}
						//中横撞门铰
 						if (!holeProduct.zhDoorHinge(imosIdbwg)) {
							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——中横撞门铰\n";
							flg=false;
						}
 						//同一板件不能出现二合一和三合一
 						if(!holeProduct.connectHole(imosIdbwg, width, length, grid)) {
 							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——同一板件不能出现二合一和三合一\n";
							flg=false;
 						}
 						//拉手孔撞木框螺丝
 						if(!holeProduct.handleHoleknockScrew(imosIdbwg)) {
 							holemessag+="板件号1:"+cncbarcodenoe+",板件号2:"+cncbarcodetwo+"——拉手孔撞木框螺丝\n";
 							flg=false;
 						}
					}
				}
			}
		}
		return flg;
	}
	
	/**
	 * 
	 * 
	 * @param source
	 * 源文件
	 * @param target
	 * 目标文件
	 */
	private void copyFile(String source, String target) {

		try {
			SmbFile ptpnewFile=new SmbFile(target);
			SmbFile ptpFile=new SmbFile(source);
			SmbFileOutputStream fileOutputStream=null;
			SmbFileInputStream fileInputStream=null;
			SmbFile[] files = ptpFile.listFiles();
			if (!ptpnewFile.exists()) {
				ptpnewFile.mkdirs();
			}else{
				String[] content = ptpnewFile.list();//取得当前目录下所有文件和文件夹
				for(String name : content){
					SmbFile temp = new SmbFile(ptpnewFile, name);
					temp.delete();
				}
			}
			for (SmbFile file : files) {
				fileInputStream=new SmbFileInputStream(file);
				byte[] b=new byte[1024];
				int count=0;
				SmbFile $newFile = new SmbFile(target+"/"+file.getName());
				fileOutputStream=new SmbFileOutputStream($newFile);
				if(!$newFile.exists()){
					$newFile.createNewFile();
				}
				while((count=fileInputStream.read(b,0,1024))!=-1){
					fileOutputStream.write(b, 0, count);
				}
				fileOutputStream.flush();
				fileOutputStream.close();
				fileInputStream.close();

			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SmbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}

}
