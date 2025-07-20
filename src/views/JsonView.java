package views;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * View for rendering JSON responses.
 * Handles JSON formatting and serialization.
 */
public class JsonView implements View {
    
    @Override
    public void render(Object data, OutputStream output) throws IOException {
        String jsonContent = generateJson(data);
        
        // Write HTTP headers
        String headers = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: " + getContentType() + "\r\n" +
                        "Content-Length: " + jsonContent.getBytes().length + "\r\n" +
                        "Access-Control-Allow-Origin: *\r\n" +
                        "\r\n";
        
        output.write(headers.getBytes());
        output.write(jsonContent.getBytes());
        output.flush();
    }
    
    /**
     * @brief Returns the content type for JSON responses
     * @return String containing "application/json; charset=UTF-8"
     */
    @Override
    public String getContentType() {
        return "application/json; charset=UTF-8";
    }
    
    /**
     * Generates JSON content from the provided data.
     * 
     * @param data The data to serialize to JSON
     * @return JSON string representation
     */
    private String generateJson(Object data) {
        if (data == null) {
            return "null";
        }
        
        if (data instanceof String) {
            return "\"" + escapeJson((String) data) + "\"";
        }
        
        if (data instanceof Number) {
            return data.toString();
        }
        
        if (data instanceof List) {
            StringBuilder json = new StringBuilder("[");
            List<?> list = (List<?>) data;
            
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) json.append(",");
                json.append(generateJson(list.get(i)));
            }
            
            json.append("]");
            return json.toString();
        }
        
        // For other objects, create a simple JSON representation
        return "{\"data\":\"" + escapeJson(data.toString()) + "\"}";
    }
    
    /**
     * Escapes special characters for JSON format.
     * 
     * @param text The text to escape
     * @return The escaped text
     */
    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                  .replace("\"", "\\\"")
                  .replace("\b", "\\b")
                  .replace("\f", "\\f")
                  .replace("\n", "\\n")
                  .replace("\r", "\\r")
                  .replace("\t", "\\t");
    }
}
