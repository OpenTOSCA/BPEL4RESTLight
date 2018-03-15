package org.opentosca.bpel4restlight.rest;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;

import sun.misc.IOUtils;

/**
 * This static-class eases HTTP-method execution by self-managed fault-handling
 * and automated Response-information processing
 */
public class LowLevelRestApi {
	
	// Local HttpClient used for every communication (Singleton implementation)
	private static HttpClient httpClient = new HttpClient();
	
	private static Logger log = Logger.getLogger(LowLevelRestApi.class.getName());
	/**
	 * Executes a passed HttpMethod (Method type is either PUT, POST, GET or
	 * DELETE) and returns a HttpResponseMessage
	 * 
	 * @param method Method to execute
	 * @return HttpResponseMessage which contains all information about the
	 *         execution
	 */
	public static HttpResponseMessage executeHttpMethod(HttpMethod method) {
		
		HttpResponseMessage responseMessage = null;
		
		try {
			log.info("Method invocation on URI: \n");
			log.info(method.getURI().toString());
			// Execute Request
			LowLevelRestApi.httpClient.executeMethod(method);
			responseMessage = LowLevelRestApi.extractResponseInformation(method);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			// Release Connection anyway
			method.releaseConnection();
		}
		
		// Extract response information and return
		return responseMessage;
	}
	
	/**
	 * Extracts the response information from an executed HttpMethod
	 * 
	 * @param method Executed Method
	 * @return Packaged response information
	 */
	private static HttpResponseMessage extractResponseInformation(HttpMethod method) {
		// Create and return HttpResponseMethod
		HttpResponseMessage responseMessage = new HttpResponseMessage();
		responseMessage.setStatusCode(method.getStatusCode());
		try {
			String responseBody = isToString(method.getResponseBodyAsStream());
			System.out.println("The response from the client:");								
			System.out.println(responseBody);
			responseMessage.setResponseBody(responseBody);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responseMessage;
		
	}
	
	private static String isToString(InputStream inputStream) throws IOException {
		final int bufferSize = 1024;
		final char[] buffer = new char[bufferSize];
		final StringBuilder out = new StringBuilder();
		Reader in = new InputStreamReader(inputStream, "UTF-8");
		for (; ; ) {
		    int rsz = in.read(buffer, 0, buffer.length);
		    if (rsz < 0)
		        break;
		    out.append(buffer, 0, rsz);
		}
		return out.toString();
	}
	
}
