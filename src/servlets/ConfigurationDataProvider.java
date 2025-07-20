package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import graph.Topic;
import graph.Agent;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * @file ConfigurationDataProvider.java
 * @brief Servlet for providing current agent configuration data as JSON
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This servlet provides the current agent configuration in JSON format
 * for use by the network visualization component. It extracts information
 * from the active TopicManager to build a configuration representation.
 */
public class ConfigurationDataProvider implements Servlet {
    
    @Override
    public void close() {
        // Cleanup if needed
    }
    
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        try {
            TopicManager topicManager = TopicManagerSingleton.get();
            String jsonResponse = buildConfigurationJson(topicManager);
            sendJsonResponse(toClient, jsonResponse);
        } catch (Exception e) {
            System.err.println("ConfigurationDataProvider: Error generating configuration data: " + e.getMessage());
            sendErrorResponse(toClient, "Failed to generate configuration data");
        }
    }
    
    /**
     * @brief Builds JSON representation of current configuration
     * @param topicManager The topic manager instance
     * @return JSON string representing the configuration
     */
    private String buildConfigurationJson(TopicManager topicManager) {
        StringBuilder json = new StringBuilder();
        json.append("{\"agents\":[");
        
        // Get all topics from topic manager
        List<Topic> allTopics = new ArrayList<>(topicManager.getTopics());
        boolean firstAgent = true;
        
        // Track agents already processed to avoid duplicates
        List<Agent> processedAgents = new ArrayList<>();
        
        // Build agent configurations from topic subscribers
        for (Topic topic : allTopics) {
            List<Agent> subscribers = topic.getSubs();
            
            for (Agent agent : subscribers) {
                // Skip if we've already processed this agent
                if (processedAgents.contains(agent)) {
                    continue;
                }
                processedAgents.add(agent);
                
                if (!firstAgent) {
                    json.append(",");
                }
                firstAgent = false;
                
                json.append("{");
                json.append("\"type\":\"").append(agent.getClass().getSimpleName()).append("\",");
                json.append("\"agentClass\":\"").append(agent.getClass().getSimpleName()).append("\",");
                json.append("\"subscriptions\":[");
                
                // Get agent's subscription topics
                boolean firstSub = true;
                for (Topic t : allTopics) {
                    if (t.getSubs().contains(agent)) {
                        if (!firstSub) json.append(",");
                        json.append("\"").append(t.name).append("\"");
                        firstSub = false;
                    }
                }
                
                json.append("],");
                json.append("\"publications\":[");
                
                // Get agent's publication topics
                boolean firstPub = true;
                for (Topic t : allTopics) {
                    if (t.getPubs().contains(agent)) {
                        if (!firstPub) json.append(",");
                        json.append("\"").append(t.name).append("\"");
                        firstPub = false;
                    }
                }
                
                json.append("]}");
            }
        }
        
        json.append("]}");
        return json.toString();
    }
    
    /**
     * @brief Sends JSON response
     * @param toClient Output stream
     * @param json JSON content
     * @throws IOException if writing fails
     */
    private void sendJsonResponse(OutputStream toClient, String json) throws IOException {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "Content-Length: " + json.getBytes("UTF-8").length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" + json;
        
        toClient.write(response.getBytes("UTF-8"));
    }
    
    /**
     * @brief Sends error response
     * @param toClient Output stream
     * @param errorMessage Error message
     * @throws IOException if writing fails
     */
    private void sendErrorResponse(OutputStream toClient, String errorMessage) throws IOException {
        String json = "{\"error\":\"" + errorMessage + "\"}";
        String response = "HTTP/1.1 500 Internal Server Error\r\n" +
                "Content-Type: application/json; charset=utf-8\r\n" +
                "Content-Length: " + json.getBytes("UTF-8").length + "\r\n" +
                "Access-Control-Allow-Origin: *\r\n" +
                "\r\n" + json;
        
        toClient.write(response.getBytes("UTF-8"));
    }
}
