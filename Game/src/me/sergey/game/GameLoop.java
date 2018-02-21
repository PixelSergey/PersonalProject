package me.sergey.game;

import me.sergey.sprites.GButton;
import java.util.ArrayList;
import java.util.HashMap;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import me.sergey.communication.Communicator;
import me.sergey.sprites.Sprite;

public class GameLoop extends AnimationTimer{
    private final long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;
    private final GButton start;
    private Sprite player;
    private int stage;
    private HashMap inputs;
    private ArrayList keys;
    private double elapsed;
    
    public GameLoop(long startNanos, GraphicsContext graphicsContext){
        this.startNanos = startNanos;
        this.gc = graphicsContext;
        this.stage = 0;
        
        keyHandler = new KeyHandler();
        gc.getCanvas().getScene().setOnKeyPressed(keyHandler);
        gc.getCanvas().getScene().setOnKeyReleased(keyHandler);
        
        comm = new Communicator();
        
        player = new Sprite(gc, "/assets/player.png");
        
        connect = new GButton(gc, "/assets/connect.png", 500, 500){
            @Override
            protected void onClick(){
                comm.connect();
            }
        };
        
        start = new GButton(gc, "/assets/start.png", 500, 600){
            @Override
            protected void onClick(){
                stage = 1;
            }
        };
    }
    
    @Override
    public void handle(long nanos){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.drawImage(new Image("/assets/stage_" + stage + ".png"), 0, 0);
        inputs = comm.receive();
        keys = keyHandler.getKeylist();
        elapsed = (nanos-startNanos)/1000000000.0;
        
        if(stage == 0){ // Title Screen
            gc.fillText("Debug Info:\n" + 
                    "Time: " + elapsed + "s\n" +
                    "Controller: " + inputs.toString() + "\n" +
                    "Keyboard: " + keys, 5, 15);
            connect.draw();
            start.draw();
        }
        else if(stage > 0){
            player.setXY(10,10);
            
            if(keys.contains("Up")){
                player.setFacing(0);
            }else if(keys.contains("Down")){
                player.setFacing(180);
            }else if(keys.contains("Left")){
                player.setFacing(270);
            }else if(keys.contains("Right")){
                player.setFacing(90);
            }
            player.draw();
        }
    }
}
