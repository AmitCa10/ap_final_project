package views;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import graph.Topic;
import graph.Message;

/**
 * View for rendering topic data in HTML format.
 * Handles the presentation of topics and their messages.
 */
public class TopicView implements View {
    
    @Override
    public void render(Object data, OutputStream output) throws IOException {
        // Add debugging
        System.out.println("TopicView.render called with data: " + data);
        System.out.println("Data type: " + (data != null ? data.getClass().getSimpleName() : "null"));
        
        String htmlContent = generateTopicTable(data);
        
        // Write just the HTML content (no headers for embedded content)
        output.write(htmlContent.getBytes());
        output.flush();
    }
    
    @Override
    public String getContentType() {
        return "text/html; charset=UTF-8";
    }
    
    /**
     * Generates HTML table content for topics.
     * 
     * @param data List of topics or error message
     * @return HTML table rows
     */
    @SuppressWarnings("unchecked")
    private String generateTopicTable(Object data) {
        if (data instanceof String) {
            String errorMsg = (String) data;
            System.out.println("TopicView: Rendering error message: " + errorMsg);
            return "<tr><td colspan=\"2\" style=\"text-align: center; color: #ff6b6b;\">" + 
                   escapeHtml(errorMsg) + "</td></tr>";
        }
        
        if (data instanceof List) {
            List<Topic> topics = (List<Topic>) data;
            System.out.println("TopicView: Rendering " + topics.size() + " topics");
            
            if (topics.isEmpty()) {
                return "<tr><td colspan=\"2\" style=\"text-align: center; color: #87ceeb;\">" +
                       "No topics available</td></tr>";
            }
            
            StringBuilder html = new StringBuilder();
            for (Topic topic : topics) {
                Message lastMsg = topic.getLastMessage();
                String lastMessage = "<em style=\"color: #c9a96e;\">No messages yet</em>";
                if (lastMsg != null && lastMsg.asText != null && !lastMsg.asText.isEmpty()) {
                    // Display the actual message value (including 0, 0.0, etc.)
                    lastMessage = escapeHtml(lastMsg.asText);
                }
                
                System.out.println("TopicView: Topic " + topic.name + " has message: " + 
                    (lastMsg != null ? lastMsg.asText : "null"));
                
                html.append("<tr style=\"background: rgba(177, 156, 217, 0.1); transition: all 0.3s ease;\">")
                    .append("<td style=\"padding: 12px 15px; border-bottom: 1px solid rgba(177, 156, 217, 0.2); color: #f0f0f0;\">")
                    .append(escapeHtml(topic.name))
                    .append("</td>")
                    .append("<td style=\"padding: 12px 15px; border-bottom: 1px solid rgba(177, 156, 217, 0.2); color: #f0f0f0;\">")
                    .append(lastMessage)
                    .append("</td>")
                    .append("</tr>");
            }
            
            return html.toString();
        }
        
        return "<tr><td colspan=\"2\" style=\"text-align: center; color: #ff6b6b;\">" +
               "Invalid data format</td></tr>";
    }
    
    /**
     * Escapes HTML special characters to prevent XSS.
     * 
     * @param text The text to escape
     * @return The escaped text
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
