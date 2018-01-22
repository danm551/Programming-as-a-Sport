import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class HelpBox {
	
	   public HelpBox(){
	    	JFrame helpBox = new JFrame();
	    	helpBox.setSize(600, 800);
	    	helpBox.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	    	helpBox.setLocationRelativeTo(null);
	    	
	    	JPanel mainPanel = new JPanel();
	    
	    	String line;
	    	StringBuilder sb = new StringBuilder();
	    	
	    	File helpFile;
	    	File decodedHelpFile;
	    	String helpFilePath, decodedHelpFilePath;
	    	
	    	helpFile = new File("help/help.html");
			try {
				Scanner scanner = new Scanner(helpFile);
				
				while(scanner.hasNextLine()){
					line = scanner.nextLine();
					sb.append(line);
				}
				
				scanner.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

	    	JLabel title = new JLabel(sb.toString());
	    	
	    	mainPanel.add(title);
	    	
	    	helpBox.add(mainPanel);
	    	helpBox.setVisible(true);
	    	
	 	   helpBox.addWindowListener(new WindowListener(){

			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {
				helpBox.dispose();	
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
	    }
}
