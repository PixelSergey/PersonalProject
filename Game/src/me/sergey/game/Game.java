package me.sergey.game;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
 
public class Game extends Application{
    private GameLoop gameLoop = new GameLoop();
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello World!");
        
        Label desc = new Label("Press keys and look at stdout");
        
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, gameLoop);
        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, gameLoop);
        
        StackPane root = new StackPane();
        root.getChildren().add(desc);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
}
