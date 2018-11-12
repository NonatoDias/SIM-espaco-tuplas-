/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXListView;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import main.tuplas.CounterEntry;
import main.tuplas.UserEntry;
import main.util.Lookup;
import net.jini.space.JavaSpace;

/**
 * FXML Controller class
 *
 * @author Nonato Dias
 */
public class FXMLRadarController implements Initializable {

    @FXML
    private StackPane loadingPane;
    
    @FXML
    private Label labelRadar;

    @FXML
    private JFXListView<Label> jFXListUsers;

    @FXML
    void handleMouseClick(MouseEvent event) {

    }
    
    JavaSpace javaSpace;
    Float maxDistance = new Float(0);
    UserEntry user = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadingPane.toFront();
        init();
    }    
    
    public void init(){
        System.out.println("Procurando pelo servico JavaSpace...");
        loading(true);
        
        Thread t = new Thread(()->{
            Lookup finder = new Lookup(JavaSpace.class);
            this.javaSpace = (JavaSpace) finder.getService();
            if (this.javaSpace == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            } 
            System.out.println("O servico JavaSpace foi encontrado.");
            
            loading(false);
            getUserInRadar();
        });
        t.setDaemon(true);
        t.start();
    }
    
    public void setUserConfigs(UserEntry user, Float distance){
        this.user = user; 
        this.maxDistance = distance;
        labelRadar.setText("Máx distância: "+distance + "m");
        System.out.println("RADAR "+user.id +"-"+ user.login +"("+user.lat+", " + user.lng+")");
    }
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }
    
    public void getUserInRadar(){
        loading(true);
        Thread t = new Thread(()->{
            try {
                int count = 0;
                CounterEntry counter = (CounterEntry) this.javaSpace.read(new CounterEntry(), null, 2 * 1000);
                UserEntry template = new UserEntry();
                while(count <= counter.lastIdUser){
                    template.id = count;
                    UserEntry userAux = (UserEntry) this.javaSpace.read(template, null, 2 * 1000);
                    if (userAux == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                    }else {
                        
                        Platform.runLater(()->{
                            double d = getDistance(userAux, user);
                            if(d < maxDistance){
                                String text = userAux.id + " - " +userAux.login + " - "+d;
                                System.out.println("RADAR: "+ text);
                                jFXListUsers.getItems().add(new Label(text));
                            }
                        });
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
    
}
