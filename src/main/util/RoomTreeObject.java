/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.util;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Nonato Dias
 */
public class RoomTreeObject extends RecursiveTreeObject<RoomTreeObject>{
    public StringProperty name;

    public RoomTreeObject(String name) {
        this.name = new SimpleStringProperty(name);
    }
}
