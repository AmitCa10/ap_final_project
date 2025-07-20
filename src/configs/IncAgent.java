package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * @file IncAgent.java
 * @brief Agent implementation that increments input values by 1
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * IncAgent is a simple unary operation agent that subscribes to one input topic,
 * increments each received numeric value by 1, and publishes the result to an
 * output topic. This agent demonstrates immediate processing without state
 * accumulation between messages.
 */
public class IncAgent implements Agent {

    /** @brief Name of the subscription topic */
    private final String sub;
    
    /** @brief Output topic where incremented values are published */
    private final Topic  out;
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager tm;

    /**
     * @brief Constructor for creating an increment agent
     * @param subs Array of subscription topic names (must have exactly 1 element)
     * @param pubs Array of publication topic names (must have exactly 1 element)
     * @throws IllegalArgumentException if subs or pubs arrays are empty
     * 
     * Creates a new increment agent that subscribes to the specified input topic
     * and will publish incremented results to the output topic. The agent
     * automatically registers itself as a subscriber and publisher.
     */
    public IncAgent(String[] subs, String[] pubs) {
        if (subs.length == 0 || pubs.length == 0)
            throw new IllegalArgumentException("IncAgent needs 1 sub & 1 pub");

        sub = subs[0];
        tm  = TopicManagerSingleton.get();

        tm.getTopic(sub).subscribe(this);

        out = tm.getTopic(pubs[0]);
        out.addPublisher(this);
    }

    /**
     * @brief Returns the name of this agent type
     * @return A string containing "IncAgent"
     */
    @Override public String getName() { return "IncAgent"; }
    
    /**
     * @brief Resets the agent's internal state (no-op for this stateless agent)
     * 
     * IncAgent doesn't maintain internal state between messages, so this
     * method is empty. Each message is processed independently.
     */
    @Override public void reset()    {}

    /**
     * @brief Callback method invoked when the subscribed topic receives a message
     * @param topic The name of the topic that received the message
     * @param msg The message containing the numeric value to increment
     * 
     * This method processes incoming messages by extracting the numeric value,
     * incrementing it by 1, and immediately publishing the result. Invalid
     * numeric values (NaN) are ignored.
     */
    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (!Double.isNaN(v))
            out.publish(new Message(v + 1));
    }

    /**
     * @brief Cleans up the agent by unsubscribing from topics and removing publisher status
     * 
     * This method unregisters the agent from the subscribed topic and removes
     * its publisher status from the output topic. This prevents memory leaks
     * and ensures proper cleanup when the agent is no longer needed.
     */
    @Override
    public void close() {
        tm.getTopic(sub).unsubscribe(this);
        out.removePublisher(this);
    }
}