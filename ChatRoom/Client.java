package ChatRoom;

import java.net.*;

import java.io.*;

import java.util.*;

	public class Client  {
	 
	    private ObjectInputStream sIn;       // input from socket
	    private ObjectOutputStream sOut;     // output to socket
	    private Socket skt;
	    private CGui cgui;
	    private String svr, uname;	// server and username
	    private int p;	//port

	    Client(String server, int port, String username) {
	        this(server, port, username, null);
	    }
	    //constructor with parameters name of server port number username and is it gui 
	    Client(String svr, int p, String uname, CGui cgui) {
	        this.svr = svr;
	        this.p = p;
	        this.uname = uname;
	        this.cgui = cgui;
	    }
	     
	    public boolean StartClient() {
	        try {	// connect with server
	        	skt = new Socket(svr, p);
	        }
	        catch(Exception ec) {	// failed to connect
	        	DisplayMsg("Error connectiong to server:" + ec);
	            return false;
	        }
	         
	        String m = "Connection accepted " + skt.getInetAddress() + ":" + skt.getPort();
	        DisplayMsg(m);
	     
	        // input and output stream
	        try
	        {
	        	sIn  = new ObjectInputStream(skt.getInputStream());
	        	sOut = new ObjectOutputStream(skt.getOutputStream());
	        }
	        catch (IOException eIO) {
	        	DisplayMsg("Exception creating new Input/output Streams: " + eIO);
	            return false;
	        }
	        new ListenServer().start();	// thread to listen to server
	        try
	        {
	        	sOut.writeObject(uname);
	        }
	        catch (IOException eIO) {
	        	DisplayMsg("Exception doing login : " + eIO);
	            disconnect();
	            return false;
	        }
	        return true;// if everthing worked 
	    }
	    // message for client
	    private void DisplayMsg(String m) {
	        if(cgui == null)
	            System.out.println(m);     
	        else
	        	cgui.change(m + "\n");      //append client output area
	    }
	    // output for server 
	    void sendMsg(Message m) {
	        try {
	        	sOut.writeObject(m);
	        }
	        catch(IOException e) {
	        	DisplayMsg("Exception writing to server: " + e);
	        }
	    }
	    // to stop and disconnect it
	    private void disconnect() {
	        try {
	            if(sIn != null) sIn.close();
	        }
	        catch(Exception e) {} 
	        try {
	            if(sOut != null) sOut.close();
	        }
	        catch(Exception e) {} 
	        try{
	            if(skt != null) skt.close();
	        }
	        catch(Exception e) {} 

	        if(cgui != null)
	        	cgui.FailedConnection();
	             
	    }
	   
	    public static void main(String[] args) {
	        int portNum = 1500;	//default port 1500
	        String svraddress = "localhost";	// default server localhost 
	        String uName = "Anonymous";	// default user name
	 
	        switch(args.length) {	// depanding on number of arguments calling constructor
	            case 3:
	                svraddress = args[2];
	            case 2:
	                try {
	                    portNum = Integer.parseInt(args[1]);
	                }
	                catch(Exception e) {
	                    System.out.println("Invalid port number.");
	                    System.out.println("Usage is: > java Client [username] [portNumber] [serverAddress]");
	                    return;
	                }
	            case 1:
	                uName = args[0];
	            case 0:
	                break;
	            default:
	                System.out.println("Usage is: > java Client [username] [portNumber] {serverAddress]");
	            return;
	        }
	        Client c = new Client(svraddress, portNum, uName);	// creating client
	        if(!c.StartClient())	// to start connection
	            return;
	         
	        Scanner scanner = new Scanner(System.in);
	        while(true) {	// waiting for message 
	            System.out.print("> ");
	            String m = scanner.nextLine(); // getting msg from user
	            if(m.equalsIgnoreCase("LOGOUT")) { //checking if msg is logout
	                c.sendMsg(new Message(Message.LOGOUT, ""));
	                break;
	            }
	            else if(m.equalsIgnoreCase("WHOISIN")) {// is who is in 
	                c.sendMsg(new Message(Message.WHOISIN, ""));              
	            }
	            else {              // simple msg
	                c.sendMsg(new Message(Message.MESSAGE, m));
	            }
	        }
	        
	        c.disconnect();// disconnecting    
	    }
	 
	    // waiting for msg and sending it to output
	    class ListenServer extends Thread {
	 
	        public void run() {
	            while(true) {
	                try {
	                    String m = (String) sIn.readObject();
	                    if(cgui == null) {
	                        System.out.println(m);
	                        System.out.print("> ");
	                    }
	                    else {
	                    	cgui.change(m);
	                    }
	                }
	                catch(IOException e) {
	                	DisplayMsg("Server has close the connection: " + e);
	                    if(cgui != null)
	                    	cgui.FailedConnection();
	                    break;
	                }
	                catch(ClassNotFoundException e2) {
	                }
	            }
	        }
	    }
	}