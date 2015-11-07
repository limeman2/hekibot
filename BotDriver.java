
public class BotDriver {
	public static String CHANNEL_NAME = "";
	
	public static void main(String[] args) {
		String oAuth = "";
		if (args.length == 1) {
			BotDriver.CHANNEL_NAME = "#hekimae";
			oAuth = args[0];
		} else {
			BotDriver.CHANNEL_NAME = args[0];
			oAuth = args[1];
		}
		System.out.println(System.getProperty("java.runtime.version"));
		String host = "irc.twitch.tv";
		int port = 6667;
		
		
		HekiBot bot = new HekiBot();
		bot.setVerbose(true);
		try {
			bot.connect(host, port, oAuth);
			bot.joinChannel(CHANNEL_NAME);
			bot.sendMessage(CHANNEL_NAME, "HeyGuys ");
		} 
		catch (Exception e) {
			System.out.println("Uhm, couldn't connect to twitch chat servers. For some reason.");
			System.out.println(e.getMessage());
		}
		
	}
}
