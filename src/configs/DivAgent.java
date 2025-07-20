package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * @file DivAgent.java
 * @brief Agent implementation that performs division of two input values
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * DivAgent is a specialized agent that subscribes to exactly two input topics,
 * waits for numeric values from both, and publishes their quotient to an output topic.
 * This agent includes division by zero protection, returning NaN when the divisor is zero.
 */
public class DivAgent implements Agent {

    /** @brief Array of subscription topic names (expected to have exactly 2 elements) */
    private final String[] subs;
    
    /** @brief Output topic where the quotient will be published */
    private final Topic    out;
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager tm;

    /** @brief Current values from the two input topics (x is dividend, y is divisor) */
    private double  x, y;
    
    /** @brief Flags indicating whether values have been received from each input */
    private boolean hasX, hasY;

    /**
     * @brief Constructor for creating a division agent
     * @param subs Array of subscription topic names (must have exactly 2 elements)
     * @param pubs Array of publication topic names (must have exactly 1 element)
     * @throws IllegalArgumentException if subs doesn't have 2 elements or pubs is empty
     * 
     * Creates a new division agent that subscribes to the specified input topics
     * and will publish results to the output topic. The agent automatically registers
     * itself as a subscriber to the inputs and publisher to the output.
     */
    public DivAgent(String[] subs, String[] pubs) {
        if (subs.length < 2 || pubs.length == 0)
            throw new IllegalArgumentException("DivAgent needs 2 subs & 1 pub");

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
     * @return A string containing "DivAgent"
     */
    @Override public String getName() { return "DivAgent"; }

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
     * been received, it computes their quotient with division-by-zero protection
     * and publishes the result. The agent then resets itself for the next cycle.
     */
    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (Double.isNaN(v)) return;

        if (topic.equals(subs[0])) { x = v; hasX = true; }
        else if (topic.equals(subs[1])) { y = v; hasY = true; }

        if (hasX && hasY) {
            if (y != 0) {
                out.publish(new Message(x / y));
            } else {
                out.publish(new Message(Double.NaN));
            }
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
