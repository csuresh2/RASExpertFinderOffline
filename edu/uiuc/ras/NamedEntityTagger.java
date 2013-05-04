package edu.uiuc.ras;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.alchemyapi.api.AlchemyAPI;

import java.io.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.xpath.XPathExpressionException;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.File;

/**
 * This class tags the entities and concepts in the raw text documents obtained from the crawler
 * 
 * Output: Creates two documents per raw text input file. The first output file contains the tagged entities
 * Second contains the tagged concepts
 * 
 * @author rucha
 * @updater chethans
 */
public class NamedEntityTagger
{
	public static final String ALCHEMY_API_KEY = "ce3494152ad3be9e4ec0322576a48efd06111040";
	public static final String TRANSFORMER_EXCEPTION = "transformerException";
	public static final int STATUS_ERROR = -1;
	String parsedDatafolder;
	String taggeddatafolder;
	String inputFileName;
	AlchemyAPI alchemyAPI;

	/**
	 * Constructor to initialize data folders.
	 * @param inputfolder
	 * @param outputfolder
	 */
	public NamedEntityTagger(String inputfolder, String outputfolder)
	{
		parsedDatafolder = inputfolder;
		taggeddatafolder = outputfolder;
	}

	/**
	 * Wrapper method which generates the tagged output files for each input file
	 */
	public void tagData()
	{
		File[] files = new File(parsedDatafolder).listFiles();
		for(File ipfile: files)
			generateTaggedOutput(ipfile);
	}
	
	/**
	 * This method returns the String representation of an XML document.
	 * 
	 * @param doc
	 * @return
	 */
	public String getStringFromDocument(Document doc)
	{
	    try
	    {
	       DOMSource domSource = new DOMSource(doc);
	       StringWriter writer = new StringWriter();
	       StreamResult result = new StreamResult(writer);
	       TransformerFactory transformerFactory = TransformerFactory.newInstance();
	       Transformer transformer = transformerFactory.newTransformer();
	       transformer.transform(domSource, result);
	       return writer.toString();
	    }
	    catch(TransformerException tex)
	    {
	       tex.printStackTrace();
	       return TRANSFORMER_EXCEPTION;
	    }
	}

	/**
	 * This method generates the tagged output using the Alchemy api. Generates two 
	 * output files per input file: 1) Tagged entities file 2) Tagged concepts file
	 * 
	 * @param ipFile: Raw text file
	 */
	public void generateTaggedOutput(File inputFile)
	{
		try
		{
			inputFileName = inputFile.getName();
			alchemyAPI = AlchemyAPI.GetInstanceFromString(ALCHEMY_API_KEY);

			// Context
			TaggedOutputContext taggedOutputExecutor = new TaggedOutputContext();

			// Executing tagging strategies.
			TaggedOutputStrategy entityOutputStrategy = 
					new NamedEntityTaggedOutputStrategy(this);
			
			TaggedOutputStrategy conceptOutputStrategy = 
					new ConceptTaggedOutputStrategy(this);

			taggedOutputExecutor.executeStrategy(entityOutputStrategy);
			taggedOutputExecutor.executeStrategy(conceptOutputStrategy);
		}
		catch(XPathExpressionException xpException)
		{
			System.out.println("XPathExpressionException message: " + xpException.getMessage());
		}
		catch(ParserConfigurationException parseException)
		{
			System.out.println("Parse config exception message: " + parseException.getMessage());
		}
		catch(SAXException saxException)
		{
			System.out.println("Sax exception message: " + saxException);
		}
		catch(IOException ioException)
		{
			System.out.println("IO exception message: " + ioException.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
