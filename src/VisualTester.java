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
	int rectX = 0;
	int rectY = 0;
	int numClicks = 0;
	int numBubbles = 5;
	int procedureAdvances = 0;

	public void setup() {
		size(w, h);
		images = PDFHelper.getPImagesFromPdf("/omrtest.pdf");
	}

	public void draw() {
		background(255);
		if (images.size() > 0) {
			current_image = images.get(currentImageIndex);
			image(current_image, 0, 0);			// display image i
			fill(0);
			text(mouseX + "\t\t" + mouseY, 30, 30);
			noFill();
			if (shift.isPressed()) {
				fill(25, 25, 25, 50);
				int height = mouseY - rectY;
				int width = (mouseX - rectX)/(numBubbles-1);
				for (int i = 1; i < numBubbles; i++)
					line(rectX + width*i, rectY, rectX + width * i, rectY + height);
			}
			if ((numClicks+1) % 2 == 0) {
				rect(rectX, rectY, mouseX-rectX, mouseY-rectY);
			}
			
		}
	}

	public void mouseReleased() {
//		currentImageIndex = (currentImageIndex + 1) % images.size();			// increment current image
		this.rectX = mouseX;
		this.rectY = mouseY;
		numClicks++;
	}
	
	public int getNumBubbles() {
		return numBubbles;
	}

	public void setNumBubbles(int numBubbles) {
		this.numBubbles = numBubbles;
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
	
	public void keyPressed(KeyEvent e) {
		toggleKey(e.getKeyCode(), true);
	}

	public void keyReleased(KeyEvent e) {
		toggleKey(e.getKeyCode(), false);
	}
//
//	public void keyTyped(KeyEvent e) {
//	}

	public void toggleKey(int keyCode, boolean isPressed) {
		if (keyCode == KeyEvent.VK_SHIFT) {
			shift.toggle(isPressed);
		}
	}
}
