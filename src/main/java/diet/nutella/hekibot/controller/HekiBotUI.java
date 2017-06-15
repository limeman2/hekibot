package main.java.diet.nutella.hekibot.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.pircbotx.PircBotX;
import org.pircbotx.output.OutputIRC;

import main.java.diet.nutella.hekibot.loyaltytracker.LoyaltyTracker;
import main.java.diet.nutella.hekibot.ui.Callable;
import main.java.diet.nutella.hekibot.ui.Menu;
import main.java.diet.nutella.hekibot.ui.MenuEntry;

public class HekiBotUI {
	
	///// Static Scanner object for user input
	private Scanner inputReader;
	
	///// Fields
	private PircBotX bot;
	private Menu mainMenu;
	
	///// Hard coded main menu layout 		
	
	private final MenuEntry connectEntry; 
	
	private final MenuEntry disconnectEntry;
	
	private final MenuEntry showUsersEntry;
	
	private final MenuEntry quitEntry;
	
	private final MenuEntry forceTrackEntry;
	
	//private final MenuEntry STOP_TRACKING_ENTRY = new MenuEntry("S", "Stop tracking");
	//private final MenuEntry QUIT_ENTRY = new MenuEntry("Q", "Quit");

	private List<MenuEntry> mainMenuEntries;
	
	private static final String MAIN_MENU_TEXT = "Main menu";
	
	public HekiBotUI(PircBotX bot) {
		this.bot = bot;
		
		///// Initialize main menu object
		
		mainMenuEntries = new ArrayList<MenuEntry>();
		
		connectEntry = new MenuEntry("C", "Connect to Twitch", 
				new TwitchConnector(bot, true));
		
		disconnectEntry = new MenuEntry("D", "Disconnect from Twitch", 
				new TwitchConnector(bot, false));
		
		showUsersEntry = new MenuEntry("S", "Show users in channel", new Callable() {
				public void call() {
					LoyaltyTracker.getInstance().printUsers();
				}
			}
		);
		
		forceTrackEntry = new MenuEntry("F", "Force tracking DEV ONLY", new Callable() {
			public void call() {
				LoyaltyTracker.getInstance().forceTrack();
			}
		});
		
		quitEntry = new MenuEntry("TQ", "Terminate and quit", new Callable() {
			public void call() {
				OutputIRC irc = new OutputIRC(bot);
				if (bot.isConnected()) {
					System.out.println("Terminating bot...");
					irc.message(BotDriver.CHANNEL_NAME, "I'm outta here!");
					irc.quitServer();
					bot.close();
				} 
				System.out.println("Bye!");
				System.exit(0);
			}
		});
		
		mainMenuEntries.add(quitEntry);
		mainMenuEntries.add(showUsersEntry);
		mainMenuEntries.add(forceTrackEntry);
		
		inputReader = new Scanner(System.in);
		
		///// Create Main Menu object
		mainMenu = new Menu(MAIN_MENU_TEXT, mainMenuEntries, inputReader);
		
		//// Update main menu, primarily to add the Connect entry
		updateMainMenu();
		
		///// Run main loop when UI is instantiated
		mainLoop();
	}
	
	private void mainLoop() {
		while(true) {
			updateMainMenu();
			printMainMenuInformation();
			System.out.println();
			mainMenu.printMenu();
			mainMenu.handleInput();	
		}
	}
	
	private void updateMainMenu() {
		if (bot.isConnected()) {
			if (!mainMenu.contains(disconnectEntry)) {
				mainMenu.addEntry(disconnectEntry, 0);
			}
			if (mainMenu.contains(connectEntry)) {
				mainMenu.removeEntry(connectEntry);
			}
		} else {
			if (!mainMenu.contains(connectEntry)) {
				mainMenu.addEntry(connectEntry, 0);
			}
			if (mainMenu.contains(disconnectEntry)) {
				mainMenu.removeEntry(disconnectEntry);
			}
			
		}
	}
	
	private void printMainMenuInformation() {
		if (bot.isConnected()) {
			System.out.println("HekiBot is connected to Twitch.");
			
		} else {
			System.out.println("HekiBot is not connected.");
		}
		System.out.print("HekiBot is currently ");
		if (!LoyaltyTracker.getInstance().isTracking()) {
			System.out.print("NOT ");
		}
		
		System.out.println("tracking loyalty.");
	}
	
}
