package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to read configuration properties
 */
public class ConfigReader {
    
    private static Properties properties;
    
    static {
        loadProperties();
    }
    
    /**
     * Load properties from config.properties file
     */
    private static void loadProperties() {
        properties = new Properties();
        try {
            // Try to load from classpath first
            InputStream inputStream = ConfigReader.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            
            if (inputStream != null) {
                properties.load(inputStream);
                inputStream.close();
            } else {
                // Fallback to file system
                FileInputStream fileInputStream = new FileInputStream(
                    "src/main/resources/config.properties");
                properties.load(fileInputStream);
                fileInputStream.close();
            }
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
            // Set default properties if file not found
            setDefaultProperties();
        }
    }
    
    /**
     * Set default properties if config file is not found
     */
    private static void setDefaultProperties() {
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "false");
        properties.setProperty("implicit.wait", "10");
        properties.setProperty("explicit.wait", "20");
        properties.setProperty("base.url", "https://www.flipkart.com/");
        properties.setProperty("search.product", "laptop");
        properties.setProperty("short.wait", "5");
        properties.setProperty("medium.wait", "10");
        properties.setProperty("long.wait", "30");
    }
    
    /**
     * Get all properties
     * @return Properties object
     */
    public static Properties getProperties() {
        return properties;
    }
    
    /**
     * Get property value by key
     * @param key Property key
     * @return Property value
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    /**
     * Get property value by key with default value
     * @param key Property key
     * @param defaultValue Default value if key not found
     * @return Property value or default value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Get browser name from config
     * @return Browser name
     */
    public static String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    /**
     * Get base URL from config
     * @return Base URL
     */
    public static String getBaseUrl() {
        return getProperty("base.url", "https://www.flipkart.com/");
    }
    
    /**
     * Get search product from config
     * @return Search product
     */
    public static String getSearchProduct() {
        return getProperty("search.product", "laptop");
    }
    
    /**
     * Get test email from config
     * @return Test email
     */
    public static String getTestEmail() {
        return getProperty("test.email", "your_email@test.com");
    }
    
    /**
     * Get test password from config
     * @return Test password
     */
    public static String getTestPassword() {
        return getProperty("test.password", "your_password");
    }
}