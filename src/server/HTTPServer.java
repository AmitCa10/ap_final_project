package server;

import servlet.Servlet;

/**
 * @file HTTPServer.java
 * @brief Interface defining the contract for HTTP server implementations
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This interface defines the essential operations that any HTTP server implementation
 * must provide. It extends Runnable to support running the server in a separate thread
 * and provides methods for servlet registration and lifecycle management.
 */
public interface HTTPServer extends Runnable{
    
    /**
     * @brief Registers a servlet to handle requests for a specific HTTP method and URI
     * @param httpCommand The HTTP method (GET, POST, DELETE, etc.)
     * @param uri The URI pattern to map to the servlet
     * @param s The servlet instance to handle matching requests
     */
    public void addServlet(String httpCommand, String uri, Servlet s);
    
    /**
     * @brief Removes a servlet mapping for a specific HTTP method and URI
     * @param httpCommand The HTTP method
     * @param uri The URI pattern to unmap
     */
    public void removeServlet(String httpCommand, String uri);
    
    /**
     * @brief Starts the HTTP server and begins listening for connections
     */
    public void start();
    
    /**
     * @brief Stops the HTTP server and releases all resources
     */
    public void close();
}
