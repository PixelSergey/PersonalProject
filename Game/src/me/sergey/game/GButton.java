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
    private float scale;
    
    public GButton(GraphicsContext gc, String path){
        this(gc, path, 0, 0, 1);
    }
    
    public GButton(GraphicsContext gc, String path, int x, int y){
        this(gc, path, x, y, 1);
    }
    
    public GButton(GraphicsContext gc, String path, int x, int y, float scale){
        this.img = new Image(path);
        this.gc = gc;
        this.x = x;
        this.y = y;
        this.scale = scale;
        
        this.gc.getCanvas().getScene().setOnMouseClicked(this);
        this.gc.getCanvas().getScene().setOnMouseMoved(this);
    }
    
    public void draw(){
        gc.drawImage(img, x, y, img.getWidth() * scale, img.getHeight() * scale);
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
            if(e.getX() > x && e.getX() < x + img.getWidth() * scale && e.getY() > y && e.getY() < y + img.getHeight() * scale){
                clickHandle();
            }
        }
    }
    
    abstract void clickHandle();
}
