package me.sergey.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Base {
    protected GraphicsContext gc;
    protected Image img;
    protected int x;
    protected int y;
    
    public Base(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public Base(GraphicsContext gc, String path, int x, int y){
        this.img = new Image(path);
        this.gc = gc;
        this.x = (int)(x - (img.getWidth()/2));
        this.y = (int)(y - (img.getHeight()/2));
    }
    
    public void draw(){
        if(x>=0 && y>=0){
            gc.drawImage(img, x, y);
        }
    }
    
    public void setImg(String path){
        this.img = new Image(path);
    }
    
    public int getX(){
        return (int) (x + (img.getWidth()/2));
    }
    
    public int getY(){
        return (int) (y + (img.getHeight()/2));
    }
    
    public void setX(int x){
        setX(x, false);
    }
    
    public void setX(int x, boolean center){
        if(center){
            this.x = (int)(x - (img.getWidth()/2));
        }else{
            this.x = x;
        }
    }
    
    public void setY(int y){
        setY(y, false);
    }
    
    public void setY(int y, boolean center){
        if(center){
            this.y = (int)(y - (img.getHeight()/2));
        }else{
            this.y = y;
        }
    }
    
    public void setXY(int x, int y){
        setXY(x, y, false);
    }
    
    public void setXY(int x, int y, boolean center){
        setX(x, center);
        setY(y, center);
    }
}
