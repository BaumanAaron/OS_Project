package OperatingSystems.DistributedMutualExlusion;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server2 {
    private static final int SERVER_ID = 2; // Unique ID for this server
    private static final int PORT = 5002; // Port number for this server
    private static final String SERVER_LIST = "127.0.0.1:5001,127.0.0.1:5002,127.0.0.1:5003,127.0.0.1:5004,127.0.0.1:5005";

    private static final Map<Integer, Socket> connections = new HashMap<>();
    private static final List<ServerInfo> servers = new ArrayList<>();

    // Lamport Clock
    private static int lamportClock = 0;

    public static void main(String[] args) {
        parseServerList(SERVER_LIST);

        startConnectionCheckingThread();

        new Thread(Server2::startServer).start();

        connectToServers();

        // Start 10 simulated clients
        simulateClients();

        handleCommands();
    }

    private static void parseServerList(String serverList) {
        String[] serverEntries = serverList.split(",");
        for (int i = 0; i < serverEntries.length; i++) {
            String[] parts = serverEntries[i].split(":");
            servers.add(new ServerInfo(i + 1, parts[0], Integer.parseInt(parts[1])));
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
                synchronized (Server1.class) {
                    String[] parts = message.split(":", 2);
                    int receivedClock = Integer.parseInt(parts[0]);
                    String payload = parts[1];

                    lamportClock = Math.max(lamportClock, receivedClock) + 1;

                    System.out.println("Server " + SERVER_ID + " received: \"" + payload + "\" with clock " + receivedClock);
                    System.out.println("Updated Lamport Clock: " + lamportClock);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void startConnectionCheckingThread() {
        new Thread(() -> {
            while (true) {
                connectToServers();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void connectToServers() {
        for (ServerInfo server : servers) {
            if (server.id == SERVER_ID) continue;

            if (!connections.containsKey(server.id)) {
                try {
                    Socket socket = new Socket(server.host, server.port);
                    connections.put(server.id, socket);
                    System.out.println("Server " + SERVER_ID + " connected to Server " + server.id);
                } catch (IOException e) {
                    // Uncomment if you want to see connection errors
                    // System.out.println("Server " + SERVER_ID + " could not connect to Server " + server.id);
                }
            }
        }
    }

    private static void broadcastMessage(String message) {
        synchronized (Server1.class) {
            lamportClock++;

            String timestampedMessage = lamportClock + ":" + message;

            for (Map.Entry<Integer, Socket> entry : connections.entrySet()) {
                try {
                    PrintWriter out = new PrintWriter(entry.getValue().getOutputStream(), true);
                    out.println(timestampedMessage);
                } catch (IOException e) {
                    System.err.println("Error sending message to Server " + entry.getKey());
                }
            }

            System.out.println("Broadcasted message: \"" + message + "\" with clock " + lamportClock);
        }
    }

    private static void handleCommands() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Server " + SERVER_ID + " Command Interface Ready.");
        System.out.println("Type `SHOW_CONNECTIONS` to list connected servers, `EXIT` to quit.");

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

    /**
     * Simulates 10 clients performing read/write actions.
     */
    private static void simulateClients() {
        Random random = new Random();
        for (int i = 1; i <= 10; i++) {
            int clientId = i;
            new Thread(() -> {
                while (true) {
                    int action = random.nextInt(2); // 0 for read, 1 for write
                    String actionType = action == 0 ? "READ" : "WRITE";

                    synchronized (Server1.class) {
                        lamportClock++; // Increment clock before action
                    }

                    String message = "Client " + clientId + " performs " + actionType + " operation.";
                    broadcastMessage(message);

                    try {
                        Thread.sleep(random.nextInt(3000) + 1000); // Random delay between actions
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
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
