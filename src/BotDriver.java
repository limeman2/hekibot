import java.io.IOException;
import java.util.Scanner;

import org.jibble.pircbot.IrcException;

public class BotDriver {
	public static String CHANNEL_NAME = "";
	
	public static String concatNames(String[] names) {
		StringBuilder sb = new StringBuilder();
		if (names.length == 1) return names[0];
		
		for (int i = 0; i < names.length - 1; i++) {
			sb.append(names[i]);
			if (i != names.length - 1) sb.append(",");
			sb.append(" ");
		}
		
		sb.append("and ").append(names[names.length - 1]);
		return sb.toString();
	}
	
	public static void main(String[] args) { 
		String oAuth = "";
		
		if (args.length < 2) {
			BotDriver.CHANNEL_NAME = "#limeman2";
			oAuth = args[0];
		} else {
			BotDriver.CHANNEL_NAME = args[0];
			oAuth = args[1];
		}
		
		String host = "irc.twitch.tv";
		int port = 6667;
		
		System.out.println("Starting up the bot...");
		HekiBot bot = new HekiBot();
		//bot.setVerbose(true);
		
		Scanner reader;
		
		try {
			bot.connect(host, port, oAuth);
			bot.joinChannel(CHANNEL_NAME);
			bot.sendMessage(CHANNEL_NAME, "HeyGuys ");
			System.out.println("hekibot is up and running!");
			System.out.println(bot.toString());
			
			while (true) {
				System.out.println();
				if (bot.isPaused()) {
					System.out.println("HekiBot is currently paused");
				} else {
					System.out.println("HekiBot is currently open");
				}
				System.out.println("Main menu");
				System.out.println("=============");
				System.out.println();
				System.out.println("[N]ew game");
				System.out.println("[M]essage to channel");
				System.out.println("[E]dit queue/current game");
				System.out.println("[S]how queue");
				System.out.println("[D]isplay current game");
				if (bot.isPaused()) {
					System.out.println("Un[P]ause bot");
				} else {
					System.out.println("[P]ause bot");
				}
				System.out.println("[CLEAR] queue");
				System.out.println("[Sh]ut down");
				reader = new Scanner(System.in);
				String input = reader.nextLine();
				switch (input) {
					case "N":
						new ViewerGame(bot);
						break;
					case "M":
						System.out.println("[Q] list");
						System.out.println("q [H]elp");
						System.out.println("[J]oin help");
						System.out.println("[C]ustom message");
						input = reader.nextLine();
						
						switch(input) {
							case "Q":
								bot.sendQueueList();
								break;
							case "H":
								break;
							case "J":
								break;
							case "C":
								System.out.println("Enter message:");
								input = reader.nextLine();
								bot.sendMessage(CHANNEL_NAME, input);
								break;
							default:
								System.out.println("Invalid input, returning to main menu.");
								break;
						}
						
						break;
						
					case "E":
						System.out.println("Edit [Q]ueue or current [G]ame?");
						input = reader.nextLine();
						if (input.equals("Q")) {
							while (true) {
								System.out.println();
								System.out.println(bot.getIndexedList());
								System.out.println("[A]dd player");
								System.out.println("[R]emove player");
								System.out.println("[B]ack");
								
								input = reader.nextLine();
								if (input.equals("A")) {
									System.out.println("Enter name:");									
									input = reader.nextLine();
									System.out.println("Enter a position:");
									int pos = reader.nextInt();
									
								} else if (input.equals("R")) {
									System.out.println("Enter name:");
									bot.removeFromQueue(reader.nextLine());
								} else if (input.equals("B")) {
									break;
								}
							}
							
						} else if (input.equals("G")) {
							
						}
						break;
					case "S":
						System.out.println();
						System.out.println(bot.getIndexedList());
						break;
					case "D":
						System.out.println();
						bot.printCurrentGame();
						break;
					case "P":
						bot.togglePause();
						break;
					case "CLEAR":
						System.out.println("Are you absolutely sure?!?!");
						input = reader.nextLine();
						if (input.equals("YES")) bot.clearQueue();
						break;
					case "Sh":
						System.out.println("Are you absolutely sure?!?!");
						input = reader.nextLine();
						if (input.equals("YES")) {
							bot.shutDown();
							System.exit(0);
						}
						break;
					default:
						System.out.println("Invalid input, try again!");
						break;
				}
			}
		} catch (IOException | IrcException e) {
			e.printStackTrace();
		}	
	}
}
