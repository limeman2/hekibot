package main.java.diet.nutella.hekibot.loyaltytracker;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import main.java.diet.nutella.hekibot.model.SimpleTwitchUser;
import main.java.diet.nutella.hekibot.model.TwitchAPIClient;
import main.java.diet.nutella.hekibot.model.UserDAO;

public class CurrentUsersTracker {
	private UserDAO dao;
	private TwitchAPIClient twitchAPIClient;
	private List<SimpleTwitchUser> users;

	public CurrentUsersTracker(UserDAO dao) {
		this.dao = dao;
		this.users = new ArrayList<SimpleTwitchUser>();
		this.twitchAPIClient = new TwitchAPIClient();
		SimpleTwitchUser[] array;
		try {
			array = this.twitchAPIClient.getUsersInChannel();
			if (array != null) {
				this.users.addAll(Arrays.asList(twitchAPIClient.getUsersInChannel()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public SimpleTwitchUser[] getUsers() {
		SimpleTwitchUser[] array = new SimpleTwitchUser[users.size()];
		for(int i = 0; i < array.length; i++) {
			array[i] = users.get(i);
		}
		return array;
	}

	public void update() {
		SimpleTwitchUser[] array = null;
		try {
			array = twitchAPIClient.getUsersInChannel();
		} catch (SocketTimeoutException ste) {
			System.out.println("Connection to Twitch API to get users in channel has timed out. API down?");
		} catch (IOException ioex) {
			ioex.printStackTrace();
			users.clear();
		}
		users.clear();
		if (array != null) {
			this.users.addAll(Arrays.asList(array));
		}
	}
}
