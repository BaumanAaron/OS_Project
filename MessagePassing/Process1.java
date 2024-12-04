package OperatingSystems.DistributedMutualExlusion.MessagePassing;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Process1 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5001;
    private static final Random random = new Random();
    private static PriorityQueue<Request> queue = new PriorityQueue<>();
    private static final Map<Integer, Integer> responses = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        // Start a thread to listen for incoming messages
        new Thread(Process1::listen).start();

        // Generate 10 random request and client operations
        for (int i = 0; i < 10; i++) {
            int delay = random.nextInt(10000) + 5000;
            Thread.sleep(delay);
            int operationType = random.nextBoolean() ? 0 : 1;
            sendRequest(operationType, i);
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
                    String client = message.split(":")[4];
                    
                    if (mess_type.equals("REQUEST")) {
                        //Adds request from other processes to the queue
                        queue.add(new Request(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),Integer.parseInt(client)));
                        for (Request request : queue) {
                            System.out.println(request);
                        }
                        /* Then check whether critical section is being used
                        if (operation.equals("0")) {
                            // Client want to read
                            System.out.println("Client " +client+" from Port " + message.split(":")[2] + " wants to READ");
                        } else {
                            // Client want to write
                            System.out.println("Client " +client+" from Port " + message.split(":")[2] + " wants to WRITE");
                        }*/
                        sendResponse(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),Integer.parseInt(client));
                        
                        synchronized (lamportClock) {
                            int receivedClock = Integer.parseInt(message.split(":")[1]);
                            lamportClock.set(Math.max(lamportClock.get(), receivedClock) + 1);
                        }
                    }
                    
                    else if(mess_type.equals("RESPONSE")){
                        System.out.println("Server " + (PORT-5000) + " responded.");
                        int lamp = Integer.parseInt(lampTime);
                        responses.replace(lamp, responses.get(lamp), responses.get(lamp)+1);
                        if (responses.get(lamp) == 1/*1connections.size()-1*/){
                            if (queue.peek().getLamportClock() == lamp){
                                criticalSection(Integer.parseInt(operation),Integer.parseInt(client));
                                sendRelease(lamp,Integer.parseInt(port),Integer.parseInt(operation),Integer.parseInt(client));
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

    private static void sendRequest(int operationType, int client) {
        int timestamp = lamportClock.incrementAndGet();
        String message = "REQUEST:" + timestamp + ":" + PORT + ":" + operationType + ":" + client;
        
        //Adds this processes request to the queue
        queue.add(new Request(timestamp, PORT, operationType, client));
        for (Request request : queue) {
            System.out.println(request);
        }
        
        //add this process to hashmap w/ counter 0
        responses.put(timestamp, 0);
        
        try (Socket socket = new Socket("localhost", 5002);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            // When sending out message add to queue
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void sendResponse(int lampTime, int port, int operation, int client){
        String message = "RESPONSE:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        try (Socket socket = new Socket("localhost", port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void sendRelease(int lampTime, int port, int operation, int client){
        String message = "RELEASE:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        //Removes this processes request from the queue
        queue.poll();
        for (Request request : queue) {
            System.out.println(request);
        }
        
        try (Socket socket = new Socket("localhost", 5002);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void criticalSection(int operation, int client) throws IOException{
        String filePath = "centralDatabase/schedule.txt";
        File file = new File(filePath);
        if (operation==0){
            System.out.println("Client " + client + " read from: " + file.getName());
        }
        else{
            writeFile(file);
            System.out.println("Client " + client + " wrote to: " + file.getName());
        }
    }
    
    //we should change this a little later (not urgent)
    public static void writeFile(File file) throws IOException {
        int counter = 1;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            bw.write("Schedule for Project Demos\n\n");
            bw.write("Monday\t12/11/2024\t8:30-11:00");
            while (counter<=8){
                bw.write((random.nextInt(8)+1)+" Grant & Aaron\n");
                counter++;
            }
        }
    }

}