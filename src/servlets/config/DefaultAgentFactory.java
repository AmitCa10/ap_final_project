package servlets.config;

import graph.Agent;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import graph.Topic;
import graph.Message;

/**
 * @file DefaultAgentFactory.java
 * @brief Default implementation of AgentFactory for standard agent types
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This factory creates instances of the standard agent types (PlusAgent, IncAgent, etc.)
 * and handles initial value processing for reactive agents.
 */
public class DefaultAgentFactory implements AgentFactory {
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager topicManager;
    
    /** @brief Array of supported agent types */
    private static final String[] SUPPORTED_TYPES = {
        "PlusAgent", "IncAgent", "MulAgent", "DivAgent", "SubAgent"
    };
    
    /**
     * @brief Constructor
     */
    public DefaultAgentFactory() {
        this.topicManager = TopicManagerSingleton.get();
    }
    
    @Override
    public Agent createAgent(AgentConfiguration config) throws AgentCreationException {
        String agentClass = config.getAgentClass();
        String[] subscriptions = config.getSubscriptions();
        String[] publications = config.getPublications();
        
        try {
            Agent agent = createAgentInstance(agentClass, subscriptions, publications);
            initializeAgentWithExistingValues(agent, agentClass, subscriptions);
            return agent;
        } catch (Exception e) {
            throw new AgentCreationException(
                "Failed to create agent: " + e.getMessage(), agentClass, e);
        }
    }
    
    @Override
    public boolean canCreateAgent(String agentClass) {
        for (String supportedType : SUPPORTED_TYPES) {
            if (supportedType.equals(agentClass)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String[] getSupportedAgentTypes() {
        return SUPPORTED_TYPES.clone();
    }
    
    /**
     * @brief Creates an agent instance based on the agent class name
     * @param agentClass The agent class name
     * @param subscriptions Array of subscription topics
     * @param publications Array of publication topics
     * @return The created agent instance
     * @throws AgentCreationException if agent creation fails
     */
    private Agent createAgentInstance(String agentClass, String[] subscriptions, String[] publications) 
            throws AgentCreationException {
        
        switch (agentClass) {
            case "PlusAgent":
                return new configs.PlusAgent(subscriptions, publications);
            case "IncAgent":
                return new configs.IncAgent(subscriptions, publications);
            case "MulAgent":
                return new configs.MulAgent(subscriptions, publications);
            case "DivAgent":
                return new configs.DivAgent(subscriptions, publications);
            case "SubAgent":
                return new configs.SubAgent(subscriptions, publications);
            default:
                throw new AgentCreationException("Unknown agent type: " + agentClass, agentClass);
        }
    }
    
    /**
     * @brief Initializes an agent with existing topic values for reactive behavior
     * @param agent The agent instance to initialize
     * @param agentClass The agent class name for type-specific handling
     * @param subscriptions Array of subscription topics
     */
    private void initializeAgentWithExistingValues(Agent agent, String agentClass, String[] subscriptions) {
        if (subscriptions.length == 0) {
            return;
        }
        
        try {
            switch (agentClass) {
                case "PlusAgent":
                case "MulAgent":
                case "DivAgent":
                case "SubAgent":
                    // Binary operation agents need both inputs
                    initializeBinaryAgent(agent, subscriptions);
                    break;
                case "IncAgent":
                    // Unary operation agents need single input
                    initializeUnaryAgent(agent, subscriptions);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Warning: Failed to initialize agent with existing values: " + e.getMessage());
            // Not a fatal error - agent will work when new messages arrive
        }
    }
    
    /**
     * @brief Initializes binary operation agents with existing topic values
     * @param agent The agent instance
     * @param subscriptions Array of subscription topics
     */
    private void initializeBinaryAgent(Agent agent, String[] subscriptions) {
        if (subscriptions.length < 2) {
            return;
        }
        
        boolean hasAllValues = true;
        for (String topicName : subscriptions) {
            if (!topicManager.containsTopic(topicName)) {
                hasAllValues = false;
                break;
            }
            
            Topic topic = topicManager.getTopic(topicName);
            Message lastMsg = topic.getLastMessage();
            if (lastMsg == null) {
                hasAllValues = false;
                break;
            }
        }
        
        if (hasAllValues) {
            System.out.println("DefaultAgentFactory: Initializing binary agent with existing values");
            for (String topicName : subscriptions) {
                Topic topic = topicManager.getTopic(topicName);
                Message lastMsg = topic.getLastMessage();
                agent.callback(topicName, lastMsg);
            }
        }
    }
    
    /**
     * @brief Initializes unary operation agents with existing topic values
     * @param agent The agent instance
     * @param subscriptions Array of subscription topics
     */
    private void initializeUnaryAgent(Agent agent, String[] subscriptions) {
        if (subscriptions.length == 0) {
            return;
        }
        
        String topicName = subscriptions[0];
        if (topicManager.containsTopic(topicName)) {
            Topic topic = topicManager.getTopic(topicName);
            Message lastMsg = topic.getLastMessage();
            if (lastMsg != null) {
                System.out.println("DefaultAgentFactory: Initializing unary agent with existing value");
                agent.callback(topicName, lastMsg);
            }
        }
    }
}
