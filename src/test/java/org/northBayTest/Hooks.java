package org.northBayTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import java.io.FileReader;
import java.io.Reader;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.northBayTest.modelClass.TestData;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v140.emulation.Emulation;
import org.openqa.selenium.devtools.v140.emulation.model.DevicePosture;

import static org.northBayTest.utils.HtmlReportGenerator.finalizeReport;
import static org.northBayTest.utils.HtmlReportGenerator.initializeReport;
import static org.openqa.selenium.devtools.v140.emulation.model.DevicePosture.Type.CONTINUOUS;

public class Hooks {

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
    }
    try (Reader reader = new FileReader("src/test/resources/test-data.json")) {
      dataList = new Gson().fromJson(reader, new TypeToken<List<TestData>>() {
      }.getType());
    } catch (Exception e) {
      e.printStackTrace();
    }
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
        360, 720, 2.75, true,
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

    devTools.send(Emulation.setUserAgentOverride(
        "Mozilla/5.0 (Linux; Android 11; Pixel 5) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/126.0.0.0 Mobile Safari/537.36",
        Optional.empty(),
        Optional.empty(),
        Optional.empty()
    ));
    initializeReport();
    try (Reader reader = new FileReader("src/test/resources/test-data.json")) {
      dataList = new Gson().fromJson(reader, new TypeToken<List<TestData>>() {
      }.getType());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @AfterAll
  public static void tearDown() {
    if (driver != null) {
      driver.quit();
      driver = null;
    }
  }

  public static WebDriver getDriver() {
    return driver;
  }

}
