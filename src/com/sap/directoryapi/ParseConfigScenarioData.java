package com.sap.directoryapi;
import java.io.*;
import com.sap.aii.mapping.api.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import org.w3c.dom.*;

import com.sap.aii.mapping.api.AbstractTransformation;
import com.sap.aii.mapping.api.StreamTransformationException;
import com.sap.aii.mapping.api.TransformationInput;
import com.sap.aii.mapping.api.TransformationOutput;

public class ParseConfigScenarioData extends AbstractTransformation {

	public static void main(String[] args) throws StreamTransformationException {
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {
			fin = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/ConfigurationScenarioRead_Output.xml");
			fout = new FileOutputStream("target1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ParseConfigScenarioData javaMap = new ParseConfigScenarioData();
		javaMap.execute(fin, fout);
	}

	@Override

	public void transform(TransformationInput transformationIn, TransformationOutput transformationOut)
			throws StreamTransformationException {
		execute(transformationIn.getInputPayload().getInputStream(), transformationOut.getOutputPayload()
				.getOutputStream());
	}

	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		try {

			String PartyID = null;
			String ComponentID = null;
			String ChannelID = null;
			String ConfigurationScenarioID = null;

			String ICODetails = null;			

			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=factory.newDocumentBuilder();
			/*input document in form of XML*/
			Document doc=db.parse(inputStream);
			Element root = doc.getDocumentElement(); 

			NodeList ConfigurationScenarioRoot = root.getElementsByTagName("ConfigurationScenario");
			//System.out.println("PartyID|ComponentID|ChannelID|Direction|TransportProtocol|MessageProtocol|AdapterNamespace|AdapterStatus|AdapterModuleList");  

			for (int i = 0; i < ConfigurationScenarioRoot.getLength(); i++) {  
				
				Element ConfigurationScenarioRootElement = (Element) ConfigurationScenarioRoot.item(i);

				//Fetch Direction, Transport & Message Protocol
				ConfigurationScenarioID= ConfigurationScenarioRootElement.getElementsByTagName("ConfigurationScenarioID").item(0).getTextContent();
				System.out.println(ConfigurationScenarioID);
				NodeList ConfigurationScenariochildNodes = ConfigurationScenarioRoot.item(i).getChildNodes();

				for (int j = 0; j < ConfigurationScenariochildNodes.getLength(); j++) {            		 
					if(ConfigurationScenariochildNodes.item(j).getNodeName() == "IntegratedConfiguration")
					{
						Element IntegratedConfigurationID = (Element) ConfigurationScenariochildNodes.item(j);
						String SenderPartyID = IntegratedConfigurationID.getElementsByTagName("SenderPartyID").item(0).getTextContent();
						String SenderComponentID = IntegratedConfigurationID.getElementsByTagName("SenderComponentID").item(0).getTextContent();
						String InterfaceName = IntegratedConfigurationID.getElementsByTagName("InterfaceName").item(0).getTextContent();
						String InterfaceNamespace = IntegratedConfigurationID.getElementsByTagName("InterfaceNamespace").item(0).getTextContent();
						ICODetails = "ICO"+"|"+SenderComponentID+"|"+InterfaceName+"|"+InterfaceNamespace;
//						System.out.println(ConfigurationScenarioID+"|"+ICODetails); 
					}

					if(ConfigurationScenariochildNodes.item(j).getNodeName() == "ReceiverDetermination")
					{
						Element ReceiverDeterminationID = (Element) ConfigurationScenariochildNodes.item(j);
						String SenderPartyID = ReceiverDeterminationID.getElementsByTagName("SenderPartyID").item(0).getTextContent();
						String SenderComponentID = ReceiverDeterminationID.getElementsByTagName("SenderComponentID").item(0).getTextContent();
						String InterfaceName = ReceiverDeterminationID.getElementsByTagName("InterfaceName").item(0).getTextContent();
						String InterfaceNamespace = ReceiverDeterminationID.getElementsByTagName("InterfaceNamespace").item(0).getTextContent();
						ICODetails = "RcvDet"+"|"+SenderComponentID+"|"+InterfaceName+"|"+InterfaceNamespace;
//						System.out.println(ConfigurationScenarioID+"|"+ICODetails); 
					}						
				}				  
			}

		} catch (Exception exception) {
			getTrace().addDebugMessage(exception.getMessage());
			throw new StreamTransformationException(exception.toString());
		}
	}
}
