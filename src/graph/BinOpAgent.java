package graph;

import java.util.function.BinaryOperator;

import graph.TopicManagerSingleton.TopicManager;

/**
 * @file BinOpAgent.java
 * @brief Agent that performs binary operations on messages from two input topics
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * BinOpAgent is a specialized agent that subscribes to two input topics, waits for
 * messages from both, and then applies a binary operation (such as addition,
 * subtraction, multiplication, etc.) to the received values. The result is published
 * to an output topic. This agent implements synchronization logic to ensure both
 * inputs are available before computing the result.
 */
public class BinOpAgent implements Agent {

    /** @brief The unique name of this agent */
    private final String name;
    
    /** @brief Name of the first input topic */
    private final String in1, in2;
    
    /** @brief The output topic where results are published */
    private final Topic  out;
    
    /** @brief The binary operation to perform on the input values */
    private final BinaryOperator<Double> op;

    /** @brief Current values from the input topics */
    private double  v1, v2;
    
    /** @brief Flags indicating whether values have been received from each input */
    private boolean has1, has2;

    /**
     * @brief Constructor for creating a binary operation agent
     * @param name Unique identifier for this agent
     * @param input1 Name of the first input topic to subscribe to
     * @param input2 Name of the second input topic to subscribe to
     * @param output Name of the output topic to publish results to
     * @param op Binary operation to apply (e.g., Double::sum, (a,b) -> a-b)
     * 
     * Creates a new binary operation agent that subscribes to the specified
     * input topics and will publish results to the output topic. The agent
     * automatically registers itself as a subscriber and publisher.
     */
    public BinOpAgent(String name,
                      String input1, String input2,
                      String output,
                      BinaryOperator<Double> op) {

        this.name = name;
        this.in1  = input1;
        this.in2  = input2;
        this.op   = op;

        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(input1).subscribe(this);
        tm.getTopic(input2).subscribe(this);

        out = tm.getTopic(output);
        out.addPublisher(this);

        reset();                       // start from a clean slate
    }

    @Override public String getName() { return name; }

    @Override
    public void reset() {
        has1 = has2 = false;
        v1 = v2 = 0;
    }

    @Override
    public void callback(String topic, Message msg) {
        double val = msg.asDouble;
        if (Double.isNaN(val)) return;          // ignore bad data

        if (topic.equals(in1)) { v1 = val; has1 = true; }
        else if (topic.equals(in2)) { v2 = val; has2 = true; }

        if (has1 && has2) {                     // both inputs ready
            out.publish(new Message(op.apply(v1, v2)));
            reset();                            // wait for two fresh inputs
        }
    }

    @Override
    public void close() {
        TopicManager tm = TopicManagerSingleton.get();
        tm.getTopic(in1).unsubscribe(this);
        tm.getTopic(in2).unsubscribe(this);
        out.removePublisher(this);
    }
}
