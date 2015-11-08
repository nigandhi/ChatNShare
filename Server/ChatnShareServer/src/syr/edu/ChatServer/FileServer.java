package syr.edu.ChatServer;
/*
 * 
 * CSE651 - Mobile Application Programming
 * Final Project - ChatNShare
 * Created by Nikhita Chandra/Nirav Gandhi/Sangeeta Gill on 15-Apr-15.
 * Source: https://app.box.com/shared/130ph7fsbgrj1h69pksq
 * File: FileServer.java
 * 
 * 
 * Functionalities:
 * Defines methods for Sending and Receiving files
 * 
 */
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class FileServer extends Thread
{
	private static final int PORT_NUMBER = 1339;

	/** List of print writers associated with current clients,
	 * one for each. */
	private ArrayList<OutputStream> clients;

	/** Creates a new server. */
	public FileServer() {
		clients = new ArrayList<OutputStream>();
	}

	public void run()
	{
		start();
	}
	/** Starts the server. */
	public void start() {
		System.out.println("File server started on port "+ PORT_NUMBER + "!"); 
		try {
			ServerSocket s = new ServerSocket(PORT_NUMBER); 
			while(true) {
				Socket incoming = s.accept(); 
				new ClientHandler(incoming).start(); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Chat server stopped."); 
	}

	/** Adds a new client identified by the given print writer. */
	private void addClient(OutputStream out) {
		
		if(clients.contains(out))
			return;
		System.out.println("New Client Connected..!!");
		synchronized(clients) {
			clients.add(out);
		}
	}

	/** Adds the client with given print writer. */
	private void removeClient(OutputStream out) {
		synchronized(clients) {
			clients.remove(out);
		}
	}

	/** Broadcasts the given text to all clients. 
	 * @throws IOException */
	private void broadcast(String file, OutputStream client ) throws IOException {
		file+="\n";
		byte[] filename=file.getBytes();
		String file_=file.substring(0, file.lastIndexOf('\n'));
		File myFile = new File (file_);
		byte [] mybytearray  = new byte [(int)myFile.length()];
		FileInputStream fis = new FileInputStream(myFile);
		BufferedInputStream bis;
		bis = new BufferedInputStream(fis);
		bis.read(mybytearray,0,mybytearray.length);
		String end="End"+"\n";
		byte[] end_=end.getBytes();
		
		for (OutputStream out: clients) {
			//if(out!=client)
			{
				System.out.println(clients.size());
				try {
					out.write(filename,0,filename.length);
					out.flush();
					Thread.sleep(100);
					System.out.println("Waiting...");				

					out.write(mybytearray, 0, mybytearray.length);
					out.flush();
					Thread.sleep(100);
					out.write(end_,0,end_.length);
					out.flush();
					System.out.println("Done."+mybytearray.length);
					Thread.sleep(100);
				}
				catch(Exception e){
					System.out.println("Exception form Broadcast...!!");
					e.printStackTrace();
				}
			}
		}
		bis.close();
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
			int byteRead =0;
			while(true)
			{
				OutputStream out;
				try
				{
					out = incoming.getOutputStream();
					FileServer.this.addClient(out);
					BufferedReader br = new BufferedReader(new InputStreamReader(incoming.getInputStream()));
					while(!br.ready()){}
					String filename = br.readLine();
					if(filename==null)
						continue;
					System.out.println("File name : "+filename);

					InputStream in = incoming.getInputStream();
					OutputStream os = new FileOutputStream(filename);
					byte[] byteArray = new byte[1024];
					int bytes=0;
					while((byteRead = in.read(byteArray, 0, byteArray.length))!= -1 )
					{
						String temp=new String(byteArray,"UTF-8");
						if(temp.contains("End")){
							System.out.println("File Received"+filename);
							break;
						}
						os.write(byteArray, 0, byteRead);
					}
					synchronized(os){
						os.wait(100);
					}
					os.close();
					System.out.println("File Received...total bytes:"+bytes);
					broadcast(filename, out);
				}
				catch (Exception e) 
				{
					System.out.println("Exception.");
					e.printStackTrace(); 
				}
			} 

		}
	}

}
