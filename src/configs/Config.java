package configs;

/**
 * @file Config.java
 * @brief Interface defining the contract for configuration objects
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * The Config interface provides a standard contract for configuration objects
 * that can create and manage system components. Implementations typically
 * handle the instantiation and lifecycle management of agents, topics,
 * and other system resources based on configuration data.
 */
public interface Config {
    
    /**
     * @brief Creates and initializes the configuration-defined components
     * 
     * This method is responsible for instantiating all agents, topics,
     * and other resources defined in the configuration. It should establish
     * all necessary connections and prepare the system for operation.
     */
    void create();
    
    /**
     * @brief Returns the name of this configuration
     * @return A string identifier for this configuration
     * 
     * Provides a human-readable name that can be used for logging,
     * debugging, or user interface purposes to identify this particular
     * configuration instance.
     */
    String getName();
    
    /**
     * @brief Returns the version number of this configuration
     * @return An integer representing the configuration version
     * 
     * Version information can be used for compatibility checking,
     * migration purposes, or to track configuration evolution over time.
     */
    int getVersion();
    
    /**
     * @brief Cleans up and releases all resources created by this configuration
     * 
     * This method should properly shut down all agents, close topic connections,
     * and release any other resources that were allocated during the create()
     * phase to prevent memory leaks and ensure clean system shutdown.
     */
    void close();
}
