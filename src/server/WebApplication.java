package server;

import servlets.SimpleConfLoader;
import servlets.TopicDisplayer;
import servlets.HtmlLoader;

/**
 * @file WebApplication.java
 * @brief Main entry point for the Agent Graph Manager web application
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This class serves as the main entry point for the Agent Graph Manager application.
 * It sets up the HTTP server with all necessary servlets and provides a web-based
 * interface for managing and monitoring agent networks.
 */
public class WebApplication {
    
    /**
     * @brief Main method that starts the Agent Graph Manager web application
     * @details This method initializes the HTTP server, configures all servlets,
     * and starts listening on port 8080. The application provides:
     * - Static file serving for the web interface
     * - Configuration upload and processing
     * - Real-time topic monitoring
     * - Message publishing capabilities
     * 
     * @param args Command line arguments (currently unused)
     * 
     * The server runs with the following endpoints:
     * - GET  / - Main application page
     * - GET  /index.html - Main application page  
     * - GET  /app/conf-loader - Configuration upload form
     * - POST /app/conf-loader - Configuration processing
     * - GET  /app/topics - Topic monitoring (returns HTML table)
     * - POST /app/topics - Message publishing
     */
    public static void main(String[] args) {
        System.out.println("Starting Web Application for Agent Graph Manager...");
        
        MyHTTPServer server = new MyHTTPServer(8080, 10);
        
        try {
            // Set up servlets
            System.out.println("Setting up servlets...");
            
            // HTML loader for static files
            HtmlLoader htmlLoader = new HtmlLoader("html_files");
            server.addServlet("GET", "/", htmlLoader);
            server.addServlet("GET", "/index.html", htmlLoader);
            
            // Configuration loader (simple version)
            SimpleConfLoader confLoader = new SimpleConfLoader();
            server.addServlet("GET", "/app/conf-loader", confLoader);
            server.addServlet("POST", "/app/conf-loader", confLoader);
            
            // Topic displayer for monitoring and message publishing
            TopicDisplayer topicDisplayer = new TopicDisplayer();
            server.addServlet("GET", "/app/topics", topicDisplayer);
            server.addServlet("POST", "/app/topics", topicDisplayer);
            
            System.out.println("Servlets configured:");
            System.out.println("  GET  /               -> HtmlLoader (main page)");
            System.out.println("  GET  /index.html     -> HtmlLoader");
            System.out.println("  GET  /app/conf-loader -> SimpleConfLoader (upload form)");
            System.out.println("  POST /app/conf-loader -> SimpleConfLoader (config processing)");
            System.out.println("  GET  /app/topics     -> TopicDisplayer (monitoring)");
            System.out.println("  POST /app/topics     -> TopicDisplayer (message publishing)");
            
            // Start the server
            System.out.println("\nStarting server on port 8080...");
            server.start();
            
            System.out.println("\n🚀 Server started successfully!");
            System.out.println("📋 Access the application at: http://localhost:8080");
            System.out.println("📊 Topic monitor at: http://localhost:8080/app/topics");
            System.out.println("📁 Config upload at: http://localhost:8080/app/conf-loader");
            System.out.println("\nPress Ctrl+C to stop the server.");
            
            // Add shutdown hook for graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\nShutting down server...");
                server.close();
                System.out.println("Server stopped.");
            }));
            
            // Keep the main thread alive
            server.join();
            
        } catch (Exception e) {
            System.err.println("Error starting server: " + e.getMessage());
            e.printStackTrace();
            server.close();
        }
    }
}
