package ChatRoom;

import java.io.*;

	//The different types of message sent by the Client
	public class Message implements Serializable {
    protected static final long serialVersionUID = 1112122200L;
	    static final int WHOISIN = 0, MESSAGE = 1, LOGOUT = 2;//whoisin for list of user connected
	    //message for message and logout for logging out
	    private int t;
	    private String m;
	    
	    Message(int t, String m) {
	        this.t = t;
	        this.m = m;
	    }
	     
	    // getters
	    int getType() {
	        return t;
	    }
	    String getMsg() {
	        return m;
	    }
	}