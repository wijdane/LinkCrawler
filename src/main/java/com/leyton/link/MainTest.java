package com.leyton.link;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class MainTest {

    public static final String SIGN = "https://www.linkedin.com/login?trk=guest_homepage-basic_nav-header-signin";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String LOGIN_FORM_ACTION_CONTAINER_BUTTON = ".login__form_action_container button";

    public static void main(String args[]) throws Exception {

        Main.declareDrivers();
        WebDriver driver = new ChromeDriver();
        driver.get(SIGN);

        randomWait();

        WebElement username = driver.findElement(By.id(USERNAME));
        WebElement password = driver.findElement(By.id(PASSWORD));

        WebElement button = driver.findElement(By.cssSelector(LOGIN_FORM_ACTION_CONTAINER_BUTTON));

        username.sendKeys("wijdane.oudli@gmail.com");
        password.sendKeys("");

        LinkedinService linkedinService = new LinkedinService();

        randomWait();

        button.click();

        linkedinService.searchForCompanies(driver);

    }

    public static void randomWait(){
        try {
            Thread.sleep((long) (1000+Math.random()*1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
