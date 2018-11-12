/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nonato Dias
 */
public class UserStorage {
    private String filepath = "./users.txt";
    private String divider = "&amp;";
    
    public void init(){
        File f = new File(filepath);
        if(!f.exists()){
            addUser("admin", "admin");
        }
    }
    
    public void addUser(String login, String pass){
        FileWriter f;
        try {
            f = new FileWriter(filepath, true);
            BufferedWriter bufferedWriter = new BufferedWriter(f);
            bufferedWriter.append(login.trim()+ divider + pass.trim());
            bufferedWriter.newLine();
            bufferedWriter.close();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean hasUser(String login, String pass){
        String line = null;
        boolean hasIt = false;
        try {
            FileReader fileReader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null && hasIt == false) {
                String[] parts = line.split(divider);
                if(parts[0].equals(login) && parts[1].equals(pass)){
                    hasIt = true;
                }
            }   
            bufferedReader.close();    
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return hasIt;
    }
    
    public boolean hasUserLogin(String login){
        String line = null;
        boolean hasIt = false;
        try {
            FileReader fileReader = new FileReader(filepath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null && hasIt == false) {
                String[] parts = line.split(divider);
                if(parts[0].equals(login)){
                    hasIt = true;
                }
            }   
            bufferedReader.close();    
        }catch(Exception ex) {
            ex.printStackTrace();
        }
        return hasIt;
    }
}
