package com.leyton.link;

import com.opencsv.CSVReader;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;


public class Main {
    public static final int MAX_PROXIES = 724;
    // csv file info
    private static int numberOfAccount = 1;
    private static LinkedinService linkedinService = new LinkedinService();
    private static ChromeDriver driver;
    private static int count = 0;

    public static void main(String[] args) throws AWTException, InterruptedException, IOException {
        String home = System.getProperty("user.home");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/drivers/chromedriver");
        System.setProperty("webdriver.firefox.marionette", "src/main/resources/drivers/geckodriver");
        String fileName = home + "/proxies.csv";
        try (FileInputStream fis = new FileInputStream(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             CSVReader reader = new CSVReader(isr, ':')) {
            String[] nextLine;
            // index
            int i = 0;
            List<String[]> myEntries = reader.readAll();
            do {
                int random = (int) ((Math.random() * MAX_PROXIES));
                final String username = myEntries.get(random)[2];
                final String password = myEntries.get(random)[3];
                final String proxy = myEntries.get(random)[0] + ":" + myEntries.get(random)[1];
                try {
                    if (driver != null) {
                        driver.quit();
                    }
                    authAndWork(username, password, proxy);
                } catch (Exception ex) {
                    System.out.println("Error : other window will be created." + ex);
                }
            } while (count < numberOfAccount);
        }
    }

    private static void authAndWork(String username, String password, String proxy) throws InterruptedException, IOException {
        // create proxy
        DesiredCapabilities capabilities = createProxy(proxy);
        driver = new ChromeDriver(capabilities);
        authentification(username, password);
        waitingForInfo();
        count += linkedinService.createLinkedInAccount(driver);
    }

    private static void authentification(final String username, final String password) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(4000);
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

    private static void waitingForInfo() throws InterruptedException {
        Thread.sleep((long) (Math.random() * 2000 + 1000));
    }
}
