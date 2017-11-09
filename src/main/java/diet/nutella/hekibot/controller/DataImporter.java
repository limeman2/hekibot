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
			File jarPath = new File(DataImporter.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "/hekimae_points.csv";

			scan = new Scanner(new File(propertiesPath));
			scan.nextLine();

			String line;
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
				coins = lineReader.nextInt();
				time = lineReader.nextInt();
				id = lineReader.nextInt();

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
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}
