/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.tuplas;

import net.jini.core.entry.Entry;

/**
 *
 * @author Nonato Dias
 */
public class UserEntry implements Entry{
    public String login;
    public String password;
    
    public UserEntry(){};
    
    public void setAttributes(String login, String pass){
        this.login = login;
        this.password = pass;
    }
}
