/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.shape.Circle;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLHomeController implements Initializable {

    @FXML
    private JFXButton btnAddRoom;

    @FXML
    private JFXTreeTableView<?> tableRooms;

    @FXML
    private JFXButton btnRadar;

    @FXML
    private JFXTextField textFieldLat;

    @FXML
    private JFXTextField textFieldLng;

    @FXML
    private JFXTextField textFieldDistance;
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
