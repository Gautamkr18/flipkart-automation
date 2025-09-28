import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import java.time.Duration;

public class FlipkartTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://www.flipkart.com/");

            // Close login popup if present
            try {
                WebElement closePopup = driver.findElement(By.cssSelector("button._2KpZ6l._2doB4z"));
                closePopup.click();
            } catch (Exception e) {}

            // Login steps
            driver.findElement(By.xpath("//a[text()='Login']")).click();
            driver.findElement(By.xpath("//input[@class='_2IX_2- VJZDxU']")).sendKeys("your_email@test.com");
            driver.findElement(By.xpath("//input[@type='password']")).sendKeys("your_password");
            driver.findElement(By.xpath("//button[@type='submit']")).click();

            // Search product
            WebElement searchBox = driver.findElement(By.name("q"));
            searchBox.sendKeys("laptop");
            driver.findElement(By.cssSelector("button[type='submit']")).click();

            // Select first product
            driver.findElement(By.cssSelector("div._1AtVbE:first-of-type a")).click();

            // Switch to new tab
            for (String handle : driver.getWindowHandles()) {
                driver.switchTo().window(handle);
            }

            // Add to cart & proceed
            driver.findElement(By.xpath("//button[text()='Add to cart']")).click();
            driver.findElement(By.xpath("//span[text()='Place Order']")).click();

            System.out.println("Navigated to Payment Page Successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}
