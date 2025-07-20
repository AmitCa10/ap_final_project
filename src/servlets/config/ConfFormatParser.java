package servlets.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @file ConfFormatParser.java
 * @brief Parser for line-based CONF configuration format
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This parser handles the legacy CONF format where each agent is defined
 * in three consecutive lines: agent class, subscriptions, publications.
 * Implements the Strategy pattern for different configuration formats.
 */
public class ConfFormatParser implements ConfigurationParser {
    
    @Override
    public List<AgentConfiguration> parseConfiguration(String configContent) throws ConfigurationException {
        if (configContent == null || configContent.trim().isEmpty()) {
            throw new ConfigurationException("Configuration content is empty", "CONF");
        }
        
        List<AgentConfiguration> configurations = new ArrayList<>();
        String[] lines = configContent.trim().split("\\n");
        
        if (lines.length % 3 != 0) {
            throw new ConfigurationException(
                "CONF format requires groups of 3 lines (agent class, subscriptions, publications). " +
                "Found " + lines.length + " lines.", "CONF");
        }
        
        for (int i = 0; i < lines.length; i += 3) {
            try {
                String agentClass = lines[i].trim();
                String[] subscriptions = parseTopicList(lines[i + 1]);
                String[] publications = parseTopicList(lines[i + 2]);
                
                // Extract simple class name from full package name
                String simpleClassName = extractSimpleClassName(agentClass);
                
                AgentConfiguration config = new AgentConfiguration(simpleClassName, subscriptions, publications);
                
                if (!config.isValid()) {
                    throw new ConfigurationException(
                        "Invalid agent configuration: " + config.toString(), "CONF", i / 3 + 1);
                }
                
                configurations.add(config);
                
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ConfigurationException(
                    "Incomplete agent definition at line " + (i + 1), "CONF", i + 1, e);
            } catch (Exception e) {
                throw new ConfigurationException(
                    "Error parsing agent at line " + (i + 1) + ": " + e.getMessage(), "CONF", i + 1, e);
            }
        }
        
        if (configurations.isEmpty()) {
            throw new ConfigurationException("No valid agent configurations found", "CONF");
        }
        
        return configurations;
    }
    
    @Override
    public boolean canParse(String configContent) {
        if (configContent == null || configContent.trim().isEmpty()) {
            return false;
        }
        
        // CONF format should not start with JSON markers
        String content = configContent.trim();
        if (content.startsWith("{") || content.startsWith("[")) {
            return false;
        }
        
        // Check if it follows the 3-line pattern
        String[] lines = content.split("\\n");
        if (lines.length < 3 || lines.length % 3 != 0) {
            return false;
        }
        
        // Check if first line of each group contains "Agent"
        for (int i = 0; i < lines.length; i += 3) {
            if (!lines[i].contains("Agent")) {
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public String getFormatName() {
        return "CONF";
    }
    
    /**
     * @brief Parses a comma-separated list of topics
     * @param topicLine Line containing comma-separated topic names
     * @return Array of cleaned topic names
     */
    private String[] parseTopicList(String topicLine) {
        if (topicLine == null || topicLine.trim().isEmpty()) {
            return new String[0];
        }
        
        String[] topics = topicLine.trim().split(",");
        List<String> cleanedTopics = new ArrayList<>();
        
        for (String topic : topics) {
            String cleanTopic = topic.trim();
            if (!cleanTopic.isEmpty()) {
                cleanedTopics.add(cleanTopic);
            }
        }
        
        return cleanedTopics.toArray(new String[0]);
    }
    
    /**
     * @brief Extracts simple class name from fully qualified class name
     * @param fullClassName Full class name (e.g., "configs.PlusAgent")
     * @return Simple class name (e.g., "PlusAgent")
     */
    private String extractSimpleClassName(String fullClassName) {
        if (fullClassName == null || fullClassName.trim().isEmpty()) {
            return "";
        }
        
        String className = fullClassName.trim();
        int lastDot = className.lastIndexOf('.');
        return lastDot >= 0 ? className.substring(lastDot + 1) : className;
    }
}
