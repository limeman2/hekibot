package main.java.diet.nutella.hekibot.viewergame;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.diet.nutella.hekibot.HekiBot;
import main.java.diet.nutella.hekibot.controller.BotDriver;

public class ViewerGame {
	private boolean done;
	private List<String> stagingArea = new ArrayList<String>();
	
	public ViewerGame(String[] players) {
		for (String s : players) {
			this.stagingArea.add(s);
		}
		done = false;
	}
	
	public ViewerGame(HekiBot bot) {
		pickPlayers(bot);
		
		showUI(bot);
	}
	
	public void showUI (HekiBot bot) {
		while(!done) {
			printPlayers();
			
			System.out.println();
			System.out.println("[L]et's go!");
			System.out.println("[P]ick more players");
			System.out.println("[R]emove player");
			
			Scanner reader = new Scanner(System.in);
			String input = reader.nextLine();
			
			
			/*
			switch(input) {
				case "L":
					startGame();
					break;
					
				case "P":
					pickPlayers(bot);
					break;
					
				case "R":
					removePlayer();
					break;
			}*/
			
		}
	}
	
	public void printPlayers() {
		for (int i = 0; i < stagingArea.size(); i++) {
			System.out.println(i + ": " + stagingArea.get(i));
		}
	}
	
	private void pickPlayers(HekiBot bot) {
		boolean invalidInput = false;
		Scanner reader = new Scanner(System.in);
		
		do {
			System.out.println();
			System.out.println(bot.getQueueList());
			System.out.println();
			System.out.println("[F]irst X players");
			System.out.println("[C]ustom selection of entries");
			String input = reader.nextLine();
			
			/*
			switch (input) {
				case "F":
					System.out.println("How many players?");
				
					int numberOfPlayers = reader.nextInt();
					String[] players = bot.pickFromQueue(numberOfPlayers);
					
					//bot.sendMessage(BotDriver.CHANNEL_NAME, BotDriver.concatNames(players) + " has been selected for a game! Let's roll PogChamp");
					for(String s : players) stagingArea.add(s);
					bot.newViewerGame(this);
					break;
			
				case "C":
					break;
					
				default:
					System.out.println("Invalid input!");
					invalidInput = true;
					break;
			} */
		} while (invalidInput);
	}
	
	private void removePlayer() {
		System.out.println("Who do you wish to remove?");
		Scanner reader = new Scanner(System.in);
		int input = reader.nextInt();
		System.out.println(stagingArea.remove(input) + " has been removed from the staging area.");
	}
	
	public void startGame() {
		this.done = true;
	}
	
	public boolean isInGame(String[] names) {
		for (String player1 : stagingArea) {
			for(String player2 : names) {
				if (player1.equals(player2)) return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return BotDriver.concatNames((String[]) stagingArea.toArray()) + " are currently playing!";
	}
}
