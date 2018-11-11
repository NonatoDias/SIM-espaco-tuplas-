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
    private StackPane dialogPane;

    @FXML
    private JFXTextField textFieldLogin;

    @FXML
    private JFXButton btnSignUp;

    @FXML
    private JFXPasswordField textFieldPass;
    
    @FXML
    private JFXTextField textFieldLat;

    @FXML
    private JFXTextField textFieldLng;

    Dialog alert;
    UserStorage userStorage = new UserStorage();
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clearFields();
        alert = new Dialog(dialogPane, (e)->{
            
        });
        
        btnLogin.setOnAction((e)->{
            login(textFieldLogin.getText(), textFieldPass.getText());
        });
        
        btnSignUp.setOnAction((e)->{
            signUp(textFieldLogin.getText(), textFieldPass.getText());
        });
        
        userStorage.init();
        loadingPane.toFront();
        loading(false);
    }    
    
    public void login(String login, String pass){
        if(userStorage.hasUser(login, pass)){
            openHome(login);
            clearFields();
        }
        else{
            alert.show("Ops!", "Parece que o usuario "+login+" ainda não foi cadastrado!");
        }
    }
    
    public void signUp(String login, String pass){
        if(!userStorage.hasUser(login, pass)){
            alert.show("Bem vindo!", "O usuario "+login+" foi cadastrado com sucesso!");
            userStorage.addUser(login, pass);
        }
        else{
            alert.show("Ops!", "Parece que o usuario "+login+" já foi cadastrado!");
        }
    }
    
    public void openHome(String login){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLHome.fxml")); 
        Parent home = null;  
        try {
            home = (Parent)loader.load();
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        FXMLHomeController ctrl = loader.<FXMLHomeController>getController();
        ctrl.setUserConfigs(login, textFieldLat.getText(), textFieldLng.getText());
        
        Scene scene = new Scene(home);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    /*public void init(){
        System.out.println("Procurando pelo servico JavaSpace...");
        
        Thread t = new Thread(()->{
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
            
        });
        t.setDaemon(true);
        t.start();
    }*/
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }

    private void clearFields() {
        textFieldLogin.setText("admin");
        textFieldPass.setText("admin");
        textFieldLat.setText("0.0");
        textFieldLng.setText("0.0");
    }
    
}
