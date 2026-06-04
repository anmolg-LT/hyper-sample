# JUnit Selenium HyperExecute Sample Project

## Project Overview

This is a sample project demonstrating how to run Selenium automation tests using JUnit 5 on LambdaTest's HyperExecute platform. HyperExecute is a smart test orchestration platform that provides optimal speed and parallel test execution across multiple browsers and operating systems.

**Project Type:** Java test automation sample
**Primary Purpose:** Demonstrate HyperExecute integration with JUnit and Selenium
**Organization:** LambdaTest

## Tech Stack

### Core Framework
- **Java:** 17 (compiled with Java 17, runtime can be newer)
- **Build Tool:** Maven 3.x
- **Test Framework:** JUnit Jupiter 5.14.0
- **Selenium:** 4.39.0
- **WebDriver Manager:** 5.1.0 (Bonigarcia)

### Dependencies
```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.14.0</version>
</dependency>
<dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.39.0</version>
</dependency>
```

### Maven Plugins
- **maven-surefire-plugin:** 3.5.4 (test execution and reporting)
- **maven-compiler-plugin:** 3.14.1
- **maven-site-plugin:** 3.9.1 (report generation)
- **maven-jxr-plugin:** 3.6.0 (source cross-reference)

## Project Structure

```
junit-selenium-hyperexecute-sample-main/
├── src/
│   └── test/
│       ├── java/
│       │   └── hyperexecute/
│       │       ├── ToDoAddAndCompleteTest.java
│       │       ├── ToDoAddAndComplete2Test.java
│       │       ├── ToDoAddAndComplete3Test.java
│       │       ├── ToDoAddAndComplete4Test.java
│       │       ├── ToDoAddAndComplete5Test.java
│       │       ├── ToDoFilterTest.java
│       │       ├── ToDoClearAndDeleteTest.java
│       │       ├── InputFormSubmitTest.java
│       │       ├── InputFormSubmit2Test.java
│       │       ├── InputFormSubmit3Test.java
│       │       ├── InputFormSubmit4Test.java
│       │       ├── BingSearchTest.java
│       │       ├── BingSearch2Test.java
│       │       ├── BingSearch3Test.java
│       │       └── BingSearch4Test.java
│       └── resources/
├── yaml/
│   ├── junit_hyperexecute_hybrid_sample.yaml      # Multi-platform hybrid config
│   ├── linux/
│   │   ├── junit_hyperexecute_autosplit_sample.yaml
│   │   └── junit_hyperexecute_matrix_sample.yaml
│   ├── mac/
│   │   ├── junit_hyperexecute_autosplit_sample.yaml
│   │   └── junit_hyperexecute_matrix_sample.yaml
│   └── win/
│       ├── junit_hyperexecute_autosplit_sample.yaml
│       └── junit_hyperexecute_matrix_sample.yaml
├── target/                                         # Build output (generated)
│   ├── site/                                       # Test reports
│   └── surefire-reports/                           # JUnit test reports
├── pom.xml
├── .hyperexecuteignore
├── .gitpod.yml
└── README.md
```

