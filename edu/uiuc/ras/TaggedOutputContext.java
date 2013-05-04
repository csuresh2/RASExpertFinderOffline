package edu.uiuc.ras;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public class TaggedOutputContext
{
	public void executeStrategy(TaggedOutputStrategy strategy) throws XPathExpressionException, 
		IOException, SAXException, ParserConfigurationException
	{
		strategy.execute();
	}
}
