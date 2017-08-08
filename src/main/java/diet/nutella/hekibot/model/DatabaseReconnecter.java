package main.java.diet.nutella.hekibot.model;

import java.util.Date;
import java.util.TimerTask;

public class DatabaseReconnecter extends TimerTask {

	private UserDAO dao;
	
	public DatabaseReconnecter(UserDAO dao) {
		this.dao = dao;
	}
	
	public void run() {
		this.dao.connect();
		System.out.println("Reconnecting to DB at " + new Date());
	}

}
