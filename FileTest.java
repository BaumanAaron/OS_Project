/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package os_project;

import java.io.*;

/**
 *
 * @author grant
 */
public class FileTest {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String filePath = "centralDatabase/schedule.txt";
        File file = new File(filePath);
        
        if (file.exists()) {
            System.out.println("File found at: " + file.getAbsolutePath());
        } else {
            System.out.println("File does not exist at: " + file.getAbsolutePath());
        }
    }
    
}
