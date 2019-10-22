package com.webservice;

import java.net.MalformedURLException;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

public class Test {
	public static void main(String[] args) {
		try {
			SmbFile file = new SmbFile("smb://administrator:ROCO1234567@172.16.3.205/inbox");
			
			if (!file.exists()){  
			    file.mkdirs();  
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}
