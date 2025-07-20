package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * @file MulAgent.java
 * @brief Agent implementation that performs multiplication of two input values
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * MulAgent is a specialized agent that subscribes to exactly two input topics,
 * waits for numeric values from both, and publishes their product to an output topic.
 * This agent demonstrates the synchronization pattern where computation only occurs
 * after receiving input from all required sources.
 */
public class MulAgent implements Agent {

    /** @brief Array of subscription topic names (expected to have exactly 2 elements) */
    private final String[] subs;
    
    /** @brief Output topic where the product will be published */
    private final Topic    out;
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager tm;

    /** @brief Current values from the two input topics */
    private double  x, y;
    
    /** @brief Flags indicating whether values have been received from each input */
    private boolean hasX, hasY;

    /**
     * @brief Constructor for creating a multiplication agent
     * @param subs Array of subscription topic names (must have exactly 2 elements)
     * @param pubs Array of publication topic names (must have exactly 1 element)
     * @throws IllegalArgumentException if subs doesn't have 2 elements or pubs is empty
     * 
     * Creates a new multiplication agent that subscribes to the specified input topics
     * and will publish results to the output topic. The agent automatically registers
     * itself as a subscriber to the inputs and publisher to the output.
     */
    public MulAgent(String[] subs, String[] pubs) {
        if (subs.length < 2 || pubs.length == 0)
            throw new IllegalArgumentException("MulAgent needs 2 subs & 1 pub");

        this.subs = subs;
        tm  = TopicManagerSingleton.get();

        tm.getTopic(subs[0]).subscribe(this);
        tm.getTopic(subs[1]).subscribe(this);

        out = tm.getTopic(pubs[0]);
        out.addPublisher(this);

        reset();
    }

    /**
     * @brief Returns the name of this agent type
     * @return A string containing "MulAgent"
     */
    @Override public String getName() { return "MulAgent"; }

    /**
     * @brief Resets the agent's internal state to initial conditions
     * 
     * Clears both input values and resets the flags indicating whether
     * values have been received. This prepares the agent for a new
     * computation cycle.
     */
    @Override public void reset() {
        x = y = 0;
        hasX = hasY = false;
    }

    /**
     * @brief Callback method invoked when a subscribed topic receives a message
     * @param topic The name of the topic that received the message
     * @param msg The message containing the numeric value
     * 
     * This method processes incoming messages by storing the numeric value
     * and checking if both inputs are now available. If both inputs have
     * been received, it computes their product and publishes the result.
     * The agent then resets itself for the next computation cycle.
     */
    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (Double.isNaN(v)) return;

        if (topic.equals(subs[0])) { x = v; hasX = true; }
        else if (topic.equals(subs[1])) { y = v; hasY = true; }

        if (hasX && hasY) {
            out.publish(new Message(x * y));
            reset();
        }
    }

    /**
     * @brief Cleans up the agent by unsubscribing from topics and removing publisher status
     * 
     * This method unregisters the agent from all subscribed topics and removes
     * its publisher status from the output topic. This prevents memory leaks
     * and ensures proper cleanup when the agent is no longer needed.
     */
    @Override
    public void close() {
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(subs[1]).unsubscribe(this);
        out.removePublisher(this);
    }
}
