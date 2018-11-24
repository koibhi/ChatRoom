package ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SGui extends JFrame implements ActionListener, WindowListener {
	    private static final long serialVersionUID = 1L;
	    private JButton ReStart;	// button for stopping and starting server
	    private JTextArea chatroom, evnt;//are for event and chat room
	    private JTextField PortNum;	//portnumber
	    private Server svr; //server
	    // constructor with port(to listen) number as parameter 
	    SGui(int p) {

			super("Server");
			svr = null;
	        JPanel north = new JPanel();
	        north.add(new JLabel("Port number: "));
	        PortNum = new JTextField("  " + p);
	        north.add(PortNum);// adding portnumber to north of panel
	        ReStart = new JButton("Begin");
	        ReStart.addActionListener(this);
	        north.add(ReStart);	// adding restart to north of panel
	        add(north, BorderLayout.NORTH);
	        
	        JPanel center = new JPanel(new GridLayout(2,1));
	        chatroom = new JTextArea(80,80);
	        chatroom.setEditable(false);
	        changeRoom("Chat room.\n");
	        center.add(new JScrollPane(chatroom));// adding chat room to center panel
	        evnt = new JTextArea(80,80);
	        evnt.setEditable(false);
	        changeEvent("Events log.\n");
	        center.add(new JScrollPane(evnt));	// adding event to center panel
	        add(center);
	   
			addWindowListener(this);// to listen to action
	        setSize(400, 600);
	        setVisible(true);
	    }      
	    void changeRoom(String str) {	// append message and put it at end 
	        chatroom.append(str);
	        chatroom.setCaretPosition(chatroom.getText().length() - 1);
	    }
	    void changeEvent(String str) {	//// append message and put it at end 
	        evnt.append(str);
	        evnt.setCaretPosition(chatroom.getText().length() - 1);
	    }
	    // begin and stop is clicked
	    public void actionPerformed(ActionEvent e) {
	        // if already running stop it
	        if(svr != null) {
	        	svr.StopSrv();
	        	svr = null;
	            PortNum.setEditable(true);
	            ReStart.setText("Begin");
	            return;
	        }
	        // begin server
	        int p;
	        try {
	            p = Integer.parseInt(PortNum.getText().trim());
	        }
	        catch(Exception er) {
	        	changeEvent("Invalid port number");
	            return;
	        }
	        svr = new Server(p, this);	//  new server
	        new ServerRun().start();	// begin server as thread
	        ReStart.setText("Stop");
	        PortNum.setEditable(false);
	    }
	    // place to enter server
	    public static void main(String[] arg) {
	        new SGui(1500); // starting server at defauld port (1500)
	    }
	    // closing connection with server to free ports if app is closed
	    public void windowClosing(WindowEvent e) {
	        // if  server exist
	        if(svr != null) {
	            try {
	            	svr.StopSrv();          // asking  it  to close the connections
	            }
	            catch(Exception eClose) {
	            }
	            svr = null;
	        }
	        // disposing the frame
	        dispose();
	        System.exit(0);
	    }
	    // i hate these methods so not gonna use it ( i dont need it)
	    public void windowClosed(WindowEvent e) {}
	    public void windowOpened(WindowEvent e) {}
	    public void windowIconified(WindowEvent e) {}
	    public void windowDeiconified(WindowEvent e) {}
	    public void windowActivated(WindowEvent e) {}
	    public void windowDeactivated(WindowEvent e) {}
	    // thread for running server
	    class ServerRun extends Thread {
	        public void run() {
	        	svr.StartSrv();         //trying to start server
	            ReStart.setText("Begin");	// if failed to start
	            PortNum.setEditable(true);
	            changeEvent("Server crashed\n");
	            svr = null;
	        }
	    }
	}
