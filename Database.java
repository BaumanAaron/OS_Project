package os_project3;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
    private static final int PORT = 5006;
    private static final Random random = new Random();
    private static final File file = new File("centralDatabase/schedule.txt");
    private static final Semaphore writerLock = new Semaphore(1, true);
    private static final Semaphore mainLock = new Semaphore(1,true);
    private static final Semaphore readerLock = new Semaphore(1,true);
    private static AtomicInteger currentReaders = new AtomicInteger(0);
    
    public static void main(String[] args) throws InterruptedException{
        new Thread(Database::listen).start();
    }
    
    private static void listen(){
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Database listening on port " + PORT);

            while (true) {
                try (Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                    String message = in.readLine();
                    
                    String mess_type = message.split(":")[0];
                    String lampTime = message.split(":")[1];
                    String port = message.split(":")[2];
                    String operation = message.split(":")[3];
                    String client = message.split(":")[4];
                   
                    broadcastRelease(Integer.parseInt(lampTime), Integer.parseInt(port), Integer.parseInt(operation), client);
                    
                    CriticalSection(message);
                }
    
            }
        }
         catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void broadcastRelease(int lampTime, int port, int operation, String client){
        String message = "RELEASE:" + lampTime + ":" + port + ":" + operation + ":" + client;
        
        //THIS NEEDS TO BE CHANGED TO BROADCAST FORMAT
        try (Socket socket = new Socket("localhost", 5001);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (Socket socket = new Socket("localhost", 5002);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println(message);
            //System.out.println(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static void CriticalSection(String message){
        String operation = message.split(":")[3];
        String client = message.split(":")[4];
        if (operation.equals("0")){
            //reader
            new Thread(() -> {
                try {
                    readFile(client);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        } else {
            // Writer
            new Thread(() -> {
                try {
                    writeFile(client);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
    private static void readFile(String client) throws InterruptedException{
        mainLock.acquire(); //wait until first in line
        
        //if there aren't already readers, if there is writerLock is already acquired
        if (currentReaders.get() == 0){
            writerLock.acquire(); //lock out writers
        }
        mainLock.release(); //once into reading, let next wait
        
        readerLock.acquire();
        currentReaders.incrementAndGet(); //add a reader
        readerLock.release();
        
        //do reading
        String targetName = client; // The person whose balance you want to check

        boolean found = false;
        int money = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into name and balance
                String[] parts = line.split(":");
                String name = parts[0];
                String balance = parts[1];
                // If we found the target person
                if (name.equals(targetName)) {
                    found = true;
                    money = Integer.parseInt(balance);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        Thread.sleep(random.nextInt(2000)+1000);//simulate reading
        
        if (found){
            System.out.println(client+", your current balance is "+money);
        }
        else{
            System.out.println(client+", you are not in the system.");
        }
        
        readerLock.acquire();
        currentReaders.decrementAndGet(); //subtract a reader
            
            //if no more readers, allow writers
        if (currentReaders.get() == 0){
            writerLock.release(); //unlock writers
        }
        
        readerLock.release();
    }
    
    private static void writeFile(String client) throws InterruptedException{
        mainLock.acquire(); //waits until it is first in line
        writerLock.acquire(); //no longer waiting
        mainLock.release(); //once writer lock acquired, let next wait
        
        //do writing
        String targetName = client; // The person whose balance you want to update or add
        int newBalance = random.nextInt(100000); // New balance to set

        List<String> lines = new ArrayList<>();
        boolean found = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Split the line into name and balance
                String[] parts = line.split(":");
                String name = parts[0];
                String balance = parts[1];
                // If we found the target person, update their balance
                if (name.equals(targetName)) {
                    balance = String.valueOf(newBalance); // Update balance
                    found = true;
                }

                // Add the updated line to the list
                lines.add(name + ":" + balance);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread.sleep(random.nextInt(4000)+1000);//simulate writing
        
        if (found) { //if person was already in system
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (String line : lines) {
                    writer.write(line);
                    writer.newLine();
                }
                System.out.println(targetName + ", your balance has been updated to " + newBalance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //if person wasn't already in system, add new entry
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(targetName + ":" + newBalance);
                writer.newLine();
                System.out.println(targetName + " not found. Adding new entry with balance " + newBalance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        writerLock.release();
        
    }
}
        