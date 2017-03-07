import processing.core.PImage;

public class Problem {
	private int topX;
	private int topY;
	private int width;
	private int height;
	private int numBubbles;
	private int threshold;
	private Answer[] answers;
	private PImage image;
	
	private static final int OFFSET = 10;
	
	public Problem(int topX, int topY, int width, int height, int numBubbles, PImage image) {
		this.topX = topX;
		this.topY = topY;
		this.width = width;
		this.height = height;
		this.numBubbles = numBubbles;
		this.answers = new Answer[numBubbles+1];
		this.image = image;
		
		calculateThreshold();
		createAnswers();
	}
	
	private void createAnswers() {
		int answerWidth = image.width/numBubbles, index = 0;
		
		for (int c = this.topX; c < this.topX+width; c += answerWidth) 
			this.answers[index++] = new Answer(this, c, this.topY, this.height, answerWidth, image);
	}

	private void calculateThreshold() {
		for (Answer answer : this.answers)
			this.threshold += answer.calculateAverage();
		
		this.threshold /= answers.length;
		this.threshold -= this.OFFSET;
	}

	/***
	 * Returns the darkest Bubble within a given 2d array of pixels with the problem starting at row r, column c,
	 * and being width x height having numBubbles as the number of answers within the problem
	 * @param r the starting index of the row
	 * @param c the starting index of the col
	 * @param width the width of the problem image
	 * @param height the height of the problem image
	 * @param numBubbles the number of bubbles within the problem
	 * @param pixels the pixels of the image
	 * @return the index of the darkest bubble within the problem
	 */
	public int determineDarkestBubble(int r, int c, int width, int height, int numBubbles, int[][] pixels) {
		int darkestIndex = 255, darkestVal = 0, index = 0;

		for (int col = c; col < pixels[0].length-width; col += pixels[0].length/numBubbles) {	//loop over each answerblock
			int sum = 0;
			for (int i = r; i < (pixels.length/numBubbles)*r; i++)								//add up values inside answerblocks
				for (int j = col; j < (pixels[0].length/numBubbles)*col; j++)
					sum += pixels[i][j];

			if (sum < darkestVal) {																//compare
				darkestIndex = index;
				darkestVal = sum;
			}
			index++;
		}
		return darkestIndex;
	}

	public int getThreshold() {
		return this.threshold;
	}
	
	public String getBubbledAnswerIndexes() {
		StringBuilder answers = new StringBuilder();
		
		for (int index = 0; index < this.answers.length; index++) {
			if (this.answers[index].isBubbled())
				answers.append(index + ", ");
		}
		
		return answers.toString().substring(0, answers.length() - 2);
	}
	
	public Answer[] getBubbledAnswers() {
		int size = calculateNumBubbledAnswers(), index = 0;
		Answer[] bubbledAnswers = new Answer[size];
		
		for (Answer answer : this.answers)
			if (answer.isBubbled())
				bubbledAnswers[index++] = answer;
		
		return bubbledAnswers;
	}

	private int calculateNumBubbledAnswers() {
		int size = 0;
		
		for (Answer answer : this.answers)
			if (answer.isBubbled()) size++;
		
		return size;
	}
	
	public String bubbleAnswers(String bubbleLetters) {
		String answerIndexes = getBubbledAnswerIndexes();
		StringBuilder answers = new StringBuilder();
		int size = answerIndexes.length();
		
		for (int i = 0; i < size; i++) {
			int index = Integer.parseInt(answerIndexes.substring(i, i+1));
			answers.append(bubbleLetters.substring(index, index+1));
		}
		
		return answers.toString();
	}
	
}
