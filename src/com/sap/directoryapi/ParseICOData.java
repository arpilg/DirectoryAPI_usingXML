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

public class ParseICOData extends AbstractTransformation {

	public static void main(String[] args) throws StreamTransformationException {
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {
			fin = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/ICORead_Output.xml");
			fout = new FileOutputStream("target1.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ParseICOData javaMap = new ParseICOData();
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

			String ConfigurationScenarioID = null;
			String SenderChannelDetails =null;
			String RcvChannelDetails =null;	
			String ICODetails =null;
			
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=factory.newDocumentBuilder();
			/*input document in form of XML*/
			Document doc=db.parse(inputStream);
			Element root = doc.getDocumentElement(); 

			NodeList IntegratedConfigurationRoot = root.getElementsByTagName("IntegratedConfiguration");

			for (int i = 0; i < IntegratedConfigurationRoot.getLength(); i++) {  
				String ICOHeader = "";
				String ICOSenderDetails = "";
				ICODetails = "";
				
				Element IntegratedConfigurationRootElement = (Element) IntegratedConfigurationRoot.item(i);
				NodeList OutboundProcessinglist = IntegratedConfigurationRootElement.getElementsByTagName("OutboundProcessing");
				
				NodeList IntegratedConfigurationchildNodes = IntegratedConfigurationRootElement.getChildNodes();

				for (int j = 0; j < IntegratedConfigurationchildNodes.getLength(); j++) { 

					if(IntegratedConfigurationchildNodes.item(j).getNodeName() == "IntegratedConfigurationID")
					{
						Element IntegratedConfigurationID = (Element) IntegratedConfigurationchildNodes.item(j);
						
						String SenderComponentID = IntegratedConfigurationID.getElementsByTagName("SenderComponentID").item(0).getTextContent();
						String InterfaceName = IntegratedConfigurationID.getElementsByTagName("InterfaceName").item(0).getTextContent();
						String InterfaceNamespace = IntegratedConfigurationID.getElementsByTagName("InterfaceNamespace").item(0).getTextContent();
						ICOHeader = InterfaceName+"|"+InterfaceNamespace+"|"+SenderComponentID;
						//System.out.println(ICOHeader);
					}
					
					if(IntegratedConfigurationchildNodes.item(j).getNodeName() == "InboundProcessing")
					{
						Element InboundProcessingID = (Element) IntegratedConfigurationchildNodes.item(j);
						Element SenderCommChannelItem = (Element) InboundProcessingID.getElementsByTagName("CommunicationChannel").item(0);
						String ComponentID = SenderCommChannelItem.getElementsByTagName("ComponentID").item(0).getTextContent();
						String ChannelID = SenderCommChannelItem.getElementsByTagName("ChannelID").item(0).getTextContent();
						
						ICOSenderDetails = ICOHeader+"|"+ChannelID;
					}
					
					if(IntegratedConfigurationchildNodes.item(j).getNodeName() == "OutboundProcessing")
					{
						Element OutboundProcessingID = (Element) IntegratedConfigurationchildNodes.item(j);
						
						Element ReceiverInterfaceItem = (Element) OutboundProcessingID.getElementsByTagName("ReceiverInterface").item(0);
						Element ReceiverCommChannelItem = (Element) OutboundProcessingID.getElementsByTagName("CommunicationChannel").item(0);						
						
						String name = ReceiverInterfaceItem.getElementsByTagName("Name").item(0).getTextContent();
						String namespace = ReceiverInterfaceItem.getElementsByTagName("Namespace").item(0).getTextContent();
						
						String ComponentID = ReceiverCommChannelItem.getElementsByTagName("ComponentID").item(0).getTextContent();
						String ChannelID = ReceiverCommChannelItem.getElementsByTagName("ChannelID").item(0).getTextContent();
						
						ICODetails = ICOSenderDetails+"|"+name+"|"+namespace+"|"+ComponentID+"|"+ChannelID;
						System.out.println(ICOHeader);
					}		
					
//					for (int k = 0; k < OutboundProcessinglist.getLength(); k++) { 
//						Element OutboundProcessingID = (Element) IntegratedConfigurationchildNodes.item(k);
//						Element ReceiverInterfaceItem = (Element) OutboundProcessingID.getElementsByTagName("ReceiverInterface").item(0);
//						Element ReceiverCommChannelItem = (Element) OutboundProcessingID.getElementsByTagName("CommunicationChannel").item(0);	
//						
//						String name = ReceiverInterfaceItem.getElementsByTagName("Name").item(0).getTextContent();
//						String namespace = ReceiverInterfaceItem.getElementsByTagName("Namespace").item(0).getTextContent();
//						
//						String RcvComponentID = ReceiverCommChannelItem.getElementsByTagName("ComponentID").item(0).getTextContent();
//						String RcvChannelID = ReceiverCommChannelItem.getElementsByTagName("ChannelID").item(0).getTextContent();
//					}
				}				  
			}

		} catch (Exception exception) {
			getTrace().addDebugMessage(exception.getMessage());
			throw new StreamTransformationException(exception.toString());
		}
	}
}
