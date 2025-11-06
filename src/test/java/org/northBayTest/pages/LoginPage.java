package org.northBayTest.pages;

import org.northBayTest.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class LoginPage extends BasePage {

  private WebDriver driver;

  @FindBy(id = "email")
  private WebElement textBoxEmail;

  @FindBy(id = "password")
  private WebElement textBoxPassword;

  @FindBy(xpath = "//button[text()='Log in']")
  private WebElement buttonLogin;

  public LoginPage() {
    this.driver = Hooks.getDriver();
    PageFactory.initElements(driver, this);
  }

  public void enterDetailsAndLogin(String email, String password) {
    textBoxEmail.sendKeys(email);
    textBoxPassword.sendKeys(password);
    buttonLogin.click();
  }

}
