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
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import main.tuplas.UserEntry;
import main.util.Lookup;
import net.jini.space.JavaSpace;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLHomeController implements Initializable {

    @FXML
    private StackPane loadingPane;
    
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
        loadingPane.toFront();
        loading(true);
        
        new Thread(()->{
            init();
        }).start();
    }    
    
    public void init(){
        System.out.println("Procurando pelo servico JavaSpace...");
        Lookup finder = new Lookup(JavaSpace.class);
        JavaSpace space = (JavaSpace) finder.getService();
        if (space == null) {
            System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            System.exit(-1);
        } 
        System.out.println("O servico JavaSpace foi encontrado.");
        
        Thread t = new Thread(() -> {
            while (true) {
                try {
                    UserEntry template = new UserEntry();
                    UserEntry user = (UserEntry) space.take(template, null, 60 * 1000);
                    if (user == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                        System.exit(0);
                    }
                    System.out.println("Mensagem recebida: "+ user.login);
                    
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }
    
}
