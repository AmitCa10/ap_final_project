package servlets.config;

import graph.Agent;

/**
 * @file AgentFactory.java
 * @brief Interface for creating agent instances
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This interface defines the contract for creating agents from configuration.
 * It supports the Factory Method pattern and Open-Closed Principle by allowing
 * new agent types to be added without modifying existing code.
 */
public interface AgentFactory {
    
    /**
     * @brief Creates an agent instance from configuration
     * @param config The agent configuration containing class name and topics
     * @return A configured and initialized agent instance
     * @throws AgentCreationException if the agent cannot be created
     */
    Agent createAgent(AgentConfiguration config) throws AgentCreationException;
    
    /**
     * @brief Checks if this factory can create the specified agent type
     * @param agentClass The agent class name to check
     * @return true if this factory can create the agent type, false otherwise
     */
    boolean canCreateAgent(String agentClass);
    
    /**
     * @brief Returns the supported agent types by this factory
     * @return Array of supported agent class names
     */
    String[] getSupportedAgentTypes();
}
