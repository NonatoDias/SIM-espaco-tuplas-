/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.tuplas.CounterEntry;
import main.tuplas.UserEntry;
import main.tuplas.UserRoomEntry;
import main.util.Lookup;
import main.util.RoomTreeObject;
import net.jini.core.lease.Lease;
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
    private JFXTreeTableView<RoomTreeObject> tableRooms;

    @FXML
    private JFXButton btnRadar;

    @FXML
    private JFXTextField textFieldLat;

    @FXML
    private JFXTextField textFieldLng;

    @FXML
    private JFXTextField textFieldRadius;
    
    @FXML
    private Label labelUser;
    
    JavaSpace javaSpace;
    UserEntry user = new UserEntry();
    List<String> roomsAux = new ArrayList<String>();
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadingPane.toFront();
        loading(false);
        btnAddRoom.setOnAction((e)->{
            addRoom();
        });
        
        btnRadar.setOnAction((e)->{
            getUserInRadar();
        });
        
        //setUserConfigs("admin", "0", "0");
        init(null);
    }    
    
    /**
     * Metodo para pegar referencia JAVASPACE
     * Adiciona o usuário q efetuou login
     * @param callback 
     */
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
            /*try {
                this.javaSpace.write(user, null, Lease.FOREVER);
                System.out.println("User "+ user.login +" adicionado");
                loading(false);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
            loading(false);
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
    public void setUserConfigs(String login, String lat, String lng){
        labelUser.setText(login);
        textFieldLat.setText(lat);
        textFieldLng.setText(lng);
        textFieldRadius.setText("1");        
        user.setAttributes(1, login, Float.valueOf(lat), Float.valueOf(lng));
    }
    
    private void addRoom() {
        String name = "Sala 1";
        roomsAux.add(name);
        addUserRoomEntry(name, ()->{
            creatTableRoom();
        });
    }
    
    private void removeRoom(int index) {
        String r = roomsAux.remove(index);
        removeUserRoomEntry(r, ()->{
            creatTableRoom();
        });
    }
    
    public void addUserRoomEntry(String roomName, Runnable callback){
        loading(true);
        UserRoomEntry userRoom = new UserRoomEntry();
        Thread t = new Thread(()->{
            try {
                userRoom.setAttributes(user.login, roomName);
                this.javaSpace.write(userRoom, null, Lease.FOREVER);
                System.out.println("userRoom "+ roomName +" adicionado");
                loading(false);
                if(callback != null){
                    Platform.runLater(callback);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        t.setDaemon(true);
        t.start();
    }
    
    public void removeUserRoomEntry(String roomName, Runnable callback){
        loading(true);
        Thread t = new Thread(()->{
            try {
                UserRoomEntry template = new UserRoomEntry();
                template.roomName = roomName;
                UserRoomEntry userRoom = (UserRoomEntry) this.javaSpace.take(template, null, 60 * 1000);
                if (template == null) {
                    System.out.println("Tempo de espera esgotado. Encerrando...");
                    
                }else {
                    System.out.println("APAGAR userRoom "+ roomName +" OK");
                }
                loading(false);
                if(callback != null){
                    Platform.runLater(callback);
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
    
    public void openRoom(int index){
        Parent p = null;
        try {
            p = FXMLLoader.load(getClass().getResource("FXMLRoom.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(FXMLLoginDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Scene scene = new Scene(p);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public void creatTableRoom(){
        double width = (tableRooms.getPrefWidth()- 2);
        
        //Coluna NOME
        JFXTreeTableColumn<RoomTreeObject, String> name = new JFXTreeTableColumn<RoomTreeObject, String>("Salas disponíveis");
        name.setPrefWidth(width-240);
        name.setCellValueFactory((param)->{
            return param.getValue().getValue().name;
        });
        
        //Coluna Entrar
        JFXTreeTableColumn<RoomTreeObject, String> action = new JFXTreeTableColumn<RoomTreeObject, String>("Ações");
        action.setPrefWidth(110);
        action.setCellFactory((final TreeTableColumn<RoomTreeObject, String> param) -> {
            final TreeTableCell<RoomTreeObject, String> cell = new TreeTableCell<RoomTreeObject, String>() {
                final JFXButton btn = new JFXButton("Entrar");
                
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        
                        //Callback do botão
                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                        //btn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
                        btn.getStyleClass().add("btn-primary");
                        btn.setOnAction(event -> {
                            openRoom(getIndex());
                        });
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;
        });
              
        //Coluna Apagar
        JFXTreeTableColumn<RoomTreeObject, String> actionDelete = new JFXTreeTableColumn<RoomTreeObject, String>("");
        actionDelete.setPrefWidth(110);
        actionDelete.setCellFactory((final TreeTableColumn<RoomTreeObject, String> param) -> {
            final TreeTableCell<RoomTreeObject, String> cell = new TreeTableCell<RoomTreeObject, String>() {
                final JFXButton btn = new JFXButton("Apagar");
                
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        
                        //Callback do botão
                        btn.setButtonType(JFXButton.ButtonType.RAISED);
                        //btn.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white;");
                        btn.getStyleClass().add("btn-danger");
                        btn.setOnAction(event -> {
                            removeRoom(getIndex());
                        });
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;
        });
        
        ObservableList<RoomTreeObject> rooms = FXCollections.observableArrayList();
        for(String r: roomsAux){
            rooms.add(new RoomTreeObject(r));
        }
        
        final TreeItem<RoomTreeObject> root = new RecursiveTreeItem<RoomTreeObject>(rooms, RecursiveTreeObject::getChildren);
        tableRooms.getColumns().setAll(name, action, actionDelete);
        tableRooms.setRoot(root);
        tableRooms.setShowRoot(false);
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
                    UserEntry user = (UserEntry) this.javaSpace.read(template, null, 3 * 1000);
                    if (user == null) {
                        System.out.println("Tempo de espera esgotado. Encerrando...");
                    }else {
                        System.out.println(" - Lendo ... "+ user.id + " - "+ user.login);
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
}
