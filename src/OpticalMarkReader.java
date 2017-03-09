import javax.swing.JFrame;

import processing.core.PImage;

/***
 * Class to perform image processing for optical mark reading
 * 
 */
public class OpticalMarkReader {
	

	/***
	 * Method to do optical mark reading on page image.  Return an AnswerSheet object representing the page answers.
	 * @param image
	 * @return
	 */
	public AnswerSheet processPageImage(PImage image, AnswerSheetFormat format) {
		image.filter(PImage.GRAY);
		int index = 0;
		int[] numProblems = format.getProblemCols();
		int[] xStarts = format.getProblemXStarts();
		int[] yStarts = format.getProblemYStart();
		Problem[] problems = new Problem[format.getNumProblems()];
		
		for (int col = 0; col < numProblems.length; col++) {								//loop over each column
			for (int problemNum = 0; problemNum < numProblems[col]; problemNum++) {
				if (index < 98) {//coment out later
				System.out.println(numProblems[col] + " vs. " + xStarts.length + ", " + yStarts.length);
				System.out.println(index + " vs. " + problems.length + "\n");
				problems[index++] = new Problem(xStarts[col], yStarts[col], format.getProblemWidth(),
						format.getProblemHeight(), format.getNumBubbles(), image);
				}
			}
		}
		
		return new AnswerSheet(format.getKeyLetters(), problems, image);
	}
	
	public int[][] getRectangleAt(int startRow, int startCol, int width, int height, PImage image) {
		int[][] rect = new int[height][width];
		
		for (int row = 0; row < rect.length; row++)
			for (int col = 0; col < rect[row].length; col++)
				rect[row][col] = getPixelAt(row, col, image);
		
		return rect;
	}
	
	/***
	 * Gets a pixel value out of the PImage at row and col
	 * @param row the row of the pixel
	 * @param col the col of the pixel
	 * @param image the image
	 * @return the pixel value at the row and col within the image
	 */
	public static int getPixelAt(int row, int col, PImage image) {
		image.loadPixels();
		int color = image.pixels[row*image.width+col] & 255;
//		System.out.println(color);
		return color;
	}
	
}
