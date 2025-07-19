package graph;

import java.util.Date;
import java.nio.charset.StandardCharsets;

public class Message {

    public final byte[] data;
    public final String asText;
    public final double asDouble;
    public final Date   date;

    // canonical constructor
    public Message(byte[] data) { // chose byte[] as it is the most general form
        this.data      = data;
        this.asText    = new String(data, StandardCharsets.UTF_8);
        this.asDouble  = parseDoubleSafely(this.asText);
        this.date      = new Date();          // creation time
    }

    public Message(String text) {
        this(text.getBytes(StandardCharsets.UTF_8));
    }

    public Message(double value) {
        this(Double.toString(value)); // calls the text constructor
    }

    private static double parseDoubleSafely(String s) {
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException ex) {
            return Double.NaN;                // spec‑mandated fallback
        }
    }
}