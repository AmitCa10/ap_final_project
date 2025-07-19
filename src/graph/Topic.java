package graph;

import java.util.List;
import java.util.ArrayList;

public class Topic {
    public final String name;
    List<Agent> subs;
    List<Agent> pubs;
    private Message lastMessage;

    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = new Message("0.0");
    }

    public void subscribe(Agent a) {subs.add(a);}

    public void unsubscribe(Agent a) {subs.remove(a);}

    public void publish(Message m) {
        for (Agent ag : subs) {
            ag.callback(name, m);
        }
        this.lastMessage = m;
    }

    public void addPublisher(Agent a) {pubs.add(a);}

    public void removePublisher(Agent a) {pubs.remove(a);}
    
    public List<Agent> getSubs() {return subs;}
    
    public List<Agent> getPubs() {return pubs;}
    
    public Message getLastMessage() {return lastMessage;}
}
