
public class QueueEntry {
	private int space;
	private String name;
	
	public QueueEntry (String name, int space) {
		this.name = name;
		this.space = space;
	}
	
	public String getName() {
		return name;
	}
	
	public int getSpace() {
		return space;
	}
}
