import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class GetProperties {
	private Properties props = new Properties();
	
	///////////// Singleton pattern
	private static GetProperties instance;
	public GetProperties() {
		InputStream inputStream = null;
		try {
			String propFileName = "config.properties";
 
			inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
 
			if (inputStream != null) {
				props.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
			}
 
			//String channelName = props.getProperty("channel-name");
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
