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
    public Float lat;
    public Float lng;
    
    public UserEntry(){};
    
    public void setAttributes(String login, Float lat, Float lng) {
        this.login = login;
        this.lat = lat;
        this.lng = lng;
    }
}
