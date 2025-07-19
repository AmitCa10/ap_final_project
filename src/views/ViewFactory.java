package views;

/**
 * Factory class for creating different types of views.
 * Centralizes view creation logic and supports different view types.
 */
public class ViewFactory {
    
    public enum ViewType {
        HTML,
        JSON,
        TOPIC_TABLE
    }
    
    /**
     * Creates a view of the specified type.
     * 
     * @param type The type of view to create
     * @return A view instance
     * @throws IllegalArgumentException If the view type is not supported
     */
    public static View createView(ViewType type) {
        switch (type) {
            case HTML:
                return new HtmlView(getDefaultHtmlTemplate());
            case JSON:
                return new JsonView();
            case TOPIC_TABLE:
                return new TopicView();
            default:
                throw new IllegalArgumentException("Unsupported view type: " + type);
        }
    }
    
    /**
     * Creates an HTML view with a custom template.
     * 
     * @param template The HTML template to use
     * @return An HTML view instance
     */
    public static View createHtmlView(String template) {
        return new HtmlView(template);
    }
    
    /**
     * Gets the default HTML template.
     * 
     * @return A basic HTML template
     */
    private static String getDefaultHtmlTemplate() {
        return "<!DOCTYPE html>\n" +
               "<html lang=\"en\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
               "    <title>Agent Graph Manager</title>\n" +
               "</head>\n" +
               "<body>\n" +
               "    {{content}}\n" +
               "</body>\n" +
               "</html>";
    }
}
