package me.sergey.sprites;

import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

public class Player extends Sprite{
    private ArrayList<Sprite> projectiles;
    private final int resetAmmo;
    private int ammo;
    private int health;
    
    public Player(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public Player(GraphicsContext gc, String path, double x, double y){
        this(gc, path, x, y, 90);
    }
    
    public Player(GraphicsContext gc, String path, double x, double y, int angle){
        this(gc, path, x, y, angle, 10);
    }
    
    public Player(GraphicsContext gc, String path, double x, double y, int angle, int resetAmmo){
        super(gc, path, x, y, angle);
        projectiles = new ArrayList<>();
        this.resetAmmo = resetAmmo;
        this.ammo = resetAmmo;
        health = 100;
    }

    public int getAmmo(){
        return ammo;
    }
    
    public void setAmmo(int ammo){
        this.ammo = ammo;
    }
    
    public ArrayList<Sprite> getProjectiles(){
        return projectiles;
    }
    
    public void removeProjectiles(Iterable<Sprite> toRemove){
        for(Sprite projectile : toRemove){
            projectiles.remove(projectile);
        }
    }
    
    public int getHealth(){
        return health;
    }
    
    public void setHealth(int health){
        this.health = health;
    }

    public void damage(int health){
        this.health -= health;
    }
    
    public void fire(){
        if(ammo != 0){
            projectiles.add(new Sprite(gc, "/assets/projectile.png", getX(true), getY(true), angle));
            ammo--;
        }
    }
    
    public void halt(){
        projectiles.clear();
        ammo = resetAmmo;
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
        super.draw();
    }
}
