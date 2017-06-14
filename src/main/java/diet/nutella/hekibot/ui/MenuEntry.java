package main.java.diet.nutella.hekibot.ui;

public class MenuEntry {
	private String key;
	private String value;
	private Callable action;
	
	public MenuEntry(String key, String value, Callable action) {
		this.key = key;
		this.value = value;
		this.action = action;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void executeAction() {
		this.action.call();
	}

	public boolean equals(Object obj) {
		return this.value == obj.toString();
	}

	@Override
	public String toString() {
		return this.value;
	}
	
	
	
}

