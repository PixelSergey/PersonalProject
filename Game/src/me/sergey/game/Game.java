package me.sergey.game;


import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Box;
import javafx.stage.Stage;
 
public class Game extends Application implements EventHandler<KeyEvent>{
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        
        Label desc = new Label("Press keys and look at stdout");
        
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, this);
        
        StackPane root = new StackPane();
        root.getChildren().add(desc);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }
    
    @Override
    public void handle(KeyEvent e){
        System.out.println(e.getCode() + " pressed");
    }
}