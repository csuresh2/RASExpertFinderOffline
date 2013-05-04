package edu.uiuc.ras;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.security.*;

/**
 * This class is used to parse the exported Nutch segments and create one 
 * document for each web page crawled
 * 
 * Output: The parseDataFolder contains the parsed files
 * 
 * @author adarshms
 */


public class SegmentDataParser 
{
	private String readDataFolder;
	private String parseDataFolder;
	
	/**
	 * Constructor to initialize the SegmentDataParser object.
	 * @param readDataFolder, parseDataFolder
	 */
	
	public SegmentDataParser(String readDataFolder, String parseDataFolder)
	{
		this.readDataFolder = readDataFolder;
		this.parseDataFolder = parseDataFolder;
	}

	/*
	 * This method lists all the exported segment text files performs 
	 * the readSegment operation on each of the file
	 */
	
	void parseContent()
	{
		File[] segmentFolders = new File(readDataFolder + "segments/").listFiles();
		for(File folder: segmentFolders)
			parseSegment(folder.getName());
	}
	
	/*
	 * This method parses the content of each segment text file and 
	 * stores them into different document files, one document per url
	 * crawled
	 */
	
	void parseSegment(String segmentFolderName)
	{
		try
		{
			BufferedReader br=new BufferedReader(new FileReader(readDataFolder + "segments/" + segmentFolderName + "/dump"));
			BufferedWriter bwUrl=new BufferedWriter(new FileWriter(parseDataFolder + "urls.txt", true));
			String curline;
			while((curline=br.readLine())!=null)
			{
				if(curline.contains("Recno::"))
				{
					String url=br.readLine();
					url = url.substring(url.indexOf("http://"));
					MessageDigest md=MessageDigest.getInstance("MD5");
					md.update(url.getBytes(), 0, url.length());
					
					String digest = new BigInteger(1, md.digest()).toString(16);
					curline=br.readLine(); //skip blank line
					curline=br.readLine(); //skip "ParseText"
					
					String parseText=br.readLine();
					File output=new File(parseDataFolder+digest+".content");
					
					if (!output.exists()) {
						output.createNewFile();
					}
					
					FileWriter fwContent = new FileWriter(output.getAbsoluteFile());
					BufferedWriter bwContent=new BufferedWriter(fwContent);
					bwContent.write(parseText);
					bwContent.close();
					bwUrl.append(url + "\n");
				}
			}
			bwUrl.close();
			br.close();
		}
		
		catch(Exception e)
		{
			System.out.println("Exception in parseSegment() " + e);
		}	
	}
		
	public static void main(String args[])
	{
		SegmentDataParser index = new SegmentDataParser("/home/adarshms/academics/cs410/project/read_data/", "/home/adarshms/academics/cs410/project/parse_data/");
		index.parseContent();
	}
}