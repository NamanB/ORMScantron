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
		
		matchLetter();
	}
	
	public void matchLetter() {
		StringBuilder ans = new StringBuilder();
		for (int i = 0; i < problems.length; i++)
			ans.append(i + ". " + problems[i].bubbleAnswers(this.bubbleLetters) + "\n");
			
		this.answers = ans.toString();
	}
	
}
