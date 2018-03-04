package me.sergey.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Base {
    protected GraphicsContext gc;
    protected Image img;
    protected double x;
    protected double y;
    
    public Base(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public Base(GraphicsContext gc, String path, double x, double y){
        this.img = new Image(path);
        this.gc = gc;
        this.x = (x - (img.getWidth()/2));
        this.y = (y - (img.getHeight()/2));
    }
    
    public void draw(){
        if(x>=0 && y>=0){
            gc.drawImage(img, x, y);
        }
    }
    
    public Image getImg(){
        return img;
    }
    
    public void setImg(String path){
        this.img = new Image(path);
    }
    
    public double getX(){
        return getX(false);
    }
    
    public double getX(boolean center){
        if(center){
            return (x + (img.getWidth()/2));
        }else{
            return x;
        }
    }
    
    public double getY(){
        return getY(false);
    }
    
    public double getY(boolean center){
        if(center){
            return (y + (img.getWidth()/2));
        }else{
            return y;
        }
    }
    
    public void setX(double x){
        setX(x, false);
    }
    
    public void setX(double x, boolean center){
        if(x < 0 || x + img.getWidth() >= gc.getCanvas().getWidth()){
            return;
        }
        if(center){
            this.x = (x - (img.getWidth()/2));
        }else{
            this.x = x;
        }
    }
    
    public void setY(double y){
        setY(y, false);
    }
    
    public void setY(double y, boolean center){
        if(y < 0 || y + img.getHeight() > gc.getCanvas().getHeight()){
            return;
        }
        if(center){
            this.y = (y - (img.getHeight()/2));
        }else{
            this.y = y;
        }
    }
    
    public void setXY(double x, double y){
        setXY(x, y, false);
    }
    
    public void setXY(double x, double y, boolean center){
        setX(x, center);
        setY(y, center);
    }
}
