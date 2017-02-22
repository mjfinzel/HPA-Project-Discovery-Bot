import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class PossibleChoice {
	String name;
	Point position = new Point(0,0);
	int likelihood = 0;
	//BufferedImage whatToCheck;
	
	boolean isSelected = false;
	boolean isCorrect = false;
	boolean isWrong = false;

	boolean isGuess = false;

	public PossibleChoice(String name){
		this.name = name;
		if(name.equalsIgnoreCase("Nucleoplasm")){
			position = new Point(47,32);
		}
		else if(name.equalsIgnoreCase("Nuclear speckles")){
			position = new Point(125,32);
		}
		else if(name.equalsIgnoreCase("Nucleoli (fibrillar center)")){
			position = new Point(203,32);
		}
		else if(name.equalsIgnoreCase("Nucleus")){
			position = new Point(8,54);
		}
		else if(name.equalsIgnoreCase("Nuclear bodies (many)")){
			position = new Point(86,54);
		}
		else if(name.equalsIgnoreCase("Nucleoli (rim)")){
			position = new Point(164,54);
		}
		else if(name.equalsIgnoreCase("Nuclear bodies (few)")){
			position = new Point(47,77);
		}
		else if(name.equalsIgnoreCase("Nucleoli")){
			position = new Point(125,77);
		}
		else if(name.equalsIgnoreCase("Nuclear membrane")){
			position = new Point(203,77);
		}
		//second row
		else if(name.equalsIgnoreCase("Aggresome")){
			position = new Point(47,167);
		}
		else if(name.equalsIgnoreCase("Cytoskeleton (intermediate filaments)")){
			position = new Point(125,167);
		}
		else if(name.equalsIgnoreCase("Cytoskeleton (actin filaments)")){
			position = new Point(203,167);
		}
		else if(name.equalsIgnoreCase("Centrosome")){
			position = new Point(280,167);
		}
		else if(name.equalsIgnoreCase("Vesicles")){
			position = new Point(358,167);
		}
		else if(name.equalsIgnoreCase("Cytoplasm")){
			position = new Point(8,189);
		}
		else if(name.equalsIgnoreCase("Rods and rings")){
			position = new Point(86,189);
		}
		else if(name.equalsIgnoreCase("Cytoskeleton (microtubules)")){
			position = new Point(164,189);
		}
		else if(name.equalsIgnoreCase("Microtubule organizing center")){
			position = new Point(241,189);
		}
		else if(name.equalsIgnoreCase("The Golgi Apparatus")){
			position = new Point(319,189);
		}
		else if(name.equalsIgnoreCase("Mitochondria")){
			position = new Point(47,212);
		}
		else if(name.equalsIgnoreCase("Cytoskeleton (microtubule ends)")){
			position = new Point(125,212);
		}
		else if(name.equalsIgnoreCase("Cytoskeleton (cytokinetic bridge)")){
			position = new Point(203,212);
		}		
		else if(name.equalsIgnoreCase("Endoplasmic reticulum")){
			position = new Point(280,212);
		}

		//third row
		else if(name.equalsIgnoreCase("Focal adhesions")){
			position = new Point(47,302);
		}
		else if(name.equalsIgnoreCase("Unspecific")){
			position = new Point(281,302);
		}
		else if(name.equalsIgnoreCase("Cell junctions")){
			position = new Point(8,324);
		}
		else if(name.equalsIgnoreCase("Cell-to-cell variations")){
			position = new Point(164,324);
		}
		else if(name.equalsIgnoreCase("Negative")){
			position = new Point(242,324);
		}
		else if(name.equalsIgnoreCase("Plasma membrane")){
			position = new Point(47,347);
		}
		//1010,248
		position.x = position.x;//+441+GamePanel.imagePosition.x;
		position.y = position.y;//-6+GamePanel.imagePosition.y;
	}
	public void getStats(){
		//whatToCheck = GamePanel.robot.createScreenCapture(new Rectangle(position.x+12,position.y-1,26,5));
		isSelected = false;
		isCorrect = false;
		isWrong = false;
		for(int i = position.x+12;i<position.x+38;i++){
			for(int j = position.y-1;j<position.y+5;j++){
				
				Color current = new Color(GamePanel.whatToCheck.getRGB(i, j),true);
				//isSelected
				if(current.getRed()>=80&&current.getGreen()>=135&&current.getBlue()>=175){
					if(current.getRed()<=110&&current.getGreen()<=155&&current.getBlue()<=195){
						isSelected = true;
					}	
				}
				//isCorrect
				if(current.getRed()>=145&&current.getGreen()>=200&&current.getBlue()>=80){
					if(current.getRed()<=160&&current.getGreen()<=220&&current.getBlue()<=100){
						isCorrect = true;
					}	
				}
				//isWrong
				if(current.getRed()>=210&&current.getGreen()>=110&&current.getBlue()>=90){
					if(current.getRed()<=225&&current.getGreen()<=130&&current.getBlue()<=110){
						isWrong = true;
					}
				}
			}
		}
	}
//	public boolean isSelected(){
//		whatToCheck = GamePanel.robot.createScreenCapture(new Rectangle(position.x+12,position.y-1,26,5));
//		boolean isSelected = false;
//		for(int i = 0;i<whatToCheck.getWidth();i++){
//			for(int j = 0;j<whatToCheck.getHeight();j++){
//				Color current = new Color(whatToCheck.getRGB(i, j),true);
//				if(current.getRed()>=80&&current.getGreen()>=135&&current.getBlue()>=175){
//					if(current.getRed()<=110&&current.getGreen()<=155&&current.getBlue()<=195){
//						return true;
//					}	
//				}
//				
//			}
//		}
//		
//		return false;
//	}
//	public boolean isCorrectAnswer(){
//		whatToCheck = GamePanel.robot.createScreenCapture(new Rectangle(position.x+12,position.y-1,26,5));
//		boolean isCorrect = false;
//		for(int i = 0;i<whatToCheck.getWidth();i++){
//			for(int j = 0;j<whatToCheck.getHeight();j++){
//				Color current = new Color(whatToCheck.getRGB(i, j),true);
//				if(current.getRed()>=145&&current.getGreen()>=200&&current.getBlue()>=80){
//					if(current.getRed()<=160&&current.getGreen()<=220&&current.getBlue()<=100){
//						return true;
//					}	
//				}
//				
//			}
//		}
//		
//		return false;
//	}
//	public boolean isWrongAnswer(){
//		whatToCheck = GamePanel.robot.createScreenCapture(new Rectangle(position.x+12,position.y-1,26,5));
//		
//		for(int i = 0;i<whatToCheck.getWidth();i++){
//			for(int j = 0;j<whatToCheck.getHeight();j++){
//				Color current = new Color(whatToCheck.getRGB(i, j),true);
//				if(current.getRed()>=210&&current.getGreen()>=110&&current.getBlue()>=90){
//					if(current.getRed()<=225&&current.getGreen()<=130&&current.getBlue()<=110){
//						return true;
//					}
//				}
//				
//			}
//		}
//		
//		return false;
//	}
	public boolean mouseOverThis(){
		if (((Controller.mousePosition.x >= position.x+12) && 
				(Controller.mousePosition.x<=position.x + 38) &&
				(Controller.mousePosition.y>=position.y) && 
				(Controller.mousePosition.y<=position.y+42)))
		{
			return true;
		}
		return false;
	}
	public void clickThis(){
		
		GamePanel.click(GamePanel.randomNumber(position.x+13+441+GamePanel.imagePosition.x, position.x+37+441+GamePanel.imagePosition.x), GamePanel.randomNumber(position.y-6+GamePanel.imagePosition.y, position.y+32-6+GamePanel.imagePosition.y));
	}
	public void Draw(Graphics g){
//		if(whatToCheck!=null)
//			g.drawImage(whatToCheck,position.x,position.y,null); 
//		if(isGuess){
//			g.drawImage(GamePanel.selectedIcon,position.x,position.y,null);
//		}
//		if(mouseOverThis()){
//			g.drawImage(GamePanel.selectedIcon,position.x,position.y,null);
//		}
	}
}
