/**
 *
 */
package com.mw.framework.serializer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * @Project ROCOWeb
 * @Copyright © 2008-2014 SPRO Technology Consulting Limited. All rights
 *            reserved.
 * @fileName com.mw.framework.serializer.JsonDateFullSerializer.java
 * @Version 1.0.0
 * @author Allan Ai
 * @time 2015-3-11
 * 
 */
public class JsonDateFullSerializer extends JsonSerializer<Date> {

	private static final SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	@Override
	public void serialize(Date date, JsonGenerator jsonGenerator,SerializerProvider serializerProvider) throws IOException,JsonProcessingException {
		jsonGenerator.writeString(sdf.format(date));
	}

}
