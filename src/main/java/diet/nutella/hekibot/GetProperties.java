package main.java.diet.nutella.hekibot;

import main.java.diet.nutella.hekibot.controller.RedeemCommand;

import java.io.*;
import java.util.*;

public class GetProperties {
	public static Properties getConfigProperties() {
		Properties result = new Properties();
		InputStream inputStream = null;

		try {
			File jarPath = new File(GetProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "/config.properties";

			inputStream = new FileInputStream(propertiesPath);

			if (inputStream != null) {
				result.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propertiesPath + "' not found in the classpath ");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static Map<String, RedeemCommand> getRedeemCommands() {
	    Map<String, RedeemCommand> res = new HashMap<>();
		Properties props = new Properties();
		InputStream inputStream = null;

		try {
		    File jarPath = new File(GetProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "/redeem_commands.properties";

			inputStream = new FileInputStream(propertiesPath);

			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propertiesPath + "' not found in the classpath ");
			}
		} catch (Exception e) {
			System.out.println("Exception: " + e);
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Scanner s;

		for (Map.Entry<Object, Object> e : props.entrySet()) {
		    s = new Scanner((String) e.getValue());
		    s.useDelimiter(",");
		    String cmd = s.next();
		    int cost = s.nextInt();
		    String response = s.next();

		    res.put(cmd, new RedeemCommand(cmd, cost, response, null));
        }

        return res;
    }
}
