package tests;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.ConfigReader;
import utils.WebDriverManager;

import java.time.Duration;
import java.util.Set;

/**
 * Flipkart automation test class with modern selectors and improved error handling
 */
public class FlipkartTest {
    
    private WebDriver driver;
    private WebDriverWait wait;
    
    @BeforeClass
    public void setUp() {
        // Initialize driver using WebDriverManager
        String browser = ConfigReader.getBrowser();
        driver = WebDriverManager.initializeDriver(browser);
        wait = WebDriverManager.getWait();
        
        System.out.println("Browser initialized: " + browser);
    }
    
    @Test(priority = 1, description = "Navigate to Flipkart and verify homepage")
    public void testNavigateToFlipkart() {
        try {
            String baseUrl = ConfigReader.getBaseUrl();
            driver.get(baseUrl);
            
            // Wait for page to load and verify title
            wait.until(ExpectedConditions.titleContains("Flipkart"));
            String pageTitle = driver.getTitle();
            System.out.println("Page title: " + pageTitle);
            
            Assert.assertTrue(pageTitle.contains("Flipkart"), 
                "Page title should contain 'Flipkart'");
            
            // Close login popup if present
            closeLoginPopupIfPresent();
            
            System.out.println("Successfully navigated to Flipkart homepage");
            
        } catch (Exception e) {
            System.err.println("Error navigating to Flipkart: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(priority = 2, description = "Search for a product")
    public void testSearchProduct() {
        try {
            String searchProduct = ConfigReader.getSearchProduct();
            
            // Find search box with multiple selector strategies
            WebElement searchBox = findSearchBox();
            Assert.assertNotNull(searchBox, "Search box should be found");
            
            // Clear and enter search term
            searchBox.clear();
            searchBox.sendKeys(searchProduct);
            
            // Find and click search button
            WebElement searchButton = findSearchButton();
            if (searchButton != null) {
                searchButton.click();
            } else {
                // Alternative: Press Enter
                searchBox.sendKeys(Keys.ENTER);
            }
            
            // Wait for search results to load
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.cssSelector("[data-testid='search-result']")),
                ExpectedConditions.presenceOfElementLocated(By.className("_1AtVbE")),
                ExpectedConditions.presenceOfElementLocated(By.className("_13oc-S")),
                ExpectedConditions.presenceOfElementLocated(By.className("_2kHMtA"))
            ));
            
            // Verify search results are displayed
            String currentUrl = driver.getCurrentUrl();
            Assert.assertTrue(currentUrl.contains("search") || currentUrl.contains(searchProduct), 
                "URL should contain search term or search keyword");
            
            System.out.println("Search completed successfully for: " + searchProduct);
            
        } catch (Exception e) {
            System.err.println("Error searching for product: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(priority = 3, description = "Click on first search result")
    public void testClickFirstProduct() throws InterruptedException {
        try {
            // Wait a bit for results to stabilize
            Thread.sleep(2000);
            
            // Find first product with multiple selector strategies
            WebElement firstProduct = findFirstProduct();
            Assert.assertNotNull(firstProduct, "First product should be found");
            
            // Store the main window handle
            String mainWindow = driver.getWindowHandle();
            
            // Click on the first product
            scrollToElementAndClick(firstProduct);
            
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
            
            // Wait for product page to load
            wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.className("_35KyD6")),
                ExpectedConditions.presenceOfElementLocated(By.className("B_NuCI")),
                ExpectedConditions.presenceOfElementLocated(By.className("_1fQZEK")),
                ExpectedConditions.titleContains("Buy")
            ));
            
            System.out.println("Successfully navigated to product page");
            
        } catch (Exception e) {
            System.err.println("Error clicking on first product: " + e.getMessage());
            throw e;
        }
    }
    
    @Test(priority = 4, description = "Verify product page elements")
    public void testVerifyProductPage() {
        try {
            // Verify we're on a product page by checking for common elements
            boolean isProductPage = wait.until(ExpectedConditions.or(
                ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(text(),'Add to cart') or contains(text(),'ADD TO CART')]")),
                ExpectedConditions.presenceOfElementLocated(By.xpath("//button[contains(@class,'_2KpZ6l _2U9uOA _3v1-ww')]")),
                ExpectedConditions.presenceOfElementLocated(By.className("_312sl9")),
                ExpectedConditions.presenceOfElementLocated(By.className("_30jeq3"))
            )) != null;
            
            Assert.assertTrue(isProductPage, "Should be on product page");
            
            String currentUrl = driver.getCurrentUrl();
            System.out.println("Product page URL: " + currentUrl);
            System.out.println("Product page verified successfully");
            
        } catch (Exception e) {
            System.err.println("Error verifying product page: " + e.getMessage());
            throw e;
        }
    }
    
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            System.out.println("Cleaning up and closing browser...");
            WebDriverManager.quitDriver();
        }
    }
    
    // Helper Methods
    
    /**
     * Close login popup if present
     */
    private void closeLoginPopupIfPresent() {
        try {
            // Multiple possible selectors for close button
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
                    Thread.sleep(1000); // Wait for popup to close
                    System.out.println("Login popup closed");
                    return;
                } catch (TimeoutException e) {
                    // Try next selector
                    continue;
                }
            }
            
            // If no popup found, that's fine
            System.out.println("No login popup found or already closed");
            
        } catch (Exception e) {
            System.out.println("No login popup to close or error closing: " + e.getMessage());
        }
    }
    
    /**
     * Find search box using multiple strategies
     */
    private WebElement findSearchBox() {
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
            } catch (TimeoutException e) {
                continue;
            }
        }
        
        throw new NoSuchElementException("Search box not found with any selector");
    }
    
    /**
     * Find search button using multiple strategies
     */
    private WebElement findSearchButton() {
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
            } catch (NoSuchElementException e) {
                continue;
            }
        }
        
        return null; // Return null if not found, will use Enter key instead
    }
    
    /**
     * Find first product using multiple strategies
     */
    private WebElement findFirstProduct() {
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
            } catch (TimeoutException e) {
                continue;
            }
        }
        
        // Fallback: Find any clickable product link
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href, '/p/') or contains(@href, '/product')]")));
        } catch (TimeoutException e) {
            throw new NoSuchElementException("First product not found with any selector");
        }
    }
    
    /**
     * Scroll to element and click it
     */
    private void scrollToElementAndClick(WebElement element) {
        try {
            // Scroll element into view
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", element);
            
            Thread.sleep(500); // Wait for scroll to complete
            
            // Try regular click first
            if (element.isEnabled() && element.isDisplayed()) {
                element.click();
            } else {
                // JavaScript click as fallback
                ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].click();", element);
            }
        } catch (Exception e) {
            // JavaScript click as final fallback
            ((JavascriptExecutor) driver).executeScript(
                "arguments[0].click();", element);
        }
    }
}