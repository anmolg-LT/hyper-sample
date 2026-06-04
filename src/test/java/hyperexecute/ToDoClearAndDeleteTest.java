package hyperexecute;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ToDoClearAndDeleteTest {
    protected RemoteWebDriver driver = null;
    public static String status;
    String gridURL = "@hub.lambdatest.com/wd/hub";
    String user_name = System.getenv("LT_USERNAME") == null ? "LT_USERNAME" : System.getenv("LT_USERNAME");
    String access_key = System.getenv("LT_ACCESS_KEY") == null ? "LT_ACCESS_KEY" : System.getenv("LT_ACCESS_KEY");

    @BeforeAll
    public static void start() {
        System.out.println("Running JUnit test on HyperExecute Grid");
    }

    @BeforeEach
    public void setup() {
        System.out.println("Setting up resources to run tests on HyperExecute Grid");
    }

    public void SetUpBrowser(String browserName, String version, String platform,
            String build, String name) {
        MutableCapabilities capabilities = new MutableCapabilities();

        // W3C compliant capabilities
        capabilities.setCapability("browserName", browserName);
        capabilities.setCapability("browserVersion", version);
        capabilities.setCapability("platformName", platform);

        // LambdaTest specific capabilities under LT:Options
        MutableCapabilities ltOptions = new MutableCapabilities();
        ltOptions.setCapability("build", build);
        ltOptions.setCapability("name", name);
        ltOptions.setCapability("network", true);
        ltOptions.setCapability("visual", true);
        ltOptions.setCapability("video", true);
        ltOptions.setCapability("console", true);
        ltOptions.setCapability("selenium_version", "4.24.0");
        capabilities.setCapability("LT:Options", ltOptions);

        try {
            driver = new RemoteWebDriver(new URL("https://" + user_name + ":" + access_key + gridURL), capabilities);
        } catch (MalformedURLException e) {
            System.out.println("Invalid grid URL");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource("setup_testEnvironment")
    public void test_ToDoClearAndDelete(String browserName, String version, String platform, String build, String name) {
        SetUpBrowser(browserName, version, platform, build, name);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://anmolg-lt.github.io/New-Sample-To-Do/");
            System.out.println("Navigated to ToDo App");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("input[placeholder='What needs to be done?']")));

            // Add an item and delete it
            driver.findElement(By.cssSelector("input[placeholder='What needs to be done?']"))
                    .sendKeys("Temporary item");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            Thread.sleep(1000);
            System.out.println("Added temporary item");

            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[@class='todo-text'][text()='Temporary item']")));

            // Delete the item
            driver.findElement(By.xpath(
                    "//li[.//span[text()='Temporary item']]//button[@aria-label='Delete']")).click();
            Thread.sleep(1000);

            // Verify item is gone
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            assertTrue(driver.findElements(
                    By.xpath("//span[@class='todo-text'][text()='Temporary item']")).isEmpty(),
                    "Deleted item should no longer be visible");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("Delete verified");

            // Add two items and mark both as done
            driver.findElement(By.cssSelector("input[placeholder='What needs to be done?']"))
                    .sendKeys("Done A");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            Thread.sleep(1000);

            driver.findElement(By.cssSelector("input[placeholder='What needs to be done?']"))
                    .sendKeys("Done B");
            driver.findElement(By.cssSelector("button[type='submit']")).click();
            Thread.sleep(1000);

            driver.findElement(By.xpath(
                    "//li[.//span[text()='Done A']]//input[@type='checkbox']")).click();
            Thread.sleep(1000);
            driver.findElement(By.xpath(
                    "//li[.//span[text()='Done B']]//input[@type='checkbox']")).click();
            Thread.sleep(1000);
            System.out.println("Marked both items as done");

            // Switch to Completed filter and clear completed
            driver.findElement(By.xpath(
                    "//button[contains(@class,'filter-btn') and .//span[text()='Completed']]")).click();
            Thread.sleep(1000);
            driver.findElement(By.xpath("//button[contains(text(),'Clear completed')]")).click();
            Thread.sleep(1000);

            // Verify empty state
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(),'No items yet')]")));
            WebElement stats = driver.findElement(By.cssSelector(".topbar-stats"));
            assertTrue(stats.getText().contains("0"), "Should show 0 items after clearing");
            System.out.println("Clear completed verified");

            System.out.println("ToDo Clear and Delete test completed successfully");
            status = "passed";
        } catch (Exception e) {
            status = "failed";
            System.out.println("Test failed: " + e.getMessage());
        }
    }

    @AfterEach
    public void TearDownClass() {
        if (driver != null) {
            ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
            driver.quit();
            System.out.println("Browser resources released");
        }
    }

    @AfterAll
    public static void endTest() {
        System.out.println("JUnit execution on HyperExecute Grid complete");
    }

    /* The data is not being read from CSV file */
    static Stream<Arguments> setup_testEnvironment() {
        String platform_name = System.getenv("TARGET_OS");
        System.out.println(platform_name);

        return Stream.of(
                arguments("Chrome", "latest", platform_name,
                        "DailyRegressionBuild",
                        "ToDo Clear & Delete"),
                arguments("Microsoft Edge", "latest", platform_name,
                        "DailyRegressionBuild",
                        "ToDo Clear & Delete"));
    }
}
