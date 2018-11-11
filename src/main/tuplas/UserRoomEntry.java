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
public class UserRoomEntry implements Entry{
    public String userLogin;
    public String roomName;
    
    public UserRoomEntry(){};

    public void setAttributes(String userLogin, String roomName) {
        this.userLogin = userLogin;
        this.roomName = roomName;
    }
}
