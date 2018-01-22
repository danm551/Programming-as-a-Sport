import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.xml.bind.DatatypeConverter;

public class ClientOne {
	
	//primitives
	protected static int imageId = 0;
	protected static int port;
	protected int time;
	protected float percentage, constraint;
	private boolean p1Wins = false, p2Wins = false, gridSetting = true, practice = true;
	private volatile boolean drawing = false;
	protected static String masterImage, finalImage;
	private static String IPAddress;
	private String id = "p1", line = null, image, test = null, pattern = null;
	private String[] resultPacket;
	
	//classes
	private static ClientOne client;
	private static Socket socket;
	private static StringBuilder code = new StringBuilder();
	private Timer timer;
	private BufferedReader in;
	protected PrintWriter out;
	private DrawImage drawImage;
	private Font willBeginFont, timeLabelFont, percentageLabelFont, drawBtnFont, textAreaFont, resultScreenFont, playerTextFont;
	private File oldDiffJavaFile, oldDiffClassFile, p1MyFinalImage, p1OpponentFinalImage, p1OpponentDiffImage, p1MyDiffImage, 
		differenceImageOne, playerOneTemp; 
	
	//swing
	private JFrame frame;
	private JPanel mainPanel, container, topPanel, imagePanel_One, imagePanel_Two;
	protected JLabel label_1, myImageLabel, label_3, willBegin, rules, tempLabel, timeLabel, percentageLabel, practiceText, logoLabel, 
    	resultsImageLabel, masterImageLabel, playerImageLabel, differenceImageLabel, winOrLoseLabel, opponentImage, opponentImageText, 
    	opponentDiffImage, opponentDiffImageText, opponentDiffPercentLabel, playerImageText, refImageText, playerId, resultsExplanation, gridLabel1, gridLabel2,
    	resultsBorder1, resultsBorder2, resultsBorder3, resultsBorder4;
	private ImageIcon image_2 = new ImageIcon(), logoImage, resultsImage, blankCanvasIcon;
	private JTextArea textArea;
	private JScrollPane scrollPane;
	private JButton drawBtn, helpBtn, menuBtn, gridBtn;
	private SpringLayout springLayout;

    public ClientOne() {
    	buildGUI();
    }
    
    public static void main(String[] args) throws Exception {
    	IPAddress = args[0];
    	port = Integer.parseInt(args[1]);
    	
        client = new ClientOne();
        client.run();
    }

