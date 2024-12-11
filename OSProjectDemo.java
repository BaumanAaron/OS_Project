package osprojectdemo;

import java.io.*;
import java.net.*;

/**
 *
 * @author grant
 */
public class OSProjectDemo {
    public static void main(String[] args) throws InterruptedException {
        String message;
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 1 + ":" + 5001 + ":" + 0 + ":" + "Abby";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 2 + ":" + 5001 + ":" + 0 + ":" + "Blake";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 3 + ":" + 5001 + ":" + 0 + ":" + "Christine";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(25000);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 4 + ":" + 5001 + ":" + 0 + ":" + "Dave";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 5 + ":" + 5001 + ":" + 0 + ":" + "Emma";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 6 + ":" + 5001 + ":" + 1 + ":" + "Frank";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(25000);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 7 + ":" + 5001 + ":" + 0 + ":" + "George";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 8 + ":" + 5001 + ":" + 1 + ":" + "Hailey";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 9 + ":" + 5001 + ":" + 0 + ":" + "Isaac";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(25000);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 10 + ":" + 5001 + ":" + 1 + ":" + "Josie";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 11 + ":" + 5001 + ":" + 0 + ":" + "Kurt";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 12 + ":" + 5001 + ":" + 0 + ":" + "Lily";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(25000);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 13 + ":" + 5001 + ":" + 1 + ":" + "Mike";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 14 + ":" + 5001 + ":" + 0 + ":" + "Natalie";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 15 + ":" + 5001 + ":" + 1 + ":" + "Ocean";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(25000);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 16 + ":" + 5001 + ":" + 1 + ":" + "Paige";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 17 + ":" + 5001 + ":" + 1 + ":" + "Ryan";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
        
        Thread.sleep(10);
        
        try (Socket socket = new Socket("localhost", 5006);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            
            message = "CSREQUEST:" + 18 + ":" + 5001 + ":" + 1 + ":" + "Sally";
            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            System.err.println("Couldn't reach database");
        }
    }
    
}
