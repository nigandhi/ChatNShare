package syr.edu.ChatServer;
/*
 * 
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 * Source: https://app.box.com/shared/130ph7fsbgrj1h69pksq
 * 
 * File: ChatServer.java
 * 
 * Functionalities:
 * Main chat activity of ChatnShare
 * Defines methods for Sending and Receiving chat messages
 * Spawns a new thread for FileTransfer class
 * 
 */

import java.io.*;
import java.net.*; 
import java.util.*;

public class ChatServer {

    private static final String USAGE = "Usage: java ChatServer";

    /** Default port number on which this server to be run. */
    private static final int PORT_NUMBER = 1337;

    /** List of print writers associated with current clients,
     * one for each. */
    private List<PrintWriter> clients;

    /** Creates a new server. */
    public ChatServer() {
        clients = new LinkedList<PrintWriter>();
    }

    /** Starts the server. */
    public void start() {
    	new Thread(new FileServer()).start();
    	System.out.println("Chat server started on port "+ PORT_NUMBER + "!");
        
        try {
            ServerSocket s = new ServerSocket(PORT_NUMBER); 
            while(true) {
                Socket incoming = s.accept(); 
                new Thread(new ClientHandler(incoming)).start();
                        
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Chat server stopped."); 
    }

    /** Adds a new client identified by the given print writer. */
    private void addClient(PrintWriter out) {
    	if(clients.contains(out))
    		return;
        synchronized(clients) {
            clients.add(out);
        }
    }

    /** Adds the client with given print writer. */
    private void removeClient(PrintWriter out) {
        synchronized(clients) {
            clients.remove(out);
        }
    }

    /** Broadcasts the given text to all clients. */
    private void broadcast(String msg, PrintWriter client ) {
    	for (PrintWriter out: clients) {
    		if(out!=client)
    		{
    			out.println(msg);
    			out.flush();
    		}
    	}
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            System.out.println(USAGE);
            System.exit(-1);
        }
       // new FileServer().start();
        new ChatServer().start();
        
    }

    /** A thread to serve a client. This class receive messages from a
     * client and broadcasts them to all clients including the message
     * sender. */
    private class ClientHandler extends Thread {

        /** Socket to read client messages. */
        private Socket incoming; 

        /** Creates a hander to serve the client on the given socket. */
        public ClientHandler(Socket incoming) {
            this.incoming = incoming;
        }

        /** Starts receiving and broadcasting messages. */
        public void run() {
            PrintWriter out = null;
            try {
                out = new PrintWriter(
                        new OutputStreamWriter(incoming.getOutputStream()));
                ChatServer.this.addClient(out);
                out.println("Welcome to ChatNShare! ");
                out.flush();
                BufferedReader in
                    = new BufferedReader(
                        new InputStreamReader(incoming.getInputStream())); 
                String msg="";
                while(true) {
                    msg += in.readLine();                    
                    if (msg == null) 
                    {
                        break; 
                    } 
                    else 
                    {
                        System.out.println("Received: " + msg);
                        ChatServer.this.broadcast(msg,out);
                    }
                }
                incoming.close(); 
                ChatServer.this.removeClient(out);
            } 
            catch (Exception e) 
            {
                if (out != null) {
                    ChatServer.this.removeClient(out);
                }
                e.printStackTrace(); 
            }
        }
    }
}