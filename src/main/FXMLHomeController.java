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
import main.tuplas.UserEntry;
import main.util.Lookup;
import main.util.RoomTreeObject;
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
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        loadingPane.toFront();
        
        btnAddRoom.setOnAction((e)->{
            openRoom();
        });
        
        //loading(false);
        //creatTableRoom();
        
        creatTableRoom();
        
        setUserConfigs("admin", "0", "0");
        initAndAddUserEntry(()->{
            
        });
    }    
    
    public void init(){
        /*System.out.println("Procurando pelo servico JavaSpace...");
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
        t.start();*/
    }
    
    public void initAndAddUserEntry(Runnable callback){
        System.out.println("Procurando pelo servico JavaSpace...");
        loading(true);
        
        Thread t = new Thread(()->{
            Lookup finder = new Lookup(JavaSpace.class);
            this.javaSpace = (JavaSpace) finder.getService();
            if (this.javaSpace == null) {
                System.out.println("O servico JavaSpace nao foi encontrado. Encerrando...");
            } 
            System.out.println("O servico JavaSpace foi encontrado.");
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
    }
    
    public void setUserConfigs(String login, String lat, String lng){
        labelUser.setText(login);
        textFieldLat.setText(lat);
        textFieldLng.setText(lng);
        textFieldRadius.setText("1");        
        user.setAttributes(login, Float.valueOf(lat), Float.valueOf(lng));
    }
    
    private void loading(Boolean flag){
        loadingPane.setVisible(flag);
    }
    
    public void openRoom(){
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
        name.setPrefWidth(2*width/3);
        name.setCellValueFactory((param)->{
            return param.getValue().getValue().name;
        });
        
        //Coluna AÇÕES
        JFXTreeTableColumn<RoomTreeObject, String> action = new JFXTreeTableColumn<RoomTreeObject, String>("Ações");
        action.setPrefWidth(width/3);
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
                            System.out.println(getIndex());
                            openRoom();
                        });
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;
        });
        
        ObservableList<RoomTreeObject> rooms = FXCollections.observableArrayList();
        rooms.add(new RoomTreeObject("SAla 1"));
        rooms.add(new RoomTreeObject("SAla 2"));
        
        final TreeItem<RoomTreeObject> root = new RecursiveTreeItem<RoomTreeObject>(rooms, RecursiveTreeObject::getChildren);
        tableRooms.getColumns().setAll(name, action);
        tableRooms.setRoot(root);
        tableRooms.setShowRoot(false);
    }
    
}
