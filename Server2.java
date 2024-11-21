package OperatingSystems.DistributedMutualExlusion;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server2 {
    private static final int SERVER_ID = 2; // Unique ID for this server
    private static final int PORT = 5002; // Port number for this server
    private static final String SERVER_LIST = "127.0.0.1:5001,127.0.0.1:5002,127.0.0.1:5003,127.0.0.1:5004,127.0.0.1:5005";
    // List of all servers in the network, including their IP addresses and ports

    private static final Map<Integer, Socket> connections = new HashMap<>(); // Map of connected servers
    private static final List<ServerInfo> servers = new ArrayList<>(); // List of all server details

    public static void main(String[] args) {
        parseServerList(SERVER_LIST); // Parse the server list and populate the `servers` list

        // Start the server to listen for incoming connections
        new Thread(Server2::startServer).start();

        // Connect to other servers in the network
        connectToServers();

        // Start the command interface for user input
        handleCommands();
    }

    /**
     * Parses the server list string and initializes the server information.
     * @param serverList Comma-separated string of server addresses and ports
     */
    private static void parseServerList(String serverList) {
        String[] serverEntries = serverList.split(","); // Split the list into individual server entries
        for (int i = 0; i < serverEntries.length; i++) {
            String[] parts = serverEntries[i].split(":"); // Split each entry into host and port
            servers.add(new ServerInfo(i, parts[0], Integer.parseInt(parts[1]))); // Add server info to the list
        }
    }

    /**
     * Starts the server to listen for incoming client connections.
     */
    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) { // Bind to the specified port
            System.out.println("Server " + SERVER_ID + " listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept an incoming connection
                // Handle the connection in a new thread
                new Thread(() -> handleIncomingConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles incoming connections from other servers or clients.
     * @param clientSocket Socket for the incoming connection
     */
    private static void handleIncomingConnection(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            // Read messages from the client and print them
            while ((message = in.readLine()) != null) {
                System.out.println("Server " + SERVER_ID + " received: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Connects to other servers in the network.
     */
    private static void connectToServers() {
        for (ServerInfo server : servers) {
            if (server.id == SERVER_ID) continue; // Skip connecting to self

            while (true) {
                try {
                    Socket socket = new Socket(server.host, server.port); // Connect to the server
                    connections.put(server.id, socket); // Store the connection
                    System.out.println("Server " + SERVER_ID + " connected to Server " + server.id);
                    break; // Exit loop on successful connection
                } catch (IOException e) {
                    // Retry the connection after 1 second
                    System.out.println("Server " + SERVER_ID + " retrying connection to Server " + server.id);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Broadcasts a message to all connected servers.
     * @param message The message to broadcast
     */
    private static void broadcastMessage(String message) {
        for (Map.Entry<Integer, Socket> entry : connections.entrySet()) {
            try {
                PrintWriter out = new PrintWriter(entry.getValue().getOutputStream(), true); // Get output stream
                out.println(message); // Send the message
            } catch (IOException e) {
                System.err.println("Error sending message to Server " + entry.getKey());
            }
        }
    }

    /**
     * Handles user commands from the terminal.
     */
    private static void handleCommands() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Server " + SERVER_ID + " Command Interface Ready.");
        System.out.println("Type `SHOW_CONNECTIONS` to list connected servers, or `EXIT` to quit.");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim(); // Read user input

            if ("SHOW_CONNECTIONS".equalsIgnoreCase(command)) {
                showConnections(); // Display connected servers
            } else if ("EXIT".equalsIgnoreCase(command)) {
                System.out.println("Shutting down server...");
                break; // Exit the loop and terminate the program
            } else {
                System.out.println("Unknown command. Try `SHOW_CONNECTIONS` or `EXIT`.");
            }
        }

        scanner.close(); // Close the scanner
        System.exit(0); // Terminate the program
    }

    /**
     * Displays a list of currently connected servers.
     */
    private static void showConnections() {
        System.out.println("Connected servers:");
        if (connections.isEmpty()) {
            System.out.println("No active connections."); // No servers connected
        } else {
            for (Map.Entry<Integer, Socket> entry : connections.entrySet()) {
                // Display each connected server's ID and address
                System.out.println("Server ID: " + entry.getKey() + ", Address: " + entry.getValue().getRemoteSocketAddress());
            }
        }
    }

    /**
     * Represents server information such as ID, host, and port.
     */
    static class ServerInfo {
        int id; // Server ID
        String host; // Server host address
        int port; // Server port

        public ServerInfo(int id, String host, int port) {
            this.id = id;
            this.host = host;
            this.port = port;
        }
    }
}
