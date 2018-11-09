/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import main.tuplas.UserEntry;
import net.jini.space.JavaSpace;
import main.util.Lookup;
import net.jini.core.transaction.TransactionException;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLLoginDocumentController implements Initializable {
    
    @FXML
    private StackPane loadingPane;
    
    @FXML
    private JFXButton btnLogin;

    @FXML
    private StackPane dialogStackPane;

    @FXML
    private JFXTextField textFieldLogin;

    @FXML
    private JFXButton btnSignIn;

    @FXML
    private JFXPasswordField textFieldPass;

    JavaSpace javaSpace;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnLogin.setOnAction((e)->{
            openHome();
        });
        
        loadingPane.toFront();
        loading(true);
        
        System.out.println("Procurando pelo servico JavaSpace...");
        /*Platform.runLater(()->{
            
        });*/
        new Thread(()->{
            Lookup finder = new Lookup(JavaSpace.class);
            this.javaSpace = (JavaSpace) finder.getService();
            if (this.javaSpace == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            } 
            System.out.println("O servico JavaSpace foi encontrado.");

            UserEntry user = new UserEntry();
            user.setAttributes("admin", "admin");

            try {
                this.javaSpace.write(user, null, Long.MAX_VALUE);
                System.out.println("User "+ user.login +" adicionado");
                
                loading(false);

            } catch (Exception e) {
                e.printStackTrace();
            }
            
        }).start();
    }    
    
    public void openHome(){
        Parent home = null;
        try {
            home = FXMLLoader.load(getClass().getResource("FXMLHome.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(home);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }
    
}
