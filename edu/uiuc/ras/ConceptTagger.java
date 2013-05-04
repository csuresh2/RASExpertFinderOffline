package edu.uiuc.ras;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

/**
 * This class is used to perform two tasks
 * 1) Incorporate feedback from Google for each named entity
 * 2) Tag concepts in the feedback documents returned by google, in order to enrich the profile for each entity
 * 
 * Output: Adds concept keywords to the profile document for each named entity
 * 
 * 
 * @author rucha
 * @updater chethans
 */

public class ConceptTagger
{
	public static String ALCHEMY_API_KEY = "d467516bddf39b258993c26ec1115984cf15e0cd";
	public static String GOOGLE_LIST_KEY_STRING = "AIzaSyCF2MoqZXlsa96eBy7G_ZY7dz6KtNXbMmI";
	public static String GOOGLE_LIST_CX_STRING = "012838771652296013735:scgi1baa_tk";

	String persondatafolder;
	String taggedconceptdatafolder;

	/*
	 * Constructor that takes in input and output folder names as arguments 
	 * to instantiate an object.
	 */
	public ConceptTagger(String inputfolder, String outputfolder)
	{
		persondatafolder=inputfolder;
		taggedconceptdatafolder=outputfolder;
	}
	
	/*
	 * This method returns the top 10 URLS from google. The query string is the name of the entity file 
	 * which corresponds to the name of the person entity
	 */
	public List<String> getURLS(File inputFile)
	{
		String queryText = inputFile.getName();
		//System.out.println(query_text);
		List<String> urls=new ArrayList<String>();
		Customsearch customsearch = new Customsearch(new NetHttpTransport(), 
				new JacksonFactory(), null);

	    try
	    {
	        com.google.api.services.customsearch.Customsearch.Cse.List list = 
	        	customsearch.cse().list(queryText);
	        list.setKey(GOOGLE_LIST_KEY_STRING);
	        list.setCx(GOOGLE_LIST_CX_STRING);
	        Search results = list.execute();
	        List<Result> items = results.getItems();
	        System.out.println("Size is " + results.toPrettyString());

	        for(Result result:items)
	        	urls.add(result.getLink().toString());

	        for(String s:urls)
	        	System.out.println(s);
	    }
	    catch (IOException e)
	    {
	        e.printStackTrace();
	    }

	    return urls;
	}
	
	/*
	 * This method adds concept keywords to the profile document for each named entity, using Alchemy API
	 * @input: 1) List of feedback URLS from Google 2) Name of the person entity
	 * @output: Concept keywords tagged from the text of the feedback documents
	 */
	public void generateConceptFiles(List<String> urls, String personName)
	{
		int counter=1;
		try
		{
			for(String url: urls)
			{
				Document jsoupDocument = Jsoup.connect(url).ignoreContentType(true).get();
				String text = jsoupDocument.body().text();
				AlchemyAPI alchemyAPI = AlchemyAPI.GetInstanceFromString(ALCHEMY_API_KEY);
				org.w3c.dom.Document document = alchemyAPI.TextGetRankedConcepts(text);
				String conceptDocumentString = getStringFromDocument(document);

				File file = new File(taggedconceptdatafolder + ""+ personName);
				if (!file.exists()) {
					if (file.mkdir()) {
						//	System.out.println("Directory is created!");
					} else {
						System.out.println("Failed to create directory!");
					}
				}

				String folderName=file.getAbsolutePath();
				File conceptFile=new File(folderName +"/"+ personName + counter +".concept.xml");
				counter++;

				if(!conceptFile.exists())
					conceptFile.createNewFile();

				BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(conceptFile));
				bufferedWriter.write(conceptDocumentString);
				bufferedWriter.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * This method returns the String representation of an XML document.
	 * 
	 */
	public String getStringFromDocument(org.w3c.dom.Document doc)
	{
		try
		{	
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		}
		catch(TransformerException ex)
		{
			ex.printStackTrace();
			return null;
		}
	} 

	/*
	 * Wrapper method which generates the tagged concepts from Google for each named entity
	 */
	public void tagConcepts()
	{
		File[] files = new File(persondatafolder).listFiles();

		for(File inputFile: files)
		{
			String personName = inputFile.getName();
			System.out.println(personName);
			List<String> urls = getURLS(inputFile);
			generateConceptFiles(urls, personName);
		}
	}
}