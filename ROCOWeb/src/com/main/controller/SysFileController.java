package com.main.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileInputStream;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.main.domain.sale.SaleBgfile;
import com.main.domain.sys.SysFile;
import com.main.manager.SysFileManager;
import com.main.util.MyFileUtil;
import com.mw.framework.bean.Constants;
import com.mw.framework.bean.Message;
import com.mw.framework.commons.BaseController;
import com.mw.framework.domain.SysDataDict;
import com.mw.framework.manager.CommonManager;
import com.mw.framework.mapper.MapRowMapper;
import com.mw.framework.model.JdbcExtGridBean;
import com.mw.framework.utils.DateTools;
import com.mw.framework.utils.JcifsUtils;
import com.mw.framework.utils.ZStringUtils;
import com.mw.framework.utils.ZipUtils;

/**
 *
 */
@Controller
@RequestMapping("/main/sysFile/*")
public class SysFileController extends BaseController {

	private static final Logger logger = LoggerFactory
			.getLogger(SysFileController.class);
	@Autowired
	private CommonManager commonManager;

	@Autowired
	private SysFileManager sysFileManager;

	/**
	 * 文件上传 spring MVC 文件上传
	 * 
	 * @param sysFile
	 * @param result
	 * @return
	 */
	@RequestMapping(value = "/fileupload", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> fileupload(@Valid SysFile sysFile,
			BindingResult result) {
		Message msg = null;
		// 判断前台输入的值类型是否跟后台的匹配
		if (result.hasErrors()) {
			StringBuffer sb = new StringBuffer();
			// 显示错误信息
			List<FieldError> fieldErrors = result.getFieldErrors();
			for (FieldError fieldError : fieldErrors) {
				sb.append(fieldError.getField() + "|"
						+ fieldError.getDefaultMessage());
			}
			String json = "{success: false}";
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		} else {
			try {
				//获取配置信息
				if(StringUtils.isEmpty(sysFile.getFileType()))
				{
					sysFile.setFileType("HKPZ");
				}
	        	Map<String,String[]> parameterMap = new LinkedHashMap<String, String[]>();
	        	parameterMap.put("ICEQtrieTree__keyVal", new String[]{"FILE_PATH"});
	        	parameterMap.put("ICEQkeyVal", new String[]{"FILE"});
	            List<SysDataDict> queryByRange = commonManager.queryByRange(SysDataDict.class, parameterMap);
	             
	            String smbPath = null;
	            if(queryByRange.size()>0){
	            	smbPath = queryByRange.get(0).getDescEnUs();
	            	String format = DateTools.getNowDateYYMMDD();
	            	smbPath=smbPath+Constants.FILE_DIR+sysFile.getFileType()+Constants.FILE_DIR+format;
	            }else{
	            	String json = "{success: false}";
					HttpHeaders responseHeaders = new HttpHeaders();
					responseHeaders.setContentType(MediaType.TEXT_HTML);
					return new ResponseEntity<String>(json, responseHeaders,
							HttpStatus.INTERNAL_SERVER_ERROR);
	            }
				
				String uuid = UUID.randomUUID().toString().replace("-", "");
				CommonsMultipartFile commonsMultipartFile = sysFile.getFile();
				String oldName = commonsMultipartFile.getOriginalFilename();
                oldName = oldName.substring(oldName.lastIndexOf("."));
				sysFile.setUploadFileName(uuid.replace("-","")+oldName);
				sysFile.setUploadFileNameOld(commonsMultipartFile
						.getOriginalFilename());
				sysFile.setUploadFilePath(smbPath);
				
				boolean uploadFile = JcifsUtils.uploadFile(sysFile.getUploadFilePath(),sysFile.getUploadFileName(), commonsMultipartFile);
            	//保存操作记录
            	if(uploadFile){
            		SysFile obj = sysFileManager.save(sysFile);
            	}else{
                	msg = new Message("FILE-UP-503","Execl存储至文件服务器失败");
                }
			} catch (Exception e) {
				e.printStackTrace();
				String json = "{success: false}";
				HttpHeaders responseHeaders = new HttpHeaders();
				responseHeaders.setContentType(MediaType.TEXT_HTML);
				return new ResponseEntity<String>(json, responseHeaders,
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			String json = "{success: true}";
			HttpHeaders responseHeaders = new HttpHeaders();
			responseHeaders.setContentType(MediaType.TEXT_HTML);
			return new ResponseEntity<String>(json, responseHeaders,
					HttpStatus.OK);
		}
	}
	
	/**
	 * 补购附件文件下载
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/bjFileDownload")
	public Message bjFileDownload(String id, HttpServletRequest request,
			HttpServletResponse response) {
		//System.out.println("文件下载："+id);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Message msg = null;
		try {
			SaleBgfile obj = commonManager.getOne(id, SaleBgfile.class);
			if (obj != null) {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("multipart/form-data");
				response.setHeader("Content-Disposition",
						"attachment;fileName="
								+ new String(obj.getUploadFileNameOld()
										.getBytes("gbk"), "ISO8859-1"));
				
				String uploadFileName = obj.getUploadFileName();
				String uploadFilePath = obj.getUploadFilePath();
				inputStream = new BufferedInputStream(new SmbFileInputStream(new SmbFile(uploadFilePath + MyFileUtil.FILE_DIR + uploadFileName)));
				
				if(inputStream!=null){
					outputStream = response.getOutputStream();
					byte[] b = new byte[2048];
					int length;
					while ((length = inputStream.read(b)) > 0) {
						outputStream.write(b, 0, length);
					}
				}
			} else {
				msg = new Message("MM-S-500", "");
				return msg;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 文件下载
	 * 
	 * @param id
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/fileDownload")
	public Message fileDownload(String id, HttpServletRequest request,
			HttpServletResponse response) {
		//System.out.println("文件下载："+id);
		InputStream inputStream = null;
		OutputStream outputStream = null;
		Message msg = null;
		try {
			SysFile obj = commonManager.getOne(id, SysFile.class);
			if (obj != null) {
				response.setCharacterEncoding("UTF-8");
				response.setContentType("multipart/form-data");
				response.setHeader("Content-Disposition",
						"attachment;fileName="
								+ new String(obj.getUploadFileNameOld()
										.getBytes("gbk"), "ISO8859-1"));
				
				String uploadFileName = obj.getUploadFileName();
				String uploadFilePath = obj.getUploadFilePath();
				inputStream = new BufferedInputStream(new SmbFileInputStream(new SmbFile(uploadFilePath + MyFileUtil.FILE_DIR + uploadFileName)));
				
				if(inputStream!=null){
					outputStream = response.getOutputStream();
					byte[] b = new byte[2048];
					int length;
					while ((length = inputStream.read(b)) > 0) {
						outputStream.write(b, 0, length);
					}
				}
			} else {
				msg = new Message("MM-S-500", "");
				return msg;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		} catch (IOException e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
				}
			}
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * 查询相关上传文件
	 * 
	 * @param foreignId
	 * @param fileType
	 * @return
	 */
	@RequestMapping(value = "/querySysFile", method = RequestMethod.GET)
	@ResponseBody
	public JSONArray queryMaterialFile(String foreignId, String fileType) {
		List<SysFile> list = sysFileManager.querySysFile(foreignId);
		String[] strings = new String[] { "hibernateLazyInitializer",
				"handler", "fieldHandler", "sort", "uploadFileName",
				"uploadFilePath", "file" };
		//System.out.println(JSONArray.fromObject(list, super
		//		.getJsonConfig(strings)));
		return JSONArray.fromObject(list, super.getJsonConfig(strings));

	}

	/**
	 * 根据id数组 删除SysFile
	 * 
	 * @param ids
	 * @return @
	 */
	@RequestMapping(value = "/deleteSysFileByIds", method = RequestMethod.POST)
	@ResponseBody
	public Message deleteMaterialFileByIds(String[] ids) {
		Message msg = null;
		try {
			String path = "/upload/sys";
			String realPath = super.getSession().getServletContext()
					.getRealPath(path);
			for (int i = 0; i < ids.length; i++) {
				SysFile obj = commonManager.getOne(ids[i], SysFile.class);
				if (obj != null) {
					String foreignId = obj.getForeignId();
					String uploadFileName = obj.getUploadFileName();
					FileUtils.deleteQuietly(new File(realPath + File.separator
							+ foreignId + File.separator + uploadFileName));
				}
				sysFileManager.delete(ids[i], SysFile.class);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = new Message("MM-S-500", e.getLocalizedMessage());
		}
		return msg;
	}
	
	/**
	 * 批量下载文件
	 * @param ids 订单IDS
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/fileloadBatch/{ids}")
	@ResponseBody
	public Message fileloadBatch(@PathVariable String [] ids, HttpServletRequest request,
	HttpServletResponse response){
		List<SmbFile> smbFileList=new ArrayList<SmbFile>();
		List<Map<String,SmbFile>> smbFileMapList=new ArrayList<Map<String,SmbFile>>();
		List<Object[]> idList=new ArrayList<Object[]>();
		for(int i=0;i<ids.length;i++){
			String param[] =ids[i].split(":");
			//System.out.println(param.length);
			if(param.length<2){
				continue;
			}
			String id=param[0];
			String fileName=param[1];//asdsd02255
			if(ZStringUtils.isEmpty(id)){
				continue;
			}
			idList.add(new Object[]{id});
			List<SysFile> sysFileList=sysFileManager.querySysFile(id);
			for(int j=0;j<sysFileList.size();j++){
				SysFile sysfile=sysFileList.get(j);
				Map<String,SmbFile> mapSmbFile=new HashMap<String, SmbFile>();
				String fid=sysfile.getId();
				SysFile obj = commonManager.getOne(fid, SysFile.class);
				String uploadFileName = obj.getUploadFileName();//保存在服务器的文件名称
				String uploadFileNameOld = obj.getUploadFileNameOld();//上传文件名称
				String uploadFilePath = obj.getUploadFilePath();
				SmbFile smbFile=null;
				try {
					smbFile = new SmbFile(uploadFilePath + MyFileUtil.FILE_DIR + uploadFileName);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				mapSmbFile.put(fileName+"_"+(j+1)+"_"+uploadFileNameOld, smbFile);
				smbFileMapList.add(mapSmbFile);
				smbFileList.add(smbFile);	
			}	
		}
		ZipUtils.downloadZip(smbFileMapList,null,response,request);
		String update_loadTime="update sale_header h set h.load_time=nvl(h.load_time,0)+1 where h.id=?";
		jdbcTemplate.batchUpdate(update_loadTime, idList);
		return new Message("下载成功");
	}
	
}
