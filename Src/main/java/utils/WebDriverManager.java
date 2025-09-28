package utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Properties;

/**
 * WebDriver utility class for managing browser drivers and configurations
 */
public class WebDriverManager {
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final Properties config = ConfigReader.getProperties();
    
    /**
     * Initialize WebDriver based on browser configuration
     * @param browserName Browser name (chrome, firefox, edge)
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver(String browserName) {
        if (driver == null) {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--disable-notifications");
                    chromeOptions.addArguments("--disable-popup-blocking");
                    chromeOptions.addArguments("--disable-dev-shm-usage");
                    chromeOptions.addArguments("--no-sandbox");
                    
                    // Add headless mode if configured
                    if (Boolean.parseBoolean(config.getProperty("headless", "false"))) {
                        chromeOptions.addArguments("--headless");
                    }
                    
                    driver = new ChromeDriver(chromeOptions);
                    break;
                    
                case "firefox":
                    io.github.bonigarcia.wdm.WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver();
                    break;
                    
                case "edge":
                    io.github.bonigarcia.wdm.WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                    
                default:
                    throw new IllegalArgumentException("Browser not supported: " + browserName);
            }
            
            // Configure driver settings
            driver.manage().window().maximize();
            driver.manage().deleteAllCookies();
            
            int implicitWait = Integer.parseInt(config.getProperty("implicit.wait", "10"));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));
            
            // Initialize WebDriverWait
            int explicitWait = Integer.parseInt(config.getProperty("explicit.wait", "20"));
            wait = new WebDriverWait(driver, Duration.ofSeconds(explicitWait));
        }
        
        return driver;
    }
    
    /**
     * Get current WebDriver instance
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return driver;
    }
    
    /**
     * Get WebDriverWait instance
     * @return WebDriverWait instance
     */
    public static WebDriverWait getWait() {
        return wait;
    }
    
    /**
     * Quit the WebDriver and clean up resources
     */
    public static void quitDriver() {
        if (driver != null) {
            driver.quit();
            driver = null;
            wait = null;
        }
    }
    
    /**
     * Navigate to URL
     * @param url URL to navigate to
     */
    public static void navigateToUrl(String url) {
        if (driver != null) {
            driver.get(url);
        }
    }
    
    /**
     * Get page title
     * @return Current page title
     */
    public static String getPageTitle() {
        return driver != null ? driver.getTitle() : "";
    }
    
    /**
     * Get current URL
     * @return Current URL
     */
    public static String getCurrentUrl() {
        return driver != null ? driver.getCurrentUrl() : "";
    }
}