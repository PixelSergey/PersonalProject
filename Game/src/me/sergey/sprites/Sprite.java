package me.sergey.sprites;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Sprite extends Base{
    private ImageView imgView;
    private int angle;
    private Rotate r;
    private PixelReader sprReader;
    private ArrayList<Sprite> projectiles;
    private int ammo;
    
    public Sprite(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public Sprite(GraphicsContext gc, String path, double x, double y){
        this(gc, path, x, y, 90);
    }
    
    public Sprite(GraphicsContext gc, String path, double x, double y, int angle){
        super(gc, path, x, y);
        imgView = new ImageView();
        imgView.setImage(img);
        projectiles = new ArrayList<>();
        sprReader = img.getPixelReader();
        this.angle = angle;
        ammo = 10;
    }
    
    public int getFacing(){
        return angle;
    }
    
    public void setFacing(int angle){
        if(angle < 360){
            this.angle = angle;
        }
    }

    public void move(int angle, double speed){
        setX(x + Math.sin(Math.toRadians(angle)) * speed);
        setY(y - Math.cos(Math.toRadians(angle)) * speed);
    }
    
    @Override
    public void setImg(String path){
        img = new Image(path);
        sprReader = img.getPixelReader();
    }
    
    public int getAmmo(){
        return ammo;
    }
    
    public void setAmmo(int ammo){
        this.ammo = ammo;
    }
    
    public void fire(){
        if(ammo > 0){
            projectiles.add(new Sprite(gc, "/assets/projectile.png", getX(true), getY(true), angle));
            ammo--;
        }
    }
    
    public void halt(){
        projectiles.clear();
        ammo = 10;
    }
    
    @Override
    public void draw(){
        ArrayList<Sprite> toRemove = new ArrayList<>();
        for(Sprite projectile : projectiles){
            projectile.move(projectile.getFacing(), 12);
            projectile.draw();
            if(projectile.getX() <= 10 || projectile.getX() + projectile.getImg().getWidth() >= gc.getCanvas().getWidth() - 10 || projectile.getY() <= 10 || projectile.getY() + projectile.getImg().getHeight() >= gc.getCanvas().getHeight() - 10){
                toRemove.add(projectile);
            }
        }
        projectiles.removeAll(toRemove);
        if(x >= 0 && y >= 0){
            gc.save();
            r = new Rotate(angle, x+img.getWidth()/2, y+img.getHeight()/2); // Set pivot around centerpoint
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.drawImage(img, x, y);
            gc.restore();
        }
    }
    
    public boolean touchesColor(PixelReader reader, Color color){
        int j, i = 0;
        while(i < img.getWidth()){
            j = 0;
            while(j < img.getHeight()){
                if(reader.getColor((int)(i + x), (int)(j + y)).equals(color) && !sprReader.getColor(i, j).equals(Color.TRANSPARENT)){
                    return true;
                }
                j++;
            }
            i++;
        }
        return false;
    }
    
}
