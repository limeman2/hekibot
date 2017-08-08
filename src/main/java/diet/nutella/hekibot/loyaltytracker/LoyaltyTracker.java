package main.java.diet.nutella.hekibot.loyaltytracker;

import java.util.Timer;

import main.java.diet.nutella.hekibot.controller.OnlineChecker;
import main.java.diet.nutella.hekibot.model.SimpleTwitchUser;
import main.java.diet.nutella.hekibot.model.UserDAO;

public class LoyaltyTracker {
	
	///////////// Static, final variables /////////
	
	public static final int DEF_PAYOUT_INTERVAL = 1000 * 60;
	
	///////////// Singleton pattern ///////////////
	
	private static LoyaltyTracker instance;
	private LoyaltyTracker(boolean trackingLoyalty) {
		
		////// Initialize instance variables ///////
		
		
		this.trackingLoyalty = trackingLoyalty;
		this.payoutInterval = DEF_PAYOUT_INTERVAL;
		this.dao = new UserDAO();
		
		this.loyaltyUpdater = new LoyaltyUpdater(dao);
	};
	
	static {
		instance = new LoyaltyTracker(false);	////// initialize singleton instance with default of 'false'
	}
	
	public static LoyaltyTracker getInstance() {
		return instance;
	}
	
	///////////// Fields //////////////////////
	
	private Timer loyaltyTimer;				///// Timer object which tracks loyalty
	private boolean trackingLoyalty;		///// Whether loyalty should be tracked or not
	private boolean streamOnline;			///// Whether stream is online
	private int payoutInterval;				///// Time interval (in minutes) for coin payouts
	private LoyaltyUpdater loyaltyUpdater;
	private UserDAO dao;
	private CurrentUsersTracker currentUsers;
	
	
	///////////// Public methods //////////////
	
	public void trackLoyalty(boolean track) {
		if (track && !this.trackingLoyalty) {
			this.loyaltyUpdater = new LoyaltyUpdater(dao);
			this.loyaltyTimer = new Timer();
			this.loyaltyTimer.scheduleAtFixedRate(loyaltyUpdater, 0, DEF_PAYOUT_INTERVAL);
			this.trackingLoyalty = true;
		} else if (!track && this.trackingLoyalty) {
			this.loyaltyUpdater.cancel();
			this.loyaltyTimer.cancel();
			this.trackingLoyalty = false;
		}
	}
	
	public void printUsers() {
		for (SimpleTwitchUser user : currentUsers.getUsers()) {
			System.out.println(user);
		}
		System.out.println();
	}
	
	public boolean isTracking() {
		return trackingLoyalty;
	}

	public boolean isStreamOnline() {
		return streamOnline;
	}

	public void setStreamOnline(boolean streamOnline) {
		this.streamOnline = streamOnline;
	}
	
	public UserDAO getDAO() {
		return this.dao;
	}
}
