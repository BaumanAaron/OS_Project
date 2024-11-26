package OperatingSystems.MessagePassing;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;

public class Process2 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5002;
    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException {
        // Start a thread to listen for incoming messages
        new Thread(Process2::listen).start();

        // Generate 10 random request and client operations
        for (int i = 0; i < 10; i++) {
            int delay = random.nextInt(10000) + 5000;
            Thread.sleep(delay);
            int operationType = random.nextBoolean() ? 0 : 1;
            sendRequest(operationType);
        }
    }

    private static void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Process 1 listening on port " + PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String message = in.readLine();
                    // System.out.println("RECIEVED: "+message);
                    // System.out.println(message.split(":")[0]+" "+message.split(":")[1]+"
                    // "+message.split(":")[2]+" "+message.split(":")[3]);
                    if (message.split(":")[0].equals("REQUEST")) {
                        // Logic to add to a queue
                        // Then check whether critical section is being used
                        if (message.split(":")[3] == "0") {
                            // Client want to read
                            System.out.println("Client from Port " + message.split(":")[2] + " wants to READ");
                        } else {
                            // Client want to write
                            System.out.println("Client from Port " + message.split(":")[2] + " wants to WRITE");
                        }
                    }
                    synchronized (lamportClock) {
                        int receivedClock = Integer.parseInt(message.split(":")[1]);
                        lamportClock.set(Math.max(lamportClock.get(), receivedClock) + 1);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(int operationType) {
        int timestamp = lamportClock.incrementAndGet();
        String message = "REQUEST:" + timestamp + ":" + PORT + ":" + operationType;

        try (Socket socket = new Socket("localhost", 5001);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            // When sending out message add to queue
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
