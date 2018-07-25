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

public class ParseFileChannelData extends AbstractTransformation {

	public static void main(String[] args) throws StreamTransformationException
	{
		FileInputStream fin = null;
		FileOutputStream fout = null;

		try {
			fin = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/PI_PRD_CommChannelRead_Output.xml");
			fout = new FileOutputStream("target.txt");
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		ParseFileChannelData javaMap = new ParseFileChannelData();
		javaMap.execute(fin, fout);
	}

	@Override

	public void transform(TransformationInput transformationIn, TransformationOutput transformationOut) throws StreamTransformationException {
		execute(transformationIn.getInputPayload().getInputStream(), transformationOut.getOutputPayload().getOutputStream());
	}

	public void execute(InputStream inputStream, OutputStream outputStream) throws StreamTransformationException {
		try {

			String PartyID = null;
			String ComponentID = null;
			String ChannelID = null;
			String Direction = null;
			String TransportProtocol = null;
			String MessageProtocol = null;
			String AdapterStatus =null;
			String AdapterNamespace = null;
			String sep = "|" ;
			String sep2 = " && " ;
			String OutputDirectory = null;
			String OutputFileName = null;
			String IsDynamicConfig = null;


			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=factory.newDocumentBuilder();
			/*input document in form of XML*/
			Document doc=db.parse(inputStream);
			Element root = doc.getDocumentElement(); 

			NodeList CommunicationChannelRoot = root.getElementsByTagName("CommunicationChannel");
			System.out.println("PartyID|ComponentID|ChannelID|Direction|TransportProtocol|MessageProtocol|AdapterNamespace|AdapterStatus|AdapterModuleList|Folder|Filename|ftphost|ftpport|ftpuser|DynamicConfig?|SFTPFingerprint");  

			for (int i = 0; i < CommunicationChannelRoot.getLength(); i++)
			{	
				
				Element CommunicationChannelSingle = (Element) CommunicationChannelRoot.item(i);
				StringBuffer FileChannelData = new StringBuffer("");
				StringBuffer AdapterModuleList = new StringBuffer("");
				
				//To Delete
				String AuthMethod = "";
				
				NodeList tempAdapterSpecAttribute = CommunicationChannelSingle.getElementsByTagName("AdapterSpecificAttribute");
				for(int k=0; k < tempAdapterSpecAttribute.getLength();k++)
				{
					Element tempAdapterSpecificAttribute = (Element) tempAdapterSpecAttribute.item(k);	
					String tempAdapterStatusParam = tempAdapterSpecificAttribute.getElementsByTagName("Name").item(0).getTextContent();
					if(tempAdapterStatusParam.contains("authMethod"))
					{
						AuthMethod = tempAdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
					}	
				}
				//To Delete
				

				//Fetch Direction, Transport & Message Protocol
				Direction= CommunicationChannelSingle.getElementsByTagName("Direction").item(0).getTextContent();
				TransportProtocol = CommunicationChannelSingle.getElementsByTagName("TransportProtocol").item(0).getTextContent();
				MessageProtocol = CommunicationChannelSingle.getElementsByTagName("MessageProtocol").item(0).getTextContent();

				Element CommunicationChannelID = (Element) CommunicationChannelSingle.getElementsByTagName("CommunicationChannelID").item(0);
				Element AdapterMetadata = (Element) CommunicationChannelSingle.getElementsByTagName("AdapterMetadata").item(0);
				Element ModuleProcess = (Element) CommunicationChannelSingle.getElementsByTagName("ModuleProcess").item(0);
				NodeList ProcessStep = ModuleProcess.getElementsByTagName("ProcessStep");				

				PartyID = CommunicationChannelID.getElementsByTagName("PartyID").item(0).getTextContent();
				ComponentID = CommunicationChannelID.getElementsByTagName("ComponentID").item(0).getTextContent();
				ChannelID = CommunicationChannelID.getElementsByTagName("ChannelID").item(0).getTextContent();
				AdapterNamespace = AdapterMetadata.getElementsByTagName("Namespace").item(0).getTextContent();

				for (int x = 0; x < ProcessStep.getLength(); x++)
				{ 
					Element moduleElement = (Element) ProcessStep.item(x);
					String AdapterModuleName=moduleElement.getElementsByTagName("ModuleName").item(0).getTextContent();
					AdapterModuleList.append(AdapterModuleName).append(sep2);
				}

				if(TransportProtocol.contains("File") || TransportProtocol.contains("FTP"))
				{
					OutputDirectory = "";
					OutputFileName = "";
					IsDynamicConfig = "";
					String ftphost ="";
					String ftpport ="";
					String ftpuser ="";			
					String keyStore ="";
					String clientCertificate = "";

					NodeList AdapterSpecAttribute = CommunicationChannelSingle.getElementsByTagName("AdapterSpecificAttribute");
					for(int x=0; x < AdapterSpecAttribute.getLength();x++)
					{
						Element AdapterSpecificAttribute = (Element) AdapterSpecAttribute.item(x);	
						String AdapterStatusParam = AdapterSpecificAttribute.getElementsByTagName("Name").item(0).getTextContent();

						if(AdapterStatusParam.contains("adapterStatus"))
							AdapterStatus = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();

						if(AdapterStatusParam.contains("file.targetDir")||AdapterStatusParam.contains("ftp.targetDir")||AdapterStatusParam.contains("ftp.sourceDir")||AdapterStatusParam.contains("file.sourceDir"))
						{
							OutputDirectory = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("file.targetFileName")||AdapterStatusParam.contains("file.sourceFileName"))
						{
							OutputFileName = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}	

						if(AdapterStatusParam.contains("ftp.host"))
						{

							ftphost = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("ftp.port"))
						{

							ftpport = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("ftp.user"))
						{

							ftpuser = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("enableDynConfigReceiver"))
						{
							IsDynamicConfig = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("ftp.security.keyStore"))
						{

							keyStore = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.contains("ftp.security.clientCertificate"))
						{

							clientCertificate = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
					}

					FileChannelData.append(OutputDirectory).append(sep);
					FileChannelData.append(OutputFileName).append(sep);
					FileChannelData.append(ftphost).append(sep);
					FileChannelData.append(ftpport).append(sep);
					FileChannelData.append(ftpuser).append(sep);

					if(IsDynamicConfig.contains("1"))
						FileChannelData.append("yes");
					else
						FileChannelData.append("no");
					if(!(keyStore ==""))
					FileChannelData.append(sep).append(keyStore).append(sep2).append(clientCertificate);

//					System.out.println(PartyID+sep+ComponentID+sep+ChannelID+sep+Direction+sep+TransportProtocol+sep+MessageProtocol+sep+AdapterNamespace+sep+AdapterStatus+sep+AdapterModuleList+sep+FileChannelData);
				}

				if(TransportProtocol.equals("SSHFtp"))
				{
					OutputDirectory = "";
					OutputFileName = "";
					IsDynamicConfig = "";
					String ftphost ="";
					String ftpport ="";
					String ftpuser ="";
					String SFTPFingerprint = "";

					NodeList AdapterSpecAttribute = CommunicationChannelSingle.getElementsByTagName("AdapterSpecificAttribute");
					for(int x=0; x < AdapterSpecAttribute.getLength();x++)
					{
						Element AdapterSpecificAttribute = (Element) AdapterSpecAttribute.item(x);	
						String AdapterStatusParam = AdapterSpecificAttribute.getElementsByTagName("Name").item(0).getTextContent();

						if(AdapterStatusParam.contains("adapterStatus"))
							AdapterStatus = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();

						if(AdapterStatusParam.equals("fileDirectory")||AdapterStatusParam.equals("filePath"))
						{
							OutputDirectory = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
							//							FileChannelData.append(OutputDirectory).append(sep2);
						}
						if(AdapterStatusParam.equals("regFileName")||AdapterStatusParam.equals("fileName"))
						{
							OutputFileName = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
							//							FileChannelData.append(OutputFileName).append(sep2);
						}	

						if(AdapterStatusParam.equals("serverhost"))
						{

							ftphost = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.equals("serverport"))
						{

							ftpport = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.equals("userName"))
						{

							ftpuser = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.equals("asma.in") || AdapterStatusParam.equals("dynamicValues"))
						{
							IsDynamicConfig = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
						if(AdapterStatusParam.equals("fingerprint"))
						{
							SFTPFingerprint = AdapterSpecificAttribute.getElementsByTagName("Value").item(0).getTextContent();
						}
					}

					FileChannelData.append(OutputDirectory).append(sep);
					FileChannelData.append(OutputFileName).append(sep);
					FileChannelData.append(ftphost).append(sep);
					FileChannelData.append(ftpport).append(sep);
					FileChannelData.append(ftpuser).append(sep);
					if(IsDynamicConfig.contains("1"))
						FileChannelData.append("yes").append(sep);
					else
						FileChannelData.append("no").append(sep);
					FileChannelData.append(SFTPFingerprint);

					//For loop on each channel ends
				}
//				System.out.println(PartyID+sep+ComponentID+sep+ChannelID+sep+Direction+sep+TransportProtocol+sep+MessageProtocol+sep+AdapterNamespace+sep+AdapterStatus+sep+AdapterModuleList+sep+FileChannelData);
				System.out.println(PartyID+sep+ComponentID+sep+ChannelID+sep+AuthMethod);
			}
		} catch (Exception exception) {
			getTrace().addDebugMessage(exception.getMessage());
			throw new StreamTransformationException(exception.toString());
		}
	}
}
