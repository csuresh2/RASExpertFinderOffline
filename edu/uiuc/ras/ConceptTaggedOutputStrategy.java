package edu.uiuc.ras;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;

/**
 * This is a concrete execution strategy.
 * @author chethans
 *
 */
public class ConceptTaggedOutputStrategy implements TaggedOutputStrategy
{
	private NamedEntityTagger net;

	/**
	 * Constructor to initialize the named entity tagger object.
	 * @param net
	 */
	public ConceptTaggedOutputStrategy(NamedEntityTagger net)
	{
		this.net = net;
	}

	/**
	 * Execution strategy to generated tagged entities file.
	 * @throws IOException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 */
	public void execute() throws IOException, XPathExpressionException, SAXException, 
		ParserConfigurationException
	{
		BufferedReader reader = new BufferedReader(new FileReader(
				new File(net.parsedDatafolder + net.inputFileName)));
		String text = reader.readLine();

		Document conceptDocument = net.alchemyAPI.TextGetRankedConcepts(text);
		String conceptXMLText = net.getStringFromDocument(conceptDocument);

		if(conceptXMLText.equals(NamedEntityTagger.TRANSFORMER_EXCEPTION))
		{
			System.exit(NamedEntityTagger.STATUS_ERROR);
		}

		File conceptOutputFile = new File(net.taggeddatafolder + 
				net.inputFileName.replace("content","concept")+".xml");
		if(!conceptOutputFile.exists())
			conceptOutputFile.createNewFile();

		BufferedWriter conceptBufferedWriter = new BufferedWriter(new FileWriter(
			conceptOutputFile.getAbsoluteFile()));
		conceptBufferedWriter.write(conceptXMLText);

		reader.close();
		conceptBufferedWriter.close();
	}
}
