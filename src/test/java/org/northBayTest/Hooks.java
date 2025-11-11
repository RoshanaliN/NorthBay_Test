package org.northBayTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import java.io.FileReader;
import java.io.Reader;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.northBayTest.modelClass.TestData;
import org.northBayTest.utils.MiniLLMHelper;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v140.emulation.Emulation;
import org.openqa.selenium.devtools.v140.emulation.model.DevicePosture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.openqa.selenium.devtools.v140.emulation.model.DevicePosture.Type.CONTINUOUS;

public class Hooks {

  private static final Logger logger = LoggerFactory.getLogger(Hooks.class);

  private static WebDriver driver = null;
  public static List<TestData> dataList;

  @Before("not @mobile")
  public static void setup() {
    if (driver == null) {
      driver = new ChromeDriver();
      driver.manage().window().maximize();
      driver.manage().deleteAllCookies();
      driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
      driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
      logger.info("WebDriver initialized successfully for desktop.");
    }
    loadTestData();
  }

  @Before("@mobile")
  public void setUpMobile() {
    ChromeDriver chromeDriver = new ChromeDriver();
    driver = chromeDriver;
    driver.manage().deleteAllCookies();
    driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(20));
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    DevTools devTools = chromeDriver.getDevTools();
    devTools.createSession();

    devTools.send(Emulation.setDeviceMetricsOverride(
        360, 600, 1, true,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(new DevicePosture(CONTINUOUS))
    ));

    logger.info("WebDriver initialized successfully for mobile.");
    loadTestData();
  }

  @After
  public static void tearDown(Scenario scenario) {
    if (driver != null) {
      if (scenario.isFailed()) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        scenario.attach(screenshot, "image/png", "Failure Screenshot");
      }
      driver.quit();
      driver = null;
      logger.info("WebDriver quit successfully.");
    }
    MiniLLMHelper.close();
  }

  public static WebDriver getDriver() {
    return driver;
  }

  private static void loadTestData() {
    try (Reader reader = new FileReader("src/test/resources/test-data.json")) {
      dataList = new Gson().fromJson(reader, new TypeToken<List<TestData>>() {
      }.getType());
      logger.info("Test data loaded successfully. Total entries: {}", dataList.size());
    } catch (Exception e) {
      logger.error("Failed to load test data.", e);
      throw new RuntimeException("Could not load test-data.json", e);
    }
  }

}
