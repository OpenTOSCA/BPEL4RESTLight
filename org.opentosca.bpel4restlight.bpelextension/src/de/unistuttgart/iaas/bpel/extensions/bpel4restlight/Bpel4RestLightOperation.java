package de.unistuttgart.iaas.bpel.extensions.bpel4restlight;

import java.util.logging.Logger;

import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.runtime.extension.AbstractSyncExtensionOperation;
import org.apache.ode.bpel.runtime.extension.ExtensionContext;
import org.opentosca.bpel4restlight.rest.HighLevelRestApi;
import org.opentosca.bpel4restlight.rest.HttpMethod;
import org.opentosca.bpel4restlight.rest.HttpResponseMessage;
import org.w3c.dom.Element;

import com.sun.tools.javac.util.Log;

import de.unistuttgart.iaas.bpel.extensions.bpel4restlight.util.Bpel4RestLightUtil;
import de.unistuttgart.iaas.bpel.util.BPELVariableInjectionUtil;
import de.unistuttgart.iaas.bpel.util.BpelUtil;
import de.unistuttgart.iaas.xml.DomXmlConverter;

/**
 * 
 * Copyright 2011 IAAS University of Stuttgart <br>
 * <br>
 * 
 * This class provides 4 BPEL4RestLight ExtensionActivity-operations which
 * correspond to the 4 typical REST-Operations GET, PUT, POST and Delete.
 * 
 * @author uwe.breitenbuecher@iaas.uni-stuttgart.de
 * 
 */
public class Bpel4RestLightOperation extends AbstractSyncExtensionOperation {
	
	public static String wrapper = null;
	
	private Logger log = Logger.getLogger(this.getClass().toString());
	
	
	private void processResponseMessage(HttpResponseMessage responseMessage, ExtensionContext context, Element element) throws FaultException {
		// Write responsePayload to designated variable
		String responsePayloadVariableName = Bpel4RestLightUtil.getMethodAttributeValue(element, MethodAttribute.RESPONSEPAYLOADVARIABLE);
		String statusCodeVariableName = Bpel4RestLightUtil.getMethodAttributeValue(element, MethodAttribute.STATUSCODEVARIABLE);		
		
		if (responsePayloadVariableName != null && !responsePayloadVariableName.equals("")) {
			BpelUtil.writeResponsePayloadToVariable(context, responseMessage.getResponseBody().trim(), responsePayloadVariableName, Bpel4RestLightOperation.wrapper);
		}
		
		if (statusCodeVariableName != null && !statusCodeVariableName.equals("")) {
			BpelUtil.writeResponsePayloadToVariable(context, responseMessage.getStatusCode(), statusCodeVariableName, Bpel4RestLightOperation.wrapper);
		}
	}
	
	/** {@inheritDoc} */
	@Override
	protected void runSync(ExtensionContext context, Element element) throws FaultException {
		System.out.println("#########################################" + "\n");
		
		System.out.println("Starting REST Call with the following content: \n" + DomXmlConverter.nodeToString(element, null) + "\n");
		
		element = BPELVariableInjectionUtil.replaceExtensionVariables(context, element);
		
		System.out.println("Starting REST Call with the following replaced content: \n" + DomXmlConverter.nodeToString(element, null) + "\n");
				
		String httpMethod = element.getLocalName();
		
		// Extract requestUri
		String requestUri = Bpel4RestLightUtil.getMethodAttributeValue(element, MethodAttribute.REQUESTURI);
		String contentType = Bpel4RestLightUtil.getMethodAttributeValue(element, MethodAttribute.CONTENTTYPE);
		
		HttpResponseMessage responseMessage = null;
		
		// Execute corresponding HttpMethod via the HighLevelRestApi
		switch (HttpMethod.valueOf(httpMethod)) {
		
			case PUT: {
				String requestPayload = Bpel4RestLightUtil.extractRequestPayload(context, element);
				String acceptHeader = Bpel4RestLightUtil.extractAcceptHeader(context, element);
				responseMessage = HighLevelRestApi.Put(requestUri, requestPayload, acceptHeader, contentType);
				break;
			}
			
			case POST: {
				String requestPayload = Bpel4RestLightUtil.extractRequestPayload(context, element);
				String acceptHeader = Bpel4RestLightUtil.extractAcceptHeader(context, element);
				responseMessage = HighLevelRestApi.Post(requestUri, requestPayload, acceptHeader, contentType);
				break;
			}
			
			case GET: {
				String acceptHeader = Bpel4RestLightUtil.extractAcceptHeader(context, element);
				responseMessage = HighLevelRestApi.Get(requestUri, acceptHeader, contentType);
				break;
			}
			
			case DELETE: {
				String acceptHeader = Bpel4RestLightUtil.extractAcceptHeader(context, element);
				responseMessage = HighLevelRestApi.Delete(requestUri, acceptHeader, contentType);
				break;
			}
		}
		
		processResponseMessage(responseMessage, context, element);
		Bpel4RestLightOperation.wrapper = null;
	}
}
