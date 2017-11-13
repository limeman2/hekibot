package main.java.diet.nutella.hekibot.model;

import main.java.diet.nutella.hekibot.controller.BotDriver;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class TwitchAPIClient {

    private static final String USER_REQUEST_URL = String.format(
            "https://tmi.twitch.tv/group/user/%s/chatters",
            BotDriver.CHANNEL_NAME.substring(1));

    public SimpleTwitchUser[] getUsersInChannel() throws IOException {
		JSONParser jp = new JSONParser();

		/////// Get usernames in channel

		URL url = new URL(USER_REQUEST_URL);
		HttpURLConnection request = (HttpURLConnection) url.openConnection();
		request.setRequestMethod("GET");
		request.setRequestProperty("Accept", "application/vnd.twitchtv.v5+json");
		request.setRequestProperty("Client-ID", BotDriver.props.getProperty("twitch-api-client-id"));
		request.connect();

		///////// Parse JSON data into different user types

		JSONObject obj;
		try {
			obj = (JSONObject) jp.parse(new InputStreamReader((InputStream) request.getContent()));
			JSONArray staff = (JSONArray) ((JSONObject)obj.get("chatters")).get("staff");
			JSONArray viewers = (JSONArray) ((JSONObject)obj.get("chatters")).get("viewers");
			JSONArray globalMods = (JSONArray) ((JSONObject)obj.get("chatters")).get("global_mods");
			JSONArray mods = (JSONArray) ((JSONObject)obj.get("chatters")).get("moderators");
			JSONArray admins = (JSONArray) ((JSONObject)obj.get("chatters")).get("admins");

			///////// Add every user type to one big list of usernames

			List<String> userNamesInChannel = new ArrayList<String>();

			userNamesInChannel.addAll(0, staff);
			userNamesInChannel.addAll(0, viewers);
			userNamesInChannel.addAll(0, globalMods);
			userNamesInChannel.addAll(0, mods);
			userNamesInChannel.addAll(0, admins);

			//////// API call to look up IDs of every user in chat
			if (userNamesInChannel.size() == 0) return null;
			return lookupTwitchId(userNamesInChannel).toArray(new SimpleTwitchUser[0]);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

    private List<SimpleTwitchUser> lookupTwitchId(List<String> userNames) {
		if(userNames.size() == 0) return null;

		/// Initialize array to return
		List<SimpleTwitchUser> result = new ArrayList<SimpleTwitchUser>();

		/// Calculate how many API calls to make
		/// Put them in batches of 100
		int numberOfApiCalls = 1 + (userNames.size() / 100);

		/// Set up executor service and list of pending results
		ExecutorService executor = Executors.newFixedThreadPool(50);
		List<Future<SimpleTwitchUser[]>> pendingResults =
				new ArrayList<Future<SimpleTwitchUser[]>>();

		/// Tell the executor to make API calls
		while (numberOfApiCalls > 0) {
			/// Prepare one batch of up to 100 users to request
			List<String> batch = new ArrayList<String>();
			for(int i = 0; i < 100 && userNames.size() > 0; i++) {
				batch.add(userNames.remove(0));
			}

			/// Instantiate requester and give it one batch
			TwitchIdAPIRequester requester = new TwitchIdAPIRequester(batch);
			/// Add Future object to list of pending results
			pendingResults.add(executor.submit(requester));

			numberOfApiCalls--;
		}

		/// Loop through list of pending results to get the data
		for (Future<SimpleTwitchUser[]> pendingResult : pendingResults) {
			try {
				/// Get one batch from the future object
				/// Timeout if any single batch takes > 2 seconds
				SimpleTwitchUser[] batch =
						(SimpleTwitchUser[]) pendingResult.get(2, TimeUnit.SECONDS);

				if (batch != null) {
					for(SimpleTwitchUser usr : batch) {
						result.add(usr);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		executor.shutdownNow();

		return result;
	}
}
