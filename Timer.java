import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class Timer extends SwingWorker implements ActionListener{

	private boolean prematureEnd = false;
	private int timeRemaining, timeInterval;
	private long endTime = 0, startTime = 0;
	private JLabel timeLabel;
	private ClientOne playerOne;
	private ClientTwo playerTwo;
	
	public Timer(ClientOne client, JLabel label, int time){
		timeLabel = label;
		timeRemaining = timeInterval = time * 60;
		playerOne = client;
	}
	
	public Timer(ClientTwo client, JLabel label, int time){
		timeLabel = label;
		timeRemaining = timeInterval = time * 60;
		playerTwo = client;
	}

	@Override
	protected Object doInBackground() throws Exception {
		
		while(timeRemaining > -1){
				if(startTime == 0){
					startTime = System.nanoTime();
				}
				endTime = (System.nanoTime() - startTime)/1000000000;
				timeRemaining = (int)(timeInterval - endTime);
				if(timeRemaining < timeRemaining*0.25){
					timeLabel.setText("Time: " + timeRemaining);
					timeLabel.setForeground(Color.RED);
				}
				else{
					timeLabel.setText("Time: " + timeRemaining);
				}
		}
		
		//signals completion to server
		if(prematureEnd == true){
			timeLabel.setText("Ending Game...");
		}
		else{
			timeLabel.setText("Time's Up!");
			if(playerTwo == null){
				playerOne.endGame();
			}
			else{
				playerTwo.endGame();
			}
		}
		
		return null;
	}
	
	protected void stopTimer(){
		prematureEnd = true;
		timeRemaining = -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {}
}