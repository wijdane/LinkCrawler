package com.leyton.link;

import com.opencsv.CSVReader;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriverBuilder;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static final int MAX_PROXIES = 1000;
    // csv file info
    private static int numberOfAccount=1;

    public static final String LINKEDIN_CREATION_PRIFIL = "https://www.linkedin.com";
    public static final String GENERATOR_EMAIL_URL = "https://generator.email/";
    // class variable
    final static String lexicon = "abcdefghijklmnopqrstuvwxyz";

    final static java.util.Random rand = new java.util.Random();
    final static Set<String> identifiers = new HashSet<>();

    final static String[] names = new String[]{"Luna", "Langona","Syspa","Rosalina","Hafida","Amila","Samida","Abdellah","Mohammed","Ahmed","Abdeali","Sosan","Samira","Abdelhak","Kabira","Romana","Jomana"};
    final static String[] lastNames = new String[]{"Gafner", "Gagan" ,"Gagas", "Gage" ,"Gagel" ,"Gagen" ,"Gager" ,"Gagliano" ,"Gagliardi" ,"Gagliardo", "Gaglio" ,"Gaglione" ,"Gagnard","Gagne","Kabira","Romana","Jomana"};

    final static String[] titles = new String[]{"Marketing", "Coordinator",
            "Medical Assistant",
            "Web Designer",
            "Dog Trainer",
            "President of Sales",
            "Nursing Assistant",
            "Project Manager",
            "Librarian"};

    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        String home = System.getProperty("user.home");
        System.setProperty("webdriver.chrome.driver",home+"/chromedriver");
        System.setProperty("webdriver.firefox.marionette",home+"/geckodriver");

        String fileName = home+"/proxies.csv";

        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             CSVReader reader = new CSVReader(isr, ':')) {
            String[] nextLine;
            // index
            int i=0;
            List<String[]> myEntries = reader.readAll();
            for (int j = 0; j < numberOfAccount; j++) {
                int random = (int) ((Math.random()*MAX_PROXIES));
                final String username =myEntries.get(random)[2];
                final String password = myEntries.get(random)[3];
                final String proxy = myEntries.get(random)[0]+":"+myEntries.get(random)[1];
                try {
                    authAndWork(username, password, proxy);
                }
                catch (Exception ex){
                    System.out.println("nothing create other !");
                }
            }
        }

    }

    private static void authAndWork(String username, String password, String proxy) throws InterruptedException {
        // create proxy

        DesiredCapabilities capabilities = createProxy(proxy);


        WebDriver driver = new ChromeDriver(capabilities);
        authentification(username, password);

        String mailgenarated = generateMail(driver);
        waitingForInfo();
        createLinkedInAccount(driver,mailgenarated);

        Thread.sleep(5000000);
        driver.get("https://www.linkedin.com/start/join?trk=guest_homepage-basic_nav-header-join");

    }

    private static String findLastName() {
        return lastNames[(int) (Math.random()*17)];
    }

    private static String findName() {
        return names[(int) (Math.random()*17)];
    }

    private static void authentification(final String username, final String password) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    waitingForInfo();
                    Keyboard keyboard = new Keyboard();
                    keyboard.type(username);
                    keyboard.press(KeyEvent.VK_TAB);
                    waitingForInfo();
                    keyboard.type(password);
                    keyboard.press(KeyEvent.VK_TAB);
                    keyboard.press(KeyEvent.VK_TAB);
                    keyboard.press(KeyEvent.VK_ENTER);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private static DesiredCapabilities createProxy(String sProxy) {
        Proxy proxy = new Proxy();
        proxy.setHttpProxy(sProxy);
        proxy.setSslProxy(sProxy);
        proxy.setFtpProxy(sProxy);
        proxy.setAutodetect(false);
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("proxy", proxy);

        return capabilities;
    }

    private static String generateMail(WebDriver driver) throws InterruptedException {
        StringBuilder mailGenerated= new StringBuilder();
        WebElement domaine = null;
        WebElement user = null;

            driver.get(
                    GENERATOR_EMAIL_URL);
            domaine = driver.findElement(By.xpath("//*[@id=\"domainName2\"]"));
            user = driver.findElement(By.xpath("//*[@id=\"userName\"]"));
            mailGenerated.append(user.getAttribute("value"));
            mailGenerated.append("@");
            mailGenerated.append(domaine.getAttribute("value"));
            System.out.println("The mail is  : " + mailGenerated.toString());

            waitingForInfo();

        return mailGenerated.toString();
    }

    private static void waitingForInfo() throws InterruptedException {
        Thread.sleep((long) (Math.random()*2000+1000));
    }

    private static String createLinkedInAccount(WebDriver driver,String mail) throws InterruptedException {
        WebElement firstNameElement = null;
        WebElement lastNameElement = null;
        WebElement joinMailElement = null;
        WebElement joinPasswordElement = null;
        WebElement joinButton = null;

            driver.get(LINKEDIN_CREATION_PRIFIL);
            driver.get(LINKEDIN_CREATION_PRIFIL);
            waitingForInfo();
            driver.findElement(By.className("nav__button-tertiary")).click();
            waitingForInfo();
            firstNameElement = driver.findElement(By.xpath("//*[@id=\"first-name\"]"));
            lastNameElement = driver.findElement(By.xpath("//*[@id=\"last-name\"]"));
            joinMailElement = driver.findElement(By.xpath("//*[@id=\"join-email\"]"));
            joinPasswordElement = driver.findElement(By.xpath("//*[@id=\"join-password\"]"));
            joinButton =driver.findElement(By.xpath("//*[@id=\"uno-reg-join\"]/div/div/div/div[2]/div[1]/div[1]/div[2]/form/fieldset/button"));

            firstNameElement.sendKeys(findName());
            waitingForInfo();
            lastNameElement.sendKeys(findLastName());
            waitingForInfo();
            joinMailElement.sendKeys(mail);
            waitingForInfo();
            joinPasswordElement.sendKeys(generatePswd(15).toString());
            waitingForInfo();
            joinButton.click();

            // continue
            driver.get("https://www.linkedin.com/onboarding/start/?source=");
            waitingForInfo();
            driver.findElement(By.id("location-postal")).sendKeys("20250");
            waitingForInfo();
            driver.findElement(By.id("ember302")).click();
            waitingForInfo();
            driver.findElement(By.id("typeahead-input-for-title")).sendKeys(titles[(int) (Math.random()*17)]);
            waitingForInfo();
            driver.findElement(By.id("typeahead-input-for-company")).sendKeys("Capegimini");
            waitingForInfo();
            driver.findElement(By.id("ember333")).click();

            System.out.println("Congratulations LINKEDIN Created.");
            waitingForInfo();
        return  null;
    }


    private static char[] generatePswd(int len)
    {
        System.out.println("Your Password:");
        String charsCaps = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String chars = "abcdefghijklmnopqrstuvwxyz";
        String nums = "0123456789";
        String symbols = "!@#$%^&*_=+-/€.?<>)";

        String passSymbols = charsCaps + chars + nums + symbols;
        Random rnd = new Random();

        char[] password = new char[len];
        for (int i = 0; i < len; i++)
        {
            password[i] = passSymbols.charAt(rnd.nextInt(passSymbols.length()));

        }
        System.out.println(" password : "+password);
        return password;
    }

    public  static String randomIdentifier() {
        StringBuilder builder = new StringBuilder();
        while(builder.toString().length() == 0) {
            int length = rand.nextInt(5)+5;
            for(int i = 0; i < length; i++) {
                builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
            }
            if(identifiers.contains(builder.toString())) {
                builder = new StringBuilder();
            }
        }
        return builder.toString();
    }
}
