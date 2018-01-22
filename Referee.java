import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.border.Border;
import javax.swing.text.PlainDocument;


public class Referee {
	
	private boolean p1Online = false, p2Online = false;
	private String[] timeValues = {"1","3","5","10"}, constraintValues = {"50.0", "60.0", "70.0","80.0", "90.0"};
	private Border lineBorder;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame = new JFrame("Referee");
    private JPanel mainPanel, topPanel, centerPanel, sidePanel, sidePanelTop;
    private String IPAddress;
    private int port;
    private List<String> pngFiles = new ArrayList<String>();
    private JButton[] buttons;
    private JButton playerOneLight, playerTwoLight, helpBtn;
    private JLabel topPanelLabel, timeSelectLabel, constraintSelectLabel, instructions, id, optionsBorder;
    private JComboBox timeSelect, constraintSelect;
    private JTextField constraintBox;
    private ImageIcon[] icons;
    private ImageIcon logoImage;
    private Font topPanelFont, timeSelectLabelFont, invisibleText = new Font("Serif", 0, 0), stencil;
    private SpringLayout springLayout;
    private PlainDocument constraintSelectDoc;
    private JScrollPane centerPanelScroll;

    public Referee() {
    	
    	/***** IP address prompt *****/
    	try{
	    	IPAddress = (String)JOptionPane.showInputDialog(
	    			frame, 
	    			"Enter the IP address of the host:", 
	    			"Host Address",
	    			JOptionPane.WARNING_MESSAGE,
	    			null,
	    			null,
	    			"localhost"
	    			);
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		JOptionPane.showMessageDialog(
        			new JFrame(), 
        			"Bad IP address.", 
        			"IP Address Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
    		System.exit(0);
    	}
    	
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
    		System.exit(0);
    	}

        /***** Layout *****/
    	buildGUI();
    }
    
    public static void main(String[] args) throws Exception {
        Referee client = new Referee();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

    private void run() throws IOException {

        // Make connection and initialize streams
        //String serverAddress = "localhost";
        Socket socket = new Socket(IPAddress, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Process all messages from server, according to the protocol.
        while (true) {
            String line = in.readLine();
            if (line.startsWith("GETID")) {
                out.println("REFEREE");
            }
            else if(line.startsWith("P1ONLINE")){
            	p1Online = true;
            	playerOneLight.setText("<html><p style='text-align:center; font-size:15px'>Player One</p><br><p style='text-align:center; font-size:25px'>ONLINE</p></html>");
            	playerOneLight.setBackground(new Color(0, 179, 0));
            	playerOneLight.setForeground(Color.BLACK);
            }
            else if(line.startsWith("P2ONLINE")){
            	p2Online = true;
            	playerTwoLight.setText("<html><p style='text-align:center; font-size:15px'>Player Two</p><br><p style='text-align:center; font-size:25px'>ONLINE</p></html>");
            	playerTwoLight.setBackground(new Color(0, 179, 0));
            	playerTwoLight.setForeground(Color.BLACK);
            }
            else if(line.startsWith("P1OFFLINE")){
            	p1Online = false;
            	playerOneLight.setText("<html><p style='text-align:center; font-size:15px'>Player One</p><br><p style='text-align:center; font-size:22px'>OFFLINE</p></html>");
            	playerOneLight.setBackground(new Color(179, 0, 0));
            	playerOneLight.setForeground(Color.WHITE);
            }
            else if(line.startsWith("P2OFFLINE")){
            	p2Online = false;
            	playerTwoLight.setText("<html><p style='text-align:center; font-size:15px'>Player Two</p><br><p style='text-align:center; font-size:22px'>OFFLINE</p></html>");
            	playerTwoLight.setBackground(new Color(179, 0, 0));
            	playerTwoLight.setForeground(Color.WHITE);
            }
            else{
            	System.out.println(line);
            }
        }
    }
    
    
    /*
     * Builds the GUI 
     */
    private void buildGUI(){
        frame.setSize(1325, 860);
        frame.setLocationRelativeTo(null);
        
        frame.addWindowListener(new WindowListener(){
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				if(out != null){
					out.println("REFOFFLINE");
					System.exit(0);
				}
				else{
					System.exit(0);
				}
			}

			@Override
			public void windowClosed(WindowEvent e) {}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}	
        });
        
		try{
			BufferedImage icon = ImageIO.read(Referee.class.getClassLoader().getResource("frame_icon.png"));
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
        
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 240, 240));
        springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
        
