package operatingsystemsproject;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;

public class Server4 {
    private static AtomicInteger lamportClock = new AtomicInteger(0);
    private static final int PORT = 5004;
    private static final Random random = new Random();
    private static PriorityQueue<Request> queue = new PriorityQueue<>();
    private static final Map<Integer, Integer> responses = new HashMap<>();
    private static final Map<Integer, String> clients = new HashMap<>();
    private static final ArrayList<Integer> connectedServers = new ArrayList<>(Arrays.asList(5001, 5002, 5003, 5005));

    public static void main(String[] args) throws InterruptedException {
        //create client names
        clientNames();
        
        // Start a thread to listen for incoming messages
        new Thread(Server4::listen).start();

        // Generate 10 random request and client operations
        for (int i = 0; i < 10; i++) {
            int delay = random.nextInt(1000) + 3000; //every sends request 3-4 seconds
            Thread.sleep(delay);
            int operationType = random.nextBoolean() ? 0 : 1;
            sendRequest(operationType, clients.get(i));
        }
    }

    private static void listen() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server 4 listening on port " + PORT);

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
                        
                        if (responses.get(lamp) == connectedServers.size()){ //once all responses are received
                            if ((queue.peek().getLamportClock() == lamp) && queue.peek().getPort() == PORT){ //if at top of queue
                                sendCSRequest(lamp, PORT, Integer.parseInt(operation), client);
                            }
                        }
                    }
                    
                    else if(mess_type.equals("RELEASE")){
                        int releasePort = Integer.parseInt(port);
                        System.out.println("Server " + (releasePort-5000) + " released.");
                        if (releasePort == PORT){ //this is for a tie in Lamport Clock (ensures top of queue belongs to this port)
                            responses.remove(Integer.parseInt(lampTime)); //since we know it is this port, remove the responses from hashmap
                        }
                        queue.poll();
                        
                        //after top of queue is realease, check if there are more in queue
                        if (!queue.isEmpty()){
                            int a = queue.peek().getPort();
                            if (a == PORT){ //if the next one is this port
                                int b = queue.peek().getLamportClock();
                                if (responses.get(b) == connectedServers.size()){ //check if the top has enough responses
                                    sendCSRequest(b, PORT, queue.peek().getOperation(), queue.peek().getClient());
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Server 4 disconnected");
        }
    }

    private static void sendRequest(int operationType, String client) {
        int timestamp = lamportClock.incrementAndGet();
        String message = "REQUEST:" + timestamp + ":" + PORT + ":" + operationType + ":" + client;
        
        //Adds this servers request to the queue
        queue.add(new Request(timestamp, PORT, operationType, client));
        
        //add this process to hashmap w/ counter 0
        responses.put(timestamp, 0);
        
        //print request message
        System.out.println(message);
        
        //broadcast message
        for (int i=0;i<connectedServers.size();i++){
            
            int cPort = connectedServers.get(i);
            try (Socket socket = new Socket("localhost", cPort);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                
                out.println(message);
            } catch (IOException e) {
                System.err.println("Couldn't reach server "+(cPort-5000));
                connectedServers.remove(i); //remove disconnected server
                i--;//since one less server now
                
                //remove all disconnected servers pending requests (so others can go on)
                while (queue.peek().getPort() == cPort){
                    queue.poll();
                }
            }
        }
        
        //if no other servers connected
        if (connectedServers.size() == 0){
            sendCSRequest(timestamp, PORT, operationType, client);
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
        clients.put(0, "Kylie");
        clients.put(1, "Sydnee");
        clients.put(2, "Tristan");
        clients.put(3, "Nick");
        clients.put(4, "Ryan");
        clients.put(5, "Maya");
        clients.put(6, "Megan");
        clients.put(7, "Saya");
        clients.put(8, "Braxton");
        clients.put(9, "Cooper");
    }
}
