package com.mohan.parseXML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import jdk.internal.org.xml.sax.SAXException;

public class ParseXml {

	@SuppressWarnings({ "static-access", "null" })
	public static void main(String[] args) throws org.xml.sax.SAXException, SAXException, ParserConfigurationException,
			IOException, TransformerException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		FileWriter fw = null;
		BufferedWriter bw = null;
		File file = new File("D:\\Users\\bramasam\\Downloads\\xml.xml");
		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());
		String filePath = "D:\\Users\\bramasam\\Documents\\mohan\\";

		NodeList nodeList = doc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			// Get element
			Element element = (Element) nodeList.item(i);
			ParseXml parseObj = new ParseXml();
			parseObj.writeFileMethod(element, filePath, fw, bw);
		}

		/*
		 * if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
		 * System.out.println(element.getNodeName());
		 */
	}

	@SuppressWarnings("resource")
	public void writeFileMethod(Element element, String filePath, FileWriter fw, BufferedWriter bw)
			throws TransformerException, IOException {
		if (element.getElementsByTagName("*").getLength() == 0) {
			System.out.println(element + " and its parent is ->" + element.getParentNode().getNodeName());
			String fileName = element.getParentNode().getNodeName().toString();
			String fileDetail = filePath + fileName + ".txt";
			String attrTag = null;

			StringWriter buf = new StringWriter();
			Transformer xform = TransformerFactory.newInstance().newTransformer();
			xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			xform.transform(new DOMSource(element), new StreamResult(buf));

			if (element.getParentNode().getAttributes().getLength() > 0) {
				attrTag = element.getParentNode().getAttributes().item(0).toString();
			}

			// return(buf.toString());

			try {
				fw = new FileWriter(fileDetail, true);
				bw = new BufferedWriter(fw);
				if (attrTag != null) {
					bw.write(attrTag);
					bw.newLine();
				}
					
				bw.write(buf.toString());
				bw.newLine();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (fw != null)
					fw.flush();
				if (bw != null)
					bw.flush();
			}
		}

	}
}
