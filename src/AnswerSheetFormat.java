import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import processing.core.PImage;

public class AnswerSheetFormat implements Serializable {
	private int problemHeight;
	private int problemWidth;
	private int[] problemXStarts;
	private int[] problemYStarts;
	private int[] problemCols;		//number of problems in each column
	private String keyLetters;
	
	public static void main(String[] args) {
		System.out.print("Please type the path of the pdf file > ");
		Scanner scanner = new Scanner(System.in);
		String path = scanner.nextLine();								//sampe path = Main.PDF_PATH
		System.out.print("Please type the name of the format > ");
		String name = scanner.nextLine();
		createAndSaveNewAnswerSheetFormat(path, name);
	}
	
	public static void createAndSaveNewAnswerSheetFormat(String pdfPath, String name) {
		System.out.println("Time to create a new format!");
		System.out.println("Loading file..." + pdfPath);
		ArrayList<PImage> images = PDFHelper.getPImagesFromPdf(pdfPath);
		AnswerSheetFormat format = calculateFormat(images.get(0));
		format.saveFormatToFile(name);
	}
	
	public AnswerSheetFormat(int[] problemCols, int problemHeight, int problemWidth, int[] problemXStarts, int problemYStarts[],
			String keyLetters) {
		this.problemHeight = problemHeight;
		this.problemWidth = problemWidth;
		this.problemXStarts = problemXStarts;
		this.problemYStarts = problemYStarts;
		this.problemCols = problemCols;
		this.keyLetters = keyLetters;
	}
	
	private AnswerSheetFormat(int[] problemCols, int[][] information, int problemHeight, int problemWidth, String keyLetters) {
		problemXStarts = new int[information.length];
		problemYStarts = new int[information.length];
		
		for (int i = 0; i < information.length; i++) {
			problemXStarts[i] = information[i][0];				//match information to VisualTester information array
			problemYStarts[i] = information[i][1];
		}
		this.problemHeight = problemHeight;
		this.problemWidth = problemWidth;
		this.keyLetters = keyLetters;
		this.problemCols = problemCols;
	}
	
	public static AnswerSheetFormat calculateFormat(PImage image) {
		Scanner scanner = new Scanner(System.in);
		
		//add a choose format or create new format option here
		System.out.print("What are the letters for each of the bubbles?(type just letters without spaces) > ");
		String response = scanner.nextLine();
		String letters = response;
		int numBubbles = letters.length();
		
		System.out.print("Do the columns for the problems begin in the same horizontal line?(true or false) > ");
		response = scanner.nextLine();
		boolean sameLine = Boolean.parseBoolean(response);
		
		System.out.print("How many columns of problems are there? > ");
		response = scanner.nextLine();
		int[] problemCols = new int[Integer.parseInt(response)];
		
		for (int i = 0; i < problemCols.length; i++) {
			System.out.print("How many problems are there in column " + (i+1) + "? > ");
			response = scanner.nextLine();
			problemCols[i] = Integer.parseInt(response);
		}
		scanner.close();
		System.out.println("Please expand and look at the pop up window");
		
		VisualTester tester = new VisualTester();
		tester.setNumBubbles(numBubbles);
		tester.setProblemCols(problemCols);
		JFrame frame = new JFrame("PDF Calibration");
		frame.setSize(tester.w, tester.h);
		frame.add(tester);
//		JPanel container = new JPanel();	//new stuff
//		container.add(tester);
//		JScrollPane jsp = new JScrollPane(container);
//		frame.add(jsp);
		
		frame.pack();
		frame.setVisible(true);
		tester.init();
		tester.start();
		tester.setSameLine(sameLine);
		
		int[][] problemInfo = new int[problemCols.length][2];
		//create a boolean for the checkbox values
		int advancements = 0, previousAdvance = advancements-1;
		int problemWidth = 0, problemHeight = 0;
		
		while (tester.getProcedureAdvancements() < problemCols.length) {
			
			System.out.println("look at window, buffering...");		//need to find a different way to slow the speed down to get info
			if (tester.getProcedureAdvancements() != advancements) {
				advancements = tester.getProcedureAdvancements();
			}

			if (previousAdvance+2 == advancements) {
				previousAdvance = advancements-1;
				problemInfo = transferData(advancements-1, tester.getInfo(), problemInfo);
			}
		}
		for (int i = 0; i < Integer.MAX_VALUE; i++) {}	//delay for tester to display closing message
		System.out.println("done");
		problemHeight = tester.getProblemHeight();
		problemWidth = tester.getProblemWidth();
		
		tester.stop();
		tester.destroy();
		
		return new AnswerSheetFormat(problemCols, problemInfo, problemHeight, problemWidth, letters);
	}
	
