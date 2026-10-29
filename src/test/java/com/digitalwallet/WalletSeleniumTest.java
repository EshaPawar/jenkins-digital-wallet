
package com.digitalwallet;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.assertTrue;

class WalletSeleniumTest {

    private WebDriver driver;

    private static final String BASE_URL =
            "http://localhost:8081/digital-wallet/";

    @RegisterExtension
    TestWatcher screenshotOnFailure = new TestWatcher() {

        @Override
        public void testFailed(
                ExtensionContext context,
                Throwable cause) {

            takeFailureScreenshot();
        }
    };

    @BeforeEach
    void setUp() {

        System.setProperty(
                "webdriver.chrome.driver",
                "D:\\ChromeDriver\\chromedriver.exe"
        );

        ChromeOptions options = new ChromeOptions();

        driver = new ChromeDriver(options);

        driver.manage().window().maximize();
    }

    @Test
    void testHomePageLoads() {

        driver.get(BASE_URL);

        String pageSource =
                driver.getPageSource();

        assertTrue(
                pageSource.contains("Digital Wallet")
                        || pageSource.contains("digital wallet"),
                "Digital Wallet home page did not load correctly."
        );
    }

    @Test
    void testWalletFormIsDisplayed() {

        driver.get(BASE_URL);

        boolean formExists =
                driver.findElements(By.tagName("form")).size() > 0;

        assertTrue(
                formExists,
                "Wallet form was not displayed."
        );
    }

    @Test
    void testWalletFormContainsRequiredFields() {

        driver.get(BASE_URL);

        String pageSource =
                driver.getPageSource().toLowerCase();

        assertTrue(
                pageSource.contains("name"),
                "Name field is missing."
        );

        assertTrue(
                pageSource.contains("email"),
                "Email field is missing."
        );

        assertTrue(
                pageSource.contains("password"),
                "Password field is missing."
        );
    }

    @Test
    void testWalletPageIsReachable() {

        driver.get(BASE_URL);

        String currentUrl =
                driver.getCurrentUrl();

        assertTrue(
                currentUrl.contains("/digital-wallet"),
                "Digital Wallet URL was not reached."
        );
    }

    @Test
    void testWalletContentIsDisplayed() {

        driver.get(BASE_URL);

        String pageSource =
                driver.getPageSource().toLowerCase();

        assertTrue(
                pageSource.contains("wallet")
                        || pageSource.contains("digital wallet"),
                "Expected wallet content was not found."
        );
    }

    @AfterEach
    void tearDown() {

        if (driver != null) {
            driver.quit();
        }
    }

    private void takeFailureScreenshot() {

        if (driver == null) {
            return;
        }

        try {

            Path screenshotDirectory =
                    Path.of(
                            "target",
                            "selenium-screenshots"
                    );

            Files.createDirectories(
                    screenshotDirectory
            );

            File screenshot =
                    ((TakesScreenshot) driver)
                            .getScreenshotAs(
                                    OutputType.FILE
                            );

            String fileName =
                    "failure-"
                            + System.currentTimeMillis()
                            + ".png";

            Path destination =
                    screenshotDirectory.resolve(
                            fileName
                    );

            Files.copy(
                    screenshot.toPath(),
                    destination,
                    StandardCopyOption.REPLACE_EXISTING
            );

            System.out.println(
                    "Failure screenshot saved: "
                            + destination
            );

        } catch (Exception e) {

            System.out.println(
                    "Could not save failure screenshot: "
                            + e.getMessage()
            );
        }
    }
}
