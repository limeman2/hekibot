package main.java.diet.nutella.hekibot;

import java.io.*;
import java.util.Properties;

public class GetProperties {
	private Properties props = new Properties();
	
	public GetProperties() {
		InputStream inputStream = null;
		try {
			File jarPath = new File(GetProperties.class.getProtectionDomain().getCodeSource().getLocation().getPath());
			String propertiesPath = jarPath.getParentFile().getAbsolutePath() + "/config.properties";

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
	};

	
	public Properties getProperties() {
		return this.props;
	}
}
