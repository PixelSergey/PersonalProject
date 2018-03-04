package me.sergey.sprites;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;

public abstract class GButton extends Base implements EventHandler<MouseEvent>{    
    
    public GButton(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public GButton(GraphicsContext gc, String path, double x, double y){
        super(gc, path, x, y);
        this.gc.getCanvas().getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, this);
    }
    
    @Override
    public void handle(MouseEvent e) {
        if(e.getEventType().getName().equals("MOUSE_CLICKED")){
            if(e.getX() > x && e.getX() < x + img.getWidth() && e.getY() > y && e.getY() < y + img.getHeight()){
                onClick();
            }
        }
    }
    
    protected abstract void onClick();
}
