package configs;

import graph.Agent;
import graph.ParallelAgent;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @file GenericConfig.java
 * @brief Generic configuration loader that creates agents from text-based configuration files
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * GenericConfig provides a flexible way to configure and instantiate agents
 * based on text file definitions. It uses reflection to dynamically load
 * agent classes and wraps them in ParallelAgent containers for concurrent
 * execution. The configuration format uses triplets of lines: class name,
 * subscription topics, and publication topics.
 */
public class GenericConfig implements Config {

    /** @brief Path to the configuration file */
    private Path confFile;
    
    /** @brief List of instantiated parallel agents created from configuration */
    private final List<ParallelAgent> agents = new ArrayList<>();

    /**
     * @brief Sets the configuration file path from a string
     * @param p String path to the configuration file
     * 
     * Converts the string path to a Path object for internal use.
     * This overload is provided for compatibility with legacy code.
     */
    public void setConfFile(String p)       { this.confFile = Paths.get(p); }
    
    /**
     * @brief Sets the configuration file path directly
     * @param p Path object pointing to the configuration file
     */
    public void setConfFile(Path   p)       { this.confFile = p; }

    /**
     * @brief Returns the name of this configuration type
     * @return A string containing "generic"
     */
    @Override public String getName()    { return "generic"; }
    
    /**
     * @brief Returns the version number of this configuration implementation
     * @return The integer version number (currently 1)
     */
    @Override public int    getVersion() { return 1; }

    /**
     * @brief Creates and initializes agents from the configuration file
     * @throws IllegalStateException if setConfFile was not called before create()
     * @throws IllegalArgumentException if configuration file doesn't have triplet format
     * @throws RuntimeException if agent class loading or instantiation fails
     * 
     * Reads the configuration file and processes it in triplets of lines:
     * 1. Fully qualified class name of the agent
     * 2. Comma-separated list of subscription topic names
     * 3. Comma-separated list of publication topic names
     * 
     * Each agent is instantiated using reflection and wrapped in a ParallelAgent
     * with a buffer size of 128 for concurrent message processing.
     */
    @Override
    public void create() {
        if (confFile == null)
            throw new IllegalStateException("setConfFile was not called");

        List<String> lines = readAll(confFile);
        if (lines.size() % 3 != 0)
            throw new IllegalArgumentException("Config file must come in triplets");

        for (int i = 0; i < lines.size(); i += 3) {
            String className = lines.get(i).trim();
            String[] subs = split(lines.get(i + 1));
            String[] pubs = split(lines.get(i + 2));

            try {
                Class<?>     clazz = Class.forName(className);
                Constructor<?> ctor  =
                        clazz.getConstructor(String[].class, String[].class);

                Agent core = (Agent) ctor.newInstance((Object) subs, (Object) pubs);
                agents.add(new ParallelAgent(core, 128));
            } catch (Exception e) {
                throw new RuntimeException("Failed to load " + className, e);
            }
        }
    }

    /**
     * @brief Closes all instantiated agents and clears the agent list
     * 
     * Iterates through all created ParallelAgent instances, closes each one
     * to ensure proper cleanup of resources, and then clears the agents list.
     * This prevents memory leaks and ensures all agent threads are properly
     * terminated.
     */
    @Override
    public void close() {
        agents.forEach(ParallelAgent::close);
        agents.clear();
    }

    /* helpers ------------------------------------------------------------ */

    /**
     * @brief Reads all non-empty lines from a configuration file
     * @param p Path to the file to read
     * @return List of trimmed, non-empty lines from the file
     * @throws RuntimeException if file reading fails due to IOException
     * 
     * Uses Java 8 streams to efficiently read and filter file content,
     * removing empty lines and trimming whitespace from each line.
     */
    private static List<String> readAll(Path p) {
        try (BufferedReader br = Files.newBufferedReader(p)) {
            return br.lines()
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())           // Java‑8 blank test
                     .collect(Collectors.toList());       // Java‑8 equivalent
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @brief Splits a comma-separated line into an array of strings
     * @param line The input line containing comma-separated values
     * @return Array of strings with whitespace trimmed around commas
     * 
     * Uses regex pattern to split on commas while handling optional
     * whitespace around the comma delimiters.
     */
    private static String[] split(String line) {
        return line.trim().split("\\s*,\\s*");
    }
}
