import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

public class DrawImage extends SwingWorker {
	private int imageId;
	private StringBuilder code;
	private ClientOne clientOne = null;
	private ClientTwo clientTwo = null;
	private PrintWriter writer;
	private ProcessBuilder process;
	private File newImageFile, oldImageFile, oldJavaFile, oldClassFile;
	private JFrame frame;
	private JLabel myImageLabel;
	
	public DrawImage(ClientOne client, StringBuilder code, int imageId, JLabel myImageLabel, JFrame frame){
		
		this.clientOne = client;
		this.code = code;
	    this.imageId = imageId;
	    this.myImageLabel = myImageLabel;
	    this.frame = frame;
	}
	
	public DrawImage(ClientTwo client, StringBuilder code, int imageId, JLabel myImageLabel, JFrame frame){

		this.clientTwo = client;
		this.code = code;
		this.imageId = imageId;
		this.myImageLabel = myImageLabel;
		this.frame = frame;
	}

	@Override
	protected Object doInBackground() throws Exception {
		drawImage(code);
	    
		return true;
	}
	
	protected void drawImage(StringBuilder code){	
		if(clientTwo == null){
			clientOne.myImageLabel.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
		else{
			clientTwo.myImageLabel.setIcon(new ImageIcon("gui_images/redrawing.png"));
		}
    	//creates the ProcessingDraw.java file that draws the players image
    	createJavaFile();
    	//builds and runs the ProcessingDraw.java file
    	runJavaFile();

    	if(clientTwo == null){
    		clientOne.percentage = getComparePercentage(new File("master_images/" + clientOne.masterImage), new File(clientOne.finalImage));
    		String percentStringFormatted = String.format("%.2f", clientOne.percentage);
    		clientOne.percentage = Float.parseFloat(percentStringFormatted);
    		if(clientOne.percentageLabel != null){
    			clientOne.percentageLabel.setText("Similarity: " + Float.toString(clientOne.percentage) + "%");
    		}
    		if(clientOne.percentage == (float)clientOne.constraint || clientOne.percentage > clientOne.constraint){
    			clientOne.setDrawingFalse();
    			clientOne.out.println("P1WINS");
    		}
    	}
    	else{
    		clientTwo.percentage = getComparePercentage(new File("master_images/" + clientTwo.masterImage), new File(clientTwo.finalImage));
    		String percentStringFormatted = String.format("%.2f", clientTwo.percentage);
    		clientTwo.percentage = Float.parseFloat(percentStringFormatted);
    		if(clientTwo.percentageLabel != null){
    			clientTwo.percentageLabel.setText("Similarity: " + Float.toString(clientTwo.percentage) + "%");
    		}
    		if(clientTwo.percentage == (float)clientTwo.constraint || clientTwo.percentage > clientTwo.constraint){
    			clientTwo.setDrawingFalse();
    			clientTwo.out.println("P2WINS");
    		}
    	}

    	if(clientTwo == null){
    		clientOne.setDrawingFalse();
    	}
    	else{
    		clientTwo.setDrawingFalse();
    	}
    }
    
    private void createJavaFile(){
    	int index;
    	String partA;
		if (clientTwo == null) {
			partA = "import processing.core.PApplet;public class ProcessingDrawOne extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDrawOne\");}"
					+ "public void settings(){size(300,300);}public void setup(){background(255);}";
		}
		else{
			partA = "import processing.core.PApplet;public class ProcessingDrawTwo extends PApplet{public static void main(String[] args) {PApplet.main(\"ProcessingDrawTwo\");}"
					+ "public void settings(){size(300,300);}public void setup(){background(255);}";
		}
    	String partB = "}";
    	index = code.lastIndexOf("}");
    	if (clientTwo == null) {
    		code.insert(index, "save(\"playerOneTemp" + imageId + ".png\"); System.exit(0);");
    	}
    	else{
    		code.insert(index, "save(\"playerTwoTemp" + imageId + ".png\"); System.exit(0);");
    	}
    	
		try{
			if (clientTwo == null) {
				writer = new PrintWriter("ProcessingDrawOne.java", "UTF-8");
			}
			else{
				writer = new PrintWriter("ProcessingDrawTwo.java", "UTF-8");
			}
			
			writer.println(partA + code + partB);
			writer.close();
			code.setLength(0); //clears the StringBuilder
		}
		catch(Exception e){
			e.printStackTrace();
		}
    }
    
    private void runJavaFile(){
    	
    	try{
    		/***** Compiles .java file *****/
    		Runtime runtime = Runtime.getRuntime();
    
    		if (clientTwo == null) {
    			process = new ProcessBuilder("javac", "ProcessingDrawOne.java");
    		}
    		else{
    			process = new ProcessBuilder("javac", "ProcessingDrawTwo.java");
    		}
    		
    		Process p = process.start();
    		p.waitFor();
    		BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
    		String line;
    		StringBuilder error = new StringBuilder();
    		
    		while((line = reader.readLine()) != null){
    			error.append(line);
    		}
    		
    		if(error.length() == 0){
    			/***** Runs java file *****/
    			if (clientTwo == null) {
    				process = new ProcessBuilder("java", "ProcessingDrawOne"); //run file process
    			}
    			else{
    				process = new ProcessBuilder("java", "ProcessingDrawTwo");  //run file process
    			}
	 
	    		p = process.start();
	    		p.waitFor();

	        	if (clientTwo == null) {
	        		clientOne.myImageLabel.setIcon(new ImageIcon("playerOneTemp" + imageId + ".png"));
	        	}
	        	else{
	        		clientTwo.myImageLabel.setIcon(new ImageIcon("playerTwoTemp" + imageId + ".png"));
	        	}
	        	
	        	 frame.revalidate();
	            
	            //post image processing procedures for clientOne and clientTwo, respectively
	            if (clientTwo == null) {
	            	if(imageId > 0){
	            		oldImageFile = new File("playerOneTemp" + (imageId-1) + ".png");
	            		oldImageFile.delete(); //deletes previous image file
	            	}
	            	clientOne.finalImage = "playerOneTemp" + imageId + ".png";
            		oldJavaFile = new File("ProcessingDrawOne.java");
            		oldClassFile = new File("ProcessingDrawOne.class");
            		clientOne.imageId++;
	            }
	            else{
	            	if(imageId > 0){
	            		oldImageFile = new File("playerTwoTemp" + (imageId-1) + ".png");
	            		oldImageFile.delete(); //deletes previous image file
	            	}
	            	clientTwo.finalImage = "playerTwoTemp" + imageId + ".png";
            		oldJavaFile = new File("ProcessingDrawTwo.java");
            		oldClassFile = new File("ProcessingDrawTwo.class");
            		clientTwo.imageId++;
	            }
	            
            	oldJavaFile.delete(); //deletes previous .java file
        		oldClassFile.delete(); //deletes previous .class file
    		}
    		else{	
    			error.setLength(0);
    			if(clientTwo == null){
    				clientOne.myImageLabel.setIcon(new ImageIcon("gui_images/comp_error.png"));
    				clientOne.setDrawingFalse();
    			}
    			else{
    				clientTwo.myImageLabel.setIcon(new ImageIcon("gui_images/comp_error.png"));
    				clientTwo.setDrawingFalse();
    			}
    	 		JOptionPane.showMessageDialog(
            			new JFrame(), 
            			"The code did not compile.", 
            			"Code Error", 
            			JOptionPane.ERROR_MESSAGE
            			);
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public float getComparePercentage(File fileA, File fileB) {

	    float percentage = 0;
	    int total = 0, r, g, b;
		
	    try {
	        // take buffer data from both image files //
	        BufferedImage biA = ImageIO.read(fileA);
	        int sizeA = (biA.getWidth() * biA.getHeight());
	       
	        BufferedImage biB = ImageIO.read(fileB);
	        int sizeB = (biB.getWidth() * biB.getHeight());
	        
	        float count = 0;
	        
	        // compare data-buffer objects //
	        if (sizeA == sizeB) {
	            for(int x = 0; x < biA.getWidth(); x++){
	            	for(int y = 0; y < biA.getHeight(); y++){
	            		r = ((biA.getRGB(x,y)>>16)&0xFF); //bit-shift for red
	            		g = ((biA.getRGB(x,y)>>8)&0xFF); //bit-shift for green
	            		b = ((biA.getRGB(x,y)>>0)&0xFF); //bit-shift for blue
	  
	            		if(r == 255 && g == 255 && b == 255){
	            			
	            		}
	            		else if(biA.getRGB(x,y) == biB.getRGB(x,y)){
	            			count++;
	            			total++;
	            		}
	            		else{
	            			total++;
	            		}
	            	}
	            }

	            percentage = (float) ((count * 100.0) / total);
	        } else {
	            System.out.println("The images are not of the same size.");
	        }

	    } catch (Exception e) {
	        System.out.println("Could not compare the images.");
	    }
	    return percentage;
	}
}
