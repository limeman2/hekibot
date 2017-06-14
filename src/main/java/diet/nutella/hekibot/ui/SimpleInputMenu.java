package main.java.diet.nutella.hekibot.ui;

import java.util.Scanner;

public class SimpleInputMenu {
	
	public static String getInputAsString(String headLine, Scanner inputReader) {
		System.out.println(headLine);
		String in = inputReader.nextLine();
		return in;
	}
	
	private SimpleInputMenu(){}
}
