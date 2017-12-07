/*package com.mohan.parseXML;

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
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
import org.xml.sax.helpers.DefaultHandler;

public class SeparateXMLWithSAX extends DefaultHandler{

	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();

		
		 * if (args.length < 2) { System.out.println("parameter missing");
		 * System.exit(0); }
		 
		
		 * File file = new File(args[0]); String filePath = args[1].trim();
		 

		File file = new File("D:\\Users\\bramasam\\Downloads\\xml.xml");
		String filePath = "D:\\Users\\bramasam\\Documents\\mohan\\";

		DefaultHandler handler = new DefaultHandler();
		saxParser.parse(file, handler);
		saxParser.setConte
		
	}
	public void startElement()
}
*/