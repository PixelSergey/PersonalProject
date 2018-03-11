package me.sergey.communication;

import java.util.Scanner;
import com.fazecast.jSerialComm.*;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

public class Communicator extends Thread{
    private SerialPort serialPort;
    private Scanner scanner;
    private String data;
    private HashMap<String, String> buttons;
    private final Gson gson;
    
    public Communicator(){
        buttons = new HashMap<>();
        gson = new Gson();
    }
    
    public boolean connect(){
        SerialPort[] ports = SerialPort.getCommPorts();
        
        int i;
        Scanner s;
        
        if(ports.length == 0){
            System.out.println("No serial devices found.");
            return false;
        }
        
        i = 1;
        System.out.println("Select a port:");
        for(SerialPort port : ports){
            System.out.println(i++ + ": " + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")");
        }
        
        s = new Scanner(System.in);
        int chosenPort = s.nextInt();
        
        try{
            serialPort = ports[chosenPort - 1];
        }catch(ArrayIndexOutOfBoundsException e){
            System.out.println("Invalid port number.");
            return false;
        }
        
        String[] timeouts = {"TIMEOUT_READ_NONBLOCKING","TIMEOUT_READ_SEMI_BLOCKING","TIMEOUT_READ_BLOCKING", "TIMEOUT_SCANNER"};
        System.out.println("Select a timeout: (different for each computer, start by trying TIMEOUT_SCANNER if unsure)");
        i = 1;
        for(String timeout : timeouts){
            System.out.println(i++ + ": " + timeout);
        }
        
        int chosenTimeout = s.nextInt();
        if(serialPort.openPort()){
            System.out.println("Port opened successfully.");
        }else{
            System.out.println("Unable to open the port.");
            return false;
        }
        
        System.out.println("Setting timeout...");
        int timeout;
        switch(chosenTimeout){
            case(1): timeout = SerialPort.TIMEOUT_NONBLOCKING; break;
            case(2): timeout = SerialPort.TIMEOUT_READ_SEMI_BLOCKING; break;
            case(3): timeout = SerialPort.TIMEOUT_READ_BLOCKING; break;
            default:
            case(4): timeout = SerialPort.TIMEOUT_SCANNER; break;
        }
        
        serialPort.setComPortTimeouts(timeout, 0, 0);
        scanner = new Scanner(serialPort.getInputStream());
        data = "";
        
        System.out.println("Connection successful");
        return true;
    }
    
    public HashMap<String, String> receive(){
        return buttons;
    }

    @Override
    public void run(){
        connect();
        while(true){
            try{
                data = scanner.nextLine();
                if(data.equals("OFF")){
                    buttons = new HashMap();
                }else{
                    buttons = gson.fromJson(scanner.nextLine(), HashMap.class);
                }
            }catch(NullPointerException e){
            }catch(JsonSyntaxException e){
                System.out.println(e.getMessage());
            }
        }
    }  
}