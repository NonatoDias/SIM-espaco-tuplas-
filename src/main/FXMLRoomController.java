/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.tuplas.CounterEntry;
import main.tuplas.MessageEntry;
import main.tuplas.UserEntry;
import main.tuplas.UserRoomEntry;
import main.util.Lookup;
import net.jini.core.lease.Lease;
import net.jini.space.JavaSpace;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLRoomController implements Initializable {

    @FXML
    private StackPane loadingPane;
    
    @FXML
    private TextFlow msgTextFlow;
    
    @FXML
    private JFXListView<Label> jFXListUsers;
    
    @FXML public void handleMouseClick(MouseEvent arg0) {
        int index = jFXListUsers.getSelectionModel().getSelectedIndex();
        userToLogin = usersList.get(index);
        labelTo.setText("Destinatário: "+ userToLogin);
        this.msgTextFlow.getChildren().setAll(new Text(""));
    }

    @FXML
    private JFXTextField jfxTfMessage;
    
    @FXML
    private Label labelRoom;
    
    @FXML
    private Label labelTo;
    
    JavaSpace javaSpace;
    UserEntry user = new UserEntry();
    UserRoomEntry userRoom = new UserRoomEntry();
    List<String> usersList = new ArrayList<String>();
    String userToLogin = "";
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadingPane.toFront();
        loading(false);
        
        jfxTfMessage.setOnKeyPressed((KeyEvent e)->{
            if(e.getCode().equals(KeyCode.ENTER)){
                String colorPlayer = "#1e90ff";
                try {
                    addMessageEntry(jfxTfMessage.getText());
                    
                } catch (Exception ex) {
                    
                } 
                jfxTfMessage.setText("");
            }
        });
        
        //setUserRoomConfigs("admin", "0", "0", "Sala 1");
        init(()->{
            initIntervalMessages();
            initIntervalUserInRadar();
        });
    }

    public void init(Runnable callback){
        System.out.println("Procurando pelo servico JavaSpace...");
        loading(true);
        
        Thread t = new Thread(()->{
            Lookup finder = new Lookup(JavaSpace.class);
            this.javaSpace = (JavaSpace) finder.getService();
            if (this.javaSpace == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            } 
            System.out.println("O servico JavaSpace foi encontrado.");
            
            //Get users
            try {
                int count = 0;
                usersList = new ArrayList<>();
                CounterEntry counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 10 * 1000);
                UserEntry template = new UserEntry();
                while(count <= counter.lastIdUser){
                    template.id = count;
                    UserEntry userAux = (UserEntry) this.javaSpace.read(template, null, 3 * 1000);
                    if (userAux == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                    }else {
                        Platform.runLater(()->{
                            /*Text text = new Text(userAux.id + " - " +userAux.login+"\n");
                            text.setFill(Paint.valueOf("#2196f3"));
                            text.setFont(Font.font("Helvetica", FontPosture.REGULAR, 20));  
                            textFlowUsers.getChildren().addAll(text);*/
                            usersList.add(userAux.login);
                            jFXListUsers.getItems().add(new Label(userAux.id + " - " +userAux.login));
                        });
                        System.out.println(" - Lendo ... "+ userAux.id + " - "+ userAux.login + "     d= "+ getDistance(userAux, user));
                    }
                    count++;
                }
                loading(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            if(callback != null){
                callback.run();
            }
        });
        t.setDaemon(true);
        t.start();
    }    
    
    /**
     * Adiciona as configurações do usuario na View
     * Defini o userEntry
     * @param login
     * @param lat
     * @param lng 
     */
    public void setUserRoomConfigs(String userLogin, String lat, String lng, String roomName){
        labelRoom.setText(roomName);     
        System.out.println("ROOM controller ");
        user.setAttributes(0, userLogin, Float.valueOf(lat), Float.valueOf(lng));
        userRoom.setAttributes(0, userLogin, roomName);
    }
    
    public void initIntervalUserInRadar(){
        //loading(true);
        Thread t = new Thread(()->{
            List userEntryList = new ArrayList<UserEntry>();;
            try {
                int count = 0;
                CounterEntry counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 10 *1000);
                UserEntry template = new UserEntry();
                while(count <= counter.lastIdUser){
                    template.id = count;
                    UserEntry userAux = (UserEntry) this.javaSpace.read(template, null, 500);
                    if (userAux == null) {
                        System.out.println("nao encontrado: "+template.id);
                    }else {
                        userEntryList.add(userAux);
                        System.out.println(" - Lendo ... "+ userAux.id + " - "+ userAux.login + "     d= "+ getDistance(userAux, user));
                    }
                    count++;
                }
                usersList = new ArrayList<>();
                Platform.runLater(()->{
                    jFXListUsers.getItems().setAll();
                });
                for(Object o : userEntryList){
                    UserEntry u = (UserEntry) o;
                    usersList.add(u.login);
                    Platform.runLater(()->{
                        jFXListUsers.getItems().add(new Label(u.id + " - " +u.login));
                    });
                }
                Thread.sleep(1000);
                initIntervalUserInRadar();
                //loading(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    public double getDistance(UserEntry u1, UserEntry u2){
        Float a = u2.lat - u1.lat;
        Float b = u2.lng - u1.lng;
        return Math.sqrt(Math.abs(a*a - b*b));
    }
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }
    
    public void addMessageToTheView(String msg, Paint value){
        Text text = new Text(msg+"\n");
        //text.setFill(value);
        text.setFont(Font.font("Helvetica", FontPosture.REGULAR, 15));
        this.msgTextFlow.getChildren().addAll(text);
    }
    
     public void addMessageEntry(String msg){
        if(userToLogin.length() == 0) return;
        
        loading(true);
        Thread t = new Thread(()->{
            try {
                MessageEntry message = new MessageEntry();
                message.setAttributes(user.login, userToLogin, msg);
                this.javaSpace.write(message, null, Lease.FOREVER);
                    
                String strMsg = "MENSAGEM de "+ message.from + " para "+ message.to + "    "+message.content;
                System.out.println(strMsg);
                loading(false);
                Platform.runLater(()->{
                    addMessageToTheView("YOU:   " + msg, null);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
     
    public void initIntervalMessages(){
        //loading(true);
        /*DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        System.out.println("\nProcurando mensagens ....     "+dtf.format(now));*/
        Thread t = new Thread(()->{
            try {
                boolean flag = true;
                MessageEntry template = new MessageEntry();
                template.from = userToLogin;
                
                while(flag){
                    MessageEntry message = (MessageEntry) this.javaSpace.take(template, null, 1000);
                    if(message == null){
                        flag = false;
                        
                    }else{
                        System.out.println("RECEBEU: " + message.content);
                        Platform.runLater(()->{
                            addMessageToTheView(message.from.toUpperCase() + ":   " + message.content, null);
                        });
                    }
                }
                Thread.sleep(1000);
                initIntervalMessages();
                //loading(false);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
}
