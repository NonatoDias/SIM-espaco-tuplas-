/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.tuplas;

/**
 *
 * @author Nonato Dias
 */
public class CounterEntry {
    public Integer lastIdUser;
    public Integer lastIdRoom;
    
    public CounterEntry(){};
    
    public void increaseUser(){
        if(lastIdUser != null){
            lastIdUser++;
        }
    }
}