	public static AnswerSheetFormat loadFormatFromFile(String fileName) {
		String path = fileName + ".ser";
		
		if (path.indexOf("/") == -1)
			path = "sheetFormats/" + path;
		
		AnswerSheetFormat format = null;
	      try {
	         FileInputStream fileIn = new FileInputStream(path);
	         ObjectInputStream in = new ObjectInputStream(fileIn);
	         format = (AnswerSheetFormat) in.readObject();
	         in.close();
	         fileIn.close();
	      }catch(IOException i) {
	         i.printStackTrace();
	         return null;
	      }catch(ClassNotFoundException c) {
	         System.out.println("Format not found");
	         c.printStackTrace();
	         return null;
	      }
	      return format;
	}
	
	public boolean saveFormatToFile(String fileName) {
		fileName += ".ser";
		try {
	         FileOutputStream fileOut =
	         new FileOutputStream("sheetFormats/" + fileName);
	         ObjectOutputStream out = new ObjectOutputStream(fileOut);
	         out.writeObject(this);
	         out.close();
	         fileOut.close();
	         System.out.printf("Serialized data is saved in sheetFormats/" + fileName);
	      }catch(IOException i) {
	    	  i.printStackTrace();
	    	  return false;
	      }
		return true;
	}

	public static String getCreatedFormatNames() {
		StringBuilder formatNames = new StringBuilder();
		File folder = new File("sheetFormats/");
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				String fileName = listOfFiles[i].getName();
				if (fileName.substring(fileName.length() - 4).equals(".ser")) 
					formatNames.append(fileName.substring(0, fileName.length() - 4) + "\n");
			}
		}
		return formatNames.toString();
	}
	
	public static int[][] transferData(int row, int[] information, int[][] answer) {
		if (answer[row].length < information.length)
			return null;
		
		for (int i = 0; i < information.length; i++)
			answer[row][i] = information[i];
		return answer;
	}
	
	public int getProblemHeight() {
		return problemHeight;
	}

	public void setProblemHeight(int problemHeight) {
		this.problemHeight = problemHeight;
	}
	
	public int getProblemWidth() {
		return problemWidth;
	}
	
	public void setProblemWidth(int problemWidth) {
		this.problemWidth = problemWidth;
	}
	
	public int[] getProblemXStarts() {
		return problemXStarts;
	}
	
	public void setProblemXStarts(int[] problemXStarts) {
		this.problemXStarts = problemXStarts;
	}
	
	public int[] getProblemYStart() {
		return problemYStarts;
	}
	
	public void setProblemYStart(int[] problemYStart) {
		this.problemYStarts = problemYStart;
	}
	
	public String getKeyLetters() {
		return keyLetters;
	}
	
	public void setKeyLetters(String keyLetters) {
		this.keyLetters = keyLetters;
	}
	
	public int getNumBubbles() {
		return keyLetters.length();
	}

	public int[] getProblemCols() {
		return problemCols;
	}
	
	public int getNumProblems() {
		int sum = 0;
		
		for (int i = 0; i < problemCols.length; i++)
			sum += problemCols[i];
		
		return sum;
	}

}
