package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Node {
    private String     name;
    private List<Node> edges = new ArrayList<>();
    private Message    message = null;

    public Node(String name) { this.name = name; }

    /* getters / setters */
    public String  getName()           { return name; }
    public void    setName(String n)   { name = n; }
    public Message getMessage()        { return message; }
    public void    setMessage(Message m){ message = m; }
    public List<Node> getEdges()       { return edges; }

    /* graph helpers */
    public void addEdge(Node dst) {
        if (!edges.contains(dst)) edges.add(dst);
    }

    /* detects a cycle in this node’s SCC */
    public boolean hasCycles() {
        return dfs(this, new HashSet<>(), new HashSet<>());
    }
    private boolean dfs(Node cur, Set<Node> seen, Set<Node> stack) {
        if (stack.contains(cur)) return true;
        if (seen.contains(cur))  return false;
        seen.add(cur); stack.add(cur);
        for (Node nxt : cur.edges)
            if (dfs(nxt, seen, stack)) return true;
        stack.remove(cur);
        return false;
    }
}
