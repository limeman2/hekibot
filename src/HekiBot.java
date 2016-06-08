import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.jibble.pircbot.PircBot;

public class HekiBot extends PircBot {
	
	////// Database file
	
	
	////// Queue object
	private Queue q;
	
	///// Mods and people
	private String hekibot = "hekibot";
	private String heki = "hekimae";
	private String lime = "limeman2";
	//private String hekibotName = "hekibot";
	private static String[] MOD_LIST = {
			"hekimae",
			"7updiet",
			"bx3waage",
			"aske347",
			"bengt53",
			"centane",
			"chloelock",
			"henryjiii",
			"khiiess",
			"limeman2",
			"mav3r1ck1989",
			"riggsy99",
			"thebstrike",
			"mindfartio"
	};
	
	////////// Queue command strings ///////////
	private String queueCmd = "!q";
	private String oldCmd = "!queue";
	private String oldQPlusXcmd = "!q\\+";
	private String qListCmd = "!qlist";
	private String qPosCmd = "!qpos";
	private String clearQCmd = "!clearq";
	private String leaveQCmd1 = "!leaveq";
	private String leaveQCmd2 = "!qleave";
	private String commandsCmd1 = "!qhelp";
	private String commandsCmd2 = "!commands";
	private String gNightCmd = "!quit";
	private String lastGameCmd = "!lastgame";
	private String modNightCmd = "!MODNIGHT";
	
	////////// hekiCoins command strings ///////////
	private String hcRemove = "!hekicoins remove ";
	
	HashMap<Integer, String> hcResponses = new HashMap<Integer, String>();
	
	private String hcSongCmd = "!hcsong";
	private int hcSongCost = 10;
	private String hcSongResponse = " has just bought a song request for 10 hekicoins!";
	
	private String hcMapCmd = "!hcmap";
	private int hcMapCost = 100;
	private String hcMapResponse = " has just picked the next map for 100 hekicoins!";
	
	private String[] hcCommands = { hcSongCmd, hcMapCmd };
	private int[] hcCosts = { hcSongCost, hcMapCost };
	
	private String hcErrorResponse = "An error has occured. Abort mission!";
	
	////// Misc vars 
	private ViewerGame currentGame;
	private boolean isLastGame = false;
	private boolean isModNight = false;
	private boolean queueIsOpen;
	private String reasonForClosedQueue;
	private final String UNKNOWN_REASON = "the queue is closed right now!";
	//private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	//private Calendar rightNow = Calendar.getInstance();
	
	public HekiBot(boolean queueIsOpen) {
		this.setName("hekibot");
		reasonForClosedQueue = UNKNOWN_REASON;
		q = new Queue();
		this.queueIsOpen = queueIsOpen;
		hcResponses.put(-1, hcErrorResponse);
		hcResponses.put(hcSongCost, hcSongResponse);
		hcResponses.put(hcMapCost, hcMapResponse);
	}
	
	public HekiBot() {
		this(true);
	}
	
