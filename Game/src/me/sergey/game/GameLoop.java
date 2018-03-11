package me.sergey.game;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import me.sergey.communication.Communicator;
import me.sergey.sprites.Sprite;
import me.sergey.sprites.GButton;

public class GameLoop extends AnimationTimer{
    private long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;
    private final GButton start;
    private final GButton quit;
    private final Sprite player;
    private final List<List<Integer>> directions;
    private final Random random;
    private FileWriter write;
    private FileReader read;
    private int stage;
    private HashMap<String, String> inputs;
    private ArrayList<String> keys;
    private double elapsed;
    private int angle;
    private int lastHit;
    private double speed;
    private Image background;
    private boolean first;
    private boolean canMove;
    private String playerName;
    private String err;
    private HashMap<String, Double> leaderboard;
    
    public GameLoop(GraphicsContext graphicsContext){
        this.gc = graphicsContext;
        this.stage = 0;
        
        keyHandler = new KeyHandler();
        gc.getCanvas().getScene().setOnKeyPressed(keyHandler);
        gc.getCanvas().getScene().setOnKeyReleased(keyHandler);
        
        comm = new Communicator();
        comm.setDaemon(true);
        
        try{
            leaderboard = new HashMap<>();
            
            String path;
            String OS = System.getProperty("os.name").toUpperCase();
            if(OS.contains("WIN")){
                path = System.getenv("APPDATA") + "\\leaderboard.txt";
            }else if(OS.contains("MAC")){
                path = System.getProperty("user.home") + "/Library/Application Support/leaderboard.txt";
            }else if(OS.contains("NUX")){
                path = System.getProperty("user.home") + "/.config/leaderboard.txt";
            }else{
                path = System.getProperty("user.dir") + "/leaderboard.txt";
            }
            File file = new File(path);
            file.createNewFile(); // Only creates a new file if one doesn't already exist
            path = file.getAbsolutePath();
            
            read = new FileReader(path);
            char[] c = new char[10000];
            read.read(c);
            String chars = new String(c);
            if(!chars.trim().isEmpty()){
                String[] pairs = chars.trim().split(";");
                for(String pair : pairs){ // Write file data into leaderboard variable
                    String[] keyValue = pair.split(":");
                    leaderboard.put(keyValue[0], Double.parseDouble(keyValue[1]));
                }
                write = new FileWriter(path);
                for(String key : leaderboard.keySet()){
                    write.write(key + ":" + leaderboard.get(key) + ";"); // Rewrite file to prevent duplicate scores
                    write.flush();
                }
            }else{
                write = new FileWriter(path);
                write.write("");
                write.flush();
            }
        }catch(IOException e){
            System.err.println("Could not write to the leaderboard file! All scores will be deleted after exiting the game.");
            write = null;
            read = null;
        }
        
        player = new Sprite(gc, "/assets/player.png");
        
        connect = new GButton(gc, "/assets/connect.png", 500, 525){
            @Override
            public void onClick(){
                comm.start();
            }
        };
        
        start = new GButton(gc, "/assets/start.png", 500, 600){
            @Override
            public void onClick(){
                stage = 1;
                first = true;
                startNanos = System.nanoTime();
            }
        };
        
        quit = new GButton(gc, "/assets/quit.png", 960, 80){
            @Override
            public void onClick(){
                Platform.exit();
            }
        };
        
        directions = Arrays.asList(
                    Arrays.asList(225, 180, 135),
                    Arrays.asList(270, 360, 90),
                    Arrays.asList(315, 0, 45));
        
        random = new Random();
        first = true;
        lastHit = -1;
    }
    
