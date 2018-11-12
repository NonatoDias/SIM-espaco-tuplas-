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
public class CounterEntry implements Entry{
    public Integer lastIdUser;
    public Integer lastIdRoom;
    
    public CounterEntry(){};
    
    public Integer increaseUser(){
        if(lastIdUser != null){
            lastIdUser = lastIdUser + 1;
            return lastIdUser;
        }
        return null;
    }
    
    public Integer increaseRoom(){
        if(lastIdRoom != null){
            lastIdRoom = lastIdRoom + 1;
            return lastIdRoom;
        }
        return null;
    }
}
