package osprojectdemo;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Database {
    private static final int PORT = 5006;
    private static final Random random = new Random();
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
                    System.out.println(message);
                    CriticalSection(message);
                }
    
            }
        }
         catch (IOException e) {
             System.err.println("Database disconnected");
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
            //writer
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
        //this lock will ensure no readers jump writers in line
        mainLock.acquire(); //wait until first in line
        
        //if there aren't already readers (if there is writerLock is already acquired)
        if (currentReaders.get() == 0){
            writerLock.acquire(); //lock out writers
        }
        mainLock.release(); //once into reading, let next wait
        
        readerLock.acquire(); //this lock ensures currentReaders only gets updated once at a time
        currentReaders.incrementAndGet(); //add a reader
        readerLock.release();
        
        //do reading
        System.out.println(client+" started reading.");
        
        Thread.sleep(3000);//simulate reading
        
        System.out.println(client+" finished reading.");
        
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
        writerLock.acquire(); //wait until can start writing
        mainLock.release(); //once writer lock acquired, let next wait
        
        //do writing
        System.out.println(client+" started writing.");
        
        Thread.sleep(5000);//simulate writing
        
        System.out.println(client+" finished writing.");
        
        writerLock.release();
        
    }
}
