
import java.awt.Color;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;



public class Controller extends JPanel implements KeyListener,MouseListener,MouseMotionListener,MouseWheelListener{
	private JPanel gamePanel;
	public static Point cursorPos = new Point(0,0);
	public static Point mousePosition = new Point(0,0);
	public Controller(){
		this.setDoubleBuffered(true);

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mousePosition.x = (int) e.getPoint().getX();
		mousePosition.y = (int) e.getPoint().getY();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mousePosition.x = (int) e.getPoint().getX();
		mousePosition.y = (int) e.getPoint().getY();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i = 0; i<GamePanel.buttons.size();i++){
			if(GamePanel.buttons.get(i).isActive&&GamePanel.buttons.get(i).mouseOverThis()){
				GamePanel.buttons.get(i).pushButton();
			}
		}
		for(int i = 0; i<GamePanel.possibleChoices.size();i++){
			if(GamePanel.possibleChoices.get(i).mouseOverThis()){
				if(!GamePanel.possibleChoices.get(i).isGuess)
					GamePanel.possibleChoices.get(i).isGuess=true;
				else
					GamePanel.possibleChoices.get(i).isGuess=false;
			}
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_F12){
			if(GamePanel.paused){
				GamePanel.paused=false;
				System.out.println("Unpaused");
			}
			else{
				GamePanel.paused=true;
				System.out.println("Paused");
			}
			//GamePanel.saveSampleResults();
		}
		if(e.getKeyCode()==KeyEvent.VK_F11){
			GamePanel.getImage();
			GamePanel.saveSampleResults();
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}
	public static void checkKeys(){
		
	}
	@Override
	public void keyTyped(KeyEvent e) {

	}
	public void setGamePanel(JPanel panelRef) {
		gamePanel = panelRef;
		gamePanel.addKeyListener(this);
		gamePanel.addMouseListener(this);
		gamePanel.addMouseMotionListener(this);
		gamePanel.addMouseWheelListener(this);
	}

	public void updateAll(){
		if (gamePanel != null)
			gamePanel.getParent().repaint();
	}


}
