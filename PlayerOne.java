import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Time;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class PlayerOne {
	static boolean run = true;
	static private JFrame frame;
	static private JPanel mainPanel;
	static private JButton startBtn, exitBtn, connectBtn;
	static private SpringLayout springLayout;
	static private JLabel logo, IPText, portText;
	static private Font btnFont, textFont;
	static private JTextField IP, port;
	
	static private BufferedReader in;
	static private PrintWriter out;
	static private Socket socket;
	
	public static void main(String[] args){
		buildMenu();
	}
	
	private static void buildMenu(){
		frame = new JFrame("Player One");
		frame.setSize(500, 700);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainPanel = new JPanel();
		springLayout = new SpringLayout();
		mainPanel.setLayout(springLayout);
		
		logo = new JLabel(new ImageIcon("gui_images/logo_server.png"));
		
		btnFont = new Font("Stencil", 0 , 18);

		startBtn = new JButton("New Game");
		startBtn.setPreferredSize(new Dimension(125,50));
		startBtn.setBackground(new Color(255,90,0));
		startBtn.setForeground(Color.WHITE);
		startBtn.setFont(btnFont);
		startBtn.setFocusPainted(false);
		startBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				frame.setVisible(false);
				frame.revalidate();
				frame.repaint();
				
				ProcessBuilder process = new ProcessBuilder("java", "-jar", "ClientOne.jar", IP.getText(), port.getText()); //change
				try {
					Process p = process.start();
					p.waitFor();
					frame.setVisible(true);
					frame.revalidate();
					frame.repaint();
				} catch (IOException | InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		exitBtn = new JButton("Exit");
        exitBtn.setPreferredSize(new Dimension(125,50));
        exitBtn.setBackground(new Color(255,90,0));
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFont(btnFont);
		exitBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		connectBtn = new JButton("Connect");
		connectBtn.setPreferredSize(new Dimension(125,50));
		connectBtn.setBackground(new Color(255,40,0));
		connectBtn.setForeground(Color.WHITE);
		connectBtn.setFont(btnFont);
		connectBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					run();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		textFont = new Font("Calibri", 1, 18);
		
		IP = new JTextField("localhost", 10);
		IP.setFont(textFont);
		IPText = new JLabel("IP Address");
		IPText.setFont(btnFont);
		
		port = new JTextField("3000", 10);
		port.setFont(textFont);
		portText = new JLabel("Port");
		portText.setFont(btnFont);
		
		
		springLayout.putConstraint(SpringLayout.WEST, logo, -250, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, logo, 0, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, IPText, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, IPText, 180, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, IP, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, IP, 200, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, portText, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, portText, 280, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, port, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, port, 300, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, startBtn, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, startBtn, 400, SpringLayout.NORTH, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, exitBtn, 188, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, exitBtn, 480, SpringLayout.NORTH, mainPanel);
		
		mainPanel.add(logo);
		mainPanel.add(startBtn);
		mainPanel.add(exitBtn);
		mainPanel.add(IP);
		mainPanel.add(port);
		mainPanel.add(IPText);
		mainPanel.add(portText);
		
		try{
			BufferedImage icon = ImageIO.read(JavaServer.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		frame.add(mainPanel);
		frame.setVisible(true);
	}
	
	//Connects to the server
	private static void run() throws IOException{
		socket = new Socket(IP.getText(), Integer.parseInt(port.getText()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        
        while(run){
	        String line = in.readLine();
	        if (line.startsWith("GETID")) {
	            out.println("PLAYERONE"); 
	        }
	        else if(line.startsWith("LOGINSUCCESS")){
	        	run = false;
	        	startBtn.setEnabled(true);
	        }
        }
        
        
	}
}
