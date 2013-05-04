package edu.uiuc.ras;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

public interface TaggedOutputStrategy
{
	/**
	 * Decided to choose tagged output strategy from either:
	 * 1. Named Entity tagged output.
	 * 2. Concept tagged output.
	 */
	public void execute() throws IOException, XPathExpressionException, SAXException, 
	ParserConfigurationException;
}
