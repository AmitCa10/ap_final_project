package configs;

// import graph.Agent;
// import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @file Graph.java
 * @brief Graph representation of the topic-agent communication system with cycle detection
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * Graph extends ArrayList<Node> to represent the communication topology between
 * topics and agents in the system. It can construct itself from the current
 * TopicManager state and detect cycles that could lead to infinite message
 * loops or deadlocks in the agent communication system.
 */
public class Graph extends ArrayList<Node> {

    /**
     * @brief Checks if the graph contains any cycles
     * @return true if any cycles are detected in the graph, false otherwise
     * 
     * Iterates through all nodes in the graph and uses each node's individual
     * cycle detection capability to determine if there are any strongly
     * connected components with cycles. This is crucial for validating
     * that the agent communication topology is acyclic.
     */
    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) return true;
        }
        return false;
    }

    /**
     * @brief Constructs the graph from the current state of the TopicManager
     * 
     * This method builds a directed graph representation where:
     * - Topic nodes are prefixed with "T" (e.g., "Ttopic_name")
     * - Agent nodes are prefixed with "A" (e.g., "Aagent_name")
     * - Edges represent data flow: topic → subscriber agents, publisher agents → topic
     * 
     * The resulting graph can be used for topology analysis, cycle detection,
     * and visualization of the message flow patterns in the system.
     */
    public void createFromTopics() {
        clear();
        Map<String,Node> map = new HashMap<>();
        TopicManager tm = TopicManagerSingleton.get();

        /**
         * @brief Helper function to get or create a node by name
         * Uses computeIfAbsent to efficiently manage node creation and retrieval
         */
        java.util.function.Function<String,Node> get =
            nm -> map.computeIfAbsent(nm, Node::new);

        /* Topics: T<name>  |  Agents: A<name> */
        tm.getTopics().forEach(t -> {
            Node topicNode = get.apply("T" + t.name);
            /* edges: topic → subscriber agents */
            t.getSubs().forEach(ag -> {
                Node agentNode = get.apply("A" + ag.getName());
                topicNode.addEdge(agentNode);
            });
            
            /* edges: publisher agents → topic */
            t.getPubs().forEach(ag -> {
                Node agentNode = get.apply("A" + ag.getName());
                agentNode.addEdge(topicNode);
            });
        });

        addAll(map.values());
    }
}