	@Override
	protected void onDisconnect() {
		System.out.println("Disconnected. Trying to reconnect...");
		System.out.println(q.queueList());
		try {
			this.reconnect();
			this.joinChannel("#hekimae");
			this.sendMessageAndPrint("#hekimae", "I just disconnected, but now I'm back.");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	@Override 
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {
		
		
		////////// hekiCoin commands ///////////
		
		for (int i = 0; i < hcCommands.length; i++) {
			if (message.equalsIgnoreCase(hcCommands[i])) {
				sendMessageAndPrint(channel, hcRemove + sender + " " + hcCosts[i]);
			}
		}
		
		if (message.matches("Removed \\d* hekicoins from .*") && sender.equals(hekibot)) {
			Scanner scan = new Scanner(message);
			boolean costSet = false;
			int cost = -1;
			String buyer = null;
			
			while (scan.hasNext()) {
				if (scan.hasNextInt() && !costSet) {
					cost = scan.nextInt();
					costSet = true;
				 }
				 
				 buyer = scan.next();
			}
			scan.close();
			
			sendMessageAndPrint(channel, buyer + hcResponses.get(cost));
		}
		
		////////// Queue commands ///////////
		
		if (message.startsWith("!")) {				
			if ((sender.equals(heki) || sender.equals(lime))) {
				if (message.equalsIgnoreCase(clearQCmd))
					sendMessageAndPrint(channel, q.clearQueue());
				
				if (message.equalsIgnoreCase(gNightCmd)) {
					shutDown();
				} 
				
				if (message.equalsIgnoreCase(lastGameCmd)) {
					if (!isLastGame) {
						sendMessageAndPrint(channel, "This will be the last game, folks! Sorry if you're still in queue :(");
						reasonForClosedQueue = "this is the last game for tonight!";
						isLastGame = true;
						queueIsOpen = false;
					} else {
						sendMessageAndPrint(channel, "Nevermind! We'll play some more.");
						reasonForClosedQueue = UNKNOWN_REASON;
						isLastGame = false;
						queueIsOpen = true;
					}
				}
				
				if (message.equalsIgnoreCase(modNightCmd)) {
					if (!isModNight) {
						sendMessageAndPrint(channel, "IT'S MOD NIGHT, LADIES AND GENTLEMEN! ");
						isModNight = true;
					} else {
						sendMessageAndPrint(channel, "MOD NIGHT IS NOW OVER, FOLKS! ");
						isModNight = false;
					}
					
				}
			}
			
			if (message.matches(oldQPlusXcmd + "\\d")) {
				sendMessageAndPrint(channel, sender + ", you have to write your friends twitch name instead of the number. For example: !q+Riggso99 or !q+HenryJill+BX3EspEn ");
			}
			
			if ((message.equalsIgnoreCase(queueCmd) || message.matches(queueCmd + "\\+.*")) && !message.matches(oldQPlusXcmd + "\\d")) {
				Scanner scan = new Scanner(message);
				scan.useDelimiter("\\+");
				scan.next();
				ArrayList<String> players = new ArrayList<String>();
				players.add(sender);
				
				while(scan.hasNext()) {
					players.add(scan.next().toLowerCase());
				}
				
				String[] playersArray = new String[players.size()];
				
				for(int i = 0; i < players.size(); i++) {
					playersArray[i] = players.get(i).toLowerCase();
				}
					
				if (isModNight) {
					if (isOp(sender)) {
						if (queueIsOpen) {
							sendMessageAndPrint(channel, q.addEntry(playersArray));
						} else {
							sendMessageAndPrint(channel, "Sorry, " + sender + ", " + reasonForClosedQueue + " ");
						}
					} else {
							sendMessageAndPrint(channel, "Sorry, " + sender + ", tonight is MOD NIGHT!" + " ");
						}
				} else {
					if (queueIsOpen) {
						if (currentGame != null && currentGame.isInGame(playersArray)) {
							sendMessageAndPrint(channel, "You are already playing, " + sender + "!!!" + " ");
						} else {
							String output = q.addEntry(playersArray);
							sendMessageAndPrint(channel, output + " ");
						}
					} else {
						sendMessageAndPrint(channel, "Sorry, " + sender + ", " + reasonForClosedQueue + " ");
					}
				}
				scan.close();
			}	
			
			if (message.matches(queueCmd + "\\-.*")) {
				sendMessageAndPrint(channel, q.removeFromQueue(sender, message.substring(3)));
			}
			
			if (message.equalsIgnoreCase(qListCmd) && isOp(sender)) {
				sendMessageAndPrint(channel, q.queueList());
			}
			
			
			if (message.equalsIgnoreCase(qPosCmd)) {
				int pos = q.queuePosition(sender);
				if (pos == 0) {
					sendMessageAndPrint(channel, "You are not currently in the queue, " + sender);
				} else {
					sendMessageAndPrint(channel, "You are currently number " + Integer.toString(pos) + " in the queue, " + sender + ".");
				}	
			}
			
			if (message.equalsIgnoreCase(leaveQCmd1) || message.equalsIgnoreCase(leaveQCmd2)) {
				if (isModNight) {
					if(isOp(sender)) {
						sendMessageAndPrint(channel, q.leaveQueue(sender));
					} else {
						sendMessageAndPrint(channel, "Sorry, " + sender + ", tonight is MOD NIGHT!");
					}
				} else {
					sendMessageAndPrint(channel, q.leaveQueue(sender));
				}	
			}
			 
			if (message.equalsIgnoreCase(oldCmd)) {
				sendMessageAndPrint(channel, "We have changed the command to !q, " + sender + ".");
			}
			
			if (message.equalsIgnoreCase(commandsCmd1) || message.equalsIgnoreCase(commandsCmd2)) {
				sendMessageAndPrint(channel, "Available commands: !q, !q+[name], !q+[name1]+[name2], !qlist (mods only), !qpos, !leaveq");
			}
		}
	} 
	
	///////// Below: functions to be used by me in the console window
	
	public String addToQueue(String[] players) {
		return q.addEntry(players);
	}
	
	public void newViewerGame(ViewerGame vg) {
		this.currentGame = vg;
	}
	
	public String[] pickFromQueue(int amount) {
		return q.pickFromQueue(amount);
	}
	
	public void removeFromQueue(String name) {
		q.removeFromQueue(name, name);
	}
	
	public String getQueueList() {
		return q.queueList();
	}
	
	public String getIndexedList() {
		return q.indexedList();
	}
	
	public boolean isPaused() {
		return !queueIsOpen;
	}
	
	public void togglePause() {
		queueIsOpen = !queueIsOpen;
	}
	
	public void shutDown() {
		sendMessageAndPrint(BotDriver.CHANNEL_NAME, "G'NIGHT ResidentSleeper");
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			this.dispose();
		}
	}
	
	public void sendQueueList() {
		sendMessage(BotDriver.CHANNEL_NAME, "The queue: " + q.queueList());
	}
	
	public void clearQueue() {
		sendMessage(BotDriver.CHANNEL_NAME, "The queue has been cleared!");
		q.clearQueue();
	}
	
	public void printCurrentGame() {
		if (currentGame == null) {
			System.out.println("There is no current game!");
		} else {
			this.currentGame.printPlayers();
		}
	}
	
	private void sendMessageAndPrint(String channel, String message) {
		//showChatMessage("hekibot", message);
		sendMessage(channel, message);
	}
	
	private boolean isOp(String sender) {
		for (String name : MOD_LIST) {
			if (name.equals(sender))
				return true;
		}
		return false;
	}
}