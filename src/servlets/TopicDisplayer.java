package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import graph.Message;
import graph.Topic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TopicDisplayer implements Servlet {

    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        try {
            TopicManager tm = TopicManagerSingleton.get();
            
            // Handle POST request for publishing messages
            if ("POST".equals(ri.getHttpCommand())) {
                String topicName = null;
                String messageText = null;
                
                // Try to get from URL parameters first
                if (ri.getParameters().size() >= 2) {
                    topicName = ri.getParameters().get("topicName");
                    messageText = ri.getParameters().get("message");
                } else if (ri.getContent().length > 0) {
                    // Parse form data from POST body
                    String contentStr = new String(ri.getContent());
                    System.out.println("TopicDisplayer: Received POST data: " + contentStr);
                    
                    // Simple form data parsing
                    String[] pairs = contentStr.split("&");
                    Map<String, String> formData = new HashMap<>();
                    
                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=", 2);
                        if (keyValue.length == 2) {
                            try {
                                String key = java.net.URLDecoder.decode(keyValue[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");
                                formData.put(key, value);
                            } catch (Exception e) {
                                System.err.println("Error decoding form data: " + e.getMessage());
                            }
                        }
                    }
                    
                    topicName = formData.get("topicName");
                    messageText = formData.get("message");
                }
                
                if (topicName != null && messageText != null) {
                    System.out.println("TopicDisplayer: Publishing message '" + messageText + "' to topic '" + topicName + "'");
                    
                    if (!tm.containsTopic(topicName)) {
                        throw new Exception("Topic '" + topicName + "' does not exist");
                    }
                    
                    Topic topic = tm.getTopic(topicName);
                    Message message = new Message(messageText);
                    topic.publish(message);
                    System.out.println("TopicDisplayer: Message published successfully");
                }
            }
            
            // Always return the current state of topics
            Collection<Topic> topics = tm.getTopics();
            
            if (topics.isEmpty()) {
                // Return a message indicating no configuration is loaded
                String html = "<tr><td colspan=\"2\" style=\"text-align: center; color: #87ceeb; padding: 20px;\">No configuration loaded</td></tr>";
                toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
                toClient.write("Content-Type: text/html\r\n".getBytes());
                toClient.write(("Content-Length: " + html.length() + "\r\n").getBytes());
                toClient.write("\r\n".getBytes());
                toClient.write(html.getBytes());
            } else {
                // Generate HTML table rows for topics
                StringBuilder html = new StringBuilder();
                for (Topic t : topics) {
                    String lastMessage = "No messages";
                    try {
                        Message msg = t.getLastMessage();
                        if (msg != null) {
                            lastMessage = msg.asText;
                        }
                    } catch (Exception e) {
                        lastMessage = "No messages";
                    }
                    
                    html.append("<tr>")
                        .append("<td style=\"padding: 12px 15px; border-bottom: 1px solid rgba(177, 156, 217, 0.2); color: #f0f0f0;\">")
                        .append(t.name)
                        .append("</td>")
                        .append("<td style=\"padding: 12px 15px; border-bottom: 1px solid rgba(177, 156, 217, 0.2); color: #f0f0f0;\">")
                        .append(lastMessage)
                        .append("</td>")
                        .append("</tr>");
                }
                
                String response = html.toString();
                toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
                toClient.write("Content-Type: text/html\r\n".getBytes());
                toClient.write(("Content-Length: " + response.length() + "\r\n").getBytes());
                toClient.write("\r\n".getBytes());
                toClient.write(response.getBytes());
            }
            
        } catch (Exception e) {
            System.out.println("TopicDisplayer Error: " + e.getMessage());
            e.printStackTrace();
            String errorHtml = "<tr><td colspan=\"2\" style=\"text-align: center; color: #ff6b6b; padding: 20px;\">Error: " + e.getMessage() + "</td></tr>";
            toClient.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
            toClient.write("Content-Type: text/html\r\n".getBytes());
            toClient.write(("Content-Length: " + errorHtml.length() + "\r\n").getBytes());
            toClient.write("\r\n".getBytes());
            toClient.write(errorHtml.getBytes());
        }
    }

    @Override
    public void close() throws IOException {
        // Nothing to close
    }
}
