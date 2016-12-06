package models;

import play.mvc.*;
import play.libs.*;
import play.libs.F.*;

import java.util.*;

public class SimpleChat{

    // collect all websockets here
    private static List<WebSocket.Out<String>> connections = new ArrayList<WebSocket.Out<String>>();
    
    public static void start(WebSocket.In<String> in, WebSocket.Out<String> out){
        
        connections.add(out);
        	
		in.onMessage((message) -> {
			SimpleChat.notifyAll(message);
		});
		
		in.onClose(() ->{
			//SimpleChat.notifyAll("A connection closed");
		});
		
    }
    
    // Iterate connection list and write incoming message
    public static void notifyAll(String message){
        for (WebSocket.Out<String> out : connections) {
            out.write(message);
        }
    }
}