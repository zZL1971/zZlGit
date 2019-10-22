package com.mw.framework.model;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlMimeType;

public class CXFWebFileModel {

	private String fileName;
	
	private String fileExtension;
	
	private DataHandler file;

	public final String getFileName() {
		return fileName;
	}

	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public final String getFileExtension() {
		return fileExtension;
	}

	public final void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	@XmlMimeType("application/octet-stream")
	public final DataHandler getFile() {
		return file;
	}

	public final void setFile(DataHandler file) {
		this.file = file;
	}
	
}