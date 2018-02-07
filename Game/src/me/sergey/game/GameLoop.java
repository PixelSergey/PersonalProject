package me.sergey.game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import me.sergey.communication.Communicator;

public class GameLoop extends AnimationTimer{
    private final long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;
    private final GButton start;
    private int stage;

    public GameLoop(long startNanos, GraphicsContext graphicsContext){
        this.startNanos = startNanos;
        this.gc = graphicsContext;
        this.stage = 0;
        
        keyHandler = new KeyHandler();
        gc.getCanvas().getScene().setOnKeyPressed(keyHandler);
        gc.getCanvas().getScene().setOnKeyReleased(keyHandler);
        
        comm = new Communicator();
        
        connect = new GButton(gc, "/assets/connect.png", 500, 500){
            @Override
            void onClick(){
                comm.connect();
            }
        };
        
        start = new GButton(gc, "/assets/start.png", 500, 600){
            @Override
            void onClick(){
                stage = 1;
            }
        };
    }
    
    @Override
    public void handle(long nanos){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.drawImage(new Image("/assets/stage_" + stage + ".png"), 0, 0);
        if(stage == 0){
            gc.fillText("Debug Info:\n" + 
                    "Time: " + (nanos-startNanos)/1000000000.0 + "s\n" +
                    "Controller: " + comm.receive().toString() + "\n" +
                    "Keyboard: " + keyHandler.getKeylist(), 5, 15);
            connect.draw();
            start.draw();
        }
        else if(stage > 0){
            
        }
    }
}
