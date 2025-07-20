package views;

/**
 * @file ViewFactory.java
 * @brief Factory class for creating different types of views in the MVC architecture
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This factory class centralizes the creation of view objects and provides a clean
 * interface for obtaining different types of views. It implements the Factory pattern
 * to decouple view creation from view usage, making it easy to add new view types
 * or modify existing ones without affecting client code.
 */
public class ViewFactory {
    
    /**
     * @brief Enumeration of supported view types
     * 
     * This enum defines the different types of views that can be created by
     * the factory. Each type corresponds to a specific view implementation.
     */
    public enum ViewType {
        /** @brief Generic HTML view for custom HTML content */
        HTML,
        /** @brief JSON view for structured data output */
        JSON,
        /** @brief Specialized view for rendering topic tables */
        TOPIC_TABLE
    }
    
    /**
     * @brief Creates a view instance of the specified type
     * @param type The type of view to create (from ViewType enum)
     * @return A view instance configured for the requested type
     * @throws IllegalArgumentException If the view type is not supported
     * 
     * This method serves as the main entry point for creating views. It uses
     * the provided ViewType to determine which concrete view class to instantiate
     * and returns it configured with appropriate default settings.
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
