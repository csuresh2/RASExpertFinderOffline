package edu.uiuc.ras.extract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * This class encloses the tagged values of the current object/entity and 
 * the list of organizations this entity belongs to.
 * 
 * keyValues: consists of all the values from the content xml.
 * organizationsList: is a list of all organization or company names.
 * conceptsList: is a list of all concepts tagged in the xml.
 * 
 * @author chethans
 */
public class EntityDescriptor
{
	ArrayList<String> keyValues;
	ArrayList<String> organizationsList;
	ArrayList<String> conceptsList;

	public EntityDescriptor()
	{
		keyValues = new ArrayList<String>();
		organizationsList = new ArrayList<String>();
		conceptsList = new ArrayList<String>();
	}

	public EntityDescriptor(ArrayList<String> keyValues, ArrayList<String> organizationsList, 
			ArrayList<String> conceptsList)
	{
		this.keyValues = keyValues;
		this.organizationsList = organizationsList;
		this.conceptsList = conceptsList;
	}

	public ArrayList<String> getKeyValues()
	{
		return keyValues;
	}

	public ArrayList<String> getOrganizationsList()
	{
		return organizationsList;
	}

	public ArrayList<String> getConceptsList()
	{
		return conceptsList;
	}

	public void addKeyValue(String value)
	{
		keyValues.add(value);
	}

	public void addOrganization(String organization)
	{
		organizationsList.add(organization);
	}

	public void addConcept(String concept)
	{
		conceptsList.add(concept);
	}

	/*
	 * This method writes itself (i.e., its instance) onto an output 
	 * file in a tab spaced fashion.
	 */
	public void flushToFile(String filename, String organizationFileName)
	{
		try
		{
			// Create file 
			FileWriter fstream = new FileWriter(filename, true);
			BufferedWriter out = new BufferedWriter(fstream);

			// Create organizations filename
			FileWriter orgStream = new FileWriter(organizationFileName, true);
			BufferedWriter orgOut = new BufferedWriter(orgStream);

			out.write("\n");
			orgOut.write("\n");

			// Write all organizations list
			orgOut.write("Organizations List:\n");
			for(int i=0; i < organizationsList.size(); i++)
			{
				orgOut.write(organizationsList.get(i));
				orgOut.write("\n");
			}

			out.write("\n");

			// Flush Concepts onto the file.
			// out.write("Concepts:\n");
			for(int i=0; i < conceptsList.size(); i++)
			{
				out.write(conceptsList.get(i));
				out.write("\n");
			}

			// Close the output stream
			out.close();
			orgOut.close();
		}
		catch (Exception e)
		{
			//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
