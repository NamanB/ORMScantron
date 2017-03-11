import java.util.ArrayList;
import java.util.Scanner;

import processing.core.PImage;

public class Main {
	public static final String PDF_PATH = "/omrtest.pdf";
	public static OpticalMarkReader markReader = new OpticalMarkReader();
	public static String filePath = "/CSVAnalytics/";
	
	public static void main(String[] args) {
		System.out.println("Welcome!  I will now auto-score your pdf!");
		System.out.println("Loading file..." + PDF_PATH);
		ArrayList<PImage> images = PDFHelper.getPImagesFromPdf(PDF_PATH);
		
		Scanner scanner = new Scanner(System.in);
		System.out.print("Please type a name to save the scores as > ");
		String name = scanner.nextLine();
		
		System.out.println("Here is the list of the existing formats:");
		System.out.println(AnswerSheetFormat.getCreatedFormatNames());
		AnswerSheetFormat format;
		
		//add a choose format or create new format option here
		System.out.print("Type one of the scantron formats listed above or type \"new\" to create a new format > ");
		String response = scanner.nextLine();
		if (response.equals("new")) {
			System.out.print("Name the file > ");
			response = scanner.nextLine();
			format = AnswerSheetFormat.calculateFormat(images.get(0));
			format.saveFormatToFile(response);
		} else
			format = AnswerSheetFormat.loadFormatFromFile(response);
		scanner.close();
		System.out.println("\n" + response + " format loaded\n");

		System.out.println("Scoring all pages...");
		scoreAllPages(images, format, filePath+name);

		System.out.println("Complete!");
		
	}

	private static void saveResults(String name, String filePath, CSVData data) {
		System.out.println("Saving Data...");
		data.saveCurrentState(filePath + name);
	}

	/***
	 * Score all pages in list, using index 0 as the key.
	 * 
	 * NOTE:  YOU MAY CHANGE THE RETURN TYPE SO YOU RETURN SOMETHING IF YOU'D LIKE
	 * 
	 * @param images List of images corresponding to each page of original pdf
	 */
	private static void scoreAllPages(ArrayList<PImage> images, AnswerSheetFormat format, String filePath) {
		ArrayList<AnswerSheet> scoredSheets = new ArrayList<AnswerSheet>();
		CSVData data = new CSVData(filePath, 0, new String[]{"# correct", "# incorrect", " % correct", "% incorrect"});
		CSVData analytics = new CSVData(filePath + "Analytics", 0, new String[] {"Problem", "Number incorrect", "Percent Correct"});
		double[][] answerData = new double[4][images.size()];

		// Score the first page as the key		
		AnswerSheet key = markReader.processPageImage(images.get(0), format);
		String answerKey = key.getAnswers();
		
		double[][] analyticsData = new double[answerKey.length()][3];
		
		for (int i = 0; i < analyticsData.length; i++)
			analyticsData[i][0] = i+1;

		System.out.println("got to here");
		for (int i = 1; i < images.size(); i++) {
			PImage image = images.get(i);

			double correct = 0, incorrect = 0, perCorrect = 0, perIncorrect = 0;
			AnswerSheet answers = markReader.processPageImage(image, format);
			String studentAnswers = answers.getAnswers();

			// do something with answers
			for (int j = 0; j < answerKey.length(); j++) {
				if (studentAnswers.substring(j, j+1).equals(answerKey.substring(j, j+1)))
					correct++;
				else {
					incorrect++;
					analyticsData[1][i]++;
				}
			}
			answerData[i][0] = correct;
			answerData[i][1] = incorrect;
			answerData[i][2] = (correct / answerKey.length()) * 100;
			answerData[i][3] = 100 - answerData[i][2];
		}
		
		for (int i = 0; i < analyticsData.length; i++) {
			analyticsData[i][2] = analyticsData[i][3] / images.size();
		}
		
		data.setData(answerData);
		data.saveCurrentState(data.getFilePath());
		analytics.setData(analyticsData);
		analytics.saveCurrentState(analytics.getFilePath());
	}
}