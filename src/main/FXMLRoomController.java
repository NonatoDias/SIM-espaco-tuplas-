/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.text.TextFlow;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLRoomController implements Initializable {

    @FXML
    private TextFlow msgTextFlow;

    @FXML
    private JFXTextField jfxTfMessage;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
