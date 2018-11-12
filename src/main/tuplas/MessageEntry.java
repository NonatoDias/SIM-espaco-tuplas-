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
public class MessageEntry implements Entry{
    public String from;
    public String to;
    public String content;
    
    public MessageEntry(){};

    public void setAttributes(String from, String to, String content) {
        this.from = from;
        this.to = to;
        this.content = content;
    }
}
