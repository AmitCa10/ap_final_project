package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * @file SubAgent.java
 * @brief Agent implementation for performing subtraction operations with input synchronization
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * SubAgent is a binary operation agent that computes the difference between
 * two input values from separate topics. It maintains internal state to
 * synchronize inputs from two topics and performs subtraction (x - y)
 * only when both values are available.
 */
public class SubAgent implements Agent {

    /** @brief Array of subscription topic names */
    private final String[] subs;
    
    /** @brief Output topic for publishing subtraction results */
    private final Topic    out;
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager tm;

    /** @brief First operand value from subs[0] */
    private double  x;
    
    /** @brief Second operand value from subs[1] */
    private double  y;
    
    /** @brief Flag indicating if first operand has been received */
    private boolean hasX;
    
    /** @brief Flag indicating if second operand has been received */
    private boolean hasY;

    /**
     * @brief Constructor for creating a subtraction agent with input synchronization
     * @param subs Array of subscription topic names (must have at least 2 elements)
     * @param pubs Array of publication topic names (must have exactly 1 element)
     * @throws IllegalArgumentException if subs has less than 2 elements or pubs is empty
     * 
     * Creates a new subtraction agent that subscribes to two input topics and
     * publishes results to one output topic. The agent synchronizes inputs
     * from both topics before computing the subtraction operation.
     */
    public SubAgent(String[] subs, String[] pubs) {
        if (subs.length < 2 || pubs.length == 0)
            throw new IllegalArgumentException("SubAgent needs 2 subs & 1 pub");

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
     * @return A string containing "SubAgent"
     */
    @Override public String getName() { return "SubAgent"; }

    /**
     * @brief Resets the agent's internal state and synchronization flags
     * 
     * Clears both operand values to zero and resets the availability flags
     * to false, preparing the agent for the next pair of input values.
     */
    @Override public void reset() {
        x = y = 0;
        hasX = hasY = false;
    }

    /**
     * @brief Callback method invoked when a subscribed topic receives a message
     * @param topic The name of the topic that received the message
     * @param msg The message containing the numeric value for the operation
     * 
     * This method handles input synchronization by storing values from each
     * topic separately. When both operands are available, it computes the
     * subtraction (x - y) and publishes the result, then resets for the next
     * pair of inputs. Invalid numeric values (NaN) are ignored.
     */
    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (Double.isNaN(v)) return;

        // Update the corresponding operand
        if (topic.equals(subs[0])) { 
            x = v; 
            hasX = true; 
        }
        else if (topic.equals(subs[1])) { 
            y = v; 
            hasY = true; 
        }

        // REACTIVE: Calculate whenever we have both values
        if (hasX && hasY) {
            double result = x - y;
            out.publish(new Message(result));
            // NO RESET - maintain state for continuous updates
        }
    }

    /**
     * @brief Cleans up the agent by unsubscribing from all topics and removing publisher status
     * 
     * This method unregisters the agent from both subscribed topics and removes
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
