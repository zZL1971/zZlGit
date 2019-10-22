package com.mw.framework.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class DisableIllegal {

	public static boolean DisableIllegal(){
		ProcessBuilder pb = new ProcessBuilder("tasklist");
		  Process p;
		try {
			p = pb.start();
			 BufferedReader out = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getInputStream()), Charset.forName("GB2312")));
			  BufferedReader err = new BufferedReader(new InputStreamReader(new BufferedInputStream(p.getErrorStream())));
			  String ostr;
			    
			  while ((ostr = out.readLine()) != null){
			  	if(ostr.startsWith("按键精灵")){
			  		return false;
			  	}
			  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		return true;
	}
}
