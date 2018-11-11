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
import main.tuplas.CounterEntry;
import main.tuplas.UserEntry;
import net.jini.space.JavaSpace;
import main.util.Lookup;
import net.jini.core.lease.Lease;
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

    JavaSpace javaSpace;
    Dialog alert;
    UserStorage userStorage = new UserStorage();
    CounterEntry counter = null;
    UserEntry user = new UserEntry();
    
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
            user.setAttributes(null, login, Float.valueOf(textFieldLat.getText()), Float.valueOf(textFieldLng.getText()));
            addUserEntry(()->{
                openHome(login);
                clearFields();
            });
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
    
    /**
     * Metodo para pegar referencia JAVASPACE
     * Adiciona o usuário q efetuou login
     * @param callback 
     */
    public void addUserEntry(Runnable callback){
        System.out.println("Procurando pelo servico JavaSpace...");
        loading(true);
        
        Thread t = new Thread(()->{
            Lookup finder = new Lookup(JavaSpace.class);
            this.javaSpace = (JavaSpace) finder.getService();
            if (this.javaSpace == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            } 
            System.out.println("O servico JavaSpace foi encontrado.");
            
                
            getCounterEntry(()->{
                try {
                    user.id = counter.increaseUser();
                    this.javaSpace.write(user, null, Lease.FOREVER);
                    this.javaSpace.take(new CounterEntry(), null, 3 * 1000);
                    this.javaSpace.write(counter, null, Lease.FOREVER);
                    System.out.println("User "+user.id +"-"+ user.login +" adicionado");
                    loading(false);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                if(callback != null){
                    Platform.runLater(callback);
                }
            });
        });
        t.setDaemon(true);
        t.start();
    }
    
    /**
     * 
     */
    public void getCounterEntry(Runnable callback){
        loading(true);
        Thread t = new Thread(()->{
            try {
                counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 10 * 1000);
                if (counter == null) {
                    counter = new CounterEntry();
                    counter.lastIdRoom = 0;
                    counter.lastIdUser = 0;
                    System.out.println("Adicionando counter pela primeira vez");
                    this.javaSpace.write(counter, null, Lease.FOREVER);
                    
                }else {
                    System.out.println("Counter já existe");
                }
                
                if(callback != null){
                    callback.run();
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
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
