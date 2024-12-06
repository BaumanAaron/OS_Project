package os_project3;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Process2 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5002;
    private static final Random random = new Random();
    private static PriorityQueue<Request> queue = new PriorityQueue<>();
    private static final Map<Integer, Integer> responses = new HashMap<>();
    private static final Map<Integer, String> clients = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        //create client names
        clientNames();

        // Start a thread to listen for incoming messages
        new Thread(Process2::listen).start();

        // Generate 10 random request and client operations
        for (int i = 0; i < 10; i++) {
            int delay = random.nextInt(1000) + 1000;
            Thread.sleep(delay);
            int operationType = random.nextBoolean() ? 0 : 1;
            sendRequest(operationType, clients.get(i));
        }
    }

    private static void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Process 2 listening on port " + PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String message = in.readLine();
                    
                    String mess_type = message.split(":")[0];
                    String lampTime = message.split(":")[1];
                    String port = message.split(":")[2];
                    String operation = message.split(":")[3];
                    String client = message.split(":")[4];
                    
                    if (mess_type.equals("REQUEST")) {
                        //Adds request from other processes to the queue
                        queue.add(new Request(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),client));
                        for (Request request : queue) {
                            System.out.println(request);
                        }
                        
                        sendResponse(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),client);
                        
                        synchronized (lamportClock) {
                            int receivedClock = Integer.parseInt(message.split(":")[1]);
                            lamportClock.set(Math.max(lamportClock.get(), receivedClock) + 1);
                        }
                    }
                    
                    else if(mess_type.equals("RESPONSE")){
                        System.out.println("Server " + (Integer.parseInt(port)-5000) + " responded."); //the server # is incorrect but not important
                        int lamp = Integer.parseInt(lampTime);
                        responses.replace(lamp, responses.get(lamp), responses.get(lamp)+1);
                        if (responses.get(lamp) == 1/*1connections.size()-1*/){
                            if (queue.peek().getLamportClock() == lamp){
                                sendCSRequest(lamp, PORT, Integer.parseInt(operation), client);
                            }
                        }
                    }
                    
                    else if(mess_type.equals("RELEASE")){
                        System.out.println("Server " + (Integer.parseInt(port)-5000) + " released.");
                        responses.remove(Integer.parseInt(lampTime));
                        queue.poll();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void sendRequest(int operationType, String client) {
        int timestamp = lamportClock.incrementAndGet();
        String message = "REQUEST:" + timestamp + ":" + PORT + ":" + operationType + ":" + client;
        
        //Adds this processes request to the queue
        queue.add(new Request(timestamp, PORT, operationType, client));
        for (Request request : queue) {
            System.out.println(request);
        }
        
        //add this process to hashmap w/ counter 0
        responses.put(timestamp, 0);
        
        try (Socket socket = new Socket("localhost", 5001);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            // When sending out message add to queue
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void sendResponse(int lampTime, int port, int operation, String client){
        String message = "RESPONSE:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        try (Socket socket = new Socket("localhost", port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //this was changed from release
    private static void sendCSRequest(int lampTime, int port, int operation, String client){
        String message = "CSREQUEST:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void clientNames(){
        clients.put(0, "Henry");
        clients.put(1, "Max");
        clients.put(2, "Abby");
        clients.put(3, "Tatum");
        clients.put(4, "Alexis");
        clients.put(5, "Conner");
        clients.put(6, "Korbyn");
        clients.put(7, "Kate");
        clients.put(8, "Isaac");
        clients.put(9, "Joe");
    }
}
