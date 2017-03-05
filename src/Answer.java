import processing.core.PImage;

public class Answer{
	private int topX;
	private int topY;
	private int height;
	private int width;
	private PImage image;
	private boolean isBubbled;
	private Problem problem;
	
	public Answer(Problem problem, int topX, int topY, int height, int width, PImage image) {
		this.problem = problem;
		this.topX = topX;
		this.topY = topY;
		this.height = height;
		this.width = width;
		this.image = image;
		
		calculateBubbled();
	}
	
	private void calculateBubbled() {
		int average = calculateAverage();
		
		this.isBubbled = (average > problem.getThreshold());
	}
	
	public int calculateAverage() {
		int average = 0;
		
		for (int r = this.topY; r < this.topY+height; r++)
			for (int c = this.topX; c < this.topX+width; c++) 
				average += OpticalMarkReader.getPixelAt(r, c, this.image);
		
		return average /= (this.image.height*this.image.width);
	}
	
	public boolean isBubbled() {
		return this.isBubbled;
	}

}
