package edu.uiuc.ras.extract;

import java.util.ArrayList;

/**
 * This class holds the keys and values of each parsed xml file and returns this 
 * object to the NamedEntityExtractor for further processing.
 * 
 * @author chethans
 */
public class ParsedXMLObject
{
	// These keys are names of the person after parsing the current xml file
	ArrayList<String> keys;
	// Populate the entity descriptions appropriately.
	EntityDescriptor descriptor;

	public ParsedXMLObject()
	{
		keys = new ArrayList<String>();
		descriptor = new EntityDescriptor();
	}

	public ArrayList<String> getKeys()
	{
		return keys;
	}

	public EntityDescriptor getDescriptor()
	{
		return descriptor;
	}

	public void addKey(String key)
	{
		keys.add(key);
	}
}
