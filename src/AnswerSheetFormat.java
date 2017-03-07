import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import processing.core.PImage;

public class AnswerSheetFormat implements Serializable {
	private int problemHeight;
	private int problemWidth;
	private int[] problemXStarts;
	private int problemYStart;
	private int numBubbles;

	public AnswerSheetFormat(int problemHeight, int problemWidth, int[] problemXStarts, int problemYStart,
			int numBubbles) {
		this.problemHeight = problemHeight;
		this.problemWidth = problemWidth;
		this.problemXStarts = problemXStarts;
		this.problemYStart = problemYStart;
		this.numBubbles = numBubbles;
	}
	
	public static AnswerSheetFormat calculateFormat(PImage image) {
		VisualTester tester = new VisualTester();
		JFrame frame = new JFrame("PDF Calibration");
		frame.add(tester);
		frame.pack();
		frame.setVisible(true);
		tester.init();
		tester.start();
		
		//create a boolean for the checkbox values
		System.out.println(tester.isActive());
		while(!tester.isActive());
		
		tester.stop();
		tester.destroy();
		System.out.println("destroyed");

		return null;
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
	
	public boolean saveLevelToFile(String fileName) {
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
	
	public int getProblemYStart() {
		return problemYStart;
	}
	
	public void setProblemYStart(int problemYStart) {
		this.problemYStart = problemYStart;
	}
	
	public int getNumBubbles() {
		return numBubbles;
	}
	
	public void setNumBubbles(int numBubbles) {
		this.numBubbles = numBubbles;
	}

}
