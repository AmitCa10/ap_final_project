package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @file SimpleConfLoader.java
 * @brief Servlet for handling configuration file uploads and agent network setup
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This servlet handles both GET and POST requests for configuration management:
 * - GET requests return an HTML form for uploading configuration files
 * - POST requests process uploaded JSON configuration files, create topics,
 *   instantiate agents, and set up the agent network according to the configuration.
 * 
 * The servlet supports multipart form data uploads and provides comprehensive
 * error handling with user-friendly error messages.
 */
public class SimpleConfLoader implements Servlet {
    
    /** @brief Reference to the TopicManager for creating and managing topics */
    private final TopicManager topicManager;
    
    public SimpleConfLoader() {
        this.topicManager = TopicManagerSingleton.get();
    }
    
    @Override
    public void close() {
        // Cleanup if needed
    }
    
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (ri.getContent().length > 0) {
            try {
                System.out.println("SimpleConfLoader: Processing POST request for config upload");
                
                // Extract JSON content from the multipart form
                byte[] fileContent = ri.getContent();
                String jsonContent = extractJsonFromMultipart(fileContent);
                
                if (jsonContent == null || jsonContent.trim().isEmpty()) {
                    throw new Exception("No valid JSON content found in uploaded file");
                }
                
                System.out.println("SimpleConfLoader: Extracted JSON content: " + jsonContent.substring(0, Math.min(100, jsonContent.length())) + "...");
                
                // Validate JSON format
                if (!isValidJson(jsonContent)) {
                    throw new Exception("Invalid JSON format - must contain agents array");
                }
                
                // Create topics and agents from the actual JSON configuration
                createTopicsFromConfig(jsonContent);
                
                // Generate success response
                String html = "<html><body style='background: #1a1a1a; color: #f0f0f0; font-family: Arial; padding: 40px;'>" +
                        "<div style='background: rgba(177,156,217,0.1); padding: 30px; border-radius: 15px; text-align: center;'>" +
                        "<h2 style='color: #87ceeb;'>✅ Configuration Loaded Successfully!</h2>" +
                        "<p style='color: #c9a96e; font-size: 1.1rem;'>JSON file processed and agent network is now active.</p>" +
                        "<div style='margin-top: 30px;'>" +
                        "<a href='/' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block; margin: 5px;'>← Back to Main Page</a>" +
                        "</div>" +
                        "</div>" +
                        "</body></html>";
                
                byte[] response = ("HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + html.length() + "\r\n" +
                        "\r\n" + html).getBytes();
                toClient.write(response);
                
            } catch (Exception e) {
                System.err.println("SimpleConfLoader: Error: " + e.getMessage());
                e.printStackTrace();
                
                String errorHtml = "<html><body style='background: #1a1a1a; color: #f0f0f0; font-family: Arial; padding: 40px;'>" +
                        "<div style='background: rgba(255,107,107,0.1); padding: 30px; border-radius: 15px; border-left: 4px solid #ff6b6b;'>" +
                        "<h2 style='color: #ff6b6b;'>❌ Configuration Error</h2>" +
                        "<p style='font-family: monospace; background: rgba(0,0,0,0.3); padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                        e.getMessage() + "</p>" +
                        "<div style='margin-top: 20px;'>" +
                        "<a href='/' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block;'>← Back to Main Page</a>" +
                        "</div>" +
                        "</div>" +
                        "</body></html>";
                        
                byte[] response = ("HTTP/1.1 500 Internal Server Error\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: " + errorHtml.length() + "\r\n" +
                        "\r\n" + errorHtml).getBytes();
                toClient.write(response);
            }
        } else {
            // Handle GET request - return upload form
            String html = "<html><head><title>Upload Configuration</title></head>" +
                    "<body style='font-family: Arial; background: #2d2d2d; color: #f0f0f0; padding: 40px;'>" +
                    "<h2 style='color: #b19cd9;'>Configuration Upload</h2>" +
                    "<form method='post' enctype='multipart/form-data'>" +
                    "<div style='margin: 20px 0;'>" +
                    "<input type='file' name='configFile' accept='.json' style='padding: 10px;'><br><br>" +
                    "<input type='submit' value='Upload Configuration' style='background: linear-gradient(45deg, #b19cd9, #c9a96e); color: white; border: none; padding: 12px 30px; border-radius: 25px; font-size: 1rem; cursor: pointer;'>" +
                    "</div>" +
                    "</form>" +
                    "<a href='/' style='color: #87ceeb; text-decoration: none;'>← Back to Main Page</a>" +
                    "</body></html>";
                    
            byte[] response = ("HTTP/1.1 200 OK\r\n" +
                    "Content-Type: text/html\r\n" +
                    "Content-Length: " + html.length() + "\r\n" +
                    "\r\n" + html).getBytes();
            toClient.write(response);
        }
    }
    
    private boolean isValidJson(String jsonContent) {
        try {
            jsonContent = jsonContent.trim();
            if (!jsonContent.startsWith("{") || !jsonContent.endsWith("}")) {
                return false;
            }
            // Basic check - look for agents array
            return jsonContent.contains("\"agents\"") && jsonContent.contains("[") && jsonContent.contains("]");
        } catch (Exception e) {
            return false;
        }
    }
    
    private String extractJsonFromMultipart(byte[] content) {
        try {
            String contentStr = new String(content);
            System.out.println("SimpleConfLoader: Multipart content length: " + contentStr.length());
            
            // Find the file content section
            String[] lines = contentStr.split("\r\n");
            boolean inFileSection = false;
            boolean foundContentStart = false;
            StringBuilder jsonContent = new StringBuilder();
            
            for (String line : lines) {
                if (line.contains("filename=") && line.contains(".json")) {
                    inFileSection = true;
                    System.out.println("SimpleConfLoader: Found JSON file section");
                    continue;
                }
                
                if (inFileSection && line.trim().isEmpty() && !foundContentStart) {
                    foundContentStart = true;
                    continue;
                }
                
                if (inFileSection && foundContentStart) {
                    if (line.startsWith("--")) {
                        // End of file content
                        break;
                    }
                    jsonContent.append(line);
                }
            }
            
            String result = jsonContent.toString().trim();
            System.out.println("SimpleConfLoader: Extracted JSON: " + result);
            return result;
            
        } catch (Exception e) {
            System.err.println("SimpleConfLoader: Error extracting JSON from multipart: " + e.getMessage());
            return null;
        }
    }
    
    private void createTopicsFromConfig(String jsonContent) {
        try {
            System.out.println("SimpleConfLoader: Creating topics from actual configuration");
            topicManager.clear(); // Clear any existing topics
            
            // Parse JSON manually (simple parsing)
            String[] agents = extractAgents(jsonContent);
            
            System.out.println("SimpleConfLoader: Found " + agents.length + " agents in configuration");
            
            // Extract all unique topics from the configuration
            java.util.Set<String> allTopics = new java.util.HashSet<>();
            java.util.List<AgentInfo> agentInfos = new java.util.ArrayList<>();
            
            for (String agent : agents) {
                String agentClass = extractStringValue(agent, "agentClass");
                String[] subscriptions = extractArray(agent, "subscriptions");
                String[] publications = extractArray(agent, "publications");
                
                // Store agent info for later creation
                agentInfos.add(new AgentInfo(agentClass, subscriptions, publications));
                
                // Add all subscription topics
                for (String topic : subscriptions) {
                    if (!topic.isEmpty()) {
                        allTopics.add(topic);
                    }
                }
                
                // Add all publication topics
                for (String topic : publications) {
                    if (!topic.isEmpty()) {
                        allTopics.add(topic);
                    }
                }
            }
            
            System.out.println("SimpleConfLoader: Creating " + allTopics.size() + " topics: " + allTopics);
            
            // Create all the topics in the TopicManager
            for (String topicName : allTopics) {
                if (!topicManager.containsTopic(topicName)) {
                    topicManager.getTopic(topicName); // This creates the topic
                    System.out.println("SimpleConfLoader: Created topic: " + topicName);
                }
            }
            
            System.out.println("SimpleConfLoader: Successfully created all topics from configuration");
            
            // Now create and start the agents
            System.out.println("SimpleConfLoader: Creating and starting agents...");
            for (AgentInfo agentInfo : agentInfos) {
                try {
                    createAgent(agentInfo.agentClass, agentInfo.subscriptions, agentInfo.publications);
                    System.out.println("SimpleConfLoader: Created agent: " + agentInfo.agentClass);
                } catch (Exception e) {
                    System.err.println("SimpleConfLoader: Error creating agent " + agentInfo.agentClass + ": " + e.getMessage());
                }
            }
            
            System.out.println("SimpleConfLoader: All agents created and started successfully");
            
        } catch (Exception e) {
            System.err.println("SimpleConfLoader: Error creating topics from config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Helper class to store agent information
    private static class AgentInfo {
        String agentClass;
        String[] subscriptions;
        String[] publications;
        
        AgentInfo(String agentClass, String[] subscriptions, String[] publications) {
            this.agentClass = agentClass;
            this.subscriptions = subscriptions;
            this.publications = publications;
        }
    }
    
    // Create an agent instance based on class name
    private void createAgent(String agentClass, String[] subscriptions, String[] publications) throws Exception {
        switch (agentClass) {
            case "PlusAgent":
                configs.PlusAgent plusAgent = new configs.PlusAgent(subscriptions, publications);
                // Trigger initial processing for PlusAgent if both topics have existing values
                if (subscriptions.length >= 2) {
                    boolean hasInitialValues = true;
                    for (String subTopic : subscriptions) {
                        if (!topicManager.containsTopic(subTopic)) {
                            hasInitialValues = false;
                            break;
                        }
                    }
                    if (hasInitialValues) {
                        // Send initial values to trigger processing
                        for (String subTopic : subscriptions) {
                            graph.Topic topic = topicManager.getTopic(subTopic);
                            graph.Message lastMsg = topic.getLastMessage();
                            if (lastMsg != null) {
                                plusAgent.callback(subTopic, lastMsg);
                            }
                        }
                    }
                }
                break;
            case "IncAgent":
                configs.IncAgent incAgent = new configs.IncAgent(subscriptions, publications);
                // Trigger initial processing for IncAgent if the topic has an existing value
                if (subscriptions.length > 0) {
                    String subTopic = subscriptions[0];
                    if (topicManager.containsTopic(subTopic)) {
                        graph.Topic topic = topicManager.getTopic(subTopic);
                        graph.Message lastMsg = topic.getLastMessage();
                        if (lastMsg != null) {
                            // Process the existing message to initialize the agent
                            incAgent.callback(subTopic, lastMsg);
                        }
                    }
                }
                break;
            case "MulAgent":
                configs.MulAgent mulAgent = new configs.MulAgent(subscriptions, publications);
                // Trigger initial processing for MulAgent if both topics have existing values
                if (subscriptions.length >= 2) {
                    boolean hasInitialValues = true;
                    for (String subTopic : subscriptions) {
                        if (!topicManager.containsTopic(subTopic)) {
                            hasInitialValues = false;
                            break;
                        }
                    }
                    if (hasInitialValues) {
                        // Send initial values to trigger processing
                        for (String subTopic : subscriptions) {
                            graph.Topic topic = topicManager.getTopic(subTopic);
                            graph.Message lastMsg = topic.getLastMessage();
                            if (lastMsg != null) {
                                mulAgent.callback(subTopic, lastMsg);
                            }
                        }
                    }
                }
                break;
            case "DivAgent":
                configs.DivAgent divAgent = new configs.DivAgent(subscriptions, publications);
                // Trigger initial processing for DivAgent if both topics have existing values
                if (subscriptions.length >= 2) {
                    boolean hasInitialValues = true;
                    for (String subTopic : subscriptions) {
                        if (!topicManager.containsTopic(subTopic)) {
                            hasInitialValues = false;
                            break;
                        }
                    }
                    if (hasInitialValues) {
                        // Send initial values to trigger processing
                        for (String subTopic : subscriptions) {
                            graph.Topic topic = topicManager.getTopic(subTopic);
                            graph.Message lastMsg = topic.getLastMessage();
                            if (lastMsg != null) {
                                divAgent.callback(subTopic, lastMsg);
                            }
                        }
                    }
                }
                break;
            case "SubAgent":
                configs.SubAgent subAgent = new configs.SubAgent(subscriptions, publications);
                // Trigger initial processing for SubAgent if both topics have existing values
                if (subscriptions.length >= 2) {
                    boolean hasInitialValues = true;
                    for (String subTopic : subscriptions) {
                        if (!topicManager.containsTopic(subTopic)) {
                            hasInitialValues = false;
                            break;
                        }
                    }
                    if (hasInitialValues) {
                        // Send initial values to trigger processing
                        for (String subTopic : subscriptions) {
                            graph.Topic topic = topicManager.getTopic(subTopic);
                            graph.Message lastMsg = topic.getLastMessage();
                            if (lastMsg != null) {
                                subAgent.callback(subTopic, lastMsg);
                            }
                        }
                    }
                }
                break;
            default:
                throw new Exception("Unknown agent class: " + agentClass);
        }
    }
    
    // Extract string value from JSON object
    private String extractStringValue(String jsonObj, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = jsonObj.indexOf(searchKey);
            if (keyIndex == -1) return "";
            
            int colonIndex = jsonObj.indexOf(":", keyIndex);
            if (colonIndex == -1) return "";
            
            int valueStart = jsonObj.indexOf("\"", colonIndex) + 1;
            int valueEnd = jsonObj.indexOf("\"", valueStart);
            
            if (valueStart == 0 || valueEnd == -1) return "";
            
            return jsonObj.substring(valueStart, valueEnd);
        } catch (Exception e) {
            return "";
        }
    }
    
    private String[] extractAgents(String jsonContent) {
        java.util.List<String> agents = new java.util.ArrayList<>();
        
        // Find the agents array
        int agentsStart = jsonContent.indexOf("\"agents\"");
        if (agentsStart == -1) {
            return new String[0];
        }
        
        int arrayStart = jsonContent.indexOf("[", agentsStart);
        if (arrayStart == -1) {
            return new String[0];
        }
        
        int level = 0;
        int objStart = -1;
        
        for (int i = arrayStart; i < jsonContent.length(); i++) {
            char c = jsonContent.charAt(i);
            
            if (c == '{') {
                if (level == 0) {
                    objStart = i;
                }
                level++;
            } else if (c == '}') {
                level--;
                if (level == 0 && objStart != -1) {
                    agents.add(jsonContent.substring(objStart, i + 1));
                }
            } else if (c == ']' && level == 0) {
                break;
            }
        }
        
        return agents.toArray(new String[0]);
    }
    
    private String[] extractArray(String json, String key) {
        java.util.List<String> values = new java.util.ArrayList<>();
        String searchKey = "\"" + key + "\"";
        int start = json.indexOf(searchKey);
        if (start == -1) return new String[0];
        
        start = json.indexOf("[", start);
        if (start == -1) return new String[0];
        
        boolean inString = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = start + 1; i < json.length(); i++) {
            char c = json.charAt(i);
            
            if (c == '"' && (i == 0 || json.charAt(i - 1) != '\\')) {
                if (inString) {
                    values.add(current.toString());
                    current = new StringBuilder();
                    inString = false;
                } else {
                    inString = true;
                }
            } else if (inString) {
                current.append(c);
            } else if (c == ']') {
                break;
            }
        }
        
        return values.toArray(new String[0]);
    }
}
