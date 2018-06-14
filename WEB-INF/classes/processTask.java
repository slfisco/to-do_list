///// processTask servlet
// carries out XML processes based on jsp form submission
// doGet(main) creates XML DOM, runs method, saves XML and refreshes jsp
// addTask method: adds new task node based on name submitted by jsp
// markTask method: changes a task's status from "Complete" to "Not Complete" or from "Not Complete" to "Complete"
// removeTask method: removes a task node based on id submitted by jsp
import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.servlet.*;
import javax.servlet.http.*;
import java.util.*;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class processTask extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws IOException, ServletException {
		try{
			// parameter imports
			String taskName = (String)request.getParameter("name");
			String identifier = (String)request.getParameter("id");
			String method = (String)request.getParameter("method");
			// DOM setup
			File xmlFile = new File(getServletContext().getRealPath("/") + "//database.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(xmlFile);
			NodeList taskNodeList = document.getElementsByTagName("Task");
			// trigger XML related functions based on method parameter
			if (method.equals("remove")) {
				taskNodeList = removeTask(taskNodeList, identifier);
			}
			else if (method.equals("mark")) {
				taskNodeList = markTask(taskNodeList, identifier);
			}
			else if (method.equals("add")) {
				taskNodeList = addTask(taskNodeList, taskName, document);
			}
			// save XML and refresh jsp
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			String requestURL = request.getRequestURL().toString();
			response.setHeader("Refresh", "0; URL=" + requestURL.substring(0,requestURL.length() - 11));
			}	catch(Exception exc) {
			}
	}
	// adds new task node to XML
	public NodeList addTask(NodeList taskNodeList, String taskName, Document document)
	{
	String newID = "";
		for (int i =0; i<taskNodeList.getLength(); i++) {
			Node taskNode = taskNodeList.item(i);
			Element taskElement = (Element) taskNode;
			NodeList textNodeList = taskElement.getChildNodes();
			if (i == taskNodeList.getLength() - 1) {
				String lastID = taskNode.getAttributes().getNamedItem("id").getNodeValue();
				newID = Integer.toString(Integer.parseInt(lastID) + 1); //take lastID, convert to int, add 1, convert back to string
			}
		}
		Node newTaskNode = document.createElement("Task"); //create new task node
		Node newNameNode = document.createElement("Name"); //create new text node for name
		Node newNameText = document.createTextNode(taskName); //set text to taskName parameter
		Node newStatusNode = document.createElement("Status");
		Node newStatusText = document.createTextNode("Not Complete");
		Element newTaskElement = (Element)newTaskNode;
		newTaskElement.setAttribute("id", newID); //set new task node's id attribute with lastID found earlier
		document.getDocumentElement().appendChild(newTaskNode); //add new task to root node
		newTaskNode.appendChild(newNameNode);
		newNameNode.appendChild(newNameText);
		newTaskNode.appendChild(newStatusNode);
		newStatusNode.appendChild(newStatusText);
		return taskNodeList;
	}
	// changes status text of a task in XML
	public NodeList markTask(NodeList taskNodeList, String identifier)
	{
		for (int i =0; i<taskNodeList.getLength(); i++) {
			Node taskNode = taskNodeList.item(i);
			Element taskElement = (Element) taskNode;
			NodeList textNodeList = taskElement.getChildNodes();
			if (taskNode.getAttributes().getNamedItem("id").getNodeValue().equals(identifier)) {
				// cycle children nodes to find Status. Change Status to its opposite
				for (int j = 0; j<textNodeList.getLength(); j++) {
					if (textNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Node textnode = textNodeList.item(j);
						if (textnode.getNodeName().equals("Status") && textnode.getTextContent().equals("Complete")) {
							textnode.setTextContent("Not Complete");
						}
						else if (textnode.getNodeName().equals("Status") && textnode.getTextContent().equals("Not Complete")) {
							textnode.setTextContent("Complete");
						}
					}
				}
			}
		}
		return taskNodeList;
	}
	// removes a task node from XML
	public NodeList removeTask(NodeList taskNodeList, String identifier)
	{
		for (int i =0; i<taskNodeList.getLength(); i++) {
			Node taskNode = taskNodeList.item(i);
			if (taskNode.getAttributes().getNamedItem("id").getNodeValue().equals(identifier)) {
				taskNode.getParentNode().removeChild(taskNode); 
			}
		}
		return taskNodeList;
	}
}
