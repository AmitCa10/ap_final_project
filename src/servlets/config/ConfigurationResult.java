package servlets.config;

import graph.Agent;
import java.util.List;

/**
 * @file ConfigurationResult.java
 * @brief Result class for configuration loading operations
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This immutable class encapsulates the result of a configuration loading operation,
 * providing detailed information about what was created and any issues encountered.
 * Follows the Result pattern for better error handling and reporting.
 */
public final class ConfigurationResult {
    
    /** @brief Whether the configuration loading was successful */
    private final boolean success;
    
    /** @brief The configuration format that was used */
    private final String format;
    
    /** @brief Number of agents that were configured */
    private final int agentCount;
    
    /** @brief Number of topics that were created */
    private final int topicCount;
    
    /** @brief List of successfully created agents */
    private final List<Agent> createdAgents;
    
    /** @brief Result message (success or error description) */
    private final String message;
    
    /**
     * @brief Constructor for successful configuration result
     * @param success Whether the operation succeeded
     * @param format The configuration format used
     * @param agentCount Number of agents configured
     * @param topicCount Number of topics created
     * @param createdAgents List of created agent instances
     * @param message Result message
     */
    public ConfigurationResult(boolean success, String format, int agentCount, 
                             int topicCount, List<Agent> createdAgents, String message) {
        this.success = success;
        this.format = format != null ? format : "unknown";
        this.agentCount = agentCount;
        this.topicCount = topicCount;
        this.createdAgents = createdAgents != null ? new java.util.ArrayList<>(createdAgents) : new java.util.ArrayList<>();
        this.message = message != null ? message : "";
    }
    
    /**
     * @brief Constructor for failed configuration result
     * @param message Error message describing the failure
     */
    public static ConfigurationResult failure(String message) {
        return new ConfigurationResult(false, "unknown", 0, 0, new java.util.ArrayList<>(), message);
    }
    
    /**
     * @brief Gets whether the configuration loading was successful
     * @return true if successful, false otherwise
     */
    public boolean isSuccess() {
        return success;
    }
    
    /**
     * @brief Gets the configuration format that was used
     * @return The format name (e.g., "JSON", "CONF")
     */
    public String getFormat() {
        return format;
    }
    
    /**
     * @brief Gets the number of agents that were configured
     * @return The agent count
     */
    public int getAgentCount() {
        return agentCount;
    }
    
    /**
     * @brief Gets the number of topics that were created
     * @return The topic count
     */
    public int getTopicCount() {
        return topicCount;
    }
    
    /**
     * @brief Gets the list of successfully created agents
     * @return Immutable list of created agents
     */
    public List<Agent> getCreatedAgents() {
        return createdAgents;
    }
    
    /**
     * @brief Gets the result message
     * @return Success or error message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * @brief Creates a detailed summary of the configuration result
     * @return Formatted summary string
     */
    public String getSummary() {
        if (success) {
            return String.format("Successfully loaded %s configuration with %d agents and %d topics: %s",
                               format, agentCount, topicCount, message);
        } else {
            return String.format("Configuration loading failed: %s", message);
        }
    }
    
    @Override
    public String toString() {
        return String.format("ConfigurationResult{success=%s, format='%s', agents=%d, topics=%d, message='%s'}",
                           success, format, agentCount, topicCount, message);
    }
}
