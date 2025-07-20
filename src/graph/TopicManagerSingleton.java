package graph;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @file TopicManagerSingleton.java
 * @brief Singleton wrapper for the TopicManager providing global access to topics
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This class implements the Singleton pattern to provide global access to a single
 * TopicManager instance. It uses the static holder idiom for thread-safe lazy
 * initialization. The TopicManager handles all topic creation, retrieval, and
 * management operations in the agent graph system.
 */
public final class TopicManagerSingleton {

    /**
     * @brief Static holder idiom for thread-safe lazy initialization
     * 
     * The JVM guarantees that the static INSTANCE field is initialized exactly
     * once and is visible to all threads. This provides thread safety without
     * the overhead of synchronization.
     */
    private static class Holder {
        /** @brief The single TopicManager instance */
        private static final TopicManager INSTANCE = new TopicManager();
    }

    /**
     * @brief Retrieves the sole TopicManager instance
     * @return The singleton TopicManager instance
     * 
     * This method provides global access to the TopicManager. It's thread-safe
     * and uses lazy initialization through the static holder idiom.
     */
    public static TopicManager get() { return Holder.INSTANCE; }

    /**
     * @brief Inner class that manages all topics in the system
     * 
     * The TopicManager is responsible for creating, storing, and providing
     * access to all topics in the agent graph system. It ensures that topics
     * are unique by name and provides thread-safe operations.
     */
    public static class TopicManager {

        /** @brief Thread-safe map of all topics indexed by name */
        private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

        /**
         * @brief Gets or creates a topic with the specified name
         * @param name The unique name of the topic to retrieve or create
         * @return The topic instance (existing or newly created)
         * 
         * This method implements the flyweight pattern - if a topic with the
         * given name already exists, it returns the existing instance. Otherwise,
         * it creates a new topic. The operation is thread-safe.
         */
        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);   // flyweight creation
        }

        /**
         * @brief Returns a collection of all existing topics
         * @return A collection containing all topics in the system
         * 
         * This method provides access to all topics currently managed by
         * the TopicManager. The returned collection reflects the current
         * state and may change if topics are added or removed.
         */
        public Collection<Topic> getTopics() { return topics.values(); }

        /**
         * @brief Removes all topics from the manager
         * 
         * This method clears all topics from the system. It's typically used
         * for resetting the system state or during shutdown procedures.
         */
        public void clear() { topics.clear(); }
        
        /**
         * @brief Checks if a topic with the given name exists
         * @param name The name of the topic to check for
         * @return true if a topic with this name exists, false otherwise
         * 
         * This method allows checking for topic existence without creating
         * a new topic if it doesn't exist (unlike getTopic).
         */
        public boolean containsTopic(String name) { return topics.containsKey(name); }
    }

    private TopicManagerSingleton() {}
}
