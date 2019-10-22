/**
 *
 */
package com.mw.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;
import jcifs.smb.SmbFileOutputStream;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights reserved.
 * @fileName com.mw.framework.utils.JcifsUtils.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-4-24
 *
 */
public class JcifsUtils {

	public static boolean uploadFile(String path,String storageName,MultipartFile multipartFile){
		InputStream inputStream = null;
	    OutputStream outputStream = null;
	    SmbFileOutputStream smbFileOut = null;
	    
	    boolean bool = false;
		try {
			SmbFile file = new SmbFile(path);
			if (!file.exists()){  
			    file.mkdirs();  
			}
			
			smbFileOut = new SmbFileOutputStream(path+"/"+storageName);
			inputStream = new BufferedInputStream(multipartFile.getInputStream()); 
			outputStream = new BufferedOutputStream(smbFileOut); 
           
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, length);
            }
            
            bool = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
            if(smbFileOut!=null){
                try {
                	smbFileOut.close();
                } catch (IOException e) {
                }
            }
        }
		return bool;
	}
	public static boolean uploadFile(String path,String storageName,CommonsMultipartFile commonsMultipartFile){
		InputStream inputStream = null;
	    OutputStream outputStream = null;
	    SmbFileOutputStream smbFileOut = null;
	    
	    boolean bool = false;
		try {
			SmbFile file = new SmbFile(path);
			if (!file.exists()){  
			    file.mkdirs();  
			}
			
			smbFileOut = new SmbFileOutputStream(path+"/"+storageName);
			inputStream = new BufferedInputStream(commonsMultipartFile.getInputStream()); 
			outputStream = new BufferedOutputStream(smbFileOut); 
           
            byte[] b = new byte[2048];
            int length;
            while ((length = inputStream.read(b)) > 0) {
                outputStream.write(b, 0, length);
            }
            
            bool = true;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (SmbException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
            if(smbFileOut!=null){
                try {
                	smbFileOut.close();
                } catch (IOException e) {
                }
            }
        }
		return bool;
	}
	
	public static void downloadFile(String path,String...fileNames){
		
	}
}
