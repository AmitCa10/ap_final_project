package servlets.config;

import graph.TopicManagerSingleton;
import graph.TopicManagerSingleton.TopicManager;
import graph.Agent;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

/**
 * @file ConfigurationService.java
 * @brief Service class that orchestrates the configuration loading process
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This service coordinates between parsers, factories, and the topic manager
 * to load agent configurations. Follows the Facade pattern to provide a
 * simplified interface for complex configuration operations.
 */
public class ConfigurationService {
    
    /** @brief Reference to the TopicManager */
    private final TopicManager topicManager;
    
    /** @brief List of available configuration parsers */
    private final List<ConfigurationParser> parsers;
    
    /** @brief Agent factory for creating agent instances */
    private final AgentFactory agentFactory;
    
    /** @brief Multipart file extractor */
    private final MultipartFileExtractor fileExtractor;
    
    /**
     * @brief Constructor with default parsers and factory
     */
    public ConfigurationService() {
        this.topicManager = TopicManagerSingleton.get();
        this.parsers = new ArrayList<>();
        this.agentFactory = new DefaultAgentFactory();
        this.fileExtractor = new MultipartFileExtractor();
        
        // Register available parsers (Order matters - more specific parsers first)
        parsers.add(new JsonFormatParser());
        parsers.add(new ConfFormatParser());
    }
    
    /**
     * @brief Loads configuration from multipart form data
     * @param multipartData The raw multipart form data
     * @return Configuration loading result with success status and details
     * @throws ConfigurationException if loading fails
     */
    public ConfigurationResult loadConfigurationFromMultipart(byte[] multipartData) throws ConfigurationException {
        // Extract file content from multipart data
        String configContent = fileExtractor.extractFileContent(multipartData);
        
        // Load the configuration
        return loadConfiguration(configContent);
    }
    
    /**
     * @brief Loads configuration from string content
     * @param configContent The configuration content as string
     * @return Configuration loading result with success status and details
     * @throws ConfigurationException if loading fails
     */
    public ConfigurationResult loadConfiguration(String configContent) throws ConfigurationException {
        if (configContent == null || configContent.trim().isEmpty()) {
            throw new ConfigurationException("Configuration content is empty");
        }
        
        // Find appropriate parser
        ConfigurationParser parser = findParser(configContent);
        if (parser == null) {
            throw new ConfigurationException("No parser found for configuration format");
        }
        
        System.out.println("ConfigurationService: Using " + parser.getFormatName() + " parser");
        
        // Parse configuration
        List<AgentConfiguration> configurations = parser.parseConfiguration(configContent);
        
        // Clear existing configuration
        topicManager.clear();
        
        // Create topics
        Set<String> allTopics = extractAllTopics(configurations);
        createTopics(allTopics);
        
        // Create agents
        List<Agent> createdAgents = createAgents(configurations);
        
        return new ConfigurationResult(
            true, 
            parser.getFormatName(),
            configurations.size(),
            allTopics.size(),
            createdAgents,
            "Configuration loaded successfully"
        );
    }
    
    /**
     * @brief Finds the appropriate parser for the given configuration content
     * @param configContent The configuration content
     * @return The matching parser, or null if none found
     */
    private ConfigurationParser findParser(String configContent) {
        for (ConfigurationParser parser : parsers) {
            if (parser.canParse(configContent)) {
                return parser;
            }
        }
        return null;
    }
    
    /**
     * @brief Extracts all unique topic names from configurations
     * @param configurations List of agent configurations
     * @return Set of all unique topic names
     */
    private Set<String> extractAllTopics(List<AgentConfiguration> configurations) {
        Set<String> allTopics = new HashSet<>();
        
        for (AgentConfiguration config : configurations) {
            String[] topics = config.getAllTopics();
            for (String topic : topics) {
                if (!topic.isEmpty()) {
                    allTopics.add(topic);
                }
            }
        }
        
        return allTopics;
    }
    
    /**
     * @brief Creates topics in the TopicManager
     * @param topicNames Set of topic names to create
     */
    private void createTopics(Set<String> topicNames) {
        System.out.println("ConfigurationService: Creating " + topicNames.size() + " topics: " + topicNames);
        
        for (String topicName : topicNames) {
            if (!topicManager.containsTopic(topicName)) {
                topicManager.getTopic(topicName); // This creates the topic
                System.out.println("ConfigurationService: Created topic: " + topicName);
            }
        }
    }
    
    /**
     * @brief Creates agent instances from configurations
     * @param configurations List of agent configurations
     * @return List of created agent instances
     * @throws ConfigurationException if any agent creation fails
     */
    private List<Agent> createAgents(List<AgentConfiguration> configurations) throws ConfigurationException {
        List<Agent> createdAgents = new ArrayList<>();
        List<String> failures = new ArrayList<>();
        
        System.out.println("ConfigurationService: Creating " + configurations.size() + " agents");
        
        for (AgentConfiguration config : configurations) {
            try {
                if (!agentFactory.canCreateAgent(config.getAgentClass())) {
                    failures.add("Unsupported agent type: " + config.getAgentClass());
                    continue;
                }
                
                Agent agent = agentFactory.createAgent(config);
                createdAgents.add(agent);
                System.out.println("ConfigurationService: Created agent: " + config.getAgentClass());
                
            } catch (AgentCreationException e) {
                String errorMsg = "Failed to create " + config.getAgentClass() + ": " + e.getMessage();
                failures.add(errorMsg);
                System.err.println("ConfigurationService: " + errorMsg);
            }
        }
        
        if (!failures.isEmpty() && createdAgents.isEmpty()) {
            // All agents failed - throw exception
            throw new ConfigurationException("Failed to create any agents: " + String.join("; ", failures));
        } else if (!failures.isEmpty()) {
            // Some agents failed - log warnings but continue
            System.err.println("ConfigurationService: Some agents failed to create: " + String.join("; ", failures));
        }
        
        return createdAgents;
    }
}
