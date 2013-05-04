package edu.uiuc.ras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import edu.uiuc.ras.extract.ConceptXMLHandler;
import edu.uiuc.ras.extract.ContentXMLHandler;
import edu.uiuc.ras.extract.EntityDescriptor;
import edu.uiuc.ras.extract.ParsedXMLObject;
import edu.uiuc.ras.extract.XMLDriver;

/**
 * This class extracts the Named entities from the output files of the IE tagging 
 * engine.
 * 
 * Output: Creates a document per entity, each containing all the IE tagged values
 * 		   along with the list of organizations it belongs to.
 * 
 * @author chethans
 */
public class NamedEntityExtractor
{
	public static String INPUT_FILE_NAME_PREFIX ;
	public static String OUTPUT_FILE_NAME_PREFIX;
	HashMap<String, ArrayList<EntityDescriptor>> entitiesMap;
	HashMap<String, ArrayList<File>> entityXMLsMap;
	public String inputFileNamePrefix;
	public ArrayList<File> xmlFilesList;
	public ArrayList<File> contentXMLFilesList;
	public ArrayList<File> conceptXMLFilesList;
	public ArrayList<File> entityFoldersList;

	/*
	 * Constructor that takes in input and output folder names as arguments 
	 * to instantiate an object.
	 */
	public NamedEntityExtractor(String inputFolder, String outputFolder)
	{
		INPUT_FILE_NAME_PREFIX = inputFolder;
		OUTPUT_FILE_NAME_PREFIX = outputFolder;
		entitiesMap = new HashMap<String, ArrayList<EntityDescriptor>>();
	}

	/*
	 * This method build two lists that contains content xml files 
	 * and content xml files and then sorted based on their names.
	 */
	void buildContentAndConceptXMLFilesList()
	{
		contentXMLFilesList = new ArrayList<File>();
		conceptXMLFilesList = new ArrayList<File>();

		for(int i=0; i < xmlFilesList.size(); i++)
		{
			if(xmlFilesList.get(i).getName().contains("content"))
			{
				contentXMLFilesList.add(xmlFilesList.get(i));
			}
			else if(xmlFilesList.get(i).getName().contains("concept"))
			{
				conceptXMLFilesList.add(xmlFilesList.get(i));
			}
		}

		Collections.sort(contentXMLFilesList, new FileComparator());
		Collections.sort(conceptXMLFilesList, new FileComparator());
	}

