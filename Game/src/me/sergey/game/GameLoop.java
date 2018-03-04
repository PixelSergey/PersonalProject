package me.sergey.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import me.sergey.communication.Communicator;
import me.sergey.sprites.Sprite;
import me.sergey.sprites.GButton;

public class GameLoop extends AnimationTimer{
    private final long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;
    private final GButton start;
    private final Sprite player;
    private final List<List<Integer>> directions;
    private int stage;
    private HashMap inputs;
    private ArrayList<String> keys;
    private double elapsed;
    private int angle;
    private double speed;
    private Image background;
    private boolean first;
    private boolean canMove;
    
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
                first = true;
            }
        };
        
        directions = Arrays.asList(
                    Arrays.asList(225, 180, 135),
                    Arrays.asList(270, 360, 90),
                    Arrays.asList(315, 0, 45));
    }
    
    @Override
    public void handle(long nanos){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        background = new Image("/assets/stage_" + stage + ".png");
        gc.drawImage(background, 0, 0);
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
        else if(stage > 0){ // Game Logic
            if(first){ // Check whether a new stage just loaded
                player.setXY(75, 350);
                player.setFacing(90);
                first = false;
                canMove = false;
                return;
            }
            if(inputs.isEmpty()){ // Controller not connected; use keyboard
                int hor = 1, ver = 1;
                if(keys.contains("Up")){
                    hor++;
                }
                if(keys.contains("Down")){
                    hor--;
                }
                if(keys.contains("Left")){
                    ver--;
                }
                if(keys.contains("Right")){
                    ver++;
                }
                angle = directions.get(hor).get(ver);
                if(angle == 360){
                    speed = 0;
                    canMove = true;
                }else{
                    speed = 5;
                }
            }else{ // Controller connected; use controller
                Double hor, ver;
                hor = Double.parseDouble((String)inputs.get("joyX"));
                ver = Double.parseDouble((String)inputs.get("joyY"));
                if(!(hor == 0.0 && ver == 0.0)){
                    Double temp_angle = Math.toDegrees(Math.atan2(ver, hor));
                    angle = temp_angle.intValue();
                    speed = Math.sqrt(Math.pow(hor, 2) + Math.pow(ver, 2)) * 0.5;
                }else{
                    speed = 0;
                    canMove = true;
                }
            }
            
            if(canMove){
                player.setFacing(angle);
                double x = player.getX();
                double y = player.getY();
                player.move(angle, speed);
                if(player.touchesColor(background.getPixelReader(), Color.color(0, 1, 0))){
                    stage++;
                    first = true;
                }
                if(player.touchesColor(background.getPixelReader(), Color.color(0, 0, 0))){
                    player.setXY(x, y);
                }
            }
            player.draw();
        }
    }
}
