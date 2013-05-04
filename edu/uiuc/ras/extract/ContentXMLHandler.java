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
public class ContentXMLHandler implements XMLHandler
{
	ParsedXMLObject parsedObject;
	boolean isTypePerson;
	boolean isTypeOrganization;

	public ContentXMLHandler(ParsedXMLObject parsedObject)
	{
		this.parsedObject = parsedObject;
	}

	public void handleResultTag(Document doc)
	{
		System.out.println(doc.getDocumentElement().getNodeName() + ":");
		System.out.println("\t" + doc.getElementsByTagName("entities").item(0).getNodeName() + ":");
		Node entities = doc.getElementsByTagName("entities").item(0);
		NodeList entityList = doc.getElementsByTagName("entity");
		handleEntityTag(entityList, "\t");
		System.out.println("\tEnd " + entities.getNodeName());
		System.out.println("End " + doc.getDocumentElement().getNodeName());
	}

	public void handleEntityTag(NodeList nodeList, String tab)
	{
		for(int i=0; i < nodeList.getLength(); i++)
		{
			System.out.println(tab + "Entity:");
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				Element element = (Element) node;
				isTypePerson = false;
				isTypeOrganization = false;
				System.out.println(handleTextTag("type", element, "\t\t"));
				System.out.println(handleTextTag("relevance", element, "\t\t"));
				System.out.println(handleTextTag("text", element, "\t\t"));
			}
			System.out.println(tab + "End Entity");
		}
	}

	public String handleTextTag(String tagName, Element element, String tab)
	{
		StringBuffer returnString = new StringBuffer();
		for(int i = 0; i < element.getElementsByTagName(tagName).getLength(); i++)
		{
			NodeList nodeList = element.getElementsByTagName(tagName).item(i).getChildNodes();
			Node node = (Node) nodeList.item(0);
			returnString.append(tab + tagName + ": " + node.getNodeValue());

			if(tagName.equals("type") && node.getNodeValue().equals("Person"))
			{
				isTypePerson = true;
			}
			if(tagName.equals("type") && (node.getNodeValue().equals("Organization") || 
					node.getNodeValue().equals("Company")))
			{
				isTypeOrganization = true;
			}

			// Populate all the key values of this xml file.
			if(tagName.equals("text") && !isTypePerson)
			{
				parsedObject.getDescriptor().addKeyValue(node.getNodeValue());
			}

			// Build the keys of this xml file, i.e., the Person name.
			if(isTypePerson && tagName.equals("text"))
			{
				parsedObject.getKeys().add(node.getNodeValue());
			}

			// Add the organization/Company text to indicate the 
			// entity's organization.
			if(isTypeOrganization && tagName.equals("text"))
			{
				parsedObject.getDescriptor().getOrganizationsList().add(node.getNodeValue());
			}
		}

		return returnString.toString();
	}

	public ParsedXMLObject getParsedObject()
	{
		return parsedObject;
	}
}
