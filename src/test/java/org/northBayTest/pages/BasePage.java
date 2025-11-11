package org.northBayTest.pages;

import java.time.Duration;
import org.northBayTest.Hooks;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class BasePage {

  private static final Logger logger = LoggerFactory.getLogger(BasePage.class);

  WebDriver driver = null;
  WebDriverWait wait;
  Actions actions;

  public BasePage() {
    this.driver = Hooks.getDriver();
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    this.actions = new Actions(driver);
  }


  public void clickButtonWithText(String text) {
    driver.findElement(By.xpath("//button[text()='" + text + "']")).click();
  }

  public void scrollPage(String xpath) {
    actions.scrollToElement(driver.findElement((By.xpath(xpath)))).build().perform();
  }
}
