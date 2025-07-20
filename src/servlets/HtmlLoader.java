package servlets;

import servlet.Servlet;
import server.RequestParser.RequestInfo;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @file HtmlLoader.java
 * @brief Servlet for serving static HTML files and web assets
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This servlet handles serving static files (HTML, CSS, JavaScript, images) from
 * a specified directory. It maps URL paths to file system paths and serves the
 * appropriate content with correct MIME types. Used to serve the web interface
 * files for the Agent Graph Manager application.
 */
public class HtmlLoader implements Servlet {
    
    /** @brief Directory path where HTML files are stored */
    private final String htmlDir;
    
    public HtmlLoader(String htmlDirectory) {
        this.htmlDir = htmlDirectory;
    }
    
    @Override
    public void handle(RequestInfo ri, OutputStream toClient) throws IOException {
        String[] segments = ri.getUriSegments();
        
        if (segments.length == 0) {
            // Default to index.html
            serveFile("index.html", toClient);
        } else {
            String filename = segments[segments.length - 1];
            serveFile(filename, toClient);
        }
    }
    
    private void serveFile(String filename, OutputStream toClient) throws IOException {
        Path filePath = Paths.get(htmlDir, filename);
        
        try {
            byte[] content = Files.readAllBytes(filePath);
            String contentType = getContentType(filename);
            
            // Send HTTP response headers
            toClient.write("HTTP/1.1 200 OK\r\n".getBytes());
            toClient.write(("Content-Type: " + contentType + "\r\n").getBytes());
            toClient.write(("Content-Length: " + content.length + "\r\n").getBytes());
            toClient.write("\r\n".getBytes());
            
            // Send file content
            toClient.write(content);
            toClient.flush();
            
        } catch (IOException e) {
            // Send 404 Not Found
            String errorResponse = "404 Not Found";
            toClient.write("HTTP/1.1 404 Not Found\r\n".getBytes());
            toClient.write("Content-Type: text/plain\r\n".getBytes());
            toClient.write(("Content-Length: " + errorResponse.length() + "\r\n").getBytes());
            toClient.write("\r\n".getBytes());
            toClient.write(errorResponse.getBytes());
            toClient.flush();
        }
    }
    
    private String getContentType(String filename) {
        if (filename.endsWith(".html")) return "text/html";
        if (filename.endsWith(".css")) return "text/css";
        if (filename.endsWith(".js")) return "text/javascript";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        return "text/plain";
    }
    
    @Override
    public void close() throws IOException {
        // Nothing to close
    }
}
