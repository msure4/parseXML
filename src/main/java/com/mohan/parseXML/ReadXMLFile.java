package com.mohan.parseXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ReadXMLFile {

	public static void main(String[] args) {
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			String metadataFile = "D:\\\\Users\\\\bramasam\\\\Downloads\\metadata.txt";

			final String outFilePath = "D:\\Users\\bramasam\\Documents\\mohan\\";

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";
			final HashMap<String, Integer> multipleOccurence = new HashMap<>();
			final HashMap<String, String> attributeHash = new HashMap<>();
			final HashMap<String, String> nodeParent = new HashMap<>();
			final HashMap<String, Integer> writeFileMap = new HashMap<>();
			try {

				br = new BufferedReader(new FileReader(metadataFile));
				while ((line = br.readLine()) != null) {

					// use comma as separator
					String[] country = line.split(cvsSplitBy);

					if (Integer.parseInt(country[1]) > 1) {
						multipleOccurence.put(country[0], Integer.parseInt(country[1]));
						nodeParent.put(country[0], country[3]);
					}
				}
			} finally {
				br.close();
			}

			DefaultHandler handler = new DefaultHandler() {

				StringBuilder stringBuilder;

				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					System.out.println("Start Element :" + qName);
					stringBuilder = new StringBuilder();
					
					String attrTag = "";
					for (int i = 0; i < attributes.getLength(); i++) {
						attrTag += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
					}
					attributeHash.put(qName, "<" + qName + attrTag + ">");

					String parentBuilder = "";
					if (multipleOccurence.containsKey(qName)) {
						if (writeFileMap.containsKey(qName)) {
							System.out.println("found a taag to be printed -> " + qName);
							writeFileMap.put(qName, writeFileMap.get(qName) + 1);
						}

						else {
							writeFileMap.put(qName, 1);
							String[] parentString = nodeParent.get(qName).split("/");
							for (String str : parentString) {
								if (str.length() > 0 || !str.equals(""))
									parentBuilder += attributeHash.getOrDefault(str, "<" + str + ">")
											+ System.lineSeparator() + "  ";
							}
							stringBuilder.append(parentBuilder);
						}

					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {

					if (writeFileMap.containsKey(qName)) {
						writeFileMap.remove(qName);
						System.out.println(stringBuilder.toString());
					}

				}

				@Override
				public void characters(char ch[], int start, int length) throws SAXException {

					if (writeFileMap.size() > 0) {

						System.out.println(new String(ch, start, length));
						stringBuilder.append(new String(ch, start, length));
					}
				}

			};

			saxParser.parse("D:\\Users\\bramasam\\Downloads\\xml.xml", handler);

		} catch (Exception e) {
			e.printStackTrace();
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
