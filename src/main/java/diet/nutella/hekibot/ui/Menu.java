package main.java.diet.nutella.hekibot.ui;

import java.util.List;
import java.util.Scanner;

//import main.java.diet.nutella.hekibot.controller.HekiBotUI;

public class Menu {
	private String headLine;
	private List<MenuEntry> menuEntries;
	private Scanner inputReader;
	
	public Menu(String headLine, Scanner inputReader) {
		this.headLine = headLine;
		this.inputReader = inputReader;
		this.menuEntries = null;
	}
	
	public Menu(String headLine, List<MenuEntry> menuEntries, Scanner inputReader) {
		this.headLine = headLine;
		this.menuEntries = menuEntries;
		this.inputReader = inputReader;
	}
	
	public void removeEntry(int i) {
		this.menuEntries.remove(i);
	}
	
	public void removeEntry(Object o) {
		this.menuEntries.remove(o);
	}
	
	public void addEntry(MenuEntry entry) {
		this.menuEntries.add(entry);
	}
	
	public void addEntry(MenuEntry entry, int index) {
		this.menuEntries.add(index, entry);
	}
	
	public void printMenu() {
		System.out.println(headLine);		//// print menu headline
		
		//// Print an "underscore" of equal length as menu headline
		for(int i = 0; i < headLine.length(); i++) {
			System.out.print("-");
		}
		System.out.println();
		
		//// Print all menu entries
		for(MenuEntry entry : menuEntries) {
			System.out.print("[" + entry.getKey() + "] ");
			System.out.println(entry.getValue());
		}
	}
	
	public boolean contains(MenuEntry entry) {
		return menuEntries.contains(entry);
	}
	
	public void handleInput() {
		if (menuEntries != null) {
			String input = null;
			MenuEntry res = null;
			do {
				input = inputReader.nextLine();
				for (MenuEntry entry : menuEntries) {
					if (entry.getKey().equals(input)) {
						res = entry;
					}
				}
				if (res == null) {
					System.out.println("Invalid input, try again.");
				}
			} while (res == null);

			res.executeAction();
		} else {
			throw new InputOnEmptyMenuException();
		}	
	}
}
