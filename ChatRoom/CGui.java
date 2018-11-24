package ChatRoom;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
	
	public class CGui extends JFrame implements ActionListener {
	    private static final long serialVersionUID = 1L;
	    private JLabel l;
	    private JTextField Tf; //first msg will be user name rest is message
	    private JTextField tS, tP;//  svr address and  port num
	    private JButton lin, lout, wisin;// login,logout,and connected (who is in)
	    private JTextArea a;// area for chat room
	    private boolean isconct;// if connected
	    private Client C;// client
	    private int defltP;// default port (1500)
	    private String defltH;//default host 
	 
	    //if constructor receive  socket num
	    CGui(String h, int p) {
	 
	        super("Client");
	        defltP = p;
	        defltH = h;
	        JPanel nPanel = new JPanel(new GridLayout(3,1));// panel in north 
	        JPanel SndP = new JPanel(new GridLayout(1,5, 1, 3)); //location of srv and port
	        tS = new JTextField(h);//default srv address
	        tP = new JTextField("" + p);//default port num
	        tP.setHorizontalAlignment(SwingConstants.RIGHT);

	        SndP.add(new JLabel("Server Address:  "));
	        SndP.add(tS);
	        SndP.add(new JLabel("Port Number:  "));
	        SndP.add(tP);
	        SndP.add(new JLabel(""));
	        nPanel.add(SndP);// adding srv and port 
	        l = new JLabel("Please enter your username ", SwingConstants.CENTER);
	        nPanel.add(l);//adding label
	        Tf = new JTextField("Anonymous");
	        Tf.setBackground(Color.WHITE);
	        nPanel.add(Tf);//adding textfield
	        add(nPanel, BorderLayout.NORTH);

	        a = new JTextArea("Welcome to my Chat room\n", 80, 80);//chat room area
	        JPanel cPanel = new JPanel(new GridLayout(1,1));
	        cPanel.add(new JScrollPane(a));
	        a.setEditable(false);
	        add(cPanel, BorderLayout.CENTER);
	 
	        lin = new JButton("Login");//login button
	        lin.addActionListener(this);
	        lout = new JButton("Logout"); //logout button
	        lout.addActionListener(this);
	        lout.setEnabled(false);       // can only logout if logged in
	        wisin = new JButton("Who is in");//who is in button
	        wisin.addActionListener(this);
	        wisin.setEnabled(false);      // can check whoisin only if logged in
	        JPanel sPanel = new JPanel();
	        sPanel.add(lin);	// adding login
	        sPanel.add(lout);	//adding logout
	        sPanel.add(wisin);	//adding who is in
	        add(sPanel, BorderLayout.SOUTH);
	        setDefaultCloseOperation(EXIT_ON_CLOSE);
	        setSize(600, 600);
	        setVisible(true);
	        Tf.requestFocus();
		    }
	    // client call to change text in textarea
	    void change(String str) {
	        a.append(str);
	        a.setCaretPosition(a.getText().length() - 1);
	    }
	    // if connection failed  we resetting  our buttons, label, textfield
	    void FailedConnection() {
	    	lin.setEnabled(true);
	    	lout.setEnabled(false);
	    	wisin.setEnabled(false);
	        l.setText("Please enter your username ");
	        Tf.setText("Anonymous");  
	        tP.setText("" + defltP);//resetting port
	        tS.setText(defltH);//resetting host
	        tS.setEditable(false);	//letting user to change them
	        tP.setEditable(false);
	        Tf.removeActionListener(this); //dont react to <cr> after name
	        isconct = false;
	    }
		  // if action is performed at button or textfield
	    public void actionPerformed(ActionEvent e) {
	        Object o = e.getSource();
	        if(o == lout) {		// logout button
	            C.sendMsg(new Message(Message.LOGOUT, ""));
	            return;
	        }
	        if(o == wisin) {	// who is in button
	            C.sendMsg(new Message(Message.WHOISIN, ""));              
	            return;
	        }
	        // or text field
	        if(isconct) {
	            C.sendMsg(new Message(Message.MESSAGE, Tf.getText())); // send msg           
	            Tf.setText("");
	            return;
	        }
	        if(o == lin) {
	            String uname = Tf.getText().trim();	// if connection request
	            if(uname.length() == 0)	//clear username
	                return;
	            String svr = tS.getText().trim();	//ignore srv address if empty
	            if(svr.length() == 0)
	                return;
	            String portNum = tP.getText().trim(); // ignore if port  num is wrong or blank
	            if(portNum.length() == 0)
	                return;
	            int p = 0;
	            try {
	                p = Integer.parseInt(portNum);
	            }
	            catch(Exception en) {	// now port number is worng
	                return;   
	            }
	            C = new Client(svr, p, uname, this);// creating new client
	            if(!C.StartClient())	// trying to start client
	                return;
	            Tf.setText("");
	            l.setText(" Please enter your message below");
	            isconct = true;
	            lin.setEnabled(false);	// disabling login
	            lout.setEnabled(true);// enabling logout
	            wisin.setEnabled(true);// enabling whoisin
	            tS.setEditable(false);	//disabling server field
	            tP.setEditable(false);	//disabling port field
	            Tf.addActionListener(this);// listner for message from user
	        }
	    }
	// starting process
	    public static void main(String[] args) {
	        new CGui("localhost", 1500);
	    }
	}