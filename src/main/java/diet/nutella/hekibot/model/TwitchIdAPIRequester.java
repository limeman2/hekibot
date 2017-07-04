package main.java.diet.nutella.hekibot.model;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import main.java.diet.nutella.hekibot.controller.BotDriver;

public class TwitchIdAPIRequester implements Callable<SimpleTwitchUser[]> {
	private List<String> userNames;
	private List<SimpleTwitchUser> result;
	
	private static final String USER_LOOKUP_URL = 
			"https://api.twitch.tv/kraken/users?login=";
	
	public TwitchIdAPIRequester(List<String> userNames) {
		this.userNames = userNames;
		this.result = new ArrayList<SimpleTwitchUser>();
	}
	
	public TwitchIdAPIRequester(String userName) {
		this.userNames = new ArrayList<String>();
		this.userNames.add(userName);
		this.result = new ArrayList<SimpleTwitchUser>();
	}

	public SimpleTwitchUser[] call() {
		StringBuilder sb = new StringBuilder(USER_LOOKUP_URL);
		for (int i = 0; i < 100; i++) {
			if (userNames.size() == 1) {
				if (i != 0) sb.append(",");
				sb.append(userNames.remove(0));
				break;
			} else {
				if (i != 0) sb.append(",");
				sb.append(userNames.remove(0));
			}	
		}
		
		JSONParser jp = new JSONParser();
		
		URL url;
		try {
			url = new URL(sb.toString());
			HttpURLConnection request = (HttpURLConnection) url.openConnection();
			request.setRequestMethod("GET");
			request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
			request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
			request.connect();
			
			InputStream is = (InputStream) request.getContent();
			InputStreamReader isr = new InputStreamReader(is);
			JSONObject obj = ((JSONObject) jp.parse(isr));
			
			JSONArray users = (JSONArray) (obj.get("users"));
			
			//// Loop through the resulting list of users
			for (Object user : users) {
				int id = Integer.parseInt((String) ((JSONObject) user).get("_id"));
				String name = (String) ((JSONObject) user).get("name");
				result.add(new SimpleTwitchUser(id, name));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SimpleTwitchUser[] convert = new SimpleTwitchUser[result.size()];
		for(int i = 0; i < convert.length; i++) {
			convert[i] = result.get(i);
		}
		
		return convert;
	}
}
