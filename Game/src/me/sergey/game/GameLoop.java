package me.sergey.game;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import me.sergey.communication.Communicator;

public class GameLoop extends AnimationTimer{
    private long startNanos;
    private KeyHandler keyHandler;
    private Communicator comm;
    private Label desc;

    public GameLoop(long startNanos, Stage primaryStage){
        this.startNanos = startNanos;
        keyHandler = new KeyHandler();
        
        comm = new Communicator();
        
        primaryStage.setTitle("Functioning controller demo");
        desc = new Label("Press keys and look at stdout");
        
        Button connect = new Button();
        connect.setText("Connect Controller");
        connect.setOnAction((ActionEvent event) -> {  // Lambda anonymous class
            comm.connect();
        });
        
        primaryStage.addEventFilter(KeyEvent.KEY_PRESSED, keyHandler);
        primaryStage.addEventFilter(KeyEvent.KEY_RELEASED, keyHandler);
        
        BorderPane root = new BorderPane();
        root.setCenter(connect);
        root.setTop(desc);
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.show();
    }
    
    @Override
    public void handle(long nanos) {
        desc.setText("Time: " + (float)(nanos-startNanos)/1000000000 + "s" + "\n" +
                    "Controller: " + comm.receive().toString() + "\n" +
                    "Keyboard: " + keyHandler.getKeylist());
    }
}