### Key Directories
- **src/test/java/hyperexecute/**: JUnit test classes using Selenium WebDriver
- **yaml/**: HyperExecute configuration files for different execution modes and platforms
- **target/**: Maven build output, test reports, and artifacts (gitignored)

## HyperExecute Configuration

### Execution Modes

The project supports two main execution strategies:

#### 1. **Auto-Split Mode**
- Automatically distributes tests across available infrastructure
- Tests are discovered dynamically using grep/awk on test files
- File: `yaml/<platform>/junit_hyperexecute_autosplit_sample.yaml`
- Concurrency: 4 parallel sessions
- Retry on failure: Enabled (max 5 retries)

#### 2. **Matrix Mode**
- Runs tests across predefined combinations (browsers, versions, platforms)
- Matrix parameter: `classname` (test class names)
- File: `yaml/<platform>/junit_hyperexecute_matrix_sample.yaml`
- Classes: ToDoAddAndCompleteTest, ToDoFilterTest, ToDoClearAndDeleteTest, and 12 more

#### 3. **Hybrid Mode**
- Combines matrix and auto-split strategies
- Cross-platform execution (Mac, Windows, Linux)
- File: `yaml/junit_hyperexecute_hybrid_sample.yaml`
- OS Matrix: `[mac, win, linux]`

### Platform-Specific Configs
- **Linux**: `yaml/linux/*.yaml`
- **macOS**: `yaml/mac/*.yaml`
- **Windows**: `yaml/win/*.yaml`

### Common Configuration Patterns

All YAML files include:
```yaml
# Timeouts (in minutes)
globalTimeout: 150
testSuiteTimeout: 150
testSuiteStep: 150

# Dependency caching
cacheKey: '{{ checksum "pom.xml" }}'
cacheDirectories:
  - m2_cache_dir

# Pre-execution (dependency download)
pre:
  - mvn -Dmaven.repo.local=m2_cache_dir -Dmaven.test.skip=true clean install

# Artifact management
mergeArtifacts: true
uploadArtefacts:
  - name: Final-Report
    path:
      - target/site/**
  - name: Surefire-Report
    path:
      - target/surefire-reports/**
```

## Environment Setup

### Prerequisites

1. **Java Development Kit (JDK):** Java 17 or higher
2. **Maven:** 3.6+
3. **HyperExecute CLI:** Platform-specific binary
4. **LambdaTest Account:** For credentials

### Environment Variables

**Required:**
```bash
export LT_USERNAME=your_lambdatest_username
export LT_ACCESS_KEY=your_lambdatest_access_key
```

**Optional (used in tests):**
```bash
export TARGET_OS=linux          # Platform for test execution (linux/mac/win)
export TEST_OS=Windows 10       # OS name for browser capabilities
```

Get your credentials from: https://accounts.lambdatest.com/detail/profile

### Download HyperExecute CLI

```bash
# macOS
curl -O https://downloads.lambdatest.com/hyperexecute/darwin/hyperexecute
chmod +x hyperexecute

# Linux
curl -O https://downloads.lambdatest.com/hyperexecute/linux/hyperexecute
chmod +x hyperexecute

# Windows (PowerShell)
Invoke-WebRequest -Uri https://downloads.lambdatest.com/hyperexecute/windows/hyperexecute.exe -OutFile hyperexecute.exe
```

## Common Commands

### Local Development

```bash
# Clean and compile
mvn clean compile

# Run tests locally (requires local WebDriver setup)
mvn test

# Run specific test class
mvn test -Dtest=ToDoAddAndCompleteTest

# Generate test reports
mvn test site surefire-report:report

# Skip tests and just download dependencies
mvn -Dmaven.test.skip=true clean install

# Use custom Maven repository location
mvn -Dmaven.repo.local=m2_cache_dir test
```

### HyperExecute Execution

```bash
# Auto-split mode (Windows)
./hyperexecute --config yaml/win/junit_hyperexecute_autosplit_sample.yaml --force-clean-artifacts --download-artifacts

# Auto-split mode (Linux)
./hyperexecute --config yaml/linux/junit_hyperexecute_autosplit_sample.yaml --force-clean-artifacts --download-artifacts

# Matrix mode (Windows)
./hyperexecute --config yaml/win/junit_hyperexecute_matrix_sample.yaml --force-clean-artifacts --download-artifacts

# Hybrid multi-platform mode
./hyperexecute --config yaml/junit_hyperexecute_hybrid_sample.yaml --force-clean-artifacts --download-artifacts
```

### CLI Options
- `--config`: Path to HyperExecute YAML config file
- `--force-clean-artifacts`: Remove existing artifacts before execution
- `--download-artifacts`: Download test artifacts after execution
- `--verbose`: Enable verbose logging

## Test Scenarios

The project contains **15 test classes** with a total of **30 parameterized test executions** across different browsers and platforms. There are 3 distinct test types, each with multiple copies for parallel scale testing.

### ToDo App Tests

#### ToDoAddAndCompleteTest (src/test/java/hyperexecute/ToDoAddAndCompleteTest.java)

**Test Method:** `test_ToDoApp`
**Copies:** ToDoAddAndComplete2Test, ToDoAddAndComplete3Test, ToDoAddAndComplete4Test, ToDoAddAndComplete5Test

**What it tests:**
- Navigation to the Sample To-Do App
- Adding multiple todo items
- Marking a todo item as done (checkbox toggle)
- Verifying the `done` CSS class is applied to completed items
- Verifying the top-bar stats counters update correctly

**Test Steps:**
1. Navigate to `https://anmolg-lt.github.io/New-Sample-To-Do/`
2. Wait for the app to render (text input visible)
3. Add first todo item: "Yey, Let's add it to list"
4. Verify the item appears in the list (`.todo-text` span)
5. Add second todo item: "Complete HyperExecute testing"
6. Mark first item as done (click checkbox inside the `<li>`)
7. Verify first item has `done` CSS class
8. Verify top-bar stats show "1 remaining"

**Browser Configurations:**
- Chrome latest on TARGET_OS platform
- Chrome latest-1 on TARGET_OS platform

**Build Names:**
- ToDo Add & Complete - Chrome latest / Chrome latest-1
- ToDo Add & Complete (2) through (5) for scale copies

---

#### ToDoFilterTest (src/test/java/hyperexecute/ToDoFilterTest.java)

**Test Method:** `test_ToDoFilters`

**What it tests:**
- Adding multiple todo items to the Sample To-Do App
- Marking items as done
- Filter behavior (All, Active, Completed)
- Visibility of the "Clear completed" button in the Completed filter

**Test Steps:**
1. Navigate to `https://anmolg-lt.github.io/New-Sample-To-Do/`
2. Wait for the app to render (text input visible)
3. Add "Task A" and "Task B"
4. Mark "Task A" as done (click checkbox)
5. Click "Active" filter button — verify "Task B" is visible and "Task A" is hidden
6. Click "Completed" filter button — verify "Task A" is visible and "Task B" is hidden
7. Verify "Clear completed" button is visible in the Completed filter
8. Click "All" filter button — verify both "Task A" and "Task B" are visible

**Browser Configurations:**
- Microsoft Edge latest on TARGET_OS platform
- Microsoft Edge latest-1 on TARGET_OS platform

**Build Names:**
- ToDo Filters - Edge latest / Edge latest-1

**Key Features Tested:**
- Filter buttons (`.filter-btn`) toggling item visibility
- Element absence verification (temporarily reducing implicit wait)
- Conditional UI elements ("Clear completed" button)

---

#### ToDoClearAndDeleteTest (src/test/java/hyperexecute/ToDoClearAndDeleteTest.java)

**Test Method:** `test_ToDoClearAndDelete`

**What it tests:**
- Adding and deleting a todo item
- Verifying an item is removed from the DOM after deletion
- Marking multiple items as done
- Using the "Clear completed" action to bulk-remove done items
- Verifying the empty state after clearing

**Test Steps:**
1. Navigate to `https://anmolg-lt.github.io/New-Sample-To-Do/`
2. Wait for the app to render (text input visible)
3. Add "Temporary item"
4. Verify the item appears in the list
5. Click the Delete button (`aria-label="Delete"`) on "Temporary item"
6. Verify the item is no longer in the DOM
7. Add "Done A" and "Done B"
8. Mark both items as done (click checkboxes)
9. Click the "Completed" filter button
10. Click "Clear completed" button
11. Verify the empty state message ("No items yet — add one above.") is shown
12. Verify top-bar stats show "0" items

**Browser Configurations:**
- Chrome latest on TARGET_OS platform
- Microsoft Edge latest on TARGET_OS platform

**Build Names:**
- ToDo Clear & Delete - Chrome latest / Edge latest

**Key Features Tested:**
- Delete button interaction (`button[aria-label='Delete']`)
- Element absence verification after deletion
- "Clear completed" bulk action
- Empty state rendering

---

### Selenium Playground Tests

#### InputFormSubmitTest (src/test/java/hyperexecute/InputFormSubmitTest.java)

**Test Method:** `test_InputFormSubmit2`
**Copies:** InputFormSubmit2Test, InputFormSubmit3Test, InputFormSubmit4Test

**What it tests:**
- Navigation to LambdaTest Selenium Playground
- Filling in a complete input form (name, email, password, company, website, country, city, addresses, state, zip)
- Submitting the form
- Verifying form submission success message

**Browser Configurations:**
- Microsoft Edge latest on TARGET_OS platform
- Microsoft Edge latest-1 on TARGET_OS platform

**Build Names:**
- Input Form Submit - Edge latest / Edge latest-1
- Input Form Submit (2) through (4) for scale copies

---

### Bing Search Tests

#### BingSearchTest (src/test/java/hyperexecute/BingSearchTest.java)

**Test Method:** `test_BingSearch2`
**Copies:** BingSearch2Test, BingSearch3Test, BingSearch4Test

**What it tests:**
- Navigation to Bing
- Searching for "LambdaTest"
- Verifying search results page title is not null

**Browser Configurations:**
- Chrome latest on TARGET_OS platform
- Microsoft Edge latest on TARGET_OS platform

**Build Names:**
- Bing Search - Chrome latest / Edge latest
- Bing Search (2) through (4) for scale copies

---

### Test Execution Summary

| Test Type | Classes | Test Method | Application Under Test | Browsers | Total Runs |
|-----------|---------|-------------|------------------------|----------|------------|
| ToDo Add & Complete | ToDoAddAndCompleteTest + 4 copies | test_ToDoApp | Sample To-Do App | Chrome (latest, latest-1) | 10 |
| ToDo Filters | ToDoFilterTest | test_ToDoFilters | Sample To-Do App | Edge (latest, latest-1) | 2 |
| ToDo Clear & Delete | ToDoClearAndDeleteTest | test_ToDoClearAndDelete | Sample To-Do App | Chrome latest, Edge latest | 2 |
| Input Form Submit | InputFormSubmitTest + 3 copies | test_InputFormSubmit | Selenium Playground | Edge (latest, latest-1) | 8 |
| Bing Search | BingSearchTest + 3 copies | test_BingSearch | Bing | Chrome latest, Edge latest | 8 |
| **Total** | **15 classes** | **5 test types** | **3 applications** | **4 browser/version combos** | **30 runs** |

### Test Execution Patterns

All tests use:
- **Parameterized Testing:** `@ParameterizedTest` with `@MethodSource`
- **Concurrent Execution:** `@Execution(ExecutionMode.CONCURRENT)`
- **Implicit Waits:** 10 seconds timeout
- **Explicit Waits:** WebDriverWait for critical elements
- **Thread.sleep():** For stability during UI interactions (1 second)
- **Status Reporting:** Pass/fail status sent to LambdaTest dashboard

### Environment Variables Used

- `LT_USERNAME`: LambdaTest username for authentication
- `LT_ACCESS_KEY`: LambdaTest access key for authentication
- `TARGET_OS`: Platform for test execution (linux/mac/win) - used in test parameterization
- `TEST_OS`: OS name for browser capabilities (optional, not used in all tests)

---

## Test Structure

### Test Class Pattern

All test classes follow this structure:

```java
@Execution(ExecutionMode.CONCURRENT)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ToDoAddAndCompleteTest {
    protected RemoteWebDriver driver = null;
    public static String status;

    @BeforeAll
    public static void start() {
        // Pre-test setup
    }

    @BeforeEach
    public void setup() {
        // Before each test
    }

    public void SetUpBrowser(String browserName, String version,
                            String platform, String build, String name) {
        // LambdaTest RemoteWebDriver configuration
        // Uses LT:Options for W3C capabilities
    }

    @ParameterizedTest
    @MethodSource("setup_testEnvironment")
    public void test_ToDoApp(String browserName, String version,
                            String platform, String build, String name) {
        // Test implementation
    }

    @AfterEach
    public void TearDownClass() {
        // Cleanup and status reporting
        ((JavascriptExecutor) driver).executeScript("lambda-status=" + status);
        driver.quit();
    }

    static Stream<Arguments> setup_testEnvironment() {
        // Parameterized test data
        return Stream.of(
            arguments("Chrome", "latest", platform_name, build, name),
            arguments("Chrome", "latest-1", platform_name, build, name)
        );
    }
}
```

### Key Patterns

1. **Concurrent Execution:** Tests use `@Execution(ExecutionMode.CONCURRENT)` for parallel execution
2. **Parameterized Tests:** `@ParameterizedTest` with `@MethodSource` for data-driven testing
3. **RemoteWebDriver:** All tests use LambdaTest grid via RemoteWebDriver
4. **Capabilities:** W3C compliant with LambdaTest-specific options under `LT:Options`
5. **Status Reporting:** Tests report status back to LambdaTest dashboard via JavaScript executor

### LambdaTest Capabilities

```java
MutableCapabilities capabilities = new MutableCapabilities();
capabilities.setCapability("browserName", "Chrome");
capabilities.setCapability("browserVersion", "latest");
capabilities.setCapability("platformName", "Windows 10");

MutableCapabilities ltOptions = new MutableCapabilities();
ltOptions.setCapability("build", "JUnit HyperExecute Build");
ltOptions.setCapability("name", "Test Name");
ltOptions.setCapability("network", true);      // Capture network logs
ltOptions.setCapability("visual", true);       // Visual regression
ltOptions.setCapability("video", true);        // Record video
ltOptions.setCapability("console", true);      // Capture console logs
ltOptions.setCapability("selenium_version", "4.24.0");
capabilities.setCapability("LT:Options", ltOptions);
```

## Test Execution Flow

### HyperExecute Workflow

1. **Pre-execution:**
   - Download and cache Maven dependencies in `m2_cache_dir`
   - Skip test execution (`-Dmaven.test.skip=true`)

2. **Test Discovery:**
   - Parse Java files to find test classes: `grep 'public class' src/test/java/hyperexecute/*.java`
   - Extract class names for execution

3. **Test Execution:**
   - Run tests with Maven: `mvn -Dtest=$test test site surefire-report:report`
   - Generate Surefire and site reports

4. **Post-execution:**
   - Merge artifacts from parallel executions
   - Upload test reports to HyperExecute dashboard
   - Clean up resources

5. **Artifacts:**
   - HTML reports: `target/site/**`
   - Surefire XML reports: `target/surefire-reports/**`

## Reporting

### Generated Reports

1. **Surefire Reports** (`target/surefire-reports/`)
   - XML test results
   - Per-test execution details
   - Used by CI/CD tools

2. **Maven Site Reports** (`target/site/`)
   - HTML test reports
   - Source code cross-reference (JXR)
   - Comprehensive test execution summary

3. **HyperExecute Dashboard**
   - Real-time test execution status
   - Video recordings of test runs
   - Network and console logs
   - Cross-browser test results
   - Access at: https://automation.lambdatest.com/hyperexecute

## Conventions

### Naming Conventions
- Test classes: `*Test.java` or `*Test[Number].java`
- Package: `hyperexecute`
- YAML files: `junit_hyperexecute_<mode>_sample.yaml`

### Code Style
- Java 17 features available
- JUnit 5 (Jupiter) assertions and annotations
- Parameterized tests for browser/platform combinations
- Explicit wait patterns with WebDriverWait
- Status reporting via JavascriptExecutor

### Build Configuration
- **Source/Target:** Java 17
- **Encoding:** UTF-8
- **Parallel Execution:** Disabled in surefire config (handled by HyperExecute)
- **Test Discovery:** Uses JUnit 5 Platform

## Secrets Management

For sensitive values (API keys, tokens):

1. Navigate to HyperExecute Dashboard > Secrets
2. Create secret key (e.g., `testKey`)
3. Reference in YAML:
```yaml
env:
  PAT: ${{ .secrets.testKey }}
```

## Gitpod Integration

The project includes Gitpod support (`.gitpod.yml`):
- One-click cloud development environment
- Pre-configured with Java, Maven, and dependencies
- Integrated with LambdaTest HyperExecute

## CI/CD

GitHub Actions workflow available at `.github/workflows/main.yml`

## Troubleshooting

### Common Issues

1. **Missing Dependencies:**
   ```bash
   mvn clean install -U  # Force update dependencies
   ```

2. **Test Discovery Fails:**
   - Ensure test classes have `public class` declaration
   - Check file paths in `testDiscovery` command

3. **Environment Variables Not Set:**
   ```bash
   echo $LT_USERNAME
   echo $LT_ACCESS_KEY
   ```

4. **HyperExecute CLI Permissions:**
   ```bash
   chmod +x hyperexecute  # macOS/Linux
   ```

## Resources

- **HyperExecute Documentation:** https://www.lambdatest.com/support/docs/getting-started-with-hyperexecute/
- **LambdaTest Dashboard:** https://automation.lambdatest.com/hyperexecute
- **Support:** support@lambdatest.com
- **Community:** https://community.lambdatest.com/

## Quick Start

```bash
# 1. Clone and navigate to project
cd junit-selenium-hyperexecute-sample-main

# 2. Set credentials
export LT_USERNAME=your_username
export LT_ACCESS_KEY=your_access_key

# 3. Download HyperExecute CLI (macOS example)
curl -O https://downloads.lambdatest.com/hyperexecute/darwin/hyperexecute
chmod +x hyperexecute

# 4. Run tests on HyperExecute
./hyperexecute --config yaml/junit_hyperexecute_hybrid_sample.yaml --download-artifacts

# 5. View results
# Check the HyperExecute dashboard or downloaded artifacts
```

---

**Last Updated:** 2026-05-27
**Project Status:** Sample/Reference Implementation
**Maintained By:** LambdaTest
