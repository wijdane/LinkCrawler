package com.leyton.link;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class LinkedinService {
    public static final String LINKEDIN_CREATION_PRIFIL = "https://www.linkedin.com";
    public static final String GENERATOR_EMAIL_URL = "https://generator.email/";
    // class variable
    final static String lexicon = "abcdefghijklmnopqrstuvwxyz";

    final static java.util.Random rand = new java.util.Random();
    final static Set<String> identifiers = new HashSet<>();

    final static String[] names = new String[]{"Korbyn",
            "Markus","Ammar","Karim","Faycel","Malek","Youssef",
            "Chance",
            "Anderson","Bowie",
            "Deangelo",
            "Harlem","Forest" +
            "Benedict","Boden",
            "Camdyn","Liam",
            "Noah",
            "William",
            "James","Luna", "Langona","Syspa","Rosalina","Hafida","Amila","Samida","Abdellah","Mohammed","Ahmed","Abdeali","Sosan","Samira","Abdelhak","Kabira","Romana","Jomana"};
    final static String[] lastNames = new String[]{"PECK","SANFORD","LEWIS","WALLER","EDWARDS","EVANS","PHILLIPS","TURNER","NELSON","GONZALEZ","ADAMS","YOUNG","ALLEN","HALL","CLARK","RODRIGUEZ","Gafner", "Gagan" ,"Gagas", "Gage" ,"Gagel" ,"Gagen" ,"Gager" ,"Gagliano" ,"Gagliardi" ,"Gagliardo", "Gaglio" ,"Gaglione" ,"Gagnard","Gagne","Kabira","Romana","Jomana"};

    final static String[] titles = new String[]{"Marketing", "Coordinator",
            "Medical Assistant",
            "Web Designer",
            "Dog Trainer",
            "President of Sales",
            "Nursing Assistant",
            "Project Manager",
            "Librarian"};


    public Integer createLinkedInAccount(WebDriver driver) throws InterruptedException {
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

        String name = findName();
        firstNameElement.sendKeys(name);
        waitingForInfo();
        String lastName = findLastName();
        lastNameElement.sendKeys(lastName);
        waitingForInfo();
        String email = name + "." + lastName + ((int) (Math.random() * 1000)) + "@gmail.com";
        joinMailElement.sendKeys(email);
        waitingForInfo();
        String password = generatePswd(15).toString();
        joinPasswordElement.sendKeys(password);
        waitingForInfo();
        joinButton.click();

        // continue
        //driver.get("https://www.linkedin.com/feed/");
        waitingForInfo();
        if(!driver.getCurrentUrl().contains("login")) {
            // TODO connected
            System.out.println(email + "/" + password);
            return 1;
        }

        return  0;
    }


    public  void waitingForInfo() throws InterruptedException {
        Thread.sleep((long) (Math.random()*2000+1000));
    }

    public  String findLastName() {
        return lastNames[(int) (Math.random()*17)];
    }

    public  String findName() {
        return names[(int) (Math.random()*17)];
    }

    public  char[] generatePswd(int len)
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

}
