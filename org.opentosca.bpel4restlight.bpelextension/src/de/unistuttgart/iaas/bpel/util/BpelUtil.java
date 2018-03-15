package de.unistuttgart.iaas.bpel.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.ode.bpel.common.FaultException;
import org.apache.ode.bpel.evar.ExternalVariableModuleException;
import org.apache.ode.bpel.o.OScope.Variable;
import org.apache.ode.bpel.runtime.extension.ExtensionContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Copyright 2011 IAAS University of Stuttgart <br>
 * <br>
 * 
 * @author uwe.breitenbuecher@iaas.uni-stuttgart.de
 * 
 */
public class BpelUtil {
	
	private static Logger log = Logger.getLogger(BpelUtil.class.getName());
	
	/**
	 * This function writes a passed content to a specified processVariable
	 * (referenced by name). The content will be converted into its
	 * DOM-Representation for overwriting the processVariableContent (therefore
	 * it has to be XML-serializable, e.g. for complex data types there have to
	 * be JAX-B Annotations within the corresponding Class)
	 * 
	 * @param context ExtensionContext needed to access the processVariable
	 * @param content New content for the specified processVariable
	 * @param processVariableName Variable whose content has to be overwritten
	 * @throws FaultException
	 */
	public static void writeContentToBPELVariable(ExtensionContext context, Object content, String processVariableName, String wrapper) throws FaultException {		
		System.out.println("Writing to BPELVariable: " + processVariableName  + "\n");
		System.out.println("Content: " + content  + "\n");
		// check the node		
		// small hack for test
		Node hackNode = null;		
		
		if (wrapper != null) {
			// a hack for ode's wrapping
			content = "<" + wrapper + ">" + (String) content + "</" + wrapper + ">";
		}
		try {
			hackNode = stringToDom((String) content);
		} catch (ParserConfigurationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SAXException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Variable bpelVariable = context.getVisibleVariables().get(processVariableName);
		if (hackNode == null) {			
			log.severe("hackNode is null! \n");
		}
		if (bpelVariable == null) {
			log.severe("bpelVariable is null! \n");
		}
		try {
			// replaced responseAsNode to hackNode
			context.writeVariable(bpelVariable, hackNode);
		} catch (ExternalVariableModuleException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * This function writes a String to a BPEL Variable of type XSD-String
	 * 
	 * @param context ExtensionContext
	 * @param responsePayload ResponsePayload as String
	 * @param processVariableName Name of the target BPEL variable
	 * @throws FaultException
	 */
	public static void writeResponsePayloadToVariable(ExtensionContext context, Object responsePayload, String processVariableName, String wrapper) throws FaultException {
		BpelUtil.writeContentToBPELVariable(context, responsePayload, processVariableName, wrapper);
	}
	
	private static Node stringToDom(String xmlString) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xmlString));
		Document d = builder.parse(is);
		return d.getFirstChild();
	}
	
}
