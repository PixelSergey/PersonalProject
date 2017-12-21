package me.sergey.game;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class GameLoop extends AnimationTimer implements EventHandler<KeyEvent>{
    private ArrayList<String> keylist = new ArrayList<>();
    
    @Override
    public void handle(long nanoseconds) {
        
    }

    @Override
    public void handle(KeyEvent e){
        String key = e.getCode().getName();
        String type = e.getEventType().getName();
        
        if(type.equals("KEY_PRESSED")){
            keylist.add(key);
            System.out.println(key + " pressed");
        }else if(type.equals("KEY_RELEASED")){
            keylist.remove(key);
        }
    }
}
