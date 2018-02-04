package me.sergey.game;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;

public class Game extends Application{
    private GameLoop gameLoop;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {        
        long startNanoseconds = System.nanoTime();
        
        primaryStage.setTitle("Functioning controller demo");
        primaryStage.setResizable(false);
        
        Group root = new Group();
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        Canvas canvas = new Canvas(600,500);
        root.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gameLoop = new GameLoop(startNanoseconds, gc);
        gameLoop.start();
        
        primaryStage.show();
    }
}
