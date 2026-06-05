package hyperexecute;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.openqa.selenium.Keys;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class BingSearch2Test {
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
    public void test_BingSearch3(String browserName, String version, String platform, String build, String name) {
        SetUpBrowser(browserName, version, platform, build, name);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

        try {
            // Navigate to DuckDuckGo (an automation-friendly search engine).
            // NOTE: Bing serves a bot-detection / degraded page to automated
            // browsers — a required r.bing.com script times out (504), so the
            // search box never renders and the test fails. DuckDuckGo is stable
            // for Selenium and preserves the same "search and verify" intent.
            driver.navigate().to("https://duckduckgo.com/");
            System.out.println("Navigated to DuckDuckGo");

            // Wait for the search box to be ready, then search for LambdaTest
            WebElement searchBox = wait.until(
                    ExpectedConditions.elementToBeClickable(By.cssSelector("input[name='q']")));
            searchBox.sendKeys("LambdaTest");
            Thread.sleep(1000);
            searchBox.sendKeys(Keys.ENTER);
            System.out.println("Searched for LambdaTest");

            // Wait for the results page to load: the title and URL reflect the
            // query and the main results section is present in the DOM
            wait.until(ExpectedConditions.titleContains("LambdaTest"));
            WebElement results = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("section[data-testid='mainline']")));

            // Verify search results
            String title = driver.getTitle();
            String url = driver.getCurrentUrl();
            System.out.println("Page title: " + title);
            assertNotNull(title, "Page title should not be null");
            assertTrue(title.contains("LambdaTest"),
                    "Results page title should contain the search term");
            assertTrue(url.toLowerCase().contains("q=lambdatest"),
                    "Results page URL should contain the search query");
            assertNotNull(results, "Search results section should be present");

            System.out.println("Search test completed successfully");

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
                        "Bing Search (2)"),
                arguments("Microsoft Edge", "latest", platform_name,
                        "DailyRegressionBuild",
                        "Bing Search (2)"));
    }
}
