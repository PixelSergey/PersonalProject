package me.sergey.game;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import me.sergey.communication.Communicator;

public class GameLoop extends AnimationTimer{
    private final long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;

    public GameLoop(long startNanos, GraphicsContext graphicsContext){
        this.startNanos = startNanos;
        this.gc = graphicsContext;
        
        keyHandler = new KeyHandler();
        gc.getCanvas().getScene().setOnKeyPressed(keyHandler);
        gc.getCanvas().getScene().setOnKeyReleased(keyHandler);
        comm = new Communicator();
        
        connect = new GButton(gc, "/assets/connect.png", 100, 100) {
            @Override
            void clickHandle() {
                comm.connect();
            }
        };
    }
    
    @Override
    public void handle(long nanos){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.fillText("Time: " + (float)(nanos-startNanos)/1000000000 + "s" + "\n" +
                    "Controller: " + comm.receive().toString() + "\n" +
                    "Keyboard: " + keyHandler.getKeylist(), 5, 15);
        connect.draw();
    }
}
