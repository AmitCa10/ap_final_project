package servlets.config;

import java.util.Arrays;

/**
 * @file AgentConfiguration.java
 * @brief Data class representing an agent's configuration
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This immutable data class holds the configuration information
 * for a single agent, including its type and topic subscriptions/publications.
 * Follows the Value Object pattern for data integrity.
 */
public final class AgentConfiguration {
    
    /** @brief The agent class name (e.g., "PlusAgent", "IncAgent") */
    private final String agentClass;
    
    /** @brief Array of topic names the agent subscribes to */
    private final String[] subscriptions;
    
    /** @brief Array of topic names the agent publishes to */
    private final String[] publications;
    
    /**
     * @brief Constructor for agent configuration
     * @param agentClass The agent class name
     * @param subscriptions Array of subscription topic names
     * @param publications Array of publication topic names
     * @throws IllegalArgumentException if agentClass is null or empty
     */
    public AgentConfiguration(String agentClass, String[] subscriptions, String[] publications) {
        if (agentClass == null || agentClass.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent class cannot be null or empty");
        }
        
        this.agentClass = agentClass.trim();
        this.subscriptions = subscriptions != null ? subscriptions.clone() : new String[0];
        this.publications = publications != null ? publications.clone() : new String[0];
        
        // Clean up topic names
        cleanTopicNames(this.subscriptions);
        cleanTopicNames(this.publications);
    }
    
    /**
     * @brief Gets the agent class name
     * @return The agent class name
     */
    public String getAgentClass() {
        return agentClass;
    }
    
    /**
     * @brief Gets a copy of the subscriptions array
     * @return Array of subscription topic names
     */
    public String[] getSubscriptions() {
        return subscriptions.clone();
    }
    
    /**
     * @brief Gets a copy of the publications array
     * @return Array of publication topic names
     */
    public String[] getPublications() {
        return publications.clone();
    }
    
    /**
     * @brief Gets all unique topic names from subscriptions and publications
     * @return Array of all unique topic names
     */
    public String[] getAllTopics() {
        java.util.Set<String> allTopics = new java.util.HashSet<>();
        
        for (String topic : subscriptions) {
            if (!topic.isEmpty()) {
                allTopics.add(topic);
            }
        }
        
        for (String topic : publications) {
            if (!topic.isEmpty()) {
                allTopics.add(topic);
            }
        }
        
        return allTopics.toArray(new String[0]);
    }
    
    /**
     * @brief Validates that the configuration is valid for agent creation
     * @return true if the configuration is valid
     */
    public boolean isValid() {
        return !agentClass.isEmpty() && 
               (subscriptions.length > 0 || publications.length > 0);
    }
    
    @Override
    public String toString() {
        return String.format("AgentConfiguration{class='%s', subs=%s, pubs=%s}", 
                           agentClass, Arrays.toString(subscriptions), Arrays.toString(publications));
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        AgentConfiguration that = (AgentConfiguration) o;
        return agentClass.equals(that.agentClass) &&
               Arrays.equals(subscriptions, that.subscriptions) &&
               Arrays.equals(publications, that.publications);
    }
    
    @Override
    public int hashCode() {
        int result = agentClass.hashCode();
        result = 31 * result + Arrays.hashCode(subscriptions);
        result = 31 * result + Arrays.hashCode(publications);
        return result;
    }
    
    /**
     * @brief Helper method to clean and validate topic names
     * @param topics Array of topic names to clean
     */
    private static void cleanTopicNames(String[] topics) {
        for (int i = 0; i < topics.length; i++) {
            if (topics[i] != null) {
                topics[i] = topics[i].trim();
            } else {
                topics[i] = "";
            }
        }
    }
}
