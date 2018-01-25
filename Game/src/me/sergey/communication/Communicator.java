package me.sergey.communication;

import java.util.Scanner;
import com.fazecast.jSerialComm.*;
import java.util.HashMap;
import com.google.gson.Gson;

public class Communicator {
    private SerialPort serialPort;
    private Scanner data;
    private HashMap buttons;
    private Gson gson;
    
    public Communicator(){
        buttons = new HashMap();
        gson = new Gson();
    }
    
    public boolean connect(){
        SerialPort[] ports = SerialPort.getCommPorts();
        
        if(ports.length == 0){
            return false;
        }
        
        System.out.println("Select a port:");
        int i = 1;
        for(SerialPort port : ports){
            System.out.println(i++ + ": " + port.getSystemPortName() + " (" + port.getDescriptivePortName() + ")");
        }
        
        Scanner s = new Scanner(System.in);
        int chosenPort = s.nextInt();

        serialPort = ports[chosenPort - 1];
        if(serialPort.openPort()){
            System.out.println("Port opened successfully.");
        }else{
            System.out.println("Unable to open the port.");
            return false;
        }
        
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
        data = new Scanner(serialPort.getInputStream());
        
        return true;
    }
    
    public HashMap receive(){
        try{
            buttons = gson.fromJson(data.nextLine(), HashMap.class);
        }catch(Exception e){}
        
        return buttons;
    }
}