package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import test.TopicManagerSingleton.TopicManager;

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
            t.subs.forEach(ag -> {
                Node agentNode = get.apply("A" + ag.getName());
                topicNode.addEdge(agentNode);
            });
            
            /* edges: publisher agents → topic */
            t.pubs.forEach(ag -> {
                Node agentNode = get.apply("A" + ag.getName());
                agentNode.addEdge(topicNode);
            });
        });

        addAll(map.values());
    }
}
