package graph;

/**
 * @file Agent.java
 * @brief Interface defining the contract for all agents in the graph system
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This interface defines the core contract that all agents must implement.
 * Agents are autonomous entities that can subscribe to topics, receive messages,
 * process data, and publish results to other topics. They form the computational
 * nodes in the agent graph system.
 */
public interface Agent {
    
    /**
     * @brief Returns the unique name of this agent
     * @return A string containing the agent's name
     * 
     * Each agent should have a unique name that identifies it within the system.
     * This name is used for debugging, logging, and system administration.
     */
    String getName();
    
    /**
     * @brief Resets the agent to its initial state
     * 
     * This method should restore the agent to its initial configuration,
     * clearing any accumulated state or cached data. This is useful for
     * restarting computations or handling error recovery scenarios.
     */
    void reset();
    
    /**
     * @brief Callback method invoked when a subscribed topic receives a message
     * @param topic The name of the topic that received the message
     * @param msg The message that was published to the topic
     * 
     * This method is called by the topic system when a message is published
     * to a topic that this agent subscribes to. The agent should process
     * the message and potentially publish results to other topics.
     */
    void callback(String topic, Message msg);
    
    /**
     * @brief Closes the agent and releases any resources it may be holding
     * 
     * This method should clean up any resources, stop any running threads,
     * and prepare the agent for shutdown. It's called when the agent is
     * being removed from the system or when the application is terminating.
     */
    void close();
}
