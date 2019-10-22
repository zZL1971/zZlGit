package com.main.util;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.apache.log4j.Logger;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.main.domain.mm.MaterialFile;
import com.main.domain.sale.SaleBgfile;
import com.mw.framework.utils.DateTools;


public class MyFileUtil {
     public static  String FILE_DIR = "/";
     private static Logger logger =Logger.getLogger(MyFileUtil.class);
     
     public static boolean bgUpload(SaleBgfile saleBgfile) {
    	 Date date=new Date();
    	 date.setTime(System.currentTimeMillis());
    	 logger.info("fileUpload"+DateTools.getDateAndTime(date, DateTools.defaultFormat));
    	 
         boolean flag = true;
             InputStream inputStream = null; 
             OutputStream outputStream = null; 
             SmbFileOutputStream file_out = null;
             try{
                 String path = saleBgfile.getUploadFilePath();
                 //System.out.println(path);
                 SmbFile file = new SmbFile(path);
                 if (!file.exists()){  
                     file.mkdirs();  
                 } 
                 CommonsMultipartFile commonsMultipartFile = saleBgfile.getFile();
                
                 file_out = new SmbFileOutputStream(path+"/"+saleBgfile.getUploadFileName());
                 inputStream = new BufferedInputStream(commonsMultipartFile.getInputStream()); 
                 outputStream = new BufferedOutputStream(file_out); 
                
                 byte[] b = new byte[2048];
                 int length;
                 while ((length = inputStream.read(b)) > 0) {
                     outputStream.write(b, 0, length);
                 }
                 
                 return flag;
                 
            }catch (Exception e) { 
                 e.printStackTrace();
                 flag = false;
                 return flag;
            }finally{
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(file_out!=null){
                    try {
                    	file_out.close();
                    } catch (IOException e) {
                    }
                }
            }
    }
     public static boolean fileUpload(MaterialFile materialFile){
    	 Date date=new Date();
    	 date.setTime(System.currentTimeMillis());
    	 logger.info("fileUpload"+DateTools.getDateAndTime(date, DateTools.defaultFormat));
    	 
         boolean flag = true;
             InputStream inputStream = null; 
             OutputStream outputStream = null; 
             SmbFileOutputStream file_out = null;
             try{
                 String path = materialFile.getUploadFilePath();
                 //System.out.println(path);
                 SmbFile file = new SmbFile(path);
                 if (!file.exists()){  
                     file.mkdirs();  
                 } 
                 CommonsMultipartFile commonsMultipartFile = materialFile.getFile();
                
                 file_out = new SmbFileOutputStream(path+"/"+materialFile.getUploadFileName());
                 inputStream = new BufferedInputStream(commonsMultipartFile.getInputStream()); 
                 outputStream = new BufferedOutputStream(file_out); 
                
                 byte[] b = new byte[2048];
                 int length;
                 while ((length = inputStream.read(b)) > 0) {
                     outputStream.write(b, 0, length);
                 }
                 
                 return flag;
                 
            }catch (Exception e) { 
                 e.printStackTrace();
                 flag = false;
                 return flag;
            }finally{
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(file_out!=null){
                    try {
                    	file_out.close();
                    } catch (IOException e) {
                    }
                }
            }
    }
     public static boolean fileUploadBy2020(MaterialFile materialFile,InputStream input){
    	 boolean flag = true;
         InputStream inputStream = null; 
         OutputStream outputStream = null; 
         SmbFileOutputStream file_out = null;
         try{
             String path = materialFile.getUploadFilePath();
             SmbFile file = new SmbFile(path);
             if (!file.exists()){  
                 file.mkdirs();  
             } 
             file_out = new SmbFileOutputStream(path+"/"+materialFile.getUploadFileName());
             inputStream = new BufferedInputStream(input); 
             outputStream = new BufferedOutputStream(file_out); 
             byte[] b = new byte[2048];
             int length;
             while ((length = inputStream.read(b)) > 0) {
                 outputStream.write(b, 0, length);
             }
             return flag;
        }catch (Exception e) { 
             e.printStackTrace();
             flag = false;
             return flag;
        }finally{
            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
            }
            if(file_out!=null){
                try {
                	file_out.close();
                } catch (IOException e) {
                }
            }
        }
     }
     public static boolean fileUpload2020(MaterialFile materialFile,InputStream input){
    	 Date date=new Date();
    	 date.setTime(System.currentTimeMillis());
    	 logger.info("fileUpload"+DateTools.getDateAndTime(date, DateTools.defaultFormat));
         boolean flag = true;
             InputStream inputStream = null; 
             OutputStream outputStream = null; 
             SmbFileOutputStream file_out = null;
             try{
                 String path = materialFile.getUploadFilePath();
                 //System.out.println(path);
                 SmbFile file = new SmbFile(path);
                 if (!file.exists()){  
                     file.mkdirs();  
                 } 
                 file_out = new SmbFileOutputStream(path+"/"+materialFile.getUploadFileName());
                 inputStream = new BufferedInputStream(input); 
                 outputStream = new BufferedOutputStream(file_out); 
                
                 byte[] b = new byte[2048];
                 int length;
                 while ((length = inputStream.read(b)) > 0) {
                     outputStream.write(b, 0, length);
                 }
                 outputStream.close();
                 inputStream.close();
                 return flag;
                 
            }catch (Exception e) { 
                 e.printStackTrace();
                 flag = false;
                 return flag;
            }finally{
                if(outputStream!=null){
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(inputStream!=null){
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
                if(file_out!=null){
                    try {
                    	file_out.close();
                    } catch (IOException e) {
                    }
                }
            }
    }
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            //System.out.println("11");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


