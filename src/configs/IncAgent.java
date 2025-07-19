package test;

import test.TopicManagerSingleton.TopicManager;

public class IncAgent implements Agent {

    private final String sub;
    private final Topic  out;
    private final TopicManager tm;

    public IncAgent(String[] subs, String[] pubs) {
        if (subs.length == 0 || pubs.length == 0)
            throw new IllegalArgumentException("IncAgent needs 1 sub & 1 pub");

        sub = subs[0];
        tm  = TopicManagerSingleton.get();

        tm.getTopic(sub).subscribe(this);

        out = tm.getTopic(pubs[0]);
        out.addPublisher(this);
    }

    @Override public String getName() { return "IncAgent"; }
    @Override public void reset()    {}

    @Override
    public void callback(String topic, Message msg) {
        double v = msg.asDouble;
        if (!Double.isNaN(v))
            out.publish(new Message(v + 1));
    }

    @Override
    public void close() {
        tm.getTopic(sub).unsubscribe(this);
        out.removePublisher(this);
    }
}