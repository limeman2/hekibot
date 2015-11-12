import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.jibble.pircbot.PircBot;

public class HekiBot extends PircBot {
	
	////// Database file
	
	
	////// Queue object
	private Queue q;
	
	///// Mods and people
	private String heki = "hekimae";
	private String lime = "limeman2";
	private String hekibotName = "hekibot";
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
			"thebstrike"
	};
	
	////////// Command strings ///////////
	private String queueCmd = "!q";
	private String oldCmd = "!queue";
	private String qListCmd = "!qlist";
	private String qPosCmd = "!qpos";
	//private String nextQCmd = "!nextq";
	private String clearQCmd = "!clearq";
	private String leaveQCmd1 = "!leaveq";
	private String leaveQCmd2 = "!qleave";
	private String commandsCmd1 = "!qhelp";
	private String commandsCmd2 = "!commands";
	private String gNightCmd = "!quit";
	private String lastGameCmd = "!lastgame";
	private String modNightCmd = "!MODNIGHT";
	
	////// Misc vars 
	private boolean isLastGame = false;
	private boolean isModNight = false;
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private Calendar rightNow = Calendar.getInstance();
	
	public HekiBot() {
		this.setName("hekibot");
		q = new Queue();
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
		if (!sender.equals(hekibotName)) showChatMessage(sender, message);
		
		if (message.startsWith("!")) {				
			if ((sender.equals(heki) || sender.equals(lime))) {
				if (message.equalsIgnoreCase(clearQCmd))
					sendMessageAndPrint(channel, q.clearQueue());
				
				if (message.matches("!nextq \\d")) {
					sendMessageAndPrint(channel, q.nextQueue(Integer.parseInt(Character.toString(message.charAt(7)))));
				}
				
				if (message.equalsIgnoreCase(gNightCmd)) {
					sendMessageAndPrint(channel, "G'NIGHT ResidentSleeper");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						this.dispose();
					}
				} 
				
				if (message.equalsIgnoreCase(lastGameCmd)) {
					if (!isLastGame) {
						sendMessageAndPrint(channel, "This will be the last game, folks! Sorry if you're still in queue :(");
						isLastGame = true;
					} else {
						sendMessageAndPrint(channel, "Nevermind! We'll play some more.");
						isLastGame = false;
					}
				}
				
				if (message.equalsIgnoreCase(modNightCmd)) {
					if (!isModNight) {
						sendMessageAndPrint(channel, "IT'S MOD NIGHT, LADIES AND GENTLEMEN!");
						isModNight = true;
					} else {
						sendMessageAndPrint(channel, "MOD NIGHT IS NOW OVER, FOLKS!");
						isModNight = false;
					}
					
				}
			}
			
			if (message.equalsIgnoreCase(queueCmd)) {
				if (isModNight) {
					if (isOp(sender)) {
						if (!isLastGame) {
							sendMessageAndPrint(channel, q.addEntry(sender, 0));
						} else {
							sendMessageAndPrint(channel, "Sorry! This is the last game, " + sender);
						}
					} else {
						sendMessageAndPrint(channel, "Sorry, " + sender + ", tonight is MOD NIGHT!");
					}
				} else {
					if (!isLastGame) {
						sendMessageAndPrint(channel, q.addEntry(sender, 0));
					} else {
						sendMessageAndPrint(channel, "Sorry! This is the last game, " + sender);
					}
				}
			}
			
			if (message.matches("!q\\+\\d")) {
				if (isModNight) {
					if (isOp(sender)) {
						if (!isLastGame) {
							sendMessageAndPrint(channel, q.addEntry(sender, Integer.parseInt(Character.toString(message.charAt(3)))));
						} else {
							sendMessageAndPrint(channel, "Sorry! This is the last game, " + sender);
						}
					} else {
						sendMessageAndPrint(channel, "Sorry, " + sender + ", tonight is MOD NIGHT!");
					}
				} else {
					if (!isLastGame) {
						sendMessageAndPrint(channel, q.addEntry(sender, Integer.parseInt(Character.toString(message.charAt(3)))));
					} else {
						sendMessageAndPrint(channel, "Sorry! This is the last game, " + sender);
					}
				}		
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
				sendMessageAndPrint(channel, "Available commands: !q, !q+1, !q+2, !qlist (mods only), !qpos, !leaveq");
			}
		}
	} 
	
	private void showChatMessage(String sender, String message) {
		System.out.println(timeFormat.format(rightNow.getTime()) + " | " + sender + ": " + message);
	}
	
	private void sendMessageAndPrint(String channel, String message) {
		showChatMessage("hekibot", message);
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