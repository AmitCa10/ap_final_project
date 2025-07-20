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

/**
 * @file MyHTTPServer.java
 * @brief Multi-threaded HTTP server implementation for the Agent Graph Manager
 * @author Advanced Programming Course
 * @date 2025
 * @version 1.0
 * 
 * This class implements a multi-threaded HTTP server that can handle concurrent
 * requests using a thread pool. It supports servlet-based request routing for
 * GET, POST, and DELETE methods. The server provides the infrastructure for
 * the Agent Graph Manager web application.
 */
public class MyHTTPServer extends Thread implements HTTPServer {

    /** @brief Port number the server listens on */
    private final int port;
    
    /** @brief Map of GET request servlets indexed by URI path */
    private final ConcurrentMap<String, Servlet> servletsGet    = new ConcurrentHashMap<>();
    
    /** @brief Map of POST request servlets indexed by URI path */
    private final ConcurrentMap<String, Servlet> servletsPost   = new ConcurrentHashMap<>();
    
    /** @brief Map of DELETE request servlets indexed by URI path */
    private final ConcurrentMap<String, Servlet> servletsDelete = new ConcurrentHashMap<>();
    
    /** @brief Thread pool for handling concurrent requests */
    private final ExecutorService threadPool;
    
    /** @brief Flag indicating if the server is running */
    private volatile boolean running;
    
    /** @brief The server socket for accepting connections */
    private ServerSocket serverSocket;

    /**
     * @brief Constructor for MyHTTPServer
     * @param port The port number to listen on
     * @param nThreads The number of threads in the thread pool for handling requests
     * 
     * Creates a new HTTP server that will listen on the specified port and use
     * a thread pool of the specified size to handle concurrent requests.
     */
    public MyHTTPServer(int port, int nThreads) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(nThreads);
    }

    /* ---------------- registry ---------------- */

    /**
     * @brief Registers a servlet for a specific HTTP method and URI
     * @param httpCommand The HTTP method (GET, POST, DELETE)
     * @param uri The URI path to map the servlet to
     * @param s The servlet instance to handle requests to this URI
     * 
     * Maps a servlet to handle requests for a specific HTTP method and URI.
     * The servlet will be invoked when requests matching both the method
     * and URI are received.
     */
    public void addServlet(String httpCommand, String uri, Servlet s) {
        getRequestMap(httpCommand).put(uri, s);
    }
    
    /**
     * @brief Removes a servlet mapping for a specific HTTP method and URI
     * @param httpCommand The HTTP method (GET, POST, DELETE)
     * @param uri The URI path to remove the servlet mapping from
     */
    public void removeServlet(String httpCommand, String uri) {
        getRequestMap(httpCommand).remove(uri);
    }
    
    /**
     * @brief Gets the appropriate servlet map for the given HTTP command
     * @param cmd The HTTP command (GET, POST, DELETE)
     * @return The ConcurrentMap containing servlets for this HTTP method
     * @throws IllegalArgumentException if the HTTP command is not supported
     */
    private ConcurrentMap<String, Servlet> getRequestMap(String cmd) {
        switch (cmd.toUpperCase()) {
            case "GET":    return servletsGet;
            case "POST":   return servletsPost;
            case "DELETE": return servletsDelete;
            default: throw new IllegalArgumentException("Invalid HTTP command: " + cmd);
        }
    }

    /* ---------------- longest‑prefix matcher (restored) ---------------- */
    /**
     * @brief Finds the appropriate servlet for the given request using longest-prefix matching
     * @param ri The RequestInfo containing the parsed HTTP request details
     * @return The servlet that should handle this request, or null if none found
     * 
     * Uses longest-prefix matching to find the most specific servlet mapping
     * for the request URI. This allows for hierarchical URL structures.
     */
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
