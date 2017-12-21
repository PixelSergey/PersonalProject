package me.sergey.game;

import java.util.ArrayList;
import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class GameLoop extends AnimationTimer implements EventHandler<KeyEvent>{
    private ArrayList<String> keylist = new ArrayList<>();
    private long startNanoseconds;

    GameLoop(long startNanoseconds){
        this.startNanoseconds = startNanoseconds;
    }
    
    @Override
    public void handle(long nanoseconds) {
        System.out.println(keylist);
    }

    @Override
    public void handle(KeyEvent e){
        String key = e.getCode().getName();
        String type = e.getEventType().getName();
        
        if(type.equals("KEY_PRESSED")){
            if(!keylist.contains(key)){
                keylist.add(key);
            }
        }else if(type.equals("KEY_RELEASED")){
            keylist.remove(key);
        }
    }
}