	/*
	 * This method parses the output file of IE tagger and constructs the entitiesMap 
	 * by building a key value relationship between the entity (key) and 
	 * EntityDescriptors (values).
	 */
	void parseInput()
	{
		// Parse it based on the output file of IE tagger.
		try
		{
			if(conceptXMLFilesList.size() != contentXMLFilesList.size())
			{
				throw new Exception("Number of contentXMLFiles and conceptXMLFiles " +
						"are not the same");
			}

			// Parse the XML file(s) and update the parsedObject.
			for(int i=0; i < contentXMLFilesList.size(); i++)
			{
				ContentXMLHandler contentXMLHandler = new ContentXMLHandler(
						new ParsedXMLObject());
				XMLDriver driver = new XMLDriver();
				driver.handler(contentXMLFilesList.get(i), contentXMLHandler);
				ArrayList<String> keys = contentXMLHandler.getParsedObject().getKeys();

				EntityDescriptor value = contentXMLHandler.getParsedObject().getDescriptor();
				// Update the hashMap with the keys and values.
				for(int j = 0; j < keys.size(); j++)
				{
					if(!entitiesMap.containsKey(keys.get(j)))
					{
						ArrayList<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
						list.add(value);
						entitiesMap.put(keys.get(j), list);
					}
					else
					{
						ArrayList<EntityDescriptor> list = entitiesMap.get(keys.get(j));
						list.add(value);
						entitiesMap.put(keys.get(j), list);
					}
				}
			}
		}
		// Must handle each exception individually - refactor later.
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Flush the entitiesMap on to the output files such that each entity forms 
	 * a document and contains its values and organization it belongs to.
	 */
	void flushEntitiesMap()
	{
		for (Map.Entry<String, ArrayList<EntityDescriptor>> entry : entitiesMap.entrySet())
		{
			String key = entry.getKey();
			ArrayList<EntityDescriptor> values = entry.getValue();

			for(int i = 0; i < values.size(); i++)
			{
				if(!(new File(OUTPUT_FILE_NAME_PREFIX + key)).exists() && 
						RASUtilities.isValidFileName(OUTPUT_FILE_NAME_PREFIX + key))
				{
					values.get(i).flushToFile(OUTPUT_FILE_NAME_PREFIX + key, 
							OUTPUT_FILE_NAME_PREFIX + key + ".org");
				}
				else if(RASUtilities.isValidFileName(OUTPUT_FILE_NAME_PREFIX + key))
				{
					values.get(i).flushToFile(OUTPUT_FILE_NAME_PREFIX + key, 
							OUTPUT_FILE_NAME_PREFIX + key + ".org");
				}
			}
		}
	}

	/*
	 * This method finds all xml files in the directory inputFileNamePrefix.
	 */
	void findAllXMLFiles()
	{
		xmlFilesList = RASUtilities.getXMLFiles(new File(inputFileNamePrefix));
	}

	/*
	 * Get the list of all xml files the need to be parsed.
	 */
	public ArrayList<File> getXMLFilesList()
	{
		return xmlFilesList;
	}

	/*
	 * This method identifies the entity directories from the tagger's output 
	 * folder (i.e., extractor's input) and builds the entityFoldersList.
	 */
	void populateEntityDirectoryList()
	{
		entityFoldersList = new ArrayList<File>();

	    File[] files = new File(inputFileNamePrefix).listFiles();

	    for (File pf : files)
	    {
	    	if (pf.isDirectory())
	    	{
	    		entityFoldersList.add(pf);
	    	}
	    }
	}

	/*
	 * This method builds the entityXMLsMap, that contains a mapping of KEY 
	 * i.e., the entity name to VALUE i.e., a list of xml file names containing 
	 * the concepts that decribe the entity (KEY).
	 */
	void populateEntityXMLsMap()
	{
		for(int i=0; i < entityFoldersList.size(); i++)
		{
			String key = entityFoldersList.get(i).getName();

			if(!entityXMLsMap.containsKey(key))
			{
				entityXMLsMap.put(key, RASUtilities.getXMLFiles(
						new File(inputFileNamePrefix + key + "/")));
			}
			else
			{
				entityXMLsMap.put(key, RASUtilities.getXMLFiles(
						new File(inputFileNamePrefix + key + "/")));
			}
		}
	}

	/*
	 * This method parses the concept xmls (collected during the enriching 
	 * phase by the ConceptTagger) around the entity.
	 */
	void parseEntityXMLList()
	{
		// Populate the entityXMLsMap.
		populateEntityXMLsMap();

		// Parse it based on the output file of IE tagger.
		try
		{
			for (Map.Entry<String, ArrayList<File>> entry : entityXMLsMap.entrySet())
			{
				ArrayList<File> xmlFilesList = entry.getValue();

				// Parse the XML file(s) and update the parsedObject.
				for(int i=0; i < xmlFilesList.size(); i++)
				{
					ConceptXMLHandler conceptXMLHandler = new ConceptXMLHandler(
						new ParsedXMLObject());
					XMLDriver driver = new XMLDriver();
					driver.handler(xmlFilesList.get(i), conceptXMLHandler);

					EntityDescriptor value = conceptXMLHandler.getParsedObject().getDescriptor();
					// Update the hashMap with the key and value.
					if(!entitiesMap.containsKey(entry.getKey()))
					{
						ArrayList<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
						list.add(value);
						entitiesMap.put(entry.getKey(), list);
					}
					else
					{
						ArrayList<EntityDescriptor> list = entitiesMap.get(entry.getKey());
						list.add(value);
						entitiesMap.put(entry.getKey(), list);
					}
				}
			}
		}
		// Must handle each exception individually - refactor later.
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}

	/*
	 * Creates the entity files, where each file consists of the key values that 
	 * defines or describes that person.
	 */
	public void createEntityFiles()
	{
		this.inputFileNamePrefix = INPUT_FILE_NAME_PREFIX;
		findAllXMLFiles();
		buildContentAndConceptXMLFilesList();
		parseInput();
		flushEntitiesMap();
	}

	/*
	 * This method enriches the concepts built around an entity by utilizing the 
	 * output from ConceptTagger.
	 */
	public void enrichEntityFiles()
	{
		entitiesMap = new HashMap<String, ArrayList<EntityDescriptor>>();
		entityXMLsMap = new HashMap<String, ArrayList<File>>();

		populateEntityDirectoryList();
		parseEntityXMLList();
		flushEntitiesMap();
	}

	/**
	 * This is a file comparator class that defines how to compare 
	 * two files while sorting the list of files.
	 * 
	 * @author chethans
	 */
	private class FileComparator implements Comparator<File>
	{
		public int compare(File f1, File f2)
		{
			return f1.getName().compareTo(f2.getName());
		}
	}
}
