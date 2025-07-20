package servlets.config;

/**
 * @file AgentCreationException.java
 * @brief Exception for agent creation failures
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This exception is thrown when agent instantiation fails due to
 * invalid configuration, missing dependencies, or runtime errors.
 */
public class AgentCreationException extends Exception {
    
    /** @brief The agent class that failed to be created */
    private final String agentClass;
    
    /**
     * @brief Constructor with message and agent class
     * @param message Descriptive error message
     * @param agentClass The agent class that failed to be created
     */
    public AgentCreationException(String message, String agentClass) {
        super(message);
        this.agentClass = agentClass;
    }
    
    /**
     * @brief Constructor with message, agent class, and cause
     * @param message Descriptive error message
     * @param agentClass The agent class that failed to be created
     * @param cause The underlying exception
     */
    public AgentCreationException(String message, String agentClass, Throwable cause) {
        super(message, cause);
        this.agentClass = agentClass;
    }
    
    /**
     * @brief Gets the agent class that failed to be created
     * @return The agent class name
     */
    public String getAgentClass() {
        return agentClass;
    }
    
    @Override
    public String getMessage() {
        return super.getMessage() + " [Agent: " + agentClass + "]";
    }
}
