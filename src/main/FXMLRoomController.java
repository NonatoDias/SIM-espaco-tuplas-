/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import main.tuplas.CounterEntry;
import main.tuplas.UserEntry;
import main.tuplas.UserRoomEntry;
import main.util.Lookup;
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
    private TextFlow textFlowUsers;

    @FXML
    private JFXTextField jfxTfMessage;
    
    @FXML
    private Label labelRoom;
    
    JavaSpace javaSpace;
    UserEntry user = new UserEntry();
    UserRoomEntry userRoom = new UserRoomEntry();
    
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
                    addMessageToTheView(jfxTfMessage.getText(), null);
                    
                } catch (Exception ex) {
                    
                } 
                jfxTfMessage.setText("");
            }
        });
        
        //setUserRoomConfigs("admin", "0", "0", "Sala 1");
        init(null);
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
                CounterEntry counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 10 * 1000);
                UserEntry template = new UserEntry();
                while(count <= counter.lastIdUser){
                    template.id = count;
                    UserEntry userAux = (UserEntry) this.javaSpace.read(template, null, 3 * 1000);
                    if (userAux == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                    }else {
                        Platform.runLater(()->{
                            Text text = new Text(userAux.id + " - " +userAux.login+"\n");
                            text.setFill(Paint.valueOf("#2196f3"));
                            text.setFont(Font.font("Helvetica", FontPosture.REGULAR, 20));  
                            textFlowUsers.getChildren().addAll(text);
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
        userRoom.setAttributes(userLogin, roomName);
    }
    
    public void getUserInRadar(){
        loading(true);
        Thread t = new Thread(()->{
            try {
                int count = 0;
                CounterEntry counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 60 * 1000);
                UserEntry template = new UserEntry();
                while(count <= counter.lastIdUser){
                    template.id = count;
                    UserEntry userAux = (UserEntry) this.javaSpace.read(template, null, 3 * 1000);
                    if (userAux == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                    }else {
                        
                        System.out.println(" - Lendo ... "+ userAux.id + " - "+ userAux.login + "     d= "+ getDistance(userAux, user));
                    }
                    count++;
                }
                loading(false);
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
        text.setFont(Font.font("Helvetica", FontPosture.REGULAR, 18));    
        String message = msg+"&amp;"+value;
        this.msgTextFlow.getChildren().addAll(text);
    }
    
}
