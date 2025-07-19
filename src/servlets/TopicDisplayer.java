package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import graph.Message;
import graph.Topic;
import views.View;
import views.ViewFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

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
            
            // Add debugging
            System.out.println("TopicDisplayer: Found " + topics.size() + " topics");
            for (Topic t : topics) {
                System.out.println("TopicDisplayer: Topic - " + t.name + " with message: " + 
                    (t.getLastMessage() != null ? t.getLastMessage().asText : "null"));
            }
            
            // Send proper HTTP headers first
            toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
            toClient.write("Content-Type: text/html; charset=UTF-8\r\n".getBytes());
            toClient.write("Cache-Control: no-cache, no-store, must-revalidate\r\n".getBytes());
            toClient.write("Pragma: no-cache\r\n".getBytes());
            toClient.write("Expires: 0\r\n".getBytes());
            toClient.write("\r\n".getBytes());
            
            // Use the TopicView to render the response
            View topicView = ViewFactory.createView(ViewFactory.ViewType.TOPIC_TABLE);
            
            if (topics.isEmpty()) {
                topicView.render("No configuration loaded", toClient);
            } else {
                // Convert Collection to List for the view
                List<Topic> topicList = new ArrayList<>(topics);
                topicView.render(topicList, toClient);
            }
            
        } catch (Exception e) {
            System.out.println("TopicDisplayer Error: " + e.getMessage());
            e.printStackTrace();
            
            // Use the TopicView to render the error
            View topicView = ViewFactory.createView(ViewFactory.ViewType.TOPIC_TABLE);
            
            try {
                // Write error status
                toClient.write("HTTP/1.1 500 Internal Server Error\r\n".getBytes());
                toClient.write("Content-Type: text/html\r\n".getBytes());
                toClient.write("\r\n".getBytes());
                
                // Render error through view
                topicView.render("Error: " + e.getMessage(), toClient);
            } catch (IOException ioError) {
                System.err.println("Failed to send error response: " + ioError.getMessage());
            }
        }
    }

    @Override
    public void close() throws IOException {
        // Nothing to close
    }
}
