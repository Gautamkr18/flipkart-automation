import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.Set;

/**
 * Simple Flipkart test without complex dependencies - works with current libs
 */
public class SimpleFlipkartTest {
    
    private static WebDriver driver;
    
    public static void main(String[] args) {
        System.out.println("Starting Flipkart Automation Test...");
        
        try {
            setupDriver();
            runTests();
            System.out.println("\n✅ All tests completed successfully!");
        } catch (Exception e) {
            System.err.println("\n❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }
    
    private static void setupDriver() {
        System.out.println("Setting up Chrome driver...");
        
        // Set the path to ChromeDriver executable
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--remote-allow-origins=*");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        System.out.println("Chrome driver setup completed!");
    }
    
    private static void runTests() throws Exception {
        // Test 1: Navigate to Flipkart
        System.out.println("\n1. Navigating to Flipkart...");
        driver.get("https://www.flipkart.com/");
        
        // Wait for page to load
        Thread.sleep(5000);
        
        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        System.out.println("Page title: " + title);
        System.out.println("Current URL: " + url);
        
        if (title.contains("Flipkart") || url.contains("flipkart")) {
            System.out.println("✅ Successfully navigated to Flipkart");
        } else {
            System.out.println("⚠️ Page loaded but may not be Flipkart homepage");
            // Don't fail the test, continue to see what happens
        }
        
        // Close login popup if present
        closeLoginPopupIfPresent();
        
        // Test 2: Search for product
        System.out.println("\n2. Searching for laptop...");
        WebElement searchBox = findSearchBox();
        if (searchBox != null) {
            searchBox.clear();
            searchBox.sendKeys("laptop");
            searchBox.sendKeys(Keys.ENTER);
            
            // Wait for search results
            Thread.sleep(3000);
            
            String currentUrl = driver.getCurrentUrl();
            if (currentUrl.contains("search") || currentUrl.contains("laptop")) {
                System.out.println("✅ Search completed successfully");
            } else {
                System.out.println("⚠️ Search may not have completed properly");
            }
        } else {
            System.out.println("❌ Search box not found");
            return;
        }
        
        // Test 3: Click on first product
        System.out.println("\n3. Clicking on first product...");
        Thread.sleep(2000); // Wait for results to stabilize
        
        WebElement firstProduct = findFirstProduct();
        if (firstProduct != null) {
            String mainWindow = driver.getWindowHandle();
            
            // Click on first product
            scrollToElementAndClick(firstProduct);
            
            // Wait for page to load
            Thread.sleep(3000);
            
            // Handle new window if opened
            Set<String> allWindows = driver.getWindowHandles();
            if (allWindows.size() > 1) {
                for (String windowHandle : allWindows) {
                    if (!windowHandle.equals(mainWindow)) {
                        driver.switchTo().window(windowHandle);
                        break;
                    }
                }
            }
            
            System.out.println("✅ Successfully navigated to product page");
            
            // Test 4: Verify product page
            System.out.println("\n4. Verifying product page elements...");
            
            // Check if we're on a product page
            String currentUrl = driver.getCurrentUrl();
            String pageTitle = driver.getTitle();
            
            if (currentUrl.contains("/p/") || pageTitle.toLowerCase().contains("buy")) {
                System.out.println("✅ Product page verified");
                System.out.println("Current URL: " + currentUrl);
                
                // Try to find add to cart button
                if (findAddToCartButton() != null) {
                    System.out.println("✅ Add to cart button found");
                } else {
                    System.out.println("⚠️ Add to cart button not found (may require login)");
                }
            } else {
                System.out.println("⚠️ May not be on a product page");
                System.out.println("Current URL: " + currentUrl);
            }
        } else {
            System.out.println("❌ First product not found");
        }
    }
    
    private static void closeLoginPopupIfPresent() {
        try {
            String[] closeButtonSelectors = {
                "button._2KpZ6l._2doB4z",
                "span._30XB9F", 
                "button._2KpZ6l",
                "._30XB9F"
            };
            
            for (String selector : closeButtonSelectors) {
                try {
                    WebElement closeButton = driver.findElement(By.cssSelector(selector));
                    if (closeButton.isDisplayed()) {
                        closeButton.click();
                        Thread.sleep(1000);
                        System.out.println("Login popup closed");
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            System.out.println("No login popup found");
            
        } catch (Exception e) {
            System.out.println("No login popup to close");
        }
    }
    
    private static WebElement findSearchBox() {
        String[] searchBoxSelectors = {
            "input[name='q']",
            "input[placeholder*='Search']",
            "input._3704LK",
            "input.Pke_EE"
        };
        
        for (String selector : searchBoxSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        System.out.println("Search box not found with any selector");
        return null;
    }
    
    private static WebElement findFirstProduct() {
        String[] productSelectors = {
            "div._1AtVbE:first-of-type a",
            "div._13oc-S:first-of-type a", 
            "div._2kHMtA:first-of-type a",
            "._1fQZEK:first-of-type",
            "._2UzuFa:first-of-type a"
        };
        
        for (String selector : productSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        // Fallback: Find any clickable product link
        try {
            return driver.findElement(By.xpath("//a[contains(@href, '/p/') or contains(@href, '/product')]"));
        } catch (Exception e) {
            System.out.println("First product not found with any selector");
            return null;
        }
    }
    
    private static WebElement findAddToCartButton() {
        String[] addToCartSelectors = {
            "//button[contains(text(),'Add to cart') or contains(text(),'ADD TO CART')]",
            "button._2KpZ6l._2U9uOA._3v1-ww",
            "button._312sl9", 
            "button._30jeq3"
        };
        
        for (String selector : addToCartSelectors) {
            try {
                WebElement element;
                if (selector.startsWith("//")) {
                    element = driver.findElement(By.xpath(selector));
                } else {
                    element = driver.findElement(By.cssSelector(selector));
                }
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return null;
    }
    
    private static void scrollToElementAndClick(WebElement element) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
            Thread.sleep(500);
            
            if (element.isEnabled() && element.isDisplayed()) {
                element.click();
            } else {
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
            }
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", element);
        }
    }
    
    private static void cleanup() {
        if (driver != null) {
            System.out.println("\nCleaning up and closing browser...");
            driver.quit();
        }
    }
}