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
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FormDeleteTest {
    protected RemoteWebDriver driver = null;
    public static String status;
    String gridURL = "@hub.lambdatest.com/wd/hub";
    String user_name = System.getenv("LT_USERNAME") == null ? "LT_USERNAME" : System.getenv("LT_USERNAME");
    String access_key = System.getenv("LT_ACCESS_KEY") == null ? "LT_ACCESS_KEY" : System.getenv("LT_ACCESS_KEY");
    String test_platform = System.getenv("TEST_OS");

    @BeforeAll
    public static void start() {
        System.out.println("Running JUnit test on HyperExecute Grid");
    }

    @BeforeEach
    public void setup() {
        System.out.println("Setting up resources to run tests on HyperExecute Grid");
    }

    public void SetUpBrowser(String browserName, String version, String platform, String build, String name) {
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
    public void test_FormDelete(String browserName, String version, String platform, String build, String name) {
        SetUpBrowser(browserName, version, platform, build, name);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            driver.get("https://anmolg-lt.github.io/New-Sample-To-Do/");
            System.out.println("Navigated to ToDo App");

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

            // Switch to the Forms view
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                    "//button[contains(@class,'nav-link') and normalize-space(.)='Forms']"))).click();
            Thread.sleep(1000);

            // Create a form to delete
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("button.new-form-btn"))).click();
            Thread.sleep(1000);
            driver.findElement(By.cssSelector("input[aria-label='Name']")).sendKeys("Delete Me");
            driver.findElement(By.cssSelector("input[aria-label='Age']")).sendKeys("50");
            driver.findElement(By.cssSelector("input[aria-label='City']")).sendKeys("Rome");
            driver.findElement(By.cssSelector("input[aria-label='Email']")).sendKeys("del@example.com");
            driver.findElement(By.xpath("//button[normalize-space(.)='Save form']")).click();
            Thread.sleep(1000);
            System.out.println("Created form 'Delete Me'");

            // The detail view exposes a Delete button that returns to the list
            wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//button[normalize-space(.)='Delete']"))).click();
            Thread.sleep(1000);
            System.out.println("Deleted the form");

            // Verify the entry is gone and the empty state is shown
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//p[contains(@class,'empty') and contains(.,'No saved forms yet')]")));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            assertTrue(driver.findElements(
                    By.xpath("//span[@class='entry-name' and text()='Delete Me']")).isEmpty(),
                    "Deleted form should no longer be in the list");
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            System.out.println("Empty state verified after deletion");

            System.out.println("Form Delete test completed successfully");
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
                        "Form Delete"),
                arguments("Microsoft Edge", "latest", platform_name,
                        "DailyRegressionBuild",
                        "Form Delete"));
    }
}
