import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class QueueEntry {
	private int numberOfPlayers;
	private List<String> names;
	
	public QueueEntry (String[] names) {
		this.numberOfPlayers = names.length;
		this.names = new ArrayList<String>(Arrays.asList(names));
	}
	
	public List<String> getNames() {
		return names;
	}
	
	public int size() {
		return numberOfPlayers;
	}
	
	public String removePlayer(String player) {
		if (names.remove(player)) {
			numberOfPlayers = names.size();
			return player;
		} 
		return "";
	}
	
	public String search(String[] players) {
		String contains = null;
		for (String n1 : this.names) {
			for (String n2 : players) {
				if (n1.equals(n2)) {
					contains = n2;
				}
			}
		}
		return contains;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < this.names.size(); i++) {
			sb.append(this.names.get(i));
			if (i != names.size() - 1) sb.append(" + ");	
		}
		return sb.toString();
	}
}
