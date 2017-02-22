
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;



public class AppletUI extends JFrame{
	private static final long serialVersionUID = -6215774992938009947L;
	public static int windowWidth=800;
	public static int windowHeight=650;
	public static final long milisecInNanosec = 1000000L;
	public static final long secInNanosec = 1000000000L;
	public long lastDrawTime = System.currentTimeMillis();
	public static int GAME_FPS = 30;
	private final long GAME_UPDATE_PERIOD = secInNanosec / GAME_FPS;
	long logFileSize = new File("C:/Program Files (x86)/Steam/steamapps/common/Path of Exile/logs/Client.txt").length();
	Controller ctrl;
	int frames = 0;
	public static Point location = new Point(1100,400);

	public static void main(String[] args) throws IOException{

		AppletUI f = new AppletUI ();
		f.setSize(windowWidth,windowHeight);
		f.setVisible(true);
		//server = new Server();
		//client1 = new Client();
		//client2 = new Client();
	}

	public AppletUI() {

		setSize(windowWidth,windowHeight);
		Container pane = getContentPane();
		pane.setLayout(new BorderLayout());

		JPanel drawPanel = new GamePanel();
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setAlwaysOnTop(true);
		drawPanel.setBackground(new Color(10,10,10));
		ctrl = new Controller();
		this.addKeyListener(ctrl);
		ctrl.setGamePanel(drawPanel);
		//this.setFocusable(true);
		pane.add(drawPanel);
		setLocation(location.x,location.y);
		//this.setExtendedState(Frame.MAXIMIZED_BOTH);  
		//this.setUndecorated(true);  
		//We start game in new thread.
		Thread gameThread = new Thread() {			
			public void run(){
				gameLoop();
			}
		};
		//setVisible(true);
		gameThread.start();
	}
	public void gameLoop(){
		long beginTime, timeTaken, timeLeft;
		while(true){
			windowWidth = this.getWidth();
			windowHeight = this.getHeight();
			
			
			beginTime = System.nanoTime();
			
			Controller.checkKeys();
				repaint();
			

			timeTaken = System.nanoTime() - beginTime;
			timeLeft = (GAME_UPDATE_PERIOD - timeTaken) / milisecInNanosec; // In milliseconds
			// If the time is less than 10 milliseconds, then we will put thread to sleep for 10 millisecond so that some other thread can do some work.
			if (timeLeft < 10){ 
				timeLeft = 10; //set a minimum
			}
			try {
				//Provides the necessary delay and also yields control so that other thread can do work.
				Thread.sleep(timeLeft);
			} catch (InterruptedException ex) { }
		}
	}
}
