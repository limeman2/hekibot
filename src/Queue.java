import java.util.ArrayList;

public class Queue {
	private ArrayList<QueueEntry> entries;
	
	public Queue () {
		entries = new ArrayList<QueueEntry>();
	}
	
	public String nextQueue(int entryAmount) {
		StringBuilder sb = new StringBuilder();
		int c = 0;
		for (int i = 0; c != entryAmount && (i < entries.size()) ; ) {
			QueueEntry e = entries.get(i);
			if (c + e.getSpace() <= entryAmount) {
				c += e.getSpace();
				StringBuilder sbPlus = new StringBuilder();
				if (e.getSpace() != 1) {
					sbPlus.append(" +").append(e.getSpace() - 1);
				}
				
				if (c == entryAmount && e.getSpace() != entryAmount) {
					sb.append(" and ").append(entries.remove(i).getName()).append(sbPlus);
					break;
				}
				else if (sb.toString().equals(""))
					sb.append(entries.remove(i).getName()).append(sbPlus);
				else
					sb.append(", ").append(entries.remove(i).getName()).append(sbPlus);
				if (c == entryAmount)
					break;
			}
			else
				i++;
		}
		
		sb.append(" has been selected from the queue!");
		
		if (c != entryAmount)
			sb.append(" I could not pick " + entryAmount + " people from the queue.");
		
		return sb.toString();
	}
	
	public String addEntry(String name, int space) {
		space++;
		if (space > 3)
			return "Sorry, you can not bring more than 2 friends, " + name + " FailFish";
		
		if (space < 1)
			return "Don't be silly, " + name + ".";
		
		for (QueueEntry e : entries) {
			if (e.getName().equals(name)) {
				return "You are already in the queue, " + name + ". You are currently number " + queuePosition(name) + " in the queue.";
			}
		}
		entries.add(new QueueEntry(name, space));
		return "You have been added to the queue, " + name + ". You are currently number " + this.queuePosition(name) + " in the queue.";
	}
	
	public int queuePosition(String name) {
		int c = 1;
		for (QueueEntry e : entries) {
			if (name.equals(e.getName())) 
				return c;
			
			c += e.getSpace();
		}
		return 0;
	}
	
	public String queueList() {
		StringBuilder sb = new StringBuilder();
		if (entries.isEmpty())
			return "The queue is empty.";
		
		for (QueueEntry e : entries) {
			if (e.getSpace() > 1) {
				sb.append(e.getName()).append(" +").append(e.getSpace() - 1);
			} else {
				sb.append(e.getName());
			}
			
			if (!entries.get(entries.size() - 1).equals(e))
				sb.append(", ");
			
		}
		return sb.toString();
	}
	
	public String leaveQueue(String sender) {
		int i = 0;
		for (QueueEntry e : entries) {
			if (e.getName().equals(sender)) {
				return ("You have left the queue, " + entries.remove(i).getName() + ".");
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