        stencil = new Font("Stencil", 1, 15);
        
        centerPanel = new JPanel();
        centerPanel.setLayout(new GridLayout(0, 3, 0, 0));
        
        instructions = new JLabel("<html><p style='font-family:Arial; font-size:13px'>Click on an image below to send it to both players and start the game.</p><html>");
        
        id = new JLabel("<html><pre style='font-family:Bernard MT Condensed; font-size:40'>R e f e r e e</pre><html>");
        
        timeSelect = new JComboBox(timeValues);
        timeSelect.setPreferredSize(new Dimension(50, 25));
        timeSelect.setEditable(true);
        timeSelectLabel = new JLabel("Minutes : ");
        timeSelectLabelFont = new Font("Arial", 1, 15);
        timeSelectLabel.setFont(timeSelectLabelFont);
        
        constraintSelect = new JComboBox(constraintValues);
        constraintSelect.setPreferredSize(new Dimension(55, 25));
        constraintSelect.setEditable(true);
        constraintSelectLabel = new JLabel("Constraint (%) : ");
        constraintSelectLabel.setFont(timeSelectLabelFont);
        
        helpBtn = new JButton("Help");
        helpBtn.setFocusPainted(false);
        helpBtn.setPreferredSize(new Dimension(301,55));
        helpBtn.setFont(new Font("Stencil", 1, 20));
        helpBtn.setBackground(new Color(255,90,0));
        helpBtn.setForeground(Color.WHITE);
        helpBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				new HelpBox();
			}
        	
        });
    	helpBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				helpBtn.setBackground(new Color(125,45,0));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				helpBtn.setBackground(new Color(255,90,0));
			}
		});
 
        playerOneLight = new JButton("<html><p style='text-align:center; font-size:15px'>Player One</p><br><p style='text-align:center; font-size:22px'>OFFLINE</p></html>");
        playerOneLight.setFont(stencil);
        playerOneLight.setBorder(null);
        playerOneLight.setPreferredSize(new Dimension(150, 430));
        playerOneLight.setBackground(new Color(179, 0, 0));
        playerOneLight.setForeground(Color.WHITE);
        
        playerTwoLight = new JButton("<html><p style='text-align:center; font-size:15px'>Player Two</p><br><p style='text-align:center; font-size:22px'>OFFLINE</p></html>");
        playerTwoLight.setFont(stencil);
        playerTwoLight.setBorder(null);
        playerTwoLight.setPreferredSize(new Dimension(150, 430));
        playerTwoLight.setBackground(new Color(179, 0, 0));
        playerTwoLight.setForeground(Color.WHITE);
        
      	lineBorder = BorderFactory.createRaisedBevelBorder();
  		optionsBorder = new JLabel();
  		optionsBorder.setPreferredSize(new Dimension(300,125));
  		optionsBorder.setBorder(lineBorder);
        
        centerPanelScroll = new JScrollPane(centerPanel);
        centerPanelScroll.setPreferredSize(new Dimension(950,611));
        centerPanelScroll.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        centerPanelScroll.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(centerPanelScroll);
        mainPanel.add(timeSelectLabel);
        mainPanel.add(timeSelect);
        mainPanel.add(constraintSelectLabel);
        mainPanel.add(constraintSelect);
        mainPanel.add(helpBtn);
        mainPanel.add(playerOneLight);
        mainPanel.add(playerTwoLight);
        mainPanel.add(optionsBorder);
        mainPanel.add(instructions);
        mainPanel.add(id);
        
        springLayout.putConstraint(SpringLayout.WEST, instructions, 240, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, instructions, 10, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, centerPanelScroll, 25, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, centerPanelScroll, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, optionsBorder, 977, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, optionsBorder, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, timeSelectLabel, 1000, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeSelectLabel, 58, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, timeSelect, 1075, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeSelect, 55, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, constraintSelectLabel, 1000, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, constraintSelectLabel, 118, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, constraintSelect, 1120, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, constraintSelect, 115, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerOneLight, 976, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerOneLight, 165, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerTwoLight, 1127, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerTwoLight, 165, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, helpBtn, 976, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, helpBtn, 595, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, id, 25, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, id, 665, SpringLayout.NORTH, mainPanel);
        
        // Logo image //
        topPanel = new JPanel();
        topPanel.setBackground(Color.WHITE);
        topPanel.setPreferredSize(new Dimension(1500, 95));
        topPanel.setLayout(springLayout);
        
        topPanelLabel = new JLabel();
        topPanelFont = new Font("Serif", 1, 40);
        topPanelLabel.setFont(topPanelFont);
        logoImage = new ImageIcon(Referee.class.getClassLoader().getResource("logo_ref.png"));
        topPanelLabel.setIcon(logoImage);

        topPanel.add(topPanelLabel);
        
        try{
        	
        	File[] files = new File("master_images/").listFiles();
        	
        	for(File file : files){
        		if(file.isFile()){
        			pngFiles.add(file.getName());
        		}
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        }
        
        buttons = new JButton[pngFiles.size()];
        icons = new ImageIcon[pngFiles.size()];
        for(int i = 0; i < pngFiles.size(); i++){
        	icons[i] = new ImageIcon("master_images/" + pngFiles.get(i));
        	buttons[i] = new JButton(icons[i]);
        	buttons[i].setText(pngFiles.get(i));
        	buttons[i].setFont(invisibleText);
        	buttons[i].setBackground(Color.WHITE);
        	
        	Border lineBorder;
        	Color screenBorderColor = new Color(200,100,0);
    		lineBorder = BorderFactory.createLineBorder(screenBorderColor);
        	lineBorder = BorderFactory.createRaisedBevelBorder();
    		buttons[i].setBorder(lineBorder);
    		int index = i;
    		buttons[i].addMouseListener(new MouseListener(){
				@Override
				public void mouseClicked(MouseEvent e) {}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {}

				@Override
				public void mouseEntered(MouseEvent e) {
					buttons[index].setBackground(new Color(255,90,0));
				}

				@Override
				public void mouseExited(MouseEvent e) {
					buttons[index].setBackground(new Color(160,160,160));
				}
    			
    		});

        	buttons[i].addActionListener(new ActionListener(){

    			@Override
    			public void actionPerformed(ActionEvent e) {
    				String constraint = constraintSelect.getSelectedItem().toString();
    				String time = timeSelect.getSelectedItem().toString();
    				boolean timeReady = false, constraintReady = false;

    				// Validates constraint input //
    				if(constraint.isEmpty()){
    					constraint = "0";
    					constraintReady = true;
    				}
    				else{
    					try{
        					Float constFloat = Float.parseFloat(constraint);
        					if(constFloat > 100 || constFloat < 0){
        						JOptionPane.showMessageDialog(
        								new JFrame(),
        								"Constraint range is 0-100.",
        								"Invalid Constraint Value",
        								JOptionPane.ERROR_MESSAGE
        								);
        					}
        					else{
        						constraintReady = true;
        					}
        				}
        				catch(Exception e1){
       						JOptionPane.showMessageDialog(
    								new JFrame(),
    								"Constraint value must be a decimal or integer.",
    								"Invalid Constraint Value",
    								JOptionPane.ERROR_MESSAGE
       								);
        					e1.printStackTrace();
        				}
    				}		
    		
    				// Validates minute input //
       				if(time.isEmpty()){
       					JOptionPane.showMessageDialog(
								new JFrame(),
								"Please select a minute value.",
								"No Minutes Selected",
								JOptionPane.ERROR_MESSAGE
       							);
    				}
       				else{
        				try{
        					int timeInt = Integer.parseInt(time);
        					if(timeInt < 0){
        						JOptionPane.showMessageDialog(
        								new JFrame(),
        								"Please select a minute greater than 0.",
        								"Invalid Minute Value",
        								JOptionPane.ERROR_MESSAGE
        								);
        					}
        					else{
        						timeReady = true;
        					}
        				}
        				catch(Exception e2){
       						JOptionPane.showMessageDialog(
    								new JFrame(),
    								"Minute value must be an integer.",
    								"Invalid Minute Value",
    								JOptionPane.ERROR_MESSAGE
       								);
        					e2.printStackTrace();
        				}
       				}
       				if(p1Online && p2Online){
       					if(timeReady && constraintReady){
       						out.println(e.getActionCommand() + "&" + timeSelect.getSelectedItem() + "&" + constraintSelect.getSelectedItem());
       					}
       				}
       				else{
   						JOptionPane.showMessageDialog(
								new JFrame(),
								"One or both players are not connected.",
								"Player(s) Not Connected",
								JOptionPane.ERROR_MESSAGE
   								);
       				}
       				
    			}
            });
        	
            centerPanel.add(buttons[i]);
        }
        
        frame.add(mainPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.setResizable(true);
    }
}
