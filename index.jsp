<%@page language="java" contentType="text/html" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
   <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
   <title>To-Do List App</title>
   <link href="${pageContext.request.contextPath}//layout.css" rel="stylesheet" type="text/css">
</head>
   
<body>
<div id="title">
	My To-Do List
</div>
<p>
<%@ page import="java.io.File,javax.xml.parsers.DocumentBuilder,javax.xml.parsers.DocumentBuilderFactory,org.w3c.dom.Document,org.w3c.dom.NodeList,org.w3c.dom.Node,org.w3c.dom.Element" %>
<%
//DOM setup
File file = new File(getServletContext().getRealPath("/") + "//database.xml");
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
DocumentBuilder db = dbf.newDocumentBuilder();
Document document = db.parse(file);

//display XML as tasks
NodeList taskNodeList = document.getElementsByTagName("Task"); //makes a nodelist of all tasks
for (int i = 0; i<taskNodeList.getLength(); i++) { //cycle through task nodes
	Node taskNode = taskNodeList.item(i);
	Element taskElement = (Element) taskNode;
//stores taskID attribute to send to servlet
	String taskID = taskNode.getAttributes().getNamedItem("id").getNodeValue();
//gets child nodes (name and status) from task element
	NodeList textNodeList = taskElement.getChildNodes();
//go through child nodes to get name and status elements
	for (int j = 0; j<textNodeList.getLength(); j++) {
//if an element node, get the text from it (could be text node)
		if (textNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) { 
			Node textnode = textNodeList.item(j);
			if (textnode.getNodeName() == "Name") {
%>				
<%-- display Task Number (not equal to ID) and task name --%>
				<p><div id="task"><%= i+1%>) <%= textnode.getTextContent()%></div>
<%				
			} 
			else if (textnode.getNodeName() == "Status") {
%> 
<%-- display status, change status and remove task buttons --%>
					Status: <% if (textnode.getTextContent().equals("Complete")) {
								%> <font color="LimeGreen"><%= textnode.getTextContent()%></font>
					<% } 
					else { 
								%> <font color="red"><%= textnode.getTextContent()%></font>
<% 								} 
%>					<br>
					<form action="markTask" method="GET">
						<button type="submit">Change Status</button>
						<input type="hidden" name="id" value= "<%= taskID %>">
					</form>
					<form action="removeTask" method="GET">
						<button type="submit">Remove Task</button>
						<input type="hidden" name="id" value= "<%= taskID %>">
					</form>
				</p>
				<br>
<% 			}
		}
	}
} //end XML entries %>
</p>
<%-- display add task field and button --%>
Enter new task here:
	<form action="addTask" method="GET">
		<input type="text" name="name" required="required">
		<button type="submit">Add Task</button>
	</form>
</body>
</html>