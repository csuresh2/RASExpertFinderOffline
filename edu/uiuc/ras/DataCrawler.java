package edu.uiuc.ras;

import java.io.IOException;

/**
 * This class is used to perform the crawling of the web pages
 * using a third party library - Apache Nutch
 * 
 * Output: The crawlDataFolder contains the output of the crawl
 * 
 * @author adarshms
 */

public class DataCrawler
{
	private String nutchHome;
	private String crawlDataFolder;
	
	/**
	 * Constructor to initialize the DataCrawler object.
	 * @param nutchHome crawlDataFolder
	 */
	
	public DataCrawler(String nutchHome, String crawlDataFolder)
	{
		this.crawlDataFolder = crawlDataFolder;
		this.nutchHome = nutchHome;
	}
	
	/**
	 * This method starts the crawling of the web pages by executing 
	 * a ShellCommand
	 * 
	 * @param
	 * @return
	 */
	
	void startCrawl() throws IOException, InterruptedException
	{
		try
		{
			String startCrawlCommand = nutchHome + "bin/nutch crawl urls -dir "
							+ crawlDataFolder + " -depth 5 -topN 50";
			
			ShellCommandExecutor shellCommand = new ShellCommandExecutor(startCrawlCommand);
			int exitValue = shellCommand.execute();
			System.out.println("Exit value : " + exitValue);
		}
		catch(Exception e)
		{
			System.out.println("Exception in startCrawl() - " + e);
		}
	}
}