package edu.uiuc.ras;
import java.io.IOException;


public class RASMain 
{
	public static void main(String args[]) throws IOException, InterruptedException
	{
		String rootFolder = "/home/adarshms/academics/cs410/project/data/";
		String nutchHome = "/home/adarshms/academics/cs410/project/nutch/";
		String crawlDataFolder = rootFolder + "crawl_data/";
		String readDataFolder = rootFolder + "read_data/";
		String parseDataFolder = rootFolder + "parse_data/";
		String filterDataFolder = rootFolder + "filter_data/";
		String taggedDataFolder = rootFolder + "tagged_data/";
		String extractDataFolder = rootFolder + "extract_data/";
		
		DataCrawler crawler = new DataCrawler(nutchHome, crawlDataFolder);
		crawler.startCrawl();
		
		SegmentDataReader readData = new SegmentDataReader(crawlDataFolder, 
				readDataFolder, nutchHome);
		readData.readContent();
		
		SegmentDataParser parseData = new SegmentDataParser(readDataFolder, 
				parseDataFolder);
		parseData.parseContent();
		
		ContentFilter filterData = new ContentFilter(parseDataFolder, 
				filterDataFolder, "");
		filterData.filterData();
		
		NamedEntityTagger tagData = new NamedEntityTagger(filterDataFolder, 
				taggedDataFolder);;
		tagData.tagData();
		
		NamedEntityExtractor extractData = new NamedEntityExtractor(taggedDataFolder,
				extractDataFolder);
		extractData.createEntityFiles();

		ConceptTagger tagConcepts = new ConceptTagger(extractDataFolder,
				taggedDataFolder);
		tagConcepts.tagConcepts();
		
		extractData.enrichEntityFiles();
	}
}
