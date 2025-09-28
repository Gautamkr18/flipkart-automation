import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Set;

/**
 * Simple Flipkart test runner without Maven/TestNG dependencies
 * Run this class directly if you don't have Maven setup
 */
public class FlipkartTestRunner {
    
    private static WebDriver driver;
    private static WebDriverWait wait;
    
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
        
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        
        System.out.println("Chrome driver setup completed!");
    }
    
    private static void runTests() throws Exception {
        // Test 1: Navigate to Flipkart
        System.out.println("\n1. Navigating to Flipkart...");
        driver.get("https://www.flipkart.com/");
        wait.until(ExpectedConditions.titleContains("Flipkart"));
        System.out.println("✅ Successfully navigated to Flipkart");
        
        // Close login popup if present
        closeLoginPopupIfPresent();
        
        // Test 2: Search for product
        System.out.println("\n2. Searching for laptop...");
        WebElement searchBox = findSearchBox();
        searchBox.clear();
        searchBox.sendKeys("laptop");
        
        // Try search button, fallback to Enter key
        WebElement searchButton = findSearchButton();
        if (searchButton != null) {
            searchButton.click();
        } else {
            searchBox.sendKeys(Keys.ENTER);
        }
        
        // Wait for search results
        wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='search-result']")),
            ExpectedConditions.presenceOfElementLocated(By.className("_1AtVbE")),
            ExpectedConditions.presenceOfElementLocated(By.className("_13oc-S")),
            ExpectedConditions.presenceOfElementLocated(By.className("_2kHMtA"))
        ));
        
        System.out.println("✅ Search completed successfully");
        
        // Test 3: Click on first product
        System.out.println("\n3. Clicking on first product...");
        Thread.sleep(2000); // Wait for results to stabilize
        
        WebElement firstProduct = findFirstProduct();
        String mainWindow = driver.getWindowHandle();
        
        // Click on first product
        scrollToElementAndClick(firstProduct);
        
        // Handle new window
        Set<String> allWindows = driver.getWindowHandles();
        if (allWindows.size() > 1) {
            for (String windowHandle : allWindows) {
                if (!windowHandle.equals(mainWindow)) {
                    driver.switchTo().window(windowHandle);
                    break;
                }
            }
        }
        
        // Wait for product page to load
        wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.className("_35KyD6")),
            ExpectedConditions.presenceOfElementLocated(By.className("B_NuCI")),
            ExpectedConditions.presenceOfElementLocated(By.className("_1fQZEK")),
            ExpectedConditions.titleContains("Buy")
        ));
        
        System.out.println("✅ Successfully navigated to product page");
        
        // Test 4: Verify product page
        System.out.println("\n4. Verifying product page elements...");
        boolean hasAddToCart = wait.until(ExpectedConditions.or(
            ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Add to cart') or contains(text(),'ADD TO CART')]")),
            ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'_2KpZ6l _2U9uOA _3v1-ww')]")),
            ExpectedConditions.presenceOfElementLocated(By.className("_312sl9")),
            ExpectedConditions.presenceOfElementLocated(By.className("_30jeq3"))
        )) != null;
        
        if (hasAddToCart) {
            System.out.println("✅ Product page verified - Add to cart button found");
        } else {
            System.out.println("⚠️ Product page loaded but add to cart button not found");
        }
        
        System.out.println("Current URL: " + driver.getCurrentUrl());
    }
    
    private static void closeLoginPopupIfPresent() {
        try {
            String[] closeButtonSelectors = {
                "button._2KpZ6l._2doB4z",
                "button[data-testid='close']",
                "span._30XB9F",
                "button._2KpZ6l",
                "._30XB9F"
            };
            
            for (String selector : closeButtonSelectors) {
                try {
                    WebElement closeButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.cssSelector(selector)));
                    closeButton.click();
                    Thread.sleep(1000);
                    System.out.println("Login popup closed");
                    return;
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
            "input.Pke_EE",
            "input[data-testid='search-input']"
        };
        
        for (String selector : searchBoxSelectors) {
            try {
                return wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector(selector)));
            } catch (Exception e) {
                continue;
            }
        }
        
        throw new NoSuchElementException("Search box not found");
    }
    
    private static WebElement findSearchButton() {
        String[] searchButtonSelectors = {
            "button[type='submit']",
            "button._2iLD__",
            "button.L0Z3Pu",
            "button[data-testid='search-button']",
            ".L0Z3Pu"
        };
        
        for (String selector : searchButtonSelectors) {
            try {
                return driver.findElement(By.cssSelector(selector));
            } catch (Exception e) {
                continue;
            }
        }
        
        return null;
    }
    
    private static WebElement findFirstProduct() {
        String[] productSelectors = {
            "div._1AtVbE:first-of-type a",
            "div._13oc-S:first-of-type a",
            "div._2kHMtA:first-of-type a",
            "div[data-testid='search-result']:first-of-type a",
            "._1fQZEK:first-of-type",
            "._2UzuFa:first-of-type a"
        };
        
        for (String selector : productSelectors) {
            try {
                WebElement element = wait.until(
                    ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
                if (element.isDisplayed()) {
                    return element;
                }
            } catch (Exception e) {
                continue;
            }
        }
        
        // Fallback
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/p/') or contains(@href, '/product')]")));
        } catch (Exception e) {
            throw new NoSuchElementException("First product not found");
        }
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