package com.mohan.parseXML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import jdk.internal.org.xml.sax.SAXException;

@SuppressWarnings("restriction")
public class ParseXMLFlexter {

	public static void main(String[] args) throws org.xml.sax.SAXException, SAXException, ParserConfigurationException,
			IOException, TransformerException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		if (args.length < 2) {
			System.out.println("parameter missing");
			System.exit(0);
		}

		// File file = new File("D:\\Users\\bramasam\\Downloads\\xml.xml");
		
//		String filePath = "D:\\Users\\bramasam\\Documents\\mohan\\";
		
		File file = new File(args[0]);
		String filePath = args[1].trim();

		Document doc = db.parse(file);
		doc.getDocumentElement().normalize();
		System.out.println("Root element " + doc.getDocumentElement().getNodeName());

		Map<String, String> processedMap = new HashMap<>();
		Map<String, Integer> uuID = new HashMap<>();

		NodeList nodeList = doc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			// Get element
			Element element = (Element) nodeList.item(i);
			ParseXMLFlexter parseObj = new ParseXMLFlexter();
			parseObj.prepareWrite(element, filePath, processedMap, uuID);
		}

		/*
		 * if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
		 * System.out.println(element.getNodeName());
		 */
	}

	@SuppressWarnings("resource")
	public void prepareWrite(Element element, String filePath, Map<String, String> processedMap,
			Map<String, Integer> uuID) throws TransformerException, IOException {
		if (element.getElementsByTagName("*").getLength() == 0) {
			System.out.println(element + " and its parent is ->" + element.getParentNode().getNodeName());
			String fileName = "";
			String attrTag = "";
			String fileDetail = "";
			Boolean isAttr = false;

			String processedValue = processedMap.get(element.getParentNode().getNodeName());

			if (element.getParentNode().getAttributes().getLength() > 0) {
				for (int i = 0; i < element.getParentNode().getAttributes().getLength(); i++)
					attrTag += element.getParentNode().getAttributes().item(i).getNodeValue().toString();

				fileName = element.getParentNode().getNodeName().toString();
				fileDetail = filePath + fileName + ".txt";
				isAttr = true;

				/*
				 * Integer currentUuid; currentUuid =
				 * uuID.get(element.getParentNode().getNodeName()); if (currentUuid == null )
				 * uuID.put(element.getParentNode().getNodeName(), 1) ; else {
				 * if(!processedValue
				 * .equalsIgnoreCase(element.getParentNode().getAttributes().item(0).
				 * getNodeValue().toString())) { uuID.put(element.getParentNode().getNodeName(),
				 * uuID.get(element.getParentNode().getNodeName()) + 1 ); } }
				 */

				if (processedValue == null || !processedValue
						.equalsIgnoreCase(element.getParentNode().getAttributes().item(0).getNodeValue().toString())) {
					if (processedValue == null)
						uuID.put(element.getParentNode().getNodeName(), 1);
					else
						uuID.put(element.getParentNode().getNodeName(),
								uuID.get(element.getParentNode().getNodeName()) + 1);
					processedMap.put(element.getParentNode().getNodeName(),
							element.getParentNode().getAttributes().item(0).getNodeValue().toString());
					writeFile(element, fileDetail, attrTag, isAttr, uuID.get(element.getParentNode().getNodeName()));

				}

				attrTag = "";
				isAttr = false;

			}

			for (int i = 0; i < element.getAttributes().getLength(); i++)
				attrTag += element.getAttributes().item(i).getNodeValue().toString();

			if (attrTag != "")
				fileName = element.getNodeName().toString();
			else
				fileName = element.getParentNode().getNodeName().toString();
			fileDetail = filePath + fileName + ".txt";

			writeFile(element, fileDetail, attrTag, isAttr,
					uuID.getOrDefault(element.getParentNode().getNodeName(), 1));
			attrTag = "";

		}

	}

	public void writeFile(Element element, String fileDetail, String attrTag, Boolean isAttr, Integer uuID)
			throws IOException {
		FileWriter fw = null;
		BufferedWriter bw = null;
		File fileBuffer = new File(fileDetail);
		String header = "";
		if (!fileBuffer.exists()) {
			header = addHeader(element);
		}

		try {

			fw = new FileWriter(fileDetail, true);
			bw = new BufferedWriter(fw);
			if (header != "")
				bw.write(header);
			if (attrTag != "") {
				bw.newLine();
				bw.write(uuID + "\t" + attrTag + "\t");
			}
			if (!isAttr)
				bw.write(element.getTextContent().toString() + "\t");

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fw != null)
				fw.flush();
			if (bw != null)
				bw.flush();
		}
	}

	public String addHeader(Element element) {
		String nodeName = "";
		String attr = "UUID" + "\t";
		Node childNode = element.getParentNode().getFirstChild();
		while (childNode.getNextSibling() != null) {
			childNode = childNode.getNextSibling();
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				nodeName += childNode.getNodeName() + "\t";
				for (int i = 0; i < element.getAttributes().getLength(); i++)
					attr += element.getAttributes().item(i).getNodeName().toString();
			}

		}
		return attr + nodeName;

	}
}
