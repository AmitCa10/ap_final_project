package servlet;

import java.io.IOException;
import java.io.OutputStream;
import server.RequestParser.RequestInfo;

/**
 * @file Servlet.java
 * @brief Interface defining the contract for HTTP request handlers
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This interface defines the contract that all servlet implementations must follow.
 * Servlets are responsible for handling HTTP requests and generating appropriate
 * responses. They are registered with the HTTP server and mapped to specific URIs.
 */
public interface Servlet {
    
    /**
     * @brief Handles an HTTP request and generates a response
     * @param ri The RequestInfo object containing parsed HTTP request details
     * @param toClient The OutputStream to write the HTTP response to
     * @throws IOException if there's an error writing to the output stream
     * 
     * This method is called by the HTTP server when a request matches the servlet's
     * registered URI pattern. The servlet should parse the request, perform any
     * necessary processing, and write a complete HTTP response to the output stream.
     */
    void handle(RequestInfo ri, OutputStream toClient) throws IOException;
    
    /**
     * @brief Closes the servlet and releases any resources it may be holding
     * @throws IOException if there's an error during cleanup
     * 
     * This method is called when the servlet is being unregistered or when the
     * server is shutting down. Implementations should close any open files,
     * database connections, or other resources.
     */
    void close() throws IOException;
}
