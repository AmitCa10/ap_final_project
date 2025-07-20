package servlets.config;

import java.util.List;

/**
 * @file ConfigurationParser.java
 * @brief Interface for parsing different configuration formats
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This interface defines the contract for parsing configuration files.
 * It supports the Open-Closed Principle by allowing new configuration
 * formats to be added without modifying existing code.
 */
public interface ConfigurationParser {
    
    /**
     * @brief Parses configuration content and extracts agent definitions
     * @param configContent The raw configuration content as a string
     * @return List of agent configuration objects
     * @throws ConfigurationException if the content is invalid or cannot be parsed
     */
    List<AgentConfiguration> parseConfiguration(String configContent) throws ConfigurationException;
    
    /**
     * @brief Validates whether the given content can be parsed by this parser
     * @param configContent The configuration content to validate
     * @return true if this parser can handle the content format, false otherwise
     */
    boolean canParse(String configContent);
    
    /**
     * @brief Returns a human-readable name for this configuration format
     * @return The format name (e.g., "JSON", "CONF", "XML")
     */
    String getFormatName();
}
