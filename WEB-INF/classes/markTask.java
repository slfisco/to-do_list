// markTask servlet
// changes xml Status node text from Complete to Not Complete or from Not Complete to Complete, updates xml and refreshes jsp
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

public class markTask extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
	throws IOException, ServletException {
		try{
			String identifier = (String)request.getParameter("id");
			// DOM setup
			File xmlFile = new File(getServletContext().getRealPath("/") + "//database.xml");
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(xmlFile);
			NodeList taskNodeList = document.getElementsByTagName("Task");
			// cycles through task nodes to find node with specific id
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
			// save XML and refresh jsp
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);
			String requestURL = request.getRequestURL().toString();
			response.setHeader("Refresh", "0; URL=" + requestURL.substring(0,requestURL.length() - 8));
			}	catch(Exception exc) {
			}
	}
}
