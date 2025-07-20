package graph;

import java.util.Date;
import java.nio.charset.StandardCharsets;

/**
 * @file Message.java
 * @brief Represents a message that can be passed between agents through topics
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * A Message encapsulates data that flows through the agent graph system.
 * Messages can contain arbitrary binary data and provide convenient access
 * to the data as text and numeric values. Each message is timestamped
 * with its creation time for debugging and monitoring purposes.
 */
public class Message {

    /** @brief Raw binary data of the message */
    public final byte[] data;
    
    /** @brief Text representation of the message data (UTF-8 decoded) */
    public final String asText;
    
    /** @brief Numeric representation of the message data (parsed as double) */
    public final double asDouble;
    
    /** @brief Timestamp when this message was created */
    public final Date   date;

    /**
     * @brief Canonical constructor that creates a message from binary data
     * @param data The raw binary data for this message
     * 
     * This is the primary constructor that accepts binary data as the most
     * general form. The data is automatically converted to text (UTF-8) and
     * numeric (double) representations for convenient access by agents.
     * A timestamp is automatically assigned when the message is created.
     */
    public Message(byte[] data) { // chose byte[] as it is the most general form
        this.data      = data;
        this.asText    = new String(data, StandardCharsets.UTF_8);
        this.asDouble  = parseDoubleSafely(this.asText);
        this.date      = new Date();          // creation time
    }

    /**
     * @brief Convenience constructor that creates a message from text
     * @param text The text content for this message
     * 
     * This constructor converts the text to UTF-8 bytes and calls the
     * canonical constructor. This is a convenience method for creating
     * text-based messages without manual byte conversion.
     */
    public Message(String text) {
        this(text.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * @brief Convenience constructor that creates a message from a numeric value
     * @param value The numeric value to convert to a message
     * 
     * This constructor converts the double value to its string representation
     * and then to a text-based message. Useful for agents that work primarily
     * with numeric data.
     */
    public Message(double value) {
        this(Double.toString(value)); // calls the text constructor
    }

    /**
     * @brief Safely parses a string to a double value with fallback handling
     * @param s The string to parse as a double
     * @return The parsed double value, or Double.NaN if parsing fails
     * 
     * This method attempts to parse the string as a double and returns
     * Double.NaN if the string is not a valid number. This prevents
     * NumberFormatException from propagating and provides a consistent
     * fallback behavior for invalid numeric data.
     */
    private static double parseDoubleSafely(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return Double.NaN;                // spec‑mandated fallback
        }
    }
}