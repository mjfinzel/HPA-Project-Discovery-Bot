
import java.awt.AWTException;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;

import javafx.geometry.Point3D;

import javax.imageio.ImageIO;
import javax.swing.JPanel;



public class GamePanel extends JPanel{
	private static final long serialVersionUID = 7734877696044080629L;

	public static BufferedImage choicesImage = Images.load("/Textures/Choices.png");
	public static BufferedImage selectedIcon = Images.load("/Textures/SelectedIcon.png");
	public static BufferedImage percentageIcon = Images.load("/Textures/PercentageIcon.png");
	public static BufferedImage[][] continueButton = Images.cut("/Textures/ContinuteButton.png",64,3);
	public static BufferedImage loadingScreen = Images.load("/Textures/LoadingScreen.png");

	public static Point imagePosition = new Point(0,0);

	static ArrayList<PossibleChoice> possibleChoices = new ArrayList<PossibleChoice>();
	public static ArrayList<Sample> priorSamples = new ArrayList<Sample>();
	public static Sample currentGuess = null;
	public static ArrayList<String> answers = new ArrayList<String>();

	public static ArrayList<Button> buttons = new ArrayList<Button>();

	public static ArrayList<Double> confidenceInRecentGuesses = new ArrayList<Double>();

	public static ArrayList<Point> accuracyOfRecentGuesses = new ArrayList<Point>();
	public static double averageRecentConfidence = 0;

	static LocalDateTime time = LocalDateTime.now();
	static long timeOfLastCheck = System.currentTimeMillis();
	static long maxDailyTime = 1000*60*60*8;

	public static String baseDirectory = "";

	static int guessNumber = -1;

	static int totalBreakTime = 0;

	static Robot robot;
	static BufferedImage currentImage;
	static BufferedImage guessImage;

	static int totalRed=0;
	static int totalGreen=0;
	static int totalBlue=0;
	static int totalBlack = 0;

	static int sizeOfLargestGreenShape = 0;

	static double percentRed=0;
	static double percentGreen=0;
	static double percentBlue=0;
	static double percentBlack = 0;
	static Point oldMousePosition = new Point(0,0);

	long timeOfLastSample = 0;
	ArrayList<Long> timeBetweenSamples = new ArrayList<Long>();

	static boolean recentlySaved = false;

	static double similarity = 0;
	static double confidence = 0;

	static int amountOfGreenTouchingBlue=0;
	static int amountOfGreenTouchingRed=0;
	static int amountOfBlueTouchingRed=0;
	static int amountOfBlackTouchingRed;
	static int amountOfBlackTouchingGreen;
	static int amountOfBlackTouchingBlue;

	public static int breakTime = 0;


	static int amountOfRedTouchingRed=0;
	static int amountOfGreenTouchingGreen=0;
	static int amountOfBlueTouchingBlue=0;
	static int amountOfBlackTouchingBlack = 0;

	public static long timeForNextBreak = 0;

	public static BufferedImage whatToCheck = null;

	int currentChoice = -1;

	int state = 1;
	long timeUsedToday = 0;

	int framesPerAction = 30;
	int framesSinceLastAction = 0;

	public static boolean paused = true;
	public static boolean stopped = false;
	public static long timeSlept = 0;
	public static long timeStarted = System.currentTimeMillis();

	int answerClicks = 0;//number of times answers were clicked for this sample
	
	ContinueButton contButton = new ContinueButton();
	
	static BufferedImage entireScreen = null;

	//trial account credentials:
	//username: PoorPeasant
	//password: GiveMe1

	public GamePanel(){

		try {
			if(!System.getProperty("os.name").contains("Windows")){
				//linux
				baseDirectory = AppletUI.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
				//remove the final location from the path
				for(int i = baseDirectory.length()-1;i>=0;i--){
					if((baseDirectory.charAt(i)+"").equals(File.separator)){
						baseDirectory = baseDirectory.substring(0, i);
						break;
					}
				}
				baseDirectory = baseDirectory+File.separator+"ProjectDiscovery_Saves";
			}
			else{
				baseDirectory = System.getenv("APPDATA")+File.separator+"ProjectDiscovery_Saves";
			}

			System.out.println("Base directory is: "+baseDirectory);
		} catch (URISyntaxException e1) {

			e1.printStackTrace();
		}
		int hour = 1000*60*60;
		timeForNextBreak = System.currentTimeMillis()+randomNumber(hour,hour*2);
		//randomize by up to an hour less or more than the limit to avoid suspicion
		maxDailyTime = maxDailyTime+randomNumber(-3600000,3600000);
		paused=false;
		initializePossibleChoices();

		this.setDoubleBuffered(true);
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//load any older samples that were processed before this time of execution
		getPriorSamples();

		//make eve be the active window
		activate.makeActive("EVE - King Currency");
	}

