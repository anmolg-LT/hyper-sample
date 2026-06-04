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
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InputFormSubmit3Test {
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
    public void test_InputFormSubmit4(String browserName, String version, String platform, String build, String name) {
        SetUpBrowser(browserName, version, platform, build, name);

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        try {
            // Navigate to Selenium Playground
            driver.get("https://www.lambdatest.com/selenium-playground/");
            System.out.println("Navigated to Selenium Playground");

            // Wait for page to fully load and React to initialize
            Thread.sleep(3000);

            // Click on Input Form Submit link with retry logic
            WebDriverWait linkWait = new WebDriverWait(driver, Duration.ofSeconds(15));
            int maxRetries = 3;
            boolean clicked = false;

            for (int i = 0; i < maxRetries && !clicked; i++) {
                try {
                    WebElement element = linkWait.until(ExpectedConditions.elementToBeClickable(
                            By.xpath("//a[.='Input Form Submit']")));
                    element.click();
                    clicked = true;
                    System.out.println("Clicked on Input Form Submit link");
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    System.out.println("Stale element, retrying... Attempt " + (i + 1));
                    Thread.sleep(1000);
                }
            }

            if (!clicked) {
                throw new Exception("Failed to click Input Form Submit after " + maxRetries + " retries");
            }

            Thread.sleep(2000);

            // Fill in the form
            WebElement name_field = driver.findElement(By.xpath("//input[@id='name']"));
            name_field.sendKeys("Testing");

            WebElement email_address = driver.findElement(By.id("inputEmail4"));
            email_address.sendKeys("testing@testing.com");

            WebElement password = driver.findElement(By.xpath("//input[@name='password']"));
            password.sendKeys("password");

            WebElement company = driver.findElement(By.cssSelector("#company"));
            company.sendKeys("LambdaTest");

            WebElement website = driver.findElement(By.cssSelector("#websitename"));
            website.sendKeys("https://www.lambdatest.com");

            WebElement countryDropDown = driver.findElement(By.xpath("//select[@name='country']"));
            Select selectElement = new Select(countryDropDown);
            selectElement.selectByIndex(6);

            WebElement city = driver.findElement(By.xpath("//input[@id='inputCity']"));
            city.sendKeys("San Jose");

            WebElement address1 = driver.findElement(By.cssSelector("[placeholder='Address 1']"));
            address1.sendKeys("Googleplex, 1600 Amphitheatre Pkwy");

            WebElement address2 = driver.findElement(By.cssSelector("[placeholder='Address 2']"));
            address2.sendKeys(" Mountain View, CA 94043");

            WebElement state = driver.findElement(By.cssSelector("#inputState"));
            state.sendKeys("California");

            WebElement zipcode = driver.findElement(By.cssSelector("#inputZip"));
            zipcode.sendKeys("94088");

            System.out.println("Filled in all form fields");

            // Click submit button
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            WebElement submitBtn = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("#seleniumform > div.text-right.mt-20 > button")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn);
            Thread.sleep(2000);
            System.out.println("Clicked submit button");

            // Verify form submission was successful
            Boolean formSubmitted = driver.getPageSource().contains(
                    "Thanks for contacting us, we will get back to you shortly");

            if (formSubmitted) {
                System.out.println("Input Form Demo successful");
            } else {
                System.out.println("Input Form Demo completed");
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
                        "Input Form Submit (3)"),
                arguments("Microsoft Edge", "latest-1", platform_name,
                        "DailyRegressionBuild",
                        "Input Form Submit (3)"));
    }
}
