package org.northBayTest.pages;

import java.time.Duration;
import org.northBayTest.Hooks;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class BasePage {

  WebDriver driver = null;

  WebDriverWait wait;

  public BasePage() {
    this.driver = Hooks.getDriver();
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
  }


  public void clickButtonWithText(String text) {
    driver.findElement(By.xpath("//button[text()='" + text + "']")).click();
  }

  public void scrollPage(String xpath) {
    Actions actions = new Actions(driver);
    actions.scrollToElement(driver.findElement((By.xpath(xpath)))).build().perform();
  }


}
