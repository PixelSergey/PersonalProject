package me.sergey.game;

import java.util.ArrayList;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;

public class KeyHandler implements EventHandler<KeyEvent>{
    private ArrayList<String> keylist;
    
    public KeyHandler(){
        keylist = new ArrayList<>();
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
    
    public ArrayList<String> getKeylist(){
        return keylist;
    }
    
}
