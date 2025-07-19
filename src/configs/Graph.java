package configs;

import graph.Agent;
import graph.Topic;
import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph extends ArrayList<Node> {

    public boolean hasCycles() {
        for (Node n : this) {
            if (n.hasCycles()) return true;
        }
        return false;
    }

    public void createFromTopics() {
        clear();
        Map<String,Node> map = new HashMap<>();
        TopicManager tm = TopicManagerSingleton.get();

        /* helper – get (or create) a node by name */
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
