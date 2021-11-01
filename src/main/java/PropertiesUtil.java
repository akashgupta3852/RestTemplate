//
//
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Properties;
//
//import com.beanit.jasn1.ber.types.BerInteger;
//
//public class PropertiesUtil {
//	private static String configPropertiesdirectoryLocation = "src/main/resources/config.properties";
//	private static Properties prop;
//
//	// Get server's max size from configuration-properties file
//	public static BerInteger getServerMaxMsgSize() throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		return new BerInteger(Long.parseLong(prop.getProperty("max-message-size")));
//	}
//
//	// Get server's init max-retry-limit from properties file
//	public static int getInitMaxRetry() throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		return Integer.parseInt(prop.getProperty("init-max-retry"));
//	}
//
//	// Get number of unsuccessful tries of init from properties file
//	public static int getInitFailureCount() throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		return Integer.parseInt(prop.getProperty("init-fails-count"));
//	}
//
//	// Get number of unsuccessful tries of init from properties file
//	public static void setInitFailureCount(int count) throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		prop.setProperty("init-fails-count", String.valueOf(count));
//		OutputStream output = new FileOutputStream(configPropertiesdirectoryLocation);
//		prop.store(output, "File updated");
//	}
//
//	// Get number of unsuccessful tries of init from properties file
//	public static void addInitFailureCount() throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		int count = getInitFailureCount() + 1;
//		prop.setProperty("init-fails-count", String.valueOf(count));
//		OutputStream output = new FileOutputStream(configPropertiesdirectoryLocation);
//		prop.store(output, "File updated");
//	}
//
//	// Get the initialize success response from the properties file
//	public static boolean getInitSuccessStatus() throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		return Boolean.parseBoolean(prop.getProperty("init-success-status"));
//	}
//
//	// Set the initialize success response from the properties file
//	public static void setInitSuccessStatus(boolean status) throws IOException {
//		loadProperties(configPropertiesdirectoryLocation);
//		String successStatus = "" + status;
//		prop.setProperty("init-success-status", successStatus);
//		OutputStream output = new FileOutputStream(configPropertiesdirectoryLocation);
//		prop.store(output, "File updated");
//	}
//
//	// Load the properties
//	private static void loadProperties(String dirLocation) throws IOException {
//		prop = new Properties();
//		InputStream input = new FileInputStream(dirLocation);
//		prop.load(input);
//	}
//}
