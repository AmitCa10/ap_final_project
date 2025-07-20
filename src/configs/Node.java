package configs;

import graph.Message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @file Node.java
 * @brief Graph node representation with cycle detection capabilities
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * Node represents a vertex in a directed graph structure with support for
 * message storage and cycle detection. Each node maintains a list of outgoing
 * edges and can detect if it participates in a strongly connected component
 * with cycles using depth-first search algorithms.
 */
public class Node {
    /** @brief Name identifier for this node */
    private String     name;
    
    /** @brief List of outgoing edges to other nodes */
    private List<Node> edges = new ArrayList<>();
    
    /** @brief Optional message associated with this node */
    private Message    message = null;

    /**
     * @brief Constructor for creating a named node
     * @param name The string identifier for this node
     */
    public Node(String name) { this.name = name; }

    /**
     * @brief Gets the name of this node
     * @return The string name identifier
     */
    public String  getName()           { return name; }
    
    /**
     * @brief Sets the name of this node
     * @param n The new string name identifier
     */
    public void    setName(String n)   { name = n; }
    
    /**
     * @brief Gets the message associated with this node
     * @return The Message object, or null if no message is set
     */
    public Message getMessage()        { return message; }
    
    /**
     * @brief Sets the message associated with this node
     * @param m The Message object to associate with this node
     */
    public void    setMessage(Message m){ message = m; }
    
    /**
     * @brief Gets the list of outgoing edges from this node
     * @return List of Node objects representing connected destinations
     */
    public List<Node> getEdges()       { return edges; }

    /**
     * @brief Adds a directed edge from this node to the destination node
     * @param dst The destination node to connect to
     * 
     * Creates a directed edge if it doesn't already exist. Duplicate edges
     * to the same destination are prevented by checking containment.
     */
    public void addEdge(Node dst) {
        if (!edges.contains(dst)) edges.add(dst);
    }

    /**
     * @brief Detects if this node participates in any cycles within its strongly connected component
     * @return true if cycles are detected, false otherwise
     * 
     * Uses depth-first search with a recursion stack to detect back edges
     * that indicate the presence of cycles. This is particularly useful
     * for validating directed acyclic graph (DAG) properties.
     */
    public boolean hasCycles() {
        return dfs(this, new HashSet<>(), new HashSet<>());
    }
    
    /**
     * @brief Internal depth-first search method for cycle detection
     * @param cur Current node being visited in the DFS traversal
     * @param seen Set of nodes that have been completely processed
     * @param stack Set of nodes in the current recursion stack (path)
     * @return true if a back edge (cycle) is detected, false otherwise
     * 
     * This private helper implements the classic DFS-based cycle detection
     * algorithm using three colors: white (unvisited), gray (in stack),
     * and black (completely processed).
     */
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
