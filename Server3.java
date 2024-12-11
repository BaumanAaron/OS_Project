package os_project4;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Server3 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5003;
    private static final Random random = new Random();
    private static PriorityQueue<Request> queue = new PriorityQueue<>();
    private static final Map<Integer, Integer> responses = new HashMap<>();
    private static final Map<Integer, String> clients = new HashMap<>();

    public static void main(String[] args) throws InterruptedException {
        //create client names
        clientNames();
        
        // Start a thread to listen for incoming messages
        new Thread(Server3::listen).start();

        // Generate 10 random request and client operations
        for (int i = 0; i < 10; i++) {
            int delay = random.nextInt(1000) + 5000; //every 5-6 seconds
            Thread.sleep(delay);
            int operationType = random.nextBoolean() ? 0 : 1;
            sendRequest(operationType, clients.get(i));
        }
    }

    private static void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server 3 listening on port " + PORT);

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
                        //Adds request from other servers to the queue
                        queue.add(new Request(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),client));
                        /*for (Request request : queue) {
                            System.out.println(request);
                        }*/
                        
                        sendResponse(Integer.parseInt(lampTime),Integer.parseInt(port),Integer.parseInt(operation),client);
                        
                        synchronized (lamportClock) {
                            int receivedClock = Integer.parseInt(message.split(":")[1]);
                            lamportClock.set(Math.max(lamportClock.get(), receivedClock) + 1);
                        }
                    }
                    
                    else if(mess_type.equals("RESPONSE")){
                        int lamp = Integer.parseInt(lampTime);
                        responses.replace(lamp, responses.get(lamp), responses.get(lamp)+1); //increment response count
                        
                        System.out.println("Response #"+responses.get(lamp)); //this was changed to show current # of responses
                        
                        if (responses.get(lamp) == 4){ //once all responses are received
                            if ((queue.peek().getLamportClock() == lamp) && queue.peek().getPort() == PORT){ //if at top of queue
                                sendCSRequest(lamp, PORT, Integer.parseInt(operation), client);
                            }
                        }
                    }
                    
                    else if(mess_type.equals("RELEASE")){
                        int releasePort = Integer.parseInt(port);
                        System.out.println("Server " + (releasePort-5000) + " released.");
                        if (releasePort == PORT){
                            responses.remove(Integer.parseInt(lampTime));//need to change this to check w/ releasePort first
                        }
                        queue.poll();
                        
                        //THIS WAS ADDED
                        if (!queue.isEmpty()){
                            int a = queue.peek().getPort();
                            if (a == PORT){
                                int b = queue.peek().getLamportClock();
                                if (responses.get(b) == 4){
                                    sendCSRequest(b, PORT, queue.peek().getOperation(), queue.peek().getClient());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server 3 disconnected");
        }
    }

    private static void sendRequest(int operationType, String client) {
        int timestamp = lamportClock.incrementAndGet();
        String message = "REQUEST:" + timestamp + ":" + PORT + ":" + operationType + ":" + client;
        
        //Adds this servers request to the queue
        queue.add(new Request(timestamp, PORT, operationType, client));
        /*for (Request request : queue) {
            System.out.println(request);
        }*/
        
        //add this process to hashmap w/ counter 0
        responses.put(timestamp, 0);
        
        //print request message
        System.out.println(message);
        
        //send to server 1
        try (Socket socket = new Socket("localhost", 5001);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach server 1");
        }
        
        //send to server 2
        try (Socket socket = new Socket("localhost", 5002);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach server 2");
        }
        
        //send to server 4
        try (Socket socket = new Socket("localhost", 5004);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach server 4");
        }
        
        //send to server 5
        try (Socket socket = new Socket("localhost", 5005);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach server 5");
        }
    }
    
    private static void sendResponse(int lampTime, int port, int operation, String client){
        String message = "RESPONSE:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        //send back to server that sent request
        try (Socket socket = new Socket("localhost", port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach server "+(port-5000));
        }
    }
    //this was changed from release
    private static void sendCSRequest(int lampTime, int port, int operation, String client){
        String message = "CSREQUEST:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        //send to database
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
    }
    
    private static void clientNames(){
        clients.put(0, "Brett");
        clients.put(1, "Kathy");
        clients.put(2, "Robin");
        clients.put(3, "Greg");
        clients.put(4, "Laddie");
        clients.put(5, "Chris");
        clients.put(6, "Doug");
        clients.put(7, "Tera");
        clients.put(8, "Susie");
        clients.put(9, "Zach");
    }
}
