package me.sergey.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Sprite extends Base{
    protected ImageView imgView;
    protected int angle;
    protected Rotate r;
    protected PixelReader sprReader;
    protected boolean check;
    
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
        sprReader = img.getPixelReader();
        this.angle = angle;
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
    
    @Override
    public void draw(){
        check = true;
        if(x >= 0 && y >= 0){
            gc.save();
            r = new Rotate(angle, x+img.getWidth()/2, y+img.getHeight()/2); // Set pivot around centerpoint
            gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
            gc.drawImage(img, x, y);
            gc.restore();
        }
    }
    
    public boolean touches(Sprite target){
        if(!check){
            return false;
        }
        check = false;
        return !(
           target.getX() > this.x + img.getWidth()
        || target.getX() + target.getImg().getWidth() < this.x
        || target.getY() > this.y + img.getHeight()
        || target.getY() + target.getImg().getHeight() < this.y
                );
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