    private void run() throws IOException {

        // Make connection and initialize streams
		try {
			socket = new Socket(IPAddress, port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream(), true);
		} catch (UnknownHostException e) {
    		JOptionPane.showMessageDialog(
        			new JFrame(), 
        			"Bad IP address and/or port number.", 
        			"IP Address/ Port Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
			new PlayerOne().main(null);
			frame.dispose();
			e.printStackTrace();
		} catch (IOException e) {
    		JOptionPane.showMessageDialog(
        			new JFrame(), 
        			"Bad IP address and/or port number.", 
        			"IP Address/ Port Error", 
        			JOptionPane.ERROR_MESSAGE
        			);
			new PlayerOne().main(null);
			frame.dispose();
			e.printStackTrace();
		}

        // Process all messages from server, according to the protocol.
        while (true) {
        	
			line = in.readLine();

            if (line.startsWith("GETID")) {
                out.println("PLAYERONE");
            }
            else if(line.startsWith("option")){
            	String[] argumentArray = new String[3];
            	argumentArray = line.split("&");
            	image = argumentArray[0];
            	time = Integer.parseInt(argumentArray[1]);
            	constraint = Float.parseFloat(argumentArray[2]);
            	if(masterImage == null){
            		out.println("Server message received.");
            		masterImage = image;
            		startGame(image, time);
            	}
            }
            else if(line.startsWith("P1ENDGAME")){
            	if(practice == false){
	            	timer.stopTimer();
	            	p1Wins = true;
	            	endGame();
            	}
            }
            else if(line.startsWith("P2ENDGAME")){
            	if(practice == false){
	            	timer.stopTimer();
	            	p2Wins = true;
	            	endGame();
            	}
            }
            else if(line.startsWith("PACKET")){
            	line = line.substring(6);
            	int index = 0, i = 0;
            	resultPacket = new String[7];
            	while(line.indexOf("SPLIT") >= 0){
            		index = line.indexOf("SPLIT");
            	    resultPacket[i] = line.substring(0, index);
            		line = line.substring(index+5);
            		if(i == 5){
            			resultPacket[i+1] = line;
            		}
            		i++;
            	}
            	
            	differenceImageLabel.setText("Similarity: " + resultPacket[0] + "%");
            	opponentDiffImageText.setText("Similarity: " + resultPacket[1] + "%");
            	if(p1Wins == true){
            		winOrLoseLabel.setText("<html>Player 1 Wins!<br>Constraint met first!</html>");
           	        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 820, SpringLayout.WEST, mainPanel);
        	        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 340, SpringLayout.NORTH, mainPanel);
            	}
            	else if(p1Wins == true){
            		winOrLoseLabel.setText("<html>Player 2 Wins!<br>Constraint met first!</html>");
           	        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 820, SpringLayout.WEST, mainPanel);
        	        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 340, SpringLayout.NORTH, mainPanel);
            	}
            	else{
	                if(resultPacket[2].startsWith("P1")){
	                	winOrLoseLabel.setText("PLAYER 1 WINS!");
	           	        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 850, SpringLayout.WEST, mainPanel);
	        	        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 350, SpringLayout.NORTH, mainPanel);
	                }
	                else if(resultPacket[2].startsWith("P2")){
	                	winOrLoseLabel.setText("PLAYER 2 WINS");
	           	        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 850, SpringLayout.WEST, mainPanel);
	        	        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 350, SpringLayout.NORTH, mainPanel);
	                }
	                else if(resultPacket[2].startsWith("TIE")){
	                	winOrLoseLabel.setText("IT'S A TIE!");
	                    springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 880, SpringLayout.WEST, mainPanel);
	        	        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 350, SpringLayout.NORTH, mainPanel);
	                }
            	}

                String p1FinalData = resultPacket[3]; 
                byte[] finalData1 = DatatypeConverter.parseBase64Binary(p1FinalData.substring(p1FinalData.indexOf(",") + 1));
                BufferedImage bufferedImage1 = ImageIO.read(new ByteArrayInputStream(finalData1));
                ImageIO.write(bufferedImage1, "png", new File("p1MyFinalImage.png")); 
                
                myImageLabel.setText("");
                myImageLabel.setIcon(new ImageIcon("p1MyFinalImage.png"));
                
              	String p2FinalData = resultPacket[4]; 
                byte[] finalData2 = DatatypeConverter.parseBase64Binary(p2FinalData.substring(p2FinalData.indexOf(",") + 1)); 
                BufferedImage bufferedImage2 = ImageIO.read(new ByteArrayInputStream(finalData2));
                ImageIO.write(bufferedImage2, "png", new File("p1OpponentFinalImage.png")); 
                
                opponentImage.setText("");
                opponentImage.setIcon(new ImageIcon("p1OpponentFinalImage.png"));
                
                String p2DiffData = resultPacket[6]; 
                byte[] diffData1 = DatatypeConverter.parseBase64Binary(p2DiffData.substring(p2DiffData.indexOf(",") + 1)); 
                BufferedImage bufferedImage3 = ImageIO.read(new ByteArrayInputStream(diffData1));
                ImageIO.write(bufferedImage3, "png", new File("p1OpponentDiffImage.png")); 
                
                opponentDiffImage.setText("");
                opponentDiffImage.setIcon(new ImageIcon("p1OpponentDiffImage.png"));
                
                String p1DiffData = resultPacket[5]; 
                byte[] diffData2 = DatatypeConverter.parseBase64Binary(p1DiffData.substring(p1DiffData.indexOf(",") + 1)); 
                BufferedImage bufferedImage4 = ImageIO.read(new ByteArrayInputStream(diffData2));
                ImageIO.write(bufferedImage4, "png", new File("p1MyDiffImage.png")); 
                
                resultsImageLabel.setIcon(new ImageIcon("p1MyDiffImage.png"));
            	frame.revalidate();
            	frame.repaint();
            	
            	p1MyFinalImage = new File("p1MyFinalImage.png"); 
            	p1OpponentFinalImage = new File("p1OpponentFinalImage.png"); 
            	p1OpponentDiffImage = new File("p1OpponentDiffImage.png"); 
            	p1MyDiffImage = new File("p1MyDiffImage.png"); 
            	differenceImageOne = new File("differenceImageOne.png"); 
            	playerOneTemp = new File(finalImage);
            	
            	p1MyFinalImage.delete();
            	p1OpponentFinalImage.delete();
            	p1OpponentDiffImage.delete();
            	p1MyDiffImage.delete(); 
            	differenceImageOne.delete(); 
            	playerOneTemp.delete(); 
            }
        }
    }
    
    /*
     * Builds initial GUI settings
     */
    private void buildGUI(){
    	practice = true; //cancels call to end game during practice
    	
    	frame = new JFrame("Player One");
        frame.setSize(1500, 900);
        frame.setLocationRelativeTo(null);

		try{
			BufferedImage icon = ImageIO.read(ClientOne.class.getClassLoader().getResource("frame_icon.png"));;
			frame.setIconImage(icon);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		drawBtnFont = new Font("Stencil", 0 , 25);
       
        logoLabel = new JLabel();
        logoLabel.setPreferredSize(new Dimension(1500, 95));
        logoImage = new ImageIcon(ClientOne.class.getClassLoader().getResource("logo.png"));
        logoLabel.setIcon(logoImage);
        
        willBegin = new JLabel("Your game will begin shortly...");
        willBegin.setFont(drawBtnFont);
        
        rules = new JLabel("Type code in the box. Your image will appear on the right.");
        rules.setFont(drawBtnFont);
        
        practiceText = new JLabel("Practice Area");
        practiceText.setFont(drawBtnFont);
        
        textArea = new JTextArea(10, 40);
        textArea.setCaretColor(Color.WHITE);

        textArea.setText("public void draw(){fill(0,255,0);rect(100,100,50,50);}");//delete
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setBackground(Color.BLACK);
        textArea.setForeground(Color.WHITE);
        textAreaFont = new Font("Serif", 1, 25);
        textArea.setFont(textAreaFont);
        scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500,430));
        scrollPane.setVerticalScrollBarPolicy (JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy (JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        Border lineBorder2;
    	Color screenBorderColor2 = new Color(200,100,0);
		lineBorder2 = BorderFactory.createLineBorder(screenBorderColor2);
        scrollPane.setBorder(lineBorder2);
		textArea.addKeyListener(new KeyListener(){

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == 68 && e.isControlDown() && drawing == false){
					if(textArea.getText().contains("fill(255)")){
			    		JOptionPane.showMessageDialog(
			        			new JFrame(), 
			        			"Color value 255 is not allowed. Please choose another color!", 
			        			"Color 255 Not Allowed", 
			        			JOptionPane.ERROR_MESSAGE
			        			);
					}
					else{
						drawing = true;
						DrawImage drawImage = new DrawImage(client, code.append(textArea.getText()), imageId, myImageLabel, frame);
						drawImage.execute();
					}
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
      
        myImageLabel = new JLabel();
        myImageLabel.setOpaque(true);
        blankCanvasIcon = new ImageIcon("gui_images/blank_canvas.png");
        myImageLabel.setIcon(blankCanvasIcon);
        
        helpBtn = new JButton("Help");
        helpBtn.setFocusPainted(false);
        helpBtn.setPreferredSize(new Dimension(100,50));
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
        
        drawBtn = new JButton("DRAW");
        drawBtn.setPreferredSize(new Dimension(125,50));
        drawBtn.setBackground(new Color(255,90,0));
        drawBtn.setForeground(Color.WHITE);
        drawBtn.setFont(drawBtnFont);
        drawBtn.setFocusPainted(false);
        drawBtn.addMouseListener(new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				test = textArea.getText();
				pattern = "(.*)[fill]+(\\s*)\\([0]*(([2-9][5-9][5-9])|([3-9][0-9][0-9]+)|([1-9][0-9][0-9][0-9]+))\\)(.*)"; //no fill value >= 255
				boolean match = Pattern.matches(pattern, test);
				
				if(match){
		    		JOptionPane.showMessageDialog(
		        			new JFrame(), 
		        			"White RGB value 255 is not allowed. Please choose another color!", 
		        			"White RGB 255 Not Allowed", 
		        			JOptionPane.ERROR_MESSAGE
		        			);
				}
				else if(drawing == false){
					drawing = true;
				    drawImage = new DrawImage(client, code.append(textArea.getText()), imageId, myImageLabel, frame);
					drawImage.execute();
				}
				else{
					e.consume();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				drawBtn.setBackground(new Color(125,45,0));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				drawBtn.setBackground(new Color(255,90,0));
			}
        	
        });
        
		gridBtn = new JButton("Grid");
		gridBtn.setFocusPainted(false);
        gridBtn.setPreferredSize(new Dimension(100,50));
        gridBtn.setFont(new Font("Stencil", 1, 20));
        gridBtn.setBackground(new Color(100, 160, 100));
        gridBtn.setForeground(Color.WHITE);
        gridLabel1 = new JLabel(new ImageIcon("gui_images/grid.png"));
        gridLabel1.setVisible(true);
        gridLabel2 = new JLabel(new ImageIcon("gui_images/grid.png"));
        gridLabel2.setVisible(true);
        gridBtn.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				if(gridSetting){
					gridSetting = false;
					gridLabel1.setVisible(false);
					gridLabel2.setVisible(false);
					gridBtn.setBackground(new Color(160, 100, 100));
				}
				else{
					gridSetting = true;
					gridLabel1.setVisible(true);
					gridLabel2.setVisible(true);
					gridBtn.setBackground(new Color(100, 160, 100));
				}
			}
        	
        });
    	gridBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				if(gridSetting){
					gridBtn.setBackground(new Color(40, 100, 40));
				}
				else{
					gridBtn.setBackground(new Color(100, 40, 40));
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if(gridSetting){
					gridBtn.setBackground(new Color(100, 160, 100));
				}
				else{
					gridBtn.setBackground(new Color(160, 100, 100));
				}
			}
		});
        
        playerId = new JLabel("<html><pre style='font-family:Bernard MT Condensed; font-size:40'>P l a y e r  O n e</pre><html>"); //change
        
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(255,255, 240)); //Light Yellow
        springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
        mainPanel.add(logoLabel);
        mainPanel.add(willBegin);
        mainPanel.add(rules);
        mainPanel.add(practiceText);
        mainPanel.add(scrollPane);
        mainPanel.add(gridLabel1);
        mainPanel.add(myImageLabel);
        mainPanel.add(drawBtn);
        mainPanel.add(helpBtn);
        mainPanel.add(playerId);
        mainPanel.add(gridBtn);
        
        springLayout.putConstraint(SpringLayout.WEST, logoLabel, 0, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, logoLabel, 0, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, willBegin, 570, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, willBegin, 130, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, rules, 400, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, rules, 160, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, practiceText, 220, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, practiceText, 230, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 75, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 280, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, gridLabel1, 1110, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, gridLabel1, 280, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, myImageLabel, 1110, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, myImageLabel, 280, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, drawBtn, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, drawBtn, 730, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, helpBtn, 1310, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, helpBtn, 620, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerId, 1125, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerId, 800, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, gridBtn, 1110, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, gridBtn, 620, SpringLayout.NORTH, mainPanel);
        
        frame.add(mainPanel);
        
        frame.addWindowListener(new WindowListener(){
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				if(out != null){
					out.println("P1OFFLINE");
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
        
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);
    }
    
    /*
     * Builds remaining GUI elements 
     * Calls listener for redraw
     */
    private void startGame(String image, int time){
    	practice = false;
    	
    	frame.remove(mainPanel);
    	
    	container = new JPanel();
    	topPanel = new JPanel();
    	topPanel.add(logoLabel);
    	container.setLayout(new BorderLayout());
    	container.add(topPanel, BorderLayout.NORTH);
    	container.add(mainPanel, BorderLayout.CENTER);
    	
    	mainPanel.remove(willBegin);
    	mainPanel.remove(rules);
    	mainPanel.remove(practiceText);
        mainPanel.remove(scrollPane);
        mainPanel.remove(myImageLabel);
        mainPanel.remove(drawBtn);
        mainPanel.remove(helpBtn);
        SpringLayout springLayout = new SpringLayout();
        mainPanel.setLayout(springLayout);
    	
        label_1 = new JLabel();
        ImageIcon image_1 = new ImageIcon("master_images/" + image);
        label_1.setIcon(image_1);
        
        imagePanel_One = new JPanel();
        imagePanel_One.add(label_1);
        
        imagePanel_Two = new JPanel();
        image_2 = new ImageIcon("gui_images/blank_canvas.png");
        myImageLabel.setIcon(image_2);
        imagePanel_Two.add(myImageLabel);
        
		timeLabel = new JLabel();
		timeLabelFont = new Font("Stencil", 0, 32);
		timeLabel.setFont(timeLabelFont);
		
		percentageLabel = new JLabel("Similarity: %");
		percentageLabelFont = new Font("Stencil", 0, 32);
		percentageLabel.setFont(percentageLabelFont);
        
		
		refImageText = new JLabel("Referee Image");
		playerTextFont = new Font("Stencil", 0, 22);
		refImageText.setFont(playerTextFont);
		
		playerImageText = new JLabel("Your Image");
		playerImageText.setFont(playerTextFont);
        
		mainPanel.add(gridLabel2);
        mainPanel.add(imagePanel_One);
        mainPanel.add(imagePanel_Two);
        mainPanel.add(scrollPane);
        mainPanel.add(drawBtn);
        mainPanel.add(helpBtn);
        mainPanel.add(refImageText);
        mainPanel.add(playerImageText);
        mainPanel.add(timeLabel);
        mainPanel.add(percentageLabel);
        
        
        springLayout.putConstraint(SpringLayout.WEST, gridLabel2, 700, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, gridLabel2, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_One, 700, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_One, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, gridLabel1, 1100, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, gridLabel1, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_Two, 1100, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_Two, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, scrollPane, 75, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, scrollPane, 50, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, drawBtn, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, drawBtn, 500, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, helpBtn, 1310, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, helpBtn, 400, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, refImageText, 780, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, refImageText, 365, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerImageText, 1200, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerImageText, 365, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, timeLabel, 950, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, timeLabel, 550, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, percentageLabel, 950, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, percentageLabel, 650, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerId, 75, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerId, 700, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, gridBtn, 1100, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, gridBtn, 400, SpringLayout.NORTH, mainPanel);
   
        frame.add(container);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        
        timer = new Timer(this, timeLabel, time);
        timer.execute();
    }
    
    private void differenceImage(){
    	/***** Creates .java file for difference image *****/
    	code.insert(0, "import processing.core.PApplet; import processing.core.PImage;"
    			+ "public class differenceImageOne extends PApplet{" 
    			+ "String masterImage = \"master_images/" + masterImage + "\";String playerImage = \"" + finalImage + "\";"
    			+ "PImage imageOne;" 
    			+ "PImage imageTwo;public static void main(String[] args) {"
    			+ "PApplet.main(\"differenceImageOne\");}public void settings(){imageOne = loadImage(masterImage, \"png\");" 
    					+ "imageTwo = loadImage(playerImage, \"png\");size(300,300);}public void setup(){fill(125);}public void draw() {loadPixels();imageOne.loadPixels();imageTwo.loadPixels();"
    					+ "for (int i = 0; i < imageOne.pixels.length; i++) {"
    					+ "float r1 = red(imageOne.pixels[i]);float g1 = green(imageOne.pixels[i]);float b1 = blue(imageOne.pixels[i]);float r2 = red(imageTwo.pixels[i]);"
    					+ "float g2 = green(imageTwo.pixels[i]);float b2 = blue(imageTwo.pixels[i]);"
    					+ " if((r1 == 255 && g1 == 255 && b1 == 255) && (r2 == 255 && g2 == 255 && b2 == 255)){}"
    					+ "else if(!(r1 == r2) && !(g1 == g2) && !(b1 == b2)){r1 = 255;g1 = 0;b1 = 0;}else if((r1 == r2) && (g1 == g2) && (b1 == b2)){r1 = 0;g1 = 0;b1 = 255;}"
    					+ "else{r1 = 255;g1 = 0;b1 = 0;}pixels[i] = color(r1, g1, b1);}updatePixels();"
    					+ "save(\"differenceImageOne.png\"); System.exit(0); }}");
    
		try{
			PrintWriter writer = new PrintWriter("differenceImageOne.java", "UTF-8");
			writer.println(code);
			writer.close();
			code.setLength(0);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		/***** Compiles .java file *****/
		ProcessBuilder pb = new ProcessBuilder("javac", "differenceImageOne.java");
		try{
			Process process = pb.start();
			process.waitFor();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		/***** Runs java .class file *****/
		pb = new ProcessBuilder("java", "differenceImageOne");
		try{
			Process process = pb.start();
			process.waitFor();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		oldDiffJavaFile = new File("differenceImageOne.java"); 
		oldDiffClassFile = new File("differenceImageOne.class"); 
		oldDiffJavaFile.delete(); //deletes old difference .java file 
		oldDiffClassFile.delete(); //deletes old difference .class file
    } 
    
    public void buildResultScreen(){
    	frame.setSize(1500, 900);
    	frame.setTitle("Player One Results"); 
    	
    	// Build container //
    	container.remove(mainPanel);
    	mainPanel = new JPanel();
    	springLayout = new SpringLayout();
    	mainPanel.setLayout(springLayout);
    	container.add(mainPanel, BorderLayout.CENTER);
    	
    	masterImageLabel = new JLabel("Referee");
    	playerImageLabel = new JLabel("You");
    	differenceImageLabel = new JLabel();
    	winOrLoseLabel = new JLabel("Winner? Waiting for results...");
    	resultsExplanation = new JLabel("<html><font style='font-size:10px'>&#8226 Images were judged for per-pixel color accuracy.<br>"
    			+ "&#8226 A blue pixel is an intersecting pixel.<br>"
    			+ "&#8226 A red pixel is a non-intersecting pixel.</font></html>");
    	
    	resultScreenFont = new Font("Stencil", 1, 20);
    	
        Border resultImageBorder = BorderFactory.createLineBorder(new Color(100,100,100), 5);
    	
    	myImageLabel = new JLabel();
    	myImageLabel.setBorder(resultImageBorder);
    	opponentImage = new JLabel();
    	opponentImage.setText("<html>Waiting for results...</html>");
    	opponentImage.setBorder(resultImageBorder);
		opponentImage.setHorizontalAlignment(SwingConstants.CENTER);
		opponentImage.setPreferredSize(new Dimension(300,300));
    	opponentImageText = new JLabel("Opponent");
    	opponentDiffImage = new JLabel("<html>Waiting for results...</html>"); 
    	opponentDiffImage.setBorder(resultImageBorder);
    	opponentDiffImage.setHorizontalAlignment(SwingConstants.CENTER);
    	opponentDiffImage.setPreferredSize(new Dimension(300,300));
    	opponentDiffImageText = new JLabel(); 
    	
    	opponentImage.setFont(resultScreenFont);
    	opponentDiffImage.setFont(resultScreenFont);
    	masterImageLabel.setFont(resultScreenFont);
    	playerImageLabel.setFont(resultScreenFont);
    	differenceImageLabel.setFont(resultScreenFont);
    	winOrLoseLabel.setFont(resultScreenFont);
    	opponentImageText.setFont(resultScreenFont);
    	opponentDiffImageText.setFont(resultScreenFont);
    	
    	resultsImageLabel = new  JLabel();
    	resultsImageLabel.setBorder(resultImageBorder);
    	if(imageId > 0){
    		resultsImage = new ImageIcon("differenceImageOne.png"); //change
    		resultsImageLabel.setIcon(resultsImage);
    		
    		myImageLabel.setIcon(new ImageIcon(finalImage));
    	}
    	else{
    		resultsImageLabel.setText("No image available.");
    		resultsImageLabel.setText("No image available.");
    		resultsImageLabel.setVerticalAlignment(JLabel.CENTER);
    		resultsImageLabel.setPreferredSize(new Dimension(300,300));
    		
    		myImageLabel.setText("No image available.");
    		myImageLabel.setVerticalAlignment(JLabel.CENTER);
    		myImageLabel.setPreferredSize(new Dimension(300,300));
    	}
    	
    	menuBtn = new JButton("Menu");
        menuBtn.setPreferredSize(new Dimension(147, 75));
        menuBtn.setBackground(new Color(255,90,0));
        menuBtn.setForeground(Color.WHITE);
        menuBtn.setFont(drawBtnFont);
        menuBtn.setFocusPainted(false);
        menuBtn.addActionListener(new ActionListener(){
        	
			@Override
			public void actionPerformed(ActionEvent e) {
				out.println("P1OFFLINE");
				System.exit(0);
			}
        	
        });
    	menuBtn.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {
				menuBtn.setBackground(new Color(125,45,0));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				menuBtn.setBackground(new Color(255,90,0));
			}
		});
    	
        mainPanel.add(imagePanel_One);
        mainPanel.setBackground(new Color(255,255, 240));
        mainPanel.add(myImageLabel);
        mainPanel.add(resultsImageLabel);
        mainPanel.add(playerImageLabel);
        mainPanel.add(masterImageLabel);
        mainPanel.add(differenceImageLabel);
        mainPanel.add(winOrLoseLabel);
        mainPanel.add(resultsExplanation);
        mainPanel.add(opponentImage);
        mainPanel.add(opponentImageText);
        mainPanel.add(opponentDiffImage); 
        mainPanel.add(opponentDiffImageText); 
        mainPanel.add(menuBtn);
        
        springLayout.putConstraint(SpringLayout.WEST, imagePanel_One, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, imagePanel_One, 170, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, myImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, myImageLabel, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, resultsImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, resultsImageLabel, 390, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, masterImageLabel, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, masterImageLabel, 140, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, playerImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, playerImageLabel, 10, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, differenceImageLabel, 450, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, differenceImageLabel, 360, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentImage, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentImage, 40, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentDiffImage, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentDiffImage, 390, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentImageText, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentImageText, 10, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, opponentDiffImageText, 1140, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, opponentDiffImageText, 360, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, winOrLoseLabel, 780, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, winOrLoseLabel, 355, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, resultsExplanation, 775, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, resultsExplanation, 500, SpringLayout.NORTH, mainPanel);
        
        springLayout.putConstraint(SpringLayout.WEST, menuBtn, 50, SpringLayout.WEST, mainPanel);
        springLayout.putConstraint(SpringLayout.NORTH, menuBtn, 615, SpringLayout.NORTH, mainPanel);
        
        mainPanel.revalidate();
        mainPanel.repaint();

        // Begins sequence to send individual information to server //
        String p1FinalData; 
        String p1DiffData; 
        BufferedImage p1Image, p1DiffImage; 
    	File imageFile1 = new File(finalImage);
    	File imageFile2 = new File("differenceImageOne.png");
    	
    	try {
			p1Image = ImageIO.read(imageFile1); 
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ImageIO.write(p1Image, "png", baos1); 
	        p1FinalData = "1data:image/png;base64," +  
	        		DatatypeConverter.printBase64Binary(baos1.toByteArray());
	        out.println(p1FinalData);  
    	}
    	catch (IOException e) {
 			e.printStackTrace();
 		}
    	
    	try {
			p1DiffImage = ImageIO.read(imageFile2); 
			ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
			ImageIO.write(p1DiffImage, "png", baos1);  
	        p1DiffData = "d1data:image/png;base64," +  
	        		DatatypeConverter.printBase64Binary(baos1.toByteArray());
	        out.println(p1DiffData);  
    	}
    	catch (IOException e) {
 			e.printStackTrace();
 		}

        out.println("PERCENTAGEONE" + percentage);   
    }
    
    protected void endGame(){
    	if(drawing == false){
	    	try{
	    		if(imageId > 0){
	    			differenceImage();
	    		}
	    		buildResultScreen();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    	else{
    		//Synch on the variable 'drawing'. When DrawImage finally changes the value of 'drawing', client will be able to see it.
    		synchronized(this){
	    		while(drawing == true){
	    			try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	    		}
    		}
	    	try{
	    		if(imageId > 0){
	    			differenceImage();
	    		}
	    		buildResultScreen();
	    	}
	    	catch(Exception e){
	    		e.printStackTrace();
	    	}
    	}
    }
    
    //Synch on 'drawing'
    public void setDrawingFalse(){
    	synchronized(this){
	    	drawing = false;
	    	this.notifyAll();
    	}
    }
}