/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Nonato Dias
 */
public class main extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent p = FXMLLoader.load(getClass().getResource("FXMLLoginDocument.fxml"));
        //Parent p = FXMLLoader.load(getClass().getResource("FXMLHome.fxml"));
        //Parent p = FXMLLoader.load(getClass().getResource("FXMLRoom.fxml"));
        Scene scene = new Scene(p);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
