package main.java.diet.nutella.hekibot;

import java.util.HashMap;



import main.java.diet.nutella.hekibot.loyaltytracker.LoyaltyTracker;
import main.java.diet.nutella.hekibot.viewergame.Queue;
import main.java.diet.nutella.hekibot.viewergame.ViewerGame;

public class HekiBot  {
	
	////// Singleton pattern
	private static HekiBot instance;
	private HekiBot() {
		//this.setName("hekibot");
		loyaltyTracker = LoyaltyTracker.getInstance();
	};
	
	static {
		instance = new HekiBot();	////// initialize singleton instance with default of 'false'
	}
	
	public static HekiBot getInstance() {
		return instance;
	}
	
	////// Database file
	
	
	////// Queue object
	private Queue q;
	
	////// Loyalty Tracker
	private LoyaltyTracker loyaltyTracker;
	
	///// Mods and people
	private String hekibot = "hekibot";
	private String heki = "hekimae";
	private String lime = "limeman2";
	
	//private String hekibotName = "hekibot";
	
	
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
	
	////////// hekiCoin vars ///////////
	private String hcRemove = "!hekicoins remove ";
	
	private HashMap<Integer, String> hcResponses = new HashMap<Integer, String>();
	
	private String hcSongCmd = "!hcsong";
	private int hcSongCost = 10;
	private String hcSongResponse = " has just bought a song request for 10 hekicoins!";
	
	private String hcNameCmd = "!hcname";
	private int hcNameCost = 50;
	private String hcNameResponse = " gets their name on the screen for 50 hekicoins!";
	
	private String hcMapCmd = "!hcmap";
	private int hcMapCost = 100;
	private String hcMapResponse = " has just picked the next map for 100 hekicoins!";

	private String hcSignCmd = "!hcsign";
	private int hcSignCost = 150;
	private String hcSignResponse = " has just bought a steam profile sign for 150 hekicoins!";
	
	private String hcSkipCmd = "!hcskip";
	private int hcSkipCost = 300;
	private String hcSkipResponse = " has just skipped the queue for 300 hekicoins!";
	
	private String hc1v1Cmd = "!hc1v1";
	private int hc1v1Cost = 500;
	private String hc1v1Response = " has just challenged heki to a 1v1 for 500 hekicoins!";
	
	private String hcPostCmd = "!hcpost";
	private int hcPostCost = 1000;
	private String hcPostResponse = " has just bought a personalized postcard for 1000 hekicoins!";
	
	private String hcSteamCmd = "!hcsteam";
	private int hcSteamCost = 1500;
	private String hcSteamResponse = " has just bought a steam add for 1500 hekicoins!";
	
	private String hcSerenadeCmd = "!hcserenade";
	private int hcSerenadeCost = 2500;
	private String hcSerenadeResponse = " has just bought a personal serenade from heki for 2500 hekicoins!";
	
	private String hc12Cmd = "!hc12";
	private int hc12Cost = 5000;
	private String hc12Response = " has just bought a 12 HOUR STREAM for 5000 hekicoins! PogChamp ";
	
	private String[] hcCommands = { hcSongCmd, hcNameCmd, hcMapCmd, hcSignCmd, hcSkipCmd, hc1v1Cmd, hcPostCmd, hcSteamCmd, hcSerenadeCmd, hc12Cmd };
	private int[] hcCosts = { hcSongCost, hcNameCost, hcMapCost, hcSignCost, hcSkipCost, hc1v1Cost, hcPostCost, hcSteamCost, hcSerenadeCost, hc12Cost };
	
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
	
	/*
	public HekiBot(boolean queueIsOpen) {
		this.setName("hekibot");
		reasonForClosedQueue = UNKNOWN_REASON;
		q = new Queue();
		this.queueIsOpen = queueIsOpen;
		
		
		
		loyaltyTracker = LoyaltyTracker.getInstance();
		
		hcResponses.put(-1, hcErrorResponse);
		hcResponses.put(hcSongCost, hcSongResponse);
		hcResponses.put(hcNameCost, hcNameResponse);
		hcResponses.put(hcMapCost, hcMapResponse);
		hcResponses.put(hcSignCost, hcSignResponse);
		hcResponses.put(hcSkipCost, hcSkipResponse);
		hcResponses.put(hc1v1Cost, hc1v1Response);
		hcResponses.put(hcPostCost, hcPostResponse);
		hcResponses.put(hcSteamCost, hcSteamResponse);
		hcResponses.put(hcSerenadeCost, hcSerenadeResponse);
		hcResponses.put(hc12Cost, hc12Response);
	} 
	*/
	
	protected void onMessage(String channel, String sender, String login,
			String hostname, String message) {

		
		
		////////// hekiCoin commands ///////////
		/*
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
			
			String costString = hcResponses.get(cost);
			
			if (costString != null) sendMessageAndPrint(channel, "/me " + buyer + costString);
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
	*/
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

	
	
	public void printCurrentGame() {
		if (currentGame == null) {
			System.out.println("There is no current game!");
		} else {
			this.currentGame.printPlayers();
		}
	}
	
}
