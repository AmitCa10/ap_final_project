package test;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;


public final class TopicManagerSingleton {

    /** Static holder idiom — JVM guarantees thread safety. */
    private static class Holder {
        private static final TopicManager INSTANCE = new TopicManager();
    }

    /** Retrieve the sole TopicManager instance. */
    public static TopicManager get() { return Holder.INSTANCE; }

    public static class TopicManager {

        private final ConcurrentHashMap<String, Topic> topics = new ConcurrentHashMap<>();

        public Topic getTopic(String name) {
            return topics.computeIfAbsent(name, Topic::new);   // flyweight creation
        }

        public Collection<Topic> getTopics() { return topics.values(); }

        public void clear() { topics.clear(); }
    }

    private TopicManagerSingleton() {}
}
