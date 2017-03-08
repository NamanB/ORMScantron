import java.awt.Event;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Scrollable;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PImage;
import sun.java2d.loops.DrawRect;

public class VisualTester extends PApplet implements KeyListener {
	ArrayList<PImage> images;
	PImage current_image;
	int currentImageIndex = 0;
	int w = 1200;
	int h = 900;
	private int rectX = 0, rectY = 0, problemWidth = 0, problemHeight = 0, startY = 0;
	private int numClicks = 0;
	private int procedureAdvances = 0;
	private int[] information = new int[2];//4];
	private int numBubbles = 5;
	private int[] problemCols = new int[] {25, 25, 25, 25};
	
	private boolean debug = false;
	private boolean sameLine = true;
	
	public void setup() {
		size(w, h);
		images = PDFHelper.getPImagesFromPdf("/omrtest.pdf");
	}

	public void draw() {
		background(255);
		if (images.size() > 0) {
			current_image = images.get(currentImageIndex);
			image(current_image, 0, 0);			// display image i
			
			if (debug) 
				text((mouseY-rectY) + "\t\t" + (mouseX-rectX) + "\t\t" + this.problemHeight + "\t\t" + this.problemWidth, 25, 200);
			
			displayGrid();
			runProcedure();
		}
	}

	public void mouseReleased() {
		if (shift.isPressed()) {
			if (procedureAdvances > 0) {
				information[0] = mouseX;
				information[1] = mouseY;
			}
			else {
				this.problemWidth = Math.abs(mouseX - rectX);
				this.problemHeight = Math.abs(mouseY - rectY);
				this.startY = mouseY - this.problemHeight;
				information[0] = mouseX;						//topX
				information[1] = mouseY;						//topY
//				information[2] = this.width;					//height
//				information[3] = this.height;					//width
			}
			procedureAdvances++;
		}
		this.rectX = mouseX;
		this.rectY = mouseY;
		numClicks++;
	}
	
	private void runProcedure() {
		fill(0);
		if (this.procedureAdvances == 0)
			text("Directions:\n - Click to draw rectangles. Right arrow advances a page, left goes back. "
					+ "Delete goes back to the previous step.\n\nDraw a rectangle at the top left corner around the "
					+ "first problem's bubbles. Hold shift to preview the grid.\nPlease click while"
					+ " holding shift once each square in the grid has exactly one bubble inside it "
					+ "for all the pages.", 25, 25);
		else if (this.procedureAdvances == problemCols.length)
			text("Answer sheet format created. Please close this window.", 25, 25);
		else if (this.procedureAdvances < problemCols.length) 
			text("Directions:\n - Right arrow advances a page, left goes back. "
					+ "Delete goes back to the previous step.\n\nDraw a rectangle at the top left corner around the "
					+ "column's problem bubbles. Hold shift to finalize preview the grid.\nPlease click while"
					+ " holding shift once each square in the grid has exactly one bubble inside it "
					+ "for all the pages.\nRepeat for col " + (procedureAdvances+1) + " and click"
					+ "in it's top corner", 25, 25);
			
	}
	
	private void displayGrid() {
		noFill();
		if (procedureAdvances > 0) {
			if (!sameLine) {
				this.startY = mouseY;
			}
			if (shift.isPressed()) 
				fill(25, 25, 25, 50);
			for(int i = 0; i < problemCols[0]; i++) 
				displayGridRect(this.problemHeight, this.problemWidth, mouseX, startY+this.problemHeight*i);
		} else if ((numClicks+1) % 2 == 0) {
			if (shift.isPressed()) {
				fill(25, 25, 25, 50);
				int height = mouseY - rectY;
				int width = mouseX - rectX;
				for (int i = 0; i < problemCols[0]; i++) {
					displayGridRect(height, width, rectX, rectY+height*i);
				}
			} else {
				noFill();
				rect(rectX, rectY, mouseX-rectX, mouseY-rectY);
			}
		}
	}
	
	private void displayGridRect(int height, int width, int startX, int startY) {
		for (int i = 1; i < numBubbles; i++)
			line(startX + width/numBubbles*i, startY, startX + width/numBubbles*i, startY+height);
		rect(startX, startY, width, height);
	}
	
	public int getNumBubbles() {
		return numBubbles;
	}

	public void setNumBubbles(int numBubbles) {
		this.numBubbles = numBubbles;
	}
	
	public int[] getInfo() {
		return information;
	}
	
	public int getProcedureAdvancements() {
		return procedureAdvances;
	}
	
	public void setProblemCols(int[] cols) {
		this.problemCols = cols;
	}
	
	public void setSameLine(boolean sameLine) {
		this.sameLine = sameLine;
	}
	
	public int getProblemWidth() {
		return problemWidth;
	}
	
	public int getProblemHeight() {
		return problemHeight;
	}

	public class Key {
		private int numTimesPressed = 0;
		private boolean pressed = false;

		public int getNumTimesPressed() {
			return numTimesPressed;
		}

		public boolean isPressed() {
			return pressed;
		}

		public void toggle(boolean isPressed) {
			pressed = isPressed;
			if (isPressed)
				numTimesPressed++;
		}
	}
	
	private Key shift = new Key();
	private Key right = new Key();
	private Key left = new Key();
	private Key delete = new Key();
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();
		
		toggleKey(code, false);
		if (code == KeyEvent.VK_RIGHT)
			currentImageIndex = (currentImageIndex + 1) % images.size();			// increment current image forward
		else if (code == KeyEvent.VK_LEFT)
			currentImageIndex = (currentImageIndex + images.size()-1) % images.size();
		else if (code == KeyEvent.VK_BACK_SPACE && procedureAdvances > 0)
			procedureAdvances--;
	}
	
	public void toggleKey(int keyCode, boolean isPressed) {
		if (keyCode == KeyEvent.VK_SHIFT) {
			shift.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_RIGHT) {
			right.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_LEFT) {
			left.toggle(isPressed);
		}
		if (keyCode == KeyEvent.VK_BACK_SPACE) {
			delete.toggle(isPressed);
		}
	}
	
}
