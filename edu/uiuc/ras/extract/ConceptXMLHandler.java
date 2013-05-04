package edu.uiuc.ras.extract;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class holds the XMLdriver's parsedObject that will then be returned to 
 * the NamedEntityExtractor. This does the major parsing work for the XMLDriver 
 * class and populates the parsedObject.
 * 
 * @author chethans
 */
public class ConceptXMLHandler implements XMLHandler
{
	ParsedXMLObject parsedObject;
	boolean isTypePerson;
	boolean isTypeOrganization;

	public ConceptXMLHandler(ParsedXMLObject parsedObject)
	{
		this.parsedObject = parsedObject;
	}

	public void handleResultTag(Document doc)
	{
		System.out.println(doc.getDocumentElement().getNodeName() + ":");
		System.out.println("\t" + doc.getElementsByTagName("concepts").item(0).getNodeName() + ":");
		Node concepts = doc.getElementsByTagName("concepts").item(0);
		NodeList conceptList = doc.getElementsByTagName("concept");
		handleConceptTag(conceptList, "\t");
		System.out.println("\tEnd " + concepts.getNodeName());
		System.out.println("End " + doc.getDocumentElement().getNodeName());
	}

	public void handleConceptTag(NodeList nodeList, String tab)
	{
		for(int i=0; i < nodeList.getLength(); i++)
		{
			System.out.println(tab + "Concept:");
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				System.out.println(handleConceptTextTag("text", element, "\t\t"));
			}
			System.out.println(tab + "End Concept");
		}
	}

	public String handleConceptTextTag(String tagName, Element element, String tab)
	{
		StringBuffer returnString = new StringBuffer();

		for(int i = 0; i < element.getElementsByTagName(tagName).getLength(); i++)
		{
			NodeList nodeList = element.getElementsByTagName(tagName).item(i).getChildNodes();
			Node node = (Node) nodeList.item(0);
			returnString.append(tab + tagName + ": " + node.getNodeValue());

			if(tagName.equals("text"))
			{
				parsedObject.getDescriptor().addConcept(node.getNodeValue());
			}
		}

		return returnString.toString();
	}

	public ParsedXMLObject getParsedObject()
	{
		return parsedObject;
	}
}
