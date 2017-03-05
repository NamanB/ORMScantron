import processing.core.PImage;

/***
 * A class to represent a set of answers from a page
 */
public class AnswerSheet {
	private String answers;
	private String bubbleLetters;
	private Problem[] problems;
	private PImage image;

	public AnswerSheet(String bubbleLetters, Problem[] problems, PImage image) {
		this.bubbleLetters = bubbleLetters;
		this.problems = problems;
		this.image = image;
	}
	
	public String matchLetter() {
		for (Problem problem : this.problems)
			problem.getBubbledAnswerIndexes();
		
		return null;
		
	}
	
}
