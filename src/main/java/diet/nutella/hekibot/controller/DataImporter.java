package main.java.diet.nutella.hekibot.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import main.java.diet.nutella.hekibot.model.UserDAO;
import main.java.diet.nutella.hekibot.model.UserInDB;
import main.java.diet.nutella.hekibot.ui.Callable;

public class DataImporter implements Callable {
	
	private UserDAO dao;
	
	public DataImporter() {
		this.dao = new UserDAO();
	}
	
	public void call() {
		Scanner scan;
		try {
			scan = new Scanner(new File("hekimae_points.csv"));
			scan.nextLine();
			
			String line = new String();
			List<UserInDB> list = new ArrayList<UserInDB>();
			
			int id;
			String name;
			int coins;
			int time;
			
			while(scan.hasNext()) {
				line = scan.nextLine();
				Scanner lineReader = new Scanner(line);
				lineReader.useDelimiter(",");
				
				name = lineReader.next();
				id = lineReader.nextInt();
				coins = lineReader.nextInt();
				time = lineReader.nextInt();
				lineReader.close();
				
				list.add(new UserInDB(id, name, coins, time));
			}
			
			int i = 0;
			System.out.println("Updating...");
			for(UserInDB user : list) {
				
				if(i % 500 == 0) {
					System.out.println(i + " / " + list.size()); 
				}
				dao.updateUser(user);
				i++;
			}
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
