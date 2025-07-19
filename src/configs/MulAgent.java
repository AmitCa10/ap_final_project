package configs;

import graph.Agent;
import graph.Message;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;

public class MulAgent implements Agent {

    private final String[] subs;
    private final Topic    out;
    private final TopicManager tm;

    private double  x, y;
    private boolean hasX, hasY;

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

    @Override public String getName() { return "MulAgent"; }

    @Override public void reset() {
        x = y = 0;
        hasX = hasY = false;
    }

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

    @Override
    public void close() {
        tm.getTopic(subs[0]).unsubscribe(this);
        tm.getTopic(subs[1]).unsubscribe(this);
        out.removePublisher(this);
    }
}
