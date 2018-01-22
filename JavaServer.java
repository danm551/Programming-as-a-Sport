import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class JavaServer {

    private static int port = 3000;
    
    private static ServerSocket listener;
    private static HashSet<String> names = new HashSet<String>();
    private static HashSet<String> finished = new HashSet<String>();
    private static HashSet<String> playersOnline = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private static PrintWriter refereeOut;
    
    static boolean constraintWinner = false;
    static JFrame frame;
    static JPanel mainPanel, topPanel, bottomPanel, logoPanel;
    static JLabel runningLabel, logoLabel, portLabel, ipLabel;
    static JButton btn, refereeStatus, p1Status, p2Status;
    static ImageIcon logoImage;
    static Font serverFont;
    static float p1Percent = 200, p2Percent = 200;
    static String winner = null, p1FinalData = null, p2FinalData= null, p1DiffData = null, p2DiffData = null;
    static File frameIcon;
    static JTextField ipAddress;

    public static void main(String[] args) throws Exception {
    	
        /***** Port prompt *****/
    	try{
	    	port = Integer.parseInt((String)JOptionPane.showInputDialog(
	    			frame, 
	    			"Enter the port number:", 
	    			"Port Selection",
	    			JOptionPane.WARNING_MESSAGE,
	    			null,
	    			null,
	    			"3000"
	    			));
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(
        			new JFrame(), 
        			"Cannot use port " + port, 
        			"Port Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
    		System.exit(1);
    	}
        
        buildGUI();
        
        try{
        	listener = new ServerSocket(port);
        }
        catch(Exception e){
			   JOptionPane.showMessageDialog(new JFrame(),
					    "Socket in use.",
					    "Socker Error",
					    JOptionPane.ERROR_MESSAGE
			   );
			   System.exit(0);
        }
        
        try {
            while (true) {
                new Handler(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }
    
	private static void buildGUI(){
		frame = new JFrame("Server");
		frame.setSize(500,700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		
		try{
			BufferedImage icon = ImageIO.read(JavaServer.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.WHITE);
		
		runningLabel = new JLabel();
		runningLabel.setText("<html><font color='black'>Server:</font> <font color='rgb(0, 179, 0)' >RUNNING</font></html>");
		serverFont = new Font("Serif", 1, 20);
		runningLabel.setFont(serverFont);
		runningLabel.setForeground(new Color(0, 179, 0));
		
		logoLabel = new JLabel();
		logoImage = new ImageIcon(JavaServer.class.getClassLoader().getResource("logo_server.png"));
		logoLabel.setIcon(logoImage);
		
		ipLabel = new JLabel("IP Address: ");
		ipLabel.setForeground(Color.BLACK);
		ipLabel.setFont(serverFont);
		
		ipAddress = new JTextField();
		ipAddress.setBorder(null);
		ipAddress.setEditable(false);
		ipAddress.setOpaque(false);
		ipAddress.setForeground(new Color(0, 179, 0));
		ipAddress.setFont(serverFont);
		try{
			ipAddress.setText(InetAddress.getLocalHost().toString());
		}
		catch(Exception e){
			e.printStackTrace();
			ipLabel.setText("Could not determine your IP address.");
			ipLabel.setForeground(new Color(179, 0, 0));
		}
		
		portLabel = new JLabel("<html><font color='black'>Port: </font>" + "<font color= 'rgb(0, 179, 0)'>" + port + "</font></html>");
		portLabel.setForeground(new Color(0, 179, 0));
		portLabel.setFont(serverFont);
		
		btn = new JButton("<html><p style='font-family:Stencil; font-size:12px; text-align:center'>Shutdown</p></html>");
		btn.setPreferredSize(new Dimension(130, 75));
		btn.setBackground(new Color(255,90,0)); 
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
		btn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				System.exit(0);
			}
		});
		btn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(new Color(125,45,0));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(new Color(255,90,0));
			}
		});
		
		refereeStatus = new JButton("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>Referee<br>OFFLINE</p></html>");
		refereeStatus.setPreferredSize(new Dimension(130, 200));
		refereeStatus.setBackground(new Color(179, 0, 0));
		refereeStatus.setForeground(Color.WHITE);
		
		p1Status = new JButton("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P1<br>OFFLINE</p></html>");
		p1Status.setPreferredSize(new Dimension(130, 200));
		p1Status.setBackground(new Color(179, 0, 0));
		p1Status.setForeground(Color.WHITE);
		
		p2Status = new JButton("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P2<br>OFFLINE</p></html>");
		p2Status.setPreferredSize(new Dimension(130, 200));
		p2Status.setBackground(new Color(179, 0, 0));
		p2Status.setForeground(Color.WHITE);

		SpringLayout springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);
		mainPanel.add(logoLabel);
		mainPanel.add(runningLabel);
		mainPanel.add(btn);
		mainPanel.add(ipLabel);
		mainPanel.add(ipAddress);
		mainPanel.add(portLabel);
		mainPanel.add(refereeStatus);
		mainPanel.add(p1Status);
		mainPanel.add(p2Status);
		
		springLayout.putConstraint(SpringLayout.WEST, logoLabel, 0, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, logoLabel, 0, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, runningLabel, 25, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, runningLabel, 100, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, ipLabel, 25, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, ipLabel, 150, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, ipAddress, 130, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, ipAddress, 150, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, portLabel, 25, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, portLabel, 200 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, refereeStatus, 25, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, refereeStatus, 270 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, p1Status, 175, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, p1Status, 270 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, p2Status, 325, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, p2Status, 270 , SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, btn, 175, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, btn, 550 , SpringLayout.NORTH, mainPanel);
		
		frame.add(mainPanel);
		frame.setVisible(true);
	}
    
	private static class Handler extends Thread {
		private String name;
	    private Socket socket;
	    private BufferedReader in;
	    private PrintWriter out;
	
	   public Handler(Socket socket) {
		   this.socket = socket;
	   }
	
	   public void run() {
		   try {
			   in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	           out = new PrintWriter(socket.getOutputStream(), true);
	
	           //gets name from client and stores it in 'names'
	           while (true) {
	        	   out.println("GETID");
	               name = in.readLine();
	               if (name == null) {
	            	   return;
	               }
	               synchronized (names) {
	            	   if (!names.contains(name)) {
	            		   names.add(name);
	            		   switch(name){
		            		   case "PLAYERONE": 
		            			   p1Status.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P1<br>ONLINE</p></html>");
		            			   p1Status.setBackground(new Color(0, 179, 0));
		            			   p1Status.setForeground(Color.BLACK);
		            			   playersOnline.add("PLAYERONE");
		            			   if(names.contains("REFEREE")){
		            				   refereeOut.println("P1ONLINE");
		            			   }
		            			   break;
		            		   case "PLAYERTWO": 
		            			   p2Status.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P2<br>ONLINE</p></html>");
		            			   p2Status.setBackground(new Color(0, 179, 0));
		            			   p2Status.setForeground(Color.BLACK);
		            			   playersOnline.add("PLAYERTWO");
		            			   if(names.contains("REFEREE")){
		            				   refereeOut.println("P2ONLINE");
		            			   }
		            			   break;
		            		   case "REFEREE": 
		            			   refereeStatus.setBackground(new Color(0, 179, 0));
		            			   refereeStatus.setForeground(Color.BLACK);
		            			   refereeStatus.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>Referee<br>ONLINE</p></html>");
		            			   refereeOut = new PrintWriter(socket.getOutputStream(), true);
		            			   if(playersOnline.contains("PLAYERONE")){
		            				   refereeOut.println("P1ONLINE");
		            			   }
		            			   if(playersOnline.contains("PLAYERTWO")){
		            				   refereeOut.println("P2ONLINE");
		            			   }
		            			   break;
		            		   default: 
		            			   System.out.println("Name switch statement error.");
		            			   break;
		            		   }
	            		   
	            		   break;
	                   }
	               }
	           }
	           
	           //add writer to set of writers
	           out.println(name + " HAS LOGGED IN");
	           writers.add(out);
	
	           //processes incoming protocols
	           while (true) {
	        	   String input = in.readLine();
	               if (input == null) {
	            	   return;
	               }
	               //sends image name to players
	               else if(input.startsWith("option")){
	            	   for(PrintWriter writer : writers){
	            		   writer.println(input);
	            	   }
	               }
	               else if(input.startsWith("PERCENTAGEONE") || input.startsWith("PERCENTAGETWO")){
	            	   if(input.startsWith("PERCENTAGEONE")){
	            		   String substring = input.substring(13);
	            		   p1Percent = Float.parseFloat(substring);
	            		   isResultPacketReady();
	            	   }
	            	   else if(input.startsWith("PERCENTAGETWO")){
	            		   String substring = input.substring(13);
	            		   p2Percent = Float.parseFloat(substring);
	            		   isResultPacketReady();
	            	   }
	            	   
	            	   if(p1Percent != 200 && p2Percent != 200){
	            		   if(p2Percent > p1Percent){
	            			   winner = "P1";
	            			   isResultPacketReady();
	            		   }
	            		   else if(p1Percent > p2Percent){
	            			   winner = "P2";
	            			   isResultPacketReady();
	            		   }
	            		   else{
	            			   winner = "TIE";
	            			   isResultPacketReady();
	            		   }
	            	   }
	               }
	               else if(input.startsWith("1data:image")){
	            	  p1FinalData = input.substring(1);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("2data:image")){
	            	  p2FinalData = input.substring(1);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("d1data:image")){
	            	  p1DiffData = input.substring(2);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("d2data:image")){
	            	  p2DiffData = input.substring(2);
	            	  isResultPacketReady();
	               }
	               else if(input.startsWith("REFOFFLINE")){
	            	  refereeStatus.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>Referee<br>OFFLINE</p></html>");
	           		  refereeStatus.setBackground(new Color(179, 0, 0));
	           		  refereeStatus.setForeground(Color.WHITE);
	            	  refereeOut.println("REFOFFLINE");
	            	  names.remove("REFEREE");
		           	  p1Percent = 200;
		        	  p1FinalData = null;
		        	  p1DiffData = null;
	               }
	               else if(input.startsWith("P1OFFLINE")){
	            	   p1Status.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P1<br>OFFLINE</p></html>");
        			   p1Status.setBackground(new Color(179, 0, 0));
        			   p1Status.setForeground(Color.WHITE);
	            	   refereeOut.println("P1OFFLINE");
	            	   names.remove("PLAYERONE");
	            	   playersOnline.remove("PLAYERONE");
		           	   p1Percent = 200;
		        	  p1FinalData = null;
		        	  p1DiffData = null;
	               }
	               else if(input.startsWith("P2OFFLINE")){
	            	   p2Status.setText("<html><p style='font-family:Stencil; font-size:15px; text-align:center'>P2<br>OFFLINE</p></html>");
        			   p2Status.setBackground(new Color(179, 0, 0));
        			   p2Status.setForeground(Color.WHITE);
	            	   refereeOut.println("P2OFFLINE");
	            	   names.remove("PLAYERTWO");
	            	   playersOnline.remove("PLAYERTWO");
		           	   p1Percent = 200;
		        	  p1FinalData = null;
		        	  p1DiffData = null;
	               }
	               else if(input.startsWith("P1WINS")){
	            	   if(constraintWinner == false){
	            		   constraintWinner = true;
	            		   for(PrintWriter writer : writers){
	            			   writer.println("P1ENDGAME");
	            		   }
	            	   }
	               }
	               else if(input.startsWith("P2WINS")){
	            	   if(constraintWinner == false){
	            		   constraintWinner = true;
	            		   for(PrintWriter writer : writers){
	            			   writer.println("P2ENDGAME");
	            		   }
	            	   }
	               }
	               
	               for (PrintWriter writer : writers) {
	            	   writer.println("MESSAGE " + name + ": " + input);
	               }
	           }
	       }catch (IOException e) {
	    	   e.printStackTrace();
	       } finally {
	    	   //socket closing procedures
	    	   if (name != null) {
	    		   names.remove(name);
	           }
	           if (out != null) {
	        	   writers.remove(out);
	           }
	           try {
	        	   socket.close();
	           } 
	           catch (IOException e) {
	           }
	         }
	   }
	}
	
	public static float compareImage(File fileA, File fileB) {

	    float percentage = 0;
	    try {
	        // take buffer data from both image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        DataBuffer dbA = biA.getData().getDataBuffer();
	        int sizeA = dbA.getSize();
	        BufferedImage biB = ImageIO.read(fileB);
	        DataBuffer dbB = biB.getData().getDataBuffer();
	        int sizeB = dbB.getSize();
	        int count = 0;
	        // compare data-buffer objects //
	        if (sizeA == sizeB) {

	            for (int i = 0; i < sizeA; i++) {

	                if (dbA.getElem(i) == dbB.getElem(i)) {
	                    count = count + 1;
	                }

	            }
	            percentage = (count * 100) / sizeA;
	        } else {
	            System.out.println("Both the images are not of same size");
	        }

	    } catch (Exception e) {
	        System.out.println("Failed to compare image files ...");
	    }
	    return percentage;
	}
	
	private static void isResultPacketReady(){
		
	    if(p1Percent < 200 && p2Percent< 200 && winner != null && p1FinalData != null && p2FinalData != null && p1DiffData != null && p2DiffData != null){
	    	for(PrintWriter writer : writers){
	    		finished.clear();
	    		writer.println("PACKET" + p1Percent + "SPLIT" + p2Percent + "SPLIT" + winner + "SPLIT" + p1FinalData + "SPLIT" + p2FinalData + "SPLIT" + p1DiffData + "SPLIT" + p2DiffData);
	    	}
	    	
    		p1Percent = 200;
    		p2Percent = 200;
    		winner = null;
    		p1FinalData = null;
    		p2FinalData = null;
    		p1DiffData = null;
    		p2DiffData = null;
    		constraintWinner = false;
	    }
	}
}