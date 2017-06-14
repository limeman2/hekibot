package main.java.diet.nutella.hekibot.loyaltytracker;

import java.util.Timer;

import main.java.diet.nutella.hekibot.model.UserDAO;

public class LoyaltyTracker {
	
	///////////// Static, final variables /////////
	
	public static final int DEF_PAYOUT_INTERVAL = 5;
	
	///////////// Singleton pattern ///////////////
	
	private static LoyaltyTracker instance;
	private LoyaltyTracker(boolean trackingLoyalty) {
		////// Initialize instance variables ///////
		this.onlineTimer = new Timer();
		this.onlineChecker = new OnlineChecker();
		this.trackingLoyalty = trackingLoyalty;
		this.payoutInterval = DEF_PAYOUT_INTERVAL;
		this.dao = new UserDAO();
		this.loyaltyUpdater = new LoyaltyUpdater(dao);
		
		onlineTimer.scheduleAtFixedRate(onlineChecker, 0, 30 * 1000);  //// Start checking if stream is online
	};
	
	static {
		instance = new LoyaltyTracker(false);	////// initialize singleton instance with default of 'false'
	}
	
	public static LoyaltyTracker getInstance() {
		return instance;
	}
	
	///////////// Fields //////////////////////
	
	private Timer onlineTimer;				///// Timer object which checks whether stream is online
	private Timer loyaltyTimer;				///// Timer object which tracks loyalty
	private OnlineChecker onlineChecker;	///// Check if stream is online
	private boolean trackingLoyalty;		///// Whether loyalty should be tracked or not
	private int payoutInterval;				///// Time interval (in minutes) for coin payouts
	private LoyaltyUpdater loyaltyUpdater;
	private UserDAO dao;
	
	///////////// Public methods //////////////
	
	public void trackLoyalty(boolean track) {
		if (track && !trackingLoyalty) {
			loyaltyTimer = new Timer();
			loyaltyTimer.scheduleAtFixedRate(loyaltyUpdater, 0, 1000 * 60);
			trackingLoyalty = true;
		} else if (!track && trackingLoyalty) {
			loyaltyTimer.cancel();
			trackingLoyalty = false;
		}
	}
	
	public boolean isTracking() {
		return trackingLoyalty;
	}
}
