package test;

import java.util.function.BinaryOperator;

import test.TopicManagerSingleton.TopicManager;

public class BinOpAgent implements Agent {

    private final String name;
    private final String in1, in2;
    private final Topic  out;
    private final BinaryOperator<Double> op;

    private double  v1, v2;
    private boolean has1, has2;

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
