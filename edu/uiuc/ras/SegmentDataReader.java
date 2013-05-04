package edu.uiuc.ras;

import java.io.File;
import java.io.IOException;

/**
 * This class is used to export the Nutch segments into text format using
 * using the Nutch readseg command
 * 
 * Output: The readDataFolder contains exported output
 * 
 * @author adarshms
 */

public class SegmentDataReader 
{
	private String crawlDataFolder;
	private String readDataFolder;
	private String nutchHome;
	
	/**
	 * Constructor to initialize the SegmentDataReader object.
	 * @param crawlDataFolder readDataFolder, nutchHome
	 */
	
	public SegmentDataReader(String crawlDataFolder, String readDataFolder, String nutchHome)
	{
		this.crawlDataFolder = crawlDataFolder;
		this.readDataFolder = readDataFolder;
		this.nutchHome = nutchHome;
	}
	
	
	/*
	 * This method lists all the segment folders in the Nutch crawlDataFolder
	 * and performs the readSegment operation on each of the folder
	 */
	
	void readContent() throws IOException, InterruptedException
	{
		String rawSegmentDataFolder = crawlDataFolder + "/segments/";
		File[] rawSegmentFolders = new File(rawSegmentDataFolder).listFiles();
		for(File folder: rawSegmentFolders)
			readSegment(folder.getName());
	}
	
	/*
	 * This method reads a Nutch segment folder by executing a ShellCommand
	 */
	
	void readSegment(String rawSegmentFolderName)
	{
		try
		{
			String rawSegmentFolderPath = crawlDataFolder + "segments/" + rawSegmentFolderName;
			String segmentFolderPath = readDataFolder + "segments/" + rawSegmentFolderName;
			String readSegmentCommand = nutchHome + "bin/nutch readseg -dump " + rawSegmentFolderPath + " " + segmentFolderPath + " -nocontent -nofetch -nogenerate -noparse -noparsedata";
			
			ShellCommand shellCommand = new ShellCommand(readSegmentCommand);
			int exitValue = shellCommand.execute();
			System.out.println("Exit value : " + exitValue);
		}
		catch(Exception e)
		{
			System.out.println("Exception in readSegment " + e);
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException
	{
		SegmentDataReader read = new SegmentDataReader("/home/adarshms/academics/cs410/project/crawl_data/", "/home/adarshms/academics/cs410/project/read_data/", "/home/adarshms/academics/cs410/project/nutch/");
		read.readContent();
	}
}