package views;

import java.io.IOException;
import java.io.OutputStream;

/**
 * View for rendering HTML content.
 * Handles HTML template rendering and formatting.
 */
public class HtmlView implements View {
    private String template;
    
    public HtmlView(String template) {
        this.template = template;
    }
    
    /**
     * @brief Renders data as HTML with complete HTTP response headers
     * @param data The data to render as HTML content
     * @param output Stream to write the rendered HTML response
     * @throws IOException if writing to output stream fails
     */
    @Override
    public void render(Object data, OutputStream output) throws IOException {
        String htmlContent = generateHtmlContent(data);
        
        // Write HTTP headers
        String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + getContentType() + "\r\n" +
                        "Content-Length: " + htmlContent.getBytes().length + "\r\n" +
                        "\r\n";
        
        output.write(headers.getBytes());
        output.write(htmlContent.getBytes());
        output.flush();
    }
    
    /**
     * @brief Returns the content type for HTML responses
     * @return String containing "text/html; charset=UTF-8"
     */
    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }
    
    /**
     * Generates HTML content based on the template and data.
     * 
     * @param data The data to render in the template
     * @return The rendered HTML content
     */
    private String generateHtmlContent(Object data) {
        if (data == null) {
            return template;
        }
        
        // Simple template substitution
        String content = template;
        if (data instanceof String) {
            content = content.replace("{{content}}", (String) data);
        }
        
        return content;
    }
}
