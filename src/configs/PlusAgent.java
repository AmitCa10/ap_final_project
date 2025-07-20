package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

/**
 * @file PlusAgent.java
 * @brief REACTIVE addition agent that immediately recalculates on input changes
 * @author Advanced Programming Course
 * @date 2025
 * @version 2.0
 * 
 * PlusAgent implements a REACTIVE binary operation agent that subscribes to exactly 
 * two input topics and publishes their sum to an output topic. Unlike batch-mode 
 * agents, this agent maintains persistent state and immediately recalculates the 
 * sum whenever either input value changes, enabling real-time mathematical operations.
 * 
 * Key Features:
 * - Reactive updates: Any change to X or Y immediately triggers sum recalculation
 * - Persistent state: Values are maintained between calculations
 * - Change detection: Only publishes when input values actually change
 * - Real-time operation: No waiting for paired inputs in subsequent calculations
 */
public class PlusAgent implements Agent {

    /** @brief Array of subscription topic names (expected to have exactly 2 elements) */
    private final String[] subs;
    
    /** @brief Output topic where the sum will be published */
    private final Topic    out;
    
    /** @brief Reference to the TopicManager for topic operations */
    private final TopicManager tm;

    /** @brief Current values from the two input topics */
    private double  x, y;
    
    /** @brief Flags indicating whether values have been received from each topic */
    private boolean hasX, hasY;

    /**
     * @brief Constructor for creating an addition agent with input synchronization
     * @param subs Array of subscription topic names (must have at least 2 elements)
     * @param pubs Array of publication topic names (must have exactly 1 element)
     * @throws IllegalArgumentException if subs has less than 2 elements or pubs is empty
     * 
     * Creates a new addition agent that subscribes to two input topics and
     * publishes results to one output topic. The agent synchronizes inputs
     * from both topics before computing the addition operation.
     */
    public PlusAgent(String[] subs, String[] pubs) {
        if (subs.length < 2 || pubs.length == 0)
            throw new IllegalArgumentException("PlusAgent needs 2 subs & 1 pub");

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
     * @return A string containing "PlusAgent"
     */
    @Override public String getName() { return "PlusAgent"; }

    /**
     * @brief Resets the agent to initial state (primarily for testing/restart scenarios)
     * 
     * In REACTIVE mode, reset is rarely needed during normal operation since the agent
     * maintains persistent state. This method is mainly useful for testing scenarios
     * or when explicitly restarting the computation pipeline.
     */
    @Override public void reset() {
        x = y = 0;
        hasX = hasY = false;
        System.out.println("PlusAgent: RESET - Cleared all values and flags");
    }

    /**
     * @brief Callback method invoked when a subscribed topic receives a message
     * @param topic The name of the topic that received the message
     * @param msg The message containing the numeric value for the addition
     * 
     * This method implements REACTIVE MODE: any change to either input immediately
     * triggers a new sum calculation if both values are available. Unlike batch mode,
     * this agent maintains persistent state and recalculates on every input change.
     */
    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        System.out.println("PlusAgent: Received " + v + " from topic " + topic);
        
        if (Double.isNaN(v)) return;

        // Update the appropriate input value
        boolean valueChanged = false;
        if (topic.equals(subs[0])) {
            if (!hasX || x != v) {
                x = v;
                hasX = true;
                valueChanged = true;
                System.out.println("PlusAgent: Updated X = " + x);
            }
        }
        else if (topic.equals(subs[1])) {
            if (!hasY || y != v) {
                y = v;
                hasY = true;
                valueChanged = true;
                System.out.println("PlusAgent: Updated Y = " + y);
            }
        }

        // REACTIVE CALCULATION: Compute sum whenever we have both values AND something changed
        if (hasX && hasY && valueChanged) {
            double sum = x + y;
            System.out.println("PlusAgent: REACTIVE CALC: " + x + " + " + y + " = " + sum);
            out.publish(new Message(sum));
            System.out.println("PlusAgent: Published updated sum = " + sum);
        } else if (!hasX || !hasY) {
            System.out.println("PlusAgent: Waiting for both inputs (hasX=" + hasX + ", hasY=" + hasY + ")");
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