	public static void initializePossibleChoices(){
		possibleChoices.clear();
		//add possible choices to an array list
		possibleChoices.add(new PossibleChoice("Nucleoplasm"));
		possibleChoices.add(new PossibleChoice("Nuclear speckles"));
		possibleChoices.add(new PossibleChoice("Nucleoli (fibrillar center)"));
		possibleChoices.add(new PossibleChoice("Nucleus"));
		possibleChoices.add(new PossibleChoice("Nuclear bodies (many)"));
		possibleChoices.add(new PossibleChoice("Nucleoli (rim)"));
		possibleChoices.add(new PossibleChoice("Nuclear bodies (few)"));
		possibleChoices.add(new PossibleChoice("Nucleoli"));
		possibleChoices.add(new PossibleChoice("Nuclear membrane"));
		//second row
		possibleChoices.add(new PossibleChoice("Aggresome"));
		possibleChoices.add(new PossibleChoice("Cytoskeleton (intermediate filaments)"));
		possibleChoices.add(new PossibleChoice("Cytoskeleton (actin filaments)"));
		possibleChoices.add(new PossibleChoice("Centrosome"));
		possibleChoices.add(new PossibleChoice("Vesicles"));
		possibleChoices.add(new PossibleChoice("Cytoplasm"));
		possibleChoices.add(new PossibleChoice("Rods and rings"));
		possibleChoices.add(new PossibleChoice("Cytoskeleton (microtubules)"));
		possibleChoices.add(new PossibleChoice("Microtubule organizing center"));
		possibleChoices.add(new PossibleChoice("The Golgi Apparatus"));
		possibleChoices.add(new PossibleChoice("Mitochondria"));
		possibleChoices.add(new PossibleChoice("Cytoskeleton (microtubule ends)"));
		possibleChoices.add(new PossibleChoice("Cytoskeleton (cytokinetic bridge)"));
		possibleChoices.add(new PossibleChoice("Endoplasmic reticulum"));

		//third row
		possibleChoices.add(new PossibleChoice("Focal adhesions"));
		possibleChoices.add(new PossibleChoice("Unspecific"));
		possibleChoices.add(new PossibleChoice("Cell junctions"));
		possibleChoices.add(new PossibleChoice("Cell-to-cell variations"));
		possibleChoices.add(new PossibleChoice("Negative"));
		possibleChoices.add(new PossibleChoice("Plasma membrane"));
	}
	public static void moveMouse(int x, int y){


		robot.mouseMove(x, y);    


	}
	public static void click(int x, int y){
		//System.out.println("attempting click at: "+x+","+y);
		boolean moveSlow = false;
		if(moveSlow){
			double mouseX = MouseInfo.getPointerInfo().getLocation().getX();
			double mouseY = MouseInfo.getPointerInfo().getLocation().getY();
			double speed = randomNumber(40,50);

			int timeSlept = 0;

			double dx = (x - mouseX) / ((double) speed);
			double dy = (y - mouseY) / ((double) speed);

			for (int step = 1; step <= speed+1; step++) {
				int sleepAmount=randomNumber(1,2);
				sleep(sleepAmount);
				timeSlept+=timeSlept;
				robot.mouseMove((int) (mouseX + dx * step), (int) (mouseY + dy * step));
			}


			if(timeSlept<140){
				sleep(randomNumber(140-timeSlept,160-timeSlept));
			}
		}
		else{
			robot.mouseMove(x,y);
			sleep(randomNumber(80,100));
		}
		robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		sleep(randomNumber(50,120));
		robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
		sleep(randomNumber(80,120));

	}
	public static void sleep(int time){
		try {
			timeSlept+=time;
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static long getTimeUsedToday(){
		//if the usage log doesn't exist create the log file
		String path = baseDirectory+File.separator+"Usage_Log";
		String folderName = path;
		Path saveDirectory = Paths.get(folderName);

		InputStream dataRead = null;
		//create an empty folder to store save files in if none exist
		if(Files.notExists(saveDirectory)){			
			//(use relative path for Unix systems)
			File dir = new File(path);
			//(works for both Windows and Linux)
			dir.mkdirs();
		}

		//create a text file inside the folder to store the information
		File data = new File((saveDirectory.toAbsolutePath())+File.separator+"Usage_log.txt");
		try {
			data.createNewFile();
		} catch (IOException e) {}
		try {
			dataRead = new FileInputStream(path+File.separator+"Usage_log.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		//check for an entry in the usage log for today
		String entry = null;

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(dataRead, "UTF-8"))){
			Sample currentSample = new Sample();
			String line;
			//for every line in the text file, read it to a string called 'line'
			while ((line = reader.readLine()) != null) {
				//get an array representing all the characters in the line
				char[] lineChars = line.toCharArray();
				String dateToLookFor = time.getMonth().name()+" "+time.getDayOfMonth()+" "+time.getYear();
				if(line.contains(dateToLookFor)){
					entry = line;
					//System.out.println("The program has already been run today.");

				}
			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//if there is no entry for today
		if(entry==null){

			//create an entry
			//write the relevant data to the file
			FileWriter output = null;
			try{
				String beginingOfEntry = time.getMonth().name()+" "+time.getDayOfMonth()+" "+time.getYear();
				String secondPart = " "+(System.currentTimeMillis()-timeOfLastCheck+"\n");
				entry = beginingOfEntry + secondPart;
				output = new FileWriter(data,true);
				output.write(entry);
				timeOfLastCheck = System.currentTimeMillis();
			}
			catch(Exception ex){
				System.out.println("Failed to write to (or create) file to store the data.");
				ex.printStackTrace();
			}
			finally{
				try{output.close();}catch(Exception ex){System.out.println("F There was some kind of error closing the file.");}
			}

		}
		long timeUsedToday = (System.currentTimeMillis()-timeOfLastCheck);
		try {
			// input the file content to the String "input"
			BufferedReader file = new BufferedReader(new FileReader(saveDirectory.toAbsolutePath()+File.separator+"Usage_log.txt"));
			String line;String input = "";

			while ((line = file.readLine()) != null) input += line + '\n';

			file.close();

			String[] variables = entry.split(" ");

			String beginingOfEntry = time.getMonth().name()+" "+time.getDayOfMonth()+" "+time.getYear();
			timeUsedToday = Long.valueOf(variables[3])+(System.currentTimeMillis()-timeOfLastCheck);
			String secondPart = " "+timeUsedToday+"\n";
			input = input.replace(entry, beginingOfEntry+secondPart); 

			// write the new String with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream(saveDirectory.toAbsolutePath()+File.separator+"Usage_log.txt");
			fileOut.write(input.getBytes());
			fileOut.close();
			timeOfLastCheck = System.currentTimeMillis();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}

		//return the total time used
		//timeUsedToday-=totalBreakTime*500;
		return timeUsedToday;

	}

	public static void clickRefresh(){
		int x = randomNumber(imagePosition.x+5,imagePosition.x+20);
		int y = randomNumber(imagePosition.y-25,imagePosition.y-7);
		click(x,y);
	}
	public static boolean colorsAreSimilar(Color a, Color b){
		int threshold = 3;
		if(a.getRed()>=b.getRed()-threshold&&a.getRed()<=b.getRed()+threshold){
			if(a.getGreen()>=b.getGreen()-threshold&&a.getGreen()<=b.getGreen()+threshold){
				if(a.getBlue()>=b.getBlue()-threshold&&a.getBlue()<=b.getBlue()+threshold){
					return true;
				}
			}
		}
		return false;
	}
	public static void getImage(){
		long startTime = System.currentTimeMillis();


		for(int i = 0; i<buttons.size();i++){
			buttons.get(i).isActive=false;
		}
		do{
			sleep(randomNumber(250,270));
			currentImage=null;
			long startingTime = System.currentTimeMillis();
			entireScreen = robot.createScreenCapture(new Rectangle(0,0,1220,1080));
			//saveBufferedImage(entireScreen,baseDirectory+"images"+File.separator+priorSamples.size()+".png");
			//find the start of the image we care about
			for(int i = 0; i<entireScreen.getWidth()-404;i++){
				for(int j = 0; j<entireScreen.getHeight()-404;j++){
					Color current = new Color(entireScreen.getRGB(i, j),true);

					//if current is the same color as the top left corner of the image
					if(colorsAreSimilar(current,new Color(134,134,134))){
						//if((current.getRed()==134&&current.getGreen()==134&&current.getBlue()==134)){
						//System.out.println("first check is true");
						boolean correctSpot = true;
						//check if nearby pixels match the pattern
						for(int x = 0; x<9;x++){
							for(int y = 0; y<5;y++){
								Color color = new Color(entireScreen.getRGB(i+x, j+y),true);
								if(x==0||y==0){
									if(!colorsAreSimilar(color,new Color(134,134,134))){
										if(!colorsAreSimilar(color,new Color(140,140,140))){
											if(!colorsAreSimilar(color,new Color(132,132,133))){
												correctSpot=false;
											}
										}
									}
								}
								if(x==1||y==1){
									if(colorsAreSimilar(color,new Color(134,134,134))){
										if(colorsAreSimilar(color,new Color(140,140,140))){
											if(colorsAreSimilar(color,new Color(132,132,133))){
												correctSpot=false;
											}
										}
									}
								}
							}
						}
						
						Color bottomCorner = new Color(entireScreen.getRGB(i+403, j+403),true);
						
						if(colorsAreSimilar(bottomCorner,new Color(137,137,137))){
							//if(bottomCorner.getRed()==137&&bottomCorner.getGreen()==137&&bottomCorner.getBlue()==137){
							if(correctSpot){
								currentImage = entireScreen.getSubimage(i+2, j+2, 400, 400);
								imagePosition.x = i;
								imagePosition.y=j;
								initializePossibleChoices();
								System.out.println("found correct spot!");
							}
						}
					}


				}
			}
			System.out.println("Checking for image took: "+(System.currentTimeMillis()-startingTime)+"ms");
			if(currentImage!=null){
				getImageInfo();	
			}
		}while(currentImage!=null&&(checkIfImageFinishedLoading()==false||(totalGreen==0&&totalBlack>44000&&totalBlack<45000)));

		System.out.println("Took "+ (System.currentTimeMillis()-startTime)+ "ms to get image.");

	}

	public static boolean sampleIsSimilar(Sample sample,double threshold){
		//minimum percentage of similarity for things to be similar

		if(getTotalRedSimilarity(sample)>threshold){
			if(getTotalGreenSimilarity(sample)>threshold){
				if(getTotalBlueSimilarity(sample)>threshold){
					if(getGreenTouchBlueSimilarity(sample)>threshold){
						if(getGreenTouchRedSimilarity(sample)>threshold){
							if(getBlueTouchRedSimilarity(sample)>threshold){
								if(getBlackTouchRedSimilarity(sample)>threshold){
									if(getBlackTouchGreenSimilarity(sample)>threshold){
										if(getBlackTouchBlueSimilarity(sample)>threshold){
											if(getRedTouchRedSimilarity(sample)>threshold){
												if(getGreenTouchGreenSimilarity(sample)>threshold){
													if(getBlueTouchBlueSimilarity(sample)>threshold){
														if(getBlackTouchBlackSimilarity(sample)>threshold){
															return true;
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return false;
	}
	public static double getTotalSimilarity(Sample sample){
		double total = 0;
		double[] similarities = new double[14];
		similarities[0] = getTotalRedSimilarity(sample);
		similarities[1] = getTotalGreenSimilarity(sample);
		similarities[2] = getTotalBlueSimilarity(sample);
		similarities[3] = getGreenTouchBlueSimilarity(sample);
		similarities[4] = getGreenTouchRedSimilarity(sample);
		similarities[5] = getBlueTouchRedSimilarity(sample);
		similarities[6] = getBlackTouchRedSimilarity(sample);
		similarities[7] = getBlackTouchGreenSimilarity(sample);
		similarities[8] = getBlackTouchBlueSimilarity(sample);
		similarities[9] = getRedTouchRedSimilarity(sample);
		similarities[10] = getGreenTouchGreenSimilarity(sample);
		similarities[11] = getBlueTouchBlueSimilarity(sample);
		similarities[12] = getBlackTouchBlackSimilarity(sample);
		similarities[13] = getGreenShapeSizeSimilarity(sample);

		int smallest = 0;
		for(int i =1; i<similarities.length;i++){
			if(similarities[i]<similarities[smallest]){
				smallest=i;
			}
		}
		confidence = similarities[smallest];

		return confidence;
	}
	public static double getGreenShapeSizeSimilarity(Sample sample){
		double percentGreenVsBlue = (double)sizeOfLargestGreenShape/(double)totalBlue;
		double percentGreenVsBlueOfSample = (double)sample.sizeOfLargestGreenShape/(double)sample.totalBlue;

		if(percentGreenVsBlue<percentGreenVsBlueOfSample){
			return (percentGreenVsBlue/percentGreenVsBlueOfSample)*100.0;
		}
		else{
			return (percentGreenVsBlueOfSample/percentGreenVsBlue)*100.0;
		}
	}
	public static double getTotalRedSimilarity(Sample sample){
		//determine similarity of totalRed
		double percentRedWithoutBlack = (double)totalRed/(160000.0-(double)totalBlack);
		double percentRedWithoutBlackForSample = (double)sample.totalRed/(160000.0-(double)sample.totalBlack);
		if(percentRedWithoutBlack<percentRedWithoutBlackForSample){
			return (percentRedWithoutBlack/percentRedWithoutBlackForSample)*100.0;
		}
		else{
			return (percentRedWithoutBlackForSample/percentRedWithoutBlack)*100.0;
		}
	}
	public static double getTotalGreenSimilarity(Sample sample){
		//determine similarity of totalGreen
		double percentGreenWithoutBlack = (double)totalGreen/(160000.0-(double)totalBlack);
		double percentGreenWithoutBlackForSample = (double)sample.totalGreen/(160000.0-(double)sample.totalBlack);
		if(percentGreenWithoutBlack<percentGreenWithoutBlackForSample){
			return (percentGreenWithoutBlack/percentGreenWithoutBlackForSample)*100.0;
		}
		else{
			return (percentGreenWithoutBlackForSample/percentGreenWithoutBlack)*100.0;
		}
	}
	public static double getTotalBlueSimilarity(Sample sample){
		//determine similarity of totalBlue
		double percentBlueWithoutBlack = (double)totalBlue/(160000.0-(double)totalBlack);
		double percentBlueWithoutBlackForSample = (double)sample.totalBlue/(160000.0-(double)sample.totalBlack);
		if(percentBlueWithoutBlack<percentBlueWithoutBlackForSample){
			return (percentBlueWithoutBlack/percentBlueWithoutBlackForSample*100.0);
		}
		else{
			return (percentBlueWithoutBlackForSample/percentBlueWithoutBlack*100.0);
		}
	}
	public static double getGreenTouchBlueSimilarity(Sample sample){
		//percentage of green pixels that touch blue pixels
		double temp1 = (amountOfGreenTouchingBlue/(double)totalGreen);
		double temp2 = (sample.amountOfGreenTouchingBlue/(double)sample.totalGreen);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getGreenTouchRedSimilarity(Sample sample){
		//percentage of green pixels that touch Red pixels
		double temp1 = (amountOfGreenTouchingRed/(double)totalGreen);
		double temp2 = (sample.amountOfGreenTouchingRed/(double)sample.totalGreen);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlueTouchRedSimilarity(Sample sample){
		//percentage of Blue pixels that touch Red pixels
		double temp1 = (amountOfBlueTouchingRed/(double)totalBlue);
		double temp2 = (sample.amountOfBlueTouchingRed/(double)sample.totalBlue);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlackTouchRedSimilarity(Sample sample){
		//percentage of red pixels that touch black pixels
		double temp1 = (amountOfBlackTouchingRed/(double)totalRed);
		double temp2 = (sample.amountOfBlackTouchingRed/(double)sample.totalRed);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlackTouchGreenSimilarity(Sample sample){
		//percentage of green pixels that touch black pixels
		double temp1 = (amountOfBlackTouchingGreen/(double)totalGreen);
		double temp2 = (sample.amountOfBlackTouchingGreen/(double)sample.totalGreen);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlackTouchBlueSimilarity(Sample sample){
		//percentage of blue pixels that touch black pixels
		double temp1 = (amountOfBlackTouchingBlue/(double)totalBlue);
		double temp2 = (sample.amountOfBlackTouchingBlue/(double)sample.totalBlue);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getRedTouchRedSimilarity(Sample sample){
		//percentage of red pixels that touch red pixels
		double temp1 = (amountOfRedTouchingRed/(double)totalRed);
		double temp2 = (sample.amountOfRedTouchingRed/(double)sample.totalRed);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getGreenTouchGreenSimilarity(Sample sample){
		//percentage of Green pixels that touch Green pixels
		double temp1 = (amountOfGreenTouchingGreen/(double)totalGreen);
		double temp2 = (sample.amountOfGreenTouchingGreen/(double)sample.totalGreen);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlueTouchBlueSimilarity(Sample sample){
		//percentage of Blue pixels that touch Blue pixels
		double temp1 = (amountOfBlueTouchingBlue/(double)totalBlue);
		double temp2 = (sample.amountOfBlueTouchingBlue/(double)sample.totalBlue);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	public static double getBlackTouchBlackSimilarity(Sample sample){
		//percentage of Black pixels that touch Black pixels
		double temp1 = (amountOfBlackTouchingBlack/(double)totalBlack);
		double temp2 = (sample.amountOfBlackTouchingBlack/(double)sample.totalBlack);

		if(temp1<temp2){
			return (temp1/temp2)*100.0;
		}
		else{
			return (temp2/temp1)*100.0;
		}
	}
	//returns the prior sample that is the most similar to the current sample
	public static Sample getMostSimilarExperience(){
		long startingTime = System.currentTimeMillis();
		Sample mostSimilar = null;
		//make sure it's similar
		//for each prior sample
		for(int i = 0; i<priorSamples.size();i++){
			if(mostSimilar==null&&sampleIsSimilar(priorSamples.get(i),50)){
				mostSimilar=priorSamples.get(i);
			}
			else{
				//if this is similar to the current sample
				if(sampleIsSimilar(priorSamples.get(i),50)){
					//check if the current sample is more similar to this sample than mostSimilar
					if(getTotalSimilarity(priorSamples.get(i))>getTotalSimilarity(mostSimilar)){
						mostSimilar=priorSamples.get(i);
						similarity=getTotalSimilarity(priorSamples.get(i));
					}
				}
			}
		}
		//if none are more than 50% similar, try to find one that's at least kind of similar
		if(mostSimilar==null){
			//for each prior sample
			for(int i = 0; i<priorSamples.size();i++){
				if(mostSimilar==null&&sampleIsSimilar(priorSamples.get(i),5)){
					mostSimilar=priorSamples.get(i);
				}
				else{
					//if this is similar to the current sample
					if(sampleIsSimilar(priorSamples.get(i),5)){
						//check if the current sample is more similar to this sample than mostSimilar
						if(getTotalSimilarity(priorSamples.get(i))>getTotalSimilarity(mostSimilar)){
							mostSimilar=priorSamples.get(i);
							similarity=getTotalSimilarity(priorSamples.get(i));
						}
					}
				}
			}
		}
		System.out.println("Finding most similar sample took: "+(System.currentTimeMillis()-startingTime)+"ms");
		return mostSimilar;
		
	}
	public static void getImageInfo(){
		long startingTime = System.currentTimeMillis();
		totalRed = 0;
		totalGreen = 0;
		totalBlue= 0;
		totalBlack = 0;
		percentRed=0;
		percentGreen=0;
		percentBlue=0;
		percentBlack = 0;
		amountOfGreenTouchingBlue = 0;
		amountOfGreenTouchingRed = 0;
		amountOfBlueTouchingRed = 0;
		amountOfBlackTouchingRed = 0;
		amountOfBlackTouchingGreen = 0;
		amountOfBlackTouchingBlue = 0;

		amountOfRedTouchingRed=0;
		amountOfGreenTouchingGreen=0;
		amountOfBlueTouchingBlue=0;
		amountOfBlackTouchingBlack=0;
		currentGuess=null;
		guessImage = null;

		for(int j = 0; j<possibleChoices.size();j++){			
			possibleChoices.get(j).isGuess=false;
		}
		//determine total amounts of each color
		for(int i = 0; i<currentImage.getWidth();i++){
			for(int j = 0; j<currentImage.getHeight();j++){
				Color current = new Color(currentImage.getRGB(i, j),true);
				int threshold = 12;
				if(isRed(current)){
					totalRed++;
				}
				else if(isGreen(current)){
					totalGreen++;
				}
				else if(isBlue(current)){
					totalBlue++;
				}
				else{
					totalBlack++;
				}
			}
		}
		percentRed = ((double)totalRed/160000.0)*100;
		percentGreen = ((double)totalGreen/160000.0)*100;
		percentBlue = ((double)totalBlue/160000.0)*100;
		percentBlack = ((double)totalBlack/160000.0)*100;

		//determine how much the various colors touch each other
		for(int i = 0; i<currentImage.getWidth();i++){
			for(int j = 0; j<currentImage.getHeight();j++){
				Color current = new Color(currentImage.getRGB(i, j),true);
				if(isGreen(current)&&getNearbyColors(new Point(i,j)).getBlue()>0){
					amountOfGreenTouchingBlue++;
				}
				if(isGreen(current)&&getNearbyColors(new Point(i,j)).getRed()>0){
					amountOfGreenTouchingRed++;
				}
				if(isBlue(current)&&getNearbyColors(new Point(i,j)).getRed()>0){
					amountOfBlueTouchingRed++;
				}
				//black
				if(!(isBlue(current)||isRed(current)||isGreen(current))&&getNearbyColors(new Point(i,j)).getRed()>0){
					amountOfBlackTouchingRed++;
				}
				if(!(isBlue(current)||isRed(current)||isGreen(current))&&getNearbyColors(new Point(i,j)).getGreen()>0){
					amountOfBlackTouchingGreen++;
				}
				if(!(isBlue(current)||isRed(current)||isGreen(current))&&getNearbyColors(new Point(i,j)).getBlue()>0){
					amountOfBlackTouchingBlue++;
				}

				if(isRed(current)&&getNearbyColors(new Point(i,j)).getRed()>0){
					amountOfRedTouchingRed++;
				}
				if(isGreen(current)&&getNearbyColors(new Point(i,j)).getGreen()>0){
					amountOfGreenTouchingGreen++;
				}
				if(isBlue(current)&&getNearbyColors(new Point(i,j)).getBlue()>0){
					amountOfBlueTouchingBlue++;
				}
				int totalBlack = 8-(int)getNearbyColors(new Point(i,j)).getRed()-(int)getNearbyColors(new Point(i,j)).getGreen()-(int)getNearbyColors(new Point(i,j)).getBlue();
				if(!(isBlue(current)||isRed(current)||isGreen(current))&&totalBlack>0){
					amountOfBlackTouchingBlack++;
				}
			}
		}
		//determine size of largest green
		sizeOfLargestGreenShape = getSizeOfLargestGreenShape();

		//determine what prior sample is the most similar to this image
		currentGuess = getMostSimilarExperience();
		if(currentGuess!=null){
			//System.out.println("Trying to load image at: "+baseDirectory+File.separator+"ProjectDiscovery_Saves/images/"+guessNumber+".png");
			guessImage = Images.fancyLoad(baseDirectory+File.separator+"images"+File.separator+currentGuess.guessNumber+".png");
			//calculate recent confidence
			if(confidenceInRecentGuesses.size()>=50){
				//remove oldest entry
				confidenceInRecentGuesses.remove(0);
			}
			confidenceInRecentGuesses.add(similarity);
			double totalConfidence = 0;
			for(int i = 0; i<confidenceInRecentGuesses.size();i++){
				totalConfidence+=confidenceInRecentGuesses.get(i);
			}

			averageRecentConfidence = totalConfidence/(double)confidenceInRecentGuesses.size();
			//loop through the answers for this guess
			for(int i = 0;i<currentGuess.answer.size();i++){
				for(int j = 0; j<possibleChoices.size();j++){
					if(possibleChoices.get(j).name.equals(currentGuess.answer.get(i))){
						possibleChoices.get(j).isGuess=true;
					}
				}
			}
			//			for(int i = 0; i<buttons.size();i++){
			//				if(buttons.get(i).name=="Correct"){
			//					buttons.get(i).isActive=true;
			//				}
			//				if(buttons.get(i).name=="Incorrect"){
			//					buttons.get(i).isActive=true;
			//				}
			//			}
		}
		else{
			//if there is nothing similar to this, pick cytoplasm

			possibleChoices.get(14).isGuess=true;
			//			for(int i = 0; i<buttons.size();i++){
			//				if(buttons.get(i).name=="Submit"){
			//					buttons.get(i).isActive=true;
			//				}
			//			}
		}
		System.out.println("Getting image info took: "+(System.currentTimeMillis()-startingTime)+"ms");
	}
	public static int getSizeOfLargestGreenShape(){
		boolean visited[][] = new boolean[currentImage.getWidth()][currentImage.getHeight()];
		int sizeOfLargest = 0;
		for(int i = 0; i<currentImage.getWidth();i++){
			for(int j = 0; j<currentImage.getHeight();j++){
				if(sizeOfLargest==1000){
					return sizeOfLargest;
				}
				if(isGreen(new Color(currentImage.getRGB(i, j),true))){
					int size = 1;
					int result = getSizeOfGreenShape(new Point(i,j),new boolean[currentImage.getWidth()][currentImage.getHeight()],size);
					if(result>sizeOfLargest){
						sizeOfLargest=result;
					}
				}

			}
		}
		return sizeOfLargest;
	}
	public static int getSizeOfGreenShape(Point position, boolean[][] visited, int size){
		visited[position.x][position.y]=true;
		boolean visitedAtLeastOne = false;
		Point directions[] = {	
				new Point(position.x,position.y-1),
				new Point(position.x,position.y+1),
				new Point(position.x-1,position.y),
				new Point(position.x+1,position.y)
		};
		if(size<=1000){
			for(int i = 0; i<directions.length;i++){

				//if this direction is within bounds
				if(!(directions[i].x<0||directions[i].y<0||directions[i].x>currentImage.getWidth()-1||directions[i].y>currentImage.getHeight()-1)){

					//if this direction hasn't been visited
					if(!visited[directions[i].x][directions[i].y]){

						Color color = new Color(currentImage.getRGB(directions[i].x, directions[i].y),true);
						if(isGreen(color)){

							//visit this direction
							size = getSizeOfGreenShape(directions[i],visited,size+1);
						}
					}
				}
			}
		}
		else{
			return 1000;
		}

		return size;
	}
	public static boolean isRed(Color color){

		if(color.getRed()>10&&color.getRed()>=color.getGreen()&&color.getRed()>=color.getBlue()){
			return true;
		}
		return false;
	}
	public static boolean isGreen(Color color){

		if(color.getGreen()>10&&color.getGreen()>=color.getBlue()&&color.getGreen()>=color.getRed()){
			return true;
		}
		return false;
	}
	public static boolean isBlue(Color color){

		if(color.getBlue()>10&&color.getBlue()>=color.getGreen()&&color.getBlue()>=color.getRed()){
			return true;
		}
		return false;
	}
	public static Color getNearbyColors(Point position){
		Color totals = new Color(0,0,0);
		int threshold = 12;
		for(int i = position.x-1; i<=position.x+1;i++){
			for(int j = position.y-1;j<=position.y+1;j++){
				if(i>0&&i<400&&j>0&&j<400){
					Color current = new Color(currentImage.getRGB(i, j),true);
					if(!(i==position.x&&j==position.y)){
						//red
						if(current.getRed()>=threshold&&current.getGreen()<=threshold&&current.getBlue()<=threshold){
							totals = new Color(totals.getRed()+1,totals.getGreen(),totals.getBlue());
						}
						//green
						if(current.getRed()<=threshold&&current.getGreen()>=threshold&&current.getBlue()<=threshold){
							totals = new Color(totals.getRed(),totals.getGreen()+1,totals.getBlue());
						}
						//blue
						if(current.getRed()<=threshold&&current.getGreen()<=threshold&&current.getBlue()>=threshold){
							totals = new Color(totals.getRed(),totals.getGreen(),totals.getBlue()+1);
						}
					}
				}
			}
		}
		return totals;
	}
	public static void loadConfig(){

	}
	public static void getPriorSamples(){
		priorSamples.clear();
		String pathAsString = baseDirectory+File.separator+"DataFromSamples.txt";
		Path path = Paths.get(pathAsString);

		InputStream data = null;

		//if the file paths don't exist
		if(Files.notExists(path)){
			System.out.println("File containing samples did not exist. None were loaded.");
			return;
		}
		else{
			//    InputStream in = new FileInputStream(theFile);  
			try {
				data = new FileInputStream(pathAsString);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} 
		}

		try(BufferedReader reader = new BufferedReader(new InputStreamReader(data, "UTF-8"))){
			Sample currentSample = new Sample();
			String line;
			//for every line in the text file, read it to a string called 'line'
			while ((line = reader.readLine()) != null) {
				//get an array representing all the characters in the line
				char[] lineChars = line.toCharArray();
				//check which variable this line is for and assign it's value to that variable
				if(line.contains("Sample ")&&!line.contains("Sample {")){
					currentSample.guessNumber = Integer.valueOf(line.substring("Sample ".length(),line.length()-1));

				}
				if(line.contains("totalRed = ")){
					currentSample.totalRed = Integer.valueOf(line.substring("totalRed = ".length()+1,line.length()));
				}
				if(line.contains("totalGreen = ")){
					currentSample.totalGreen = Integer.valueOf(line.substring("totalGreen = ".length()+1,line.length()));
				}
				if(line.contains("totalBlue = ")){
					currentSample.totalBlue = Integer.valueOf(line.substring("totalBlue = ".length()+1,line.length()));
				}
				if(line.contains("totalBlack = ")){
					currentSample.totalBlack = Integer.valueOf(line.substring("totalBlack = ".length()+1,line.length()));
				}

				if(line.contains("amountOfGreenTouchingBlue = ")){
					currentSample.amountOfGreenTouchingBlue = Integer.valueOf(line.substring("amountOfGreenTouchingBlue = ".length()+1,line.length()));
				}
				if(line.contains("amountOfGreenTouchingRed = ")){
					currentSample.amountOfGreenTouchingRed = Integer.valueOf(line.substring("amountOfGreenTouchingRed = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlueTouchingRed = ")){
					currentSample.amountOfBlueTouchingRed = Integer.valueOf(line.substring("amountOfBlueTouchingRed = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlackTouchingRed = ")){
					currentSample.amountOfBlackTouchingRed = Integer.valueOf(line.substring("amountOfBlackTouchingRed = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlackTouchingGreen = ")){
					currentSample.amountOfBlackTouchingGreen = Integer.valueOf(line.substring("amountOfBlackTouchingGreen = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlackTouchingBlue = ")){
					currentSample.amountOfBlackTouchingBlue = Integer.valueOf(line.substring("amountOfBlackTouchingBlue = ".length()+1,line.length()));
				}

				if(line.contains("amountOfRedTouchingRed = ")){
					currentSample.amountOfRedTouchingRed = Integer.valueOf(line.substring("amountOfRedTouchingRed = ".length()+1,line.length()));
				}
				if(line.contains("amountOfGreenTouchingGreen = ")){
					currentSample.amountOfGreenTouchingGreen = Integer.valueOf(line.substring("amountOfGreenTouchingGreen = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlueTouchingBlue = ")){
					currentSample.amountOfBlueTouchingBlue = Integer.valueOf(line.substring("amountOfBlueTouchingBlue = ".length()+1,line.length()));
				}
				if(line.contains("amountOfBlackTouchingBlack = ")){
					currentSample.amountOfBlackTouchingBlack = Integer.valueOf(line.substring("amountOfBlackTouchingBlack = ".length()+1,line.length()));
				}//"	sizeOfLargestGreenShape = "
				if(line.contains("sizeOfLargestGreenShape = ")){
					currentSample.sizeOfLargestGreenShape = Integer.valueOf(line.substring("sizeOfLargestGreenShape = ".length()+1,line.length()));
				}
				if(line.contains("Answer: ")){
					line = line.substring("Answer: ".length()+1,line.length());
					String[] array = line.split(",");
					for(int i = 0; i<array.length;i++){
						currentSample.answer.add(array[i]);
					}
				}
				if(line.contains("}")){
					priorSamples.add(currentSample);
					currentSample = new Sample();
				}


			}

		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public static void saveSampleResults(){
		//getPriorSamples();
		//make sure the current image is of the correct region
		if(true||currentImage!=null&&currentImage.getWidth()==400&&currentImage.getHeight()==400){

			boolean alreadyExisted=false;
			//make sure that this sample cannot already be found in the list of prior samples
			for(int i = 0;i<priorSamples.size();i++){
				Sample prior = priorSamples.get(i);
				if(prior.totalRed==totalRed&&prior.totalGreen==totalGreen&&prior.totalBlue==totalBlue){
					if(prior.amountOfBlackTouchingBlack==amountOfBlackTouchingBlack){
						if(prior.amountOfRedTouchingRed==amountOfRedTouchingRed){
							if(prior.amountOfGreenTouchingGreen==amountOfGreenTouchingGreen){
								if(prior.amountOfBlueTouchingBlue==amountOfBlueTouchingBlue){
									alreadyExisted=true;
									System.out.println("This sample has already been saved.");
								}
							}
						}
					}
				}
			}
			System.out.println("Checking if the sample existed already.");
			if(alreadyExisted==false){
				System.out.println("This sample has not already been saved.");
				String folderName = baseDirectory;
				Path saveDirectory = Paths.get(folderName);
				String path = baseDirectory;

				//create an empty folder to store save files in if none exist
				if(Files.notExists(saveDirectory)){			
					//(use relative path for Unix systems)
					File dir = new File(path);
					//(works for both Windows and Linux)
					dir.mkdirs();
				}

				//create a text file inside the folder to store the information
				File data = new File((saveDirectory.toAbsolutePath())+File.separator+"DataFromSamples.txt");
				try {
					data.createNewFile();
				} catch (IOException e) {
					System.out.println("new file already existed");
				}

				//write the relevant data to the file
				FileWriter output = null;
				try{
					System.out.println("writing stuff to file, totalRed for new data is: "+totalRed);
					output = new FileWriter(data,true);
					output.write("Sample "+priorSamples.size()+"{\n");
					output.write("	totalRed = "+totalRed+"\n");
					output.write("	totalGreen = "+totalGreen+"\n");
					output.write("	totalBlue = "+totalBlue+"\n" );
					output.write("	totalBlack = "+totalBlack+"\n" );

					output.write("	amountOfGreenTouchingBlue = "+amountOfGreenTouchingBlue+"\n");
					output.write("	amountOfGreenTouchingRed = "+amountOfGreenTouchingRed+"\n");
					output.write("	amountOfBlueTouchingRed = "+amountOfBlueTouchingRed+"\n");
					output.write("	amountOfBlackTouchingRed = "+amountOfBlackTouchingRed+"\n");
					output.write("	amountOfBlackTouchingGreen = "+amountOfBlackTouchingGreen+"\n");
					output.write("	amountOfBlackTouchingBlue = "+amountOfBlackTouchingBlue+"\n");

					output.write("	amountOfRedTouchingRed = "+amountOfRedTouchingRed+"\n");
					output.write("	amountOfGreenTouchingGreen = "+amountOfGreenTouchingGreen+"\n");
					output.write("	amountOfBlueTouchingBlue = "+amountOfBlueTouchingBlue+"\n");
					output.write("	amountOfBlackTouchingBlack = "+amountOfBlackTouchingBlack+"\n");
					output.write("	sizeOfLargestGreenShape = "+sizeOfLargestGreenShape+"\n");

					output.write("	Answer: ");
					for(int i = 0; i<answers.size();i++){
						output.write(answers.get(i));
						if(i<answers.size()-1){
							output.write(",");
						}
					}
					output.write("\n");

					output.write("}\n");
					try{output.close();}catch(Exception ex){System.out.println("There was some kind of error closing the file.");}

				}
				catch(Exception ex){
					System.out.println("Failed to write to (or create) file to store the data.");
					ex.printStackTrace();
				}
				finally{
					try{output.close();}catch(Exception ex){System.out.println("F There was some kind of error closing the file.");}
				}
				saveBufferedImage(currentImage,path+File.separator+"images"+File.separator+priorSamples.size()+".png");
			}

		}
	}
	public static void saveBufferedImage(BufferedImage img, String pathToImage){

		System.out.println("Saving image to: "+pathToImage);

		File outputfile = new File(pathToImage);
		outputfile.mkdirs();
		try {
			ImageIO.write(img, "png", outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	static int randomNumber(int min, int max){
		return min + (int)(Math.random() * ((max - min) + 1));
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Draw(g);
	}
	public void update(){
		
		//if the program isn't paused
		if(paused==false&&stopped == false){
			//if we're not taking a break;
			if(breakTime<=0){

				//if it is time for another action
				if(framesSinceLastAction>=framesPerAction){
					long startingTime = System.currentTimeMillis();
					if(true){


						System.out.println("------------------update called-----------------");

						//check if we are being shown the continue button
						Point continueButtonPos = contButton.checkForContinueButton();
						System.out.println("Took "+(System.currentTimeMillis()-startingTime)+"ms to get continue button position.");
						//if the image is not null or continue button visible
						if(currentImage!=null||continueButtonPos.x!=-1){
							//determine if all the guess answers that should be selected are selected

							whatToCheck = GamePanel.robot.createScreenCapture(new Rectangle(imagePosition.x+441,imagePosition.y-6,410,400));
							
							boolean allAreSelected = true;
							boolean correctAnswerIsShown = false;
							for(int i = 0; i<possibleChoices.size();i++){
								possibleChoices.get(i).getStats();
								if(possibleChoices.get(i).isGuess&&possibleChoices.get(i).isSelected==false){
									allAreSelected = false;
								}
								if(possibleChoices.get(i).isCorrect==true){
									correctAnswerIsShown=true;
								}
							}

							//System.out.println("first bit: "+(System.currentTimeMillis()-strtTime)+"ms");
							
							Color communityConsensusColor = robot.getPixelColor(imagePosition.x+60,imagePosition.y-37);
							
							if(correctAnswerIsShown&&!recentlySaved){
								System.out.println("state 3");
								state = 3;
								if(timeOfLastSample!=0){
									timeBetweenSamples.add(System.currentTimeMillis()-timeOfLastSample);
									if(timeBetweenSamples.size()>50){
										timeBetweenSamples.remove(0);
									}
								}
								timeOfLastSample = System.currentTimeMillis();
							}
							else if(!(communityConsensusColor.getRed()<=20&&communityConsensusColor.getGreen()<=20&&communityConsensusColor.getBlue()<=20)&&!recentlySaved){
								System.out.println("state 5");
								state = 5;
								if(timeOfLastSample!=0&&(System.currentTimeMillis()-timeOfLastSample)<60000){//if it took more than 60 seconds don't add it
									timeBetweenSamples.add(System.currentTimeMillis()-timeOfLastSample);
									if(timeBetweenSamples.size()>50){
										timeBetweenSamples.remove(0);
									}
								}
								timeOfLastSample = System.currentTimeMillis();
							}
							else if(continueButtonPos.x!=-1){//continue button is visible
								System.out.println("state 4");
								state = 4;
							}
							//go to the next stage if they are all selected as they should be
							else if(!allAreSelected&&answerClicks<5){
								System.out.println("state 1");
								state = 1;
							}
							else{//all are selected but correct answer isn't shown
								System.out.println("state 2");
								state = 2;
							}
							//System.out.println("second bit: "+(System.currentTimeMillis()-strtTime)+"ms");

							//1 = select all guess answers
							if(state==1){
								recentlySaved = false;

								for(int i = 0; i<possibleChoices.size();i++){
									if(possibleChoices.get(i).isGuess){
										if(possibleChoices.get(i).isSelected==false){
											possibleChoices.get(i).clickThis();

										}
									}
								}
								answerClicks++;

							}
							//2 = click submit
							else if(state==2){
								//long stateStart = System.currentTimeMillis();
								answerClicks = 0;

								int x = randomNumber(imagePosition.x+334,imagePosition.x+334+168);
								int y = randomNumber(imagePosition.y+456+10,imagePosition.y+456+28);

								//click the submit button
								click(x,y);
								//System.out.println("time since state start: "+(System.currentTimeMillis()-stateStart));
							}
							//3 = record proper answer
							else if(state==3){
								System.out.println("Recording answers");
								int totalRight = 0;
								int totalWrong = 0;
								for(int i = 0; i<possibleChoices.size();i++){	

									if(possibleChoices.get(i).isCorrect==true){
										totalRight++;
										answers.add(possibleChoices.get(i).name);
										System.out.println("Added "+possibleChoices.get(i).name+" as an answer.");
									}
									if(possibleChoices.get(i).isWrong){
										totalWrong++;
									}


								}
								accuracyOfRecentGuesses.add(new Point(totalWrong,totalRight));
								if(accuracyOfRecentGuesses.size()>100){
									accuracyOfRecentGuesses.remove(0);
								}
								boolean worthSaving = true;
								if(currentGuess!=null){
									if(similarity>=100){
										worthSaving = false;
									}
								}
								if(worthSaving){
									System.out.println("Saving sample.");
									saveSampleResults();
								}
								answers.clear();
								currentImage = null;
								state = 1;
								recentlySaved = true;

							}
							//4 = click continue
							else if(state==4){

								int x = randomNumber(continueButtonPos.x,continueButtonPos.x+60);
								int y = randomNumber(continueButtonPos.y,continueButtonPos.y+10);

								//click the continue button
								click(x,y);
							}
							else if(state==5){
								//System.out.println("Community consensus detected");
								answers.clear();
								currentImage = null;
								state = 1;
								recentlySaved = true;
							}
						}
						else if(currentImage==null){
							System.out.println("getting image");
							getImage();
						}
					}
					
					
					if(randomNumber(1,100)==1){
						timeUsedToday = getTimeUsedToday();
					}
					
					long fatigue = timeUsedToday/(1000*60*60);
					//randomize the delay until the next action to a time between 1 and 1.33+(.066 to .53 depending on time used) seconds
					framesPerAction = randomNumber(30,40+((int)fatigue*2));
					framesSinceLastAction=0;
					//move the mouse off of the area it should be clicking in
					if(oldMousePosition.x>imagePosition.x+1200||oldMousePosition.x<imagePosition.x-100){
						moveMouse(oldMousePosition.x+randomNumber(-10,10),oldMousePosition.y+randomNumber(-10,10));
					}
					else{
						moveMouse(AppletUI.location.x+randomNumber(200,230),AppletUI.location.y+randomNumber(-20,-50));
					}
					
					//check if time limit was reached

					if(timeUsedToday>maxDailyTime){
						stopped = true;
						System.out.println("Paused because the program cannot safely run any longer today. Time limit was reached.");

					}
					else{
						stopped = false;
						//if it's time for the next break
						if(System.currentTimeMillis()>=timeForNextBreak){
							//next break should be some time in the next 1 to 2 hours
							int hour = 1000*60*60;
							timeForNextBreak = System.currentTimeMillis()+randomNumber(hour,hour*2);
							//total duration of the next break should be 5 to 20 minutes
							breakTime=randomNumber(30*60*5,30*60*20);
							totalBreakTime+=breakTime;
						}

					}
					
					
					System.out.println("This action took: "+(System.currentTimeMillis()-startingTime)+"ms");
				}
				framesSinceLastAction++;
			}
			else{
				breakTime--;
			}
		}
		
	}

//	public Point checkForContinueButton(){
//		BufferedImage entireScreen = robot.createScreenCapture(new Rectangle(100,300,800,680));
//		for (int b = 0; b<4;b++){
//			Color continueStart1 = new Color(continueButton[0][b].getRGB(0, 0),true);
//			for(int i = 0; i<entireScreen.getWidth()-65;i++){
//				for(int j = 0; j<entireScreen.getHeight()-4;j++){
//					Color current = new Color(entireScreen.getRGB(i, j),true);
//					if(colorsAreSimilar(current,continueStart1)){
//						//if(current.getRed()==continueStart1.getRed()&&current.getGreen()==continueStart1.getGreen()&&current.getBlue()==continueStart1.getBlue()){
//						//System.out.println("found the start of continue button");
//						boolean found = true;
//						for(int x = 0; x<continueButton[0][b].getWidth();x++){
//							for(int y = 0; y<continueButton[0][b].getHeight();y++){
//								Color screenColor = new Color(entireScreen.getRGB(i+x, j+y));
//								Color contColor = new Color(continueButton[0][b].getRGB(x, y));
//								if(!colorsAreSimilar(screenColor, contColor)){
//									//if(!screenColor.equals(contColor)){
//									found=false;
//								}
//							}
//						}
//						if(found){
//							System.out.println("Found the continue button.");
//							return new Point(100+i,300+j);
//						}
//					}
//				}
//			}
//		}
//
//		return new Point(-1,-1);
//	}
	public static boolean checkIfImageFinishedLoading(){

		for(int i = 0;i<currentImage.getWidth()-loadingScreen.getWidth();i++){
			for(int j = 0; j<currentImage.getHeight()-loadingScreen.getHeight();j++){
				Color current = new Color(currentImage.getRGB(i, j),true);
				if(current.getRed()==9&&current.getGreen()==9&&current.getBlue()==9){
					boolean found = true;
					for(int x = 0; x<loadingScreen.getWidth();x++){
						for(int y = 0; y<loadingScreen.getHeight();y++){
							Color currColor = new Color(currentImage.getRGB(i+x, j+y));
							Color contColor = new Color(loadingScreen.getRGB(x, y));
							if(!currColor.equals(contColor)){
								found=false;
							}
						}
					}
					if(found){
						return false;
					}
				}
			}
		}
		return true;
	}
	public void Draw(Graphics g){
		
		update();
		
		Font font = new Font("Iwona Heavy",Font.BOLD,14);
		g.setFont(font);
		FontMetrics m = g.getFontMetrics(font);
		g.setColor(Color.white);
		if(currentImage!=null){


			//g.drawImage(choicesImage, 0, 0, null);
			g.drawImage(currentImage, 50, 300,240,240, null);
			g.drawString("Current Image", 190-(m.stringWidth("Current Image")/2), 570);
			if(guessImage!=null){
				g.drawImage(guessImage, 300, 300,240,240, null);
				g.drawString("Guess image", 420-(m.stringWidth("Guess image")/2), 570);
			}
			else{
				g.drawString("No image of guess was saved", 420-(m.stringWidth("No image of guess was saved")/2), 570);
			}
			if(currentChoice>0){
				g.drawImage(selectedIcon, possibleChoices.get(currentChoice).position.x, possibleChoices.get(currentChoice).position.y, null);

				if(possibleChoices.get(currentChoice).likelihood>0){
					g.drawImage(percentageIcon, possibleChoices.get(currentChoice).position.x, possibleChoices.get(currentChoice).position.y, null);
				}
			}
			g.drawString("Total red: ",420,15);
			g.drawString("Total green: ",420,35);
			g.drawString("Total blue: ",420,55);
			g.drawString("Total black: ",420,75);

			g.drawString(""+totalRed,520,15);
			g.drawString(""+totalGreen,520,35);
			g.drawString(""+totalBlue,520,55);
			g.drawString(""+totalBlack,520,75);

			g.drawString(new DecimalFormat("#.##").format(percentRed)+"%",610,15);
			g.drawString(new DecimalFormat("#.##").format(percentGreen)+"%",610,35);
			g.drawString(new DecimalFormat("#.##").format(percentBlue)+"%",610,55);
			g.drawString(new DecimalFormat("#.##").format(percentBlack)+"%",610,75);

			g.drawString("# Green pixels touching blue: ",420,105);
			g.drawString("# Green pixels touching red: ",420,125);
			g.drawString("# Blue pixels touching red: ",420,145);
			g.drawString("# Black pixels touching red: ",420,165);
			g.drawString("# Black pixels touching green: ",420,185);
			g.drawString("# Black pixels touching blue: ",420,205);

			g.drawString("# Red pixels touching red: ",420,225);
			g.drawString("# Green pixels touching green: ",420,245);
			g.drawString("# Blue pixels touching blue: ",420,265);
			g.drawString("# Black pixels touching black: ",420,285);

			g.drawString(""+amountOfGreenTouchingBlue,660,105);
			g.drawString(""+amountOfGreenTouchingRed,660,125);
			g.drawString(""+amountOfBlueTouchingRed,660,145);
			g.drawString(""+amountOfBlackTouchingRed,660,165);
			g.drawString(""+amountOfBlackTouchingGreen,660,185);
			g.drawString(""+amountOfBlackTouchingBlue,660,205);

			g.drawString(""+amountOfRedTouchingRed,660,225);
			g.drawString(""+amountOfGreenTouchingGreen,660,245);
			g.drawString(""+amountOfBlueTouchingBlue,660,265);
			g.drawString(""+amountOfBlackTouchingBlack,660,285);
			
			

			if(currentGuess!=null){
				//g.drawString("Was this guess correct?", 200-(m.stringWidth("Was this guess correct?")/2), 425);

			}
			else{
				g.setColor(Color.red);
				g.drawString("Not enough data to make a reasonable guess.", 200-(m.stringWidth("Not enough data to make a reasonable guess.")/2), 425);

				g.drawString("Click the options which represent the correct answer.", 200-(m.stringWidth("Click the options which represent the correct answer.")/2), 455);
			}


			//			//draw buttons
			//			for(int i = 0; i<buttons.size();i++){
			//				if(buttons.get(i).isActive){
			//					buttons.get(i).Draw(g);
			//				}
			//			}
			g.setColor(Color.white);
			g.drawString("Database size: "+priorSamples.size(),10,100);
			if(currentGuess!=null){
				g.drawString("Confidence in current guess: ",10,150);
				double totalWrong = 0;
				for(int i = 0; i<accuracyOfRecentGuesses.size();i++){
					if(accuracyOfRecentGuesses.get(i).x>0){
						totalWrong+=1;
					}
					//totalAnswers+=accuracyOfRecentGuesses.get(i).x+accuracyOfRecentGuesses.get(i).y;
				}
				double avgCorrectness = ((double)totalWrong/(double)accuracyOfRecentGuesses.size())*100.0;
				g.drawString("Percent wrong of "+accuracyOfRecentGuesses.size()+" recent samples: "+new DecimalFormat("#.##").format((avgCorrectness))+"%",10,250);
				font = new Font("Iwona Heavy",Font.BOLD,30);
				g.setFont(font);
				if(confidence<=20){
					g.setColor(Color.red);
				}
				else if(confidence>20&&confidence<=40){
					g.setColor(new Color(255,106,0));
				}
				else if(confidence>40&&confidence<=80){
					g.setColor(Color.yellow);
				}
				else if(confidence>80){
					g.setColor(Color.green);
				}
				g.drawString(new DecimalFormat("#.##").format((confidence))+"%",15+m.stringWidth("Confidence in current guess: "),150);
			}
			else{
				g.drawString("No similar samples in database.",10,150);
			}
		}
		else{
			g.drawString("Press f12 with this window not overlapping the project discovery window to begin.",10,200);
		}
		if(stopped){
			g.setColor(Color.red);
			for(int i = 0; i<10;i++){
				g.drawString("Program will resume tomorrow. Daily time limit reached.",10,50+(100*i));
			}
		}
		if(breakTime>0){
			g.setColor(Color.red);
			for(int i = 0; i<10;i++){
				g.drawString("Taking a break, program will resume in: "+(breakTime/30)+" seconds.",10,50+(100*i));
			}
		}
		font = new Font("Iwona Heavy",Font.BOLD,10);
		g.setFont(font);
		g.setColor(Color.white);
		double total = 0;
		for(int i = 0; i<timeBetweenSamples.size();i++){
			total+=timeBetweenSamples.get(i);
		}
		double averageTimeBetweenSamples = ((total/(double)timeBetweenSamples.size()))/1000.0;
		g.drawString("Average time per sample = "+new DecimalFormat("#.###").format(averageTimeBetweenSamples)+" seconds",10,50);
		long hours = (timeUsedToday/3600000);
		long minutes = (timeUsedToday%3600000)/60000;
		long seconds = (timeUsedToday-(hours*3600000)-(minutes*60000))/1000;
		g.drawString("Time used so far today = "+hours+" hours, "+minutes+" minutes, " + seconds+" seconds.",10,80);
		//draw current guess if there is one
		//		for(int i = 0;i<possibleChoices.size();i++){
		//			possibleChoices.get(i).Draw(g);
		//		}
		long percentTimeSlept = (long)((double)((double)timeSlept/(double)(System.currentTimeMillis()-timeStarted)*100.0));
		g.drawString("Percent of running time wasted waiting: "+percentTimeSlept+"%",10,35);
		
	}
}
