package OperatingSystems.DistributedMutualExlusion;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server4 {
    private static final int SERVER_ID = 4;
    private static final int PORT = 5004;
    private static final String SERVER_LIST = "127.0.0.1:5001,127.0.0.1:5002,127.0.0.1:5003,127.0.0.1:5004,127.0.0.1:5005";

    private static final Map<Integer, Socket> connections = new HashMap<>();
    private static final List<ServerInfo> servers = new ArrayList<>();

    public static void main(String[] args) {
        parseServerList(SERVER_LIST);

        // Start the server and connect to others
        new Thread(Server4::startServer).start();
        connectToServers();

        // Start command interface for user input
        handleCommands();
    }

    private static void parseServerList(String serverList) {
        String[] serverEntries = serverList.split(",");
        for (int i = 0; i < serverEntries.length; i++) {
            String[] parts = serverEntries[i].split(":");
            servers.add(new ServerInfo(i, parts[0], Integer.parseInt(parts[1])));
        }
    }

    private static void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server " + SERVER_ID + " listening on port " + PORT);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> handleIncomingConnection(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleIncomingConnection(Socket clientSocket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Server " + SERVER_ID + " received: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void connectToServers() {
        for (ServerInfo server : servers) {
            if (server.id == SERVER_ID) continue; // Skip self
            while (true) {
                try {
                    Socket socket = new Socket(server.host, server.port);
                    connections.put(server.id, socket);
                    System.out.println("Server " + SERVER_ID + " connected to Server " + server.id);
                    break;
                } catch (IOException e) {
                    System.out.println("Server " + SERVER_ID + " retrying connection to Server " + server.id);
                    try {
                        Thread.sleep(1000); // Retry after 1 second
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
            }
        }
    }

    private static void broadcastMessage(String message) {
        for (Map.Entry<Integer, Socket> entry : connections.entrySet()) {
            try {
                PrintWriter out = new PrintWriter(entry.getValue().getOutputStream(), true);
                out.println(message);
            } catch (IOException e) {
                System.err.println("Error sending message to Server " + entry.getKey());
            }
        }
    }

    private static void handleCommands() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Server " + SERVER_ID + " Command Interface Ready.");
        System.out.println("Type `SHOW_CONNECTIONS` to list connected servers, or `EXIT` to quit.");

        while (true) {
            System.out.print("> ");
            String command = scanner.nextLine().trim();

            if ("SHOW_CONNECTIONS".equalsIgnoreCase(command)) {
                showConnections();
            } else if ("EXIT".equalsIgnoreCase(command)) {
                System.out.println("Shutting down server...");
                break;
            } else {
                System.out.println("Unknown command. Try `SHOW_CONNECTIONS` or `EXIT`.");
            }
        }

        scanner.close();
        System.exit(0);
    }

    private static void showConnections() {
        System.out.println("Connected servers:");
        if (connections.isEmpty()) {
            System.out.println("No active connections.");
        } else {
            for (Map.Entry<Integer, Socket> entry : connections.entrySet()) {
                System.out.println("Server ID: " + entry.getKey() + ", Address: " + entry.getValue().getRemoteSocketAddress());
            }
        }
    }

    static class ServerInfo {
        int id;
        String host;
        int port;

        public ServerInfo(int id, String host, int port) {
            this.id = id;
            this.host = host;
            this.port = port;
        }
    }
}

