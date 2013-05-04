package edu.uiuc.ras;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is a utilities class that encloses all utility methods.
 * Note: The following methods have duplicate copies in its 
 * original classes, that needs to be removed and update its 
 * references appropriately.
 */
public class RASUtilities
{
	/*
	 * This method identifies if the argument aFileName is a valid
	 * file name or not.
	 */
	public static boolean isValidFileName(final String aFileName)
	{
	    final File aFile = new File(aFileName);
	    boolean isValid = true;
	    try
	    {
	        if (aFile.createNewFile())
	        {
	            aFile.delete();
	        }
	    }
	    catch (IOException e)
	    {
	        isValid = false;
	    }

	    return isValid;
	}

	/*
	 * This method is for getting a list of all xml files in the 
	 * directory "folder".
	 */
	public static ArrayList<File> getXMLFiles(File folder)
	{
	    ArrayList<File> aList = new ArrayList<File>();

	    File[] files = folder.listFiles();
	    for (File pf : files)
	    {
	    	if (pf.isFile() && isXMLFile(pf).indexOf("xml") != -1)
	    	{
	    		aList.add(pf);
	    	}
	    }

	    return aList;
	}

	/*
	 * This is a helper method that identifies if the file 'f'
	 * is a xml file.
	 */
	public static String isXMLFile(File f)
	{
		if (f.getName().indexOf(".") == -1)
		{
			return "";
		}
		else
		{
			return f.getName().substring(f.getName().length() - 3, f.getName().length());
		}
	}
}
