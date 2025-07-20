package graph;

import java.util.List;
import java.util.ArrayList;

/**
 * @file Topic.java
 * @brief Represents a communication topic in the agent graph system
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * A Topic is a named communication channel through which agents can publish
 * and subscribe to messages. Topics implement a publish-subscribe pattern
 * where multiple agents can subscribe to receive messages, and any agent
 * can publish messages to notify all subscribers.
 */
public class Topic {
    
    /** @brief The unique name identifier for this topic */
    public final String name;
    
    /** @brief List of agents subscribed to this topic */
    List<Agent> subs;
    
    /** @brief List of agents that can publish to this topic */
    List<Agent> pubs;
    
    /** @brief The most recently published message on this topic */
    private Message lastMessage;

    /**
     * @brief Constructor for creating a new topic
     * @param name The unique name for this topic
     * 
     * Creates a new topic with the specified name and initializes it with
     * a default message value of "0.0". Empty subscriber and publisher lists
     * are also created.
     */
    Topic(String name) {
        this.name = name;
        this.subs = new ArrayList<>();
        this.pubs = new ArrayList<>();
        this.lastMessage = new Message("0.0");
    }

    /**
     * @brief Subscribes an agent to receive messages from this topic
     * @param a The agent to subscribe to this topic
     * 
     * When a message is published to this topic, the subscribed agent's
     * callback method will be invoked with the message content.
     */
    public void subscribe(Agent a) {subs.add(a);}

    /**
     * @brief Unsubscribes an agent from this topic
     * @param a The agent to unsubscribe from this topic
     * 
     * The agent will no longer receive notifications when messages are
     * published to this topic.
     */
    public void unsubscribe(Agent a) {subs.remove(a);}

    /**
     * @brief Publishes a message to all subscribers of this topic
     * @param m The message to publish
     * 
     * Notifies all subscribed agents by calling their callback method
     * with this topic's name and the message. Also updates the topic's
     * last message for monitoring purposes.
     */
    public void publish(Message m) {
        for (Agent ag : subs) {
            ag.callback(name, m);
        }
        this.lastMessage = m;
    }

    /**
     * @brief Adds an agent as a publisher for this topic
     * @param a The agent to add as a publisher
     * 
     * Registers an agent as being capable of publishing messages to this topic.
     * This is primarily used for tracking and visualization purposes.
     */
    public void addPublisher(Agent a) {pubs.add(a);}

    public void removePublisher(Agent a) {pubs.remove(a);}
    
    public List<Agent> getSubs() {return subs;}
    
    public List<Agent> getPubs() {return pubs;}
    
    public Message getLastMessage() {return lastMessage;}
}