    @Override
    public void handle(long nanos){
        gc.clearRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        try{
            background = new Image("/assets/stage_" + stage + ".png");
        }catch(IllegalArgumentException e){ // Next stage does not exist (last level cleared/passed)
            stage = -1;
            first = true;
            return;
        }
        gc.drawImage(background, 0, 0);
        
        inputs = comm.receive();
        keys = keyHandler.getKeylist();
        
        if(stage == 0){ // Title Screen
            gc.setFont(Font.getDefault());
            gc.setFill(Color.color(0, 0, 0));
            gc.fillText("Debug Info:\n" + 
                    "Controller: " + inputs.toString() + "\n" +
                    "Keyboard: " + keys, 5, 15);
            
            if(first){
                player.setXY(500, 225);
                speed = 10;
                angle = random.nextInt(360);
                canMove = false;
                first = false;
            }
            if(keys.isEmpty() && Collections.frequency(inputs.values(), "0") == inputs.values().size()){
                canMove = true;
            }
            if(canMove){
                if(!inputs.isEmpty() && inputs.get("Start").equals("1")){
                    start.onClick();
                }
            }
            
            if(player.getX() < 10 && lastHit != 4){
                angle = 360 - angle - random.nextInt(16); // Set angle to opposite minus a bit
                lastHit = 4;
            }else if (player.getX() + player.getImg().getWidth() > gc.getCanvas().getWidth() - 10 && lastHit != 2){
                angle = 360 - angle - random.nextInt(16); // Set angle to opposite minus a bit
                lastHit = 2;
            }else if(player.getY() < 10 && lastHit != 0){
                angle = 180 - angle + random.nextInt(16); // Set angle to opposite plus a bit
                lastHit = 0;
            }else if(player.getY() + player.getImg().getHeight() > gc.getCanvas().getHeight() - 10 && lastHit != 3){
                angle = 180 - angle + random.nextInt(16); // Set angle to opposite plus a bit
                lastHit = 3;
            }
            player.move(angle, speed);
            player.setFacing((int)(nanos/1_700_000.0)%360);
            
            player.draw();
            connect.draw();
            start.draw();
            quit.draw();
        }
        
        else if(stage > 0){ // Game Logic
            elapsed = (nanos-startNanos)/1_000_000_000.0;
            if(first){ // Check whether a new stage just loaded
                player.setXY(75, 350);
                player.setFacing(90);
                first = false;
                canMove = false;
                return;
            }
            if(keys.isEmpty() && (inputs.isEmpty() || Collections.frequency(inputs.values(), "0") == inputs.values().size())){
                canMove = true;
            }
            
            if(canMove){
                if(inputs.isEmpty()){ // Controller disconnected or off; use keyboard
                    int hor = 1, ver = 1;
                    if(keys.contains("Up")){
                        hor++;
                    }
                    if(keys.contains("Down")){
                        hor--;
                    }
                    if(keys.contains("Left")){
                        ver--;
                    }
                    if(keys.contains("Right")){
                        ver++;
                    }
                    if(keys.contains("Esc")){
                        stage = 0;
                        first = true;
                        return;
                    }
                    if(keys.contains("F12")){
                        stage++;
                        first = true;
                        return;
                    }
                    
                    angle = directions.get(hor).get(ver);
                    if(angle == 360){
                        speed = 0;
                    }else{
                        speed = 5;
                    }
                }else{ // Controller connected and on; use controller
                    if(inputs.get("X").equals("1") && inputs.get("Y").equals("1") && inputs.get("Start").equals("1")){
                        stage++;
                        first = true;
                        return;
                    }
                    if(inputs.get("Start").equals("1")){
                        stage = 0;
                        first = true;
                        return;
                    }
                    
                    Double hor, ver;
                    hor = Double.parseDouble((String)inputs.get("joyX"));
                    ver = Double.parseDouble((String)inputs.get("joyY"));
                    if(!(hor == 0.0 && ver == 0.0)){
                        angle = (int)Math.toDegrees(Math.atan2(hor, ver));
                        speed = Math.sqrt(Math.pow(hor, 2) + Math.pow(ver, 2)) * 0.05;
                    }else{
                        speed = 0;
                        canMove = true;
                    }
                }
                
                player.setFacing(angle);
                double x = player.getX();
                double y = player.getY();
                player.move(angle, speed);
                if(player.touchesColor(background.getPixelReader(), Color.color(0, 1, 0))){
                    stage++;
                    first = true;
                    return;
                }
                if(player.touchesColor(background.getPixelReader(), Color.color(0, 0, 0))){
                    player.setXY(x, y);
                }
            }
            player.draw();
            
        }else if(stage == -1){ // Victory
            if(first){
                playerName = "";
                err = "";
                first = false;
            }
            if(keys.isEmpty() && Collections.frequency(inputs.values(), "0") == inputs.values().size()){
                canMove = true;
            }
            if(canMove){
                if(keys.size() == 1){
                    canMove = false;
                    String input = keys.get(0);
                    
                    if(input.length() == 1){
                        if(Character.isLetter(input.charAt(0)) || Character.isDigit(input.charAt(0))){
                            if(playerName.length() < 9){
                                playerName += input;
                                err = "";
                            }else{
                                err = "Names can only be up to nine characters long";
                            }
                        }else{
                            err = "Names can only contain letters and numbers";
                        }
                    }else{
                        err = "Names can only contain letters and numbers";
                    }
                    if(input.equals("Backspace")){
                        if(playerName.length() > 0){
                            playerName = playerName.substring(0, playerName.length() - 1);
                            err = "";
                        }                        
                    }
                    else if(input.equals("Delete")){
                        playerName = "";
                        err = "";
                    }
                    else if(input.equals("Enter")){
                        err = "";
                        if(!playerName.equals("")){
                            leaderboard.put(playerName, elapsed);
                            try{
                                write.write(playerName + ":" + elapsed + ";");
                                write.flush();
                            }catch(NullPointerException e){
                            }catch(IOException e){
                                System.out.println(Arrays.toString(e.getStackTrace()));
                            }
                        }
                        stage = 0;
                        first = true;
                        return;
                    }
                }
            }
            
            if(playerName.equals("")){
                err = "A blank name will delete your score";
            }
            
            gc.setFill(Color.color(1, 0, 0));
            gc.setFont(Font.font("Constantia", 14));
            gc.fillText(err, 235, 477);
            
            gc.setFill(Color.color(1, 1, 1));
            gc.setFont(Font.font("Bodoni MT Black", 42));
            gc.fillText(playerName, 250, 450);
            
            gc.setFont(Font.font("Lucida Fax", 20));
            gc.fillText("Score: " + elapsed, 310, 322);
            
            List<Double> scores = new ArrayList(leaderboard.values());
            Collections.sort(scores);
            scores = scores.subList(0, Math.min(6, scores.size()));
            for(Double score : scores){
                for(String leader : leaderboard.keySet()){
                    if(leaderboard.get(leader).equals(score)){
                        int pos = scores.indexOf(score);
                        gc.setFont(Font.font("Lucida Fax", 26));
                        gc.fillText(pos+1 + ": " + leader + "\n", 780, 380 + pos * 40 + pos * 25);
                        gc.setFont(Font.font("Lucida Fax", 20));
                        gc.fillText(score + "\n\n", 780, 380 + pos * 40 + pos * 25 + 25);
                    }
                }
            }
        }
    }
}
