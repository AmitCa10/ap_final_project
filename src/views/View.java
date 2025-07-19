package views;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Base interface for all view components in the MVC architecture.
 * Views are responsible for rendering data and handling presentation logic.
 */
public interface View {
    /**
     * Renders the view content to the output stream.
     * 
     * @param data The data to render
     * @param output The output stream to write to
     * @throws IOException If an I/O error occurs during rendering
     */
    void render(Object data, OutputStream output) throws IOException;
    
    /**
     * Gets the content type for this view (e.g., "text/html", "application/json").
     * 
     * @return The MIME type of the content this view produces
     */
    String getContentType();
}
