package main.java.diet.nutella.hekibot;

import java.util.ArrayList;

public class Queue {
	private ArrayList<QueueEntry> entries;
	
	public Queue () {
		entries = new ArrayList<QueueEntry>();
	}
	
	public String[] pickFromQueue (int amount) {
		String[] result = new String[amount];
		int resultIndex = 0;
		int c = 0;
		for (int i = 0; c != amount && (i < entries.size()) ; ) {
			QueueEntry e = entries.get(i);
			
			if (c + e.size() <= amount) {
				c += e.size();
				
				for (int k = 0; k < e.size(); k++) {
					result[resultIndex] = e.getNames().get(k);
					resultIndex++;
				}
				entries.remove(i);	
				
				if (c == amount)
					break;
			} else {
				i++;
			}
		}
			
		return result;
	}
	
	public String addEntry(String[] names) {
		if (names.length > 3) {
			return "Sorry, you can not bring more than 2 friends, " + names[0] + " FailFish";
		}
		
		for (QueueEntry e : entries) {
			String s = e.search(names);
			if (s != null) {
				return s + " is already in the queue!";
			}
		}
		
		QueueEntry qe = new QueueEntry(names);
		entries.add(qe);
		
		return qe + " has been added to the queue at number " + queuePosition(names[0]) + "!";		
	}
	
	public int queuePosition(String name) {
		int c = 1;
		for (QueueEntry e : entries) {
			for (String n : e.getNames()) {
				if (name.equals(n)) 
					return c;
				c++;
			}
		}
		return 0;
	}
	
	public String search(String[] names) {
		for (QueueEntry e : entries) {
			String s = e.search(names);
			if (s != null) {
				return s;
			}
		}
		return null;
	}
	
	public String queueList() {
		StringBuilder sb = new StringBuilder();
		if (entries.isEmpty())
			return "The queue is empty.";
		
		for (int i = 0; i < entries.size(); i++) {
			sb.append(entries.get(i));
			if (i != entries.size() - 1) sb.append(",");
			sb.append(" ");
		}
		
		return sb.toString();
	} 
	
	public String indexedList() {
		int i = 0;
		StringBuilder sb = new StringBuilder();
		for (QueueEntry qe : entries) {
			for (String s : qe.getNames()) {
				sb.append(i).append(": ").append(s).append("\n");
				i++;
			}
		}
		return sb.toString();
	}
	
	public String removeFromQueue(String sender, String playerToRemove) {
		boolean playerFound = false;
		int i = 0;
		for (QueueEntry e : entries) {
			for (String n : e.getNames()) {
				if (n.equals(sender)) {
					playerFound = true;
					for (String n1 : e.getNames()) {
						if (n1.equals(playerToRemove)) {
							e.removePlayer(playerToRemove);
							if (e.size() == 0) entries.remove(i);
							return playerToRemove + " has been removed from the queue! ";
						}
					}
				}
			}
			i++;
		}
		
		if (playerFound) {
			return "You can't remove that player, " + sender + "! ";
		} else {
			return "You are not even in the queue, " + sender + "! ";
		}
		
	}
	
	public String leaveQueue(String sender) {
		int i = 0;
		for (QueueEntry e : entries) {
			for (String n : e.getNames()) {
				if (n.equals(sender)) {
					if (e.size() == 1) {
						return "You have left the queue, " + entries.remove(i).getNames().get(0) + ".";
					} else {
						return "You have left the queue, " + e.removePlayer(sender) + ".";
					}	
				}
			}
			i++;
		}
		return "You are not even in the queue, " + sender + "!";
	}
	
	public String clearQueue() {
		entries.clear();
		return "The queue has been cleared.";
	}
}
