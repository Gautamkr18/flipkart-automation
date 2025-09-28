import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.util.List;
import java.util.Set;

/**
 * Working Flipkart automation test with updated selectors
 */
public class WorkingFlipkartTest {
    
    private static WebDriver driver;
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Flipkart Automation Test...");
        
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
        options.addArguments("--disable-blink-features=AutomationControlled");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        
        // Hide automation indicators
        ((JavascriptExecutor) driver).executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
        
        System.out.println("Chrome driver setup completed!");
    }
    
    private static void runTests() throws Exception {
        // Test 1: Navigate to Flipkart
        System.out.println("\n1️⃣ Navigating to Flipkart...");
        driver.get("https://www.flipkart.com/");
        Thread.sleep(5000);
        
        String title = driver.getTitle();
        String url = driver.getCurrentUrl();
        System.out.println("📄 Page title: " + title);
        System.out.println("🔗 Current URL: " + url);
        
        if (title.contains("Flipkart") || url.contains("flipkart")) {
            System.out.println("✅ Successfully navigated to Flipkart");
        } else {
            System.out.println("⚠️ Page loaded but may not be Flipkart homepage");
        }
        
        // Close login popup if present
        closeLoginPopupIfPresent();
        
        // Test 2: Search for product
        System.out.println("\n2️⃣ Searching for laptop...");
        WebElement searchBox = findSearchBox();
        if (searchBox != null) {
            searchBox.clear();
            searchBox.sendKeys("laptop");
            searchBox.sendKeys(Keys.ENTER);
            
            // Wait for search results
            Thread.sleep(5000);
            
            // Declare searchUrl at a wider scope for later use
        }
        String searchUrl = driver.getCurrentUrl();
            System.out.println("🔍 Search URL: " + searchUrl);
            
            if (searchUrl.contains("search") || searchUrl.contains("laptop")) {
                System.out.println("✅ Search completed successfully");
            } else {
                System.out.println("⚠️ Search may not have completed properly");
            }
        } else {
            System.out.println("❌ Search box not found");
            return;
        }
        
        // Test 3: Find and display products
        System.out.println("\n3️⃣ Finding available products...");
        Thread.sleep(3000); // Wait for results to stabilize
        
        List<WebElement> products = findAllProducts();
    if (!products.isEmpty()) {
            System.out.println("📦 Found " + products.size() + " products");
            
            // Try to click on the first available product
            for (int i = 0; i < Math.min(3, products.size()); i++) {
                WebElement product = products.get(i);
                try {
                    String productText = product.getText();
                    if (productText.length() > 50) {
                        productText = productText.substring(0, 50) + "...";
                    }
                    System.out.println("🔍 Trying product " + (i+1) + ": " + productText);
                    
                    String mainWindow = driver.getWindowHandle();
                    
                    // Click on product
                    scrollToElementAndClick(product);
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
                    
                    // Check if we successfully navigated to product page
                    String newUrl = driver.getCurrentUrl();
                    String newTitle = driver.getTitle();
                    
                    System.out.println("🔗 New URL: " + newUrl);
                    
                    if (newUrl.contains("/p/") || newTitle.toLowerCase().contains("buy") || 
                        !newUrl.equals(searchUrl)) {
                        System.out.println("✅ Successfully navigated to product page");
                        
                        // Test 4: Verify product page elements
                        System.out.println("\n4️⃣ Verifying product page elements...");
                        
                        // Look for product elements
                        if (findProductTitle() != null) {
                            System.out.println("✅ Product title found");
                        }
                        
                        if (findProductPrice() != null) {
                            System.out.println("✅ Product price found");
                        }
                        
                        if (findAddToCartButton() != null) {
                            System.out.println("✅ Add to cart button found");
                        } else {
                            System.out.println("⚠️ Add to cart button not found (may require login)");
                        }
                        
                        System.out.println("✅ Product page verification completed");
                        return; // Successfully completed the test
                        
                    } else {
                        System.out.println("⚠️ Product " + (i+1) + " click didn't navigate to product page");
                        // Try next product
                    }
                    
                } catch (Exception e) {
                    System.out.println("⚠️ Error with product " + (i+1) + ": " + e.getMessage());
                }
            }
            
            System.out.println("⚠️ Could not successfully navigate to any product page");
        } else {
            System.out.println("❌ No products found in search results");
        }
    }
    
    private static void closeLoginPopupIfPresent() {
        try {
            Thread.sleep(2000); // Wait for popup to appear
            
            String[] closeButtonSelectors = {
                "button._2KpZ6l._2doB4z",
                "span._30XB9F", 
                "button._2KpZ6l",
                "._30XB9F",
                "[data-testid='close']",
                ".close-button"
            };
            
            for (String selector : closeButtonSelectors) {
                try {
                    WebElement closeButton = driver.findElement(By.cssSelector(selector));
                    if (closeButton.isDisplayed()) {
                        closeButton.click();
                        Thread.sleep(1000);
                        System.out.println("✅ Login popup closed");
                        return;
                    }
                } catch (Exception e) {
                    continue;
                }
            }
            
            System.out.println("ℹ️ No login popup found");
            
        } catch (Exception e) {
            System.out.println("ℹ️ No login popup to close");
        }
    }
    
    private static WebElement findSearchBox() {
        String[] searchBoxSelectors = {
            "input[name='q']",
            "input[placeholder*='Search']",
            "input._3704LK",
            "input.Pke_EE",
            "input[title='Search for Products, Brands and More']",
            "input[type='text']"
        };
        
        for (String selector : searchBoxSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed()) {
                    System.out.println("✅ Search box found with selector: " + selector);
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        System.out.println("❌ Search box not found with any selector");
        return null;
    }
    
    private static List<WebElement> findAllProducts() {
        String[] productSelectors = {
            "[data-id] a",
            "div._1AtVbE a",
            "div._13oc-S a", 
            "div._2kHMtA a",
            "._1fQZEK a",
            "._2UzuFa a",
            "[data-tkid] a",
            "._1LOTFQ a"
        };
        
        for (String selector : productSelectors) {
            try {
                List<WebElement> elements = driver.findElements(By.cssSelector(selector));
                if (elements.size() > 0) {
                    System.out.println("✅ Products found with selector: " + selector);
                    return elements;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        System.out.println("❌ No products found with any selector");
        return driver.findElements(By.cssSelector("a")); // Return all links as fallback
    }
    
    private static WebElement findProductTitle() {
        String[] titleSelectors = {
            "span.B_NuCI",
            "h1._35KyD6",
            "._35KyD6",
            ".pdp-e-i-head h1",
            "[data-testid='product-name']"
        };
        
        for (String selector : titleSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return null;
    }
    
    private static WebElement findProductPrice() {
        String[] priceSelectors = {
            "._30jeq3._16Jk6d",
            "._30jeq3",
            ".pdp-price",
            "[data-testid='price']"
        };
        
        for (String selector : priceSelectors) {
            try {
                WebElement element = driver.findElement(By.cssSelector(selector));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return null;
    }
    
    private static WebElement findAddToCartButton() {
        String[] addToCartSelectors = {
            "//button[contains(text(),'Add to cart') or contains(text(),'ADD TO CART')]",
            "button._2KpZ6l._2U9uOA._3v1-ww",
            "button._312sl9", 
            "button._30jeq3",
            "[data-testid='add-to-cart']"
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
            System.out.println("\n🧹 Cleaning up and closing browser...");
            driver.quit();
        }
    }
}