package com.mohan.parseXML;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class XMLWithSAXParserAdv {

	public static void main(String[] args) {
		try {

			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader reader = saxParser.getXMLReader();

			reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

			if (args.length < 3) {
				System.out.println("argument missing");
				System.exit(1);
			}

			// Metadata input file in csv format
			String metadataFile = args[0].trim();

			// input XML file which needs to be parsed
			String inputXMLFile = args[1].trim();

			// Output folder path where the split XML needs to be stored
			final String outFilePath = args[2].trim() + "\\";

			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";

			HashMap<String, String> attributeHash = new HashMap<>();
			HashMap<String, String> nodeParent = new HashMap<>();
			HashMap<String, Integer> multipleOccurence = new HashMap<>();
			HashMap<String, Integer> primaryKey = new HashMap<>();
			try {

				br = new BufferedReader(new FileReader(metadataFile));
				int iteration = 0;

				while ((line = br.readLine()) != null) {
					if (iteration == 0) {
						iteration++;
						continue;
					}
					// use comma as separator
					String[] country = line.split(cvsSplitBy);

					// Split the metadata file and save nodes which has more than one occurrence and
					// its parent hierarchy
					if (Integer.parseInt(country[3].trim()) > 1) {
						multipleOccurence.put(country[0].trim(), 1);
						nodeParent.put(country[0].trim(), country[1]);
						if (primaryKey.size() == 0) {
							primaryKey.put(country[0], 1);
						}
					}
				}
			} finally {
				br.close();
			}

			DefaultHandler handler = new DefaultHandler() {

				StringBuilder stringBuilder = new StringBuilder();
				Boolean startWriting = false;

				// startElement – which is called whenever there is an open tag e.g <tag_name>
				public void startElement(String uri, String localName, String qName, Attributes attributes)
						throws SAXException {

					// Store the attributes of a node which appears in the start tag of the element
					String attrTag = "";
					for (int i = 0; i < attributes.getLength(); i++) {
						attrTag += " " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"";
					}

					attributeHash.put(qName, "<" + qName + attrTag + ">");

					String parentBuilder = "";

					// check if the current node is one of multi occurred node
					if (primaryKey.containsKey(qName)) {
						// outFilePath + "/" + qName + "_" + index + ".txt";

						// when you find the fist multi-occurrence tag as a start tag, set flag
						// startWriting to true meaning, we need to start writing the output file from
						// this point onwards

						startWriting = true;
						String[] parentString = nodeParent.get(qName).split("/");

						// Read the nodes parent hierarchy and find build the tags including the
						// attributes if present into parentBuilder string
						for (int i = 0; i < parentString.length; i++) {
							if (parentString[i].length() > 0 || !parentString[i].equals(""))
								if (attributeHash.containsKey(parentString[i]))
									parentBuilder += attributeHash.get(parentString[i]);
								else
									parentBuilder += "<" + parentString[i] + ">";
							if ((parentString[i].length() > 0 || !parentString[i].equals(""))
									&& i < parentString.length - 1) {
								parentBuilder += System.lineSeparator();

							}
						}
						stringBuilder.append(parentBuilder);
					} else {
						if (startWriting)
							stringBuilder.append(attributeHash.get(qName));
					}
				}

				// endElement – Which is called whenever there is a close tag e.g </tag_name>
				public void endElement(String uri, String localName, String qName) throws SAXException {

					if (startWriting)
						stringBuilder.append("</" + qName + ">");

					// if you come across the end tag, that's the point where the stored string has
					// to be written into the output file with appending the occurrence number

					if (primaryKey.containsKey(qName)) {
						String parentBuilder = "";
						String[] parentString = nodeParent.get(qName).split("/");
						for (int i = parentString.length - 2; i >= 0; i--)
							if (parentString[i].length() > 0 || !parentString[i].equals("")) {
								parentBuilder += System.lineSeparator() + "</" + parentString[i] + ">";
							}
						stringBuilder.append(parentBuilder);

						XMLWithSAXParserAdv parserObj = new XMLWithSAXParserAdv();
						try {
							parserObj.prepareWrite(stringBuilder, qName, multipleOccurence.get(qName), outFilePath);
						} catch (IOException e) {
							e.printStackTrace();
						}

						// check if the String which we have built has elements which has multiple
						// occurrence in the metadata file
						parserObj.parseInnerTags(stringBuilder, primaryKey, multipleOccurence, outFilePath, nodeParent,
								attributeHash);

						// reset all variables for the next iteration
						multipleOccurence.put(qName, multipleOccurence.get(qName) + 1);
						startWriting = false;
						stringBuilder = new StringBuilder();
					}

				}

				// characters – which is called all other scenarios covered by the
				// above-mentioned methods

				public void characters(char ch[], int start, int length) throws SAXException {
					String value = new String(ch, start, length);

					if (startWriting) {
						stringBuilder.append(value);
					}
				}

			};

			saxParser.parse(inputXMLFile, handler);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void prepareWrite(StringBuilder stringBuilder, String qName, Integer index, String filePath)
			throws IOException {

		// create the name of the file with the tag name and the occurrence index
		String fileDetail = filePath + "/" + qName + "_" + index + ".xml";
		FileWriter fw = null;
		BufferedWriter bw = null;

		try {

			// Write the file output
			fw = new FileWriter(fileDetail, true);
			bw = new BufferedWriter(fw);

			bw.write(stringBuilder.toString());
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

	private void parseInnerTags(StringBuilder stringBuilder, HashMap<String, Integer> primaryKey,
			HashMap<String, Integer> multipleOccurence, String filePath, HashMap<String, String> nodeParent,
			HashMap<String, String> attributeHash) {
		String parentBuilder = "";
		String trailerBuilder = "";
		Boolean startWrite = false;
		StringBuilder innerBuilder = new StringBuilder();
		
		//iterate through all multi occurred tags
		for (String key : multipleOccurence.keySet()) {
			
			//for every tag other than the parent tag
			if (!primaryKey.containsKey(key)) {
				parentBuilder = "";
				trailerBuilder = "";
				String[] parentString = nodeParent.get(key).split("/");
				
				//build the parent string along with attributes
				for (int i = 0; i < parentString.length - 1; i++) {
					if (parentString[i].length() > 0 || !parentString[i].equals(""))
						if (attributeHash.containsKey(parentString[i]))
							parentBuilder += attributeHash.get(parentString[i]);
						else
							parentBuilder += "<" + parentString[i] + ">";
					if ((parentString[i].length() > 0 || !parentString[i].equals("")) && i < parentString.length - 1)
						parentBuilder += System.lineSeparator();
				}
				
				//build trailer

				for (int j = parentString.length - 3; j >= 0; j--)
					if (parentString[j].length() > 0 || !parentString[j].equals(""))
						trailerBuilder += System.lineSeparator() + "</" + parentString[j] + ">";

				String[] lines = stringBuilder.toString().split("\\n");

				
				//for every line from the string 
				for (String str : lines) {
					//marks the beginning of the tag
					if (str.contains(key) && !str.trim().startsWith("</" + key)) {
						innerBuilder.append(parentBuilder);
						startWrite = true;
					}
					
					//start building the string to be written
					if (startWrite)
						innerBuilder.append(str + System.lineSeparator());

					//start writing output file when the tag ends
					if ((str.contains(key) && str.trim().startsWith("</" + key)) || (str.contains(key)
							&& str.trim().startsWith("<" + key) && str.trim().endsWith("</" + key + ">"))) {
						innerBuilder.append(trailerBuilder);

						XMLWithSAXParserAdv parserObj = new XMLWithSAXParserAdv();
						try {
							parserObj.prepareWrite(innerBuilder, key, multipleOccurence.get(key), filePath);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						multipleOccurence.put(key, multipleOccurence.get(key) + 1);
						innerBuilder = new StringBuilder();
						startWrite = false;
					}

				}
			}

			else
				continue;
		}

	}
}