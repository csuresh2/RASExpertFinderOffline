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
public class NamedEntityTaggedOutputStrategy implements TaggedOutputStrategy
{
	private NamedEntityTagger net;

	/**
	 * Constructor to initialize the named entity tagger object.
	 * @param net
	 */
	public NamedEntityTaggedOutputStrategy(NamedEntityTagger net)
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

		Document namedEntityDocument = net.alchemyAPI.TextGetRankedNamedEntities(text);
		String namedEntityXMLText = net.getStringFromDocument(namedEntityDocument);

		if(namedEntityXMLText.equals(NamedEntityTagger.TRANSFORMER_EXCEPTION))
		{
			System.exit(NamedEntityTagger.STATUS_ERROR);
		}

		File namedEntityOutputFile = new File(net.taggeddatafolder + net.inputFileName + ".xml");
		if(!namedEntityOutputFile.exists())
			namedEntityOutputFile.createNewFile();

		BufferedWriter namedEntityBufferedWriter = new BufferedWriter(new FileWriter(
			namedEntityOutputFile.getAbsoluteFile()));
		namedEntityBufferedWriter.write(namedEntityXMLText);

		reader.close();
		namedEntityBufferedWriter.close();
	}
}
