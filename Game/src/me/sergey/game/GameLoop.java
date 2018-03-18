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
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import me.sergey.communication.Communicator;
import me.sergey.sprites.Sprite;
import me.sergey.sprites.GButton;
import me.sergey.sprites.Player;

public class GameLoop extends AnimationTimer{
    private long startNanos;
    private final KeyHandler keyHandler;
    private final GraphicsContext gc;
    private final Communicator comm;
    private final GButton connect;
    private final GButton start;
    private final GButton quit;
    private final GButton difficulty_btn;
    private final Player player;
    private final Player enemy;
    private final List<List<Integer>> directions;
    private final Random random;
    private final HashMap<Integer, List<Integer>> enemyPositions;
    private final AudioClip success;
    private final AudioClip loss;
    private final AudioClip levelup;
    private final AudioClip shot;
    private final AudioClip hit;
    private final AudioClip kill;
    private int difficulty;
    private FileWriter write;
    private FileReader read;
    private int stage;
    private HashMap<String, String> inputs;
    private ArrayList<String> keys;
    private double score;
    private int angle;
    private int lastHit;
    private int timeOffset;
    private double speed;
    private Image background;
    private boolean first;
    private boolean canMove;
    private boolean canFire;
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
        
        player = new Player(gc, "/assets/player.png");
        enemy = new Player(gc, "/assets/enemy.png", -1, -1, 90, -1);
        
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
                timeOffset = 0;
                player.setHealth(100);
                enemy.setHealth(100);
                startNanos = System.nanoTime();
            }
        };
        
        quit = new GButton(gc, "/assets/quit.png", 960, 80){
            @Override
            public void onClick(){
                Platform.exit();
            }
        };
        
        difficulty = 1;
        difficulty_btn = new GButton(gc, "/assets/easy.png", 865, 720){
            @Override
            public void onClick(){
                difficulty += 1;
                switch(difficulty){
                    default:
                        difficulty = 1;
                    case 1:
                        setImg("/assets/easy.png");
                        break;
                    case 2:
                        setImg("/assets/medium.png");
                        break;
                    case 3:
                        setImg("/assets/hard.png");
                        break;
                }
            }
        };
        
        directions = Arrays.asList(
                    Arrays.asList(225, 180, 135),
                    Arrays.asList(270, 360, 90),
                    Arrays.asList(315, 0, 45));
        
        enemyPositions = new HashMap(){
            {
                put(3, Arrays.asList(480, 300));
                put(4, Arrays.asList(425, 300));
                put(5, Arrays.asList(885, 300));
                put(6, Arrays.asList(700, 175));
                put(7, Arrays.asList(315, 515));
                put(8, Arrays.asList(500, 600));
            }
        };
        
        success = new AudioClip(this.getClass().getResource("/assets/success.mp3").toExternalForm());
        loss = new AudioClip(this.getClass().getResource("/assets/loss.mp3").toExternalForm());
        shot = new AudioClip(this.getClass().getResource("/assets/shot.mp3").toExternalForm());
        levelup = new AudioClip(this.getClass().getResource("/assets/levelup.mp3").toExternalForm());
        hit = new AudioClip(this.getClass().getResource("/assets/hit.mp3").toExternalForm());
        kill = new AudioClip(this.getClass().getResource("/assets/kill.mp3").toExternalForm());
        
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
            levelup.stop();
            success.play(0.5);
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
                if(!inputs.isEmpty() && inputs.get("Start").equals("1") || keys.contains("Enter")){
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
            difficulty_btn.draw();
        }
        
        else if(stage > 0){ // Game Logic
            score = (double)Math.round(((nanos-startNanos)/1_000_000_000.0 - timeOffset)/(difficulty) * 10000d) / 10000d;
            gc.setFont(Font.font("Lucidia Fax", 18));
            gc.setFill(Color.color(1, 1, 1));
            gc.fillText("Ammo: " + player.getAmmo(), 10, 20);
            gc.fillText("Health: " + player.getHealth(), 100, 20);
            gc.fillText("Score: " + score, 10, 38);
            
            if(first){ // Check whether a new stage just loaded
                player.setXY(75, 350);
                player.setFacing(90);
                first = false;
                canMove = false;
                canFire = false;
                return;
            }
            if(keys.isEmpty() && (inputs.isEmpty() || Collections.frequency(inputs.values(), "0") == inputs.values().size())){
                canMove = true;
                canFire = true;
            }
            
            if(enemyPositions.containsKey(stage) && enemy.getHealth() != 0 && difficulty != 1){
                enemy.setXY(enemyPositions.get(stage).get(0), enemyPositions.get(stage).get(1), true);
                enemy.setFacing((int)Math.toDegrees(Math.atan2(enemy.getY() - player.getY(), enemy.getX() - player.getX())) - 90);
                enemy.draw();
                int chance = 0;
                if(difficulty == 2){
                    chance = 200;
                }else if(difficulty == 3){
                    chance = 125;
                }
                if(random.nextInt(chance) == 0){
                    enemy.fire();
                    shot.play(0.5);
                }
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
                    if(keys.contains("Space")){
                        if(canFire){
                            player.fire();
                            shot.play(0.5);
                            canFire = false;
                        }
                    }else{
                        canFire = true;
                    }
                    if(keys.contains("Esc")){
                        stage = 0;
                        player.halt();
                        enemy.halt();
                        first = true;
                        return;
                    }
                    if(keys.contains("F12")){
                        stage++;
                        player.halt();
                        enemy.halt();
                        enemy.setHealth(100);
                        first = true;
                        return;
                    }
                    
                    angle = directions.get(hor).get(ver);
                    if(angle == 360){
                        speed = 0;
                    }else{
                        speed = 7;
                    }
                }else{ // Controller connected and on; use controller
                    if(inputs.get("X").equals("1") && inputs.get("Y").equals("1") && inputs.get("Start").equals("1")){
                        stage++;
                        player.halt();
                        enemy.halt();
                        enemy.setHealth(100);
                        first = true;
                        return;
                    }
                    if(inputs.get("Start").equals("1")){
                        stage = 0;
                        player.halt();
                        enemy.halt();
                        first = true;
                        return;
                    }
                    if(inputs.get("A").equals("1") || inputs.get("B").equals("1")){
                        if(canFire){
                            player.fire();
                            shot.play(0.5);
                            canFire = false;
                        }
                    }else{
                        canFire = true;
                    }
                    
                    Double hor, ver;
                    hor = Double.parseDouble((String)inputs.get("joyX"));
                    ver = Double.parseDouble((String)inputs.get("joyY"));
                    if(!(hor == 0.0 && ver == 0.0)){
                        angle = (int)Math.toDegrees(Math.atan2(hor, ver));
                        speed = Math.sqrt(Math.pow(hor, 2) + Math.pow(ver, 2)) * 0.075;
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
                    levelup.play(0.5);
                    player.halt();
                    enemy.halt();
                    enemy.setHealth(100);
                    first = true;
                    return;
                }
                if(player.touchesColor(background.getPixelReader(), Color.color(0, 0, 0))){
                    player.setXY(x, y);
                }
            }
            player.draw();
            
            if(enemy.getHealth() != 0){
                ArrayList<Sprite> toRemove = new ArrayList<>();
                for(Sprite projectile : enemy.getProjectiles()){
                    if(player.touches(projectile)){
                        hit.play(0.5);
                        timeOffset -= 2;
                        toRemove.add(projectile);
                        player.damage(20);
                        if(player.getHealth() <= 0){
                            stage = -2;
                            loss.play(0.5);
                            player.halt();
                            enemy.halt();
                            enemy.setHealth(100);
                            first = true;
                            return;
                        }
                    }
                }
                enemy.removeProjectiles(toRemove);
                toRemove.clear();
                for(Sprite projectile : player.getProjectiles()){
                    if(enemy.touches(projectile)){
                        kill.play(0.5);
                        timeOffset += 3;
                        toRemove.add(projectile);
                        enemy.damage(100);
                    }
                }
                player.removeProjectiles(toRemove);
                toRemove.clear();
            }
            
        }else if(stage == -1){ // Victory
            if(first){
                playerName = "";
                err = "";
                canMove = false;
                first = false;
            }
            if(keys.isEmpty() && inputs.isEmpty()){
                canMove = true;
            }else if(keys.isEmpty() && !inputs.isEmpty() && Collections.frequency(inputs.values(), "0") == inputs.values().size()){
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
                        saveQuit();
                        return;
                    }
                }
                if(!inputs.isEmpty() && inputs.get("Start").equals("1")){
                    saveQuit();
                    return;
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
            gc.fillText("Score: " + score, 310, 322);
            
            List<Double> scores = new ArrayList<>(leaderboard.values());
            Collections.sort(scores);
            scores = scores.subList(0, Math.min(6, scores.size()));
            for(Double leaderScore : scores){
                for(String leader : leaderboard.keySet()){
                    if(leaderboard.get(leader).equals(leaderScore)){
                        int pos = scores.indexOf(leaderScore);
                        gc.setFont(Font.font("Lucida Fax", 26));
                        gc.fillText(pos+1 + ": " + leader + "\n", 780, 380 + pos * 40 + pos * 25);
                        gc.setFont(Font.font("Lucida Fax", 20));
                        gc.fillText(leaderScore + "\n\n", 780, 380 + pos * 40 + pos * 25 + 25);
                    }
                }
            }
            
        }else if(stage == -2){ // Game over
            if(first){
                canMove = false;
                first = false;
            }
            if(keys.isEmpty() && inputs.isEmpty()){
                canMove = true;
            }else if(keys.isEmpty() && !inputs.isEmpty() && Collections.frequency(inputs.values(), "0") == inputs.values().size()){
                canMove = true;
            }
            if(canMove){
                if(inputs.isEmpty() && keys.contains("Enter") || !inputs.isEmpty() && inputs.get("Start").equals("1")){
                    stage = 0;
                    lastHit = -1;
                    first = true;
                    return;
                }
            }
            
            gc.setFont(Font.font("Lucida Fax", 26));
            gc.fillText("Score: " + score, 310, 322);
        }
    }
    
    public void saveQuit(){
        err = "";
        if(!playerName.equals("")){
            leaderboard.put(playerName, score);
            try{
                write.write(playerName + ":" + score + ";");
                write.flush();
            }catch(NullPointerException e){
            }catch(IOException e){
                System.out.println(Arrays.toString(e.getStackTrace()));
            }
        }
        stage = 0;
        lastHit = -1;
        first = true;
    }
}
