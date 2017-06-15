package main.java.diet.nutella.hekibot.controller;

import java.io.IOException;

import org.pircbotx.PircBotX;
import org.pircbotx.exception.IrcException;
import org.pircbotx.output.OutputIRC;

import main.java.diet.nutella.hekibot.HekiBot;
import main.java.diet.nutella.hekibot.ui.Callable;

public class TwitchConnector implements Callable {
	private static final int DEFAULT_TIMEOUT = 5000 / 200;
	private boolean connect;
	private PircBotX bot;
	
	public TwitchConnector(PircBotX bot, boolean connect) {
		this.bot = bot;
		this.connect = connect;
	}
	
	@Override
	public void call() {
		
		////////// check if we want to connect or disconnect
		if (!connect) {
			if(bot.isConnected()) {
				Thread t = new Thread() {
					@Override
					public void run() {
						OutputIRC irc = new OutputIRC(bot);
						irc.quitServer();
						while (bot.isConnected()) {
							try {
								Thread.sleep(200);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				};
				
				t.run();
				
				try {
					t.join(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {	
				Thread t = new Thread() {
					@Override
					public void run() {
						try {
							bot.startBot();
						} catch (IOException | IrcException e) {
							e.printStackTrace();
						}
					}
				};
				
				t.start();
				
				int i = 0;
				while (!bot.isConnected() && i < DEFAULT_TIMEOUT) {
					try {
						Thread.sleep(200);
						i++;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} 
		}
	}
}
