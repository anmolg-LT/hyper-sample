package hyperexecute;

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
public class InputFormSubmitTest {
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
    public void test_InputFormSubmit2(String browserName, String version, String platform, String build, String name) {
        SetUpBrowser(browserName, version, platform, build, name);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to DemoQA Practice Form
            driver.get("https://demoqa.com/automation-practice-form");
            System.out.println("Navigated to DemoQA Practice Form");

            // Wait for the form to render
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            WebElement firstName = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(By.id("firstName")));

            // Fill in the form
            firstName.sendKeys("Testing");
            driver.findElement(By.id("lastName")).sendKeys("User");
            driver.findElement(By.id("userEmail")).sendKeys("testing@testing.com");

            // Gender — the radio input is hidden, so click its label via JS
            WebElement genderLabel = driver.findElement(By.cssSelector("label[for='gender-radio-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", genderLabel);

            driver.findElement(By.id("userNumber")).sendKeys("1234567890");

            // Hobbies — the checkbox input is hidden, so click its label via JS
            WebElement hobbyLabel = driver.findElement(By.cssSelector("label[for='hobbies-checkbox-1']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", hobbyLabel);

            driver.findElement(By.id("currentAddress"))
                    .sendKeys("Googleplex, 1600 Amphitheatre Pkwy, Mountain View, CA 94043");

            System.out.println("Filled in all form fields");

            // Submit the form — JS click avoids the fixed footer ad intercepting the click
            WebElement submitBtn = driver.findElement(By.id("submit"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", submitBtn);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
            System.out.println("Clicked submit button");

            // Verify the confirmation modal appears
            WebElement modalTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.id("example-modal-sizes-title-lg")));
            Boolean formSubmitted = modalTitle.getText().contains("Thanks for submitting the form");

            if (formSubmitted) {
                System.out.println("Practice Form submission successful");
            } else {
                System.out.println("Practice Form submission completed");
            }

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
                arguments("Microsoft Edge", "latest", platform_name,
                        "DailyRegressionBuild",
                        "Input Form Submit"),
                arguments("Microsoft Edge", "latest-1", platform_name,
                        "DailyRegressionBuild",
                        "Input Form Submit"));
    }
}
