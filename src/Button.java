import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;




public class Button {
	Point position = new Point();
	boolean isActive = true;//whether or not it should be visible
	int width = 150;
	int height = 30;
	String name;
	public Button(Point pos, String name){
		position.x = pos.x;
		position.y = pos.y;
		this.name = name;
	}
	public void pushButton(){
		if(name=="Correct"){
			GamePanel.saveSampleResults();//save information about this to a file
			System.out.println("Button for \"Correct\" was pushed!");
		}
		if(name=="Incorrect"){
			System.out.println("Button for \"Incorrect\" was pushed!");
		}
		if(name=="Submit"){
			System.out.println("Button for \"Submit\" was pushed!");
			GamePanel.answers.clear();
			for(int i = 0; i<GamePanel.possibleChoices.size();i++){
				if(GamePanel.possibleChoices.get(i).isGuess){
					GamePanel.answers.add(GamePanel.possibleChoices.get(i).name);
				}
			}
			GamePanel.saveSampleResults();
		}
	}
	public boolean mouseOverThis(){
		if (((Controller.mousePosition.x >= position.x) && 
				(Controller.mousePosition.x<=position.x + width) &&
				(Controller.mousePosition.y>=position.y) && 
				(Controller.mousePosition.y<=position.y+height)))
		{
			return true;
		}
		return false;
	}
	public void Draw(Graphics g){
		Font font = new Font("Iwona Heavy",Font.BOLD,14);
		g.setFont(font);
		FontMetrics m = g.getFontMetrics(font);
		
		if(name=="Correct"){
			g.setColor(new Color(104,196,114));
			g.fillRect(position.x, position.y, width, height);
			g.setColor(new Color(63,119,70));
			g.drawRect(position.x, position.y, width, height);
		}
		else if(name=="Incorrect"){
			g.setColor(new Color(181,79,79));
			g.fillRect(position.x, position.y, width, height);
			g.setColor(new Color(127,56,56));
			g.drawRect(position.x, position.y, width, height);
		}
		else if(name=="Submit"){
			g.setColor(new Color(119,198,255));
			g.fillRect(position.x, position.y, width, height);
			g.setColor(new Color(74,123,158));
			g.drawRect(position.x, position.y, width, height);
		}
		
		font = new Font("Iwona Heavy",Font.BOLD,18);
		g.setColor(Color.black);
		g.drawString(name, position.x+(width/2)-(m.stringWidth(name)/2), position.y+20);
		if(mouseOverThis()){
			//highlight button
			g.setColor(new Color(255,255,255,40));
			g.fillRect(position.x, position.y, width, height);
		}
	}
}
