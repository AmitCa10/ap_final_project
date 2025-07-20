package servlets.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @file JsonFormatParser.java
 * @brief Parser for JSON configuration format
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This parser handles JSON configuration files with agent definitions.
 * Uses manual JSON parsing to avoid external dependencies while providing
 * robust error handling and validation.
 */
public class JsonFormatParser implements ConfigurationParser {
    
    @Override
    public List<AgentConfiguration> parseConfiguration(String configContent) throws ConfigurationException {
        if (configContent == null || configContent.trim().isEmpty()) {
            throw new ConfigurationException("Configuration content is empty", "JSON");
        }
        
        try {
            String[] agentObjects = extractAgentObjects(configContent);
            List<AgentConfiguration> configurations = new ArrayList<>();
            
            for (int i = 0; i < agentObjects.length; i++) {
                try {
                    String agentJson = agentObjects[i];
                    
                    String agentClass = extractStringValue(agentJson, "type");
                    if (agentClass.isEmpty()) {
                        agentClass = extractStringValue(agentJson, "agentClass");
                    }
                    
                    String[] subscriptions = extractArrayValue(agentJson, "subscriptions");
                    String[] publications = extractArrayValue(agentJson, "publications");
                    
                    // Extract simple class name
                    String simpleClassName = extractSimpleClassName(agentClass);
                    
                    AgentConfiguration config = new AgentConfiguration(simpleClassName, subscriptions, publications);
                    
                    if (!config.isValid()) {
                        throw new ConfigurationException(
                            "Invalid agent configuration: " + config.toString(), "JSON", i + 1);
                    }
                    
                    configurations.add(config);
                    
                } catch (Exception e) {
                    throw new ConfigurationException(
                        "Error parsing agent " + (i + 1) + ": " + e.getMessage(), "JSON", i + 1, e);
                }
            }
            
            if (configurations.isEmpty()) {
                throw new ConfigurationException("No valid agent configurations found in JSON", "JSON");
            }
            
            return configurations;
            
        } catch (ConfigurationException e) {
            throw e;
        } catch (Exception e) {
            throw new ConfigurationException("Failed to parse JSON configuration: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean canParse(String configContent) {
        if (configContent == null || configContent.trim().isEmpty()) {
            return false;
        }
        
        String content = configContent.trim();
        
        // Must be a JSON object
        if (!content.startsWith("{") || !content.endsWith("}")) {
            return false;
        }
        
        // Must contain agents array
        return content.contains("\"agents\"") && content.contains("[") && content.contains("]");
    }
    
    @Override
    public String getFormatName() {
        return "JSON";
    }
    
    /**
     * @brief Extracts agent objects from the JSON agents array
     * @param jsonContent Full JSON configuration content
     * @return Array of individual agent JSON objects as strings
     * @throws ConfigurationException if agents array cannot be found or parsed
     */
    private String[] extractAgentObjects(String jsonContent) throws ConfigurationException {
        List<String> agents = new ArrayList<>();
        
        // Find the agents array
        int agentsStart = jsonContent.indexOf("\"agents\"");
        if (agentsStart == -1) {
            throw new ConfigurationException("Missing 'agents' array in JSON configuration", "JSON");
        }
        
        int arrayStart = jsonContent.indexOf("[", agentsStart);
        if (arrayStart == -1) {
            throw new ConfigurationException("Invalid 'agents' array format", "JSON");
        }
        
        int level = 0;
        int objStart = -1;
        
        for (int i = arrayStart; i < jsonContent.length(); i++) {
            char c = jsonContent.charAt(i);
            
            if (c == '{') {
                if (level == 0) {
                    objStart = i;
                }
                level++;
            } else if (c == '}') {
                level--;
                if (level == 0 && objStart != -1) {
                    agents.add(jsonContent.substring(objStart, i + 1));
                }
            } else if (c == ']' && level == 0) {
                break;
            }
        }
        
        if (agents.isEmpty()) {
            throw new ConfigurationException("No agent objects found in agents array", "JSON");
        }
        
        return agents.toArray(new String[0]);
    }
    
    /**
     * @brief Extracts a string value from a JSON object
     * @param jsonObj JSON object as string
     * @param key The key to extract
     * @return The string value, or empty string if not found
     */
    private String extractStringValue(String jsonObj, String key) {
        try {
            String searchKey = "\"" + key + "\"";
            int keyIndex = jsonObj.indexOf(searchKey);
            if (keyIndex == -1) return "";
            
            int colonIndex = jsonObj.indexOf(":", keyIndex);
            if (colonIndex == -1) return "";
            
            int valueStart = jsonObj.indexOf("\"", colonIndex) + 1;
            int valueEnd = jsonObj.indexOf("\"", valueStart);
            
            if (valueStart == 0 || valueEnd == -1) return "";
            
            return jsonObj.substring(valueStart, valueEnd);
        } catch (Exception e) {
            return "";
        }
    }
    
    /**
     * @brief Extracts an array value from a JSON object
     * @param jsonObj JSON object as string
     * @param key The key to extract
     * @return Array of string values
     */
    private String[] extractArrayValue(String jsonObj, String key) {
        List<String> values = new ArrayList<>();
        String searchKey = "\"" + key + "\"";
        int start = jsonObj.indexOf(searchKey);
        if (start == -1) return new String[0];
        
        start = jsonObj.indexOf("[", start);
        if (start == -1) return new String[0];
        
        boolean inString = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = start + 1; i < jsonObj.length(); i++) {
            char c = jsonObj.charAt(i);
            
            if (c == '"' && (i == 0 || jsonObj.charAt(i - 1) != '\\')) {
                if (inString) {
                    String value = current.toString().trim();
                    if (!value.isEmpty()) {
                        values.add(value);
                    }
                    current = new StringBuilder();
                    inString = false;
                } else {
                    inString = true;
                }
            } else if (inString) {
                current.append(c);
            } else if (c == ']') {
                break;
            }
        }
        
        return values.toArray(new String[0]);
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
