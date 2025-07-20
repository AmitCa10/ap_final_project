package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;
import servlets.config.ConfigurationService;
import servlets.config.ConfigurationResult;
import servlets.config.ConfigurationException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @file SimpleConfLoader.java
 * @brief Servlet for configuration file uploads following SOLID principles
 * @author Advanced Programming Course
 * @date 2025
 * @version 2.0
 * 
 * This servlet has been refactored to follow SOLID principles:
 * - Single Responsibility: Only handles HTTP request/response logic
 * - Open-Closed: Easy to extend with new configuration formats
 * - Liskov Substitution: Uses interfaces for extensibility
 * - Interface Segregation: Small, focused interfaces
 * - Dependency Inversion: Depends on ConfigurationService abstraction
 * 
 * The heavy lifting is now done by specialized classes in the config package.
 */
public class SimpleConfLoader implements Servlet {
    
    /** @brief Configuration service that handles all configuration logic */
    private final ConfigurationService configurationService;
    
    /**
     * @brief Constructor
     */
    public SimpleConfLoader() {
        this.configurationService = new ConfigurationService();
    }
    
    @Override
    public void close() {
        // Cleanup if needed
    }
    
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        if (ri.getContent().length > 0) {
            handleConfigurationUpload(ri, toClient);
        } else {
            handleUploadForm(toClient);
        }
    }
    
    /**
     * @brief Handles POST requests for configuration file uploads
     * @param ri Request information
     * @param toClient Output stream to send response
     * @throws IOException if response writing fails
     */
    private void handleConfigurationUpload(RequestInfo ri, OutputStream toClient) throws IOException {
        try {
            System.out.println("SimpleConfLoader: Processing configuration upload");
            
            // Use the configuration service to load the configuration
            ConfigurationResult result = configurationService.loadConfigurationFromMultipart(ri.getContent());
            
            // Send success response
            sendSuccessResponse(toClient, result);
            
        } catch (ConfigurationException e) {
            System.err.println("SimpleConfLoader: Configuration error: " + e.getMessage());
            sendErrorResponse(toClient, e);
        } catch (Exception e) {
            System.err.println("SimpleConfLoader: Unexpected error: " + e.getMessage());
            e.printStackTrace();
            sendErrorResponse(toClient, new ConfigurationException("Unexpected error: " + e.getMessage(), e));
        }
    }
    
    /**
     * @brief Handles GET requests by returning the upload form
     * @param toClient Output stream to send response
     * @throws IOException if response writing fails
     */
    private void handleUploadForm(OutputStream toClient) throws IOException {
        String html = createUploadFormHtml();
        sendHtmlResponse(toClient, html, 200, "OK");
    }
    
    /**
     * @brief Sends a successful configuration loading response
     * @param toClient Output stream
     * @param result Configuration loading result
     * @throws IOException if writing fails
     */
    private void sendSuccessResponse(OutputStream toClient, ConfigurationResult result) throws IOException {
        String html = "<html><body style='background: #1a1a1a; color: #f0f0f0; font-family: Arial; padding: 40px;'>" +
                "<div style='background: rgba(177,156,217,0.1); padding: 30px; border-radius: 15px; text-align: center;'>" +
                "<h2 style='color: #87ceeb;'>✅ Configuration Loaded Successfully!</h2>" +
                "<div style='background: rgba(135,206,235,0.1); padding: 20px; border-radius: 10px; margin: 20px 0;'>" +
                "<h3 style='color: #c9a96e; margin-top: 0;'>Configuration Details:</h3>" +
                "<p style='color: #f0f0f0; margin: 5px 0;'><strong>Format:</strong> " + result.getFormat() + "</p>" +
                "<p style='color: #f0f0f0; margin: 5px 0;'><strong>Agents Created:</strong> " + result.getAgentCount() + "</p>" +
                "<p style='color: #f0f0f0; margin: 5px 0;'><strong>Topics Created:</strong> " + result.getTopicCount() + "</p>" +
                "</div>" +
                "<p style='color: #c9a96e; font-size: 1.1rem;'>Agent network is now active and ready for use.</p>" +
                "<div style='margin-top: 30px;'>" +
                "<a href='/' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block; margin: 5px;'>← Back to Main Page</a>" +
                "<a href='/app/topics' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block; margin: 5px;'>View Topics →</a>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlResponse(toClient, html, 200, "OK");
    }
    
    /**
     * @brief Sends an error response for configuration failures
     * @param toClient Output stream
     * @param exception The configuration exception that occurred
     * @throws IOException if writing fails
     */
    private void sendErrorResponse(OutputStream toClient, ConfigurationException exception) throws IOException {
        String errorHtml = "<html><body style='background: #1a1a1a; color: #f0f0f0; font-family: Arial; padding: 40px;'>" +
                "<div style='background: rgba(255,107,107,0.1); padding: 30px; border-radius: 15px; border-left: 4px solid #ff6b6b;'>" +
                "<h2 style='color: #ff6b6b;'>❌ Configuration Error</h2>" +
                "<div style='background: rgba(0,0,0,0.3); padding: 15px; border-radius: 5px; margin: 15px 0;'>" +
                "<pre style='color: #f0f0f0; font-family: monospace; margin: 0; white-space: pre-wrap;'>" +
                escapeHtml(exception.getMessage()) + "</pre>" +
                "</div>" +
                "<div style='margin-top: 20px;'>" +
                "<a href='/app/conf-loader' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block; margin: 5px;'>← Try Again</a>" +
                "<a href='/' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block; margin: 5px;'>Back to Main Page</a>" +
                "</div>" +
                "</div>" +
                "</body></html>";
        
        sendHtmlResponse(toClient, errorHtml, 500, "Internal Server Error");
    }
    
    /**
     * @brief Creates the HTML for the configuration upload form
     * @return HTML string for the upload form
     */
    private String createUploadFormHtml() {
        return "<html><head><title>Upload Configuration</title></head>" +
                "<body style='font-family: Arial; background: #2d2d2d; color: #f0f0f0; padding: 40px;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background: rgba(177,156,217,0.1); padding: 30px; border-radius: 15px;'>" +
                "<h2 style='color: #b19cd9; text-align: center; margin-top: 0;'>Configuration Upload</h2>" +
                "<p style='color: #c9a96e; text-align: center; margin-bottom: 30px;'>Upload JSON or CONF format configuration files</p>" +
                "<form method='post' enctype='multipart/form-data' style='text-align: center;'>" +
                "<div style='margin: 30px 0;'>" +
                "<input type='file' name='configFile' accept='.json,.conf' " +
                "style='padding: 15px; background: rgba(255,255,255,0.1); border: 2px solid #b19cd9; border-radius: 10px; color: #f0f0f0; font-size: 1rem; width: 100%; max-width: 400px;'><br><br>" +
                "<input type='submit' value='Upload Configuration' " +
                "style='background: linear-gradient(45deg, #b19cd9, #c9a96e); color: white; border: none; padding: 15px 40px; border-radius: 25px; font-size: 1.1rem; cursor: pointer; transition: all 0.3s ease;' " +
                "onmouseover='this.style.transform=\"scale(1.05)\"' onmouseout='this.style.transform=\"scale(1)\"'>" +
                "</div>" +
                "</form>" +
                "<div style='text-align: center; margin-top: 30px;'>" +
                "<a href='/' style='color: #87ceeb; text-decoration: none; padding: 12px 25px; background: rgba(135,206,235,0.2); border-radius: 8px; display: inline-block;'>← Back to Main Page</a>" +
                "</div>" +
                "<div style='margin-top: 30px; padding: 20px; background: rgba(135,206,235,0.1); border-radius: 10px;'>" +
                "<h3 style='color: #87ceeb; margin-top: 0;'>Supported Formats:</h3>" +
                "<ul style='color: #f0f0f0; text-align: left;'>" +
                "<li><strong>JSON:</strong> {\"agents\": [{\"type\": \"PlusAgent\", \"subscriptions\": [\"X\", \"Y\"], \"publications\": [\"Sum\"]}]}</li>" +
                "<li><strong>CONF:</strong> Line-based format with agent class, subscriptions, and publications on separate lines</li>" +
                "</ul>" +
                "</div>" +
                "</div>" +
                "</body></html>";
    }
    
    /**
     * @brief Sends an HTML response
     * @param toClient Output stream
     * @param html HTML content
     * @param statusCode HTTP status code
     * @param statusText HTTP status text
     * @throws IOException if writing fails
     */
    private void sendHtmlResponse(OutputStream toClient, String html, int statusCode, String statusText) throws IOException {
        byte[] response = ("HTTP/1.1 " + statusCode + " " + statusText + "\r\n" +
                "Content-Type: text/html; charset=utf-8\r\n" +
                "Content-Length: " + html.getBytes("UTF-8").length + "\r\n" +
                "\r\n" + html).getBytes("UTF-8");
        toClient.write(response);
    }
    
    /**
     * @brief Escapes HTML special characters
     * @param text Text to escape
     * @return Escaped text
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}
