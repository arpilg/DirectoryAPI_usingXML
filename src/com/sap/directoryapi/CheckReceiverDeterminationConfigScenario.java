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

public class CheckReceiverDeterminationConfigScenario {

	public static void main(String[] args) throws StreamTransformationException {
		try {

			FileInputStream fin = null;
			FileInputStream fin2 = null;

			try {
				fin = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/ConfigurationScenarioRead_Output.xml");
				fin2 = new FileInputStream("C:/Arpil/Anglian Water/PJP Directory API/ReceiverDeterminationReadOutput.xml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			String SenderComponentID = null;
			String InterfaceName = null;
			String InterfaceNamespace = null;
			String CSSenderComponentID = null;
			String CSInterfaceName = null;
			String CSInterfaceNamespace = null;
			Boolean bool = false; 
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			DocumentBuilder db=factory.newDocumentBuilder();
			/*input document in form of XML*/
			Document CSdoc=db.parse(fin);
			Document RcvDetdoc=db.parse(fin2);
			
			
			Element CSroot = CSdoc.getDocumentElement(); 
			NodeList ConfigurationScenarioNL = CSroot.getElementsByTagName("ReceiverDetermination");

			Element RcvDetroot = RcvDetdoc.getDocumentElement(); 
			NodeList RcvDetNL = RcvDetroot.getElementsByTagName("ReceiverDeterminationID");

			System.out.println("RcvDetNL"+RcvDetNL.getLength());
			System.out.println("CS"+ConfigurationScenarioNL.getLength());
			for (int i = 0; i < RcvDetNL.getLength(); i++)
			{
				
				Element RcvDetEle = (Element) RcvDetNL.item(i);
				SenderComponentID = RcvDetEle.getElementsByTagName("SenderComponentID").item(0).getTextContent();
				InterfaceName = RcvDetEle.getElementsByTagName("InterfaceName").item(0).getTextContent();
				InterfaceNamespace = RcvDetEle.getElementsByTagName("InterfaceNamespace").item(0).getTextContent();	
				
				bool =false;
				for (int j = 0; j < ConfigurationScenarioNL.getLength(); j++) 
				{							
					if(bool == false)
					{
						Element CSRcvDetEle = (Element) ConfigurationScenarioNL.item(j);

						CSSenderComponentID = CSRcvDetEle.getElementsByTagName("SenderComponentID").item(0).getTextContent();
						CSInterfaceName = CSRcvDetEle.getElementsByTagName("InterfaceName").item(0).getTextContent();
						CSInterfaceNamespace = CSRcvDetEle.getElementsByTagName("InterfaceNamespace").item(0).getTextContent();
						
						if( SenderComponentID.equalsIgnoreCase(CSSenderComponentID) && InterfaceName.equalsIgnoreCase(CSInterfaceName) && InterfaceNamespace.equalsIgnoreCase(CSInterfaceNamespace))
						{
							bool = true;							
						}
					}
				}	
				System.out.println(SenderComponentID+"|"+InterfaceName+"|"+InterfaceNamespace+"|"+bool);
			}

		} catch (Exception exception) {
			throw new StreamTransformationException(exception.toString());
		}
	}
}
