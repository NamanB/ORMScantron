import java.util.ArrayList;
import java.util.Scanner;

import processing.core.PImage;

public class Main {
	public static final String PDF_PATH = "/omrtest.pdf";
	public static OpticalMarkReader markReader = new OpticalMarkReader();
	
	public static void main(String[] args) {
		System.out.println("Welcome!  I will now auto-score your pdf!");
		System.out.println("Loading file..." + PDF_PATH);
		ArrayList<PImage> images = PDFHelper.getPImagesFromPdf(PDF_PATH);
		
		System.out.println("Here is the list of the existing formats:");
		System.out.println(AnswerSheetFormat.getCreatedFormatNames());
		AnswerSheetFormat format;
		
		Scanner scanner = new Scanner(System.in);
		
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
		System.out.println(response + " format loaded\n");

		System.out.println("Scoring all pages...");
		scoreAllPages(images, format);

		System.out.println("Complete!");
		
		// Optional:  add a saveResults() method to save answers to a csv file
	}

	/***
	 * Score all pages in list, using index 0 as the key.
	 * 
	 * NOTE:  YOU MAY CHANGE THE RETURN TYPE SO YOU RETURN SOMETHING IF YOU'D LIKE
	 * 
	 * @param images List of images corresponding to each page of original pdf
	 */
	private static void scoreAllPages(ArrayList<PImage> images, AnswerSheetFormat format) {
		ArrayList<AnswerSheet> scoredSheets = new ArrayList<AnswerSheet>();

		// Score the first page as the key		
		AnswerSheet key = markReader.processPageImage(images.get(0), format);

		for (int i = 1; i < images.size(); i++) {
			PImage image = images.get(i);

			AnswerSheet answers = markReader.processPageImage(image, format);

			// do something with answers
		}
	}
}