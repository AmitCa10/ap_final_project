package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.*;
import servlet.Servlet;

public class MyHTTPServer extends Thread implements HTTPServer {

    private final int port;
    private final ConcurrentMap<String, Servlet> servletsGet    = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Servlet> servletsPost   = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Servlet> servletsDelete = new ConcurrentHashMap<>();
    private final ExecutorService threadPool;
    private volatile boolean running;
    private ServerSocket serverSocket;

    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    /* ---------------- registry ---------------- */

    public void addServlet(String httpCommand, String uri, Servlet s) {
        getRequestMap(httpCommand).put(uri, s);
    }
    public void removeServlet(String httpCommand, String uri) {
        getRequestMap(httpCommand).remove(uri);
    }
    private ConcurrentMap<String, Servlet> getRequestMap(String cmd) {
        switch (cmd.toUpperCase()) {
            case "GET":    return servletsGet;
            case "POST":   return servletsPost;
            case "DELETE": return servletsDelete;
            default: throw new IllegalArgumentException("Invalid HTTP command: " + cmd);
        }
    }

    /* ---------------- longest‑prefix matcher (restored) ---------------- */
    private Servlet getServlet(RequestParser.RequestInfo ri) {
        String[] segs = ri.getUriSegments();
        ConcurrentMap<String, Servlet> map = getRequestMap(ri.getHttpCommand());

        // iterate from longest prefix to shortest (including root "/")
        for (int i = segs.length; i >= 0; i--) {
            String prefix = "/" + String.join("/", Arrays.copyOf(segs, i));
            Servlet s = map.get(prefix.isEmpty() ? "/" : prefix);
            if (s != null) return s;
        }
        return null;
    }

    /* ---------------- main loop ---------------- */

    @Override public void run() {
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            running = true;

            while (running) {
                try {
                    Socket client = serverSocket.accept();
                    threadPool.submit(() -> handleClient(client));
                } catch (SocketTimeoutException ignored) {}
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();               // ensure resources released
        }
    }

    private void handleClient(Socket clientSocket) {
        try (BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream   output = clientSocket.getOutputStream()) {

            RequestParser.RequestInfo ri = RequestParser.parseRequest(input);
            Servlet servlet = getServlet(ri);

            if (servlet != null) {
                servlet.handle(ri, output);
            } else {                                  // 404
                String body = "404 Not Found";
                output.write(("HTTP/1.1 404 Not Found\r\n" +
                              "Content-Type: text/plain\r\n" +
                              "Content-Length: " + body.length() + "\r\n\r\n" +
                              body).getBytes());
                output.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------------- graceful shutdown (restored close()) ------------- */
    @Override
    public void close() {
        running = false;
        try { if (serverSocket != null) serverSocket.close(); }
        catch (IOException ignored) {}

        threadPool.shutdownNow();
        servletsGet.values().forEach(this::safeClose);
        servletsPost.values().forEach(this::safeClose);
        servletsDelete.values().forEach(this::safeClose);
    }
    private void safeClose(Servlet s) { try { s.close(); } catch (IOException ignored) {} }
}
