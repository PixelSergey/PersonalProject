package me.sergey.sprites;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

public class Sprite extends Base{
    private ImageView imgView;
    private int angle;
    private Rotate r;
    
    public Sprite(GraphicsContext gc, String path){
        this(gc, path, -1, -1);
    }
    
    public Sprite(GraphicsContext gc, String path, int x, int y) {
        super(gc, path, x, y);
        imgView = new ImageView();
        imgView.setImage(img);
        angle = 90;
    }
    
    public int getFacing(){
        return angle;
    }
    
    public void setFacing(int angle){
        if(angle < 360){
            this.angle = angle;
        }
    }
    
    @Override
    public void draw(){
        gc.save();
        r = new Rotate(angle, x+img.getWidth(), y+img.getHeight()); // Set pivot around bottom-right corner
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
        gc.drawImage(img, getX(), getY());
        gc.restore();
    }
    
    public boolean touchesColor(PixelReader reader, Color color){
        int j, i = 0;
        while(i < img.getWidth()){
            j = 0;
            while(j < img.getHeight()){
                if(reader.getColor(i, j) == color){
                    return true;
                }
                j++;
            }
            i++;
        }
        return false;
    }
    
}
