package test;

import java.util.List;
import java.util.ArrayList;

public class Topic {
    public final String name;
    List<Agent> subs;
    List<Agent> pubs;

    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
    }

    public void subscribe(Agent a) {subs.add(a);}

    public void unsubscribe(Agent a) {subs.remove(a);}

    public void publish(Message m) {
        for (Agent ag : subs) {
            ag.callback(name, m);
        }
    }

    public void addPublisher(Agent a) {pubs.add(a);}

    public void removePublisher(Agent a) {pubs.remove(a);}
}
