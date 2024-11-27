package OperatingSystems.DistributedMutualExlusion.MessagePassing;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.Random;

public class Process1 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5001;
    private static final Random random = new Random();
    private static PriorityQueue<Request> queue = new PriorityQueue<>();

    public static void main(String[] args) throws InterruptedException {
        // Start a thread to listen for incoming messages
        new Thread(Process1::listen).start();

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
                    
                    String mess_type = message.split(":")[0];
                    String lampTime = message.split(":")[1];
                    String port = message.split(":")[2];
                    String operation = message.split(":")[3];

                    //Adds request from other processes to the queue
                    queue.add(new Request(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation)));
                    for (Request request : queue) {
                        System.out.println(request);
                    }
                    
                    if (mess_type.equals("REQUEST")) {
                        // Logic to add to a queue
                        // Then check whether critical section is being used
                        if (operation == "0") {
                            // Client want to read
                            // Do
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
        
        //Adds this processes request to the queue
        queue.add(new Request(timestamp, PORT, operationType));
        for (Request request : queue) {
            System.out.println(request);
        }
        
        try (Socket socket = new Socket("localhost", 5002);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            // When sending out message add to queue
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
