package me.sergey.game;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;

public class Game extends Application{
    private GameLoop gameLoop;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {        
        long startNanoseconds = System.nanoTime();
        gameLoop = new GameLoop(startNanoseconds, primaryStage);
        gameLoop.start();
    }
}
