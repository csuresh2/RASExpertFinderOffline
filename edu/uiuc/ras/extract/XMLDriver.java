package edu.uiuc.ras.extract;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * This is a xml driver class that parses the xml file and returns its values 
 * in a format desirable to the NamedEntityExtractor.
 * 
 * @author chethans
 */
public class XMLDriver
{
	public void handler(File xmlFile, XMLHandler xmlHandler) 
		throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document doc = docBuilder.parse(xmlFile);
		doc.getDocumentElement().normalize();

		xmlHandler.handleResultTag(doc);
	}
}
