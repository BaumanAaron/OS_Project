package os_project;

import java.io.*;
import java.net.*;

public class Client1B {
    private static final String SERVER_HOST = "127.0.0.1"; // Server IP (localhost for testing)
    private static final int SERVER_PORT = 5001; // Server port

    public static void main(String[] args) throws InterruptedException {
        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Client1B connected to Server1");

            // Send a message to the server
            out.println("Client1B: Aloha Server1!");
            
            //this is for testing
            Thread.sleep(20000);
            out.println("Client1B: write");
            
            // Listen for server responses
            String response;
            while ((response = in.readLine()) != null) {
                System.out.println("Server1 Response: " + response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}