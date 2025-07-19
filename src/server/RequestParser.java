package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    /* ------------------------------------------------------------------ */
    public static RequestInfo parseRequest(BufferedReader reader) throws IOException {
        try {
            String[] firstLine   = reader.readLine().split(" ");
            String   httpCommand = firstLine[0];
            String   uri         = firstLine[1];

            /* ---------- URI ---------- */
            String[] uriParts    = uri.split("\\?");
            String[] uriSegments = parseUriSegments(uriParts[0]);      // FIX
            Map<String,String> parameters = new HashMap<>();
            if (uriParts.length > 1)
                parameters.putAll(parseParameters(uriParts[1]));

            /* ---------- headers ---------- */
            Map<String,String> headers = new HashMap<>();
            String line;
            int    contentLength = 0;
            while (!(line = reader.readLine()).isEmpty()) {
                String[] hp = line.split(": ");
                if (hp.length == 2) {
                    headers.put(hp[0], hp[1]);
                    if (hp[0].equalsIgnoreCase("Content-Length"))
                        contentLength = Integer.parseInt(hp[1]);
                }
            }

            /* ---------- body & optional filename ---------- */
            StringBuilder content = new StringBuilder();
            if (contentLength > 0) {
                // filename (multipart)
                reader.mark(1024);
                line = reader.readLine();
                if (line != null && line.contains("filename=")) {
                    parameters.put("filename", line.split("filename=")[1]);
                    while (!(line = reader.readLine()).isEmpty()) {} // skip to blank
                } else {
                    reader.reset();
                }
                // copy body exactly as original code expects
                while (!(line = reader.readLine()).isEmpty())
                    content.append(line).append('\n');
                while (reader.ready()) reader.readLine();            // drain
            }

            return new RequestInfo(httpCommand, uri, uriSegments,
                                   parameters, content.toString().getBytes());
        } catch (Exception e) {
            throw new IOException("Error parsing request: " + e.getMessage());
        }
    }

    /* ---------- helpers ---------- */

    private static String[] parseUriSegments(String path) {           // FIX
        if (path.equals("/") || path.isEmpty())
            return new String[0];
        return path.substring(1).split("/");
    }

    private static Map<String,String> parseParameters(String qs) throws IOException {
        Map<String,String> map = new HashMap<>();
        for (String pair : qs.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2)
                map.put(URLDecoder.decode(kv[0], "UTF-8"),
                        URLDecoder.decode(kv[1], "UTF-8"));
        }
        return map;
    }

    /* ---------- DTO ---------- */
    public static class RequestInfo {
        private final String             httpCommand;
        private final String             uri;
        private final String[]           uriSegments;
        private final Map<String,String> parameters;
        private final byte[]             content;

        public RequestInfo(String c,String u,String[] s,Map<String,String> p,byte[] b){
            httpCommand=c; uri=u; uriSegments=s; parameters=p; content=b; }
        public String             getHttpCommand(){ return httpCommand; }
        public String             getUri()        { return uri; }
        public String[]           getUriSegments(){ return uriSegments; }
        public Map<String,String> getParameters() { return parameters; }
        public byte[]             getContent()    { return content; }
    }
}
