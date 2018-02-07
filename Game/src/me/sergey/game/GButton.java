package me.sergey.game;

import javafx.event.EventHandler;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

public abstract class GButton implements EventHandler<MouseEvent>{
    private GraphicsContext gc;
    private Image img;
    private int x;
    private int y;
    
    public GButton(GraphicsContext gc, String path){
        this(gc, path, 0, 0);
    }
    
    public GButton(GraphicsContext gc, String path, int x, int y){
        this.img = new Image(path);
        this.gc = gc;
        this.x = (int)(x - (img.getWidth()/2));
        this.y = (int)(y - (img.getHeight()/2));
        
        this.gc.getCanvas().getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, this);
    }
    
    public void draw(){
        gc.drawImage(img, x, y);
    }
    
    public void setImg(String path){
        this.img = new Image(path);
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public void setCoords(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    @Override
    public void handle(MouseEvent e) {
        if(e.getEventType().getName().equals("MOUSE_CLICKED")){
            if(e.getX() > x && e.getX() < x + img.getWidth() && e.getY() > y && e.getY() < y + img.getHeight()){
                onClick();
            }
        }
    }
    
    abstract void onClick();
}
