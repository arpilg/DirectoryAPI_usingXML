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

public class ParseCommChannelData extends AbstractTransformation {

	public static void main(String[] args) throws StreamTransformationException {
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {
			fin = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/PI_PRD_CommChannelRead_Output.xml");
			fout = new FileOutputStream("target.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ParseCommChannelData javaMap = new ParseCommChannelData();
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
			String Direction = null;
			String TransportProtocol = null;
			String MessageProtocol = null;
			String AdapterStatusParam =null;
			String AdapterStatus =null;
			String AdapterNamespace = null;			

			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=factory.newDocumentBuilder();
			/*input document in form of XML*/
			Document doc=db.parse(inputStream);
			Element root = doc.getDocumentElement(); 

			NodeList CommunicationChannelRoot = root.getElementsByTagName("CommunicationChannel");
			System.out.println("PartyID|ComponentID|ChannelID|Direction|TransportProtocol|MessageProtocol|AdapterNamespace|AdapterStatus|AdapterModuleList");  

			for (int i = 0; i < CommunicationChannelRoot.getLength(); i++) {  
				String AdapterModuleList = "";
				Element CommunicationChannelRootElement = (Element) CommunicationChannelRoot.item(i);

				//Fetch Direction, Transport & Message Protocol
				Direction= CommunicationChannelRootElement.getElementsByTagName("Direction").item(0).getTextContent();
				TransportProtocol = CommunicationChannelRootElement.getElementsByTagName("TransportProtocol").item(0).getTextContent();
				MessageProtocol = CommunicationChannelRootElement.getElementsByTagName("MessageProtocol").item(0).getTextContent();

				NodeList CommunicationChannelchildNodes = CommunicationChannelRoot.item(i).getChildNodes();

				for (int j = 0; j < CommunicationChannelchildNodes.getLength(); j++) {            		 
					if(CommunicationChannelchildNodes.item(j).getNodeName() == "CommunicationChannelID")
					{
						Element CommunicationChannelID = (Element) CommunicationChannelchildNodes.item(j);
						PartyID = CommunicationChannelID.getElementsByTagName("PartyID").item(0).getTextContent();
						ComponentID = CommunicationChannelID.getElementsByTagName("ComponentID").item(0).getTextContent();
						ChannelID = CommunicationChannelID.getElementsByTagName("ChannelID").item(0).getTextContent();
					}

					if(CommunicationChannelchildNodes.item(j).getNodeName() == "AdapterMetadata")
					{
						Element AdapterMetadata = (Element) CommunicationChannelchildNodes.item(j);
						AdapterNamespace = AdapterMetadata.getElementsByTagName("Namespace").item(0).getTextContent();
					}

					if(CommunicationChannelchildNodes.item(j).getNodeName() == "AdapterSpecificAttribute")
					{
						Element AdapterSpecificAttribute = (Element) CommunicationChannelchildNodes.item(j);
						AdapterStatusParam = AdapterSpecificAttribute.getElementsByTagName("Name").item(0).getTextContent();
						if(AdapterStatusParam.contains("adapterStatus"))
							AdapterStatus = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
					}
					
					
					if(CommunicationChannelchildNodes.item(j).getNodeName() == "ModuleProcess")
					{
						String AdapterModuleName=null;						
						Node ModuleProcess = CommunicationChannelchildNodes.item(j);
						NodeList ModuleProcessnl= ModuleProcess.getChildNodes();
						
						for (int x = 0; x < ModuleProcessnl.getLength(); x++)
						{ 
							if(ModuleProcessnl.item(x).getNodeName() == "ProcessStep")
							{
								Element moduleElement = (Element) ModuleProcessnl.item(x);
								AdapterModuleName=moduleElement.getElementsByTagName("ModuleName").item(0).getTextContent();
								AdapterModuleList = AdapterModuleList + ";" + AdapterModuleName;
							}
						}
					}

				}

				System.out.println(PartyID+"|"+ComponentID+"|"+ChannelID+"|"+Direction+"|"+TransportProtocol+"|"+MessageProtocol+"|"+AdapterNamespace+"|"+AdapterStatus+"|"+AdapterModuleList);   
			}

		} catch (Exception exception) {
			getTrace().addDebugMessage(exception.getMessage());
			throw new StreamTransformationException(exception.toString());
		}
	}
}
