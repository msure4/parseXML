package com.mohan.parseXML;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class SpearateXML {

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException,
			TransformerFactoryConfigurationError, TransformerException {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();

		/*
		 * if (args.length < 2) { System.out.println("parameter missing");
		 * System.exit(0); }
		 */

		File file = new File("D:\\Users\\bramasam\\Downloads\\xml.xml");
		String filePath = "D:\\Users\\bramasam\\Documents\\mohan\\";

		/*
		 * File file = new File(args[0]); String filePath = args[1].trim();
		 */

		Document doc = db.parse(file);
		db.setEntityResolver(new EntityResolver() {
			// Handle doctype tag
			@Override
			public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
				if (systemId.contains(".dtd")) {
					return new InputSource(new StringReader(""));
				} else {
					return null;
				}
			}
		});

		doc.getDocumentElement().normalize();
		ArrayList<String> tagList = new ArrayList<>();
		HashMap<String, Integer> parsedValues = new HashMap<>();

		// Iterate through nodes from the root node and break if you find the first
		// multi occurrence tag
		NodeList nodeList = doc.getElementsByTagName("*");
		for (int i = 0; i < nodeList.getLength(); i++) {
			// Get element
			Element element = (Element) nodeList.item(i);
			SpearateXML parseObj = new SpearateXML();
			NodeList currentElement = doc.getElementsByTagName(element.getNodeName());
			if (parsedValues.containsKey(element.getNodeName()))
				continue;
			if (currentElement.getLength() > 1) {
				for (int j = 0; j < currentElement.getLength(); j++)
					parseObj.prepareWrite((Element) currentElement.item(j), filePath, j + 1, tagList);
			} else {

				// Add the tags parsed to complete the XML in the output
				tagList.add(element.getNodeName());
				String attrTag = "";
				for (int k = 0; k < element.getAttributes().getLength(); k++)
					attrTag += element.getAttributes().item(k).getNodeName() + "=\""
							+ element.getAttributes().item(k).getNodeValue() + "\" ";
				if (attrTag != "") {
					attrTag += ">";
					tagList.add(attrTag);
				}
			}
			parsedValues.put(element.getNodeName(), 1);
		}

	}

	private void prepareWrite(Element element, String filePath, Integer index, ArrayList<String> tagList)
			throws TransformerFactoryConfigurationError, TransformerException, IOException {

		// Convert the tag into a String including the tag name, attributes with close
		// tags
		StringWriter buf = new StringWriter();
		Transformer xform = TransformerFactory.newInstance().newTransformer();
		xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		xform.transform(new DOMSource(element), new StreamResult(buf));

		String fileName = element.getNodeName().toString();
		String fileDetail = filePath + "/" + fileName + "_" + index + ".txt";
		FileWriter fw = null;
		BufferedWriter bw = null;
		String headerBuilder = "";
		String footerBuilder = "";
		String spaceHolder = "";

		for (int i = 0; i < tagList.size(); i++) {
			if (i + 1 < tagList.size() && tagList.get(i + 1).contains("=")) {
				headerBuilder += "<" + tagList.get(i) + " " + tagList.get(i + 1) + System.lineSeparator();
				i++;
			} else
				headerBuilder += "<" + tagList.get(i) + ">" + System.lineSeparator();
			for (int z = 0; z <= i; z++)
				headerBuilder += "  ";

		}
		for (int i = tagList.size() - 1; i >= 0; i--)
			if (!tagList.get(i).contains("=")) {
				for (int z = 0; z < i; z++)
					spaceHolder += "  ";
				footerBuilder += System.lineSeparator() + spaceHolder + "</" + tagList.get(i) + ">";
				spaceHolder = "";
			}
		try {

			// Write the file output
			fw = new FileWriter(fileDetail, true);
			bw = new BufferedWriter(fw);

			bw.write(headerBuilder + buf.toString() + footerBuilder);
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
