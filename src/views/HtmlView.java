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
