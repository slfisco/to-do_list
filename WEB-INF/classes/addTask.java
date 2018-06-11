// addTask servlet
// creates new xml task node with name of name parameter and status of "Not Complete". Updates xml and refreshes jsp.
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

public class addTask extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws IOException, ServletException {
		try{
			String taskName = (String)request.getParameter("name");
			String newID = "";
			// DOM setup
			File xmlFile = new File(getServletContext().getRealPath("/") + "//database.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(xmlFile);
			NodeList taskNodeList = document.getElementsByTagName("Task");
			// cycles through task nodes to find last ID attribute
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
			// save XML and refresh jsp
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			String requestURL = request.getRequestURL().toString();
			response.setHeader("Refresh", "0; URL=" + requestURL.substring(0,requestURL.length() - 7));
			}	catch(Exception exc) {
			}
	}
}
