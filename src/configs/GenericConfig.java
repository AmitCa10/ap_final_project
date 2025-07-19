package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GenericConfig implements Config {

    private Path confFile;
    private final List<ParallelAgent> agents = new ArrayList<>();

    /* MainTrain passes a String, so keep both overloads */
    public void setConfFile(String p)       { this.confFile = Paths.get(p); }
    public void setConfFile(Path   p)       { this.confFile = p; }

    @Override public String getName()    { return "generic"; }
    @Override public int    getVersion() { return 1; }

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

    @Override
    public void close() {
        agents.forEach(ParallelAgent::close);
        agents.clear();
    }

    /* helpers ------------------------------------------------------------ */

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

    private static String[] split(String line) {
        return line.trim().split("\\s*,\\s*");
    }
}
