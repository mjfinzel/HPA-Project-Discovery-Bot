import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class ContinueButton {
	Point lastContinueButtonPos = null;
	public ContinueButton(){

	}
	public Point checkForContinueButton(){
		BufferedImage entireScreen = GamePanel.robot.createScreenCapture(new Rectangle(100,300,800,680));
		//if we've found the continue button in the past
		if(lastContinueButtonPos!=null){
			Point whatWeFound = findContinueButton(entireScreen,lastContinueButtonPos);
			if(whatWeFound.x==-1&&whatWeFound.y==-1){
				whatWeFound = findContinueButton(entireScreen,new Point(0,0));
			}
			return whatWeFound;
		}
		else{
			findContinueButton(entireScreen,new Point(0,0));
		}

		return new Point(-1,-1);
	}
	public Point findContinueButton(BufferedImage img, Point startPos){
		for (int b = 0; b<4;b++){
			Color continueStart1 = new Color(GamePanel.continueButton[0][b].getRGB(0, 0),true);
			if(startPos.x>0&&startPos.y>0){
				Color current = new Color(img.getRGB(startPos.x, startPos.y),true);
				if(GamePanel.colorsAreSimilar(current,continueStart1)){
					//if(current.getRed()==continueStart1.getRed()&&current.getGreen()==continueStart1.getGreen()&&current.getBlue()==continueStart1.getBlue()){
					//System.out.println("found the start of continue button");
					boolean found = true;
					for(int x = 0; x<GamePanel.continueButton[0][b].getWidth();x++){
						for(int y = 0; y<GamePanel.continueButton[0][b].getHeight();y++){
							Color screenColor = new Color(img.getRGB(startPos.x+x, startPos.y+y));
							Color contColor = new Color(GamePanel.continueButton[0][b].getRGB(x, y));
							if(!GamePanel.colorsAreSimilar(screenColor, contColor)){
								//if(!screenColor.equals(contColor)){
								found=false;
								return new Point(-1,-1);
							}
						}
					}
					if(found){
						System.out.println("Re-Found the continue button.");
						return new Point(100+startPos.x,300+startPos.y);
					}
				}
			}
			else{			
				for(int i = 0; i<img.getWidth()-65;i++){
					for(int j = 0; j<img.getHeight()-4;j++){
						Color current = new Color(img.getRGB(i, j),true);
						if(GamePanel.colorsAreSimilar(current,continueStart1)){

							boolean found = true;
							for(int x = 0; x<GamePanel.continueButton[0][b].getWidth();x++){
								for(int y = 0; y<GamePanel.continueButton[0][b].getHeight();y++){
									Color screenColor = new Color(img.getRGB(i+x, j+y));
									Color contColor = new Color(GamePanel.continueButton[0][b].getRGB(x, y));
									if(!GamePanel.colorsAreSimilar(screenColor, contColor)){
										
										found=false;
									}
								}
							}
							if(found){
								System.out.println("Located the continue button!");
								lastContinueButtonPos = new Point(i,j);
								return new Point(100+i,300+j);
							}
						}
					}
				}
			}	
		}
		return new Point(-1,-1);
	}
}
