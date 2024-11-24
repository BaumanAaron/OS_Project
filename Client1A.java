package os_project;

import java.io.*;
import java.net.*;

public class Client1A {
    private static final String SERVER_HOST = "127.0.0.1"; // Server IP (localhost for testing)
    private static final int SERVER_PORT = 5001; // Server port

    public static void main(String[] args) throws InterruptedException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Client1A connected to Server1");

            // Send a message to the server
            out.println("Client1A: Aloha Server1!");
            
            //this is for testing
            Thread.sleep(10000);
            out.println("Client1A: read");
            
            // Listen for server responses (this will be changed but to know if can enter CS)
            String response;
            while ((response = in.readLine()) != null) {
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}