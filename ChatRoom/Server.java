package ChatRoom;

import java.io.*;

	import java.net.*;

	import java.text.SimpleDateFormat;

	import java.util.*;

	public class Server {
	    private static int uqeId;//id for connection
	    private ArrayList<ThreadforClient> alist; //list of client
	    private SGui srvgui;	// for gui of server
	    private SimpleDateFormat datef; //time
	    private int p;	//port number 
	    private boolean stopserver; // act as switch to turn stop server
	    // constructor with port num to listen  as input parameter
	    public Server(int p) {
	        this(p, null);
	    }
	    public Server(int p, SGui srvgui) {
	        this.srvgui = srvgui; // if wanna create gui
	        this.p = p;	// port
	        datef = new SimpleDateFormat("HH:mm:ss"); // date format
	        alist = new ArrayList<ThreadforClient>(); // client list
	    }

	    public void StartSrv() {
	    	stopserver = true;
	    	// creating socket for server and waith for clients
	        try
	        {
	            ServerSocket svrSkt = new ServerSocket(p);// socket
	            while(stopserver)  // waiting for connections
	            {
	            	DisplayEvent(" waiting for Clients on port " + p + ".");
	                Socket skt = svrSkt.accept();      // accepting connection
	                if(!stopserver)// stopping (if asked)
	                    break;
	                ThreadforClient T = new ThreadforClient(skt);  // creating thread for client
	                alist.add(T);                                  // saving thread on list
	                T.start();
	            }
	            // to stop
	            try {
	            	svrSkt.close();
	                for(int i = 0; i < alist.size(); ++i) {
	                	ThreadforClient Tc = alist.get(i);
	                    try {
	                    Tc.sIn.close();
	                    Tc.sOut.close();
	                    Tc.skt.close();
	                    }
	                    catch(IOException E) { }
	                }
	            }
	            catch(Exception e) {
	            	DisplayEvent("error closing the server and clients: " + e);
	            }
	        }
	        catch (IOException e) {
	            String m = datef.format(new Date()) + " Exception on new ServerSocket: " + e + "\n";
	            DisplayEvent(m);
	        }
	    }      
	    // to stop server
	    protected void StopSrv() {
	    	stopserver = false;
	        try {
	            new Socket("localhost", p);
	        }
	        catch(Exception e) { }
	    }

	    // displaying event
	    private void DisplayEvent(String m) {
	        String t = datef.format(new Date()) + " " + m;
	        if(srvgui == null)
	            System.out.println(t);
	        else
	        	srvgui.changeEvent(t + "\n");
	    }
	    //messages to cliens
	    private synchronized void BroadcastMsg(String msg) {
	        String t = datef.format(new Date());
	        String msgLf = t + " " + msg + "\n";
	        if(srvgui == null)	//message  for output(gui)
	            System.out.print(msgLf);
	        else
	        	srvgui.changeRoom(msgLf);     // append in the chat room 
	         
	        for(int i = alist.size(); --i >= 0;) {// loop in reverse for disconnecting client
	        	ThreadforClient Ct = alist.get(i);
	            if(!Ct.writeMsg(msgLf)) {// msg to client
	            	alist.remove(i);
	            	DisplayEvent("Disconnected Client " + Ct.uname + " removed from list.");
	            }
	        }
	    }

	    //if logged out
	    synchronized void RemoveClient(int Id) {
	        for(int i = 0; i < alist.size(); ++i) {// searching for id and removing it
	        	ThreadforClient Ct = alist.get(i);
	            if(Ct.Id == Id) {
	            	alist.remove(i);
	                return;
	            }
	        }
	    }

	    public static void main(String[] args) {
	        int portNum = 1500;// default port number 1500
	        switch(args.length) {
	            case 1:
	                try {
	                    portNum = Integer.parseInt(args[0]);
	                }
	                catch(Exception e) {
	                    System.out.println("Invalid port number.");
	                    System.out.println("Usage is: > java Server [portNumber]");
	                    return;
	                }
	            case 0:
	                break;
	            default:
	                System.out.println("Usage is: > java Server [portNumber]");
	                return;
	                 
	        }
	        Server svr = new Server(portNum);// server object 
	        svr.StartSrv();	// starting it
	    }	 
	   // threads for clients(one to one ration)
	    class ThreadforClient extends Thread {
	        Socket skt;// for communication
	        ObjectInputStream sIn;
	        ObjectOutputStream sOut;
	        int Id;//unique id
	        String uname;//username
	        Message Cm;// message
	        String Date;//date
	 
	        // Constructor
	        ThreadforClient(Socket skt) {
	            Id = ++uqeId;
	            this.skt = skt;
	            //Creating data streams
	            System.out.println("Thread trying to create Object Input/Output Streams");
	            try
	            {
	                
	                sOut = new ObjectOutputStream(skt.getOutputStream());//first output
	                sIn  = new ObjectInputStream(skt.getInputStream());//input
	                uname = (String) sIn.readObject();//user name
	                DisplayEvent(uname + " just connected.");
	            }
	            catch (IOException e) {
	            	DisplayEvent("Exception creating new Input/output Streams: " + e);
	                return;
	            }
	            catch (ClassNotFoundException e) {
	            }
	            Date = new Date().toString() + "\n";
	        }	 
	       
	        public void run() {// loop waiting for logout
	            boolean stopserver = true;
	            while(stopserver) {
	                try {
	                	Cm = (Message) sIn.readObject();
	                }
	                catch (IOException e) {
	                	DisplayEvent(uname + " Exception reading Streams: " + e);
	                    break;             
	                }
	                catch(ClassNotFoundException e2) {
	                    break;
	                }
	                String m = Cm.getMsg();	 
	                switch(Cm.getType()) {
	                case Message.MESSAGE:
	                	BroadcastMsg(uname + ": " + m);
	                    break;
	                case Message.LOGOUT:
	                	DisplayEvent(uname + " disconnected with a LOGOUT message.");
	                    stopserver = false;
	                    break;
	                case Message.WHOISIN:
	                    writeMsg("List of the users connected at " + datef.format(new Date()) + "\n");
	                    for(int i = 0; i < alist.size(); ++i) {// scanning all user connceted
	                    	ThreadforClient ct = alist.get(i);
	                        writeMsg((i+1) + ") " + ct.uname + " since " + ct.Date);
	                    }
	                    break;
	                }
	            }
	           
	            RemoveClient(Id);// removing client from list 
	            CloseEvr();
	        }
	        private void CloseEvr() {	// closing everything
	            try {
	                if(sOut != null) sOut.close();
	            }
	            catch(Exception e) {}
	            try {
	                if(sIn != null) sIn.close();
	            }
	            catch(Exception e) {};
	            try {
	                if(skt != null) skt.close();
	            }
	            catch (Exception e) {}
	        }
	        //msg for client output
	        private boolean writeMsg(String m) {
	             if(!skt.isConnected()) {	// if connected send msg
	            	CloseEvr();
	                return false;
	            }
	            try {
	                sOut.writeObject(m);	//writing message
	            }
	            catch(IOException e) {
	            	DisplayEvent("Error sending message to " + uname);
	            	DisplayEvent(e.toString());
	            }
	            return true;
	        }
	    }
	}
