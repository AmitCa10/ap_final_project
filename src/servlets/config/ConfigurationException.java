package servlets.config;

/**
 * @file ConfigurationException.java
 * @brief Exception class for configuration parsing errors
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This exception is thrown when configuration parsing fails due to
 * invalid format, missing required fields, or other parsing errors.
 * Provides specific error context for better debugging.
 */
public class ConfigurationException extends Exception {
    
    /** @brief The configuration format that caused the error */
    private final String format;
    
    /** @brief The line number where the error occurred (if applicable) */
    private final int lineNumber;
    
    /**
     * @brief Constructor with message only
     * @param message Descriptive error message
     */
    public ConfigurationException(String message) {
        super(message);
        this.format = "unknown";
        this.lineNumber = -1;
    }
    
    /**
     * @brief Constructor with message and format
     * @param message Descriptive error message
     * @param format The configuration format (e.g., "JSON", "CONF")
     */
    public ConfigurationException(String message, String format) {
        super(message);
        this.format = format;
        this.lineNumber = -1;
    }
    
    /**
     * @brief Constructor with message, format, and line number
     * @param message Descriptive error message
     * @param format The configuration format
     * @param lineNumber The line number where error occurred
     */
    public ConfigurationException(String message, String format, int lineNumber) {
        super(message);
        this.format = format;
        this.lineNumber = lineNumber;
    }
    
    /**
     * @brief Constructor with message and cause
     * @param message Descriptive error message
     * @param cause The underlying exception that caused this error
     */
    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
        this.format = "unknown";
        this.lineNumber = -1;
    }
    
    /**
     * @brief Constructor with full details
     * @param message Descriptive error message
     * @param format The configuration format
     * @param lineNumber The line number where error occurred
     * @param cause The underlying exception
     */
    public ConfigurationException(String message, String format, int lineNumber, Throwable cause) {
        super(message, cause);
        this.format = format;
        this.lineNumber = lineNumber;
    }
    
    /**
     * @brief Gets the configuration format that caused the error
     * @return The configuration format
     */
    public String getFormat() {
        return format;
    }
    
    /**
     * @brief Gets the line number where the error occurred
     * @return Line number, or -1 if not applicable
     */
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String getMessage() {
        StringBuilder sb = new StringBuilder(super.getMessage());
        
        if (!format.equals("unknown")) {
            sb.append(" [Format: ").append(format).append("]");
        }
        
        if (lineNumber > 0) {
            sb.append(" [Line: ").append(lineNumber).append("]");
        }
        
        return sb.toString();
    }
}
